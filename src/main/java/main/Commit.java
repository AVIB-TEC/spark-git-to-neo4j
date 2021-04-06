package main;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import utils.Config;

public class Commit {
	
	private static Config config = Config.getInstance();
	private String id;
	private String author;
	private String date;
	private ArrayList<CommitFile> files;
	private String comment;
	
	public Commit(String commitText) {
		
		Pattern idRegex = Pattern.compile("^(\\s)?([\\w]+)");
		Pattern authorRegex = Pattern.compile(config.getProperty("regex_commit_author"));
		Pattern dateRegex = Pattern.compile(config.getProperty("regex_commit_date"));
		Pattern commentRegex = Pattern.compile(config.getProperty("regex_commit_comment"));
		Pattern filesRegex = Pattern.compile(config.getProperty("regex_files"));
		
		//Matcher
		this.id =  getMatch(idRegex.matcher(commitText));
		this.author =getMatch(authorRegex.matcher(commitText));
		this.date = getMatch(dateRegex.matcher(commitText));
		this.comment = getMatch(commentRegex.matcher(commitText));
		this.files = getMatchFile(filesRegex.matcher(commitText));
		
	}
	
	private String getMatch(Matcher matcher) {
		matcher.find();
		String result = "";
		if(matcher.groupCount() > 0) {
			result = matcher.group(0).trim();
		}
		return result;
	}
	
	private ArrayList<CommitFile> getMatchFile(Matcher matcher) {
		ArrayList<CommitFile> result = new ArrayList<CommitFile>();
		while(matcher.find()) {
			result.add(new CommitFile(matcher.group(0).trim()));
		}
		return result;
	}

	public ArrayList<CommitFile> getFiles() {
		return files;
	}

	public void setFiles(ArrayList<CommitFile> files) {
		this.files = files;
	}
	
}