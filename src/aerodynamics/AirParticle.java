package aerodynamics;

import aerodynamics.tools.Line;

public class AirParticle {
    
    public double x, y, xs, ys, T, m;
    
    public AirParticle(double x, double y, double xs, double ys){
        this.x = x;
        this.y = y;
        
        this.xs = xs;
        this.ys = ys;
        
        T = 2000;
        m = 0.000001;
    }
    
    public double v(){
        return Math.sqrt(xs * xs + ys * ys);
    }
    
    
    public boolean move(){
        //boolean answers "should this be removed?"
        
        double t = Simulation.deltaTime();
        
        x += xs * t;
        y += ys * t;
        
        if(x < 0 || x >= Draw.width || y < 0 || y >= Draw.height)
            return true;
        
        try{
            Simulation.metaArray[(int)(x/Simulation.metaSize) + 2][(int)(y/Simulation.metaSize) + 2].add(this);
        }catch(ArrayIndexOutOfBoundsException e){
            System.out.println("AirParticle.move: out of array");
        }
        
        return false;
    }
    
    
    public boolean interact(PressurePoint p, double time, boolean containsThis){
        
        if(p.m <= 0)
            return false;
        
        double dx = p.x - this.x;
        double dy = p.y - this.y;
        double pressure = p.m;
        
        
        if(containsThis){
            if(p.m == 1)        //if this is only particle in its p.p
                return false;
            dx = (p.m * this.x - p.x)/(p.m - 1) - this.x;   //correct position to not account for this particle
            dy = (p.m * this.y - p.y)/(p.m - 1) - this.y;
            pressure--;
        }
        
        //distance
        double vLength = Math.sqrt(dx * dx + dy * dy);
        
        if(vLength == 0)
            return false;
        
        //if(Math.sqrt(vLengthSq) > )
        //double relativeSpeed = Math.sqrt(square(this.xs - p.xs) + square(this.ys - p.ys));
        
        
        dx /= vLength;// * (vLength + 0.25) * (vLength + 0.25);
        dy /= vLength;// * (vLength + 0.25) * (vLength + 0.25);
        
        
        this.xs -= dx * time * 200 * pressure;
        this.ys -= dy * time * 200 * pressure;
        
        
//        this.t -= Math.sqrt(square(dx * time * p.t) + square(dy * time * p.t));
//        this.t = (this.t + p.t) / 2;
//        p.t = this.t;
        //interact with lines
        //again to reduce leaking
        for(Line l: p.lines){
            if(intersects(l, Simulation.deltaTime()))
                if(l.isVoid)
                    return true;//remove
                else
                    collideWithLine(l.x2 - l.x1, l.y2 - l.y1);
        }//for lines
        
        return false;
    }//interact
    
//    private double square(double x){
//        return x * x;
//    }//square
    
    
    
    public void collideWithLine(double lineX, double lineY){
        double lLength = Math.hypot(lineX, lineY);
        
        lineX /= lLength;
        lineY /= lLength;
        
        double temp = lineX;
        lineX = -lineY;
        lineY = temp;
        temp = -2 * (lineX * this.xs + lineY * this.ys);
        
        this.xs += lineX * temp;
        this.ys += lineY * temp;
        
    }//collideWall
    
    
    
    
    public boolean intersects(Line l, double time){
        double lx = l.x2 - l.x1;                    //line's vector's x
        double ly = l.y2 - l.y1;                    //line's vector's y
        double px = xs * time;          //particle's velocity vector's x
        double py = ys * time;          //particle's velocity vector's y
        
        double pXl = px * ly - py * lx;             //(particle velocity) X line
        
        
        //check if collides
        if(pXl != 0){
            
            double plx = l.x1 - x;                //particle -> line = (plx, ply)
            double ply = l.y1 - y;
            double plXpv = plx * py - ply * px;     //(particle -> line) X (particle velocity)
            
            double n = plXpv / pXl;
            
            if(n >= 0 && n <= 1){
                
                double plXl = plx * ly - ply * lx;  //(particle -> line) X line
                
                double m = plXl / pXl;
                
                if(m >= 0 && m <= 1){
                    return true;
                }
            }
        }
        return false;
    }//intersects
    
}
