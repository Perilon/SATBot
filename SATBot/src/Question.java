import java.util.Arrays;

public class Question {
	public String question; //question text to be parsed/asserted
	public int number; //question number: 1-4
	public String type; //question type: one or multiple
	public int answer; //index of correct choice
	public String[] choices; //A-D answer text to be parsed/checked

	
	public Question(String[] lines){
		String[] line0 = lines[0].split(":");
		number = Integer.parseInt((line0[0].trim()));
		type = line0[1].trim();
		question = String.join(":", Arrays.copyOfRange(line0, 2, line0.length)).trim();
		for(int i = 1; i < 5; i++){
			if(lines[i].charAt(0) == '*'){
				answer = i-1;
				lines[i] = lines[i].substring(1);
			}
			lines[i] = lines[i].trim();
		}
		String A = lines[1].substring(3);
		String B = lines[2].substring(3);
		String C = lines[3].substring(3);
		String D = lines[4].substring(3);
		choices = new String[]{A,B,C,D};
		
//		for (int i = 0; i < choices.length; i++) {
//			System.out.println(choices[i]);
//		}
		
	}
	
	//returns question to original string format
	public String toString(){
		String str = this.number+": "+this.type+": "+this.question+"\n";
		String[] ch = new String[]{"A)","B)","C)","D)"};
		for(int i=0;i<4;i++){
			if (i == this.answer){
				str+="*";
			}
			else{ str+=" ";}
			str+= ch[i]+" "+this.choices[i];
			if (i != 4){str+="\n";}
		}
		return str;
	}
}
