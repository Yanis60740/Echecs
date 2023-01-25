/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chessproject;

import java.io.Serializable;

/**
 * Classe pièce
 * @author victo
 */
public class Piece implements Serializable {
    private boolean isWhite;
    private PieceType type;

    public boolean isWhite() {
        return isWhite;
    }

    public PieceType getType() {
        return type;
    }
    
    public Piece(boolean isWhite, PieceType type){
        this.isWhite=isWhite;
        this.type = type;
    }
    
    public void promotion(PieceType newType){
        if(type!=PieceType.PAWN || newType==PieceType.PAWN || newType==PieceType.KING) throw new IllegalArgumentException(); //Just in case, var checker
        type=newType; 
    }
}
