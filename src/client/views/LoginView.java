package client.views;

import client.Data;
import client.View;
import client.ViewNames;
import interfaces.IAuthentication;
import shared.logic.AuthResponse;
import shared.logic.RMIClient;
import shared.utils.Utils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.rmi.RemoteException;

public class LoginView extends View {

    private JPanel panel;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JFrame frame;
    private RMIClient<IAuthentication> auth;
    private boolean failed;
    private boolean fresh;

    public LoginView(String auth) {
        this.auth = new RMIClient<>(auth);
        this.failed = false;
        this.fresh = true;
    }

    @Override
    public void render(JFrame frame) {
        if( this.fresh ){
            this.firstRender(frame);
        }else{
            this.dirtyRender();
        }
    }

    private void dirtyRender(){
        if( this.failed ){
            int thirdLabel = 7*Data.UNIT;
            JLabel failedMessage = new JLabel("Usuario y/o contraseña erroneo",SwingConstants.CENTER);
            failedMessage.setForeground(Color.RED);
            Data.setTextCentered( failedMessage , thirdLabel , Data.TEXT_WIDTH + 2*Data.UNIT);
            panel.add(failedMessage);
        }
    }

    private void firstRender(JFrame frame){
        this.frame = frame;
        this.fresh = false;

        this.panel = new JPanel();
        panel.setBounds(0,0, Data.WIDTH,Data.HEIGHT);
        panel.setLayout(null);
        frame.getContentPane().add(panel);

        int firstLabel = Data.PADDING + Data.UNIT;

        JLabel userLabel = new JLabel("Tarjeta", SwingConstants.CENTER);
        Data.setTextCentered(userLabel , firstLabel);
        Data.setDefaultFont(userLabel,14);
        panel.add(userLabel);

        int firstField = firstLabel + (Data.UNIT* 3/4);

        this.usernameField = new JTextField();
        Data.setTextCentered( usernameField , firstField);
        Data.setDefaultFont(usernameField, 11, Font.PLAIN);
        panel.add(usernameField);

        int secondLabel = firstField + Data.UNIT + ((6*Data.UNIT)/10);

        JLabel passwordLabel = new JLabel("Contraseña",SwingConstants.CENTER);
        Data.setTextCentered( passwordLabel , secondLabel);
        Data.setDefaultFont(passwordLabel,14);
        panel.add(passwordLabel);

        int secondField = secondLabel + ((Data.UNIT* 3)/4);

        this.passwordField = new JPasswordField();
        Data.setTextCentered( passwordField , secondField );
        Data.setDefaultFont(passwordField);
        panel.add(passwordField);

        int buttonHeight = secondField + ((Data.UNIT* 7)/4);

        JButton loginButton = new JButton("Login");
        Data.setButtonCentered(loginButton, buttonHeight);
        Data.setDefaultFont(loginButton);
        panel.add(loginButton);
        loginButton.addActionListener(this::attemptLogin);
    }

    private void attemptLogin(ActionEvent arg){
        String user = this.usernameField.getText();
        String password = new String(this.passwordField.getPassword());
        String pass = Utils.sha256(password).get();
        try {
            IAuthentication stub = this.auth.getStub("Auth");
            AuthResponse response = stub.attemptLogin( user , pass );
            this.failed = !response.isSuccess();
            if( response.isSuccess() ){
                String token = response.getToken();
                if( token.isEmpty() ){
                    this.controller.changeView(ViewNames.INVENTORY);
                }else{
                    this.controller.changeView(ViewNames.ADMIN_MENU);
                }
            }
        } catch (RemoteException e) {
            this.failed = true;
            this.render(this.frame);
            this.frame.getContentPane().repaint();
        }
    }
}
