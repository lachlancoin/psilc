/*     */ package lc1.domains;
/*     */ 
/*     */ import java.util.List;
/*     */ import lc1.domainseq.DomainList.SymbolMap;
/*     */ import lc1.pfam.SpeciesAlphabet;
/*     */ import org.biojava.bio.symbol.SymbolList;
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class Smoothing
/*     */ {
/*     */   public ContextCount freq;
/*     */   public static final int ContextCount = 5;
/*     */   
/*     */   public abstract LogOddsMap makeLogOddsMap(DomainList.SymbolMap paramSymbolMap, boolean paramBoolean);
/*     */   
/*     */   abstract class LogOddsMap
/*     */   {
/*     */     protected int num2;
/*     */     protected int num2_3;
/*     */     protected int num3;
/*     */     protected int total;
/*     */     protected LogOddsMap lom_1_context;
/*     */     protected LogOddsMap lom_1_species;
/*     */     
/*     */     LogOddsMap(DomainList.SymbolMap smap, boolean top)
/*     */       throws ArithmeticException
/*     */     {
/*     */       try
/*     */       {
/*  31 */         if (top)
/*     */         {
/*     */ 
/*     */ 
/*  35 */           smap.truncate(Smoothing.this.freq);
/*     */         }
/*     */         
/*  38 */         if (smap.length() > 1)
/*     */         {
/*  40 */           this.lom_1_context = Smoothing.this.makeLogOddsMap(smap.truncate(), false);
/*     */         }
/*     */         
/*  43 */         if (smap.species().length() > 1) {
/*  44 */           this.lom_1_species = Smoothing.this.makeLogOddsMap(smap.truncateSpecies(), false);
/*     */         }
/*  46 */         this.total = Smoothing.this.freq.getTotal();
/*  47 */         this.num3 = Smoothing.this.freq.getCount(Smoothing.this.freq.getSpeciesAlphabet().root(), smap.symbol());
/*     */         
/*  49 */         if (this.num3 == 0) {
/*  50 */           throw new ArithmeticException(
/*  51 */             "no count for symbol " + smap.symbol() + " in species " + smap.species().symbolAt(1));
/*     */         }
/*  53 */         if (this.total == 0) {
/*  54 */           throw new ArithmeticException(
/*  55 */             "total is zero" + smap.symbol().get(0) + " in species " + smap.species().symbolAt(1));
/*     */         }
/*     */         
/*     */ 
/*  59 */         this.num2_3 = Smoothing.this.freq.getCount(smap.species().symbolAt(1), smap.full());
/*  60 */         this.num2 = (this.num2_3 == 0 ? 1 : Smoothing.this.freq.getCount(smap.species().symbolAt(smap.species().length()), smap.context()));
/*  61 */         if ((this.num2 == 0) && (this.num2_3 != 0)) throw new RuntimeException(this.num2_3 + " is zero " + 
/*  62 */             this.num2 + "\n" + smap.context() + "\n" + smap.full());
/*     */       }
/*     */       catch (Throwable t) {
/*  65 */         t.printStackTrace();
/*  66 */         System.exit(0);
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     abstract double condProb();
/*     */     
/*     */ 
/*     */ 
/*     */     double score()
/*     */     {
/*  78 */       double rProb = this.num3 / this.total;
/*  79 */       double condProb = condProb();
/*  80 */       if (condProb == 0.0D) throw new RuntimeException("cond prob is zero ");
/*  81 */       if (rProb == 0.0D) throw new RuntimeException("r prob is zero ");
/*  82 */       if (Double.isInfinite(condProb)) throw new RuntimeException("cond prob is infinite ");
/*  83 */       if (Double.isInfinite(rProb)) throw new RuntimeException("r prob is zero ");
/*  84 */       return condProb / rProb;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     public boolean equals(Object m)
/*     */     {
/*  95 */       return ((m instanceof LogOddsMap)) && (this.num2_3 == ((LogOddsMap)m).num2_3) && (this.num2 == ((LogOddsMap)m).num2) && (this.num3 == ((LogOddsMap)m).num3) && ((this.lom_1_context == ((LogOddsMap)m).lom_1_context) || (this.lom_1_context.equals(((LogOddsMap)m).lom_1_context))) && ((this.lom_1_species == ((LogOddsMap)m).lom_1_species) || (this.lom_1_species.equals(((LogOddsMap)m).lom_1_species)));
/*     */     }
/*     */     
/*     */     public int hashCode()
/*     */     {
/* 100 */       return this.num2_3;
/*     */     }
/*     */     
/*     */     public String toString() {
/* 104 */       String str = new String();
/* 105 */       str = str + this.num3 + " " + this.num2 + " " + this.num2_3;
/* 106 */       return str;
/*     */     }
/*     */   }
/*     */ }


/* Location:              /home/lachlan/Desktop/psilc1.21/psilc.jar!/lc1/domains/Smoothing.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */