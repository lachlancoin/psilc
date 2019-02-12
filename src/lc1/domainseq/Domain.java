/*     */ package lc1.domainseq;
/*     */ 
/*     */ import com.braju.format.Format;
/*     */ import com.braju.format.Parameters;
/*     */ import java.io.PrintWriter;
/*     */ import lc1.pfam.PfamAlphabet;
/*     */ import org.biojava.bio.seq.Feature.Template;
/*     */ import org.biojava.bio.seq.FeatureHolder;
/*     */ import org.biojava.bio.seq.Sequence;
/*     */ import org.biojava.bio.seq.StrandedFeature.Template;
/*     */ import org.biojava.bio.seq.impl.SimpleStrandedFeature;
/*     */ import org.biojava.bio.symbol.Alphabet;
/*     */ import org.biojava.bio.symbol.IllegalAlphabetException;
/*     */ import org.biojava.bio.symbol.Location;
/*     */ import org.biojava.bio.symbol.LocationTools;
/*     */ import org.biojava.bio.symbol.Symbol;
/*     */ 
/*     */ 
/*     */ 
/*     */ public class Domain
/*     */   extends SimpleStrandedFeature
/*     */ {
/*     */   protected Symbol symbol;
/*  24 */   protected double score = 0.0D;
/*  25 */   protected double evalue = 0.0D;
/*  26 */   protected boolean isMagic = false;
/*     */   protected char mode;
/*     */   
/*     */   public static Domain magicDomain(Sequence sourceSeq, boolean end) throws IllegalAlphabetException {
/*  30 */     return new Domain(sourceSeq, sourceSeq, new MagicTemplate(sourceSeq.getAlphabet(), 
/*  31 */       end ? sourceSeq.length() + 1 : 0));
/*     */   }
/*     */   
/*     */   public Symbol getSymbol() {
/*  35 */     return this.symbol;
/*     */   }
/*     */   
/*     */ 
/*     */   public double getScore()
/*     */   {
/*  41 */     return this.score;
/*     */   }
/*     */   
/*     */   public double getEvalue()
/*     */   {
/*  46 */     return this.evalue;
/*     */   }
/*     */   
/*     */   public void incrScore(double sc) {
/*  50 */     this.score += sc;
/*     */   }
/*     */   
/*     */   public boolean isMagic() {
/*  54 */     return this.isMagic;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Domain(Sequence sourceSeq, FeatureHolder parent, Template template)
/*     */     throws IllegalArgumentException, IllegalAlphabetException
/*     */   {
/*  65 */     super(sourceSeq, parent, template);
/*  66 */     this.symbol = template.symbol;
/*  67 */     this.score = template.score;
/*  68 */     this.evalue = template.evalue;
/*  69 */     this.mode = template.mode;
/*  70 */     this.isMagic = template.isMagic;
/*     */   }
/*     */   
/*     */   public Feature.Template makeTemplate() {
/*  74 */     Template ft = new Template();
/*  75 */     fillTemplate(ft);
/*  76 */     return ft;
/*     */   }
/*     */   
/*     */   protected void fillTemplate(Template ft) {
/*  80 */     super.fillTemplate(ft);
/*  81 */     ft.score = getScore();
/*  82 */     ft.symbol = getSymbol();
/*  83 */     ft.isMagic = this.isMagic;
/*  84 */     ft.evalue = getEvalue();
/*  85 */     ft.mode = this.mode;
/*     */   }
/*     */   
/*     */   public String toString() {
/*  89 */     return getLocation() + " " + getSymbol().getName();
/*     */   }
/*     */   
/*     */   public static class Template
/*     */     extends StrandedFeature.Template
/*     */   {
/*     */     public Symbol symbol;
/*  96 */     public double score = 0.0D;
/*  97 */     public boolean isMagic = false;
/*     */     public double evalue;
/*  99 */     public char mode = 'f';
/*     */     
/*     */     public boolean equals(Object o) {
/* 102 */       if (!(o instanceof Template)) return false;
/* 103 */       Template d = (Template)o;
/*     */       
/*     */ 
/*     */ 
/* 107 */       return (d.symbol == this.symbol) && (d.score == this.score) && (d.mode == this.mode) && (LocationTools.areEqual(d.location, this.location));
/*     */     }
/*     */   }
/*     */   
/*     */   public char mode() {
/* 112 */     return this.mode;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/* 118 */   final String printString = "%10s %10s %5d %5d %5.3g ";
/*     */   
/*     */   public void print(PrintWriter pw) {
/* 121 */     pw.print(Format.sprintf("%10s %10s %5d %5d %5.3g ", 
/* 122 */       new Parameters(getSequence().getName())
/* 123 */       .add(getSymbol().getName())
/* 124 */       .add(getLocation().getMin())
/* 125 */       .add(getLocation().getMax())
/* 126 */       .add(getScore())));
/*     */   }
/*     */   
/*     */   public static class MagicTemplate extends Domain.Template {
/*     */     public MagicTemplate(Alphabet alph, int position) {
/* 131 */       this.location = LocationTools.makeLocation(position, position);
/* 132 */       this.symbol = ((PfamAlphabet)alph).getMagicalState();
/* 133 */       this.isMagic = true;
/*     */     }
/*     */   }
/*     */ }


/* Location:              /home/lachlan/Desktop/psilc1.21/psilc.jar!/lc1/domainseq/Domain.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */