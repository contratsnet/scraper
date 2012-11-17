package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Extraction {
	
	public static ResultSet executerExtraction(String requete) throws ClassNotFoundException, SQLException{


		Class.forName("com.mysql.jdbc.Driver");

		Connection conn = DriverManager.getConnection(BD,USER,MOT_PASSE);

		Statement stReq = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

		ResultSet extraction = stReq.executeQuery(requete);

		return extraction;

	}
	
	public static void executerCreation(String requete) throws ClassNotFoundException, SQLException{


		Class.forName("com.mysql.jdbc.Driver");

		Connection conn = DriverManager.getConnection(BD,USER,MOT_PASSE);

		Statement stReq = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

		stReq.execute(requete);
		
		stReq.close();
		conn.close();

	}


}
