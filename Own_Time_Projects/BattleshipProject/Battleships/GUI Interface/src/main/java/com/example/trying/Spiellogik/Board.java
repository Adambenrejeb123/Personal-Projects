package com.example.trying.Spiellogik;


public class Board {
    private int SizeX;
    private int SizeY;


    public static boolean IsVertical = false ; 
    public static boolean legit = false;
    Squere board[][];

    public Board(int x,int y){
        this.SizeX=x;
        this.SizeY=y;
        fillBoard(x,y);
    }

    public int GetSizeX(){
        return SizeX;
    }

    public int GetSizeY(){
        return SizeY;
    }

    public Squere GetSquere(int x,int y ){
        return board[x][y];
    }

    public Squere[][] getBoard() {
        return board;
    }
     // fill the Board with Ocean

    public Squere[][] fillBoard(int x , int y){
        board=new Squere[SizeX][SizeY];
        for (int row=0;row<x;row++){
            for (int col=0;col<y;col++){
                board[row][col]=new Squere(row,col,SquareStatur.OCEAN);
            }
        }
        return board;
    }

    //put the Ship on the Board.
    public void BuiltShip(Squere square, Ship ship){
        if ( IsVertical ){BuiltShipVertical(square, ship);}
        else { BuiltShipHorizontal(square, ship);}
        return;

    }




    public void BuiltShipHorizontal(Squere square, Ship ship){
        switch (ship.GettypevonShip().Getlabel()){
            case  1:
                square.setSquarestat(SquareStatur.SHIP);
                ship.Addsquere(square);
                break;
            case 2:
                //lehni thabt belkchi tatla3 declari fil x
                square.setSquarestat(SquareStatur.SHIP);
                ship.Addsquere(square);
                int x =square.Getx();
                int y = square.Gety();
                ship.Addsquere(new Squere(x,y+1,SquareStatur.SHIP));
                break;
            case  3:
                square.setSquarestat(SquareStatur.SHIP);
                ship.Addsquere(square);
                x =square.Getx();
                y = square.Gety();
                ship.Addsquere(new Squere(x,y+1,SquareStatur.SHIP));
                ship.Addsquere(new Squere(x,y+2,SquareStatur.SHIP));
                break;
            case 4:
                square.setSquarestat(SquareStatur.SHIP);
                ship.Addsquere(square);
                x =square.Getx();
                y = square.Gety();
                ship.Addsquere(new Squere(x,y+1,SquareStatur.SHIP));
                ship.Addsquere(new Squere(x,y+2,SquareStatur.SHIP));
                ship.Addsquere(new Squere(x,y+3,SquareStatur.SHIP));
                break;
            case 5: 
                square.setSquarestat(SquareStatur.SHIP);
                ship.Addsquere(square);
                x =square.Getx();
                y = square.Gety();
                ship.Addsquere(new Squere(x,y+1,SquareStatur.SHIP));
                ship.Addsquere(new Squere(x,y+2,SquareStatur.SHIP));
                ship.Addsquere(new Squere(x,y+3,SquareStatur.SHIP));
                ship.Addsquere(new Squere(x,y+4,SquareStatur.SHIP));
                break;   
            default:
                break;
        }


    }
    public void BuiltShipVertical(Squere square, Ship ship){
        switch (ship.GettypevonShip().Getlabel()){
            case  1:
                square.setSquarestat(SquareStatur.SHIP);
                ship.Addsquere(square);
                break;
            case 2:
                //lehni thabt belkchi tatla3 declari fil x
                square.setSquarestat(SquareStatur.SHIP);
                ship.Addsquere(square);
                int x =square.Getx();
                int y = square.Gety();
                ship.Addsquere(new Squere(x+1,y,SquareStatur.SHIP));
                break;
            case  3:
                square.setSquarestat(SquareStatur.SHIP);
                ship.Addsquere(square);
                x =square.Getx();
                y = square.Gety();
                ship.Addsquere(new Squere(x+1,y,SquareStatur.SHIP));
                ship.Addsquere(new Squere(x+2,y,SquareStatur.SHIP));
                break;
            case 4:
                square.setSquarestat(SquareStatur.SHIP);
                ship.Addsquere(square);
                x =square.Getx();
                y = square.Gety();
                ship.Addsquere(new Squere(x+1,y,SquareStatur.SHIP));
                ship.Addsquere(new Squere(x+2,y,SquareStatur.SHIP));
                ship.Addsquere(new Squere(x+3,y,SquareStatur.SHIP));
                break;
            case 5: 
                square.setSquarestat(SquareStatur.SHIP);
                ship.Addsquere(square);
                x =square.Getx();
                y = square.Gety();
                ship.Addsquere(new Squere(x+1,y,SquareStatur.SHIP));
                ship.Addsquere(new Squere(x+2,y,SquareStatur.SHIP));
                ship.Addsquere(new Squere(x+3,y,SquareStatur.SHIP));
                ship.Addsquere(new Squere(x+4,y,SquareStatur.SHIP));
                break;   
            default:
                break;
        }


    }


}
