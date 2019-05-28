package client;

import shared.functional.NullaryFunction;

import javax.swing.JFrame;
import java.util.HashMap;

public class GUIController {

    private JFrame frame;
    private HashMap<ViewNames, NullaryFunction<View>> views;
    private ViewNames currentView;
    private ViewNames defaultView;

    public GUIController(JFrame frame, ViewNames defaultView) {
        this.frame = frame;
        this.currentView = defaultView;
        this.defaultView = defaultView;
        this.views = new HashMap<>();
    }

    public void bindView( NullaryFunction<View> view , ViewNames name ){
        this.views.put(name,view);
    }

    public void goToDefault(){
        this.changeView(this.defaultView);
    }

    private View getView( ViewNames viewName ){
        View v;
        if( this.views.containsKey(viewName) ){
            v = this.views.get(viewName).apply();
        }else{
            v = this.views.get(this.defaultView).apply();
        }
        v.setController(this);
        return v;
    }

    private void setCurrentView( ViewNames view ){
        if( this.views.containsKey(view) ){
            this.currentView = view;
        }else{
            this.currentView = this.defaultView;
        }
    }

    public void changeView( ViewNames viewName ){
        this.frame.getContentPane().removeAll();
        View v = this.getView(viewName);
        this.setCurrentView(viewName);
        v.render(this.frame);
        this.frame.getContentPane().repaint();
    }

    public void render(){
        this.getView(this.currentView).render(this.frame);
        this.frame.setVisible(true);
    }
}
