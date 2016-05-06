
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.Reader;
import java.io.StringReader;

import edu.stanford.nlp.process.Tokenizer;
import edu.stanford.nlp.process.TokenizerFactory;
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.process.DocumentPreprocessor;
import edu.stanford.nlp.process.PTBTokenizer;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.Sentence;
import edu.stanford.nlp.trees.*;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
	

class Parser {
	// changed this from "([^)]*)\\((.*)-\\d+'*?, (.*)-\\d+'*?\\)" so it doesn't fail to match where ' has been replaced by SINGLEQUOTE
	private Pattern p = Pattern.compile("([^)]*)\\((.*)-\\d+'*.*, (.*)-\\d+'*.*\\)");
//	private Pattern p2 = Pattern.compile("([^)]*)\\((.*-\\d+'*?), (.*-\\d+'*?)\\)");
//	private Pattern p3 = Pattern.compile("(.*)-(\\d+'*?)");
  /**
   * The main method demonstrates the easiest way to load a parser.
   * Simply call loadModel and specify the path of a serialized grammar
   * model, which can be a file, a resource on the classpath, or even a URL.
   * For example, this demonstrates loading a grammar from the models jar
   * file, which you therefore need to include on the classpath for ParserDemo
   * to work.
   *
   * Usage: {@code java ParserDemo [[model] textFile]}
   * e.g.: java ParserDemo edu/stanford/nlp/models/lexparser/chineseFactored.ser.gz data/chinese-onesent-utf8.txt
   *
   */
  /*public static void main(String[] args) {
    LexicalizedParser lp = LexicalizedParser.loadModel("edu/stanford/nlp/models/lexparser/englishPCFG.ser.gz");
    
    
    String textFile = (args.length > 1) ? args[1] : args[0];
    	depParse(lp, textFile);
    
  }*/
	
//  private int toAdd(int index, ArrayList<Integer> missing) {
//	int val = 0;
////	System.out.println("missing.size() = " + Integer.toString(missing.size()));
//	for (int i = 0; i < missing.size(); i++) {
////		System.out.println("Boolean.toString(index > missing.get(i)) = " + Boolean.toString(index > missing.get(i)));
//		if (index > missing.get(i)) {
////			System.out.println("index = " + Integer.toString(index));
////			System.out.println("missing.get(i) = " + Integer.toString(missing.get(i)));
//			val++;
//		}
//	}
////	System.out.println("index - val = " + Integer.toString(index - val));
//	return index - val; 
//  }
	
  public ArrayList<String[]> parseString(String text, Lemmatizer lemmatizer) {
//	    System.out.println(text);
	  	ArrayList<String[]> triples = new ArrayList<String[]>();
	    LexicalizedParser lp = LexicalizedParser.loadModel("edu/stanford/nlp/models/lexparser/englishPCFG.ser.gz");
	    TreebankLanguagePack tlp = lp.treebankLanguagePack(); // a PennTreebankLanguagePack for English
	    GrammaticalStructureFactory gsf = null;
	    if (tlp.supportsGrammaticalStructures()) {
	      gsf = tlp.grammaticalStructureFactory();
	    }
	    // You could also create a tokenizer here (as below) and pass it
	    // to DocumentPreprocessor
	    Reader reader = new StringReader(text);
	    for (List<HasWord> sentence : new DocumentPreprocessor(reader)) {
	      Tree parse = lp.apply(sentence);
	     // parse.pennPrint();
	     // System.out.println();
	      

	      	      
	      if (gsf != null) {
	        GrammaticalStructure gs = gsf.newGrammaticalStructure(parse);
	        List<TypedDependency> tdl = gs.typedDependenciesCCprocessed();
	        
//	        System.out.println("tdl = " + tdl.toString());
//	        
//	        StringBuilder sentStrB = new StringBuilder();
//	        
//	        for(int i=0;i< tdl.size();i++){
//	        	String rel0 = tdl.get(i).toString();
//	        	Matcher m = p.matcher(rel0);
//	        	m.find();
//	        	sentStrB.append((m.group(3) + " "));
//	        }
//	        String sentStr = sentStrB.toString().trim();
//	        ArrayList<String> lemmas = lemmatizer.lemmatize(sentStr);
//	        lemmas.add(0, "ROOT");
//	        
//	        System.out.println("lemmas = " + lemmas.toString());
	        	        
//	        ArrayList<Integer> indexpositions = new ArrayList<Integer>();
//	        for(int j=0;j< tdl.size();j++) {
//	        	String s = tdl.get(j).toString();
//	        	Matcher n = p.matcher(s);
//	        	Matcher n2 = p2.matcher(s);
//	        	n.find();
//	        	n2.find();
//	        	Matcher n3_2 = p3.matcher(n2.group(2));
//	        	Matcher n3_3 = p3.matcher(n2.group(3));
//	        	n3_2.find();
//	        	n3_3.find();
//	        	int z2index = Integer.parseInt(n3_3.group(2));
//	        	indexpositions.add(z2index - 1);
//	        }
//	        
//	        System.out.println("indexpositions = " + indexpositions.toString());
//	        
//	        ArrayList<Integer> missing = new ArrayList<Integer>();
//	        for (int q = 0; q < indexpositions.get(indexpositions.size()-1); q++) {
//	        	if (!(indexpositions.contains(q))) {
//	        		missing.add(q+1);
//	        	}
//	        }
//	        
//	        System.out.println("missing = " + missing.toString());
//	        	
//	        System.out.println("tdl.size() = " + Integer.toString(tdl.size()));
//	        System.out.println("lemmas.size() = " + Integer.toString(lemmas.size()));
//	        
//	        int toAdd = 0;
	        	
	        for(int i=0;i< tdl.size();i++){
	        	

	        	String rel = tdl.get(i).toString();
	        		        	
	        	rel = rel.replaceAll("'", "SINGLEQUOTE");  //not allowed in PowerLoom words
	        	//probably need more replacement here.
	        	Matcher m = p.matcher(rel);
//	        	Matcher m2 = p2.matcher(rel);
	        	m.find();
//	        	m2.find();
//	        		        		        
//	        	Matcher m3_2 = p3.matcher(m2.group(2));
//	        	Matcher m3_3 = p3.matcher(m2.group(3));
//	        	m3_2.find();
//	        	m3_3.find();
//	        	
//	        	int w1index = Integer.parseInt(m3_2.group(2));
//	        	int w2index = Integer.parseInt(m3_3.group(2));
 	
//	        	String[] triple = new String[]{m.group(1).replaceAll("root", "rootrel"), lemmas.get(toAdd(w1index, missing)), lemmas.get(toAdd(w2index, missing))};
	        	
//	        	triples.add(triple);
	        	
	        	String[] triple = new String[]{m.group(1).replaceAll("root", "rootrel"),lemmatizer.lemmatizeWord(m.group(2)),lemmatizer.lemmatizeWord(m.group(3))};
//	        	System.out.println("triple = " + triple[0].toString() + " "+ triple[1].toString() + " " + triple[2].toString());
	        	triples.add(triple);
	        	
	        	
	        	//PowerLoom won't allow both root as a relation and root as a concept
	        }
//	        System.out.println(tdl);
//	        System.out.println();
	      }
	    }
	    
//	    System.out.println("Triples size = " + Integer.toString(triples.size()));
//	    for (int i = 0; i < triples.size(); i++) {
//	    	for (int j = 0; j < triples.get(i).length; j++) {
//	    		System.out.println(triples.get(i)[j]);
//	    	}
//	    }
	    
	    return triples;	
	    
	  }
  /**
   * demoDP demonstrates turning a file into tokens and then parse
   * trees.  Note that the trees are printed by calling pennPrint on
   * the Tree object.  It is also possible to pass a PrintWriter to
   * pennPrint if you want to capture the output.
   * This code will work with any supported language.
   */
  /*
  public static void depParse(LexicalizedParser lp, String filename) {
    // This option shows loading, sentence-segmenting and tokenizing
    // a file using DocumentPreprocessor.
    TreebankLanguagePack tlp = lp.treebankLanguagePack(); // a PennTreebankLanguagePack for English
    GrammaticalStructureFactory gsf = null;
    if (tlp.supportsGrammaticalStructures()) {
      gsf = tlp.grammaticalStructureFactory();
    }
    // You could also create a tokenizer here (as below) and pass it
    // to DocumentPreprocessor
    for (List<HasWord> sentence : new DocumentPreprocessor(filename)) {
      Tree parse = lp.apply(sentence);
     // parse.pennPrint();
     // System.out.println();

      if (gsf != null) {
        GrammaticalStructure gs = gsf.newGrammaticalStructure(parse);
        Collection tdl = gs.typedDependenciesCCprocessed();
        System.out.println(tdl);
        System.out.println();
      }
    }
  }*/

}