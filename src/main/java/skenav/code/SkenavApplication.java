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
    }

    private void environment_setup(SkenavConfiguration config, Environment environment) {
        File uploadDirectory = new File(config.getUploadDirectory() + "usercontent/");
        File dbFile = new File(config.getUploadDirectory() + "usercontent/database.mv.db");
        if (!uploadDirectory.exists()) {
            final boolean mkdirs = uploadDirectory.mkdirs();
            System.out.println("----" + mkdirs);
        }
        if (!dbFile.exists()) {
            Database.createTable();
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
        Database database = new Database();

        final UploadResources uploadResources = new UploadResources(configuration.getUploadDirectory());
        final HomeResources homeResources = new HomeResources();
        //final HomeResources homeResources = new HomeResources(database);
        final FileMgrResources fileMgrResources = new FileMgrResources();

        String test = "test file name";
        database.addFile(test);
        environment.jersey().register(MultiPartBundle.class);
        environment.jersey().register(uploadResources);
        environment.jersey().register(homeResources);
        environment.jersey().register(fileMgrResources);


    }
}
