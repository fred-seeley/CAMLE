import java.util.*;
import java.io.*;
import java.lang.reflect.Array;
import org.antlr.runtime.*;
import org.antlr.runtime.tree.*;

public class Irt
{
// CAMLE TOKENS BEGIN
    public static final String[] tokenNames = new String[] {"NONE", "NONE", "NONE", "NONE", "TRUE", "FALSE", "IF", "THEN", "ELSE", "DO", "WHILE", "SKIP", "READ", "WRITE", "WRITELN", "SEMICOLON", "OPENPAREN", "CLOSEPAREN", "ASSIGN", "AND", "OR", "NOT", "GT", "GEQ", "EQ", "NEQ", "LEQ", "LT", "ADD", "SUB", "MUL", "IDENT", "INTNUM", "STRING", "COMMENT", "WS"};
    public static final int CLOSEPAREN = 17;
    public static final int LT = 27;
    public static final int WHILE = 10;
    public static final int ELSE = 8;
    public static final int DO = 9;
    public static final int SUB = 29;
    public static final int SEMICOLON = 15;
    public static final int NOT = 21;
    public static final int AND = 19;
    public static final int MUL = 30;
    public static final int TRUE = 4;
    public static final int WRITE = 13;
    public static final int IF = 6;
    public static final int INTNUM = 32;
    public static final int SKIP = 11;
    public static final int NEQ = 25;
    public static final int WS = 35;
    public static final int THEN = 7;
    public static final int WRITELN = 14;
    public static final int READ = 12;
    public static final int OR = 20;
    public static final int ASSIGN = 18;
    public static final int GT = 22;
    public static final int IDENT = 31;
    public static final int GEQ = 23;
    public static final int OPENPAREN = 16;
    public static final int EQ = 24;
    public static final int COMMENT = 34;
    public static final int FALSE = 5;
    public static final int STRING = 33;
    public static final int LEQ = 26;
    public static final int ADD = 28;
// CAMLE TOKENS END
    
    private static int labels = 0;
    
    
    public static IRTree convert(CommonTree ast)
    {
        return program(ast);
    }
    
    public static IRTree program(CommonTree ast)
    {
        return statements(ast);
    }
    
    public static IRTree statements(CommonTree ast)
    {
        IRTree irt = null;
        
        Token token = ast.getToken();
        int tokenType = ast.getToken().getType();
        if (tokenType == SEMICOLON && ast.getChildCount() == 2)
        {
            CommonTree ast1 = (CommonTree)ast.getChild(0);
            CommonTree ast2 = (CommonTree)ast.getChild(1);
            
            IRTree irt1 = statements(ast1);
            IRTree irt2 = statements(ast2);
            
            irt = new IRTree("SEQ", irt1, irt2);
        }
        else if (tokenType != SEMICOLON)
        {
            irt = statement(ast);
        }
        else
        {
            error(token);
        }
        
        return irt;
    }
    
    public static IRTree statement(CommonTree ast)
    {
        IRTree irt = null;
        
        Token token = ast.getToken();
        int tokenType = ast.getToken().getType();
        
        if (tokenType == ASSIGN)
        {
            CommonTree ast1 = (CommonTree)ast.getChild(0);
            CommonTree ast2 = (CommonTree)ast.getChild(1);
            
            IRTree irt1 = variable(ast1);            
            IRTree irt2 = expression(ast2);
            
            irt = new IRTree("MOVE", irt1, irt2);
        }
        else if (tokenType == SKIP)
        {
            irt = new IRTree("NOP");
        }
        else if (tokenType == IF)
        {
            CommonTree ast1 = (CommonTree)ast.getChild(0);
            CommonTree ast2 = (CommonTree)ast.getChild(1);
            CommonTree ast3 = (CommonTree)ast.getChild(2);
            
            String n = newLabel();
            String n1 = newLabel();
            String n2 = newLabel();
            
            IRTree irt5 = new IRTree("SEQ", statements(ast3), label(n));
            
            IRTree irt4 = new IRTree("SEQ", label(n2), irt5);
            
            IRTree irt3 = new IRTree("SEQ", jump(n), irt4);
            
            IRTree irt2 = new IRTree("SEQ", statements(ast2), irt3);
            
            IRTree irt1 = new IRTree("SEQ", label(n1), irt2);
            
            irt = new IRTree("SEQ", cjump(ast1, n1, n2), irt1);
        }
        else if (tokenType == WHILE)
        {
            CommonTree ast1 = (CommonTree)ast.getChild(0);
            CommonTree ast2 = (CommonTree)ast.getChild(1);
            
            String n = newLabel();
            String n1 = newLabel();
            String n2 = newLabel();
            
            IRTree irt4 = new IRTree("SEQ", jump(n1), label(n));
            
            IRTree irt3 = new IRTree("SEQ", statements(ast2), irt4);
            
            IRTree irt2 = new IRTree("SEQ", label(n2), irt3);
            
            IRTree irt1 = new IRTree("SEQ", cjump(ast1, n2, n), irt2);
            
            irt = new IRTree("SEQ", label(n1), irt1);
        }
        else if (tokenType == READ)
        {
            CommonTree ast1 = (CommonTree)ast.getChild(0);
            
            IRTree irt1 = variable(ast1);
            
            irt = new IRTree("RD", irt1);
        }
        else if (tokenType == WRITE)
        {
            CommonTree ast1 = (CommonTree)ast.getChild(0);
            Token token1 = ast1.getToken();
            
            if (token1.getType() == STRING)
            {
                String s = token1.getText();
                
                irt = wrs(s);
            }
            else if (token1.getType() == INTNUM || token1.getType() == IDENT || token1.getType() == SUB 
                            || token1.getType() == ADD || token1.getType() == MUL)
            {
                IRTree irt1 = expression(ast1);
                
                irt = new IRTree("WR", irt1);
            }
            else if (token1.getType() == TRUE || token1.getType() == FALSE || token1.getType() == AND
                            || token1.getType() == OR || token1.getType() == NOT)
            {
                String n = newLabel();
                String n1 = newLabel();
                String n2 = newLabel();
                
                IRTree irt5 = new IRTree("SEQ", wrs("false"), label(n));
                
                IRTree irt4 = new IRTree("SEQ", label(n2), irt5);
                
                IRTree irt3 = new IRTree("SEQ", jump(n), irt4);
                
                IRTree irt2 = new IRTree("SEQ", wrs("true"), irt3);
                
                IRTree irt1 = new IRTree("SEQ", label(n1), irt2);
                
                irt = new IRTree("SEQ", cjump(ast1, n1, n2), irt1);
            }
            else
            {
                error(token1);
            }
        }
        else if (tokenType == WRITELN)
        {
            irt = wrs("\n");
        }
        else
        {
            error(token);
        }
        
        return irt;
    }
    
    public static IRTree expression(CommonTree ast)
    {
        IRTree irt = null;
        
        Token token = ast.getToken();
        int tokenType = ast.getToken().getType();
        if (tokenType == INTNUM)
        {
            irt = constant(ast);
        }
        else if (tokenType == IDENT && Memory.isAllocated(token.getText()))
        {
            irt = variable(ast);
        }
        else if (tokenType == ADD || tokenType == SUB || tokenType == MUL)
        {
            CommonTree ast1 = (CommonTree)ast.getChild(0);
            CommonTree ast2 = (CommonTree)ast.getChild(1);
            
            IRTree irt1 = expression(ast1);
            IRTree irt2 = expression(ast2);
            
            if(irt1.getOp().equals("CONST") && irt2.getOp().equals("CONST"))
            {
                Integer x = Integer.parseInt(irt1.getSub(0).getOp());
                Integer y = Integer.parseInt(irt2.getSub(0).getOp());
                
                if (tokenType == ADD)
                {
                    irt = new IRTree("CONST", new IRTree(Integer.toString(x + y)));
                }
                else if (tokenType == SUB)
                {
                    irt = new IRTree("CONST", new IRTree(Integer.toString(x - y)));
                }
                else //if (tokenType == MUL)
                {
                    irt = new IRTree("CONST", new IRTree(Integer.toString(x * y)));
                }
            }
            else
            {
                irt = new IRTree("BINOP", new IRTree(tokenNames[tokenType]), irt1, irt2);
            }
        }
        else
        {
            error(token);
        }
        
        return irt;
    }
    
    public static IRTree cjump(CommonTree ast, String n1, String n2)
    {
        IRTree irt = null;
        
        Token token = ast.getToken();
        int tokenType = token.getType();
        
        IRTree op = new IRTree(tokenNames[ast.getToken().getType()]);
        
        if (tokenType == AND)
        {
            CommonTree ast1 = (CommonTree)ast.getChild(0);
            CommonTree ast2 = (CommonTree)ast.getChild(1);
            
            String n3 = newLabel();
            
            IRTree irt1 = new IRTree("SEQ", label(n3), cjump(ast2, n1, n2));
            
            irt = new IRTree("SEQ", cjump(ast1, n3, n2), irt1);
        }
        else if (tokenType == OR)
        {
            CommonTree ast1 = (CommonTree)ast.getChild(0);
            CommonTree ast2 = (CommonTree)ast.getChild(1);
            
            String n3 = newLabel();
            
            IRTree irt1 = new IRTree("SEQ", label(n3), cjump(ast2, n1, n2));
            
            irt = new IRTree("SEQ", cjump(ast1, n1, n3), irt1);
        }
        else if (tokenType == NOT)
        {
            CommonTree ast1 = (CommonTree)ast.getChild(0);
            
            irt = cjump(ast1, n2, n1);
        }
        else if (tokenType == GT || tokenType == GEQ || tokenType == EQ || tokenType == NEQ || tokenType == LEQ || tokenType == LT)
        {
            IRTree e1 = expression((CommonTree)ast.getChild(0));
            IRTree e2 = expression((CommonTree)ast.getChild(1));
            
            if(e1.getOp().equals("CONST") && e2.getOp().equals("CONST"))
            {
                Integer x = Integer.parseInt(e1.getSub(0).getOp());
                Integer y = Integer.parseInt(e2.getSub(0).getOp());
                
                if(tokenType == GT && x > y)
                {
                    irt = jump(n1);
                }
                else if(tokenType == GEQ && x >= y)
                {
                    irt = jump(n1);
                }
                else if(tokenType == EQ && x == y)
                {
                    irt = jump(n1);
                }
                else if(tokenType == NEQ && x != y)
                {
                    irt = jump(n1);
                }
                else if(tokenType == LEQ && x <= y)
                {
                    irt = jump(n1);
                }
                else if(tokenType == LT && x < y)
                {
                    irt = jump(n1);
                }
                else
                {
                    irt = jump(n2);
                }
            }
            else
            {
                irt = new IRTree("CJUMP", op, e1, e2, name(n1), name(n2));
            }
        }
        else if (tokenType == TRUE)
        {
            irt = jump(n1);
        }
        else if (tokenType == FALSE)
        {
            irt = jump(n2);
        }
        else
        {
            error(token);
        }
        
        return irt;
    }
    
    public static IRTree constant(CommonTree ast)
    {
        IRTree irt = null;
        
        Token token = ast.getToken();
        int tokenType = ast.getToken().getType();
        if (tokenType == INTNUM)
        {
            String t = token.getText();
            
            irt = new IRTree("CONST", new IRTree(t));
        }
        else
        {
            error(token);
        }
        
        return irt;
    }
    
    public static IRTree variable(CommonTree ast)
    {
        IRTree irt = null;
        
        Token token = ast.getToken();
        int tokenType = ast.getToken().getType();
        if (tokenType == IDENT && token.getText().length() <= 8)
        {
            String s = token.getText();
            String m = String.valueOf(Memory.getVariableAddress(s));
            
            IRTree irt1 = new IRTree("CONST", new IRTree(m));
            
            irt = new IRTree("MEM", irt1);
        }
        else
        {
            error(token);
        }
        
        return irt;
    }
    
    public static IRTree wrs(String s)
    {
        String m = String.valueOf(Memory.allocateString(s));
        
        IRTree irt = new IRTree("MEM", new IRTree("CONST", new IRTree(m)));
        
        return new IRTree("WRS", irt);
    }
    
    public static IRTree jump(String label)
    {
        return new IRTree("JUMP", name(label));
    }
    
    public static IRTree name(String label)
    {
        return new IRTree("NAME", new IRTree(label));
    }
    
    public static IRTree label(String label)
    {
        return new IRTree("LABEL", new IRTree(label));
    }
    
    public static String newLabel()
    {
        labels ++;
        return "l" + Integer.toString(labels);
    }
    
    private static void error(Token token)
    {
        System.out.println("line " + token.getLine() + ":" + token.getCharPositionInLine() + " error at token '" + token.getText() + "'");
        System.exit(1);
    }
}
