package com.example.trying;
import com.example.trying.Spiellogik.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class IpController {
    static public PlayingController playControl;
    @FXML
    public Label Feedback;

    @FXML 
    public TextField iP_Field;
    @FXML
    public TextField Port_Field;


    static public PlayingController2 playControl2;
    private Stage stage;
    private Scene scene;
    public Thread ClientGame;
    public Thread ServerGame;
    // private Parent root;
    public void switchtoMain(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("hello-view.fxml"));
        stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void switchtoPlaying(ActionEvent event) throws IOException {
        boolean Continue = false;
        

        if ( !iP_Field.getText().equals("") && !Port_Field.getText().equals("") ){
        server.IP = iP_Field.getText();
        try {
            server.PORT = Integer.valueOf(Port_Field.getText());
        } catch (Exception e) {
            Feedback.setText("Port Must be Numeric");
            return;
        }
        
        }else {Feedback.setText("Input is Empty");return;}

        
        
        try {
            Socket TestConnection = new Socket(server.IP, server.PORT); //Establiches connection to server
            Continue = true;
        } catch (Exception e) {
            System.out.println("No Server Connection ");
            Feedback.setText("Input Is Wrong/ No Server");
            System.out.println("IP ="+server.IP+"\n Port ="+ String.valueOf(server.PORT));
            e.printStackTrace();
           Continue = false;
        }
        // before Join , make sure that server is On 
        
      if ( Continue ){
        Thread T1=   new Thread(new Client_Thread());
        T1.start();
        ClientGame=  new Thread(new MainThreadClient());
        ClientGame.start();
        
       
        
        
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Playing.fxml"));
        Parent root = loader.load();

        playControl = loader.getController();
        

        stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
      }
    }
    
    public void switchtoHosting(ActionEvent event) throws IOException {

       if (!Port_Field.getText().equals("")  ){
        try {
            ServerThread.PORT = Integer.valueOf(Port_Field.getText());
        } catch (Exception e) {
            Feedback.setText("Port Must be Numeric");
            return;
        }
       }else {Feedback.setText("Please Set a Port");return;}
 

       try {
        ServerSocket TestSocket = new ServerSocket(ServerThread.PORT);
        System.out.println("Possible");
        TestSocket.close();
        
       } catch (Exception e) {
          Feedback.setText("Port already Used");
          return;
        }


        Thread T1=   new Thread(new ServerThread());
        T1.start();
        ServerGame = new Thread ( new MainThread());
        ServerGame.start();
        

        FXMLLoader loader = new FXMLLoader(getClass().getResource("Playing2.fxml"));
        Parent root = loader.load();

        playControl2 = loader.getController(); 

        
        stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }


    
}
