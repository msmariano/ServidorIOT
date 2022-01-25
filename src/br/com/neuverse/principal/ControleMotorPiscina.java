package br.com.neuverse.principal;

import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;

import br.com.neuverse.entity.ComandoIOT;
import br.com.neuverse.entity.ControleRest;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ControleMotorPiscina {
    
    public static ControleRest cr = new ControleRest();
    public static ComandoIOT com;

    public static void main(String[] args) {
      
        
        try {
            com = cr.motorPiscina("estadoAtual");
            System.out.println();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }


        System.out.println("Iniciando...");
        JFrame f = new JFrame();
        JFormattedTextField valor = new JFormattedTextField();
        //JLabel versao = new JLabel();
        //versao.setText("V1.0.0");
        JButton ligaDesliga = new JButton();
        ligaDesliga.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ComandoIOT com = new ComandoIOT();
               
                
            }
        });
        
        
        
        f.setSize(500, 500);
		f.setLayout(new BorderLayout());
        Dimension ds = Toolkit.getDefaultToolkit().getScreenSize(); 
		Dimension dw = f.getSize(); 
		f.setLocation((ds.width - dw.width) / 2, (ds.height - dw.height) / 2);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setResizable(false);
        f.setTitle("Controle Motor Piscina V1.0.0");
        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(f.getContentPane());
		f.getContentPane().setLayout(layout);
		layout.setHorizontalGroup(
				layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(0, 400, Short.MAX_VALUE));
		layout.setVerticalGroup(
				layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(0, 300, Short.MAX_VALUE));

        //versao.setBounds(0, 0, 100, 45);
        valor.setBounds(200, 0, 100, 45);
        ligaDesliga.setBounds(200, 100, 100, 45);
        valor.setText("0");
		f.add(valor);
        f.add(ligaDesliga);
        //f.add(versao);

        f.setVisible(true);

        

        while(true) {
            SimpleDateFormat sd = new SimpleDateFormat("HH:mm");
            try {
                String hora = sd.format(new Date());
                valor.setText(hora);
                Thread.sleep(30000);   
				
            } catch (Exception e) {
            }
        }
    }
    
}
