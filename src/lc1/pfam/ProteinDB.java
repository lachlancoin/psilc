/*     */ package lc1.pfam;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.Iterator;
/*     */ import java.util.Properties;
/*     */ import java.util.SortedSet;
/*     */ import java.util.TreeSet;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ class ProteinDB
/*     */ {
/*     */   protected PfamSqlWrite sqlw;
/*     */   protected PfamSqlRead sqlr;
/*     */   protected SortedSet proteins;
/*     */   protected int total;
/*     */   protected Properties params;
/*     */   protected static String table;
/*     */   protected static String restrictions;
/*  22 */   protected static String[] select = { "auto_pfamseq" };
/*     */   
/*     */ 
/*     */   ProteinDB() {}
/*     */   
/*     */   public ProteinDB(Properties params, String guideTable)
/*     */   {
/*  29 */     if ((guideTable != "pfamseq") || (params.containsKey("pfamB"))) {
/*  30 */       table = "context_protein_log1";
/*     */     }
/*     */     else
/*  33 */       table = "context_protein_log";
/*  34 */     restrictions = "FROM " + table;
/*  35 */     this.params = params;
/*     */     
/*  37 */     this.proteins = new TreeSet();
/*  38 */     SqlTools.createAndCheck(params, table, "auto_pfamseq int(10) unsigned NOT NULL UNIQUE");
/*  39 */     this.sqlr = new PfamSqlRead(params, select, restrictions);
/*  40 */     this.sqlw = new PfamSqlWrite(params, table);
/*  41 */     Iterator i = this.sqlr.getQuery();
/*  42 */     while (i.hasNext()) {
/*  43 */       this.proteins.add(new Integer((String)((ArrayList)i.next()).get(0)));
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public int next(int current, int max)
/*     */   {
/*  51 */     Integer curr = new Integer(current);
/*  52 */     SortedSet set = this.proteins.subSet(curr, new Integer(max + 1));
/*  53 */     for (int i = current; i <= max; i++) {
/*  54 */       if (!set.contains(new Integer(i))) {
/*  55 */         return i;
/*     */       }
/*     */     }
/*  58 */     return max + 1;
/*     */   }
/*     */   
/*     */   public boolean canAdd(int auto_pfamseq) {
/*     */     try {
/*  63 */       if (this.proteins.contains(new Integer(auto_pfamseq)))
/*     */       {
/*  65 */         return false;
/*     */       }
/*     */     }
/*     */     catch (Throwable localThrowable) {}
/*     */     
/*     */ 
/*  71 */     return true;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public synchronized void add(int auto_pfamseq, int min)
/*     */   {
/*     */     try
/*     */     {
/*  80 */       SortedSet head = this.proteins.subSet(new Integer(min), new Integer(auto_pfamseq));
/*  81 */       int prev; int prev; if (head.isEmpty()) {
/*  82 */         prev = min - 1;
/*     */       }
/*     */       else {
/*  85 */         prev = ((Integer)head.last()).intValue();
/*     */       }
/*  87 */       for (int i = prev + 1; i <= auto_pfamseq; i++) {
/*  88 */         this.proteins.add(new Integer(i));
/*  89 */         this.sqlw.addStringEntries(new String[] { Integer.toString(i) });
/*     */       }
/*     */     }
/*     */     catch (Throwable t) {
/*  93 */       t.printStackTrace();
/*     */     }
/*     */   }
/*     */   
/*     */   public int size() {
/*  98 */     return this.proteins.size();
/*     */   }
/*     */   
/*     */   public String toString() {
/* 102 */     String str = new String();
/* 103 */     int j = 0;
/* 104 */     for (Iterator i = this.proteins.iterator(); i.hasNext();) {
/* 105 */       if (j > 10) break;
/* 106 */       str = str + i.next().toString() + " ";
/*     */     }
/* 108 */     return str;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getStart()
/*     */   {
/* 114 */     if (this.proteins.size() > 0) {
/* 115 */       return ((Integer)this.proteins.last()).intValue() + 1;
/*     */     }
/* 117 */     return 1;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public ArrayList getMarkers(int jumps, int min, int max)
/*     */   {
/* 124 */     int upper = (int)Math.ceil(max / jumps);
/* 125 */     int lower = (int)Math.floor(min / jumps);
/* 126 */     ArrayList markers = new ArrayList();
/* 127 */     for (int i = upper - 1; i >= lower; i--) {
/* 128 */       if (this.proteins.subSet(new Integer(jumps * i), new Integer(jumps * (i + 1) + 1)).size() < jumps) {
/* 129 */         markers.add(new Integer(i));
/*     */       }
/*     */     }
/* 132 */     return markers;
/*     */   }
/*     */ }


/* Location:              /home/lachlan/Desktop/psilc1.21/psilc.jar!/lc1/pfam/ProteinDB.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */