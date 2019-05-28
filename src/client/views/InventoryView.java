package client.views;

import client.GUIHelpers;
import client.View;
import interfaces.IAuthentication;
import interfaces.IClient;
import interfaces.IProductSystem;
import shared.logic.RMIClient;
import shared.logic.RMIServer;
import shared.utils.Utils;
import store.Product;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.List;

public class InventoryView extends View implements IClient {

    private JPanel panel;
    private JTable table;
    private int balance;
    private RMIClient<IAuthentication> auth;
    private RMIClient<IProductSystem> market;
    private RMIServer<InventoryView,IClient> server;
    private int state;
    private JFrame frame;
    private boolean isBound;

    public InventoryView(String auth, String market) {
        this.auth = new RMIClient<>(auth);
        this.market = new RMIClient<>(market);
        this.state = 0;
        this.isBound = false;
    }

    @Override
    public void alertCommit(int tid) throws RemoteException {
        System.out.println("WOOOO");
        this.state = 1;
        this.frame.getContentPane().removeAll();
        this.render(this.frame);
        this.frame.getContentPane().repaint();
    }

    @Override
    public void alertAbort(int tid, boolean manual) throws RemoteException {
        System.out.println("NOOOOO");
        this.state = -1;
        this.frame.getContentPane().removeAll();
        this.render(this.frame);
        this.frame.getContentPane().repaint();
    }

    @Override
    public void render(JFrame frame) {
        if( this.frame == null ){
            this.frame = frame;
        }

        this.panel = new JPanel();
        panel.setBounds(0,0, GUIHelpers.WIDTH, GUIHelpers.HEIGHT);
        panel.setLayout(null);
        frame.getContentPane().add(panel);

        JLabel label = new JLabel("Market");
        GUIHelpers.setTextCentered(label , 10 , 300);
        label.setHorizontalAlignment(JLabel.CENTER);
        label.setVerticalAlignment(JLabel.CENTER);
        GUIHelpers.setDefaultFont(label,20);

        JLabel balance = new JLabel("Balance: " + this.getBalance());
        GUIHelpers.setTextCentered(balance , 10 , GUIHelpers.WIDTH - GUIHelpers.PADDING);
        balance.setHorizontalAlignment(JLabel.LEFT);
        balance.setVerticalAlignment(JLabel.TOP);
        GUIHelpers.setDefaultFont(balance , 16);

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBounds( 0 , (7* GUIHelpers.UNIT)/5 , GUIHelpers.WIDTH, 6* GUIHelpers.UNIT);

        JPanel inventory = this.createInventory();
        scrollPane.setViewportView(inventory);

        JButton checkout = new JButton("Comprar");
        GUIHelpers.setButtonCentered( checkout , 8* GUIHelpers.UNIT , GUIHelpers.UNIT);
        GUIHelpers.setDefaultFont(checkout);
        checkout.addActionListener(this::purchase);

        JButton logout = new JButton("Salir");
        logout.setBounds( GUIHelpers.UNIT, 8* GUIHelpers.UNIT, GUIHelpers.UNIT + GUIHelpers.BUTTON_WIDTH , GUIHelpers.BUTTON_HEIGHT);
        GUIHelpers.setDefaultFont(logout);
        logout.addActionListener(this::logout);

        JButton wipe = new JButton("Limpiar");
        wipe.setBounds( 6* GUIHelpers.UNIT + 20, 8* GUIHelpers.UNIT, GUIHelpers.UNIT + GUIHelpers.BUTTON_WIDTH , GUIHelpers.BUTTON_HEIGHT);
        GUIHelpers.setDefaultFont(wipe);
        wipe.addActionListener(this::wipe);

        if( this.state < 0 ){
            JLabel failed = new JLabel("Failed to make purchase. Please try again later", SwingConstants.CENTER);
            failed.setForeground(Color.RED);
            GUIHelpers.setTextCentered(failed, 8* GUIHelpers.UNIT + (GUIHelpers.UNIT/2) , GUIHelpers.WIDTH);
            panel.add(failed);
        }

        if( this.state > 0 ){
            JLabel success = new JLabel("Purchase successful", SwingConstants.CENTER);
            success.setForeground(Color.GREEN);
            GUIHelpers.setTextCentered(success, 8* GUIHelpers.UNIT + (GUIHelpers.UNIT/2) , GUIHelpers.WIDTH);
            panel.add(success);
        }


        panel.add(checkout);
        panel.add(scrollPane);
        panel.add(balance);
        panel.add(label);
        panel.add(logout);
        panel.add(wipe);

    }

    private int getBalance(){
        String user = this.controller.getSharedState().getUser();
        try {
            return this.auth.getStub("Auth").getBalance(user);
        } catch (RemoteException e) {
            return 0;
        }
    }

    private JPanel createInventory(){
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel,BoxLayout.Y_AXIS));
        try {
            List<Product> products = this.market.getStub("Market").getAllProducts();
            for( Product p : products ){
                final int _id = p.getId();
                JPanel tuple = new JPanel();
                tuple.setSize( 500 , 20 );
                tuple.setLayout(new GridLayout(1,5,10,20));
                tuple.add(new JLabel(p.getName()));
                tuple.add(new JLabel(String.valueOf(p.getQuantity())));
                tuple.add(new JLabel("$" + String.valueOf(p.getValue())));
                final JSpinner amount = new JSpinner(new SpinnerNumberModel(0 ,0 , p.getQuantity(),1));
                tuple.add( amount );
                JButton addToCart = new JButton("Agregar");
                final String name = p.getName();
                final int cost = p.getValue();
                addToCart.addActionListener((arg) -> {
                    int quantity = (Integer)(amount.getValue());
                    int id = _id;
                    this.clicked(id , quantity, name , cost);
                });
                tuple.add(addToCart);
                panel.add(tuple);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
            JPanel p = new JPanel();
            p.setSize( 500 , 20 );
            p.setLayout(new GridLayout(1,3,10,20));
            panel.add(new JLabel("Could not communicate with the server"));
        }
        return panel;
    }

    public void clicked(int id,int amount, String name , int cost){
        if( amount > 0 ){
            System.out.println(id + " Adding to cart: "  + amount + " " + name + ", price " + cost);
            this.controller.getSharedState().addProductToCart(id, amount,name,cost);
        }
    }

    public void purchase(ActionEvent arg){
        HashMap<Integer,Product> cart = this.controller.getSharedState().getCart();
        IProductSystem market = null;
        try {
            if( !this.isBound ){
                server = new RMIServer<>(() -> this);
                server.createAndRebind("AdminStockClient");
                this.isBound = true;
            }
            market = this.market.getStub("Market");
            int tid = market.startProductTransaction(Utils.getLocalAddress(),"AdminStockClient");
            for( Integer key : cart.keySet() ){
                Product p = cart.get(key);
                market.attemptUpdateProductQuantity( tid , p.getId(), p.getQuantity() );
            }
            market.attemptPurchase( tid, this.controller.getSharedState().getUser() );
            this.controller.getSharedState().wipeCart();
        } catch (Exception e) {
            e.printStackTrace();
            this.controller.getSharedState().wipeCart();
            this.state = -1;
            this.frame.getContentPane().removeAll();
            this.render(this.frame);
            this.frame.getContentPane().repaint();
        }
    }

    private void logout(ActionEvent arg){
        this.server.unbindStub("MarketClient");
        this.controller.wipeState();
        this.controller.goToDefault();
    }

    private void wipe(ActionEvent arg){
        this.controller.getSharedState().wipeCart();
    }
}
