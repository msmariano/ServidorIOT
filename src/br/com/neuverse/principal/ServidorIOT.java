package br.com.neuverse.principal;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.net.InetSocketAddress;
import java.net.URI;
import java.security.KeyStore;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.TrustManagerFactory;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MqttDefaultFilePersistence;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpsServer;
import com.sun.net.httpserver.HttpsConfigurator;
import com.sun.net.httpserver.HttpsParameters;

import br.com.neuverse.database.Configuracao;
import br.com.neuverse.entity.Dispositivo;
import br.com.neuverse.entity.InterGpioBananaPi;
import br.com.neuverse.entity.InterfGpioRaspPI;
import br.com.neuverse.entity.Parametro;
import br.com.neuverse.entity.Pool;
import br.com.neuverse.entity.Versao;
import de.pi3g.pi.oled.Font;
import de.pi3g.pi.oled.OLEDDisplay;

////openssl s_client -connect 73cd8514e7c447ff91d697b4b02f88c5.s1.eu.hivemq.cloud:8883 -showcerts < /dev/null 2> /dev/null | sed -n '/BEGIN/,/END/p' > server.pem
//openssl x509 -outform der -in server.pem -out ca.crt
//sudo keytool -import -noprompt -trustcacerts -alias ca -file ca.crt -keystore /usr/lib/jvm/jdk-8-oracle-arm32-vfp-hflt/jre/lib/security/cacerts -storepass changeit
//sudo pppd updetach noauth silent nodeflate pty "/usr/bin/ssh msmariano@2001:1284:f509:b00a:8cb6:3e9a:1332:5e76 /usr/sbin/pppd nodetach notty noauth" ipparam vpn 192.168.0.250:192.168.18.250

public class ServidorIOT implements HttpHandler, MqttCallbackExtended ,IMqttMessageListener/*,Runnable */{

    Pool pool = new Pool();
    List<Pool> conectores = new ArrayList<>();
    private HttpsServer httpServer;
    Type TypeListPool = new TypeToken<ArrayList<Pool>>() {}.getType();
    Type TypeListDisp = new TypeToken<ArrayList<Dispositivo>>() {}.getType();
    private String linhasDisplay [] = new String[5];
    private MqttClient mqttClient;
    private MqttConnectOptions mqttOptions;
    boolean aliveOk = true;
    Object obj = new Object();
    private Boolean isMonitorando = false;

    public static void main(String[] args) {
        if (args.length > 0) {
            if (args[0].equals("ver")) {
                System.out.print("ServidorIOT V00_00_51 11/10/2023.");
                return;
            }
            if (args[0].equals("uuid")) {
                UUID uniqueKey = UUID.randomUUID();
		        String idGerado = uniqueKey.toString();
                System.out.println(idGerado);
                return;
            }
        }
        new ServidorIOT();//.run();
    }

    public ServidorIOT() {
        //System.setProperty("javax.net.ssl.trustStore", "/usr/lib/jvm/jdk-8-oracle-arm32-vfp-hflt/jre/lib/security/cacerts");
        //System.setProperty("javax.net.ssl.trustStorePassword", "changeit");
        // Inicio controle deste Servidor
        conectores.add(pool);
        try {

            mqttOptions = new MqttConnectOptions();
            mqttOptions.setMaxInflight(200);
            mqttOptions.setKeepAliveInterval(10);
            mqttOptions.setAutomaticReconnect(true);
            mqttOptions.setCleanSession(true);
            mqttOptions.setSSLHostnameVerifier(null);
            mqttOptions.setUserName("neuverse");
            mqttOptions.setPassword("M@r040370".toCharArray());
           try {
                mqttClient = new MqttClient("ssl://f897f821.ala.us-east-1.emqxsl.com:8883", pool.getId(), new MqttDefaultFilePersistence(System.getProperty("java.io.tmpdir")));
                mqttClient.setCallback(this);
                mqttClient.connect(mqttOptions);
            } catch (MqttException ex) {
                Log.log(this,"Erro ao se conectar ao broker mqtt "+ " - " + ex,"ERROR");
            }

            Configuracao cfg = new Configuracao();
            String nomeServidor = cfg.getNomeServidor();
            pool.setNick(nomeServidor);
            List<Parametro> listaBtnGpio = cfg.retornaBtnGpio();
            System.out.println("Carregando configuracao Gpio...");
            if (cfg.getTipoServidor().equals(1)) {
                for (Parametro btnGpio : listaBtnGpio) {
                    InterfGpioRaspPI iGpioRaspPI = new InterfGpioRaspPI(btnGpio.getC1(), btnGpio.getC2(),
                            btnGpio.getC3(),
                            btnGpio.getC4(), btnGpio.getC8(),btnGpio.getC5(),pool.getId());
                    pool.getDispositivos().add(iGpioRaspPI);
                    iGpioRaspPI.setMqttClient(mqttClient);
                }
            }
            else if (cfg.getTipoServidor().equals(2)) {
                carregaGpioButtonsBanana(pool.getId());
            }
            if (cfg.getControlePiscina()) {
                Log.log(this, "Iniciando Controle Piscina", "DEBUG");                
                ControlePiscina controlePiscina = new ControlePiscina();
                controlePiscina.setLinhasDisplay(linhasDisplay);
                controlePiscina.inicializar();
                new Thread() {
                    @Override
                    public void run() {
                        OLEDDisplay display;                       
                        try {
                            for (int i = 0; i < linhasDisplay.length; i++) {
                                linhasDisplay[i] = "";
                            }
                            Versao v = new Versao();
                            display = new OLEDDisplay(0, 60);
                            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                            while (true) {
                                Thread.sleep(1000);
                                display.clear();
                                display.drawString("Neuverse Tecnologia. " + v.getVersao(), Font.FONT_5X8, 0, 0, true);
                                display.drawString("ServidorIOT " + v.getVersao(), Font.FONT_5X8, 0, 10, true);
                                display.drawString(sdf.format(new Date()), Font.FONT_5X8, 0, 20, true);
                                display.drawString(linhasDisplay[0], Font.FONT_5X8, 0, 30, true);
                                display.drawString(linhasDisplay[1], Font.FONT_5X8, 0, 40, true);
                                display.drawString(linhasDisplay[2], Font.FONT_5X8, 0, 50, true);
                                display.update();
                            }
                        } catch (Exception e) {
                            System.out.println(e.getMessage());
                        }
                    }
                }.start();
            }
            System.out.println("Carregando Servidor Rest...");
            httpServer = HttpsServer.create(new InetSocketAddress(27016), 0);
            SSLContext sslContext = SSLContext.getInstance("TLS");
            char[] password = "password".toCharArray();
            KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
            FileInputStream fis = new FileInputStream("/home/pi/Desktop/servidoriothttps.jks");
            ks.load(fis, password);
            KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
            kmf.init(ks, password);
            TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
            tmf.init(ks);
            sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
            httpServer.setHttpsConfigurator(new HttpsConfigurator(sslContext) {
                public void configure(HttpsParameters params) {
                    try {
                        // initialise the SSL context
                        SSLContext context = getSSLContext();
                        SSLEngine engine = context.createSSLEngine();
                        params.setNeedClientAuth(false);
                        params.setCipherSuites(engine.getEnabledCipherSuites());
                        params.setProtocols(engine.getEnabledProtocols());
                        // Set the SSL parameters
                        SSLParameters sslParameters = context.getSupportedSSLParameters();
                        params.setSSLParameters(sslParameters);

                    } catch (Exception ex) {
                        System.out.println("Failed to create HTTPS port");
                    }
                }
            });
            httpServer.createContext("/ServidorIOT", this);
            ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(10);
            httpServer.setExecutor(threadPoolExecutor);
            this.httpServer.start();

            
           
        } catch (Exception e) {
            System.out.println("Erro ao instanciar ServidorIOT:" + e.getMessage());
        }
    }

    public void carregaGpioButtonsBanana(String idPool) {
        try {
            Configuracao cfg = new Configuracao();
            List<Parametro> listaBtnGpio = cfg.retornaBtnGpio();
            for (Parametro btnGpio : listaBtnGpio) {
                try {
                    InterGpioBananaPi bgrpi = new InterGpioBananaPi(btnGpio.getC1(), btnGpio.getC2(), btnGpio.getC3(),
                            btnGpio.getC4(), btnGpio.getC9(), btnGpio.getC10(), btnGpio.getC8(), idPool,btnGpio.getC5());
                    bgrpi.setMqttClient(mqttClient);
                    if (btnGpio.getC1() > -1 || btnGpio.getC2() > -1) {
                        pool.getDispositivos().add(bgrpi);

                    }
                } catch (Exception e) {
                    Log.log(this, "Inicializa carregaGpioButtonsBanana :" + e.getMessage(), "DEBUG");
                }
            }

        } catch (Exception e) {
            Log.log(this, "carregaGpioButtons() :" + e.getMessage(), "DEBUG");
        }
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        BufferedReader br = null;
        String inputRequest = null;
        StringBuilder requestContent = new StringBuilder();
        if (exchange.getRequestBody() != null) {
            br = new BufferedReader(new InputStreamReader(exchange.getRequestBody()));
            if (br != null) {
                while ((inputRequest = br.readLine()) != null) {
                    requestContent.append(inputRequest);
                }
            }
        }
        if (exchange.getRequestMethod().toUpperCase().equals("POST")) {
            URI uri = exchange.getRequestURI();
            exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
            try {
                if (uri.getPath().equals("/ServidorIOT/listar")) {
                    Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
                     for(Dispositivo dsp : pool.getDispositivos()){
                        if(dsp.getId().equals(6)){
                            System.out.println();
                        }
                    }
                    send(200, gson.toJson(conectores), exchange);
                } else if (uri.getPath().equals("/ServidorIOT/atualizar")) {
                    Gson gson = new GsonBuilder().create();
                    List<Pool> poolsAtualizar = gson.fromJson(requestContent.toString(), TypeListPool);
                    for (Pool poolAtualizar : poolsAtualizar) {
                        for (Pool pool : conectores) {
                            if (pool.getId().equals(poolAtualizar.getId())) {
                                for (Dispositivo dispositivoAtualizar : poolAtualizar.getDispositivos()) {
                                    Dispositivo dispositivo = pool.buscar(dispositivoAtualizar.getId());
                                    if (dispositivo != null)
                                        dispositivo.updateStatus(dispositivoAtualizar.getStatus());
                                }
                                break;
                            }
                        }
                    }
                    send(200, "", exchange);

                } else if (uri.getPath().equals("/ServidorIOT/info")) {
                    send(200, "{\"Servico\":\"ServidorIOT\"}", exchange);
                } else {
                    send(404, "", exchange);
                }
            } catch (Exception e) {
                send(500, e.getMessage(), exchange);
            }
        }
    }

    public void send(Integer code, String body, HttpExchange exchange) throws IOException {
        byte[] bs = body.getBytes("UTF-8");
        exchange.sendResponseHeaders(code, bs.length);
        OutputStream os = exchange.getResponseBody();
        os.write(bs);
        os.flush();
        os.close();
    }

    public String[] getLinhasDisplay() {
        return linhasDisplay;
    }

    public void setLinhasDisplay(String[] linhasDisplay) {
        this.linhasDisplay = linhasDisplay;
    }

