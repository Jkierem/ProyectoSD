package client.views;

import client.GUIHelpers;
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
            int thirdLabel = 7* GUIHelpers.UNIT;
            JLabel failedMessage = new JLabel("Usuario y/o contraseña erroneo",SwingConstants.CENTER);
            failedMessage.setForeground(Color.RED);
            GUIHelpers.setTextCentered( failedMessage , thirdLabel , GUIHelpers.TEXT_WIDTH + 2* GUIHelpers.UNIT);
            panel.add(failedMessage);
        }
    }

    private void firstRender(JFrame frame){
        this.frame = frame;
        this.fresh = false;

        this.panel = new JPanel();
        panel.setBounds(0,0, GUIHelpers.WIDTH, GUIHelpers.HEIGHT);
        panel.setLayout(null);
        frame.getContentPane().add(panel);

        int firstLabel = GUIHelpers.PADDING + GUIHelpers.UNIT;

        JLabel userLabel = new JLabel("Tarjeta", SwingConstants.CENTER);
        GUIHelpers.setTextCentered(userLabel , firstLabel);
        GUIHelpers.setDefaultFont(userLabel,14);
        panel.add(userLabel);

        int firstField = firstLabel + (GUIHelpers.UNIT* 3/4);

        this.usernameField = new JTextField();
        GUIHelpers.setTextCentered( usernameField , firstField);
        GUIHelpers.setDefaultFont(usernameField, 11, Font.PLAIN);
        panel.add(usernameField);

        int secondLabel = firstField + GUIHelpers.UNIT + ((6* GUIHelpers.UNIT)/10);

        JLabel passwordLabel = new JLabel("Contraseña",SwingConstants.CENTER);
        GUIHelpers.setTextCentered( passwordLabel , secondLabel);
        GUIHelpers.setDefaultFont(passwordLabel,14);
        panel.add(passwordLabel);

        int secondField = secondLabel + ((GUIHelpers.UNIT* 3)/4);

        this.passwordField = new JPasswordField();
        GUIHelpers.setTextCentered( passwordField , secondField );
        GUIHelpers.setDefaultFont(passwordField);
        panel.add(passwordField);

        int buttonHeight = secondField + ((GUIHelpers.UNIT* 7)/4);

        JButton loginButton = new JButton("Login");
        GUIHelpers.setButtonCentered(loginButton, buttonHeight);
        GUIHelpers.setDefaultFont(loginButton);
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
                this.controller.getSharedState().setUser(user);
                if( token.isEmpty() ){
                    this.controller.changeView(ViewNames.INVENTORY);
                }else{
                    this.controller.getSharedState().setToken(token);
                    this.controller.changeView(ViewNames.ADMIN_MENU);
                }
            }else{
                this.render(this.frame);
                this.frame.getContentPane().repaint();
            }
        } catch (RemoteException e) {
            e.printStackTrace();
            this.failed = true;
            this.render(this.frame);
            this.frame.getContentPane().repaint();
        }
    }
}
