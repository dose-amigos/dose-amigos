package info.doseamigos.feedevents;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.util.IOUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Guice;
import com.google.inject.Injector;
import info.doseamigos.ClientRequestObject;
import info.doseamigos.CommonModule;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

/**
 * Class that holds Lambda Handlers for Feed Events.
 */
public class FeedEventWebService {

    private final ObjectMapper objectMapper;
    private final FeedEventService service;

    public FeedEventWebService() {
        Injector injector = Guice.createInjector(
            new CommonModule(),
            new FeedEventGuiceModule()
        );
        service = injector.getInstance(FeedEventService.class);
        objectMapper = injector.getInstance(ObjectMapper.class);
    }

    /**
     * Gets all feed events in the system.
     * @param inputStream
     * @param outputStream
     * @param context
     * @throws IOException
     */
    public void getFeedEvents(
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

        logger.log("Calling get feed events");
        logger.log("Input: " + clientRequestObject.getQueryParams());
        logger.log("User: " + clientRequestObject.getSessionUser());

        List<FeedEvent> feedEvents = service.getFeedEvents(clientRequestObject.getSessionUser());

        objectMapper.writeValue(outputStream, feedEvents);

    }
}
