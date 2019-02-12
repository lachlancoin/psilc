/*     */ package lc1.phyl;
/*     */ 
/*     */ import java.io.PrintWriter;
/*     */ import java.util.Arrays;
/*     */ import pal.datatype.CodonTable;
/*     */ import pal.datatype.CodonTableFactory;
/*     */ import pal.datatype.Codons;
/*     */ import pal.datatype.DataType;
/*     */ import pal.datatype.Nucleotides;
/*     */ import pal.math.OrthogonalHints;
/*     */ import pal.misc.ExternalParameterListener;
/*     */ import pal.misc.PalObjectListener;
/*     */ import pal.misc.ParameterEvent;
/*     */ import pal.substmodel.AbstractRateMatrix;
/*     */ import pal.substmodel.AminoAcidModel;
/*     */ import pal.substmodel.NucleotideModel;
/*     */ import pal.substmodel.RateMatrix;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class DomainCodonModel
/*     */   implements RateMatrix, ExternalParameterListener
/*     */ {
/*     */   RateMatrix model;
/*     */   RateMatrix modelDNA;
/*  37 */   Codons codons = Codons.DEFAULT_INSTANCE;
/*     */   
/*     */   private static final double equalAmino = 0.05D;
/*     */   
/*     */   private static final double equalCodon = 0.015625D;
/*  42 */   private static final int[][][] aminoToDNA = new int[20][][];
/*  43 */   private static final int[][] aminoToCodon = new int[20][];
/*  44 */   private static final int[][] codonToDNA = new int[64][];
/*     */   private static int[] termStates;
/*  46 */   double[][] tmpStore = new double[20][20];
/*  47 */   double[][] tmpStoreDNA = new double[20][20];
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private double[] calculateEquilibriumFreqs()
/*     */   {
/*  76 */     double[] equilibriumFreqs = new double[64];
/*     */     
/*  78 */     Arrays.fill(equilibriumFreqs, 0.0D);
/*  79 */     double[] innerProbs = this.model.getEquilibriumFrequencies();
/*  80 */     double[] dnaProbs = this.modelDNA.getEquilibriumFrequencies();
/*  81 */     for (int i = 0; i < 20; i++) {
/*  82 */       int[][] to = aminoToDNA[i];
/*  83 */       int[] codonTo = aminoToCodon[i];
/*     */       
/*  85 */       double[] dna_based_freq = new double[to.length];
/*  86 */       double totalDNABased = 0.0D;
/*  87 */       for (int k = 0; k < to.length; k++) {
/*  88 */         int[] dna = to[k];
/*  89 */         dna_based_freq[k] = (dnaProbs[0] * dnaProbs[1] * dnaProbs[3]);
/*  90 */         totalDNABased += dna_based_freq[k];
/*     */       }
/*  92 */       for (int k = 0; k < to.length; k++) {
/*  93 */         equilibriumFreqs[codonTo[k]] = (innerProbs[i] * (dna_based_freq[k] / totalDNABased));
/*     */       }
/*     */     }
/*  96 */     return equilibriumFreqs;
/*     */   }
/*     */   
/*     */   public RateMatrix getProteinModel() {
/* 100 */     return this.model;
/*     */   }
/*     */   
/*     */   public RateMatrix getDNAModel() {
/* 104 */     return this.modelDNA;
/*     */   }
/*     */   
/*     */   double[] getDNATransProbs(int[] dnaFrom, int[][] dnaTo, double[][] dnaTrans) {
/* 108 */     double[] prob = new double[dnaTo.length];
/* 109 */     double total = 0.0D;
/* 110 */     for (int i = 0; i < dnaTo.length; i++) {
/* 111 */       prob[i] = (dnaTrans[dnaFrom[0]][dnaTo[i][0]] * 
/* 112 */         dnaTrans[dnaFrom[1]][dnaTo[i][1]] * 
/* 113 */         dnaTrans[dnaFrom[2]][dnaTo[i][2]]);
/* 114 */       total += prob[i];
/*     */     }
/* 116 */     for (int i = 0; i < dnaTo.length; i++) {
/* 117 */       prob[i] /= total;
/*     */     }
/* 119 */     return prob;
/*     */   }
/*     */   
/*     */   private double[][] calculateTransitionProbs(double[][] amino, double[][] dna, double[][] transitionProbs_) {
/* 123 */     for (int i = 0; i < transitionProbs_.length; i++) {
/* 124 */       Arrays.fill(transitionProbs_[i], 0.0D);
/*     */     }
/*     */     
/*     */ 
/* 128 */     for (int i = 0; i < termStates.length; i++) {
/* 129 */       int codonFrom = termStates[i];
/* 130 */       int[] dnaFrom = codonToDNA[codonFrom];
/* 131 */       for (int codonTo = 0; codonTo < 64; codonTo++) {
/* 132 */         int[] dnaTo = codonToDNA[codonTo];
/* 133 */         transitionProbs_[codonFrom][codonTo] = 
/* 134 */           (dna[dnaFrom[0]][dnaTo[0]] * 
/* 135 */           dna[dnaFrom[1]][dnaTo[1]] * 
/* 136 */           dna[dnaFrom[2]][dnaTo[2]]);
/*     */       }
/*     */     }
/*     */     
/* 140 */     for (int i = 0; i < 20; i++) {
/* 141 */       int[] codonFrom = aminoToCodon[i];
/* 142 */       int[][] dnaFrom = aminoToDNA[i];
/* 143 */       for (int k = 0; k < codonFrom.length; k++) {
/* 144 */         for (int j = 0; j < 20; j++) {
/* 145 */           int[] codonTo = aminoToCodon[j];
/* 146 */           int[][] dnaTo = aminoToDNA[j];
/* 147 */           double[] dnaTrans = getDNATransProbs(dnaFrom[k], dnaTo, dna);
/* 148 */           for (int l = 0; l < dnaTo.length; l++) {
/* 149 */             transitionProbs_[codonFrom[k]][codonTo[l]] = (amino[i][j] * dnaTrans[l]);
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/* 154 */     return transitionProbs_;
/*     */   }
/*     */   
/*     */   public DomainCodonModel(RateMatrix model, RateMatrix modelDNA)
/*     */   {
/*  52 */     CodonTable codonTable = CodonTableFactory.createUniversalTranslator();
/*     */     
/*  54 */     Nucleotides DNA = Nucleotides.DEFAULT_INSTANCE;
/*  55 */     termStates = codonTable.getTerminatorIndexes();
/*  56 */     for (int i = 0; i < 20; i++) {
/*  57 */       char[][] codonChar = codonTable.getCodonsFromAminoAcidState(i);
/*  58 */       int[][] dna = new int[codonChar.length][3];
/*  59 */       int[] codonSt = new int[codonChar.length];
/*  60 */       for (int j = 0; j < codonChar.length; j++) {
/*  61 */         codonSt[j] = Codons.getCodonIndexFromNucleotides(codonChar[j]);
/*  62 */         for (int k = 0; k < 3; k++) {
/*  63 */           dna[j][k] = DNA.getState(codonChar[j][k]);
/*     */         }
/*     */       }
/*  66 */       aminoToDNA[i] = dna;
/*  67 */       aminoToCodon[i] = codonSt;
/*     */     }
/*  69 */     for (int i = 0; i < 64; i++) {
/*  70 */       codonToDNA[i] = Codons.getNucleotideStatesFromCodonIndex(i);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 163 */     this.model = model;
/* 164 */     this.modelDNA = modelDNA;
/* 165 */     if ((modelDNA.getDimension() != 4) || (model.getDimension() != 20)) { throw new RuntimeException("dimensions must be 20, 4");
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public int getTypeID()
/*     */   {
/* 172 */     return 0;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public int getModelID()
/*     */   {
/* 179 */     return 0;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public String getUniqueName()
/*     */   {
/* 186 */     return this.model.getUniqueName() + "_" + this.modelDNA.getUniqueName() + "_codon";
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public int getDimension()
/*     */   {
/* 193 */     return 64;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public double[] getEquilibriumFrequencies()
/*     */   {
/* 200 */     return calculateEquilibriumFreqs();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public double getEquilibriumFrequency(int i)
/*     */   {
/* 207 */     throw new RuntimeException("not implemented");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public DataType getDataType()
/*     */   {
/* 214 */     return this.codons;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public double[][] getRelativeRates()
/*     */   {
/* 221 */     throw new RuntimeException("not implemented");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public double getTransitionProbability(int i, int j)
/*     */   {
/* 228 */     throw new RuntimeException("not implemented");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void getTransitionProbabilities(double[][] probabilityStore)
/*     */   {
/* 236 */     this.model.getTransitionProbabilities(this.tmpStore);
/* 237 */     this.modelDNA.getTransitionProbabilities(this.tmpStoreDNA);
/* 238 */     calculateTransitionProbs(this.tmpStore, this.tmpStoreDNA, probabilityStore);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setDistance(double distance)
/*     */   {
/* 245 */     this.model.setDistance(distance);
/* 246 */     this.modelDNA.setDistance(distance);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setDistanceTranspose(double distance)
/*     */   {
/* 253 */     this.model.setDistanceTranspose(distance);
/* 254 */     this.modelDNA.setDistanceTranspose(distance);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void addPalObjectListener(PalObjectListener pol)
/*     */   {
/* 261 */     this.model.addPalObjectListener(pol);
/* 262 */     this.modelDNA.addPalObjectListener(pol);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void removePalObjectListener(PalObjectListener pol)
/*     */   {
/* 269 */     this.model.removePalObjectListener(pol);
/* 270 */     this.modelDNA.removePalObjectListener(pol);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public OrthogonalHints getOrthogonalHints()
/*     */   {
/* 277 */     return this.model.getOrthogonalHints();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public Object clone()
/*     */   {
/* 284 */     return new DomainCodonModel((AminoAcidModel)this.model.clone(), (NucleotideModel)this.modelDNA.clone());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public double setParametersNoScale(double[] parameters)
/*     */   {
/* 292 */     throw new RuntimeException("not implemented");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void scale(double scaleValue)
/*     */   {
/* 301 */     this.model.scale(scaleValue);
/* 302 */     this.modelDNA.scale(scaleValue);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void parameterChanged(ParameterEvent pe)
/*     */   {
/* 309 */     ((AbstractRateMatrix)this.model).parameterChanged(pe);
/* 310 */     ((AbstractRateMatrix)this.modelDNA).parameterChanged(pe);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getParameterName(int i)
/*     */   {
/* 318 */     if (i < this.model.getNumParameters())
/* 319 */       return this.model.getParameterName(i);
/* 320 */     return this.modelDNA.getParameterName(i - this.model.getNumParameters());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void report(PrintWriter out)
/*     */   {
/* 328 */     this.model.report(out);
/* 329 */     this.modelDNA.report(out);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public int getNumParameters()
/*     */   {
/* 337 */     return this.model.getNumParameters() + this.modelDNA.getNumParameters();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setParameter(double param, int n)
/*     */   {
/* 345 */     if (n < this.model.getNumParameters()) {
/* 346 */       this.model.setParameter(param, n);
/*     */     } else {
/* 348 */       this.modelDNA.setParameter(param, n - this.model.getNumParameters());
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public double getParameter(int n)
/*     */   {
/* 356 */     if (n < this.model.getNumParameters()) {
/* 357 */       return this.model.getParameter(n);
/*     */     }
/* 359 */     return this.modelDNA.getParameter(n - this.model.getNumParameters());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setParameterSE(double paramSE, int n)
/*     */   {
/* 367 */     if (n < this.model.getNumParameters()) {
/* 368 */       this.model.setParameterSE(paramSE, n);
/*     */     } else {
/* 370 */       this.modelDNA.setParameterSE(paramSE, n - this.model.getNumParameters());
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public double getLowerLimit(int n)
/*     */   {
/* 378 */     if (n < this.model.getNumParameters()) {
/* 379 */       return this.model.getLowerLimit(n);
/*     */     }
/* 381 */     return this.modelDNA.getLowerLimit(n - this.model.getNumParameters());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public double getUpperLimit(int n)
/*     */   {
/* 389 */     if (n < this.model.getNumParameters()) {
/* 390 */       return this.model.getUpperLimit(n);
/*     */     }
/* 392 */     return this.modelDNA.getUpperLimit(n - this.model.getNumParameters());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public double getDefaultValue(int n)
/*     */   {
/* 400 */     if (n < this.model.getNumParameters()) {
/* 401 */       return this.model.getDefaultValue(n);
/*     */     }
/* 403 */     return this.modelDNA.getDefaultValue(n - this.model.getNumParameters());
/*     */   }
/*     */ }


/* Location:              /home/lachlan/Desktop/psilc1.21/psilc.jar!/lc1/phyl/DomainCodonModel.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */