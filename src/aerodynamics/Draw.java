package aerodynamics;

import aerodynamics.tools.Tool;
import aerodynamics.tools.Line;
import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.Point;
import java.awt.Font;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelListener;
import java.awt.event.MouseWheelEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Draw extends JPanel implements KeyListener, MouseListener, MouseWheelListener{
    
    public static final int width = 1000, height = 600;
    
    long t, lastTime;
    int fpsCount = 0;
    double time;
    Simulation sim = new Simulation();
    public static Tool currentTool = Tool.line;
    public static double wX = 0, wY = 0, zoom = 1;
    BufferedImage toolBar;
    
    
    //variables for key presses
    public boolean updateCursor;
    boolean leftWall, topWall, rightWall, bottomWall, infoOn = true;
    int tempCutOff = 0;
    
    String[] info = {"Instructions:", 
                "Draw obstacles: 1",
                "Draw voids: 2", 
                "Spawn particles: 3",
                "Control spawn rate: 4, 5",
                "Stop using a tool with mouse 2 or esc",
                "Zoom: mouse wheel",
                "Time step control: Q, E", 
                "Snap to grid: S",
                "Toggle symmetry: Z",
                "Grid size: A, D",
                "Delete all particles: Del",
                "Reset: Ctrl + R",
                "Cancel last line: Ctrl + Z",
                "Pause: Spacebar",
                "Hide/unhide slow particles: R, F",
                "Toggle this info: I",
                "Note: round leaking corners"};
    
    
    public Draw(){
        addKeyListener(this);
        addMouseListener(this);
        addMouseWheelListener(this);
        
        
        try{
            toolBar = ImageIO.read(getClass().getResource("/aerodynamics/tools/cursors/ToolBar.png"));
        }catch(IOException e){
            System.out.println("Toolbar was not found");
        }
        
        
        sim.init(this);
        sim.start();
        lastTime = System.currentTimeMillis();
        setCursor(currentTool.cursor);
    }//public Draw
    
    
    public static int xToWindow(double i){
        return (int)((i - wX) * zoom);
    }
    
    public static int yToWindow(double i){
        return (int)((i - wY) * zoom);
    }
    
    public static int xFromWindow(double i){
        return (int)(i / zoom + wX);
    }
    
    public static int yFromWindow(double i){
        return (int)(i / zoom + wY);
    }
    
    
    @Override
    public void paint(Graphics g){
        try{
            
            if(updateCursor)
                setCursor(currentTool.cursor);
            
            //clear BG
            g.setColor(Color.black);
            g.fillRect(0, 0, width, height);
            
            
            if(currentTool != Tool.spawn){
                
                //draw grid
                for(int i = 0; i <= width / Line.gridSize; i++){
                    
                    if(i % 4 == 0)
                        g.setColor(new Color(50, 50, 50));
                    else
                        g.setColor(new Color(25, 25, 25));
                    
                    drawLine(g, i * Line.gridSize, 0, i * Line.gridSize, height);
                }
                for(int i = 0; i <= height / Line.gridSize; i++){
                    
                    if(i % 4 == 0)
                        g.setColor(new Color(50, 50, 50));
                    else
                        g.setColor(new Color(25, 25, 25));
                    
                    drawLine(g, 0, i * Line.gridSize, width, i * Line.gridSize);
                }
                
                if(Line.symmetry){
                    g.setColor(new Color(120, 120, 120));
                    drawLine(g, 0, height/2, width, height/2);
                }
                
            }else{//currentTool == spawnTool
                
                
                //draw particles
                for(int i = 0; i < Simulation.particles.size(); i++){

                    try{
                        AirParticle p = Simulation.particles.get(i);

                        int shade = (int)((p.T - tempCutOff) / 5);
                        if(shade > 255)
                            shade = 255;
                        if(shade > 0){
                            g.setColor(new Color(255, 255 - shade, 255 - shade));
                            drawLine(g, p.x, p.y, p.x, p.y);
                        }

                    }catch(Exception e){}//System.out.println(e + " at draw particle");}
                }
            }
            
            //draw pause
            if(!sim.running){
                g.setColor(new Color(100, 100, 100, 220));
                g.fillRect(width-82, 5, 15, 40);
                g.fillRect(width-57, 5, 15, 40);
            }
            
            
            g.drawImage(toolBar, width - 32, 2, null);
            
            
            //draw lines
            for(Line l: Simulation.lines){
                if(l.isVoid)
                    g.setColor(Color.blue);
                else
                    g.setColor(Color.white);
                    
                drawLine(g, l.x1, l.y1, l.x2, l.y2);
            }
            
            //draw linedebug
            g.setColor(Color.green);
            for(int i = 0; i < Simulation.metaArray.length; i++){
                for(int j = 0; j < Simulation.metaArray[0].length; j++){
                    if(!Simulation.metaArray[i][j].lines.isEmpty())
                        g.fillRect(i * Simulation.metaSize, 
                                j * Simulation.metaSize, 
                                Simulation.metaSize, Simulation.metaSize);
                    
                }
            }
            
            
            //draw currently drawing line
            Point p = getMousePosition();
            if(currentTool == Tool.line)
                g.setColor(Color.white);
            else if(currentTool.equals(Tool.voidLine))
                g.setColor(Color.blue);
            
            if(Line.drawing && p != null){
                drawLine(g, Line.lastx, Line.lasty, p.x / zoom + wX, p.y / zoom + wY);
                if(Line.symmetry)
                    drawLine(g, Line.lastx, getHeight() - Line.lasty, p.x / zoom + wX, getHeight() - p.y / zoom + wY);
            }
            
            
            
            //draw time step indicator
            g.setColor(new Color(10, 175, 10));
            g.drawString("Time step", 5, height - 18);
            g.drawRect(5, height - 13, 55, 5);
            g.fillRect(5, height - 13, (int)(sim.dID * 8), 5);
            
            
            
            g.setFont(new Font("", 0, 12));
            
            
            //technical details
            String s = Simulation.particles.size() + " particles";
            int x = width - 250;
            
            if(currentTool == Tool.spawn){
                s += " (spawn rate: " + (int)(sim.toSpawn / 50) + ")";
                x -= 90;
            }
                
            s += "; draw time: " + (int)time + "ms; " + (int)(1000000/sim.time) + "fps";
            
            g.drawString(s, x, height - 5);
            
            
            
            //draw instructions
            if(infoOn)
                for(int i = 0; i < info.length; i++){
                    g.drawString(info[i], 5, (i + 1) * 13);
                }
            
            
            //info in the bottom
            g.drawString("Average velocity: " + Integer.toString((int)sim.avgV), 280, height - 5);
            
            g.drawString("Hiding particles slower than " + tempCutOff, 400, height - 5);
            
            s = "Snap enabled";
            if(!Line.snap)
                s = "Snap disabled";
            g.drawString(s, 70, height - 5);
            
            
            s = "Symmetry enabled";
            if(!Line.symmetry)
                s = "Symmetry disabled";
            g.drawString(s, 160, height - 5);
            
            try{
                g.drawString(Integer.toString((int)this.getMousePosition().getX()), 900, 20);
                g.drawString(Integer.toString((int)this.getMousePosition().getY()), 930, 20);
            }catch(NullPointerException e){}
            
            //keyIsPressed functions
            if(sim.running){
                if(Actions.ePressed){
                    if(Simulation.dID + 0.1 <= 7)
                        Simulation.dID += 0.1;
                }if(Actions.qPressed){
                    if(Simulation.dID - 0.1 >= 0)
                        Simulation.dID -= 0.1;
                }
            }
            
            
            //calculate how much to wait
            t = System.currentTimeMillis() - lastTime;
            
            if(fpsCount > 25){                                  //if >25 frames passed, reset time
                time = 0;                                       //aka keeps max time up for 25 frames
            }
            if(t > time){                                       //update time to be max time
                time = t;
                fpsCount = 0;
            }else{
                fpsCount++;
            }

            long toWait = 30 - t;
            
            //wait
            if(toWait > 0)
                Thread.sleep(toWait);
            
            //System.out.println(getHeight());
        }catch(Exception e){
            System.out.println(e + " at Draw.paint");
            e.printStackTrace(System.out);
        }
        
        lastTime = System.currentTimeMillis();
        
        repaint();
    }//paint
    
    
    private void drawLine(Graphics g, double x, double y, double x2, double y2){
        
        g.drawLine(xToWindow(x), yToWindow(y), xToWindow(x2), yToWindow(y2));
        
    }
    
    
    @Override
    public void keyPressed(KeyEvent e){}
    @Override
    public void keyReleased(KeyEvent e){}
    @Override
    public void keyTyped(KeyEvent e){}
    @Override
    public void mousePressed(MouseEvent e){}
    @Override
    public void mouseReleased(MouseEvent e){}
    @Override
    public void mouseClicked(MouseEvent e){}
    @Override
    public void mouseEntered(MouseEvent e){
        requestFocus();
    }
    @Override
    public void mouseExited(MouseEvent e){}
    @Override
    public void mouseWheelMoved(MouseWheelEvent e){}
    
}
