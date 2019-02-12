/*    */ package lc1.domainseq;
/*    */ 
/*    */ import org.biojava.bio.seq.FeatureHolder;
/*    */ import org.biojava.bio.seq.Sequence;
/*    */ import org.biojava.bio.symbol.IllegalAlphabetException;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class FSDomain
/*    */   extends Domain
/*    */ {
/*    */   protected FSDomain(Sequence sourceSeq, FeatureHolder parent, Domain.Template template)
/*    */     throws IllegalArgumentException, IllegalAlphabetException
/*    */   {
/* 18 */     super(sourceSeq, parent, template);
/*    */   }
/*    */   
/*    */   public SequenceScore getScores(int position) {
/* 22 */     return null;
/*    */   }
/*    */ }


/* Location:              /home/lachlan/Desktop/psilc1.21/psilc.jar!/lc1/domainseq/FSDomain.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */