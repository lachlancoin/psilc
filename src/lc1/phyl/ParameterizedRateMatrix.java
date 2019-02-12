/*     */ package lc1.phyl;
/*     */ 
/*     */ import java.io.PrintWriter;
/*     */ import pal.datatype.DataType;
/*     */ import pal.math.OrthogonalHints;
/*     */ import pal.misc.PalObjectListener;
/*     */ import pal.misc.Parameterized;
/*     */ import pal.substmodel.RateMatrix;
/*     */ 
/*     */ public abstract class ParameterizedRateMatrix implements Parameterized, RateMatrix
/*     */ {
/*     */   protected final RateMatrix base;
/*     */   protected final int no_params;
/*     */   
/*     */   public ParameterizedRateMatrix(RateMatrix model)
/*     */   {
/*  17 */     this.base = model;
/*  18 */     this.no_params = this.base.getNumParameters();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public RateMatrix getBaseRateMatrix()
/*     */   {
/*  28 */     return this.base;
/*     */   }
/*     */   
/*     */   public Object clone() {
/*     */     try {
/*  33 */       return (RateMatrix)super.clone();
/*     */ 
/*     */ 
/*     */     }
/*     */     catch (CloneNotSupportedException e)
/*     */     {
/*     */ 
/*  40 */       throw new InternalError();
/*     */     }
/*     */   }
/*     */   
/*     */   public String getUniqueName() {
/*  45 */     return this.base + "_gwF";
/*     */   }
/*     */   
/*     */   public int getTypeID()
/*     */   {
/*  50 */     return this.base.getTypeID();
/*     */   }
/*     */   
/*  53 */   public int getModelID() { return 7 * this.base.getModelID(); }
/*     */   
/*     */   public int getDimension() {
/*  56 */     return this.base.getDimension();
/*     */   }
/*     */   
/*  59 */   public double[] getEquilibriumFrequencies() { return this.base.getEquilibriumFrequencies(); }
/*     */   
/*     */   public double getEquilibriumFrequency(int i) {
/*  62 */     return this.base.getEquilibriumFrequency(i);
/*     */   }
/*     */   
/*  65 */   public DataType getDataType() { return this.base.getDataType(); }
/*     */   
/*     */   public double[][] getRelativeRates() {
/*  68 */     return this.base.getRelativeRates();
/*     */   }
/*     */   
/*  71 */   public double getTransitionProbability(int fromState, int toState) { return this.base.getTransitionProbability(fromState, toState); }
/*     */   
/*     */   public void setDistance(double distance) {
/*  74 */     this.base.setDistance(distance);
/*     */   }
/*     */   
/*  77 */   public void setDistanceTranspose(double distance) { this.base.setDistance(distance); }
/*     */   
/*     */   public double setParametersNoScale(double[] parameters) {
/*  80 */     return this.base.setParametersNoScale(parameters);
/*     */   }
/*     */   
/*  83 */   public void scale(double scaleValue) { this.base.scale(scaleValue); }
/*     */   
/*     */   public void getTransitionProbabilities(double[][] probabilityStore)
/*     */   {
/*  87 */     this.base.getTransitionProbabilities(probabilityStore);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void report(PrintWriter out)
/*     */   {
/*  98 */     this.base.report(out);
/*     */   }
/*     */   
/* 101 */   public OrthogonalHints getOrthogonalHints() { return this.base.getOrthogonalHints(); }
/*     */   
/*     */ 
/*     */   public void addPalObjectListener(PalObjectListener pol)
/*     */   {
/* 106 */     this.base.addPalObjectListener(pol);
/*     */   }
/*     */   
/* 109 */   public void removePalObjectListener(PalObjectListener pol) { this.base.removePalObjectListener(pol); }
/*     */ }


/* Location:              /home/lachlan/Desktop/psilc1.21/psilc.jar!/lc1/phyl/ParameterizedRateMatrix.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */