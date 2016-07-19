package info.doseamigos;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import info.doseamigos.authusers.IdTokenDeserializer;
import info.doseamigos.authusers.AuthUser;

import javax.inject.Provider;

/**
 * Provider for the ObjectMapper that maps the input stream coming in from the client.
 */
public class ObjectMapperProvider implements Provider<ObjectMapper> {
    @Override
    public ObjectMapper get() {
        ObjectMapper objectMapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addDeserializer(AuthUser.class, new IdTokenDeserializer());
        objectMapper.registerModule(module);

        return objectMapper;
    }
}
