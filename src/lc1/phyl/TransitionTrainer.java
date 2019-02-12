/*    */ package lc1.phyl;
/*    */ 
/*    */ import java.io.File;
/*    */ import java.util.Iterator;
/*    */ import java.util.SortedSet;
/*    */ import java.util.TreeSet;
/*    */ import lc1.dp.ConversionMap;
/*    */ import pal.alignment.Alignment;
/*    */ import pal.alignment.AlignmentUtils;
/*    */ import pal.alignment.ReadAlignment;
/*    */ import pal.datatype.DataType;
/*    */ import pal.datatype.Transitions;
/*    */ import pal.misc.Identifier;
/*    */ import pal.substmodel.RateMatrix;
/*    */ import pal.substmodel.SubstitutionModel;
/*    */ import pal.substmodel.SubstitutionModel.Utils;
/*    */ import pal.substmodel.UniformRate;
/*    */ import pal.tree.Node;
/*    */ import pal.tree.Tree;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class TransitionTrainer
/*    */ {
/*    */   double[] totalFreq;
/*    */   SubstitutionModel substM;
/*    */   
/*    */   TransitionTrainer(File[] alignFiles, char model, double[] startingRates)
/*    */     throws Exception
/*    */   {
/* 39 */     DataType dt = 
/* 40 */       model == 'd' ? Transitions.DELETE_INSTANCE : model == 'm' ? Transitions.MATCH_INSTANCE : 
/* 41 */       Transitions.INSERT_INSTANCE;
/* 42 */     Alignment[] alignments = new Alignment[alignFiles.length];
/* 43 */     this.totalFreq = new double[dt.getNumStates()];
/* 44 */     double totalSites = 0.0D;
/* 45 */     for (int i = 0; i < alignFiles.length; i++) {
/* 46 */       alignments[i] = new ReadAlignment(alignFiles[i].getAbsolutePath());
/*    */       
/* 48 */       double[] freq = AlignmentUtils.estimateFrequencies(alignments[i]);
/* 49 */       double siteCount = alignments[i].getSiteCount() * 
/* 50 */         alignments[i].getSequenceCount();
/* 51 */       totalSites += siteCount;
/* 52 */       for (int j = 0; j < freq.length; j++) {
/* 53 */         this.totalFreq[j] += freq[j] * siteCount;
/*    */       }
/*    */     }
/*    */     
/* 57 */     for (int j = 0; j < this.totalFreq.length; j++) {
/* 58 */       this.totalFreq[j] /= totalSites;
/*    */     }
/* 60 */     RateMatrix rm = null;
/*    */     
/*    */ 
/* 63 */     this.substM = SubstitutionModel.Utils.createSubstitutionModel(rm, new UniformRate());
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public double[] getMatchDistances(Alignment align, Tree tree, char to)
/*    */   {
/* 74 */     double total = 0.0D;
/* 75 */     double no_obs = 0.0D;
/* 76 */     for (int j = 0; j < align.getSiteCount(); j++) {
/* 77 */       SortedSet nodes = new TreeSet(ConversionMap.NODE_COMPARATOR);
/* 78 */       for (int i = 0; i < tree.getExternalNodeCount(); i++) {
/* 79 */         Node n = tree.getExternalNode(i);
/* 80 */         if (align.getData(align.whichIdNumber(
/* 81 */           n.getIdentifier().getName()), j) == to) {
/* 82 */           nodes.add(n);
/*    */         }
/*    */       }
/*    */       
/* 86 */       nodes = ConversionMap.minimumExplanationNodes(nodes);
/* 87 */       for (Iterator it = nodes.iterator(); it.hasNext();) {
/* 88 */         total += ((Node)it.next()).getBranchLength();
/* 89 */         no_obs += 1.0D;
/*    */       }
/*    */     }
/* 92 */     return new double[] { total / no_obs, no_obs };
/*    */   }
/*    */ }


/* Location:              /home/lachlan/Desktop/psilc1.21/psilc.jar!/lc1/phyl/TransitionTrainer.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */