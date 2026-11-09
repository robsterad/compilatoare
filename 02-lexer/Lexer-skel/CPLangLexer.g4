lexer grammar CPLangLexer;

@members {
    private int commentDepth = 0;
    private int commentStartLine = 0;
}

// Cuvinte cheie (inainte de ID)
IF    : 'if';
THEN  : 'then';
ELSE  : 'else';
FI    : 'fi';
TRUE  : 'true';
FALSE : 'false';
FOR   : 'for';
DO    : 'do';

// Operatori din manualul CPLang
EQ     : '==';
LE     : '<=';
LT     : '<';
ASSIGN : '=';
PLUS   : '+';
MINUS  : '-';
MUL    : '*';
DIV    : '/';

// Delimitatori
SEMI   : ';';
COMMA  : ',';
LPAREN : '(';
RPAREN : ')';
LBRACE : '{';
RBRACE : '}';

// Literali numerici: .25, 3. si 3.14 sunt FLOAT;
// 15 este INT, iar -15 este MINUS urmat de INT.
FLOAT : [0-9]+ '.' [0-9]* | '.' [0-9]+;
INT   : [0-9]+;

// Tipuri si identificatori
TYPE : [A-Z][a-zA-Z_]*;
ID   : [a-z][a-zA-Z_]*;

// Regula STRING este inclusa ca exemplu autonom. Daca scheletul
// primit la curs are o regula STRING mai completa, pastrati-o pe aceea.
STRING : '"' ('\\' . | ~["\\\r\n])* '"';
UNCLOSED_STRING
    : '"' ('\\' . | ~["\\\r\n])* ('\r'? '\n' | EOF)
      { System.err.println("Eroare lexicala: string neincheiat la linia " + getLine()); }
      -> skip
    ;

// Comentarii de linie
LINE_COMMENT : '//' ~[\r\n]* -> skip;

// Comentarii bloc (eventual imbricate)
BLOCK_COMMENT_START
    : '/*' {
        commentDepth = 1;
        commentStartLine = getLine();
      } -> pushMode(COMMENT_MODE), more
    ;

// Delimitator de inchidere fara deschidere
UNMATCHED_COMMENT_END
    : '*/' {
        System.err.println("Eroare lexicala: */ fara /* la linia " + getLine());
      } -> skip
    ;

WS : [ \t\r\n]+ -> skip;

ERROR_CHAR
    : . {
        System.err.println("Eroare lexicala: caracter necunoscut '" + getText() + "' la linia " + getLine());
      } -> skip
    ;

mode COMMENT_MODE;

// In interiorul comentariului folosim more (nu skip), astfel incat
// regula COMMENT_EOF sa poata fi incercata cand se ajunge la EOF.
COMMENT_OPEN : '/*' { commentDepth++; } -> more;
COMMENT_CLOSE
    : '*/' {
        commentDepth--;
        if (commentDepth == 0) {
            popMode();
            skip();
        } else {
            more();
        }
      }
    ;

COMMENT_EOF
    : EOF {
        System.err.println("Eroare lexicala: comentariu bloc neincheiat, inceput la linia " + commentStartLine);
      } -> skip
    ;

COMMENT_CHAR : . -> more;
