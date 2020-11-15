package sample;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

import java.io.IOException;

public class Controller{
    public Button connect;
    public Label throttleLabel;
    public Label rollLabel;
    public Label pitchLabel;
    public Label yawLabel;
    public ImageView Drone;
    public Pane DronePane;
    public Label ip;

    boolean running = false;
    RecieveUDP recieveUDP = new RecieveUDP(this);
    Thread udpListen = new Thread(recieveUDP);

    Connection broadcast = new Connection(recieveUDP, this);
    Thread udpBroadcast = new Thread(broadcast);

    // Only runnable once
    public void connect(ActionEvent actionEvent) throws IOException {
        if(!running){
            running = true;
            udpListen.start();
            connect.setText("Connecting...");
            udpBroadcast.start();
        }
    }

    DroneMovement droneMovement = new DroneMovement(recieveUDP, this);
    Thread droneMovementThread = new Thread(droneMovement);
    public void droneInputs(){
        droneMovementThread.start();
    }
}
