package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {

    /**
     * Server details.
     **/
    private final String server = "localhost";
    private final int port = 50000; // 50000 - 50100 currently open on router at home.


    private Socket socket;
    private ObjectOutputStream outStream;
    private ObjectInputStream inStream;

    /**
     * Open connection to server.
     **/
    protected void initialize() throws IOException {
        System.out.println("Starting client. Connecting to " + server + " on port " + port + ".");
        socket = new Socket(server, port);
        outStream = new ObjectOutputStream(socket.getOutputStream());
        inStream = new ObjectInputStream(socket.getInputStream());
        System.out.println("Client connected.");
    }

    /**
     * Send a java object to server.
     **/
    protected void sendObject(Object obj) throws IOException {
        outStream.writeObject(obj);
    }

    /**
     * Listen for a java object from server.
     **/
    protected Object receiveObject() throws ClassNotFoundException, IOException {
        return inStream.readObject();
    }

    /**
     * Close all streams / connections.
     **/
    public void closeAll() {
        try {
            outStream.close();
            inStream.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}