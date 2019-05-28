package client.views;

import client.Data;
import client.View;
import client.ViewNames;

import javax.swing.*;
import java.awt.*;

public class InventoryView extends View {

    private JPanel panel;

    public InventoryView() { }

    @Override
    public void render(JFrame frame) {
        this.panel = new JPanel();
        panel.setBounds(0,0, Data.WIDTH,Data.HEIGHT);
        panel.setLayout(null);
        panel.setBackground(Color.GRAY);
        frame.getContentPane().add(panel);

        JButton button = new JButton("Back");
        Data.setButtonCentered(button,5*Data.UNIT);
        Data.setDefaultFont(button);
        panel.add(button);
        button.addActionListener((arg) -> {
            this.controller.goToDefault();
        });
    }
}
