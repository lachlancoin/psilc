/*     */ package lc1.dp;
/*     */ 
/*     */ import java.io.PrintStream;
/*     */ import lc1.util.Print;
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract interface NAlignment
/*     */ {
/*  10 */   public static final Helper[] nHelp = { new Helper(2, null), new Helper(3, null), new Helper(4, null), new Helper(5, null) };
/*     */   
/*     */ 
/*     */   public static final double eta = 0.5D;
/*     */   
/*     */ 
/*     */   public static final double tau = 0.1D;
/*     */   
/*     */ 
/*     */   public static final double epsilon = 0.5D;
/*     */   
/*     */   public static final double delta = 0.4D;
/*     */   
/*  23 */   public static final double b = 2.0D * Helper.log2(0.5D);
/*     */   
/*     */   public abstract Point score();
/*     */   
/*     */   public static class Helper {
/*  28 */     static double c = log2(0.5D);
/*  29 */     static double d = -log2(0.6400000000000001D);
/*  30 */     static double e = -log2(0.5555555555555556D);
/*  31 */     static double s = log2(2.0D);
/*  32 */     static double log2 = Math.log(2.0D);
/*     */     double[][] logProb;
/*     */     double[][] logOdds;
/*     */     
/*  36 */     static Helper makeHelper(int N) { try { if (N - 1 > NAlignment.nHelp.length) {
/*  37 */           throw new Exception("Cannot produce a helper with length greater than " + (NAlignment.nHelp.length + 1));
/*     */         }
/*     */       } catch (Throwable t) {
/*  40 */         t.printStackTrace();
/*  41 */         System.exit(1);
/*  42 */         return null;
/*     */       }
/*  44 */       return NAlignment.nHelp[(N - 2)];
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     double[] logProbStart;
/*     */     
/*     */ 
/*     */     double[] logProbEnd;
/*     */     
/*     */ 
/*     */     int N;
/*     */     
/*     */ 
/*     */     Helper(int paramInt, Helper paramHelper)
/*     */     {
/*  61 */       this(paramInt); }
/*  62 */     private Helper(int N) { this.N = N;
/*  63 */       this.logProb = getProb(N);
/*  64 */       this.logProbStart = getBeginProb(N);
/*  65 */       this.logProbEnd = new double[N];
/*  66 */       System.out.println("logProb " + Print.toStringD(this.logProb));
/*  67 */       System.out.println("logProbStart " + Print.toString(this.logProbStart));
/*  68 */       System.out.println("logProbEnd " + Print.toString(this.logProbEnd));
/*     */     }
/*     */     
/*     */ 
/*     */     static double log2(double d)
/*     */     {
/*  74 */       return Math.log(d) / log2;
/*     */     }
/*     */     
/*     */ 
/*     */     double[][] getLogOdds()
/*     */     {
/*  80 */       if (this.logOdds == null) {
/*  81 */         this.logOdds = calcTrans(this.N);
/*     */       }
/*  83 */       return this.logOdds;
/*     */     }
/*     */     
/*     */     double[][] getLogProb() {
/*  87 */       return this.logProb;
/*     */     }
/*     */     
/*     */     double[] getLogProbStart() {
/*  91 */       return this.logProbStart;
/*     */     }
/*     */     
/*     */     double[] getLogProbEnd()
/*     */     {
/*  96 */       return this.logProbEnd;
/*     */     }
/*     */     
/*     */     private double[] endTrans(int N) {
/* 100 */       double[] c_m = new double[N];
/* 101 */       if (N == 2) {
/* 102 */         c_m[0] = c;
/* 103 */         c_m[1] = 0.0D;
/*     */       }
/* 105 */       return c_m;
/*     */     }
/*     */     
/*     */     private static double[] getBeginProb(int N) {
/* 109 */       double[] prob1 = new double[N];
/* 110 */       int tot = (int)(N * (N + 1) / 2.0D);
/* 111 */       for (int i = 1; i <= N; i++) {
/* 112 */         prob1[(i - 1)] = log2(i / tot);
/*     */       }
/* 114 */       return prob1;
/*     */     }
/*     */     
/*     */     private double[][] calcTrans(int N) {
/* 118 */       double[][] d_mn = new double[N][N];
/* 119 */       for (int i = 1; i <= N; i++) {
/* 120 */         for (int j = 1; j <= N; j++) {
/* 121 */           d_mn[(i - 1)][(j - 1)] = (this.logProb[(i - 1)][(j - 1)] - log2(Math.pow(0.5D, j)));
/*     */         }
/*     */       }
/* 124 */       return d_mn;
/*     */     }
/*     */     
/*     */     private static double[][] getProb(int N)
/*     */     {
/* 129 */       double[][] prob1 = new double[N][N];
/* 130 */       for (int i = 1; i <= N; i++) {
/* 131 */         for (int j = 1; j < i; j++) {
/* 132 */           prob1[(i - 1)][(j - 1)] = log2(1.0D / (N - 1) * 0.4D);
/*     */         }
/* 134 */         prob1[(i - 1)][(i - 1)] = log2(0.5D);
/* 135 */         for (int j = i + 1; j <= N; j++) {
/* 136 */           prob1[(i - 1)][(j - 1)] = log2(1.0D / (N - 1) * 0.4D);
/*     */         }
/*     */       }
/* 139 */       return prob1;
/*     */     }
/*     */     
/*     */     private static int[][] getN_mn(int N) {
/* 143 */       int[][] res = new int[N][N];
/* 144 */       for (int i = 1; i <= N; i++) {
/* 145 */         for (int j = 1; j <= N; j++) {
/* 146 */           if (i == j) {
/* 147 */             res[(i - 1)][(j - 1)] = 1;
/*     */           }
/* 149 */           else if (i > j) {
/* 150 */             res[(i - 1)][(j - 1)] = Combinatorics.binomialCoeff(i, j);
/*     */           }
/* 152 */           else if (i < j) {
/* 153 */             res[(i - 1)][(j - 1)] = Combinatorics.binomialCoeff(N - i, j - i);
/*     */           }
/*     */         }
/*     */       }
/*     */       
/* 158 */       return res;
/*     */     }
/*     */   }
/*     */ }


/* Location:              /home/lachlan/Desktop/psilc1.21/psilc.jar!/lc1/dp/NAlignment.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */