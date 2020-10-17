package net;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Date;
/**
 * Http body entity
 */
public class Entity {

    private byte[] data;
    private File file;

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
        res.setHeader("Content-Type", Files.probeContentType(entity.file.toPath()));
        res.setHeader("Content-Length", entity.file.length());
    }

    /**
     * 
     * @param file
     * @throws IOException
     */
    protected Entity(File file) throws IOException {
        this.file = file;
        data = Files.readAllBytes(file.toPath());
    }

    /**
     * @return the data
     */
    protected byte[] getData() { return data; }
}
