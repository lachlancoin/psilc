/*     */ package lc1.treefam;
/*     */ 
/*     */ import java.util.Arrays;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import java.util.TreeMap;
/*     */ import org.omegahat.Probability.Distributions.Normal;
/*     */ import org.omegahat.Probability.Distributions.UnnormalizedDensity;
/*     */ import org.omegahat.Simulation.MCMC.CustomMetropolisHastingsSampler;
/*     */ import org.omegahat.Simulation.MCMC.CustomMetropolisSampler;
/*     */ import org.omegahat.Simulation.MCMC.GenericChainStepEvent;
/*     */ import org.omegahat.Simulation.MCMC.Listeners.ListenerWriter;
/*     */ import org.omegahat.Simulation.MCMC.Listeners.StrippedListenerWriter;
/*     */ import org.omegahat.Simulation.MCMC.MCMCEvent;
/*     */ import org.omegahat.Simulation.MCMC.MCMCListenerHandle;
/*     */ import org.omegahat.Simulation.MCMC.MCMCState;
/*     */ import org.omegahat.Simulation.MCMC.SymmetricProposal;
/*     */ import org.omegahat.Simulation.RandomGenerators.CollingsPRNG;
/*     */ import org.omegahat.Simulation.RandomGenerators.CollingsPRNGAdministrator;
/*     */ import org.omegahat.Simulation.RandomGenerators.PRNG;
/*     */ import pal.math.MultivariateFunction;
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
/*     */ public class MultipleMCMCSampler
/*     */ {
/*     */   MultivariateFunction[] likelihood;
/*     */   Object[] keys;
/*     */   double probOfEachTree;
/*     */   double logProbOfEachTree;
/*     */   static final double probOfSwitching = 0.5D;
/*  41 */   static final double logProbOfInnerNode = Math.log(0.5D);
/*     */   
/*  43 */   static final double logProbOfNotInnerNode = Math.log(0.5D);
/*     */   
/*     */ 
/*  46 */   double maxValue = 0.0D;
/*     */   
/*  48 */   final int burn_in = 100;
/*     */   
/*  50 */   final int iterations = 500;
/*     */   
/*  52 */   final int thinningInterval = 1;
/*     */   
/*  54 */   static final Double zeroD = new Double(0.0D);
/*     */   
/*  56 */   static final Double minL = new Double(0.01D);
/*     */   
/*  58 */   static final Double negMinL = new Double(-0.01D);
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   int numParameters;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   SymmetricProposal proposal;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   BranchLengthDensity target;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public MultipleMCMCSampler(Map likelihoodMap)
/*     */   {
/*  80 */     this.likelihood = new MultivariateFunction[
/*  81 */       likelihoodMap.keySet().size()];
/*  82 */     this.keys = new Object[likelihoodMap.keySet().size()];
/*  83 */     likelihoodMap.keySet().toArray(this.keys);
/*  84 */     for (int il = 0; il < this.keys.length; il++) {
/*  85 */       this.likelihood[il] = ((MultivariateFunction)
/*  86 */         likelihoodMap.get(this.keys[il]));
/*     */     }
/*     */     
/*     */ 
/*  90 */     this.probOfEachTree = (1.0D / (this.keys.length - 1.0D));
/*     */     
/*  92 */     this.logProbOfEachTree = Math.log(this.probOfEachTree);
/*  93 */     this.numParameters = ((MultivariateFunction)likelihoodMap.get(this.keys[0]))
/*  94 */       .getNumArguments();
/*     */     try {
/*  96 */       for (int i = 0; i < this.keys.length; i++) {
/*  97 */         if ((i > 0) && (this.likelihood[i] == this.likelihood[0]))
/*  98 */           throw new Exception("these should not be same !!  ");
/*  99 */         if (((MultivariateFunction)likelihoodMap.get(this.keys[i]))
/* 100 */           .getNumArguments() != this.numParameters)
/* 101 */           throw new Exception(
/* 102 */             "likelihoods must all have same number of parameters");
/*     */       }
/*     */     } catch (Exception exc) {
/* 105 */       exc.printStackTrace();
/* 106 */       System.exit(0);
/*     */     }
/*     */     try {
/* 109 */       this.target = new BranchLengthDensity();
/* 110 */       this.proposal = new TreeProposal();
/*     */     }
/*     */     catch (Throwable t) {
/* 113 */       t.printStackTrace();
/*     */     }
/*     */   }
/*     */   
/*     */   private boolean finished(int[] counts, int parentCount) {
/* 118 */     int best = parentCount;
/* 119 */     int next_best = 0;
/* 120 */     for (int i = 0; i < counts.length; i++) {
/* 121 */       if (counts[i] > best) {
/* 122 */         next_best = best;
/* 123 */         best = counts[i];
/* 124 */       } else if (counts[i] > next_best) {
/* 125 */         next_best = counts[i];
/*     */       }
/*     */     }
/* 128 */     if (best - next_best > 100) {
/* 129 */       return true;
/*     */     }
/* 131 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */   public Map run()
/*     */     throws Exception
/*     */   {
/* 138 */     int[] count = new int[this.keys.length];
/* 139 */     int parentCount = 0;
/* 140 */     Arrays.fill(count, 0);
/* 141 */     CustomMetropolisSampler[] mcmc = new CustomMetropolisSampler[this.keys.length];
/* 142 */     TreeListenerWriter[] l = new TreeListenerWriter[this.keys.length];
/* 143 */     double[] start = new double[this.numParameters];
/* 144 */     Arrays.fill(start, 0.1D);
/* 145 */     for (int ij = 0; ij < this.keys.length; ij++) {
/* 146 */       Object initialState = { new Integer(ij), start };
/* 147 */       mcmc[ij] = new CustomMetropolisSampler(initialState, 
/*     */       
/* 149 */         this.target, 
/* 150 */         this.proposal, 
/* 151 */         this.prng);
/*     */       
/* 153 */       mcmc[ij].iterate(100);
/*     */       
/* 155 */       l[ij] = new TreeListenerWriter(this.keys.length);
/* 156 */       MCMCListenerHandle localMCMCListenerHandle = mcmc[ij].registerListener(l[ij]);
/*     */     }
/* 158 */     int k = 0;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 163 */     while (!finished(count, parentCount))
/*     */     {
/*     */ 
/* 166 */       Arrays.fill(count, 0);
/* 167 */       parentCount = 0;
/*     */       
/* 169 */       for (int i = 0; i < mcmc.length; i++) {
/* 170 */         mcmc[i].iterate(500);
/* 171 */         parentCount += l[i].parentCount;
/*     */         
/*     */ 
/*     */ 
/* 175 */         for (int ij = 0; ij < this.keys.length; ij++) {
/* 176 */           count[ij] += l[i].counts[ij];
/*     */         }
/*     */       }
/* 179 */       k++;
/*     */     }
/* 181 */     boolean parentBest = true;
/* 182 */     Object m = new TreeMap(TreeSearch.INT_COMP);
/* 183 */     for (int i = 0; i < this.keys.length; i++) {
/* 184 */       parentBest = (parentBest) && (parentCount > count[i]);
/* 185 */       ((Map)m).put(this.keys[i], new Integer(count[i]));
/*     */     }
/* 187 */     if (parentBest) {
/* 188 */       return null;
/*     */     }
/* 190 */     return (Map)m;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 200 */   private CollingsPRNGAdministrator a = new CollingsPRNGAdministrator();
/*     */   
/* 202 */   PRNG prng = new CollingsPRNG(this.a.registerPRNGState());
/*     */   
/*     */ 
/*     */   CustomMetropolisHastingsSampler mcmc;
/*     */   
/*     */ 
/*     */   MCMCListenerHandle lh;
/*     */   
/*     */   ListenerWriter l;
/*     */   
/*     */ 
/*     */   class BranchLengthDensity
/*     */     implements UnnormalizedDensity
/*     */   {
/*     */     BranchLengthDensity() {}
/*     */     
/*     */ 
/*     */     public double logUnnormalizedPDF(Object obj)
/*     */     {
/* 221 */       Object[] obj1 = (Object[])obj;
/* 222 */       double[] d = (double[])obj1[1];
/* 223 */       int tree_id = ((Integer)obj1[0]).intValue();
/*     */       
/*     */ 
/*     */ 
/* 227 */       return -1.0D * MultipleMCMCSampler.this.likelihood[tree_id].evaluate(d);
/*     */     }
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
/* 245 */     public double unnormalizedPDF(Object obj) { return Math.exp(logUnnormalizedPDF(obj)); }
/*     */   }
/*     */   
/*     */   class TreeProposal implements SymmetricProposal { TreeProposal() {}
/*     */     
/* 250 */     double sigma = 1.0D;
/*     */     
/* 252 */     Normal normal = new Normal(MultipleMCMCSampler.this.prng, new Double[] { new Double(0.0D), 
/* 253 */       new Double(this.sigma) });
/*     */     
/*     */     public double logConditionalPDF(Object to, Object from) {
/* 256 */       return Math.log(conditionalPDF(to, from));
/*     */     }
/*     */     
/*     */     public double conditionalPDF(Object to, Object from) {
/* 260 */       Integer treeIDTo = (Integer)((Object[])to)[0];
/* 261 */       Integer treeIDFrom = (Integer)((Object[])from)[0];
/* 262 */       if (treeIDTo.equals(treeIDFrom)) {
/* 263 */         double[] branchLengthsTo = (double[])((Object[])to)[1];
/* 264 */         double[] branchLengthsFrom = (double[])((Object[])from)[1];
/* 265 */         double logProb = 1.0D / branchLengthsFrom.length;
/*     */         
/*     */ 
/* 268 */         int id_changed = -1;
/* 269 */         for (int i = 0; i < branchLengthsFrom.length; i++) {
/* 270 */           if (branchLengthsFrom[i] != branchLengthsTo[i]) {
/* 271 */             id_changed = i;
/* 272 */             break;
/*     */           }
/*     */         }
/* 275 */         if (branchLengthsTo[id_changed] < 0.0D)
/* 276 */           return 0.0D;
/* 277 */         this.normal.setMean(branchLengthsFrom[id_changed]);
/* 278 */         this.normal.setStandardDeviation(this.sigma);
/* 279 */         if (branchLengthsTo[id_changed] == 0.01D)
/*     */         {
/* 281 */           logProb = logProb * (this.normal.CDF(MultipleMCMCSampler.minL) - this.normal.CDF(MultipleMCMCSampler.zeroD) + (this.normal.CDF(MultipleMCMCSampler.zeroD) - this.normal.CDF(MultipleMCMCSampler.negMinL)));
/*     */         }
/*     */         else
/*     */         {
/* 285 */           logProb = logProb * (this.normal.PDF(new Double(branchLengthsTo[id_changed])) + this.normal.PDF(new Double(-1.0D * branchLengthsTo[id_changed])));
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 293 */         return logProb * 0.5D;
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 299 */       return 0.5D * MultipleMCMCSampler.this.probOfEachTree;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     public double transitionProbability(Object from, Object to)
/*     */     {
/* 307 */       return conditionalPDF(to, from);
/*     */     }
/*     */     
/*     */     public double logTransitionProbability(Object from, Object to)
/*     */     {
/* 312 */       return logConditionalPDF(to, from);
/*     */     }
/*     */     
/*     */     public Object generate(Object cond) {
/* 316 */       Integer treeID = (Integer)((Object[])cond)[0];
/*     */       
/* 318 */       double ran = MultipleMCMCSampler.this.prng.nextDouble();
/*     */       
/* 320 */       if (ran < 0.5D) {
/* 321 */         int new_id = (int)Math.floor(MultipleMCMCSampler.this.prng.nextDouble() * (
/* 322 */           MultipleMCMCSampler.this.keys.length - 1));
/* 323 */         if (new_id >= treeID.intValue())
/* 324 */           new_id++;
/* 325 */         Integer newTreeID = new Integer(new_id);
/* 326 */         return new Object[] { newTreeID, ((Object[])cond)[1] };
/*     */       }
/* 328 */       double[] branchLengths = (double[])((double[])((Object[])cond)[1])
/* 329 */         .clone();
/* 330 */       int id = (int)Math.floor(MultipleMCMCSampler.this.prng.nextDouble() * 
/* 331 */         branchLengths.length);
/* 332 */       branchLengths[id] = ((Double)this.normal.generate(
/* 333 */         branchLengths[id], this.sigma)).doubleValue();
/*     */       
/* 335 */       if (branchLengths[id] < 0.0D)
/*     */       {
/* 337 */         branchLengths[id] = Math.abs(branchLengths[id]);
/*     */       }
/* 339 */       if (branchLengths[id] < 0.01D) {
/* 340 */         branchLengths[id] = 0.01D;
/*     */       }
/* 342 */       return new Object[] { treeID, branchLengths };
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   class TreeListenerWriter
/*     */     extends StrippedListenerWriter
/*     */   {
/*     */     int[] counts;
/*     */     
/* 352 */     int parentCount = 0;
/*     */     
/* 354 */     int index = 0;
/*     */     
/*     */     TreeListenerWriter(int length) throws Exception {
/* 357 */       this.counts = new int[length];
/* 358 */       Arrays.fill(this.counts, 0);
/*     */     }
/*     */     
/*     */     public void notify(MCMCEvent e)
/*     */     {
/* 363 */       if (Math.IEEEremainder(this.index, 1.0D) == 0.0D) {
/* 364 */         MCMCState current = ((GenericChainStepEvent)e).getCurrent();
/* 365 */         Integer treeId = (Integer)((Object[])((org.omegahat.Simulation.MCMC.ContainerState)current)
/* 366 */           .getContents())[0];
/*     */         
/* 368 */         if (treeId.intValue() < 0) {
/* 369 */           this.parentCount += 1;
/*     */         } else
/* 371 */           this.counts[treeId.intValue()] += 1;
/*     */       }
/* 373 */       this.index += 1;
/*     */     }
/*     */   }
/*     */ }


/* Location:              /home/lachlan/Desktop/psilc1.21/psilc.jar!/lc1/treefam/MultipleMCMCSampler.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */