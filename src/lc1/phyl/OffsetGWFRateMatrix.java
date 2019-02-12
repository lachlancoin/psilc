/*    */ package lc1.phyl;
/*    */ 
/*    */ import pal.substmodel.RateMatrix;
/*    */ 
/*    */ public class OffsetGWFRateMatrix extends ParameterizedRateMatrix
/*    */ {
/*    */   double baseF;
/*    */   double lowerL;
/*    */   double upperL;
/*    */   
/*    */   public OffsetGWFRateMatrix(WAG_GWF base, double fOffset, double range)
/*    */   {
/* 13 */     super(base);
/* 14 */     this.baseF = base.getParameter(0);
/* 15 */     setParameter(fOffset, 1);
/* 16 */     this.lowerL = Math.max(0.01D - this.baseF, -1.0D * range);
/* 17 */     this.upperL = Math.min(1.0D - this.baseF, range);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public void setParameter(double param, int i)
/*    */   {
/* 27 */     this.base.setParameter(param + this.baseF, i);
/*    */     try
/*    */     {
/* 30 */       if (this.base.getParameter(i) != param + this.baseF) throw new Exception("something wrong ");
/*    */     }
/*    */     catch (Throwable t)
/*    */     {
/* 34 */       t.printStackTrace();
/* 35 */       System.exit(0);
/*    */     }
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public int getNumParameters()
/*    */   {
/* 43 */     return 1;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public double getParameter(int i)
/*    */   {
/* 51 */     return this.base.getParameter(i) - this.baseF;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public String getParameterName(int i)
/*    */   {
/* 60 */     return "F";
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public void setParameterSE(double paramSE, int n) {}
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public double getLowerLimit(int n)
/*    */   {
/* 76 */     return this.lowerL;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public double getUpperLimit(int n)
/*    */   {
/* 88 */     return this.upperL;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public double getDefaultValue(int n)
/*    */   {
/* 98 */     return 0.0D;
/*    */   }
/*    */ }


/* Location:              /home/lachlan/Desktop/psilc1.21/psilc.jar!/lc1/phyl/OffsetGWFRateMatrix.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */