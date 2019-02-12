/*     */ package lc1.util;
/*     */ 
/*     */ import java.io.BufferedOutputStream;
/*     */ import java.io.BufferedReader;
/*     */ import java.io.File;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.FileReader;
/*     */ import java.io.IOException;
/*     */ import java.io.PrintStream;
/*     */ import java.io.PrintWriter;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collection;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import lc1.pfam.PfamAlphabet;
/*     */ import org.biojava.bio.Annotation;
/*     */ import org.biojava.bio.seq.DNATools;
/*     */ import org.biojava.bio.seq.SequenceIterator;
/*     */ import org.biojava.bio.seq.io.SeqIOTools;
/*     */ import org.biojava.bio.seq.io.SymbolTokenization;
/*     */ import org.biojava.bio.symbol.Alphabet;
/*     */ import org.biojava.bio.symbol.Symbol;
/*     */ import org.biojava.utils.ProcessTools;
/*     */ import org.ensembl.datamodel.Analysis;
/*     */ import org.ensembl.datamodel.Gene;
/*     */ import org.ensembl.datamodel.Transcript;
/*     */ import org.ensembl.datamodel.Translation;
/*     */ import org.ensembl.datamodel.impl.SimplePeptideFeatureImpl;
/*     */ import org.ensembl.driver.AdaptorException;
/*     */ import org.ensembl.driver.ConfigurationException;
/*     */ import org.ensembl.driver.Driver;
/*     */ import org.ensembl.driver.DriverManager;
/*     */ import org.ensembl.driver.GeneAdaptor;
/*     */ import org.ensembl.driver.SimplePeptideAdaptor;
/*     */ import org.ensembl.driver.TranscriptAdaptor;
/*     */ import org.ensembl.driver.plugin.standard.MySQLDriver;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class Ensembl
/*     */ {
/*  53 */   static final String[] geneId = {
/*  54 */     "CG", 
/*  55 */     "ENSPTR", 
/*  56 */     "ENSGAL", 
/*  57 */     "ENSG0", 
/*  58 */     "ENSANG", 
/*  59 */     "ENSCBR", 
/*  60 */     "SINFRU", 
/*  61 */     "ENSRNO", 
/*  62 */     "ENSMUS", 
/*  63 */     "ENSDAR", 
/*  64 */     "C" };
/*     */   
/*     */ 
/*  67 */   static final String[] taxaId = {
/*  68 */     "7227", 
/*  69 */     "9598", 
/*  70 */     "9031", 
/*  71 */     "9606", 
/*  72 */     "180454", 
/*  73 */     "6238", 
/*  74 */     "31033", 
/*  75 */     "10116", 
/*  76 */     "10090", 
/*  77 */     "7955", 
/*  78 */     "6239" };
/*     */   
/*     */ 
/*     */   public static String getTaxaId(String gene)
/*     */   {
/*  83 */     for (int i = 0; i < geneId.length; i++) {
/*  84 */       if (gene.startsWith(geneId[i]))
/*  85 */         return taxaId[i];
/*     */     }
/*  87 */     return taxaId[(taxaId.length - 1)];
/*     */   }
/*     */   
/*     */   public static Map getDBNames(File phigsdb, String limit) throws Exception {
/*  91 */     BufferedReader br = new BufferedReader(new FileReader(phigsdb));
/*  92 */     String st = "";
/*  93 */     Map l = new HashMap();
/*  94 */     while ((st = br.readLine()) != null)
/*  95 */       if (!st.startsWith("#")) {
/*  96 */         String[] str = st.split("\\s+");
/*     */         
/*  98 */         System.err.println(st);
/*     */         try {
/* 100 */           l.put(str[0], 
/* 101 */             new MySQLDriver("ensembldb.ensembl.org", 
/* 102 */             str[1], 
/* 103 */             "anonymous"));
/*     */         }
/*     */         catch (Exception exc) {
/* 106 */           System.err.println("no db found for" + st);
/*     */         }
/*     */       }
/* 109 */     return l;
/*     */   }
/*     */   
/*     */   public static class Phigs {
/*     */     String id;
/* 114 */     boolean containsHuman = false;
/* 115 */     public List genes = new ArrayList();
/* 116 */     public List taxa = new ArrayList();
/*     */     
/*     */     public Phigs(String id) {
/* 119 */       this.id = id;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   private static Iterator readPhigsPfamSummary(File ph)
/*     */     throws Exception
/*     */   {
/* 127 */     BufferedReader br = new BufferedReader(new FileReader(ph));
/* 128 */     String first = br.readLine();
/* 129 */     new Iterator()
/*     */     {
/*     */ 
/* 132 */       public boolean hasNext() { return Ensembl.this != null; }
/*     */       
/*     */       public Object next() {
/* 135 */         Ensembl.Phigs phigs = new Ensembl.Phigs(Ensembl.this.split("\t")[0]);
/*     */         
/*     */ 
/* 138 */         while ((Ensembl.this != null) && (Ensembl.this.startsWith(phigs.id)))
/*     */         {
/* 140 */           String[] st1 = Ensembl.this.split("\\s+");
/* 141 */           phigs.genes.add(st1[2]);
/* 142 */           phigs.taxa.add(st1[3]);
/*     */           try {
/* 144 */             this.st = this.val$br.readLine();
/*     */           }
/*     */           catch (IOException e) {
/* 147 */             e.printStackTrace();
/*     */           }
/*     */         }
/* 150 */         return phigs;
/*     */       }
/*     */       
/*     */       public void remove() {}
/*     */     };
/*     */   }
/*     */   
/*     */   private static Iterator readPhigsSummary(File ph)
/*     */     throws Exception
/*     */   {
/* 160 */     BufferedReader br = new BufferedReader(new FileReader(ph));
/*     */     
/* 162 */     String first = br.readLine();
/* 163 */     new Iterator()
/*     */     {
/*     */ 
/* 166 */       public boolean hasNext() { return Ensembl.this != null; }
/*     */       
/*     */       public Object next() {
/* 169 */         Ensembl.Phigs phigs = new Ensembl.Phigs(Ensembl.this.split("\\s+")[0]);
/*     */         
/*     */ 
/* 172 */         while ((Ensembl.this != null) && (Ensembl.this.startsWith(phigs.id)))
/*     */         {
/* 174 */           String[] st1 = Ensembl.this.split("\\s+");
/* 175 */           phigs.genes.add(st1[2]);
/* 176 */           phigs.taxa.add(st1[3]);
/* 177 */           if (st1[3].equals("Human")) phigs.containsHuman = true;
/*     */           try {
/* 179 */             this.st = this.val$br.readLine();
/*     */           }
/*     */           catch (IOException e) {
/* 182 */             e.printStackTrace();
/*     */           }
/*     */         }
/* 185 */         return phigs;
/*     */       }
/*     */       
/*     */       public void remove() {}
/*     */     };
/*     */   }
/*     */   
/*     */   public static void main(String[] args) throws Exception
/*     */   {
/* 194 */     getPhigsClusters(args);
/*     */   }
/*     */   
/*     */   public static void consistentPfam() throws Exception
/*     */   {
/* 199 */     File dirOut = new File("/nfs/team71/phd/lc1/Data/");
/* 200 */     File summ = new File(dirOut, "summary_lc1");
/* 201 */     int cons = 0;
/* 202 */     int incons = 0;
/* 203 */     for (Iterator phigs = readPhigsPfamSummary(summ); phigs.hasNext();) {
/* 204 */       Phigs phig = (Phigs)phigs.next();
/* 205 */       for (int i = 1; i < phig.taxa.size(); i++) {
/* 206 */         if (!phig.taxa.get(0).equals(phig.taxa.get(i))) {
/* 207 */           incons++;
/* 208 */           System.err.println("incons " + phig.id);
/* 209 */           break;
/*     */         }
/*     */       }
/* 212 */       cons++;
/*     */     }
/* 214 */     System.err.println("cons " + cons + " incons " + incons);
/*     */   }
/*     */   
/*     */   public static void mv(String[] args) throws Exception
/*     */   {
/* 219 */     File dirOut = new File("/nfs/farm/Pfam/lc1/");
/* 220 */     File summ = new File(dirOut, "summary_lc1");
/* 221 */     String phigs = dirOut.getAbsolutePath() + "/families_phigs1/";
/* 222 */     Set s = new HashSet();
/* 223 */     for (Iterator it = SheetIO.read(summ, "\\s+"); it.hasNext();) {
/* 224 */       List l = (List)it.next();
/* 225 */       if (s.add(l.get(1))) {
/* 226 */         String[] command = { "mv", phigs + l.get(1), phigs + l.get(0) };
/* 227 */         System.err.println(Arrays.asList(command));
/* 228 */         ProcessTools.exec(command, null, new PrintWriter(System.out), new PrintWriter(System.err));
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public static void getPhigsClusters(String[] args)
/*     */     throws Exception
/*     */   {
/* 236 */     File dirOut = new File("/nfs/farm/Pfam/lc1");
/* 237 */     PfamAlphabet pfam_alph = PfamAlphabet.makeAlphabet(new File("/nfs/team71/phd/lc1/Data/lc1"));
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 243 */     Map drivers = getDBNames(new File("//nfs/team71/phd/lc1/java/ensj/resources/data/databases_phigs"), null);
/* 244 */     long time = System.currentTimeMillis();
/* 245 */     int j = 1;
/* 246 */     File parent = new File(dirOut, "families_phigs");
/* 247 */     if (!parent.exists()) { parent.mkdir();
/*     */     }
/* 249 */     for (Iterator phigs = readPhigsSummary(new File("/nfs/treefam/work/data/phigs_id.lst")); phigs.hasNext();) {
/* 250 */       Phigs phig = (Phigs)phigs.next();
/*     */       
/* 252 */       if (phig.id.equals("187687")) {
/* 253 */         File dir = new File(parent, phig.id);
/* 254 */         dir.mkdir();
/* 255 */         System.err.println(dir);
/*     */         do
/*     */         {
/* 258 */           Thread.currentThread();Thread.sleep(100L);Thread.currentThread();
/* 256 */         } while (Thread.activeCount() > 5);
/*     */         
/*     */ 
/*     */ 
/* 260 */         Runnable th = new Runnable()
/*     */         {
/*     */           public void run() {
/*     */             try {
/* 264 */               size = Ensembl.printPhigs(Ensembl.this, this.val$dir, this.val$drivers, null, this.val$pfam_alph);
/*     */             } catch (Exception exc) { int[] size;
/* 266 */               exc.printStackTrace();
/*     */             }
/*     */           }
/* 269 */         };
/* 270 */         th.run();
/*     */         
/*     */ 
/* 273 */         j++;
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public static int[] printPhigs(Phigs phig, File dir, Map drivers, PrintStream summary, Alphabet pfam_alph) throws Exception
/*     */   {
/* 280 */     SymbolTokenization token = pfam_alph.getTokenization("token");
/* 281 */     File out = new File(dir, "seed.dna.fa");
/* 282 */     File outPfam = new File(dir, "pfamA");
/* 283 */     Alphabet alph = DNATools.getDNA();
/* 284 */     Collection pfamset = new HashSet();
/* 285 */     Map seqs = new HashMap();
/* 286 */     if ((out.exists()) && (out.length() > 0L)) {
/* 287 */       BufferedReader fastaOs = new BufferedReader(new FileReader(out));
/* 288 */       SequenceIterator it = SeqIOTools.readFastaDNA(fastaOs);
/* 289 */       while (it.hasNext()) {
/* 290 */         org.biojava.bio.seq.Sequence sq = it.nextSequence();
/* 291 */         seqs.put(sq.getName(), sq.seqString());
/*     */       }
/* 293 */       fastaOs.close();
/*     */     }
/* 295 */     if ((outPfam.exists()) && (outPfam.length() > 0L)) {
/* 296 */       BufferedReader fastaOs = new BufferedReader(new FileReader(outPfam));
/* 297 */       String st = "";
/* 298 */       while ((st = fastaOs.readLine()) != null) {
/* 299 */         pfamset.add(st.split("\t")[0]);
/*     */       }
/* 301 */       fastaOs.close();
/*     */     }
/*     */     
/* 304 */     for (int k = 0; k < phig.genes.size(); k++) {
/* 305 */       String id = (String)phig.genes.get(k);
/* 306 */       String taxa = (String)phig.taxa.get(k);
/*     */       
/*     */ 
/*     */ 
/* 310 */       Driver driver = (Driver)drivers.get(taxa);
/* 311 */       if ((!seqs.containsKey(id)) && (driver != null)) {
/* 312 */         System.err.println("fetching " + id);
/* 313 */         Gene gene = fetchByGeneID(driver, id);
/* 314 */         if (gene == null) {
/* 315 */           System.err.println("is null " + id);
/*     */         }
/*     */         else {
/* 318 */           List trans = gene.getTranscripts();
/* 319 */           if (trans.size() == 0) {
/* 320 */             System.err.println("no transcripts " + id);
/*     */           }
/*     */           else {
/* 323 */             Translation translation = ((Transcript)trans.get(0)).getTranslation();
/* 324 */             if (translation == null) {
/* 325 */               System.err.println("no translation " + id);
/*     */             }
/*     */             else
/*     */             {
/* 329 */               List pfam = new ArrayList();
/*     */               
/* 331 */               for (Iterator it = driver.getSimplePeptideAdaptor().fetch(translation).iterator(); it.hasNext();) {
/* 332 */                 SimplePeptideFeatureImpl impl = (SimplePeptideFeatureImpl)it.next();
/* 333 */                 if (impl.getAnalysis().getLogicalName().equals("Pfam")) {
/* 334 */                   pfam.add(impl.getDisplayName());
/*     */                 }
/*     */               }
/*     */               
/* 338 */               if (summary != null) summary.println(phig.id + "\t" + dir.getName() + "\t" + id + "\t" + pfam);
/* 339 */               pfamset.addAll(pfam);
/* 340 */               String prot = translation.getSequence().getString();
/*     */               
/* 342 */               seqs.put(id, prot);
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 351 */     PrintStream pw = 
/* 352 */       new PrintStream(new BufferedOutputStream(new FileOutputStream(out)));
/* 353 */     PrintStream pw1 = 
/* 354 */       new PrintStream(new BufferedOutputStream(new FileOutputStream(outPfam)));
/* 355 */     for (Iterator it = pfamset.iterator(); it.hasNext();) {
/*     */       try {
/* 357 */         Symbol sym = token.parseToken((String)it.next());
/* 358 */         pw1.print(sym.getName());pw1.print("\t");
/* 359 */         pw1.print(sym.getAnnotation().getProperty("pfamA_id"));pw1.print("\t");
/* 360 */         pw1.print(sym.getAnnotation().getProperty("ls_dom_thresh"));pw1.print("\t");
/* 361 */         pw1.print(sym.getAnnotation().getProperty("ls_seq_thresh"));pw1.print("\t");
/* 362 */         pw1.print(sym.getAnnotation().getProperty("modelLength"));pw1.println("\t");
/*     */       }
/*     */       catch (Exception exc) {
/* 365 */         exc.printStackTrace();
/*     */       }
/*     */     }
/* 368 */     for (Iterator it = seqs.keySet().iterator(); it.hasNext();) {
/* 369 */       Object id = it.next();
/* 370 */       Object prot = seqs.get(id);
/* 371 */       pw.print(">");pw.println(id);
/*     */       
/* 373 */       pw.println(prot);
/* 374 */       pw.println();
/*     */     }
/* 376 */     pw.close();
/* 377 */     pw1.close();
/*     */     
/* 379 */     return new int[] { pfamset.size(), seqs.keySet().size() };
/*     */   }
/*     */   
/*     */   public static void rearrangeChrom6(String[] args)
/*     */     throws Exception
/*     */   {
/* 385 */     File dirOut = new File("/nfs/farm/Pfam/lc1/chrom6/");
/* 386 */     PfamAlphabet pfam_alph = PfamAlphabet.makeAlphabet(new File("/nfs/team71/phd/lc1/Data/lc1"));
/* 387 */     SymbolTokenization token = pfam_alph.getTokenization("token");
/* 388 */     Map drivers = getDBNames(new File("//nfs/team71/phd/lc1/java/ensj/resources/data/databases_phigs"), null);
/* 389 */     File parent = new File(dirOut, "cluster");
/* 390 */     File parent_new = new File(dirOut, "new");
/* 391 */     File[] f = parent.listFiles();
/* 392 */     int j = 1;
/* 393 */     for (int i = 0; i < f.length; i++)
/*     */     {
/* 395 */       BufferedReader br = new BufferedReader(new FileReader(f[i]));
/*     */       
/*     */ 
/* 398 */       Alphabet alph = DNATools.getDNA();
/* 399 */       Collection pfamset = new HashSet();
/* 400 */       Map seqs = new HashMap();
/*     */       
/* 402 */       for (SequenceIterator seqIt = SeqIOTools.readFastaDNA(br); seqIt.hasNext();) {
/* 403 */         org.biojava.bio.seq.Sequence seq = seqIt.nextSequence();
/* 404 */         String id = seq.getName();
/* 405 */         String prot = seq.seqString();
/* 406 */         seqs.put(id, prot);
/* 407 */         String taxa = 
/* 408 */           id.startsWith("ENSMUS") ? "Mouse" : id.startsWith("ENSRNO") ? "Rat" : "Human";
/* 409 */         Driver driver = (Driver)drivers.get(taxa);
/*     */         
/* 411 */         if (driver != null) {
/* 412 */           Translation translation = fetchByTranscriptID(driver, id);
/* 413 */           if (translation != null)
/*     */           {
/*     */ 
/*     */ 
/* 417 */             List pfam = new ArrayList();
/*     */             
/* 419 */             for (Iterator it = driver.getSimplePeptideAdaptor().fetch(translation).iterator(); it.hasNext();) {
/* 420 */               SimplePeptideFeatureImpl impl = (SimplePeptideFeatureImpl)it.next();
/* 421 */               if (impl.getAnalysis().getLogicalName().equals("Pfam")) {
/* 422 */                 pfam.add(impl.getDisplayName());
/*     */               }
/*     */             }
/*     */             
/* 426 */             pfamset.addAll(pfam);
/*     */           }
/*     */         }
/*     */         else {
/* 430 */           System.err.println("no db for " + taxa);
/*     */         }
/*     */       }
/*     */       
/* 434 */       if ((pfamset.size() != 0) && (seqs.keySet().size() >= 3))
/*     */       {
/* 436 */         File dir = new File(parent_new, j);
/*     */         
/* 438 */         if (!dir.exists()) dir.mkdir();
/* 439 */         File out = new File(dir, "seed.dna.fa");
/* 440 */         File outPfam = new File(dir, "pfamA");
/* 441 */         if ((!out.exists()) || (out.length() == 0L))
/*     */         {
/* 443 */           System.err.println("redoing " + out.getAbsolutePath());
/* 444 */           System.err.println(j);
/* 445 */           System.err.println(f[i].getName());
/* 446 */           PrintStream pw = 
/* 447 */             new PrintStream(new BufferedOutputStream(new FileOutputStream(out)));
/* 448 */           PrintStream pw1 = 
/* 449 */             new PrintStream(new BufferedOutputStream(new FileOutputStream(outPfam)));
/* 450 */           for (Iterator it = pfamset.iterator(); it.hasNext();) {
/*     */             try {
/* 452 */               Symbol sym = token.parseToken((String)it.next());
/* 453 */               pw1.print(sym.getName());pw1.print("\t");
/* 454 */               pw1.print(sym.getAnnotation().getProperty("pfamA_id"));pw1.print("\t");
/* 455 */               pw1.print(sym.getAnnotation().getProperty("ls_dom_thresh"));pw1.print("\t");
/* 456 */               pw1.print(sym.getAnnotation().getProperty("ls_seq_thresh"));pw1.print("\t");
/* 457 */               pw1.print(sym.getAnnotation().getProperty("modelLength"));pw1.println("\t");
/*     */             }
/*     */             catch (Exception exc) {
/* 460 */               exc.printStackTrace();
/*     */             }
/*     */           }
/* 463 */           for (Iterator it = seqs.keySet().iterator(); it.hasNext();) {
/* 464 */             Object id = it.next();
/* 465 */             Object prot = seqs.get(id);
/* 466 */             pw.print(">");pw.println(id);
/*     */             
/* 468 */             pw.println(prot);
/* 469 */             pw.println();
/*     */           }
/* 471 */           pw.close();
/* 472 */           pw1.close();
/* 473 */           br.close();
/*     */         }
/* 475 */         j++;
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static Driver createDriver(String configFilename)
/*     */     throws ConfigurationException
/*     */   {
/* 487 */     Driver driver = DriverManager.load(configFilename);
/* 488 */     return driver;
/*     */   }
/*     */   
/*     */   public static Translation fetchByTranscriptID(Driver driver, String transcriptID)
/*     */     throws AdaptorException
/*     */   {
/* 494 */     if (((transcriptID.startsWith("ENS")) || (transcriptID.startsWith("SINFR"))) && (transcriptID.indexOf('.') > 0)) {
/* 495 */       transcriptID = transcriptID.substring(0, transcriptID.indexOf('.'));
/*     */     }
/* 497 */     TranscriptAdaptor geneAdaptor = driver.getTranscriptAdaptor();
/* 498 */     Transcript gene = null;
/*     */     try {
/* 500 */       gene = geneAdaptor.fetch(transcriptID);
/*     */     }
/*     */     catch (AdaptorException e) {
/* 503 */       e.printStackTrace();
/*     */     }
/* 505 */     if (gene == null) { return null;
/*     */     }
/*     */     
/* 508 */     return gene.getTranslation();
/*     */   }
/*     */   
/*     */   public static Gene fetchByGeneID(Driver driver, String geneID)
/*     */     throws Exception
/*     */   {
/* 514 */     if (((geneID.startsWith("ENS")) || (geneID.startsWith("SINFR"))) && (geneID.indexOf('.') > 0)) {
/* 515 */       geneID = geneID.substring(0, geneID.indexOf('.'));
/*     */     }
/* 517 */     GeneAdaptor geneAdaptor = driver.getGeneAdaptor();
/* 518 */     Gene gene = null;
/* 519 */     gene = geneAdaptor.fetch(geneID);
/*     */     
/* 521 */     return gene;
/*     */   }
/*     */ }


/* Location:              /home/lachlan/Desktop/psilc1.21/psilc.jar!/lc1/util/Ensembl.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */