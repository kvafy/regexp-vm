package cz.kvafy;

import cz.kvafy.regexp.*;

//TODO unit tests
//TODO regexp enhancements:
//       * repetition operators +,? (greedy and non-greedy variants)
//       * non-capturing parens (:?)

public class RegexpMain {

    public static void main(String[] args) {
        String[] inputsMatchOK = {"ab", "abc", "abcc", "abccc"};
        String[] inputMatchFail = {"abca", "abccd", "basdf"};
        
        log("Compiling the pattern...");
        Pattern pattern = Pattern.compile("ab(c*)");
        
        log("testing positive matches");
        testMatches(pattern, inputsMatchOK, true);
        
        log("testing negative matches");
        testMatches(pattern, inputMatchFail, false);
    }
    
    private static void testMatches(Pattern pattern, String[] inputs, boolean expectedResult) {
        for(String input : inputs) {
            String errorMsg = null;
            Matcher matcher = pattern.matcher(input);
            if(matcher.matches() == expectedResult) {
                if(expectedResult == true) {
                    // group tests
                    java.util.regex.Matcher standardMatcher = standardMatcher(pattern.pattern(), input);
                    standardMatcher.matches(); // force the match
                    for(int i = 0; i < standardMatcher.groupCount(); i++) {
                        if(!matcher.group(i).equals(standardMatcher.group(i))) {
                            errorMsg = "#group(" + i + ") isn't equal";
                            break;
                        }
                    }
                }
            }
            else
                errorMsg = "#matches isn't equal";
            
            if(errorMsg == null)
                System.out.println(" [ok] " + input);
            else
                System.out.println(" [fail] " + input + ": " + errorMsg);
        }
    }
    
    private static java.util.regex.Matcher standardMatcher(String pattern, String input) {
        return java.util.regex.Pattern.compile(pattern).matcher(input);
    }
    
    private static void log(String msg) {
        System.out.println(RegexpMain.class.getName() + "> " + msg);
    }
}
