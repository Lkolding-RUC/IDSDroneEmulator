package sample;

import javafx.application.Platform;

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
    private double scaleX;
    private double scaleY;
    private double rotation;
    private boolean running = true;
    private double pitchAdapter;


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

        pitchAdapter = Double.parseDouble(pitch)/75.0;


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
                        if(offsetX + Double.parseDouble(roll) < 500 && offsetX + Double.parseDouble(roll) > 0){
                            controller.Drone.setLayoutX(offsetX + Double.parseDouble(roll));
                        }
                        if(offsetY + Double.parseDouble(throttle) < 450 && offsetY + Double.parseDouble(throttle) >0 ){
                            controller.Drone.setLayoutY(offsetY + Double.parseDouble(throttle));
                        }

                        scaleX = controller.Drone.getScaleX();
                        scaleY = controller.Drone.getScaleY();
                        if(scaleX + pitchAdapter > 0.5 && scaleY + pitchAdapter > 0.5 && scaleX + pitchAdapter < 3.0 && scaleY + pitchAdapter < 3.0){
                            controller.Drone.setScaleX(scaleX + pitchAdapter);
                            controller.Drone.setScaleY(scaleY + pitchAdapter);
                        }

                        rotation = controller.Drone.getRotate();
                        controller.Drone.setRotate(rotation + Double.parseDouble(yaw));

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
