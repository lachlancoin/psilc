/*     */ package lc1.phyl;
/*     */ 
/*     */ import org.omegahat.Probability.Distributions.Normal;
/*     */ import org.omegahat.Probability.Distributions.UnnormalizedDensity;
/*     */ import org.omegahat.Simulation.MCMC.CustomMetropolisHastingsSampler;
/*     */ import org.omegahat.Simulation.MCMC.GeneralProposal;
/*     */ import org.omegahat.Simulation.MCMC.Listeners.ListenerWriter;
/*     */ import org.omegahat.Simulation.MCMC.MCMCListenerHandle;
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
/*     */ public class MCMCSampler
/*     */ {
/*     */   MultivariateFunction likelihood;
/*     */   double[] min;
/*     */   double[] max;
/*  25 */   double maxValue = 0.0D;
/*     */   
/*     */ 
/*     */   final int iterations;
/*     */   
/*     */ 
/*     */   TreeProposal proposal;
/*     */   
/*     */ 
/*     */   UnnormalizedDensity target;
/*     */   
/*     */ 
/*     */   public MCMCSampler(MultivariateFunction likelihood, int iterations)
/*     */   {
/*  39 */     this.likelihood = likelihood;
/*  40 */     this.iterations = iterations;
/*  41 */     this.min = new double[likelihood.getNumArguments()];
/*  42 */     this.max = new double[likelihood.getNumArguments()];
/*  43 */     for (int i = 0; i < this.min.length; i++) {
/*  44 */       this.min[i] = likelihood.getLowerBound(i);
/*  45 */       this.max[i] = likelihood.getUpperBound(i);
/*     */     }
/*     */     try
/*     */     {
/*  49 */       this.target = new BranchLengthDensity();
/*  50 */       this.proposal = new TreeProposal();
/*     */ 
/*     */     }
/*     */     catch (Throwable t)
/*     */     {
/*     */ 
/*  56 */       t.printStackTrace();
/*     */     }
/*     */   }
/*     */   
/*     */   public double[] run(double min, double[] state) {
/*  61 */     this.maxValue = min;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*  66 */     this.mcmc = new CustomMetropolisHastingsSampler(state, this.target, this.proposal, this.prng, true);
/*  67 */     this.mcmc.iterate(this.iterations);
/*  68 */     return (double[])this.mcmc.current();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  75 */   private CollingsPRNGAdministrator a = new CollingsPRNGAdministrator();
/*  76 */   PRNG prng = new CollingsPRNG(this.a.registerPRNGState());
/*     */   
/*     */ 
/*     */   CustomMetropolisHastingsSampler mcmc;
/*     */   
/*     */ 
/*     */   MCMCListenerHandle lh;
/*     */   
/*     */ 
/*     */   ListenerWriter l;
/*     */   
/*     */ 
/*     */ 
/*     */   class BranchLengthDensity
/*     */     implements UnnormalizedDensity
/*     */   {
/*     */     BranchLengthDensity() {}
/*     */     
/*     */ 
/*     */     public double unnormalizedPDF(Object obj)
/*     */     {
/*  97 */       double[] d = (double[])obj;
/*  98 */       for (int i = 0; i < d.length; i++)
/*     */       {
/* 100 */         if ((d[i] < MCMCSampler.this.min[i]) || (d[i] > MCMCSampler.this.max[i])) return 0.0D;
/*     */       }
/* 102 */       if (MCMCSampler.this.likelihood.evaluate(d) < MCMCSampler.this.maxValue) return 1.0D;
/* 103 */       return 0.0D;
/*     */     }
/*     */     
/*     */ 
/*     */     public double logUnnormalizedPDF(Object obj)
/*     */     {
/* 109 */       return Math.log(unnormalizedPDF(obj));
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   class TreeProposal
/*     */     implements GeneralProposal
/*     */   {
/* 117 */     double sigma = 1.0D;
/* 118 */     Normal normal = new Normal(MCMCSampler.this.prng, new Double[] { new Double(0.0D), new Double(this.sigma) });
/*     */     
/*     */     TreeProposal() {}
/*     */     
/*     */     public double logConditionalPDF(Object to, Object from) {
/* 123 */       return Math.log(conditionalPDF(to, from));
/*     */     }
/*     */     
/*     */     public double conditionalPDF(Object to, Object from)
/*     */     {
/* 128 */       double[] branchLengthsTo = (double[])to;
/* 129 */       double[] branchLengthsFrom = (double[])from;
/* 130 */       int id_changed = -1;
/* 131 */       for (int i = 0; i < branchLengthsFrom.length; i++) {
/* 132 */         if (branchLengthsFrom[i] != branchLengthsTo[i]) {
/* 133 */           id_changed = i;
/* 134 */           break;
/*     */         }
/*     */       }
/* 137 */       if (branchLengthsTo[id_changed] < 0.0D) return 0.0D;
/* 138 */       this.normal.setMean(branchLengthsFrom[id_changed]);this.normal.setStandardDeviation(this.sigma);
/* 139 */       return this.normal.PDF(new Double(branchLengthsTo[0])) + 
/* 140 */         this.normal.PDF(new Double(-1.0D * branchLengthsTo[0]));
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     public double transitionProbability(Object from, Object to)
/*     */     {
/* 147 */       return conditionalPDF(to, from);
/*     */     }
/*     */     
/*     */ 
/*     */     public double logTransitionProbability(Object from, Object to)
/*     */     {
/* 153 */       return logConditionalPDF(to, from);
/*     */     }
/*     */     
/*     */     public Object generate(Object cond)
/*     */     {
/* 158 */       double[] branchLengths = (double[])((double[])cond).clone();
/* 159 */       int id = (int)Math.floor(MCMCSampler.this.prng.nextDouble() * branchLengths.length);
/* 160 */       branchLengths[id] = ((Double)this.normal.generate(branchLengths[id], this.sigma)).doubleValue();
/* 161 */       if (branchLengths[id] < 0.0D) {
/* 162 */         branchLengths[id] = Math.abs(branchLengths[id]);
/*     */       }
/* 164 */       return branchLengths;
/*     */     }
/*     */   }
/*     */ }


/* Location:              /home/lachlan/Desktop/psilc1.21/psilc.jar!/lc1/phyl/MCMCSampler.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */