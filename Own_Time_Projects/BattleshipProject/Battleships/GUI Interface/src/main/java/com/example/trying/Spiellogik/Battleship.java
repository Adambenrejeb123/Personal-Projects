package com.example.trying.Spiellogik;

public class Battleship {
    private Display display;
    public Game game=new Game();
    // private Input input;


    public Battleship(){
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
