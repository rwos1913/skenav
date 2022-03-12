package skenav.core.views;

import io.dropwizard.views.View;

public class SetupView extends View {
    private final String uploaddirectory;
    public SetupView(String uploaddirectory) {
        super("pageSetup.mustache");
        this.uploaddirectory = uploaddirectory;
    }
    public String getUploaddirectory(){
        return uploaddirectory;
    }
}
