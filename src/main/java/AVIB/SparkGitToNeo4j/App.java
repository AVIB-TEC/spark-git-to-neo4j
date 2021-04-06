package AVIB.SparkGitToNeo4j;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import database.Neo4jHelper;
import entities.FileNode;
import main.Commit;
import utils.Config;

public class App {

	private String workDir = System.getProperty("user.dir");
	private static Config config = Config.getInstance();
	
	public static void main(String[] args) {
		long startTime = System.nanoTime();
		
		App main = new App();
		Neo4jHelper helper = new Neo4jHelper();
		if(!main.getGitData()) {
			System.out.println("Error getting git Data.");
			return;
		}
		String commits = main.getCommits();
		ArrayList<Commit> commitList = main.createCommits(commits);	
		
		int totalFiles = 0;
		int totalRelationships = 0;
		//Neo4j
		for (int i = 0; i < commitList.size(); i++) {
			ArrayList<FileNode> files = helper.searchFiles(commitList.get(i));
			int savedFiles = helper.saveCommit(commitList.get(i),files);
			totalFiles += files.size();
	        totalRelationships += savedFiles;
		}
		
		long endTime = System.nanoTime();
		long totalTime = endTime - startTime;
		System.out.println("********* ===== Totales ====== ***********");
		System.out.println("Commits: "+commitList.size());
		System.out.println("Files found: "+ totalFiles);
		System.out.println("Relationships created: "+totalRelationships);
		System.out.println("Execution time: "+ totalTime+" ns");
		System.out.println("********** ======= Fin ======= ***********");
	}
	
	private ArrayList<Commit> createCommits(String text) {
		ArrayList<Commit> commits = new ArrayList<Commit>();
		String[] commitSplit = text.split("(^|\\n)(commit+(?=\\s{1}\\w{40}\\n))");
		for (int i = 0; i < commitSplit.length; i++) {
			if(commitSplit[i].equals("")) {
				continue;
			}
			Commit temp = new Commit(commitSplit[i]);
						
			if(temp.getFiles().size() > 0 ) {
				commits.add(temp);
			}
		}
		return commits;
	}
	
	private boolean getGitData() {
		File tempFile = new File(workDir + "\\public\\log.txt"); 
	    if (!deleteFile(tempFile)) {
	    	return false;
	    }
	    
	    boolean isWindows = System.getProperty("os.name").toLowerCase().startsWith("windows");
	    if(isWindows) {
	    	List<ProcessBuilder> builders = Arrays.asList(
		    	      new ProcessBuilder("cmd.exe", "/c","if exist", workDir + "\\public\\repo\\", "rmdir", "/s", "/q", workDir + "\\public\\repo\\"), 
		    	      new ProcessBuilder("cmd.exe", "/c","mkdir", "repo"),
		    	      new ProcessBuilder("cmd.exe", "/c","git", "clone", config.getProperty("repository_url"), workDir + "\\public\\repo"),
		    	      new ProcessBuilder("cmd.exe", "/c","git --git-dir=./repo\\.git   log --stat --name-only >> "+ workDir+"\\"+config.getProperty("file_log_url")),
		    	      new ProcessBuilder("cmd.exe", "/c","rmdir", "/s", "/q",  workDir + "\\public\\repo\\")  
		    		);
	    	for (int i =0; i< builders.size();i++) {
				builders.get(i).directory(new File("public"));
			}
		    try {
				executeCommand(builders);
			} catch (Exception e) {
				System.out.println(e);
				return false;
			}
	    }
	    else {
	    	System.out.println("Linux not yet supported");
	    }	
	    return true;
	}
	
	public boolean deleteFile(File file) {
		if(file.exists()) {
			if(file.delete()) {
				return true;
			}
			else {
		    	System.out.println("Error deleting file: "+file.getAbsolutePath());
				return false;
			}
		}else {
			System.out.println("File does not exist: " +file.getAbsolutePath());
			return true;
		}
	}
	
	public String getCommits() {
		String filePath = this.workDir+"\\public\\log.txt";
		String content = null;
	    try {
	    	byte[] encoded = Files.readAllBytes(Paths.get(filePath));
	    	content = new String(encoded, StandardCharsets.UTF_8);
	    } catch (IOException e) {
	    	e.printStackTrace();
	    }
	    return content;
	}
	
	public boolean executeCommand(List<ProcessBuilder> builders) throws IOException, InterruptedException {
		
		for (int i = 0; i < builders.size(); i++) {
			ProcessBuilder builder = builders.get(i);
			builder.directory(new File("public"));
		    builder.redirectErrorStream(true);
		    builder.redirectOutput(ProcessBuilder.Redirect.INHERIT);
		    builder.redirectError(ProcessBuilder.Redirect.INHERIT);
		    builder.redirectInput(ProcessBuilder.Redirect.INHERIT);
		    Process process = builder.start();
		    int exitCode = process.waitFor();
		    if(exitCode != 0) {
		    	return false; 
		    }
		    System.out.println("Done running command "+i);
		    process.destroy();
		}
		return true;
		
	}
}
