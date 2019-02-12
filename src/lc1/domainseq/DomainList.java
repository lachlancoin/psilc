/*     */ package lc1.domainseq;
/*     */ 
/*     */ import java.util.Arrays;
/*     */ import java.util.Comparator;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import lc1.domains.ContextCount;
/*     */ import lc1.pfam.DomainAlphabet;
/*     */ import org.biojava.bio.Annotation;
/*     */ import org.biojava.bio.BioException;
/*     */ import org.biojava.bio.seq.Feature.Template;
/*     */ import org.biojava.bio.seq.FeatureFilter;
/*     */ import org.biojava.bio.seq.FeatureRealizer;
/*     */ import org.biojava.bio.seq.SimpleFeatureHolder;
/*     */ import org.biojava.bio.seq.SimpleFeatureRealizer;
/*     */ import org.biojava.bio.seq.impl.SimpleSequence;
/*     */ import org.biojava.bio.symbol.Symbol;
/*     */ import org.biojava.bio.symbol.SymbolList;
/*     */ import org.biojava.utils.ChangeVetoException;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class DomainList
/*     */   extends SimpleSequence
/*     */ {
/*     */   public static FeatureRealizer domainFeatureRealizer()
/*     */   {
/*  34 */     SimpleFeatureRealizer fr = new SimpleFeatureRealizer();
/*     */     try {
/*  36 */       fr.addImplementation(
/*  37 */         Class.forName("lc1.domainseq.Domain$Template"), 
/*  38 */         Class.forName("lc1.domainseq.Domain"));
/*  39 */       fr.addImplementation(
/*  40 */         Class.forName("lc1.dp.DomainAnnotation$Template"), 
/*  41 */         Class.forName("lc1.dp.DomainAnnotation"));
/*     */     } catch (Exception exc) {
/*  43 */       exc.printStackTrace();
/*     */     }
/*  45 */     return fr;
/*     */   }
/*     */   
/*  48 */   double score = Double.NEGATIVE_INFINITY;
/*     */   private DomainFeatureHolder featureHolder;
/*     */   
/*     */   public DomainList(SymbolList proteinSeq, String urn, String name, Annotation annot) {
/*  52 */     super(proteinSeq, name, urn, annot, domainFeatureRealizer());
/*     */   }
/*     */   
/*     */   public DomainList(SymbolList proteinSeq, String urn, String name, Annotation annot, Feature.Template[] features) throws BioException
/*     */   {
/*  57 */     this(proteinSeq, name, urn, annot);
/*     */     try {
/*  59 */       for (int i = 0; i < features.length; i++)
/*     */       {
/*  61 */         createFeature(features[i]);
/*     */       }
/*     */     } catch (ChangeVetoException exc) {
/*  64 */       BioException exc1 = new BioException("couldn't realize features");
/*  65 */       exc1.initCause(exc);
/*  66 */       throw exc1;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int countFeatures()
/*     */   {
/*  77 */     if (featureHolderAllocated())
/*  78 */       return getFeatureHolder().countFeatures();
/*  79 */     return 0;
/*     */   }
/*     */   
/*     */   class DomainFeatureHolder extends SimpleFeatureHolder {
/*     */     public Iterator features() {
/*  84 */       return getFeatures().iterator();
/*     */     }
/*     */     
/*  87 */     public DomainFeatureHolder() { super(); }
/*     */     
/*     */     void removeAll() {
/*  90 */       getFeatures().removeAll(getFeatures());
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*  95 */   protected boolean featureHolderAllocated() { return this.featureHolder != null; }
/*     */   
/*     */   protected SimpleFeatureHolder getFeatureHolder() {
/*  98 */     if (this.featureHolder == null) {
/*  99 */       this.featureHolder = new DomainFeatureHolder();
/*     */     }
/* 101 */     return this.featureHolder;
/*     */   }
/*     */   
/* 104 */   public void removeAllFeatures() { ((DomainFeatureHolder)getFeatureHolder()).removeAll(); }
/*     */   
/*     */   public void setScore(double sc)
/*     */   {
/* 108 */     this.score = sc;
/*     */   }
/*     */   
/*     */ 
/*     */   public static class SymbolMap
/*     */   {
/*     */     private List sl;
/*     */     private SymbolList species;
/*     */     
/*     */     public List full()
/*     */     {
/* 119 */       return this.sl;
/*     */     }
/*     */     
/*     */     public List context() {
/* 123 */       return this.sl.subList(0, this.sl.size() - 1);
/*     */     }
/*     */     
/*     */     public List symbol() {
/* 127 */       return this.sl.subList(this.sl.size() - 1, this.sl.size());
/*     */     }
/*     */     
/*     */     public boolean equals(Object o) {
/* 131 */       if ((o instanceof SymbolMap)) {
/* 132 */         SymbolMap sm = (SymbolMap)o;
/*     */         
/* 134 */         return (full().equals(sm.full())) && (this.species.equals(sm.species()));
/*     */       }
/* 136 */       return false;
/*     */     }
/*     */     
/*     */     public int hashCode() {
/* 140 */       return this.sl.hashCode() * 11 + 
/* 141 */         this.species.hashCode();
/*     */     }
/*     */     
/*     */     public SymbolList species() {
/* 145 */       return this.species;
/*     */     }
/*     */     
/*     */     public int length() {
/* 149 */       return this.sl.size() - 1;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     public void truncate(int n)
/*     */     {
/* 158 */       this.sl = DomainList.truncate(this.sl, n);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     public void truncate(ContextCount freq)
/*     */     {
/* 165 */       this.sl = freq.longestExtendable(this.sl);
/*     */     }
/*     */     
/*     */     public SymbolMap truncate()
/*     */     {
/* 170 */       return new SymbolMap(this.species, DomainList.truncate(this.sl));
/*     */     }
/*     */     
/*     */     public SymbolMap truncateSpecies() {
/* 174 */       return new SymbolMap(this.species.subList(2, this.species.length()), this.sl);
/*     */     }
/*     */     
/*     */     private SymbolMap(SymbolList species, List sl) {
/* 178 */       this.sl = sl;
/* 179 */       this.species = species;
/*     */     }
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
/*     */     private SymbolMap(SymbolList species, Domain[] domains, DomainAlphabet alph)
/*     */     {
/* 196 */       Symbol[] new_doms = new Symbol[domains.length];
/* 197 */       for (int i = 0; i < new_doms.length; i++) {
/* 198 */         new_doms[i] = domains[i].getSymbol();
/*     */       }
/* 200 */       this.sl = Arrays.asList(new_doms);
/*     */       
/*     */ 
/* 203 */       this.species = species;
/*     */     }
/*     */     
/*     */     public SymbolMap(SymbolList species, Domain[] fullList, int[] contextIndices, int sl_lastIndex, DomainAlphabet alph) {
/* 207 */       Symbol[] new_doms = new Symbol[contextIndices.length + 1];
/* 208 */       for (int i = 0; i < contextIndices.length; i++) {
/* 209 */         Symbol sym = fullList[contextIndices[i]].getSymbol();
/* 210 */         new_doms[i] = (sym.getAnnotation().containsProperty("clan") ? 
/* 211 */           (Symbol)sym.getAnnotation().getProperty("clan") : sym);
/*     */       }
/* 213 */       Symbol sym = fullList[sl_lastIndex].getSymbol();
/* 214 */       new_doms[contextIndices.length] = (sym.getAnnotation().containsProperty("clan") ? 
/* 215 */         (Symbol)sym.getAnnotation().getProperty("clan") : sym);
/*     */       
/* 217 */       this.sl = Arrays.asList(new_doms);
/*     */       
/* 219 */       this.species = species;
/*     */     }
/*     */     
/*     */     private SymbolMap(SymbolList species, Symbol[] domains, DomainAlphabet alph) {
/* 223 */       this.sl = Arrays.asList(domains);
/* 224 */       this.species = species;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static List truncate(List sl)
/*     */   {
/*     */     try
/*     */     {
/* 239 */       int length = sl.size();
/* 240 */       if (length <= 1) {
/* 241 */         throw new Exception("Context has zero length");
/*     */       }
/*     */       
/* 244 */       return sl.subList(1, length);
/*     */     }
/*     */     catch (Throwable t) {
/* 247 */       t.printStackTrace(); }
/* 248 */     return null;
/*     */   }
/*     */   
/*     */   public static List truncate(List sl, int n)
/*     */   {
/*     */     try {
/* 254 */       if ((sl.size() < n) || (n == 0))
/* 255 */         throw new Exception("Context length less than ContextCount or n is 0 " + 
/* 256 */           sl.size() + " " + n);
/* 257 */       if (sl.size() == n) {
/* 258 */         return sl;
/*     */       }
/* 260 */       return 
/* 261 */         sl.subList(sl.size() - n + 1, sl.size());
/*     */ 
/*     */     }
/*     */     catch (Throwable t)
/*     */     {
/* 266 */       t.printStackTrace(); }
/* 267 */     return null;
/*     */   }
/*     */   
/*     */ 
/* 271 */   public static Comparator LENGTH_COMPARATOR = new Comparator() {
/*     */     public int compare(Object dl1, Object dl2) {
/* 273 */       if (((dl1 instanceof SymbolList)) && ((dl2 instanceof SymbolList))) {
/* 274 */         if (((SymbolList)dl1).length() < ((SymbolList)dl2).length()) {
/* 275 */           return -1;
/*     */         }
/* 277 */         if (((SymbolList)dl1).length() > ((SymbolList)dl2).length()) {
/* 278 */           return 1;
/*     */         }
/* 280 */         for (int i = 1; i <= ((SymbolList)dl1).length(); i++) {
/* 281 */           String st1 = ((SymbolList)dl1).symbolAt(i).getName();
/* 282 */           String st2 = ((SymbolList)dl2).symbolAt(i).getName();
/* 283 */           if (!st1.equals(st2))
/*     */           {
/* 285 */             if (st1.equals("!-1"))
/* 286 */               return -1;
/* 287 */             if (st2.equals("!-1"))
/* 288 */               return 1;
/* 289 */             int int1 = Integer.parseInt(st1);
/* 290 */             int int2 = Integer.parseInt(st2);
/* 291 */             if (int1 < int2)
/* 292 */               return -1;
/* 293 */             if (int1 > int2)
/* 294 */               return 1;
/*     */           }
/*     */         }
/*     */       }
/* 298 */       return 0;
/*     */     }
/*     */     
/*     */     public boolean equals(Object obj) {
/* 302 */       return obj == this;
/*     */     }
/*     */   };
/*     */ }


/* Location:              /home/lachlan/Desktop/psilc1.21/psilc.jar!/lc1/domainseq/DomainList.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */