package info.doseamigos.echo;

import com.amazon.speech.slu.Intent;
import com.amazon.speech.speechlet.*;
import com.amazon.speech.ui.PlainTextOutputSpeech;
import com.amazon.speech.ui.Reprompt;
import com.amazon.speech.ui.SimpleCard;
import com.amazonaws.services.lambda.runtime.Client;
import com.google.api.client.util.DateTime;
import com.google.inject.Guice;
import com.google.inject.Injector;
import info.doseamigos.amigousers.AmigoUserGuiceModule;
import info.doseamigos.authusers.AuthUser;
import info.doseamigos.authusers.AuthUserGuiceModule;
import info.doseamigos.authusers.AuthUserService;
import info.doseamigos.doseevents.DoseEvent;
import info.doseamigos.doseevents.DoseEventService;
import info.doseamigos.doseevents.DoseEventsGuiceModule;
import info.doseamigos.doseevents.EventType;
import info.doseamigos.doseseries.DoseSeriesGuiceModule;
import info.doseamigos.meds.Med;
import info.doseamigos.meds.MedGuiceModule;
import info.doseamigos.meds.MedService;
import org.joda.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static java.lang.Math.abs;

/**
 * Speechlet for Echo related tasks.
 */
public class DoseAmigosSpeechlet implements Speechlet {

    private static final Logger log = LoggerFactory.getLogger(DoseAmigosSpeechlet.class);
    private MedService medService;
    private AuthUserService authUserService;
    private DoseEventService doseEventService;

    @Override
    public void onSessionStarted(SessionStartedRequest request, Session session) throws SpeechletException {
        Injector injector = Guice.createInjector(
            new DoseEventsGuiceModule(),
            new DoseSeriesGuiceModule(),
            new AmigoUserGuiceModule(),
            new MedGuiceModule(),
            new AuthUserGuiceModule()
        );
        medService = injector.getInstance(MedService.class);
        authUserService = injector.getInstance(AuthUserService.class);
        doseEventService = injector.getInstance(DoseEventService.class);
    }

    @Override
    public SpeechletResponse onLaunch(LaunchRequest request, Session session) throws SpeechletException {
        AuthUser loggedInUser = getSessionUser(session);

        String name = loggedInUser.getAmigoUser().getName();
        PlainTextOutputSpeech outputSpeech = new PlainTextOutputSpeech();
        outputSpeech.setText("Welcome to Dose Amigos " + name + ", you can add a new medication.");
        Reprompt reprompt = new Reprompt();
        reprompt.setOutputSpeech(outputSpeech);
        SimpleCard card = new SimpleCard();
        card.setTitle("Add New Medication");
        card.setContent("Welcome to Dose Amigos, you can add a new medication.");
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
        String intentName = (intent != null) ? intent.getName() : null;
        AuthUser sessionUser = getSessionUser(session);
        PlainTextOutputSpeech outputSpeech = new PlainTextOutputSpeech();
        SimpleCard card = new SimpleCard();

        switch (intentName) {
            case "TakeMeds":
                log.info("recording med taking");
                return takeMeds(sessionUser);
            case "ListMeds":
                log.info("Getting list of medications for user");
                return listMedsResponse(sessionUser);
            case "AddMed":
                String medName = intent.getSlot("MedName").getValue();
                if (medName == null || medName.trim().isEmpty()) {
                    throw new RuntimeException("Med Name is null or empty");
                }
                Med newMed = medService.addByName(sessionUser, medName);
                log.info("Added new med: " + newMed);
                outputSpeech.setText(String.format("You've added %s to your list of medications.", newMed.getName()));

                card.setTitle("Added Medication");
                card.setContent("You added the following Medication: " + newMed);
                return SpeechletResponse.newTellResponse(outputSpeech, card);

            default:
                throw new RuntimeException("Invalid Intent Name found.");
        }
    }

    private SpeechletResponse listMedsResponse(AuthUser sessionUser) {
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

    private SpeechletResponse takeMeds(AuthUser sessionUser) {
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

    @Override
    public void onSessionEnded(SessionEndedRequest request, Session session) throws SpeechletException {

    }
}
