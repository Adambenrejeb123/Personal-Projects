package com.example.trying.Spiellogik;



public enum ShipType {
    MOUDAMIR(5),
    EMLEK(4),
    KANAS(3),
    EDIA(2),
    KABIRA(1),
    GUEZ(7);

    private final Integer label;

    ShipType(int l){
        this.label=l;
    }

    public int Getlabel(){
        return label;
    }
}
