package server;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.ServerSocket;

import net.ClientHandler;
import net.ClientHandlerException;
import net.Interfaces;
import net.Request;
import net.Response;

public class Main implements Interfaces.OnClientRequest {

    private static final int PORT = 80;
    private static final String HOSTNAME = "192.168.1.3";

    private static ServerSocket serverSocket;

    /**
     * 
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        serverSocket = new ServerSocket(PORT, 50, Inet4Address.getByName(HOSTNAME));
        System.out.println(String.format("http://%s:%s", HOSTNAME, PORT));
        while(true){
            try {
                ClientHandler clientHandler = new ClientHandler(serverSocket.accept());
                clientHandler.setOnClientRequest(new Main());
                clientHandler.handle();
            }catch(ClientHandlerException e){
                
            }
        }
    }

    /**
     * Close the server socket from external program (Not supported yet).
     * @throws IOException
     */
    public static void close() throws IOException {
        serverSocket.close();
    }

    @Override
    public boolean onClientRequest(Request req, Response res) throws ClientHandlerException, IOException {
        System.out.println(req.getMethod());
        req.use("/DOC");
        return false;
    }
    
}