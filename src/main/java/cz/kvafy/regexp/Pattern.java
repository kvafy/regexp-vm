package cz.kvafy.regexp;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.DefaultErrorStrategy;
import org.antlr.v4.runtime.InputMismatchException;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;

public class Pattern {
    /** Regular expression this Pattern instance represents. */
    private String pattern;
    /** The pattern compiled into code of regexp matching virtual machine. */
    private VMCode compiledRegexp;
    
    private Pattern(String pattern, VMCode compiledRegexp) {
        this.pattern = pattern;
        this.compiledRegexp = compiledRegexp;
    }
    
    /**
     * Compile the given regular expression.
     * @param pattern
     *     A regular expression.
     * @return
     *     Returns <arg>pattern</arg> compiled.
     * @throws IllegalArgumentException
     *     When the <arg>pattern</arg> is syntactically invalid.
     */
    public static Pattern compile(String pattern) {
        // initialize the lexer-parser machinery
        ANTLRInputStream input = new ANTLRInputStream(pattern);
        RegexpLexer lexer = new RegexpLexer(input);
        CommonTokenStream tokenStream = new CommonTokenStream(lexer);
        RegexpParser parser = new RegexpParser(tokenStream);
        
        // make sure we don't attempt to recover from a syntactic error
        parser.setErrorHandler(new BailErrorStrategy());
        
        // parse the input, starting from "init" non-terminal
        ParseTree tree;
        try {
            tree = parser.init();
        }
        catch(RuntimeException ex) {
            throw new IllegalArgumentException("Error compiling pattern \"" + pattern + "\": " + ex.getMessage());
        }
        
        // process the abstract syntax tree using a visit to build VM code
        RegexpTreeToVMCodeVisitor visitor = new RegexpTreeToVMCodeVisitor();
        VMCode vmCode = visitor.visit(tree);
        return new Pattern(pattern, vmCode);
    }
    
    /**
     * Create a matcher for given input string.
     * The matcher can be used to invoke {@link Matcher#matches()} or
     * {@link Matcher#find()} over the given <arg>input</arg>.
     * @param input
     *     String against which to match the regular expression this Pattern
     *     represents.
     * @return
     *     Matcher object for <arg>input</arg>.
     */
    public Matcher matcher(String input) {
        return new Matcher(input, compiledRegexp);
    }
    
    /**
     * Return regular expression, this Pattern represents, in its String form.
     */
    public String pattern() {
        return this.pattern;
    }
}
