package scraper;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.apache.pdfbox.io.IOUtils;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

public class Entreprises {

	/*Extraction du registre à partir d'une page spécifique*/
	public static void getRegistre(String pagePres, int departElement) throws Exception{
		/*"name":"Bover B.V.",
            "company_number":"17087985",
            "jurisdiction_code":"nl",
            "incorporation_date":null,
            "dissolution_date":null,
            "company_type":"Besloten Vennootschap",
            "registry_url":"https://server.db.kvk.nl/TST-BIN/FU/TSWS001@?BUTT=17087985",
            "branch_status":null,
            "inactive":false,
            "current_status":"Active",
            "created_at":"2011-01-12T21:50:57+00:00",
            "updated_at":"2012-02-02T10:36:46+00:00",
            "retrieved_at":"2011-08-25T14:37:37+01:00",
            "opencorporates_url":"http://opencorporates.com/companies/nl/17087985",
            "previous_names":null,
            "source":*/
		String[] attributs = new String[12];
		/*Token pour pouvoir augmenter le nombre de téléchargement quotidien*/
		String token = "?api_token="METTRE_TOKEN_A_LA_SUITE_ICI;
		/*Requête à l'api d'open corporates*/
		String requeteURL = "http://api.opencorporates.com/companies/search?jurisdiction_code=ca_qc&per_page=100&page=";

		requeteURL += pagePres + token;
		//requeteURL += pagePres;
		
		String json = stringOfUrl(requeteURL);
		JsonParser jparse = new JsonParser();
		JsonObject job = jparse.parse(json).getAsJsonObject();
		JsonArray jay = job.get("companies").getAsJsonArray();
		JsonObject jPre = new JsonObject();
		JsonObject jCompagnie = new JsonObject();
		int taille = jay.size();
		int page = job.get("page").getAsInt();

		/*Extraction du JSON*/
		for(int i = 0; i < taille; i++){

			jCompagnie = jay.get(i).getAsJsonObject().getAsJsonObject("company");
			if(jCompagnie.has("name"))
				if(jCompagnie.get("name").toString().length() <= 150){
					attributs[0] = jCompagnie.get("name").toString();
				}else{
					attributs[0] = jCompagnie.get("name").toString().substring(0,149);
				}
			if(jCompagnie.has("company_number"))
				attributs[1] = jCompagnie.get("company_number").toString();
			if(jCompagnie.has("jurisdiction_code"))
				attributs[2] = jCompagnie.get("jurisdiction_code").toString();
			if(jCompagnie.has("incorporation_date"))
				attributs[3] = jCompagnie.get("incorporation_date").toString();
			if(jCompagnie.has("dissolution_date"))
				attributs[4] = jCompagnie.get("dissolution_date").toString();
			if(jCompagnie.has("company_type"))
				attributs[5] = jCompagnie.get("company_type").toString();
			if(jCompagnie.has("registry_url"))
				attributs[6] = jCompagnie.get("registry_url").toString();
			if(jCompagnie.has("branch_status"))
				attributs[7] = jCompagnie.get("branch_status").toString();
			if(jCompagnie.has("inactive"))
				attributs[8] = jCompagnie.get("inactive").toString();
			if(jCompagnie.has("current_status"))
				attributs[9] = jCompagnie.get("current_status").toString();
			if(jCompagnie.has("opencorporates_url"))
				attributs[10] = jCompagnie.get("opencorporates_url").toString();
			if(jCompagnie.has("source"))
				attributs[11] = jCompagnie.get("source").toString();

			/*Insertion dans la BD*/
			insertionAttributs(attributs, i,page);
		}
	}

	/*Méthode de création de la table*/
	public static void createTable() throws ClassNotFoundException, SQLException{

		String creation = "CREATE TABLE IF NOT EXISTS registreEntreprise (" +
				"nom varchar(150) DEFAULT NULL, numero varchar(12), codejurisdiction varchar(6) DEFAULT NULL, dtincorporation date default '0001-01-01', " +
				"dtdissolution date default '0001-01-01', typecompagnie varchar(20) DEFAULT NULL, urlregistre varchar(75) DEFAULT NULL, branchestatut varchar(20) DEFAULT NULL, " +
				"inactive boolean, statutcourant varchar(100) default null, urlsourceouverte varchar(75) DEFAULT NULL, source varchar(50) DEFAULT NULL, " +
				"page INT, noinpage INT, dttmchargement TIMESTAMP DEFAULT NOW(), PRIMARY KEY(numero))";

		String destruction = "DROP TABLE IF EXISTS registreEntreprise";
		String indexNoComp = "CREATE INDEX ixnocomp ON registreEntreprise (numero)";
		String indexNomComp = "CREATE INDEX ixnomcomp ON registreEntreprise (nom)";

		util.Extraction.executerCreation(destruction);

		util.Extraction.executerCreation(creation);

		util.Extraction.executerCreation(indexNoComp);
		util.Extraction.executerCreation(indexNomComp);
	}

