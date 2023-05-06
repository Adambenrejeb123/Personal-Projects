package com.example.trying;

public class Decoding {
    
    static public String extract_name(String msg){
        String ClientName = "";
        int firstspace = msg.indexOf("#");
         if ( firstspace != -1){
            ClientName = msg.substring(0, firstspace + client.get_offset());
        }
        return ClientName;
    }

    // a function to decode the coded message sent by clients
    // it extracts the Client's Message
    static public String extract_text(String msg){
        String Message= "";
        int firstspace = msg.indexOf("#");
        if ( firstspace != -1){
         Message = msg.substring(firstspace+client.get_offset()+1);
        }
        return Message;
    }


    static public String extract_Oldname(String msg){
        String Message = extract_text(msg);
        int firstspace = msg.indexOf("$");
        if ( firstspace != -1){
            Message = msg.substring(firstspace+1);
           }
        return Message;
    }

    
}
