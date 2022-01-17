package Views;

import Model.*;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

/**
 * Views
 * Created by Nguyen Thanh Kien 19127456
 * Date 20/12/2021 - 1:33 CH
 * Description: ...
 */
public class loginView extends JFrame {
    public static loginView instance;
    private JPanel MainPanel;
    private JButton SIGNINButton;
    private JButton SIGNUPButton;
    private JTextField usernameTextField;
    private JTextField ServerTextField;
    private JPasswordField passwordPasswordField;
    Clients client;
    public static loginView getInstance() {
        if (instance == null) {
            instance = new loginView();
        }
        return instance;
    }
    public Clients getClient() {
        return client;
    }

    private loginView() {
        //Window close listener
        this.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                try {
                    if(client != null) {
                        client.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        SIGNUPButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                signupView.getInstance().createAndShowGUI();
            }
        });
        SIGNINButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(usernameTextField.getText().isEmpty() || passwordPasswordField.getText().isEmpty() || ServerTextField.getText().isEmpty()){
                    JOptionPane.showMessageDialog(null, "Please fill all the information");
                }
                else {
                    String IP = ServerTextField.getText().split(":")[0];
                    int port = Integer.parseInt(ServerTextField.getText().split(":")[1]);
                    try {
                        client = Clients.getInstance(IP, port);
                        String username = usernameTextField.getText();
                        String password = passwordPasswordField.getText();
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("type", "login");
                        jsonObject.put("username", username);
                        jsonObject.put("password", password);
                        //get hostname and port of client
                        jsonObject.put("sender", client.getHostname());
                        client.send(jsonObject.toString());
                        client.setUsername(username);

                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                        JOptionPane.showMessageDialog(null, "Server is not available");
                    }
                }
            }
        });
    }

    public void createAndShowGUI(){
        getInstance().setTitle("Login");
        getInstance().setContentPane(MainPanel);
        getInstance().setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getInstance().pack();
        getInstance().setVisible(true);
    }

    public Clients getClients() {
        return client;
    }
}
