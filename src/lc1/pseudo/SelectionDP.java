/*     */ package lc1.pseudo;
/*     */ 
/*     */ import java.util.Arrays;
/*     */ import lc1.util.Print;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class SelectionDP
/*     */ {
/*     */   int seqLength;
/*     */   TraceMatrix forward;
/*     */   TraceMatrix backward;
/*     */   static final int DOMAIN = 0;
/*     */   static final int PROTEIN = 1;
/*     */   static final int START = 2;
/*  19 */   static final double[][] transitions = { { 0.98D, 0.01D, 0.01D }, 
/*  20 */     { 0.19D, 0.8D, 0.01D }, 
/*  21 */     { 0.95D, 0.05D, 0.0D } };
/*     */   
/*  23 */   final int num_states = 2;
/*     */   
/*     */   public SelectionDP(double[] domainLikelihood, double[] proteinLikelihood)
/*     */   {
/*  27 */     this.seqLength = domainLikelihood.length;
/*  28 */     this.forward = new TraceMatrix(domainLikelihood, proteinLikelihood, true);
/*  29 */     this.backward = new TraceMatrix(domainLikelihood, proteinLikelihood, false);
/*     */   }
/*     */   
/*     */ 
/*     */   public double[] getPosteriorProteinProb()
/*     */   {
/*  35 */     double[] res = new double[this.seqLength];
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  41 */     for (int i = 0; i < res.length; i++)
/*     */     {
/*  43 */       res[i] = (Math.exp(this.forward.logscale[i] + this.backward.logscale[i] - this.forward.logscale[(this.seqLength - 1)]) * (
/*  44 */         this.forward.score[1][i] * this.backward.score[1][i] / this.forward.endScore));
/*     */       
/*     */       try
/*     */       {
/*  48 */         if (Double.isNaN(res[i])) {
/*  49 */           res[i] = 0.0D;
/*  50 */           throw new Exception("isnan " + this.forward.endScore);
/*     */         }
/*     */       } catch (Exception exc) {
/*  53 */         exc.printStackTrace();
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*  58 */     return res;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   class TraceMatrix
/*     */   {
/*     */     double overallScore;
/*     */     
/*     */ 
/*     */ 
/*  71 */     double threshold = 1.0E-100D;
/*  72 */     double thresholdMax = 1.0E100D;
/*     */     
/*     */ 
/*  75 */     protected final double[][] score = new double[2][SelectionDP.this.seqLength];
/*  76 */     protected double endScore = Double.NEGATIVE_INFINITY;
/*     */     
/*     */ 
/*  79 */     final int[][] trace = new int[2][SelectionDP.this.seqLength];
/*     */     
/*  81 */     final double[] logscale = new double[SelectionDP.this.seqLength];
/*  82 */     int endTrace = -1;
/*     */     
/*     */ 
/*     */ 
/*     */     protected double[][] emissions;
/*     */     
/*     */ 
/*     */ 
/*     */     TraceMatrix(double[] domainEmiss, double[] protEmiss, boolean forward)
/*     */     {
/*  92 */       Arrays.fill(this.score[0], Double.NEGATIVE_INFINITY);
/*  93 */       Arrays.fill(this.score[1], Double.NEGATIVE_INFINITY);
/*  94 */       Arrays.fill(this.logscale, 0.0D);
/*  95 */       Arrays.fill(this.trace[0], -1);
/*  96 */       Arrays.fill(this.trace[1], -1);
/*  97 */       this.emissions = new double[2][SelectionDP.this.seqLength];
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 102 */       for (int i = 0; i < SelectionDP.this.seqLength; i++) {
/* 103 */         this.emissions[0][i] = Math.exp(domainEmiss[i]);
/* 104 */         this.emissions[1][i] = Math.exp(protEmiss[i]);
/*     */       }
/* 106 */       if (forward) forwards(); else {
/* 107 */         backwards();
/*     */       }
/*     */     }
/*     */     
/*     */     public String toString()
/*     */     {
/* 113 */       StringBuffer str = new StringBuffer();
/* 114 */       for (int i = 0; i < this.score.length; i++) {
/* 115 */         str.append(i + ": \n" + Print.toString(this.score[i]));
/*     */       }
/* 117 */       return str.toString();
/*     */     }
/*     */     
/*     */     private void forwards() {
/* 121 */       initialForwardScore(0);
/* 122 */       initialForwardScore(1);
/* 123 */       for (int i = 1; i < SelectionDP.this.seqLength; i++) {
/* 124 */         this.logscale[i] = this.logscale[(i - 1)];
/* 125 */         this.score[0][i] = calculateForwardScore(0, i);
/* 126 */         this.score[1][i] = calculateForwardScore(1, i);
/* 127 */         double min = Math.min(this.score[0][i], this.score[1][i]);
/* 128 */         double max = Math.max(this.score[0][i], this.score[1][i]);
/* 129 */         if (min < this.threshold) {
/* 130 */           min = Math.max(max / this.thresholdMax, min);
/* 131 */           this.score[0][i] /= min;
/* 132 */           this.score[1][i] /= min;
/* 133 */           this.logscale[i] += Math.log(min);
/*     */         }
/*     */       }
/* 136 */       finalForwardScore();
/*     */     }
/*     */     
/*     */     private void backwards() {
/* 140 */       initialBackwardScore(0);
/* 141 */       initialBackwardScore(1);
/* 142 */       for (int i = SelectionDP.this.seqLength - 2; i >= 0; i--) {
/* 143 */         this.logscale[i] = this.logscale[(i + 1)];
/* 144 */         this.score[0][i] = calculateBackwardScore(0, i);
/* 145 */         this.score[1][i] = calculateBackwardScore(1, i);
/* 146 */         double min = Math.min(this.score[0][i], this.score[1][i]);
/* 147 */         double max = Math.max(this.score[0][i], this.score[1][i]);
/* 148 */         if (min < this.threshold) {
/* 149 */           min = Math.max(max / this.thresholdMax, min);
/* 150 */           this.score[0][i] /= min;
/* 151 */           this.score[1][i] /= min;
/* 152 */           this.logscale[i] += Math.log(min);
/*     */         }
/*     */       }
/* 155 */       finalBackwardScore();
/*     */     }
/*     */     
/*     */     private void finalForwardScore() {
/* 159 */       double[] forwardScore = new double[2];
/* 160 */       for (int k = 0; k < 2; k++) {
/* 161 */         forwardScore[k] = (this.score[k][(SelectionDP.this.seqLength - 1)] * SelectionDP.transitions[k][2]);
/*     */       }
/* 163 */       this.endTrace = (forwardScore[1] > forwardScore[0] ? 1 : 0);
/* 164 */       this.endScore = (forwardScore[0] + forwardScore[1]);
/*     */     }
/*     */     
/*     */     private void finalBackwardScore() {
/* 168 */       double[] backwardScore = new double[2];
/* 169 */       for (int l = 0; l < 2; l++) {
/* 170 */         backwardScore[l] = (this.score[l][0] * SelectionDP.transitions[2][l] * this.emissions[l][0]);
/*     */       }
/* 172 */       this.endTrace = (backwardScore[1] > backwardScore[0] ? 1 : 0);
/* 173 */       this.endScore = (backwardScore[0] + backwardScore[1]);
/*     */     }
/*     */     
/*     */     private void initialForwardScore(int j)
/*     */     {
/* 178 */       this.score[j][0] = (this.emissions[j][0] * SelectionDP.transitions[2][j]);
/* 179 */       this.trace[j][0] = 2;
/*     */     }
/*     */     
/*     */     private void initialBackwardScore(int j)
/*     */     {
/* 184 */       this.score[j][(SelectionDP.this.seqLength - 1)] = SelectionDP.transitions[j][2];
/* 185 */       this.trace[j][(SelectionDP.this.seqLength - 1)] = 2;
/*     */     }
/*     */     
/*     */     private double calculateForwardScore(int l, int i) {
/* 189 */       double[] forwardScore = new double[2];
/* 190 */       for (int k = 0; k < 2; k++) {
/* 191 */         forwardScore[k] = (this.score[k][(i - 1)] * SelectionDP.transitions[k][l]);
/*     */       }
/* 193 */       this.trace[l][i] = (forwardScore[1] > forwardScore[0] ? 1 : 0);
/* 194 */       return this.emissions[l][i] * (forwardScore[0] + forwardScore[1]);
/*     */     }
/*     */     
/*     */     private double calculateBackwardScore(int k, int i)
/*     */     {
/* 199 */       double[] backwardScore = new double[2];
/* 200 */       for (int l = 0; l < 2; l++) {
/* 201 */         backwardScore[l] = (this.emissions[l][(i + 1)] * this.score[l][(i + 1)] * SelectionDP.transitions[k][l]);
/*     */       }
/* 203 */       this.trace[k][i] = (backwardScore[1] > backwardScore[0] ? 1 : 0);
/* 204 */       return backwardScore[0] + backwardScore[1];
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     int[] getStatePath()
/*     */     {
/* 216 */       int[] result = new int[SelectionDP.this.seqLength];
/* 217 */       int currEl = this.endTrace;
/* 218 */       for (int i = SelectionDP.this.seqLength; i >= 0; i--) {
/* 219 */         i--;
/* 220 */         result[i] = currEl;
/* 221 */         currEl = this.trace[currEl][i];
/*     */       }
/* 223 */       return result;
/*     */     }
/*     */   }
/*     */ }


/* Location:              /home/lachlan/Desktop/psilc1.21/psilc.jar!/lc1/pseudo/SelectionDP.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */