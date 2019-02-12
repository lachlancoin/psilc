/*     */ package lc1.dp;
/*     */ 
/*     */ import java.io.BufferedOutputStream;
/*     */ import java.io.File;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.PrintStream;
/*     */ import java.util.Iterator;
/*     */ import org.apache.commons.cli.CommandLine;
/*     */ import org.biojava.bio.dist.Distribution;
/*     */ import org.biojava.bio.seq.ProteinTools;
/*     */ import org.biojava.bio.seq.io.SymbolTokenization;
/*     */ import org.biojava.bio.symbol.FiniteAlphabet;
/*     */ import org.biojava.bio.symbol.IntegerAlphabet.IntegerSymbol;
/*     */ import org.biojava.bio.symbol.SimpleSymbolList;
/*     */ import org.biojava.bio.symbol.Symbol;
/*     */ import org.biojava.bio.symbol.SymbolList;
/*     */ import pal.alignment.SitePattern;
/*     */ import pal.datatype.AminoAcids;
/*     */ import pal.datatype.DataType;
/*     */ import pal.substmodel.SubstitutionModel;
/*     */ import pal.tree.SimulatedAlignment;
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
/*     */ public class Calibrate
/*     */ {
/*     */   Tree tree;
/*  38 */   final int SIZE = 5000;
/*  39 */   static final DataType AMINO = new AminoAcids();
/*  40 */   static final SymbolTokenization parser = getParser();
/*     */   Distribution lengthDist;
/*     */   Distribution symDist;
/*     */   
/*     */   private static SymbolTokenization getParser()
/*     */   {
/*     */     try
/*     */     {
/*  48 */       return ProteinTools.getAlphabet().getTokenization("token");
/*     */     }
/*     */     catch (Throwable t) {
/*  51 */       t.printStackTrace(); }
/*  52 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static void main(String[] args)
/*     */     throws Exception
/*     */   {
/*  60 */     Distribution[] dist = new Distribution[2];
/*     */     
/*  62 */     Calibrate cal = new Calibrate(dist[0], dist[1]);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   static ProbabilityDistribution pfamLengthDistribution(int len)
/*     */   {
/*  70 */     new ProbabilityDistribution()
/*     */     {
/*  72 */       public double sample() { return this.val$len; }
/*     */       
/*     */       public double probability(double x) {
/*  75 */         if (x == this.val$len) return 1.0D;
/*  76 */         return 0.0D;
/*     */       }
/*     */     };
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
/*     */   public Calibrate(Distribution lengthDistribution, Distribution symDist)
/*     */   {
/*  95 */     this.lengthDist = lengthDistribution;
/*  96 */     this.symDist = symDist;
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
/*     */   SymbolList generateRandomSequence()
/*     */   {
/* 109 */     int length = ((IntegerAlphabet.IntegerSymbol)this.lengthDist.sampleSymbol()).intValue();
/* 110 */     Symbol[] syms = new Symbol[length];
/* 111 */     for (int i = 0; i < length; i++)
/*     */     {
/* 113 */       syms[i] = this.symDist.sampleSymbol();
/*     */     }
/* 115 */     return new SimpleSymbolList(syms, length, this.symDist.getAlphabet());
/*     */   }
/*     */   
/*     */ 
/* 119 */   public Iterator generateRandomSequences(int no) { new Iterator() {
/*     */       int i;
/*     */       
/* 122 */       public boolean hasNext() { return this.i < this.val$no; }
/*     */       
/*     */       public Object next() {
/* 125 */         return Calibrate.this.generateRandomSequence();
/*     */       }
/*     */       
/*     */       public void remove() {}
/*     */     }; }
/*     */   
/*     */   public void writeSequences(int no, File dir) throws Exception {
/* 132 */     Iterator seqIt = generateRandomSequences(no);
/* 133 */     int i = 0;
/* 134 */     while (seqIt.hasNext()) {
/* 135 */       SymbolList seq = (SymbolList)seqIt.next();
/* 136 */       PrintStream op1 = 
/* 137 */         new PrintStream(new BufferedOutputStream(new FileOutputStream(new File(dir, i + ".align"))));
/* 138 */       writeSequence(seq, op1);
/* 139 */       op1.close();
/* 140 */       i++;
/*     */     }
/*     */   }
/*     */   
/*     */   void writeSequence(SymbolList seq, PrintStream op1) {
/* 145 */     op1.println(seq.toString());
/*     */   }
/*     */   
/*     */   public static void alignmentCalibrate(CommandLine params) throws Exception {
/* 149 */     String[] args = params.getOptionValues("input");
/* 150 */     Distribution[] dist = new Distribution[2];
/*     */     
/* 152 */     AlignmentCalibrate cal = new AlignmentCalibrate(dist[0], dist[1], 10);
/*     */     
/*     */ 
/*     */ 
/* 156 */     cal.writeSequences(Integer.parseInt(params.getOptionValue("max")), 
/* 157 */       new File(params.getOptionValue("output")));
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
/*     */   public static SitePattern generateRandomSitePattern(Tree tree, int len, SubstitutionModel substM)
/*     */   {
/* 179 */     SimulatedAlignment simA = new SimulatedAlignment(len, tree, substM);
/* 180 */     simA.simulate();
/* 181 */     return SitePattern.getSitePattern(simA);
/*     */   }
/*     */ }


/* Location:              /home/lachlan/Desktop/psilc1.21/psilc.jar!/lc1/dp/Calibrate.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */