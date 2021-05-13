package skenav.code;

import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

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

    }
}
