package info.doseamigos.amigousers;

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
 * Lambda handlers class.
 */
public class AmigoWebService {

    private final AmigoUserService amigoUserService;
    private final ObjectMapper objectMapper;

    public AmigoWebService() {
        Injector injector = Guice.createInjector(
            new CommonModule(),
            new AmigoUserGuiceModule()
        );
        amigoUserService = injector.getInstance(AmigoUserService.class);
        objectMapper = injector.getInstance(ObjectMapper.class);
    }

    public void getCurrentUser(
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

        logger.log("Calling get current user");
        logger.log("Input: " + clientRequestObject.getQueryParams());
        logger.log("User: " + clientRequestObject.getSessionUser());

        objectMapper.writeValue(outputStream, clientRequestObject.getSessionUser().getAmigoUser());
    }

    public void getAmigos(
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

        logger.log("Calling get current user");
        logger.log("Input: " + clientRequestObject.getQueryParams());
        logger.log("User: " + clientRequestObject.getSessionUser());

        List<AmigoUser> amigoUsers = amigoUserService.getAmigosForAuthUser(clientRequestObject.getSessionUser());

        objectMapper.writeValue(outputStream, amigoUsers);
    }

    public void createNewAmigo(
        InputStream inputStream,
        OutputStream outputStream,
        Context context
    ) throws IOException {
        LambdaLogger logger = context.getLogger();

        //Logging purposes
        String streamContents = IOUtils.toString(inputStream);
        logger.log("Stream Contents: " + streamContents);


        ClientRequestObject<AmigoUser> clientRequestObject = objectMapper.readValue(
            streamContents,
            objectMapper.getTypeFactory().constructParametrizedType(
                ClientRequestObject.class,
                ClientRequestObject.class,
                AmigoUser.class));

        logger.log("Calling get current user");
        logger.log("Input: " + clientRequestObject.getQueryParams());
        logger.log("User: " + clientRequestObject.getSessionUser());

        AmigoUser newAmigo = amigoUserService.createNewAmigo(
            clientRequestObject.getSessionUser(),
            clientRequestObject.getBody()
        );

        objectMapper.writeValue(outputStream, newAmigo);
    }

    public void updateNewAmigo(
        InputStream inputStream,
        OutputStream outputStream,
        Context context
    ) throws IOException {
        LambdaLogger logger = context.getLogger();

        //Logging purposes
        String streamContents = IOUtils.toString(inputStream);
        logger.log("Stream Contents: " + streamContents);


        ClientRequestObject<AmigoUser> clientRequestObject = objectMapper.readValue(
            streamContents,
            objectMapper.getTypeFactory().constructParametrizedType(
                ClientRequestObject.class,
                ClientRequestObject.class,
                AmigoUser.class));

        logger.log("Calling get current user");
        logger.log("Input: " + clientRequestObject.getQueryParams());
        logger.log("User: " + clientRequestObject.getSessionUser());

        AmigoUser newAmigo = amigoUserService.updateAmigo(
            clientRequestObject.getSessionUser(),
            clientRequestObject.getBody()
        );

        objectMapper.writeValue(outputStream, newAmigo);
    }

    public void deleteNewAmigo(
        InputStream inputStream,
        OutputStream outputStream,
        Context context
    ) throws IOException {
        LambdaLogger logger = context.getLogger();

        //Logging purposes
        String streamContents = IOUtils.toString(inputStream);
        logger.log("Stream Contents: " + streamContents);


        ClientRequestObject<Long> clientRequestObject = objectMapper.readValue(
            streamContents,
            objectMapper.getTypeFactory().constructParametrizedType(
                ClientRequestObject.class,
                ClientRequestObject.class,
                Long.class));

        logger.log("Calling get current user");
        logger.log("Input: " + clientRequestObject.getQueryParams());
        logger.log("User: " + clientRequestObject.getSessionUser());

        AmigoUser newAmigo = amigoUserService.deleteAmigo(
            clientRequestObject.getSessionUser(),
            clientRequestObject.getBody()
        );

        objectMapper.writeValue(outputStream, newAmigo);
    }

}
