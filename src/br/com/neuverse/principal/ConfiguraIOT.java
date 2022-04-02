package br.com.neuverse.principal;

import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTML.Attribute;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import br.com.neuverse.entity.ButtonIot;
import br.com.neuverse.entity.ComandoIOT;
import br.com.neuverse.entity.ConfigIOT;
import br.com.neuverse.entity.ControleRest;
import br.com.neuverse.entity.ConfigIOT.ConectorSessao;
import br.com.neuverse.enumerador.Status;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ConfiguraIOT {
    JFrame f = new JFrame();
    JFormattedTextField ipLocal = new JFormattedTextField();
    JFormattedTextField tfssid = new JFormattedTextField();
    JFormattedTextField tfssidpassw = new JFormattedTextField();
    JButton gravar = new JButton("gravar");
    JButton configurar = new JButton("configurar");
    JEditorPane pane = new JEditorPane(); 
    ConfigIOT configIOT = new ConfigIOT();
    JLabel totBiot = new JLabel();

    public static void main(String[] args) throws Exception {

        ConfiguraIOT conf = new ConfiguraIOT();
        //conf.gerarTelaConfig();     

        conf.configIOT.getSsidSessao().setPassword("80818283");
        conf.configIOT.getSsidSessao().setSsid("Escritorio");
        conf.configIOT.getServidorSessao().setEndereco("192.168.10.254");
        conf.configIOT.getServidorSessao().setPorta(27016);
        conf.configIOT.getConectorSessao().setSenha("mar0403");
        conf.configIOT.getConectorSessao().setUsuario("msmariano");
        conf.configIOT.getServidorRestSessao().setIp("192.168.10.254");
        conf.configIOT.getConectorSessao().setNome("DeviceNeuverse");
        conf.configIOT.getServidorRestSessao().setPorta(8080);
        SimpleDateFormat sd  = new SimpleDateFormat("ddMMyyyy");            
        conf.configIOT.setDataAtualizacao(sd.format(new Date()));
        conf.configIOT.getButtonIOTSessao().add(conf.gerarConfBt(1,14,13,"BT1",Status.KEY,Status.OFF,Status.HIGH));
        conf.configIOT.getButtonIOTSessao().add(conf.gerarConfBt(2,27,13,"BT2",Status.KEY,Status.OFF,Status.HIGH));
        conf.configIOT.getButtonIOTSessao().add(conf.gerarConfBt(3,26,13,"BT3",Status.KEY,Status.OFF,Status.HIGH));
        conf.configIOT.getButtonIOTSessao().add(conf.gerarConfBt(4,25,13,"BT4",Status.KEY,Status.OFF,Status.HIGH));
        conf.configIOT.getButtonIOTSessao().add(conf.gerarConfBt(5,33,13,"BT5",Status.KEY,Status.OFF,Status.HIGH));
        conf.configIOT.getButtonIOTSessao().add(conf.gerarConfBt(6,32,13,"BT6",Status.KEY,Status.OFF,Status.HIGH));
        ControleRest cr = new ControleRest();
        conf.configIOT.setDataAtualizacao(sd.format(new Date()));
        ComandoIOT com;
        com = new ComandoIOT();
        com.setAcao("setConfig");
        Gson gson = new GsonBuilder().setDateFormat("dd/MM/yyyy HH:mm:ss").create();
        String jSon = gson.toJson(conf.configIOT);
        com.setConteudo(jSon);
        jSon = gson.toJson(com);
        cr.setIp("192.168.10.31");
        com = cr.sendRest(jSon);
        
        

        /*while (true) {
            try {
                //conf.networkPing();
                //conf.info("192.168.10.27");
                Thread.sleep(10000);
            } 
            catch (Exception e) {
            }
        }*/
    }

    ButtonIot gerarConfBt(int id,int gpioNum, int gpioNumControle,String nomeGpio,
        Status funcao,Status status,Status tecla) {
        ButtonIot biot = new ButtonIot();
        biot.setButtonID(id);
        biot.setGpioNum(gpioNum);
        biot.setGpioNumControle(gpioNumControle);
        biot.setNomeGpio(nomeGpio);
        biot.setFuncao(funcao);
        biot.setStatus(status);
        biot.setTecla(tecla);
        return biot;
    }

    public String preencheLinha(Integer id,String value,String attributo){
        StringBuilder txtHtmlCfg = new StringBuilder();
        txtHtmlCfg.append("<tr>");
        txtHtmlCfg.append("<td>"+attributo+"</td>");
        txtHtmlCfg.append("<td><input id = '"+attributo+id+"' type='text'value='"+value+"'></input></td> ");
        txtHtmlCfg.append("</tr>");
        return txtHtmlCfg.toString();
    }

    public void info(String ip){
        try{
            ip = ip.replace("/", "");
            System.out.println("Buscando DeviceIOT em "+ip);
            ControleRest cr = new ControleRest();
            ComandoIOT com;
            String jSon = "";
            com = new ComandoIOT();
            Gson gson = new GsonBuilder().setDateFormat("dd/MM/yyyy HH:mm:ss").create();
            com.setAcao("info");
            jSon = gson.toJson(com);
            cr.setIp(ip);
            com = cr.sendRest(jSon);
            System.out.println(com.getResultado());

        }catch(Exception e){
            System.err.println(e.getMessage());
        }

    }

    public void setConfig(){
        try{
            Gson gson = new GsonBuilder().setDateFormat("dd/MM/yyyy HH:mm:ss").create();
            HTMLDocument doc = (HTMLDocument) pane.getDocument();
            String posicao = (String) doc.getElement("posicao").getAttributes().getAttribute(Attribute.VALUE);
            ButtonIot biot = configIOT.getButtonIOTSessao().get(Integer.parseInt(posicao));
            biot.setNomeGpio((String) doc.getElement("niot0").getAttributes().getAttribute(Attribute.VALUE));
            
            ControleRest cr = new ControleRest();
            SimpleDateFormat sd  = new SimpleDateFormat("ddMMyyyy HH:mm:ss");            
            configIOT.setDataAtualizacao(sd.format(new Date()));
            ComandoIOT com;
            com = new ComandoIOT();
            com.setAcao("setConfig");
            String jSon = gson.toJson(configIOT);
            com.setConteudo(jSon);
            jSon = gson.toJson(com);
            cr.setIp("192.168.10.27");
            com = cr.sendRest(jSon);
        }
        catch(Exception e){

        }
   }

    public void retConfig(){

        try {
            ControleRest cr = new ControleRest();
            ComandoIOT com;
            String jSon = "";
            com = new ComandoIOT();
            Gson gson = new GsonBuilder().setDateFormat("dd/MM/yyyy HH:mm:ss").create();
            com.setAcao("retConfig");
            jSon = gson.toJson(com);
            cr.setIp("192.168.10.27");
            com = cr.sendRest(jSon);
            configIOT = gson.fromJson(com.getResultado(), ConfigIOT.class);               
            
            ipLocal.setText(configIOT.getServidorRestSessao().getIp());
            tfssid.setText(configIOT.getSsidSessao().getSsid());
            tfssidpassw.setText(configIOT.getSsidSessao().getPassword());
            StringBuilder txtHtmlCfg = new StringBuilder();
            txtHtmlCfg.append("<input type = 'HIDDEN' id='posicao' value = '0'/>");
            txtHtmlCfg.append("<table border = '1'>");
            if(configIOT.getButtonIOTSessao().size()>0)
            {
                ButtonIot biot = configIOT.getButtonIOTSessao().get(0);
                
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
            f.add(pane);
            totBiot.setText("Total botoes:"+configIOT.getButtonIOTSessao().size());
            f.add(totBiot);
        }
        catch(Exception e){

        }

    }
    

    public void gerarTelaConfig(){
       
        JLabel labelIpLocal = new JLabel("IpHost:");
        JLabel lbssid = new JLabel("ssid:");
        JLabel lbssidpassw = new JLabel("ssidPwd:");

        f.setSize(350, 500);
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

        labelIpLocal.setBounds(20,0,50,20);
        ipLocal.setBounds(70, 0, 100, 20);
        lbssid.setBounds(20,25,50,20);
        tfssid.setBounds(70,25,100,20);
        lbssidpassw.setBounds(20,50,50,20);
        tfssidpassw.setBounds(70,50,100,20);
        ipLocal.setEnabled(false);
        pane.setBounds(15, 180, 300, 200);
        gravar.setBounds(200, 50, 100, 20);
        configurar.setBounds(200, 25, 100, 20);
        totBiot.setBounds(15,150,200,20);
        f.add(ipLocal);
        f.add(labelIpLocal);
        f.add(lbssid);
        f.add(tfssid);
        f.add(lbssidpassw);
        f.add(tfssidpassw);
        f.add(gravar);
        f.add(configurar);
        f.add(totBiot);
        f.setVisible(true);

        gravar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setConfig();
            }
        });

        configurar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                retConfig();
            }
        });
    }

    
    public void networkPing() throws IOException{
        
        InetAddress localhost = InetAddress.getLocalHost();
        // uso de IPv4 e assumido!
        byte[] ip = {(byte) 192,(byte) 168,10,1};

        for (int i = 1; i <= 254; i++) {
            ip[3] = (byte) i;
            InetAddress address = InetAddress.getByAddress(ip);
            if (address.isReachable(10)) {
                info(address.toString());
            }
            //else if (!address.getHostAddress().equals(address.getHostName())) {
                //System.out.println(address + " maquina reconhecida por um DNSLookup");
            //} 
            //else {
            //    System.out.println(address + " o endereço de host e o nome do host são iguais, o host name não pode ser resolvido.");
            //}
        }        
    }
}


/*
HTMLDocument doc = (HTMLDocument) pane.getDocument();
        Element e =  doc.getElement("niot");
 AttributeSet as = e.getAttributes();
           String s = (String) as.getAttribute(Attribute.VALUE);
           for (ButtonIot biot : configIOT.getButtonIOTSessao()){
            Object  o = as.getAttribute(Attribute.VALUE); 
            //o = (Object) biot.getNomeGpio();
            System.out.println(o);
            break;

             
        
        

        //Get the ref of foo element
        //Element ele=doc.getElement("foo");
        //ListView view=new ListView(ele);
        //System.out.println(ele.getElementCount());
        //try{
        //     doc.insertBeforeEnd(ele.getElement(0), "<ul><li>Test");          
       // }catch(Exception ex){}

*/

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

            valor.setText(com.getResultado());
            
            Element e =  doc.getElement("niot0");

                AttributeSet as = e.getAttributes();
                String s = (String) as.getAttribute(Attribute.VALUE);
                
                 Object  o = as.getAttribute(Attribute.VALUE); 
                 //o = (Object) biot.getNomeGpio();
                 System.out.println(o);
                 break;
            
            
            */
