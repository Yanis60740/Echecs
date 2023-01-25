/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chessproject;

import java.util.concurrent.CountDownLatch;

/**
 *
 * @author victo
 */
public class PromotionModel implements AutreEventListener {
    private PromotionController ctrl;
    private PromotionWindow view;
    private CountDownLatch cdl;
    private PromotionTransferrer pieceType;
    
    public PromotionModel(CountDownLatch cdl, PromotionTransferrer pt){
        ctrl = new PromotionController(this);
        view = new PromotionWindow(this,ctrl);
        this.cdl=cdl;
        pieceType = pt;
    }

    @Override
    public void actionADeclancher(AutreEvent evt) { //Bring back player's choice
        pieceType.pt = ((PieceType)(evt.getDonnee()));
        AutreEventNotifieur aen = new AutreEventNotifieur();
        aen.addAutreEventListener(view);
        aen.diffuserAutreEvent(new AutreEvent(this,"DIE"));
        cdl.countDown();
    }
}
