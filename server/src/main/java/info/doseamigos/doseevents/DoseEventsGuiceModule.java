package info.doseamigos.doseevents;

import com.google.inject.AbstractModule;

/**
 * Created by jking31cs on 6/20/16.
 */
public class DoseEventsGuiceModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(DoseEventService.class).to(MockDoseEventService.class);
    }
}
