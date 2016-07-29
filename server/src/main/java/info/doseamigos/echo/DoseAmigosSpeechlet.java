package info.doseamigos.echo;

import com.amazon.speech.slu.Intent;
import com.amazon.speech.slu.Slot;
import com.amazon.speech.speechlet.*;
import com.amazon.speech.ui.PlainTextOutputSpeech;
import com.amazon.speech.ui.Reprompt;
import com.amazon.speech.ui.SimpleCard;
import com.amazonaws.services.lambda.runtime.Client;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.Joiner;
import com.google.inject.Guice;
import com.google.inject.Injector;
import info.doseamigos.amigousers.AmigoUser;
import info.doseamigos.amigousers.AmigoUserGuiceModule;
import info.doseamigos.authusers.AuthUser;
import info.doseamigos.authusers.AuthUserGuiceModule;
import info.doseamigos.authusers.AuthUserService;
import info.doseamigos.doseevents.DoseEvent;
import info.doseamigos.doseevents.DoseEventService;
import info.doseamigos.doseevents.DoseEventsGuiceModule;
import info.doseamigos.doseevents.EventType;
import info.doseamigos.doseseries.DoseSeries;
import info.doseamigos.doseseries.DoseSeriesGuiceModule;
import info.doseamigos.doseseries.DoseSeriesService;
import info.doseamigos.feedevents.FeedEvent;
import info.doseamigos.feedevents.FeedEventGuiceModule;
import info.doseamigos.feedevents.FeedEventService;
import info.doseamigos.meds.Med;
import info.doseamigos.meds.MedGuiceModule;
import info.doseamigos.meds.MedService;
import info.doseamigos.sharerequests.ShareRequest;
import info.doseamigos.sharerequests.ShareRequestGuiceModule;
import info.doseamigos.sharerequests.ShareRequestService;
import org.joda.time.DateTimeFieldType;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormatter;
import org.omg.CORBA.StringHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.time.DayOfWeek;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.lang.Math.abs;

/**
 * Speechlet for Echo related tasks.
 */
public class DoseAmigosSpeechlet implements Speechlet {

    private static final Logger log = LoggerFactory.getLogger(DoseAmigosSpeechlet.class);
    private AuthUserService authUserService;
    private DoseEventService doseEventService;
    private DoseSeriesService doseSeriesService;
    private FeedEventService feedEventService;

    private AuthUser sessionUser;

    @Override
    public void onSessionStarted(SessionStartedRequest request, Session session) throws SpeechletException {
        Injector injector = Guice.createInjector(
            new DoseEventsGuiceModule(),
            new ShareRequestGuiceModule(),
            new DoseSeriesGuiceModule(),
            new AmigoUserGuiceModule(),
            new MedGuiceModule(),
            new AuthUserGuiceModule(),
            new FeedEventGuiceModule()
        );
        authUserService = injector.getInstance(AuthUserService.class);
        doseEventService = injector.getInstance(DoseEventService.class);
        doseSeriesService = injector.getInstance(DoseSeriesService.class);
        feedEventService = injector.getInstance(FeedEventService.class);
        sessionUser = getSessionUser(session);
    }

    @Override
    public SpeechletResponse onLaunch(LaunchRequest request, Session session) throws SpeechletException {
        String name = sessionUser.getAmigoUser().getName();
        PlainTextOutputSpeech outputSpeech = new PlainTextOutputSpeech();
        StringBuilder builder = new StringBuilder();
        builder
            .append("Welcome to Dose Amigos ")
            .append(name)
            .append(".  You can add a new medication, list your upcoming doses, take you medication, and check on your amigos.");
        outputSpeech.setText(builder.toString());
        Reprompt reprompt = new Reprompt();
        reprompt.setOutputSpeech(outputSpeech);
        SimpleCard card = new SimpleCard();
        card.setTitle("Add New Medication");
        card.setContent(builder.toString());
        return SpeechletResponse.newAskResponse(outputSpeech, reprompt, card);
    }

    private AuthUser getSessionUser(Session session) {
        log.info(session.getUser().getUserId());
        if (session.getUser().getAccessToken() == null) {
            log.info("The access token is null.");
            throw new NullPointerException();
        }
        String accessToken = session.getUser().getAccessToken();
        log.info(accessToken);
        log.info("Getting user from google.");
        AuthUser loggedInUser = authUserService.getByToken(accessToken);
        log.info("Received user from google.");
        return loggedInUser;
    }

