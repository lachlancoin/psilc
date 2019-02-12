/*     */ package lc1.phyl;
/*     */ 
/*     */ import java.io.Serializable;
/*     */ import pal.alignment.SitePattern;
/*     */ import pal.distance.DistanceMatrix;
/*     */ import pal.substmodel.SubstitutionModel;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class HMMAlignmentDistanceMatrix
/*     */   extends DistanceMatrix
/*     */   implements Serializable
/*     */ {
/*     */   private HMMPairwiseDistance pwd;
/*     */   
/*     */   public HMMAlignmentDistanceMatrix(SitePattern[] sp, SubstitutionModel[] m)
/*     */   {
/*  43 */     this(sp, m, true);
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
/*     */   protected HMMAlignmentDistanceMatrix(SitePattern[] sp, SubstitutionModel[] m, boolean immediateCompute)
/*     */   {
/*  59 */     super(new double[sp[0].getSequenceCount()][sp[0].getSequenceCount()], 
/*  60 */       sp[0]);
/*  61 */     if (immediateCompute) {
/*  62 */       computeDistances();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void recompute(SubstitutionModel[] m)
/*     */   {
/*  73 */     this.pwd.updateModel(m);
/*  74 */     computeDistances();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void recompute(SitePattern[] sp)
/*     */   {
/*  84 */     this.pwd.updateSitePattern(sp);
/*  85 */     computeDistances();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private void computeDistances()
/*     */   {
/*  95 */     for (int i = 0; i < getSize(); i++) {
/*  96 */       setDistance(i, i, 0.0D);
/*  97 */       for (int j = i + 1; j < getSize(); j++)
/*     */       {
/*  99 */         this.progress_ = (2 * (i * getSize() + j) / (getSize() * getSize()));
/* 100 */         double d = this.pwd.getDistance(i, j);
/* 101 */         setDistance(i, j, d);
/* 102 */         setDistance(j, i, d);
/*     */       }
/*     */     }
/* 105 */     this.progress_ = -1.0D;
/*     */   }
/*     */   
/*     */   public final double getProgress()
/*     */   {
/* 110 */     return this.progress_;
/*     */   }
/*     */   
/* 113 */   private double progress_ = -1.0D;
/*     */ }


/* Location:              /home/lachlan/Desktop/psilc1.21/psilc.jar!/lc1/phyl/HMMAlignmentDistanceMatrix.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */