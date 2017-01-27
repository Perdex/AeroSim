package aerodynamics;

import java.util.*;
import aerodynamics.tools.Line;

public class PressurePoint {
    
    public static final double R = 8.31451, dimension = 0.001;
    
    ArrayList<Line> lines;
    double x, y, xs, ys, m, T;
    int frame = 0;
    
    public PressurePoint(int mass, int x, int y){
        m = mass;
        this.x = x * Simulation.metaCellSize;
        this.y = y * Simulation.metaCellSize;
        T = 280;
        
        lines = new ArrayList<Line>();
    }
    
    public PressurePoint(){
        m = 0;
        T = 280;
        lines = new ArrayList<Line>();
    }
    
    public static double V(){
        return dimension * dimension;
    }
    
    public double pressure(){
        return m * T * R / V();
    }
    
    public void add(AirParticle particle){
        
        //reset this area
        if(frame != Simulation.frame || m == 0){
            x = particle.x;
            y = particle.y;
            xs = particle.xs;
            ys = particle.ys;
            
            frame = Simulation.frame;
            m = particle.m;
            
            T = particle.T;
            return;
        }
        
        //add particle
        x += (particle.x - x) / m;
        y += (particle.y - y) / m;
        
        xs += (particle.xs - xs) / m;
        ys += (particle.ys - ys) / m;
        
        T += (particle.T - T) / m;
        
        m += particle.m;
        
    }
    
    public void add(Line l){
        lines.add(l);
    }
    public void remove(Line l){
        lines.remove(l);
    }
}
