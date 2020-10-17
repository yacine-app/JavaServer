package net;

import java.io.IOException;
import java.net.Socket;

public class ClientHandler {

    public static String getStatusMessage(int status){
        switch(status){
            case Response.HTTP_200:
                return "OK";
            case Response.HTTP_403:
                return "Forbidden";
            case Response.HTTP_404:
                return "Not found";
            default:
                return "Unknown";
        }
    }

    public static final String LINE_SPLITER = "\r\n";
    public static final String HEADER_SPLITER = ": ";
    public static final String VERSION = "HTTP/1.1";

    protected Socket socket;

    private String workspace = System.getProperty("user.dir");
    private Interfaces.OnClientRequest onClientRequest;
    protected Request request;
    protected Response response;
    private Thread thread;
    private Runnable runnable = new Runnable(){
        @Override
        public void run(){
            try {
                if(onClientRequest == null) throw new ClientHandlerException();
                onClientRequest.onClientRequest(request, response);
            }catch(ClientHandlerException | IOException e){
                //TODO: Handle client exception
            }
        }
    };

    /**
     * 
     * @param socket
     * @throws ClientHandlerException
     * @throws IOException
     */
    public ClientHandler(Socket socket) throws ClientHandlerException, IOException {
        this.socket = socket;
        this.request = new Request(this);
        this.response = new Response(this);

    }

    /**
     * 
     * @throws ClientHandlerException
     */
    public synchronized void handle() throws ClientHandlerException {
        this.thread = new Thread(runnable);
        thread.start();
    }

    //@SuppressWarnings("unused")
    public void removeOnClientRequest() { this.onClientRequest = null; }

    /**
     * @param onClientRequest the onClientRequest to set
     */
    public void setOnClientRequest(Interfaces.OnClientRequest onClientRequest) { this.onClientRequest = onClientRequest; }
    
    /**
     * @return the workspace
     */
    public String getWorkspace() { return workspace; }
}