/*     */ package lc1.domainseq;
/*     */ 
/*     */ import java.io.BufferedReader;
/*     */ import java.io.IOException;
/*     */ import java.io.PrintStream;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Comparator;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import lc1.dp.Combinatorics;
/*     */ import lc1.pfam.DomainAlphabet;
/*     */ import org.biojava.bio.Annotation;
/*     */ import org.biojava.bio.seq.Feature;
/*     */ import org.biojava.bio.seq.FeatureHolder;
/*     */ import org.biojava.bio.seq.Sequence;
/*     */ import org.biojava.bio.seq.SequenceIterator;
/*     */ import org.biojava.bio.seq.db.SequenceDB;
/*     */ import org.biojava.bio.symbol.Location;
/*     */ import org.biojava.bio.symbol.LocationTools;
/*     */ import org.biojava.bio.symbol.Symbol;
/*     */ import org.biojava.bio.symbol.SymbolList;
/*     */ import org.biojava.utils.ChangeVetoException;
/*     */ 
/*     */ public class DomainListUtilities
/*     */ {
/*     */   public static SequenceIterator readPfamFile(BufferedReader in, DomainAlphabet alph, SequenceDB prots)
/*     */   {
/*  31 */     new SequenceIterator()
/*     */     {
/*     */       String s;
/*     */       
/*     */ 
/*     */       String[] nextRow;
/*     */       
/*     */ 
/*     */       char slash;
/*     */       
/*     */ 
/*     */       String split;
/*     */       
/*     */ 
/*     */       boolean hasNext;
/*     */       
/*     */ 
/*     */ 
/*     */       public Sequence nextSequence()
/*     */       {
/*     */         try
/*     */         {
/*  53 */           List sh = new ArrayList();
/*  54 */           Object[] obj = DomainListUtilities.transform(this.nextRow);
/*  55 */           sh.add(obj);
/*     */           
/*  57 */           while (((this.s = DomainListUtilities.this.readLine()) != null) && (this.s.startsWith(this.nextRow[0]))) {
/*  58 */             String[] row1 = this.s.split(this.split);
/*  59 */             Object[] obj1 = DomainListUtilities.transform(row1);
/*  60 */             sh.add(obj1);
/*     */           }
/*  62 */           if (this.s == null) {
/*  63 */             this.hasNext = false;
/*     */           }
/*     */           else {
/*  66 */             this.nextRow = this.s.split(this.split);
/*     */           }
/*  68 */           return lc1.pfam.PfamDB.buildList(sh, this.val$prots.getSequence(this.nextRow[0]), this.val$alph);
/*     */         }
/*     */         catch (Exception ioe) {
/*  71 */           ioe.printStackTrace(); }
/*  72 */         return null;
/*     */       }
/*     */       
/*     */       public boolean hasNext()
/*     */       {
/*  77 */         return this.hasNext;
/*     */       }
/*     */     };
/*     */   }
/*     */   
/*     */   private static Object[] transform(String[] row)
/*     */   {
/*  84 */     Object[] obj = new Object[row.length];
/*  85 */     System.arraycopy(row, 0, obj, 0, row.length);
/*  86 */     obj[2] = new Integer(Integer.parseInt(row[2]));
/*  87 */     obj[3] = new Integer(Integer.parseInt(row[3]));
/*  88 */     obj[4] = new Float(Double.parseDouble(row[4]));
/*  89 */     obj[6] = new Integer(Integer.parseInt(row[6]));
/*  90 */     obj[8] = new Float(Double.parseDouble(row[8]));
/*  91 */     return obj;
/*     */   }
/*     */   
/*  94 */   public static final Comparator LENGTH_COMPARATOR = new Comparator() {
/*     */     public int compare(Object o1, Object o2) {
/*  96 */       if (((o1 instanceof DomainList)) && ((o2 instanceof DomainList))) {
/*  97 */         DomainList s1 = (DomainList)o1;
/*  98 */         DomainList s2 = (DomainList)o2;
/*  99 */         if (s1.countFeatures() < s2.countFeatures()) return -1;
/* 100 */         if (s1.countFeatures() > s2.countFeatures()) return 1;
/*     */       }
/* 102 */       return o1.hashCode() > o2.hashCode() ? 1 : o1.hashCode() < o2.hashCode() ? -1 : 0;
/*     */     }
/*     */     
/*     */     public boolean equals(Object o) {
/* 106 */       return this == o;
/*     */     }
/*     */   };
/*     */   
/*     */   public static void annotateNovel(DomainList newL, DomainList oldL)
/*     */   {
/*     */     try
/*     */     {
/* 114 */       Map newS = symbolMap(newL);
/* 115 */       Map oldS = symbolMap(oldL);
/* 116 */       for (Iterator it = newS.keySet().iterator(); it.hasNext();) {
/* 117 */         Object obj = it.next();
/* 118 */         if (!oldS.keySet().contains(obj))
/* 119 */           ((Domain)newS.get(obj)).getAnnotation().setProperty("novel", new Boolean(true));
/*     */       }
/*     */     } catch (ChangeVetoException t) {
/* 122 */       t.printStackTrace();
/*     */     }
/*     */   }
/*     */   
/* 126 */   public static Map symbolMap(DomainList dl) { Map m = new HashMap();
/* 127 */     FeatureHolder fh = dl.filter(FeatureUtils.DOMAIN_FILTER);
/* 128 */     for (Iterator it = FeatureUtils.iterator(fh, FeatureUtils.END_INCREASING, 1); it.hasNext();) {
/* 129 */       Domain d = (Domain)it.next();
/* 130 */       Object[] o = { d.getSymbol(), new Integer(d.getLocation().getMin()) };
/* 131 */       m.put(o, d);
/*     */     }
/* 133 */     return m;
/*     */   }
/*     */   
/*     */   public static String seqString(SymbolList dl) {
/* 137 */     Iterator it = dl.iterator();
/* 138 */     String st = new String();
/* 139 */     while (it.hasNext()) {
/* 140 */       st = st + " " + ((Symbol)it.next()).getAnnotation().getProperty("Name");
/*     */     }
/* 142 */     return st;
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
/*     */   public static int residuesCovered(Sequence[] dl)
/*     */   {
/* 200 */     Location loc = null;
/* 201 */     for (int i = 0; i < dl.length; i++) {
/* 202 */       if ((dl[i] != null) && (dl[i].length() != 0))
/* 203 */         for (Iterator it = dl[i].features(); it.hasNext();) {
/* 204 */           Domain dom = (Domain)it.next();
/* 205 */           Location locInner = dom.getLocation();
/* 206 */           if (loc == null) loc = locInner; else
/* 207 */             loc = LocationTools.union(locInner, loc);
/*     */         }
/*     */     }
/* 210 */     int count = 0;
/* 211 */     if (loc == null) return 0;
/* 212 */     for (Iterator it = loc.blockIterator(); it.hasNext();) {
/* 213 */       Location locInner = (Location)it.next();
/* 214 */       count += locInner.getMax() - locInner.getMin() + 1;
/*     */     }
/* 216 */     return count;
/*     */   }
/*     */   
/*     */   public static Feature containsOverlap(FeatureHolder dl, Feature sym)
/*     */   {
/* 221 */     Iterator it = dl.features();
/* 222 */     Location loc = sym.getLocation();
/* 223 */     while (it.hasNext()) {
/* 224 */       Feature next = (Feature)it.next();
/* 225 */       Location locN = next.getLocation();
/* 226 */       if (locN.overlaps(loc))
/*     */       {
/* 228 */         return next;
/*     */       }
/*     */     }
/* 231 */     return null;
/*     */   }
/*     */   
/*     */   public static List getOverlaps(FeatureHolder dl, Feature sym) {
/* 235 */     List l = new ArrayList();
/* 236 */     Location loc = sym.getLocation();
/* 237 */     Iterator it = dl.features();
/* 238 */     while (it.hasNext()) {
/* 239 */       Feature next = (Feature)it.next();
/* 240 */       Location lN = next.getLocation();
/*     */       
/* 242 */       if (loc.overlaps(lN)) {
/* 243 */         l.add(next);
/*     */       }
/*     */     }
/* 246 */     return l;
/*     */   }
/*     */   
/*     */   public static boolean simLengths(DomainList bList1, DomainList bList)
/*     */   {
/* 251 */     if (Math.abs((bList1.length() - bList.length()) / bList1.length()) > 0.2D) {
/* 252 */       return false;
/*     */     }
/* 254 */     return true;
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
/*     */   public static Iterator subListSymbolMapIterator(Symbol[] symL, int i)
/*     */   {
/* 285 */     new Iterator() {
/*     */       Iterator it1;
/*     */       
/* 288 */       public boolean hasNext() { return this.it1.hasNext(); }
/*     */       
/*     */       public Object next() {
/* 291 */         int[] pos1 = (int[])this.it1.next();
/*     */         
/* 293 */         Symbol[] sym = new Symbol[pos1.length];
/* 294 */         for (int i = 0; i < pos1.length; i++) {
/* 295 */           sym[i] = DomainListUtilities.this[(pos1[i] - 1)];
/*     */         }
/* 297 */         return sym;
/*     */       }
/*     */       
/*     */ 
/*     */       public void remove() {}
/*     */     };
/*     */   }
/*     */   
/*     */ 
/*     */   public static SequenceIterator addStartAndEndDomains(SequenceIterator iterator)
/*     */   {
/* 308 */     new SequenceIterator() {
/*     */       public boolean hasNext() {
/* 310 */         return DomainListUtilities.this.hasNext();
/*     */       }
/*     */       
/*     */       public Sequence nextSequence()
/*     */       {
/*     */         try {
/* 316 */           Sequence seq = DomainListUtilities.this.nextSequence();
/* 317 */           seq.createFeature(new Domain.MagicTemplate(seq.getAlphabet(), 0));
/* 318 */           seq.createFeature(new Domain.MagicTemplate(seq.getAlphabet(), seq.length()));
/* 319 */           return seq;
/*     */         } catch (Exception exc) {
/* 321 */           exc.printStackTrace();
/* 322 */           System.err.println("couldn't create feature");
/*     */         }
/* 324 */         return null;
/*     */       }
/*     */     };
/*     */   }
/*     */ }


/* Location:              /home/lachlan/Desktop/psilc1.21/psilc.jar!/lc1/domainseq/DomainListUtilities.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */