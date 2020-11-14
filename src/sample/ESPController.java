package sample;

import javafx.application.Platform;

import javax.crypto.spec.PSource;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.ArrayList;

public class ESPController implements Runnable{
    private String throttle;
    private String roll;
    private String pitch;
    private String yaw;
    private Controller controller;
    RecieveUDP recieveUDP;
    private double offsetX;
    private double offsetY;
    private boolean running = true;

    public ESPController(RecieveUDP recieveUDP, Controller controller) {
        this.controller = controller;
        this.recieveUDP = recieveUDP;
    }

    public void moveDrone(ArrayList<String> inputs){
        // Flere muligheder for at sætte værdierne
        // Vi får en arraylist string hvor hver element er en værdi.
        // 1. Vi kan sætte hvert element i arrayen til hver værdi manuelt --> starter med denne
        // 2. På en eller anden måde bruge et loop til at assigne hver værdi


        throttle = inputs.get(1);
        roll = inputs.get(2);
        pitch = inputs.get(3);
        yaw = inputs.get(4);

        // Set the values in the GUI
        Platform.runLater(
                new Runnable() {
                    @Override
                    public void run() {
                        controller.throttleLabel.setText(throttle);
                        controller.rollLabel.setText(roll);
                        controller.pitchLabel.setText(pitch);
                        controller.yawLabel.setText(yaw);


                        offsetX=controller.Drone.getLayoutX();
                        offsetY=controller.Drone.getLayoutY();
                        if(offsetX + Double.parseDouble(yaw) < controller.DronePane.getHeight() && offsetX + Double.parseDouble(yaw) > 0){
                            controller.Drone.setLayoutX(offsetX + Double.parseDouble(yaw));
                        }
                        if(offsetY + Double.parseDouble(throttle) < controller.DronePane.getWidth() && offsetY + Double.parseDouble(throttle) >0 ){
                            controller.Drone.setLayoutY(offsetY + Double.parseDouble(throttle));
                        }

                    }
                }
        );
    }


    @Override
    public void run() {
        while(running){
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            moveDrone(recieveUDP.getRc());
        }
    }
}
