package br.com.neuverse.principal;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import br.com.neuverse.entity.Comando;
import br.com.neuverse.enumerador.ComEnum;
import br.com.neuverse.enumerador.Status;

public class ScktCom {

    private SSLSocket socket;
    private Sensor sensor;
    private boolean inverter = false;

    public ScktCom(String nomeSensor, boolean iv, String gp, String direcao) {
        sensor = new Sensor(gp, direcao,nomeSensor);
        inverter = iv;
        if(inverter)
            sensor.ligar(1);
        else
            sensor.ligar(0);

    }

    public boolean isInverter() {
        return inverter;
    }

    public void setInverter(boolean inverter) {
        this.inverter = inverter;
    }

    public void monitoraPin() {
        new Thread() {
            @Override
            public void run() {
                boolean acionada = false;
                Comando com = new Comando();
                Log.log(this, "Iniciando monitoraPin "+sensor.getGpio(), "DEBUG");
                while (true) {
                    if (sensor.lerPin(sensor.getGpio()).equals("1")) {
                        if (!acionada) {
                            acionada = true; 
                            if(!inverter)
                                com.setDevice(Status.ON); 
                            else
                                com.setDevice(Status.OFF);   
                            sendCom(com);                        
                        }
                    } else{
                        if(acionada){
                            acionada = false;
                            if(!inverter)
                                com.setDevice(Status.OFF);
                            else
                                com.setDevice(Status.ON); 
                            sendCom(com);  
                        }
                        
                        
                    }

                    try {
                        Thread.sleep(1000);
                    } catch (Exception e) {

                    }
                }
            }
        }.start();
    }

    public void inicializar() {
        new Thread() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(5000);
                        clienteServidorIot(false);
                        
                    } catch (Exception e) {
                        Log.log(this, "clienteServidorIot " + sensor.getNome() + e.getMessage(), "DEBUG");
                    }
                   
                }
            }
        }.start();
       
    }

    public void clienteServidorIot(boolean timeout)
            throws JsonSyntaxException, IOException, NoSuchAlgorithmException, KeyManagementException {

        Log.log(this, "Cliente conectando "+ sensor.getNome(), "DEBUG");

        TrustManager[] trustAllCerts = { new X509TrustManager() {
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            public void checkClientTrusted(X509Certificate[] certs, String authType) {
            }

            public void checkServerTrusted(X509Certificate[] certs, String authType) {
            }
        } };

        SSLContext sc = SSLContext.getInstance("SSL");
        sc.init(null, trustAllCerts, new SecureRandom());
        SSLSocketFactory factory = (SSLSocketFactory) sc.getSocketFactory();
        SSLSocketFactory.getDefault();
        socket = (SSLSocket) factory.createSocket("192.168.0.254", 27018);
        //if (timeout)
        //    socket.setSoTimeout(5000);
        socket.startHandshake();
        Log.log(this, "Cliente conectado "+ sensor.getNome(), "DEBUG");
        PrintWriter out = new PrintWriter(
                new BufferedWriter(
                        new OutputStreamWriter(
                                socket.getOutputStream())));

        Comando com = new Comando();
        if(sensor.lerPin(sensor.getGpio()).equals("0")){
            if(inverter)
                 com.setDevice(Status.ON);
            else
                 com.setDevice(Status.OFF);
        }
        else
        {
            if(inverter)
                 com.setDevice(Status.OFF);
            else
                 com.setDevice(Status.ON);

        }
       
        com.setNick(sensor.getNome());
        Gson gson = new GsonBuilder().setDateFormat("dd/MM/yyyy HH:mm:ss").create();
        String jSon = gson.toJson(com);
        out.println(jSon);
        out.flush();
        if (out.checkError()){
            Log.log(this,"SSLSocketClient:  java.io.PrintWriter error","DEBUG");
            return;
        }

        BufferedReader in = new BufferedReader(
                new InputStreamReader(
                        socket.getInputStream()));

        monitoraPin();

        String inputLine;
        while ((inputLine = in.readLine()) != null) {
            Log.log(this, "Mens: " + inputLine, "DEBUG");
            com = gson.fromJson(inputLine, Comando.class);

            if (com.isAlive()) {
                try {
                    com.setConectado(true);
                    String jSonConectado = gson.toJson(com);
                    out.println(jSonConectado);
                    out.flush();  
                    Log.log(this,"GpioRemoto Alive recv","DEBUG");                  
                } catch (Exception e) {
                }
                continue;
            }

            if (com.getComando().equals(ComEnum.LIGAR)) {
                if (!inverter)
                    sensor.ligar(1);
                else
                    sensor.ligar(0);
            } else if (com.getComando().equals(ComEnum.DESLIGAR)) {
                if (!inverter)
                    sensor.ligar(0);
                else
                    sensor.ligar(1);
            }
        }
        Log.log(this, "Cliente desconectado", "DEBUG");
        out.close();
        in.close();
        socket.close();
    }

    public void sendCom(Comando com) {
        try {
            PrintWriter out = new PrintWriter(
                    new BufferedWriter(
                            new OutputStreamWriter(
                                    socket.getOutputStream())));
            Gson gson = new GsonBuilder().setDateFormat("dd/MM/yyyy HH:mm:ss").create();
            String jSon = gson.toJson(com);
            out.println(jSon);
            out.flush();
        } catch (Exception e) {
        }

    }

}
