package info.doseamigos.doseevents;

import com.google.inject.AbstractModule;

/**
 * Guice Module that binds specifically DoseEvents related Services/Daos.
 */
public class DoseEventsGuiceModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(DoseEventService.class).to(MockDoseEventService.class);
    }
}
