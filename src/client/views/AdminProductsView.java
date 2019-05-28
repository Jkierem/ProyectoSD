package client.views;

import client.GUIHelpers;
import client.View;
import client.ViewNames;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AdminProductsView extends View implements IClient {

    private RMIClient<IProductSystem> market;
    private RMIServer<AdminProductsView,IClient> server;
    private int state;
    private boolean bound;
    private HashMap<String,Integer> lookup;

    private JFrame frame;
    private JComboBox<String> products;
    private JTextField amount;
    private String selectedProduct;

    public AdminProductsView(String market) {
        this.market = new RMIClient<>(market);
        this.lookup = new HashMap<>();
        this.selectedProduct = "";
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

        List<String> productList = this.getProducts();
        this.selectedProduct = !productList.isEmpty() ? productList.get(0): "";
        products = new JComboBox<>(productList.toArray(new String[productList.size()]));
        GUIHelpers.setComboCentered(products, GUIHelpers.UNIT*3);
        GUIHelpers.setDefaultFont(products);

        amount = new JTextField("1");
        amount.setBounds( products.getX() , products.getY() + GUIHelpers.UNIT , products.getWidth() , products.getHeight());
        GUIHelpers.setDefaultFont(amount);

        JButton restock = new JButton("Depositar");
        restock.setBounds( products.getX() , products.getY() + 2* GUIHelpers.UNIT , products.getWidth() , products.getHeight());
        GUIHelpers.setDefaultFont(restock);

        JButton back = new JButton("Atras");
        back.setBounds( products.getX() , products.getY() + 3* GUIHelpers.UNIT , products.getWidth() , products.getHeight());
        GUIHelpers.setDefaultFont(back);

        products.addActionListener(this::onSelect);
        restock.addActionListener(this::restock);
        back.addActionListener(this::back);

        frame.add(products);
        frame.add(amount);
        frame.add(restock);
        frame.add(back);

        if( this.state < 0 ){
            JLabel failed = new JLabel("Failed to make Restock. Please try again later", SwingConstants.CENTER);
            failed.setForeground(Color.RED);
            GUIHelpers.setTextCentered(failed, products.getY() + 4* GUIHelpers.UNIT , GUIHelpers.WIDTH);
            frame.add(failed);
        }

        if( this.state > 0 ){
            JLabel success = new JLabel("Restock successful", SwingConstants.CENTER);
            success.setForeground(Color.GREEN);
            GUIHelpers.setTextCentered(success, products.getY() + 4* GUIHelpers.UNIT , GUIHelpers.WIDTH);
            frame.add(success);
        }
    }

    private List<String> getProducts(){
        try {
            List<Product> ps = this.market.getStub("Market").getAllProducts();
            List<String>  names = new ArrayList<>();
            for ( Product p : ps ){
                names.add(p.getName());
                this.lookup.put( p.getName() , p.getId() );
            }
            return names;
        } catch (RemoteException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    private void onSelect(ActionEvent arg){
        this.selectedProduct = (String)this.products.getSelectedItem();
    }

    private void back(ActionEvent arg){
        this.server.unbindStub("AdminStockClient");
        this.controller.changeView(ViewNames.ADMIN_MENU);
    }

    private void restock(ActionEvent arg){
        int rid = this.lookup.get(this.selectedProduct);
        try {
            int amount = Integer.parseInt(this.amount.getText());
            String host = Utils.getLocalAddress();
            String binding = "AdminStockClient";
            if( !this.bound ){
                this.server = new RMIServer<>(() -> this);
                this.server.createAndRebind(binding);
                this.bound = true;
            }
            IProductSystem market = this.market.getStub("Market");
            int tid = market.startProductTransaction(host,binding);
            market.attemptUpdateProductQuantity(tid,rid,-1 * amount);
            market.attemptAdminRestock(tid,this.controller.getSharedState().getToken());
        } catch (Exception e) {
            e.printStackTrace();
            this.state = -1;
            this.repaint();
        }
    }

    private void repaint(){
        this.frame.getContentPane().removeAll();
        this.render(this.frame);
        this.frame.getContentPane().repaint();
    }
}
