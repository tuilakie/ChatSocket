package Views;

import Model.Clients;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

/**
 * Views
 * Created by Nguyen Thanh Kien 19127456
 * Date 20/12/2021 - 2:03 CH
 * Description: ...
 */
public class signupView extends JFrame {
    public static signupView instance;
    private JPanel MainPanel;
    private JButton SIGNUPButton;
    private JTextField usernameTextField;
    private JPasswordField passwordPasswordField;
    private JPasswordField rePassowordPasswordField;
    private JTextField serverTextField;
    private Clients client;
    public static signupView getInstance() {
        if (instance == null) {
            instance = new signupView();
        }
        return instance;
    }

    public Clients getClient() {
        return client;
    }

    private signupView() {
        //close window
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                dispose();
            }
        });
        SIGNUPButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Sign up");
                if (usernameTextField.getText().equals("") && !passwordPasswordField.getText().equals("") && !rePassowordPasswordField.getText().equals("") && !serverTextField.getText().equals("")) {
                    JOptionPane.showMessageDialog(null, "Please fill all field");
                }
                if (!passwordPasswordField.getText().equals(rePassowordPasswordField.getText())) {
                    JOptionPane.showMessageDialog(null, "Password not match");
                }
                String IP = serverTextField.getText().split(":")[0];
                int port = Integer.parseInt(serverTextField.getText().split(":")[1]);

                try {
                    client = Clients.getInstance(IP, port);
                    String username = usernameTextField.getText();
                    String password = passwordPasswordField.getText();
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("username", username);
                    jsonObject.put("password", password);
                    jsonObject.put("type", "register");
                    jsonObject.put("sender",client.getHostname());
                    client.send(jsonObject.toString());
                } catch (IOException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Server is not available");
                }
            }

        });
    }

    public void createAndShowGUI() {
        new JFrame("Sign up");
        setContentPane(MainPanel);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setVisible(true);
    }
}
