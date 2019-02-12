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
/*     */ 
/*     */ public class DNACodonModel
/*     */   implements RateMatrix, ExternalParameterListener
/*     */ {
/*     */   RateMatrix modelDNA;
/*  35 */   private static final int[][] codonToDNA = new int[64][];
/*  36 */   private static final Codons codons = Codons.DEFAULT_INSTANCE;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   double[][] tmpStoreDNA;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private double[] calculateEquilibriumFreqs()
/*     */   {
/*  52 */     double[] equilibriumFreqs = new double[64];
/*     */     
/*  54 */     Arrays.fill(equilibriumFreqs, 0.0D);
/*  55 */     double[] dnaProbs = this.modelDNA.getEquilibriumFrequencies();
/*  56 */     for (int i = 0; i < 64; i++) {
/*  57 */       int[] dna = codonToDNA[i];
/*  58 */       equilibriumFreqs[i] = (dnaProbs[dna[0]] * dnaProbs[dna[1]] * dnaProbs[dna[2]]);
/*     */     }
/*  60 */     return equilibriumFreqs;
/*     */   }
/*     */   
/*     */ 
/*     */   private double[][] calculateTransitionProbs(double[][] dna, double[][] transitionProbs_)
/*     */   {
/*  66 */     for (int codonFrom = 0; codonFrom < 64; codonFrom++) {
/*  67 */       int[] dnaFrom = codonToDNA[codonFrom];
/*  68 */       for (int codonTo = 0; codonTo < 64; codonTo++) {
/*  69 */         int[] dnaTo = codonToDNA[codonTo];
/*  70 */         transitionProbs_[codonFrom][codonTo] = 
/*  71 */           (dna[dnaFrom[0]][dnaTo[0]] * 
/*  72 */           dna[dnaFrom[1]][dnaTo[1]] * 
/*  73 */           dna[dnaFrom[2]][dnaTo[2]]);
/*     */       }
/*     */     }
/*  76 */     return transitionProbs_;
/*     */   }
/*     */   
/*     */   public DNACodonModel(RateMatrix modelDNA)
/*     */   {
/*  40 */     CodonTable codonTable = CodonTableFactory.createUniversalTranslator();
/*  41 */     Nucleotides DNA = Nucleotides.DEFAULT_INSTANCE;
/*  42 */     for (int i = 0; i < 64; i++) {
/*  43 */       codonToDNA[i] = Codons.getNucleotideStatesFromCodonIndex(i);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*  48 */     this.tmpStoreDNA = new double[20][20];
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  83 */     this.modelDNA = modelDNA;
/*  84 */     if (modelDNA.getDimension() != 4) { throw new RuntimeException(" 4 " + modelDNA.getDimension());
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public int getTypeID()
/*     */   {
/*  91 */     return 0;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public int getModelID()
/*     */   {
/*  98 */     return 0;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public String getUniqueName()
/*     */   {
/* 105 */     return this.modelDNA.getUniqueName() + "_codon";
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public int getDimension()
/*     */   {
/* 112 */     return 64;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public double[] getEquilibriumFrequencies()
/*     */   {
/* 119 */     return calculateEquilibriumFreqs();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public double getEquilibriumFrequency(int i)
/*     */   {
/* 126 */     throw new RuntimeException("not implemented");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public DataType getDataType()
/*     */   {
/* 133 */     return codons;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public double[][] getRelativeRates()
/*     */   {
/* 140 */     throw new RuntimeException("not implemented");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public double getTransitionProbability(int i, int j)
/*     */   {
/* 147 */     throw new RuntimeException("not implemented");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void getTransitionProbabilities(double[][] probabilityStore)
/*     */   {
/* 155 */     this.modelDNA.getTransitionProbabilities(this.tmpStoreDNA);
/* 156 */     calculateTransitionProbs(this.tmpStoreDNA, probabilityStore);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setDistance(double distance)
/*     */   {
/* 163 */     this.modelDNA.setDistance(distance);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setDistanceTranspose(double distance)
/*     */   {
/* 170 */     this.modelDNA.setDistanceTranspose(distance);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void addPalObjectListener(PalObjectListener pol)
/*     */   {
/* 177 */     this.modelDNA.addPalObjectListener(pol);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void removePalObjectListener(PalObjectListener pol)
/*     */   {
/* 184 */     this.modelDNA.removePalObjectListener(pol);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public OrthogonalHints getOrthogonalHints()
/*     */   {
/* 191 */     return this.modelDNA.getOrthogonalHints();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public Object clone()
/*     */   {
/* 198 */     return new DNACodonModel((AbstractRateMatrix)this.modelDNA.clone());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public double setParametersNoScale(double[] parameters)
/*     */   {
/* 206 */     throw new RuntimeException("not implemented");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void scale(double scaleValue)
/*     */   {
/* 215 */     this.modelDNA.scale(scaleValue);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void parameterChanged(ParameterEvent pe)
/*     */   {
/* 222 */     ((AbstractRateMatrix)this.modelDNA).parameterChanged(pe);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getParameterName(int i)
/*     */   {
/* 230 */     return this.modelDNA.getParameterName(i);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void report(PrintWriter out)
/*     */   {
/* 238 */     this.modelDNA.report(out);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public int getNumParameters()
/*     */   {
/* 246 */     return this.modelDNA.getNumParameters();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setParameter(double param, int n)
/*     */   {
/* 255 */     this.modelDNA.setParameter(param, n);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public double getParameter(int n)
/*     */   {
/* 263 */     return this.modelDNA.getParameter(n);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setParameterSE(double paramSE, int n)
/*     */   {
/* 271 */     this.modelDNA.setParameterSE(paramSE, n);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public double getLowerLimit(int n)
/*     */   {
/* 279 */     return this.modelDNA.getLowerLimit(n);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public double getUpperLimit(int n)
/*     */   {
/* 287 */     return this.modelDNA.getUpperLimit(n);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public double getDefaultValue(int n)
/*     */   {
/* 295 */     return this.modelDNA.getDefaultValue(n);
/*     */   }
/*     */ }


/* Location:              /home/lachlan/Desktop/psilc1.21/psilc.jar!/lc1/phyl/DNACodonModel.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */