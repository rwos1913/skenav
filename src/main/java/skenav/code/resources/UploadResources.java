package skenav.code.resources;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.*;
import skenav.code.db.Database;

@Path("upload")
@Produces(MediaType.TEXT_HTML)
public class UploadResources {
    private String uploadDirectory;
    Database database;
    public UploadResources(String uploadDirectory, Database database) {

        this.uploadDirectory = uploadDirectory;
        this.database = database;

    }


    @POST
    //@Path("upload")
    //@Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response uploadFile(
            @FormDataParam("file") final InputStream fileInputStream,
            @FormDataParam("file") final FormDataContentDisposition contentDispositionHeader) throws IOException {
        String filename = contentDispositionHeader.getFileName();
        String filetype = parseFileType(filename);
        String uploadedFileLocation = uploadDirectory + "usercontent/" + filename;
        writeToFile(fileInputStream, uploadedFileLocation);
        database.addFile(filename, filetype);
        String output = "File uploaded to : " + uploadedFileLocation;
        System.out.println(output);
        return Response.ok(output).build();

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
    private String parseFileType(String filename) {
        String filetype = "";
        int i = filename.lastIndexOf('.');
        if (i > 0) {
            filetype = filename.substring(i+1);
        }

        return filetype;
    }
}
