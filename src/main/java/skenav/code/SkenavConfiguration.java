package skenav.code;

import io.dropwizard.Configuration;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.constraints.NotEmpty;

public class SkenavConfiguration extends Configuration {
    @NotEmpty private String template;
    @NotEmpty private String defaultName = "stranger";
    @NotEmpty private String uploadDirectory;

    @JsonProperty public String getTemplate() {
        return this.template;
    }
    @JsonProperty public void setTemplate(String template) {
        this.template = template;
    }
    @JsonProperty public String getDefaultName() {
        return this.defaultName;
    }
    @JsonProperty public void setDefaultName(String name) {
        this.defaultName = name;
    }
    @JsonProperty public void setUploadDirectory(String uploadDirectory) {
        this.uploadDirectory = uploadDirectory;
    }
    @JsonProperty public String getUploadDirectory() {
        return this.uploadDirectory;
    }
}
