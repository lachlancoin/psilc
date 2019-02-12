/*     */ package lc1.phyhmm;
/*     */ 
/*     */ import JSci.maths.statistics.NormalDistribution;
/*     */ import JSci.maths.statistics.ProbabilityDistribution;
/*     */ import lc1.phyl.FastLikelihoodCalculator;
/*     */ import pal.alignment.SitePattern;
/*     */ import pal.math.ConjugateGradientSearch;
/*     */ import pal.math.MultivariateFunction;
/*     */ import pal.math.MultivariateMinimum;
/*     */ import pal.math.OrthogonalHints;
/*     */ import pal.math.OrthogonalSearch;
/*     */ import pal.substmodel.RateMatrix;
/*     */ import pal.tree.Tree;
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
/*     */ class TreeOptimizer
/*     */ {
/*     */   final FastLikelihoodCalculator lhc;
/*     */   MultivariateMinimum mvm;
/*     */   PriorDistribution prior;
/*     */   Tree tree;
/*     */   RateMatrix substM;
/*     */   
/*     */   TreeOptimizer(SitePattern translatedAlign, RateMatrix substM, Tree tree, boolean[] logscale, double[] mean, double[] stddev)
/*     */   {
/* 245 */     this.tree = tree;
/* 246 */     this.substM = substM;
/* 247 */     int numParams = substM.getNumParameters();
/* 248 */     if (numParams == 1)
/*     */     {
/* 250 */       this.mvm = new OrthogonalSearch();
/*     */     }
/*     */     else
/*     */     {
/* 254 */       this.mvm = new ConjugateGradientSearch();
/*     */     }
/*     */     
/* 257 */     this.lhc = new FastLikelihoodCalculator(translatedAlign, tree);
/* 258 */     this.lhc.setModel(substM);
/* 259 */     ProbabilityDistribution[] dist = new ProbabilityDistribution[logscale.length];
/* 260 */     for (int i = 0; i < dist.length; i++) {
/* 261 */       dist[i] = new NormalDistribution(
/* 262 */         logscale[i] != 0 ? Math.log(mean[i]) : mean[i], 
/* 263 */         stddev[i]);
/*     */     }
/* 265 */     this.prior = new PriorDistribution(dist, logscale);
/*     */   }
/*     */   
/*     */   public double[] optimize()
/*     */   {
/* 270 */     MultivariateFunction mvf = new MultivariateFunction()
/*     */     {
/* 272 */       public int getNumArguments() { return TreeOptimizer.this.substM.getNumParameters(); }
/*     */       
/*     */       public double evaluate(double[] argument) {
/* 275 */         for (int i = 0; i < argument.length; i++) {
/* 276 */           TreeOptimizer.this.substM.setParameter(argument[i], i);
/*     */         }
/*     */         
/* 279 */         TreeOptimizer.this.lhc.setModel(TreeOptimizer.this.substM);
/* 280 */         double result = TreeOptimizer.this.lhc.calculateLogLikelihood() + 
/* 281 */           TreeOptimizer.this.prior.logpdf(argument);
/*     */         
/* 283 */         return -1.0D * result;
/*     */       }
/*     */       
/* 286 */       public double getLowerBound(int index) { return TreeOptimizer.this.substM.getLowerLimit(index); }
/*     */       
/*     */       public double getUpperBound(int index) {
/* 289 */         return TreeOptimizer.this.substM.getUpperLimit(index);
/*     */       }
/*     */       
/* 292 */       public OrthogonalHints getOrthogonalHints() { return null; }
/* 293 */     };
/* 294 */     double[] xvec = new double[this.substM.getNumParameters()];
/* 295 */     for (int index = 0; index < xvec.length; index++) {
/* 296 */       xvec[index] = this.substM.getParameter(index);
/*     */     }
/* 298 */     this.mvm.findMinimum(mvf, xvec, 1, 2);
/* 299 */     return xvec;
/*     */   }
/*     */ }


/* Location:              /home/lachlan/Desktop/psilc1.21/psilc.jar!/lc1/phyhmm/TreeOptimizer.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */