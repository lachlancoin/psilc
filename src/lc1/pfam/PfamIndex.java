/*     */ package lc1.pfam;
/*     */ 
/*     */ import gnu.trove.TObjectLongHashMap;
/*     */ import java.io.BufferedReader;
/*     */ import java.io.BufferedWriter;
/*     */ import java.io.File;
/*     */ import java.io.FileReader;
/*     */ import java.io.FileWriter;
/*     */ import java.io.PrintStream;
/*     */ import java.io.PrintWriter;
/*     */ import java.io.RandomAccessFile;
/*     */ import java.util.Arrays;
/*     */ import java.util.Iterator;
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
/*     */ public class PfamIndex
/*     */ {
/*     */   RandomAccessFile pfam_ls;
/*     */   File indexFile;
/*  31 */   TObjectLongHashMap index = new TObjectLongHashMap();
/*     */   
/*     */   public Iterator getNameIterator()
/*     */   {
/*  35 */     Object[] keys = this.index.keys();
/*  36 */     new Iterator() {
/*     */       int i;
/*     */       
/*  39 */       public boolean hasNext() { return this.i < this.val$keys.length; }
/*     */       
/*     */       public Object next()
/*     */       {
/*  43 */         Object[] row = new Object[4];
/*  44 */         row[0] = this.val$keys[this.i];
/*  45 */         row[1] = this.val$keys[this.i];
/*  46 */         row[2] = "-2147483648";
/*  47 */         row[3] = "-2147483648";
/*  48 */         this.i += 1;
/*  49 */         return Arrays.asList(row);
/*     */       }
/*     */       
/*     */       public void remove() {}
/*     */     };
/*     */   }
/*     */   
/*     */   public PfamIndex(File REPOS) throws Exception
/*     */   {
/*  58 */     File pfam_ls = new File(REPOS, "Pfam_ls");
/*  59 */     File indexFile = new File(REPOS, "Pfam_ls_idx");
/*  60 */     this.pfam_ls = new RandomAccessFile(pfam_ls, "r");
/*     */     
/*  62 */     if ((indexFile.exists()) && (indexFile.length() > 0L)) {
/*  63 */       BufferedReader br = new BufferedReader(new FileReader(indexFile));
/*  64 */       String st = "";
/*  65 */       while ((st = br.readLine()) != null) {
/*  66 */         String[] str = st.split("\t");
/*  67 */         String pf = str[0];
/*  68 */         int ind = pf.indexOf('.');
/*  69 */         if (ind >= 0) pf = pf.substring(0, ind);
/*  70 */         this.index.put(pf, Long.parseLong(str[1]));
/*     */       }
/*  72 */       br.close();
/*     */     }
/*     */     else {
/*  75 */       System.err.println("indexing Pfam_ls - this will take some time, but only needs to be done once");
/*  76 */       PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(indexFile)));
/*  77 */       String st = "";
/*  78 */       long i = 0L;
/*  79 */       long startPos = this.pfam_ls.getFilePointer();
/*  80 */       String pfamId = "";
/*  81 */       while ((st = this.pfam_ls.readLine()) != null) {
/*  82 */         if (st.startsWith("ACC")) pfamId = st.split("\\s+")[1];
/*  83 */         int ind = pfamId.indexOf('.');
/*  84 */         if (ind >= 0) { pfamId = pfamId.substring(0, ind);
/*     */         }
/*  86 */         i += 1L;
/*  87 */         if (st.startsWith("//")) {
/*  88 */           this.pfam_ls.readLine();
/*  89 */           long endPos = this.pfam_ls.getFilePointer();
/*  90 */           this.index.put(String.copyValueOf(pfamId.toCharArray()), 
/*  91 */             startPos);
/*     */           
/*  93 */           pw.print(pfamId);pw.print("\t");pw.print(startPos + "\t");
/*  94 */           pw.println();
/*  95 */           startPos = endPos;
/*     */         }
/*     */       }
/*  98 */       pw.close();
/*  99 */       System.err.println("finished indexing ");
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void writeHMMFile(String name, BufferedWriter pw)
/*     */     throws Exception
/*     */   {
/* 110 */     if (!this.index.contains(name)) {
/* 111 */       System.err.println("doesn't contain " + name);
/* 112 */       return;
/*     */     }
/* 114 */     this.pfam_ls.seek(this.index.get(name));
/* 115 */     String header = "HMMER2.0  [2.3.1]";
/* 116 */     pw.write("\n");
/*     */     for (;;) {
/* 118 */       String st = this.pfam_ls.readLine();
/* 119 */       pw.write(st);pw.write("\n");
/* 120 */       if (st.startsWith("//")) break;
/*     */     }
/* 122 */     pw.close();
/*     */   }
/*     */ }


/* Location:              /home/lachlan/Desktop/psilc1.21/psilc.jar!/lc1/pfam/PfamIndex.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */