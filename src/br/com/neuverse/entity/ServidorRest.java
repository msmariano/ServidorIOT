package br.com.neuverse.entity;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.math.BigInteger;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import javax.crypto.SecretKey;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.TrustManagerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpsServer;
import com.sun.net.httpserver.HttpsConfigurator;
import com.sun.net.httpserver.HttpsParameters;


import br.com.neuverse.database.Usuario;
import br.com.neuverse.enumerador.Status;
import br.com.neuverse.principal.Log;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;




public class ServidorRest implements HttpHandler {
    private HttpsServer httpServer;
    private HttpServer servidorHttp;
    private List<Conector> listaConectores;
    private InfoServidor infoServidor;

    private final SecretKey CHAVE = Keys.hmacShaKeyFor(
		"7f-j&CKk=coNzZc0y7_4obMP?#TfcYq%fcD0mDpenW2nc!lfGoZ|d?f&RNbDHUX6"
		.getBytes(StandardCharsets.UTF_8));
  
    
    public ServidorRest(){
        /*try {
            httpServer = HttpsServer.create(new InetSocketAddress(8080), 0);
  
            
            /////////////////
            SSLContext sslContext = SSLContext.getInstance("TLS");
            // initialise the keystore
            char[] password = "password".toCharArray();
            KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
            FileInputStream fis = new FileInputStream("/home/pi/Desktop/servidoriothttps.jks");
            ks.load(fis, password);

            // setup the key manager factory
            KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
            kmf.init(ks, password);

            // setup the trust manager factory
            TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
            tmf.init(ks);

            // setup the HTTPS context and parameters
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


   ///////////////////////

            httpServer.createContext("/ServidorIOT", this);
            ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor)Executors.newFixedThreadPool(10);
            httpServer.setExecutor(threadPoolExecutor);
            this.httpServer.start();
        } catch (Exception e) {
            Log.log(this,e.getMessage(),"DEBUG");
        }*/
    }
    public ServidorRest(Boolean https,int porta){
        super();
        if(https) {
            try {                
                httpServer = HttpsServer.create(new InetSocketAddress(porta), 0);
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
                ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor)Executors.newFixedThreadPool(10);
                httpServer.setExecutor(threadPoolExecutor);
                this.httpServer.start();
            } catch (Exception e) {
                Log.log(this,e.getMessage(),"DEBUG");
            }
        }
        else{
            try {
                servidorHttp = HttpServer.create(new InetSocketAddress(porta), 0);
                servidorHttp.createContext("/ServidorIOT", this);
                ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor)Executors
                    .newFixedThreadPool(10);
                servidorHttp.setExecutor(threadPoolExecutor);
                this.servidorHttp.start();
            } catch (Exception e) {
                Log.log(this,"servidorHttp: "+e.getMessage(),"DEBUG");
            }

        }
    }

    public void monitoraConectores(ServidorRest sr){        
        new Thread() {
            @Override
            public void run() {
                Log.log(sr, "Iniciando monitoraConectores()","INFO");
                while (true) {
                    try {
                        for(Conector con :listaConectores){  
                            if(con != null){
                                if(con.getTimeStampAlive()!=null) {
                                    Date data = new Date();                          
                                    Long t = Long.parseLong(con.getTimeStampAlive());
                                    if((data.getTime() - t)>(300*1000)) {
                                        listaConectores.remove(con);
                                        Log.log(this, "Removendo "+con.getNome(), "DEBUG_N5");
                                        break;
                                    }
                                }
                            }   
                        }                        
                    }
                    catch(Exception e){
                        Log.log(this, "erro monitoraConectores "+e.getMessage(), "DEBUG_N5");
                    }
                    try{
                        Thread.sleep(5000);
                    }
                    catch(Exception e){
                        Log.log(this, "monitoraConectores time", "DEBUG");
                    }
                    
                }
            }
        }.start();
    }

    public InfoServidor getInfoServidor() {
        return infoServidor;
    }

    public void setInfoServidor(InfoServidor infoServidor) {
        this.infoServidor = infoServidor;
    }

    public List<Conector> getListaConectores() {
        return listaConectores;
    }

    public void setListaConectores(List<Conector> listaConectores) {
        this.listaConectores = listaConectores;
    }

    public void send(Integer code,String body,HttpExchange exchange) throws IOException{
        byte[] bs = body.getBytes("UTF-8");
        exchange.sendResponseHeaders(code, bs.length);
        OutputStream os = exchange.getResponseBody();
        os.write(bs);
        os.flush();
        os.close();
        Log.log(this,"Enviando code "+code,"INFO");
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Log.log(this,"Requisicao exchange:"+exchange.toString(),"INFO");
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

        if(exchange.getRequestMethod().toUpperCase().equals("POST")){
            URI uri = exchange.getRequestURI();

            if(uri.getPath().equals("/ServidorIOT/info")){
                exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
                Gson gson = new GsonBuilder().setDateFormat("dd/MM/yyyy HH:mm:ss").excludeFieldsWithoutExposeAnnotation().create();
                try{
                    String jSon = gson.toJson(infoServidor);
                    send(200,jSon,exchange);
                }
                catch(Exception e){
                    send(500,e.getMessage(),exchange);
                }
            }
            else if(uri.getPath().equals("/ServidorIOT/plugon")){
                boolean bConfigure = true;
                boolean bErro = false;
                Conector conRetirar = null;
                Gson gson = new GsonBuilder().setDateFormat("dd/MM/yyyy HH:mm:ss").create();
                Conector plug = gson.fromJson(requestContent.toString(), Conector.class);
                Log.log(this,"plugon origem:" + plug.getIp()+" "+plug.getNome()+" "+plug.getIdConector(),"INFO");
                UUID uniqueKey = UUID.randomUUID();
                String id = uniqueKey.toString();
                for(Conector con :listaConectores){
                    if(con.getIdConector().equals(plug.getIdConector())){
                        plug.setReqRet("plug");  
                        Date date = new Date();
                        plug.setTimeStampAlive(String.valueOf(date.getTime()));
                        bConfigure = false;
                        break;
                    }
                    /*else if(con.getNome().equals(plug.getNome())) {
                        bErro = true;
                        bConfigure = false;
                        conRetirar = con;
                        break;
                    }*/
                     
                }
                if(bConfigure){
                    conRetirar = null;
                    Log.log(this,"Configurando conector na lista.","INFO");
                    for(Conector con :listaConectores){
                        if(con.getNome().equals(plug.getNome())){
                            conRetirar.setDevices(null);
                            conRetirar.setButtons(null);
                            conRetirar = con;
                            break;
                        }
                    }
                    if(conRetirar!=null){
                        listaConectores.remove(conRetirar);
                        plug.setReqRet("alive");
                        Date date = new Date();
                        plug.setTimeStampAlive(String.valueOf(date.getTime()));
                    }   
                    else{
                        plug.setReqRet("plug");  
                        Date date = new Date();
                        plug.setTimeStampAlive(String.valueOf(date.getTime()));
                    } 
                    if(plug.getButtons() == null || plug.getButtons().size()==0){
                        if(!plug.getIot().getjSon().trim().equals("")){
                            Type listType = new TypeToken<ArrayList<ButtonIot>>(){}.getType();
                            List<ButtonIot> listaBiot =gson.fromJson(plug.getIot().getjSon(), listType);
                            plug.setButtons(listaBiot);
                            plug.setDevices(new ArrayList<>());
                            for(ButtonIot bIot:plug.getButtons()){
                                RestControle rc = new RestControle();
                                rc.setId(bIot.getButtonID());
                                rc.toDo(plug);
                                plug.getDevices().add(rc);                                    
                            }                                
                        }
                    }                   
                    plug.setIdConector(id);
                    listaConectores.add(plug);  
                    exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
                    send(200,gson.toJson(plug),exchange);                  
                }
                
                else if(bErro) {
                    plug.setErro("Já existe um conector com este nome na lista.");
                    Log.log(this,"Já existe um conector com este nome na lista.","INFO");
                    listaConectores.remove(conRetirar);
                    exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
                    send(500,gson.toJson(plug),exchange);
                }
                else {
                    exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
                    send(200,gson.toJson(plug),exchange);
                }
            }
            else if(uri.getPath().equals("/ServidorIOT/loginServidorIOT")){
                Log.log(this,"Logando...","DEBUG");

                Gson gson = new GsonBuilder().setDateFormat("dd/MM/yyyy HH:mm:ss").create();
                Usuario usuario = gson.fromJson(requestContent.toString(), Usuario.class);
                try {
                    usuario.setSenha(convertPasswordToMD5(usuario.getSenha()));
                } catch (NoSuchAlgorithmException e1) {
                   
                }
		        if( usuario.obterPorUsuarioSenha()){
                    
                    try{
                        KeyPairGenerator keyGenerator = KeyPairGenerator.getInstance("RSA");
                        keyGenerator.initialize(2048);
                
                        KeyPair kp = keyGenerator.genKeyPair();
                        //PublicKey publicKey = (PublicKey) kp.getPublic();
                        PrivateKey privateKey = (PrivateKey) kp.getPrivate();
                
                        //String encodedPublicKey = Base64.getEncoder().encodeToString(publicKey.getEncoded());
                        String token = Jwts.builder().setSubject(usuario.getNome())
                        .setExpiration(
                            Date.from(
                                LocalDateTime.now().plusMinutes(15L)
                                    .atZone(ZoneId.systemDefault())
                                .toInstant()))
                        .setIssuer("neuverse.com")
                        .claim("groups", new String[] { "user", "admin","ServidorNeuverse","ServidorNeuverseIOT" })
                        .signWith(privateKey)
                        .compact();
                        usuario.setToken(token);
                         
                    }  
                    catch(Exception e){
                        send(500,e.getMessage(),exchange);    
                        Log.log(this,e.getMessage(),"DEBUG"); 
                    }
                }
                usuario.setSenha(null);
                String jSon = gson.toJson(usuario);
                exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
                send(200,jSon,exchange); 

            }
            else if(uri.getPath().equals("/ServidorIOT/comando")){
                try{
                    Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().setDateFormat("dd/MM/yyyy HH:mm:ss").create();
                    Conector plug = gson.fromJson(requestContent.toString(), Conector.class);
                    
                    Log.log(this, "Plug "+ plug.getNome(), "DEBUG");
                    for(Conector con :listaConectores){
                        Log.log(this, "Conector jSon "+ requestContent.toString(), "DEBUG");
                    }

                    for(Conector con :listaConectores){
                        if(con.getIdConector().equals(plug.getIdConector())){
                            int tDevice = 0;
                            if(con.getDevices()==null)
                                tDevice = 0;
                            else
                                tDevice = con.getDevices().size();
                            Log.log(this,"comando encontrou "+plug.getNome()+" com "+tDevice+" devices","INFO");
                            Type listType = new TypeToken<ArrayList<ButtonIot>>(){}.getType();
                            List<ButtonIot> listaBiot = gson.fromJson(plug.getIot().getjSon(),listType);
                            for (ButtonIot buttonIot : listaBiot) {
                                if(buttonIot.getSelecionado()!=null&&buttonIot.getSelecionado()){
                                    for(Device device : con.getDevices()) {
                                        if(device.getId().equals(buttonIot.getButtonID())){
                                            Log.log(this,"Comando encontrou device: "+buttonIot.getNick(),"DEBUG");
                                            if(buttonIot.getStatus().equals(Status.ON)){
                                                device.on();
                                            } 
                                            else if(buttonIot.getStatus().equals(Status.OFF)){
                                                device.off();
                                            } 
                                            else if(buttonIot.getStatus().equals(Status.PUSH)){
                                                if(device.getStatus().equals(Status.ON)){
                                                    device.off();    
                                                }
                                                else
                                                    device.on();    
                                            }
                                            break; 
                                        }
                                    }
                                }
                            }
                        }
                    }
                    send(200,"Ok!",exchange);
                }catch(Exception e){
                    send(200,e.getMessage(),exchange);
                }            

            }
            else if(uri.getPath().equals("/ServidorIOT/listarIOTs")){
                Log.log(this,"listarIOTs","INFO");
                //List<String> iots = new ArrayList<>();
                Gson gson = new GsonBuilder().setDateFormat("dd/MM/yyyy HH:mm:ss").excludeFieldsWithoutExposeAnnotation().create();
                try{
                    /*for(Conector con :listaConectores){

                        for(ButtonIot bIot : con.getButtons()){
                            for(Device device: con.getDevices()){
                                if(device.getId().equals(bIot.getButtonID())){
                                    bIot.setStatus(device.getStatus());
                                }
                            }
                        }
                        String jSon = gson.toJson(con.getButtons());
                        con.getIot().setjSon(jSon);
                        iots.add(con.getIot().getjSon());
                    }*/
                    
                    exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
                    send(200,gson.toJson(listaConectores),exchange);
                }
                catch (Exception e) {
                    send(500,e.getMessage(),exchange);
                }
            }
            else{
                send(404,"",exchange);
            }           
        }
        else{
            send(405,"",exchange);
        }
    }   
    

    /**
     * @return HttpServer return the httpServer
     */
    public HttpsServer getHttpServer() {
        return httpServer;
    }

    /**
     * @param httpServer the httpServer to set
     */
    public void setHttpServer(HttpsServer httpServer) {
        this.httpServer = httpServer;
    }

    public static String convertPasswordToMD5(String password) throws NoSuchAlgorithmException {
		MessageDigest md = MessageDigest.getInstance("MD5");

		BigInteger hash = new BigInteger(1, md.digest(password.getBytes()));

		return String.format("%32x", hash);
	}


}
