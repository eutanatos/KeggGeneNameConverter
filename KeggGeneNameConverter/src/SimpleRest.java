import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;


public class SimpleRest {
    /*
     * �������� ������ �� http, ����� ����� � ������ ��������� � ���� ������
     */
	public static List<String> httpGet(String urlStr) throws IOException { 
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
	public static List<String> genesGet(String filePathStr) throws IOException {
		List<String> names = new ArrayList<String>();
		
		BufferedReader fileReader;
		try {
			fileReader = new BufferedReader(new FileReader(filePathStr + "genes.txt"));
			String line = "";
			while ((line = fileReader.readLine()) != null) {
			    names.add(line);
			    System.out.println(line);
				}
			}
			catch (IOException e) {
			    e.printStackTrace();
			}
		return names;
	}
	/*
	 * ����� ����� � ������ �������
	 */
	public static void searchGenes(List<String> geneNames, List<String> str) {
		for (String gene : geneNames) { //�������� �� ������ ���� ������
        	String outText = ""; // ���� ���������� ��������� ������ ���������� ��� ����
           	//String request = "http://rest.kegg.jp/find/hsa/" + gene; ������� ������ ������, ��� ��������� ������ � �������
        	System.out.println("��� " + gene + ":");
        	for (String line : str) { //�������� �� ������ � �������
        		if (line.contains(" " + gene + ",")||line.contains("\t" + gene + ",")||line.contains("\t" + gene + ";")) { //�������� �� ������� ���� � ������, �������� ��� ����� 
        			outText = outText + "\t" + line + "\n";
            		//outText.concat("\t" + line + "\n"); //�� �������� (((
        		}
        	}

        	if (outText.isEmpty()) {
        		System.out.println("\t���������� �� �������\n");
        	} else {
        		System.out.println(outText);
        	}
		}
	}
	
    public static void main(String[] args){
        
    	String errors = ""; //��� ������
    	String request = "http://rest.kegg.jp/list/hsa"; //������ ����� �������
    	List<String> str = new ArrayList<String>(); //�������������� ������ ��� �������� ������ �������
    	String path = "";
    	 List<String> geneNames = new ArrayList<String>(); //�������������� ������ ��� �������� ���� �����
    	
    	 /*
         * ��������� ������ ����� 
         */
         try {
			geneNames = genesGet(path);
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
         * ���� � ������� ���������
         */
        if (errors == "") {
        	System.out.println("�� ����� " + path +" �������� " + "���������"  + geneNames.size() + " �����");
            searchGenes(geneNames, str);
        } else {
        	System.out.println(errors);
        }
        System.out.println(errors);
    }
}

