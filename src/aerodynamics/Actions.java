package aerodynamics;

import aerodynamics.tools.Tool;
import aerodynamics.tools.Line;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.ArrayList;

public class Actions extends Draw{
    
    public static boolean ePressed = false, qPressed = false;
    
    @Override
    public void keyPressed(KeyEvent e){
        int key = e.getKeyCode();
        switch(key){
            
            case KeyEvent.VK_SPACE:
                sim.running = !sim.running;
                break;
                
                
            case KeyEvent.VK_E:
                ePressed = true;
                break;
                
                
            case KeyEvent.VK_Q:
                qPressed = true;
                break;
                
                
            case KeyEvent.VK_DELETE:
                sim.reset();
                break;
                
                
            case KeyEvent.VK_ESCAPE:
                Line.drawing = false;
                break;
                
            case KeyEvent.VK_R:
                if(e.isControlDown()){
                    Simulation.lines = new ArrayList<Line>();
                    Tool.spawn.reset();
                    updateCursor = true;
                    leftWall = false;
                    topWall = false;
                    rightWall = false;
                    bottomWall = false;
                    sim.reset();
                }else
                    tempCutOff += 100;
                break;
                
                
            case KeyEvent.VK_F:
                if(tempCutOff >= 100)
                    tempCutOff -= 100;
                break;
                
                
            case KeyEvent.VK_Z:
                if(e.isControlDown() ){
                    if(Simulation.lines.get(Simulation.lines.size()-1).isSymmetric)
                        currentTool.cancelLast();
                    currentTool.cancelLast();
                }else{
                    Line.symmetry = !Line.symmetry;
                }
                break;
                
                
            case KeyEvent.VK_S:
                Line.snap = !Line.snap;
                break;
                
                
            case KeyEvent.VK_A:
                if(Line.gridSize > 1)
                    Line.gridSize /= 2;
                break;
                
                
            case KeyEvent.VK_D:
                if(Line.gridSize < 40)
                    Line.gridSize *= 2;
                break;
                
                
            case KeyEvent.VK_I:
                infoOn = !infoOn;
                break;
                
                
            case KeyEvent.VK_1:
                currentTool = Tool.line;
                updateCursor = true;
                break;
                
            
            case KeyEvent.VK_2:
                currentTool = Tool.voidLine;
                updateCursor = true;
                break;
                
                
            case KeyEvent.VK_3:
                Tool.spawn.reset();
                currentTool = Tool.spawn;
                Line.drawing = false;
                updateCursor = true;
                break;
                
                
            case KeyEvent.VK_4:
                if(sim.toSpawn > 20)
                    sim.toSpawn /= 1.5;
                break;
                
                
            case KeyEvent.VK_5:
                sim.toSpawn *= 1.5;
                if(sim.toSpawn > 50000){
                    sim.toSpawn = 50000;
                }
                break;
                
        }
    }//KeyPressed
    
    
    
    @Override
    public void keyReleased(KeyEvent e){
        int key = e.getKeyCode();
        switch(key){
            
            case KeyEvent.VK_E:
                ePressed = false;
                break;
                
                
            case KeyEvent.VK_Q:
                qPressed = false;
                break;
                
        }
    }//keyReleased
    
    
    
    @Override
    public void mousePressed(MouseEvent e){
        currentTool.click(e);
    }//mousePressed
    
    
    
    @Override
    public void mouseWheelMoved(MouseWheelEvent e){
        
        double zoomRate = 1.2;
        
        if(e.getWheelRotation() > 0){
            zoomRate = 1 / zoomRate;
        }
        
        if(zoom * zoomRate > 16 || zoom * zoomRate < 1)
            return;
        
        zoom *= zoomRate;
        
        double z = (zoomRate - 1) / zoom;
        
        wX += getMousePosition().x * z;
        wY += getMousePosition().y * z;
        
        //correct if going over edge
        if(wX < 0)
            wX = 0;
        if(wY < 0)
            wY = 0;
        if(wX / width + 1 / zoom > 1)
            wX = width - width / zoom;
        if(wY / height + 1 / zoom > 1)
            wY = height - height / zoom;
        
    }//mouseWheelMoved
    
    
}//Actions
