package info.doseamigos.authusers;

import com.google.inject.AbstractModule;

/**
 * Created by jking31cs on 6/27/16.
 */
public class AuthUserGuiceModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(AuthUserService.class).to(MockAuthUserService.class);
        bind(AuthUserDao.class).to(MySQLAuthUserDao.class);
    }
}
