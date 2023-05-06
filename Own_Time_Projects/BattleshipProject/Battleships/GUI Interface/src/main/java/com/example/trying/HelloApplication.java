package com.example.trying;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;

import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import java.io.IOException;

public class HelloApplication extends Application {
    public static Stage PrimaryStage ;
    @Override
    
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        PrimaryStage = stage;
        // Group root = new Group();

        PrimaryStage.setOnCloseRequest(e -> CloseProgram());
        PrimaryStage.setResizable(false);
        
        Image icon = new Image("Logo.png");
        Scene scene = new Scene(fxmlLoader.load(),Color.LIGHTBLUE);
        stage.getIcons().add(icon);
        stage.setTitle("BattleShips");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        Application.launch();
       
    }


    private  void CloseProgram(){ // test = exit
       
        PrimaryStage.close();
        System.out.println("System Exit1?");
        System.exit(0);

    }
}