/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chessproject;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Le controlleur
 * @author victo
 */
public class BoardPressController implements ActionListener{

    private ChessBoard model;
    
    public BoardPressController(ChessBoard m){
        model=m;
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        int abs = Integer.parseInt(e.getActionCommand().substring(0, 1));
        int ord = Integer.parseInt(e.getActionCommand().substring(2, 3));
        model.doWhenPress(abs,ord);
        //System.out.println("You pressed "+abs+ord);
    }
    
}
