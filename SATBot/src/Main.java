import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;
import java.io.File;

import edu.stanford.nlp.ling.CoreAnnotations.LemmaAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;
import java.util.Properties;


public class Main {
	public static void main(String[] args) throws Exception {
		String inputTxt = args[0];
		//String output = args[1];
		
		
		// Make kbfileDefault whatever.  This was for testing purposes.  We can either make a new one for each
		// story and keep them all in the directory, or overwrite the same one for each
		
	    String kbfileDefault = "./test.plm";
	    String workingModule = "STORY";
	    String[] loomParams = new String[]{kbfileDefault, workingModule};
	    
	    Lemmatizer lemmatizer = new Lemmatizer();
		
		Main m = new Main();//oddity to to static/nonstatic issues
		String txt = m.readFile(inputTxt).trim();
		ArrayList<ArrayList<String[]>> storyRelations = m.processStoryTexts(txt, lemmatizer);
		
		ArrayList<ArrayList<ArrayList<String[]>>> storyQuestionRelations = m.processStoryQuestions(txt, lemmatizer);
		ArrayList<ArrayList<ArrayList<ArrayList<String[]>>>> storyChoiceRelations = m.processStoryChoices(txt, lemmatizer);
				
//		m.getQuestionTypes(txt, lemmatizer);
		
		ArrayList<Integer> plAnswers = doPowerLoom(loomParams, storyRelations, storyQuestionRelations, storyChoiceRelations);
		System.out.println("PowerLoom answers = " + plAnswers.toString());
		
		ArrayList<Integer> correctAnswers = m.getCorrectAnswers(txt);
		System.out.println("Correct answers = " + correctAnswers.toString());
		
		double percentCorrect = m.calcPercentCorrect(correctAnswers, plAnswers);
		System.out.println("Percent correct = " + Double.toString(percentCorrect));
		
	}
	
	public ArrayList<ArrayList<String[]>> processStoryTexts(String txt, Lemmatizer lemmatizer){
		ArrayList<ArrayList<String[]>> storyTextRels = new ArrayList<ArrayList<String[]>>();
		
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
			ArrayList<String[]> relsToAssert = p.parseString(stories.get(i).text, lemmatizer);
			storyTextRels.add(relsToAssert);
		}
		return storyTextRels;
	}
	
	
	
	// getting into the weeds
	public ArrayList<ArrayList<ArrayList<String[]>>> processStoryQuestions(String txt, Lemmatizer lemmatizer){
		ArrayList<ArrayList<ArrayList<String[]>>> storyQuestionRels = new ArrayList<ArrayList<ArrayList<String[]>>>();
		
		String[] txts = txt.split("\\*{51}");
		ArrayList<Story> stories = new ArrayList<Story>();
		for(int i=1; i < txts.length; i++){
			stories.add(new Story(txts[i]));
		}
	
		Parser p = new Parser();
		for(int i=0; i < stories.size();i++){
			
			ArrayList<ArrayList<String[]>> qParses = new ArrayList<ArrayList<String[]>>();
			ArrayList<Question> questions = stories.get(i).questions;
			
			// To do: write code that takes the choices for each question and combines each choice
			// with the question text, for its own parse, that can be evaluated against the
			// truthiness of the corresponding story
			
			// Parse the question itself
			for (Question q : questions) {
				ArrayList<String[]> qParse = p.parseString(q.question, lemmatizer);
				
				qParses.add(qParse);
			}
			
			storyQuestionRels.add(qParses);
		}
		return storyQuestionRels;
	}
	
	public ArrayList<ArrayList<ArrayList<ArrayList<String[]>>>> processStoryChoices(String txt, Lemmatizer lemmatizer){
		
		ArrayList<ArrayList<ArrayList<ArrayList<String[]>>>> storyChoiceRels = new ArrayList<ArrayList<ArrayList<ArrayList<String[]>>>>();
		
		String[] txts = txt.split("\\*{51}");
		ArrayList<Story> stories = new ArrayList<Story>();
		for(int i=1; i < txts.length; i++){
			stories.add(new Story(txts[i]));
		}
		
		Parser p = new Parser();
		for(int i=0; i < stories.size();i++){
			ArrayList<Question> questions = stories.get(i).questions;
			
			ArrayList<ArrayList<ArrayList<String[]>>> q_cParses = new ArrayList<ArrayList<ArrayList<String[]>>>();
			
			for (Question q : questions) {
				ArrayList<ArrayList<String[]>> cParses = new ArrayList<ArrayList<String[]>>();
				String[] choices = q.choices;
				
				for (String choice : choices) {
//					System.out.println(choice);
					ArrayList<String[]> cParse = p.parseString(choice, lemmatizer);
					cParses.add(cParse);
				}
				
				q_cParses.add(cParses);
				
			}
			
			storyChoiceRels.add(q_cParses);
		}
		return storyChoiceRels;
	}
	
	
	public void getQuestionTypes(String txt, Lemmatizer lemmatizer){
		
		String[] txts = txt.split("\\*{51}");
		ArrayList<Story> stories = new ArrayList<Story>();
		String[] qTypes = new String[]{"who", "what", "where", "when", "why", "how", "which"};
		ArrayList<ArrayList<String>> allQs = new ArrayList<ArrayList<String>>();
		for(int i=1; i < txts.length; i++){
			stories.add(new Story(txts[i]));
		}
		for (int i = 0; i < stories.size(); i++) {
			ArrayList<Question> questions = stories.get(i).questions;
			for (Question q : questions) {
				allQs.add(lemmatizer.lemmatizeSentence(q.question));
			}
		}
		ArrayList<ArrayList<String>> qOther = new ArrayList<ArrayList<String>>();
		for (ArrayList<String> qAL : allQs) {
			boolean present = false;
			for (String w : qTypes) {
				if (qAL.get(0).equals(w)) {
					present = true;
				}
			}
			if (!present) {
				qOther.add(qAL);
			}
		}
		for (int k = 0; k < qTypes.length; k++) {
			ArrayList<ArrayList<String>> qType = new ArrayList<ArrayList<String>>();
			for (ArrayList<String> qAL : allQs) {
				if (qAL.get(0).equals(qTypes[k])) {
					qType.add(qAL);
				}
			}
			System.out.println("qType: " + qTypes[k] + ":\tnumber of this type = " + Integer.toString(qType.size()) + "\n");
			for (ArrayList<String> qAL : qType) {
				System.out.println(qAL.toString());
			}
			System.out.println("\n\n");
		}
		System.out.println("qOther:\tnumber of this type = " + Integer.toString(qOther.size()) + "\n");
		for (ArrayList<String> other : qOther) {
			System.out.println(other.toString());
		}
	}
	
	public static ArrayList<Integer> doPowerLoom(String[] params, ArrayList<ArrayList<String[]>> storyTextRelations,
			ArrayList<ArrayList<ArrayList<String[]>>> storyQuestionRelations, ArrayList<ArrayList<ArrayList<ArrayList<String[]>>>> storyChoiceRelations) {
		
		createNewFile(params[0]);
		
		PowerLoom Loom = new PowerLoom();
		ArrayList<Integer> answers = Loom.initializePowerLoom(params, storyTextRelations, storyQuestionRelations, storyChoiceRelations);
		
		return answers;
	}
	
	
	public static ArrayList<Integer> getCorrectAnswers(String txt) {
		ArrayList<Integer> correctAnswers = new ArrayList<Integer>();
		String[] txts = txt.split("\\*{51}");
		ArrayList<Story> stories = new ArrayList<Story>();
		for(int i=1; i < txts.length; i++){
			stories.add(new Story(txts[i]));
		}
		
		for (Story story : stories) {
			ArrayList<Question> questions = story.questions;
			for (Question question : questions) {
				int correctAnswer = question.answer;
				correctAnswers.add(correctAnswer);
			}
		}
		return correctAnswers;
	}
	
	public static double calcPercentCorrect(ArrayList<Integer> correctAns, ArrayList<Integer> candAns) {
		
		int lenCorr = correctAns.size();
		int lenCand = candAns.size();
		assert lenCorr == lenCand;
		int numSame = 0;
		for (int i = 0; i < lenCorr; i++) {
			if (correctAns.get(i).equals(candAns.get(i))) {
				numSame++;
			}
		}
				
		double ans = numSame / (double) lenCorr;
		return ans;
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

