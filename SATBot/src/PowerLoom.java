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
ArrayList<ArrayList<ArrayList<String[]>>> storyQuestionRelations, ArrayList<ArrayList<ArrayList<String[]>>> storyChoiceRelations) {

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
	  
	  static void doPowerLoomExamples(String workingModule, ArrayList<ArrayList<String[]>> storyRelations,
ArrayList<ArrayList<ArrayList<String[]>>> storyQuestionRelations, ArrayList<ArrayList<ArrayList<String[]>>> storyChoiceRelations) {
		  
		  // For clarity...
		  
		    for (int i = 0; i < storyRelations.size(); i++) {
		    	
			    PLI.createModule(workingModule, null, false);
				  
			    PLI.sChangeModule(workingModule, null);
			    
			    // ((name STRING) (parent LOGIC-OBJECT) (module MODULE) (environment ENVIRONMENT))
			    // no parent; null module = current module; null environment = default environment = 'TAXONOMIC-ENV'
			    // TAXONOMIC-ENV specifies that a knowledge base query should take into account explicitly-asserted propositions
			    // plus any rules that specify subsumption relationships.
			    
			    // PLI.createConcept("company", null, null, null);
		    	
		    	// All the sentences from one story
		    	ArrayList<String[]> storyTriples = storyRelations.get(i);
		    	
		    	ArrayList<ArrayList<String[]>> questionTriples = storyQuestionRelations.get(i);
		    	
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
		    		System.out.println(String.join(" ", storyTriples.get(j)));
		    		
		    		PLI.sAssertProposition("("+String.join(" ", storyTriples.get(j))+")", workingModule, null);
		    		
		    		PowerLoomExample.printPowerLoomTruth("("+String.join(" ", storyTriples.get(j))+")", workingModule, null);
		    		
		    	}
			    
		    }
	  }

}
