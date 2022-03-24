package br.com.neuverse.entity;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import br.com.neuverse.principal.Log;



public class ServidorRest implements HttpHandler {
    private HttpServer httpServer;
    private List<Conector> listaConectores;
    
    public ServidorRest(){
        try {
            httpServer = HttpServer.create(new InetSocketAddress(8080), 0);
            httpServer.createContext("/ServidorIOT", this);
            httpServer.setExecutor(null);
            this.httpServer.start();

        } catch (IOException e) {
            
        }
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

        if(exchange.getRequestMethod().toUpperCase().equals("POST")){
            URI uri = exchange.getRequestURI();

            if(uri.getPath().equals("/ServidorIOT/info")){
                send(200,"{\"teste\":1234}",exchange);
            }
            else if(uri.getPath().equals("/ServidorIOT/plugon")){
                boolean bConfigure = true;
                Log.log(this,"plugon");
                Gson gson = new GsonBuilder().setDateFormat("dd/MM/yyyy HH:mm:ss").create();
                Conector plug = gson.fromJson(requestContent.toString(), Conector.class);
                UUID uniqueKey = UUID.randomUUID();
                String id = uniqueKey.toString();
                for(Conector con :listaConectores){
                    if(con.getId().equals(plug.getId())){
                        bConfigure = false;
                        break;
                    }
                }
                if(bConfigure){
                    Conector conRetirar = null;
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
                        
                    plug.setId(id);
                    listaConectores.add(plug);                    
                }
                exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
                send(200,gson.toJson(plug),exchange);
            }
            else if(uri.getPath().equals("/ServidorIOT/listarIOTs")){
                Log.log(this,"listarIOTs");
                List<String> iots = new ArrayList<>();
                for(Conector con :listaConectores){
                    iots.add(con.getNome());
                }
                Gson gson = new GsonBuilder().setDateFormat("dd/MM/yyyy HH:mm:ss").create();
                exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
                send(200,gson.toJson(iots),exchange);
            }
            else{
                send(404,"",exchange);
            }           
        }
        else{
            send(405,"",exchange);
        }
    }
   
    
}
