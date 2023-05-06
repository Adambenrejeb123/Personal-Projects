package com.example.trying.Spiellogik;


import java.util.ArrayList;
import java.util.List;

import com.example.trying.Client_Thread;
import com.example.trying.IpController;
import com.example.trying.PlayingController;

import javafx.application.Platform;
import javafx.scene.paint.Color;



public class ClientGame {
    private List<Ship> shipPlayer1=new ArrayList<>();
     private List<Ship> shipPlayer2=new ArrayList<>();
     public  boolean GameOn;
     List<Board> boards;
     public static int shipCounter = 0;
     

     public void GameLogik(){
          ClientInput board1=new ClientInput();
          boards=board1.getBoards();
          Board boardPlayer1 =boards.get(0);
          Board boardPlayer2=boards.get(1);
          //lehni chtestamlha fazet il plus 1
          // il thnin hia adad il Ship eli chenhothom
          
          
          for (int i=0;i<5;i++){                // Wait for Opponent to fill out site and then play
               Ship one =board1.ClientcreateShip(1);
               while (one.IsplacementOk(one,shipPlayer2,boardPlayer2)==false){
                    ClientInput.ShipNum1--;
                    one=board1.ClientcreateShip(0);
               }
               shipPlayer2.add(one);
          }
          // PlayingController.ClientNotif.setText("Your Turn");
          Platform.runLater( new Runnable() {
               @Override public void run(){
               IpController.playControl.ClientNotif.setText("Your Turn");}
          } );
          PlayingController.Aasba = true;
          Board.IsVertical=false  ;
          IpController.playControl.Horizontal.setDisable(false);
          ClientInput.ShipNum1=1; // Temporare ! 
          for (int i=0;i<5;i++){
            Ship one =board1.createShip(0);
            while (one.IsplacementOk(one,shipPlayer1,boardPlayer1)==false){
              
                 IpController.playControl.PreviousMessage += "\nInvalid Placement";
                 IpController.playControl.Chat.setText(IpController.playControl.PreviousMessage);
                 IpController.playControl.Chat.setScrollTop(Double.MAX_VALUE);
                 IpController.playControl.shipCounter--;
                 IpController.playControl.biggness++;
                 ClientInput.ShipNum1--;
                 one=board1.createShip(0);




            }

            for (int j=0;j<one.GetNewShip().size();j++) {
               System.out.printf("Size at %d ,index : %d\n",one.GetNewShip().size(), j);
               IpController.playControl.grid[one.GetNewShip().get(j).Gety()][one.GetNewShip().get(j).Getx()].setFill(Color.GREEN);
          }
          
            shipPlayer1.add(one);
            shipCounter++;
            //Removing images
            if(shipCounter == 1){
                IpController.playControl.ship1.setVisible(false);
            }
            if(shipCounter == 2){
                IpController.playControl.ship2.setVisible(false);
            }
            if(shipCounter == 3){
                IpController.playControl.ship3.setVisible(false);
            }
            if(shipCounter == 4){
                IpController.playControl.ship4.setVisible(false);
            }
            if(shipCounter == 5){
                IpController.playControl.ship5.setVisible(false);
            }
       }
          Player player1=new Player(shipPlayer1,boardPlayer2);
          Player1 player2=new Player1(shipPlayer2,boardPlayer1);
          GameOn=true; //// Win condition SERVER
          Display display=new Display();
          System.out.println("-----Player1 Board -------");
          display.PrintBoard(boardPlayer1);
          System.out.println("--------------------------");
          System.out.println("-----Player2 Board -------");
          display.PrintBoard(boardPlayer2);
          int NumberofshipsPlayer1=player1.NumberOfSquareofShips(shipPlayer1);
          int NumberofshipsPlayer2=player2.NumberOfSquareofShips(shipPlayer2);
          // PlayingController.ClientNotif.setText("Opponent's Turn");
          Platform.runLater( new Runnable() {
               @Override public void run(){
               IpController.playControl.ClientNotif.setText("Opponent's Turn");}
          } );

          IpController.playControl.Horizontal.setDisable(true);
          while (GameOn){
               int[] ShootCoordination;
               
               ShootCoordination=board1.Clientshoot(1);
               
               if (player1.Shot(ShootCoordination[0],ShootCoordination[1])){
                    display.PrintBoard(player1.Getboard());
                    NumberofshipsPlayer1--;
               }else {
                    display.PrintBoard(player1.Getboard());
               }
               if (NumberofshipsPlayer1==0){
                    display.PrintBoard(player1.Getboard());
                    System.out.println("ANWSER AB");
                    IpController.playControl.PreviousMessage += "\nYou Lost! ( Please leave xD ) ";
                 IpController.playControl.Chat.setText(IpController.playControl.PreviousMessage);
                 IpController.playControl.Chat.setScrollTop(Double.MAX_VALUE);
                 IpController.playControl.changeScene( "Lost.fxml");
                 Client_Thread.Stop=true;
                    break;
               }
               
               ShootCoordination=board1.shoot(0);
               Platform.runLater( new Runnable() {
                    @Override public void run(){
                    IpController.playControl.ClientNotif.setText("Opponent's Turn");}
               } );
               if (player2.Shot(ShootCoordination[0],ShootCoordination[1])){
                    display.PrintBoard(player2.Getboard());
                    NumberofshipsPlayer2--;
               }else {
                    display.PrintBoard(player2.Getboard());
               }
               if (NumberofshipsPlayer2==0){
                    display.PrintBoard(player2.Getboard());
                    System.out.println("ANWSER CD");
                    IpController.playControl.PreviousMessage += "\nYou Won! ( Please leave xD ) ";
                 IpController.playControl.Chat.setText(IpController.playControl.PreviousMessage);
                 IpController.playControl.Chat.setScrollTop(Double.MAX_VALUE);
                 IpController.playControl.changeScene( "YouWin.fxml");
                    Client_Thread.Stop=true;

                    break;
               }


          }

          //// winning
          System.out.println("TEST TEST TEST");
          Board.legit = true;
     }
}
