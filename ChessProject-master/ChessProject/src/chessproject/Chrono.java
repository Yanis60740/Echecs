/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chessproject;

import java.io.Serializable;

/**
 * Modèle de chronomètre
 * @author victo
 */
public class Chrono implements Serializable{
    private int timeLeft;
    private boolean yourTurn;
    private transient Thread t;
    private transient AutreEventNotifieur Vaen;
    private transient AutreEventNotifieur Maen;
    private char tag;
    
    public Chrono(char tag){
        timeLeft=3*60;
        this.tag=tag;
    }
    
    public void setAEN(AutreEventNotifieur aen, ChessBoard cb){
        this.Vaen=aen;
        Maen = new AutreEventNotifieur();
        Maen.addAutreEventListener(cb);
    }

    public void startTimer(){
        yourTurn=true;
        Runnable afficheMessage = new Runnable() {
            public void run() {
                try {
                    for (; yourTurn && (timeLeft > 0); timeLeft--) {
                        Vaen.diffuserAutreEvent(new AutreEvent(this,timeLeft+":"+tag));
                        Thread.sleep(1000);
                    }
                } catch (InterruptedException ie) {
                }
                if (yourTurn) {
                    Vaen.diffuserAutreEvent(new AutreEvent(this,0+":"+tag));
                    Maen.diffuserAutreEvent(new AutreEvent(this,tag)); //Envoyer au modèle comme quoi la partie est terminée.
                }
            }
        };
        t = new Thread(afficheMessage);
        t.start();
    }
    
    public void turnFinished(){
        t.interrupt();
        yourTurn=false;     
    }
}
