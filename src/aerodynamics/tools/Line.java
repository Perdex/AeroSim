package aerodynamics.tools;

import aerodynamics.Draw;
import aerodynamics.Simulation;
import java.util.ArrayList;


public class Line {
    public static boolean drawing = false, snap = false, symmetry = true;
    public static double lastx = 0, lasty = 0;
    public static int gridSize = 10;
    
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
                boolean isVoid = false;
                if(Draw.currentTool == Tool.voidLine)
                    isVoid = true;
                
                add(new Line(x, y, isVoid));
                
                if(symmetry)
                    add(new Line(x, Draw.height - y, lastx, Draw.height - lasty, isVoid));
            
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
        for(point p: getPoints(this))
            Simulation.getMetaInt(p.x, p.y).remove(this);
    }//remove
    
    public static void add(Line l){
        Simulation.lines.add(l);
        
        for(point p: getPoints(l))
            Simulation.getMetaInt(p.x, p.y).add(l);
        
    }//add
    
    //returns the meta-array indeces that are close to this line
    private static ArrayList<point> getPoints(Line l){
        double lineLength = l.getLength();
        
        //x, y will increment from 0 to dx, dy in steps of metasize
        double x = 0, y = 0;
        double dx = l.x2 - l.x1, dy = l.y2 - l.y1;
        dx /= lineLength;
        dy /= lineLength;
        
        int x0 = (int)Math.round(l.x1/Simulation.metaCellSize);
        int y0 = (int)Math.round(l.y1/Simulation.metaCellSize);
        
        ArrayList<point> points = new ArrayList<>();
        
        while(sq(x) + sq(y) < sq(lineLength / Simulation.metaCellSize)){
            
            
            for(int i = -1; i < 2; i++){
                for(int j = -1; j < 2; j++){

                    //make the indices
                    int xi = (int)Math.round(x) + x0 + i;
                    int yi = (int)Math.round(y) + y0 + j;

                    if(xi < 0 || xi >= Simulation.metaWidth() ||
                            yi < 0 || yi >= Simulation.metaHeight())
                        continue;

                    point p = new point(xi, yi);
                    if(!points.contains(p))
                        points.add(p);
                    
                }
            }
            
            x += dx;
            y += dy;
        }
        return points;
    }
    
    private static class point{
        final int x, y;
        public point(int x, int y){
            this.x = x;
            this.y = y;
        }
        
        @Override
        public boolean equals(Object other){
            if(!(other instanceof point))
                return false;
            point o = (point)other;
            return x == o.x && y == o.y;
        }

        @Override
        public int hashCode(){
            int hash = 7;
            hash = 67 * hash + this.x;
            hash = 67 * hash + this.y;
            return hash;
        }
        
    }
    
    private static double sq(double d){
        return d * d;
    }
}

