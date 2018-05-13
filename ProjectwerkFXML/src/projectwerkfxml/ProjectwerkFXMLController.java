/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projectwerkfxml;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import mqttbiometricdataservice.IMqttDataHandler;
import mqttbiometricdataservice.MqttBiometricDataService;

/**
 *
 * @author jensv
 */
public class ProjectwerkFXMLController implements Initializable, IMqttDataHandler {

    @FXML
    private Label heartbeat;
    @FXML
    private Button start;

    private MqttBiometricDataService jens_heartbeat;
    private MqttBiometricDataService jens_temperature;
    private MqttBiometricDataService jens_x_value;
    private MqttBiometricDataService jens_y_value;
    private MqttBiometricDataService jens_z_value;

    @FXML
    private void handleStart(ActionEvent event) {
        System.out.println("Starting biometric station.");
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        jens_heartbeat = new MqttBiometricDataService("jens", "heartbeat");
        jens_heartbeat.setDataHandler(this);
        jens_temperature = new MqttBiometricDataService("jens", "temperature");
        jens_temperature.setDataHandler(this);
        jens_x_value = new MqttBiometricDataService("jens", "accelero_x_value");
        jens_x_value.setDataHandler(this);
        jens_y_value = new MqttBiometricDataService("jens", "accelero_y_value");
        jens_y_value.setDataHandler(this);
        jens_z_value = new MqttBiometricDataService("jens", "accelero_z_value");
        jens_z_value.setDataHandler(this);
        disconnectClientOnClose();
    }

    @Override
    public void dataArrived(String channel, String data) {
        System.out.println("Received data (on channel = " + channel + "): " + data);

        // Om van die cross-thread error af te komen heb je onderstaand stukje code nodig.
        // Gevonden op stack-overflow https://stackoverflow.com/questions/11690683/javafx-update-ui-from-another-thread
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                //Update UI here     
                heartbeat.setText(data);
            }
        });
    }
    
    private void disconnectClientOnClose() {
        // Source: https://stackoverflow.com/a/30910015
        start.sceneProperty().addListener((observableScene, oldScene, newScene) -> {
            if (oldScene == null && newScene != null) {
                // scene is set for the first time. Now its the time to listen stage changes.
                newScene.windowProperty().addListener((observableWindow, oldWindow, newWindow) -> {
                    if (oldWindow == null && newWindow != null) {
                        // stage is set. now is the right time to do whatever we need to the stage in the controller.
                        ((Stage) newWindow).setOnCloseRequest((event) -> {
                            jens_heartbeat.disconnect();
                            jens_temperature.disconnect();
                            jens_x_value.disconnect();
                            jens_y_value.disconnect();
                            jens_z_value.disconnect();
                        });
                    }
                });
            }
        });
    }
}
