package com.example.trying.Spiellogik;

public class MainThread implements Runnable {


    public static Battleship battleship;

    @Override
    public void run() {
        battleship=new Battleship();
        battleship.Anfangen();
    }

    
}
