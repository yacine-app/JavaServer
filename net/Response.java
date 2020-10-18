package net;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * HTTP/1.1 response class java
 * @author yacine-app
 * @version 1.0
 */
public class Response {

    public static final int HTTP_200 = 200;
    public static final int HTTP_203 = 203;
    public static final int HTTP_206 = 206;
    public static final int HTTP_403 = 403;
    public static final int HTTP_404 = 404;
    
    private ClientHandler clientHandler;
    private HashMap<String, String> responseHeaders = new HashMap<>();
    private String headRes = "";
    private Entity entity = null;

    /**
     * 
     * @param clientHandler
     */
    public Response(ClientHandler clientHandler){
        this.clientHandler = clientHandler;
    }

    /**
     * @param entity the entity to set
     */
    public void setEntity(Entity entity) { this.entity = entity; }

    /**
     * 
     * @throws IOException
     * @throws ClientHandlerException
     */
    public synchronized void end() throws IOException, ClientHandlerException {
        OutputStream outputStream = this.clientHandler.socket.getOutputStream();
        outputStream.write(this.headRes.getBytes());
        setHeader("Date", new Date(System.currentTimeMillis()));
        Entity.setFileData(entity, this);
        Set<Map.Entry<String, String>> set = this.responseHeaders.entrySet();
        Iterator<Map.Entry<String, String>> iterator = set.iterator();
        while(iterator.hasNext()) {
            Map.Entry<String, String> entry = (Map.Entry<String, String>) iterator.next();
            String a = entry.getKey() + ClientHandler.HEADER_SPLITER + entry.getValue() + ClientHandler.LINE_SPLITER;
            outputStream.write(a.getBytes());
            //System.out.print(a);
        }
        outputStream.write(ClientHandler.LINE_SPLITER.getBytes());
        if(entity == null){
            this.clientHandler.socket.close();
            throw new ClientHandlerException();
        }
        this.entity.copyToOutputStream(outputStream);
        outputStream.write(ClientHandler.LINE_SPLITER.getBytes());
        outputStream.flush();
        outputStream.close();
        this.clientHandler.socket.close();
    }

    /**
     * 
     * @param message
     * @throws IOException
     * @throws ClientHandlerException
     */
    public synchronized void writeEnd(String message) throws IOException, ClientHandlerException {
        write(message);
        end();
    }

    /**
     * 
     * @param message
     * @throws IOException
     * @throws ClientHandlerException
     */
    public synchronized void write(String message) throws IOException, ClientHandlerException {
        if(message == null)return;
        this.clientHandler.socket.getOutputStream().write(message.getBytes());
    }

    /**
     * 
     * @param bytes
     * @throws IOException
     * @throws ClientHandlerException
     */
    public synchronized void write(byte[] bytes)throws IOException, ClientHandlerException {
        this.clientHandler.socket.getOutputStream().write(bytes);
    }

    /**
     * 
     * @param status
     */
    public void setResponseStatus(int status) {
        headRes = ClientHandler.VERSION + " " +  status + " " + ClientHandler.getStatusMessage(status) + ClientHandler.LINE_SPLITER;
    }

    /**
     * 
     * @param name
     * @param value
     */
    public void setHeader(String name, String value){
        responseHeaders.put(name, value);
    }

    /**
     * 
     * @param name
     * @param value
     */
    public void setHeader(String name, int value){ setHeader(name, String.valueOf(value)); }

    /**
     * 
     * @param name
     * @param value
     */
    public void setHeader(String name, long value){ setHeader(name, String.valueOf(value)); }

    /**
     * 
     * @param name
     * @param value
     */
    public void setHeader(String name, float value){ setHeader(name, String.valueOf(value)); }

    /**
     * 
     * @param name
     * @param value
     */
    public void setHeader(String name, Date value){ setHeader(name, value.toString()); }

    /**
     * 
     * @param name
     * @param value
     */
    public void setHeader(String name, boolean value){ setHeader(name, String.valueOf(value)); }

}
