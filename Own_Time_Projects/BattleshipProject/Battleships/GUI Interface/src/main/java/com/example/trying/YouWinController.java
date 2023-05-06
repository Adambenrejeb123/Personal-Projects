package com.example.trying;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class YouWinController {
    private Stage stage;
    private Scene scene;

    private Parent root;
    @FXML
    private Button Quit;
    public void switchtoMain(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("hello-view.fxml"));
        stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
    @FXML
    public void Test(ActionEvent e){ // test = exit
        Stage stage = (Stage) Quit.getScene().getWindow();
        stage.close();
        System.out.println("System Exit?");
        System.exit(0);

    }
}
