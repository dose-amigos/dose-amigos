package info.doseamigos.sharerequests;

import com.google.inject.AbstractModule;

/**
 * Guice Module for ShareRequest objects.
 */
public class ShareRequestGuiceModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(ShareRequestService.class).to(DefaultShareRequestService.class);
        bind(ShareRequestDao.class).to(MySQLShareRequestDao.class);
    }
}
