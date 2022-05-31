package io.dropwizard.testing.junit;

import io.dropwizard.core.Application;
import io.dropwizard.core.Configuration;
import io.dropwizard.core.setup.Environment;
import io.dropwizard.testing.ConfigOverride;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.junit.ClassRule;
import org.junit.Test;

import java.util.Collections;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

public class DropwizardAppRuleWithoutConfigTest {
    @SuppressWarnings("deprecation")
    @ClassRule
    public static final DropwizardAppRule<Configuration> RULE = new DropwizardAppRule<>(TestApplication.class, null,
        ConfigOverride.config("server.applicationConnectors[0].port", "0"),
        ConfigOverride.config("server.adminConnectors[0].port", "0"));

    @Test
    public void runWithoutConfigFile() {
        Map<String, String> response = RULE.client().target("http://localhost:" + RULE.getLocalPort() + "/test")
            .request()
            .get(new GenericType<Map<String, String>>() {
            });
        assertThat(response).containsOnly(entry("color", "orange"));
    }

    public static class TestApplication extends Application<Configuration> {
        @Override
        public void run(Configuration configuration, Environment environment) throws Exception {
            environment.jersey().register(new TestResource());
        }
    }

    @Path("test")
    @Produces(MediaType.APPLICATION_JSON)
    public static class TestResource {
        @GET
        public Response get() {
            return Response.ok(Collections.singletonMap("color", "orange")).build();
        }
    }
}
