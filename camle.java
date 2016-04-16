import java.io.*;
import org.antlr.runtime.*;
import org.antlr.runtime.tree.*;

class camle
{
    public static void main(String[] args)
    {
        System.out.println("CAMLE - Compiler to Abstract Machine for Language Engineering");
        String opt = "", source = "", output = "";
        
        if (args.length == 1 && args[0].charAt(0) != '-')
        {
            opt = "";
            source = args[0];
        }
        else if (args.length == 2 && args[0].charAt(0) == '-' && args[1].charAt(0) != '-')
        {
            opt = args[0];
            source = args[1];
        }
        else
        {
            System.out.println("Usage: antlr3 camle [-lex | -syn | -irt] SOURCE");
            System.exit(1);
        }
        
        int x;
        if ((x = source.lastIndexOf('.')) != -1)
        {
            output = source.substring(0, x);
        }
        output += ".ass";
        
        try
        {
            CharStream cs = new ANTLRFileStream(source);			
            Lex lexer = new Lex(cs);
            
            if (opt.equals("-lex"))
            {
                Token T = lexer.nextToken();
                while (T.getType() != -1)
                {
                    System.out.println(T.getType() + " \"" + T.getText() + "\"");
                    T = lexer.nextToken();
                }
                System.exit(0);
            }
            
            Syn parser = new Syn(new CommonTokenStream(lexer));
            
            CommonTree parserTree = (CommonTree) parser.program().getTree();

            if(parser.getNumberOfSyntaxErrors() > 0)
            {
                System.exit(1);
            }
            
            if (opt.equals("-syn"))
            {
                System.out.println(parserTree.toStringTree());
                System.exit(0);
            }
            
            IRTree irt = Irt.convert(parserTree);
            
            if (opt.equals("-irt"))
            {
                System.out.println(irt);
                Memory.dumpData(System.out);
                System.exit(0);
            }
            
            PrintStream o = new PrintStream(new FileOutputStream(output));
            Cg.program(irt, o);
        }
        catch(Exception e)
        {
            System.err.println(e.toString());
            e.printStackTrace();
        }
    }
}
