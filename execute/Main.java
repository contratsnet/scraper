package execute;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;

public class Main {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		
		/*FAIRE EXÉCUTER LES DIFFÉRENTS MODULES...*/
		
		/*ALLER CHERCHER LES PV SUR LE SITE DE LA VILLE ET TRANSFORMER LES PDF EN .TXT*/
		Downloader.getURL();
		ResultSet res = scraper.LirePDF.getInfo();
		scraper.LirePDF.getFichiers(PATH_DES_FICHIERS_PDF,PATH_OU_LES_FICHIERS_TXT_SERONT_ENVOYÉS);
		scraper.Entreprises.getRegistre();
		scraper.Entreprises.createTable();
		
		/*SCRIPT SQL POUR SAVOIR OÙ EN EST RENDU LE SCRAPPAGE DE OPENCORPORATES*/
		/*String info = "SELECT maxpage, MAX(noinpage) as maxligne " +
		"FROM (SELECT a.maxpage, b.noinpage " +
		"FROM (SELECT MAX(page) as maxpage from registreentreprise) AS a inner join registreentreprise as b " +
		"ON b.page = a.maxpage" +
		") GROUP BY maxpage";
		
		ResultSet res = util.Extraction.executerExtraction(info);
		
		if(res != null){
			res.first();
			pageDep = res.getInt("maxpage");
			noInPage = res.getInt("noinpage");
		}*/
		
		/*POUR ROULER LE SCRAPPER D'OPENCORPORATE*/
		//i < 17054
		for(int i = 9040; i < 17054; i++){
			scraper.Entreprises.getRegistre(Integer.toString(i), 12);
		}
	}

}
