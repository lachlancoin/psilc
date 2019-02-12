/*     */ package lc1.domainseq;
/*     */ 
/*     */ import java.util.Comparator;
/*     */ import java.util.Iterator;
/*     */ import java.util.Set;
/*     */ import java.util.SortedSet;
/*     */ import java.util.TreeSet;
/*     */ import org.biojava.bio.Annotation;
/*     */ import org.biojava.bio.BioException;
/*     */ import org.biojava.bio.seq.Feature;
/*     */ import org.biojava.bio.seq.FeatureFilter;
/*     */ import org.biojava.bio.seq.FeatureFilter.ByClass;
/*     */ import org.biojava.bio.seq.FeatureHolder;
/*     */ import org.biojava.bio.symbol.Location;
/*     */ import org.biojava.bio.symbol.Symbol;
/*     */ import org.biojava.utils.ChangeVetoException;
/*     */ 
/*     */ 
/*     */ public class FeatureUtils
/*     */ {
/*  21 */   public static Comparator END_INCREASING = new Comparator() {
/*     */     public int compare(Object o1, Object o2) {
/*  23 */       Location l1 = ((Feature)o1).getLocation();
/*  24 */       Location l2 = ((Feature)o2).getLocation();
/*  25 */       if (l1.getMax() < l2.getMax())
/*  26 */         return -1;
/*  27 */       if (l1.getMax() > l2.getMax())
/*  28 */         return 1;
/*  29 */       if (l1.getMin() < l2.getMin())
/*  30 */         return -1;
/*  31 */       if (l1.getMin() > l2.getMin())
/*  32 */         return 1;
/*  33 */       if (o1.hashCode() < o2.hashCode())
/*  34 */         return -1;
/*  35 */       if (o1.hashCode() > o2.hashCode()) {
/*  36 */         return 1;
/*     */       }
/*  38 */       return 0;
/*     */     }
/*     */     
/*     */     public boolean equals(Object o) {
/*  42 */       return o == this;
/*     */     }
/*     */   };
/*     */   
/*  46 */   public static Comparator END_DECREASING = new Comparator() {
/*     */     public int compare(Object o1, Object o2) {
/*  48 */       Location l1 = ((Feature)o1).getLocation();
/*  49 */       Location l2 = ((Feature)o2).getLocation();
/*  50 */       return -1 * FeatureUtils.END_INCREASING.compare(o1, o2);
/*     */     }
/*     */     
/*     */     public boolean equals(Object o) {
/*  54 */       return o == this;
/*     */     }
/*     */   };
/*     */   
/*  58 */   public static final FeatureFilter NOVEL_FILTER = new FeatureFilter() {
/*     */     public boolean accept(Feature d) {
/*  60 */       if (((d instanceof Domain)) && 
/*  61 */         (d.getAnnotation().containsProperty("contextTrue")) && 
/*  62 */         (d.getAnnotation().getProperty("contextTrue").equals(
/*  63 */         Boolean.TRUE))) {
/*  64 */         if ((d.getAnnotation().containsProperty("wasTrue")) && 
/*  65 */           (d.getAnnotation().getProperty("wasTrue").equals(
/*  66 */           Boolean.TRUE))) {
/*  67 */           if (d.getAnnotation().containsProperty(
/*  68 */             "failedSequenceScore")) {
/*  69 */             if ((d.getAnnotation().getProperty("failedSequenceScore").equals(
/*  70 */               Boolean.FALSE)) && (!((Domain)d).isMagic())) return true;
/*     */           }
/*     */         }
/*     */       }
/*  60 */       return 
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  70 */         false;
/*     */     }
/*     */   };
/*     */   
/*     */ 
/*     */   public static boolean contains(FeatureHolder dl, Symbol sym, int total)
/*     */   {
/*  77 */     int count = 0;
/*  78 */     FeatureHolder fh = dl.filter(DOMAIN_FILTER);
/*  79 */     Iterator it = fh.features();
/*  80 */     while (it.hasNext()) {
/*  81 */       if (((Domain)it.next()).getSymbol().equals(sym)) {}
/*     */       
/*  83 */       count++;
/*  84 */       if (count >= total)
/*  85 */         return true;
/*     */     }
/*  87 */     return false;
/*     */   }
/*     */   
/*  90 */   public static final FeatureFilter FS_REJECTS_FILTER = new FeatureFilter() {
/*     */     public boolean accept(Feature d) {
/*  92 */       if (((d instanceof Domain)) && 
/*  93 */         (d.getAnnotation().containsProperty(
/*  94 */         "failedSequenceScore"))) {
/*  95 */         if (d.getAnnotation().getProperty("failedSequenceScore").equals(
/*  96 */           Boolean.FALSE)) {
/*  97 */           if ((d.getAnnotation().containsProperty("hasFS")) && 
/*  98 */             (d.getAnnotation().getProperty("hasFS").equals(Boolean.TRUE))) return true;
/*     */         }
/*     */       }
/*  92 */       return 
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  98 */         false;
/*     */     }
/*     */   };
/*     */   
/* 102 */   public static final FeatureFilter SEQUENCE_SCORE_FILTER = new FeatureFilter() {
/*     */     public boolean accept(Feature d) {
/* 104 */       if (((d instanceof Domain)) && 
/* 105 */         (d.getAnnotation().containsProperty(
/* 106 */         "failedSequenceScore"))) {
/* 107 */         if ((d.getAnnotation().getProperty("failedSequenceScore").equals(
/* 108 */           Boolean.TRUE)) && (!((Domain)d).isMagic())) return true;
/*     */       }
/* 104 */       return 
/*     */       
/*     */ 
/*     */ 
/* 108 */         false;
/*     */     }
/*     */   };
/*     */   
/* 112 */   public static final FeatureFilter SEQUENCE_SCORE_FILTER1 = new FeatureFilter() {
/*     */     public boolean accept(Feature d) {
/* 114 */       if (((d instanceof Domain)) && 
/* 115 */         (((Domain)d).getScore() > 0.0D) && 
/* 116 */         (d.getAnnotation().containsProperty(
/* 117 */         "failedSequenceScore"))) {
/* 118 */         if ((d.getAnnotation().getProperty("failedSequenceScore").equals(
/* 119 */           Boolean.FALSE)) && (!((Domain)d).isMagic())) return true;
/*     */       }
/* 114 */       return 
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 119 */         false;
/*     */     }
/*     */   };
/*     */   
/* 123 */   public static final FeatureFilter NEW_NEGATIVE_FILTER = new FeatureFilter() {
/*     */     public boolean accept(Feature d) {
/* 125 */       if (((d instanceof Domain)) && 
/* 126 */         (d.getAnnotation().containsProperty("wasTrue")) && 
/* 127 */         (d.getAnnotation().getProperty("wasTrue").equals(
/* 128 */         Boolean.TRUE))) {
/* 129 */         if ((d.getAnnotation().containsProperty("ContextTrue")) && 
/* 130 */           (d.getAnnotation().getProperty("ContextTrue").equals(
/* 131 */           Boolean.TRUE)) && (!((Domain)d).isMagic())) return true;
/*     */       }
/* 125 */       return 
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 131 */         false;
/*     */     }
/*     */   };
/*     */   
/* 135 */   public static final FeatureFilter DOMAIN_FILTER = new FeatureFilter() {
/*     */     public boolean accept(Feature d) {
/* 137 */       return d instanceof Domain;
/*     */     }
/*     */   };
/*     */   
/* 141 */   public static final FeatureFilter NON_SCORABLE_FILTER = new FeatureFilter() {
/*     */     public boolean accept(Feature d) {
/* 143 */       return !(d instanceof Domain);
/*     */     }
/*     */   };
/*     */   
/*     */   public static FeatureFilter lessThan(Feature feat, Comparator c) {
/* 148 */     new FeatureFilter() {
/*     */       public boolean accept(Feature d) {
/* 150 */         return FeatureUtils.this.compare(d, this.val$feat) == -1;
/*     */       }
/*     */     };
/*     */   }
/*     */   
/*     */   public static FeatureFilter sameSymbol(Symbol sym) {
/* 156 */     new FeatureFilter() {
/*     */       public boolean accept(Feature d) {
/* 158 */         return ((Domain)d).getSymbol() == FeatureUtils.this;
/*     */       }
/*     */     };
/*     */   }
/*     */   
/*     */   public static FeatureFilter lessThanOrEqual(Feature feat, Comparator c)
/*     */   {
/* 165 */     new FeatureFilter() {
/*     */       public boolean accept(Feature d) {
/* 167 */         return FeatureUtils.this.compare(d, this.val$feat) <= 0;
/*     */       }
/*     */     };
/*     */   }
/*     */   
/*     */   public static Comparator getReverseComparator(Comparator comp) {
/* 173 */     new Comparator() {
/*     */       public int compare(Object o1, Object o2) {
/* 175 */         return -1 * FeatureUtils.this.compare(o1, o2);
/*     */       }
/*     */       
/*     */       public boolean equals(Object obj) {
/* 179 */         return this == obj;
/*     */       }
/*     */     };
/*     */   }
/*     */   
/*     */   public static Set getFeatureSet(FeatureHolder fHolder, Set set)
/*     */   {
/*     */     try {
/* 187 */       FeatureHolder fh = fHolder.filter(new FeatureFilter.ByClass(
/* 188 */         Class.forName("lc1.domainseq.Domain")));
/*     */       
/*     */ 
/* 191 */       for (Iterator it = fh.features(); it.hasNext();) {
/* 192 */         set.add(it.next());
/*     */       }
/*     */     }
/*     */     catch (ClassNotFoundException exc) {
/* 196 */       exc.printStackTrace();
/*     */     }
/* 198 */     return set;
/*     */   }
/*     */   
/*     */   public static Iterator iterator(FeatureHolder fHolder, Comparator c, int chunks)
/*     */   {
/*     */     try {
/* 204 */       if (chunks <= 0) throw new Exception("chunks must be >=1");
/* 205 */       SortedSet set = new TreeSet(c);
/* 206 */       getFeatureSet(fHolder, set);
/* 207 */       Iterator it1 = set.iterator();
/* 208 */       if (chunks == 1) {
/* 209 */         return it1;
/*     */       }
/* 211 */       new Iterator()
/*     */       {
/*     */         Symbol[] feat;
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         public boolean hasNext()
/*     */         {
/* 222 */           return this.val$it1.hasNext();
/*     */         }
/*     */         
/*     */         public Object next() {
/* 226 */           Symbol[] result = new Symbol[this.val$chunks];
/* 227 */           System.arraycopy(this.feat, 1, result, 0, result.length - 1);
/* 228 */           result[(result.length - 1)] = ((Domain)this.val$it1.next()).getSymbol();
/* 229 */           this.feat = result;
/* 230 */           return result;
/*     */         }
/*     */         
/*     */ 
/*     */         public void remove() {}
/*     */       };
/*     */     }
/*     */     catch (Exception exc)
/*     */     {
/* 239 */       exc.printStackTrace(); }
/* 240 */     return null;
/*     */   }
/*     */   
/*     */   public static FeatureHolder preceding(FeatureHolder fh, Feature f)
/*     */   {
/* 245 */     FeatureFilter preceding = new FeatureFilter() {
/*     */       public boolean accept(Feature d) {
/* 247 */         return FeatureUtils.this.getLocation().getMax() > d.getLocation().getMax();
/*     */       }
/* 249 */     };
/* 250 */     return fh.filter(preceding);
/*     */   }
/*     */   
/*     */   public static void merge(FeatureHolder fh1, FeatureHolder fh2)
/*     */     throws BioException, ChangeVetoException
/*     */   {
/* 256 */     for (Iterator it = fh2.features(); it.hasNext();) {
/* 257 */       fh1.createFeature(((Feature)it.next()).makeTemplate());
/*     */     }
/*     */   }
/*     */ }


/* Location:              /home/lachlan/Desktop/psilc1.21/psilc.jar!/lc1/domainseq/FeatureUtils.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */