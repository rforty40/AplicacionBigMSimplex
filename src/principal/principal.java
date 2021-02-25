package principal;

import java.awt.Dimension;
import java.awt.Toolkit;
import javax.swing.JOptionPane;
import vista.FrameBigM;
import vista.Framehd;

public class principal {

    public static void main(String[] args) {

       
        int ancho = java.awt.Toolkit.getDefaultToolkit().getScreenSize().width;
        int alto = java.awt.Toolkit.getDefaultToolkit().getScreenSize().height;
        if(ancho>=1920 && alto >= 1024){
             FrameBigM frameBigM = new FrameBigM();
             frameBigM.setVisible(true);
        }else{
            Framehd framehd= new Framehd();
            framehd.setVisible(true);
        }
        
        
        
    }
}
