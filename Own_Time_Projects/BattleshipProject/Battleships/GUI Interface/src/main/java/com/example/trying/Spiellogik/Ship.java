package com.example.trying.Spiellogik;

import java.util.List;

public class Ship {
    private List<Squere> NewShip;
    private ShipType typevonShip;

    public Ship(List<Squere> New , ShipType type){
        NewShip=New;
        this.typevonShip=type;
    }

    public List<Squere> GetNewShip(){
        return NewShip;
    }

    public ShipType GettypevonShip(){
        return typevonShip;
    }

    public void Addsquere(Squere square){
        this.NewShip.add(square);
    }

    public Boolean IsplacementOk(Ship ship1, List<Ship> ships,Board board){
        int Zahl=0;
        //Verify if the Ship is on the Board
        for (int i=0;i<ship1.GetNewShip().size();i++) {
            if (ship1.GetNewShip().get(i).Gety() > board.GetSizeY()-1 || ship1.GetNewShip().get(i).Getx()>board.GetSizeX()-1 ){
                Zahl++;
            }
            //verify if the ship is on other Ship
            for (int k=0;k< ships.size();k++){
                for (int z=0;z< ships.get(k).GetNewShip().size();z++){
                    if (ship1.GetNewShip().get(i).Gety()==ships.get(k).GetNewShip().get(z).Gety() &&
                            ship1.GetNewShip().get(i).Getx()==ships.get(k).GetNewShip().get(z).Getx()){
                        Zahl++;
                    }
                }
            }

        }
        if (Zahl==0){
            return true;
        }else {
            return false;
        }
    }

    @Override
    public String toString() {
        String returned = String.valueOf(this.typevonShip.Getlabel()); 
        return returned ;
    }


}
