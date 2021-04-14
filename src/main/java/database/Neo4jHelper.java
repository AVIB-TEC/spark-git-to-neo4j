package database;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;

import AVIB.SparkGitToNeo4j.AvibSparkContext;
import entities.FileNode;
import main.Commit;
import main.CommitFile;
import utils.Config;

public class Neo4jHelper implements Serializable {
	
	private static final long serialVersionUID = -526782322927255560L;
	
	private static Config config = Config.getInstance();
    private String projectId = config.getProperty("neo4j_project_id");
	private int recordCoincidence;
	private static AvibSparkContext avibSpark = AvibSparkContext.getInstance();
	private static JavaSparkContext sparkContext;
	private int maxCoincidence;
	private FileNode maxRecord;
	private int cont;
	private int savedFiles;
	
	public Neo4jHelper() {
		sparkContext = avibSpark.getContext();
	}
	
	public List<FileNode> searchFiles(Commit commit){
		List<FileNode> filesToSave = new ArrayList<FileNode>();
		List<CommitFile> filesList = commit.getFiles();
		JavaRDD<CommitFile> files2 = sparkContext.parallelize(filesList);
		files2.foreach(file ->{
			 Connector conn = new Connector();
        	 List<FileNode> resultList = conn.searchFiles(projectId, file.getName());
        	 JavaRDD<FileNode> result = sparkContext.parallelize(resultList);
        	 long size =result.count(); 
        	 if(size == 1) {
              	filesToSave.add(result.collect().get(0));
        	 }
             else if(size > 1){
             	maxCoincidence = 0;
                maxRecord = result.collect().get(0);
                if(file.getWords().size() >=1) {
                	result.foreach(item ->{
                		this.recordCoincidence = 0;
                 		List<String> wordsList = file.getWords();
                 		JavaRDD<String> words = sparkContext.parallelize(wordsList);
                 		words.foreach(word -> {
                 			if(item.getQualifiedname().toLowerCase().indexOf(word.toLowerCase())> -1) {
                 				this.recordCoincidence++;
  							}
                 		});
                        if (recordCoincidence > maxCoincidence) {
                        	maxCoincidence = recordCoincidence;
                            maxRecord = item;
                        }
                	});
                 }
                 filesToSave.add(maxRecord);
             }
        	 conn.close();
        });
        return filesToSave;
	}

	public int saveCommit(Commit commit, JavaRDD<FileNode> files) {
		
		System.out.println("--------- Saving commit ---------");
		long filesSize = files.count();
		if(files != null && filesSize < 1){
			return 0;
		}
		String comment  = sanitize(commit.getComment());
		savedFiles = 0;
		cont = 1;
		files.foreach(file ->{
			if(cont %10 ==0) {
				System.out.println("Checked file "+cont+" of "+filesSize);
			}
            cont+=1;
            Connector conn = new Connector();
            conn.saveCommit(projectId, commit,file, comment);
            savedFiles+=1;
            FileNode current = file;
            files.foreach(file2 ->{
            	if(current != file2) {
            		savedFiles++;
            		Connector conn2 = new Connector();
               		conn2.commitFileRelationship(projectId, current, file2);
               		conn2.close();
            	}
            });
            conn.close();
		});

		System.out.println("Commit: "+commit.getId());
		System.out.println("Archivos encontrados: "+filesSize);
		System.out.println("Relaciones creadas: "+savedFiles);
		System.out.println("*****************");
        return savedFiles;
	}
	
	
	
	private String  sanitize(String text) {
		
	     return text.replace("&", "&amp;")
	             .replace("\"", "&quot;")
	             .replace("'", "&#x27;")
	             .replace("<", "&lt;")
	             .replace(">", "&gt;")
	             .replace("/", "&#x2F;")
	             .replace("\\", "&#x5C;")
	             .replace("`", "&#96;'");
	}
}
