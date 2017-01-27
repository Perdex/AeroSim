package aerodynamics;

import aerodynamics.tools.Line;
import aerodynamics.tools.Tool;
import java.awt.Point;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
//import aerodynamics.sensors.*;

public class Simulation extends Thread{
    
    public static final int metaSize = 3;
    public static ArrayList<Line> lines = new ArrayList<Line>();
    public static ArrayList<AirParticle> particles; 
    public static PressurePoint[][] metaArray = 
            new PressurePoint[Draw.width / metaSize + 4][Draw.height / metaSize + 4];
//    public static ArrayList<ActiveSensor> activeSensors = new ArrayList<ActiveSensor>();
//    public static ArrayList<PassiveSensor> passiveSensors = new ArrayList<PassiveSensor>();
    public static int frame = 0;
    public static final boolean timeDebug = false;
    
    int toSpawn = 500;
    public boolean running = true;
    private int fpsCount = 0;       //calc/s
    private long t, lastTime, time3;
    static double dID = 3.5, time, avgV;     //dID is delta time exponent
    Draw draw;
    ArrayList<Integer> toRemove;
    
    
    public static double deltaTime(){
        return 0.001 * Math.pow(2, dID);
    }//getDeltaTime
    
    @Override
    public void run(){
        
        for(int i = 0; i < Draw.height / metaSize + 4; i++){        //add external pressure
            metaArray[0][i] = new PressurePoint(2, 0, i);
            metaArray[1][i] = new PressurePoint(2, 1, i);
            metaArray[Draw.width / metaSize + 2][i] = new PressurePoint(2, Draw.width / metaSize + 2, i);
            metaArray[Draw.width / metaSize + 3][i] = new PressurePoint(2, Draw.width / metaSize + 3, i);
        }
        for(int i = 0; i < Draw.width / metaSize + 4; i++){
            metaArray[i][0] = new PressurePoint(2, i, 0);
            metaArray[i][1] = new PressurePoint(2, i, 1);
            metaArray[i][Draw.height / metaSize + 2] = new PressurePoint(2, i, Draw.height / metaSize + 2);
            metaArray[i][Draw.height / metaSize + 3] = new PressurePoint(2, i, Draw.height / metaSize + 3);
        }
        
        for(int i = 2; i < Draw.width / metaSize + 3; i++){
            for(int j = 2; j < Draw.height / metaSize + 3; j++){
                metaArray[i][j] = new PressurePoint();
            }
        }
        
        
        while(draw.isEnabled()){
            while(running){
                
                time3 = System.nanoTime();
                
                
                frame++;
                
                toRemove = new ArrayList<Integer>();
                
                if(timeDebug){
                    System.out.print("1: " + (int)(System.nanoTime() - time3)/100000);
                    time3 = System.nanoTime();
                }
                
                //run particles
                try{
                    avgV = 0;
                    //move all particles at once
                    for(int i = 0; i < particles.size(); i++){
                        AirParticle p = particles.get(i);
                        
                        avgV += p.v();
                        
                        p.move();
                        
                    }
                    
                    if(timeDebug){
                        System.out.print(", 2: " + (int)(System.nanoTime() - time3)/100000);
                        time3 = System.nanoTime();
                    }

                    avgV /= particles.size();
                    
                    //remove particles outside view
                    int reducer = 0;
                    for(int i: toRemove){
                        particles.remove(i - reducer);
                        reducer++;
                    }
                    
                    toRemove = new ArrayList<Integer>();
                    
                    if(timeDebug){
                        System.out.print(", 3: " + (int)(System.nanoTime() - time3)/100000);
                        time3 = System.nanoTime();
                    }
                    
                    //run other stuff after checking if particle should be removed
                    particles.stream().forEach((p) -> {
                        try{
                            runParticle(p);//run particle
                        }catch(Exception e){
                            //System.out.println(e + " at Simulation.runParticle");
                        }
                    });
                    
                    if(timeDebug){
                        System.out.print(", 4: " + (int)(System.nanoTime() - time3)/100000);
                        time3 = System.nanoTime();
                    }
                    
                    //remove particles that hit voidlines
                    reducer = 0;
                    for(int i: toRemove){
                        particles.remove(i - reducer);
                        reducer++;
                    }
                    
                    if(timeDebug){
                        System.out.println(", 5: " + (int)(System.nanoTime() - time3)/100000);
                        time3 = System.nanoTime();
                    }
                    
                }catch(ConcurrentModificationException e){
                    System.out.println(e + " at Simulation.run -> run particles");
                }
                
//                if(toRemove.size() > 0)
//                    System.out.println(toRemove.size() + " " + particles.size());
                
                
                //spawn new particles
                if(Draw.currentTool == Tool.spawn && Tool.spawn.active)
                    spawnParticles((int)(deltaTime() * toSpawn));
                
                if(timeDebug){
                    System.out.println("6: " + (int)(System.nanoTime() - time3)/100000);
                    time3 = System.nanoTime();
                }
                
                
                //calculate how long to wait for
                t = System.nanoTime() - lastTime;
                
                double toWait = 20000 - (t / 1000);
                
                if(fpsCount > 1000000 / time){          //if a second is passed, reset time
                    time = 0;                           //aka keeps max time up for 25 frames
                }
                if(t / 1000 > time){                    //update time to be max time
                    time = t / 1000;
                    if(toWait > 0)
                        time += toWait;
                    fpsCount = 0;
                }else{
                    fpsCount++;
                }
                
                //wait
                if(toWait >= 1)
                    try{
                        Thread.sleep((int)(toWait / 1000));
                    }catch(Exception e){}
                
                lastTime = System.nanoTime();
                
                
            }//main while loop
            
            try{
                Thread.sleep(100);
            }catch(Exception e){}
            lastTime = System.nanoTime();
            
            
        }//outer while loop
    }//run
    
    
    
    private void spawnParticles(int amount){
        
        try{
            Point p = draw.getMousePosition();
            
            if(p == null)
                return;
            
            int x = Draw.xFromWindow(p.getX());
            int y = Draw.yFromWindow(p.getY());
                for(int i = 0; i < amount; i++){
                    Simulation.particles.add(new AirParticle(
                            x + Math.random() * 10 - 5, 
                            y + Math.random() * 10 - 5, 
                            Math.random() * 100 - 50,
                            Math.random() * 100 - 50));
                }
        }catch(Exception e){
            System.out.println(e + ": Probably for mouse being out of window");
        }
    }
    
    
    
    private boolean runParticle(AirParticle p){
        //boolean means will be removed
        
        
        //interact with other particles
        for(int i = -2; i <= 2; i++){
            for(int j = -2; j <= 2; j++){
                
                //don't check corners
                if(i == -2 && j == -2){
                    j++;
                }else if(i == -2 && j == 2){
                    i = -1;
                    j = -2;
                }else if(i == 2 && j == -2){
                    j++;
                }else if(i == 2 && j == 2){
                    break;
                }
                
                boolean zeroes = false;
                if(i == 0 && j == 0)
                    zeroes = true;
                
                int x = (int)(p.x / metaSize) + i;
                int y = (int)(p.y / metaSize) + j;
                if(x >= 0 && x < Draw.width/metaSize && y >= 0 && y < Draw.height/metaSize)
                    if(p.interact(metaArray[x + 2][y + 2], deltaTime(), zeroes))
                        return true;
                
        }}
        
        
        
        return false;
        
    }//runParticle
    
    
    
    public void init(Draw draw){
        reset();
        this.draw = draw;
        
        //init lines
        lines = new ArrayList<Line>();
        Line.click(400, 300);
        Line.click(400, 290);
        Line.click(420, 270);
        Line.click(440, 270);
        Line.click(460, 290);
        
        lastTime = System.nanoTime();
    }//init
    
    public static void removeLine(int i){
        lines.get(i).remove();
        lines.remove(i);
    }//removeLine
    
    public void reset(){
        //init particles
        particles = new ArrayList<AirParticle>();
//        for(int i = 0; i < 25; i++){
//            for(int j = 0; j < 25; j++){
//                particles.add(new AirParticle((int)(Draw.width * i / 50), (int)(Draw.height * j / 50),
//                        Math.random() * 50 - 25, Math.random() * 50 - 25));
//            }
//        }
    }//reset
}
