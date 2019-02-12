/*     */ package lc1.dp;
/*     */ 
/*     */ import pal.alignment.SitePattern;
/*     */ import pal.datatype.DataType;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class HmmerProfileHMM
/*     */   extends ProfileHMM
/*     */ {
/*     */   NullModel nullModel;
/*     */   
/*     */   protected HmmerProfileHMM(String name, int columns, DataType dt, int insertRates, int matchRates)
/*     */   {
/*  40 */     super(name, columns, dt, insertRates, matchRates);
/*  41 */     this.nullModel = new NullModel(dt, insertRates);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void set()
/*     */   {
/*  48 */     super.set();
/*  49 */     this.nullModel.set();
/*     */   }
/*     */   
/*     */   public double[] prob(EmissionState emiss, SitePattern sp) {
/*  53 */     byte[] pattern = sp.pattern[0];
/*  54 */     double[] dist = emiss.getDistribution();
/*  55 */     double[] res = new double[pattern.length];
/*  56 */     for (int i = 0; i < res.length; i++) {
/*  57 */       if (pattern[i] == 20) {
/*  58 */         res[i] = 1.0D;
/*     */       }
/*     */       else {
/*  61 */         res[i] = dist[pattern[i]];
/*     */       }
/*     */     }
/*  64 */     double[] result = new double[sp.getSiteCount()];
/*  65 */     for (int i = 0; i < result.length; i++) {
/*  66 */       result[i] = res[sp.alias[i]];
/*     */     }
/*  68 */     return result;
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
/*     */   public synchronized NullModel getNullModel()
/*     */   {
/*  82 */     return this.nullModel;
/*     */   }
/*     */   
/*     */ 
/*     */   public class NullModel
/*     */     extends MarkovModel
/*     */   {
/*     */     EmissionState gState;
/*     */     
/*     */     DotState fState;
/*  92 */     public double gTogTrans = 0.0D;
/*  93 */     public double goTofTrans = 0.0D;
/*     */     
/*     */     public void set() {
/*  96 */       super.set();
/*  97 */       this.goTofTrans = getTransition(this.gState, this.fState);
/*  98 */       this.gTogTrans = getTransition(this.gState, this.gState);
/*     */     }
/*     */     
/*     */     public EmissionState gState()
/*     */     {
/* 103 */       return this.gState;
/*     */     }
/*     */     
/*     */     public DotState fState()
/*     */     {
/* 108 */       return this.fState;
/*     */     }
/*     */     
/*     */     NullModel(DataType dt, int insertRates) {
/* 112 */       super(dt);
/*     */       try {
/* 114 */         this.gState = 
/* 115 */           new EmissionState.InsertState("G", dt, HmmerProfileHMM.this.getInsert(1).getDistribution(), insertRates);
/*     */         
/* 117 */         this.fState = new DotState("F");
/* 118 */         addState(this.gState);addState(this.fState);
/*     */       }
/*     */       catch (Throwable t) {
/* 121 */         t.printStackTrace();
/*     */       }
/*     */     }
/*     */   }
/*     */ }


/* Location:              /home/lachlan/Desktop/psilc1.21/psilc.jar!/lc1/dp/HmmerProfileHMM.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */