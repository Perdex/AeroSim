package aerodynamics;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class Aerodynamics{
    
    
    static JFrame fr = new JFrame("Fluid flow sim");
    static JPanel panel = new Actions();
    public static void main(String[] args){
        fr.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        fr.setSize(Draw.width + 6, Draw.height + 28);
        fr.add(panel);
        fr.setResizable(false);
        fr.setLocation(150, 120);
        fr.setVisible(true);
        
        panel.setFocusable(true);
    }
}
