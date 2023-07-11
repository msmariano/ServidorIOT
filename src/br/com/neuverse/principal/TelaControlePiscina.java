package br.com.neuverse.principal;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.ws.rs.HttpMethod;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.TextArea;

import br.com.neuverse.entity.Comando;
import br.com.neuverse.entity.Conector;
import br.com.neuverse.enumerador.ComEnum;
import br.com.neuverse.enumerador.Status;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;

public class TelaControlePiscina {

    static {
        disableSslVerification();
    }

    private static void disableSslVerification() {
        try {
            // Create a trust manager that does not validate certificate chains
            TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                }

                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                }
            }
            };

            // Install the all-trusting trust manager
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

            // Create all-trusting host name verifier
            HostnameVerifier allHostsValid = new HostnameVerifier() {
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            };

            // Install the all-trusting host verifier
            HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }
    }

    // private Socket socket;
    private String horarios = "00:00;04:00;08:00;12:00;16:00;20:00";
    private Integer minutos = 30;
    final static private String bomba = "10";
    final static private String filtro = "17";
    final static private String chave = "9";
    final static private String nivelAlto = "20";
    final static private String nivelBaixo = "21";
    final static private String on = "1";
    final static private String off = "0";
    static private boolean onByKey = false;
    Comando c = new Comando();

    TextArea txt;
    JFrame f = new JFrame();
    JButton btBomba = new JButton("Filtro");
    JButton btBombaDreno = new JButton("Dreno");
    JButton crono = new JButton("");

    JButton btAgua = new JButton("Água");
    JButton btlimite = new JButton("Lim.Crítico");
    JPanel statusConfig = new JPanel();
    public JLabel statusTxt = new JLabel("Parado.");

    boolean pushLogic = false;
    boolean pushLogicDreno = false;

    @SuppressWarnings("deprecation")
    public static void main(String[] args) throws IOException {

        if (args.length > 0) {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            new TelaControlePiscina().iniciarTela();
            return;

        }
        Runtime.getRuntime().exec("java -jar "
                + (new File(TelaControlePiscina.class.getProtectionDomain().getCodeSource().getLocation().getPath()))
                        .getAbsolutePath()
                + " cmd");

    }

    public void iniciarTela() {

        btBomba.setContentAreaFilled(false);
        btBomba.setOpaque(true);
        btBomba.setBackground(Color.GRAY);

        btBombaDreno.setContentAreaFilled(false);
        btBombaDreno.setOpaque(true);
        btBombaDreno.setBackground(Color.GRAY);

        btAgua.setContentAreaFilled(false);
        btAgua.setOpaque(true);
        btAgua.setBackground(Color.GRAY);

        btlimite.setContentAreaFilled(false);
        btlimite.setOpaque(true);
        btlimite.setBackground(Color.GRAY);
        btlimite.setVisible(false);

        txt = new TextArea(new SimpleDateFormat("HH:mm:ss").format(new Date()) + " Iniciando", 2, 30);
        f.setSize(360, 200);
        f.setLayout(new BorderLayout());
        Dimension ds = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension dw = f.getSize();
        f.setLocation((ds.width - dw.width) / 2, (ds.height - dw.height) / 2);
        f.setResizable(false);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setTitle("Neuverse");
        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(f.getContentPane());
        f.getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(0, 400, Short.MAX_VALUE));
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(0, 300, Short.MAX_VALUE));

        statusConfig.setBounds(0, 465, 500, 20);
        statusConfig.setVisible(true);
        statusConfig.setBackground(Color.GRAY);
        statusConfig.setBorder(BorderFactory.createLineBorder(Color.orange, 2));
        statusConfig.add(statusTxt);

        btBomba.setBounds(0, 0, 100, 20);
        btBombaDreno.setBounds(0, 30, 100, 20);
        crono.setBounds(0, 60, 30, 30);
        crono.setVisible(false);

        btBombaDreno.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!pushLogicDreno) {
                    ligarDreno(0);
                    pushLogicDreno = true;

                } else if (pushLogicDreno) {
                    ligarDreno(1);
                    pushLogicDreno = false;
                }
            }
        });

        txt.setBounds(0, 70, 400, 300);

        btAgua.setBounds(110, 0, 100, 20);
        btlimite.setBounds(230, 0, 100, 20);
        btAgua.setEnabled(false);
        btlimite.setEnabled(false);
        btBomba.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!pushLogic) {
                    ligarBombaFiltro(1);
                    onByKey = true;
                    pushLogic = true;

                } else if (pushLogic) {
                    ligarBombaFiltro(0);
                    onByKey = false;
                    pushLogic = false;
                }
            }
        });
        f.add(btBomba);
        f.add(btBombaDreno);
        f.add(btAgua);
        f.add(btlimite);
        f.add(crono);
        // f.add(statusConfig);
        // f.add(txt);
        f.setVisible(true);

        monitoraChave();

    }

    public void iniciarConexao() throws Exception {
        SSLContext sslContext = buildSslContext(new FileInputStream("/home/pi/Desktop/servidoriotsslpradovelho.pem"));
        SSLSocketFactory factory = sslContext.getSocketFactory();
        SSLSocket socket = (SSLSocket) factory.createSocket("192.168.10.254", 27015);
        socket.startHandshake();
    }

    public void logarServidor() {

        Conector con = new Conector();

    }

    public static void escreverPin(String pin, String valor) {

        try {
            BufferedWriter gpio = new BufferedWriter(new FileWriter("/sys/class/gpio/gpio" + pin + "/value", false));
            gpio.write(valor);
            gpio.close();
        } catch (Exception e) {

        }
    }

    public static String lerPin(String pin) {
        String ret = null;

        try {
            BufferedReader gpio = new BufferedReader(new FileReader("/sys/class/gpio/gpio" + pin + "/value"));
            ret = String.valueOf(String.format("%c", gpio.read()));
            gpio.close();
        } catch (Exception e) {

        }
        return ret;
    }

    public void ativarPin(String pin, String direcao) {

        try {
            BufferedWriter gpio = null;
            try {
                gpio = new BufferedWriter(new FileWriter("/sys/class/gpio/export", false));
                gpio.write(pin);
                gpio.close();
            } catch (Exception e) {
            }

            System.out.println("Ativando pin " + pin + " para " + direcao);
            gpio = new BufferedWriter(new FileWriter("/sys/class/gpio/gpio" + pin + "/direction", false));
            gpio.write(direcao);
            gpio.close();
        } catch (Exception e) {

        }

    }

    public static SSLContext buildSslContext(InputStream... inputStreams) throws Exception {
        X509Certificate cert;
        KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
        trustStore.load(null);

        for (InputStream inputStream : inputStreams) {
            try {
                CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
                cert = (X509Certificate) certificateFactory.generateCertificate(inputStream);
            } finally {
                inputStream.close();
            }
            String alias = cert.getSubjectX500Principal().getName();
            trustStore.setCertificateEntry(alias, cert);
        }

        TrustManagerFactory tmf = TrustManagerFactory.getInstance("X509");
        tmf.init(trustStore);
        TrustManager[] trustManagers = tmf.getTrustManagers();
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, trustManagers, null);

        return sslContext;
    }

    public void alteraBt(Color c, JButton jbt) {
        jbt.setContentAreaFilled(false);
        jbt.setOpaque(true);
        jbt.setBackground(c);
    }

    public void ligarBombaFiltro(Integer acao) {
        switch (acao) {
            case 0:
                escreverPin(filtro, off);
                alteraBt(Color.GREEN, btBomba);
                break;
            case 1:
                escreverPin(filtro, on);
                alteraBt(Color.RED, btBomba);
                break;
        }
    }

    public void ligarDreno(Integer acao) {
        switch (acao) {
            case 0:
                escreverPin(bomba, off);
                alteraBt(Color.RED, btBombaDreno);
                break;
            case 1:
                escreverPin(bomba, on);
                alteraBt(Color.GREEN, btBombaDreno);
                break;
        }
    }

    public void timer(String hor, Integer min) {

        if (hor != null) {
            horarios = hor;
        }
        if (min != null) {
            minutos = min;
        }
        System.out.println("Timer Ativado para os horários:" + horarios + " por " + minutos + " minuto(s)");

        new Thread() {
            @Override
            public void run() {
                try {
                    BufferedWriter gpio = null;
                    try {
                        gpio = new BufferedWriter(new FileWriter("/sys/class/gpio/export", false));
                        gpio.write(String.valueOf(17));
                        gpio.close();
                    } catch (Exception e) {
                        System.err.println("Export:" + e.getMessage());
                    }
                    gpio = new BufferedWriter(new FileWriter("/sys/class/gpio/gpio17/direction", false));
                    gpio.write("out");
                    gpio.close();
                    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                    System.out.println(sdf.format(new Date()));
                    while (true) {
                        String t = sdf.format(new Date());
                        Thread.sleep(1000);
                        statusTxt.setText(new SimpleDateFormat("HH:mm:ss").format(new Date()));
                        if (horarios.contains(t)) {
                            String text = txt.getText();
                            txt.setText(text + "\r\n" + new SimpleDateFormat("HH:mm:ss").format(new Date())
                                    + " Ativando timer");
                            ligarBombaFiltro(1);
                            Thread.sleep(1000 * 60 * minutos);

                            if (!onByKey) {
                                ligarBombaFiltro(0);
                            }
                        }
                    }
                } catch (Exception e) {
                    System.err.println(e.getMessage());
                }
            }
        }.start();
    }

    public void monitoraAgua() {
        new Thread() {
            @Override
            public void run() {
                boolean alerta = false;
                boolean maximo = false;

                while (true) {
                    // System.out.println(lerPin("20"));
                    // System.out.println(lerPin("21"));
                    if (lerPin(nivelBaixo).equals("1")) {
                        if (!alerta) {
                            alerta = true;
                            String text = txt.getText();
                            txt.setText(text + "\r\n" + new SimpleDateFormat("HH:mm:ss").format(new Date())
                                    + " Alerta para presença de agua na caixa!!!");
                            btAgua.setContentAreaFilled(false);
                            btAgua.setOpaque(true);
                            btAgua.setBackground(Color.YELLOW);
                        }

                    } else {
                        if (alerta) {
                            String text = txt.getText();
                            txt.setText(text + "\r\n" + new SimpleDateFormat("HH:mm:ss").format(new Date())
                                    + " Caixa esvaziada!!!\r\nDesligando bombeamento!!!");
                            ligarDreno(1);
                            btAgua.setContentAreaFilled(false);
                            btAgua.setOpaque(true);
                            btAgua.setBackground(Color.GREEN);
                        }
                        alerta = false;
                    }
                    if (lerPin(nivelAlto).equals("1")) {
                        if (!maximo) {
                            maximo = true;
                            String text = txt.getText();
                            txt.setText(text + "\r\n" + new SimpleDateFormat("HH:mm:ss").format(new Date())
                                    + " Ligando bombeamento!!!\r\n");
                            System.out.println("");
                            ligarDreno(0);
                        }

                    } else {
                        maximo = false;
                    }

                    try {
                        Thread.sleep(1000);
                    } catch (Exception e) {

                    }
                }
            }
        }.start();
    }

    public void monitoraNivel() {
        new Thread() {
            boolean inverter = false;

            @Override
            public void run() {
                while (true) {
                    if (lerPin(nivelAlto).equals("1")) {
                        if (!inverter) {
                            btlimite.setVisible(false);
                            inverter = true;
                        } else {
                            btlimite.setVisible(true);
                            inverter = false;
                        }
                    } else
                        btlimite.setVisible(false);

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {

                    }
                }
            }
        }.start();
    }

    public void monitoraChave() {

        new Thread() {
            @Override
            public void run() {
                ImageIcon ico = new javax.swing.ImageIcon(getClass().getResource("/Imagens/crono.png"));
                crono.setVisible(false);
                crono.setIcon(ico);

                while (true) {
                    if (c.getTimer() != null && c.getTimer().equals(Status.ON)) {
                        crono.setVisible(!crono.isVisible());
                    } else
                        crono.setVisible(false);

                    try {
                        Thread.sleep(1000);
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                }
            }
        }.start();

        new Thread() {
            @Override
            public void run() {

                while (true) {

                    c.setComando(ComEnum.LERSENSORES);
                    Gson gson = new GsonBuilder().setDateFormat("dd/MM/yyyy HH:mm:ss").create();
                    String jSon = gson.toJson(c);
                    try {
                        c = restCrtlPis(jSon);

                        if (c != null) {


                            if (c.getStFiltro().equals(Status.ON)) {
                                btBomba.setContentAreaFilled(false);
                                btBomba.setOpaque(true);
                                btBomba.setBackground(Color.RED);

                            } else if (c.getStFiltro().equals(Status.OFF)) {
                                btBomba.setContentAreaFilled(false);
                                btBomba.setOpaque(true);
                                btBomba.setBackground(Color.GREEN);

                            }

                            if (c.getNivel().equals(Status.NORMAL_AGUA)) {
                                btAgua.setContentAreaFilled(false);
                                btAgua.setOpaque(true);
                                btAgua.setBackground(Color.GREEN);

                            } else if (c.getNivel().equals(Status.PRESENCA_AGUA)) {
                                btAgua.setContentAreaFilled(false);
                                btAgua.setOpaque(true);
                                btAgua.setBackground(Color.YELLOW);

                            }

                            if(c.getStDreno().equals(Status.OFF)){
                                btBombaDreno.setContentAreaFilled(false);
                                btBombaDreno.setOpaque(true);
                                btBombaDreno.setBackground(Color.GREEN);
                            }
                            else if(c.getStDreno().equals(Status.ON)){
                                btBombaDreno.setContentAreaFilled(false);
                                btBombaDreno.setOpaque(true);
                                btBombaDreno.setBackground(Color.RED);
                            }

                        } else {
                            btBomba.setContentAreaFilled(false);
                            btBomba.setOpaque(true);
                            btBomba.setBackground(Color.GRAY);
                            btAgua.setContentAreaFilled(false);
                            btAgua.setOpaque(true);
                            btAgua.setBackground(Color.GRAY);
                            btBombaDreno.setContentAreaFilled(false);
                            btBombaDreno.setOpaque(true);
                            btBombaDreno.setBackground(Color.GRAY);
                        }
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                        btBomba.setContentAreaFilled(false);
                        btBomba.setOpaque(true);
                        btBomba.setBackground(Color.GRAY);
                        btAgua.setContentAreaFilled(false);
                        btAgua.setOpaque(true);
                        btAgua.setBackground(Color.GRAY);
                        btBombaDreno.setContentAreaFilled(false);
                        btBombaDreno.setOpaque(true);
                        btBombaDreno.setBackground(Color.GRAY);
                    }

                    try {
                        Thread.sleep(5000);
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                }
            }
        }.start();
    }

    public Comando restCrtlPis(String jSon) throws Exception {

        URL url = new URL("https://192.168.0.125:8080/ServidorIOT/controlePiscina");
        HttpsURLConnection con = (HttpsURLConnection) url.openConnection();

        con.setRequestMethod(HttpMethod.POST);
        con.setRequestProperty("Content-Type", "application/json");
        con.setRequestProperty("Host ", "192.168.0.125");
        con.setRequestProperty("Connection", "Keep-Alive");
        con.setRequestProperty("Content-Length", String.valueOf(jSon.length()));
        con.setRequestProperty("Accept", "*/*");
        con.setRequestProperty("Accept-Encoding", "gzip, deflate, br");
        con.setDoOutput(true);

        try (OutputStream os = con.getOutputStream()) {
            byte[] input = jSon.getBytes();
            os.write(input, 0, jSon.length());
        }
        int responseCode = con.getResponseCode();

        System.out.println("Response code:" + responseCode);

        if (responseCode == HttpsURLConnection.HTTP_OK) {
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                System.out.println(inputLine);
                response.append(inputLine);
            }
            in.close();

            String json = response.toString().substring(0, con.getContentLength());
            Gson gson = new GsonBuilder().setDateFormat("dd/MM/yyyy HH:mm:ss").create();
            Comando comando = gson.fromJson(json, Comando.class);
            return comando;
        }
        return null;
    }

}
