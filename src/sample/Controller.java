package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    public Button connect;
    public TableView inputTable;
    public TableColumn throttle;
    public TableColumn roll;
    public TableColumn pitch;
    public TableColumn yaw;
    public Label throttleLabel;
    public Label rollLabel;
    public Label pitchLabel;
    public Label yawLabel;

    // Måske lige gyldig?
    private InetAddress controllerIP;


    public void initialize(){
    }




    boolean running = false;
    RecieveUDP recieveUDP = new RecieveUDP(this);
    Thread udpListen = new Thread(recieveUDP);

    Connection broadcast = new Connection(recieveUDP, this);
    Thread udpBroadcast = new Thread(broadcast);

    ESPController espController = new ESPController(recieveUDP, this);
    Thread espControllerThread = new Thread(espController);

    // Only runnable once
    public void connect(ActionEvent actionEvent) throws IOException {
        if(!running){
            running = true;
            udpListen.start();
            connect.setText("Connecting...");
            udpBroadcast.start();
            espControllerThread.start();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
