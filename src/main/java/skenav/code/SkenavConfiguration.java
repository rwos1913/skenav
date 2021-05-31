package skenav.code;

import io.dropwizard.Configuration;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.bundles.assets.AssetsBundleConfiguration;
import io.dropwizard.bundles.assets.AssetsConfiguration;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class SkenavConfiguration extends Configuration implements AssetsBundleConfiguration {
    @NotEmpty private String uploadDirectory;
    @Valid
    @NotNull
    @JsonProperty private final AssetsConfiguration assets = AssetsConfiguration.builder().build();
    @JsonProperty public void setUploadDirectory(String uploadDirectory) {
        this.uploadDirectory = uploadDirectory;
    }
    @JsonProperty public String getUploadDirectory() {
        return this.uploadDirectory;
    }

    @Override
    public AssetsConfiguration getAssetsConfiguration() {
        return assets;
    }
}
