/*    */ package lc1.domains;
/*    */ 
/*    */ import lc1.domainseq.DomainList.SymbolMap;
/*    */ 
/*    */ class Interpolation extends Smoothing
/*    */ {
/*    */   public Smoothing.LogOddsMap makeLogOddsMap(DomainList.SymbolMap smap, boolean top)
/*    */   {
/*  9 */     return new LogOddsMap(smap, top);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   class LogOddsMap
/*    */     extends Smoothing.LogOddsMap
/*    */   {
/*    */     double pseudo_species;
/*    */     
/*    */     double pseudo_context;
/*    */     
/*    */ 
/*    */     LogOddsMap(DomainList.SymbolMap smap, boolean top)
/*    */     {
/* 24 */       super(smap, top);
/* 25 */       this.pseudo_species = (this.lom_1_species == null ? 0.0D : ContextTransitionScores.SPECIES);
/* 26 */       this.pseudo_context = (this.lom_1_context == null ? 0.0D : ContextTransitionScores.CONTEXT);
/*    */     }
/*    */     
/*    */     double condProb() {
/* 30 */       double result = (1.0D - this.pseudo_species) * (this.num2_3 / this.num2 + (
/* 31 */         this.pseudo_context == 0.0D ? 0.0D : this.pseudo_context * this.lom_1_context.condProb()));
/*    */       
/* 33 */       result += 
/* 34 */         (this.pseudo_species == 0.0D ? 0.0D : this.pseudo_species * this.lom_1_species.condProb());
/*    */       
/* 36 */       return result;
/*    */     }
/*    */   }
/*    */ }


/* Location:              /home/lachlan/Desktop/psilc1.21/psilc.jar!/lc1/domains/Interpolation.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */