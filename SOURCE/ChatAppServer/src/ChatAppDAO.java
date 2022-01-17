import Model.*;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Data
 * Created by Nguyen Thanh Kien 19127456
 * Date 20/12/2021 - 12:02 CH
 * Description: ...
 */
public class ChatAppDAO {
    final String FILE_NAME_USER = "Users.txt";
    final String FILE_NAME_CHAT = "Chat.txt";
    ArrayList<User> listUser = new ArrayList<>();
    ArrayList<ChatMessage> listChat = new ArrayList<>();
    private ChatAppDAO(){
        try {
            if(!new File(FILE_NAME_USER).exists()){
                new File(FILE_NAME_USER).createNewFile();
            }
            if (!new File(FILE_NAME_CHAT).exists()){
                new File(FILE_NAME_CHAT).createNewFile();
            }
            ObjectInputStream oisUsers = new ObjectInputStream(new FileInputStream(new File(FILE_NAME_USER)));
            ObjectInputStream oisChat = new ObjectInputStream(new FileInputStream(new File(FILE_NAME_CHAT)));
            listChat = (ArrayList<ChatMessage>) oisChat.readObject();
            listUser = (ArrayList<User>) oisUsers.readObject();
            oisUsers.close();
            oisChat.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        for (User user : listUser){
            user.setOnline(false);
        }

    }
    //get instance
    private static ChatAppDAO instance;

    public static ChatAppDAO getInstance() {
        if (instance == null) {
            instance = new ChatAppDAO();
        }
        return instance;
    }

    public void addUser(User user){
        listUser.add(user);
    }
    public ArrayList<User> getListUser() {
        return listUser;
    }

    public void removeUser(User user){
        listUser.remove(user);
        save();
    }
    public User getUser(String username){
        for (User user : listUser){
            if (user.getUsername().equals(username)){
                return user;
            }
        }
        return null;
    }
    public int CheckLogin(String username, String password){
        if(getUser(username) != null){
            if (getUser(username).getPassword().equals(password)){
                if(getUser(username).getOnline()){
                    return -1;
                }
                getUser(username).setOnline(true);
                save();
                return 1;
            }
        }
        return 0;
    }
    public boolean CheckRegister(String username, String password){
        if (getUser(username) == null){
            listUser.add(new User(username, password));
            save();
            return true;
        }
        return false;
    }
    public void Logout(String username){
        getUser(username).setOnline(false);
        save();
    }
    public void save(){
        try {
            ObjectOutputStream oosUser = new ObjectOutputStream(new FileOutputStream(new File(FILE_NAME_USER)));
            ObjectOutputStream oosChat = new ObjectOutputStream(new FileOutputStream(new File(FILE_NAME_CHAT)));
            oosUser.writeObject(listUser);
            oosChat.writeObject(listChat);
            oosUser.close();
            oosChat.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public ArrayList<User> getOnlineList(){
        ArrayList<User> list = new ArrayList<>();
        for (User user : listUser){
            if(user == null) continue;
            if (user.getOnline()){
                list.add(user);
            }
        }
        return list;
    }
    public boolean isOnline(String username){
        return getUser(username).getOnline();
    }

    public void addMessage(ChatMessage chatMessage){
        listChat.add(chatMessage);
    }
    public ArrayList<ChatMessage> getListChat(){
        return listChat;
    }
    public void removeChat(ChatMessage chatMessage){
        listChat.remove(chatMessage);
    }
}


