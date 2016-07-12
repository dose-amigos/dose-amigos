package info.doseamigos.doseseries;

import com.google.inject.AbstractModule;

/**
 * Guice Module for DoseSeries stuff.
 */
public class DoseSeriesGuiceModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(DoseSeriesService.class).to(DefaultDoseSeriesService.class);
        bind(DoseSeriesDao.class).to(MySQLDoseSeriesDao.class);
    }
}
