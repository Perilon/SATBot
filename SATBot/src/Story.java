import java.util.ArrayList;
import java.util.Arrays;

public class Story {
	public String ID; //ID, i.e. mc.500.0
	public String author; //author number
	public String qualScore; //dunno what this is, but it's called the same thing in the training file
	public String workTimes; //likewise, same as in original file
	public String[] creativityWords; //keywords?
	public String text; //story text to be parsed & asserted
	public ArrayList<Question> questions; //list of four questions, see Question class
	
	public Story(String txt){
			txt = txt.trim();
			String[] lines = txt.split("\n");
			int l = lines.length;
			ID = lines[0];
			author = lines[1];
			qualScore = lines[2];
			workTimes = lines[3];
			creativityWords = lines[4].split(","); //needs extra parsing if we care about these
			
			text = String.join(" ", Arrays.copyOfRange(lines, 6, l-23));
			
			Question Q1 = new Question(Arrays.copyOfRange(lines, l-23, l-18));
			Question Q2 = new Question(Arrays.copyOfRange(lines, l-17, l-12));
			Question Q3 = new Question(Arrays.copyOfRange(lines, l-11, l-6));
			Question Q4 = new Question(Arrays.copyOfRange(lines, l-5, l));
			questions = new ArrayList<Question>();
			questions.add(Q1);
			questions.add(Q2);
			questions.add(Q3);
			questions.add(Q4);
	}
	
	//returns story to original text format
	public String toString(){
		String str = this.ID+"\n"+this.author+"\n"+this.qualScore+"\n"+this.workTimes+"\n"+
				this.creativityWords+"\n\n"+this.text+"\n\n"+
				this.questions.get(0).toString()+"\n"+this.questions.get(1).toString()+"\n"+
				this.questions.get(2).toString()+"\n"+this.questions.get(3).toString()+"\n\n";
		return str;
	}
}
