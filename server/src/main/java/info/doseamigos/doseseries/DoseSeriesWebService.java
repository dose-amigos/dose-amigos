package info.doseamigos.doseseries;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.util.IOUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Guice;
import com.google.inject.Injector;
import info.doseamigos.ClientRequestObject;
import info.doseamigos.CommonModule;
import info.doseamigos.amigousers.AmigoUserGuiceModule;
import info.doseamigos.doseevents.DoseEventsGuiceModule;
import info.doseamigos.meds.MedGuiceModule;
import info.doseamigos.meds.MedService;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

/**
 * WebService to contain StreamHandlers for the Amazon Lambda functions related to DoseSeries.
 */
public class DoseSeriesWebService {

    private final ObjectMapper objectMapper;
    private final DoseSeriesService doseSeriesService;

    /**
     * Constructor that creates a Guice Injector and gets objects out of there.
     */
    public DoseSeriesWebService() {
        Injector injector = Guice.createInjector(
            new CommonModule(),
            new AmigoUserGuiceModule(),
            new MedGuiceModule(),
            new DoseEventsGuiceModule(),
            new DoseSeriesGuiceModule()
        );
        objectMapper = injector.getInstance(ObjectMapper.class);
        doseSeriesService = injector.getInstance(DoseSeriesService.class);
    }

    /**
     * Handler for AddDoseSeries Lambda calls.
     * @param inputStream
     * @param outputStream
     * @param context
     */
    public void addDoseSeries(
        InputStream inputStream,
        OutputStream outputStream,
        Context context
    ) throws IOException {
        LambdaLogger logger = context.getLogger();

        //Logging purposes
        String streamContents = IOUtils.toString(inputStream);
        logger.log("Stream Contents: " + streamContents);


        ClientRequestObject<DoseSeries> clientRequestObject = objectMapper.readValue(
            streamContents,
            objectMapper.getTypeFactory().constructParametrizedType(
                ClientRequestObject.class,
                ClientRequestObject.class,
                DoseSeries.class));

        logger.log("Calling create/update DoseSeries");
        logger.log("Input: " + clientRequestObject.getQueryParams());
        logger.log("User: " + clientRequestObject.getSessionUser());

        DoseSeries savedSeries = doseSeriesService.addSeries(
            clientRequestObject.getSessionUser(), clientRequestObject.getBody());

        objectMapper.writeValue(outputStream, savedSeries);
    }

    public void getDoseSeriesForUser(
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
                Object.class));

        logger.log("Calling create/update DoseSeries");
        logger.log("Input: " + clientRequestObject.getQueryParams());
        logger.log("User: " + clientRequestObject.getSessionUser());

        List<DoseSeries> allSeries = doseSeriesService.getSeriesForUser(clientRequestObject.getSessionUser(),
            clientRequestObject.getSessionUser().getAmigoUser());

        objectMapper.writeValue(outputStream, allSeries);
    }
}
