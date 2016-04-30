import edu.isi.powerloom.*;
import edu.isi.powerloom.logic.*;
import edu.isi.stella.Module;
import edu.isi.stella.javalib.*;
import edu.isi.stella.Stella_Object;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.io.*;


public class PowerLoom {
	
	  private static void loadVerbosely (String filename) {
	    System.out.print("  Loading " + filename + " ...");
	    PLI.load(filename, null);
	    System.out.println("  done.");
	  }
	
	  public static void initializePowerLoom (String[] args, ArrayList<ArrayList<String[]>> storyTextRelations,
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
			
		    doPowerLoomExamples(wModule, storyTextRelations, storyQuestionRelations, storyChoiceRelations);
		  }
	  
	  static void doPowerLoomExamples(String workingModule, ArrayList<ArrayList<String[]>> storyTextRelations,
ArrayList<ArrayList<ArrayList<String[]>>> storyQuestionRelations, ArrayList<ArrayList<ArrayList<ArrayList<String[]>>>> storyChoiceRelations) {
		  
		  // For clarity...
		  
		    for (int i = 0; i < storyTextRelations.size(); i++) {
		    	
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
		    			System.out.println("story " + Integer.toString(i) + " question " + Integer.toString(j) + " triple " + Integer.toString(k) + " = " +
questionTriples.get(j).get(k)[0] + " " + questionTriples.get(j).get(k)[1] + " " + questionTriples.get(j).get(k)[2]);
		    		}
		    		
		    		for (int L = 0; L < choiceTriples.get(j).size(); L++) {
		    			for (int m = 0; m < choiceTriples.get(j).get(L).size(); m++) {
		    				System.out.println("\tstory " + Integer.toString(i) + " question " + Integer.toString(j) + " choice " + Integer.toString(L) +
" triple " + Integer.toString(m) +  " = " + choiceTriples.get(j).get(L).get(m)[0] + " " + choiceTriples.get(j).get(L).get(m)[1] + " " + choiceTriples.get(j).get(L).get(m)[2]);
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
		    	
		    	for (String c : concepts){
		    		PLI.sCreateConcept(c, null, workingModule, null);
		    	}
		    	for (String r : relations){
		    		PLI.sCreateRelation(r, 2, workingModule, null);
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
				    	
				    	// if dealing with a question starting with "what"
				    	if (questionTriples.get(j).get(0)[2].equals("what")) {
				    		
				    		// identify the verb
				    		String verb = questionTriples.get(j).get(0)[1];
				    		
				    		// for the triples in the choice
				    		for (int m = 0; m < choiceTriples.get(j).get(L).size(); m++) {
				    			
				    			// find the one containing the rootrel, i.e., for choices like "the rock" or "Mr. fish"
				    			if (choiceTriples.get(j).get(L).get(m)[0].equals("rootrel")) {
				    				
				    				// identify the noun
				    				String noun = choiceTriples.get(j).get(L).get(m)[2];
				    				
				    				// make a new triple with the noun as the subject that does the verb
				    				String[] newTriple = {"nsubj", verb, noun};
				    				
				    				// add it to the new set of triples for this question-choice pair
				    				questionChoiceCombinedParse.add(newTriple);
				    				
				    			}
				    		}
				    						    		
				    	}
				    	else if (questionTriples.get(j).get(0)[2].equals("which")){
				    		// identify the verb
				    		String verb = questionTriples.get(j).get(0)[1];
				    		// for the triples in the choice
				    		for (int m = 0; m < choiceTriples.get(j).get(L).size(); m++) {
				    			// find the one containing the rootrel, i.e., for choices like "the rock" or "Mr. fish"
				    			if (choiceTriples.get(j).get(L).get(m)[0].equals("rootrel")) {
				    				// identify the noun
				    				String noun = choiceTriples.get(j).get(L).get(m)[2];
				    				// make a new triple with the noun as the subject that does the verb
				    				String[] newTriple = {"nsubj", verb, noun};
				    				// add it to the new set of triples for this question-choice pair
				    				questionChoiceCombinedParse.add(newTriple);
				    			}
				    		}
				    	}
				    	// add the rest of the triples from the question into the set of triples for this q-c pair
				    	for (int n = 1; n < questionTriples.get(j).size(); n++) {
				    		questionChoiceCombinedParse.add(questionTriples.get(j).get(n));
				    	}
				    	//Note these will be the same for all choices
			    		
				    	// add the set of triples for this q-c pair to the list with one entry for each choice
				    	questionChoiceCombinedParses.add(questionChoiceCombinedParse);
				    	
			    	}
			    	
			    	// TODO: create relations and concepts as above + evaluate truth for each entry in questionChoiceCombinedParses
			    		//for each qCCP
			    	String[] letter = {"A","B","C","D"};
			    	for(int qc=0; qc<questionChoiceCombinedParses.size();qc++){
			    		//for each triple in the qCCP
					    for (int t = 0; t < questionChoiceCombinedParses.get(qc).size(); t++) {
					    	//PowerLoomExample.printPowerLoomTruth("("+String.join(" ", storyTriples.get(j))+")", workingModule, null);
					    	TruthValue answer = PLI.sAsk("("+String.join(" ", storyTriples.get(j))+")", workingModule, null);
					    	if (PLI.isTrue(answer)) {
					    	      System.out.println("Question "+j+" choice"+letter[qc]+" has a true triple");
					    	    } else if (PLI.isFalse(answer)) {
					    	      System.out.println("Question "+j+" choice"+letter[qc]+" has a false triple");
					   		    } else if (PLI.isUnknown(answer)) {
					    	      System.out.println("Question "+j+" choice"+letter[qc]+" has a unknown triple");
					    	    }
					    	}
			    		}
			    		
		    	}
		    	
		    	
		    	
		    	
			    
		    }
	  }

}
