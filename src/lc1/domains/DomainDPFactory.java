/*    */ package lc1.domains;
/*    */ 
/*    */ import java.io.File;
/*    */ import org.apache.commons.cli.CommandLine;
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
/*    */ 
/*    */ 
/*    */ public class DomainDPFactory
/*    */ {
/*    */   CommandLine params;
/*    */   String id;
/*    */   TransitionScores model;
/*    */   
/*    */   DomainDPFactory(CommandLine params)
/*    */   {
/*    */     try
/*    */     {
/* 28 */       this.params = params;
/* 29 */       File dir = new File(params.getOptionValue("dir", "."));
/* 30 */       this.model = TransitionScoresFactory.getContextModel(params);
/*    */     }
/*    */     catch (Throwable t) {
/* 33 */       t.printStackTrace();
/*    */     }
/*    */     
/* 36 */     if (params.hasOption("fs")) {
/* 37 */       this.id = "FSDomainDP";
/*    */     } else {
/* 39 */       this.id = "LSDomainDP";
/*    */     }
/*    */   }
/*    */   
/*    */   DomainDPFactory(ContextCount cc, CommandLine params)
/*    */   {
/*    */     try {
/* 46 */       this.params = params;
/*    */       
/*    */ 
/* 49 */       this.model = TransitionScoresFactory.getContextModel(cc, params);
/*    */ 
/*    */     }
/*    */     catch (Throwable t)
/*    */     {
/* 54 */       t.printStackTrace();
/*    */     }
/* 56 */     this.params = params;
/* 57 */     if (params.hasOption("fs")) {
/* 58 */       this.id = "FSDomainDP";
/*    */     } else
/* 60 */       this.id = "LSDomainDP";
/* 61 */     if (params.hasOption("fixed_domain")) {
/* 62 */       this.id = "FixedDomainDP";
/*    */     }
/*    */   }
/*    */   
/*    */   public DomainDP create() {
/*    */     try {
/* 68 */       DomainDP dp = (DomainDP)Class.forName("lc1.domains." + this.id)
/* 69 */         .newInstance();
/* 70 */       dp.setModel(this.model);
/* 71 */       dp.setTable(this.params);
/* 72 */       return dp;
/*    */     } catch (Throwable t) {
/* 74 */       t.printStackTrace(); }
/* 75 */     return null;
/*    */   }
/*    */ }


/* Location:              /home/lachlan/Desktop/psilc1.21/psilc.jar!/lc1/domains/DomainDPFactory.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */