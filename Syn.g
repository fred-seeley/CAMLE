// COMS22201: Syntax analyser

parser grammar Syn;

options
{
    tokenVocab = Lex;
    output = AST;
    backtrack = true;
    memoize = true;
}

@members
{
	private String cleanString(String s)
    {
		String tmp;
		tmp = s.replaceAll("^'", "");
		s = tmp.replaceAll("'$", "");
		tmp = s.replaceAll("''", "'");
		return tmp;
	}
}

program :
    statements
  ;

statements :
    statement ( SEMICOLON^ statement )*
  ;

statement :
  | variable ASSIGN^ exp
  | SKIP
  | IF^ boolexp
    THEN! statement
    ELSE! statement
  | WHILE^ boolexp
    DO! statement
  | READ^ OPENPAREN! variable CLOSEPAREN!
  | WRITE^ OPENPAREN! (  boolexp | exp | string ) CLOSEPAREN!
  | WRITELN^
  | OPENPAREN! statements CLOSEPAREN!
  ;

boolexp :
    boolterm ( (AND | OR)^ boolterm )*
  ;

boolterm :
    bool
  | NOT^ bool
  ;

bool :
    TRUE
  | FALSE
  | exp ( GT | GEQ | EQ | NEQ | LEQ | LT)^ exp
  | OPENPAREN! boolexp CLOSEPAREN!
  ;

exp :
    term ( ( ADD | SUB )^ term )*
  ;

term : 
    factor ( MUL^ factor )*
  ;

factor :
    variable
  | constant
  | OPENPAREN! exp CLOSEPAREN!
  ;

constant :
    INTNUM
  ;

variable :
    IDENT
  ;

string
    scope { String tmp; }
    :
    s=STRING { $string::tmp = cleanString($s.text); }-> STRING[$string::tmp]
;
