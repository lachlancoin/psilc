/*     */ package lc1.util;
/*     */ 
/*     */ import java.io.PrintStream;
/*     */ 
/*     */ 
/*     */ 
/*     */ public class Infer
/*     */ {
/*     */   final double n;
/*     */   final double n1;
/*     */   final double n2;
/*     */   final double p;
/*     */   final double p1;
/*     */   final double p2;
/*     */   final double np;
/*     */   final double np1;
/*     */   final double np2;
/*  18 */   final double v = 3.0D;
/*  19 */   final double w = 39.0D;
/*  20 */   double[] result = new double[9];
/*  21 */   double[] powers = new double[9];
/*     */   
/*     */   Infer(double n1, double n2, double s1, double s2)
/*     */   {
/*  25 */     this.n1 = n1;
/*  26 */     this.n2 = n2;
/*  27 */     this.p1 = (s1 / 100.0D * n1);
/*  28 */     this.p2 = (s2 / 100.0D * n2);
/*  29 */     this.n = (n1 + n2);
/*  30 */     this.p = (this.p1 + this.p2);
/*  31 */     this.np = (this.n - this.p);
/*  32 */     this.np2 = (n2 - this.p2);
/*  33 */     this.np1 = (n1 - this.p1);
/*     */   }
/*     */   
/*     */   public static void main(String[] args) {
/*  37 */     Infer model = new Infer(Double.parseDouble(args[0]), 
/*  38 */       Double.parseDouble(args[1]), 
/*  39 */       Double.parseDouble(args[2]), 
/*  40 */       Double.parseDouble(args[3]));
/*     */     
/*  42 */     model.factors();
/*  43 */     double ans = model.calculate(model.result, model.powers);
/*  44 */     System.out.println("The answer is " + ans);
/*  45 */     model.getClass();model.getClass();double prob1 = lnprob(model.n, model.p, 3.0D, 39.0D);
/*  46 */     model.getClass();model.getClass();double prob2 = lnprob(model.n1, model.p1, 3.0D, 39.0D);
/*  47 */     model.getClass();model.getClass();double prob3 = lnprob(model.n2, model.p2, 3.0D, 39.0D);
/*  48 */     ans = prob2 + prob3 - prob1;
/*  49 */     System.out.println("Here " + prob1 + " " + prob2 + " " + prob3 + " " + ans);
/*     */   }
/*     */   
/*     */   double term(double P, double V)
/*     */   {
/*  54 */     return Math.log(P + V - 1.0D) - Math.log(P);
/*     */   }
/*     */   
/*     */   double ratio(int i, double P1, double P2, double V) {
/*  58 */     if (i == 1) {
/*  59 */       return term(P1 + P2, V) - term(P1, V);
/*     */     }
/*     */     
/*  62 */     return term(P1 + P2, V) - term(P2, V);
/*     */   }
/*     */   
/*     */   double c_term(double P, double V)
/*     */   {
/*  67 */     return Math.log(P + V - 1.0D);
/*     */   }
/*     */   
/*     */   double c_ratio(double P1, double P2, double V) {
/*  71 */     return c_term(P1 + P2, V) - (c_term(P1, V) + c_term(P2, V));
/*     */   }
/*     */   
/*     */   void factors()
/*     */   {
/*  76 */     this.result[0] = (-ratio(1, this.n1, this.n2, 42.0D));
/*  77 */     this.result[1] = ratio(2, this.n1, this.n2, 42.0D);
/*  78 */     this.result[2] = ratio(1, this.p1, this.p2, 3.0D);
/*  79 */     this.result[3] = ratio(2, this.p1, this.p2, 3.0D);
/*  80 */     this.result[4] = ratio(1, this.np1, this.np2, 39.0D);
/*  81 */     this.result[5] = ratio(2, this.np1, this.np2, 39.0D);
/*  82 */     this.result[6] = (-c_ratio(this.n1, this.n2, 42.0D));
/*  83 */     this.result[7] = c_ratio(this.p1, this.p2, 3.0D);
/*  84 */     this.result[8] = c_ratio(this.np1, this.np2, 39.0D);
/*  85 */     this.powers[0] = this.n1;
/*  86 */     this.powers[1] = this.n2;
/*  87 */     this.powers[2] = this.p1;
/*  88 */     this.powers[3] = this.p2;
/*  89 */     this.powers[4] = this.np1;
/*  90 */     this.powers[5] = this.np2;
/*  91 */     this.powers[6] = 41.0D;
/*  92 */     this.powers[7] = 2.0D;
/*  93 */     this.powers[8] = 38.0D;
/*     */   }
/*     */   
/*     */   double calculate(double[] num, double[] pow)
/*     */   {
/*  98 */     double ans = 0.0D;
/*  99 */     for (int i = 0; i < num.length; i++) {
/* 100 */       ans += pow[i] * num[i];
/*     */     }
/*     */     
/* 103 */     return -(ans + Math.log(38.0D) + Math.log(37.0D));
/*     */   }
/*     */   
/*     */ 
/*     */   static double lnbeta(double V, double W)
/*     */   {
/* 109 */     return 1.0D + (V - 1.0D) * Math.log(V - 1.0D) + 
/* 110 */       (W - 1.0D) * Math.log(W - 1.0D) - 
/* 111 */       (V + W) * Math.log(V + W);
/*     */   }
/*     */   
/*     */   static double lnNP(double N, double P) {
/* 115 */     return N * Math.log(N) - (
/* 116 */       (N - P) * Math.log(N - P) + P * Math.log(P));
/*     */   }
/*     */   
/*     */   static double lnprob(double N, double P, double V, double W) {
/* 120 */     return lnNP(N, P) + lnbeta(V, W) - Math.log(31980.0D);
/*     */   }
/*     */ }


/* Location:              /home/lachlan/Desktop/psilc1.21/psilc.jar!/lc1/util/Infer.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */