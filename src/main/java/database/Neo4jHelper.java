package database;

import java.util.ArrayList;
import java.util.List;

import entities.FileNode;
import main.Commit;
import main.CommitFile;
import utils.Config;

public class Neo4jHelper {
	private static Config config = Config.getInstance();
	private Connector instance = Connector.getInstance();
    private String projectId = config.getProperty("neo4j_project_id");

	public ArrayList<FileNode> searchFiles(Commit commit){
		ArrayList<FileNode> filesToSave = new ArrayList<FileNode>();
        ArrayList<CommitFile> files = commit.getFiles();
        //instance.connect();
        for (int i = 0; i < files.size(); i++) {
            ArrayList<FileNode> result = instance.searchFiles(projectId, files.get(i).getName());
            if(result.size() == 1) {
            	filesToSave.add(result.get(0));
            }
            else if(result.size()>= 2){
            	int maxCoincidence = 0;
                FileNode maxRecord = result.get(0);
                if(files.get(i).getWords().size() >=1){
                	for (int j = 0; j < result.size(); j++) {
                		 int recordCoincidence = 0;
                		 List<String> words = files.get(j).getWords();
                		 for (int k = 0; k < words.size(); k++) {
							if(result.get(j).getQualifiedname().toLowerCase().indexOf(words.get(k).toLowerCase())> -1) {
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
		}
        return filesToSave;
	}

	public int saveCommit(Commit commit, ArrayList<FileNode> files) {
		 if (files != null && files.size()< 1) {
			 return 0;
		 }
		 String comment  = sanitize(commit.getComment());
		 int savedFiles = 0;
		 int cont = 1;
		 for (int i = 0; i < files.size(); i++) {
			if(cont %10 ==0) {
                System.out.println("Checked file "+cont+" of "+files.size());
			}
            cont+=1;
            instance.saveCommit(projectId, commit, files.get(i), comment);
            savedFiles+=1;
            FileNode current = files.get(i);
            for (int j = 0; j < files.size(); j++) {
            	if(current != files.get(j)) {
               		instance.commitFileRelationship(projectId, current, files.get(j));
               	}
    				
    		}
		}
		System.out.println("Commit: "+commit.getId());
		System.out.println("Archivos encontrados: "+files.size());
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
