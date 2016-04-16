// COMS22201: Lexical analyser

lexer grammar Lex;

//---------------------------------------------------------------------------
// KEYWORDS
//---------------------------------------------------------------------------
TRUE         : 'true' ;
FALSE        : 'false' ;

IF           : 'if' ;
THEN         : 'then' ;
ELSE         : 'else' ;

DO           : 'do' ;
WHILE        : 'while' ;

SKIP         : 'skip' ;

READ         : 'read' ;
WRITE        : 'write' ;
WRITELN      : 'writeln' ;

//---------------------------------------------------------------------------
// OPERATORS
//---------------------------------------------------------------------------
SEMICOLON    : ';' ;
OPENPAREN    : '(' ;
CLOSEPAREN   : ')' ;

ASSIGN       : ':=' ;

AND          : '&' ;
OR           : '|' ;
NOT          : '!' ;

GT           : '>' ;
GEQ          : '>=' ;
EQ           : '=' ;
NEQ          : '!=' ;
LEQ          : '<=' ;
LT           : '<' ;

ADD          : '+' ;
SUB          : '-' ;
MUL          : '*' ;



IDENT        : ('A'..'Z' | 'a'..'z') ('A'..'Z' | 'a'..'z' | '0'..'9')*  ;

INTNUM       : ('0'..'9')+ ;

STRING       : '\'' ('\'' '\'' | ~'\'')* '\'' ;

COMMENT      : '{' (~'}')* '}' {skip();} ;

WS           : (' ' | '\t' | '\r' | '\n' )+ {skip();} ;
