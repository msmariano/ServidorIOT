package br.com.neuverse.principal;

import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.sql.Date;
import java.time.LocalDateTime;
import java.time.ZoneId;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import io.jsonwebtoken.Jwts;

public class TesteToken {


    public static String convertPasswordToMD5(String password) throws NoSuchAlgorithmException {
		MessageDigest md = MessageDigest.getInstance("MD5");

		BigInteger hash = new BigInteger(1, md.digest(password.getBytes()));

		return String.format("%32x", hash);
	}
	
	public static String gerarTokenPesquisa(String numAmostra,String ano, String senha,String codFichaColeta) {
		String token = null;
		InfoLaudoEmail iml = new InfoLaudoEmail();
		iml.setAmostra(numAmostra);
		iml.setAno(ano);
		iml.setCodigo(senha);
		Gson gson = new GsonBuilder().setDateFormat("dd/MM/yyyy HH:mm:ss").create();
		String jSon = gson.toJson(iml);
		
		try{
            KeyPairGenerator keyGenerator = KeyPairGenerator.getInstance("RSA");
            keyGenerator.initialize(2048);
            KeyPair kp = keyGenerator.genKeyPair();
            PrivateKey privateKey = (PrivateKey) kp.getPrivate();
            token = Jwts.builder().setSubject("")
            .setExpiration(
                            Date.from(
                                LocalDateTime.now().plusMinutes(15L)
                                    .atZone(ZoneId.systemDefault())
                                .toInstant()))
            .setIssuer("celepar.pr.gov.br")
            .claim("relatorioSicadi",jSon)
            .signWith(privateKey)
            .compact();
        }  
        catch(Exception e){
        }
		return token;
	}

    public static void main(String[] args) {
      
		try{
            String senha = convertPasswordToMD5("#C3l3p@rIat2022 "+" "+"107623");
            String t = gerarTokenPesquisa("1940","2022",senha,"");
            System.out.println(t);
            
        }  
        catch(Exception e){
        }

    }
}
