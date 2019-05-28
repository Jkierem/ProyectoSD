package client;

import client.views.*;

import javax.swing.*;
import java.awt.*;

public class GUIRunner {
    public static void main(String[] args) {
        JFrame frame = new JFrame();
        frame.setResizable(false);
        frame.setBounds(0, 0, GUIHelpers.WIDTH, GUIHelpers.HEIGHT);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(new BorderLayout());
        GUIController controller = new GUIController(frame, ViewNames.LOGIN);
        setViews(controller);
        controller.render();
    }

    private static void setViews(GUIController controller){
        controller.bindView(() -> new LoginView("localhost") , ViewNames.LOGIN );
        controller.bindView(() -> new InventoryView("localhost","localhost"), ViewNames.INVENTORY);

        controller.bindView(() -> new AdminView(), ViewNames.ADMIN_MENU);
        controller.bindView(() -> new AdminFundsView("localhost"), ViewNames.ADD_FUNDS);
        controller.bindView(() -> new AdminProductsView("localhost"), ViewNames.ADD_PRODUCTS);
    }
}
