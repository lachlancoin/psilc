/*    */ package lc1.domainseq;
/*    */ 
/*    */ 
/*    */ 
/*    */ public class MultipleDomainList
/*    */ {
/*    */   int[][] ds;
/*    */   
/*    */ 
/*    */   double[] score;
/*    */   
/*    */   double[] transitionScore;
/*    */   
/* 14 */   public static final MultipleDomainList BEGIN = new MultipleDomainList();
/*    */   
/* 16 */   int current = 0;
/*    */   
/*    */   public MultipleDomainList(int length) {
/* 19 */     this.ds = new int[length][0];
/* 20 */     this.score = new double[length];
/* 21 */     this.transitionScore = new double[length];
/*    */   }
/*    */   
/*    */   public double getScore(int i) {
/* 25 */     return this.score[i];
/*    */   }
/*    */   
/*    */   public double getTransScore(int i) {
/* 29 */     return this.transitionScore[i];
/*    */   }
/*    */   
/*    */   private MultipleDomainList() {
/* 33 */     this(1);
/* 34 */     this.score[0] = 0.0D;
/* 35 */     this.ds[0] = new int[1];
/*    */   }
/*    */   
/*    */   public int size() {
/* 39 */     return this.ds.length;
/*    */   }
/*    */   
/*    */   public void addSequence(int[] dl, double sc, double trans_sc) throws Exception {
/* 43 */     if (this.current >= this.ds.length) throw new Exception("Already filled up");
/* 44 */     this.score[this.current] = sc;
/* 45 */     this.ds[this.current] = dl;
/* 46 */     this.transitionScore[this.current] = trans_sc;
/* 47 */     this.current += 1;
/*    */   }
/*    */   
/*    */   public int[] sequenceAt(int i)
/*    */   {
/* 52 */     return this.ds[i];
/*    */   }
/*    */ }


/* Location:              /home/lachlan/Desktop/psilc1.21/psilc.jar!/lc1/domainseq/MultipleDomainList.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */