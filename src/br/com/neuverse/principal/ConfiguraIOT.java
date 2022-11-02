package br.com.neuverse.principal;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JEditorPane;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.event.MouseInputAdapter;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import javax.swing.text.Element;
import javax.swing.text.TableView.TableRow;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.ListView;
import javax.swing.text.html.HTML.Attribute;
import java.awt.FlowLayout;
import org.w3c.dom.events.MouseEvent;
import java.awt.Color;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import br.com.neuverse.entity.ButtonIot;
import br.com.neuverse.entity.ComandoIOT;
import br.com.neuverse.entity.ConfigDados;
import br.com.neuverse.entity.ConfigIOT;
import br.com.neuverse.entity.ControleRest;
import br.com.neuverse.entity.ConfigIOT.ConectorSessao;
import br.com.neuverse.enumerador.Status;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;
import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.RemoteDevice;

public class ConfiguraIOT implements ActionListener{
    JFrame f = new JFrame();

    JFormattedTextField ipLocal = new JFormattedTextField();
    JFormattedTextField tfssid = new JFormattedTextField();
    JFormattedTextField tfssidpassw = new JFormattedTextField();
    JButton gravar = new JButton("gravar");
    JButton ler = new JButton("ler");
    JButton acBt = new JButton("+");
    JEditorPane pane = new JEditorPane();
    ConfigIOT configIOT = new ConfigIOT();
    JLabel totBiot = new JLabel();
    // String [] colunas = {"ID", "PinIN",
    // "PinOUT","Nick","Funcao","Status","Tecla"};
    /*
     * Object [][] dados = {
     * {"",new JButton("+"),new JButton("+"),new JButton("+"),new JButton("+"),new
     * JButton("+"),new JButton("+")}
     * };
     */
    // DefaultTableModel tableModel = new DefaultTableModel(dados, colunas);
    JTable tabela;// = new JTable(tableModel);

    public static void main(String[] args) throws Exception {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        ConfiguraIOT conf = new ConfiguraIOT();
        conf.gerarTelaConfig();

        /*
         * conf.configIOT.getSsidSessao().setPassword("80818283");
         * conf.configIOT.getSsidSessao().setSsid("Escritorio");
         * conf.configIOT.getServidorSessao().setEndereco("192.168.10.254");
         * conf.configIOT.getServidorSessao().setPorta(27016);
         * conf.configIOT.getConectorSessao().setSenha("mar0403");
         * conf.configIOT.getConectorSessao().setUsuario("msmariano");
         * conf.configIOT.getServidorRestSessao().setIp("192.168.10.254");
         * conf.configIOT.getConectorSessao().setNome("DeviceNeuverse");
         * conf.configIOT.getServidorRestSessao().setPorta(8080);
         * SimpleDateFormat sd = new SimpleDateFormat("ddMMyyyy");
         * conf.configIOT.setDataAtualizacao(sd.format(new Date()));
         * conf.configIOT.getButtonIOTSessao().add(conf.gerarConfBt(1,14,13,"BT1",Status
         * .KEY,Status.OFF,Status.HIGH));
         * conf.configIOT.getButtonIOTSessao().add(conf.gerarConfBt(2,27,13,"BT2",Status
         * .KEY,Status.OFF,Status.HIGH));
         * conf.configIOT.getButtonIOTSessao().add(conf.gerarConfBt(3,26,13,"BT3",Status
         * .KEY,Status.OFF,Status.HIGH));
         * conf.configIOT.getButtonIOTSessao().add(conf.gerarConfBt(4,25,13,"BT4",Status
         * .KEY,Status.OFF,Status.HIGH));
         * conf.configIOT.getButtonIOTSessao().add(conf.gerarConfBt(5,33,13,"BT5",Status
         * .KEY,Status.OFF,Status.HIGH));
         * conf.configIOT.getButtonIOTSessao().add(conf.gerarConfBt(6,32,13,"BT6",Status
         * .KEY,Status.OFF,Status.HIGH));
         * ControleRest cr = new ControleRest();
         * conf.configIOT.setDataAtualizacao(sd.format(new Date()));
         * ComandoIOT com;
         * com = new ComandoIOT();
         * com.setAcao("setConfig");
         * Gson gson = new GsonBuilder().setDateFormat("dd/MM/yyyy HH:mm:ss").create();
         * String jSon = gson.toJson(conf.configIOT);
         * com.setConteudo(jSon);
         * jSon = gson.toJson(com);
         * cr.setIp("192.168.10.31");
         * com = cr.sendRest(jSon);
         */

        /*
         * while (true) {
         * try {
         * //conf.networkPing();
         * //conf.info("192.168.10.27");
         * Thread.sleep(10000);
         * }
         * catch (Exception e) {
         * }
         * }
         */
    }

    ButtonIot gerarConfBt(int id, int gpioNum, int gpioNumControle, String nomeGpio,
            Status funcao, Status status, Status tecla) {
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

    public String preencheLinha(Integer id, String value, String attributo) {
        StringBuilder txtHtmlCfg = new StringBuilder();
        txtHtmlCfg.append("<tr>");
        txtHtmlCfg.append("<td>" + attributo + "</td>");
        txtHtmlCfg.append("<td><input id = '" + attributo + id + "' type='text'value='" + value + "'></input></td> ");
        txtHtmlCfg.append("</tr>");
        return txtHtmlCfg.toString();
    }

    public void info(String ip) {
        try {
            ip = ip.replace("/", "");
            System.out.println("Buscando DeviceIOT em " + ip);
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

        } catch (Exception e) {
            System.err.println(e.getMessage());
        }

    }

    public void setConfig() {
        try {

            LocalDevice localDevice = LocalDevice.getLocalDevice();

            RemoteDevice[] remoteDevice = localDevice.getDiscoveryAgent().retrieveDevices(DiscoveryAgent.PREKNOWN);

            for(RemoteDevice rm : remoteDevice) {
                System.out.print("Devices: " + rm.getFriendlyName(false));
            }

            Gson gson = new GsonBuilder().setDateFormat("dd/MM/yyyy HH:mm:ss").create();
            // HTMLDocument doc = (HTMLDocument) pane.getDocument();
            // String posicao = (String)
            // doc.getElement("posicao").getAttributes().getAttribute(Attribute.VALUE);
            // ButtonIot biot =
            // configIOT.getButtonIOTSessao().get(Integer.parseInt(posicao));
            // biot.setNomeGpio((String)
            // doc.getElement("niot0").getAttributes().getAttribute(Attribute.VALUE));

            // ControleRest cr = new ControleRest();
            SimpleDateFormat sd = new SimpleDateFormat("ddMMyyyy HH:mm:ss");
            configIOT.setDataAtualizacao(sd.format(new Date()));
            ComandoIOT com;
            com = new ComandoIOT();
            com.setAcao("setConfig");
            String jSon = gson.toJson(configIOT);
            // com.setConteudo(jSon);
            // jSon = gson.toJson(com);
            // cr.setIp("192.168.10.27");
            // com = cr.sendRest(jSon);
        } catch (Exception e) {
            System.out.println("");
        }
    }

    public void retConfig() {

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
            if (configIOT.getButtonIOTSessao().size() > 0) {
                ButtonIot biot = configIOT.getButtonIOTSessao().get(0);

                txtHtmlCfg.append(preencheLinha(biot.getButtonID(), biot.getNomeGpio(), "niot"));
                txtHtmlCfg.append(preencheLinha(biot.getButtonID(), String.valueOf(biot.getButtonID()), "ID"));
                txtHtmlCfg.append(preencheLinha(biot.getButtonID(), String.valueOf(biot.getGpioNum()), "GpioEscrita"));
                txtHtmlCfg.append(
                        preencheLinha(biot.getButtonID(), String.valueOf(biot.getGpioNumControle()), "GpioLeitura"));
                txtHtmlCfg.append(
                        preencheLinha(biot.getButtonID(), String.valueOf(biot.getFuncao().getValor()), "Funcao"));
                txtHtmlCfg.append(
                        preencheLinha(biot.getButtonID(), String.valueOf(biot.getStatus().getValor()), "Status"));
            }
            txtHtmlCfg.append("</table>");
            pane.setContentType("text/html");
            pane.setText(txtHtmlCfg.toString());
            f.add(pane);
            totBiot.setText("Total botoes:" + configIOT.getButtonIOTSessao().size());
            f.add(totBiot);
        } catch (Exception e) {

        }

    }

    @SuppressWarnings({"unchecked", "deprecated"})
    public Status pegarStatus(Object obj){
        JComboBox<Status> cbF = (JComboBox<Status>)obj;
        return (Status) cbF.getSelectedItem();

    }

    public Integer tfTextToInteger(Object obj){
        JTextField jid = (JTextField)obj;
        return Integer.parseInt(jid.getText());    }

    public void gerarTelaConfig() {

        TableCellRenderer tableRenderer;
        tabela = new JTable(new JTableButtonModel());
        tabela.setRowHeight(20);
        tableRenderer = tabela.getDefaultRenderer(Object.class); // JButton.class);
        tabela.setDefaultRenderer(/* JButton.class */Object.class, new JTableButtonRenderer(tableRenderer));
        tabela.addMouseListener(new JTableMouseListener(tabela));
        tabela.setDefaultEditor(Object.class,new CellEditor());

        JLabel lbssid = new JLabel("ssid:");
        JLabel lbssidpassw = new JLabel("ssidPwd:");
        f.setSize(500, 500);
        f.setLayout(new BorderLayout());
        Dimension ds = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension dw = f.getSize();
        f.setLocation((ds.width - dw.width) / 2, (ds.height - dw.height) / 2);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setResizable(false);
        f.setTitle("Configuração IOT by Neuverse(v1.2.100_a)");
        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(f.getContentPane());
        f.getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(0, 400, Short.MAX_VALUE));
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(0, 300, Short.MAX_VALUE));

        tabela.addMouseListener(null);
        JScrollPane barraRolagem = new JScrollPane(tabela);
        lbssid.setBounds(20, 25, 70, 20);
        tfssid.setBounds(100, 25, 100, 20);
        lbssidpassw.setBounds(20, 50, 70, 20);
        tfssidpassw.setBounds(100, 50, 100, 20);
        barraRolagem.setBounds(20, 150, 460, 150);
        acBt.setBounds(200, 301, 100, 20);
        pane.setBounds(50, 180, 300, 200);
        gravar.setBounds(150, 450, 100, 20);
        ler.setBounds(250, 450, 100, 20);
        totBiot.setBounds(50, 150, 200, 20);


        JComboBox cb = new JComboBox(Status.listDescricao().toArray());

