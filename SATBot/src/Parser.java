
import java.util.ArrayList;
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
	private Pattern p = Pattern.compile("([^)]*)\\((.*)-\\d+'*?, (.*)-\\d+'*?\\)");
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
  public ArrayList<String[]> parseString(String text) {
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
	        for(int i=0;i< tdl.size();i++){
	        	String rel = tdl.get(i).toString();
	        	rel = rel.replaceAll("'", "SINGLEQUOTE");  //not allowed in PowerLoom words
	        	//probably need more replacement here.
	        	Matcher m = p.matcher(rel);
	        	m.find();
	        	triples.add(new String[]{m.group(1).replaceAll("root", "rootrel"),m.group(2),m.group(3)});
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

  public Parser() {}

}