package br.com.neuverse.principal;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.text.ParseException;
import java.util.StringTokenizer;

import io.jsonwebtoken.io.IOException;

public class Util {

    
    public static String pegarMac(){
        String os = System.getProperty("os.name");
        if(os.startsWith("Linux")) {
            try {
                return linuxParseMacAddress(linuxRunIfConfigCommand());
            } catch (ParseException | IOException e) {
              
            } catch (java.io.IOException e) {
            }
        }
        return "";
    }

    private static String linuxParseMacAddress(String ipConfigResponse) throws ParseException {
            
        StringTokenizer tokenizer = new StringTokenizer(ipConfigResponse, "\n");      
    
        while(tokenizer.hasMoreTokens()) {
            String line = tokenizer.nextToken().trim();
            
            //MAC address
            int macAddressPosition = line.indexOf("ether");
            if(macAddressPosition < 0) continue;
    
            String macAddressCandidate = line.substring(macAddressPosition + 6,23).trim();
            if(linuxIsMacAddress(macAddressCandidate)) {
                    return macAddressCandidate;              
            }
        }
    
        return "";
    }

    private final static boolean linuxIsMacAddress(String macAddressCandidate) {
        if(macAddressCandidate.length() != 17) return false;
        return true;
    }

    private final static String linuxRunIfConfigCommand() throws IOException, java.io.IOException {
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
    
}
