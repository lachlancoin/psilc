/*     */ package lc1.dp;
/*     */ 
/*     */ import java.util.Arrays;
/*     */ import lc1.util.Print;
/*     */ import pal.datatype.DataType;
/*     */ import pal.substmodel.GammaRates;
/*     */ import pal.substmodel.RateDistribution;
/*     */ import pal.substmodel.RateMatrix;
/*     */ 
/*     */ 
/*     */ public class EmissionState
/*     */   extends State
/*     */ {
/*     */   RateDistribution rates;
/*     */   State associatedState;
/*     */   double[] dist;
/*     */   DataType dt;
/*     */   RateMatrix subst;
/*     */   
/*     */   public String getName()
/*     */   {
/*  22 */     return this.name;
/*     */   }
/*     */   
/*     */   public void setDistribution(double[] freq) {
/*  26 */     this.dist = freq;
/*     */   }
/*     */   
/*     */   void validateSum() throws Exception {
/*  30 */     if (this.dt != null) {
/*  31 */       MarkovModel.validateMapSum(this.dist);
/*     */     }
/*     */   }
/*     */   
/*     */   public double[] getDistribution()
/*     */   {
/*  37 */     return this.dist;
/*     */   }
/*     */   
/*     */   private EmissionState(String name, int adv)
/*     */   {
/*  42 */     super(name, adv);
/*     */   }
/*     */   
/*     */ 
/*     */   public EmissionState(String name, DataType dt, int adv, int no_rates)
/*     */   {
/*  48 */     super(name, adv);
/*  49 */     this.dt = dt;
/*  50 */     if (dt != null) {
/*  51 */       this.dist = new double[dt.getNumStates()];
/*  52 */       Arrays.fill(this.dist, 0.0D);
/*     */     }
/*  54 */     this.rates = new GammaRates(no_rates, 1.0D);
/*     */   }
/*     */   
/*     */   public String extendedString() {
/*  58 */     StringBuffer sb = new StringBuffer(this.name + "\n");
/*  59 */     sb.append(Print.toString(this.dt));
/*  60 */     sb.append(Print.toString(this.dist));
/*  61 */     return sb.toString();
/*     */   }
/*     */   
/*     */   protected EmissionState(String name, DataType dt, double[] freq, int adv, int no_rates) {
/*  65 */     this(name, dt, adv, no_rates);
/*  66 */     this.dist = freq;
/*     */   }
/*     */   
/*     */   public void setAssociatedState(State state)
/*     */   {
/*  71 */     this.associatedState = state;
/*     */   }
/*     */   
/*     */   public void setSubstModel(RateMatrix substM) {
/*  75 */     this.subst = substM;
/*     */   }
/*     */   
/*     */   public RateMatrix getSubstitutionModel() {
/*  79 */     return this.subst;
/*     */   }
/*     */   
/*  82 */   public RateDistribution getRateDistribution() { return this.rates; }
/*     */   
/*     */   public State getAssociatedState()
/*     */   {
/*  86 */     return this.associatedState;
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
/*     */   public static class MatchDeleteState
/*     */     extends EmissionState
/*     */   {
/*     */     MatchDeleteState(String name, DataType dt, double[] freq, int no_rates)
/*     */     {
/* 109 */       super(dt, freq, 1, no_rates);
/*     */     }
/*     */     
/*     */     public MatchDeleteState(String name, DataType dt, int no_rates)
/*     */     {
/* 114 */       super(dt, 1, no_rates);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public static class InsertState
/*     */     extends EmissionState
/*     */   {
/*     */     InsertState(String name, DataType dt, double[] freq, int no_rates)
/*     */     {
/* 124 */       super(dt, freq, 1, no_rates);
/*     */     }
/*     */     
/*     */     public InsertState(String name, DataType dt, int no_rates) {
/* 128 */       super(dt, 1, no_rates);
/*     */     }
/*     */   }
/*     */ }


/* Location:              /home/lachlan/Desktop/psilc1.21/psilc.jar!/lc1/dp/EmissionState.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */