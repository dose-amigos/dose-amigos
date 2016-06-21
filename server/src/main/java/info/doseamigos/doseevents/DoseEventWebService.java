package info.doseamigos.doseevents;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.google.inject.Guice;
import com.google.inject.Injector;
import info.doseamigos.authusers.AuthUser;

import java.util.List;

/**
 * WebService for DoseEvents.
 */
public class DoseEventWebService {

    private final DoseEventService service;


    public DoseEventWebService() {
        Injector injector = Guice.createInjector(
            new DoseEventsGuiceModule()
        );
        service = injector.getInstance(DoseEventService.class);
    }

    public List<DoseEvent> getEvents(Context context) {
        LambdaLogger logger = context.getLogger();
        logger.log("Calling getEvents from DoseEventsWebService");
        //TODO get authuser from google auth stuff.
        return service.getEventsForUser(new AuthUser());
    }
}