cb.setBounds(300, 0, 100, 20);

        f.add(cb);
        f.add(lbssid);
        f.add(tfssid);
        f.add(lbssidpassw);
        f.add(tfssidpassw);
        f.add(gravar);
        f.add(ler);
        f.add(totBiot);
        f.add(barraRolagem);
        f.add(acBt);
        f.setVisible(true);

        gravar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                configIOT = null;
                configIOT = new ConfigIOT();
                configIOT.getSsidSessao().setSsid(tfssid.getText());
                configIOT.getSsidSessao().setPassword(tfssidpassw.getText());
                JTableButtonModel modelo = (JTableButtonModel)tabela.getModel();
                for(Object obj[] : modelo.getRows()){
                    JTextField nick = (JTextField)obj[3];                                      
                    ButtonIot bIot = gerarConfBt(
                        tfTextToInteger(obj[0]),
                        tfTextToInteger(obj[1]),
                        tfTextToInteger(obj[2]),
                        nick.getText(),
                        pegarStatus(obj[4]),
                        pegarStatus(obj[5]),
                        pegarStatus(obj[6]));
                    configIOT.getButtonIOTSessao().add(bIot);
                }               
                setConfig();
            }
        });

        acBt.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JTableButtonModel modelo = (JTableButtonModel) tabela.getModel();
                modelo.newRecord();
                modelo.fireTableDataChanged();

            }
        });

        ler.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // retConfig();

                // tfssid.setText((String)tableModel.getValueAt(0, 0));
            }
        });
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        
    }

    public void networkPing() throws IOException {

        InetAddress localhost = InetAddress.getLocalHost();
        // uso de IPv4 e assumido!
        byte[] ip = { (byte) 192, (byte) 168, 10, 1 };

        for (int i = 1; i <= 254; i++) {
            ip[3] = (byte) i;
            InetAddress address = InetAddress.getByAddress(ip);
            if (address.isReachable(10)) {
                info(address.toString());
            }
            // else if (!address.getHostAddress().equals(address.getHostName())) {
            // System.out.println(address + " maquina reconhecida por um DNSLookup");
            // }
            // else {
            // System.out.println(address + " o endereço de host e o nome do host são
            // iguais, o host name não pode ser resolvido.");
            // }
        }
    }

}

/*
 * HTMLDocument doc = (HTMLDocument) pane.getDocument();
 * Element e = doc.getElement("niot");
 * AttributeSet as = e.getAttributes();
 * String s = (String) as.getAttribute(Attribute.VALUE);
 * for (ButtonIot biot : configIOT.getButtonIOTSessao()){
 * Object o = as.getAttribute(Attribute.VALUE);
 * //o = (Object) biot.getNomeGpio();
 * System.out.println(o);
 * break;
 * 
 * 
 * 
 * 
 * 
 * //Get the ref of foo element
 * //Element ele=doc.getElement("foo");
 * //ListView view=new ListView(ele);
 * //System.out.println(ele.getElementCount());
 * //try{
 * // doc.insertBeforeEnd(ele.getElement(0), "<ul><li>Test");
 * // }catch(Exception ex){}
 * 
 */

/*
 * configIOT.getSsidSessao().setSsid("neuverseServidorIot");
 * configIOT.getSsidSessao().setPassword(" nileindrei");
 * configIOT.getServidorSessao().setEndereco("192.168.10.1");
 * configIOT.getServidorSessao().setPorta(27015);
 * jSon = gson.toJson(configIOT);
 * 
 * com.setAcao("setConfig");
 * com.setConteudo(jSon);
 * jSon = gson.toJson(com);
 * com = cr.sendRest(jSon);
 * 
 * valor.setText(com.getResultado());
 * 
 * Element e = doc.getElement("niot0");
 * 
 * AttributeSet as = e.getAttributes();
 * String s = (String) as.getAttribute(Attribute.VALUE);
 * 
 * Object o = as.getAttribute(Attribute.VALUE);
 * //o = (Object) biot.getNomeGpio();
 * System.out.println(o);
 * break;
 * 
 * 
 */
class JTableButtonRenderer  implements TableCellRenderer {
    private TableCellRenderer defaultRenderer;
    public JTableButtonRenderer(TableCellRenderer renderer) {
        defaultRenderer = renderer;
    }
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
            int row, int column) {
        if (value instanceof Component)
            return (Component) value;
        return defaultRenderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
    }
}



