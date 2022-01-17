package Views;

import Model.*;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Views
 * Created by Nguyen Thanh Kien 19127456
 * Date 22/12/2021 - 8:55 CH
 * Description: ...
 */
public class ChatView extends JFrame {
    public static ChatView instance;
    private JList OnlineList;
    private JButton sendButton;
    private JTextField Message;
    private JPanel MainPanel;
    private JButton fileButton;
    private JButton exitButton;
    private JLabel ChatWith;
    private JScrollPane ChatLog;
    private Clients client;
    public String selectedUser = null;

    private ChatView(Clients client) {
        this.client = client;
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("type", "exit");
                jsonObject.put("user",client.getUsername());
                client.send(jsonObject.toString());
                System.exit(0);
            }
        });
        //action close window
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("type", "exit");
                jsonObject.put("user",client.getUsername());
                client.send(jsonObject.toString());
                System.exit(0);
            }
        });

        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(Message.getText().length() == 0){
                    JOptionPane.showMessageDialog(null,"Message is empty");
                    return;
                }
                if(OnlineList.getSelectedValue() == null){
                    JOptionPane.showMessageDialog(null,"Please select a user");
                }
                String message = Message.getText();
                String Sender = client.getUsername();
                String Receiver = OnlineList.getSelectedValue().toString();
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("type", "message");
                jsonObject.put("content",new ChatMessage(message,Sender,Receiver,false));
                client.send(jsonObject.toString());
                addMessage(Sender,message,false);
                client.getChatMessages().add(new ChatMessage(message,Sender,Receiver,false));
                Message.setText("");
            }
        });
        // if select user in list
        OnlineList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(OnlineList.getSelectedValue() == null){
                    return;
                }
                selectedUser = OnlineList.getSelectedValue().toString();
                ChatWith.setText("Chat with " + selectedUser);
                JPanel newPanel = createChatLog(client.getUsername(),selectedUser);
                ChatLog.setViewportView(newPanel);
            }
        });
        fileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(selectedUser == null){
                    JOptionPane.showMessageDialog(null,"Please select a user");
                    return;
                }
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle("Choose file to send");
                if(fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION){
                    File file = fileChooser.getSelectedFile();
                    try {
                        SimpleFile sf = new SimpleFile(file,client.getUsername(),selectedUser);
                        client.send(sf);
                        ChatMessage cm = new ChatMessage(file.getName(),client.getUsername(),selectedUser,true);
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("type","message");
                        jsonObject.put("content",cm);
                        client.send(jsonObject.toString());
                        addMessage(client.getUsername(),file.getName(),true);
                        client.getChatMessages().add(cm);
                    }
                    catch (Exception ex){
                        ex.printStackTrace();
                    }
                }
            }
        });
    }

    public static ChatView getInstance(Clients client){
        if (instance == null) {
            instance = new ChatView(client);
        }
        return instance;
    }

    public void createAndShowGUI() {
       ChatView.getInstance(client).setTitle("Login with: "+client.getUsername());
        ChatView.getInstance(client).setContentPane(MainPanel);
        ChatView.getInstance(client).setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ChatView.getInstance(client).pack();
        ChatView.getInstance(client).setVisible(true);
    }


    public JPanel createChatLog(String sender, String receiver){
        ArrayList<ChatMessage> content = client.getChatMessagesOfClient(sender,receiver);
        ChatMessage.SortByDate(content);
        if(content == null){
            return new JPanel();
        }

        JPanel chatLog = new JPanel();
        chatLog.setLayout(new BoxLayout(chatLog, BoxLayout.PAGE_AXIS));

        //chat log
        for (ChatMessage cm : content){
            JPanel message = new JPanel();
            String From = cm.getFrom();
            JLabel contentLabel = new JLabel(cm.getContent());
            contentLabel.setFont(new Font("Arial",Font.PLAIN,15));
            contentLabel.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
            contentLabel.setOpaque(true);
            if(From.equals(sender)){
                message.setLayout(new FlowLayout(FlowLayout.RIGHT));
                contentLabel.setBackground(Color.BLUE);
                contentLabel.setForeground(Color.WHITE);
            }
            else if(From.equals(receiver)){
                message.setLayout(new FlowLayout(FlowLayout.LEFT));
                contentLabel.setBackground(Color.lightGray);
                contentLabel.setForeground(Color.BLACK);
            }
            message.add(contentLabel);
            chatLog.add(message);
            if(cm.isFile()){
                //underline
                contentLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
                contentLabel.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        // open file
                        try {
                            File file = new File(cm.getContent());
                            Desktop.getDesktop().open(file);
                        } catch (IOException ex) {
                            ex.printStackTrace();
                            JOptionPane.showMessageDialog(null,"File not found");
                        }
                    }
                });
            }
            message.add(contentLabel);
            chatLog.add(message);
            chatLog.revalidate();
            chatLog.repaint();
        }
        return chatLog;
    }

    public JList<String> getOnlineList() {
        return OnlineList;
    }
    public void addMessage(String username, String content, boolean isFile){
        JPanel newChat = (JPanel) ChatLog.getViewport().getView();
        if(newChat == null){
            return;
        }
        if(!(selectedUser.equals(username)||client.getUsername().equals(username))){
            return;
        }
        JPanel message = new JPanel();
        String From = username;
        JLabel contentLabel = new JLabel(content);
        contentLabel.setFont(new Font("Arial", Font.PLAIN, 15));
        contentLabel.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
        contentLabel.setOpaque(true);

        if(From.equals(client.getUsername())){
            message.setLayout(new FlowLayout(FlowLayout.RIGHT));
            contentLabel.setBackground(Color.BLUE);
            contentLabel.setForeground(Color.WHITE);
        }
        else{
            message.setLayout(new FlowLayout(FlowLayout.LEFT));
            contentLabel.setBackground(Color.lightGray);
            contentLabel.setForeground(Color.BLACK);
        }
        message.setOpaque(true);
        if(isFile){
            //underline
            contentLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
            contentLabel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    // open file
                    try {
                        File file = new File(content);
                        Desktop.getDesktop().open(file);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(null,"File not found");
                    }
                }
            });
        }
        message.add(contentLabel);
        newChat.add(message);
        newChat.revalidate();
        newChat.repaint();
    }
}
