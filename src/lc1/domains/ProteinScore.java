package lc1.domains;

import lc1.domainseq.Domain;
import org.biojava.bio.seq.Sequence;

public abstract interface ProteinScore
{
  public abstract float getScore(Sequence paramSequence, Domain paramDomain);
  
  public abstract void preComputeScores();
  
  public abstract char method();
}


/* Location:              /home/lachlan/Desktop/psilc1.21/psilc.jar!/lc1/domains/ProteinScore.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */