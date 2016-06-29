package info.doseamigos.meds;

import com.google.inject.AbstractModule;

/**
 * Guice Module binding Med related interfaces with implementations.
 */
public class MedGuiceModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(MedService.class).to(MockMedService.class);
    }
}
