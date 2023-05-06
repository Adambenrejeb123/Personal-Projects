package com.example.trying.Spiellogik;



public class ClientBattleship {
    private Display display;
    private ClientGame game=new ClientGame();
    // private Input input;


    public ClientBattleship(){
        display=new Display();
    }

    public void Anfangen(){
        // display.PrintMenu();
        MainMenu();
    }

    public void MainMenu(){
       
       
         
          
          display.PrintMessage("you choosed to play the Game");
          game.GameLogik();
          return;
           
            
        
    }

    public void ExitGame(){
        display.printExitMessage();
        System.exit(0);
    }
}

