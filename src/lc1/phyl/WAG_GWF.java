/*    */ package lc1.phyl;
/*    */ 
/*    */ import pal.substmodel.WAG;
/*    */ 
/*    */ 
/*    */ public class WAG_GWF
/*    */   extends WAG
/*    */ {
/*    */   private final double[][] dbrates;
/*    */   private final double[] dbfreq;
/*    */   
/*    */   public WAG_GWF(double[] params, double[] freq)
/*    */   {
/* 14 */     super(freq);
/* 15 */     setParameters(params);
/* 16 */     int numStates = freq.length;
/* 17 */     this.dbfreq = new double[numStates];
/* 18 */     this.dbrates = new double[numStates][numStates];
/* 19 */     super.rebuildRateMatrix(this.dbrates, null);
/* 20 */     WAG.getOriginalFrequencies(this.dbfreq);
/*    */   }
/*    */   
/*    */   public int getNumParameters() {
/* 24 */     return 1;
/*    */   }
/*    */   
/*    */ 
/*    */   protected void fromQToR()
/*    */   {
/* 30 */     double F = getParameter(0);
/* 31 */     for (int i = 0; i < this.dbrates.length; i++)
/*    */     {
/* 33 */       for (int j = i + 1; j < this.dbrates.length; j++)
/*    */       {
/* 35 */         double mod = StrictMath.pow(this.frequency[j] / this.dbfreq[j], 1.0D - F) * 
/* 36 */           StrictMath.pow(this.dbfreq[i] / this.frequency[i], F);
/* 37 */         this.rate[i][j] = (this.dbrates[i][j] * mod);
/* 38 */         double mod_1 = StrictMath.pow(this.frequency[i] / this.dbfreq[i], 1.0D - F) * 
/* 39 */           StrictMath.pow(this.dbfreq[j] / this.frequency[j], F);
/* 40 */         this.rate[j][i] = (this.dbrates[i][j] * mod_1);
/*    */       }
/*    */     }
/* 43 */     makeValid();
/* 44 */     normalize();
/* 45 */     updateMatrixExp();
/* 46 */     fireParametersChangedEvent();
/*    */   }
/*    */   
/*    */   public double getLowerLimit(int n)
/*    */   {
/* 51 */     return 0.0D;
/*    */   }
/*    */   
/*    */   public double getUpperLimit(int n) {
/* 55 */     return 1.0D;
/*    */   }
/*    */   
/*    */   public double getDefaultValue(int n) {
/* 59 */     return 0.5D;
/*    */   }
/*    */ }


/* Location:              /home/lachlan/Desktop/psilc1.21/psilc.jar!/lc1/phyl/WAG_GWF.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */