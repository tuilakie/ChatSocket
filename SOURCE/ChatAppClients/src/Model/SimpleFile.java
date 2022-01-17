package Model;

import java.io.*;

/**
 * Model
 * Created by Nguyen Thanh Kien 19127456
 * Date 24/12/2021 - 9:58 CH
 * Description: ...
 */
public class SimpleFile implements Serializable {
    private String sender;
    private String receiver;
    private String filename;
    private byte[] data;

    public SimpleFile(File file,String sender, String receiver) throws IOException {
        this.sender = sender;
        this.receiver = receiver;
        this.filename = file.getName();
        this.data = new byte[(int) file.length()];
        BufferedInputStream bis = new BufferedInputStream( new FileInputStream(file));
        bis.read(data, 0, data.length);
        bis.close();
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }
    public void CreateFile() throws IOException {
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filename));
        bos.write(data, 0, data.length);
        bos.flush();
        bos.close();
    }
}
