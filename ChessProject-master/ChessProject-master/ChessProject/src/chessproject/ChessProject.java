package chessproject;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

/**
 * La vue
 * @author victo
 */
public class ChessProject extends JFrame implements AutreEventListener {

    private ChessBoard chessBoard;
    private BoardPressController controller;
    private JButton[][] buttonGrid;
    private boolean shouldSave;
    private JLabel whiteChrono;
    private JLabel blackChrono;
    
    public ChessProject(ChessBoard savedBoard){
        shouldSave=true;
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                try{
                    FileOutputStream saveFile = new FileOutputStream("src/savefile.txt"); //Supprime automatiquement ancienne sauvegarde
                    if(shouldSave){
                        ObjectOutputStream oos = new ObjectOutputStream(saveFile);
                        oos.writeObject(chessBoard);
                        oos.close();
                        System.out.println("Partie sauvegardée!");                        
                    }
                    else System.out.println("Sauvegarde détruite!");
                    saveFile.close();                   
                }
                catch(IOException exp){System.out.println("Fichier mal ouvert..."+exp.getMessage());}
            }
        });
        buttonGrid = new JButton[7][8];
        setLayout(new GridLayout(8,8));  
        chessBoard = (savedBoard!=null)?savedBoard:new ChessBoard();
        chessBoard.setTransientVars();
        chessBoard.feedAEN(this); 
        controller = new BoardPressController(chessBoard);
        for(int i=0;i<7*8;i++){
            JButton tile = new JButton();
            tile.setPreferredSize(new Dimension(50,50));
            Color colTile = (i%2==(i/8)%2)?new Color(0,0,0): new Color(255,255,255); //Les tuiles noires ont une parité de colonne/rangée égale          
            tile.setBackground(colTile); //NE MARCHE PAS SUR MAC???
            tile.setActionCommand(((Integer)(6-(i/8))).toString()+" "+((Integer)(i%8)).toString());
            buttonGrid[6-(i/8)][i%8] = tile;
            tile.addActionListener(controller);
            add(tile);
        }
        chessBoard.initialiserPlateau(savedBoard==null);   
        whiteChrono = new JLabel("3:00");
        add(whiteChrono);
        blackChrono = new JLabel("3:00");
        add(blackChrono);
        this.pack();
        this.setVisible(true);
    }
    
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable(){
            public void run(){
                try{
                    FileInputStream saveFile = new FileInputStream("src/savefile.txt");
                    ObjectInputStream ois = new ObjectInputStream(saveFile);
                    new ChessProject((ChessBoard)ois.readObject());
                    ois.close();
                    System.out.println("Fichier bien ouvert!");
                }
                catch(ClassNotFoundException | FileNotFoundException e){new ChessProject(null);}
                catch(IOException e){new ChessProject(null);}
            }
        });
    }

    @Override
    public void actionADeclancher(AutreEvent evt) {
        if(evt.getSource().getClass()==ChessBoard.class){
           String[] commList = evt.getDonnee().toString().split(":");
            switch(commList[0]){
                case "ADD":{
                    int abs = Integer.parseInt(commList[1]), ord = Integer.parseInt(commList[2]);
                    String piecepng = "Chess_";
                    switch(commList[3]){
                        case "QUEEN" : piecepng+="q"; break;
                        case "KING" : piecepng+="k"; break;
                        case "BISHOP" : piecepng+="b"; break;
                        case "KNIGHT" : piecepng+="n"; break;
                        case "PAWN" : piecepng+="p"; break;
                        case "ROOK" : piecepng+="r"; break;
                    }
                    piecepng+=(commList[4].equals("true")?"l":"d")+"t60.png";
                    Icon ico = new ImageIcon("src/images/"+piecepng);
                    SwingUtilities.invokeLater(new Runnable(){
                        public void run(){
                            buttonGrid[abs][ord].setIcon(ico);
                        }
                    });   
                    break;
                }
            case "DEL":{
                int abs = Integer.parseInt(commList[1]), ord = Integer.parseInt(commList[2]);
                SwingUtilities.invokeLater(new Runnable(){
                    public void run(){
                        buttonGrid[abs][ord].setIcon(null);
                    }
                });
                break;
            }
            case "WIN": {
                shouldSave=false;
                SwingUtilities.invokeLater(new Runnable(){
                    public void run(){
                        new EndGame(commList[1]);
                    }
                });
                break; 
            }
            default:
                throw new UnsupportedOperationException("Not supported yet.");           
            }
        }
        else{
            String[] commList = evt.getDonnee().toString().split(":");
            int timeLeft = Integer.parseInt(commList[0]);
            SwingUtilities.invokeLater(new Runnable(){
                    public void run(){
                        if(commList[1].equals("W"))
                            whiteChrono.setText(timeLeft/60+":"+String.format("%02d",timeLeft%60)); 
                        else
                            blackChrono.setText(timeLeft/60+":"+String.format("%02d",timeLeft%60)); 
                    }
            });                 
        }
    }  
}
