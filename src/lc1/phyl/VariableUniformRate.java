/*    */ package lc1.phyl;
/*    */ 
/*    */ import pal.substmodel.UniformRate;
/*    */ 
/*    */ 
/*    */ 
/*    */ public class VariableUniformRate
/*    */   extends UniformRate
/*    */ {
/*    */   public VariableUniformRate(double rate, double range)
/*    */   {
/* 12 */     this.RANGE = range;
/* 13 */     this.defaultRate = rate;
/* 14 */     this.lowerlimit = (rate / this.RANGE);
/* 15 */     this.upperlimit = (rate * this.RANGE);
/* 16 */     this.rate[0] = rate;
/*    */   }
/*    */   
/* 19 */   private double RANGE = 2.0D;
/*    */   
/*    */   private double defaultRate;
/*    */   
/*    */   private double lowerlimit;
/*    */   
/*    */   private double upperlimit;
/*    */   
/*    */   public int getNumParameters()
/*    */   {
/* 29 */     return 1;
/*    */   }
/*    */   
/*    */   public void setParameter(double param, int n)
/*    */   {
/* 34 */     this.rate[n] = param;
/*    */   }
/*    */   
/*    */   public double getParameter(int n)
/*    */   {
/* 39 */     return this.rate[n];
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public void setParameterSE(double paramSE, int n) {}
/*    */   
/*    */ 
/*    */   public double getLowerLimit(int n)
/*    */   {
/* 49 */     return this.lowerlimit;
/*    */   }
/*    */   
/*    */   public double getUpperLimit(int n)
/*    */   {
/* 54 */     return this.upperlimit;
/*    */   }
/*    */   
/*    */   public double getDefaultValue(int n)
/*    */   {
/* 59 */     return this.defaultRate;
/*    */   }
/*    */ }


/* Location:              /home/lachlan/Desktop/psilc1.21/psilc.jar!/lc1/phyl/VariableUniformRate.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */