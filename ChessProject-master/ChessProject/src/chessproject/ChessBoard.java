/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chessproject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import javax.swing.SwingUtilities;

/**
 * Le modèle
 * @author victo
 */
public class ChessBoard implements Serializable, AutreEventListener {
    private Piece[][] board;
    private transient Piece selectedPiece; private transient int[] piecePosition;
    private boolean isTurnForWhite;
    private transient AutreEventNotifieur aen;
    private transient ArrayList<int[]> possibleMoves;
    private transient boolean partieFinie;
    private Chrono whiteTimer;
    private Chrono blackTimer;
    private transient CountDownLatch cdl;
    private transient boolean promotionTime;
    
    public ChessBoard(){ 
        board = new Piece[7][8];
        isTurnForWhite=true;
        whiteTimer = new Chrono('W');
        blackTimer = new Chrono('K');
    }
    
    public void setTransientVars(){
        aen = new AutreEventNotifieur();
        whiteTimer.setAEN(aen,this);
        blackTimer.setAEN(aen,this);
        partieFinie=false;
        promotionTime=false;
    }
    
    public void initialiserPlateau(boolean isNewGame){
        if(isNewGame){
            for(int i=0;i<8;i++){ //Add Pawns
                Piece whitePawn = new Piece(true,PieceType.PAWN);
                Piece blackPawn = new Piece(false,PieceType.PAWN);
                placePiece(whitePawn,new int[] {1,i});
                placePiece(blackPawn,new int[] {5,i});
            }
            for(int i : new int[] {0,7}){
                Piece whiteRook = new Piece(true,PieceType.ROOK);
                Piece blackRook = new Piece(false,PieceType.ROOK);
                placePiece(whiteRook, new int[] {0,i});
                placePiece(blackRook, new int[] {6,i});
            }
            for(int i : new int[] {1,6}){
                Piece whiteKnight = new Piece(true,PieceType.KNIGHT);
                Piece blackKnight = new Piece(false,PieceType.KNIGHT);
                placePiece(whiteKnight,new int[] {0,i});
                placePiece(blackKnight,new int[] {6,i});
            }
            for(int i : new int[] {2,5}){
                Piece whiteBishop = new Piece(true,PieceType.BISHOP);
                Piece blackBishop = new Piece(false,PieceType.BISHOP);
                placePiece(whiteBishop,new int[] {0,i});
                placePiece(blackBishop,new int[] {6,i});
            }
            Piece whiteQueen = new Piece(true,PieceType.QUEEN);
            Piece blackQueen = new Piece(false,PieceType.QUEEN);
            placePiece(whiteQueen,new int[] {0,3});
            placePiece(blackQueen,new int[] {6,3});
            Piece whiteKing = new Piece(true,PieceType.KING);
            Piece blackKing = new Piece(false,PieceType.KING);
            placePiece(whiteKing,new int[] {0,4});
            placePiece(blackKing,new int[] {6,4});
        }
        else{
            for(int i=0;i<7;i++){
                for(int j=0;j<8;j++){
                    if(board[i][j]!=null) placePiece(board[i][j],new int[] {i,j});
                }
            }
        }
        if(isTurnForWhite) whiteTimer.startTimer();
        else{
                Thread ai = new Thread() {                    
                    public void run() {
                       blackTimer.startTimer();
                       AITurn();
                    }
                };
                ai.start();            
        }
    }
    
    public void feedAEN(AutreEventListener ael){
        aen.addAutreEventListener(ael);
    }
    
    private void placePiece(Piece p, int[] coord){
        Piece capturedPiece = board[coord[0]][coord[1]];
        int winCondition = 0;
        if(p!=capturedPiece && capturedPiece!=null && capturedPiece.getType()==PieceType.KING){ //1ère condition pour initialiser plateau sauvegardé
            winCondition=capturedPiece.isWhite()?1:-1;
        }
        board[coord[0]][coord[1]]=p;
        if(p.getType()==PieceType.PAWN && (coord[0]==6 || coord[0]==0)){ //Color doesn't matter
            if(isTurnForWhite){
                promotionTime=true;
                cdl = new CountDownLatch(1);
                PromotionTransferrer newPieceType = new PromotionTransferrer();
                SwingUtilities.invokeLater(new Runnable(){
                    public void run(){
                        PromotionModel pm = new PromotionModel(cdl,newPieceType);
                    }
                });
                try{
                    cdl.await();
                    promotionTime=false;
                }
                catch(InterruptedException ie) {}
                p.promotion(newPieceType.pt);
            }
            else p.promotion(PieceType.QUEEN);
        }
        aen.diffuserAutreEvent(new AutreEvent(this,"ADD:"+coord[0]+":"+coord[1]+":"+p.getType().toString()+":"+p.isWhite())); //Prevenir la vue
        if(winCondition!=0){
            aen.diffuserAutreEvent(new AutreEvent(this,"WIN:"+((winCondition==-1)?"W":"K")+"-DEFEAT")); //Fais appraître pop-up
            partieFinie=true;
        }
    }
    
    public void doWhenPress(int abs, int ord){
        if(partieFinie||promotionTime) return;
        Thread t = new Thread(){
            public void run(){
                if(selectedPiece==null || selectedPiece.isWhite()!=isTurnForWhite || !isMoveLegal(new int[] {abs,ord})) checkPossibleMoves(abs,ord);
                else{
                    whiteTimer.turnFinished();
                    deletePiece();
                    placePiece(selectedPiece,new int[]{abs,ord});
                    endMove();
                    if(!partieFinie) {
                        Thread ai = new Thread() {                    
                            public void run() {
                                blackTimer.startTimer();
                                try{
                                    Random rng = new Random();
                                    Thread.sleep(rng.nextInt(5001));
                                }
                                catch(InterruptedException ie){}
                                finally{AITurn();}
                            }
                        };
                        ai.start();
                    }
                } //move                
            }
        };
        t.start();
    }
    
    private void AITurn(){
        if(partieFinie) return;
        Random rng = new Random();
        while(possibleMoves==null || possibleMoves.size()<1) checkPossibleMoves(rng.nextInt(7),rng.nextInt(8));
        try{Thread.sleep(possibleMoves.size()*100);} catch(InterruptedException ie){}
        deletePiece();
        placePiece(selectedPiece,possibleMoves.get(rng.nextInt(possibleMoves.size())));
        endMove();
        blackTimer.turnFinished();
        if(!partieFinie) whiteTimer.startTimer();
    }
    
    private boolean isMoveLegal(int[] chosenMove){
        for(int[] legalMove : possibleMoves){
            if(Arrays.equals(legalMove, chosenMove)) return true;
        }
        return false;
    }
    
    private void deletePiece(){
        board[piecePosition[0]][piecePosition[1]] = null;
        aen.diffuserAutreEvent(new AutreEvent(this,"DEL:"+piecePosition[0]+":"+piecePosition[1]));
    }
    
    private void endMove(){
        selectedPiece=null;
        piecePosition=null;
        possibleMoves=null;
        isTurnForWhite=!isTurnForWhite;
    }
    
