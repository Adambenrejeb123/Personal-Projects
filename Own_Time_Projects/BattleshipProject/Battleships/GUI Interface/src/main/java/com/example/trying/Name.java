package com.example.trying;


// Essentially just a Glorified Incementing naming method , Lol
public class Name {

    protected static final String Name = "Client ";
    protected static int NameV = 0;
    static private String Own_Name;

    public Name(){
        Own_Name= Name + NameV;
        NameV ++ ;
        System.out.printf("[Server] [%s] Connected!\n", Own_Name); // Decalres the the client is Now properly connected
    }

    static public String Get_Name(){
        return Own_Name;
    }
}
