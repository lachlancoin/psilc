/*     */ package lc1.phyl;
/*     */ 
/*     */ import java.util.Collection;
/*     */ import java.util.Iterator;
/*     */ import java.util.Set;
/*     */ import java.util.SortedMap;
/*     */ import java.util.TreeMap;
/*     */ import org.omegahat.Simulation.RandomGenerators.CollingsPRNG;
/*     */ import org.omegahat.Simulation.RandomGenerators.CollingsPRNGAdministrator;
/*     */ import org.omegahat.Simulation.RandomGenerators.PRNG;
/*     */ import pal.math.ConjugateDirectionSearch;
/*     */ import pal.math.MultivariateFunction;
/*     */ 
/*     */ public class NestedInference
/*     */ {
/*  16 */   int numAtoms = 10;
/*  17 */   private static CollingsPRNGAdministrator a = new CollingsPRNGAdministrator();
/*  18 */   PRNG prng = new CollingsPRNG(a.registerPRNGState());
/*     */   
/*     */ 
/*  21 */   int mcmc = 100;
/*  22 */   boolean approx = false;
/*     */   
/*     */   static final double x_thresh = 0.1D;
/*     */   static final double fx_thresh = 0.1D;
/*  26 */   double evidence = 0.0D;
/*  27 */   double log_scaling = Double.NEGATIVE_INFINITY;
/*  28 */   double wlogw = 0.0D;
/*     */   
/*  30 */   double last = 0.0D;
/*     */   
/*  32 */   double maxValue = Double.POSITIVE_INFINITY;
/*     */   
/*  34 */   final ModifiedMultivariateMinimum mvm = new ModifiedMultivariateMinimum();
/*     */   static final boolean print = false;
/*     */   
/*     */   class ModifiedMultivariateMinimum extends ConjugateDirectionSearch {
/*     */     ModifiedMultivariateMinimum() {
/*  39 */       this.numFuncStops = 0;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     public boolean stopCondition(double fx, double[] x, double tolfx, double tolx, boolean firstCall)
/*     */     {
/*  47 */       return fx < NestedInference.this.maxValue;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*  54 */   SortedMap atoms = new TreeMap();
/*     */   MultivariateFunction likelihood;
/*     */   MCMCSampler sampler;
/*     */   
/*     */   NestedInference(MultivariateFunction likelihood, int numAtoms) {
/*  59 */     this.numAtoms = numAtoms;
/*  60 */     this.likelihood = likelihood;
/*  61 */     fillMap();
/*     */     
/*  63 */     if ((this.mcmc > 0) && (!this.approx)) this.sampler = new MCMCSampler(likelihood, this.mcmc);
/*     */   }
/*     */   
/*     */   private double[] sampleFromPrior() {
/*  67 */     double[] d = new double[this.likelihood.getNumArguments()];
/*  68 */     for (int j = 0; j < d.length; j++) {
/*  69 */       double lowerB = this.likelihood.getLowerBound(j);
/*  70 */       d[j] = (this.prng.nextDouble() * (this.likelihood.getUpperBound(j) - lowerB) + 
/*  71 */         lowerB);
/*     */     }
/*  73 */     return d;
/*     */   }
/*     */   
/*     */   private double[] sampleFromPrior(double minLikelihood) throws Exception {
/*  77 */     this.maxValue = (-1.0D * minLikelihood);
/*     */     
/*  79 */     if (this.approx) {
/*  80 */       double[] d = sampleFromPrior();
/*  81 */       if (this.likelihood.evaluate(d) < this.maxValue) return d;
/*  82 */       this.mvm.optimize(this.likelihood, d, 1.0E-5D, 1.0E-6D);
/*  83 */       double min = this.likelihood.evaluate(d);
/*     */       
/*  85 */       if (min > this.maxValue) throw new Exception("likelihood not greater than minLikelihood " + min + " " + this.maxValue);
/*  86 */       return d;
/*     */     }
/*  88 */     if (this.mcmc > 0) {
/*  89 */       int i = (int)Math.floor(Math.random() * this.atoms.size());
/*  90 */       Iterator it = this.atoms.values().iterator();
/*  91 */       for (int k = 0; k < i; k++) {
/*  92 */         it.next();
/*     */       }
/*  94 */       double[] d = this.sampler.run(this.maxValue, (double[])it.next());
/*  95 */       double min = this.likelihood.evaluate(d);
/*     */       
/*  97 */       if (min > this.maxValue) throw new Exception("likelihood not greater than minLikelihood " + min + " " + this.maxValue);
/*  98 */       return d;
/*     */     }
/*     */     for (;;)
/*     */     {
/* 102 */       double[] d = sampleFromPrior();
/* 103 */       if (-1.0D * this.likelihood.evaluate(d) > minLikelihood) { return d;
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   private void fillMap()
/*     */   {
/* 110 */     for (int i = 0; i < this.numAtoms; i++) {
/* 111 */       double[] d = sampleFromPrior();
/* 112 */       this.atoms.put(new Double(-1.0D * this.likelihood.evaluate(d)), d);
/*     */     }
/*     */   }
/*     */   
/*     */   private double log_hk_avg(int k)
/*     */   {
/* 118 */     return -1.0D * Math.log(this.numAtoms) + k * Math.log(this.numAtoms / (this.numAtoms + 1));
/*     */   }
/*     */   
/*     */   private double log_xk_avg(int k)
/*     */   {
/* 123 */     return k * Math.log(this.numAtoms / (this.numAtoms + 1));
/*     */   }
/*     */   
/*     */   private boolean terminate(int i) {
/* 127 */     double fx_diff = ((Double)this.atoms.lastKey()).doubleValue() - ((Double)this.atoms.firstKey()).doubleValue();
/*     */     
/* 129 */     if (fx_diff < 0.1D) {
/* 130 */       Iterator it = this.atoms.values().iterator();
/* 131 */       double[] first = (double[])it.next();
/* 132 */       double[] comp; int ik; for (; it.hasNext(); 
/*     */           
/* 134 */           ik < comp.length)
/*     */       {
/* 133 */         comp = (double[])it.next();
/* 134 */         ik = 0; continue;
/* 135 */         if (Math.abs(comp[ik] - first[ik]) > 0.1D) return false;
/* 134 */         ik++;
/*     */       }
/*     */       
/*     */ 
/* 138 */       return true;
/*     */     }
/* 140 */     return false;
/*     */   }
/*     */   
/*     */   double logFinalContribution(int k)
/*     */   {
/* 145 */     double total_likelihood = 0.0D;
/* 146 */     for (Iterator it = this.atoms.keySet().iterator(); it.hasNext();) {
/* 147 */       Double key = (Double)it.next();
/* 148 */       total_likelihood += Math.exp(key.doubleValue());
/*     */     }
/* 150 */     return log_xk_avg(k) + Math.log(total_likelihood / this.numAtoms);
/*     */   }
/*     */   
/*     */   public double getEvidence()
/*     */     throws Exception
/*     */   {
/* 156 */     for (int i = 1; !terminate(i); i++) {
/* 157 */       Double l_i = (Double)this.atoms.firstKey();
/* 158 */       this.atoms.remove(l_i);
/* 159 */       double w_i = l_i.doubleValue() + log_hk_avg(i);
/* 160 */       double log_scaling_new = Math.max(w_i, this.log_scaling);
/*     */       
/*     */ 
/* 163 */       if (this.evidence > 0.0D)
/*     */       {
/* 165 */         this.evidence = (Math.exp(Math.log(this.evidence) - log_scaling_new + this.log_scaling) + Math.exp(w_i - log_scaling_new));
/*     */       }
/*     */       else {
/* 168 */         this.evidence = Math.exp(w_i - log_scaling_new);
/*     */       }
/* 170 */       this.log_scaling = log_scaling_new;
/*     */       
/* 172 */       double[] nextD = sampleFromPrior(l_i.doubleValue());
/*     */       
/* 174 */       this.atoms.put(new Double(-1.0D * this.likelihood.evaluate(nextD)), nextD);
/*     */     }
/*     */     
/* 177 */     double term_score = logFinalContribution(i) - this.log_scaling;
/* 178 */     double total = Math.exp(this.evidence) + Math.exp(term_score);
/* 179 */     double res = Math.log(total) + this.log_scaling;
/*     */     
/* 181 */     return res;
/*     */   }
/*     */ }


/* Location:              /home/lachlan/Desktop/psilc1.21/psilc.jar!/lc1/phyl/NestedInference.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */