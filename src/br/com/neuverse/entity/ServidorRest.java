package br.com.neuverse.entity;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.net.InetSocketAddress;
import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import br.com.neuverse.enumerador.Status;
import br.com.neuverse.principal.Log;
import br.com.neuverse.principal.Main;



public class ServidorRest implements HttpHandler {
    private HttpServer httpServer;
    private List<Conector> listaConectores;
    private InfoServidor infoServidor;
  
    
    public ServidorRest(){
        try {
            httpServer = HttpServer.create(new InetSocketAddress(8080), 0);
            httpServer.createContext("/ServidorIOT", this);
            ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor)Executors.newFixedThreadPool(10);
            httpServer.setExecutor(threadPoolExecutor);
            this.httpServer.start();
        } catch (IOException e) {
            
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
                Gson gson = new GsonBuilder().setDateFormat("dd/MM/yyyy HH:mm:ss").create();
                String jSon = gson.toJson(infoServidor);
                send(200,jSon,exchange);
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
            else if(uri.getPath().equals("/ServidorIOT/comando")){
                try{
                    Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().setDateFormat("dd/MM/yyyy HH:mm:ss").create();
                    Conector plug = gson.fromJson(requestContent.toString(), Conector.class);
               
                    for(Conector con :listaConectores){
                        if(con.getNome().equals(plug.getNome())){
                            Log.log(this,"comando encontrou "+plug.getNome(),"INFO");
                            Type listType = new TypeToken<ArrayList<ButtonIot>>(){}.getType();
                            List<ButtonIot> listaBiot = gson.fromJson(plug.getIot().getjSon(),listType);
                            for (ButtonIot buttonIot : listaBiot) {
                                for(Device device : con.getDevices()) {
                                    if(device.getId().equals(buttonIot.getButtonID())){
                                        if(buttonIot.getStatus().equals(Status.ON)){
                                            device.on();
                                        } 
                                        else if(buttonIot.getStatus().equals(Status.OFF)){
                                            device.off();
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
    public HttpServer getHttpServer() {
        return httpServer;
    }

    /**
     * @param httpServer the httpServer to set
     */
    public void setHttpServer(HttpServer httpServer) {
        this.httpServer = httpServer;
    }



}
