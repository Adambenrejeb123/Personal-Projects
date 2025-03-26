package de.luh.vss.chat.server;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import de.luh.vss.chat.common.Message;
import de.luh.vss.chat.common.MessageType;
import de.luh.vss.chat.common.User;


import java.io.*;
import java.net.*;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.SQLOutput;
import java.time.Duration;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;


import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

public class Server {
    private static final String API = System.getenv("DEEPSEEK-ENV");
    private static final String API_KEY = "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";
    private static final String BASE_URL = "https://openrouter.ai/api/v1";
    private static int GHOST_UDP_PORT = 7007;
    private static final User.UserId GHOST_USER_ID = new User.UserId(9999);

    //The Mapping Global variable is used access the stored current section connected users
    //It stores the users based on their Id and session so that they can be accessed later if we need them
    //this facilitates scaling later, as new features like global messages for example can be added rather easily.
    public static final Map<User.UserId, ClientHandler> connectedUsers = new ConcurrentHashMap<>();
    public static void main(String[] args) throws IOException {
        /*
        * Here we call the firestore api, which is a cloud hosting storage service given by Google.
        * As you may have already noticed , the DS concept that we are trying to use/apply here, is Data logging and storage on the cloud
        * The same DS concept can be used for error logging, Back ups and much more.
        * Here we will be applying it in conversation logging, as in the conversations between users will be stored in the cloud rather
        * than on the server or on the client's machine, saving space and money.
        *
        *
        * Explanation of the collection/Database structure in question:
        * ->The collection is called Chatlogs and is composed of the following:
        *   ->An Array called Participants: Has the participating users via IDs.
        *   ->An issue Date to determine when the conversation started.
        *   ->A subcollection called Messages:
        *       ->Start Date.
        *       ->Text.
        *       ->Sender.
        *
        *
        * */
        GoogleCredentials credentials = GoogleCredentials.getApplicationDefault();
        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(credentials)
                .setProjectId("verteilte-systeme-447819")
                .build();
        FirebaseApp.initializeApp(options);

        Firestore db = FirestoreClient.getFirestore();
        startServer(db);
    }

    //Initializing the server. Announcing Date , Port and other hints along the way
    //Nothing out of the ordinary just normal server side logging.
    //We associate the serverID as the port its used in.
    public static void startServer(Firestore db) throws IOException {
        ServerSocket tcpSocket = new ServerSocket(5553);

        while (true) {
            System.out.println("Good day, today is the " + getTodayDate());
            System.out.println("Listening for connection on port: " + 5553);
            Socket clientSocket = tcpSocket.accept();
            System.out.println("Server Client Connection Acquired. Opening a thread");
            //Start a thread for the user that just connected, passing in the db so we can manipulate it.

            new Thread(() -> {
                try {
                    //The code here is a little messy, but to adhere to Java's weird OOP rules , I have to create an instance of the server
                    //We pass in our db
                    Server server = new Server();
                    startGhostUserListener(db);
                    fetchDocumentById(db);
                    server.handleClient(clientSocket,db);
                } catch (IOException | InterruptedException | ReflectiveOperationException e) {
                    throw new RuntimeException(e);
                } catch (ExecutionException e) {
                    throw new RuntimeException(e);
                }
            }).start();
        }
    }

    /*
    * The Ghost user is a demo user, pretty much the same as the users that were introduced to us in the Assignment
    * The user 9999 is meant to simulate another user to interact with, his goal is to respond with a random response
    * everytime he receives a packet from one of the clients , after responding he Logs the conversation in Our cloud database
    * again this is one of the uses of our cloud database service that is extremely useful and even essential in DS.
    *
    *   USER ID: 9999
    *   Listens on port: 7007
    *
    * */

    private static void startGhostUserListener(Firestore db) {
        new Thread(() -> {
            try {
                //Start listening for UDP requests on port 7007
                DatagramSocket ghostSocket = new DatagramSocket(GHOST_UDP_PORT);
                System.out.println("GhostUser listening on UDP port " + GHOST_UDP_PORT);
                byte[] buffer = new byte[1024];
                while (true) {
                    byte[] buf = new byte[1024];
                    DatagramPacket packet = new DatagramPacket(buf, buf.length);
                    ghostSocket.receive(packet); // block until a packet arrives


                    try {
                        //Read message
                        Message incomingMsg = handleIncomingDatagram(packet);

                        //If it is part of the protocol AND is a ChatMessage, read.

                        if (incomingMsg instanceof Message.ChatMessage) {
                            //Turn message into ChatMessage variable for further inspection and get extract the metadata
                            //we need for storage and response through String manipulation.


                            Message.ChatMessage chatMsg = (Message.ChatMessage) incomingMsg;
                            System.out.println("[Server] ChatMessage text: " + chatMsg.toString());
                            String userIDString = chatMsg.getRecipient().toString().split("id=")[1].split("]")[0];
                            System.out.println("[Server] From user: " + userIDString);
                            //Get the conversation IF it exists, otherwise create a new one and start logging there. conversation
                            //existence depends on both the date AND the participant IDs.
                            String conversationID = createOrDetectConversation(db,Integer.valueOf(userIDString),9999);
                            //Log the user message in the conversation
                            addMessageToConversation(db,conversationID,userIDString, chatMsg.getMessage());

                            var body2 = String.format("""
                                {
                                    "model": "deepseek/deepseek-chat",
                                    "messages": [
                                        {
                                            "role": "user",
                                            "content": "You have just received the following message, answer it as if you were chatting with the person: do now acknowledge anything just write your reply and nothing else : %s"
                                        }
                                    ]
                                }
                                """, chatMsg.getMessage());

                            var request2 = HttpRequest.newBuilder()
                                    .uri(URI.create(BASE_URL + "/chat/completions"))
                                    .header("Content-Type","application/json")
                                    .header("Authorization","Bearer " + API)
                                    .timeout(Duration.ofSeconds(30))
                                    .POST(HttpRequest.BodyPublishers.ofString(body2))
                                    .build();

                            var client2 = HttpClient.newHttpClient();
                            String responseString2 ="";

                            try {
                                var response2 = client2.send(request2, HttpResponse.BodyHandlers.ofString());
                                var responseBody2 = response2.body();

                                if(!responseBody2.isBlank()){
                                    responseString2 = getMessagefromModel(responseBody2);

                                }


                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                            System.out.println("Response was: " + responseString2);
                            Server server = new Server();
                            Message.ChatMessage ghostresponse = new Message.ChatMessage(GHOST_USER_ID,responseString2);
                            server.sendChatMessageUDP(ghostresponse,InetAddress.getByName("localhost"),Integer.valueOf(userIDString));
                            //Finally, log the ghost user's message in the same conversation
                            addMessageToConversation(db,conversationID,"9999", ghostresponse.getMessage());

                        } else {
                            System.out.println("[Server] Received non-chat message or unrecognized type.");
                        }

                    } catch (ReflectiveOperationException e) {
                        System.out.println("Error parsing UDP message: " + e.getMessage());
                    }
                }


            } catch (IOException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }, "GhostUser-UDP-Listener").start();
    }

    public static String getMessagefromModel(String responseBody) throws JsonProcessingException {
        if (!responseBody.isBlank()) {
            ObjectMapper mapper = new ObjectMapper();
            // Parse the response into a JsonNode tree
            JsonNode root = mapper.readTree(responseBody);

            // Traverse the tree: get the "choices" array, then the first element, then "message", then "content"
            JsonNode choices = root.path("choices");
            if (choices.isArray() && choices.size() > 0) {
                JsonNode firstChoice = choices.get(0);
                JsonNode messageNode = firstChoice.path("message");
                String messageContent = messageNode.path("content").asText();

                // Print or use the extracted content
                return messageContent;
            } else {
               return "No choices found in the response.";
            }
        }
        return "An error has occured, empty body received or wrong format";
    }


    //Deserialize and parse the UDP packet.
    public static Message handleIncomingDatagram(DatagramPacket packet) throws IOException, ReflectiveOperationException {
        ByteArrayInputStream byteIn =
                new ByteArrayInputStream(packet.getData(), 0, packet.getLength());
        DataInputStream dataIn = new DataInputStream(byteIn);
        Message incomingMsg = Message.parse(dataIn);
        System.out.println("[Server] Deserialized incoming UDP message: " + incomingMsg);
        return incomingMsg;
    }


    /*Client handling logic.
        SERVER ID: 5553

        The server Creates an instance for every client that works the same way, everytime a user connects a registration
        is required, when a registration happens the server broadcasts it to every user.
        In the prototype, the functionality of the Server is to fetch the logs from the Cloud storage
        and then look for the specific data that correlates to the Query sent by the user.

     */
    public void handleClient(Socket clientSocket,Firestore db)
            throws IOException, InterruptedException, ReflectiveOperationException, ExecutionException {
        User.UserId serverId = new User.UserId(5553);
        System.out.println("Successfully connected");
        System.out.println("Registering the user in the Online users");
        DataInputStream in = new DataInputStream(clientSocket.getInputStream());
        DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream());
        //wait for a register request first

        while(true){
            int readType = in.readInt();
            MessageType type = MessageType.fromInt(readType);
            if(type == MessageType.REGISTER_REQUEST){
                Message.RegisterRequest userRequest = new Message.RegisterRequest(in);
                System.out.println("Request received from user: " + userRequest.getUserId());
                System.out.println("Adding user to online users");
                connectedUsers.put(userRequest.getUserId(),new ClientHandler(clientSocket, userRequest.getUserId()));
                //Tell user the request was recieved and was handled
                Message.ChatMessage message = new Message.ChatMessage(serverId,"Successfully connected");
                message.toStream(out);
                out.flush();
                //Send Updated users
                Message.ChatMessage broadCastMessage = broadCastOnlineUsers();
                broadCastMessage.toStream(out);
                out.flush();

                break;
            }

        }
        System.out.println("Now accepting Chat requests from user");
        //Second loop to process the Query, fetch the data and resend them to the USER
        while(true){
            int readType = in.readInt();
            MessageType type = MessageType.fromInt(readType);
            if(type == MessageType.CHAT_MESSAGE){
                Message.ChatMessage messageFromUser = new Message.ChatMessage(in);
                System.out.println("Request received from user: " + messageFromUser.getRecipient());
                if(messageFromUser.getMessage().toLowerCase().equals("exit")){
                    System.out.println("User Disconnected");
                    //terminate the thread that was serving the user, remove the user from connected users.
                    Message.ChatMessage message2 = new Message.ChatMessage(serverId,"Goodbye!");
                    message2.toStream(out);
                    out.flush();
                    removeDisconnectedUser(messageFromUser.getRecipient());
                    break;
                }
                //Tell user the request was received and was handled, send back requested data.
                System.out.println("User said: " + messageFromUser.getMessage());
                handleMessageRequest(db,messageFromUser,out,Integer.valueOf(messageFromUser.getRecipient().toString().split("id=")[1].split("]")[0]));
                Message.ChatMessage message2 = new Message.ChatMessage(serverId,"Request Recieved");
                message2.toStream(out);
                out.flush();

            }

        }

    }

    //Removes a user from the connected users map.

    public static void removeDisconnectedUser(User.UserId user){
        if(connectedUsers.containsKey(user)){
            connectedUsers.remove(user);
            System.out.println("User " + user + "Disconnected from the server");
        }
    }

    //Fetch the list of users, Build a chat message that has them online.
    public static Message.ChatMessage broadCastOnlineUsers(){
        StringBuilder builder = new StringBuilder().append("The following users are online:");
        for(User.UserId user : connectedUsers.keySet()){
            builder.append("User: " + user + " Currently online");
            System.out.println("User: " + user + " Currently online");
        }
        builder.append(" Ghost user 9999 Currently online");
        return new Message.ChatMessage(new User.UserId(5553),builder.toString());
    }


    //Method for fetching a Document by its Collection ID , used for Debugging at the start of the project

    public static void fetchDocumentById(Firestore db) throws ExecutionException, InterruptedException {
        ApiFuture<QuerySnapshot> query = db.collection("Chatlogs").get();
        System.out.println("Got the data from the firestore database");
        QuerySnapshot querySnapshot = query.get();
        List<QueryDocumentSnapshot> documents = querySnapshot.getDocuments();
        System.out.println("Printing");
        for (QueryDocumentSnapshot document : documents) {
            System.out.println("Creation Date: " + document.getTimestamp("CreatedAt"));
        }

    }



    /*Helper functions for everything.*/



    //This method is to fetch the messages from the correct conversation id which we previously got the createOrDetectConversation method
    //We then filter it according to the query given by the Client and we extract the relevant messages, put them in a Map
    //and return it back as a result.
    private static List<Map<String, Object>> getMessagesFromConversation(Firestore db, String conversationId, String timePeriod) throws ExecutionException, InterruptedException {
        List<Map<String, Object>> messages = new ArrayList<>();
        CollectionReference messagesCollection = db.collection("Chatlogs").document(conversationId).collection("Messages");

        // Define start and end times based on the time period
        Instant startTime = null, endTime = Instant.now(); // Default to now
        if ("today".equalsIgnoreCase(timePeriod)) {
            startTime = Instant.now().truncatedTo(java.time.temporal.ChronoUnit.DAYS); // Start of today
        } else if ("yesterday".equalsIgnoreCase(timePeriod)) {
            startTime = Instant.now().minus(1, java.time.temporal.ChronoUnit.DAYS).truncatedTo(java.time.temporal.ChronoUnit.DAYS);
            endTime = Instant.now().truncatedTo(java.time.temporal.ChronoUnit.DAYS);
        } else if ("last week".equalsIgnoreCase(timePeriod)) {
            startTime = Instant.now().minus(7, java.time.temporal.ChronoUnit.DAYS).truncatedTo(java.time.temporal.ChronoUnit.DAYS);
        }

        // Query Firestore for messages within the time range
        Query query = messagesCollection.whereGreaterThanOrEqualTo("SentAt", Date.from(startTime));
        if (endTime != null) {
            query = query.whereLessThan("SentAt", Date.from(endTime));
        }

        ApiFuture<QuerySnapshot> querySnapshot = query.get();
        for (QueryDocumentSnapshot document : querySnapshot.get().getDocuments()) {
            Map<String, Object> messageData = new HashMap<>();
            messageData.put("SentBy", document.getString("SentBy"));
            messageData.put("Message", document.getString("Message"));
            messageData.put("SentAt", document.getTimestamp("SentAt").toDate().toString());
            messages.add(messageData);
        }

        return messages;
    }

    //Helper function to get the correct conversation between user1 and user 2 by checking if the 2 ids are present in the participants
    //Column in firestore
    private static List<String> getRelevantConversations(Firestore db, Integer user1ID, Integer user2ID) throws ExecutionException, InterruptedException {
        List<String> conversationIds = new ArrayList<>();
        ApiFuture<QuerySnapshot> query = db.collection("Chatlogs")
                .whereArrayContains("Participants", user1ID.toString())
                .get();

        QuerySnapshot querySnapshot = query.get();
        for (QueryDocumentSnapshot document : querySnapshot.getDocuments()) {
            List<String> participants = (List<String>) document.get("Participants");
            if (participants.contains(user2ID.toString())) {
                conversationIds.add(document.getId());
            }
        }
        return conversationIds;
    }

    //Fetching all the messages from the conversation for later filtering and processing.
    private static List<Map<String, Object>> getMessagesFromAllConversations(Firestore db, List<String> conversationIds, String timePeriod) throws ExecutionException, InterruptedException {
        List<Map<String, Object>> allMessages = new ArrayList<>();

        for (String conversationId : conversationIds) {
            List<Map<String, Object>> messages = getMessagesFromConversation(db, conversationId, timePeriod);
            allMessages.addAll(messages);
        }

        return allMessages;
    }


    //Using the helper functions we previously mentioned, the handleMessageRequest function filters the messages
    //and finally broadcasts them to the user that requested them, with timestamps and the user that originally
    //Sent them.
    private void handleMessageRequest(Firestore db, Message.ChatMessage message, DataOutputStream out, int userID) throws IOException, ExecutionException, InterruptedException {
        String timePeriod = message.getMessage(); // "today", "yesterday", "last week"

        // Get all relevant conversations
        List<String> conversationIds = getRelevantConversations(db, userID, 9999); // Example participants

        // Fetch messages from all relevant conversations
        List<Map<String, Object>> messages = getMessagesFromAllConversations(db, conversationIds, timePeriod);
        if (messages.isEmpty()) {
            Message.ChatMessage response = new Message.ChatMessage(new User.UserId(5553), "No messages found for " + timePeriod);
            response.toStream(out);
            out.flush();
            return;
        }

        // Format and send messages to the client
        StringBuilder responseText = new StringBuilder("Messages for " + timePeriod + ":\n");
        for (Map<String, Object> msg : messages) {
            responseText.append("[").append(msg.get("SentAt")).append("] ")
                    .append(msg.get("SentBy")).append(": ")
                    .append(msg.get("Message")).append("\n");
        }

        Message.ChatMessage response = new Message.ChatMessage(new User.UserId(5553), responseText.toString());
        response.toStream(out);
        out.flush();
    }


    //Function to avoid having to write the same sending logic for chat messages over and over again
    private void sendChatMessageUDP(Message.ChatMessage chatMsg, InetAddress serverAddress, int serverUdpPort)
            throws IOException {

        // 1) Serialize the ChatMessage
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

    //Not much to talk about here
    public static String getCurrentTimestamp() {
        // Use ISO_INSTANT to match Firestore format
        DateTimeFormatter formatter = DateTimeFormatter.ISO_INSTANT;
        String firestoreTimestamp = formatter.format(Instant.now());
        return firestoreTimestamp;
    }

    //Here too
    public static String getTodayDate(){
        String today= getCurrentTimestamp();
        return today.split("T")[0];
    }

    //Function logs the message in Firestore, as per Firestore's protocol, we need to send back a Hashmap
    //With the key being the column in the storage and the value being the value that needs to be stored
    //in that respective column
    private static void addMessageToConversation(Firestore db, String conversationId, String sender, String messageText) {
        Map<String, Object> messageData = new HashMap<>();
        messageData.put("SentBy", sender); // Sender's user ID or name
        messageData.put("Message", messageText); // The actual message
        messageData.put("SentAt", new Date()); // Current timestamp
        db.collection("Chatlogs").document(conversationId).collection("Messages").add(messageData);
    }

    //Returns the id of the current conversation if it exists, otherwise creates a new Conversation in that database
    //and returns its newly created ID at the end of it.
    public static String createOrDetectConversation(Firestore db, Integer user1ID, Integer user2ID) throws ExecutionException, InterruptedException {
        // Query all Chatlogs documents
        ApiFuture<QuerySnapshot> query = db.collection("Chatlogs").get();
        System.out.println("Got the data from the Firestore database");
        QuerySnapshot querySnapshot = query.get();
        List<QueryDocumentSnapshot> documents = querySnapshot.getDocuments();
        String todayDate = getTodayDate();

        for (QueryDocumentSnapshot document : documents) {
            // Check if both IDs exist in the Participants array
            List<String> participants = (List<String>) document.get("Participants");
            if (participants != null && participants.contains(user1ID.toString()) && participants.contains(user2ID.toString())) {
                // Check if the CreatedAt timestamp is from today
                if (document.getTimestamp("CreatedAt")
                        .toDate()
                        .toInstant()
                        .atZone(java.time.ZoneId.systemDefault())
                        .toLocalDate()
                        .toString()
                        .equals(todayDate)) {
                    System.out.println("The conversation already exists and has the ID: " + document.getId());
                    return document.getId(); // Return the existing conversation ID
                }
            }
        }

        System.out.println("No conversation matches. Creating a new conversation for message storage...");
        Map<String, Object> data = new HashMap<>();
        data.put("CreatedAt", new Date());
        data.put("Participants", Arrays.asList(user1ID.toString(), user2ID.toString()));

        // Add a new conversation document
        DocumentReference docRef = db.collection("Chatlogs").add(data).get();
        System.out.println("New conversation created with ID: " + docRef.getId());
        return docRef.getId(); // Return the new document ID
    }



    //Class that has Metadata we need for ease of use later on.
    public class ClientHandler {
        Socket clientSocket;
        User.UserId id;
        public ClientHandler(Socket clientSocket, User.UserId id){
            this.clientSocket = clientSocket;
            this.id = id;
        }



    }
}
