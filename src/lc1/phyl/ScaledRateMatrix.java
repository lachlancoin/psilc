/*     */ package lc1.phyl;
/*     */ 
/*     */ import pal.substmodel.RateMatrix;
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ScaledRateMatrix
/*     */   extends ParameterizedRateMatrix
/*     */ {
/*  10 */   double FSE = 0.1D;
/*  11 */   final double defaultF = 1.0D;
/*  12 */   double scale = 1.0D;
/*     */   
/*     */   public ScaledRateMatrix(RateMatrix model) {
/*  15 */     super(model);
/*     */   }
/*     */   
/*     */ 
/*     */   public void setDistance(double dist)
/*     */   {
/*  21 */     super.setDistance(this.scale * dist);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public int getNumParameters()
/*     */   {
/*  29 */     return this.no_params + 1;
/*     */   }
/*     */   
/*     */ 
/*     */   public void setParameter(double param, int n)
/*     */   {
/*  35 */     if (n < this.no_params) {
/*  36 */       this.base.setParameter(param, n);
/*     */     }
/*     */     else {
/*  39 */       this.scale = param;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public double getParameter(int n)
/*     */   {
/*  48 */     if (n < this.no_params) {
/*  49 */       return this.base.getParameter(n);
/*     */     }
/*     */     
/*  52 */     return this.scale;
/*     */   }
/*     */   
/*     */ 
/*     */   public void setParameterSE(double paramSE, int n)
/*     */   {
/*  58 */     if (n < this.no_params) {
/*  59 */       this.base.setParameterSE(paramSE, n);
/*     */     }
/*     */     else {
/*  62 */       this.FSE = paramSE;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public double getLowerLimit(int n)
/*     */   {
/*  69 */     if (n < this.no_params) {
/*  70 */       return this.base.getLowerLimit(n);
/*     */     }
/*     */     
/*  73 */     return 0.01D;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public double getUpperLimit(int n)
/*     */   {
/*  81 */     if (n < this.no_params) {
/*  82 */       return this.base.getUpperLimit(n);
/*     */     }
/*     */     
/*  85 */     return 100.0D;
/*     */   }
/*     */   
/*     */ 
/*     */   public double getDefaultValue(int n)
/*     */   {
/*  91 */     if (n < this.no_params) {
/*  92 */       return this.base.getDefaultValue(n);
/*     */     }
/*     */     
/*  95 */     return 1.0D;
/*     */   }
/*     */   
/*     */   public String getParameterName(int i)
/*     */   {
/* 100 */     if (i < this.no_params) {
/* 101 */       return this.base.getParameterName(i);
/*     */     }
/*     */     
/* 104 */     return "F";
/*     */   }
/*     */ }


/* Location:              /home/lachlan/Desktop/psilc1.21/psilc.jar!/lc1/phyl/ScaledRateMatrix.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */