package br.com.neuverse.principal;

import java.awt.BorderLayout;

import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.UIManager;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;
import com.pi4j.io.gpio.trigger.GpioCallbackTrigger;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.Callable;

public class MedidorAgua {
    
    private Double pulsos = 0.0;
    private Double volume = 0.0;
    
    @SuppressWarnings("deprecation")
    public static void main(String[] args) throws IOException {


        if (args.length > 0) {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            new MedidorAgua().inicializar();
            return;

        }
        Runtime.getRuntime().exec("java -jar "+ (new File(MedidorAgua.class.getProtectionDomain().getCodeSource().getLocation().getPath())).getAbsolutePath() + " cmd");

        

        
    }

    public void inicializar(){
        System.out.println("Iniciando...");
        JFrame f = new JFrame();
        JFormattedTextField valor = new JFormattedTextField();
        //JLabel versao = new JLabel();
        //versao.setText("V1.0.0");
        f.setSize(500, 100);
		f.setLayout(new BorderLayout());
        Dimension ds = Toolkit.getDefaultToolkit().getScreenSize(); 
		Dimension dw = f.getSize(); 
		f.setLocation((ds.width - dw.width) / 2, (ds.height - dw.height) / 2);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setResizable(false);
        f.setTitle("Medidor Agua V1.0.4");
        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(f.getContentPane());
		f.getContentPane().setLayout(layout);
		layout.setHorizontalGroup(
				layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(0, 400, Short.MAX_VALUE));
		layout.setVerticalGroup(
				layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(0, 300, Short.MAX_VALUE));

        //versao.setBounds(0, 0, 100, 45);
        valor.setBounds(200, 0, 100, 45);
        valor.setText("0");
		f.add(valor);
        //f.add(versao);

        f.setVisible(true);

        final GpioController gpio = GpioFactory.getInstance();
        final GpioPinDigitalInput sensor = gpio.provisionDigitalInputPin(RaspiPin.GPIO_02, PinPullResistance.PULL_UP);

        sensor.addTrigger(new GpioCallbackTrigger(new Callable<Void>() {
            public Void call() throws Exception {
                pulsos++;
                //F=6.6*Q(Q=L/MIN)
                //396 pulsos por litro
              
                 
                volume = (pulsos /690);

                valor.setText(String.format("%.2f", volume));
                return null;
            }
        }));

        sensor.addListener(new GpioPinListenerDigital() {
            @Override
            public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
               
               
                
            }
        });

        

    }
    
}