    private void checkPossibleMoves(int abs, int ord){
        selectedPiece = board[abs][ord];
        piecePosition = new int[]{abs,ord};
        if(selectedPiece==null||selectedPiece.isWhite()!=isTurnForWhite){ /*System.out.println("Not your piece!");*/ return;} //will be empty
        //else System.out.println("Is a piece!");
        possibleMoves = new ArrayList<>();
        //Depends on type of piece
        switch(selectedPiece.getType()){
            case PAWN: //1 forward
                int[] newCoord = new int[]{piecePosition[0]+(selectedPiece.isWhite()?1:-1),piecePosition[1]};
                if(isMovePossible(selectedPiece.isWhite(),newCoord)<=1) possibleMoves.add(newCoord);
                break;
            case BISHOP: //Diagonal+ 
                int[][] deltaListB = new int[][]{new int[]{-1,-1},new int[]{1,1},new int[]{-1,1},new int[]{1,-1}};
                for(int[] delta : deltaListB){
                    checkMovesInLine(selectedPiece.isWhite(),piecePosition,delta,possibleMoves);
                }
                break;
            case ROOK: //Orthogonal+
                int[][] deltaListR = new int[][]{new int[]{-1,0},new int[]{1,0},new int[]{0,1},new int[]{0,-1}};
                for(int[] delta : deltaListR){
                    checkMovesInLine(selectedPiece.isWhite(),piecePosition,delta,possibleMoves);
                }
                break;
            case KNIGHT: //2 then 1
                int[][] deltaListN = new int[][]{new int[]{-1,-2},new int[]{1,2},new int[]{-1,2},new int[]{1,-2},new int[]{-2,1},new int[]{2,1},new int[]{-2,-1},new int[]{2,-1}};
                for(int[] delta : deltaListN){
                    int[] testedCoord = new int[] {piecePosition[0]+delta[0],piecePosition[1]+delta[1]};
                    if(isMovePossible(selectedPiece.isWhite(),testedCoord)<=1) possibleMoves.add(testedCoord);
                }
                break;
            case KING: //Orthogonal and Diagonal
                int[][] deltaListK = new int[][]{new int[]{-1,-1},new int[]{1,1},new int[]{-1,1},new int[]{1,-1},new int[]{-1,0},new int[]{1,0},new int[]{0,1},new int[]{0,-1}};
                for(int[] delta : deltaListK){
                    int[] testedCoord = new int[] {piecePosition[0]+delta[0],piecePosition[1]+delta[1]};
                    if(isMovePossible(selectedPiece.isWhite(),testedCoord)<=1) possibleMoves.add(testedCoord);
                }
                break;
            case QUEEN://Orthogonal+ and Diagonal+
                int[][] deltaListQ = new int[][]{new int[]{-1,-1},new int[]{1,1},new int[]{-1,1},new int[]{1,-1},new int[]{-1,0},new int[]{1,0},new int[]{0,1},new int[]{0,-1}};
                for(int[] delta : deltaListQ){
                    checkMovesInLine(selectedPiece.isWhite(),piecePosition,delta,possibleMoves);
                }
                break;
            
        }
        /*for(int[] c : possibleMoves)
            System.out.println(c[0]+""+c[1]);*/
    }
    
    private void checkMovesInLine(boolean color, int[] coord, int[] deltas, ArrayList<int[]> moveList){
        int[] newCoord = new int[] {coord[0]+deltas[0],coord[1]+deltas[1]};
        while(isMovePossible(color,newCoord)==0){
            moveList.add(newCoord.clone());
            newCoord[0]+=deltas[0]; newCoord[1]+=deltas[1];
        }
        if(isMovePossible(color,newCoord)==1) moveList.add(newCoord);
    }
    
    /**
    * Returns : 
    * 0 : Empty cell
    * 1 : Opposite piece
    * 2 : Own piece
    * 3 : Out of board
    */
    private int isMovePossible(boolean isAColor,int[] coord){
        if(coord[0]<0||coord[0]>=7||coord[1]<0||coord[1]>=8) return 3;
        if(board[coord[0]][coord[1]]==null) return 0;
        if(isAColor!=board[coord[0]][coord[1]].isWhite()) return 1;
        return 2;
    }

    @Override
    public void actionADeclancher(AutreEvent evt) { //NO MORE TIME
        partieFinie=true;
        //isTurnForWhite=false;
        aen.diffuserAutreEvent(new AutreEvent(this,"WIN:"+evt.getDonnee()+"-TIME"));
    }
}
