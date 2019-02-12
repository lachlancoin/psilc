/*     */ package lc1.phyhmm;
/*     */ 
/*     */ import com.braju.format.Format;
/*     */ import java.io.BufferedReader;
/*     */ import java.io.BufferedWriter;
/*     */ import java.io.File;
/*     */ import java.io.FileReader;
/*     */ import java.io.FileWriter;
/*     */ import java.io.PrintStream;
/*     */ import java.io.PrintWriter;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import lc1.dp.AlignmentHMM;
/*     */ import lc1.dp.AlignmentProfileParser;
/*     */ import lc1.dp.ProfileDP;
/*     */ import lc1.pfam.DomainAlphabet;
/*     */ import lc1.pfam.PfamAlphabet;
/*     */ import lc1.pfam.StockholmIndex;
/*     */ import lc1.phyl.ScaledRateMatrix;
/*     */ import lc1.phyl.WAG_GWF;
/*     */ import lc1.treefam.AlignTools;
/*     */ import lc1.util.Print;
/*     */ import org.apache.commons.cli.CommandLine;
/*     */ import org.apache.commons.cli.OptionBuilder;
/*     */ import org.apache.commons.cli.Options;
/*     */ import org.apache.commons.cli.Parser;
/*     */ import org.apache.commons.cli.PosixParser;
/*     */ import org.biojava.bio.Annotation;
/*     */ import org.biojava.bio.seq.io.SymbolTokenization;
/*     */ import org.biojava.bio.symbol.Symbol;
/*     */ import pal.alignment.Alignment;
/*     */ import pal.alignment.AlignmentUtils;
/*     */ import pal.alignment.SimpleAlignment;
/*     */ import pal.alignment.SitePattern;
/*     */ import pal.datatype.AminoAcids;
/*     */ import pal.misc.Identifier;
/*     */ import pal.substmodel.RateMatrix;
/*     */ import pal.tree.ReadTree;
/*     */ import pal.tree.Tree;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class PfamRates
/*     */ {
/*  66 */   static final Options OPTIONS = new Options() {};
/*     */   Alignment prot_align;
/*     */   double[][] posteriorProbs;
/*     */   Tree tree;
/*     */   File repos;
/*     */   StockholmIndex stockholmIndex;
/*     */   double[] initialParams;
/*     */   
/*     */   public static void main(String[] args) throws Exception {
/*  75 */     Parser DP_PARSER = new PosixParser();
/*  76 */     CommandLine params = DP_PARSER.parse(OPTIONS, args);
/*  77 */     File repos = new File(params.getOptionValue("repos"));
/*  78 */     String domain = params.getOptionValue("domain");
/*  79 */     DomainAlphabet alph = PfamAlphabet.makeAlphabet(repos);
/*  80 */     Symbol symbol = alph.getTokenization("token").parseToken(domain);
/*  81 */     PfamRates rates = new PfamRates(repos, symbol);
/*  82 */     rates.trainRates();
/*  83 */     rates.printRates();
/*     */   }
/*     */   
/*     */   static Alignment readAlignment(File f) throws Exception {
/*  87 */     BufferedReader br = new BufferedReader(new FileReader(f));
/*  88 */     String st = "";
/*  89 */     List ids = new ArrayList();
/*  90 */     List seqs = new ArrayList();
/*  91 */     while ((st = br.readLine()) != null) {
/*  92 */       String[] row = st.split("\\s+");
/*  93 */       ids.add(new Identifier(row[0]));
/*  94 */       seqs.add(row[1].toUpperCase().replace('.', '-'));
/*     */     }
/*  96 */     br.close();
/*  97 */     return new SimpleAlignment(
/*  98 */       (Identifier[])ids.toArray(new Identifier[0]), 
/*  99 */       (String[])seqs.toArray(new String[0]), 
/* 100 */       AminoAcids.DEFAULT_INSTANCE);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   PfamRates(File repos, Symbol symbol)
/*     */     throws Exception
/*     */   {
/* 110 */     this.repos = repos;
/* 111 */     File pfam_ls = new File(repos, "Pfam-A.seed");
/* 112 */     this.stockholmIndex = (pfam_ls.exists() ? new StockholmIndex(repos) : null);
/* 113 */     this.name = ((String)symbol.getAnnotation().getProperty("pfamA_id"));
/* 114 */     File pfamF = new File(repos, "Pfam/" + this.name);
/* 115 */     File alignF = new File(pfamF, "/SEED");
/* 116 */     if ((pfamF.exists()) && (pfamF.length() != 0L) && (!alignF.exists())) {}
/* 117 */     this.stockholmIndex.writeHMMFile(this.name);
/* 118 */     this.prot_align = readAlignment(alignF);
/*     */     
/* 120 */     BufferedReader br = new BufferedReader(new FileReader(new File(repos, "Pfam/" + this.name + "/HMM_ls")));
/*     */     
/* 122 */     AlignmentProfileParser parser = AlignmentProfileParser.makeParser(br, 
/* 123 */       true, 1, 1);
/* 124 */     AlignmentHMM hmmA = (AlignmentHMM)parser.parse();
/* 125 */     for (int i = 0; i < this.prot_align.getIdCount(); i++) {
/* 126 */       Alignment sl = new SimpleAlignment(this.prot_align.getIdentifier(i), 
/* 127 */         this.prot_align.getAlignedSequenceString(i), this.prot_align.getDataType());
/* 128 */       ProfileDP dp = new ProfileDP(symbol, hmmA, SitePattern.getSitePattern(sl), sl.getIdentifier(0).getName(), false);
/* 129 */       double score = dp.search(true);
/* 130 */       if (i == 0) { this.posteriorProbs = dp.getPosteriorMatch(hmmA.matchIndices);
/*     */       }
/*     */       else {
/* 133 */         double[][] posteriorProbsInner = dp.getPosteriorMatch(hmmA.matchIndices);
/* 134 */         for (int j = 0; j < this.posteriorProbs.length; j++) {
/* 135 */           for (int ik = 0; ik < this.posteriorProbs[0].length; ik++) {
/* 136 */             this.posteriorProbs[j][ik] = Math.max(posteriorProbsInner[j][ik], this.posteriorProbs[j][ik]);
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/* 141 */     this.sp = SitePattern.getSitePattern(this.prot_align);
/* 142 */     this.rates = new double[hmmA.matchIndices.length][2];
/* 143 */     File treeF = new File(pfamF, "tree.nhx");
/* 144 */     if ((!treeF.exists()) || (treeF.length() == 0L)) {
/* 145 */       AlignTools.phyml(treeF, this.prot_align);
/*     */     }
/* 147 */     this.tree = new ReadTree(treeF.getAbsolutePath());
/* 148 */     RateMatrix rm1 = new WAG_GWF(new double[] { 0.0D }, AlignmentUtils.estimateFrequencies(this.sp));
/* 149 */     System.err.println(rm1.getNumParameters());
/*     */     
/* 151 */     this.rm = RateTreeBuild.optimisedRateMatrix(this.prot_align, rm1, this.tree);
/* 152 */     this.initialParams = new double[this.rm.getNumParameters()];
/* 153 */     for (int i = 0; i < this.rm.getNumParameters(); i++) {
/* 154 */       this.initialParams[i] = this.rm.getParameter(i);
/*     */     }
/* 156 */     this.logscale = new boolean[] { false, true };
/* 157 */     this.stddev = new double[] { 0.5D, 1.0D };
/* 158 */     this.paramName = new String[] { "GWF", "RATE" };
/* 159 */     System.err.println("initial params " + Print.toString(this.initialParams));
/*     */   }
/*     */   
/*     */   String[] paramName;
/*     */   boolean[] logscale;
/*     */   double[][] rates;
/*     */   double[] stddev;
/*     */   ScaledRateMatrix rm;
/*     */   SitePattern sp;
/*     */   String name;
/*     */   private int getMaxIndex(double[] d)
/*     */   {
/* 171 */     int i = 0;
/* 172 */     for (int j = 0; j < d.length; j++) {
/* 173 */       if (d[j] > d[i]) i = j;
/*     */     }
/* 175 */     if (d[i] > 0.5D) return i;
/* 176 */     return -1;
/*     */   }
/*     */   
/*     */   void printRates() throws Exception {
/* 180 */     for (int j = 0; j < this.paramName.length; j++) {
/* 181 */       File f = new File(this.repos, "Pfam/" + this.name + "/" + this.paramName[j]);
/* 182 */       PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(f)));
/* 183 */       for (int i = 0; i < this.rates.length; i++) {
/* 184 */         pw.println(Format.sprintf("%5.2f", new Object[] { new Double(this.rates[i][j]) }));
/*     */       }
/* 186 */       pw.close();
/*     */     }
/*     */   }
/*     */   
/*     */   void trainRates() {
/* 191 */     for (int j = 0; j < this.rates.length; j++) {
/* 192 */       int site = getMaxIndex(this.posteriorProbs[j]);
/* 193 */       if (site < 0) {
/* 194 */         for (int i = 0; i < this.paramName.length; i++) {
/* 195 */           this.rates[j][i] = (this.logscale[i] != 0 ? 1.0D : 0.0D);
/*     */         }
/*     */       }
/*     */       else {
/* 199 */         byte[][] pattern = new byte[this.sp.getSequenceCount()][1];
/* 200 */         int alias = this.sp.alias[site];
/* 201 */         for (int k = 0; k < pattern.length; k++) {
/* 202 */           pattern[k][0] = this.sp.pattern[k][alias];
/*     */         }
/* 204 */         SitePattern sp1 = new SitePattern(AminoAcids.DEFAULT_INSTANCE, 1, pattern.length, 
/* 205 */           this.sp, 1, new int[1], new int[] { 1 }, pattern);
/* 206 */         TreeOptimizer mp = new TreeOptimizer(sp1, this.rm, this.tree, this.logscale, this.initialParams, this.stddev);
/* 207 */         this.rates[j] = mp.optimize();
/* 208 */         System.err.println("rate " + (j + 1) + " : " + Print.toString(this.rates[j]));
/*     */       }
/*     */     }
/*     */   }
/*     */ }


/* Location:              /home/lachlan/Desktop/psilc1.21/psilc.jar!/lc1/phyhmm/PfamRates.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */