package info.doseamigos.sharerequests;

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
 * Where all Lambda handler methods live.
 */
public class ShareRequestWebService {

    private final ShareRequestService service;
    private final ObjectMapper objectMapper;

    /**
     * Creates guice injector and gets out the object mapper and service from it.
     */
    public ShareRequestWebService() {
        Injector injector = Guice.createInjector(
            new CommonModule(),
            new ShareRequestGuiceModule()
        );
        this.service = injector.getInstance(ShareRequestService.class);
        this.objectMapper = injector.getInstance(ObjectMapper.class);
    }

    /**
     * GET call that gets all pending share requests for a user.
     * @param inputStream
     * @param outputStream
     * @param context
     * @throws IOException
     */
    public void getSharedRequests(
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

        logger.log("Calling create/update Med");
        logger.log("Input: " + clientRequestObject.getQueryParams());
        logger.log("User: " + clientRequestObject.getSessionUser());

        List<ShareRequest> amigoRequests = service.getPendingShareRequests(clientRequestObject.getSessionUser());

        objectMapper.writeValue(outputStream, amigoRequests);
    }

    /**
     * POST call that creates a new share request.
     * @param inputStream
     * @param outputStream
     * @param context
     * @throws IOException
     */
    public void createNewShareRequest(
        InputStream inputStream,
        OutputStream outputStream,
        Context context
    ) throws IOException {
        LambdaLogger logger = context.getLogger();

        //Logging purposes
        String streamContents = IOUtils.toString(inputStream);
        logger.log("Stream Contents: " + streamContents);


        ClientRequestObject<ShareRequest> clientRequestObject = objectMapper.readValue(
            streamContents,
            objectMapper.getTypeFactory().constructParametrizedType(
                ClientRequestObject.class,
                ClientRequestObject.class,
                ShareRequest.class));

        logger.log("Calling create/update Med");
        logger.log("Input: " + clientRequestObject.getQueryParams());
        logger.log("User: " + clientRequestObject.getSessionUser());

        ShareRequest request = service.addNewShareRequest(
            clientRequestObject.getSessionUser(), clientRequestObject.getBody());

        objectMapper.writeValue(outputStream, request);
    }

    /**
     * PUT call that updates a share request.
     * @param inputStream
     * @param outputStream
     * @param context
     * @throws IOException
     */
    public void updateShareRequest(
        InputStream inputStream,
        OutputStream outputStream,
        Context context
    ) throws IOException {
        LambdaLogger logger = context.getLogger();

        //Logging purposes
        String streamContents = IOUtils.toString(inputStream);
        logger.log("Stream Contents: " + streamContents);


        ClientRequestObject<ShareRequest> clientRequestObject = objectMapper.readValue(
            streamContents,
            objectMapper.getTypeFactory().constructParametrizedType(
                ClientRequestObject.class,
                ClientRequestObject.class,
                ShareRequest.class));

        logger.log("Calling create/update Med");
        logger.log("Input: " + clientRequestObject.getQueryParams());
        logger.log("User: " + clientRequestObject.getSessionUser());

        ShareRequest request = service.updateShareRequest(
            clientRequestObject.getSessionUser(), clientRequestObject.getBody());

        objectMapper.writeValue(outputStream, request);
    }

}
