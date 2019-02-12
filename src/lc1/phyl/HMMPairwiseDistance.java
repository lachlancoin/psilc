/*     */ package lc1.phyl;
/*     */ 
/*     */ import java.io.Serializable;
/*     */ import pal.alignment.SitePattern;
/*     */ import pal.datatype.DataType;
/*     */ import pal.math.UnivariateMinimum;
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
/*     */ public class HMMPairwiseDistance
/*     */   implements Serializable
/*     */ {
/*     */   public double distance;
/*     */   public double distanceSE;
/*     */   private int numStates;
/*     */   private double jcratio;
/*     */   private SitePattern[] sitePattern;
/*     */   private UnivariateMinimum um;
/*     */   private MultiplePairwiseLikelihood of;
/*     */   
/*     */   public HMMPairwiseDistance(SitePattern[] sp, SubstitutionModel[] substM)
/*     */   {
/*  49 */     this.of = new MultiplePairwiseLikelihood(sp, substM);
/*  50 */     updateSitePattern(sp);
/*  51 */     this.um = new UnivariateMinimum();
/*     */   }
/*     */   
/*     */   public void updateModel(SubstitutionModel[] substM) {
/*  55 */     this.of.updateModel(substM);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void updateSitePattern(SitePattern[] sp)
/*     */   {
/*  65 */     this.sitePattern = sp;
/*  66 */     this.numStates = sp[0].getDataType().getNumStates();
/*  67 */     this.jcratio = ((this.numStates - 1.0D) / this.numStates);
/*  68 */     this.of.updateSitePattern(sp);
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
/*     */   public double getDistance(int s1, int s2)
/*     */   {
/*  84 */     double dist = getObservedDistance(s1, s2);
/*     */     
/*  86 */     if (dist != 0.0D)
/*     */     {
/*     */ 
/*  89 */       double start = 1.0D - dist / this.jcratio;
/*  90 */       if (start > 0.0D)
/*     */       {
/*  92 */         start = -this.jcratio * Math.log(start);
/*     */       }
/*     */       else
/*     */       {
/*  96 */         start = dist;
/*     */       }
/*     */       
/*     */ 
/* 100 */       this.of.setSequences(s1, s2);
/* 101 */       if ((start > 1.0D) || (start < 1.0E-9D))
/*     */       {
/*     */ 
/* 104 */         dist = this.um.findMinimum(this.of, 6);
/*     */ 
/*     */       }
/*     */       else
/*     */       {
/* 109 */         dist = this.um.findMinimum(start, this.of, 6);
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 114 */     double f2x = this.um.f2minx;
/*     */     
/* 116 */     if (1.0D < f2x)
/*     */     {
/* 118 */       this.distanceSE = Math.sqrt(1.0D / f2x);
/*     */     }
/*     */     else
/*     */     {
/* 122 */       this.distanceSE = 1.0D;
/*     */     }
/*     */     
/* 125 */     this.distance = dist;
/* 126 */     return dist;
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
/*     */   private boolean isDifferent(int s1, int s2)
/*     */   {
/* 143 */     if ((s1 == this.numStates) || (s2 == this.numStates))
/*     */     {
/* 145 */       return false;
/*     */     }
/*     */     
/*     */ 
/* 149 */     if (s1 == s2)
/*     */     {
/* 151 */       return false;
/*     */     }
/*     */     
/*     */ 
/* 155 */     return true;
/*     */   }
/*     */   
/*     */   private double getObservedDistance(int s1, int s2) {
/* 159 */     double sum = 0.0D;
/* 160 */     for (int i = 0; i < this.sitePattern.length; i++) {
/* 161 */       sum += getObservedDistance(this.sitePattern[i], s1, s2);
/*     */     }
/* 163 */     return sum / this.sitePattern.length;
/*     */   }
/*     */   
/*     */   private double getObservedDistance(SitePattern sp, int s1, int s2)
/*     */   {
/* 168 */     byte[] seqPat1 = sp.pattern[s1];
/* 169 */     byte[] seqPat2 = sp.pattern[s2];
/* 170 */     int[] weight = sp.weight;
/* 171 */     int diff = 0;
/* 172 */     for (int i = 0; i < sp.numPatterns; i++)
/*     */     {
/* 174 */       if (isDifferent(seqPat1[i], seqPat2[i]))
/*     */       {
/* 176 */         diff += weight[i];
/*     */       }
/*     */     }
/*     */     
/* 180 */     return diff / sp.getSiteCount();
/*     */   }
/*     */ }


/* Location:              /home/lachlan/Desktop/psilc1.21/psilc.jar!/lc1/phyl/HMMPairwiseDistance.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */