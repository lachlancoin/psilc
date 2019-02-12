/*     */ package lc1.domains;
/*     */ 
/*     */ import java.util.List;
/*     */ import lc1.domainseq.DomainList.SymbolMap;
/*     */ import org.biojava.bio.symbol.Symbol;
/*     */ import org.biojava.bio.symbol.SymbolList;
/*     */ 
/*     */ public class ContextTransitionScores implements TransitionScores
/*     */ {
/*     */   protected Smoothing method;
/*     */   protected ContextCount freq;
/*     */   protected lc1.pfam.DomainAlphabet al;
/*     */   protected Symbol magicSymbol;
/*     */   
/*     */   public ContextTransitionScores(Smoothing method, ContextCount freq)
/*     */   {
/*  17 */     this.method = method;
/*  18 */     this.freq = freq;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public ContextCount getFrequency()
/*     */   {
/*  36 */     return this.freq;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public double getTransitionScore(SymbolList species, List context)
/*     */   {
/*  56 */     int[][][] counts = this.freq.getCounts(context, species);
/*  57 */     return logOdds(counts[0], counts[1]); }
/*     */   
/*  59 */   double log2 = Math.log(2.0D);
/*     */   
/*     */ 
/*  62 */   static double SPECIES = 0.1D;
/*     */   
/*     */ 
/*  65 */   static double CONTEXT = 0.4D;
/*     */   
/*     */   public double getTransitionScore(DomainList.SymbolMap smap) {
/*     */     try {
/*  69 */       if (smap.full().size() == 0) throw new Exception("context cannot have zero length");
/*     */     }
/*     */     catch (Throwable t) {
/*  72 */       t.printStackTrace();
/*     */     }
/*  74 */     int[][][] counts = this.freq.getCounts(smap.full(), smap.species());
/*  75 */     return logOdds(counts[0], counts[1]);
/*     */   }
/*     */   
/*     */ 
/*     */   public double logOdds(int[][] countFull, int[][] countContext)
/*     */   {
/*  81 */     int length_i = countFull.length;
/*  82 */     int length_j = countFull[0].length;
/*  83 */     double randomProb = countFull[(length_i - 1)][(length_j - 1)] / countContext[(length_i - 1)][(length_j - 1)];
/*  84 */     if (randomProb == 0.0D) {
/*  85 */       throw new RuntimeException("random prob undefined ");
/*     */     }
/*     */     
/*  88 */     double result = randomProb;
/*  89 */     for (int i = length_i - 2; i >= 0; i--) {
/*  90 */       result = CONTEXT * result + 
/*  91 */         (1.0D - CONTEXT) * (countFull[i][(length_j - 1)] / countContext[i][(length_j - 1)]);
/*     */     }
/*  93 */     for (int j = length_j - 2; j >= 0; j--) {
/*  94 */       double result_j = countFull[(length_i - 1)][j] / countContext[(length_i - 1)][j];
/*  95 */       for (int i = length_i - 2; i >= 0; i--) {
/*  96 */         result_j = CONTEXT * result_j + 
/*  97 */           (1.0D - CONTEXT) * (countFull[i][j] / countContext[i][j]);
/*     */       }
/*  99 */       result = SPECIES * result + (1.0D - SPECIES) * result_j;
/*     */     }
/*     */     
/* 102 */     double res = Math.log(result / randomProb) / Math.log(2.0D);
/* 103 */     if (Double.isNaN(res)) {
/* 104 */       throw new RuntimeException("is nan " + result + " " + randomProb);
/*     */     }
/* 106 */     return res;
/*     */   }
/*     */ }


/* Location:              /home/lachlan/Desktop/psilc1.21/psilc.jar!/lc1/domains/ContextTransitionScores.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */