package aerodynamics.tools;

import aerodynamics.Simulation;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.awt.event.MouseEvent;

public class LineTool extends Tool{
    
    
    public LineTool(String s){
        
        BufferedImage skin = null;
        
        try{
            skin = ImageIO.read(getClass().getResource("/aerodynamics/tools/cursors/" + s + ".png"));
        }catch(IOException e){
            System.out.println("Line.png was not found");
        }
        
        cursor = Toolkit.getDefaultToolkit().createCustomCursor(skin, new Point(1, 0), "lineTool");
        
    }
    
    @Override
    public void click(MouseEvent e){
        if(e.getButton() == 1)
            Line.click(e.getX(), e.getY());
        else if(e.getButton() == 3)
            Line.drawing = false;
    }
    
    @Override
    public void cancelLast(){
        if(Simulation.lines.size() > 0)
            Simulation.removeLine(Simulation.lines.size()-1);
    }
    
}
