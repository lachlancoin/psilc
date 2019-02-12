/*     */ package lc1.domainseq;
/*     */ 
/*     */ import java.io.PrintStream;
/*     */ import lc1.util.Print;
/*     */ 
/*     */ 
/*     */ 
/*     */ public class PfamSequenceScore
/*     */   implements SequenceScore
/*     */ {
/*     */   private Double[] score;
/*     */   private int start;
/*     */   Double score0;
/*     */   
/*     */   public int length()
/*     */   {
/*  17 */     return this.score.length;
/*     */   }
/*     */   
/*     */   public int start()
/*     */   {
/*  22 */     return this.start;
/*     */   }
/*     */   
/*     */   public PfamSequenceScore(int len, int start) {
/*  26 */     this.score = new Double[len];
/*  27 */     this.start = start;
/*     */   }
/*     */   
/*     */   public void subtract(double val) {
/*  31 */     for (int i = 0; i < this.score.length; i++) {
/*  32 */       this.score[i] = new Double(this.score[i].doubleValue() - val);
/*     */     }
/*     */   }
/*     */   
/*     */   public void set(int pos, double val) {
/*  37 */     this.score[(pos - 1)] = new Double(val);
/*     */   }
/*     */   
/*     */   public double full()
/*     */   {
/*  42 */     return this.score[(this.score.length - 1)].doubleValue();
/*     */   }
/*     */   
/*     */   public void restrict(int pos) throws Exception {
/*  46 */     if (pos > this.score.length) throw new Exception("pos: " + pos + "length " + this.score.length);
/*  47 */     Double[] new_score = new Double[this.score.length - pos + 1];
/*  48 */     System.arraycopy(this.score, pos - 1, new_score, 0, new_score.length);
/*  49 */     this.score = new_score;
/*  50 */     this.score0 = null;
/*  51 */     this.start = (this.start + pos - 1);
/*     */   }
/*     */   
/*     */   public double get(int pos)
/*     */   {
/*  56 */     return getDouble(pos).doubleValue();
/*     */   }
/*     */   
/*     */ 
/*     */   Double getDouble(int pos)
/*     */   {
/*  62 */     if (pos == 0) {
/*  63 */       if (this.score0 != null) return this.score0;
/*  64 */       return this.score[0];
/*     */     }
/*  66 */     return this.score[(pos - 1)];
/*     */   }
/*     */   
/*     */   public SequenceScore setPreviousBest(int new_start, int length1, double transScore)
/*     */     throws Exception
/*     */   {
/*  72 */     int start_point = this.start + new_start - 1;
/*  73 */     if (new_start < 1) {
/*  74 */       length1 = length();
/*  75 */       start_point += -(new_start + 1);
/*  76 */       new_start = 1;
/*     */     }
/*  78 */     System.out.println(new_start + ":" + length1 + ": " + length());
/*  79 */     PfamSequenceScore bestScores = new PfamSequenceScore(length1, start_point);
/*  80 */     if (new_start > 2) bestScores.score0 = getDouble(new_start - 1);
/*  81 */     for (int i = 1; i <= bestScores.length(); i++)
/*     */     {
/*  83 */       bestScores.set(i, get(new_start + i - 1) + transScore);
/*     */     }
/*  85 */     return bestScores;
/*     */   }
/*     */   
/*     */ 
/*     */   public static int convertCoords(int start1, int start2, int pos2)
/*     */   {
/*  91 */     return pos2 + start2 - 1 - start1 + 1;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String toString()
/*     */   {
/* 100 */     return Print.toString(this.score);
/*     */   }
/*     */ }


/* Location:              /home/lachlan/Desktop/psilc1.21/psilc.jar!/lc1/domainseq/PfamSequenceScore.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */