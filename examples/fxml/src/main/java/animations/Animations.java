/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package animations;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;



/**
 *
 * @author toor
 */
public class Animations extends Application {
    
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader spinnerSceneLoader = new FXMLLoader(getClass().getResource("Spinner.fxml"));
        Parent root = (Parent) spinnerSceneLoader.load();
        
        SpinnerController ctrlrPointer = (SpinnerController) spinnerSceneLoader.getController();
        
        
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    /**
     *
		 * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}

