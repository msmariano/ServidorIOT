package br.com.neuverse.principal;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyStore;
import java.util.Arrays;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;

public class RoteadorApp {

    private static ServerSocket servidor;

    public void inicializar(int porta){
       try {
        servidor =  getServerSocket(porta);
        new Thread() {
			@Override
			public void run() {
				while (true) {
					try {
						Socket esquerdo;
						esquerdo = servidor.accept();
						esquerdo.setKeepAlive(true);
                       
                       
						new Thread() {
							@Override
							public void run() {

                                 Socket direito = new Socket();

							
                                while(true){
                                     try {
                                        BufferedReader reader = new BufferedReader(new InputStreamReader(esquerdo.getInputStream(),StandardCharsets.UTF_8.name()));
                                    } catch (Exception e){

                                    }
                                }
								
							}
						}.start();

					} catch (Exception e) {
						Log.log(this, e.getMessage(), "DEBUG");
					}
				}
			}
		}.start();


    } catch (Exception e) {
       
    }

    }


    private ServerSocket getServerSocket(Integer porta) throws Exception {

		InetSocketAddress address = new InetSocketAddress("0.0.0.0", porta);
		// Backlog is the maximum number of pending connections on the socket,
		// 0 means that an implementation-specific default is used
		int backlog = 0;

		Path keyStorePath = Paths.get("/home/pi/Desktop/servidoriothttps.jks");
		char[] keyStorePassword = "password".toCharArray();

		// Bind the socket to the given port and address
		ServerSocket serverSocket = getSslContext(keyStorePath, keyStorePassword)
				.getServerSocketFactory()
				.createServerSocket(address.getPort(), backlog, address.getAddress());

		Arrays.fill(keyStorePassword, '0');

		return serverSocket;
	}

    	private SSLContext getSslContext(Path keyStorePath, char[] keyStorePass)
			throws Exception {

		KeyStore keyStore = KeyStore.getInstance("JKS");
		keyStore.load(new FileInputStream(keyStorePath.toFile()), keyStorePass);

		KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("SunX509");
		keyManagerFactory.init(keyStore, keyStorePass);

		SSLContext sslContext = SSLContext.getInstance("TLS");
		// Null means using default implementations for TrustManager and SecureRandom
		sslContext.init(keyManagerFactory.getKeyManagers(), null, null);
		return sslContext;
	}

    
}
