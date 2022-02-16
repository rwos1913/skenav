package skenav.core;

import com.google.common.collect.ImmutableMap;
import io.dropwizard.Application;
import io.dropwizard.forms.MultiPartBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.dropwizard.views.ViewBundle;
import skenav.core.db.Database;
import skenav.core.resources.*;
import io.dropwizard.bundles.assets.ConfiguredAssetsBundle;
import skenav.core.security.Crypto;
import skenav.core.security.ServletRequestFilter;

import javax.servlet.DispatcherType;
import java.io.File;
import java.util.EnumSet;

public class SkenavApplication extends Application<SkenavConfiguration> {
	public static void main(String[] args) throws Exception {
		new SkenavApplication().run(args);
		// test code
		String testPassword = "password";
		String hashedpw = Crypto.hashPassword(testPassword);
		System.out.println(hashedpw);

	}

	private void environment_setup(SkenavConfiguration config, Environment environment) {
		File uploadDirectory = new File(config.getUploadDirectory() + "usercontent" + OS.pathSeparator());
		File dbFile = new File(config.getUploadDirectory() + "usercontent" + OS.pathSeparator() + "database.mv.db");
		File hlsDirectory = new File(config.getUploadDirectory() + "usercontent" + OS.pathSeparator() + "hlstestfolder");

		if (!uploadDirectory.exists()) {
			final boolean mkdirs = uploadDirectory.mkdirs();
			System.out.println("----" + mkdirs);
		}
		if (!dbFile.exists()) {
			Database.createTable();
			//TODO: Check if crypto seed specifically exists
			Crypto.setCryptoSeed();
			//java.awt.Desktop.getDesktop().browse();
		}
		if (!hlsDirectory.exists()) {
			final boolean mkdirs = hlsDirectory.mkdirs();
			System.out.println("----" + mkdirs);
		}
		// creates thread pool
	   // ThreadManagement threadManagement = new ThreadManagement();
		//.threadManagement.createThreadPool();
	}

	@Override
	public void initialize(Bootstrap<SkenavConfiguration> bootstrap) {
	   bootstrap.addBundle(new ConfiguredAssetsBundle(ImmutableMap.<String, String>builder()
			.put("/www", "/static")
			   .put(OS.getUserContentDirectory(), "/files")
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
		final VideoResources videoResources = new VideoResources(configuration.getUploadDirectory());
		final LoginResources loginResources = new LoginResources();
		final RegisterResources registerResources = new RegisterResources();
		final SetupResources setupResources = new SetupResources();
		// TEST METHODS
		//queryResources.viewFilesToJSON();

		environment.jersey().register(queryResources);
		environment.jersey().register(MultiPartBundle.class);
		environment.jersey().register(uploadResources);
		environment.jersey().register(homeResources);
		environment.jersey().register(fileMgrResources);
		environment.jersey().register(videoResources);
		environment.jersey().register(loginResources);
		environment.jersey().register(registerResources);
		environment.jersey().register(setupResources);
		environment.servlets().addFilter("custom filter name", new ServletRequestFilter()).addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, "/*");


	}
}