    @Override
    public void connectionLost(Throwable cause) {
        Log.log(this, "mqttClient connectionLost", "SALVARDIRETO");
        try {
            //mqttClient.reconnect();
        } catch (Exception e) {
            Log.log(this, "mqttClient reconnect():" + e.getMessage(), "SALVARDIRETO");
        }
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        Log.log(this, "messageArrivedMQTT["+topic+"]:"+new String(message.getPayload()), "SALVARDIRETO");
        if (topic.equals("br/com/neuverse/geral/info")) {
            Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
            new Thread() {
                @Override
			    public void run() {
                    try {
                        mqttClient.publish("br/com/neuverse/geral/lista", gson.toJson(conectores).getBytes(), 0, false);
                    } catch (MqttException e) {
                    }
                }
            }.start();
        }
        else if (topic.equals("br/com/neuverse/servidores/" + pool.getId() + "/atualizar")) {
            new Thread() {
                @Override
			    public void run() {
                    try {
                        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
                        List<Pool> poolsAtualizar = gson.fromJson(new String(message.getPayload()), TypeListPool);
                        for (Pool poolAtualizar : poolsAtualizar) {
                            for (Pool pool : conectores) {
                                if (pool.getId().equals(poolAtualizar.getId())) {
                                    for (Dispositivo dispositivoAtualizar : poolAtualizar.getDispositivos()) {
                                        Dispositivo dispositivo = pool.buscar(dispositivoAtualizar.getId());
                                        if (dispositivo != null)
                                            dispositivo.updateStatus(dispositivoAtualizar.getStatus());
                                    }
                                    break;
                                }
                            }
                        }
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                }
            }.start();            
        }
        else if (topic.equals("br/com/neuverse/servidores/" + pool.getId() + "/alive")) {
            
                //aliveOk = true;
                //obj.notifyAll();
                
        }

    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
    }

    @Override
    public void connectComplete(boolean reconnect, String serverURI) {
        subscribe(0, this,"br/com/neuverse/servidores/" +pool.getId()+ "/#","br/com/neuverse/geral/info");
        if(!isMonitorando){
            monitora();
            isMonitorando = true;
        }
        Log.log(this,"mqttClient conectado","SALVARDIRETO");
       
    }

    public IMqttToken subscribe(int qos, IMqttMessageListener gestorMensagemMQTT, String... topicos) {
        if (mqttClient == null || topicos.length == 0) {
            return null;
        }
        for(String s : topicos){
             Log.log(this,"Subscribe:"+s,"DEBUG");
        }       

        int tamanho = topicos.length;
        int[] qoss = new int[tamanho];
        IMqttMessageListener[] listners = new IMqttMessageListener[tamanho];

        for (int i = 0; i < tamanho; i++) {
            qoss[i] = qos;
            listners[i] = gestorMensagemMQTT;
        }
        try {
            return mqttClient.subscribeWithResponse(topicos, qoss, listners);
        } catch (MqttException ex) {
            return null;
        }
    }

    void mqttClientConectar() {
        try {
            if (mqttClient != null) {
                try {
                    mqttClient.disconnect();
                    mqttClient.close();
                    mqttClient = null;
                } catch (Exception e) {
                    Log.log(this, "mqttClientConectar()_1:" + e.getMessage(), "SALVARDIRETO");
                }

            }
            mqttClient = new MqttClient("ssl://f897f821.ala.us-east-1.emqxsl.com:8883", pool.getId(),
                    new MqttDefaultFilePersistence(System.getProperty("java.io.tmpdir")));
            mqttClient.setCallback(this);
            mqttClient.connect(mqttOptions);
            for(Dispositivo dsp : pool.getDispositivos()){
                dsp.setMqttClient(mqttClient);
            }
            Log.log(this, "Conectando mqttClient", "SALVARDIRETO");
        } catch (Exception e) {
            Log.log(this, "mqttClientConectar()_2:" + e.getMessage(), "SALVARDIRETO");
        }

    }

    void monitora(){
        new Thread() {
            @Override
            public void run() {
                while(true){
                    try {
                        Thread.sleep(10000);
                        if(mqttClient.isConnected())
                            mqttClient.publish("br/com/neuverse/servidores/" + pool.getId() + "/alive","alive".getBytes(),
                                0,false);
                    } catch (Exception e) {
                    }
                }
            }
        }.start();
    }

   /* @Override
    public void run() {
        /*while(true){            
            try{
                if(mqttClient==null)
                    mqttClientConectar();
                Thread.sleep(1000*60);
                aliveOk = false;
                if(mqttClient.isConnected())
                    mqttClient.publish("br/com/neuverse/servidores/" + pool.getId() + "/alive","alive".getBytes(),0,false);
               

                obj.wait(5000);


                if(!aliveOk){
                   mqttClientConectar();
                }
            }
            catch(Exception e){
                Log.log(this,"ServidorIOT.run():"+e.getMessage(),"SALVARDIRETO");
            }
        }
    }*/
}
