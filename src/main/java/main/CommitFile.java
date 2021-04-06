package main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import utils.Config;

public class CommitFile  {
	private String name;
	private String fullName;
	private List<String> words;
	
	private static Config config = Config.getInstance();

	public CommitFile(String fileTxt) {
		this.fullName = fileTxt.trim();		
		Pattern fullnameRegex = Pattern.compile(config.getProperty("regex_file_name"));
		this.name =  getMatch(fullnameRegex.matcher(this.fullName));
		fileTxt = fileTxt.replace(this.name, "");
		this.name = this.name.replaceAll("\\.\\w+", "");

		List<String> ignorePaths = Arrays.asList(new String[]{"src","Src","Tests","tests"});
		String[] tempFilter = fileTxt.split("\\/|\\.");
		ArrayList<String> filter= new ArrayList<String>();
		for (int i = 0; i < tempFilter.length; i++) {
			tempFilter[i] = tempFilter[i].replace("\\n", "");
			if(!ignorePaths.contains(tempFilter[i])) {
				filter.add(tempFilter[i]);
			}
		}
		this.words = filter.stream().distinct().collect(Collectors.toList());
	}
	
	private String getMatch(Matcher matcher) {
		String result = "";
		if(matcher.find()) {
			result = matcher.group().trim();
		}
		return result;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public List<String> getWords() {
		return words;
	}

	public void setWords(List<String> words) {
		this.words = words;
	}
}
