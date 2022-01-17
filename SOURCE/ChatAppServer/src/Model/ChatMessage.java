package Model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

/**
 * Model
 * Created by Nguyen Thanh Kien 19127456
 * Date 15/01/2022 - 3:43 CH
 * Description: ...
 */
public class ChatMessage  implements Serializable {
    public static final long serialVersionUID = 1L;
    private String content;
    private String From;
    private String To;
    private boolean isFile;
    private Date date;

    public ChatMessage(String content, String from, String to, boolean isFile) {
        this.content = content;
        From = from;
        To = to;
        this.isFile = isFile;
        date = new Date();
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getFrom() {
        return From;
    }

    public void setFrom(String from) {
        From = from;
    }

    public String getTo() {
        return To;
    }

    public void setTo(String to) {
        To = to;
    }

    public boolean isFile() {
        return isFile;
    }

    public void setFile(boolean file) {
        isFile = file;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
    @Override
    public String toString() {
        return content + "|" + From + "|" + To + "|" + isFile + "|" + date;
    }
    // parse
    public static ChatMessage parse(String msg) {
        String[] arr = msg.split("\\|");
        if(arr.length != 5) return null;
        return new ChatMessage(arr[0], arr[1], arr[2], Boolean.parseBoolean(arr[3]));
    }
    // sort by date
    public static void SortByDate(ArrayList<ChatMessage> list){
        Collections.sort(list, new Comparator<ChatMessage>() {
            @Override
            public int compare(ChatMessage o1, ChatMessage o2) {
                return o1.getDate().compareTo(o2.getDate());
            }
        });
    }
}

