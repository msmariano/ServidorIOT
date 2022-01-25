package br.com.neuverse.entity;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;

import javax.ws.rs.HttpMethod;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class ControleRest {


    public ComandoIOT motorPiscina(String acao) throws Exception {
       
        URL url = new URL("http://192.168.0.241");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod(HttpMethod.POST);
        con.setRequestProperty("Content-Type", "application/json");
        con.setRequestProperty("Host", "192.168.0.241");
        con.setRequestProperty("Connection", "Keep-Alive");
        String l = "{\"acao\":\""+acao+"\"}";
        con.setRequestProperty("Content-Length", String.valueOf(l.length()));
        con.setRequestProperty("Accept", "*/*");
        con.setRequestProperty("Accept-Encoding", "gzip, deflate, br");
        con.setDoOutput(true);
        
        try(OutputStream os = con.getOutputStream()) {
            byte[] input = l.getBytes();
            os.write(input, 0, l.length());			
        }
        int responseCode = con.getResponseCode();

        System.out.println(responseCode);
       System.out.println(con.getContentLength());  


        if(responseCode == HttpURLConnection.HTTP_OK){
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                System.out.println(inputLine);
                response.append(inputLine);
            }
            in.close();

            String json = response.toString().substring(0,con.getContentLength());
            Gson gson = new GsonBuilder().setDateFormat("dd/MM/yyyy HH:mm:ss").create();
            ComandoIOT ciot = gson.fromJson(json, ComandoIOT.class);
			return ciot;
        }
		return null;
    }
    
}
