package lc1.domainseq;

import org.biojava.bio.Annotation;
import org.biojava.bio.seq.SequenceIterator;

public abstract interface DomainMatrix
{
  public abstract void addSequence(MultipleDomainList paramMultipleDomainList, String paramString1, String paramString2, Annotation paramAnnotation);
  
  public abstract SequenceIterator sequences()
    throws Exception;
  
  public abstract void annotate();
}


/* Location:              /home/lachlan/Desktop/psilc1.21/psilc.jar!/lc1/domainseq/DomainMatrix.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */