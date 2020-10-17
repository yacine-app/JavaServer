package net;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

/**
 * HTTP/1.1 request class java
 * @author yacine-app
 * @version 1.0
 */
public class Request {

    private ClientHandler clientHandler;
    private String method, version, host, path;
    private HashMap<String, String> requestHeaders = new HashMap<>();

    /**
     * 
     * @param clientHandler
     * @throws ClientHandlerException
     * @throws IOException
     */
    protected Request(ClientHandler clientHandler) throws ClientHandlerException, IOException {
        this.clientHandler = clientHandler;
        InputStream inputStream = clientHandler.socket.getInputStream();
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        String[] a = bufferedReader.readLine().split(" ");
        this.method = a[0];
        this.path = a[1];
        this.version = a[2];
        this.host = bufferedReader.readLine().split(ClientHandler.HEADER_SPLITER)[1];
        String b;
        while((b = bufferedReader.readLine()).length() > 0){
            String[] c = b.split(ClientHandler.HEADER_SPLITER);
            requestHeaders.put(c[0], c[1]);
        }
        //inputStreamReader.close();
        //bufferedReader.close();
    }

    /**
     * 
     * @param path
     * @param callBack
     * @throws ClientHandlerException
     * @throws IOException
     */
    public void get(String path, Interfaces.CallBack<String> callBack) throws ClientHandlerException, IOException {
        if(path.equals(this.path) || (path.equals("*"))) callBack.onValue(this.clientHandler.getWorkspace());
    }

    /**
     * Use default workspace get web resourses, if file in workspace folder matches the requested one it will be sent to the client with its meta data otherwise it will report 404/403 if request a forler without default index.html file or unreadable file.
     * @param path choose the path of your workspace folder.
     * @throws ClientHandlerException when an error happened within ClientHandler class (Server error).
     * @throws IOException when an IO error happened while loading or buffering a file from workspace.
     */
    public void use(String path) throws ClientHandlerException, IOException {
        String dirPath = this.path.equals("/") ? "/index.html" : this.path;
        File file = new File(this.clientHandler.getWorkspace() + path + dirPath);
        if(!file.exists())
            this.clientHandler.response.setResponseStatus(Response.HTTP_404);
        else if(file.isDirectory() || !file.canRead())
            this.clientHandler.response.setResponseStatus(Response.HTTP_403);
        else {
            this.clientHandler.response.setResponseStatus(Response.HTTP_200);
            this.clientHandler.response.setEntity(new Entity(file));
        }
        this.clientHandler.response.end();
    }

    /**
     * 
     * @param name name of request header
     * @return the value from request header
     */
    public String getHeaderValue(String name){ return this.requestHeaders.get(name); }

    /**
     * @return the method
     */
    public String getMethod() {
        return method;
    }

    /**
     * @return the version
     */
    public String getVersion() {
        return version;
    }

    /**
     * @return the path
     */
    public String getPath() {
        return path;
    }

    /**
     * @return the host
     */
    public String getHost() {
        return host;
    }
}
