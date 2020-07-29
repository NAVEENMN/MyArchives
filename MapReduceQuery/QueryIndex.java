import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.net.URL;

public class QueryIndex {
	public static void main(String[] args) {
	FileInputStream fstream = null;
	FileInputStream fstreamq = null;
	URL location = QueryIndex.class.getProtectionDomain().getCodeSource().getLocation();
	String path_here = location.getPath();
	try {
		fstream = new FileInputStream(path_here+"/"+args[0]);
		fstreamq = new FileInputStream(path_here+"/"+args[1]);
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
while ((query_words_list = brq.readLine()) != null) {
Str.add(query_words_list);
}
} catch (IOException e) {
// TODO Auto-generated catch block
e.printStackTrace();
}
//Read File Line By Line
try {
while ((strLine = br.readLine()) != null) {
// Print the content on the console
String[] splitArray = strLine.split("\\s+");
//for(int i = 0; i<Str.size();i++){
// if(ArrayUtils.contains( fieldsToInclude, "id" ))
// System.out.println (strLine);
//}
for(String s:splitArray){
for(int i = 0; i<Str.size(); i++){
if(s.contentEquals(Str.get(i))){
System.out.println(strLine);
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
}
