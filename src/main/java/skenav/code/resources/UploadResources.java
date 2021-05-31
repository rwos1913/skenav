package skenav.code.resources;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import skenav.code.views.UploadView;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.*;


@Path("/")
@Produces(MediaType.TEXT_HTML)
public class UploadResources {
    private String uploadDirectory;

    public UploadResources(String uploadDirectory) {
        this.uploadDirectory = uploadDirectory;
    }

    @POST
    @Path("upload")
    //@Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response uploadFile(
            @FormDataParam("file") final InputStream fileInputStream,
            @FormDataParam("file") final FormDataContentDisposition contentDispositionHeader) throws IOException {

        String uploadedFileLocation = uploadDirectory + "usercontent/" + contentDispositionHeader.getFileName();

        writeToFile(fileInputStream, uploadedFileLocation);
        String output = "File uploaded to : " + uploadedFileLocation;
        System.out.println(output);
        return Response.ok(output).build();

    }

    @GET
    public UploadView UploadView(String uploadDirectory) {
        return new UploadView(uploadDirectory);
    }

    private void writeToFile(InputStream fileInputStream, String uploadedFileLocation) throws IOException {
        int read;
        final int BUFFER_LENGTH = 1024;
        final byte[] buffer = new byte[BUFFER_LENGTH];
        OutputStream out = new FileOutputStream(new File(uploadedFileLocation));
        while ((read = fileInputStream.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
        out.flush();
        out.close();
    }
}
