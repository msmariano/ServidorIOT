package br.com.neuverse.principal;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Terminal {
    private Socket socket;
    private Long pid;
    
    List<String> regras = new ArrayList<>();
    synchronized void enviar(String mens) {
        BufferedWriter saida;
        try {
            saida = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            saida.write(mens + "\r\n");
            saida.flush();
        } catch (IOException e) {
        }
    }

    public void executar() {
        try {
            BufferedReader entradalog = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));
            while (true) {
                String mens = entradalog.readLine();
                if (mens.equals("")) {
                    socket.close();
                    break;
                }
                if(mens.contains("regras=")){
                    if(mens.substring(0, 1).equals("r")){
                        String r[] = mens.split("=");
                        regras.add(r[1]);
                        enviar("Acrescentando regra "+r[1]);
                    }
                }
                else if(mens.contains("remove=")){
                    if(mens.substring(0, 1).equals("r")){
                        String r[] = mens.split("=");
                        regras.remove(r[1]);
                        enviar("removendo regra "+r[1]);
                    }
                }
                else if(mens.contains("kill")){
                    Runtime.getRuntime().exec("kill " + pid).waitFor();
                }
                else if(mens.contains("quit")){
                    enviar("bye!");
                    Thread.sleep(1000);
                    socket.close();
                }

            }
        } catch (Exception e) {
        }
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public Long getPid() {
        return pid;
    }

    public void setPid(Long pid) {
        this.pid = pid;
    }

}
