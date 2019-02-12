/*     */ package lc1.phyl;
/*     */ 
/*     */ import java.io.BufferedOutputStream;
/*     */ import java.io.File;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.io.PrintStream;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.Set;
/*     */ import lc1.util.FlatFileTools;
/*     */ import lc1.util.SheetIO;
/*     */ import org.biojava.bio.Annotation;
/*     */ import org.biojava.bio.seq.Sequence;
/*     */ import org.biojava.bio.seq.SequenceIterator;
/*     */ import org.biojava.bio.seq.db.SequenceDB;
/*     */ import org.biojava.bio.seq.impl.SimpleSequence;
/*     */ import org.biojava.bio.seq.io.SeqIOTools;
/*     */ import org.biojava.utils.ProcessTools;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class KOGUtils
/*     */ {
/*  33 */   static final String[] specN = { "ath", "cel", "dme", "hsa", "sce", "spo", "ecu" };
/*  34 */   static final String[] taxa = { "3702", "6239", "7227", "9606", "4932", "4896", "6035" };
/*     */   
/*  36 */   static final Map taxaMap = new HashMap() {};
/*     */   
/*     */   public static void main(String[] args)
/*     */     throws Exception
/*     */   {}
/*     */   
/*     */   public static void alignClusters()
/*     */     throws Exception
/*     */   {
/*  45 */     File outpDir = new File(".");
/*  46 */     String bin = "";
/*  47 */     String[] input = new File(outpDir, "cluster").list();
/*  48 */     for (int i = 0; i < input.length; i++)
/*     */       try
/*     */       {
/*  51 */         File file = new File(outpDir, "cluster/" + input[i]);
/*  52 */         File out = new File("align/" + file.getName() + ".aln");
/*  53 */         if ((!out.exists()) || (out.length() <= 0L))
/*     */         {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  60 */           if ((file.exists()) && (file.length() > 0L)) {
/*  61 */             String[] command = { "clustalw", file.getAbsolutePath() };
/*  62 */             ProcessTools.exec(command, null, null, null);
/*  63 */             command = new String[] { "mv", file.getAbsolutePath() + ".aln", 
/*  64 */               "align" };
/*  65 */             ProcessTools.exec(command, null, null, null);
/*  66 */           } } } catch (Exception exc) { exc.printStackTrace();
/*     */       } }
/*     */   
/*     */   public void remove() {}
/*     */   
/*  71 */   public static void kogToClusters(File kog, File outDir, File fasta) throws Exception { Iterator it = SheetIO.read(kog, "\\s+");
/*  72 */     String id = (String)((List)it.next()).get(1);
/*  73 */     File index = new File(fasta.getAbsolutePath() + "_idx");
/*  74 */     if ((!index.exists()) || (index.length() == 0L)) {
/*  75 */       FlatFileTools.createIndex(index, "fasta", "protein");
/*  76 */       FlatFileTools.addFilesToIndex(index, new File[] { fasta });
/*     */     }
/*  78 */     SequenceDB seqDB = FlatFileTools.open(index);
/*  79 */     while (it.hasNext()) {
/*  80 */       Iterator it1 = new Iterator() {
/*  81 */         List next = (List)KOGUtils.this.next();
/*     */         
/*     */         public Object next() {
/*  84 */           Object obj = this.next;
/*  85 */           this.next = (KOGUtils.this.hasNext() ? (List)KOGUtils.this.next() : null);
/*     */           
/*  87 */           return obj;
/*     */         }
/*     */         
/*  90 */         public boolean hasNext() { return (this.next != null) && (this.next.size() > 1);
/*     */         }
/*  92 */       };
/*  93 */       Map m = getKog(it1);
/*     */       
/*  95 */       if (m.size() > 2) {
/*  96 */         print(m, id, seqDB, outDir);
/*     */       }
/*  98 */       id = (String)((List)it.next()).get(1);
/*     */     }
/*     */   }
/*     */   
/*     */   public static void print(Map m, String id, File outDir) throws Exception {
/* 103 */     PrintStream ps = new PrintStream(new BufferedOutputStream(new FileOutputStream(new File(outDir, id))));
/* 104 */     for (Iterator it2 = m.keySet().iterator(); it2.hasNext();) {
/* 105 */       Object key = it2.next();
/* 106 */       ps.println(key + " " + m.get(key));
/*     */     }
/* 108 */     ps.close();
/*     */   }
/*     */   
/*     */   public static Map getKog(Iterator it)
/*     */   {
/* 113 */     Map m = new HashMap();
/* 114 */     while (it.hasNext()) {
/* 115 */       List row = (List)it.next();
/*     */       
/*     */ 
/* 118 */       Object key = row.get(1);
/* 119 */       if (m.containsKey(key)) {
/* 120 */         Collection c = (Collection)m.get(key);
/* 121 */         c.add(row.get(2));
/*     */       }
/*     */       else {
/* 124 */         Collection c = new ArrayList();
/* 125 */         c.add(row.get(2));
/* 126 */         m.put(key, c);
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 131 */     Map m2 = new HashMap();
/* 132 */     for (Iterator it1 = m.keySet().iterator(); it1.hasNext();) {
/* 133 */       Object key = it1.next();
/* 134 */       Collection c = (Collection)m.get(key);
/*     */       
/* 136 */       if (c.size() == 1) {
/* 137 */         m2.put(key, c.iterator().next());
/*     */       }
/*     */     }
/* 140 */     return m2;
/*     */   }
/*     */   
/*     */   public static void print(Map m, String id, SequenceDB seqDB, File outDir) throws Exception
/*     */   {
/* 145 */     Iterator it = m.entrySet().iterator();
/* 146 */     SequenceIterator seqIt = new SequenceIterator()
/*     */     {
/* 148 */       public boolean hasNext() { return KOGUtils.this.hasNext(); }
/*     */       
/*     */       public Sequence nextSequence() {
/* 151 */         Map.Entry l = (Map.Entry)KOGUtils.this.next();
/* 152 */         String id1 = ((String)l.getKey()).split(":")[0];
/* 153 */         String idI = (String)KOGUtils.taxaMap.get(id1);
/*     */         try
/*     */         {
/* 156 */           Sequence seq = this.val$seqDB.getSequence((String)l.getValue());
/* 157 */           return new SimpleSequence(seq, idI, idI, Annotation.EMPTY_ANNOTATION);
/*     */         } catch (Exception exc) {
/* 159 */           exc.printStackTrace(); }
/* 160 */         return null;
/*     */       }
/*     */       
/* 163 */     };
/* 164 */     OutputStream os = new BufferedOutputStream(new FileOutputStream(outDir.getAbsolutePath() + "/" + id));
/* 165 */     SeqIOTools.writeFasta(os, seqIt);
/* 166 */     os.close();
/*     */   }
/*     */ }


/* Location:              /home/lachlan/Desktop/psilc1.21/psilc.jar!/lc1/phyl/KOGUtils.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */