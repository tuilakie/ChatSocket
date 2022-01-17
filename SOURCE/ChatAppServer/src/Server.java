
import Model.*;
import org.json.JSONObject;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * PACKAGE_NAME
 * Created by Nguyen Thanh Kien 19127456
 * Date 20/12/2021 - 12:00 CH
 * Description: ...
 */
/*https://stackoverflow.com/questions/13115784/sending-a-message-to-all-clients-client-server-communication/13116162?fbclid=IwAR22BdsH3lmBnGFDXSYnxcPhxtPTZj1fobtAuHA5euYL-eLnLfMhPpyuVco*/

public class Server {
    private ServerSocket serverSocket;
    ChatAppDAO DAO;
    ArrayList<ConnectionToClient> clients;
    LinkedBlockingQueue<Object> messages;

    public Server(int PORT) {
        try {
            serverSocket = new ServerSocket(PORT);
            DAO = ChatAppDAO.getInstance();
            clients = new ArrayList<ConnectionToClient>();
            messages = new LinkedBlockingQueue<Object>();
            System.out.println("Server is running");
            Thread Accept = new Thread() {
                public void run() {
                    while (true) {
                        try {
                            Socket socket = serverSocket.accept();
                            ConnectionToClient client = new ConnectionToClient(socket);
                            clients.add(client);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            };
            Accept.start();
            Thread MessageHandler = new Thread() {
                public void run() {
                    while (true) {
                        try {
                            Object message = messages.take();
                            //TODO: handle message
                            System.out.println("Message: " + message);
                            //parse message to JSON object
                            JSONObject json = new JSONObject(message.toString());
                            String type = json.getString("type");
                            System.out.println("type: " + type);
                            switch (type) {
                                case "login": {
                                    String sender = json.getString("sender");
                                    System.out.println("sender: " + sender);
                                    JSONObject jsonLogin = new JSONObject();
                                    String username = json.getString("username");
                                    String password = json.getString("password");
                                    if (DAO.CheckLogin(username, password) == 1) {
                                        System.out.println("Login success");
                                        //send message to client
                                        jsonLogin.put("type", "login");
                                        jsonLogin.put("status", "success");
                                        setUsername(sender, username);
                                        SendChatHistory(sender);
                                        UpdateOnlineUsers();
                                    } else if (DAO.CheckLogin(username, password) == -1) {
                                        // already login
                                        System.out.println("Already login");
                                        jsonLogin.put("type", "login");
                                        jsonLogin.put("status", "already login");
                                    }
                                    else  {
                                        System.out.println("Login failed");
                                        //send message to client
                                        jsonLogin.put("type", "login");
                                        jsonLogin.put("status", "failed");;
                                    }
                                    SendToOneByHostname(sender, jsonLogin.toString());
                                }
                                break;
                                case "register": {
                                    String sender = json.getString("sender");
                                    System.out.println("sender: " + sender);
                                    JSONObject jsonRegister = new JSONObject();
                                    String username = json.getString("username");
                                    String password = json.getString("password");
                                    System.out.println("Username: " + username);
                                    System.out.println("Password: " + password);
                                    if (DAO.CheckRegister(username, password)) {
                                        System.out.println("Register success");
                                        //send message to client
                                        jsonRegister.put("type", "register");
                                        jsonRegister.put("status", "success");
                                    }else {
                                        System.out.println("Register failed");
                                        //send message to client
                                        jsonRegister.put("type", "register");
                                        jsonRegister.put("status", "failed");
                                    }
                                    SendToOneByHostname(sender, jsonRegister.toString());
                                }
                                break;
                                case "exit": {
                                    String user = json.getString("user");
                                    DAO.Logout(user);
                                    UpdateOnlineUsers();
                                }
                                break;
                                case "message": {
                                   ChatMessage cm = ChatMessage.parse(json.getString("content"));
                                   System.out.println("-Message: " + cm.getContent() + " from " + cm.getFrom() + " to " + cm.getTo());
                                    if(DAO.isOnline(cm.getTo())) {
                                        DAO.addMessage(cm);
                                        sendToOne(cm.getTo(), json.toString());
                                    }
                                }
                                break;
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            };
            MessageHandler.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new Server(9999);
    }

    private class ConnectionToClient {
        private Socket socket;
        private ObjectInputStream input;
        private ObjectOutputStream output;
        private String hostname;
        private String username = null;

        ConnectionToClient(Socket socket) throws IOException {
            this.socket = socket;
            this.hostname = socket.getInetAddress().getHostAddress() + ":" + socket.getPort();
            System.out.println("Connected to " + hostname);
            output = new ObjectOutputStream(socket.getOutputStream());
            input = new ObjectInputStream(socket.getInputStream());
            Thread Read = new Thread() {
                public void run() {
                    try {
                        while (true) {
                            Object message = input.readObject();
                            if (message instanceof SimpleFile) {
                                SimpleFile file = (SimpleFile) message;
                                String sender = file.getSender();
                                String receiver = file.getReceiver();
                                String filename = file.getFilename();
                                sendToOne(receiver, file);
                                continue;
                            }
                            messages.put(message);
                        }
                    } catch (IOException | InterruptedException | ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            };
            Read.start();
        }

        public void Write(Object message)  {
            try {
                output.writeObject(message);
                output.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public boolean isClosed() {
            return socket.isClosed();
        }

        public void close() throws IOException {
            socket.close();
            input.close();
            output.close();
        }
    }

    public void sendToOne(String username, Object message) {
        for (ConnectionToClient client : clients) {
            if (client.username.equals(username)) {
                client.Write(message);
            }
        }
    }
    public void SendToOneByHostname(String hostname, Object message) {
        for (ConnectionToClient client : clients) {
            if (client.hostname.equals(hostname)) {
                client.Write(message);
            }
        }
    }

    public void sendToAll(Object message)  {
        for (ConnectionToClient client : clients) {
            client.Write(message);
        }
    }
    public void UpdateOnlineUsers() {
        JSONObject jsonUser = new JSONObject();
        ArrayList<User> usersList = DAO.getOnlineList();
        String onlineList = "";
        jsonUser.put("type", "online");
        for (User user : usersList) {
            onlineList += user.getUsername() + ",";
        }
        jsonUser.put("onlineList", onlineList);
        sendToAll(jsonUser.toString());
    }
    public void SendChatHistory(String hostname) {
        JSONObject jsonChat = new JSONObject();
        jsonChat.put("type", "chatHistory");
        ArrayList<ChatMessage> chatList = DAO.getListChat();
        String chatHistory = "";
        for (ChatMessage chat : chatList) {
            chatHistory += chat.toString() + ",";
        }
        jsonChat.put("content", chatHistory);
        SendToOneByHostname(hostname, jsonChat.toString());
    }
    public void setUsername(String hostname, String username) {
        for (ConnectionToClient client : clients) {
            if (client.hostname.equals(hostname)) {
                client.username = username;
            }
        }
    }
}