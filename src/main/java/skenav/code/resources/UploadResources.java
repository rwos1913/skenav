package skenav.code.resources;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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
    // receives post from client
    public Response uploadFile(
            //gets inputstream from html form
            @FormDataParam("file") final InputStream fileInputStream,
            //gets content disposition from html form
            @FormDataParam("file") final FormDataContentDisposition contentDispositionHeader) throws IOException {
        String filename = contentDispositionHeader.getFileName();
        String filetype = parseFileType(filename);
        //TODO: better validate file type
        String datetime = getDateTime();
        String uploadedFileLocation = uploadDirectory + "usercontent/" + filename;
        // calls write to file
        writeToFile(fileInputStream, uploadedFileLocation);
        database.addFile(filename, filetype, datetime);
        String output = "File uploaded to : " + uploadedFileLocation;
        System.out.println(output);
        return Response.ok(output).build();

    }

// writes file from inputstream to disk
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
    //checks extension for file type
    private String parseFileType(String filename) {
        String filetype = "";
        int i = filename.lastIndexOf('.');
        if (i > 0) {
            filetype = filename.substring(i+1);
        }

        return filetype;
    }
    private String getDateTime() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        return dtf.format(now);
    }

}