	/*Méthode d'insertion des info entreprises dans la table de la BD*/
	public static void insertionAttributs(String[] att, int id, int page) throws ClassNotFoundException, SQLException{

		String requete = "INSERT INTO registreEntreprise (" +
				"nom, numero, codejurisdiction, dtincorporation, dtdissolution, typecompagnie, urlregistre, branchestatut, " +
				"inactive, statutcourant, urlsourceouverte, source, page, noinpage, dttmchargement) VALUES (";

		for(int i = 0; i< att.length; i++){
			if(att[i] != null){
				if(att[i].toUpperCase().equals("NULL")){
					requete += "DEFAULT,";
				}else if (att[i].toUpperCase().equals("TRUE")){
					requete += "1,";
				}else if (att[i].toUpperCase().equals("FALSE")){
					requete += "0,";
				}else{
					requete += "'" + att[i].replaceAll("\"","").replaceAll("'","''").replaceAll("\\\\","") + "',";
				}
			}else{
				requete += "DEFAULT,";
			}

		}
		requete += Integer.toString(page) + ", " + Integer.toString(id) + ", NOW())";
		System.out.println(requete);
		util.Extraction.executerCreation(requete);

	}

	/*Pris de http://stackoverflow.com/questions/7467568/parsing-json-from-url*/
	public static String stringOfUrl(String addr){
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		URL url;
		try {
			url = new URL(addr);

			IOUtils.copy(url.openStream(), output);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return output.toString();
	}


	/*Méthode exploratrice pour extraire les entreprises à même la BD de la Gazette à l'aide de mots clés.*/
	public static void getGazetteEntreprise() throws ClassNotFoundException, SQLException{

		String[] entreprise = new String[8]; 
		entreprise[0] = "COMPAGNIE";
		entreprise[1] = "FIRME";
		entreprise[2] = "ENTREPRISE";
		entreprise[3] = "SOCIETE";
		entreprise[4] = "COLLECTIF";
		entreprise[5] = "MAISON";
		entreprise[6] = "COMMERCE";
		entreprise[7] = "CORPORATION";

		String requete = "SELECT DISTINCT replace(upper(a.nom), char(10),' ') as nom " +
				"FROM (SELECT SUBSTR(text,locate('" + entreprise[0] + "',UPPER(text)),60) AS nom  " +
				"FROM items " +
				"WHERE UPPER(text) LIKE '%" + entreprise[0] + "%' " +
				"UNION " +
				"SELECT SUBSTR(text,locate('" + entreprise[1] + "',UPPER(text)),50) AS nom " +
				"FROM items " +
				"WHERE UPPER(text) LIKE '%" + entreprise[1] + "%' " +
				"AND   UPPER(text) NOT LIKE '%CONFIRME%' " +
				"UNION " +
				"SELECT SUBSTR(text,locate('" + entreprise[2] + "',UPPER(text)),50) AS nom " +
				"FROM items " +
				"WHERE UPPER(text) LIKE '%" + entreprise[2] + "%' " +
				"UNION " +
				"SELECT SUBSTR(text,locate('" + entreprise[3] + "',UPPER(text)),50) AS nom " +
				"FROM items " +
				"WHERE UPPER(text) LIKE '%" + entreprise[3] + "%' " +
				"UNION " +
				"SELECT SUBSTR(text,locate('" + entreprise[4] + "',UPPER(text)),50) AS nom " +
				"FROM items " +
				"WHERE UPPER(text) LIKE '%" + entreprise[4] + "%' " +
				"UNION " +
				"SELECT SUBSTR(text,locate('" + entreprise[5] + "',UPPER(text)),50) AS nom " +
				"FROM items " +
				"WHERE UPPER(text) LIKE '%" + entreprise[5] + "%' " +
				"UNION " +
				"SELECT SUBSTR(text,locate('" + entreprise[6] + "',UPPER(text)),50) AS nom " +
				"FROM items " +
				"WHERE UPPER(text) LIKE '%" + entreprise[6] + "%' " +
				"UNION " +
				"SELECT SUBSTR(text,locate('" + entreprise[7] + "',UPPER(text)),50) AS nom " +
				"FROM items " +
				"WHERE UPPER(text) LIKE '%" + entreprise[7] + "%') AS a " +
				"ORDER BY a.nom";

		ResultSet ent = util.Extraction.executerExtraction(requete);
		ArrayList<String> listEnt = new ArrayList<String>();
		if(ent != null){
			while(ent.next()){
				String nomEnt = ent.getString("nom");
				for(int i = 0; i < entreprise.length; i++){
					if(nomEnt.contains(entreprise[i])){
						nomEnt = nomEnt.substring(nomEnt.indexOf(entreprise[i]) + entreprise[i].length());
					}
				}
				listEnt.add(nomEnt.trim().replaceAll("«","").replaceAll("»","").replaceAll("\\p{Punct}|\\d",""));
			}	
		}
		HashMap<String, Integer> freq = new HashMap<String,Integer>();
		for(int i = 0; i < listEnt.size(); i++){
			String[] mots = listEnt.get(i).split(" ");
			for(String w:Arrays.asList(mots)){
				/*Pour éviter les mots d'une lettre*/
				if(w.length() > 1){
					Integer num=freq.get(w);
					if (num!=null)
						freq.put(w,num+1);
					else
						freq.put(w,1);
				}
			}
		}
		int tailleMap = freq.size();
		System.out.println(freq.toString());
	}

}
