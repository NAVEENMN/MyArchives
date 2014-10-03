import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;


public class QueryFullIndex {

	public static void main(String[] args) {
        FileInputStream fstream = null;
        FileInputStream fstreamq = null;
		try {
			fstream = new FileInputStream("/home/nmysore/Documents/hadoop/output/part-00000");
			fstreamq = new FileInputStream("/home/nmysore/Documents/hadoop/query_input/query.txt");
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
        try {
			while ((strLine = br.readLine()) != null)   {
			  // Print the content on the console
			String[] splitArray = strLine.split("\\s+");
			for(String s:splitArray){
				for(int i = 0; i<Str.size(); i++){
					if(s.contentEquals(Str.get(i))){
						reduced_file_list = tokenize_file_names(strLine);
						System.out.println(Str.get(i)+":"+reduced_file_list);
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
		}	}
	public static String tokenize_file_names(String line){
		Set<String> file_names_non_duplicate = new HashSet<String>(Arrays.asList(line.split("@|\\+|\\s")));
		ArrayList<String> nr_file_names = new ArrayList<String>(file_names_non_duplicate);
		ArrayList<String> final_file_names = new ArrayList<String>();
		for(int i=0; i<file_names_non_duplicate.size();i++){
		if (nr_file_names.get(i).contains(".txt")){
		final_file_names.add(nr_file_names.get(i));
		}
		}
		String file_lst = "";
		for(int i=0; i<final_file_names.size();i++){
			file_lst += (final_file_names.get(i)+", ");
		}
		return(file_lst);
	}
	
}

