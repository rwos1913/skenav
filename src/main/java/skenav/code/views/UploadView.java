package skenav.code.views;

import io.dropwizard.views.View;

public class UploadView extends View {
    public UploadView(String uploadDirectory) {

        super("pageUpload.mustache");
    }
}
