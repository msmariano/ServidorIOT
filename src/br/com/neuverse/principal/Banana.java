package br.com.neuverse.principal;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

import br.com.neuverse.entity.ButtonGpioBananaPi;
import br.com.neuverse.entity.Conector;

public class Banana {

    // private Socket socket;
    private String horarios = "00:00;04:00;08:00;12:00;16:00;20:00";
    private Integer minutos = 30;

    public static void main(String[] args) throws IOException {

        System.out.println("Iniciando...");

        if (args.length > 0) {
            BufferedWriter gpio = null;
            try {
                gpio = new BufferedWriter(new FileWriter("/sys/class/gpio/export", false));
                gpio.write(String.valueOf(17));
                gpio.close();
            } catch (Exception e) {
                System.err.println("Export:" + e.getMessage());
            }

            if (args[0].equals("1")) {
                System.out.println("Escrevendo 1");
                gpio = new BufferedWriter(new FileWriter("/sys/class/gpio/gpio17/direction", false));
                gpio.write("out");
                gpio.close();
                gpio = new BufferedWriter(new FileWriter("/sys/class/gpio/gpio17/value", false));
                gpio.write("1");
                gpio.close();
                return;
            } else if (args[0].equals("0")) {
                System.out.println("Escrevendo 0");
                gpio = new BufferedWriter(new FileWriter("/sys/class/gpio/gpio17/direction", false));
                gpio.write("out");
                gpio.close();
                gpio = new BufferedWriter(new FileWriter("/sys/class/gpio/gpio17/value", false));
                gpio.write("0");
                gpio.close();
                return;
            } else if (args[0].equals("timer")) {
                gpio = new BufferedWriter(new FileWriter("/sys/class/gpio/gpio17/direction", false));
                gpio.write("out");
                gpio.close();
                gpio = new BufferedWriter(new FileWriter("/sys/class/gpio/gpio17/value", false));
                gpio.write("0");
                gpio.close();
                String par = null;
                Integer min = null;
                if (args.length > 1)
                    par = args[1];
                if (args.length > 2)
                    min = Integer.parseInt(args[2]);
                new Banana().timer(par,min);
            }

        } else {

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
                        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
                        while (true) {
                            System.out.println(sdf.format(new Date()));
                            gpio = new BufferedWriter(new FileWriter("/sys/class/gpio/gpio17/value", false));
                            gpio.write("1");
                            gpio.close();
                            Thread.sleep(1000);
                            gpio = new BufferedWriter(new FileWriter("/sys/class/gpio/gpio17/value", false));
                            gpio.write("0");
                            gpio.close();
                            Thread.sleep(1000);
                            System.err.println(".");
                        }
                    } catch (Exception e) {
                        System.err.println(e.getMessage());
                    }
                }
            }.start();
        }

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

    public void timer(String hor,Integer min) {

        if (hor != null) {
            horarios = hor;
        }
        if (min != null) {
            minutos = min;
        }
        System.out.println("Timer Ativado para os horários:" + horarios+" por "+minutos+" minuto(s)");

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
                        if (horarios.contains(t)) {
                            gpio = new BufferedWriter(new FileWriter("/sys/class/gpio/gpio17/value", false));
                            gpio.write("1");
                            gpio.close();
                            System.out.println("Saida Ativada");
                            Thread.sleep(1000 * 60 * minutos);
                            gpio = new BufferedWriter(new FileWriter("/sys/class/gpio/gpio17/value", false));
                            gpio.write("0");
                            gpio.close();
                            System.out.println("Saida Desativada");
                            Thread.sleep(1000);
                            System.err.println(".");
                        }
                    }
                } catch (Exception e) {
                    System.err.println(e.getMessage());
                }
            }
        }.start();
    }

}
