grammar Regexp;

init : expr EOF ;

expr // rules with decreasing priority
     : '.'              # dot
     | '\\' DIGIT       # backreference
     | LITERAL_NONDIGIT # literal
     | DIGIT            # literal
     | '(:?' expr ')'   # noncapturingGroup
     | '(' expr ')'     # capturingGroup
     | expr '*?'        # repetitionStarNongreedy
     | expr '+?'        # repetitionPlusNongreedy
     | expr '??'        # repetitionQuestionmarkNongreedy
     | expr '*'         # repetitionStarGreedy
     | expr '+'         # repetitionPlusGreedy
     | expr '?'         # repetitionQuestionmarkGreedy
     | expr expr        # concatenation
     | expr '|' expr    # alteration
     ;

LITERAL_NONDIGIT : '\\' '\\'
                 | '\\' '|'
                 | '\\' '*'
                 | '\\' '('
                 | '\\' ')'
                 | '\\' '.'
                 | ~[\\|*().0-9]
                 ;

DIGIT : [0-9] ;