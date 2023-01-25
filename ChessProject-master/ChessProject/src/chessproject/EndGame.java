/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chessproject;

import java.awt.GridLayout;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

/**
 * Fenêtre de fin de partie (roi tué)
 * @author victo
 */
public class EndGame extends JFrame{
    public EndGame(String endResult){
        String[] res = endResult.split("-");
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new GridLayout(2,1));
        JLabel flavorText;
        if(res[1].equals("TIME")){
            flavorText = new JLabel("Temps écoulé! "+(res[0].equals("W")?"L'ordinateur":"Le joueur")+" gagne!");
            res[0]=res[0].equals("W")?"K":"W";
        }
        else
            flavorText = new JLabel("Le roi "+(res[0].equals("W")?"noir":"blanc")+" est mort ! Longue vie au roi "+(res[0].equals("W")?"blanc !":"noir !"));
        add(flavorText);
        ImageIcon kingpng = new ImageIcon("src/images/Chess_k"+(res[0].equals("W")?"l":"d")+"t60.png"); 
        JLabel image = new JLabel(kingpng);
        add(image);
        this.pack();
        this.setVisible(true);
    }
}
