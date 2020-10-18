package net;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.Date;
/**
 * Http body entity
 */
public class Entity {

    private File file;
    private long start, end;
    private String mimeType = "application/octet-stream";

    /**
     * 
     * @param entity
     * @param res
     * @throws IOException
     */
    protected static void setFileData(Entity entity, Response res) throws IOException {
        if(entity == null)return;
        res.setHeader("Last-Modified", new Date(entity.file.lastModified()));
        res.setHeader("Accept-Ranges", "bytes");
        res.setHeader("Keep-Alive", true);
        res.setHeader("Content-Type", Files.probeContentType(entity.file.toPath()));
        res.setHeader("Content-Length", entity.end - entity.start + 1);
        res.setHeader("Content-Range", "bytes " + entity.start + "-" + entity.end + "/" + entity.file.length());
    }

    /**
     * 
     * @param file
     * @throws IOException
     */
    protected Entity(File file) throws IOException {
        this.file = file;
        this.start = 0L;
        this.end = file.length();
        String mimeType = Files.probeContentType(file.toPath());
        this.mimeType = mimeType != null && !mimeType.isBlank() ? mimeType : this.mimeType;
    }

    /**
     * 
     * @param off
     * @param length
     */
    protected void setReadRange(long start, long end){
        this.start = start;
        this.end = Math.min(file.length(), end);
        System.out.println("Read end: " + this.end + " / File length: " + this.file.length());
    }

    /**
     * 
     * @param outputStream
     * @throws IOException
     */
    protected void copyToOutputStream(OutputStream outputStream) throws IOException {
        FileInputStream inputStream = new FileInputStream(file);
        final long maxread = 1024 * 24;
        byte[] buffer = new byte[(int) maxread];
        long remaining = end - start + 1;
        System.out.println("Remaining: "+ remaining +"\r");
        inputStream.skip(start);
        try {
            while(remaining > 0){
                int read = inputStream.read(buffer, 0, (int) Math.min(maxread, remaining));
                outputStream.write(buffer, 0, read);
                outputStream.flush();
                remaining -= read;
                System.out.print("Remaining: "+ remaining +"\r");
            }
        }catch(IndexOutOfBoundsException e){

        } finally{
            inputStream.close();
        }
    }

    /**
     * @return the mimeType
     */
    protected String getMimeType() { return mimeType; }
}
