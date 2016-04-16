import java.util.ArrayList;
import java.util.HashMap;
import java.io.*;

public class Memory
{
    private static ArrayList<Byte> memory = new ArrayList<Byte>();
    private static HashMap<String, Integer> stringAddresses = new HashMap<String, Integer>();
    private static HashMap<String, Integer> variableAddresses = new HashMap<String, Integer>();
        
    public static int allocateString(String text)
    {
        if (stringAddresses.get(text) != null)
        {
            return stringAddresses.get(text);
        }
        
        int address = memory.size();
        
        for (int i = 0; i < text.length(); i++)
        {
            memory.add(new Byte("", text.charAt(i)));
        }
        
        memory.add(new Byte("", 0));
        
        stringAddresses.put(text, address);
        
        return address;
    }
    
    public static Integer getVariableAddress(String variable)
    {
        if (variableAddresses.containsKey(variable)) 
        {
            return variableAddresses.get(variable);
        }
        else
        {
            return allocateVariable(variable);
        }
    }
    
    public static Integer allocateVariable(String variable)
    {
        int address = memory.size();
        while (address % 4 != 0)
        {
            memory.add(new Byte("", 0));
            address = memory.size();
        }
        
        for (int i = 0; i < 4; i++)
        {
            memory.add(new Byte(variable, 0));
        }
        
        variableAddresses.put(variable, address);
        
        return address;
    }
    
    public static Boolean isAllocated(String variable)
    {
        return variableAddresses.containsKey(variable);
    }
    
    public static void dumpData(PrintStream o)
    {
        for (int i = 0; i < memory.size(); i++)
        {
            String s;
            Byte b = memory.get(i);
            int c = b.getContents();
            if (c >= 32)
            {
                s = String.valueOf((char)c);
            }
            else
            {
                s = ""; // "\\"+String.valueOf(c);
            }
            o.println("DATA " + c + " ; " + s + " " + b.getName());
        }
    }
}

class Byte
{
    String varname;
    int contents;

    Byte(String n, int c)
    {
        varname = n;
        contents = c;
    }

    String getName()
    {
        return varname;
    }

    int getContents()
    {
        return contents;
    }
}
