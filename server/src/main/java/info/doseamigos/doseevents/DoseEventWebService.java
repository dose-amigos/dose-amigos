package info.doseamigos.doseevents;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.util.IOUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Guice;
import com.google.inject.Injector;
import info.doseamigos.ClientRequestObject;
import info.doseamigos.CommonModule;
import info.doseamigos.amigousers.AmigoUserGuiceModule;
import info.doseamigos.doseseries.DoseSeriesGuiceModule;
import info.doseamigos.meds.MedGuiceModule;

/**
 * WebService for DoseEvents.
 */
public class DoseEventWebService {

    private final ObjectMapper objectMapper;
    private final DoseEventService service;

    public DoseEventWebService() {
        Injector injector = Guice.createInjector(
            new CommonModule(),
            new DoseSeriesGuiceModule(),
            new AmigoUserGuiceModule(),
            new MedGuiceModule(),
            new DoseEventsGuiceModule()
        );
        service = injector.getInstance(DoseEventService.class);
        objectMapper = injector.getInstance(ObjectMapper.class);
    }

    public void getDoseEvents(
            InputStream inputStream,
            OutputStream outputStream,
            Context context
    ) throws IOException {
        LambdaLogger logger = context.getLogger();

        //Logging purposes
        String streamContents = IOUtils.toString(inputStream);
        logger.log("Stream Contents: " + streamContents);

        ClientRequestObject<Object> clientRequestObject = objectMapper.readValue(
                streamContents,
                objectMapper.getTypeFactory().constructParametrizedType(
                        ClientRequestObject.class,
                        ClientRequestObject.class,
                        Object.class
                )
        );

        logger.log("Calling updateDoseEvents");
        logger.log("Input: " + clientRequestObject.getQueryParams());
        logger.log("User: " + clientRequestObject.getSessionUser());

        String startAtStr = clientRequestObject.getQueryParams().get("startAt");
        Date startAt = null;
        if (startAtStr == null) {
            startAt = new Date();
        } else {
            startAt = new Date(Long.parseLong(startAtStr));

        }

        String dir = clientRequestObject.getQueryParams().get("dir");
        if (dir == null) {
            dir = "next";
        }

        List<DoseEvent> events = service.getDosesForPhone(
                clientRequestObject.getSessionUser(),
                startAt,
                dir
        );

        objectMapper.writeValue(outputStream, events);
    }

    /**
     * Handles mass update lambda call for DoseEvents.
     * @param inputStream
     * @param outputStream
     * @param context
     */
    public void updateDoseEvents(
        InputStream inputStream,
        OutputStream outputStream,
        Context context
    ) throws IOException {
        LambdaLogger logger = context.getLogger();

        //Logging purposes
        String streamContents = IOUtils.toString(inputStream);
        logger.log("Stream Contents: " + streamContents);


        ClientRequestObject<List<DoseEvent>> clientRequestObject = objectMapper.readValue(
            streamContents,
            objectMapper.getTypeFactory().constructParametrizedType(
                ClientRequestObject.class,
                ClientRequestObject.class,
                objectMapper.getTypeFactory().constructCollectionType(
                    List.class,
                    DoseEvent.class
                )
            )
        );

        logger.log("Calling updateDoseEvents");
        logger.log("Input: " + clientRequestObject.getQueryParams());
        logger.log("User: " + clientRequestObject.getSessionUser());

        List<DoseEvent> updatedEvents =
            service.updateDoseEvents(clientRequestObject.getSessionUser(), clientRequestObject.getBody());

        objectMapper.writeValue(outputStream, updatedEvents);
    }

    /**
     * Cron job for marking missed doses, should run every 15 minutes or so.
     * @param inputStream
     * @param outputStream
     * @param context
     * @throws IOException
     */
    public void autoMarkMissed(
        InputStream inputStream,
        OutputStream outputStream,
        Context context
    ) throws IOException {
        service.markMissedEvents();
    }

    /**
     * Cron job for generating dose events from a series, should run once a week at the same time every week.
     */
    public void generateEvents(
        InputStream inputStream,
        OutputStream outputStream,
        Context context
    ) throws IOException {
        service.generateDoseEventsForAllUsers();
    }

    public void getEventsWeekly(
        InputStream inputStream,
        OutputStream outputStream,
        Context context
    ) throws IOException {
        LambdaLogger logger = context.getLogger();

        //Logging purposes
        String streamContents = IOUtils.toString(inputStream);
        logger.log("Stream Contents: " + streamContents);

        ClientRequestObject<Object> clientRequestObject = objectMapper.readValue(
            streamContents,
            objectMapper.getTypeFactory().constructParametrizedType(
                ClientRequestObject.class,
                ClientRequestObject.class,
                Object.class
            )
        );

        logger.log("Calling updateDoseEvents");
        logger.log("Input: " + clientRequestObject.getQueryParams());
        logger.log("User: " + clientRequestObject.getSessionUser());

        List<DoseEvent> events = service.getWeeklyEventsForAuthUser(
            clientRequestObject.getSessionUser()
        );

        objectMapper.writeValue(outputStream, events);
    }
}
