package client;

import client.views.InventoryView;
import client.views.LoginView;

import javax.swing.*;

public class GUIRunner {
    public static void main(String[] args) {
        JFrame frame = new JFrame();
        frame.setResizable(false);
        frame.setBounds(0, 0, Data.WIDTH, Data.HEIGHT);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(null);
        GUIController controller = new GUIController(frame, ViewNames.LOGIN);
        setViews(controller);
        controller.render();
    }

    private static void setViews(GUIController controller){
        controller.bindView(() -> new LoginView("localhost") , ViewNames.LOGIN );
        controller.bindView(() -> new InventoryView(), ViewNames.INVENTORY);
    }
}
