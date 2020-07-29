import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.net.URL;


public class QueryFullIndex {

	public static void main(String[] args) {
        FileInputStream fstream = null;
        FileInputStream fstreamq = null;
	URL location = QueryFullIndex.class.getProtectionDomain().getCodeSource().getLocation();
	String path_here = location.getPath();
		try {
			fstreamq = new FileInputStream(path_here+"/"+args[0]);//arg0 query file name
			fstream = new FileInputStream(path_here+"/"+args[1]);//arg1 index file name	
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
        BufferedReader brq = new BufferedReader(new InputStreamReader(fstreamq));
        String strLine;
        String query_words_list;
        ArrayList<String> Str = new ArrayList<String>();
      //Read File Line By Line
        try {
			while ((query_words_list = brq.readLine()) != null)   {
				Str.add(query_words_list);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        //Read File Line By Line
        String reduced_file_list = null;


        File file = new File(path_here+"/"+args[2]);
        BufferedWriter output = null;
		try {
			output = new BufferedWriter(new FileWriter(file));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        
        try {
			while ((strLine = br.readLine()) != null)   {
			  // Print the content on the console
			String[] splitArray = strLine.split("\\s+");
			for(String s:splitArray){
				for(int i = 0; i<Str.size(); i++){
					if(s.contentEquals(Str.get(i))){
						strLine.replaceAll("\\s","");
						//System.out.println("tokenizing:" + strLine);
						reduced_file_list = tokenize_file_names(strLine, path_here);
						System.out.println(Str.get(i)+":"+"\n"+reduced_file_list);
						try {
					          output.write(Str.get(i)+":"+"\n"+reduced_file_list);
					          
					        } catch ( IOException e ) {
					           e.printStackTrace();
					        }
					}
				}
			}
			
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        //Close the input stream
        try {
			br.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
        try {
			output.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
	}
	public static String extractNumber(final String str) {                

	    if(str == null || str.isEmpty()) return "";

	    StringBuilder sb = new StringBuilder();
	    boolean found = false;
	    for(char c : str.toCharArray()){
	        if(Character.isDigit(c)){
	            sb.append(c);
	            found = true;
	        } else if(found){
	            // If we already found a digit before and this char is not a digit, stop looping
	            break;                
	        }
	    }

	    return sb.toString();
	}
	
	public static String tokenize_file_names(String line, String path_here){
		Set<String> file_names_non_duplicate = new HashSet<String>(Arrays.asList(line.split("@|\\+|\\s")));
		ArrayList<String> nr_file_names = new ArrayList<String>(file_names_non_duplicate);
		ArrayList<String> final_file_names = new ArrayList<String>();
		String current_file = null;
		for(int i=0; i<file_names_non_duplicate.size();i++){
			//System.out.println(extractNumber(nr_file_names.get(i)).toString());
		if (nr_file_names.get(i).contains(".txt")){
		current_file = nr_file_names.get(i);
		}
		else{
			String index = extractNumber(nr_file_names.get(i).toString());
			try{
			Integer x = Integer.valueOf(Integer.parseInt(index));
			String file_loc = path_here+"/input/"+current_file;
			RandomAccessFile file = new RandomAccessFile(file_loc, "r");
		    file.seek(x);
		    String line_from_index = file.readLine();
	        file.close();
	        String tru_ln = current_file+"@"+x.toString()+"->"+line_from_index+"\n";
	        final_file_names.add(tru_ln);
			}catch(Exception e){
				//System.out.print("non integer text found" + nr_file_names.get(i));//
			}
		}
		}
		String file_lst = "";
		for(int i=0; i<final_file_names.size();i++){
			file_lst += (final_file_names.get(i));
		}
		return(file_lst);
	}
	
}


