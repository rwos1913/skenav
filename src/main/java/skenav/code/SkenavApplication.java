package skenav.code;

import com.google.common.collect.ImmutableMap;
import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.forms.MultiPartBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.dropwizard.views.ViewBundle;
import org.bouncycastle.jcajce.provider.digest.SHA3;
import org.bouncycastle.util.encoders.Hex;
import skenav.code.db.Database;
import skenav.code.resources.FileMgrResources;
import skenav.code.resources.HomeResources;
import skenav.code.resources.QueryResources;
import skenav.code.resources.UploadResources;
import io.dropwizard.bundles.assets.ConfiguredAssetsBundle;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class SkenavApplication extends Application<SkenavConfiguration> {
    public static void main(String[] args) throws Exception {
        new SkenavApplication().run(args);
        // test code

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
            .put("/www", "/static")
               .put("/assets", "/files")
            .build()));
        bootstrap.addBundle(new ViewBundle<SkenavConfiguration>());
        bootstrap.addBundle(new MultiPartBundle());
    }


    @Override
    public void run(SkenavConfiguration configuration, Environment environment) {
        environment_setup(configuration,environment);
        Database database = new Database();

        final UploadResources uploadResources = new UploadResources(configuration.getUploadDirectory(), database, configuration.getHashFilename());
        final HomeResources homeResources = new HomeResources();
        //final HomeResources homeResources = new HomeResources(database);
        final FileMgrResources fileMgrResources = new FileMgrResources();
        final QueryResources queryResources = new QueryResources(database);
        // TEST METHODS
        //queryResources.viewFilesToJSON();

        environment.jersey().register(queryResources);
        environment.jersey().register(MultiPartBundle.class);
        environment.jersey().register(uploadResources);
        environment.jersey().register(homeResources);
        environment.jersey().register(fileMgrResources);


    }
}
