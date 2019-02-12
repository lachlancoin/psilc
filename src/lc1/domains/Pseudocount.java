/*    */ package lc1.domains;
/*    */ 
/*    */ import lc1.domainseq.DomainList.SymbolMap;
/*    */ 
/*    */ class Pseudocount
/*    */   extends Smoothing
/*    */ {
/*    */   public Smoothing.LogOddsMap makeLogOddsMap(DomainList.SymbolMap smap, boolean top)
/*    */   {
/* 10 */     return new LogOddsMap(smap, top);
/*    */   }
/*    */   
/*    */ 
/*    */   class LogOddsMap
/*    */     extends Smoothing.LogOddsMap
/*    */   {
/*    */     double pseudo_context;
/*    */     
/*    */     double pseudo_species;
/*    */     
/*    */     double pseudo;
/*    */     
/*    */     LogOddsMap(DomainList.SymbolMap smap, boolean top)
/*    */     {
/* 25 */       super(smap, top);
/*    */       
/* 27 */       this.pseudo_context = (this.lom_1_context == null ? 0.0D : ContextTransitionScores.CONTEXT * this.num2);
/* 28 */       this.pseudo_species = (this.lom_1_species == null ? 0.0D : ContextTransitionScores.SPECIES * this.num2);
/* 29 */       this.pseudo = (this.pseudo_context + this.pseudo_species);
/*    */     }
/*    */     
/*    */     double condProb() {
/*    */       double result;
/*    */       double result;
/* 35 */       if (this.lom_1_species == null) { double result;
/* 36 */         if (this.lom_1_context == null) {
/* 37 */           result = this.num2_3 / this.num2;
/*    */         } else {
/* 39 */           result = (this.num2_3 + this.pseudo_context * this.lom_1_context.condProb()) / (this.pseudo_context + this.num2);
/*    */         }
/*    */       } else {
/*    */         double result;
/* 43 */         if (this.lom_1_context == null) {
/* 44 */           result = (this.num2_3 + this.pseudo_species * this.lom_1_species.condProb()) / (this.pseudo_species + this.num2);
/*    */         } else
/* 46 */           result = (this.num2_3 + this.pseudo_context * this.lom_1_context.condProb() + this.pseudo_species * this.lom_1_species.condProb()) / (this.pseudo + this.num2);
/*    */       }
/* 48 */       return result;
/*    */     }
/*    */   }
/*    */ }


/* Location:              /home/lachlan/Desktop/psilc1.21/psilc.jar!/lc1/domains/Pseudocount.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */