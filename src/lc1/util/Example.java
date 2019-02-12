/*     */ package lc1.util;
/*     */ 
/*     */ import java.io.BufferedOutputStream;
/*     */ import java.io.BufferedReader;
/*     */ import java.io.File;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.FileReader;
/*     */ import java.io.IOException;
/*     */ import java.io.PrintStream;
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
/*     */ import org.ensembl.datamodel.Analysis;
/*     */ import org.ensembl.datamodel.ArchiveStableID;
/*     */ import org.ensembl.datamodel.CoordinateSystem;
/*     */ import org.ensembl.datamodel.Exon;
/*     */ import org.ensembl.datamodel.Gene;
/*     */ import org.ensembl.datamodel.GeneSnapShot;
/*     */ import org.ensembl.datamodel.KaryotypeBand;
/*     */ import org.ensembl.datamodel.Location;
/*     */ import org.ensembl.datamodel.SequenceRegion;
/*     */ import org.ensembl.datamodel.Transcript;
/*     */ import org.ensembl.datamodel.TranscriptSnapShot;
/*     */ import org.ensembl.datamodel.Translation;
/*     */ import org.ensembl.datamodel.TranslationSnapShot;
/*     */ import org.ensembl.datamodel.impl.SimplePeptideFeatureImpl;
/*     */ import org.ensembl.driver.AdaptorException;
/*     */ import org.ensembl.driver.ConfigurationException;
/*     */ import org.ensembl.driver.Driver;
/*     */ import org.ensembl.driver.DriverManager;
/*     */ import org.ensembl.driver.ExonAdaptor;
/*     */ import org.ensembl.driver.GeneAdaptor;
/*     */ import org.ensembl.driver.KaryotypeBandAdaptor;
/*     */ import org.ensembl.driver.LocationConverter;
/*     */ import org.ensembl.driver.SequenceRegionAdaptor;
/*     */ import org.ensembl.driver.SimplePeptideAdaptor;
/*     */ import org.ensembl.driver.StableIDEventAdaptor;
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
/*     */ public class Example
/*     */ {
/*     */   public static Map getDBNames(File phigsdb)
/*     */     throws Exception
/*     */   {
/* 107 */     BufferedReader br = new BufferedReader(new FileReader(phigsdb));
/* 108 */     String st = "";
/* 109 */     Map l = new HashMap();
/* 110 */     while ((st = br.readLine()) != null) {
/* 111 */       String[] str = st.split("\\s+");
/* 112 */       l.put(str[0], 
/* 113 */         new MySQLDriver("ensembldb.ensembl.org", 
/* 114 */         str[1], 
/* 115 */         "anonymous"));
/*     */     }
/*     */     
/* 118 */     return l;
/*     */   }
/*     */   
/*     */   static class Phigs {
/*     */     String id;
/* 123 */     boolean containsHuman = false;
/* 124 */     List genes = new ArrayList();
/* 125 */     List taxa = new ArrayList();
/*     */     
/*     */     Phigs(String id) {
/* 128 */       this.id = id;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private static Iterator readPhigsPfamSummary(File ph)
/*     */     throws Exception
/*     */   {
/* 137 */     BufferedReader br = new BufferedReader(new FileReader(ph));
/* 138 */     String first = br.readLine();
/* 139 */     new Iterator()
/*     */     {
/*     */ 
/* 142 */       public boolean hasNext() { return Example.this != null; }
/*     */       
/*     */       public Object next() {
/* 145 */         Example.Phigs phigs = new Example.Phigs(Example.this.split("\t")[0]);
/* 146 */         System.err.println(phigs.id);
/*     */         
/* 148 */         while ((Example.this != null) && (Example.this.startsWith(phigs.id)))
/*     */         {
/* 150 */           String[] st1 = Example.this.split("\\s+");
/* 151 */           phigs.genes.add(st1[2]);
/* 152 */           phigs.taxa.add(st1[3]);
/*     */           try {
/* 154 */             this.st = this.val$br.readLine();
/*     */           }
/*     */           catch (IOException e) {
/* 157 */             e.printStackTrace();
/*     */           }
/*     */         }
/* 160 */         return phigs;
/*     */       }
/*     */       
/*     */       public void remove() {}
/*     */     };
/*     */   }
/*     */   
/*     */   private static Iterator readPhigsSummary(File ph, String prevStr)
/*     */     throws Exception
/*     */   {
/* 170 */     BufferedReader br = new BufferedReader(new FileReader(ph));
/*     */     String first;
/* 172 */     String first; if (prevStr != null) {
/* 173 */       String st = br.readLine();
/* 174 */       String[] str = st.split("\\s+");
/* 175 */       while (!str[2].equals(prevStr)) {
/* 176 */         st = br.readLine();
/* 177 */         str = st.split("\\s+");
/*     */       }
/* 179 */       String id = str[0];
/* 180 */       while (str[0].equals(id)) {
/* 181 */         st = br.readLine();
/* 182 */         str = st.split("\\s+");
/*     */       }
/* 184 */       first = st;
/*     */     }
/*     */     else {
/* 187 */       first = br.readLine();
/*     */     }
/* 189 */     new Iterator()
/*     */     {
/*     */ 
/* 192 */       public boolean hasNext() { return Example.this != null; }
/*     */       
/*     */       public Object next() {
/* 195 */         Example.Phigs phigs = new Example.Phigs(Example.this.split("\\s+")[0]);
/* 196 */         System.err.println(phigs.id);
/*     */         
/* 198 */         while ((Example.this != null) && (Example.this.startsWith(phigs.id)))
/*     */         {
/* 200 */           String[] st1 = Example.this.split("\\s+");
/* 201 */           phigs.genes.add(st1[2]);
/* 202 */           phigs.taxa.add(st1[3]);
/* 203 */           if (st1[3].equals("Human")) phigs.containsHuman = true;
/*     */           try {
/* 205 */             this.st = this.val$br.readLine();
/*     */           }
/*     */           catch (IOException e) {
/* 208 */             e.printStackTrace();
/*     */           }
/*     */         }
/* 211 */         return phigs;
/*     */       }
/*     */       
/*     */       public void remove() {}
/*     */     };
/*     */   }
/*     */   
/* 218 */   public static void consistentPfam() throws Exception { File dirOut = new File("/nfs/team71/phd/lc1/Data/");
/* 219 */     File summ = new File(dirOut, "summary_lc1");
/* 220 */     int cons = 0;
/* 221 */     int incons = 0;
/* 222 */     for (Iterator phigs = readPhigsPfamSummary(summ); phigs.hasNext();) {
/* 223 */       Phigs phig = (Phigs)phigs.next();
/* 224 */       for (int i = 1; i < phig.taxa.size(); i++) {
/* 225 */         if (!phig.taxa.get(0).equals(phig.taxa.get(i))) {
/* 226 */           incons++;
/* 227 */           break;
/*     */         }
/*     */       }
/* 230 */       cons++;
/*     */     }
/* 232 */     System.err.println("cons " + cons + " incons " + incons);
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
/*     */   public static void main(String[] args)
/*     */     throws Exception
/*     */   {
/* 247 */     File dirOut = new File("/nfs/team71/phd/lc1/Data/");
/* 248 */     PfamAlphabet pfam_alph = PfamAlphabet.makeAlphabet(new File("/nfs/team71/phd/lc1/Data/lc1"));
/* 249 */     SymbolTokenization token = pfam_alph.getTokenization("token");
/* 250 */     File summ = new File(dirOut, "summary_lc1");
/*     */     
/* 252 */     PrintStream summary = 
/* 253 */       new PrintStream(new BufferedOutputStream(new FileOutputStream(summ)));
/* 254 */     Map drivers = getDBNames(new File("//nfs/team71/phd/lc1/java/ensj/resources/data/databases_phigs"));
/* 255 */     long time = System.currentTimeMillis();
/* 256 */     int j = 1;
/* 257 */     File parent = new File(dirOut, "families_phigs");
/* 258 */     if (!parent.exists()) parent.mkdir();
/* 259 */     String nextStr = null;
/*     */     
/* 261 */     int last = 0;
/* 262 */     String[] str = parent.list();
/* 263 */     for (int i = 0; i < str.length; i++) {
/* 264 */       int curr = Integer.parseInt(str[i]);
/* 265 */       if (curr > last) last = curr;
/*     */     }
/* 267 */     System.err.println("last is " + last);
/* 268 */     if (last > 0) {
/* 269 */       File f_l = new File(parent, last);
/* 270 */       System.err.println(f_l.getAbsolutePath());
/* 271 */       System.err.println(Arrays.asList(f_l.list()));
/* 272 */       SequenceIterator seqIt = SeqIOTools.readFastaDNA(
/* 273 */         new BufferedReader(new FileReader(new File(f_l, "/seed.dna.fa"))));
/* 274 */       org.biojava.bio.seq.Sequence seq = seqIt.nextSequence();
/* 275 */       nextStr = seq.getName();
/*     */     }
/* 277 */     j = last + 1;
/*     */     
/*     */ 
/* 280 */     for (Iterator phigs = readPhigsSummary(new File("data/phigs_id.lst"), nextStr); phigs.hasNext();) {
/* 281 */       Phigs phig = (Phigs)phigs.next();
/*     */       
/*     */ 
/* 284 */       File dir = new File(parent, j);
/* 285 */       dir.mkdir();
/* 286 */       File out = new File(dir, "seed.dna.fa");
/* 287 */       File outPfam = new File(dir, "pfamA");
/* 288 */       System.err.println(j + "\t" + (System.currentTimeMillis() - time));
/* 289 */       Alphabet alph = DNATools.getDNA();
/*     */       
/* 291 */       Collection pfamset = new HashSet();
/* 292 */       Map seqs = new HashMap();
/* 293 */       for (int k = 0; k < phig.genes.size(); k++) {
/* 294 */         String id = (String)phig.genes.get(k);
/* 295 */         String taxa = (String)phig.taxa.get(k);
/* 296 */         Driver driver = (Driver)drivers.get(taxa);
/* 297 */         if (driver != null) {
/* 298 */           Translation translation = fetchByGeneID(driver, id);
/* 299 */           if (translation != null) {
/* 300 */             List pfam = new ArrayList();
/*     */             
/* 302 */             for (Iterator it = driver.getSimplePeptideAdaptor().fetch(translation).iterator(); it.hasNext();) {
/* 303 */               SimplePeptideFeatureImpl impl = (SimplePeptideFeatureImpl)it.next();
/* 304 */               if (impl.getAnalysis().getLogicalName().equals("Pfam")) {
/* 305 */                 pfam.add(impl.getDisplayName());
/*     */               }
/*     */             }
/*     */             
/* 309 */             summary.println(phig.id + "\t" + j + "\t" + id + "\t" + pfam);
/* 310 */             pfamset.addAll(pfam);
/* 311 */             String prot = translation.getSequence().getString();
/* 312 */             if (prot != null) {
/* 313 */               seqs.put(id, prot);
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */       
/*     */ 
/*     */ 
/* 321 */       if ((pfamset.size() != 0) && (seqs.keySet().size() != 0) && 
/* 322 */         (phig.containsHuman)) {
/* 323 */         PrintStream pw = 
/* 324 */           new PrintStream(new BufferedOutputStream(new FileOutputStream(out)));
/* 325 */         PrintStream pw1 = 
/* 326 */           new PrintStream(new BufferedOutputStream(new FileOutputStream(outPfam)));
/* 327 */         for (Iterator it = pfamset.iterator(); it.hasNext();) {
/*     */           try {
/* 329 */             Symbol sym = token.parseToken((String)it.next());
/* 330 */             pw1.print(sym.getName());pw1.print("\t");
/* 331 */             pw1.print(sym.getAnnotation().getProperty("pfamA_id"));pw1.print("\t");
/* 332 */             pw1.print(sym.getAnnotation().getProperty("ls_dom_thresh"));pw1.print("\t");
/* 333 */             pw1.print(sym.getAnnotation().getProperty("ls_seq_thresh"));pw1.print("\t");
/* 334 */             pw1.print(sym.getAnnotation().getProperty("modelLength"));pw1.println("\t");
/*     */           }
/*     */           catch (Exception exc) {
/* 337 */             exc.printStackTrace();
/*     */           }
/*     */         }
/* 340 */         for (Iterator it = seqs.keySet().iterator(); it.hasNext();) {
/* 341 */           Object id = it.next();
/* 342 */           Object prot = seqs.get(id);
/* 343 */           pw.print(">");pw.println(id);
/*     */           
/* 345 */           pw.println(prot);
/* 346 */           pw.println();
/*     */         }
/* 348 */         pw.close();
/* 349 */         pw1.close();
/*     */         
/* 351 */         summary.flush();
/* 352 */         j++;
/*     */       } }
/* 354 */     summary.close();
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
/*     */   public static void convertLocationToCoordinateSystemAndGetTheSeqRegionNames(Driver driver, Location sourceLoc, CoordinateSystem targetCS)
/*     */     throws AdaptorException
/*     */   {
/* 371 */     Location targetLoc = 
/* 372 */       driver.getLocationConverter().convert(sourceLoc, targetCS);
/*     */     
/* 374 */     System.out.println("Source Location = " + sourceLoc);
/* 375 */     System.out.println("Target Location = " + targetLoc);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 380 */     for (Location node = targetLoc; node != null; node = node.next()) {
/* 381 */       System.out.println(
/* 382 */         "Target Location Sequence Region = " + node.getSeqRegionName());
/*     */     }
/* 384 */     System.out.println();
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
/*     */   public static Driver createDriver(String configFilename)
/*     */     throws ConfigurationException
/*     */   {
/* 403 */     Driver driver = DriverManager.load(configFilename);
/* 404 */     return driver;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static void fetchAnExonByInternalID(Driver driver, int exonInternalID)
/*     */     throws AdaptorException
/*     */   {
/* 417 */     ExonAdaptor exonAdaptor = driver.getExonAdaptor();
/*     */     
/*     */ 
/*     */ 
/* 421 */     Exon exon = exonAdaptor.fetch(exonInternalID);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 426 */     System.out.println(
/* 427 */       "exon with internal id " + exonInternalID + " = " + exon + "\n");
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
/*     */   public static Location[] createLocations()
/*     */   {
/* 448 */     Location[] locations = new Location[1];
/*     */     
/*     */ 
/*     */ 
/* 452 */     locations[0] = new Location(new CoordinateSystem("chromosome"), "12", 
/*     */     
/* 454 */       1, 
/* 455 */       100000, 
/* 456 */       -1);
/*     */     
/* 458 */     return locations;
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
/*     */   public static void countGenesAndExonsInEachLocation(Driver driver, Location[] locations)
/*     */     throws AdaptorException
/*     */   {
/* 475 */     GeneAdaptor geneAdaptor = driver.getGeneAdaptor();
/*     */     
/*     */ 
/* 478 */     for (int i = 0; i < locations.length; i++)
/*     */     {
/* 480 */       System.out.println("Location = " + locations[i]);
/*     */       
/* 482 */       List genes = geneAdaptor.fetch(locations[i]);
/*     */       
/* 484 */       if ((genes == null) || (genes.size() == 0)) {
/* 485 */         System.out.println("No Genes found.");
/*     */       } else {
/* 487 */         int geneCount = 0;
/* 488 */         int exonCount = 0;
/* 489 */         Iterator iter = genes.iterator();
/* 490 */         while (iter.hasNext()) {
/* 491 */           Gene gene = (Gene)iter.next();
/* 492 */           geneCount++;
/* 493 */           exonCount += gene.getExons().size();
/*     */         }
/*     */         
/* 496 */         System.out.println("num genes = " + geneCount);
/* 497 */         System.out.println("num exons = " + exonCount);
/*     */       }
/*     */       
/*     */ 
/* 501 */       System.out.println();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static Translation fetchByGeneID(Driver driver, String geneID)
/*     */     throws AdaptorException
/*     */   {
/* 514 */     if (((geneID.startsWith("ENS")) || (geneID.startsWith("SINFR"))) && (geneID.indexOf('.') > 0)) {
/* 515 */       geneID = geneID.substring(0, geneID.indexOf('.'));
/*     */     }
/* 517 */     GeneAdaptor geneAdaptor = driver.getGeneAdaptor();
/* 518 */     Gene gene = null;
/*     */     try {
/* 520 */       gene = geneAdaptor.fetch(geneID);
/*     */     }
/*     */     catch (AdaptorException e) {
/* 523 */       e.printStackTrace();
/*     */     }
/* 525 */     if (gene == null) return null;
/* 526 */     List trans = gene.getTranscripts();
/* 527 */     if (trans.size() == 0) return null;
/* 528 */     return ((Transcript)trans.get(0)).getTranslation();
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
/*     */   public static void fetchGenesByLocation(Driver driver, Location location)
/*     */     throws AdaptorException
/*     */   {
/* 554 */     int nExons = 0;
/* 555 */     int nGenes = 0;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 562 */     List genes = driver.getGeneAdaptor().fetch(location);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 568 */     List genesWithChildren = driver.getGeneAdaptor().fetch(location, true);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 578 */     Iterator geneIterator = driver.getGeneAdaptor().fetchIterator(location, true);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 585 */     nExons = 0;
/* 586 */     nGenes = genes.size();
/* 587 */     int i = 0; for (int n = genes.size(); i < n; i++) {
/* 588 */       Gene g = (Gene)genes.get(i);
/* 589 */       nExons += g.getExons().size();
/*     */     }
/* 591 */     System.out.println(location.toString() + " has " + nGenes + " genes and " + nExons + " exons.");
/*     */     
/* 593 */     nExons = 0;
/* 594 */     nGenes = genesWithChildren.size();
/* 595 */     int i = 0; for (int n = genes.size(); i < n; i++) {
/* 596 */       Gene g = (Gene)genes.get(i);
/* 597 */       nExons += g.getExons().size();
/*     */     }
/* 599 */     System.out.println(location.toString() + " has " + nGenes + " genes and " + nExons + " exons.");
/*     */     
/* 601 */     nGenes = 0;
/* 602 */     nExons = 0;
/* 603 */     while (geneIterator.hasNext()) {
/* 604 */       nGenes++;
/* 605 */       nExons += ((Gene)geneIterator.next()).getExons().size();
/*     */     }
/*     */     
/* 608 */     System.out.println(location.toString() + " has " + nGenes + " genes and " + nExons + " exons.");
/* 609 */     System.out.println();
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
/*     */   public static void fetchDeletedGeneFromArchive(Driver driver, String geneStableID, int geneVersion)
/*     */     throws AdaptorException
/*     */   {
/* 627 */     StableIDEventAdaptor adaptor = driver.getStableIDEventAdaptor();
/*     */     
/*     */ 
/* 630 */     List relatedIDs = adaptor.fetchCurrent(geneStableID);
/* 631 */     for (Iterator iter = relatedIDs.iterator(); iter.hasNext();) {
/* 632 */       String relatedID = (String)iter.next();
/* 633 */       System.out.println(
/* 634 */         geneStableID + 
/* 635 */         " is related to " + 
/* 636 */         relatedID + 
/* 637 */         " in the current release.");
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 643 */     GeneSnapShot geneSnapshot = 
/* 644 */       adaptor.fetchGeneSnapShot(geneStableID, geneVersion);
/*     */     
/* 646 */     String gStableID = geneSnapshot.getArchiveStableID().getStableID();
/* 647 */     String gVersion = geneSnapshot.getArchiveStableID().getStableID();
/*     */     
/* 649 */     TranscriptSnapShot[] transcriptSnapShots = 
/* 650 */       geneSnapshot.getTranscriptSnapShots();
/* 651 */     for (int i = 0; i < transcriptSnapShots.length; i++)
/*     */     {
/* 653 */       TranscriptSnapShot tSnapShot = transcriptSnapShots[i];
/* 654 */       String tStableID = tSnapShot.getArchiveStableID().getStableID();
/* 655 */       int tVersion = tSnapShot.getArchiveStableID().getVersion();
/*     */       
/* 657 */       TranslationSnapShot tnSnapShot = tSnapShot.getTranslationSnapShot();
/* 658 */       String tnStableID = tnSnapShot.getArchiveStableID().getStableID();
/* 659 */       int tnVersion = tnSnapShot.getArchiveStableID().getVersion();
/*     */       
/* 661 */       System.out.println(
/* 662 */         gStableID + 
/* 663 */         "." + 
/* 664 */         gVersion + 
/* 665 */         " -> " + 
/* 666 */         tStableID + 
/* 667 */         "." + 
/* 668 */         tVersion + 
/* 669 */         " -> " + 
/* 670 */         tnStableID + 
/* 671 */         "." + 
/* 672 */         tnVersion);
/*     */       
/*     */ 
/* 675 */       String peptide = tnSnapShot.getPeptide();
/* 676 */       if (peptide != null) {
/* 677 */         System.out.println("Peptide: " + peptide);
/*     */       }
/*     */     }
/* 680 */     System.out.println();
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
/*     */   public static void fetchKaryotypes(Driver driver, CoordinateSystem coordinateSystem, String chromosomeName)
/*     */     throws AdaptorException
/*     */   {
/* 698 */     List l = 
/* 699 */       driver.getKaryotypeBandAdaptor().fetch(coordinateSystem, chromosomeName);
/*     */     
/* 701 */     System.out.println(
/* 702 */       "Chromosome " + chromosomeName + " has " + l.size() + " karyotypes.");
/*     */     
/* 704 */     KaryotypeBand kb = (KaryotypeBand)l.get(0);
/* 705 */     Location loc = kb.getLocation();
/* 706 */     int start = loc.getStart();
/* 707 */     int end = loc.getEnd();
/*     */     
/* 709 */     System.out.println(
/* 710 */       "The first karyotype on chromosome " + 
/* 711 */       chromosomeName + 
/* 712 */       " is from " + 
/* 713 */       start + 
/* 714 */       "bp to " + 
/* 715 */       end + 
/* 716 */       "bp.");
/*     */     
/* 718 */     System.out.println();
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
/*     */   public static void fetchSequenceRegionsSuchAsChromosomeOrContig(Driver driver, CoordinateSystem coordinateSystem)
/*     */     throws AdaptorException
/*     */   {
/* 734 */     SequenceRegion[] seqRegions = 
/* 735 */       driver.getSequenceRegionAdaptor().fetchAllByCoordinateSystem(
/* 736 */       coordinateSystem);
/* 737 */     System.out.println(
/* 738 */       "There are " + 
/* 739 */       seqRegions.length + 
/* 740 */       " sequence regions in the " + 
/* 741 */       coordinateSystem.getName() + 
/* 742 */       "." + 
/* 743 */       coordinateSystem.getVersion() + 
/* 744 */       " coordinate system.");
/*     */     
/* 746 */     SequenceRegion sr = seqRegions[0];
/* 747 */     System.out.println(coordinateSystem.getName() + " " + sr.getName() + " has length " + sr.getLength());
/*     */     
/* 749 */     System.out.println();
/*     */   }
/*     */ }


/* Location:              /home/lachlan/Desktop/psilc1.21/psilc.jar!/lc1/util/Example.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */