package client;

import javax.swing.*;

public abstract class View {
    protected GUIController controller;
    public abstract void render( JFrame frame );
    public void setController(GUIController controller){
        this.controller = controller;
    }
}
