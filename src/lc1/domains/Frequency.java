/*     */ package lc1.domains;
/*     */ 
/*     */ import gnu.trove.TIntProcedure;
/*     */ import gnu.trove.TObjectIntHashMap;
/*     */ import java.io.Serializable;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Iterator;
/*     */ import lc1.pfam.DomainAlphabet;
/*     */ import lc1.pfam.SpeciesAlphabet;
/*     */ import org.biojava.bio.dist.Distribution;
/*     */ import org.biojava.bio.dist.DistributionFactory;
/*     */ import org.biojava.bio.symbol.Symbol;
/*     */ import org.biojava.bio.symbol.SymbolList;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class Frequency
/*     */   implements Serializable
/*     */ {
/*     */   public static SpeciesAlphabet spec_al;
/*  34 */   private TObjectIntHashMap count = new TObjectIntHashMap();
/*     */   
/*     */   public void put(Object obj, int cnt) {
/*  37 */     this.count.put(obj, cnt);
/*     */   }
/*     */   
/*     */   public Object[] keys() {
/*  41 */     return this.count.keys();
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
/*     */   public int getImpl(Symbol obj)
/*     */   {
/*  55 */     return this.count.get(obj);
/*     */   }
/*     */   
/*     */   public int get(Symbol obj) {
/*  59 */     return this.count.get(obj);
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
/*  83 */   Integer total = null;
/*     */   
/*  85 */   public int total() { if (this.total != null) return this.total.intValue();
/*  86 */     int[] tot = new int[1];
/*  87 */     this.count.forEachValue(new TIntProcedure() {
/*     */       public boolean execute(int c) {
/*  89 */         this.val$tot[0] += c;
/*  90 */         return true;
/*     */       }
/*  92 */     });
/*  93 */     this.total = new Integer(tot[0]);
/*  94 */     return tot[0];
/*     */   }
/*     */   
/*     */   public String toString() {
/*  98 */     StringBuffer sb = new StringBuffer();
/*  99 */     Object[] keys = this.count.keys();
/* 100 */     for (int i = 0; i < keys.length; i++) {
/* 101 */       sb.append("[");
/* 102 */       Symbol key = (Symbol)keys[i];
/* 103 */       sb.append(key.getName());
/* 104 */       sb.append("->");
/* 105 */       sb.append(this.count.get(key));
/* 106 */       sb.append("]");
/*     */     }
/* 108 */     sb.append("\n");
/* 109 */     return sb.toString();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public Distribution getDistribution(DomainAlphabet alph, double pseudo_count)
/*     */     throws Exception
/*     */   {
/* 117 */     Distribution dist = DistributionFactory.DEFAULT.createDistribution(alph);
/* 118 */     double total = pseudo_count * alph.size();
/* 119 */     for (Iterator it = alph.iterator(); it.hasNext();) {
/* 120 */       Symbol sym = (Symbol)it.next();
/* 121 */       if (!sym.equals(alph.getMagicalState()))
/* 122 */         total += getCount(sym);
/*     */     }
/* 124 */     for (Iterator it = alph.iterator(); it.hasNext();) {
/* 125 */       Symbol sym = (Symbol)it.next();
/* 126 */       if (sym.equals(alph.getMagicalState())) {
/* 127 */         dist.setWeight(sym, 0.0D);
/*     */       } else
/* 129 */         dist.setWeight(sym, (getCount(sym) + pseudo_count) / total);
/*     */     }
/* 131 */     return dist;
/*     */   }
/*     */   
/*     */ 
/*     */   public ArrayList getSheet(SymbolList sym_list)
/*     */   {
/*     */     try
/*     */     {
/* 139 */       String str = "";
/* 140 */       if (!sym_list.equals(SymbolList.EMPTY_LIST)) {
/* 141 */         str = str + sym_list.seqString() + " ";
/*     */       }
/* 143 */       ArrayList al = new ArrayList();
/* 144 */       Object[] keys = this.count.keys();
/* 145 */       for (int i = 0; i < keys.length; i++) {
/* 146 */         Symbol sym = (Symbol)keys[i];
/* 147 */         al.add(str + sym.getName() + " " + getCount(sym));
/*     */       }
/*     */       
/* 150 */       return al;
/*     */     } catch (Throwable t) {
/* 152 */       t.printStackTrace(); } return null;
/*     */   }
/*     */   
/*     */   public void setCount(Symbol s, int w)
/*     */   {
/* 157 */     this.count.put(s, w);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int getCount(Symbol s)
/*     */   {
/* 166 */     return this.count.get(s);
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
/*     */   public boolean contains(Symbol s)
/*     */   {
/* 193 */     return this.count.containsKey(s);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public Object[] symbols()
/*     */   {
/* 201 */     return this.count.keys();
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
/*     */   public boolean equals(Object o)
/*     */   {
/* 245 */     if ((o instanceof Frequency)) {
/* 246 */       Frequency freq = (Frequency)o;
/* 247 */       return freq.count.equals(this.count);
/*     */     }
/* 249 */     return false;
/*     */   }
/*     */   
/*     */   public int hashCode() {
/* 253 */     return this.count.hashCode();
/*     */   }
/*     */ }


/* Location:              /home/lachlan/Desktop/psilc1.21/psilc.jar!/lc1/domains/Frequency.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */