package info.doseamigos.authusers;

import com.google.inject.AbstractModule;

/**
 * Guice Module for AuthUser objects.
 */
public class AuthUserGuiceModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(AuthUserService.class).to(MockAuthUserService.class);
        bind(AuthUserDao.class).to(MySQLAuthUserDao.class);
    }
}
