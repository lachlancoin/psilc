/*    */ package lc1.treefam;
/*    */ 
/*    */ import pal.misc.Parameterized;
/*    */ import pal.substmodel.RateDistribution;
/*    */ import pal.substmodel.RateMatrix;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ class SubstModel
/*    */   implements Parameterized
/*    */ {
/*    */   int rmParams;
/*    */   int rateParams;
/*    */   RateMatrix rm;
/*    */   RateDistribution rates;
/*    */   
/*    */   SubstModel(RateMatrix rm, RateDistribution rates)
/*    */   {
/* 20 */     this.rm = rm;
/* 21 */     this.rates = rates;
/* 22 */     this.rmParams = rm.getNumParameters();
/* 23 */     this.rateParams = rates.getNumParameters();
/*    */   }
/*    */   
/* 26 */   public int getNumParameters() { return this.rmParams + this.rateParams; }
/*    */   
/*    */   public double getDefaultValue(int n)
/*    */   {
/* 30 */     if (n < this.rmParams)
/*    */     {
/* 32 */       return this.rm.getDefaultValue(n);
/*    */     }
/*    */     
/* 35 */     return this.rates.getDefaultValue(n - this.rmParams);
/*    */   }
/*    */   
/*    */   public double getUpperLimit(int n) {
/* 39 */     if (n < this.rmParams) {
/* 40 */       return this.rm.getUpperLimit(n);
/*    */     }
/*    */     
/* 43 */     return this.rates.getUpperLimit(n - this.rmParams);
/*    */   }
/*    */   
/*    */   public double getLowerLimit(int n) {
/* 47 */     if (n < this.rmParams) {
/* 48 */       return this.rm.getLowerLimit(n);
/*    */     }
/*    */     
/* 51 */     return this.rates.getLowerLimit(n - this.rmParams);
/*    */   }
/*    */   
/*    */   public double getParameter(int n) {
/* 55 */     if (n < this.rmParams) {
/* 56 */       return this.rm.getParameter(n);
/*    */     }
/*    */     
/* 59 */     return this.rates.getParameter(n - this.rmParams);
/*    */   }
/*    */   
/*    */   public void setParameter(double d, int n) {
/* 63 */     if (n < this.rmParams) {
/* 64 */       this.rm.setParameter(d, n);
/*    */     }
/*    */     else
/*    */     {
/* 68 */       this.rates.setParameter(d, n - this.rmParams); }
/*    */   }
/*    */   
/*    */   public String getParameterizationInfo() {
/* 72 */     return "";
/*    */   }
/*    */   
/*    */   public void setParameterSE(double paramSE, int n) {}
/*    */ }


/* Location:              /home/lachlan/Desktop/psilc1.21/psilc.jar!/lc1/treefam/SubstModel.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */