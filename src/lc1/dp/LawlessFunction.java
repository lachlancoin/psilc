/*    */ package lc1.dp;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ abstract class LawlessFunction
/*    */   extends UnivariateFunction
/*    */ {
/*    */   double[] x;
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   double[] y;
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   int n;
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   static double Lawless423(double lambda, double[] x, double[] y, int n, int z, float c)
/*    */   {
/* 36 */     double esum = 0.0D;
/* 37 */     double total = 0.0D;
/*    */     
/* 39 */     for (int i = 0; i < n; i++)
/*    */     {
/* 41 */       double mult = y == null ? 1.0D : y[i];
/* 42 */       esum += mult * Math.exp(-1.0D * lambda * x[i]);
/* 43 */       total += mult;
/*    */     }
/*    */     
/* 46 */     esum += z * Math.exp(-1.0D * lambda * c);
/*    */     
/*    */ 
/*    */ 
/*    */ 
/* 51 */     return -1.0D * Math.log(esum / total) / lambda;
/*    */   }
/*    */   
/*    */   static double Lawless415(double lambda, double[] x, double[] c, int n)
/*    */   {
/* 56 */     double esum = 0.0D;
/* 57 */     double total = 0.0D;
/*    */     
/* 59 */     for (int i = 0; i < n; i++)
/*    */     {
/* 61 */       double mult = c == null ? 1.0D : c[i];
/* 62 */       esum += mult * Math.exp(-1.0D * lambda * x[i]);
/* 63 */       total += mult;
/*    */     }
/*    */     
/* 66 */     return -1.0D * Math.log(esum / total) / lambda;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public LawlessFunction(double[] x, double[] y, int n)
/*    */   {
/* 80 */     this.x = x;
/* 81 */     this.y = y;
/* 82 */     this.n = n;
/*    */   }
/*    */   
/*    */   public abstract double[] evaluate(double paramDouble);
/*    */ }


/* Location:              /home/lachlan/Desktop/psilc1.21/psilc.jar!/lc1/dp/LawlessFunction.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */