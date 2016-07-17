package info.doseamigos.amigousers;

import com.google.inject.AbstractModule;

/**
 * Created by jking31cs on 7/8/16.
 */
public class AmigoUserGuiceModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(AmigoUserDao.class).to(MySQLAmigoUserDao.class);
        bind(AmigoUserService.class).to(DefaultAmigoUserService.class);
    }
}
