package aerodynamics.tools;

import aerodynamics.Draw;
import aerodynamics.Simulation;


public class Line {
    static int lineMetaSize = 3;
    public static boolean drawing = false, snap = false, symmetry = true;
    public static double lastx = 0, lasty = 0;
    public static int gridSize = 10;
    public static Line[][] metaLine = new Line[Draw.width / lineMetaSize + 4][Draw.height / lineMetaSize + 4];
    
    public final boolean isVoid, isSymmetric;
    public double x1, y1, x2, y2;
    
    public Line(double x, double y, boolean isVoid){
        x1 = lastx;
        y1 = lasty;
        x2 = x;
        y2 = y;
        this.isVoid = isVoid;
        isSymmetric = false;
    }//line
    
    public Line(double x1, double y1, double x2, double y2, boolean isVoid){
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
        this.isVoid = isVoid;
        isSymmetric = true;
    }//line
    
    public static void click(double x, double y){
        
        x = x / Draw.zoom + Draw.wX;
        y = y / Draw.zoom + Draw.wY;
        
        double xOriginal = x;
        double yOriginal = y;
        
        //if snapping enabled, match to grid
        if(snap){
            x = (int)((x + gridSize / 2) / gridSize) * gridSize;
            y = (int)((y + gridSize / 2) / gridSize) * gridSize;
        }
        
        //if clicked near a line's end, match to it
        for(Line l: Simulation.lines){
            if(Math.sqrt(Math.pow(l.x1 - xOriginal, 2) + Math.pow(l.y1 - yOriginal, 2)) < 10 / Draw.zoom){
                x = l.x1;
                y = l.y1;
            }else if(Math.sqrt(Math.pow(l.x2 - xOriginal, 2) + Math.pow(l.y2 - yOriginal, 2)) < 10 / Draw.zoom){
                x = l.x2;
                y = l.y2;
            }
        }
        
        if(drawing){
            boolean unique = true;
            //check if length is zero
            if(lastx == x && lasty == y){
                unique = false;
            }else{
                for(Line l: Simulation.lines){               //check if matches with another line
                    if((l.x1 == lastx && l.y1 == lasty && l.x2 == x && l.y2 == y) ||
                            (l.x1 == x && l.y1 == y && l.x2 == lastx && l.y2 == lasty)){
                        unique = false;
                        break;
                    }
                }
            }
            //add line
            if(unique){
                boolean line = false;
                if(Draw.currentTool == Tool.voidLine)
                    line = true;
                
                add(new Line(x, y, line));
                
                if(symmetry)
                    add(new Line(x, Draw.height - y, lastx, Draw.height - lasty, line));
            
            }
        }else
            drawing = true;     //start to draw
            
        //match point for next line
        lastx = x;
        lasty = y;
        
    }//click
    
    public double getLength(){
        return Math.hypot(x2 - x1, y2 - y1);
    }
    
    public void remove(){
        //remove from metaArray
    }//remove
    
    public static void add(Line l){
        Simulation.lines.add(l);
        
        double lineLength = l.getLength();
        
         //add to metaArray
        double x = l.x1 / Simulation.metaSize, y =  l.y1 / Simulation.metaSize;
        double dx = (l.x2 - l.x1) / Simulation.metaSize, dy = (l.y2 - l.y1) / Simulation.metaSize;
        
        while(Math.hypot(x, y) < lineLength){
            if((int)x < Simulation.metaArray.length && (int)y < Simulation.metaArray[0].length)
                if((int)x >= 0 && (int)y >= 0)
                    Simulation.metaArray[(int)x][(int)y].add(l);
            x += dx;
            y += dy;
        }
        
    }//add
}
