package sample;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Arrays;

public class RecieveUDP implements Runnable{
    private boolean running = true;
    private boolean connected = false;
    private boolean rcValue = false;
    private InetAddress IP;
    private int incomingPort = 4000;
    private DatagramSocket socket;
    private byte[] buf = new byte[256];
    private String incomingMsg;
    private Controller controller;
    private ArrayList<String> rc;

    // Kan modtage en controller så vi kan stoppe broadcasten herinde fra.
    public RecieveUDP(Controller controller) {
        this.controller = controller;
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
        while(!connected){
            DatagramPacket packet = new DatagramPacket(buf, buf.length);
            try {
                socket.receive(packet);
            } catch (IOException e) {
                e.printStackTrace();
            }
            incomingMsg = new String(packet.getData(), 0, packet.getLength());
            System.out.println(incomingMsg);
            if(incomingMsg.equals("connectionSucces")) {
                System.out.println("Controller Connected!");
                IP = packet.getAddress();
                connected = true;
            }
        }
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


            // Check om de første 3 karaktere er "rc "
            // Sæt variabler så vi kan hente dem andre steder fra.
            if(incomingMsg.substring(0,Math.min(incomingMsg.length(), 3)).equals("rc ")){

                rc = new ArrayList<>(Arrays.asList(incomingMsg.split(" ")));
                if(!rcValue){
                    controller.droneInputs();
                }
                rcValue = true;

            }
        }
    }
}