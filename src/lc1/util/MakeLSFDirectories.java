/*     */ package lc1.util;
/*     */ 
/*     */ import java.io.BufferedOutputStream;
/*     */ import java.io.BufferedReader;
/*     */ import java.io.BufferedWriter;
/*     */ import java.io.File;
/*     */ import java.io.FileFilter;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.FileReader;
/*     */ import java.io.FileWriter;
/*     */ import java.io.FilenameFilter;
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStream;
/*     */ import java.io.PrintStream;
/*     */ import java.io.PrintWriter;
/*     */ import java.io.StringWriter;
/*     */ import java.util.HashSet;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Set;
/*     */ import org.biojava.bio.Annotation;
/*     */ import org.biojava.bio.seq.Sequence;
/*     */ import org.biojava.bio.seq.SequenceIterator;
/*     */ import org.biojava.bio.seq.impl.SimpleSequence;
/*     */ import org.biojava.bio.seq.io.SeqIOTools;
/*     */ import org.biojava.bio.symbol.Location;
/*     */ import org.biojava.bio.symbol.LocationTools;
/*     */ import org.biojava.utils.ProcessTools;
/*     */ import pal.alignment.Alignment;
/*     */ import pal.alignment.AlignmentUtils;
/*     */ import pal.alignment.ReadAlignment;
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
/*     */ public class MakeLSFDirectories
/*     */ {
/*     */   public static void main(String[] args)
/*     */     throws Exception
/*     */   {}
/*     */   
/*     */   private static void clean(File f)
/*     */     throws Exception
/*     */   {
/*  54 */     System.err.println(f.getName());
/*  55 */     StringWriter pw = new StringWriter();
/*  56 */     File[] files = f.listFiles(new FileFilter() {
/*     */       public boolean accept(File fil) {
/*  58 */         String n = fil.getName();
/*  59 */         if (n.startsWith("temp")) return true;
/*  60 */         if (n.indexOf("pep") > 0) return true;
/*  61 */         if (n.equals("PSILC_WAG_HKY")) return true;
/*  62 */         if (n.equals("PSILC")) return true;
/*  63 */         return false;
/*     */       }
/*     */     });
/*  66 */     for (int i = 0; i < files.length; i++) {
/*  67 */       if (files[i].isDirectory()) {
/*  68 */         File[] f1 = files[i].listFiles();
/*  69 */         for (int j = 0; j < f1.length; j++) {
/*  70 */           f1[j].delete();
/*     */         }
/*     */       }
/*  73 */       files[i].delete();
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static void getIncompleteFiles()
/*     */     throws Exception
/*     */   {
/*  96 */     File f = new File("/nfs/farm/Pfam/lc1/families_phigs");
/*     */     
/*     */ 
/*  99 */     File[] files = f.listFiles();
/* 100 */     int[] count = new int[2];
/* 101 */     Location loc = null;
/* 102 */     for (int i = 1; i < files.length; i++) {
/*     */       try {
/* 104 */         File psilc = new File(files[i], "PSILC_WAG_HKY_recursive");
/* 105 */         File fa = new File(files[i], "seed.dna.fa");
/* 106 */         if ((!fa.exists()) || (fa.length() == 0L)) {
/* 107 */           if (fa.exists()) fa.delete();
/* 108 */           File pf = new File(files[i], "pfamA");
/* 109 */           if (pf.exists()) pf.delete();
/* 110 */           files[i].delete();
/*     */         }
/*     */         else {
/* 113 */           count[0] += 1;
/*     */           
/*     */ 
/* 116 */           BufferedReader br = new BufferedReader(new FileReader(fa));
/* 117 */           for (SequenceIterator seqIt = SeqIOTools.readFastaDNA(br); 
/* 118 */                 seqIt.hasNext();) {
/* 119 */             Sequence seq = seqIt.nextSequence();
/* 120 */             if ((!seq.getName().startsWith("OTT")) && 
/* 121 */               (Math.IEEEremainder(seq.length(), 3.0D) != 0.0D))
/*     */             {
/* 123 */               br.close();
/* 124 */               break;
/*     */             }
/*     */           }
/* 127 */           br.close();
/* 128 */           File summary = new File(psilc, "summary");
/* 129 */           if ((!summary.exists()) || (summary.length() == 0L))
/*     */             try {
/* 131 */               int st1 = Integer.parseInt(files[i].getName());
/* 132 */               Location loc_inner = LocationTools.makeLocation(st1, st1);
/* 133 */               loc = loc == null ? loc_inner : loc.union(loc_inner);
/* 134 */               count[1] += 1;
/*     */             } catch (Exception exc) {}
/*     */         }
/*     */       } catch (Exception exc) {
/* 138 */         exc.printStackTrace();
/* 139 */         System.err.println("something wrong with " + files[i]);
/*     */       }
/*     */     }
/* 142 */     System.err.println("redo " + count[1] + " of " + count[0]);
/* 143 */     for (Iterator it = loc.blockIterator(); it.hasNext();) {
/* 144 */       Location loci = (Location)it.next();
/* 145 */       if (loci.getMin() == loci.getMax()) {
/* 146 */         System.err.print("'" + loci.getMin() + "'" + ",");
/*     */       }
/*     */       else {
/* 149 */         System.err.print("'" + loci.getMin() + "-" + loci.getMax() + "'" + ",");
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/* 155 */   static int size = 1;
/*     */   
/* 157 */   public static void main1(String[] args) throws IOException { File dir = new File(".");
/* 158 */     File alignDir = new File(dir, "align");
/* 159 */     Set strings = new HashSet();
/* 160 */     String[] files = alignDir.list();
/* 161 */     for (int i = 0; i < files.length; i++) {
/* 162 */       strings.add(files[i].split("_")[0]);
/*     */     }
/* 164 */     File lsfdir = new File(dir, "lsf");
/* 165 */     lsfdir.mkdir();
/* 166 */     Iterator it = strings.iterator();
/* 167 */     int i = 0;
/* 168 */     int index = 1;
/* 169 */     while (it.hasNext()) {
/* 170 */       File outDir = new File(lsfdir, index);
/* 171 */       outDir.mkdir();
/* 172 */       File alignOut = new File(outDir, "align");
/* 173 */       alignOut.mkdir();
/* 174 */       while ((it.hasNext()) && (i < size)) {
/* 175 */         String id = (String)it.next();
/* 176 */         System.err.println(id);
/* 177 */         File[] toCopy = alignDir.listFiles(new FileFilter()
/*     */         {
/*     */           public boolean accept(File f) {
/* 180 */             return (f.getName().startsWith(MakeLSFDirectories.this + "_")) || (f.getName().startsWith(MakeLSFDirectories.this));
/*     */           }
/*     */         });
/* 183 */         for (int k = 0; k < toCopy.length; k++) {
/* 184 */           File out = new File(alignOut.getAbsolutePath() + "/" + toCopy[k].getName());
/* 185 */           if (!out.exists()) {
/* 186 */             String[] command = { "cp", toCopy[k].getAbsolutePath(), out.getAbsolutePath() };
/* 187 */             ProcessTools.exec(command, null, null, null);
/*     */           } }
/* 189 */         i++;
/*     */       }
/* 191 */       i = 0;
/* 192 */       index++;
/*     */     }
/*     */   }
/*     */   
/*     */   public static void mainR(String[] args) throws Exception {
/* 197 */     File dir = new File(".");
/* 198 */     File clusterDir = new File(dir, "cluster");
/* 199 */     File alignDir = new File(dir, "align1");
/* 200 */     File lsfdir = new File(dir, "lsf");
/* 201 */     int index = 0;
/* 202 */     File[] files = lsfdir.listFiles();
/* 203 */     for (int i = 0; i < files.length; i++) {
/* 204 */       File resultFile = new File(files[i], "result");
/* 205 */       if ((resultFile.exists()) && (resultFile.length() != 0L)) {
/* 206 */         String[] names = files[i].list(new FilenameFilter() {
/*     */           public boolean accept(File f, String name) {
/* 208 */             return !f.getName().startsWith("result");
/*     */           }
/*     */         });
/* 211 */         if (names.length != 0) {
/* 212 */           String name = names[0];
/* 213 */           String[] command = { "cp", resultFile.getAbsolutePath(), 
/* 214 */             new File(alignDir, name + ".align").getAbsolutePath() };
/* 215 */           StringWriter err = new StringWriter();
/* 216 */           StringWriter err1 = new StringWriter();
/* 217 */           ProcessTools.exec(command, null, err, err1);
/* 218 */           System.out.println(err.getBuffer().toString());
/* 219 */           System.err.println(err1.getBuffer().toString());
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public static void mainCluster(String[] args) throws Exception
/*     */   {
/* 227 */     File dir = new File(".");
/* 228 */     File clusterDir = new File(dir, "cluster");
/* 229 */     File alignDir = new File(dir, "align");
/* 230 */     File[] files = clusterDir.listFiles();
/* 231 */     File lsfdir = new File(dir, "lsf");
/* 232 */     lsfdir.mkdir();
/* 233 */     PrintWriter summary = new PrintWriter(new FileWriter(new File("summ")));
/* 234 */     int index = 1;
/* 235 */     for (int i = 0; i < files.length; i++)
/*     */     {
/* 237 */       File targetDir = new File(lsfdir, index);
/* 238 */       targetDir.mkdir();
/* 239 */       for (; (i < files.length) && (targetDir.list().length < size); i++) {
/* 240 */         File alignOut = new File(alignDir, files[i].getName() + ".align");
/*     */         
/* 242 */         if ((!alignOut.exists()) || (alignOut.length() <= 0L))
/*     */         {
/*     */ 
/*     */ 
/* 246 */           if (files[i].length() != 0L) {
/* 247 */             File out = new File(targetDir, files[i].getName());
/*     */             
/*     */             try
/*     */             {
/* 251 */               BufferedReader in = new BufferedReader(new FileReader(files[i]));
/* 252 */               int count = 0;
/* 253 */               for (SequenceIterator seqIt = SeqIOTools.readFastaProtein(in); seqIt.hasNext(); seqIt.nextSequence()) {
/* 254 */                 count++;
/*     */               }
/*     */               
/* 257 */               in.close();
/* 258 */               if (count >= 3)
/*     */               {
/*     */ 
/*     */ 
/*     */ 
/* 263 */                 BufferedReader in1 = new BufferedReader(new FileReader(files[i]));
/* 264 */                 OutputStream os = new BufferedOutputStream(new FileOutputStream(out));
/* 265 */                 SequenceIterator seqIt1 = SeqIOTools.readFastaProtein(in1);
/* 266 */                 summary.println(index + "\t" + files[i].getName());
/* 267 */                 summary.flush();
/* 268 */                 while (seqIt1.hasNext()) {
/*     */                   try
/*     */                   {
/* 271 */                     Sequence seq = seqIt1.nextSequence();
/* 272 */                     String name = seq.getName().trim().split("\\s+")[0];
/*     */                     
/* 274 */                     Sequence seq1 = new SimpleSequence(seq, name, name, Annotation.EMPTY_ANNOTATION);
/* 275 */                     SeqIOTools.writeFasta(os, seq1);
/*     */                   } catch (Exception exc) {
/* 277 */                     exc.printStackTrace();
/* 278 */                     System.err.println(files[i].getName());
/*     */                   }
/*     */                 }
/*     */                 
/* 282 */                 os.close();
/* 283 */                 in.close();
/*     */               }
/* 285 */             } catch (Exception exc) { exc.printStackTrace();
/*     */             }
/*     */           } } }
/* 288 */       index++;
/*     */     }
/* 290 */     summary.close();
/*     */   }
/*     */   
/*     */   public static void mainPhyml(String[] args)
/*     */     throws Exception
/*     */   {
/* 296 */     File dir = new File(".");
/* 297 */     File alignDir = new File(dir, "align");
/* 298 */     File treeDir = new File(dir, "tree");
/* 299 */     File[] files = alignDir.listFiles();
/* 300 */     File lsfdir = new File(dir, "lsf_phyml");
/* 301 */     lsfdir.mkdir();
/* 302 */     PrintWriter summary = new PrintWriter(new FileWriter(new File("summ")));
/* 303 */     int index = 1;
/* 304 */     for (int i = 0; i < files.length; i++)
/*     */     {
/* 306 */       File targetDir = new File(lsfdir, index);
/* 307 */       targetDir.mkdir();
/* 308 */       for (; (i < files.length) && (targetDir.list().length < size); i++) {
/* 309 */         File treeOut = new File(treeDir, files[i].getName() + "_phyml_tree.txt");
/*     */         
/* 311 */         if ((!treeOut.exists()) || (treeOut.length() <= 0L))
/*     */         {
/*     */ 
/*     */ 
/* 315 */           if (files[i].length() != 0L) {
/* 316 */             File out = new File(targetDir, files[i].getName());
/*     */             try
/*     */             {
/* 319 */               Alignment align = new ReadAlignment(files[i].getAbsolutePath());
/*     */               
/*     */ 
/* 322 */               PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(out)));
/* 323 */               AlignmentUtils.printInterleaved(align, pw);
/* 324 */               pw.close();
/*     */             } catch (Exception exc) {
/* 326 */               exc.printStackTrace();
/*     */             }
/*     */           } } }
/* 329 */       index++;
/*     */     }
/* 331 */     summary.close();
/*     */   }
/*     */   
/*     */   public static void main21(String[] args)
/*     */     throws Exception
/*     */   {
/* 337 */     File dir = new File(".");
/* 338 */     SequenceIterator it = SeqIOTools.readFastaProtein(new BufferedReader(new FileReader("astral")));
/* 339 */     Set strings = new HashSet();
/* 340 */     File lsfdir = new File(dir, "lsf");
/* 341 */     lsfdir.mkdir();
/* 342 */     Iterator it1 = SheetIO.read(new File("blast_res"), "\\s+");
/* 343 */     while (it1.hasNext()) {
/* 344 */       strings.add(((List)it1.next()).get(0));
/*     */     }
/*     */     
/* 347 */     int i = 0;
/* 348 */     int index = 1;
/* 349 */     while (it.hasNext()) {
/* 350 */       File outDir = new File(lsfdir, index);
/* 351 */       OutputStream os = new BufferedOutputStream(new FileOutputStream(outDir));
/* 352 */       while ((it.hasNext()) && (i < size)) {
/* 353 */         Sequence seq = it.nextSequence();
/* 354 */         if (!strings.contains(seq.getName())) {
/* 355 */           SeqIOTools.writeFasta(os, seq);
/* 356 */           i++;
/*     */         }
/*     */       }
/* 359 */       os.close();
/* 360 */       i = 0;
/* 361 */       index++;
/*     */     }
/*     */   }
/*     */ }


/* Location:              /home/lachlan/Desktop/psilc1.21/psilc.jar!/lc1/util/MakeLSFDirectories.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */