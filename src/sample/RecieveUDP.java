package sample;

import javafx.application.Platform;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RecieveUDP implements Runnable{
    private boolean running = true;
    private boolean connected = false;
    private InetAddress IP;
    private int incomingPort = 4000;
    private DatagramSocket socket;
    private byte[] buf = new byte[256];
    private String incomingMsg;
    private Controller controller;
    private ArrayList<String> rc;

    // Kan modtage en controller så vi kan stoppe broadcasten herinde fra.
    // Dette gør vi muligvis ikke mere
    public RecieveUDP(Controller controller) {
        this.controller = controller;
        try {
            socket = new DatagramSocket(incomingPort);
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }
    // Overloader constructoren så vi ikke behøves at give RecieveUDP en controller.
    // Måske ikke nødvendigt
    public RecieveUDP(){
        try {
            socket = new DatagramSocket(incomingPort);
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<String> getRc() {
        return rc;
    }

    public boolean isConnected() {
        return connected;
    }

    public InetAddress getIP() {
        return IP;
    }

    @Override
    public void run() {
        while(running){
            // Recieve packet
            DatagramPacket packet = new DatagramPacket(buf, buf.length);
            try {
                socket.receive(packet);
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Write input to variables
            incomingMsg = new String(packet.getData(), 0, packet.getLength());
            System.out.println(incomingMsg);

            if(incomingMsg.equals("connectionSucces")) {
                System.out.println("Controller Connected!");
                IP = packet.getAddress();
                connected = true;
            }

            // Check om de første 3 karaktere er "rc "
            // Sæt variabler så vi kan hente dem andre steder fra.
            if(incomingMsg.substring(0,Math.min(incomingMsg.length(), 3)).equals("rc ")){
                System.out.println("RC command");
                rc = new ArrayList<>(Arrays.asList(incomingMsg.split(" ")));
            }
        }
    }
}