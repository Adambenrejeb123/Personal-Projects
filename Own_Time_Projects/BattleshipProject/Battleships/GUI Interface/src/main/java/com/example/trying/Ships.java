package com.example.trying;

import javafx.scene.Parent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
/*
* IMPORTANT!
* ALTHOUGH THESE ARE CALLED SHIPS, THEY ARE NOT THE SHIPS, THIS IS THE CLASS FOR THE CURSORS AND BECAUSE CHANING THE NAME
* WAS GOING TO MESS UP THE WHOLE CODE THE NAME WAS KEPT
*
* */

public class Ships extends Parent {
    private double x;
    private double y;
    private Circle c;
    // private double height;
    // private double width;
    private double radius;
    public int size = 0 ;
    public boolean vertical = false;
    private int health ;

    public Ships(int size,double x,double y ,double radius,Circle c,boolean vertical) {
        this.x = x;
        this.y = y;
        this.c = c;
        this.radius = radius;
        this.size = size;
        this.vertical = vertical;
        health= size;

    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public boolean isVertical() {
        return vertical;
    }

    public void setVertical(boolean vertical) {
        this.vertical = vertical;
    }
    public void hit(){
        health--;
    }
    public boolean isAlive(){
        return health > 0;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }



    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }
    public void setColor(Color color){
        c.setFill(color);
    }
    public void draw(){
        c.setRadius(radius);
        c.setTranslateX(x);
        c.setTranslateY(y);
    }
}
