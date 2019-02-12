/*     */ package lc1.dp;
/*     */ 
/*     */ import java.io.PrintWriter;
/*     */ import java.util.Arrays;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import lc1.domainseq.DomainList;
/*     */ import org.biojava.bio.Annotation;
/*     */ import org.biojava.bio.seq.Sequence;
/*     */ import org.biojava.bio.symbol.LocationTools;
/*     */ import org.biojava.bio.symbol.Symbol;
/*     */ import org.biojava.bio.symbol.SymbolList;
/*     */ import pal.alignment.SitePattern;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ProfileDP
/*     */ {
/*     */   public double[][] emissions;
/*     */   protected double[] nullEmissions;
/*  23 */   double threshold = 1.0E-100D;
/*  24 */   double thresholdMax = 1.0E100D;
/*     */   
/*     */   public Sequence domainList;
/*     */   
/*     */   public Sequence domainListB;
/*     */   
/*     */   protected MarkovModel hmm;
/*     */   
/*     */   protected Symbol sym;
/*     */   
/*     */   public TraceMatrix forwardTrace;
/*     */   public TraceMatrix backwardTrace;
/*     */   String protName;
/*     */   final int seqLength;
/*     */   final int modelLength;
/*  39 */   public boolean verbose = false;
/*     */   
/*  41 */   public double[][] posterior = null;
/*     */   
/*     */   public void print(PrintWriter pw, double evalue, int[] alias)
/*     */   {
/*  45 */     for (Iterator it = this.domainList.features(); it.hasNext();) {
/*  46 */       DomainAnnotation dom = (DomainAnnotation)it.next();
/*  47 */       dom.print(pw, evalue, alias);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*  52 */     pw.flush();
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
/*     */   public double[][] getPosteriorMatch(int[] matchIndices)
/*     */   {
/*  67 */     double[][] d = new double[matchIndices.length][this.seqLength];
/*  68 */     for (int j = 0; j < d.length; j++) {
/*  69 */       d[j] = this.posterior[matchIndices[j]];
/*     */     }
/*  71 */     return d;
/*     */   }
/*     */   
/*     */   public double[][] getPosteriorMatch() {
/*  75 */     double[][] d = new double[this.hmm.states.length][this.seqLength];
/*  76 */     for (int j = 0; j < d.length; j++) {
/*  77 */       d[j] = this.posterior[j];
/*     */     }
/*  79 */     return d;
/*     */   }
/*     */   
/*     */ 
/*     */   double fillPosteriorProbabilities(int i)
/*     */   {
/*  85 */     double total = 0.0D;
/*  86 */     double scale_i = Math.exp(this.forwardTrace.logscale[i] + this.backwardTrace.logscale[i] - getOverallScore()[0]);
/*  87 */     for (int j = 0; j < this.hmm.states.length; j++)
/*  88 */       if (!(this.hmm.states[j] instanceof DotState)) {
/*  89 */         this.posterior[j][i] = (scale_i * (this.forwardTrace.score[j][i] * this.backwardTrace.score[j][i]));
/*  90 */         total += this.posterior[j][i];
/*     */       }
/*  92 */     return total;
/*     */   }
/*     */   
/*     */   void scalePosterior(int i, double scale) {
/*  96 */     for (int j = 0; j < this.hmm.states.length; j++) {
/*  97 */       this.posterior[j][i] = (scale * this.posterior[j][i]);
/*     */     }
/*     */   }
/*     */   
/*     */   public ProfileDP(MarkovModel hmm, String protName, Map stateToEmissions) {
/* 102 */     this(hmm, protName, getEmissions(hmm, stateToEmissions));
/*     */   }
/*     */   
/*     */   private static double[][] getEmissions(MarkovModel hmm, Map stateToEmissions) {
/* 106 */     hmm.set();
/* 107 */     double[][] emissions = new double[hmm.states.length][];
/* 108 */     for (int i = 0; i < hmm.states.length; i++) {
/* 109 */       emissions[i] = ((double[])stateToEmissions.get(hmm.states[i]));
/*     */     }
/* 111 */     return emissions;
/*     */   }
/*     */   
/*     */   private ProfileDP(MarkovModel hmm, String protName, double[][] emissions) {
/* 115 */     hmm.set();
/* 116 */     this.modelLength = hmm.states.length;
/* 117 */     this.seqLength = emissions[0].length;
/* 118 */     this.emissions = emissions;
/* 119 */     this.protName = protName;
/* 120 */     this.hmm = hmm;
/* 121 */     this.domainList = new DomainList(SymbolList.EMPTY_LIST, protName, protName, Annotation.EMPTY_ANNOTATION);
/* 122 */     this.domainListB = new DomainList(SymbolList.EMPTY_LIST, protName, protName, Annotation.EMPTY_ANNOTATION);
/* 123 */     this.forwardTrace = new TraceMatrix(this.modelLength, this.seqLength);
/* 124 */     this.backwardTrace = new TraceMatrix(this.modelLength, this.seqLength);
/* 125 */     this.posterior = new double[hmm.states.length][this.seqLength];
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public ProfileDP(Symbol sym, AlignmentHMM hmm, SitePattern sp, String protName, boolean mix)
/*     */   {
/* 133 */     this(hmm, protName, getMatchEmissions(hmm, sp, mix));
/*     */     
/* 135 */     if (mix) {
/* 136 */       this.nullEmissions = avgMatchEmission(hmm);
/*     */     }
/*     */     else {
/* 139 */       this.nullEmissions = hmm.prob(hmm.nullModel.gState(), sp);
/*     */     }
/* 141 */     this.sym = sym;
/*     */   }
/*     */   
/*     */   private double[] avgMatchEmission(AlignmentHMM hmm) {
/* 145 */     double[] result = new double[this.emissions[0].length];
/* 146 */     for (int i = 0; i < result.length; i++) {
/* 147 */       for (int j = 0; j < hmm.matchIndices.length; j++) {
/* 148 */         int k = hmm.matchIndices[j];
/* 149 */         result[i] += this.emissions[k][i];
/*     */       }
/* 151 */       result[i] /= hmm.matchIndices.length;
/*     */     }
/* 153 */     for (int k = 0; k < hmm.insertIndices.length; k++) {
/* 154 */       int j = hmm.insertIndices[k];
/* 155 */       this.emissions[j] = result;
/*     */     }
/* 157 */     this.emissions[hmm.nIndex] = result;
/* 158 */     this.emissions[hmm.cIndex] = result;
/* 159 */     this.emissions[hmm.jIndex] = result;
/*     */     
/* 161 */     return result;
/*     */   }
/*     */   
/*     */   public static double[][] getMatchEmissions(AlignmentHMM hmm, SitePattern sitePatterns, boolean mix) {
/* 165 */     hmm.set();
/* 166 */     int seqLength = sitePatterns.getSiteCount();
/* 167 */     double[][] emissions = new double[hmm.states.length][seqLength];
/* 168 */     double[] insEmiss = mix ? null : 
/* 169 */       hmm.prob((EmissionState)hmm.states[hmm.insertIndices[0]], sitePatterns);
/* 170 */     double[] delEmiss = new double[seqLength];
/* 171 */     Arrays.fill(delEmiss, 1.0D);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 177 */     int matchLength = hmm.matchIndices.length;
/* 178 */     for (int k = 0; k < matchLength; k++) {
/* 179 */       int j = hmm.matchIndices[k];
/* 180 */       emissions[j] = hmm.prob((EmissionState)hmm.states[j], sitePatterns);
/*     */     }
/* 182 */     for (int k = 0; k < hmm.insertIndices.length; k++) {
/* 183 */       int j = hmm.insertIndices[k];
/* 184 */       emissions[j] = insEmiss;
/*     */     }
/* 186 */     for (int k = 0; k < hmm.deleteIndices.length; k++) {
/* 187 */       int j = hmm.deleteIndices[k];
/* 188 */       emissions[j] = delEmiss;
/*     */     }
/* 190 */     emissions[hmm.nIndex] = insEmiss;
/* 191 */     emissions[hmm.cIndex] = insEmiss;
/* 192 */     emissions[hmm.jIndex] = insEmiss;
/* 193 */     emissions[hmm.beginIndex] = delEmiss;
/* 194 */     emissions[hmm.endIndex] = delEmiss;
/* 195 */     emissions[hmm.magicIndex] = delEmiss;
/* 196 */     return emissions;
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
/*     */   public double[] getOverallScore()
/*     */   {
/* 215 */     double scoreForward = Math.log(this.forwardTrace.overall) + this.forwardTrace.logscale[(this.seqLength - 1)];
/* 216 */     double scoreBackward = Math.log(this.backwardTrace.overall) + this.backwardTrace.logscale[0];
/*     */     
/* 218 */     return new double[] { scoreForward, scoreBackward };
/*     */   }
/*     */   
/*     */   public double[] getNullScore() {
/* 222 */     double nullScoreForward = this.forwardTrace.logNullScore[(this.seqLength - 1)];
/* 223 */     double nullScoreBackward = this.backwardTrace.logNullScore[0];
/* 224 */     return new double[] { nullScoreForward, nullScoreBackward };
/*     */   }
/*     */   
/*     */   public double search(boolean incBackward)
/*     */   {
/* 229 */     if ((this.hmm instanceof AlignmentHMM))
/*     */     {
/* 231 */       calculateNullScoreForward();
/*     */     }
/* 233 */     calcScoresForward();
/* 234 */     if ((this.hmm instanceof AlignmentHMM)) this.forwardTrace.getStatePath(this.hmm.magicIndex, this.seqLength - 1, true);
/* 235 */     if (incBackward) calcScoresBackward();
/* 236 */     double[] sc = getOverallScore();
/*     */     
/* 238 */     double[] nullSc = getNullScore();
/* 239 */     double overallSc = sc[0] - nullSc[0];
/*     */     
/* 241 */     modifyDomainScores();
/*     */     
/* 243 */     return overallSc;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void modifyDomainScores()
/*     */   {
/* 251 */     double totalSc = 0.0D;
/* 252 */     for (Iterator it = this.domainList.features(); 
/* 253 */           it.hasNext();) {
/* 254 */       Object domA = it.next();
/* 255 */       totalSc += ((DomainAnnotation)domA).getScore();
/*     */     }
/* 257 */     double diff = (getOverallScore()[0] - getNullScore()[0] - totalSc) / this.domainList.countFeatures();
/*     */     
/* 259 */     for (Iterator it = this.domainList.features(); 
/* 260 */           it.hasNext();) {
/* 261 */       DomainAnnotation domA = (DomainAnnotation)it.next();
/* 262 */       domA.incrScore(diff);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   double forward(int j, int i)
/*     */   {
/* 270 */     State mj = this.hmm.states[j];
/* 271 */     double mEmiss = this.emissions[j][i];
/* 272 */     int max_j = -1;
/* 273 */     int[] statesTo = this.hmm.statesTo[j];
/* 274 */     double max = 0.0D;
/* 275 */     double sum = 0.0D;
/* 276 */     for (int k = 0; k < statesTo.length; k++) {
/* 277 */       int j1 = statesTo[k];
/* 278 */       double score = this.hmm.transitionsTo[j][k] * this.forwardTrace.score[j1][(i - mj.adv)];
/*     */       
/* 280 */       if (score > max) {
/* 281 */         max = score;
/* 282 */         max_j = statesTo[k];
/*     */       }
/* 284 */       sum += score;
/*     */     }
/* 286 */     double result = sum * mEmiss;
/* 287 */     this.forwardTrace.trace[j][i] = max_j;
/* 288 */     this.forwardTrace.score[j][i] = result;
/* 289 */     return result;
/*     */   }
/*     */   
/*     */   double backward(int j, int i) {
/* 293 */     State mj = this.hmm.states[j];
/* 294 */     int[] statesFrom = this.hmm.statesFrom[j];
/* 295 */     int max_j = -1;
/* 296 */     boolean print = true;
/* 297 */     double max = 0.0D;
/* 298 */     double sum = 0.0D;
/*     */     
/* 300 */     for (int k = 0; k < statesFrom.length; k++) {
/* 301 */       int j1 = statesFrom[k];
/* 302 */       State to = this.hmm.states[j1];
/*     */       
/* 304 */       int i1 = i + to.adv;
/*     */       
/* 306 */       double score = this.hmm.transitionsFrom[j][k] * this.backwardTrace.score[j1][i1] * this.emissions[j1][i1];
/* 307 */       if (score > max) {
/* 308 */         max = score;
/* 309 */         max_j = j1;
/*     */       }
/* 311 */       sum += score;
/*     */     }
/*     */     
/* 314 */     if (i >= 0) {
/* 315 */       this.backwardTrace.trace[j][i] = max_j;
/* 316 */       this.backwardTrace.score[j][i] = sum;
/*     */     }
/* 318 */     return sum;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void calculateNullScoreForward()
/*     */   {
/* 326 */     AlignmentHMM hmm = (AlignmentHMM)this.hmm;
/* 327 */     double gToG = Math.log(hmm.nullModel.gTogTrans);
/* 328 */     double gToF = Math.log(hmm.nullModel.goTofTrans);
/* 329 */     this.forwardTrace.logNullScore[0] = Math.log(this.nullEmissions[0]);
/* 330 */     for (int i = 1; i < this.seqLength; i++) {
/* 331 */       this.forwardTrace.logNullScore[i] = (this.forwardTrace.logNullScore[(i - 1)] + 
/* 332 */         Math.log(this.nullEmissions[i]) + gToG);
/*     */     }
/* 334 */     this.forwardTrace.logNullScore[(this.seqLength - 1)] += gToF;
/*     */   }
/*     */   
/* 337 */   public void calculateNullScoreBackward() { AlignmentHMM hmm = (AlignmentHMM)this.hmm;
/* 338 */     double gToG = Math.log(hmm.nullModel.gTogTrans);
/* 339 */     double gToF = Math.log(hmm.nullModel.goTofTrans);
/* 340 */     this.backwardTrace.logNullScore[(this.seqLength - 1)] = gToF;
/* 341 */     for (int i = this.seqLength - 2; i >= 0; i--) {
/* 342 */       this.backwardTrace.logNullScore[i] = (this.backwardTrace.logNullScore[(i + 1)] + 
/* 343 */         Math.log(this.nullEmissions[(i + 1)]) + gToG);
/*     */     }
/* 345 */     this.backwardTrace.logNullScore[0] += Math.log(this.nullEmissions[0]);
/*     */   }
/*     */   
/*     */ 
/*     */   protected void calcScoresForward()
/*     */   {
/* 351 */     int[] statesFrom = this.hmm.statesFrom[this.hmm.magicIndex];
/* 352 */     for (int k = 0; k < statesFrom.length; k++) {
/* 353 */       int j = statesFrom[k];
/* 354 */       this.forwardTrace.score[j][0] = (this.hmm.transitionsFrom[this.hmm.magicIndex][k] * this.emissions[j][0]);
/*     */     }
/*     */     
/* 357 */     for (int i = 1; i < this.seqLength; i++) {
/* 358 */       this.forwardTrace.logscale[i] = this.forwardTrace.logscale[(i - 1)];
/* 359 */       for (int j = 0; j < this.modelLength; j++) {
/* 360 */         if (j != this.hmm.magicIndex)
/* 361 */           forward(j, i);
/*     */       }
/* 363 */       double[] min = this.forwardTrace.minScore(i);
/* 364 */       if (min[0] < this.threshold) {
/* 365 */         double scale = Math.max(min[0], min[1] / this.thresholdMax);
/* 366 */         this.forwardTrace.scale(scale, i);
/* 367 */         this.forwardTrace.logscale[i] += Math.log(scale);
/*     */       }
/*     */     }
/*     */     
/* 371 */     this.forwardTrace.overall = forward(this.hmm.magicIndex, this.seqLength - 1);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void calcScoresBackward()
/*     */   {
/* 380 */     int[] statesTo = this.hmm.statesTo[this.hmm.magicIndex];
/* 381 */     for (int k = 0; k < statesTo.length; k++) {
/* 382 */       int j = statesTo[k];
/* 383 */       State state = this.hmm.states[j];
/* 384 */       this.backwardTrace.score[j][(this.seqLength - 1)] = this.hmm.transitionsTo[this.hmm.magicIndex][k];
/*     */     }
/*     */     
/* 387 */     for (int i = this.seqLength - 2; i >= 0; i--) {
/* 388 */       this.backwardTrace.logscale[i] = this.backwardTrace.logscale[(i + 1)];
/* 389 */       for (int j = this.modelLength - 1; j >= 0; j--) {
/* 390 */         if (j != this.hmm.magicIndex)
/* 391 */           backward(j, i);
/*     */       }
/* 393 */       double[] min = this.backwardTrace.minScore(i);
/* 394 */       if (min[0] < this.threshold) {
/* 395 */         scale = Math.max(min[0], min[1] / this.thresholdMax);
/* 396 */         this.backwardTrace.scale(scale, i);
/* 397 */         this.backwardTrace.logscale[i] += Math.log(scale);
/*     */       }
/* 399 */       double scale = fillPosteriorProbabilities(i);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 404 */     this.backwardTrace.overall = backward(this.hmm.magicIndex, -1);
/*     */   }
/*     */   
/*     */ 
/*     */   class TraceMatrix
/*     */   {
/*     */     protected final double[][] score;
/*     */     
/*     */     protected final int[][] trace;
/*     */     protected final double[] logscale;
/*     */     protected final double[] logNullScore;
/*     */     double overall;
/*     */     
/*     */     TraceMatrix(int modelLength, int seqLength)
/*     */     {
/* 419 */       this.score = new double[modelLength][seqLength];
/* 420 */       this.trace = new int[modelLength][seqLength];
/* 421 */       this.logscale = new double[seqLength];
/* 422 */       this.logNullScore = new double[seqLength];
/* 423 */       for (int j = 0; j < this.score.length; j++) {
/* 424 */         Arrays.fill(this.score[j], 0.0D);
/* 425 */         Arrays.fill(this.trace[j], -1);
/*     */       }
/* 427 */       Arrays.fill(this.logscale, 0.0D);
/*     */     }
/*     */     
/*     */     double[] minScore(int i) {
/* 431 */       double min = Double.POSITIVE_INFINITY;
/* 432 */       double max = 0.0D;
/* 433 */       for (int j = 0; j < this.score.length; j++) {
/* 434 */         if ((this.score[j][i] > 0.0D) && (this.score[j][i] < min)) {
/* 435 */           min = this.score[j][i];
/*     */         }
/* 437 */         if (this.score[j][i] > max) max = this.score[j][i];
/*     */       }
/* 439 */       return new double[] { min, max };
/*     */     }
/*     */     
/*     */     void scale(double d, int i)
/*     */     {
/* 444 */       for (int j = 0; j < this.score.length; j++) {
/* 445 */         this.score[j][i] /= d;
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     void getStatePath(int j, int i, boolean forward)
/*     */     {
/* 458 */       AlignmentHMM hmm = (AlignmentHMM)ProfileDP.this.hmm;
/* 459 */       boolean backward = !forward;
/* 460 */       int start = i;
/* 461 */       DomainAnnotation.Template domA = null;
/* 462 */       boolean inModel = false;
/* 463 */       double prev_start_score = NaN.0D;
/* 464 */       while ((j >= 0) && (j < ProfileDP.this.modelLength))
/*     */       {
/* 466 */         State state = hmm.states[j];
/* 467 */         if (((backward) && (state == hmm.begin)) || ((forward) && (state == hmm.end))) {
/* 468 */           start = i;
/* 469 */           domA = new DomainAnnotation.Template(ProfileDP.this);
/*     */         }
/* 471 */         else if (((backward) && (state == hmm.end)) || ((forward) && (state == hmm.begin))) {
/* 472 */           if (forward) {
/* 473 */             domA.location = LocationTools.makeLocation(i, start);
/* 474 */             domA.type = ProfileDP.this.protName;
/* 475 */             inModel = true;
/*     */           }
/*     */           else
/*     */           {
/* 479 */             domA.location = LocationTools.makeLocation(start, i);
/*     */           }
/*     */           try
/*     */           {
/* 483 */             if (forward) ProfileDP.this.domainList.createFeature(domA); else
/* 484 */               ProfileDP.this.domainListB.createFeature(domA);
/*     */           } catch (Exception exc) {
/* 486 */             exc.printStackTrace();
/*     */           }
/*     */         }
/* 489 */         else if (inModel) {
/* 490 */           domA.statePath.add(state);
/*     */         }
/* 492 */         j = this.trace[j][i];
/* 493 */         i += (forward ? -state.adv : state.adv);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   static double round(double num)
/*     */   {
/* 501 */     return Math.floor(num * 100.0D) / 100.0D;
/*     */   }
/*     */ }


/* Location:              /home/lachlan/Desktop/psilc1.21/psilc.jar!/lc1/dp/ProfileDP.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */