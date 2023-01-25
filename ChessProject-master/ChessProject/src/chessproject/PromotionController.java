/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chessproject;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 *
 * @author victo
 */
public class PromotionController extends WindowAdapter implements ActionListener {
    private PromotionModel m;
    AutreEventNotifieur aen;

    public PromotionController(PromotionModel m){
        this.m=m;
        aen = new AutreEventNotifieur();
        aen.addAutreEventListener(this.m);
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        PieceType pt=PieceType.valueOf(e.getActionCommand());
        aen.diffuserAutreEvent(new AutreEvent(this,pt));
    }
    
    @Override
    public void windowClosing(WindowEvent e) {
        aen.diffuserAutreEvent(new AutreEvent(this,PieceType.QUEEN));
    }
    
}
