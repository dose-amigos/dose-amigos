package info.doseamigos.meds;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * Rest Handlers for Meds.
 */
public class MedWebService {

    public MedWebService() {
        Injector injector = Guice.createInjector(
            //TODO add modules.
        );
    }

    /**
     * POST/PUT call that either adds or updates an existing med.
     * @param medInfo The med info coming in from the client.
     * @param context The LambdaContext.
     * @return The updated Med.
     */
    public Med createOrUpdateNewMed(Med medInfo, Context context) {
        LambdaLogger logger = context.getLogger();
        logger.log("Calling create/update Med");
        logger.log("Input: " + medInfo);

        medInfo.setMedId(1);
        return medInfo;
    }
}
