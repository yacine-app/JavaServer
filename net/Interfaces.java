package net;

import java.io.IOException;

public class Interfaces {
    
    public interface OnClientRequest {
        /**
         * 
         * @param req
         * @param res
         * @return
         * @throws ClientHandlerException
         */
        public boolean onClientRequest(Request req ,Response res) throws ClientHandlerException, IOException;
    }

    public interface CallBack<T> {
        /**
         * 
         * @param t
         * @throws ClientHandlerException
         * @throws IOException
         */
        void onValue(T t) throws ClientHandlerException, IOException;
    }
}