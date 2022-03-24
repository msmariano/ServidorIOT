package br.com.neuverse.principal;

import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.text.AttributeSet;
import javax.swing.text.Element;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTML.Attribute;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import br.com.neuverse.entity.ButtonIot;
import br.com.neuverse.entity.ComandoIOT;
import br.com.neuverse.entity.ConfigIOT;
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

        JEditorPane pane = new JEditorPane();
        
        
        

        //Get the ref of foo element
        /*Element ele=doc.getElement("foo");
        ListView view=new ListView(ele);
        System.out.println(ele.getElementCount());
        try{
             doc.insertBeforeEnd(ele.getElement(0), "<ul><li>Test");          
        }catch(Exception ex){}*/


        JFormattedTextField ipLocal = new JFormattedTextField();
        JLabel labelIpLocal = new JLabel("IpHost:");

        JFormattedTextField tfssid = new JFormattedTextField();
        JLabel lbssid = new JLabel("ssid:");
        JFormattedTextField tfssidpassw = new JFormattedTextField();
        JLabel lbssidpassw = new JLabel("ssidPwd:");


        try {
            com = new ComandoIOT();
            Gson gson = new GsonBuilder().setDateFormat("dd/MM/yyyy HH:mm:ss").create();
            String jSon = "";
            ConfigIOT configIOT = new ConfigIOT();
             com.setAcao("retConfig");
            jSon = gson.toJson(com);
            cr.setIp("192.168.10.27");
            com = cr.sendRest(jSon);
            configIOT = gson.fromJson(com.getResultado(), ConfigIOT.class);
            ipLocal.setText(configIOT.getServidorRestSessao().getIp());
            tfssid.setText(configIOT.getSsidSessao().getSsid());
            tfssidpassw.setText(configIOT.getSsidSessao().getPassword());
            
            StringBuilder txtHtmlCfg = new StringBuilder();
            txtHtmlCfg.append("<table border = '1'>");

            for (ButtonIot biot : configIOT.getButtonIOTSessao()){
                txtHtmlCfg.append(preencheLinha(biot.getButtonID(),biot.getNomeGpio(),"niot"));
                txtHtmlCfg.append(preencheLinha(biot.getButtonID(),String.valueOf(biot.getButtonID()),"ID"));
                txtHtmlCfg.append(preencheLinha(biot.getButtonID(),String.valueOf(biot.getGpioNum()),"GpioEscrita"));
                txtHtmlCfg.append(preencheLinha(biot.getButtonID(),String.valueOf(biot.getGpioNumControle()),"GpioLeitura"));
                txtHtmlCfg.append(preencheLinha(biot.getButtonID(),String.valueOf(biot.getFuncao().getValor()),"Funcao"));
                txtHtmlCfg.append(preencheLinha(biot.getButtonID(),String.valueOf(biot.getStatus().getValor()),"Status"));
               
                
                    
            }
            
            txtHtmlCfg.append("</table>");

            pane.setContentType("text/html");
        pane.setText(txtHtmlCfg.toString()); 
        HTMLDocument doc = (HTMLDocument) pane.getDocument();
           Element e =  doc.getElement("niot");

           AttributeSet as = e.getAttributes();
           String s = (String) as.getAttribute(Attribute.VALUE);
           for (ButtonIot biot : configIOT.getButtonIOTSessao()){
            Object  o = as.getAttribute(Attribute.VALUE); 
            //o = (Object) biot.getNomeGpio();
            System.out.println(o);
            break;
                        
            

           }
           

            /*
            configIOT.getSsidSessao().setSsid("neuverseServidorIot");
            configIOT.getSsidSessao().setPassword(" nileindrei");
            configIOT.getServidorSessao().setEndereco("192.168.10.1");
            configIOT.getServidorSessao().setPorta(27015);
            jSon = gson.toJson(configIOT);

            com.setAcao("setConfig");
            com.setConteudo(jSon);
            jSon = gson.toJson(com);
            com = cr.sendRest(jSon);

            valor.setText(com.getResultado());*/
            
            System.out.println();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }

        System.out.println("Iniciando...");
        JFrame f = new JFrame();
       
        // JLabel versao = new JLabel();
        // versao.setText("V1.0.0");
        JButton ligaDesliga = new JButton("gravar");
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
        f.setTitle("Configuração IOT V1.0.0");
        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(f.getContentPane());
        f.getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(0, 400, Short.MAX_VALUE));
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(0, 300, Short.MAX_VALUE));

        // versao.setBounds(0, 0, 100, 45);
        
        labelIpLocal.setBounds(20,0,50,20);
        ipLocal.setBounds(70, 0, 100, 20);
        lbssid.setBounds(20,25,50,20);
        tfssid.setBounds(70,25,100,20);
        lbssidpassw.setBounds(20,50,50,20);
        tfssidpassw.setBounds(70,50,100,20);

        ipLocal.setEnabled(false);


        pane.setBounds(0, 100, 300, 200);
        

        ligaDesliga.setBounds(200, 50, 100, 20);
        //valor.setText("0");
        f.add(ipLocal);
        f.add(labelIpLocal);
        f.add(pane);
        f.add(lbssid);
        f.add(tfssid);
        f.add(lbssidpassw);
        f.add(tfssidpassw);
        f.add(ligaDesliga);
        // f.add(versao);

        f.setVisible(true);

        while (true) {
            SimpleDateFormat sd = new SimpleDateFormat("HH:mm");
            try {
                String hora = sd.format(new Date());
               //valor.setText(hora);
                Thread.sleep(30000);

            } catch (Exception e) {
            }
        }
    }
    public static String preencheLinha(Integer id,String value,String attributo){
        StringBuilder txtHtmlCfg = new StringBuilder();
        txtHtmlCfg.append("<tr>");
        txtHtmlCfg.append("<td>"+attributo+"</td>");
        txtHtmlCfg.append("<td><input id = '"+attributo+"'"+id+" type='text'value='"+value+"'></input></td> ");
        txtHtmlCfg.append("</tr>");
        return txtHtmlCfg.toString();
    }

}
