import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Main {
	public static void main(String[] args) throws Exception {
		String inputTxt = args[0];
		//String output = args[1];
		Main m = new Main();//oddity to to static/nonstatic issues
		String txt = m.readFile(inputTxt).trim();
		m.processStories(txt);
	}
	
	public void processStories(String txt){
		String[] txts = txt.split("\\*{51}");
		ArrayList<Story> stories = new ArrayList<Story>();
		for(int i=1; i < txts.length; i++){
			stories.add(new Story(txts[i]));
		}
	
		Parser p = new Parser();
		for(int i=0; i < stories.size();i++){
			ArrayList<String[]> relsToAssert = p.parseString(stories.get(i).text);
		}
		
	
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
}

