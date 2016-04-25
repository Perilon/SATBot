/* Import statements for working with PowerLoom. */

import edu.isi.powerloom.*;
import edu.isi.powerloom.logic.*;
import edu.isi.stella.Module;
import edu.isi.stella.javalib.*;
import edu.isi.stella.Stella_Object;

import java.io.File;

/** PowerLoomExample class provides an example of using the Java
 *  interface to PowerLoom.  It will initialize the PowerLoom system,
 *  load the KB files specified as command line arguments, and then
 *  execute some PowerLoom code.
 *
 *  The command line arguments must load the business.plm knowledge
 *  base file or else the examples in this file will fail.  It is
 *  passed as a command line parameter so that the example does not
 *  depend on there being a particular location for the ontology
 *  files.  If no argument is given, then business.plm and
 *  kbs/business.plm will be tried, in that order.
 *
 *  In the calls to PLI methods, the last (environment) variable can
 *  always be specified as null, getting the default behavior.
 *
 *  @version $Id: PowerLoomExample.java,v 1.4 2004/06/08 00:00:34 tar Exp $
 */
public class PowerLoomExample {

  private static String kbfileDefault = "business.plm";
  private static String kbdirDefault = "kbs";
  private static String workingModule = "BUSINESS";

  private static void loadVerbosely (String filename) {
    System.out.print("  Loading " + filename + " ...");
    PLI.load(filename, null);
    System.out.println("  done.");
  }

  public static void do_powerloom_things(String[] args) {

    // Initialize the basic PowerLoom code.
    // This may need to be augmented if you want to use additional
    // PowerLoom or Stella code.  One common extension is to use
    // the PowerLoom system, which will require the additional
    // initialization, commented out below.
    System.out.print("Initializing...");
    PLI.initialize();
    // StartupPowerLoomSystem.startupPowerLoomSystem();
    System.out.println("    done.");

    
    // Load the knowledge bases.  This will either take the names from
    // the command line or else it will try a plain file default and a
    // plain file default in the default kb directory.
    System.out.println("Loading KBs:");
    if (args.length > 0) {
      for (int i = 0; i < args.length; i++) {
	loadVerbosely(args[i]);
      }
    } else if (new File(kbfileDefault).exists()) {
      loadVerbosely(kbfileDefault);
    } else if (new File(kbdirDefault, kbfileDefault).exists()) {
      loadVerbosely(new File(kbdirDefault, kbfileDefault).getAbsolutePath());
    } else {
      System.err.println("Oops.  This needs an ontology file argument!");
      System.exit(0);
    }

    // *** Initialization is now complete.  
    //     We begin working with the knowledge base.
    doPowerLoomExamples();
  }

