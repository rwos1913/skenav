package skenav.core.resources;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bouncycastle.jcajce.provider.digest.SHA3;
import org.bouncycastle.util.encoders.Hex;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import javax.ws.rs.*;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.*;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import skenav.core.Cache;
import skenav.core.OS;
import skenav.core.db.Database;
import skenav.core.security.Crypto;
import skenav.core.security.UserManagement;

@Path("upload")
@Produces(MediaType.TEXT_HTML)
public class UploadResources {
    private String hashFilename;
    Database database;
    public UploadResources(Database database, String hashFilename) {
        this.hashFilename = hashFilename;
        this.database = database;

    }
    String uploadDirectory = OS.getUserContentDirectory();


    @POST
    //@Path("upload")
    //@Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    // receives post from client
    public Response uploadFile(
            //gets inputstream from html form
            @FormDataParam("file") final InputStream fileInputStream,
            //gets content disposition from html form
            @FormDataParam("file") final FormDataContentDisposition contentDispositionHeader,
            @CookieParam("SkenavAuth") final Cookie cookie) throws IOException {
        String filename = contentDispositionHeader.getFileName();
        String filetype = parseFileType(filename);
        String filehash = hashString(filename);
        String user = parseCookieForUserName(cookie);
        System.out.println("file name from content dispo header is:" + filename);
        String filestring;
        //boolean b1 = Boolean.parseBoolean(hashFilename);
        if (hashFilename.equals("true")) {
            filestring = filehash;
        }
        else {
            filestring = filename;
        }
        //TODO: allow appending of file extension to hashed file names or just do that by defualt idk
        String datetime = getDateTime();
        String uploadedFileLocation = uploadDirectory + OS.pathSeparator() + filestring;
        // calls write to file
        writeToFile(fileInputStream, uploadedFileLocation);
        database.addFile(filehash, filename, filetype, datetime, user);
        String output = "Upload successful!";
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
        String filetype;
        String rawfiletype;
        int i = filename.lastIndexOf('.');
        if (i > 0) {
            rawfiletype = filename.substring(i+1);
            filetype = makeTypePretty(rawfiletype);
        }
        else if (i == -1) {
            filetype = "unknown";
        }
        else {
            throw new WebApplicationException(400);
        }

        return filetype;
    }
    private String makeTypePretty(String rawfiletype) {
        String rft = rawfiletype.toLowerCase();
        String prettyfiletype;
        System.out.println(rft);
        if (rft.equals("jpg")) {
            prettyfiletype = "JPEG image";
        }
        else if (rft.equals("pdf")) {
            prettyfiletype= "PDF document";
        }
        else if (rft.equals("png")) {
            prettyfiletype= "PNG image";
        }
        else if (rft.equals("txt")) {
            prettyfiletype= "Plain Text Document";
        }
        else if (rft.equals("docx") || rft.equals("doc")) {
            prettyfiletype= "Word Document";
        }
        else if (rft.equals("rtf")) {
            prettyfiletype = "RTF Document";
        }
        else if (rft.equals("mp4")) {
            prettyfiletype = "MP4 Video";
        }
        else if (rft.equals("mkv")) {
            prettyfiletype = "MKV Video";
        }
        else {
            prettyfiletype = "unknown extension: " + rawfiletype;
        }
        return prettyfiletype;
    }
    private String getDateTime() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        return dtf.format(now);
    }
    private String hashString (String input) {
        SHA3.DigestSHA3 digestSHA3 = new SHA3.Digest512();
        byte[] digest = digestSHA3.digest(input.getBytes());
        SecureRandom random = new SecureRandom();
        String output = Hex.toHexString(digest);
        return output;
    }

    private String parseCookieForUserName(Cookie cookie) throws JsonProcessingException {
        Map<String, String> cookiemap = UserManagement.cookieToMap(cookie);
        String username = cookiemap.get("username");
        return username;
    }

}
