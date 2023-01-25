/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chessproject;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.concurrent.CountDownLatch;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;

/**
 * Fenêtre de promotion (BUGGED)
 * @author victo
 */
public class PromotionWindow extends JFrame implements AutreEventListener{
    private PromotionController ctrl;
    private PromotionModel model;
    
    public PromotionWindow(PromotionModel model,PromotionController ctrl){
        this.model=model;
        this.ctrl=ctrl;
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.addWindowListener(ctrl);
        setLayout(new GridLayout(2,4));
        PieceType[] pieces = new PieceType[]{PieceType.QUEEN,PieceType.ROOK,PieceType.BISHOP,PieceType.KNIGHT};
        for(PieceType p : pieces){
            JButton b = new JButton();
            String a;
            switch(p){
                case QUEEN -> a="q";
                case ROOK -> a="r";
                case BISHOP -> a="b";
                case KNIGHT -> a="n";
                default -> a="q"; //Just in case
            }
            Icon i = new ImageIcon("src/images/Chess_"+a+"lt60.png");
            b.setIcon(i);
            b.setActionCommand(p.name());
            b.addActionListener(ctrl);
            add(b);
        }
        JLabel flavorText = new JLabel("Promotion ! Choisissez de quoi remplacer votre pion.");
        add(flavorText);
        this.pack();
        this.setVisible(true);    
    }

    @Override
    public void actionADeclancher(AutreEvent evt) {
        this.dispose();
    }
}
