package br.com.neuverse.principal.sia;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SiaPrincipal {
	


	public static void main(String[] args) {
		// Create a variable for the connection string.
		String connectionUrl = "jdbc:jtds:sqlserver://10.15.60.80:1433;databaseName=sia;user=sflprod;password=prodsfl";
		//String connectionUrl = "jdbc:jtds:sqlserver://sqldesenv01.eparana.parana:1433;databaseName=sia;user=sa_sia;password=stranger";
		String SQL = "";
		try {
			
			List<Parametro> parametrosAux = new ArrayList<>();
			List<Parametro> parametrosFinal = new ArrayList<>();
			List<Parametro> parametrosDuplicados = new ArrayList<>();
			List<Parametro> parametrosDuplicadosNMetais = new ArrayList<>();	
			
			List<String> listaMetais = new ArrayList<>();
			listaMetais.add("aluminio");
			listaMetais.add("bario");
			listaMetais.add("cadmio");
			listaMetais.add("calcio");
			listaMetais.add("chumbo");
			listaMetais.add("cobalto");
			listaMetais.add("cobre");
			listaMetais.add("cromo");
			listaMetais.add("ferro");
			listaMetais.add("magnesio");
			listaMetais.add("manganes");
			listaMetais.add("mercurio");
			listaMetais.add("niquel");
			listaMetais.add("potassio");
			listaMetais.add("prata");
			listaMetais.add("sodio");
			listaMetais.add("zinco");
			Connection con = DriverManager.getConnection(connectionUrl);
			Statement stmt = con.createStatement();
			SQL = "SELECT *,'' as laboratorio FROM TB_Parametro par order by par.codParametro,par.nomeParametro";
			
			
			
			/*SQL = "SELECT par.codParametro,par.nomeParametro,(select lab.nomeLaboratorio \r\n"
					+ "from tb_laboratorio lab where lab.CodLaboratorio = labpar.CodLaboratorio ) as laboratorio\r\n"
					+ "FROM TB_Parametro par\r\n"
					+ "left join TB_LaboratorioParametro labpar on labpar.CodParametro = par.CodParametro\r\n"
					+ "order by par.codParametro,par.nomeParametro";*/
			ResultSet rs = stmt.executeQuery(SQL);
			
			
			String codigos = "" ;
			

			// Iterate through the data in the result set and display it.
			while (rs.next()) {
				//System.out.println(rs.getString("codParametro") + " " + rs.getString("nomeParametro"));
				Parametro par = new Parametro();
				par.setCodParametro(rs.getInt("codParametro") );
				par.setNomeParametro(Normalizer.normalize(rs.getString("nomeParametro"), Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "").toLowerCase());
				if(listaMetais.contains(par.getNomeParametro()))
					par.setNomeLaboratorio("Metais");
				else
					par.setNomeLaboratorio("Não Metais");
				parametrosAux.add(par);
				codigos = codigos + rs.getInt("codParametro")  + ",";
				
			}
			
			for(Parametro parametroAux : parametrosAux) {
				boolean bAchou = false;
				for (Parametro parametroFinal : parametrosFinal) {
					if(parametroFinal.getNomeParametro().equals(parametroAux.getNomeParametro())){
						bAchou = true;
						break;
					}
				}
				if(!bAchou)
					parametrosFinal.add(parametroAux);
				else
				{
					parametrosDuplicados.add(parametroAux);
				}
				bAchou = false;
				
			}
			
			Collections.sort(parametrosFinal,new Comparator<Parametro>() {
				@Override
				public int compare(Parametro o1, Parametro o2) {
					return o1.getNomeParametro().compareTo(o2.getNomeParametro());
				}
			});
			
			Collections.sort(parametrosDuplicados,new Comparator<Parametro>() {
				@Override
				public int compare(Parametro o1, Parametro o2) {
					return o1.getNomeParametro().compareTo(o2.getNomeParametro());
				}
			});
			
			for (Parametro parametro : parametrosFinal) {
				System.out.println(parametro.getNomeParametro()+ " "+parametro.getNomeLaboratorio()+    " ["+ parametro.getCodParametro()+"]");
			}
			System.out.println();
			System.out.println();
			System.out.println("DUPLICADOS[Não Metais]--------------------------");
			String dups = "select * from TB_FichaColetaParametro where CodParametro in (";
			for (Parametro parametro : parametrosDuplicados) {
				if(parametro.getNomeLaboratorio()!=null &&!parametro.getNomeLaboratorio().trim().equals("Metais")) {
					dups = dups + parametro.getCodParametro() + ",";
					parametrosDuplicadosNMetais.add(parametro);
				}
					System.out.println(parametro.getNomeParametro()+ " "+parametro.getNomeLaboratorio()+" ["+ parametro.getCodParametro()+"]");
			}
			dups = dups +("-1)");
			System.out.println(dups);
			
			System.out.println("DUPLICADOS[Metais]--------------------------");
			for (Parametro parametro : parametrosDuplicados) {
				if(parametro.getNomeLaboratorio()!=null &&  parametro.getNomeLaboratorio().trim().equals("Metais"))
					System.out.println(parametro.getNomeParametro()+ " "+parametro.getNomeLaboratorio()+" ["+ parametro.getCodParametro()+"]");
			}
			
			System.out.println("DUPLICADOS[Sem laboratorio]--------------------------");
			for (Parametro parametro : parametrosDuplicados) {
				if(parametro.getNomeLaboratorio()==null)
					System.out.println(parametro.getNomeParametro()+ " "+parametro.getNomeLaboratorio()+" ["+ parametro.getCodParametro()+"]");
			}
			
			for (Parametro dup : parametrosDuplicadosNMetais) {
				
				for (Parametro normal : parametrosFinal) {
					if(normal.getNomeParametro().equals(dup.getNomeParametro())) {
						
						//System.out.println("delete from TB_Parametro  where  CodParametro = "+dup.getCodParametro()+";");
						
						
					}
					
				}
				
				
				//System.out.println();
				
				
				/*for (Parametro normal : parametrosFinal) {
					if(normal.getNomeParametro().equals(dup.getNomeParametro())) {
						String setCondicao = "set CodParametro = "+normal.getCodParametro()+" where CodParametro = "+dup.getCodParametro();
						
						SQL = "update  TB_ParametroPontoCronograma "+setCondicao;
						stmt.executeUpdate(SQL);
						
						SQL = "update  TB_NaturezaParametro set CodParametro = "+normal.getCodParametro()+" where CodParametro = "+dup.getCodParametro()+" "+
							  "	and CodNatureza in (select nat1.CodNatureza from TB_NaturezaParametro nat1 where nat1.CodParametro in ("+dup.getCodParametro()+") "+
							  "	and nat1.codNatureza not in (select nat2.codNatureza from TB_NaturezaParametro nat2  where nat2.CodParametro = " +normal.getCodParametro()+")) ";
						stmt.executeUpdate(SQL);
						SQL = "DELETE FROM TB_NaturezaParametro WHERE CodParametro = "+dup.getCodParametro();
						stmt.executeUpdate(SQL);
						
						SQL = "update  TB_LaboratorioParametro set CodParametro = "+normal.getCodParametro()+" where CodParametro = "+dup.getCodParametro()+" "+
								  "	and CodLaboratorio in (select nat1.CodLaboratorio from TB_LaboratorioParametro nat1 where nat1.CodParametro in ("+dup.getCodParametro()+") "+
								  "	and nat1.codLaboratorio not in (select nat2.codLaboratorio from TB_LaboratorioParametro nat2  where nat2.CodParametro = " +normal.getCodParametro()+")) ";
						stmt.executeUpdate(SQL);
						SQL = "DELETE FROM TB_LaboratorioParametro WHERE CodParametro = "+dup.getCodParametro();
						stmt.executeUpdate(SQL);
						
						
						
						SQL = "update  TB_FichaColetaParametro "+setCondicao;
						stmt.executeUpdate(SQL);
						
						
						
						SQL = "delete from TB_Parametro  where CodParametro = "+dup.getCodParametro();
						stmt.executeUpdate(SQL);
						break;
					}
				}*/
			}
			
			con.close();
			
		}
		// Handle any errors that may have occurred.
		catch (SQLException e) {
			e.printStackTrace();
		}

	}

}
