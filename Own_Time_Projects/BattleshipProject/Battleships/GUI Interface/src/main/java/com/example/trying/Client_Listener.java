package com.example.trying;
import com.example.trying.Spiellogik.*;

import javafx.application.Platform;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;


public class Client_Listener implements Runnable{

    private Socket server;
    private BufferedReader reader;
    private PrintWriter writer;

    public Client_Listener ( Socket S) throws IOException{
        this.server = S;
        this.reader = new BufferedReader(new InputStreamReader(server.getInputStream()));
        this.writer = new PrintWriter(server.getOutputStream());
    }




    @Override
    public void run() {
       String serverCommand;
    try {
        while (!Client_Thread.Stop){
       
        serverCommand = reader.readLine();
        if (serverCommand == null){throw new IOException();}    // No Server Connection
        else {
        if (serverCommand.startsWith("/ws2")){           // The Command given by the server at the start of the Connection , so to assign it a proper name
           int Index =  serverCommand.indexOf("2");
           client.Own_Name = serverCommand.substring(Index+2);
        //    System.out.println("Intiliazed Name");

        
        } else  if (serverCommand.startsWith("/txtb")){           // The Command given by the server at the start of the Connection , so to assign it a proper name
            int Index =  serverCommand.indexOf("b");
            IpController.playControl.PreviousMessage += "\n"+"Player1: "+serverCommand.substring(Index+2);
            IpController.playControl.Chat.setText(IpController.playControl.PreviousMessage);
            IpController.playControl.Chat.setScrollTop(Double.MAX_VALUE);

            System.out.println("Debug :"+serverCommand);
 
        }else  if (serverCommand.startsWith("/txtc")){           // The Command given by the server at the start of the Connection , so to assign it a proper name
            int Index =  serverCommand.indexOf("c");
            IpController.playControl.PreviousMessage += "\n"+serverCommand.substring(Index+2);
            IpController.playControl.Chat.setText(IpController.playControl.PreviousMessage);
            IpController.playControl.Chat.setScrollTop(Double.MAX_VALUE);
            System.out.println("Debug :"+serverCommand);
 
         } else if (serverCommand.startsWith("/spl")){
            // input like "/spl 11"
            int Index =  serverCommand.indexOf("l");
            Integer Two = Integer.valueOf(serverCommand.substring(Index+2)) ;
            ClientInput.SetClientCoordinates(Two / 10,Two % 10);
            System.out.println("Passed "+serverCommand); // DEBUG
            // IpController.playControl.Chat.setText(IpController.playControl.PreviousMessage); // UPDATE 
            IpController.playControl.Chat.setScrollTop(Double.MAX_VALUE);
            

         }else if (serverCommand.startsWith("/hit")){
           PlayingController.HitAllowed = true;
        //    PlayingController.ClientNotif.setText("Your Turn");
        
        Platform.runLater( new Runnable() {
            @Override public void run(){
            IpController.playControl.ClientNotif.setText("Your Turn");}
       } );

 
         } else if ( serverCommand.startsWith("/SURR")){
            //CLOSES CLIENT GAME THREAD 
            // HelloController.ipControl.ClientGame.stop();;;
            // System.out.printf("Running : %b \n", HelloController.ipControl.ClientGame.isInterrupted());
            IpController.playControl.PreviousMessage += "\nYou won! bras omek o5rej mil programme";
            IpController.playControl.Chat.setText(IpController.playControl.PreviousMessage);
            IpController.playControl.Chat.setScrollTop(Double.MAX_VALUE);

            // System.out.println("SURRENDER PASSED");

              
            System.out.println("FEEDBACK 1 ");
            Client_Thread.Stop= true;
            //WIP 8.7.2022
            IpController.playControl.changeScene( "YouWin.fxml");

            //

            
            

         }else
         if (serverCommand.startsWith("/HORIZONTAL")){
            Board.IsVertical=false ; 

         }else 
         if (serverCommand.startsWith("/VERTICAL")){
            Board.IsVertical=true  ;

        }else 
        
        System.out.println(serverCommand);}
        }
    }catch ( IOException e ) {
        System.out.println("[Client] Disconnected");
        try {
            if ( !Board.legit ){
            IpController.playControl.changeScene( "YouWin.fxml");
            Client_Thread.Stop= true;}
           
        } catch (Exception ee) {
           System.out.println("No Change Scene was possible ");
        }

    } catch (Exception e) {
        System.out.println("[Client_Listener] Couldnt Recieve Server responce");
        e.printStackTrace();
    }finally{
        writer.close();
        System.out.println("[ClientListener] Closed");
    }
       
        
    }
    
}