    @Override
    public SpeechletResponse onIntent(IntentRequest request, Session session) throws SpeechletException {
        Intent intent = request.getIntent();
        String intentName = (intent != null) ? intent.getName() : "";
        log.info("input: " + request.getIntent().getSlots());
        switch (intentName) {
            case "AMAZON.CancelIntent":
            case "AMAZON.StopIntent":
                log.info("Closing the app.");
                PlainTextOutputSpeech outputSpeech = new PlainTextOutputSpeech();
                outputSpeech.setText("Good Bye.");
                return SpeechletResponse.newTellResponse(outputSpeech);
            case "AMAZON.StartOverIntent":
                log.info("Cancelling");
                return SpeechletResponse.newAskResponse(new PlainTextOutputSpeech(), new Reprompt());
            case "AMAZON.HelpIntent":
                log.info("Return Help response");
                return helpResponse();
            case "TakeMeds":
                log.info("recording med taking");
                return takeMeds();
            case "ListMeds":
                log.info("Getting list of medications for user");
                return listMedsResponse();
            case "AddMed":
                log.info("Initializing add med");
                return addMed_init(session);
            case "AddMedLocation":
                log.info("Getting location for timezone reasons");
                try {
                    return addMed_location(session, intent);
                } catch (IOException e) {
                    log.error("Something horrible happened", e);
                    throw new RuntimeException(e);
                }
            case "AddMedName":
                log.info("Getting med name");
                return addMed_name(session, intent);
            case "AddMedDays":
                log.info("Getting med days");
                return addMed_Days(session, intent);
            case "AddMedTimes":
                log.info("Getting med times and saving to DB");
                return addMed_Times(session, intent);
            case "AmigoUpdate":
                log.info("Getting latest events on amigo.");
                return amigoUpdate();

            default:
                throw new RuntimeException("Invalid Intent Name found.");
        }
    }

    private SpeechletResponse helpResponse() {
        StringBuilder speech = new StringBuilder();
        speech.append("You can say 'I would like to add a medication' to add a medication.  I will then give you specific instructions to follow.  ");
        speech.append("You can say 'I would like to list my medications' to list your upcoming doses and their times.  ");
        speech.append("You can say 'I have taken my meds' after taking your medication to update the system.  ");
        speech.append("You can say 'I would like to check on my amigos' to get an update on your amigos.  See the Alexa App on your phone or computer for more details.");
        PlainTextOutputSpeech outputSpeech = new PlainTextOutputSpeech();
        outputSpeech.setText(speech.toString());
        SimpleCard card = new SimpleCard();
        card.setTitle("Help Request");
        card.setContent(speech.toString());

        Reprompt reprompt = new Reprompt();

        return SpeechletResponse.newAskResponse(outputSpeech, reprompt, card);
    }

    private SpeechletResponse amigoUpdate() {
        Map<AmigoUser, FeedEvent> eventMap = feedEventService.getLatestestEventsForEcho(sessionUser);
        List<AmigoUser> missedDoseAmigos = new ArrayList<>();
        List<AmigoUser> skippedDoseAmigos = new ArrayList<>();
        for (Map.Entry<AmigoUser, FeedEvent> eventEntry : eventMap.entrySet()) {
            if (eventEntry.getValue().getAction() == EventType.MISSED) {
                missedDoseAmigos.add(eventEntry.getKey());
            } else if (eventEntry.getValue().getAction() == EventType.SKIPPED) {
                skippedDoseAmigos.add(eventEntry.getKey());
            }
        }
        StringBuilder speechSB = new StringBuilder();
        speechSB.append("You have ");
        if (missedDoseAmigos.isEmpty() && skippedDoseAmigos.isEmpty()) {
            speechSB.append(" no amigos missing any of their latest doses.");
        }
        if (!missedDoseAmigos.isEmpty()) {
            speechSB.append(missedDoseAmigos.size());
            if (missedDoseAmigos.size() == 1) {
                speechSB.append(" amigo");
            } else {
                speechSB.append(" amigos");
            }
            speechSB.append(" that missed a dose,");
            if (!skippedDoseAmigos.isEmpty()) {
                speechSB.append(" and ");
            }
        }
        if (!skippedDoseAmigos.isEmpty()) {
            speechSB.append(skippedDoseAmigos.size());
            if (skippedDoseAmigos.size() == 1) {
                speechSB.append(" amigo");
            } else {
                speechSB.append(" amigos");
            }
            speechSB.append(" that skipped a dose ");
        }

        PlainTextOutputSpeech outputSpeech = new PlainTextOutputSpeech();
        SimpleCard card = new SimpleCard();
        outputSpeech.setText(speechSB.toString());
        card.setTitle("Amigo Status");
        StringBuilder cardSB = new StringBuilder();
        if (missedDoseAmigos.isEmpty() && skippedDoseAmigos.isEmpty()) {
            cardSB.append(speechSB.toString());
        } else {
            if (!missedDoseAmigos.isEmpty()) {
                cardSB.append("The following amigos has missed a medication: ");
                cardSB.append(com.google.common.base.Joiner.on(", ").join(missedDoseAmigos.stream().map(
                    new Function<AmigoUser, String>() {
                        @Override
                        public String apply(AmigoUser amigoUser) {
                            return amigoUser.getName();
                        }
                    }).collect(Collectors.toList())));
                if (!skippedDoseAmigos.isEmpty()) {
                    cardSB.append(". ");
                }
            }
            if (!skippedDoseAmigos.isEmpty()) {
                cardSB.append("The following amigos has skipped a medication: ");
                cardSB.append(com.google.common.base.Joiner.on(", ").join(skippedDoseAmigos.stream().map(
                    new Function<AmigoUser, String>() {
                        @Override
                        public String apply(AmigoUser amigoUser) {
                            return amigoUser.getName();
                        }
                    }).collect(Collectors.toList())));
            }

        }
        card.setContent(cardSB.toString());
        return SpeechletResponse.newTellResponse(outputSpeech, card);
    }

    private SpeechletResponse listMedsResponse() {
        PlainTextOutputSpeech outputSpeech = new PlainTextOutputSpeech();
        SimpleCard card = new SimpleCard();
        List<DoseEvent> doseEvents = doseEventService.getEventsForUserToday(sessionUser, sessionUser.getAmigoUser());

        StringBuilder sb = new StringBuilder();
        sb.append("Here's the following medications listed for you, " + sessionUser.getAmigoUser().getName());
        sb.append(": ");
        boolean isFirst = true;
        for (DoseEvent event : doseEvents) {
            if (!isFirst) {
                sb.append(", ");
            }
            isFirst = false;

            sb.append(event.getMed().getName()).append(formatTimeRemaining(event.getScheduledDateTime()));
        }
        outputSpeech.setText(sb.toString());
        card.setTitle("List of Medication");
        card.setContent(sb.toString());
        return SpeechletResponse.newTellResponse(outputSpeech, card);
    }

    private String formatTimeRemaining(Date targetDate) {
        long diff = targetDate.getTime() - new Date().getTime();
        StringBuilder builder = new StringBuilder();
        if (diff < 0) {
            builder.append(" is overdue by ");
            diff = diff * -1;
        } else {
            builder.append(" is due in ");
        }
        //Round to nearest minute
        long minutes = diff / 60000;
        if (minutes < 60) {
            builder.append(minutes).append(" minutes.");
        } else {
            builder.append(minutes/60).append(" hours and ");
            builder.append(minutes % 60).append(" minutes.");
        }
        return builder.toString();
    }

    private SpeechletResponse takeMeds() {
        PlainTextOutputSpeech outputSpeech = new PlainTextOutputSpeech();
        SimpleCard card = new SimpleCard();

        List<DoseEvent> doseEvents = doseEventService.getEventsForUserToday(
            sessionUser,
            sessionUser.getAmigoUser()
        );

        List<DoseEvent> toUpdate = new ArrayList<>(doseEvents.size());
        Date now = new Date();
        for (DoseEvent event : doseEvents) {
            //If you're within an hour of the dose, we're registering it as taken.
            if (abs(event.getScheduledDateTime().getTime() - new Date().getTime()) < 1000*60*60) {
                event.setAction(EventType.TAKEN);
                event.setActionDateTime(now);
                toUpdate.add(event);
            }
        }
        toUpdate = doseEventService.updateDoseEvents(sessionUser, toUpdate);

        outputSpeech.setText("Thank you, your doses have been recorded.");
        card.setTitle("Taken Medication");
        StringBuilder builder = new StringBuilder();
        builder.append("The following medication has been recorded as taken: \n");
        for (DoseEvent event : toUpdate) {
            builder.append(event.getMed().getName()).append("\n");
        }
        card.setContent(builder.toString());
        return SpeechletResponse.newTellResponse(outputSpeech, card);

    }

    private SpeechletResponse addMed_init(Session session) {

        PlainTextOutputSpeech outputSpeech = new PlainTextOutputSpeech();
        SimpleCard card = new SimpleCard();
        card.setTitle("Adding Medication");
        card.setContent("Please tell us your location for properly timed reminders");

        outputSpeech.setText("In order to set the times correctly to your timezone, I need to know your location.");

        Reprompt reprompt = new Reprompt();
        PlainTextOutputSpeech repromptSpeech = new PlainTextOutputSpeech();
        repromptSpeech.setText("Please tell me the city and state you're in.");

        return SpeechletResponse.newAskResponse(outputSpeech, reprompt, card);
    }

    private SpeechletResponse addMed_location(Session session, Intent intent) throws IOException {
        String city = intent.getSlot("city").getValue();
        String state = intent.getSlot("state").getValue();
        Map<String, Object> timezoneInfo = new LocationToTimezoneConverter().getTimezone(city + " " + state);
        session.setAttribute("timezone", timezoneInfo.get("timeZoneId"));

        PlainTextOutputSpeech outputSpeech = new PlainTextOutputSpeech();
        SimpleCard card = new SimpleCard();
        card.setTitle("Adding Medication");
        card.setContent("We received your location, now we need the name of your medication.");

        outputSpeech.setText("We've received your location, thank you. Now please tell me the name of your medication");

        Reprompt reprompt = new Reprompt();
        PlainTextOutputSpeech repromptSpeech = new PlainTextOutputSpeech();
        repromptSpeech.setText("Please tell me the name of your medication.");

        return SpeechletResponse.newAskResponse(outputSpeech, reprompt, card);
    }

    private SpeechletResponse addMed_name(Session session, Intent intent) {
        String medName = intent.getSlot("medName").getValue();
        session.setAttribute("medName", medName);

        PlainTextOutputSpeech outputSpeech = new PlainTextOutputSpeech();
        SimpleCard card = new SimpleCard();
        card.setTitle("Adding Medication");
        card.setContent("We received the name of the medication: " + medName);

        outputSpeech.setText("The name of the medication we receieved is " + medName + ".  Now tell me the days you take the medication.");

        Reprompt reprompt = new Reprompt();
        PlainTextOutputSpeech repromptSpeech = new PlainTextOutputSpeech();
        repromptSpeech.setText("Please tell us what days you take this medication.  You can say each day like Monday, Wednesday, Friday, or you can say every day");

        return SpeechletResponse.newAskResponse(outputSpeech, reprompt, card);
    }

    private SpeechletResponse addMed_Days(Session session, Intent intent) {
        List<Integer> days = new ArrayList<>(7);
        List<Slot> slots = new ArrayList<>(intent.getSlots().values());
        for (Slot slot : slots) {
            String day = slot.getValue();
            if (day != null && day.equalsIgnoreCase("EVERYDAY")) {
                days = (Arrays.asList(1,2,3,4,5,6,7));
                break;
            }
            if (day != null) {
                days.add(DayOfWeekConverter.toDayOfWeek(day).getValue());
            }
        }
        session.setAttribute("daysOfWeek", days);

        PlainTextOutputSpeech outputSpeech = new PlainTextOutputSpeech();
        SimpleCard card = new SimpleCard();
        card.setTitle("Adding Medication");
        card.setContent("We received the days the you specified");

        StringBuilder sb = new StringBuilder().append("The days you take the medication we receieved are ");
        boolean isFirst = true;
        for (Integer dayVal : days) {
            if (!isFirst) {
                sb.append(", ");
            }
            isFirst = false;
            sb.append(DayOfWeek.of(dayVal));

        }
        sb.append(".  Now please tell us the times during the day you'll take this medication.");
        outputSpeech.setText(sb.toString());

        Reprompt reprompt = new Reprompt();
        PlainTextOutputSpeech repromptSpeech = new PlainTextOutputSpeech();
        repromptSpeech.setText("Please tell us what times during the day you take this medication.  You can say up to 6 different times.");
        return SpeechletResponse.newAskResponse(outputSpeech, reprompt, card);
    }

    private SpeechletResponse addMed_Times(Session session, Intent intent) {
        List<Date> times = new ArrayList<>(6);
        List<Slot> slots = new ArrayList<>(intent.getSlots().values());
        for (Slot slot : slots) {
            String time = slot.getValue();
            log.info("time:" + time);
            if (time != null && !time.isEmpty()) {
                Date timeOfDay = LocalTime.parse(time).toDateTimeToday(
                    DateTimeZone.forTimeZone(TimeZone.getTimeZone((String) session.getAttribute("timezone")))).toDate();
                times.add(timeOfDay);
            }
        }

        DoseSeries series = new DoseSeries();
        series.setDaysOfWeek((List<Integer>) session.getAttribute("daysOfWeek"));
        series.setTimesOfDay(times);
        Med med = new Med();
        med.setName((String) session.getAttribute("medName"));
        med.setDoseUnit("dose");
        med.setDoseAmount(1);
        med.setUser(sessionUser.getAmigoUser());
        series.setMed(med);

        DoseSeries savedSeries = doseSeriesService.addSeries(sessionUser, series);

        PlainTextOutputSpeech outputSpeech = new PlainTextOutputSpeech();
        SimpleCard card = new SimpleCard();
        card.setTitle("Added Medication");
        card.setContent("");

        outputSpeech.setText("We have added " + savedSeries.getMed().getName() + " to your list of medications.");

        return SpeechletResponse.newTellResponse(outputSpeech, card);
    }

    @Override
    public void onSessionEnded(SessionEndedRequest request, Session session) throws SpeechletException {

    }
}
