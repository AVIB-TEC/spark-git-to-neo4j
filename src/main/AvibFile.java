package main;

import java.util.ArrayList;
import java.util.regex.Pattern;

import utils.Config;

public class AvibFile {
	private static Config config = Config.getInstance();

	private String name;
	private String fullName;
	private String words;

	public AvibFile(String fileTxt) {
			Pattern fileNameRegex = Pattern.compile(config.getProperty("regex_file_name"));
            
			this.fullName = fileTxt.trim();
    		this.name = fileNameRegex.matcher(this.fullName).group(0);

            fileTxt = fileTxt.replace(this.name, "");

            this.name = this.name.replace("\\.\\w+", "");
            
            String[] tempText= fileTxt.split("[\\/ | \\.]");
            for(int i = 0; i<tempText.length; i++) {
            	tempText[i] = tempText[i].replaceAll("\n", "");
            }  
            for(int i = 0; i<tempText.length; i++) {
            	tempText[i] = tempText[i].replaceAll("\n", "");
            }
            
            ArrayList<String> filter = new ArrayList<String>();
            for(int i = 0; i<tempText.length; i++) {
            	if(!config.getProperty("ignore_paths").contains(tempText[i]))
            		filter.add(tempText[i]);
            }

            this.words = filter.get(filter.size()-1);
       
	}
}
