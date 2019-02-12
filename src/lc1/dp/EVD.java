/*     */ package lc1.dp;
/*     */ 
/*     */ import java.io.BufferedOutputStream;
/*     */ import java.io.File;
/*     */ import java.io.FileNotFoundException;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.PrintStream;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import lc1.util.SheetIO;
/*     */ import org.omegahat.Simulation.MCMC.Listeners.Histogram;
/*     */ import org.omegahat.Simulation.RandomGenerators.CollingsPRNG;
/*     */ import org.omegahat.Simulation.RandomGenerators.CollingsPRNGAdministrator;
/*     */ import org.omegahat.Simulation.RandomGenerators.PRNG;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class EVD
/*     */ {
/*     */   public static void main1(String[] args)
/*     */     throws Exception
/*     */   {
/*  28 */     EVD dist = new EVD(0.1D, 30.0D);
/*  29 */     double[] results = dist.generateSampleData(50000);
/*  30 */     EVD dist1 = new EVD(results);
/*     */   }
/*     */   
/*     */   public static void getThresholdsForEvalue(File repository, File output, double evalue, int database_size)
/*     */     throws Exception
/*     */   {
/*  36 */     File[] files = repository.listFiles();
/*  37 */     PrintStream ps = new PrintStream(new BufferedOutputStream(new FileOutputStream(output)));
/*  38 */     double pvalue = evalue / database_size;
/*  39 */     for (int i = 0; i < files.length; i++) {
/*  40 */       File hmm = new File(files[i], "HMM_ls");
/*  41 */       Iterator sh = SheetIO.read(hmm, "\\s+");
/*  42 */       for (int ij = 0; ij < 15; ij++) {
/*  43 */         sh.next();
/*     */       }
/*  45 */       List row = (List)sh.next();
/*  46 */       double mu = Double.parseDouble((String)row.get(1));
/*  47 */       double lambda = Double.parseDouble((String)row.get(2));
/*     */       
/*  49 */       EVD evd = new EVD(lambda, mu);
/*  50 */       double thresh = evd.inverseExtremeValueP(pvalue);
/*  51 */       ps.println(files[i].getName() + " " + thresh);
/*     */     }
/*  53 */     ps.close();
/*     */   }
/*     */   
/*     */   public static void main(String[] args) throws Exception {
/*  57 */     EVD dist1 = new EVD(0.1D, 30.0D);
/*  58 */     EVD dist2 = new EVD(0.1D, 30.0D);
/*  59 */     int[] x = new int['È'];
/*  60 */     int[] y = new int['È'];
/*  61 */     int[] y1 = new int['È'];
/*  62 */     for (int i = 0; i < x.length; i++) {
/*  63 */       x[i] = (65336 + i);
/*  64 */       y[i] = ((int)dist1.extremeValueP(x[i]));
/*  65 */       y1[i] = ((int)dist1.extremeValueP(x[i]));
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  73 */   private static final CollingsPRNGAdministrator a = new CollingsPRNGAdministrator();
/*  74 */   private static final PRNG prng = new CollingsPRNG(a.registerPRNGState());
/*     */   double lambda;
/*     */   double mu;
/*     */   
/*     */   public double[] parameters()
/*     */   {
/*  80 */     return new double[] { this.lambda, this.mu };
/*     */   }
/*     */   
/*     */   public EVD(double lambda, double mu) {
/*  84 */     this.lambda = lambda;
/*  85 */     this.mu = mu;
/*     */   }
/*     */   
/*     */   private static double[] getDataFromSheet(File in) throws FileNotFoundException, IOException {
/*  89 */     List list = new ArrayList();
/*  90 */     SheetIO.toCollection(SheetIO.getColumn(SheetIO.read(in, "\\s+"), 6), list);
/*  91 */     double[] results = new double[list.size()];
/*  92 */     for (int i = 0; i < results.length; i++) {
/*  93 */       results[i] = Double.parseDouble((String)list.get(i));
/*     */     }
/*  95 */     return results;
/*     */   }
/*     */   
/*     */   public EVD(double[] results) throws Exception {
/*  99 */     Arrays.sort(results);
/* 100 */     int min = (int)Math.floor(results[0]);
/* 101 */     int max = (int)Math.ceil(results[(results.length - 1)]);
/* 102 */     Histogram h = new Histogram(min, max, 1.0D);
/* 103 */     h.update(results);
/*     */     
/* 105 */     fitToEVD(h, true);
/*     */   }
/*     */   
/*     */   public double[] generateSampleData(int len) {
/* 109 */     double[] data = new double[len];
/* 110 */     for (int i = 0; i < data.length; i++) {
/* 111 */       data[i] = sample();
/*     */     }
/* 113 */     return data;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public double cumulativeProb(float x)
/*     */   {
/* 122 */     return Math.exp(-1.0D * Math.exp(-1.0D * this.lambda * (x - this.mu)));
/*     */   }
/*     */   
/*     */   public double sample() {
/* 126 */     double rand = prng.nextDouble();
/* 127 */     return this.mu - 1.0D / this.lambda * Math.log(-1.0D * Math.log(rand));
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
/*     */   public double extremeValueP(double x)
/*     */   {
/* 148 */     double y = Math.exp(-1.0D * this.lambda * (x - this.mu));
/* 149 */     if (y < 1.0E-7D) {
/* 150 */       return y;
/*     */     }
/*     */     
/* 153 */     return 1.0D - Math.exp(-1.0D * y);
/*     */   }
/*     */   
/*     */   public double inverseExtremeValueP(double y) {
/*     */     double x;
/*     */     double x;
/* 159 */     if (y < 1.0E-7D) {
/* 160 */       x = Math.log(y) * (-1.0D / this.lambda) + this.mu;
/*     */ 
/*     */     }
/*     */     else
/*     */     {
/* 165 */       x = this.mu + -1.0D / this.lambda * Math.log(-1.0D * Math.log(1.0D - y));
/*     */     }
/*     */     
/* 168 */     return x;
/*     */   }
/*     */   
/*     */ 
/*     */   public double pValue(double x)
/*     */   {
/* 174 */     double pvalue = 1.0D / (1.0D + Math.pow(2.0D, x));
/* 175 */     double pvalue2 = extremeValueP(x);
/* 176 */     if (pvalue2 < pvalue) {
/* 177 */       return pvalue2;
/*     */     }
/* 179 */     return pvalue;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private void fitToEVD(Histogram hist)
/*     */   {
/* 190 */     double[][] freq = hist.frequency();
/* 191 */     double[] y = convertToLogLog(freq[1]);
/* 192 */     double[] res = lineFit(freq[0], y);
/* 193 */     this.lambda = (-res[0]);
/* 194 */     this.mu = (res[1] / this.lambda);
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
/*     */   private static double[] convertToLogLog(double[] input)
/*     */   {
/* 211 */     double[] output = new double[input.length];
/*     */     
/* 213 */     for (int i = 0; i < input.length; i++)
/*     */     {
/* 215 */       output[i] = Math.log(-Math.log(1.0D - input[i]));
/*     */     }
/*     */     
/* 218 */     return output;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static double[] lineFit(double[] x, double[] y)
/*     */   {
/* 230 */     double xavg = 0.0D;
/* 231 */     double yavg = 0.0D;
/* 232 */     int N = x.length;
/*     */     
/* 234 */     for (int i = 0; i < N; i++)
/*     */     {
/* 236 */       xavg += x[i];
/* 237 */       yavg += y[i];
/*     */     }
/*     */     
/* 240 */     xavg /= N;
/* 241 */     yavg /= N;
/*     */     
/*     */ 
/* 244 */     double vxx = 0.0D;
/* 245 */     double vyy = 0.0D;
/* 246 */     double vxy = 0.0D;
/*     */     
/* 248 */     for (int i = 0; i < N; i++)
/*     */     {
/* 250 */       vxx += (x[i] - xavg) * (x[i] - xavg);
/* 251 */       vyy += (y[i] - yavg) * (y[i] - xavg);
/* 252 */       vxy += (x[i] - xavg) * (y[i] - yavg);
/*     */     }
/*     */     
/* 255 */     double slope = vxy / vxx;
/* 256 */     double intercept = yavg - xavg * slope;
/* 257 */     double r = vxy / (Math.sqrt(vxx) * Math.sqrt(vyy));
/*     */     
/* 259 */     return new double[] { slope, intercept, r };
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
/*     */   private double[][] getFrequency(Histogram hist)
/*     */   {
/* 284 */     double[][] freq = hist.frequency();
/* 285 */     double[][] res = new double[2][freq.length];
/* 286 */     for (int i = 0; i < freq.length; i++) {
/* 287 */       res[0][i] = freq[i][0];
/* 288 */       res[1][i] = freq[i][2];
/*     */     }
/* 290 */     return res;
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
/*     */   public void fitToEVD(Histogram hist, boolean censor)
/*     */     throws Exception
/*     */   {
/* 306 */     int z = 0;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 314 */     double[][] freq = getFrequency(hist);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 321 */     int min = (int)hist.min();
/*     */     
/* 323 */     int lowbound = min;
/*     */     
/*     */ 
/* 326 */     if (censor)
/*     */     {
/*     */ 
/* 329 */       double max_freq = -1.0D;
/*     */       
/* 331 */       for (int i = 0; i < freq[0].length; i++)
/*     */       {
/*     */ 
/* 334 */         if (freq[1][i] > max_freq)
/*     */         {
/* 336 */           max_freq = freq[1][i];
/* 337 */           lowbound = (int)freq[0][i];
/*     */         }
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 344 */     int highbound = (int)hist.max() - 1;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 349 */     for (int iteration = 0; iteration < 100; iteration++)
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/* 354 */       int hsize = highbound - lowbound + 1;
/*     */       
/* 356 */       if (hsize < 5)
/*     */       {
/*     */ 
/* 359 */         throw new Exception("Not enough data to train EVD");
/*     */       }
/*     */       
/* 362 */       double[] x = new double[hsize];
/* 363 */       double[] y = new double[hsize];
/* 364 */       int n = 0;
/*     */       
/* 366 */       for (int sc = lowbound; sc <= highbound; sc++)
/*     */       {
/* 368 */         x[(sc - lowbound)] = (sc + 0.5D);
/* 369 */         y[(sc - lowbound)] = freq[1][(sc - min)];
/* 370 */         n = (int)(n + freq[1][(sc - min)]);
/*     */       }
/*     */       
/*     */ 
/* 374 */       if (n < 100)
/*     */       {
/* 376 */         throw new Exception("require fitting to at least 100 points");
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 385 */       if (censor)
/*     */       {
/*     */ 
/* 388 */         if (iteration == 0)
/*     */         {
/* 390 */           z = Math.min(hist.howmany() - n, 
/* 391 */             (int)(0.58198D * n));
/*     */ 
/*     */         }
/*     */         else
/*     */         {
/* 396 */           double psx = cumulativeProb(lowbound);
/* 397 */           z = Math.min(hist.numBins() - n, 
/* 398 */             (int)(n * psx / (1.0D - psx)));
/*     */         }
/*     */       }
/*     */       
/*     */ 
/*     */ 
/* 404 */       if (censor)
/*     */       {
/* 406 */         EVDCensoredFit(x, y, hsize, z, lowbound);
/*     */       }
/*     */       else
/*     */       {
/* 410 */         EVDMaxLikelyFit(x, y, hsize);
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 416 */       int new_highbound = (int)(this.mu - 
/* 417 */         Math.log(-1.0D * Math.log((n + z - 1) / (n + z))) / this.lambda);
/*     */       
/* 419 */       if (new_highbound >= highbound) {
/*     */         break;
/*     */       }
/*     */       
/*     */ 
/*     */ 
/* 425 */       highbound = new_highbound;
/*     */     }
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
/*     */   private void EVDCensoredFit(double[] x, double[] y, int n, int z, float c)
/*     */     throws Exception
/*     */   {
/* 475 */     UnivariateFunction mvf = new Lawless422(x, y, n, z, c);
/* 476 */     this.lambda = mvf.findZero(0.2D, 5.0E-4D);
/* 477 */     this.mu = LawlessFunction.Lawless423(this.lambda, x, y, n, z, c);
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
/*     */   private void EVDMaxLikelyFit(double[] x, double[] c, int n)
/*     */     throws Exception
/*     */   {
/* 517 */     UnivariateFunction mvf = new Lawless416(x, c, n);
/* 518 */     this.lambda = mvf.findZero(0.2D, 5.0E-6D);
/* 519 */     this.mu = LawlessFunction.Lawless415(this.lambda, x, c, n);
/*     */   }
/*     */ }


/* Location:              /home/lachlan/Desktop/psilc1.21/psilc.jar!/lc1/dp/EVD.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */