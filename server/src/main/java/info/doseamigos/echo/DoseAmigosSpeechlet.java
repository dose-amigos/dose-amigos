package info.doseamigos.echo;

import com.amazon.speech.slu.Intent;
import com.amazon.speech.speechlet.*;
import com.amazon.speech.ui.PlainTextOutputSpeech;
import com.amazon.speech.ui.Reprompt;
import com.amazon.speech.ui.SimpleCard;
import com.google.inject.Guice;
import com.google.inject.Injector;
import info.doseamigos.authusers.AuthUser;
import info.doseamigos.authusers.AuthUserGuiceModule;
import info.doseamigos.authusers.AuthUserService;
import info.doseamigos.meds.Med;
import info.doseamigos.meds.MedGuiceModule;
import info.doseamigos.meds.MedService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Speechlet for Echo related tasks.
 */
public class DoseAmigosSpeechlet implements Speechlet {

    private static final Logger log = LoggerFactory.getLogger(DoseAmigosSpeechlet.class);
    private MedService medService;
    private AuthUserService authUserService;

    @Override
    public void onSessionStarted(SessionStartedRequest request, Session session) throws SpeechletException {
        Injector injector = Guice.createInjector(
            new MedGuiceModule(),
            new AuthUserGuiceModule()
        );
        medService = injector.getInstance(MedService.class);
        authUserService = injector.getInstance(AuthUserService.class);
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
                String speechText = "Thanks " + sessionUser.getAmigoUser().getName() + ", it has been recorded.";
                outputSpeech.setText(speechText);
                card.setTitle("Recording Med Taking.");
                card.setContent(speechText);
                return SpeechletResponse.newTellResponse(outputSpeech, card);
            case "ListMeds":
                log.info("Getting list of medications for user");
                List<Med> meds = medService.medsForUser(sessionUser, sessionUser.getAmigoUser());

                StringBuilder sb = new StringBuilder();
                sb.append("Here's the following medications listed for you, " + sessionUser.getAmigoUser().getName());
                sb.append(": ");
                boolean isFirst = true;
                for (Med med : meds) {
                    if (!isFirst) {
                        sb.append(", ");
                    }
                    isFirst = false;
                    sb.append(med.getName() + " scheduled for " + med.getNextScheduled());
                }
                outputSpeech.setText(sb.toString());
                card.setTitle("List of Medication");
                card.setContent(sb.toString());
                return SpeechletResponse.newTellResponse(outputSpeech, card);
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

    @Override
    public void onSessionEnded(SessionEndedRequest request, Session session) throws SpeechletException {

    }
}
