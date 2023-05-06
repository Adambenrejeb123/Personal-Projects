package com.example.trying;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class server {

 protected static  String IP = "localhost"; 
 protected static  int PORT = 1225;    
 private static final int THREAD_COUNT = 2; // the maximum number of  simultaneously Connected Clients

 private static ArrayList<ClientHanlder> Clients = new ArrayList<>();
 private static ExecutorService pool = Executors.newFixedThreadPool(THREAD_COUNT);
 
 public static void main(String[] aStrings){
    ServerSocket server;

    try{ 
      server = new ServerSocket(PORT);
     while ( true ){       // a Loop to keep listening for new connections 
       System.out.println("[Server] Waiting for Connections ..");
       Socket Client = server.accept(); // connects client
       System.out.println("[Server] Connection established");
       ClientHanlder ClientThread = new ClientHanlder(Client, Clients); // New Class( aka thread but not really)
       Clients.add(ClientThread); 
       pool.execute(ClientThread); // Officially starts the Client
      
     }
    
      
    }catch(IOException e) {
     System.out.print("[Server] Error at Server Side");
     e.printStackTrace();

    
   }
    
 }}
