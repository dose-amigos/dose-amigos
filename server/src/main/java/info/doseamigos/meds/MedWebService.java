package info.doseamigos.meds;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.util.IOUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import info.doseamigos.ClientRequestObject;
import info.doseamigos.CommonModule;
import info.doseamigos.amigousers.AmigoUser;
import info.doseamigos.authusers.AccessTokenDeserializer;
import info.doseamigos.authusers.AuthUser;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

/**
 * Rest Handlers for Meds.
 */
public class MedWebService {

    private MedService medService;
    private ObjectMapper objectMapper;

    public MedWebService() {
        Injector injector = Guice.createInjector(
            new CommonModule(),
            new MedGuiceModule()
        );
        this.medService = injector.getInstance(MedService.class);
        this.objectMapper = injector.getInstance(ObjectMapper.class);
    }

    /**
     * POST/PUT call that either adds or updates an existing med.
     * @param inputStream The request information coming in from the client.
     * @param context The LambdaContext.
     * @return The updated Med.
     */
    public void createOrUpdateNewMed(
        InputStream inputStream,
        OutputStream outputStream,
        Context context
    ) throws IOException {
        LambdaLogger logger = context.getLogger();

        //Logging purposes
        String streamContents = IOUtils.toString(inputStream);
        logger.log("Stream Contents: " + streamContents);


        ClientRequestObject<Med> clientRequestObject = objectMapper.readValue(
            streamContents,
            objectMapper.getTypeFactory().constructParametrizedType(
                ClientRequestObject.class,
                ClientRequestObject.class,
                Med.class));

        logger.log("Calling create/update Med");
        logger.log("Input: " + clientRequestObject.getBody());
        logger.log("User: " + clientRequestObject.getSessionUser());

        Med med = medService.saveMed(clientRequestObject.getSessionUser(), clientRequestObject.getBody());

        objectMapper.writeValue(outputStream, med);
    }

    /**
     * GET call that gets meds for a given user.
     * @param inputStream
     * @param outputStream
     * @param context
     * @throws IOException
     */
    public void getMedsForUser(
        InputStream inputStream,
        OutputStream outputStream,
        Context context
    ) throws IOException {

        LambdaLogger logger = context.getLogger();

        //Logging purposes
        String streamContents = IOUtils.toString(inputStream);
        logger.log("Stream Contents: " + streamContents);


        ClientRequestObject<Integer> clientRequestObject = objectMapper.readValue(
            streamContents,
            objectMapper.getTypeFactory().constructParametrizedType(
                ClientRequestObject.class,
                ClientRequestObject.class,
                Integer.class));

        logger.log("Calling create/update Med");
        logger.log("Input: " + clientRequestObject.getQueryParams());
        logger.log("User: " + clientRequestObject.getSessionUser());

        AmigoUser userToGetMedsFor = new AmigoUser();
        userToGetMedsFor.setAmigoUserId(clientRequestObject.getBody());
        List<Med> meds = medService.medsForUser(clientRequestObject.getSessionUser(), userToGetMedsFor);

        objectMapper.writeValue(outputStream, meds);
    }


}
