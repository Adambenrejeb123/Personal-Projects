package com.example.trying.Spiellogik;

public class MainThreadClient implements Runnable {

    @Override
    public void run() {
        ClientBattleship battleship=new ClientBattleship();
        battleship.Anfangen();
    }
    
}
