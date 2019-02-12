/*     */ package lc1.treefam;
/*     */ 
/*     */ import java.util.Collection;
/*     */ import java.util.Iterator;
/*     */ import java.util.Set;
/*     */ import java.util.SortedMap;
/*     */ import java.util.TreeMap;
/*     */ import lc1.phyl.MCMCSampler;
/*     */ import org.omegahat.Simulation.RandomGenerators.CollingsPRNG;
/*     */ import org.omegahat.Simulation.RandomGenerators.CollingsPRNGAdministrator;
/*     */ import org.omegahat.Simulation.RandomGenerators.PRNG;
/*     */ import pal.math.ConjugateDirectionSearch;
/*     */ import pal.math.MultivariateFunction;
/*     */ 
/*     */ 
/*     */ public class NestedInference
/*     */ {
/*  18 */   int numAtoms = 10;
/*  19 */   private static CollingsPRNGAdministrator a = new CollingsPRNGAdministrator();
/*  20 */   PRNG prng = new CollingsPRNG(a.registerPRNGState());
/*     */   
/*     */ 
/*  23 */   int mcmc = 100;
/*  24 */   boolean approx = false;
/*     */   
/*     */   static final double x_thresh = 0.1D;
/*     */   static final double fx_thresh = 0.1D;
/*  28 */   double evidence = 0.0D;
/*  29 */   double log_scaling = Double.NEGATIVE_INFINITY;
/*  30 */   double wlogw = 0.0D;
/*     */   
/*  32 */   double last = 0.0D;
/*     */   
/*  34 */   double maxValue = Double.POSITIVE_INFINITY;
/*     */   
/*  36 */   final ModifiedMultivariateMinimum mvm = new ModifiedMultivariateMinimum();
/*     */   static final boolean print = false;
/*     */   
/*     */   class ModifiedMultivariateMinimum extends ConjugateDirectionSearch {
/*     */     ModifiedMultivariateMinimum() {
/*  41 */       this.numFuncStops = 0;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     public boolean stopCondition(double fx, double[] x, double tolfx, double tolx, boolean firstCall)
/*     */     {
/*  49 */       return fx < NestedInference.this.maxValue;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*  56 */   SortedMap atoms = new TreeMap();
/*     */   MultivariateFunction likelihood;
/*     */   MCMCSampler sampler;
/*     */   
/*     */   NestedInference(MultivariateFunction likelihood, int numAtoms) {
/*  61 */     this.numAtoms = numAtoms;
/*  62 */     this.likelihood = likelihood;
/*  63 */     fillMap();
/*     */     
/*  65 */     if ((this.mcmc > 0) && (!this.approx)) this.sampler = new MCMCSampler(likelihood, this.mcmc);
/*     */   }
/*     */   
/*     */   private double[] sampleFromPrior() {
/*  69 */     double[] d = new double[this.likelihood.getNumArguments()];
/*  70 */     for (int j = 0; j < d.length; j++) {
/*  71 */       double lowerB = this.likelihood.getLowerBound(j);
/*  72 */       d[j] = (this.prng.nextDouble() * (this.likelihood.getUpperBound(j) - lowerB) + 
/*  73 */         lowerB);
/*     */     }
/*  75 */     return d;
/*     */   }
/*     */   
/*     */   private double[] sampleFromPrior(double minLikelihood) throws Exception {
/*  79 */     this.maxValue = (-1.0D * minLikelihood);
/*     */     
/*  81 */     if (this.approx) {
/*  82 */       double[] d = sampleFromPrior();
/*  83 */       if (this.likelihood.evaluate(d) < this.maxValue) return d;
/*  84 */       this.mvm.optimize(this.likelihood, d, 1.0E-5D, 1.0E-6D);
/*  85 */       double min = this.likelihood.evaluate(d);
/*     */       
/*  87 */       if (min > this.maxValue) throw new Exception("likelihood not greater than minLikelihood " + min + " " + this.maxValue);
/*  88 */       return d;
/*     */     }
/*  90 */     if (this.mcmc > 0) {
/*  91 */       int i = (int)Math.floor(Math.random() * this.atoms.size());
/*  92 */       Iterator it = this.atoms.values().iterator();
/*  93 */       for (int k = 0; k < i; k++) {
/*  94 */         it.next();
/*     */       }
/*  96 */       double[] d = this.sampler.run(this.maxValue, (double[])it.next());
/*  97 */       double min = this.likelihood.evaluate(d);
/*     */       
/*  99 */       if (min > this.maxValue) throw new Exception("likelihood not greater than minLikelihood " + min + " " + this.maxValue);
/* 100 */       return d;
/*     */     }
/*     */     for (;;)
/*     */     {
/* 104 */       double[] d = sampleFromPrior();
/* 105 */       if (-1.0D * this.likelihood.evaluate(d) > minLikelihood) { return d;
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   private void fillMap()
/*     */   {
/* 112 */     for (int i = 0; i < this.numAtoms; i++) {
/* 113 */       double[] d = sampleFromPrior();
/* 114 */       this.atoms.put(new Double(-1.0D * this.likelihood.evaluate(d)), d);
/*     */     }
/*     */   }
/*     */   
/*     */   private double log_hk_avg(int k)
/*     */   {
/* 120 */     return -1.0D * Math.log(this.numAtoms) + k * Math.log(this.numAtoms / (this.numAtoms + 1));
/*     */   }
/*     */   
/*     */   private double log_xk_avg(int k)
/*     */   {
/* 125 */     return k * Math.log(this.numAtoms / (this.numAtoms + 1));
/*     */   }
/*     */   
/*     */   private boolean terminate(int i) {
/* 129 */     double fx_diff = ((Double)this.atoms.lastKey()).doubleValue() - ((Double)this.atoms.firstKey()).doubleValue();
/*     */     
/* 131 */     if (fx_diff < 0.1D) {
/* 132 */       Iterator it = this.atoms.values().iterator();
/* 133 */       double[] first = (double[])it.next();
/* 134 */       double[] comp; int ik; for (; it.hasNext(); 
/*     */           
/* 136 */           ik < comp.length)
/*     */       {
/* 135 */         comp = (double[])it.next();
/* 136 */         ik = 0; continue;
/* 137 */         if (Math.abs(comp[ik] - first[ik]) > 0.1D) return false;
/* 136 */         ik++;
/*     */       }
/*     */       
/*     */ 
/* 140 */       return true;
/*     */     }
/* 142 */     return false;
/*     */   }
/*     */   
/*     */   double logFinalContribution(int k)
/*     */   {
/* 147 */     double total_likelihood = 0.0D;
/* 148 */     for (Iterator it = this.atoms.keySet().iterator(); it.hasNext();) {
/* 149 */       Double key = (Double)it.next();
/* 150 */       total_likelihood += Math.exp(key.doubleValue());
/*     */     }
/* 152 */     return log_xk_avg(k) + Math.log(total_likelihood / this.numAtoms);
/*     */   }
/*     */   
/*     */   public double getEvidence()
/*     */     throws Exception
/*     */   {
/* 158 */     for (int i = 1; !terminate(i); i++) {
/* 159 */       Double l_i = (Double)this.atoms.firstKey();
/* 160 */       this.atoms.remove(l_i);
/* 161 */       double w_i = l_i.doubleValue() + log_hk_avg(i);
/* 162 */       double log_scaling_new = Math.max(w_i, this.log_scaling);
/*     */       
/*     */ 
/* 165 */       if (this.evidence > 0.0D)
/*     */       {
/* 167 */         this.evidence = (Math.exp(Math.log(this.evidence) - log_scaling_new + this.log_scaling) + Math.exp(w_i - log_scaling_new));
/*     */       }
/*     */       else {
/* 170 */         this.evidence = Math.exp(w_i - log_scaling_new);
/*     */       }
/* 172 */       this.log_scaling = log_scaling_new;
/*     */       
/* 174 */       double[] nextD = sampleFromPrior(l_i.doubleValue());
/*     */       
/* 176 */       this.atoms.put(new Double(-1.0D * this.likelihood.evaluate(nextD)), nextD);
/*     */     }
/*     */     
/* 179 */     double term_score = logFinalContribution(i) - this.log_scaling;
/* 180 */     double total = Math.exp(this.evidence) + Math.exp(term_score);
/* 181 */     double res = Math.log(total) + this.log_scaling;
/*     */     
/* 183 */     return res;
/*     */   }
/*     */ }


/* Location:              /home/lachlan/Desktop/psilc1.21/psilc.jar!/lc1/treefam/NestedInference.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */