package aerodynamics;


public class Move extends Thread{
    
    Simulation sim;
    
    public Move(Simulation s){
        sim = s;
    }
    
    @Override
    public void run(){
        while(true){
            while(sim.running){
                for(AirParticle p: Simulation.particles){
                    p.move();
                }

                try{
                    Thread.sleep(MIN_PRIORITY);
                }catch(InterruptedException e){System.out.println(e + " at move");}
            }

            try{
                Thread.sleep(100);
            }catch(Exception e){}
            
        }//outer while loop
    }
    
}
