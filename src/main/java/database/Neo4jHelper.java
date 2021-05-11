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
	//private static AvibSparkContext avibSpark = AvibSparkContext.getInstance();
	//private static JavaSparkContext sparkContext;
	private int maxCoincidence;
	private FileNode maxRecord;
	private int cont;
	private int savedFiles;
	private static List<String> idList = new ArrayList<String>();

	public Neo4jHelper() {
		//sparkContext = avibSpark.getContext();
	}

	public List<FileNode> searchFiles(Commit commit) {
		List<FileNode> filesToSave = new ArrayList<FileNode>();
		ArrayList<CommitFile> files = commit.getFiles();
		for (int i = 0; i < files.size(); i++) {
			Connector conn = new Connector();
			ArrayList<FileNode> result = conn.searchFiles(projectId, files.get(i).getName());
			if (result.size() == 1) {
				filesToSave.add(result.get(0));
			} else if (result.size() >= 2) {
				int maxCoincidence = 0;
				FileNode maxRecord = result.get(0);
				if (files.get(i).getWords().size() >= 1) {
					for (int j = 0; j < result.size(); j++) {
						int recordCoincidence = 0;
						List<String> words = files.get(j).getWords();
						for (int k = 0; k < words.size(); k++) {
							if (result.get(j).getQualifiedname().toLowerCase()
									.indexOf(words.get(k).toLowerCase()) > -1) {
								recordCoincidence++;
							}
						}
						if (recordCoincidence > maxCoincidence) {
							maxCoincidence = recordCoincidence;
							maxRecord = result.get(j);
						}
					}
				}
				filesToSave.add(maxRecord);
			}
			try {
				conn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return filesToSave;
	}

	public int saveCommit(Commit commit, List<FileNode> files) {
		String id = commit.getId();
		if(idList.contains(id)) {
			System.out.println("Repeated commit: "+id);
			return 0;
		}
		else {
			idList.add(id);
			if(idList.size() %10 == 0) {
				System.out.println("Proccessed "+ idList.size()+" commits.");
			}
			
		}
		System.out.println("--------- Saving commit ---------");
		long filesSize = files.size();
		if (files != null && filesSize < 1) {
			return 0;
		}
		String comment = sanitize(commit.getComment());
		savedFiles = 0;
		cont = 1;
		for(int i=0; i< files.size();i++) {
			if (cont % 10 == 0) {
				System.out.println("Checked file " + cont + " of " + filesSize);
			}
			cont += 1;
			try {
				Connector conn = new Connector();
				conn.saveCommit(projectId, commit, files.get(i), comment);
				savedFiles += 1;
				FileNode current = files.get(i);
				for(int j=0; j< files.size();j++) {
					if (current != files.get(j)) {
						savedFiles++;
						Connector conn2 = new Connector();
						conn2.commitFileRelationship(projectId, current, files.get(j));
						conn2.close();
					}
				}
				conn.close();
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			
		}

		System.out.println("Commit: " + commit.getId());
		System.out.println("Archivos encontrados: " + filesSize);
		System.out.println("Relaciones creadas: " + savedFiles);
		System.out.println("*****************");
		return savedFiles;
	}

	private String sanitize(String text) {

		return text.replace("&", "&amp;").replace("\"", "&quot;").replace("'", "&#x27;").replace("<", "&lt;")
				.replace(">", "&gt;").replace("/", "&#x2F;").replace("\\", "&#x5C;").replace("`", "&#96;'");
	}
}
