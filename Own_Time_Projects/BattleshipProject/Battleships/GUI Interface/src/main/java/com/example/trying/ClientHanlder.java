package com.example.trying;



import com.example.trying.Spiellogik.*;

import javafx.application.Platform;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;

public class ClientHanlder implements Runnable {
    public String Naame;
    public Socket Client;
    public PrintWriter writer;
    public BufferedReader reader;
    public ArrayList<ClientHanlder> Clients ;
    public boolean Connected;


    public ClientHanlder (Socket clientSocket, ArrayList<ClientHanlder> C) throws IOException {
        this.Client = clientSocket;
        this.reader = new BufferedReader(new InputStreamReader(Client.getInputStream()));
        this.writer = new PrintWriter(Client.getOutputStream(), true);
        this.Clients = C;
        this.Connected= true;
    }
     


    @Override
    public void run() {

       try {
//_________________________________________________ essentially server functionality _____________________________________
         String s = null ;
         new Name();
         Naame = Name.Get_Name();      // Assign a Name to The Client in an Increasing fashion
         writer.println("/ws2 "+Naame);       // Pushes the Name Command to the Client
         EToAll("'"+ Naame +"' Connected");   // Custom Message send to Other Connected Users
         while ( ( s = reader.readLine() ) != null ) {
             if ( extract_text(s).startsWith("/dis")){ // if the Client wishes to disconnect Breaks the Listening Loop , thus ending the Connection and ending the thread
                EToAll("'"+ extract_name(s) +"' Disconnected"); // broadcasts that the client is disconnected 
                System.out.printf("[Server] '%s' Disconnected\n", extract_name(s));
                this.Connected=false;
                break;} else
             if ( extract_text(s).startsWith("/ale")){  
                System.out.printf("[Server] [%s] : Changed Name to [%s]\n",extract_Oldname(s) ,extract_name(s) ); // Feedback to Server System
                EToAll("'"+extract_Oldname(s)+"' Changed Name to ["+extract_name(s)+"]");
                Naame = extract_name(s);
             }  else
             if ( extract_text(s).startsWith("/list")){  
                writer.println("[Server] Connected Users : "+ Get_ALLnames());
               
            }  else
            if ( extract_text(s).startsWith("/Disconnected")){  
                // ServerThread.Counter--;
               
            }  else
            if ( extract_text(s).startsWith("/txtb")){  
               System.out.println(s);
            //    System.out.printf("[Debug] [%s] Message: %s\n" ,extract_name(s),extract_Oldname(s) );
               IpController.playControl2.PreviousMessage += "\nPlayer2: "+extract_Oldname(s);
               IpController.playControl2.Chat.setText(IpController.playControl2.PreviousMessage);
               IpController.playControl2.Chat.setScrollTop(Double.MAX_VALUE);
               // PRINT OUT TO CONSOLE ??
             } else
             if (extract_text(s).startsWith("/spl")){
                // input like Client0#/spl$00
                Integer Two = Integer.valueOf(extract_Oldname(s)) ;
                Input.SetServerCoordinates(Two / 10,Two % 10);
                // System.out.println("Passed "+s); // DEBUG

             }
             else if (extract_text(s).startsWith("/hit")){
               PlayingController2.HitAllowed = true;
               Platform.runLater( new Runnable() {
                @Override public void run(){
                IpController.playControl2.ServerNotif.setText("Your Turn");}
           } );
            //    PlayingController2.ServerNotif.setText("Your Turn");

             }else if (extract_text(s).startsWith("/SURR")){
                // Closes ServerGame Thread

                // HelloController.ipControl.ServerGame.stop();
                // System.out.printf("Running : %b \n", HelloController.ipControl.ClientGame.isInterrupted());
                // System.out.println("SURRENDER PASSED");
                IpController.playControl2.PreviousMessage += "\nYou Won!";
                IpController.playControl2.Chat.setText(IpController.playControl2.PreviousMessage);
                IpController.playControl2.Chat.setScrollTop(Double.MAX_VALUE);
                ServerThread.Stop=true;
             
                IpController.playControl2.changeScene( "YouWin.fxml");
           
 
              }else 
             if (extract_text(s).startsWith("/VERTICAL")){
                Board.IsVertical=true; 
 
             }
             else
             if (extract_text(s).startsWith("/HORIZONTAL")){
                Board.IsVertical=false;

             }
             {
             System.out.printf("[Server] [%s] : %s\n", extract_name(s),extract_text(s) ); // Feedback to Server System
             ToAll(s); // Sends the Message ( incase it doesnt have any commands ) to the connected clients
            //  System.out.println(s); // DEBUG
             }
     
         }   
//_________________________________________________________________________________________________________________________
        }catch(SocketException e){
            System.out.println("[ClientHandler]Server Stopped Working");
            try {
                IpController.playControl2.changeScene( "YouWin.fxml");
                ServerThread.Stop=true;
            } catch (Exception ee) {
               System.out.println("No Change Scene was possible ");
            }
            e.printStackTrace();

        }
        catch( IOException e){
            System.out.println("[ClientHandler] Error Running the ClientHandler Thread!");
            e.printStackTrace();
        }
        finally{
            writer.close();
            System.out.println("[ClientHandler]Closed ");
            try {
                reader.close();
            } catch (IOException e) {
                System.out.println("[ClientHandler] Error Closing Reader ");
                e.printStackTrace();
            }
        }
        
    }
    

    
    // a function to decode the coded message sent by clients
    // it extracts the Client's Name
    private String extract_name(String msg){
        String ClientName = "";
        int firstspace = msg.indexOf("#");
         if ( firstspace != -1){
            ClientName = msg.substring(0, firstspace + client.get_offset());
        }
        return ClientName;
    }

    // a function to decode the coded message sent by clients
    // it extracts the Client's Message
    private String extract_text(String msg){
        String Message= "";
        int firstspace = msg.indexOf("#");
        if ( firstspace != -1){
         Message = msg.substring(firstspace+client.get_offset()+1);
        }
        return Message;
    }


    private String extract_Oldname(String msg){
        String Message = extract_text(msg);
        int firstspace = msg.indexOf("$");
        if ( firstspace != -1){
            Message = msg.substring(firstspace+1);
           }
        return Message;
    }


    // The Function that sends the Text to all other Connected Users
    private void ToAll(String msg){

       for ( ClientHanlder aClient : Clients ){
         if (aClient== this) {} else {
         

         aClient.writer.println("["+ extract_name(msg)+"]: "+extract_text(msg));}
       }
         
    }
    
    private String get_Name(){
        return this.Naame;
    }

    private Boolean get_status(){
        return this.Connected;
    }

    private String Get_ALLnames(){
        String txt ="| ";
        for ( ClientHanlder aClient : Clients ){
            if (!(aClient.get_Name() == null) && aClient.get_status()){
            txt += aClient.get_Name() + " | ";}}
        return txt;
    }

    // A Function That is used by the Server to Send Custom Messages
    private void EToAll(String msg){
        for ( ClientHanlder aClient : Clients ){
            if (aClient== this) {} else {
            aClient.writer.println("[Server] "+ msg);}}
    }


}
