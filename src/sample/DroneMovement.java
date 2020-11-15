package sample;

import javafx.application.Platform;

import java.util.ArrayList;

public class DroneMovement implements Runnable{
    private String throttle;
    private String roll;
    private String pitch;
    private String yaw;
    private Controller controller;
    private RecieveUDP recieveUDP;
    private double offsetX;
    private double offsetY;
    private double scaleX;
    private double scaleY;
    private double rotation;
    private boolean running = true;
    private double pitchAdapter;


    public DroneMovement(RecieveUDP recieveUDP, Controller controller) {
        this.controller = controller;
        this.recieveUDP = recieveUDP;
    }

    public void moveDrone(ArrayList<String> inputs){


        throttle = inputs.get(1);
        roll = inputs.get(2);
        pitch = inputs.get(3);
        yaw = inputs.get(4);


        //Divide controller input by 75 in order to slow down pitch speed. Otherwise drone will move forward/backward too fast
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
                        //set max horizontal travel distance of drone
                        if(offsetX + Double.parseDouble(roll) < 500 && offsetX + Double.parseDouble(roll) > 0){
                            controller.Drone.setLayoutX(offsetX + Double.parseDouble(roll));
                        }
                        //set max travel height of drone
                        if(offsetY + Double.parseDouble(throttle) < 450 && offsetY + Double.parseDouble(throttle) >0 ){
                            controller.Drone.setLayoutY(offsetY + Double.parseDouble(throttle));
                        }

                        scaleX = controller.Drone.getScaleX();
                        scaleY = controller.Drone.getScaleY();
                        //set maximum and minimum size of drone image to replicate distance traveling
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
                //without sleep, the values come in too fast, causing the GUI to freeze. This sleep allows the GUI to pick up and display the values
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            moveDrone(recieveUDP.getRc());
        }
    }
}
