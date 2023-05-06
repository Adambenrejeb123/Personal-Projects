package com.example.trying;

import java.io.IOException;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class ServerThread implements Runnable{
    protected static final String IP = "localhost"; 
    protected static int PORT = 1225;    
    public static final int THREAD_COUNT = 2; // the maximum number of  simultaneously Connected Clients
    public static boolean Stop = false;
    public  int Counter = 0;
   
    public static ArrayList<ClientHanlder> Clients = new ArrayList<>();
    public static ExecutorService pool = Executors.newFixedThreadPool(THREAD_COUNT);
    
    @Override
    public void run() {
        ServerSocket server;

    try{ 
      server = new ServerSocket(PORT);

    //  while ( !Stop ){       // a Loop to keep listening for new connections 
       while (  Counter == 0){
       System.out.println("[Server] Waiting for Connections ..");

       server.accept(); // waits for test Connection


       Socket Client = server.accept(); // connects client
       System.out.println("[Server] Connection established");
       ClientHanlder ClientThread = new ClientHanlder(Client, Clients); // New Class( aka thread but not really)
       Clients.add(ClientThread); 
       pool.execute(ClientThread); // Officially starts the Client
       Counter++;


       PlayingController2.Aasba=true;
       IpController.playControl2.PreviousMessage += "\n[Player Connected]\n[Game Started !]";
       IpController.playControl2.Chat.setText(IpController.playControl2.PreviousMessage);
       IpController.playControl2.Chat.setScrollTop(Double.MAX_VALUE);

       }
       while ( !Stop ){ try {
        Thread.sleep(20);
      } catch (InterruptedException e) {
        e.printStackTrace();
      } 
     }
    
     System.out.println("[Server] Closed");
     server.close();
    }
    catch ( BindException e ){
      System.out.println("[Server] Port Already On Use!");
    }catch(IOException e) {
     System.out.println("[Server] Error at Server Side");
     e.printStackTrace();
        
    }
    
    
}}
