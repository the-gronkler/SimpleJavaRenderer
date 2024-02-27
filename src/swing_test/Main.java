package swing_test;

import javax.swing.*;
import java.awt.*;

public class Main extends JFrame{
    public static void main(String[] args){
        SwingUtilities.invokeLater(Main::new);
    }
    public Main(){
        Container pane = this.getContentPane();
        pane.add(new RenderPanel());

        setSize(1200, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

}
