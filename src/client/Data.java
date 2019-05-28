package client;

import javax.swing.*;
import java.awt.*;

public class Data {
    public static int PADDING=20;

    public static int UNIT=50;

    public static int WIDTH=10*UNIT;
    public static int HEIGHT=10*UNIT;

    public static int BUTTON_HEIGHT=30;
    public static int BUTTON_WIDTH=80;

    public static int TEXT_HEIGHT=35;
    public static int TEXT_WIDTH=(WIDTH/3);

    public static int getXCenter( int width ){
        return (WIDTH/2) - (width/2);
    }

    public static void setTextCentered(Component field, int height ){
        field.setBounds( getXCenter(TEXT_WIDTH), height , TEXT_WIDTH, TEXT_HEIGHT );
    }

    public static void setTextCentered(Component field, int height , int width){
        field.setBounds( getXCenter(width), height , width, TEXT_HEIGHT );
    }

    public static void setButtonCentered( JButton button , int height ){
        button.setBounds( getXCenter(BUTTON_WIDTH), height , BUTTON_WIDTH, BUTTON_HEIGHT );
    }

    public static void setDefaultFont(Component c ){
        c.setFont(new Font("Tahoma", Font.BOLD, 11));
    }

    public static void setDefaultFont(Component c , int fontSize){
        c.setFont(new Font("Tahoma", Font.BOLD, fontSize));
    }

    public static void setDefaultFont(Component c , int fontSize, int style){
        c.setFont(new Font("Tahoma", style, fontSize));
    }

    public static int getYCenter( int height ){
        return (HEIGHT/2) - (height/2);
    }
}
