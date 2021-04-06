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
	
	public ArrayList<FileNode> searchFiles(Commit commit){
		ArrayList<FileNode> filesToSave = new ArrayList<FileNode>();
        ArrayList<CommitFile> files = commit.getFiles();
        //instance.connect();
        String id = config.getProperty("neo4j_project_id");
        for (int i = 0; i < files.size(); i++) {
            ArrayList<FileNode> result = instance.searchFiles(id, files.get(i).getName());
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
}
