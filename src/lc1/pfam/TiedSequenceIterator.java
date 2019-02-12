/*    */ package lc1.pfam;
/*    */ 
/*    */ import org.biojava.bio.seq.Sequence;
/*    */ import org.biojava.bio.seq.SequenceIterator;
/*    */ import org.biojava.utils.ChangeType;
/*    */ 
/*    */ 
/*    */ public abstract interface TiedSequenceIterator
/*    */   extends SequenceIterator
/*    */ {
/* 11 */   public static final TiedSequenceIterator NULL_ITERATOR = new TiedSequenceIterator() {
/*    */     public boolean hasNext() {
/* 13 */       return false;
/*    */     }
/*    */     
/*    */     public Sequence nextSequence() {
/* 17 */       return null;
/*    */     }
/*    */     
/*    */     public Sequence nextSequence(int i) {
/* 21 */       return null;
/*    */     }
/*    */     
/*    */     public void registerIncrementForwarder(ChangeType ct, boolean skip) {
/*    */       try {
/* 26 */         throw new Exception("null iterator");
/*    */       }
/*    */       catch (Throwable t) {
/* 29 */         t.printStackTrace();
/*    */       }
/*    */     }
/*    */     
/*    */     public ChangeType getChangeType() {
/*    */       try {
/* 35 */         throw new Exception("null iterator");
/*    */       }
/*    */       catch (Throwable t) {
/* 38 */         t.printStackTrace();
/*    */       }
/* 40 */       return null;
/*    */     }
/*    */   };
/*    */   
/*    */   public abstract void registerIncrementForwarder(ChangeType paramChangeType, boolean paramBoolean);
/*    */   
/*    */   public abstract Sequence nextSequence(int paramInt);
/*    */   
/*    */   public abstract ChangeType getChangeType();
/*    */ }


/* Location:              /home/lachlan/Desktop/psilc1.21/psilc.jar!/lc1/pfam/TiedSequenceIterator.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */