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
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.EnumSet;

public class SkenavApplication extends Application<SkenavConfiguration> {
	public static void main(String[] args) throws Exception {
		new SkenavApplication().run(args);
		// test code
		if (!Setup.checkBreadcrumb()) {
			try {
				java.awt.Desktop.getDesktop().browse(new URI("http://localhost:8080/setup"));
			} catch (IOException e) {
				e.printStackTrace();
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}

		}
		else {
			Setup setup = new Setup();
			setup.finalizeSetup(false, null,null);
		}

	}

	private void environment_setup(SkenavConfiguration config, Environment environment) throws URISyntaxException, IOException {

		/*Database database = new Database();
		String uploaddirectory = database.getAppData("upload directory");
		System.out.println(uploaddirectory);
		File uploadDirectory = new File(uploaddirectory);
		File dbFile = new File(uploaddirectory + "database.mv.db");
		File hlsDirectory = new File(config.getUploadDirectory() + "usercontent" + OS.pathSeparator() + "hlstestfolder");

		if (!uploadDirectory.exists()) {
			final boolean mkdirs = uploadDirectory.mkdirs();
			System.out.println("----" + mkdirs);
			java.awt.Desktop.getDesktop().browse(new URI("http://localhost:8080/setup"));
		}
		if (!dbFile.exists()) {
			Database.createTable();
			//TODO: Check if crypto seed specifically exists
			Crypto.setCryptoSeed();

		}
		if (!hlsDirectory.exists()) {
			final boolean mkdirs = hlsDirectory.mkdirs();
			System.out.println("----" + mkdirs);
		}
		// creates thread pool
	   // ThreadManagement threadManagement = new ThreadManagement();
		//.threadManagement.createThreadPool(); */
	}

	@Override
	public void initialize(Bootstrap<SkenavConfiguration> bootstrap) {
		if (Setup.checkBreadcrumb()) {
			bootstrap.addBundle(new ConfiguredAssetsBundle(ImmutableMap.<String, String>builder()
					.put("/www", "/static")
					.put(OS.getUserContentDirectory(), "/files")
					.build()));
		}
		else {
			bootstrap.addBundle(new ConfiguredAssetsBundle(ImmutableMap.<String, String>builder()
					.put("/www", "/static")
					.build()));
		}
		bootstrap.addBundle(new ViewBundle<SkenavConfiguration>());
		bootstrap.addBundle(new MultiPartBundle());
	}


	@Override
	public void run(SkenavConfiguration configuration, Environment environment) throws URISyntaxException, IOException {
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
