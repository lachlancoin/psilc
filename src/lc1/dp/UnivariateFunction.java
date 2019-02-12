/*    */ package lc1.dp;
/*    */ 
/*    */ import java.io.PrintStream;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ abstract class UnivariateFunction
/*    */ {
/*    */   public abstract double[] evaluate(double paramDouble);
/*    */   
/*    */   public double findZero(double lambda, double tol)
/*    */     throws Exception
/*    */   {
/* 15 */     double[] fx = new double[2];
/* 16 */     for (int i = 0; i < 100; i++) {
/* 17 */       fx = evaluate(lambda);
/* 18 */       if (Math.abs(fx[0]) < tol)
/* 19 */         return lambda;
/* 20 */       lambda -= fx[0] / fx[1];
/* 21 */       if (lambda <= 0.0D) {
/* 22 */         lambda = 0.001D;
/*    */       }
/*    */     }
/*    */     
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/* 32 */     double left = 0.2D;
/* 33 */     double right = 0.2D;
/* 34 */     double mid = 0.2D;
/*    */     
/* 36 */     System.err.println("Newton raphson failed - doing bisection");
/* 37 */     fx = evaluate(lambda);
/* 38 */     if (fx[0] < 0.0D) {
/*    */       do {
/* 40 */         left -= 0.03D;
/* 41 */         if (left < 0.0D) {
/* 42 */           throw new Exception(
/* 43 */             "EVDCensoredFit(): failed to bracket root");
/*    */         }
/* 45 */         fx = evaluate(left);
/* 46 */       } while (fx[0] < 0.0D);
/*    */     } else {
/*    */       do {
/* 49 */         right += 0.1D;
/* 50 */         fx = evaluate(right);
/* 51 */         if (right > 100.0D) {
/* 52 */           throw new Exception(
/* 53 */             "EVDCensoredFit(): failed to bracket root");
/*    */         }
/* 55 */       } while (fx[0] > 0.0D);
/*    */     }
/*    */     
/* 58 */     for (int i = 0; i < 100; i++) {
/* 59 */       mid = (left + right) / 2.0D;
/* 60 */       fx = evaluate(mid);
/* 61 */       if (Math.abs(fx[0]) < tol)
/* 62 */         return mid;
/* 63 */       if (fx[0] > 0.0D) {
/* 64 */         left = mid;
/*    */       } else
/* 66 */         right = mid;
/*    */     }
/* 68 */     throw new Exception(
/* 69 */       "EVDCensoredFit(): even the bisection search failed");
/*    */   }
/*    */ }


/* Location:              /home/lachlan/Desktop/psilc1.21/psilc.jar!/lc1/dp/UnivariateFunction.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */