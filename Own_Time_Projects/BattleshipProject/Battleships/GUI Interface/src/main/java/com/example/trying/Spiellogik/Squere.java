package com.example.trying.Spiellogik;

public class Squere {
    private int x;
    private  int y ;
    private SquareStatur squarestat;


    public Squere(int x , int y , SquareStatur squarestat){
        this.x=x;
        this.y=y;
        this.squarestat= squarestat;
    }

    public void setSquarestat(SquareStatur statur){
        this.squarestat=statur;
    }

    public int Getx(){
        return this.x;
    }

    public int Gety(){
        return this.y;
    }
    public SquareStatur Getsquarestat(){
        return this.squarestat;
    }

    public char GetChar(){
        char carac=' ';
        switch (this.squarestat){
            case HIT :
                carac='H';
                break;
            case EMPTY:
                carac='E';
                break;
            case SHIP:
                carac='S';
                break;
            case OCEAN:
                carac='O';
                break;
            case MISSED:
                carac='M';
                break;
            default:
                break;    
        }
        return carac;
    }
}
