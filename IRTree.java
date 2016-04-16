import java.util.*;

class IRTree
{
    private String op;
    private ArrayList<IRTree> subs;
    
    public IRTree()
    {
        this.op = op;
        subs = new ArrayList<IRTree>();
    }
    
    public IRTree(String op)
    {
        this.op = op;
        subs = new ArrayList<IRTree>();
    }
    
    public IRTree(String op, IRTree... subs)
    {
        this.op = op;
        this.subs = new ArrayList<IRTree>();
        addSubs(subs);
    }
    
    public void setOp(String op)
    {
        this.op = op;
    }
    
    public void addSub(IRTree sub)
    {
        subs.add(sub);
    }
    
    public void addSubs(IRTree... newSubs)
    {
        for(IRTree sub : newSubs)
        {
            addSub(sub);
        }
    }
    
    public String getOp()
    {
        return op;
    }
    
    public IRTree getSub(int i)
    {
        if (i >= subs.size())
        {
            System.out.println("IRTree error accessing subtree " + i + " of " + op + " node");
        }
        return subs.get(i);
    }
    
    public List<IRTree> getSubs()
    {
        return subs;
    }
    
    public String toString()
    {
        if (subs.size() == 0)
        {
            return op;
        }
        
        String s = "(" + op;
        for (int i = 0; i < subs.size(); i++)
        {
            s += " " + subs.get(i).toString();
        }
        s += ")";
        return s;
    }
}
