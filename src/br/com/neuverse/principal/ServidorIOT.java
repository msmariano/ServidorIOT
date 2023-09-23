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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

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
import com.sun.net.httpserver.HttpsServer;
import com.sun.net.httpserver.HttpsConfigurator;
import com.sun.net.httpserver.HttpsParameters;

import br.com.neuverse.database.Configuracao;
import br.com.neuverse.entity.ButtonGpioBananaPi;
import br.com.neuverse.entity.ButtonIot;
import br.com.neuverse.entity.Dispositivo;
import br.com.neuverse.entity.InterGpioBananaPi;
import br.com.neuverse.entity.InterfGpioRaspPI;
import br.com.neuverse.entity.Parametro;
import br.com.neuverse.entity.Pool;
import br.com.neuverse.enumerador.Status;

public class ServidorIOT implements HttpHandler {
    Pool pool = new Pool();
    List<Pool> conectores = new ArrayList<>();
    private HttpsServer httpServer;    
    Type TypeListPool = new TypeToken<ArrayList<Pool>>() {}.getType();
    Type TypeListDisp = new TypeToken<ArrayList<Dispositivo>>() {}.getType();
    public static void main(String[] args) {
        if (args.length > 0) {
            if (args[0].equals("ver")) {
                System.out.print("ServidorIOT V00_00_11.");
                return;
            }
        }
        new ServidorIOT();

    }

    public ServidorIOT() {
        // Inicio controle deste Servidor
        conectores.add(pool);
        try {
            Configuracao cfg = new Configuracao();
            List<Parametro> listaBtnGpio = cfg.retornaBtnGpio();
            System.out.println("Carregando configuracao Gpio...");
            if (cfg.getTipoServidor().equals(1)) {
                for (Parametro btnGpio : listaBtnGpio) {
                    InterfGpioRaspPI iGpioRaspPI = new InterfGpioRaspPI(btnGpio.getC1(), btnGpio.getC2(), btnGpio.getC3(),
                            btnGpio.getC4(), btnGpio.getC8());
                    pool.getDispositivos().add(iGpioRaspPI);
                }
            }
            else if (cfg.getTipoServidor().equals(2)) {
                carregaGpioButtonsBanana();
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
            System.out.println("Erro ao instanciar ServidorIOT:"+e.getMessage());
        }
    }

    public void carregaGpioButtonsBanana() {
		try {
			Configuracao cfg = new Configuracao();
			List<Parametro> listaBtnGpio = cfg.retornaBtnGpio();
			for (Parametro btnGpio : listaBtnGpio) {
				try {
					InterGpioBananaPi bgrpi = new InterGpioBananaPi(btnGpio.getC1(), btnGpio.getC2(), btnGpio.getC3(),
							btnGpio.getC4(), btnGpio.getC9(), btnGpio.getC10(), btnGpio.getC8());
					if (btnGpio.getC1() > -1) {
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
                if (uri.getPath().equals("/ServidorIOT/listar")){
                    Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
                    send(200, gson.toJson(conectores), exchange);
                }
                else if (uri.getPath().equals("/ServidorIOT/atualizar")) {
                    Gson gson = new GsonBuilder().create();
                    List<Pool> poolsAtualizar = gson.fromJson(requestContent.toString(), TypeListPool);
                     for(Pool poolAtualizar : poolsAtualizar){
                        for(Pool pool : conectores){
                            if(pool.getId().equals(poolAtualizar.getId())){
                                for(Dispositivo dispositivoAtualizar : poolAtualizar.getDispositivos()){
                                    Dispositivo dispositivo = pool.buscar(dispositivoAtualizar.getId());
                                    if(dispositivo!=null)
                                        dispositivo.updateStatus(dispositivoAtualizar.getStatus());
                                }
                                break;
                            }
                        }
                    }
                    send(200, "", exchange);
                   
                } 
                else if(uri.getPath().equals("/ServidorIOT/info")){
                    send(200, "{\"Servico\":\"ServidorIOT\"}", exchange);
                }
                else {
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
}
