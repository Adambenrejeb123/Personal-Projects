package com.example.trying.Spiellogik;
import com.example.trying.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Input {
    private Scanner scanner=new Scanner(System.in);
    private List<Board> boards=new ArrayList<>();
    public static Integer ShipNum1 = 1;
    public static boolean Server_wait = true;
    public static Integer ServerRow = null ;
    public static Integer ServerCol = null ;
    public static boolean Self_wait = true;
    public static Integer SelfRow = null ;
    public static Integer SelfCol = null ;

    int choice;
    List<Integer> CoordinatesAndShiptype =new ArrayList<>();

    public List<Board> getBoards(){
        GenerateBoard();
        return boards;
    }

    public void GenerateBoard(){
       
        Board board1=new Board(10,10);
        Board board2=new Board(10,10);
        boards.add(board1);
        boards.add(board2);
    }

    public Input(){

    }

    public int GetIntMenuOption(){
        choice=scanner.nextInt();
        scanner.nextLine();
        return choice;
    }

    private List<Integer> AskCoordForShipAndTyp(){           /// implementation de serveur
        this.CoordinatesAndShiptype=new ArrayList<>();
        while ( Self_wait ){
          try {
            Thread.sleep(20);
        } catch (InterruptedException e) {
            System.out.println("Code 19");
            e.printStackTrace();
        }
        }
        Self_wait = true;

        //  Dass Input muss was anders Sein 
        if ( (SelfRow != null )&& (SelfCol != null)){
        System.out.printf("Passed %d as Row and %d as Col\n", SelfRow, SelfCol); // Debug
        CoordinatesAndShiptype.add(SelfRow);
        CoordinatesAndShiptype.add(SelfCol);
        CoordinatesAndShiptype.add(ShipNum1);
        ShipNum1++;
        }
         /// Max 5 wergen die Loop // Muss nach 1 wieder gestzt werden wenn enemy nutzt es

        String Message = String.valueOf(SelfRow)+ String.valueOf(SelfCol);


        
        // Sends coordinates to Client
        for ( ClientHanlder aClient : ServerThread.Clients ){
            aClient.writer.println("/spl "+Message);
            aClient.writer.flush();
        }

        SelfCol = null ;
        SelfRow = null ;
        return CoordinatesAndShiptype;
    }
    private List<Integer> ServerAskCoordForShipAndTyp(){           /// Server Version with Read  // Needs coding
        this.CoordinatesAndShiptype=new ArrayList<>();
        // MAN BRAUCHT 3 LIST von 2 ints von server
        // Erstmal warten bis Server etwas geshickt hat 
        while ( Server_wait ){
           try {
            Thread.sleep(20);
        } catch (InterruptedException e) {
            System.out.println("Fehler Code 11");
            e.printStackTrace();
        } // waits till Server wait is false ( Set by Server )
        }

        Server_wait = true;
        if ( (ServerRow != null )&& (ServerCol != null)){
        //  System.out.printf("Passed %d as Row and %d as Col\n", ServerRow, ServerCol); debug
         CoordinatesAndShiptype.add(ServerRow);
         CoordinatesAndShiptype.add(ServerCol);
         CoordinatesAndShiptype.add(ShipNum1);
        } else { 
            System.out.println("Code Fehler 12");
        }
        ServerRow = null;
        ServerCol = null;       //reset
        ShipNum1++; /// Max 5 wergen die Loop // Muss nach 1 wieder gestzt werden wenn enemy nutzt es
        return CoordinatesAndShiptype;
    }
    
    public Ship ServercreateShip(int player){
        int GamePlayer= player+1;
        Squere shipPart;
        Ship ship;
        System.out.println("Player"+GamePlayer+" place ship");
        CoordinatesAndShiptype=ServerAskCoordForShipAndTyp();
        int row=CoordinatesAndShiptype.get(0);
        int col=CoordinatesAndShiptype.get(1);
        
        int shiptype=CoordinatesAndShiptype.get(2);
        shipPart=new Squere(row,col,SquareStatur.SHIP);
        ship=new Ship(new ArrayList<>(),ShipType.values()[shiptype-1]);
        // System.out.printf("\n Ship = %d\n ", (ShipType.values()[shiptype-1]).Getlabel()); debug
        boards.get(player).BuiltShip(shipPart,ship);
        // System.out.println(ship.GetNewShip());
        return ship;

    }

    
     //fazet il plus 1 chnestamlha fil Game
    public Ship createShip(int player){
        int GamePlayer= player+1;
        Squere shipPart;
        Ship ship;
        System.out.println("Player"+GamePlayer+" place ship");
        CoordinatesAndShiptype=AskCoordForShipAndTyp();
        int row=CoordinatesAndShiptype.get(0);
        int col=CoordinatesAndShiptype.get(1);
        
        int shiptype=CoordinatesAndShiptype.get(2);
        shipPart=new Squere(row,col,SquareStatur.SHIP);
        ship=new Ship(new ArrayList<>(),ShipType.values()[shiptype-1]);
        
        // System.out.printf("\n Ship = %d\n ", (ShipType.values()[shiptype-1]).Getlabel()); DEBUG
        boards.get(player).BuiltShip(shipPart,ship);
        // System.out.println(ship.GetNewShip());
        return ship;

    }

    public int[] shoot (int player){
        int GamePlayer=player+1;
        System.out.println("Player"+ GamePlayer+"shoot");
        int[] SelfReturn = {0,0};

        while ( Self_wait ){
            try {
              Thread.sleep(20);
          } catch (InterruptedException e) {
              System.out.println("Code 19");
              e.printStackTrace();
          }
          }
        Self_wait = true;

        if ( (SelfRow != null )&& (SelfCol != null)){
            System.out.printf("Passed %d as Row and %d as Col\n", SelfRow, SelfCol);
            SelfReturn[0] = SelfRow;
            SelfReturn[1]=  SelfCol;
        } else {
            System.out.println("Code Fehler 73");
        }
        
        String Message = String.valueOf(SelfRow)+ String.valueOf(SelfCol);

        
        // Sends coordinates to Client
        for ( ClientHanlder aClient : ServerThread.Clients ){
            aClient.writer.println("/spl "+Message);
            aClient.writer.flush();
            aClient.writer.println("/hit");
            aClient.writer.flush();
            // PlayingController2.ServerNotif.setText("Opponent's Turn");

            // also allows player to hit 
        }


        SelfCol = null;
        SelfRow = null;
        return SelfReturn;
    }

    public int[] Servershoot (int player){
        int GamePlayer=player+1;
        int[] ServerReturn = {0,0};
        System.out.println("Player2"+ GamePlayer+"shoot");
        while ( Server_wait ){
            try {
             Thread.sleep(20);
         } catch (InterruptedException e) {
             System.out.println("Fehler Code 11");
             e.printStackTrace();
         } // waits till Server wait is false ( Set by Server )
         }
         Server_wait = true;
         if ( (ServerRow != null )&& (ServerCol != null)){
            System.out.printf("Passed %d as Row and %d as Col\n", ServerRow, ServerCol); 
         ServerReturn[0] = ServerRow;
         ServerReturn[1]=  ServerCol;
         } else {
            System.out.println("Code Fehler 13");
         }
         ServerRow = null;
         ServerCol = null; 
        return ServerReturn;
    }


 

       
    

   static public void setServerOwnCoordinates ( Integer Row , Integer Col){
    SelfCol = Col;
    SelfRow = Row;
    Self_wait = false;

   }

   static public void SetServerCoordinates(Integer Row , Integer Col){
     ServerRow = Row;
     ServerCol = Col;
     Server_wait = false ;
   }
}
