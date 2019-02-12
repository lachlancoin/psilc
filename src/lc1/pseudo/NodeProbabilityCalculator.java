/*     */ package lc1.pseudo;
/*     */ 
/*     */ import com.braju.format.Format;
/*     */ import java.io.PrintStream;
/*     */ import java.io.PrintWriter;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import lc1.dp.AlignmentHMM;
/*     */ import lc1.dp.EmissionState;
/*     */ import lc1.dp.MarkovModel;
/*     */ import lc1.dp.ProfileDP;
/*     */ import lc1.dp.State;
/*     */ import lc1.phyl.DNACodonModel;
/*     */ import lc1.phyl.DomainCodonModel;
/*     */ import lc1.phyl.FastLikelihoodCalculator;
/*     */ import lc1.treefam.AttributeIdentifier;
/*     */ import pal.alignment.SimpleAlignment;
/*     */ import pal.alignment.SitePattern;
/*     */ import pal.misc.Identifier;
/*     */ import pal.substmodel.RateMatrix;
/*     */ import pal.tree.Node;
/*     */ import pal.tree.NodeUtils;
/*     */ import pal.tree.Tree;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class NodeProbabilityCalculator
/*     */ {
/*  33 */   static final String[] extendedProperties = {
/*  34 */     "domain_pseudo_list", "prot_pseudo_list", "divergence_list" };
/*     */   
/*  36 */   static final double log2 = StrictMath.log(2.0D);
/*     */   
/*  38 */   static final String[] nodeProperties = { "nuc/dom", 
/*  39 */     "nuc/prot", "prot/dom" };
/*     */   
/*     */   static final String nodeString = "%-30s %15.3g %15.3g %10.5g %10.5g  %20s\n";
/*     */   
/*     */   static final String nodeString1 = "%-30s %15s %15s %10s %10s %20s\n";
/*     */   
/*  45 */   boolean truePseudoGene = false;
/*     */   
/*  47 */   static boolean VERBOSE = false;
/*     */   
/*  49 */   static void printHeader(PrintWriter pw) { pw.print(Format.sprintf("%-30s %15s %15s %10s %10s %20s\n", new String[] { "Nodes", "nuc/dom", "prot/dom", 
/*  50 */       "postPMax", "postNMax", "taxonomy" }));
/*     */   }
/*     */   
/*     */   void print(PrintWriter pw, int k, double[] psilc, double[] posteriorMax)
/*     */   {
/*  55 */     pw.print(Format.sprintf("%-30s %15.3g %15.3g %10.5g %10.5g  %20s\n", 
/*  56 */       new Object[] { getIdString(this.node[k]), new Double(psilc[1]), new Double(psilc[0]), 
/*  57 */       new Double(posteriorMax[0]), 
/*  58 */       new Double(posteriorMax[1]), 
/*  59 */       getSpeciesString(this.node[k]) }));
/*     */   }
/*     */   
/*     */ 
/*     */   public static String getIdString(Node n)
/*     */   {
/*  65 */     if (n.isLeaf()) return n.getIdentifier().getName();
/*  66 */     Node[] nod = NodeUtils.getExternalNodes(n);
/*     */     
/*  68 */     StringBuffer sb = new StringBuffer();
/*  69 */     for (int i = 0; i < nod.length; i++) {
/*  70 */       sb.append(nod[i].getIdentifier().getName());
/*  71 */       sb.append("&&");
/*     */     }
/*  73 */     return sb.toString();
/*     */   }
/*     */   
/*  76 */   public static String getSpeciesString(Node n) { if ((n.getIdentifier() instanceof AttributeIdentifier)) {
/*  77 */       AttributeIdentifier att_node = (AttributeIdentifier)n.getIdentifier();
/*  78 */       String spec = att_node.getAttribute("E");
/*  79 */       if (spec == null) spec = att_node.getAttribute("S");
/*  80 */       if (spec != null) {
/*  81 */         return spec;
/*     */       }
/*     */     }
/*  84 */     return "";
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*  89 */   Double zero = new Double(0.0D);
/*     */   
/*     */   RateMatrix nullDNA;
/*     */   int seqLength;
/*     */   Node[] node;
/*  94 */   double[] totalMatchProb = null;
/*     */   
/*     */   static void setModelRecursive(FastLikelihoodCalculator calc, Node node, RateMatrix model)
/*     */   {
/*  98 */     calc.setModel(node, model);
/*  99 */     for (int i = 0; i < node.getChildCount(); i++) {
/* 100 */       setModelRecursive(calc, node.getChild(i), model);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public SitePattern getSubSitePatterns(SitePattern align, double[] posterior, int[] overallAlias)
/*     */   {
/* 107 */     StringBuffer[] strings = new StringBuffer[align.getSequenceCount()];
/* 108 */     List alias = new ArrayList();
/* 109 */     for (int j = 0; j < strings.length; j++) {
/* 110 */       strings[j] = new StringBuffer();
/*     */     }
/* 112 */     int currentLength = 0;
/* 113 */     for (int i = 0; i < posterior.length; i++) {
/* 114 */       if (posterior[i] > 0.01D) {
/* 115 */         for (int j = 0; j < strings.length; j++) {
/* 116 */           strings[j].append(align.getData(j, i));
/*     */         }
/* 118 */         overallAlias[i] = currentLength;
/* 119 */         currentLength++;
/*     */       }
/*     */       else {
/* 122 */         overallAlias[i] = -1;
/*     */       }
/*     */     }
/* 125 */     if (currentLength == 0) return null;
/* 126 */     String[] aligns = new String[strings.length];
/* 127 */     for (int j = 0; j < aligns.length; j++) {
/* 128 */       aligns[j] = strings[j].toString();
/*     */     }
/* 130 */     SitePattern out = SitePattern.getSitePattern(new SimpleAlignment(align, aligns, "_-?.", align.getDataType()));
/* 131 */     for (int i = 0; i < overallAlias.length; i++) {
/* 132 */       if (overallAlias[i] != -1)
/* 133 */         overallAlias[i] = out.alias[overallAlias[i]];
/*     */     }
/* 135 */     return out; }
/*     */   
/* 137 */   public static boolean recursive = false;
/*     */   SitePattern align;
/*     */   public double[] avgDomDomEmiss;
/*     */   public double[][] avgProtDomEmiss;
/*     */   public double[][] avgNucDomEmiss;
/*     */   String[] hmmNames;
/*     */   
/* 144 */   public void getAverageEmissions(Tree tree, AlignmentHMM[] hmm, double[][][] posteriorMatchProbs, RateMatrix insertP) { this.totalMatchProb = new double[this.align.getSiteCount()];
/* 145 */     this.hmmNames = new String[hmm.length];
/* 146 */     for (int i = 0; i < hmm.length; i++) {
/* 147 */       this.hmmNames[i] = hmm[i].getName();
/*     */     }
/* 149 */     if (insertP == null) throw new NullPointerException("null model is null");
/* 150 */     insertP = new DomainCodonModel(insertP, this.nullDNA);
/* 151 */     RateMatrix insertD = new DNACodonModel(this.nullDNA);
/* 152 */     int[] alias = new int[this.align.getSiteCount()];
/* 153 */     System.arraycopy(this.align.alias, 0, alias, 0, alias.length);
/*     */     
/*     */ 
/* 156 */     FastLikelihoodCalculator lhc = new FastLikelihoodCalculator(this.align, tree);
/*     */     
/* 158 */     this.avgDomDomEmiss = new double[this.seqLength];
/* 159 */     Arrays.fill(this.avgDomDomEmiss, 0.0D);
/* 160 */     this.avgProtDomEmiss = new double[this.node.length][this.seqLength];
/* 161 */     this.avgNucDomEmiss = new double[this.node.length][this.seqLength];
/*     */     
/* 163 */     lhc.setModel(insertP);
/* 164 */     double[] insertPEmissions = lhc.calculateSiteLogLikelihood();
/* 165 */     double[][] insertDEmissions = new double[this.node.length][0];
/* 166 */     for (int k = 0; k < this.node.length; k++) {
/* 167 */       Arrays.fill(this.avgProtDomEmiss[k], 0.0D);
/* 168 */       Arrays.fill(this.avgNucDomEmiss[k], 0.0D);
/* 169 */       lhc.setModel(insertP);
/* 170 */       if (recursive) {
/* 171 */         setModelRecursive(lhc, this.node[k], insertD);
/*     */       }
/*     */       else {
/* 174 */         lhc.setModel(this.node[k], insertD);
/*     */       }
/* 176 */       insertDEmissions[k] = lhc.calculateSiteLogLikelihood();
/*     */     }
/* 178 */     for (int i = 0; i < this.seqLength; i++) {
/* 179 */       double insertStatePosterior_i = 1.0D;
/* 180 */       for (int l = 0; l < hmm.length; l++) {
/* 181 */         for (int j = 0; j < posteriorMatchProbs[l].length; j++) {
/* 182 */           insertStatePosterior_i -= posteriorMatchProbs[l][j][i];
/*     */         }
/*     */       }
/*     */       
/* 186 */       insertStatePosterior_i = Math.max(0.0D, insertStatePosterior_i);
/* 187 */       this.totalMatchProb[i] = (1.0D - insertStatePosterior_i);
/* 188 */       this.avgDomDomEmiss[i] = (insertStatePosterior_i * insertPEmissions[alias[i]]);
/* 189 */       for (int k = 0; k < this.node.length; k++) {
/* 190 */         this.avgProtDomEmiss[k][i] = (insertStatePosterior_i * insertPEmissions[alias[i]]);
/*     */         
/* 192 */         this.avgNucDomEmiss[k][i] = (insertStatePosterior_i * insertDEmissions[k][alias[i]]);
/*     */       }
/*     */     }
/* 195 */     lhc.release();
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 200 */     System.err.println("doing match states - ");
/* 201 */     double[][] prot_dom_j = new double[this.node.length][alias.length];
/* 202 */     double[][] nuc_dom_j = new double[this.node.length][alias.length];
/* 203 */     for (int l = 0; l < hmm.length; l++) {
/* 204 */       System.err.println("doing model " + (l + 1) + " of " + hmm.length + " of length : " + posteriorMatchProbs[l].length);
/* 205 */       int jk = 0;
/* 206 */       for (int j = 0; j < posteriorMatchProbs[l].length; j++) {
/* 207 */         SitePattern innerSitePattern = getSubSitePatterns(this.align, posteriorMatchProbs[l][j], alias);
/* 208 */         jk++;
/* 209 */         System.err.print(j);System.err.print(" ");
/* 210 */         if (jk == 40) {
/* 211 */           System.err.println();
/* 212 */           jk = 0;
/*     */         }
/* 214 */         if (innerSitePattern != null)
/*     */         {
/* 216 */           FastLikelihoodCalculator lhc = new FastLikelihoodCalculator(innerSitePattern, tree);
/* 217 */           RateMatrix domain = hmm[l].getMatch(j).getSubstitutionModel();
/* 218 */           domain = new DomainCodonModel(domain, this.nullDNA);
/* 219 */           lhc.setModel(domain);
/*     */           
/* 221 */           double[] dom_dom_j = lhc.calculateSiteLogLikelihood();
/*     */           
/* 223 */           for (int k = 0; k < this.node.length; k++)
/*     */           {
/* 225 */             lhc.setModel(domain);
/* 226 */             if (recursive) {
/* 227 */               setModelRecursive(lhc, this.node[k], insertP);
/*     */             }
/*     */             else {
/* 230 */               lhc.setModel(this.node[k], insertP);
/*     */             }
/* 232 */             prot_dom_j[k] = lhc.calculateSiteLogLikelihood();
/* 233 */             lhc.setModel(domain);
/* 234 */             if (recursive) {
/* 235 */               setModelRecursive(lhc, this.node[k], insertD);
/*     */             }
/*     */             else {
/* 238 */               lhc.setModel(this.node[k], insertD);
/*     */             }
/* 240 */             nuc_dom_j[k] = lhc.calculateSiteLogLikelihood();
/*     */           }
/* 242 */           for (int i = 0; i < this.seqLength; i++)
/* 243 */             if (alias[i] != -1) {
/* 244 */               this.avgDomDomEmiss[i] += dom_dom_j[alias[i]] * posteriorMatchProbs[l][j][i];
/* 245 */               for (int k = 0; k < this.node.length; k++) {
/* 246 */                 this.avgProtDomEmiss[k][i] += prot_dom_j[k][alias[i]] * posteriorMatchProbs[l][j][i];
/* 247 */                 this.avgNucDomEmiss[k][i] += nuc_dom_j[k][alias[i]] * posteriorMatchProbs[l][j][i];
/*     */               }
/*     */             }
/* 250 */           lhc.release();
/*     */         } }
/* 252 */       System.err.println();
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
/*     */   public double[] calculateOverallPsilcScores(int k)
/*     */   {
/* 266 */     double[] psilc = { 0.0D, 0.0D };
/* 267 */     for (int i = 0; i < this.seqLength; i++) {
/* 268 */       psilc[0] += this.avgProtDomEmiss[k][i] - this.avgDomDomEmiss[i];
/* 269 */       psilc[1] += this.avgNucDomEmiss[k][i] - this.avgDomDomEmiss[i];
/*     */     }
/* 271 */     return psilc;
/*     */   }
/*     */   
/*     */   public double[] getPsilcScore(int k, int type) {
/* 275 */     double[] result = new double[this.avgProtDomEmiss[k].length];
/* 276 */     if (type == 0) {
/* 277 */       for (int i = 0; i < result.length; i++) {
/* 278 */         result[i] = (this.avgProtDomEmiss[k][i] - this.avgDomDomEmiss[i]);
/*     */       }
/*     */       
/*     */     } else {
/* 282 */       for (int i = 0; i < result.length; i++) {
/* 283 */         result[i] = (this.avgNucDomEmiss[k][i] - this.avgDomDomEmiss[i]);
/*     */       }
/*     */     }
/* 286 */     return result;
/*     */   }
/*     */   
/*     */ 
/*     */   static double[] exp(double[] d, int[] alias)
/*     */   {
/* 292 */     if (alias != null) {
/* 293 */       double[] res = new double[alias.length];
/* 294 */       for (int i = 0; i < res.length; i++) {
/* 295 */         res[i] = Math.exp(d[alias[i]]);
/*     */       }
/* 297 */       return res;
/*     */     }
/*     */     
/* 300 */     double[] res = new double[d.length];
/* 301 */     for (int i = 0; i < res.length; i++) {
/* 302 */       res[i] = Math.exp(d[i]);
/*     */     }
/* 304 */     return res;
/*     */   }
/*     */   
/*     */ 
/*     */   public static class SelectionModel
/*     */     extends MarkovModel
/*     */   {
/*     */     State domdom;
/*     */     
/*     */     State protdom;
/*     */     State nucdom;
/* 315 */     static boolean usingDefault = true;
/*     */     static int ddi;
/*     */     static int pdi;
/*     */     static int ndi;
/* 319 */     public static String[] fromStates = { "start", "purifying", "positive", "pseudogene" };
/*     */     
/* 321 */     public static String[][] paramNames = {
/* 322 */       { "positive", "pseudogene" }, 
/* 323 */       { "positive", "pseudogene" }, 
/* 324 */       { "purifying", "pseudogene" }, 
/* 325 */       { "purifying", "positive" } };
/*     */     
/*     */     public static String getHMMString() {
/* 328 */       if (usingDefault) return "";
/* 329 */       StringBuffer sb = new StringBuffer();
/* 330 */       for (int i = 0; i < vals.length; i++) {
/* 331 */         for (int j = 0; j < vals[i].length; j++) {
/* 332 */           sb.append("_");
/* 333 */           sb.append(vals[i][j]);
/*     */         }
/*     */       }
/* 336 */       return sb.toString();
/*     */     }
/*     */     
/* 339 */     public static double[] getVals(int k) { return vals[k]; }
/*     */     
/*     */     public double[][] getVals() {
/* 342 */       return vals;
/*     */     }
/*     */     
/* 345 */     public static void setVals(double[][] vals1) { vals = vals1; }
/*     */     
/*     */ 
/* 348 */     public static void setVals(int k, int i, double v) { vals[k][i] = v; }
/*     */     
/* 350 */     private static double[][] vals = {
/* 351 */       { 0.01D, 0.001D }, 
/* 352 */       { 0.01D, 0.0D }, 
/* 353 */       { 0.2D, 0.0D }, 
/* 354 */       { 0.0D, 0.0D } };
/*     */     
/*     */     public void set() {
/* 357 */       super.set();
/* 358 */       ddi = ((Integer)this.alias.get(this.domdom)).intValue();
/* 359 */       pdi = ((Integer)this.alias.get(this.domdom)).intValue();
/* 360 */       ndi = ((Integer)this.alias.get(this.domdom)).intValue();
/*     */     }
/*     */     
/* 363 */     private static void check() { for (int i = 0; i < vals.length; i++)
/* 364 */         if (vals[i][0] + vals[i][1] >= 0.99D) throw new RuntimeException("params not valid");
/*     */     }
/*     */     
/*     */     public SelectionModel() {
/* 368 */       super(null);
/* 369 */       check();
/* 370 */       this.domdom = addState(new EmissionState("dom_dom", null, 1, 1));
/* 371 */       this.protdom = addState(new EmissionState("prot_dom", null, 1, 1));
/* 372 */       this.nucdom = addState(new EmissionState("nuc_dom", null, 1, 1));
/*     */       
/*     */ 
/*     */ 
/* 376 */       setTransition(this.MAGIC, this.domdom, new Double(1.0D - vals[0][0] - vals[0][1]));
/* 377 */       setTransition(this.MAGIC, this.protdom, new Double(vals[0][0]));
/* 378 */       setTransition(this.MAGIC, this.nucdom, new Double(vals[0][1]));
/*     */       
/* 380 */       setTransition(this.domdom, this.domdom, new Double(0.99D - vals[1][0] - vals[1][1]));
/* 381 */       setTransition(this.domdom, this.protdom, new Double(vals[1][0]));
/* 382 */       setTransition(this.domdom, this.nucdom, new Double(vals[1][1]));
/* 383 */       setTransition(this.domdom, this.MAGIC, new Double(0.01D));
/*     */       
/* 385 */       setTransition(this.protdom, this.domdom, new Double(vals[2][0]));
/* 386 */       setTransition(this.protdom, this.protdom, new Double(0.99D - vals[2][0] - vals[2][1]));
/* 387 */       setTransition(this.protdom, this.nucdom, new Double(vals[2][1]));
/* 388 */       setTransition(this.protdom, this.MAGIC, new Double(0.01D));
/*     */       
/* 390 */       setTransition(this.nucdom, this.domdom, new Double(vals[3][0]));
/* 391 */       setTransition(this.nucdom, this.nucdom, new Double(0.99D - vals[3][0] - vals[3][1]));
/* 392 */       setTransition(this.nucdom, this.protdom, new Double(vals[3][1]));
/* 393 */       setTransition(this.nucdom, this.MAGIC, new Double(0.01D));
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static double[][] getSitePosterior(double[] avgProtDomEmiss, double[] avgNucDomEmiss, double[] avgDomDomEmiss, int[] alias)
/*     */   {
/* 402 */     SelectionModel hmm = new SelectionModel();
/* 403 */     double[] magicEmissions = new double[alias.length];
/* 404 */     Arrays.fill(magicEmissions, 1.0D);
/* 405 */     double[][] posterior = new double[2][];
/* 406 */     Map m = new HashMap();
/* 407 */     m.put(hmm.domdom, exp(avgDomDomEmiss, alias));
/* 408 */     m.put(hmm.protdom, exp(avgProtDomEmiss, alias));
/* 409 */     m.put(hmm.nucdom, exp(avgNucDomEmiss, alias));
/* 410 */     m.put(hmm.MAGIC, magicEmissions);
/*     */     
/* 412 */     ProfileDP dp = new ProfileDP(hmm, "", m);
/* 413 */     dp.search(true);
/* 414 */     double[] res = dp.getPosteriorMatch(new int[] { 2 })[0];
/* 415 */     double[] res_ = dp.getPosteriorMatch(new int[] { 3 })[0];
/*     */     
/* 417 */     posterior[0] = new double[avgDomDomEmiss.length];
/* 418 */     Arrays.fill(posterior[0], Double.NEGATIVE_INFINITY);
/* 419 */     posterior[1] = new double[avgDomDomEmiss.length];
/* 420 */     Arrays.fill(posterior[1], Double.NEGATIVE_INFINITY);
/* 421 */     for (int ik = 0; ik < alias.length; ik++) {
/* 422 */       posterior[0][alias[ik]] = res[ik];
/* 423 */       posterior[1][alias[ik]] = res_[ik];
/*     */     }
/*     */     
/* 426 */     return posterior;
/*     */   }
/*     */   
/*     */   public static double[] getMax(double[][] posterior) {
/* 430 */     double[] posteriorMax = new double[2];
/* 431 */     for (int i = 0; i < posterior[0].length; i++)
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 438 */       if (posterior[0][i] > posteriorMax[0])
/* 439 */         posteriorMax[0] = posterior[0][i];
/* 440 */       if (posterior[1][i] > posteriorMax[1])
/* 441 */         posteriorMax[1] = posterior[1][i];
/*     */     }
/* 443 */     return posteriorMax;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   NodeProbabilityCalculator(Node[] node, SitePattern align, RateMatrix nullDNA)
/*     */   {
/* 454 */     this.nullDNA = nullDNA;
/* 455 */     this.align = align;
/*     */     try {
/* 457 */       this.node = node;
/*     */       
/* 459 */       this.seqLength = align.getSiteCount();
/*     */     } catch (Exception exc) {
/* 461 */       exc.printStackTrace();
/*     */     }
/*     */   }
/*     */ }


/* Location:              /home/lachlan/Desktop/psilc1.21/psilc.jar!/lc1/pseudo/NodeProbabilityCalculator.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */