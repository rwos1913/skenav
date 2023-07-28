package skenav.core;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;


public class SkenavConfiguration extends Configuration {
	@NotEmpty
	private String usetls;

	@JsonProperty
	public String getUseTls() {
		return this.usetls;

	}
	@JsonProperty
	public void setUseTls(String usetls){
		this.usetls = usetls;
	}
}
