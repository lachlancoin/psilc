/*     */ package lc1.dp;
/*     */ 
/*     */ import com.braju.format.Format;
/*     */ import com.braju.format.Parameters;
/*     */ import java.io.PrintWriter;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import lc1.domainseq.Domain;
/*     */ import lc1.domainseq.Domain.Template;
/*     */ import org.biojava.bio.seq.FeatureHolder;
/*     */ import org.biojava.bio.seq.Sequence;
/*     */ import org.biojava.bio.symbol.Alphabet;
/*     */ import org.biojava.bio.symbol.IllegalAlphabetException;
/*     */ import org.biojava.bio.symbol.Location;
/*     */ import org.biojava.bio.symbol.Symbol;
/*     */ 
/*     */ public class DomainAnnotation extends Domain
/*     */ {
/*     */   private List statePath;
/*     */   private double modelSc;
/*     */   private double nullSc;
/*     */   private double modelTr;
/*  23 */   List emScores = new ArrayList();
/*  24 */   List emScoresN = new ArrayList();
/*  25 */   List tScores = new ArrayList();
/*     */   
/*  27 */   public List getStatePath() { return this.statePath; }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void printVerbose(PrintWriter pw)
/*     */   {
/*  51 */     pw.print("VERBOSE\n");
/*     */     
/*  53 */     pw.print("StatePath \n");toString(this.statePath, pw, " %6s");
/*  54 */     pw.print("Match - insert scores\n ");toString(this.emScores, pw, " %6.2f");
/*  55 */     pw.print("\n Insert scores\n ");toString(this.emScoresN, pw, " %6.2f");
/*  56 */     pw.print("\n Transition scores\n ");toString(this.tScores, pw, " %6.2f");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*  61 */   static final double log2 = StrictMath.log(2.0D);
/*     */   
/*     */ 
/*     */ 
/*     */   static final String printStyle = "%-30s  %12s %7s %7s %7s %7s %7s %7s\n";
/*     */   
/*     */ 
/*     */   static final String printStyle1 = "%-30s  %12s %7s %7s %7s %7s %7.2g %7.2g\n";
/*     */   
/*     */ 
/*     */ 
/*     */   void toString(List elements, PrintWriter pw, String format)
/*     */   {
/*  74 */     for (int i = elements.size() - 1; i >= 1; i--) {
/*  75 */       pw.print(Format.sprintf(format, 
/*  76 */         new Parameters(elements.get(i))));
/*     */     }
/*     */   }
/*     */   
/*     */   void toStringStates(List statePath, PrintWriter pw) {
/*  81 */     pw.print("from ");
/*  82 */     pw.print(getLocation().getMin());pw.print(" to ");
/*  83 */     pw.print(getLocation().getMax());pw.print(" with score ");
/*  84 */     pw.print(getScore());pw.print(":\n");
/*  85 */     for (int i = statePath.size() - 1; i >= 0; i--) {
/*  86 */       State sym = (State)statePath.get(i - 1);
/*  87 */       pw.print(Format.sprintf("% 6s", new Parameters(sym.getName())));
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*  93 */   static final Double zero = new Double(0.0D);
/*     */   
/*  95 */   public static void printHeader(PrintWriter pw) { pw.println("HMM scores");
/*  96 */     pw.print(Format.sprintf("%-30s  %12s %7s %7s %7s %7s %7s %7s\n", new String[] { "Name", "Domain", "start", "end", "s1", "e1", "score", "evalue" }));
/*     */   }
/*     */   
/*     */   public void print(PrintWriter pw, double evalue, int[] alias) {
/* 100 */     int st1 = getLocation().getMin();
/* 101 */     int st = alias[st1];
/* 102 */     int end1 = getLocation().getMax();
/* 103 */     int end = alias[end1];
/*     */     
/* 105 */     pw.print(Format.sprintf("%-30s  %12s %7s %7s %7s %7s %7.2g %7.2g\n", 
/* 106 */       new Parameters(getSequence().getName()).add(getSymbol().getName())
/* 107 */       .add(st).add(end).add(st1).add(end1)
/* 108 */       .add(getScore() / log2)
/*     */       
/*     */ 
/*     */ 
/* 112 */       .add(evalue)));
/*     */     
/*     */ 
/*     */ 
/* 116 */     pw.flush();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public DomainAnnotation(Sequence sourceSeq, FeatureHolder parent, Template template)
/*     */     throws IllegalArgumentException, IllegalAlphabetException
/*     */   {
/* 127 */     super(sourceSeq, parent, template);
/* 128 */     this.statePath = template.statePath;
/* 129 */     this.emScores = template.emScores;
/* 130 */     this.emScoresN = template.emScoresN;this.tScores = template.tScores;
/* 131 */     this.modelSc = template.modelSc;this.nullSc = template.nullSc;this.modelTr = template.modelTr;
/*     */   }
/*     */   
/*     */   public static class Template
/*     */     extends Domain.Template
/*     */   {
/*     */     public List statePath;
/* 138 */     private List emission = new ArrayList();
/* 139 */     public List emScores = new ArrayList();
/* 140 */     public List emScoresN = new ArrayList();
/* 141 */     public List tScores = new ArrayList();
/* 142 */     public double nullSc = 0.0D;
/* 143 */     public double modelTr = 0.0D;
/* 144 */     public double modelSc = 0.0D;
/*     */     
/*     */ 
/*     */     public Template() {}
/*     */     
/*     */ 
/*     */     public Template(Alphabet stateAlphabet) {}
/*     */     
/*     */     public Template(ProfileDP dp)
/*     */     {
/* 154 */       this.statePath = new ArrayList();
/* 155 */       this.symbol = dp.sym;
/*     */     }
/*     */     
/*     */     void addEmptyEntry()
/*     */     {
/* 160 */       this.emScores.add(DomainAnnotation.zero);
/* 161 */       this.emScoresN.add(DomainAnnotation.zero);
/*     */     }
/*     */   }
/*     */ }


/* Location:              /home/lachlan/Desktop/psilc1.21/psilc.jar!/lc1/dp/DomainAnnotation.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */