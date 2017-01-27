package aerodynamics.tools;

import aerodynamics.Simulation;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.awt.event.MouseEvent;

public class SpawnTool extends Tool{
    
    public boolean active = false;
    private BufferedImage skin1, skin2;
    
    public SpawnTool(){
        
        try{
            skin1 = ImageIO.read(getClass().getResource("/aerodynamics/tools/cursors/Spawn.png"));
            skin2 = ImageIO.read(getClass().getResource("/aerodynamics/tools/cursors/Spawn2.png"));
        }catch(IOException e){
            System.out.println("Spawn.png was not found");
        }
        
        cursor = Toolkit.getDefaultToolkit().createCustomCursor(skin1, new Point(15, 15), "spawn");
        
    }
    
    @Override
    public void click(MouseEvent e){
        if(e.getButton() == 1){
            active = true;
            cursor = Toolkit.getDefaultToolkit().createCustomCursor(skin2, new Point(15, 15), "spawn");
        }else if(e.getButton() == 3){
            active = false;
            cursor =  Toolkit.getDefaultToolkit().createCustomCursor(skin1, new Point(15, 15), "spawn");
        }
        
    }
    
    public void reset(){
        active = false;
        cursor =  Toolkit.getDefaultToolkit().createCustomCursor(skin1, new Point(15, 15), "spawn");
    }
    
    @Override
    public void cancelLast(){
        if(Simulation.lines.size() > 0)
            Simulation.removeLine(Simulation.lines.size()-1);
    }
    
}
