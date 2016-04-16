import java.util.*;
import java.io.*;
import java.lang.reflect.Array;
import org.antlr.runtime.*;
import org.antlr.runtime.tree.*;

public class Cg
{
    private static List<Integer> liveRegisters = new ArrayList<Integer>();
    private static Queue<Integer> freeRegisters = new PriorityQueue<Integer>();
    
    public static void program(IRTree irt, PrintStream out)
    {
        emit(out, "XOR R0,R0,R0");    // Initialize R0 to 0
        liveRegisters.add(0);
        statement(irt, out);
        emit(out, "HALT");            // Program must end with HALT
        Memory.dumpData(out);         // Dump DATA lines: initial memory contents
    }
    
    private static void statement(IRTree irt, PrintStream out)
    {
        if (irt.getOp().equals("SEQ"))
        {
            statement(irt.getSub(0), out);
            statement(irt.getSub(1), out);
        }
        else if (irt.getOp().equals("MOVE"))
        {
            String R = expression(irt.getSub(1), out);
            String I = irt.getSub(0).getSub(0).getSub(0).getOp();
            emit(out, "STORE " + R + ",R0," + I);
            freeRegisters(R);
        }
        else if (irt.getOp().equals("NOP"))
        {
            emit(out, "NOP");
        }
        else if (irt.getOp().equals("LABEL"))
        {
            String L = irt.getSub(0).getOp();
            emit(out, L + ":");
        }
        else if (irt.getOp().equals("CJUMP"))
        {
            String op = irt.getSub(0).getOp();
            
            String R1 = expression(irt.getSub(1), out);
            String R2 = expression(irt.getSub(2), out);
            
            String L1 = irt.getSub(3).getSub(0).getOp();
            String L2 = irt.getSub(4).getSub(0).getOp();
            
            String R = getRegister();
            
            if (op.equals("GT"))
            {
                emit(out, "SUB " + R + "," + R2 + "," + R1);
                emit(out, "BLTZ " + R + "," + L1);
            }
            else if (op.equals("GEQ"))
            {
                emit(out, "SUB " + R + "," + R1 + "," + R2);
                emit(out, "BGEZ " + R + "," + L1);
            }
            else if (op.equals("EQ"))
            {
                emit(out, "SUB " + R + "," + R1 + "," + R2);
                emit(out, "BEQZ " + R + "," + L1);
            }
            else if (op.equals("NEQ"))
            {
                emit(out, "SUB " + R + "," + R1 + "," + R2);
                emit(out, "BNEZ " + R + "," + L1);
            }
            else if (op.equals("LEQ"))
            {
                emit(out, "SUB " + R + "," + R2 + "," + R1);
                emit(out, "BGEZ " + R + "," + L1);
            }
            else if (op.equals("LT"))
            {
                emit(out, "SUB " + R + "," + R1 + "," + R2);
                emit(out, "BLTZ " + R + "," + L1);
            }
            else
            {
                error(op);
            }
            emit(out, "JMP " + L2);
            freeRegisters(R, R1, R2);
        }
        else if (irt.getOp().equals("JUMP") && irt.getSub(0).getOp().equals("NAME"))
        {
            String L = irt.getSub(0).getSub(0).getOp();
            emit(out, "JMP " + L);
        }
        else if (irt.getOp().equals("RD") && irt.getSub(0).getOp().equals("MEM") && irt.getSub(0).getSub(0).getOp().equals("CONST"))
        {
            String R = getRegister();
            String I = irt.getSub(0).getSub(0).getSub(0).getOp();
            emit(out, "RD " + R);
            emit(out, "STORE " + R + ",R0," + I);
            freeRegisters(R);
        }
        else if (irt.getOp().equals("WRS") && irt.getSub(0).getOp().equals("MEM") && irt.getSub(0).getSub(0).getOp().equals("CONST"))
        {
            String I = irt.getSub(0).getSub(0).getSub(0).getOp();
            emit(out, "WRS " + I);
        }
        else if (irt.getOp().equals("WR"))
        {
            String R = expression(irt.getSub(0), out);
            emit(out, "WR " + R);
            freeRegisters(R);
        }
        else
        {
            error(irt.getOp());
        }
    }
    
    private static String expression(IRTree irt, PrintStream out)
    {
        String R = null;
        if (irt.getOp().equals("CONST"))
        {
            R = constant(irt, out);
        }
        else if (irt.getOp().equals("MEM") && irt.getSub(0).getOp().equals("CONST"))
        {
            R = variable(irt, out);
        }
        else if (irt.getOp().equals("BINOP"))
        {
            R = getRegister();
            
            String op = irt.getSub(0).getOp();
            String e1 = expression(irt.getSub(1), out);
            String e2 = expression(irt.getSub(2), out);
            
            emit(out, op + " " + R + "," + e1 + "," + e2);
            freeRegisters(e1, e2);
        }
        else
        {
            error(irt.getOp());
        }
        return R;
    }
    
    private static String constant(IRTree irt, PrintStream out)
    {
        String R = getRegister();
        String x = irt.getSub(0).getOp();
        
        emit(out, "ADDI " + R + ",R0," + x);
        
        return R;
    }
    
    private static String variable(IRTree irt, PrintStream out)
    {
        String R = getRegister();
        String I = irt.getSub(0).getSub(0).getOp();
        
        emit(out, "LOAD " + R + ",R0," + I);
        
        return R;
    }
    
    private static String getRegister()
    {
        if (freeRegisters.peek() == null)
        {
            Integer R = liveRegisters.size();
            liveRegisters.add(R);
            return "R" + String.valueOf(R);
        }
        else
        {
            Integer R = freeRegisters.poll();
            liveRegisters.add(R);
            return "R" + String.valueOf(R);
        }
    }
    
    private static void freeRegisters(String... registers)
    {
        for (String register : registers)
        {
            Integer R = Integer.parseInt(register.substring(1));
            liveRegisters.remove(R);
            freeRegisters.add(R);
        }
    }
    
    private static void emit(PrintStream out, String s)
    {
        out.println(s);
    }
    
    private static void error(String op)
    {
        System.out.println("CG error: " + op);
        System.exit(1);
    }
}
