package execute;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.net.URL;
import java.net.URLConnection;

import java.sql.ResultSet;
import java.sql.SQLException;


public class Downloader {

	/*Méthode allant chercher les PV du conseil Exécutif à partir de la BD de la Gazette*/
	public static void getURL() throws ClassNotFoundException, SQLException, IOException{

		String requete = "SELECT DISTINCT filename, uri FROM documents WHERE uri like '%montreal%'";
		String adresse = "";
		ResultSet res = util.Extraction.executerExtraction(requete);

		if(res != null){
			while(res.next()){
				adresse = res.getString("uri").replaceAll("ODJ","PV");
				getPDF(adresse, res.getString("filename").replaceAll("ODJ","PV"));
			}
		}

	}

	/*Méthode qui extrait les pfd du site*/
	public static void getPDF(String adresse, String nomFichier) throws IOException{

		String chemin = PATH_VERS_FICHIERS;

		if(adresse.toUpperCase().contains("LAVAL")){
			chemin += "Laval/";
		}else if(adresse.toUpperCase().contains("MONTREAL")){
			chemin += "Montreal/";
		}
		chemin += nomFichier;

		/*debut d'un code pris à http://www.gnostice.com/nl_article.asp?id=207&t=How_To_Read_A_PDF_File_From_A_URL_In_Java*/
		URL url1 =	new URL(adresse);
		byte[] ba1 = new byte[1024];
		int baLength;
		FileOutputStream fos1 = new FileOutputStream(chemin);
		try {
			// Contacting the URL
			System.out.print("Connecting to " + url1.toString() + " ... ");
			URLConnection urlConn = url1.openConnection();

			// Checking whether the URL contains a PDF
			if (!urlConn.getContentType().equalsIgnoreCase("application/pdf")) {
				System.out.println("FAILED.\n[Sorry. This is not a PDF.]");
			} else {
				try {

					// Read the PDF from the URL and save to a local file
					InputStream is1 = url1.openStream();
					while ((baLength = is1.read(ba1)) != -1) {
						fos1.write(ba1, 0, baLength);
					}
					fos1.flush();
					fos1.close();
					is1.close();

				} catch (ConnectException ce) {
					System.out.println("FAILED.\n[" + ce.getMessage() + "]\n");
				}
			}

		} catch (NullPointerException npe) {
			System.out.println("FAILED.\n[" + npe.getMessage() + "]\n");
		}
	}
}
