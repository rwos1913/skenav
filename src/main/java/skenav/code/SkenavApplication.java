package skenav.code;

import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.forms.MultiPartBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.dropwizard.views.ViewBundle;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import skenav.code.health.SkenavHealthCheck;
import skenav.code.resources.SkenavResources;

public class SkenavApplication extends Application<SkenavConfiguration> {
    public static void main(String[] args) throws Exception {
        new SkenavApplication().run(args);
    }

    @Override
    public void initialize(Bootstrap<SkenavConfiguration> bootstrap) {
        bootstrap.addBundle(new AssetsBundle("/assets/index.html", "/index", null, "index.html"));
        bootstrap.addBundle(new ViewBundle<>());
    }

    @Override
    public void run(SkenavConfiguration configuration, Environment environment) {
        environment.jersey().register(MultiPartFeature.class);
        environment.jersey().register(SkenavResources.class);
    }
}
