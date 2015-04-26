grammar Regexp;

init : expr EOF ;

expr : expr '*' '?'     # repetitionStarNongreedy
     | expr '+' '?'     # repetitionPlusNongreedy
     | expr '?' '?'     # repetitionQuestionmarkNongreedy
     | expr '*'         # repetitionStarGreedy
     | expr '+'         # repetitionPlusGreedy
     | expr '?'         # repetitionQuestionmarkGreedy
     | expr expr        # concatenation
     | expr '|' expr    # alteration
     | '(' expr ')'     # capturingGroup
     | '.'              # dot
     | '\\' DIGIT       # backreference
     | LITERAL_NONDIGIT # literal
     | DIGIT            # literal
     ;

LITERAL_NONDIGIT : '\\' '\\'
                 | '\\' '|'
                 | '\\' '\*'
                 | '\\' '('
                 | '\\' ')'
                 | '\\' '\.'
                 | ~[\\|*().0-9]
                 ;

DIGIT : [0-9] ;