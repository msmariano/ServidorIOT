package br.com.neuverse.principal;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

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

    public String pegarMac(){
        String os = System.getProperty("os.name");
        if(os.startsWith("Linux")) {
            try {
                return linuxParseMacAddress(linuxRunIfConfigCommand());
            } catch (ParseException | IOException e) {
              
            }
        }
        return "";
    }

    private final static String linuxParseMacAddress(String ipConfigResponse) throws ParseException {
        String localHost = null;
        try {
            localHost = InetAddress.getLocalHost().getHostAddress();
        } catch(java.net.UnknownHostException ex) {
            ex.printStackTrace();
            throw new ParseException(ex.getMessage(), 0);
        }
    
        StringTokenizer tokenizer = new StringTokenizer(ipConfigResponse, "\n");
        String lastMacAddress = null;
    
        while(tokenizer.hasMoreTokens()) {
            String line = tokenizer.nextToken().trim();
            boolean containsLocalHost = line.indexOf(localHost) >= 0;
    
            //IP 
            if(containsLocalHost && lastMacAddress != null) {
                return lastMacAddress;
            }
    
            //MAC address
            int macAddressPosition = line.indexOf("HWaddr");
            if(macAddressPosition <= 0) continue;
    
            String macAddressCandidate = line.substring(macAddressPosition + 6).trim();
            if(linuxIsMacAddress(macAddressCandidate)) {
                lastMacAddress = macAddressCandidate;
                continue;
            }
        }
    
        ParseException ex = new ParseException
            ("Nao foi possível ler o MAC address para " + localHost + " de [" + ipConfigResponse + "]", 0);
        ex.printStackTrace();
        throw ex;
    }

    private final static boolean linuxIsMacAddress(String macAddressCandidate) {
        if(macAddressCandidate.length() != 17) return false;
        return true;
    }

    private final static String linuxRunIfConfigCommand() throws IOException {
        Process p = Runtime.getRuntime().exec("ifconfig");
        InputStream stdoutStream = new BufferedInputStream(p.getInputStream());
    
        StringBuffer buffer= new StringBuffer();
        for (;;) {
            int c = stdoutStream.read();
            if (c == -1) break;
            buffer.append((char)c);
        }
        String outputText = buffer.toString();
    
        stdoutStream.close();
    
        return outputText;
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
