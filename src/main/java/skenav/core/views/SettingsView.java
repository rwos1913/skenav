package skenav.core.views;

import io.dropwizard.views.View;

import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;


public class SettingsView extends View {
	public SettingsView() {
		super("pageSettings.mustache");
	}
}
