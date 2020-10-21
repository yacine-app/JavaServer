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

    private Request request = null;
    private File file = null;
    private StringBuilder builder = null;
    private long start, end;
    private String mimeType = "application/octet-stream";

    /**
     * 
     * @param entity
     * @param res
     * @throws IOException
     */
    protected static void setFileData(Entity entity, Response res) {
        if(entity == null)return;
        long length = 0L, lastModified = 0L;
        if(entity.file == null){
            entity.mimeType = "text/html";
            lastModified = System.currentTimeMillis();
            length = (long) entity.builder.toString().getBytes().length;
            entity.start = 0L;
            entity.end = length;
        }else {
            lastModified = entity.file.lastModified();
            length = entity.file.length();
        }
        if(entity.isBrowserable()) res.setHeader("Content-Disposition", "inline");
        else res.setHeader("Content-Disposition", "attachment; filename=\"" + entity.file.getName() + "\"");
        res.setHeader("Last-Modified", new Date(lastModified));
        res.setHeader("Accept-Ranges", "bytes");
        res.setHeader("Keep-Alive", true);
        res.setHeader("ETag", entity.getEtag());
        res.setHeader("Content-Type", entity.mimeType);
        res.setHeader("Content-Length", entity.end - entity.start + 1);
        res.setHeader("Content-Range", "bytes " + entity.start + "-" + entity.end + "/" + length);
    }

    /**
     * 
     * @param file
     * @throws IOException
     */
    protected Entity(File file, Request request) throws IOException {
        this.request = request;
        if(!file.isDirectory()){
            this.file = file;
            this.start = 0L;
            this.end = file.length();
            String mimeType = Files.probeContentType(file.toPath());
            this.mimeType = mimeType != null && !mimeType.isBlank() ? mimeType : this.mimeType;
        }else buildDirectoryExplorer(file);
    }

    /**
     * 
     * @param file
     */
    private void buildDirectoryExplorer(File file) {
        builder = new StringBuilder();
        builder.append("<!DOCTYPE html>");
        builder.append("<html>");
        builder.append("<head>");
        builder.append("<link rel=\"stylesheet\" href=\"/style/files.css\"></link>");
        builder.append("<title>Files | "+ file.getName() + "</title>");
        builder.append("</head>");
        builder.append("<body>");
        builder.append("<div class=\"files_explorer\">");
        for(File f: file.listFiles()){
            String a = f.getName();
            builder.append(String.format("<div class=\"file_link\"><a href=\"%s/%s\">%s</a></div>", this.request.getPath(), a, a.subSequence(0, a.lastIndexOf("."))));
        }
        builder.append("</div>");
        builder.append("</body>");
        builder.append("</html>");
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
     * @return
     */
    protected boolean isBrowserable(){
        System.out.println(mimeType);
        return mimeType.contains("video") | mimeType.contains("image") | mimeType.contains("audio") | mimeType.contains("text");
    }

    /**
     * 
     * @param outputStream
     * @throws IOException
     */
    protected void copyToOutputStream(OutputStream outputStream) throws IOException {
        if(this.file == null){
            outputStream.write(this.builder.toString().getBytes());
            outputStream.flush();
            return;
        }
        FileInputStream inputStream = new FileInputStream(file);
        final long maxread = 1024 * 24;
        byte[] buffer = new byte[(int) maxread];
        long remaining = end - start + 100;
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

    protected String getEtag(){
        return this.file == null ? "" : this.file.getName();
    }
}
