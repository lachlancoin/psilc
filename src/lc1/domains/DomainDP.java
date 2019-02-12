package lc1.domains;

import org.apache.commons.cli.CommandLine;
import org.biojava.bio.seq.Sequence;

public abstract interface DomainDP
{
  public abstract TransitionScores getModel();
  
  public abstract void setModel(TransitionScores paramTransitionScores);
  
  public abstract void setTable(CommandLine paramCommandLine);
  
  public abstract Sequence getStatePath(Sequence paramSequence);
  
  public abstract double score(Sequence paramSequence);
}


/* Location:              /home/lachlan/Desktop/psilc1.21/psilc.jar!/lc1/domains/DomainDP.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */