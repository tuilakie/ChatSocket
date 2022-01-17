package Model;

import java.io.*;

/**
 * PACKAGE_NAME
 * Created by Nguyen Thanh Kien 19127456
 * Date 20/12/2021 - 11:58 SA
 * Description: ...
 */

public class User implements Serializable {
    public static final long serialVersionUID = 1L;
    private String username;
    private String password;
    private boolean isOnline;
    public User(String userName, String password) {
        this.username = userName;
        this.password = password;
        this.isOnline = false;
    }

    public void setOnline(boolean isOnline) {
        this.isOnline = isOnline;
    }
    public boolean getOnline() {
        return isOnline;
    }
    public String getUsername() {
        return username;
    }
    public String getPassword() {
        return password;
    }
    public void setUserName(String userName) {
        this.username = userName;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public String toString() {
        return username + "," + password;
    }
    public static User parseUser(String user) {
        String[] userInfo = user.split(",");
        return new User(userInfo[0], userInfo[1]);
    }
}