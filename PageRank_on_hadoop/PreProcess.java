package org.myorg;
import java.util.*;
import java.util.regex.*;
/*
 * PreProcess holds the function to clean the data and fetch the titls and links
 */
public class PreProcess
{
	private static final String TitleFormat = "<title>(.+?)</title>";
	private static final String LinkFormat = "\\[\\[(.+?)\\]\\]";
	
	
	private static String CleanLinks(String title)
	{
		if(title.length()>0){
			title = title.substring(0, 1).toUpperCase()+ title.substring(1);// if the first character is uppercase change to lower case 
			title.replace(" ","_");// change space to _
			title.replace("\t", "");//remove all tabs
		}
		return title;
	}

	private String title = null;
	private HashSet<String> links = null;
	private String codedLinks = null;
	private String page = null;

	// constructor
	public PreProcess(String page){
		this.page = page;
	}
	
	// this method returns the title
	public String getTitle(){
		if(title == null){
			// parse title from page
			Pattern p = Pattern.compile(TitleFormat);
			Matcher m = p.matcher(page);
			if(m.find()) title = m.group(1);
		}
		return title;
	}
	
	// Use HashSet to eliminate duplicates
	public HashSet<String> getLinks(){
		if(links == null){
			links = new HashSet<String>();
			Pattern p = Pattern.compile(LinkFormat);
			Matcher m = p.matcher(page);
			while(m.find()){
				String rawLink = m.group(1);
				if(rawLink.contains(":")) continue;//elimibate the continue/refreal section of the link
				int section = rawLink.indexOf("#");
				if(section == 0) continue;
				if(section > 0) rawLink = rawLink.substring(0, section);
				// drop page refrence and "|" character
				int pipe = rawLink.indexOf("|");
				if(pipe >= 0) rawLink = rawLink.substring(0, pipe);
				rawLink = CleanLinks(rawLink).trim();
				if(rawLink.length()>0) links.add(rawLink);
			}
		}
		return links;
	}

	// Encode the links for usage
	public String getCodedLinks(){
		HashSet<String> links = getLinks();
		String code = String.valueOf(links.size());
		for(String l : links){
			code += "||" + l;
		}
		return code;
	}
	public boolean hasLinks(){
		return (getLinks().size() > 0);
	}	
	public void setPage(String page){
		this.page = page;
		title = null;
		links = null;
	}
}
