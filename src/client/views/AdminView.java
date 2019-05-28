package client.views;

import client.GUIHelpers;
import client.View;
import client.ViewNames;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class AdminView extends View {
    public AdminView() {
    }

    @Override
    public void render(JFrame frame) {
        JButton funds = new JButton("Agregar Fondos");
        JButton stock = new JButton("Agregar Inventario");
        JButton logout = new JButton("Salir");

        int baseline = GUIHelpers.UNIT*3;
        int width = 3 * GUIHelpers.UNIT;
        GUIHelpers.setButtonCentered( funds , baseline , width );
        GUIHelpers.setButtonCentered( stock , baseline + GUIHelpers.UNIT , width);
        GUIHelpers.setButtonCentered( logout , baseline + 2* GUIHelpers.UNIT, width );

        GUIHelpers.setDefaultFont(funds);
        GUIHelpers.setDefaultFont(stock);
        GUIHelpers.setDefaultFont(logout);

        funds.addActionListener(this::funds);
        stock.addActionListener(this::stock);
        logout.addActionListener(this::logout);

        frame.getContentPane().add(funds);
        frame.getContentPane().add(stock);
        frame.getContentPane().add(logout);
    }

    private void funds(ActionEvent arg){
        this.controller.changeView(ViewNames.ADD_FUNDS);
    }
    private void stock(ActionEvent arg){
        this.controller.changeView(ViewNames.ADD_PRODUCTS);
    }
    private void logout(ActionEvent arg){
        this.controller.wipeState();
        this.controller.goToDefault();
    }
}
