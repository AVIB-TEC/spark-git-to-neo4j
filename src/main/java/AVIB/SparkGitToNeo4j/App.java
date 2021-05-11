package AVIB.SparkGitToNeo4j;

//import static spark.Spark.get;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;

import database.Neo4jHelper;
import entities.FileNode;
import main.Commit;
import utils.Config;

public class App implements Serializable {

	private static final long serialVersionUID = 1069136416786151641L;
	private String workDir = System.getProperty("user.dir");
	private static Config config = Config.getInstance();
	private static AvibSparkContext avibSpark = AvibSparkContext.getInstance();
	private static JavaSparkContext sparkContext;
	
	private int totalFiles;
	private int totalRelationships;
	private int cont;
	
	
	private String processData() {
		long startTime = System.nanoTime();
		Neo4jHelper helper = new Neo4jHelper();
		if(!getGitData()) {
			System.out.println("Error getting git Data.");
			return "Error getting git Data";
		}
		
		String commits = getCommits();
		JavaRDD<Commit> commitList = createCommits(commits);	
		
		totalFiles = 0;
		totalRelationships = 0;
		cont = 0;
		System.out.println("--------- Processing commit list ---------");

		commitList.foreach((commit) ->{
			List<FileNode> files  = helper.searchFiles(commit);
			totalFiles+= files.size();
			System.out.println("--------- File list created for commit #"+cont+"---------");
			cont+=1;
			int savedFiles = helper.saveCommit(commit, files);
			totalRelationships += savedFiles;
			
		});
		
		long endTime = System.nanoTime();
		long totalTime = endTime - startTime;
		
		String message = "********* ===== Resulst ====== ***********\n";
		message+= "Commits: "+commitList.count() +"\n";
		message+= "Files found: "+ totalFiles +"\n";
		message+= "Relationships created: "+totalRelationships +"\n";
		message+= "Execution time: "+ totalTime+" ns "+"\n";
		message+= "********** ======= End ======= ***********\n";
		System.out.println("********* ===== Totales ====== ***********");
		System.out.println("Commits: "+commitList.count());
		System.out.println("Files found: "+ totalFiles);
		System.out.println("Relationships created: "+totalRelationships);
		System.out.println("Execution time: "+ totalTime+" ns");
		System.out.println("********** ======= Fin ======= ***********");
		sparkContext.close();
		return  message;
	}
	
	private JavaRDD<Commit> createCommits(String text) {		
		List<String> splitData = Arrays.asList(text.split("(^|\\n)(commit+(?=\\s{1}\\w{40}\\n))"));
		JavaRDD<String> data = sparkContext.parallelize(splitData).filter(row -> !row.equals(""));
		JavaRDD<Commit> commitList = data.map(row -> new Commit(row)).filter(commit -> commit.getFiles().size() > 0);
		return commitList;
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
	    	System.out.println("Running Linux ");
	    	List<ProcessBuilder> builders = Arrays.asList(
	    			new ProcessBuilder("/bin/bash", "/c","if exist", workDir + "/public/repo/", "rm", "-f", workDir + "/public/repo/"), 
	    			new ProcessBuilder("/bin/bash", "/c","mkdir", "public/repo"),
	    			new ProcessBuilder("/bin/bash", "/c","git", "clone", config.getProperty("repository_url"), workDir + "/public/repo"),
	    			new ProcessBuilder("/bin/bash", "/c","git --git-dir=./repo/.git   log --stat --name-only >> "+ workDir+"/"+config.getProperty("file_log_url")),
	    			new ProcessBuilder("/bin/bash", "/c","rm", "-RF",  workDir + "/public/repo/")  
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
	
	public static void main(String[] args) {
		App main = new App();
		sparkContext = avibSpark.getContext();
		main.processData();
		/*get("/", (req, res) -> {
			return main.processData();
	    });*/
	}
}
