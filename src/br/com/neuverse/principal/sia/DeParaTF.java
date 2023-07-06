package br.com.neuverse.principal.sia;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class DeParaTF {

    public static void main(String[] args) {
		// Create a variable for the connection string.
		String connectionUrl = "jdbc:jtds:sqlserver://10.15.60.80:1433;databaseName=sia;user=sflprod;password=prodsfl";
		//String connectionUrl = "jdbc:jtds:sqlserver://sqldesenv01.eparana.parana:1433;databaseName=sia;user=sa_sia;password=stranger";
		String SQL = "SELECT uap.CodMun X\r\n" + //
		        "\t,uap.CodUsuarioAmbientalPublico\r\n" + //
		        "\t,(\r\n" + //
		        "\t\tSELECT top 1 mun1.CodMun\r\n" + //
		        "\t\tFROM TB_Municipio mun1\r\n" + //
		        "\t\tWHERE mun1.NomeMun = (\r\n" + //
		        "\t\t\t\tSELECT mid1.NomeMun\r\n" + //
		        "\t\t\t\tFROM TB_MunIapDoc mid1\r\n" + //
		        "\t\t\t\tWHERE mid1.codMun = uap.CodMun\r\n" + //
		        "\t\t\t\t)\r\n" + //
		        "\t\t) AS Y\r\n" + //
		        "\r\n" + //
		        "\t\t\r\n" + //
		        "FROM TB_UsuarioAmbientalPublico uap\r\n" + //
		        "WHERE uap.CodUsuarioAmbientalPublico IN (\r\n" + //
		        "\t\tSELECT CodUsuarioAmbientalPublico\r\n" + //
		        "\t\tFROM TB_TermoFauna\r\n" + //
		        "\t\t)\r\n" + //
		        "\tAND CodMun IN (\r\n" + //
		        "\t\tSELECT mid1.codMun\r\n" + //
		        "\t\tFROM TB_MunIapDoc mid1\r\n" + //
		        "\t\t)\r\n" + //
		        "";
		try {
            Connection con = DriverManager.getConnection(connectionUrl);
			Statement stmt = con.createStatement();
            Statement stmt1 = con.createStatement();
            ResultSet rs = stmt.executeQuery(SQL);
            while (rs.next()) {
                int x = rs.getInt("X");
                int y = rs.getInt("Y");
                int codUsuarioAmbientalPublico = rs.getInt("codUsuarioAmbientalPublico");
                System.out.println(codUsuarioAmbientalPublico+" "+x+" "+y);
                //stmt1.executeUpdate("update TB_UsuarioAmbientalPublico  set CodMun = "+y+" WHERE CodUsuarioAmbientalPublico = "+codUsuarioAmbientalPublico+" and codMun = "+x+"");
            
            }
            rs.close();
            stmt.close();
            stmt1.close();
        }
        catch(Exception e){

        }
    }
    
}
