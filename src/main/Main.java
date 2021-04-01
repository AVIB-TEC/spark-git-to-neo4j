package main;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import utils.*;

public class Main {
	
	private String workDir = System.getProperty("user.dir");
	private static Config config = Config.getInstance();
	
	
	public static void main(String[] args) throws IOException, InterruptedException {
		
		Main main = new Main();
		
		File tempFile = new File(main.workDir + "\\public\\log.txt"); 
	    if (!main.deleteFile(tempFile)) {
	    	return;
	    }
	    
	    boolean isWindows = System.getProperty("os.name").toLowerCase().startsWith("windows");
	    if(isWindows) {
	    	List<ProcessBuilder> builders = Arrays.asList(
		    	      new ProcessBuilder("cmd.exe", "/c","if exist", main.workDir + "\\public\\repo\\", "rmdir", "/s", "/q", main.workDir + "\\public\\repo\\"), 
		    	      new ProcessBuilder("cmd.exe", "/c","mkdir", "repo"),
		    	      new ProcessBuilder("cmd.exe", "/c","git", "clone", config.getProperty("repository_url"), main.workDir + "\\public\\repo"),
		    	      new ProcessBuilder("cmd.exe", "/c","git --git-dir=./repo\\.git   log --stat --name-only >> "+ main.workDir+"\\"+config.getProperty("file_log_url")),
		    	      new ProcessBuilder("cmd.exe", "/c","rmdir", "/s", "/q",  main.workDir + "\\public\\repo\\")  
		    		);
	    	for (int i =0; i< builders.size();i++) {
				builders.get(i).directory(new File("public"));
			}
		    main.executeCommand(builders);
	    }
	    else {
	    	System.out.println("Linux not yet supported");
	    }

	  
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
	
	public void getCommits() {
		try {
			  File tempFile = new File(this.workDir + "\\resources\\public\\log.txt"); 
		      Scanner myReader = new Scanner(tempFile);
		      while (myReader.hasNextLine()) {
		        String data = myReader.nextLine();
		        System.out.println(data);
		      }
		      myReader.close();
		    } catch (FileNotFoundException e) {
		      System.out.println("An error occurred.");
		      e.printStackTrace();
		    }
	}
	
	
	public boolean executeCommand(List<ProcessBuilder> builders) throws IOException, InterruptedException {
		
		for (int i = 0; i < builders.size(); i++) {
			ProcessBuilder builder = builders.get(i);
			builder.directory(new File("public"));
		    builder.redirectErrorStream(true);
		    File log =new File("public\\java-version.log");
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
