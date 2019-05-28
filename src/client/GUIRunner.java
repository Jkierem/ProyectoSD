package client;

import client.views.*;
import shared.utils.OptionalList;

import javax.swing.*;
import java.awt.*;
import java.util.List;


public class GUIRunner {
    public static void main(String[] args) {
        OptionalList<String> argv = new OptionalList<>(List.of(args));
        String auth = argv.findNextOrElse("--auth", "localhost");
        String market = argv.findNextOrElse("--market","localhost");

        System.out.println("Auth host: " + auth);
        System.out.println("Market host: " + market);

        JFrame frame = new JFrame();
        frame.setResizable(false);
        frame.setBounds(0, 0, GUIHelpers.WIDTH, GUIHelpers.HEIGHT);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(new BorderLayout());
        GUIController controller = new GUIController(frame, ViewNames.LOGIN);
        setViews(controller,auth,market);
        controller.render();
    }

    private static void setViews(GUIController controller, String auth, String market){
        controller.bindView(() -> new LoginView(auth) , ViewNames.LOGIN );
        controller.bindView(() -> new InventoryView(auth,market), ViewNames.INVENTORY);

        controller.bindView(() -> new AdminView(), ViewNames.ADMIN_MENU);
        controller.bindView(() -> new AdminFundsView(auth), ViewNames.ADD_FUNDS);
        controller.bindView(() -> new AdminProductsView(market), ViewNames.ADD_PRODUCTS);
    }
}
