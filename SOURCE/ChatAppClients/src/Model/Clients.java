package Model;

import Views.*;
import org.json.JSONObject;

import javax.swing.*;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * PACKAGE_NAME
 * Created by Nguyen Thanh Kien 19127456
 * Date 20/12/2021 - 8:19 CH
 * Description: ...
 */

/*https://stackoverflow.com/questions/13115784/sending-a-message-to-all-clients-client-server-communication/13116162?fbclid=IwAR22BdsH3lmBnGFDXSYnxcPhxtPTZj1fobtAuHA5euYL-eLnLfMhPpyuVco*/
public class Clients {
    public static Clients instance;
    private ConnectionToServer connectionToServer;
    private Socket socket;
    private LinkedBlockingQueue<Object> messages;
    public static DefaultListModel<String> onlineList = new DefaultListModel<String>();
    private String username = null;
    private ArrayList<ChatMessage> chatMessages = new ArrayList<>();

    private Clients(String IP, int port) throws IOException {
        socket = new Socket(IP, port);
        connectionToServer = new ConnectionToServer(socket);
        messages = new LinkedBlockingQueue<Object>();
        Thread MessageHandler = new Thread(() -> {
            while (true) {
                try {

                    Object message = messages.take();
                    //TODO: Handle message
                    JSONObject jsonObject = new JSONObject(message.toString());
                    String type = jsonObject.getString("type");
                    System.out.println(jsonObject.toString());
                    switch (type) {
                        case "login":
                        {
                            if(jsonObject.getString("status").equals("success")){
                                System.out.println("Login success");
                                // call JOptionPane
                                JOptionPane.showMessageDialog(null, "Login Success");
                                loginView.getInstance().dispose();
                                ChatView.getInstance(loginView.getInstance().getClient()).getOnlineList().setModel(onlineList);
                                ChatView.getInstance(loginView.getInstance().getClient()).createAndShowGUI();
                            }
                            else if(jsonObject.getString("status").equals("already login")){
                                System.out.println("Login fail");
                                // call JOptionPane
                                JOptionPane.showMessageDialog(null, "Already login");
                            }
                            else{
                                System.out.println("username or password is wrong");
                                // call JOptionPane
                                JOptionPane.showMessageDialog(null, "username or password is wrong");
                            }
                        }
                        break;
                        case "register":
                        {
                            if(jsonObject.getString("status").equals("success")){
                                System.out.println("Register success");
                                // call JOptionPane
                                JOptionPane.showMessageDialog(null, "Register Success");
                                signupView.getInstance().dispose();
                            }
                            else{
                                System.out.println("Register fail");
                                // call JOptionPane
                                JOptionPane.showMessageDialog(null, "Register Fail");

                            }
                        }
                        break;
                        case "online":
                        {
                            String[] onlineList = jsonObject.getString("onlineList").split(",");
                            Clients.onlineList.clear();
                            for(String online : onlineList){
                                if(online.equals(username)) continue;
                                Clients.onlineList.addElement(online);
                            }
                            ChatView.getInstance(loginView.getInstance().getClient()).getOnlineList().setModel(Clients.onlineList);
                        }
                        break;
                        case "message":
                        {
                            ChatMessage content = ChatMessage.parse(jsonObject.getString("content"));
                            chatMessages.add(content);
                            System.out.println(content.getContent());
                            ChatView.getInstance(loginView.getInstance().getClient()).addMessage(content.getFrom(), content.getContent(),content.isFile());
                        }
                        break;
                        case "chatHistory":
                        {
                            String[] chatHistory = jsonObject.getString("content").split(",");
                            for(String history : chatHistory){
                                ChatMessage content = ChatMessage.parse(history);
                                chatMessages.add(content);
                            }
                        }
                        break;
                    }
                } catch (InterruptedException  e) {
                    e.printStackTrace();
                }
            }
        });
        MessageHandler.setDaemon(true);
        MessageHandler.start();
    }

    public Socket getSocket() {
        return connectionToServer.socket;
    }

    private class ConnectionToServer {
        private ObjectInputStream input;
        private ObjectOutputStream output;
        private Socket socket;
        private String hostname;

        public ConnectionToServer(Socket socket) throws IOException {
            this.socket = socket;
            this.hostname = socket.getLocalAddress().getHostAddress() + ":" + socket.getLocalPort();
            System.out.println("Connected to " + hostname);
            this.output = new ObjectOutputStream(socket.getOutputStream());
            this.input = new ObjectInputStream(socket.getInputStream());
            Thread Read = new Thread() {
                public void run() {
                    try {
                        while (true) {
                            Object message = input.readObject();
                            if (message instanceof SimpleFile){
                                SimpleFile file = (SimpleFile) message;
                                System.out.println(file.getFilename());
                                file.CreateFile();
                                continue;
                            }
                            messages.put(message);
                        }
                    } catch (IOException | InterruptedException | ClassNotFoundException e) {
                        e.printStackTrace();
                        JOptionPane.showMessageDialog(null, "Server is closed");
                        System.exit(0);
                    }
                }
            };
            Read.start();
        }

        private void write(Object message){
            try {
                output.writeObject(message);
                output.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void close() throws IOException {
            socket.close();
            output.close();
            input.close();
        }

        public boolean isClosed() {
            return socket.isClosed();
        }
        public String getHostname() {
            return hostname;
        }
    }

    public void send(Object message) {
        connectionToServer.write(message);
    }

    public void close() throws IOException {
        socket.close();
        connectionToServer.close();
    }
    public String getHostname(){
        return connectionToServer.getHostname();
    }
    public void setUsername(String username){
        this.username = username;
    }
    public String getUsername(){
        return username;
    }
    public static Clients getInstance(String IP, int port) throws IOException {
        if (instance == null) {
            instance = new Clients(IP, port);
        }
        return instance;
    }
    public ArrayList<ChatMessage> getChatMessagesOfClient(String receiver, String sender){
        ArrayList<ChatMessage> content = new ArrayList<>();
        if(chatMessages.size() == 0)
            return content;
        for (ChatMessage message : chatMessages) {
            if(message == null)
                continue;
            if (message.getTo().equals(receiver) && message.getFrom().equals(sender) || message.getTo().equals(sender) && message.getFrom().equals(receiver)) {
                content.add(message);
            }
        }
        return content;
    }
    public ArrayList<ChatMessage> getChatMessages(){
        return chatMessages;
    }
}
