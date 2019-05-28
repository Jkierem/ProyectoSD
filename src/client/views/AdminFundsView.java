package client.views;

import client.GUIHelpers;
import client.View;
import client.ViewNames;
import interfaces.IAuthentication;
import interfaces.IClient;
import shared.logic.RMIClient;
import shared.logic.RMIServer;
import shared.utils.Utils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

public class AdminFundsView extends View implements IClient {

    private RMIClient<IAuthentication> auth;
    private RMIServer<AdminFundsView,IClient> server;
    private int state;
    private boolean bound;

    private JFrame frame;
    private JComboBox<String> users;
    private JTextField amount;
    private String selectedUser;

    public AdminFundsView(String auth) {
        this.auth = new RMIClient<>(auth);
        this.state = 0;
        this.bound = false;
    }

    @Override
    public void alertCommit(int tid) throws RemoteException {
        this.state = 1;
        this.repaint();
    }

    @Override
    public void alertAbort(int tid, boolean manual) throws RemoteException {
        this.state = -1;
        this.repaint();
    }

    @SuppressWarnings("Duplicates")
    @Override
    public void render(JFrame frame) {
        if( this.frame == null ){
            this.frame = frame;
        }

        List<String> userList = this.getUsers();
        this.selectedUser = !userList.isEmpty() ? userList.get(0): "";
        users = new JComboBox<>(userList.toArray(new String[userList.size()]));
        GUIHelpers.setComboCentered(users, GUIHelpers.UNIT*3);
        GUIHelpers.setDefaultFont(users);

        amount = new JTextField("1");
        amount.setBounds( users.getX() , users.getY() + GUIHelpers.UNIT , users.getWidth() , users.getHeight());
        GUIHelpers.setDefaultFont(amount);

        JButton deposit = new JButton("Depositar");
        deposit.setBounds( users.getX() , users.getY() + 2* GUIHelpers.UNIT , users.getWidth() , users.getHeight());
        GUIHelpers.setDefaultFont(deposit);

        JButton back = new JButton("Atras");
        back.setBounds( users.getX() , users.getY() + 3* GUIHelpers.UNIT , users.getWidth() , users.getHeight());
        GUIHelpers.setDefaultFont(back);

        users.addActionListener(this::onSelect);
        deposit.addActionListener(this::deposit);
        back.addActionListener(this::back);

        frame.add(users);
        frame.add(amount);
        frame.add(deposit);
        frame.add(back);

        if( this.state < 0 ){
            JLabel failed = new JLabel("Failed to make deposit. Please try again later", SwingConstants.CENTER);
            failed.setForeground(Color.RED);
            GUIHelpers.setTextCentered(failed, users.getY() + 4* GUIHelpers.UNIT , GUIHelpers.WIDTH);
            frame.add(failed);
        }

        if( this.state > 0 ){
            JLabel success = new JLabel("Deposit successful", SwingConstants.CENTER);
            success.setForeground(Color.GREEN);
            GUIHelpers.setTextCentered(success, users.getY() + 4* GUIHelpers.UNIT , GUIHelpers.WIDTH);
            frame.add(success);
        }
    }

    private void repaint(){
        this.frame.getContentPane().removeAll();
        this.render(this.frame);
        this.frame.getContentPane().repaint();
    }

    private List<String> getUsers(){
        try {
            IAuthentication stub = this.auth.getStub("Auth");
            return new ArrayList<>(stub.getUserList(this.controller.getSharedState().getToken()));
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }

    }

    private void onSelect(ActionEvent arg){
        this.selectedUser = (String)this.users.getSelectedItem();
    }

    private void back(ActionEvent arg){
        this.server.unbindStub("AdminClient");
        this.controller.changeView(ViewNames.ADMIN_MENU);
    }

    private void deposit(ActionEvent arg){
        int amount = Integer.parseInt(this.amount.getText());
        try {
            String host = Utils.getLocalAddress();
            String binding = "AdminClient";
            if( !this.bound ){
                server = new RMIServer<>(() -> this);
                server.createAndRebind(binding);
                this.bound = true;
            }
            IAuthentication auth = this.auth.getStub("Auth");
            int tid = auth.attemptChangeBalanceWithResponse( host, binding, selectedUser , amount , this.controller.getSharedState().getToken());
            auth.commitBalanceChange( tid , this.controller.getSharedState().getToken() );
        } catch (Exception e) {
            e.printStackTrace();
            this.state = -1;
            this.repaint();
        }
    }
}
