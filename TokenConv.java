// COMS22201: Token converter to allow Java programs to use output from ANTLR

import java.lang.reflect.Array;
import java.io.*;
import java.util.*;

public class TokenConv
{
    public static void main(String[] args)
    {
        String tokensFile = args[0];
        String progFile = args[1];
        String tempFile = "CAMLE_TEMP.txt";
        List<String> tokenName = new ArrayList<String>();
        List<Integer> tokenType = new ArrayList<Integer>();
        List<String> nameOf = new ArrayList<String>();
        String line;
        boolean skipping = false;
        try
        {
            int nTokens = readTokensFile(tokensFile, tokenName, tokenType, nameOf);
            BufferedReader in = new BufferedReader(new FileReader(progFile));
            PrintStream out = new PrintStream(new FileOutputStream(tempFile));
            while ((line = in.readLine()) != null)
            {
                if (line.equals("// CAMLE TOKENS END"))
                {
                    skipping = false;
                }
                if (!skipping)
                {
                    out.println(line);
                }
                if (line.equals("// CAMLE TOKENS BEGIN"))
                {
                    skipping = true;
                    writeTokensCode(out, nTokens, tokenName, tokenType, nameOf);
                }
            }
            in.close();
            out.close();
            new File(tempFile).renameTo(new File(progFile));
        }
        catch (Exception e)
        {
            System.out.println(e.toString());
            System.exit(1);
        }
    }
    
    private static void writeTokensCode(PrintStream out, int nTokens, List<String> tokenName,
                                            List<Integer> tokenType, List<String> nameOf) throws Exception
    {
        int i;
        out.print("    public static final String[] tokenNames = new String[] {");
        out.print("\"" + nameOf.get(0) + "\"");
        for (i = 1; i < nameOf.size(); i++)
        {
            out.print(", \"" + nameOf.get(i) + "\"");
        }
        out.println("};");
        for (i = 0; i < nTokens; i++)
        {
            out.println("    public static final int " + tokenName.get(i) + " = " + tokenType.get(i) + ";");
        }
    }
    
    private static int readTokensFile(String tokensFile, List<String> tokenName,
                                         List<Integer> tokenType, List<String> nameOf) throws Exception
    {
        String line, name;
        String[] part;
        int nTokens = 0;
        int type, i;
        BufferedReader in = new BufferedReader(new FileReader(tokensFile));
        while ((line = in.readLine()) != null)
        {
            part = line.split("=");
            name = part[0];
            type = Integer.parseInt(part[1]);
            tokenName.add(name);
            tokenType.add(type);
            while (nameOf.size() <= type)
            {
                nameOf.add("NONE");
            }
            nameOf.set(type, name);
            nTokens++;
        }
        in.close();
        return nTokens;
    }
}
