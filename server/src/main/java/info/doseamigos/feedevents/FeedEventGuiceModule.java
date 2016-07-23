package info.doseamigos.feedevents;

import com.google.inject.AbstractModule;

/**
 * Guice Module for FeedEvents.
 */
public class FeedEventGuiceModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(FeedEventService.class).to(DefaultFeedEventService.class);
        bind(FeedEventDao.class).to(MySQLFeedEventDao.class);
    }
}
