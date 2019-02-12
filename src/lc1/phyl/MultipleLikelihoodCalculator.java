/*     */ package lc1.phyl;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Set;
/*     */ import java.util.TreeSet;
/*     */ import lc1.util.SheetIO;
/*     */ import pal.alignment.Alignment;
/*     */ import pal.alignment.AlignmentParseException;
/*     */ import pal.alignment.AlignmentUtils;
/*     */ import pal.alignment.ReadAlignment;
/*     */ import pal.alignment.SimpleAlignment;
/*     */ import pal.eval.FastLikelihoodCalculator;
/*     */ import pal.eval.LikelihoodCalculator;
/*     */ import pal.misc.IdGroup;
/*     */ import pal.misc.Identifier;
/*     */ import pal.substmodel.RateMatrix;
/*     */ import pal.substmodel.WAG;
/*     */ import pal.tree.Node;
/*     */ import pal.tree.ReadTree;
/*     */ import pal.tree.Tree;
/*     */ import pal.tree.TreeUtils;
/*     */ 
/*     */ public class MultipleLikelihoodCalculator implements LikelihoodCalculator
/*     */ {
/*     */   FastLikelihoodCalculator[] lhc;
/*     */   
/*     */   public static void main(String[] args) throws Exception
/*     */   {
/*  34 */     Tree t = new ReadTree(args[0]);
/*  35 */     File dir = new File("align");
/*  36 */     Iterator it = SheetIO.read(new File(args[1]), "\t");
/*  37 */     LikelihoodCalculator lh = new MultipleLikelihoodCalculator(t, dir, it);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public MultipleLikelihoodCalculator(Tree t, File dir, Iterator taxon2Alignment)
/*     */   {
/*  45 */     IdGroup leaves = TreeUtils.getLeafIdGroup(t);
/*  46 */     List lhcL = new ArrayList();
/*  47 */     Set alignmentList = new TreeSet();
/*  48 */     while (taxon2Alignment.hasNext()) {
/*  49 */       List row = (List)taxon2Alignment.next();
/*  50 */       String taxon = (String)row.get(0);
/*  51 */       if (leaves.whichIdNumber(taxon) >= 0) {
/*  52 */         String[] alignments = ((String)row.get(1)).split(" ");
/*  53 */         alignmentList.addAll(Arrays.asList(alignments));
/*     */       }
/*     */     }
/*     */     
/*  57 */     int i = 0;
/*  58 */     for (Iterator it = alignmentList.iterator(); (it.hasNext()) && (i < 20);) {
/*     */       try {
/*  60 */         String next = (String)it.next();
/*  61 */         Alignment align = new ReadAlignment(new File(dir, next).getAbsolutePath());
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*  66 */         if (getIntersectingNodes(align, t).length >= align.getIdCount()) {
/*  67 */           RateMatrix rm = new WAG(AlignmentUtils.estimateFrequencies(align));
/*  68 */           align = expandAlignment(align, t);
/*  69 */           FastLikelihoodCalculator lh = 
/*  70 */             new FastLikelihoodCalculator(pal.alignment.SitePattern.getSitePattern(align), t, rm);
/*  71 */           lhcL.add(lh);
/*     */           
/*  73 */           i++;
/*     */         }
/*  75 */       } catch (AlignmentParseException exc) { exc.printStackTrace();
/*     */       }
/*     */       catch (IOException exc) {
/*  78 */         exc.printStackTrace();
/*     */       }
/*     */     }
/*  81 */     this.lhc = new FastLikelihoodCalculator[lhcL.size()];
/*  82 */     lhcL.toArray(this.lhc);
/*     */   }
/*     */   
/*  85 */   public void release() { for (int i = 0; i < this.lhc.length; i++) {
/*  86 */       this.lhc[i].release();
/*     */     }
/*     */   }
/*     */   
/*     */   public double calculateLogLikelihood() {
/*  91 */     double res = 0.0D;
/*  92 */     for (int i = 0; i < this.lhc.length; i++) {
/*  93 */       res += this.lhc[i].calculateLogLikelihood();
/*     */     }
/*  95 */     return res;
/*     */   }
/*     */   
/*     */   public void setRateMatrix(RateMatrix rm) {
/*  99 */     for (int i = 0; i < this.lhc.length; i++) {
/* 100 */       this.lhc[i].setRateMatrix(rm);
/*     */     }
/*     */   }
/*     */   
/*     */   public void setTree(Tree t) {
/* 105 */     for (int i = 0; i < this.lhc.length; i++) {
/* 106 */       this.lhc[i].setTree(t);
/*     */     }
/*     */   }
/*     */   
/*     */   public static Alignment expandAlignment(Alignment align, Tree tree) {
/* 111 */     Identifier[] idL = new Identifier[tree.getExternalNodeCount()];
/* 112 */     String[] seqs = new String[tree.getExternalNodeCount()];
/* 113 */     char[] empty = new char[align.getSiteCount()];
/* 114 */     Arrays.fill(empty, '-');
/* 115 */     String emptyString = new String(empty);
/* 116 */     for (int i = 0; i < tree.getExternalNodeCount(); i++) {
/* 117 */       int j = align.whichIdNumber(tree.getExternalNode(i).getIdentifier().getName());
/* 118 */       idL[i] = tree.getExternalNode(i).getIdentifier();
/* 119 */       if (j < 0) seqs[i] = emptyString; else
/* 120 */         seqs[i] = align.getAlignedSequenceString(j);
/*     */     }
/* 122 */     Alignment align1 = new SimpleAlignment(idL, seqs, align.getDataType());
/* 123 */     return align1;
/*     */   }
/*     */   
/*     */   public static Node[] getIntersectingNodes(IdGroup align, Tree t)
/*     */   {
/* 128 */     List ids = new ArrayList();
/* 129 */     for (int i = 0; i < t.getExternalNodeCount(); i++) {
/* 130 */       int j = align.whichIdNumber(t.getExternalNode(i).getIdentifier().getName());
/* 131 */       if (j >= 0)
/* 132 */         ids.add(t.getExternalNode(i));
/*     */     }
/* 134 */     Node[] idL = new Node[ids.size()];
/* 135 */     ids.toArray(idL);
/* 136 */     return idL;
/*     */   }
/*     */ }


/* Location:              /home/lachlan/Desktop/psilc1.21/psilc.jar!/lc1/phyl/MultipleLikelihoodCalculator.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */