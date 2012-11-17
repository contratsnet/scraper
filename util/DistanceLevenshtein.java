package util;

public class DistanceLevenshtein {
	
	/*CALCULE LA DISTANCE DE DAMERAULEVENSTEIN VOIR http://fr.wikipedia.org/wiki/Distance_de_Damerau-Levenshtein*/
	public static int damerauLevenstein(String mot1, String mot2){
		
		int l1 = mot1.length();
		int l2 = mot2.length();
		int cout = 0;
		
		int[][] dist = new int[l1][l2];
		
		for(int i = 0; i < l1; i++){
			dist[i][0] = i;
		}
		for(int i = 0; i < l2; i++){
			dist[0][i] = i;
		}
		for(int i = 1; i < l1; i++){
			for(int j = 1; j < l2; j++){
				if(mot1.charAt(i) == mot2.charAt(j)){
					cout = 0;
				}else{
					cout = 1;
				}
				dist[i][j] = Math.min(dist[i-1][j]+1,Math.min(dist[i][j-1]+1,dist[i-1][j-1]+ cout));
				if(i > 1 && j > 1 && mot1.charAt(i) == mot2.charAt(j-1) && mot1.charAt(i-1) == mot2.charAt(j)){
					dist[i][j] = Math.min(dist[i][j],dist[i-2][j-2] + cout);
				}
			}
		}
		return dist[l1-1][l2-1];
	}

}
