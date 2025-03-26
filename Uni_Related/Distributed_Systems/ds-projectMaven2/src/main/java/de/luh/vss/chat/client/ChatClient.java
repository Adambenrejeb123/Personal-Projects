package de.luh.vss.chat.client;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

import de.luh.vss.chat.common.Message;
import de.luh.vss.chat.common.MessageType;
import de.luh.vss.chat.common.User;
import de.luh.vss.chat.frontend.ChatWindow;

public class ChatClient {
    private ChatWindow window;
    private final int USER_ID = 5188;

    public static void main(String... args) throws ReflectiveOperationException, InterruptedException {
        try {
            new ChatClient().start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void readMessage(DataInputStream in) throws IOException {
        int type = in.readInt();
        MessageType typeMessage = MessageType.fromInt(type);
        if(typeMessage == MessageType.CHAT_MESSAGE){
            Message.ChatMessage response = new Message.ChatMessage(in);
            System.out.println("Server responded: " + response.getMessage());
        }
    }
    public void start() throws IOException, ReflectiveOperationException, InterruptedException {
        System.out.println("Starting Chat Client...");

        window = new ChatWindow();
        window.setOnSendMessageListener(text -> {
            handleUserInputFromGUI(text);
        });
        // Step 1: Establish TCP connection to the server
        Socket socket = new Socket("localhost", 5553); // Server's IP and TCP port
        if (socket.isConnected()) {
            System.out.println("Socket connected to server successfully.");
        }

        // Step 2: Register User
        User.UserId id = new User.UserId(USER_ID); // User Id here can be changed here dynamically if the prototype gets actually deployed
        InetAddress localAddress = InetAddress.getByName("localhost"); // Your machine's IP or whereever the server lives-
        System.out.println("Local address: " + localAddress.toString());

        //Communication streams TCP to communicate with the server itself on port 5553
        DataInputStream in = new DataInputStream(socket.getInputStream());
        DataOutputStream out = new DataOutputStream(socket.getOutputStream());

        Message.RegisterRequest request = new Message.RegisterRequest(id,localAddress,5553);
        request.toStream(out);
        out.flush();

        //wait for confirmation
        socket.setSoTimeout(5000);

        while(true){
            readMessage(in);
            break;
        }

        while(true){
            readMessage(in);
            break;
        }
        DatagramSocket UDPsocket = new DatagramSocket(USER_ID);
        new Thread(() -> {
            try {
                while(true){
                    byte[] buf = new byte[1024];
                    DatagramPacket packet = new DatagramPacket(buf, buf.length);
                    UDPsocket.receive(packet);
                    Message incomingMsg = handleIncomingDatagram(packet);

                    // If it’s ChatMessage, display in the GUI bubble
                    if(incomingMsg instanceof Message.ChatMessage) {
                        Message.ChatMessage chatMsg = (Message.ChatMessage) incomingMsg;
                        // Gray bubble = fromUser = false
                        window.addMessageBubble(chatMsg.getMessage(), false);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, "UDP-Listener").start();

        while(true){
            System.out.println("You are currently speaking to User 9999, What would you like to say?");
            System.out.println("if you want to get the logs type history");
            Scanner scanner = new Scanner(System.in);
            String input = scanner.nextLine();
            if(input.toLowerCase().equals("history")){
                //Query Logic when requesting getting the logs.
                System.out.println("Please choose a time period");
                System.out.println("today");
                System.out.println("yesterday");
                System.out.println("Last week");
                while(true) {
                    Scanner scanner2 = new Scanner(System.in);
                    String input2 = scanner2.nextLine();
                    //Validity Check here.
                    if (!input2.toLowerCase().equals("yesterday") && !input2.toLowerCase().equals("today") && !input2.toLowerCase().equals("last week")) {
                        System.out.println("You said " + input2);
                        System.out.println("Please input a correct time period");
                    }
                    else{
                        Message.ChatMessage logRequest = new Message.ChatMessage(id,input2);
                        logRequest.toStream(out);
                        out.flush();
                        readMessage(in);
                        break;
                    }
                }
            }

            //Escape and exit logic
            //keyword is detected by Server which removes the client for connectedUsers and issues a confirmation
            //We then exit the loop directly to the response and closure of the socket.
            else if(input.equals("exit"))
            {
                Message.ChatMessage exitRequest = new Message.ChatMessage(id,input);
                exitRequest.toStream(out);
                out.flush();
                readMessage(in);
                break;

            }
            else {

                /*Each Message gets the current timestamp appended to it, this initially was to ensure that the data stored
                 * was able to be filtered by timestamps, this was later made obsolete by the fact that Firestore has a column
                 * for this, but having the timestamp here regardless means that it is enough to query the text to get it.
                 */
                sendChatMessageUDP(new Message.ChatMessage(id,input + "|" + getCurrentTimestamp()),localAddress,7007);
                byte[] buf = new byte[1024];
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                UDPsocket.receive(packet);
                handleIncomingDatagram(packet);
            }

        }








        // Step 5: Close resources
        socket.close();
    }


    private void handleUserInputFromGUI(String input) {
        try {
            window.addMessageBubble(input, true);
            InetAddress localAddress = InetAddress.getByName("localhost");
            Message.ChatMessage chatMsg = new Message.ChatMessage(
                    new User.UserId(USER_ID),
                    input + "|" + getCurrentTimestamp()
            );
            sendChatMessageUDP(chatMsg, localAddress, 7007);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //Client side helper functions

    /*
        Sending Chat messages through udp sockets.
        Removes redundancy, pretty cool if you ask me

     */
    private void sendChatMessageUDP(Message.ChatMessage chatMsg, InetAddress serverAddress, int serverUdpPort)
            throws IOException {

        // 1) Serialize the ChatMessage using your existing toStream(...) method
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        DataOutputStream dataOut = new DataOutputStream(byteOut);
        chatMsg.toStream(dataOut);
        dataOut.flush();

        byte[] payload = byteOut.toByteArray();

        // 2) Create a DatagramPacket with the serialized message
        DatagramPacket packet = new DatagramPacket(payload, payload.length, serverAddress, serverUdpPort);

        // 3) Send it
        try (DatagramSocket socket = new DatagramSocket()) {
            socket.send(packet);
            System.out.println("[Client] Sent UDP ChatMessage: " + chatMsg.getMessage());
        }
    }


    /*
    * Get the current timestamp, goal mentioned above, ISO_INSTANT for consistency with firestore api
    *
    *
    * */
    public static String getCurrentTimestamp() {
        // Use ISO_INSTANT to match Firestore format
        DateTimeFormatter formatter = DateTimeFormatter.ISO_INSTANT;
        String firestoreTimestamp = formatter.format(Instant.now());
        return firestoreTimestamp;
    }
    /*
    * Receiving messages from UDP Datagram
    * Eliminates redundant code
    * Pretty cool too
    *
    * */


    public static Message handleIncomingDatagram(DatagramPacket packet) throws IOException, ReflectiveOperationException {
        ByteArrayInputStream byteIn =
                new ByteArrayInputStream(packet.getData(), 0, packet.getLength());
        DataInputStream dataIn = new DataInputStream(byteIn);
        Message incomingMsg = Message.parse(dataIn);
        System.out.println("[Server] Deserialized incoming UDP message: " + incomingMsg);
        return incomingMsg;
    }

}
