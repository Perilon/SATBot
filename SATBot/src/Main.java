import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.io.File;

public class Main {
	public static void main(String[] args) throws Exception {
		String inputTxt = args[0];
		//String output = args[1];
		
		
		// Make kbfileDefault whatever.  This was for testing purposes.  We can either make a new one for each
		// story and keep them all in the directory, or overwrite the same one for each
		
	    String kbfileDefault = "./test.plm";
	    String workingModule = "STORY";
	    String[] loomParams = new String[]{kbfileDefault, workingModule};
		
		Main m = new Main();//oddity to to static/nonstatic issues
		String txt = m.readFile(inputTxt).trim();
		ArrayList<ArrayList<String[]>> storyRelations = m.processStories(txt);
		
		doPowerLoom(loomParams, storyRelations);

	}
	
	public ArrayList<ArrayList<String[]>> processStories(String txt){
		ArrayList<ArrayList<String[]>> storyRels = new ArrayList<ArrayList<String[]>>();
		
		String[] txts = txt.split("\\*{51}");
		ArrayList<Story> stories = new ArrayList<Story>();
		for(int i=1; i < txts.length; i++){
			stories.add(new Story(txts[i]));
		}
		
//		for (int i=0; i < stories.size(); i++) {
//			System.out.println(stories.get(i).toString());
//		}
	
		Parser p = new Parser();
		for(int i=0; i < stories.size();i++){
			ArrayList<String[]> relsToAssert = p.parseString(stories.get(i).text);
			storyRels.add(relsToAssert);
		}
		return storyRels;
	}
	
	
	public static void doPowerLoom(String[] params, ArrayList<ArrayList<String[]>> storyRelations) {
		
		createNewFile(params[0]);
		
		PowerLoom Loom = new PowerLoom();
		Loom.initializePowerLoom(params, storyRelations);
	}
	
	
	public static String readFile(String fileName) throws IOException {
	    BufferedReader br = new BufferedReader(new FileReader(fileName));
	    try {
	        StringBuilder sb = new StringBuilder();
	        String line = br.readLine();

	        while (line != null) {
	            sb.append(line);
	            sb.append("\n");
	            line = br.readLine();
	        }
	        return sb.toString();
	    } finally {
	        br.close();
	    }
	}
	
	public static void createNewFile(String filepath) {
        File file = new File(filepath);
        boolean fileCreated = false;
        try {
            fileCreated = file.createNewFile();
        } catch (IOException ioe) {
            System.out.println("Error while creating empty file: " + ioe);
        }
 
        if (fileCreated) {
            System.out.println("Created empty file: " + file.getPath());
        } else {
            System.out.println("Failed to create empty file: " + file.getPath());
        }
 
    }
	
}

