import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;


public class SimpleRest {
    
	
	static String request = "http://rest.kegg.jp/list/hsa"; 								//задаем адрес сервера
	static String filePath = "";															//путь к файлу (в корне проекта по умолчанию)
	static String fileName = "genes.txt";													//имя файла
	static String resultsFileName = "searchResults.txt";
	
    /*
     * загрузка данных по http, берет адрес и выдает результат в виде строки
     */
	public static List<String> httpGet(String urlStr) throws IOException {
		System.out.println("Загрузка данных из " + urlStr + "\n");
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
	public static List<String> genesGet(String filePath, String fileName) throws IOException {
		List<String> tempNamesList = new ArrayList<String>(); 								//временный список имен генов
		System.out.println("Загрузка списка файлов...");
		BufferedReader fileReader;
		try {
			fileReader = new BufferedReader(new FileReader(filePath + fileName)); 			//построчно читаем в line
			String line = "";
			while ((line = fileReader.readLine()) != null) {								//пока в line есть что-то дописываем в список
			    line = line.trim();															// обрезаем пробелы и нечитаемые символы
				if (line.isEmpty()) {																//пропускаем пустые строчки
					//System.out.println("пустая строка");
				} else if (line.contains(" ") || line.contains("\t")) {
			    	System.out.println("Ошибка в имени гена: \"" + line +"\""); 						//пропускаем гены с пробелами в имени и сообщаем об этом
			    } else {
				tempNamesList.add(line);														//все проверки пройдены - дописываем имя в список													
			    //System.out.println(line.length());
			    	}
				}
			}
			catch (IOException e) {
			    e.printStackTrace();
			}
		System.out.println("Из файла " + filePath + fileName + " загружено "  + tempNamesList.size() + " генов.\n");
		return tempNamesList;
	}
	/*
	 * поиск генов в выдаче сервера
	 */
	public static List<String> searchGenes(List<String> geneNames, List<String> serverOut) {
		System.out.println("Поиск генов...");
		List<String> results = new ArrayList<String>();
		for (String gene : geneNames) { 											//проходим по списку имен файлов
        	String outText = ""; 													// сюда дописываем найденные строки содержащие имя гена
        	
           	//String request = "http://rest.kegg.jp/find/hsa/" + gene; простой способ поиска, без подгрузки данных с сервера
        	
        	System.out.println("Ген " + gene + ":");
        	results.add("Ген " + gene + ":");
        	for (String line : serverOut) { 										//проходим по списку с сервера
        		if (line.contains(" " + gene + ",")||line.contains("\t" + gene + ",")||line.contains("\t" + gene + ";")) { //проверка на наличие гена в начале, середине или конце 
        			outText = outText + "\t" + line + "\n";

            		//outText.concat("\t" + line + "\n"); //не работает (((
        		}
        	}

        	if (outText.isEmpty()) {
        		System.out.println("\tСовпадений не найдено\n");
        		results.add("\tСовпадений не найдено\n");
        	} else {
        		System.out.println(outText);
    			results.add(outText);
        	}
		}
		return results;
	}
	
	/*
	 * Сохранение данных в файл
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
    	String errors = ""; 															//для вывода ошибок
    	List<String> str = new ArrayList<String>(); 								//список для хранения выдачи сервера
    	List<String> geneNames = new ArrayList<String>(); 							//список для хранения имен генов
    	List<String> results = new ArrayList<String>();										//для вывода результатов
    	 /*
         * загружаем список генов 
         */
         try {
			geneNames = genesGet(filePath, fileName);
		} catch (IOException e) {
			System.out.println("ошибка доступа к файлу");
			errors = errors + "ошибка доступа к файлу";
			e.printStackTrace();
		}
  
    	
    	/*
    	 * Делаем rest-запрос
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
         * Ищем и выводим результат в консоль
         */
        if (errors == "") {
            results = searchGenes(geneNames, str);
        }
        
        /*
         * выводим результат в файл
         */
        if (results.isEmpty()) {
        } else {
        	try {
				saveResults(results, filePath, resultsFileName);
				System.out.println("Результаты сохранены в файл " + filePath + resultsFileName);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
        
        System.out.println(errors);
    }
}

