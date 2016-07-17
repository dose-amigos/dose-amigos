package info.doseamigos.doseevents;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.util.IOUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Guice;
import com.google.inject.Injector;
import info.doseamigos.ClientRequestObject;
import info.doseamigos.CommonModule;
import info.doseamigos.amigousers.AmigoUserGuiceModule;
import info.doseamigos.authusers.AuthUser;
import info.doseamigos.doseseries.DoseSeriesGuiceModule;
import info.doseamigos.meds.MedGuiceModule;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

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
}
