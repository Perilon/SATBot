import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;


import edu.stanford.nlp.ling.CoreLabel;

import edu.stanford.nlp.hcoref.CorefCoreAnnotations.CorefChainAnnotation;
import edu.stanford.nlp.hcoref.data.CorefChain;
import edu.stanford.nlp.hcoref.data.CorefChain.CorefMention;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.util.IntTuple;

public class CorefResolver {
	protected StanfordCoreNLP pipeline;
	
	public CorefResolver() {
        // Create StanfordCoreNLP object properties, with POS tagging
        // (required for lemmatization), and lemmatization
        Properties props;
        props = new Properties();
        props.put("annotators", "tokenize, ssplit, pos, lemma, ner, parse, dcoref");

        this.pipeline = new StanfordCoreNLP(props);
    }
	public String resolveAndReplace(String text){
		String replaced = "";
		System.out.println("resolving corefs");
        // Create an empty Annotation just with the given text
        Annotation anno = new Annotation(text);
        // run all Annotators on this text
        this.pipeline.annotate(anno);
        System.out.println(anno.get(CorefChainAnnotation.class));
        Map<Integer, CorefChain> coref = anno.get(CorefChainAnnotation.class);
        
        //for each sentence
        for (int s =0; s<anno.get(CoreAnnotations.SentencesAnnotation.class).size();s++ ){
        	CoreMap sentence = anno.get(CoreAnnotations.SentencesAnnotation.class).get(s);
        	//for each token
        	for (CoreLabel token: sentence.get(TokensAnnotation.class)) {
        		String newtok = token.originalText();
        	
            	//for each coref set
        		for(Map.Entry<Integer, CorefChain> entry : coref.entrySet()) {
        			CorefChain c = entry.getValue();
        			if(c.getMentionsInTextualOrder().size() <= 1)
                        continue;
        			CorefMention reprMent = c.getRepresentativeMention();
                    String clust = "";
                    List<CoreLabel> tks = anno.get(SentencesAnnotation.class).get(reprMent.sentNum-1).get(TokensAnnotation.class);
                    for(int i = reprMent.startIndex-1; i < reprMent.endIndex-1; i++)
                        clust += tks.get(i).get(TextAnnotation.class) + " ";
                    clust = clust.trim();
                    for(CorefMention cm : c.getMentionsInTextualOrder()){
                    	String textOfMention = cm.mentionSpan;
                        IntTuple positionOfMention = cm.position; //sentence #, index
                        if(cm.position.get(0)==s+1){//only if looking at right sentence
	                        if(token.index() == cm.startIndex){
	                    		newtok = clust;
	                    	}
	                    	if(token.index() > cm.startIndex && token.index() < cm.endIndex){
	                    		newtok = "";
	                    	}
                        }
                    }//end for loop over mentions
        		}//end for loop over coref sets
        		replaced += newtok+" ";
            }//end for loop over toks
        	
        }//end for loop over sentences
        /*
        for(Map.Entry<Integer, CorefChain> entry : coref.entrySet()) {
            CorefChain c = entry.getValue();
            //this is because it prints out a lot of self references which aren't that useful
            //System.out.println(c.getMentionsInTextualOrder().size()+"refs to same thing");
            if(c.getMentionsInTextualOrder().size() <= 1)
                continue;
        
            CorefMention reprMent = c.getRepresentativeMention();
            String clust = "";
            List<CoreLabel> tks = anno.get(SentencesAnnotation.class).get(reprMent.sentNum-1).get(TokensAnnotation.class);
            for(int i = reprMent.startIndex-1; i < reprMent.endIndex-1; i++)
                clust += tks.get(i).get(TextAnnotation.class) + " ";
            clust = clust.trim();
            
            for(CorefMention cm : c.getMentionsInTextualOrder()){
            	String textOfMention = cm.mentionSpan;
                IntTuple positionOfMention = cm.position; //sentence #, index 
               
               // System.out.println(textOfMention+" "+positionOfMention);
                CoreMap sentence = anno.get(CoreAnnotations.SentencesAnnotation.class).get(positionOfMention.get(0)-1);
               // System.out.println(sentence.toString());
                for (CoreLabel token: sentence.get(TokensAnnotation.class)) {
                	
                	if(token.index() == cm.startIndex){
                		//replaced += clust+" ";
                		token.setOriginalText(clust);
                		
                	}
                	if(token.index() > cm.startIndex && token.index() < cm.endIndex){
                		token.setOriginalText("");
                	}
                	System.out.print(token.originalText()+" ");
                	//if (token.beginPosition())
                }
                
            }//end for loop over mentions
            
        }//end for loop over coref sets*/
        System.out.println("replaced: "+replaced);
        return text;
	}
}
