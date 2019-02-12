/*     */ package lc1.pfam;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Properties;
/*     */ import java.util.SortedSet;
/*     */ import java.util.TreeSet;
/*     */ import org.apache.commons.cli.CommandLine;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class IDLogger
/*     */ {
/*     */   protected PfamSqlWrite sqlw;
/*     */   protected PfamSqlRead sqlr;
/*     */   protected SortedSet proteins;
/*     */   protected Properties params;
/*     */   protected static String table;
/*     */   protected static String restrictions;
/*  27 */   protected static String[] select = { "cluster" };
/*     */   Double evalue;
/*     */   
/*     */   public IDLogger(CommandLine params1)
/*     */   {
/*  32 */     if (params1.hasOption("evalue"))
/*     */     {
/*  34 */       this.evalue = new Double(Double.parseDouble(this.params.getProperty("evalue")));
/*  35 */       table = "lc1_cluster_log_" + this.params.getProperty("evalue");
/*  36 */       restrictions = "FROM " + table;
/*     */       
/*  38 */       this.proteins = new TreeSet();
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
/*     */ 
/*     */   public List reduce(List aligns_in)
/*     */   {
/*  52 */     List aligns = new ArrayList();
/*  53 */     for (Iterator it = aligns_in.iterator(); it.hasNext();) {
/*  54 */       String test = (String)it.next();
/*  55 */       if (canAdd(test)) {
/*  56 */         aligns.add(test);
/*     */       }
/*     */     }
/*  59 */     return aligns;
/*     */   }
/*     */   
/*     */   public void add(List aligns) {
/*  63 */     if (aligns == null)
/*  64 */       return;
/*  65 */     for (Iterator it = aligns.iterator(); it.hasNext();) {
/*  66 */       add((String)it.next());
/*     */     }
/*     */   }
/*     */   
/*     */   public boolean canAdd(String auto_pfamseq) {
/*  71 */     if (this.evalue == null)
/*  72 */       return true;
/*     */     try {
/*  74 */       if (this.proteins.contains(auto_pfamseq))
/*     */       {
/*     */ 
/*  77 */         return false;
/*     */       }
/*     */     }
/*     */     catch (Throwable localThrowable) {}
/*     */     
/*  82 */     return true;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public synchronized void add(String auto_pfamseq)
/*     */   {
/*  90 */     if (this.evalue == null)
/*  91 */       return;
/*     */     try {
/*  93 */       this.proteins.add(auto_pfamseq);
/*  94 */       this.sqlw.addStringEntries(new String[] { auto_pfamseq });
/*     */     } catch (Throwable t) {
/*  96 */       t.printStackTrace();
/*     */     }
/*     */   }
/*     */   
/*     */   public int size() {
/* 101 */     if (this.evalue == null)
/* 102 */       return 0;
/* 103 */     return this.proteins.size();
/*     */   }
/*     */   
/*     */   public String toString() {
/* 107 */     if (this.evalue == null)
/* 108 */       return super.toString();
/* 109 */     String str = new String();
/* 110 */     int j = 0;
/* 111 */     for (Iterator i = this.proteins.iterator(); i.hasNext();) {
/* 112 */       if (j > 10)
/*     */         break;
/* 114 */       str = str + i.next().toString() + " ";
/*     */     }
/* 116 */     return str;
/*     */   }
/*     */ }


/* Location:              /home/lachlan/Desktop/psilc1.21/psilc.jar!/lc1/pfam/IDLogger.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */