package skenav.code;

import com.google.common.collect.ImmutableMap;
import io.dropwizard.Application;
import io.dropwizard.forms.MultiPartBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.dropwizard.views.ViewBundle;
import skenav.code.db.Database;
import skenav.code.resources.FileMgrResources;
import skenav.code.resources.HomeResources;
import skenav.code.resources.UploadResources;
import io.dropwizard.bundles.assets.ConfiguredAssetsBundle;

import java.io.File;

public class SkenavApplication extends Application<SkenavConfiguration> {
    public static void main(String[] args) throws Exception {
        new SkenavApplication().run(args);
        // call to database test method definitely clean up when it becomes needed
        Database database = Database.getInstance();
        String test = "test database file name";
        database.addFile(test);
    }

    private void environment_setup(SkenavConfiguration config, Environment environment) {
        File uploadDirectory = new File(config.getUploadDirectory() + "usercontent/");
        if (!uploadDirectory.exists()) {
            final boolean mkdirs = uploadDirectory.mkdirs();
            System.out.println("----" + mkdirs);
        }
    }

    @Override
    public void initialize(Bootstrap<SkenavConfiguration> bootstrap) {
       bootstrap.addBundle(new ConfiguredAssetsBundle(ImmutableMap.<String, String>builder()
            .put("/www","/static")
            .build()));

        bootstrap.addBundle(new ViewBundle<SkenavConfiguration>());
        bootstrap.addBundle(new MultiPartBundle());
    }


    @Override
    public void run(SkenavConfiguration configuration, Environment environment) {
        environment_setup(configuration,environment);
        final UploadResources uploadResources = new UploadResources(configuration.getUploadDirectory());
        final HomeResources homeResources = new HomeResources();
        final FileMgrResources fileMgrResources = new FileMgrResources();

        environment.jersey().register(MultiPartBundle.class);
        environment.jersey().register(uploadResources);
        environment.jersey().register(homeResources);
        environment.jersey().register(fileMgrResources);


    }
}
