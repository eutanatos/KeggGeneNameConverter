import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;


public class SimpleRest {
    
	
	static String request = "http://rest.kegg.jp/list/hsa"; 								//������ ����� �������
	static String filePath = "";															//���� � ����� (� ����� ������� �� ���������)
	static String fileName = "genes.txt";													//��� �����
	static String resultsFileName = "searchResults.txt";
	
    /*
     * �������� ������ �� http, ����� ����� � ������ ��������� � ���� ������
     */
	public static List<String> httpGet(String urlStr) throws IOException {
		System.out.println("�������� ������ �� " + urlStr + "\n");
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        if (conn.getResponseCode() != 200) {
            throw new IOException(conn.getResponseMessage());
        } // Buffer the result into a string
        BufferedReader rd = new BufferedReader(new InputStreamReader(
           conn.getInputStream()));
        //��������� ������ � ������
        List<String> sb = new ArrayList<String>();
        String line;
        while ((line = rd.readLine()) != null) {
            sb.add(line);
        }
        rd.close();
        conn.disconnect();
        return sb;
    }
	/*
	 * �������� ������ ���� �����
	 */
	public static List<String> genesGet(String filePath, String fileName) throws IOException {
		List<String> tempNamesList = new ArrayList<String>(); 								//��������� ������ ���� �����
		System.out.println("�������� ������ ������...");
		BufferedReader fileReader;
		try {
			fileReader = new BufferedReader(new FileReader(filePath + fileName)); 			//��������� ������ � line
			String line = "";
			while ((line = fileReader.readLine()) != null) {								//���� � line ���� ���-�� ���������� � ������
			    line = line.trim();															// �������� ������� � ���������� �������
				if (line.isEmpty()) {																//���������� ������ �������
					//System.out.println("������ ������");
				} else if (line.contains(" ") || line.contains("\t")) {
			    	System.out.println("������ � ����� ����: \"" + line +"\""); 						//���������� ���� � ��������� � ����� � �������� �� ����
			    } else {
				tempNamesList.add(line);														//��� �������� �������� - ���������� ��� � ������													
			    //System.out.println(line.length());
			    	}
				}
			}
			catch (IOException e) {
			    e.printStackTrace();
			}
		System.out.println("�� ����� " + filePath + fileName + " ��������� "  + tempNamesList.size() + " �����.\n");
		return tempNamesList;
	}
	/*
	 * ����� ����� � ������ �������
	 */
	public static List<String> searchGenes(List<String> geneNames, List<String> serverOut) {
		System.out.println("����� �����...");
		List<String> results = new ArrayList<String>();
		for (String gene : geneNames) { 											//�������� �� ������ ���� ������
        	String outText = ""; 													// ���� ���������� ��������� ������ ���������� ��� ����
        	
           	//String request = "http://rest.kegg.jp/find/hsa/" + gene; ������� ������ ������, ��� ��������� ������ � �������
        	
        	System.out.println("��� " + gene + ":");
        	results.add("��� " + gene + ":");
        	for (String line : serverOut) { 										//�������� �� ������ � �������
        		if (line.contains(" " + gene + ",")||line.contains("\t" + gene + ",")||line.contains("\t" + gene + ";")) { //�������� �� ������� ���� � ������, �������� ��� ����� 
        			outText = outText + "\t" + line + "\n";

            		//outText.concat("\t" + line + "\n"); //�� �������� (((
        		}
        	}

        	if (outText.isEmpty()) {
        		System.out.println("\t���������� �� �������\n");
        		results.add("\t���������� �� �������\n");
        	} else {
        		System.out.println(outText);
    			results.add(outText);
        	}
		}
		return results;
	}
	
	/*
	 * ���������� ������ � ����
	 */
	public static void saveResults(List<String> results, String filePath, String resultsFileName) throws IOException {
		PrintStream out;
	    try {
	        out = new PrintStream(filePath + resultsFileName);
	        for (String string : results) {
	        	out.println(string);
			}
	    }
	    catch (IOException e) {
	        e.printStackTrace();
	    }

	}
	
	
    public static void main(String[] args){
    	String errors = ""; 															//��� ������ ������
    	List<String> str = new ArrayList<String>(); 								//������ ��� �������� ������ �������
    	List<String> geneNames = new ArrayList<String>(); 							//������ ��� �������� ���� �����
    	List<String> results = new ArrayList<String>();										//��� ������ �����������
    	 /*
         * ��������� ������ ����� 
         */
         try {
			geneNames = genesGet(filePath, fileName);
		} catch (IOException e) {
			System.out.println("������ ������� � �����");
			errors = errors + "������ ������� � �����";
			e.printStackTrace();
		}
  
    	
    	/*
    	 * ������ rest-������
    	 */
        if (errors == "") {
        	
        	try {
        		str = httpGet(request); //��������� ������ � �������
        	} catch (IOException e) {
        		errors = errors + "������ �������� ������";
        		e.printStackTrace();
        	}
        } 
       
      
        /*
         * ���� � ������� ��������� � �������
         */
        if (errors == "") {
            results = searchGenes(geneNames, str);
        }
        
        /*
         * ������� ��������� � ����
         */
        if (results.isEmpty()) {
        } else {
        	try {
				saveResults(results, filePath, resultsFileName);
				System.out.println("���������� ��������� � ���� " + filePath + resultsFileName);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
        
        System.out.println(errors);
    }
}

