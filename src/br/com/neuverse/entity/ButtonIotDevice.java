package br.com.neuverse.entity;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.reflect.TypeToken;

import br.com.neuverse.enumerador.Status;
import br.com.neuverse.principal.Log;

public class ButtonIotDevice extends Device {

    @Expose(serialize = false)
    private ButtonIot bIot;
    @Expose(serialize = true)
    private Socket socket;
    @Expose(serialize = true)
    private Conector conector;
    @Expose(serialize = true)
    Gson gson = new GsonBuilder()
		.setDateFormat("dd/MM/yyyy HH:mm:ss")
		.excludeFieldsWithoutExposeAnnotation()
		.create();

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public ButtonIot getbIot() {
        return bIot;
    }

    public void setbIot(ButtonIot bIot) {
        this.bIot = bIot;
    }

    @Override
    boolean on() {
        bIot.setStatus(Status.ON);
        conector.setStatus(Status.PROCESSARBTN);
        String jSon = gson.toJson(conector,Conector.class);
        Log.log(this,"ON device IOT:"+jSon,"DEBUG");
        enviar(jSon+"\r\n");
        return true;
    }

    
	public synchronized void enviar(String mens) {
		BufferedWriter saida;
		try {
			saida = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			saida.write(mens);
			saida.flush();
		} catch (IOException e) {
			Log.log(this, "enviar "+bIot.getNick()+" "+e.getMessage(), "DEBUG");
		}

	}

    @Override
    public Integer getId() {
        return bIot.getButtonID();
    }

    @Override
    public void setId(Integer id) {
        // TODO Auto-generated method stub
        
    }

    @Override
    boolean off() {
        bIot.setStatus(Status.OFF);
        conector.setStatus(Status.PROCESSARBTN);
        String jSon = gson.toJson(conector,Conector.class);
        enviar(jSon+"\r\n");
        return true;
    }

    @Override
    Status getStatus() {
        return bIot.getStatus();
    }

    @Override
    public void toDo(Object obj) {
        conector = (Conector) obj;
        
    }
    
}
