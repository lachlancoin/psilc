/*     */ package lc1.dp;
/*     */ 
/*     */ import java.io.PrintStream;
/*     */ import java.util.Arrays;
/*     */ import java.util.Iterator;
/*     */ import org.biojava.bio.symbol.Symbol;
/*     */ import org.biojava.bio.symbol.SymbolList;
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class SimpleNAlignment
/*     */   implements NAlignment
/*     */ {
/*     */   protected Double score;
/*     */   protected int len;
/*     */   protected NMap scores;
/*     */   protected final Object[] dl;
/*     */   
/*     */   public SimpleNAlignment(Object[] doml)
/*     */   {
/*  21 */     this.dl = doml;
/*  22 */     this.len = doml.length;
/*  23 */     int[] limits = new int[this.len];
/*  24 */     for (int i = 0; i < this.len; i++) {
/*  25 */       doml[i] = ((SymbolList)doml[i]).subList(2, ((SymbolList)doml[i]).length() - 1);
/*  26 */       System.out.println("HERE " + ((SymbolList)doml[i]).seqString());
/*  27 */       limits[i] = ((SymbolList)doml[i]).length();
/*     */     }
/*  29 */     System.exit(0);
/*  30 */     this.scores = Combinatorics.scores(limits);
/*  31 */     NAlignment.Helper nHelp = NAlignment.Helper.makeHelper(this.len);
/*  32 */     this.scores.initialiseMatrix(nHelp.getLogProbStart(), 
/*  33 */       nHelp.getLogProb(), 
/*  34 */       nHelp.getLogProbEnd());
/*     */   }
/*     */   
/*     */ 
/*     */   public abstract double sentenceContribution(int[] paramArrayOfInt1, int[] paramArrayOfInt2);
/*     */   
/*     */   protected void update(int[] perm, int[] pos)
/*     */     throws Exception
/*     */   {
/*  43 */     int[] back = Combinatorics.subtract(pos, perm);
/*     */     
/*     */ 
/*  46 */     if (Combinatorics.hasNegative(back)) return;
/*  47 */     double sscore = sentenceContribution(perm, pos);
/*  48 */     Point point = this.scores.getBest(perm, back);
/*  49 */     point.score += sscore;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  55 */     this.scores.set(perm, pos, new Point(point.perm, back, sscore + point.score));
/*     */   }
/*     */   
/*     */   public Point score()
/*     */   {
/*     */     try
/*     */     {
/*  62 */       Iterator it = Combinatorics.count(this.scores.limits);
/*  63 */       it.next();
/*  64 */       int j; for (; it.hasNext(); 
/*     */           
/*     */ 
/*     */ 
/*  68 */           j < this.scores.perms.length)
/*     */       {
/*  65 */         int[] pos = (int[])it.next();
/*     */         
/*  67 */         update(this.scores.match, pos);
/*  68 */         j = 0; continue;
/*     */         
/*  70 */         update(this.scores.perms[j], pos);j++;
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  75 */       return this.scores.getBestFinal();
/*     */     }
/*     */     catch (Throwable t) {
/*  78 */       t.printStackTrace();
/*  79 */       System.exit(1); }
/*  80 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String printAlignment(Point finalTrace)
/*     */   {
/*  91 */     Point trace = finalTrace;
/*  92 */     String str = new String("ALIGNMENT\n ");
/*  93 */     while (!Arrays.equals(trace.position, this.scores.first_pos)) {
/*  94 */       int[] perm = trace.perm;
/*  95 */       int[] pos = trace.position;
/*     */       
/*  97 */       for (int i = 0; i < perm.length; i++) {
/*  98 */         if ((perm[i] != 0) && (pos[i] > 0)) {
/*  99 */           str = str + "\t" + ((SymbolList)this.dl[i]).symbolAt(pos[i]).getName() + "\t";
/*     */         }
/*     */         else {
/* 102 */           str = str + "\t gap \t";
/*     */         }
/*     */       }
/* 105 */       str = str + "\n";
/* 106 */       trace = this.scores.get(perm, pos);
/*     */     }
/* 108 */     return str;
/*     */   }
/*     */ }


/* Location:              /home/lachlan/Desktop/psilc1.21/psilc.jar!/lc1/dp/SimpleNAlignment.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */