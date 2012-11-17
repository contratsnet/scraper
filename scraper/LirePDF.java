package scraper;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;

public class LirePDF {

	/*Méthode pour aller chercher les noms de fichiers*/
	public static ResultSet getInfo() throws ClassNotFoundException, SQLException{

		String requete = "SELECT DISTINCT filename, uri FROM documents";
		ResultSet res = util.Extraction.executerExtraction(requete);

		return res;

	}

	/*Méthode pour aller chercher les fichiers PDF enregistrés et les transformer en TXT à partir de l'info de la BD de la Gazette*/
	public static void getFichiers(ResultSet res) throws SQLException, IOException {


		String cheminLec = "";
		String nomFichierLec = "";
		String cheminEcr = "";
		String nomFichierEcr = "";
		String adresse = "";

		FileWriter fich = null;

		if(res != null){
			while(res.next()){

				adresse = res.getString("uri");
				if(adresse.contains("montreal")){
					adresse = adresse.replace("ODJ","PV");
					System.out.println("adresse : " + adresse);
					nomFichierLec = res.getString("filename").replaceAll("ODJ","PV");
					nomFichierEcr = nomFichierLec.replace(".pdf",".txt");
					cheminLec = CHEMIN_DE_LECTURE
					cheminEcr = CHEMIN_DÉCRITURE

					if(adresse.toUpperCase().contains("LAVAL")){
						cheminLec += "Laval/";
						cheminEcr += "LavalTXT/";
					}else if(adresse.toUpperCase().contains("MONTREAL")){
						cheminLec += "Montreal/";
						cheminEcr += "MontrealTXT/";
					}
					cheminLec += nomFichierLec;
					cheminEcr += nomFichierEcr;
					PDDocument doc;
					try {
						/*Lecture dans le fichier PDF*/
						System.out.println("Lecture du PDF : " + cheminLec);
						doc = PDDocument.load(cheminLec);
						PDFTextStripper scrapper;
						scrapper = new PDFTextStripper();
						String texte;
						texte = scrapper.getText(doc);

						/*Écriture dans le fichier TXT*/
						System.out.println("Ecriture du TXT : " + cheminEcr);
						fich = new FileWriter(cheminEcr);
						BufferedWriter buf = new BufferedWriter(fich);
						buf.write(texte);
						doc.close();
						buf.close();
						fich.close();

					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
	}

	/*Méthode pour aller chercher les fichiers PDF enregistrés et les transformer en TXT à partir des fichiers extraits à la main*/
	public static void getFichiers(String cheminLec, String cheminEcr) throws SQLException, IOException {

		FileWriter fich = null;
		PDDocument doc;
		String files;
		File folder = new File(cheminLec);
		File[] listOfFiles = folder.listFiles(); 

		for(int i = 0; i < listOfFiles.length; i++){

			try {
				/*Lecture dans le fichier PDF*/
				System.out.println("Lecture du PDF : " + cheminLec + listOfFiles[i].getName());
				doc = PDDocument.load(listOfFiles[i].getPath());
				PDFTextStripper scrapper;
				scrapper = new PDFTextStripper();
				String texte;
				texte = scrapper.getText(doc);

				/*Écriture dans le fichier TXT*/
				System.out.println("Ecriture du TXT : " + cheminEcr + listOfFiles[i].getName().replace(".pdf",".txt"));
				fich = new FileWriter(cheminEcr + "/"+ listOfFiles[i].getName().replace(".pdf",".txt"));
				BufferedWriter buf = new BufferedWriter(fich);
				buf.write(texte);
				doc.close();
				buf.close();
				fich.close();

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
