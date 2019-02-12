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
/*     */ public class StockholmIndex
/*     */ {
/*     */   RandomAccessFile pfam_ls;
/*     */   File indexFile;
/*  31 */   TObjectLongHashMap index = new TObjectLongHashMap();
/*     */   File repos;
/*     */   
/*     */   public Iterator getNameIterator() {
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
/*     */   public StockholmIndex(File REPOS) throws Exception
/*     */   {
/*  58 */     File pfam_ls = new File(REPOS, "Pfam-A.seed");
/*  59 */     File indexFile = new File(REPOS, "Pfam-A.seed_idx");
/*  60 */     this.pfam_ls = new RandomAccessFile(pfam_ls, "r");
/*  61 */     this.repos = new File(REPOS, "Pfam");
/*  62 */     if ((indexFile.exists()) && (indexFile.length() > 0L)) {
/*  63 */       BufferedReader br = new BufferedReader(new FileReader(indexFile));
/*  64 */       String st = "";
/*  65 */       while ((st = br.readLine()) != null) {
/*  66 */         String[] str = st.split("\t");
/*  67 */         this.index.put(str[0], Long.parseLong(str[1]));
/*     */       }
/*  69 */       br.close();
/*     */     }
/*     */     else {
/*  72 */       PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(indexFile)));
/*  73 */       String st = "";
/*  74 */       long i = 0L;
/*  75 */       String pfamId = "";
/*  76 */       boolean inAlignment = false;
/*  77 */       while ((st = this.pfam_ls.readLine()) != null) {
/*  78 */         if (st.startsWith("#=GF ID")) pfamId = st.split("\\s+")[2];
/*  79 */         i += 1L;
/*  80 */         if ((!st.startsWith("#")) && (!st.startsWith("//")) && (!inAlignment)) {
/*  81 */           inAlignment = true;
/*  82 */           long startPos = this.pfam_ls.getFilePointer();
/*  83 */           this.index.put(String.copyValueOf(pfamId.toCharArray()), 
/*  84 */             startPos);
/*     */           
/*  86 */           pw.print(pfamId);pw.print("\t");pw.print(startPos + "\t");
/*  87 */           pw.println();
/*     */         }
/*  89 */         if (st.startsWith("//")) inAlignment = false;
/*     */       }
/*  91 */       pw.close();
/*     */     }
/*     */   }
/*     */   
/*     */   public void writeHMMFile(String name) throws Exception
/*     */   {
/*  97 */     File dir = new File(this.repos, name);
/*  98 */     dir.mkdir();
/*  99 */     File f = new File(dir, "SEED");
/* 100 */     PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(f)));
/* 101 */     if (!this.index.contains(name)) System.err.println("doesn't contain " + name);
/* 102 */     this.pfam_ls.seek(this.index.get(name));
/*     */     label143:
/* 104 */     for (;;) { String st = this.pfam_ls.readLine();
/* 105 */       if (st.startsWith("#")) {
/* 106 */         if (!st.startsWith("#=GR")) break;
/* 107 */         break label143; break;
/*     */       } else {
/* 109 */         pw.println(st);
/*     */       } }
/* 111 */     pw.close();
/*     */   }
/*     */ }


/* Location:              /home/lachlan/Desktop/psilc1.21/psilc.jar!/lc1/pfam/StockholmIndex.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */