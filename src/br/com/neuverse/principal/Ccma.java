package br.com.neuverse.principal;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Ccma {
    public static void main(String[] args) throws SQLException, ClassNotFoundException {
		// Create a variable for the connection string.
		String connectionUrl = "jdbc:jtds:sqlserver://10.15.60.80:1433;databaseName=sia;user=sflprod;password=prodsfl";
        String connectionUrlCcma = "jdbc:postgresql://pghomolog05.celepar.parana:5432/ccma?user=sa_ccma&password=stranger";
       
		//String connectionUrl = "jdbc:jtds:sqlserver://sqldesenv01.eparana.parana:1433;databaseName=sia;user=sa_sia;password=stranger";
		String SQL = "select codUsuario, 'INSERT INTO usuario_ambiental.tb_usuario_ambiental (cpf_cnpj, \r\n" + 
        "nome_razao_social, nome_fantasia, cod_estado_civil, cod_sexo, cod_tipo_pessoa, data_nascimento_inicio, \r\n" + 
        "rg_inscricao_estadual, rg_orgao_emissor, rg_uf_emissor, rg_data_emissao, email, telefone, ramal_telefone, \r\n" + 
        "fax, ramal_fax, celular, nome_pai, nome_mae, nome_conjuge, cpf_conjuge, rg_conjuge, rg_orgao_emissor_conjuge, \r\n" + 
        "rg_uf_emissor_conjuge, rg_data_emissao_conjuge, info_complementar, login_sentinela, cod_sistema_origem, \r\n" + 
        "data_hora_cadastro, cpf_responsavel_cadastro, data_hora_ultima_alteracao, cpf_responsavel_ultima_alteracao, \r\n" + 
        "site, ind_estrangeiro, pais_origem_neocep_sigla_3letras, num_docto_origem, obs_docto_origem)VALUES('+\r\n" + 
        "case when CPFUsuario is not null or CPFUsuario <> '' then CPFUsuario else CGCUsuario   end+','''+\r\n" + 
        "case when nomeusuario is not null then nomeusuario else 'NULL' end +''','+\r\n" + 
        "'''---'','\r\n" + 
        "+case when EstCivUsuario = 'SO' then '1' else case when EstCivUsuario = 'CA'  then '2' else      'NULL' end end +','+\r\n" + 
        "case when sexo = 'M' then '1' else case when sexo = 'F' then '2' else 'NULL' end end+','+\r\n" + 
        "case when TipoFisJur = 'F' then '1' else case when TipoFisJur = 'J' then '2' else 'NULL' end end+','+\r\n" + 
        "'NULL,'+\r\n" + 
        "case when TipoFisJur = 'F' and RGUsuario is not null  then ''''+RGUsuario+''''  else case when TipoFisJur = 'J' and InscrEstUsuario is not null then ''''+InscrEstUsuario+''''  else 'NULL' end end+','+\r\n" + 
        "case when RGOrgao is not null then  ''''+RGOrgao+'''' else 'NULL' end  +','+\r\n" + 
        "case when RGUf is not null then ''''+RGUf+''''  else 'NULL' end   +','+\r\n" + 
        "'NULL,'+\r\n" + 
        "case when EmailUsuario is not null then ''''+EmailUsuario+''',' else '''---'',' end +\r\n" + 
        "case when FoneUsuario is not null then ''''+substring(FoneUsuario,0,14)+''','  else 'NULL,' end+\r\n" + 
        "case when RamalContato is not null then ''''+RamalContato+''','  else 'NULL,' end+\r\n" + 
        "case when FaxUsuario is not null then ''''+substring(FaxUsuario,0,14)+''','  else 'NULL,' end  +\r\n" + 
        "'NULL,'+\r\n" + 
        "case when FoneUsuario is not null then ''''+substring(FoneUsuario,0,14) +''','  else 'NULL,' end +\r\n" + 
        "case when NomePai is not null then ''''+NomePai +''','  else 'NULL,' end +\r\n" + 
        "case when NomeMae is not null then ''''+NomeMae+''','  else 'NULL,' end +\r\n" + 
        "case when NomeConjuge is not null then ''''+NomeConjuge+''','  else 'NULL,' end +\r\n" + 
        "case when  CPFConjuge  is not null then ''''+CPFConjuge+''','  else 'NULL,' end +\r\n" + 
        "case when RGConjuge is not null then ''''+RGConjuge+''','  else 'NULL,' end +\r\n" + 
        "'NULL,'+\r\n" + 
        "'NULL,'+\r\n" + 
        "'NULL,'+\r\n" + 
        "'NULL,'+\r\n" + 
        "'NULL,'+\r\n" + 
        "'999,'+\r\n" + 
        "case when DataCadastro is not null then ''''+convert(varchar,DataCadastro,105)+ ' horaCadAqui'','  else 'NULL,' end +\r\n" + 
        "'''87522020972'','+\r\n" + 
        "case when DataAtualizacao is not null then ''''+convert(varchar,DataAtualizacao,105)+''','  else 'NULL,' end +\r\n" + 
        "'''87522020972'','+\r\n" + 
        "'NULL,'+\r\n" + 
        "'NULL,'+\r\n" + 
        "'NULL,'+\r\n" + 
        "'NULL,'+\r\n" + 
        "'NULL);' as qry \r\n " + 
        "from tb_UsuarioAmbiental";

        Connection conCcma = DriverManager.getConnection(connectionUrlCcma);
        Statement stmtCcma = conCcma.createStatement();


        Class.forName("net.sourceforge.jtds.jdbc.Driver");
        Connection con = DriverManager.getConnection(connectionUrl);
		Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery(SQL);

        Integer hora = 0;
        Integer minuto = 0;
        Integer segundo = 0;

        while (rs.next()) {
            String queryCcma = rs.getString("qry");
            if(queryCcma!=null){
                

                    

               
                if(hora.equals(24))
                    hora = 0;
                if(minuto.equals(60))
                    minuto = 0;
                if(segundo.equals(60))
                    segundo = 0;
                
                String horalCad = String.format("%02d",hora)+":"+String.format("%02d",minuto)+":"+String.format("%02d",segundo);
                queryCcma = queryCcma.replace("horaCadAqui", horalCad);
                hora++;minuto++;segundo++;    
                
                try
                {
                    stmtCcma.executeUpdate(queryCcma);
                }catch(Exception e){
                    System.out.println(e.getMessage());
                    System.out.println(queryCcma);
                }
            }
        }
        con.close();
        conCcma.close();

    }
    
}
