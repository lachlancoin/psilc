/*     */ package lc1.util;
/*     */ 
/*     */ import java.io.BufferedOutputStream;
/*     */ import java.io.BufferedReader;
/*     */ import java.io.File;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.FileReader;
/*     */ import java.io.OutputStream;
/*     */ import java.io.PrintStream;
/*     */ import java.io.RandomAccessFile;
/*     */ import java.util.Set;
/*     */ import org.biojava.bio.Annotation;
/*     */ import org.biojava.bio.BioException;
/*     */ import org.biojava.bio.seq.DNATools;
/*     */ import org.biojava.bio.seq.Sequence;
/*     */ import org.biojava.bio.seq.SequenceIterator;
/*     */ import org.biojava.bio.seq.SequenceTools;
/*     */ import org.biojava.bio.seq.db.HashSequenceDB;
/*     */ import org.biojava.bio.seq.db.SequenceDB;
/*     */ import org.biojava.bio.seq.impl.SimpleSequence;
/*     */ import org.biojava.bio.seq.io.SeqIOTools;
/*     */ import org.biojava.bio.symbol.FiniteAlphabet;
/*     */ import org.biojava.bio.symbol.Location;
/*     */ import org.biojava.bio.symbol.LocationTools;
/*     */ 
/*     */ 
/*     */ public class BlastDB
/*     */ {
/*  29 */   static boolean dna_out = true;
/*  30 */   static boolean prot_blast = false;
/*     */   static final int min_cluster_size = 2;
/*     */   static File clusterDir;
/*     */   
/*  34 */   public static void cleanFiles(String[] args) throws Exception { for (int i = 0; i < args.length; i++) {
/*  35 */       BufferedReader br = new BufferedReader(new FileReader(args[i]));
/*  36 */       SequenceIterator it = SeqIOTools.readFasta(br, 
/*  37 */         DNATools.getDNA().getTokenization("token"));
/*  38 */       SequenceIterator it2 = new SequenceIterator() {
/*     */         public boolean hasNext() {
/*  40 */           return BlastDB.this.hasNext();
/*     */         }
/*     */         
/*     */         public Sequence nextSequence() {
/*     */           try {
/*  45 */             Sequence seq1 = BlastDB.this.nextSequence();
/*  46 */             String name = seq1.getName().split("\\|")[0];
/*  47 */             return new SimpleSequence(seq1, name, name, 
/*  48 */               Annotation.EMPTY_ANNOTATION);
/*     */           } catch (BioException exc) {
/*  50 */             exc.printStackTrace(); }
/*  51 */           return null;
/*     */         }
/*     */         
/*  54 */       };
/*  55 */       OutputStream os = new BufferedOutputStream(new FileOutputStream(
/*  56 */         new File(args[i] + "_1")));
/*  57 */       SeqIOTools.writeFasta(os, it2);
/*  58 */       os.close();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   static File clusterDir1;
/*     */   
/*     */ 
/*     */   static File tmpDir;
/*     */   
/*     */ 
/*     */   static final boolean deleteOnExit = false;
/*     */   
/*     */ 
/*     */   RandomAccessFile blastResults;
/*     */   
/*     */ 
/*     */   SequenceDB searchDB;
/*     */   
/*     */   SequenceDB queryDB;
/*     */   
/*     */   public static void main1(String[] args)
/*     */     throws Exception
/*     */   {
/*  83 */     writeToFile(new File(args[1]), new File(args[0]));
/*     */   }
/*     */   
/*     */   public static void main(String[] args) throws Exception {
/*  87 */     getHomologs(new File(args[0]), 
/*  88 */       new File(args[1]), 
/*  89 */       new File(args[2]), 
/*  90 */       new File(args[3]), false);
/*     */   }
/*     */   
/*     */   public static File getHomologs(File queryFile, File searchFile, File blastFile, File outpDir, boolean dna)
/*     */     throws Exception
/*     */   {
/*  96 */     initialiseStaticVariables(outpDir, dna);
/*  97 */     boolean success = false;
/*  98 */     BlastDB db = new BlastDB(queryFile, searchFile, blastFile);
/*  99 */     return db.getClusters();
/*     */   }
/*     */   
/*     */   private static void initialiseStaticVariables(File outpDir, boolean dna) {
/* 103 */     if (clusterDir == null) {
/* 104 */       clusterDir = new File(outpDir, dna ? "cluster_dna" : "cluster");
/* 105 */       tmpDir = new File(outpDir, "tmp");
/* 106 */       clusterDir1 = clusterDir;
/* 107 */       tmpDir.mkdir();
/* 108 */       if (!clusterDir.exists())
/* 109 */         clusterDir.mkdir();
/* 110 */       if (!clusterDir1.exists()) {}
/* 111 */       clusterDir1.mkdir();
/*     */     }
/*     */   }
/*     */   
/*     */   public static void writeToFile(File out, File f) throws Exception {
/* 116 */     RandomAccessFile db = new RandomAccessFile(f, "r");
/* 117 */     PrintStream ps = new PrintStream(new BufferedOutputStream(
/* 118 */       new FileOutputStream(out)));
/*     */     
/* 120 */     String previous = "";
/* 121 */     long i = db.getFilePointer();
/* 122 */     String s; while ((s = db.readLine()) != null) { String s;
/* 123 */       String s1 = s.split("\t")[0];
/* 124 */       if (!s1.equals(previous)) {
/* 125 */         previous = s1;
/* 126 */         ps.println(s1 + " " + i);
/*     */       }
/* 128 */       i = db.getFilePointer();
/*     */     }
/* 130 */     ps.flush();
/* 131 */     ps.close();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public BlastDB(File searchFile, File fastaIndexFile, File blastFile)
/*     */     throws Exception
/*     */   {
/* 143 */     this.searchDB = FlatFileTools.open(createIndices(fastaIndexFile, "proteint"));
/*     */     
/* 145 */     this.queryDB = FlatFileTools.open(createIndices(searchFile, "proteint"));
/* 146 */     this.blastResults = new RandomAccessFile(blastFile, "r");
/*     */   }
/*     */   
/*     */   public static File createIndices(File file, String type) {
/* 150 */     File index = new File(file.getAbsolutePath() + "_idx");
/* 151 */     if ((index.exists()) || (index.length() > 0L))
/* 152 */       return index;
/* 153 */     FlatFileTools.createIndex(index, "fasta", type);
/* 154 */     FlatFileTools.addFilesToIndex(index, new File[] { file });
/* 155 */     return index;
/*     */   }
/*     */   
/*     */ 
/*     */   public File getClusters()
/*     */     throws Exception
/*     */   {
/* 162 */     String s = this.blastResults.readLine();
/* 163 */     String[] results = s.split("\t");
/* 164 */     File lastFile = null;
/* 165 */     while (s != null)
/*     */     {
/* 167 */       String alignName = results[0];
/* 168 */       String fileName = alignName;
/* 169 */       if (fileName.indexOf('.') >= 0) fileName = fileName.split("\\.")[0];
/* 170 */       File clusterF = new File(clusterDir, fileName);
/*     */       
/* 172 */       HashSequenceDB dbOut = new HashSequenceDB();
/* 173 */       PrintStream os = new PrintStream(new BufferedOutputStream(
/* 174 */         new FileOutputStream(new File(clusterDir1, fileName))));
/* 175 */       Sequence parentP = this.queryDB.getSequence(alignName);
/* 176 */       Sequence parent = this.searchDB.ids().contains(alignName) ? this.searchDB.getSequence(alignName) : parentP;
/* 177 */       System.err.println("length1 " + parent.seqString());
/* 178 */       double transfer = parentP.length() != parent.length() ? 3.0D : 1.0D;
/*     */       
/*     */ 
/*     */ 
/* 182 */       dbOut.addSequence(parent);
/*     */       
/* 184 */       while ((results[0].equals(alignName)) && (s != null))
/*     */       {
/* 186 */         Location location = null;
/* 187 */         Location location2 = null;
/* 188 */         String name = results[1];
/* 189 */         if (name.equals(parent.getName())) {
/* 190 */           s = this.blastResults.readLine();
/* 191 */           if (s != null) {
/* 192 */             results = s.split("\t");
/*     */           }
/*     */         }
/*     */         else {
/* 196 */           Sequence seq = this.searchDB.getSequence(name);
/*     */           
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 203 */           while ((results[1].equals(name)) && (
/* 204 */             results[0].equals(alignName)))
/*     */           {
/* 206 */             int s_start = Integer.parseInt(results[6]);
/*     */             
/* 208 */             int s_end = Integer.parseInt(results[7]);
/*     */             
/* 210 */             int start = Integer.parseInt(results[8]);
/* 211 */             int end = Integer.parseInt(results[9]);
/*     */             
/* 213 */             double evalue = Double.parseDouble(results[10]);
/* 214 */             double match = Double.parseDouble(results[2]);
/* 215 */             int length = Integer.parseInt(results[3]);
/* 216 */             boolean excludedClosest = false;
/* 217 */             if ((evalue < Math.pow(10.0D, -7.0D)) && (start < end)) {
/* 218 */               if ((excludedClosest) || (match < 99.0D) || 
/* 219 */                 (length < 0.8D * (s_end - s_start))) {
/* 220 */                 Location loc = LocationTools.makeLocation(s_start, 
/* 221 */                   s_end);
/* 222 */                 Location loc2 = LocationTools.makeLocation(start, 
/* 223 */                   end);
/* 224 */                 location = location == null ? loc : 
/* 225 */                   location.union(loc);
/* 226 */                 location2 = location2 == null ? loc2 : 
/* 227 */                   location2.union(loc2);
/* 228 */                 if (s_end > parent.length())
/* 229 */                   throw new Exception("this doesn't work");
/* 230 */                 if (end > seq.length() / transfer)
/* 231 */                   throw new Exception("this doesn't work " + end + 
/* 232 */                     " " + seq.length());
/*     */               } else {
/* 234 */                 excludedClosest = true;
/*     */               }
/*     */             }
/* 237 */             s = this.blastResults.readLine();
/* 238 */             if (s == null)
/*     */               break;
/* 240 */             results = s.split("\t");
/*     */           }
/* 242 */           if ((location != null) && 
/* 243 */             (location.getMax() - location.getMin() > 0.8D * 
/* 244 */             parent.length() / transfer)) {
/* 245 */             int min = 1 + (int)(3.0D * Math.floor((
/* 246 */               location2.getMin() - 1) / 3.0D));
/* 247 */             int max = (int)(3.0D * Math.ceil((
/* 248 */               location2.getMax() - 1) / 3.0D));
/*     */             
/* 250 */             Sequence seq1 = SequenceTools.subSequence(seq, (int)(1.0D + (min - 1) * transfer), Math.min(seq.length(), (int)(transfer * (max - 1))));
/* 251 */             System.err.println("length " + seq1.length() / 3);
/* 252 */             dbOut.addSequence(new SimpleSequence(seq1, name, name, 
/* 253 */               seq.getAnnotation()));
/* 254 */             System.err.println("added " + seq1.getName());
/*     */           }
/*     */         }
/*     */       }
/* 258 */       if (dbOut.ids().size() >= 2)
/*     */       {
/*     */ 
/* 261 */         for (SequenceIterator seqIT = dbOut.sequenceIterator(); seqIT.hasNext();) {
/* 262 */           Sequence seq = seqIT.nextSequence();
/* 263 */           System.err.println("rem " + Math.IEEEremainder(seq.length(), 3.0D));
/* 264 */           os.println(">" + seq.getName());
/* 265 */           os.println(seq.seqString());
/*     */         }
/* 267 */         os.close();
/* 268 */         lastFile = clusterF;
/*     */       }
/*     */     }
/*     */     
/* 272 */     return lastFile;
/*     */   }
/*     */ }


/* Location:              /home/lachlan/Desktop/psilc1.21/psilc.jar!/lc1/util/BlastDB.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */