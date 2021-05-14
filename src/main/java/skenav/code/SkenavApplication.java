package skenav.code;

import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import skenav.code.health.SkenavHealthCheck;
import skenav.code.resources.SkenavResources;

public class SkenavApplication extends Application<SkenavConfiguration> {
    public static void main(String[] args) throws Exception {
        new SkenavApplication().run(args);
    }

    @Override
    public String getName() {
        return "hello-world";
    }
    
    @Override
    public void initialize(Bootstrap<SkenavConfiguration> bootstrap) {

    }

    @Override
    public void run(SkenavConfiguration configuration,
                    Environment environment) {
        final SkenavResources resource = new SkenavResources(
                configuration.getTemplate(),
                configuration.getDefaultName()
        );
        final SkenavHealthCheck healthCheck =
                new SkenavHealthCheck(configuration.getTemplate());
        environment.healthChecks().register("template", healthCheck);
        environment.jersey().register(resource);
    }
}
