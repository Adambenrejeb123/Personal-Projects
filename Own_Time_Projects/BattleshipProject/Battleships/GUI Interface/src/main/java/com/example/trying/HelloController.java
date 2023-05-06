package com.example.trying;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;



public class HelloController {
    @FXML
    private Button Exit;
    @FXML
    private AnchorPane scenePane;
    @FXML
    public ImageView Logo;
    private Stage stage;
    private Scene scene;
    public static IpController ipControl;

    private Parent root;
    public void switchtoIp(ActionEvent event) throws IOException {
        

        FXMLLoader loader = new FXMLLoader(getClass().getResource("Ip.fxml"));
        root = loader.load();

        ipControl = loader.getController();

        stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
    @FXML
    public void Test(ActionEvent e){ // test = exit
        Stage stage = (Stage) Exit.getScene().getWindow();
        stage.close();
        System.out.println("System Exit?");
        System.exit(0);

    }
    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Aasba lik hh");
    }
}