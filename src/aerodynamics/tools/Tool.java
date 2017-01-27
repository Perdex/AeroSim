package aerodynamics.tools;

import java.awt.Cursor;
import java.awt.event.MouseEvent;

public class Tool {
    
    public static final LineTool line = new LineTool("Line");
    public static final LineTool voidLine = new LineTool("VoidLine");
    public static final SpawnTool spawn = new SpawnTool();
    
    
    public Cursor cursor;
    
    public void click(MouseEvent e){}
    
    public void cancelLast(){}
}
