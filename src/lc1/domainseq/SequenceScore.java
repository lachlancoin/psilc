package lc1.domainseq;

public abstract interface SequenceScore
{
  public abstract int length();
  
  public abstract int start();
  
  public abstract double get(int paramInt);
  
  public abstract void set(int paramInt, double paramDouble);
  
  public abstract SequenceScore setPreviousBest(int paramInt1, int paramInt2, double paramDouble)
    throws Exception;
  
  public abstract void restrict(int paramInt)
    throws Exception;
  
  public abstract void subtract(double paramDouble);
  
  public abstract double full();
}


/* Location:              /home/lachlan/Desktop/psilc1.21/psilc.jar!/lc1/domainseq/SequenceScore.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */