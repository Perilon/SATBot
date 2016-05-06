import edu.isi.powerloom.*;
import edu.isi.powerloom.logic.*;
import edu.isi.stella.Module;
import edu.isi.stella.javalib.*;
import net.sf.extjwnl.JWNLException;
import net.sf.extjwnl.data.IndexWord;
import net.sf.extjwnl.data.POS;
import net.sf.extjwnl.data.Synset;
import net.sf.extjwnl.data.Word;
import net.sf.extjwnl.dictionary.Dictionary;
import edu.isi.stella.Stella_Object;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.io.*;


public class PowerLoom {
	private static String[] powerLoomWords = {"name","thing","empty","different","range","set","float","heap","function","fork","void","mark","list","cop"};
	  private static void loadVerbosely (String filename) {
	    System.out.print("  Loading " + filename + " ...");
	    PLI.load(filename, null);
	    System.out.println("  done.");
	  }
	
	  public static ArrayList<Integer> initializePowerLoom (String[] args, ArrayList<ArrayList<String[]>> storyTextRelations,
ArrayList<ArrayList<ArrayList<String[]>>> storyQuestionRelations, ArrayList<ArrayList<ArrayList<ArrayList<String[]>>>> storyChoiceRelations) {

		    // Initialize the basic PowerLoom code.
		  		  
		    System.out.print("Initializing...");
		    PLI.initialize();
		    System.out.println("    done.");
		    System.out.println("Loading KBs:");

			loadVerbosely(args[0]);
			
			String wModule = args[1];


		    // *** Initialization is now complete.  
		    //     We begin working with the knowledge base.
			
		    ArrayList<Integer> answers = doPowerLoomExamples(wModule, storyTextRelations, storyQuestionRelations, storyChoiceRelations);
		    return answers;
		  }
	  
	  
	  
	  public static ArrayList<Integer> doPowerLoomExamples(String workingModule, ArrayList<ArrayList<String[]>> storyTextRelations,
ArrayList<ArrayList<ArrayList<String[]>>> storyQuestionRelations, ArrayList<ArrayList<ArrayList<ArrayList<String[]>>>> storyChoiceRelations) {
		  
		  // initialize list of answers that we give
		  ArrayList<Integer> answers = new ArrayList<Integer>();
		  		  
		    for (int i = 0; i < storyTextRelations.size(); i++) {
		    	
		    	System.out.println("Story #" + Integer.toString(i));
		    	
			    PLI.createModule(workingModule, null, false);
				  
			    PLI.sChangeModule(workingModule, null);
			    
			    // ((name STRING) (parent LOGIC-OBJECT) (module MODULE) (environment ENVIRONMENT))
			    // no parent; null module = current module; null environment = default environment = 'TAXONOMIC-ENV'
			    // TAXONOMIC-ENV specifies that a knowledge base query should take into account explicitly-asserted propositions
			    // plus any rules that specify subsumption relationships.
			    
			    // PLI.createConcept("company", null, null, null);
		    	
		    	// All the sentences from one story
		    	ArrayList<String[]> storyTriples = storyTextRelations.get(i);
		    	
		    	// All the questions in one story
		    	ArrayList<ArrayList<String[]>> questionTriples = storyQuestionRelations.get(i);
		    	
		    	// All the choices in one story
		    	ArrayList<ArrayList<ArrayList<String[]>>> choiceTriples = storyChoiceRelations.get(i);
		    	
		    	for (int j = 0; j < questionTriples.size(); j++) {
		    		for (int k = 0; k < questionTriples.get(j).size(); k++) {
//		    			System.out.println("story " + Integer.toString(i) + " question " + Integer.toString(j) + " triple " + Integer.toString(k) + " = " +
//questionTriples.get(j).get(k)[0] + " " + questionTriples.get(j).get(k)[1] + " " + questionTriples.get(j).get(k)[2]);
		    		}
		    		
		    		for (int L = 0; L < choiceTriples.get(j).size(); L++) {
		    			for (int m = 0; m < choiceTriples.get(j).get(L).size(); m++) {
//		    				System.out.println("\tstory " + Integer.toString(i) + " question " + Integer.toString(j) + " choice " + Integer.toString(L) +
//" triple " + Integer.toString(m) +  " = " + choiceTriples.get(j).get(L).get(m)[0] + " " + choiceTriples.get(j).get(L).get(m)[1] + " " + choiceTriples.get(j).get(L).get(m)[2]);
		    			}
		    		}
		    	}
		    	
		    	
		    	//get sets of relations and concepts because PowerLoom throws a fit if you create twice
		    	Set<String> concepts = new HashSet<String>();
		    	Set<String> relations = new HashSet<String>();
		    	for (int j = 0; j < storyTriples.size(); j++) {
		    			relations.add(storyTriples.get(j)[0]);
		    			concepts.add(storyTriples.get(j)[1]);
		    			concepts.add(storyTriples.get(j)[2]);
		    	}
		    	for(String w : powerLoomWords){
	        		for(String c : concepts){
//			    		if (!c.equals("cop") && !c.equals("mark") && !c.equals("list")) {
			    			if (c.equals(w)){c="concept"+c;}
//			    		}
	        		}
	        		for(String r : relations){
//			    		if (!r.equals("cop") && !r.equals("mark") && !r.equals("list")) {
			    			if (r.equals(w)){r="relation"+r;}
//			    		}
	        		}
	        	}
		    	for (String c : concepts){
//		    		if (!c.equals("cop") && !c.equals("mark") && !c.equals("list")) {
		    			PLI.sCreateConcept(c, null, workingModule, null);
//		    		}
		    	}
		    	assertSynonyms(concepts, workingModule, null);
		    	for (String r : relations){
//		    		if (!r.equals("cop") && !r.equals("mark") && !r.equals("list")) {
		    			PLI.sCreateRelation(r, 2, workingModule, null);
//		    		}
		    	}
		    	
		    	// All the triples in one sentence
		    	for (int j = 0; j < storyTriples.size(); j++) {
		  
		    		// Write code that defines and asserts things given a triple.
		   		
		    		//All the terms in each triple
		    		//for (int k = 0; k < storyTriples.get(j).length; k++) {   			
		    		//}
//		    		System.out.println(String.join(" ", storyTriples.get(j)));
//		    		
		    		PLI.sAssertProposition("("+String.join(" ", storyTriples.get(j))+")", workingModule, null);
//		    		
//		    		PowerLoomExample.printPowerLoomTruth("("+String.join(" ", storyTriples.get(j))+")", workingModule, null);
		    	}
		    	
		    	// For each question
		    	for (int j = 0; j < questionTriples.size(); j++) {
		    		
		    		// initialize a list of batches of new triples
		    		ArrayList<ArrayList<String[]>> questionChoiceCombinedParses = new ArrayList<ArrayList<String[]>>();
		    		
			    	
			    	// for each choice
			    	for (int L = 0; L < choiceTriples.get(j).size(); L++) {
			    		
			    		// initialize a new batch of triples
				    	ArrayList<String[]> questionChoiceCombinedParse = new ArrayList<String[]>();
				    	
				    	// if dealing with a question starting with "what" or "which"
				    	if (questionTriples.get(j).get(0)[2].equals("what") || questionTriples.get(j).get(0)[2].equals("which")) {
				    		
				    		// identify the verb
				    		String verb = questionTriples.get(j).get(0)[1];
				    		String rel = questionTriples.get(j).get(0)[0];
				    		
				    		// find out whether to substitute something other than "rootrel" from the choice
			    			boolean nsubjpresent = false;
			    			boolean dobjpresent = false;
				    		for (int m = 0; m < choiceTriples.get(j).get(L).size(); m++) {
				    			if (choiceTriples.get(j).get(L).get(m)[0].equals("nsubj")) {
				    				nsubjpresent = true;
				    			} if (choiceTriples.get(j).get(L).get(m)[0].equals("dobj")) {
				    				dobjpresent = true;
				    			}
				    		}
				    		
				    		// use "rootrel"
				    		if (!nsubjpresent && !dobjpresent) {
					    		// for the triples in the choice
					    		for (int m = 0; m < choiceTriples.get(j).get(L).size(); m++) {
					    			// find the one containing the rootrel, i.e., for choices like "the rock" or "Mr. fish"
					    			if (choiceTriples.get(j).get(L).get(m)[0].equals("rootrel")) {
					    				// identify the noun
					    				String noun = choiceTriples.get(j).get(L).get(m)[2];
					    				// make a new triple with the noun as the subject that does the verb
					    				String[] newTriple = {rel, verb, noun};
					    				// add it to the new set of triples for this question-choice pair
					    				questionChoiceCombinedParse.add(newTriple);
					    			}
					    		}
				    		} // else, do something with nsubj or dobj...
				    						    		
				    	}

				    	// add the rest of the triples from the question into the set of triples for this q-c pair
				    	for (int n = 1; n < questionTriples.get(j).size(); n++) {
				    		questionChoiceCombinedParse.add(questionTriples.get(j).get(n));
				    	}
				    	//Note these will be the same for all choices
			    		
				    	// add the set of triples for this q-c pair to the list with one entry for each choice
				    	questionChoiceCombinedParses.add(questionChoiceCombinedParse);
				    	
			    	}
			    	
			    	// for each qCCP
			    	String[] letter = {"A","B","C","D"};
			    	// initialize array of how many answers of each type we get for each q-c pair
			    	int[] qcAnswerTrueCounts = {0,0,0,0};
			    	int[] qcAnswerFalseCounts = {0,0,0,0};
			    	int[] qcAnswerUnkCounts = {0,0,0,0};
			    	for(int qc=0; qc<questionChoiceCombinedParses.size();qc++){
			    		//for each triple in the qCCP
					    for (int t = 0; t < questionChoiceCombinedParses.get(qc).size(); t++) {
					    	//PowerLoomExample.printPowerLoomTruth("("+String.join(" ", storyTriples.get(j))+")", workingModule, null);
					    	
					    	if (questionChoiceCombinedParses.get(qc).get(t)[0].equals("nsubj") || questionChoiceCombinedParses.get(qc).get(t)[0].equals("dobj")) {
					    	
					    		System.out.println("("+String.join(" ", questionChoiceCombinedParses.get(qc).get(t))+")");
					    	
						    	TruthValue answer = PLI.sAsk("("+String.join(" ", questionChoiceCombinedParses.get(qc).get(t))+")", workingModule, null);
						    	if (PLI.isTrue(answer)) {
						    	      System.out.println("Story "+i+" question "+j+" choice"+letter[qc]+" has a true triple");
						    	      qcAnswerTrueCounts[qc]++;
						    	    } else if (PLI.isFalse(answer)) {
						    	      System.out.println("Story "+i+" question "+j+" choice"+letter[qc]+" has a false triple");
						    	      qcAnswerFalseCounts[qc]++;
						   		    } else if (PLI.isUnknown(answer)) {
						    	      System.out.println("Story "+i+" question "+j+" choice"+letter[qc]+" has a unknown triple");
						    	      qcAnswerUnkCounts[qc]++;
						   		    }
					    	}
				    	}
		    		}
			    	int bestAnswer = calcBestAnswer(qcAnswerTrueCounts, qcAnswerFalseCounts, qcAnswerUnkCounts);
			    	answers.add(bestAnswer);
		    	}
		    	
		    }
		    return answers;
	  }
	  
	  public static int calcBestAnswer(int[] trueCands, int[] falseCands, int[] unkCands) {
		  double trueWeight = 1;
		  double unkWeight = 0;
		  double falseWeight = -1;
		  
		  double[] totals = {0,0,0,0};
		  
		  for (int i = 0; i < trueCands.length; i++) {
			  totals[i] += ((trueWeight * trueCands[i]) + (unkWeight * unkCands[i]) + (falseWeight * falseCands[i])); 
		  }
		  
		  int ans = -5;
		  
		  double maxVal = getMax(totals);
		  
		  // hacky way to show when we're undecided by outputting -5; might not apply correctly in all cases
		  if (maxVal == 0) {
			  return ans;
			  
			// return the first of however many choices there are that share the max value  
		  } else {
			  int numMax = 0;
			  for (int i = 0; i < trueCands.length; i++) {
				  if (totals[i] == maxVal) {
					  numMax++;
				  }
			  }
			  if (numMax == 1) {
				  for (int i = 0; i < trueCands.length; i++) {
					  if (totals[i] == maxVal) {
						  ans = i;
						  return ans;
					  }
				  }
			  } else {
				  ans = 5;
			  }
			  return ans;
		  }
	  }
	  
	  public static double getMax(double[] inputArray) {
		  double maxVal = inputArray[0];
		  for (int i = 1; i < inputArray.length; i++) {
			  if (inputArray[i] > maxVal) {
				  maxVal = inputArray[i];
			  }
		  }
		  return maxVal;
	  }
	  public static void assertSynonyms(Set<String> concepts, String workingModule, String env){
		  Dictionary d;
		  for (String c : concepts){
			try {
				 Set<String> synonyms = new HashSet<String>();
				d = Dictionary.getDefaultResourceInstance();
				  IndexWord idx = d.getIndexWord(POS.VERB, c);
				  if (idx != null){
					 
					  List<Synset> synSets = idx.getSenses();
					  if (synSets != null){
						  for (Synset synset : synSets){ 
							  List<Word> words = synset.getWords();
							  	for (Word word : words) { 
							  		if (word.getLemma().toString().split(" ").length == 1)
							  			synonyms.add(word.getLemma().toString());
							  	}
						  }
					  }
				  }
				  idx = d.getIndexWord(POS.NOUN, c);
				  if (idx != null){
					  List<Synset> synSets = idx.getSenses();
					  synSets = idx.getSenses();
					  if (synSets != null){
						  for (Synset synset : synSets){ 
							  List<Word> words = synset.getWords();
							  	for (Word word : words) { 
							  		if (!word.equals("cop") && !word.equals("mark") && !word.equals("list")) {
								  		if (word.getLemma().toString().split(" ").length == 1 && !word.toString().matches(".*\\d+.*"))
								  			synonyms.add(word.getLemma().toString());
							  		}
							  	}
						  }
					  }
				  }
				  System.out.println(synonyms);
				  synonyms.removeAll(concepts);
				  for (String s : synonyms){
					  System.out.println(s);
					  for(String w : powerLoomWords){
			        	if (s.equals(w)){s="concept"+s;}
					  }
					  s =s.replaceAll("'", "SINGLEQUOTE");
					  PLI.sCreateConcept(s, null, workingModule, null);
					  PLI.sAssertProposition("(synonym "+s+" "+c+")",workingModule,null);
				  }
			} catch (JWNLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		  }//end for loop over concepts
//		  PLI.sAssertProposition("(synonym eat enjoy)",workingModule, null);
		
		  
	  }
}
