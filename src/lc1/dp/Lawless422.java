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
/*     */ class Lawless422
/*     */   extends LawlessFunction
/*     */ {
/*     */   int z;
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
/*     */   float c;
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
/*     */   public Lawless422(double[] x, double[] y, int n, int z, float c)
/*     */   {
/* 181 */     super(x, y, n);
/* 182 */     this.n = n;
/* 183 */     this.z = z;
/* 184 */     this.c = c;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public double[] evaluate(double lambda)
/*     */   {
/* 209 */     double esum = 0.0D;
/* 210 */     double xesum = 0.0D;
/* 211 */     double xxesum = 0.0D;
/* 212 */     double xsum = 0.0D;
/*     */     
/* 214 */     double total = 0.0D;
/*     */     
/*     */ 
/* 217 */     for (int i = 0; i < this.n; i++) {
/* 218 */       double mult = this.y == null ? 1.0D : this.y[i];
/* 219 */       xsum += mult * this.x[i];
/* 220 */       esum += mult * Math.exp(-1.0D * lambda * this.x[i]);
/* 221 */       xesum += mult * this.x[i] * Math.exp(-1.0D * lambda * this.x[i]);
/* 222 */       xxesum += mult * this.x[i] * this.x[i] * Math.exp(-1.0D * lambda * this.x[i]);
/* 223 */       total += mult;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 229 */     esum += this.z * Math.exp(-1.0D * lambda * this.c);
/* 230 */     xesum += this.z * this.c * Math.exp(-1.0D * lambda * this.c);
/* 231 */     xxesum += this.z * this.c * this.c * Math.exp(-1.0D * lambda * this.c);
/*     */     
/* 233 */     double ret_f = 1.0D / lambda - xsum / total + xesum / esum;
/* 234 */     double gradient = xesum / esum * (xesum / esum) - xxesum / esum - 
/* 235 */       1.0D / (lambda * lambda);
/*     */     
/* 237 */     return new double[] { ret_f, gradient };
/*     */   }
/*     */ }


/* Location:              /home/lachlan/Desktop/psilc1.21/psilc.jar!/lc1/dp/Lawless422.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */