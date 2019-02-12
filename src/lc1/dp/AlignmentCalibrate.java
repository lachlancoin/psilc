/*     */ package lc1.dp;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.PrintStream;
/*     */ import lc1.phyl.AlignUtils;
/*     */ import lc1.phyl.CoalescentTree;
/*     */ import lc1.phyl.MaxLikelihoodTree;
/*     */ import lc1.util.Print;
/*     */ import org.biojava.bio.dist.Distribution;
/*     */ import org.biojava.bio.symbol.IntegerAlphabet.IntegerSymbol;
/*     */ import org.biojava.bio.symbol.SymbolList;
/*     */ import pal.alignment.Alignment;
/*     */ import pal.datatype.AminoAcids;
/*     */ import pal.misc.IdGroup;
/*     */ import pal.misc.SimpleIdGroup;
/*     */ import pal.substmodel.RateMatrix;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ class AlignmentCalibrate
/*     */   extends Calibrate
/*     */ {
/*     */   SubstitutionModel substM;
/*     */   IdGroup idg;
/*     */   File treeDir;
/* 192 */   int i = 0;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   void writeSequence(SymbolList seq, PrintStream op1)
/*     */   {
/* 199 */     Alignment align = null;
/* 200 */     for (int i = 0; i < align.getIdCount(); i++) {
/* 201 */       op1.println(align.getIdentifier(i) + " " + align.getAlignedSequenceString(i));
/*     */     }
/*     */   }
/*     */   
/*     */   public AlignmentCalibrate(Distribution no_sites, Distribution symDist, int no_sequences)
/*     */   {
/* 207 */     super(no_sites, symDist);
/* 208 */     MaxLikelihoodTree.setType("JTT");
/* 209 */     System.err.println("Dist " + Print.toString(symDist));
/* 210 */     this.substM = AlignUtils.getInsertModel(symDist, new AminoAcids(), false, 1.0D);
/* 211 */     System.err.println(Print.toString(this.substM.getRateMatrix().getEquilibriumFrequencies()));
/* 212 */     this.idg = new SimpleIdGroup(no_sequences, true);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   SymbolList generateRandomSequence()
/*     */   {
/* 219 */     int length = ((IntegerAlphabet.IntegerSymbol)this.lengthDist.sampleSymbol()).intValue();
/* 220 */     Tree tree = new CoalescentTree(this.idg);
/*     */     
/* 222 */     SimulatedAlignment simA = new SimulatedAlignment(length, tree, this.substM);
/* 223 */     simA.simulate();
/*     */     
/*     */ 
/* 226 */     return null;
/*     */   }
/*     */ }


/* Location:              /home/lachlan/Desktop/psilc1.21/psilc.jar!/lc1/dp/AlignmentCalibrate.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */