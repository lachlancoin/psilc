/*     */ package lc1.phyl;
/*     */ 
/*     */ import pal.alignment.SitePattern;
/*     */ import pal.distance.SequencePairLikelihood;
/*     */ import pal.math.UnivariateFunction;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ class MultiplePairwiseLikelihood
/*     */   implements UnivariateFunction
/*     */ {
/*     */   SequencePairLikelihood[] spl;
/*     */   
/*     */   public MultiplePairwiseLikelihood(SitePattern[] sp, SubstitutionModel[] model)
/*     */   {
/* 191 */     this.spl = new SequencePairLikelihood[model.length];
/* 192 */     for (int i = 0; i < this.spl.length; i++) {
/* 193 */       this.spl[i] = new SequencePairLikelihood(sp[i], model[i]);
/*     */     }
/*     */   }
/*     */   
/*     */   public void updateModel(SubstitutionModel[] model) {
/* 198 */     for (int i = 0; i < model.length; i++) {
/* 199 */       this.spl[i].updateModel(model[i]);
/*     */     }
/*     */   }
/*     */   
/*     */   public double evaluate(double x) {
/* 204 */     double sum = 0.0D;
/* 205 */     for (int i = 0; i < this.spl.length; i++) {
/* 206 */       sum += this.spl[i].evaluate(x);
/*     */     }
/* 208 */     return sum;
/*     */   }
/*     */   
/*     */   public double getLowerBound() {
/* 212 */     return this.spl[0].getLowerBound();
/*     */   }
/*     */   
/*     */   public double getUpperBound() {
/* 216 */     return this.spl[0].getUpperBound();
/*     */   }
/*     */   
/*     */   public void updateSitePattern(SitePattern[] sp) {
/* 220 */     for (int i = 0; i < this.spl.length; i++) {
/* 221 */       this.spl[i].updateSitePattern(sp[i]);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setSequences(int s1, int s2)
/*     */   {
/* 233 */     for (int i = 0; i < this.spl.length; i++) {}
/*     */   }
/*     */ }


/* Location:              /home/lachlan/Desktop/psilc1.21/psilc.jar!/lc1/phyl/MultiplePairwiseLikelihood.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */