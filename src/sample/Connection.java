package sample;

import javafx.application.Platform;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class Connection implements Runnable{

    private InetAddress ip;
    private int outgoingPort = 4004;
    private DatagramSocket socket;
    private boolean running = true;
    private byte[] buf = new byte[256];
    private RecieveUDP recieveUDP;
    private Controller controller;

    // Constructer
    public Connection(RecieveUDP recieveUDP, Controller controller) {
        this.recieveUDP = recieveUDP;
        this.controller = controller;
        try {
            socket = new DatagramSocket();
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    // Stop run();
    public void shutDown(){
        running = false;
    }

    // Broadcast UDP besked hvor ESP32 skal fange den og returnere sin egen IP
    public void broadcastMessage(String connectionMsg) throws IOException, InterruptedException {

        // 255.255.255.255 er en broadcast ip
        InetAddress broadcastAdress = InetAddress.getByName("255.255.255.255");

        socket.setBroadcast(true);

        byte[] buffer = connectionMsg.getBytes();

        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, broadcastAdress, outgoingPort);
        socket.send(packet);
        socket.setBroadcast(false);

        Thread.sleep(1000);
    }


    public void stopBroadcast(){
        running = false;
    }

    @Override
    public void run() {
        while(running){
            // Hvis der er en connection: stopbroadcast + ændre connect knappen til "connected"
            if(recieveUDP.isConnected()){
                stopBroadcast();
                Platform.runLater(
                        new Runnable() {
                            @Override
                            public void run() {
                                controller.connect.setText("Connected");
                            }
                        }
                );
                shutDown();
            }
            try {
                broadcastMessage("connectionAttempt");
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
