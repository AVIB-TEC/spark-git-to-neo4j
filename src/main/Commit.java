package main;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import utils.Config;

public class Commit {
	
	private static Config config = Config.getInstance();
	private String id;
	private String author;
	private String date;
	private String files;
	private String comment;
	
	public Commit(String commitText) {
		Pattern idRegex = Pattern.compile(config.getProperty("regex_commit_id"));
		Pattern authorRegex = Pattern.compile(config.getProperty("regex_commit_author"));
		Pattern dateRegex = Pattern.compile(config.getProperty("regex_commit_date"));
		Pattern commentRegex = Pattern.compile(config.getProperty("regex_commit_comment"));
		Pattern filesRegex = Pattern.compile(config.getProperty("regex_files"));
		
		this.id = idRegex.matcher(commitText).group(0);//.trim();
        this.author = authorRegex.matcher(commitText).group(0);//.trim();
        this.date = dateRegex.matcher(commitText).group(0);//.trim();
        this.comment = commentRegex.matcher(commitText).group(0); //(commitText.match(config.regex_commit_comment)||[""])[0].trim();
        
        String tempFileText = filesRegex.matcher(commitText).group(0);

        /*for (int i =0 ; i <tempFileText ; i++) {
            if (f.trim() == "") continue;

            try {
                this.files.push(new File(f));
            }
            catch (e) {
                console.log("ERROR File");
                console.log(f);
                // throw e;
            }
            
        }*/
	}
	
	
}