    /** Example interactions with a PowerLoom knowledge base.  We use
     *     the "s..." method calls since they are generally simpler to
     *     use from Java than constructing the Stella/PowerLoom data
     *     structures.  
     *
     * In all of these, it is important to check parentheses balance
     * for correct operation.  PowerLoom will complain otherwise,
     * which can help you debug things.
     */
    static void doPowerLoomExamples() {

    // The following code parallels the user interaction in example.text
    // First we change into the BUSINESS module, which is not strictly necessary
    //    since all the function calls will supply the module argument.
    PLI.sChangeModule(workingModule, null);

    // Then we assert a few additional facts:
    PLI.sAssertProposition("(and (company c3) (company-name c3 \"Mom's Grocery\") (number-of-employees c3 2))", workingModule, null);
    PLI.sAssertProposition("(and (corporation c4) " 
			   + "(company-name c4 \"Yoyodyne, Inc.\")"
			   + "(> (number-of-employees  c4) 20000))",
			   workingModule, null);

    // Now we do the first query, getting back a PowerLoom iterator, which we
    // then step over and print the answers.  We use a static method defined below
    // for convenience in asking the query and then printing out the answers.
    // 
    printPowerLoomAnswers("all ?x (small-company ?x)",
			  workingModule,
			  null);

    // We could make the print-out prettier by using the company name
    // rather than the instance name.  In the next query we use an
    // existentially quantified variable that is not returned to hold
    // the company instance.
    printPowerLoomAnswers2("all ?x (exists ?c (and (small-company ?c) (company-name ?c ?x)))",
			  workingModule,
			  null);


    // We here examine the differences between the NOT and FAIL
    // oerators.  NOT requires a hard proof, whereas FAIL does not.
    // Note that we need to provide a positive clause as well,
    //    otherwise PowerLoom will just give up on the query, because
    //    there are just too many things that are not small companies.
    printPowerLoomAnswers("all ?x (not (small-company ?x))",
			  workingModule,
			  null);
    printPowerLoomAnswers("all ?x (and (company ?x) (not (small-company ?x)))",
			  workingModule,
			  null);
    printPowerLoomAnswers("all ?x (and (company ?x) (fail (small-company ?x)))",
			  workingModule,
			  null);

    // Here is a slightly more complicated query.  It returns more
    // than one item in each answer tuple.
    printPowerLoomAnswers("all (?name ?emp) (exists ?c (and (company ?c) (company-name ?c ?name) (number-of-employees ?c ?emp)))",
			  workingModule,
			  null);
    // This is how one would be able to assign values from the
    // returned tuples to Java native data types.  For simplicity we
    // ask for only a single returned values from the query.
    //
    // Note: Best practice would be to include a test for the data
    //       type using one of the PLI.isInteger(), PLI.isString(),
    //       etc. functions because there are some cases where
    //       PowerLoom will return a skolem instance in place of an
    //       actual value.  The query returning the number of
    //       employees is one such example.
   String query = "1 (?instance ?name ?emp) (and (small-company ?instance) (company-name ?instance ?name) (number-of-employees ?instance ?emp))";
    PlIterator answer = PLI.sRetrieve(query, workingModule, null);
    // Check for answer.  Necessary to get to the value anyway:
    System.out.println();
    printSeparator();
    System.out.println("Retrieving '" + query + "'");
    if (answer.nextP()) {
      // Here we take apart the first tuple, held in the "value" field
      // of the PLIterator instance.  Also, these interface functions
      // require the actual module object and not just its name.  So
      // we look that up and use it.
      Module mod = PLI.getModule(workingModule, null);
      Stella_Object instance = PLI.getNthValue(answer.value, 0, mod, null);
      String cname = PLI.getNthString(answer.value, 1, mod, null);
      int employees = PLI.getNthInteger(answer.value, 2, mod, null);
      System.out.println("Returns object " + instance
			 + " with name \"" + cname
			 + "\" and " + employees + " employees.");
    } else {
      System.out.println("No answers found");
    }
    printSeparator();
    

    // We create a new relation here, taking care to pay attention to
    // the capitalization of names.  Remember that in a module that is
    // not defined to be case sensitive, internal names are mapped to
    // uppercase.  This is handled internally by the interface functions.
    PLI.sCreateRelation("competitor", 2, workingModule, null);

    // Now we assert the symmetric property about this relation:
    PLI.sAssertProposition("(symmetric competitor)", workingModule, null);

    // Remember that assertions require that instance names are used.
    PLI.sAssertProposition("(competitor c3 c4)", workingModule, null);

    // A quick test to make sure that the symmetric property holds:
    printPowerLoomAnswers("all ?c (competitor c4 ?c)", workingModule, null);

    // One could use a simpler ask query as well:
    printPowerLoomTruth("(competitor c4 c3)", workingModule, null);
    

    // Arbitrary PowerLoom commands can be run using the evaluate and
    // sEvaluate commands.  This can be useful for adding additional
    // definitions that are more complex than what the general concept
    // creation interface supports.  Anything that can be typed at the
    // PowerLoom prompt can be passed to this interface function.
    PLI.sEvaluate("(defrelation mismatched-competitor ((?c1 company) (?c2 company)) :<=> (and (competitor ?c1 c2) (> (absolute-value (- (number-of-employees ?c1) (number-of-employees ?c2))) 1000)))",
		  workingModule,
		  null);

    // And testing it:
    printPowerLoomTruth("(mismatched-competitor c4 c3)", workingModule, null);

    // Error Handling:
    // If there is an error in the PowerLoom form, then an exception will
    // be thrown.  Errors in the logic itself are subtypes of
    // edu.isi.powerloom.logic.LogicException whereas underlying internal
    // code problems will be subtypes of edu.isi.stella.StellaException
    // Note that LogicException is also a subtype of StellaException.
    //      and StellaException is a subtype of java.lang.Exception.

    try {
      System.out.println();
      System.out.println("Error example:");
      PLI.sRetrieve("all ?x (use-a-name-that-does-not-exist ?x)", workingModule, null);
    } catch (edu.isi.powerloom.logic.LogicException le) {
      System.out.println("Caught a logic exception: " + le);
    } catch (edu.isi.stella.StellaException se) {
      System.out.println("Really Bad News, an internal Stella exception: " + se);
    }

    // Not enough parentheses:
    try {
      System.out.println();
      System.out.println("Error example 2:");
      PLI.sRetrieve("all (?x ?y) (and (competitor ?x ?y) (small-company ?x)", workingModule, null);
    } catch (edu.isi.powerloom.logic.LogicException le) {
      System.out.println("Caught a logic exception: " + le);
    } catch (edu.isi.stella.StellaException se) {
      System.out.println("Really Bad News, an internal Stella exception: " + se);
    }


    System.out.println();
    System.out.println("==== End of Example Code ====");
  }

  


  /** Takes a query, gets the PowerLoom answers and iterates over them to print
   *  them out.
   *  @param query  The PowerLoom language query to be executed
   *  @param module The name of the module in which to execute the query
   *  @param env  The PowerLoom environment for the query.
   */
  static void printPowerLoomAnswers (String query, String module, Environment env) {
    // variable to hold the query answers, which is a special
    // PowerLoom iterator.
    PlIterator answer = PLI.sRetrieve(query, module, env);
    System.out.println();
    printSeparator();
    System.out.println("Answers to query `" + query + "'");
    while (answer.nextP()) {
      System.out.println(answer.value);
    }
    printSeparator();
  }

  /** Takes a query, gets the PowerLoom answers and iterates over them to print
   *  them out.  This version uses a wrapper around the native PowerLoom iterator
   *  so that the Java Iterator interface can be used.
   *
   *  @param query  The PowerLoom language query to be executed
   *  @param module The name of the module in which to execute the query
   *  @param env  The PowerLoom environment for the query.
   */
  static void printPowerLoomAnswers2 (String query, String module, Environment env) {
    // variable to hold the query answers, which uses a convenience class to wrap
    // the native PowerLoom iterator and provide the standard Java interface.

    java.util.Iterator answer = new StellaIterator(PLI.sRetrieve(query, module, env));
    System.out.println();
    printSeparator();
    System.out.println("Answers to query `" + query + "'");
    while (answer.hasNext()) {
      System.out.println(answer.next());
    }
    printSeparator();
  }

  /** Takes a propositional query, gets the PowerLoom truth value and
   *  prints it out.
   *  @param query  The PowerLoom language query to be executed
   *  @param module The name of the module in which to execute the query
   *  @param env  The PowerLoom environment for the query.
   */
  static void printPowerLoomTruth (String query, String module, Environment env) {
    // variable to hold the query answer, which is a TruthValue object in
    // the edu.isi.powerloom.logic package.
    TruthValue answer = PLI.sAsk(query, module, env);
    System.out.println();
    printSeparator();
    System.out.print("The proposition `" + query + "' is ");
    if (PLI.isTrue(answer)) {
      System.out.println("true");
    } else if (PLI.isFalse(answer)) {
      System.out.println("false");
    } else if (PLI.isUnknown(answer)) {
      System.out.println("unknown");
    }
    printSeparator();
  }

  /** Utility function to print a separator line.
   */
  static void printSeparator () {
    System.out.println("-----------------------------------------");
  }
}