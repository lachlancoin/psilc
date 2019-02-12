package lc1.domains;

import java.io.Serializable;
import lc1.domainseq.DomainList.SymbolMap;

public abstract interface TransitionScores
  extends Serializable
{
  public abstract double getTransitionScore(DomainList.SymbolMap paramSymbolMap);
  
  public abstract ContextCount getFrequency();
}


/* Location:              /home/lachlan/Desktop/psilc1.21/psilc.jar!/lc1/domains/TransitionScores.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */