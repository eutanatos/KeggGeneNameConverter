import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;


public class SimpleRest {
    /*
     * загрузка данных по http, берет адрес и выдает результат в виде строки
     */
	public static List<String> httpGet(String urlStr) throws IOException { 
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        if (conn.getResponseCode() != 200) {
            throw new IOException(conn.getResponseMessage());
        } // Buffer the result into a string
        BufferedReader rd = new BufferedReader(new InputStreamReader(
           conn.getInputStream()));
        //загружаем выдачу в список
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
	 * загрузка списка имен генов
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
	 * поиск генов в выдаче сервера
	 */
	public static void searchGenes(List<String> geneNames, List<String> str) {
		for (String gene : geneNames) { //проходим по списку имен файлов
        	String outText = ""; // сюда дописываем найденные строки содержащие им€ гена
           	//String request = "http://rest.kegg.jp/find/hsa/" + gene; простой способ поиска, без подгрузки данных с сервера
        	System.out.println("√ен " + gene + ":");
        	for (String line : str) { //проходим по списку с сервера
        		if (line.contains(" " + gene + ",")||line.contains("\t" + gene + ",")||line.contains("\t" + gene + ";")) { //проверка на наличие гена в начале, середине или конце 
        			outText = outText + "\t" + line + "\n";
            		//outText.concat("\t" + line + "\n"); //не работает (((
        		}
        	}

        	if (outText.isEmpty()) {
        		System.out.println("\t—овпадений не найдено\n");
        	} else {
        		System.out.println(outText);
        	}
		}
	}
	
    public static void main(String[] args){
        
    	String errors = ""; //дл€ ошибок
    	String request = "http://rest.kegg.jp/list/hsa"; //задаем адрес сервера
    	List<String> str = new ArrayList<String>(); //инициализируем список дл€ хранени€ выдачи сервера
    	String path = "";
    	 List<String> geneNames = new ArrayList<String>(); //инициализируем список дл€ хранени€ имен генов
    	
    	 /*
         * загружаем список генов 
         */
         try {
			geneNames = genesGet(path);
		} catch (IOException e) {
			System.out.println("ошибка доступа к файлу");
			errors = errors + "ошибка доступа к файлу";
			e.printStackTrace();
		}
  
    	
    	/*
    	 * ƒелаем rest-запрос
    	 */
        if (errors == "") {
        	
        	try {
        		str = httpGet(request); //загружаем данные с сервера
        	} catch (IOException e) {
        		errors = errors + "ошибка загрузки данных";
        		e.printStackTrace();
        	}
        } 
        
      
        /*
         * »щем и выводим результат
         */
        if (errors == "") {
        	System.out.println("»з файла " + path +" им€файла " + "загружены"  + geneNames.size() + " генов");
            searchGenes(geneNames, str);
        } else {
        	System.out.println(errors);
        }
        System.out.println(errors);
    }
}

