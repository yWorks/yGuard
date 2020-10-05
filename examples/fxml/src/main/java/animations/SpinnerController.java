/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package animations;

import java.net.URL;
import java.util.Random;
import java.util.ResourceBundle;
import javafx.animation.RotateTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.util.Duration;
import com.yworks.util.annotation.Obfuscation;

/**
 *
 * @author toor
 */
public class SpinnerController implements Initializable {

    private double angleTurned = 0.0;
    
    @FXML private Group spinnerPolygon;
    @FXML private Label label;
    @FXML private Button button;
    
    @FXML
    private void handleButtonAction(ActionEvent event) {
        RotateTransition spinSpinner;
        spinSpinner = new RotateTransition(Duration.millis(1000), spinnerPolygon);
        double randomNum = angleToSpin();
        angleTurned = (angleTurned + randomNum) % 360.0;
        spinSpinner.setByAngle(randomNum);
        spinSpinner.play();
        int newLoc = landinglocation(angleTurned);

        System.out.println("newloc: " + newLoc);
        System.out.println("angleTurned " + angleTurned);
        
        spinSpinner.setOnFinished(new EventHandler<ActionEvent>() {

            @Obfuscation(exclude=true)
            @Override
            public void handle(ActionEvent event) {
                label.setText("you are on quartile: " + newLoc);
            }
        });
        

    }

    private double angleToSpin() {
        Random r = new Random();
        return (r.nextDouble() * 720);
    }
    
    private int landinglocation (double d) {
        return (int) Math.floor((360.0 - d) / 90);
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

}

