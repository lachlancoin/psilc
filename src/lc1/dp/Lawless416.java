/*     */ package lc1.dp;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ class Lawless416
/*     */   extends LawlessFunction
/*     */ {
/*     */   public Lawless416(double[] x, double[] y, int n)
/*     */   {
/* 108 */     super(x, y, n);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public double[] evaluate(double lambda)
/*     */   {
/*     */     double total;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     double xxesum;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     double xsum;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     double xesum;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 138 */     double esum = xesum = xsum = xxesum = total = 0.0D;
/*     */     
/* 140 */     for (int i = 0; i < this.n; i++) {
/* 141 */       double mult = this.y == null ? 1.0D : this.y[i];
/* 142 */       xsum += mult * this.x[i];
/* 143 */       xesum += mult * this.x[i] * Math.exp(-1.0D * lambda * this.x[i]);
/* 144 */       xxesum += mult * this.x[i] * this.x[i] * Math.exp(-1.0D * lambda * this.x[i]);
/* 145 */       esum += mult * Math.exp(-1.0D * lambda * this.x[i]);
/* 146 */       total += mult;
/*     */     }
/*     */     
/* 149 */     double result = 1.0D / lambda - xsum / total + xesum / esum;
/* 150 */     double grad = xesum / esum * (xesum / esum) - xxesum / esum - 
/* 151 */       1.0D / (lambda * lambda);
/*     */     
/* 153 */     return new double[] { result, grad };
/*     */   }
/*     */ }


/* Location:              /home/lachlan/Desktop/psilc1.21/psilc.jar!/lc1/dp/Lawless416.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */