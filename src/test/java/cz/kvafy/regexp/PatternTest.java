package cz.kvafy.regexp;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;



public class PatternTest extends Assert {
    @Test
    public void singleCharMatch() {
        String text = "a";
        Pattern pattern = Pattern.compile("a");
        Matcher matcher = pattern.matcher(text);
        assertTrue(matcher.matches());
        assertEquals(text, matcher.group());
        assertEquals(text, matcher.group(0));
    }
    
    @Test
    public void singleCharMatch_failOnPrefix() {
        String text = "ba";
        Pattern pattern = Pattern.compile("a");
        Matcher matcher = pattern.matcher(text);
        assertFalse(matcher.matches());
    }
    
    @Test
    public void singleCharMatch_failOnPostfix() {
        String text = "ab";
        Pattern pattern = Pattern.compile("a");
        Matcher matcher = pattern.matcher(text);
        assertFalse(matcher.matches());
    }
    
    @Test
    public void singleCharFind_simple() {
        String text = "a";
        Pattern pattern = Pattern.compile("a");
        Matcher matcher = pattern.matcher(text);
        assertTrue(matcher.find());
        assertEquals(text, matcher.group());
    }
    
    @Test
    public void singleCharFind_withPretfix() {
        String text = "ba";
        String patternText = "a";
        Pattern pattern = Pattern.compile(patternText);
        Matcher matcher = pattern.matcher(text);
        assertTrue(matcher.find());
        assertEquals(patternText, matcher.group());
    }
    
    @Test
    public void singleCharFind_withPostfix() {
        String text = "ab";
        String patternText = "a";
        Pattern pattern = Pattern.compile(patternText);
        Matcher matcher = pattern.matcher(text);
        assertTrue(matcher.find());
        assertEquals(patternText, matcher.group());
    }
    
    @Test
    public void escapedSpecialCharMatch_backslash() {
        String text = "\\";
        Pattern pattern = Pattern.compile("\\\\");
        Matcher matcher = pattern.matcher(text);
        assertTrue(matcher.matches());
        assertEquals(text, matcher.group());
    }
    
    @Test
    public void escapedSpecialCharFind_backslash() {
        String text = "a\\b";
        Pattern pattern = Pattern.compile("\\\\");
        Matcher matcher = pattern.matcher(text);
        assertTrue(matcher.find());
        assertEquals("\\", matcher.group());
    }
    
    @Test
    public void escapedSpecialCharMatch_parenLeft() {
        String text = "(";
        Pattern pattern = Pattern.compile("\\(");
        Matcher matcher = pattern.matcher(text);
        assertTrue(matcher.matches());
        assertEquals(text, matcher.group());
    }
    
    @Test
    public void escapedSpecialCharFind_parenLeft() {
        String text = "a(b";
        Pattern pattern = Pattern.compile("\\(");
        Matcher matcher = pattern.matcher(text);
        assertTrue(matcher.find());
        assertEquals("(", matcher.group());
    }
    
    @Test
    public void escapedSpecialCharMatch_parenRight() {
        String text = ")";
        Pattern pattern = Pattern.compile("\\)");
        Matcher matcher = pattern.matcher(text);
        assertTrue(matcher.matches());
        assertEquals(text, matcher.group());
    }
    
    @Test
    public void escapedSpecialCharFind_parenRight() {
        String text = "a)b";
        Pattern pattern = Pattern.compile("\\)");
        Matcher matcher = pattern.matcher(text);
        assertTrue(matcher.find());
        assertEquals(")", matcher.group());
    }

    @Test
    public void greedyStarMatch() {
        String text = "aaaaa";
        Pattern pattern = Pattern.compile("a*");
        Matcher matcher = pattern.matcher(text);
        assertTrue("Matcher not matching", matcher.matches());
        assertEquals(text, matcher.group());
        assertEquals(text, matcher.group(0));
    }

    @Test
    public void greedyStarMatch_takesMaximum() {
        String text = "aaaaa";
        Pattern pattern = Pattern.compile("(a*)(a*)");
        Matcher matcher = pattern.matcher(text);
        assertTrue("Matcher not matching", matcher.matches());
        assertEquals(text, matcher.group());
        assertEquals(text, matcher.group(0));
        assertEquals(text, matcher.group(1));
        assertEquals("", matcher.group(2));
    }
    
    @Test
    public void greedyStarMatch_failOnPrefix() {
        String text = "baaaaa";
        Pattern pattern = Pattern.compile("a*");
        Matcher matcher = pattern.matcher(text);
        assertFalse(matcher.matches());
    }
    
    @Test
    public void greedyStarMatch_failOnPostfix() {
        String text = "aaaaab";
        Pattern pattern = Pattern.compile("a*");
        Matcher matcher = pattern.matcher(text);
        assertFalse(matcher.matches());
    }
    
    @Test
    public void nongreedyStarMatch() {
        String text = "aaaaa";
        Pattern pattern = Pattern.compile("a*?");
        Matcher matcher = pattern.matcher(text);
        assertTrue("Matcher not matching", matcher.matches());
        assertEquals(text, matcher.group());
        assertEquals(text, matcher.group(0));
    }

    @Test
    public void nongreedyStarMatch_takesMinimum() {
        String text = "aaaaa";
        Pattern pattern = Pattern.compile("(a*?)(a*?)");
        Matcher matcher = pattern.matcher(text);
        assertTrue("Matcher not matching", matcher.matches());
        assertEquals(text, matcher.group());
        assertEquals(text, matcher.group(0));
        assertEquals("", matcher.group(1));
        assertEquals(text, matcher.group(2));
    }
    
    @Test
    public void nongreedyStarMatch_failOnPrefix() {
        String text = "baaaaa";
        Pattern pattern = Pattern.compile("a*?");
        Matcher matcher = pattern.matcher(text);
        assertFalse(matcher.matches());
    }
    
    @Test
    public void nongreedyStarMatch_failOnPostfix() {
        String text = "aaaaab";
        Pattern pattern = Pattern.compile("a*?");
        Matcher matcher = pattern.matcher(text);
        assertFalse(matcher.matches());
    }
    
    @Test
    public void greedyStarFind() {
        String MATCHED_TEXT = "aaaa";
        String text = "bced" + MATCHED_TEXT + "kljih";
        Pattern pattern = Pattern.compile("aa*");
        Matcher matcher = pattern.matcher(text);
        assertTrue("Matcher not matching", matcher.find());
        assertEquals(MATCHED_TEXT, matcher.group());
        assertEquals(MATCHED_TEXT, matcher.group(0));
    }
    
    @Test
    public void nongreedyStarFind() {
        String text = "aaaaa";
        Pattern pattern = Pattern.compile("a*?");
        Matcher matcher = pattern.matcher(text);
        assertTrue("Matcher not matching", matcher.find());
        assertEquals("", matcher.group());
        assertEquals("", matcher.group(0));
    }
    
    
    
    @Test
    public void greedyPlusMatch() {
        String text = "aaaaa";
        Pattern pattern = Pattern.compile("a+");
        Matcher matcher = pattern.matcher(text);
        assertTrue(matcher.matches());
        assertEquals(text, matcher.group());
        assertEquals(text, matcher.group(0));
    }

    @Test
    public void greedyPlusMatch_takesMaximum() {
        String text = "aaaaa";
        Pattern pattern = Pattern.compile("(a+)(a+)");
        Matcher matcher = pattern.matcher(text);
        assertTrue(matcher.matches());
        assertEquals("aaaa", matcher.group(1));
        assertEquals("a", matcher.group(2));
    }
    
    @Test
    public void nongreedyPlusMatch() {
        String text = "aaaaa";
        Pattern pattern = Pattern.compile("a+?");
        Matcher matcher = pattern.matcher(text);
        assertTrue(matcher.matches());
        assertEquals(text, matcher.group());
        assertEquals(text, matcher.group(0));
    }

    @Test
    public void nongreedyPlusMatch_takesMinimum() {
        String text = "aaaaa";
        Pattern pattern = Pattern.compile("(a+?)(a+?)");
        Matcher matcher = pattern.matcher(text);
        assertTrue(matcher.matches());
        assertEquals("a", matcher.group(1));
        assertEquals("aaaa", matcher.group(2));
    }
    
    @Test
    public void greedyPlusFind() {
        String text = "aaaaa";
        Pattern pattern = Pattern.compile("a+");
        Matcher matcher = pattern.matcher(text);
        assertTrue(matcher.find());
        assertEquals(text, matcher.group());
        assertEquals(text, matcher.group(0));
    }
    
    @Test
    public void nongreedyPlusFind() {
        String text = "aaaaa";
        Pattern pattern = Pattern.compile("a+?");
        Matcher matcher = pattern.matcher(text);
        assertTrue(matcher.find());
        assertEquals("a", matcher.group());
    }
    
    
    
    @Test
    public void greedyQuestionmarkMatch_singleChar() {
        String text = "a";
        Pattern pattern = Pattern.compile("a?");
        Matcher matcher = pattern.matcher(text);
        assertTrue(matcher.matches());
        assertEquals(text, matcher.group());
        assertEquals(text, matcher.group(0));
    }
    
    @Test
    public void greedyQuestionmarkMatch_emptyString() {
        String text = "";
        Pattern pattern = Pattern.compile("a?");
        Matcher matcher = pattern.matcher(text);
        assertTrue(matcher.matches());
        assertEquals(text, matcher.group());
        assertEquals(text, matcher.group(0));
    }
    
    @Test
    public void nongreedyQuestionmarkMatch_singleChar() {
        String text = "a";
        Pattern pattern = Pattern.compile("a??");
        Matcher matcher = pattern.matcher(text);
        assertTrue(matcher.matches());
        assertEquals(text, matcher.group());
    }
    
    @Test
    public void nongreedyQuestionmarkMatch_emptyString() {
        String text = "";
        Pattern pattern = Pattern.compile("a??");
        Matcher matcher = pattern.matcher(text);
        assertTrue(matcher.matches());
        assertEquals("", matcher.group());
    }
    
    @Test
    public void greedyQuestionmarkFind() {
        String text = "aabbaa";
        Pattern pattern = Pattern.compile("bb?");
        Matcher matcher = pattern.matcher(text);
        assertTrue(matcher.find());
        assertEquals("bb", matcher.group());
    }
    
    @Test
    public void nongreedyQuestionmarkFind() {
        String text = "aabbaa";
        Pattern pattern = Pattern.compile("bb??");
        Matcher matcher = pattern.matcher(text);
        assertTrue(matcher.find());
        assertEquals("b", matcher.group());
    }
    
    @Test
    public void captureOneBracket() {
        String text = "a1a";
        Pattern pattern = Pattern.compile(".(1).");
        Matcher matcher = pattern.matcher(text);
        assertTrue(matcher.matches());
        assertEquals("1", matcher.group(1));
    }

    @Test
    public void capture_group0TakesAll() {
        String text = "abc123456";
        Pattern pattern = Pattern.compile(".*");
        Matcher matcher = pattern.matcher(text);
        assertTrue(matcher.matches());
        assertEquals(text, matcher.group(0));
    }

    @Test
    public void captureTwoBrackets() {
        String text = "aa12aa34aa";
        Pattern pattern = Pattern.compile(".*(12).*(34).*");
        Matcher matcher = pattern.matcher(text);
        assertTrue(matcher.matches());
        assertEquals("12", matcher.group(1));
        assertEquals("34", matcher.group(2));
    }

    @Test
    public void captureIteration_lastPassCaptured() {
        String text = "ab01";
        Pattern pattern = Pattern.compile("(ab|01)+");
        Matcher matcher = pattern.matcher(text);
        assertTrue(matcher.matches());
        assertEquals("01", matcher.group(1));
    }

    @Test
    public void backreference_okMatch() {
        String text = "catcat";
        Pattern pattern = Pattern.compile("(cat)\\1");
        Matcher matcher = pattern.matcher(text);
        assertTrue(matcher.matches());
        assertEquals(text, matcher.group());
    }
    
    @Test
    public void backreferenceWithAlteration_okMatch() {
        String text = "catcat";
        Pattern pattern = Pattern.compile("(cat|dog)\\1");
        Matcher matcher = pattern.matcher(text);
        assertTrue(matcher.matches());
        assertEquals(text, matcher.group());
    }
    
    @Test
    public void backreferenceWithAlteration_failedMatch() {
        String text = "catdog";
        Pattern pattern = Pattern.compile("(cat|dog)\\1");
        Matcher matcher = pattern.matcher(text);
        assertFalse(matcher.matches());
    }

    @Test
    public void noncapturingGroupAndCapturingGroups() {
        String text = "---catdogcat";
        Pattern patter = Pattern.compile("(:?---)(cat|dog)(dog)\\1");
        Matcher matcher = patter.matcher(text);
        assertTrue(matcher.matches());
        assertEquals("cat", matcher.group(1));
        assertEquals("dog", matcher.group(2));
    }

    @Test(expected = RuntimeException.class)
    public void noncapturingGroupCannotBeReferenced_semanticError() {
        Pattern.compile("(:?cat|dog)\\1");
    }

    @Test(expected = RuntimeException.class)
    public void backreferenceWhenNoCapturingGroup_failedCompile() {
        Pattern.compile("abc\\1");
    }

    @Test(expected = RuntimeException.class)
    public void backreferenceTooHighNumer_failedCompile() {
        Pattern.compile("(abc)\\1\\2");
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void unbalancedBrackets_fail() {
        Pattern.compile("abc(def");
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void backslashNothing_fail() {
        Pattern.compile("\\");
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void backslashRegularCharacter_fail() {
        Pattern.compile("\\_");
    }
}
