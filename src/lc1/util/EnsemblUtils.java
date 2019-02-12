/*     */ package lc1.util;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Properties;
/*     */ import org.apache.commons.cli.CommandLine;
/*     */ import org.biojava.bio.seq.SequenceIterator;
/*     */ import org.ensembl.datamodel.Exon;
/*     */ import org.ensembl.datamodel.Gene;
/*     */ import org.ensembl.datamodel.Location;
/*     */ import org.ensembl.datamodel.Transcript;
/*     */ 
/*     */ public class EnsemblUtils
/*     */ {
/*     */   public static final String JDBC_DRIVER = "jdbc_driver";
/*     */   public static final String HOST = "host";
/*     */   public static final String PORT = "port";
/*     */   public static final String USER = "user";
/*     */   public static final String PASSWORD = "password";
/*     */   public static final String ENSEMBL_DRIVER = "ensembl_driver";
/*     */   public static final String SIGNATURE = "signature";
/*     */   public static final String DATABASE = "database";
/*     */   org.ensembl.driver.Driver driver;
/*     */   org.ensembl.driver.Driver sequenceDriver;
/*     */   
/*     */   public static void main(String[] args) throws Exception
/*     */   {
/*  29 */     File in = new File(args[0]);
/*  30 */     File out = new File(args[1]);
/*  31 */     String rowSt = "0 \t 0 \t 0 \t fs \t 1 0 0";
/*  32 */     java.util.Set s = new java.util.TreeSet();
/*  33 */     for (Iterator it = SheetIO.read(in, ","); it.hasNext();) {
/*  34 */       List row = (List)it.next();
/*  35 */       StringBuffer sb = new StringBuffer(20);
/*  36 */       sb.append(row.get(1));sb.append("\t");
/*  37 */       sb.append(row.get(2));sb.append("\t");
/*  38 */       s.add(sb.toString());
/*     */     }
/*  40 */     java.io.PrintWriter pw = new java.io.PrintWriter(new java.io.BufferedWriter(new java.io.FileWriter(out)));
/*  41 */     for (Iterator it = s.iterator(); it.hasNext();) {
/*  42 */       pw.print(it.next());
/*  43 */       pw.println(rowSt);
/*     */     }
/*  45 */     pw.close();
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
/*     */   public EnsemblUtils(String database, String sequenceDatabase)
/*     */   {
/*     */     try
/*     */     {
/*  64 */       this.driver = getDriver(database);
/*     */       
/*  66 */       this.sequenceDriver = this.driver;
/*     */     }
/*     */     catch (org.ensembl.driver.ConfigurationException exc)
/*     */     {
/*  70 */       exc.printStackTrace();
/*  71 */       System.exit(0);
/*     */     }
/*     */   }
/*     */   
/*     */   public static org.ensembl.driver.Driver getDriver(String database) throws org.ensembl.driver.ConfigurationException {
/*  76 */     Properties properties = new Properties();
/*  77 */     properties.setProperty("jdbc_driver", "org.gjt.mm.mysql.Driver");
/*  78 */     properties.setProperty("host", "ecs2");
/*  79 */     properties.setProperty("user", "ensadmin");
/*  80 */     properties.setProperty("port", "3310");
/*  81 */     properties.setProperty("password", "ensembl");
/*  82 */     properties.setProperty("ensembl_driver", "org.ensembl.driver.plugin.standard.MySQLDriver");
/*  83 */     properties.setProperty("database", database);
/*     */     
/*  85 */     Properties properties1 = new Properties();
/*  86 */     properties1.setProperty("host", "kaka.sanger.ac.uk");
/*  87 */     properties1.setProperty("user", "anonymous");
/*  88 */     properties1.setProperty("port", "3306");
/*  89 */     properties1.setProperty("ensembl_driver", "org.ensembl.driver.plugin.standard.MySQLDriver");
/*  90 */     properties1.setProperty("database", database);
/*     */     
/*  92 */     return org.ensembl.driver.DriverManager.load(properties);
/*     */   }
/*     */   
/*     */   public EnsemblUtils(String database) {
/*  96 */     this(database, database);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/* 101 */   public static String[] databases = { "homo_sapiens_core_18_34", "", "", "fugu_rubripes_core_18_2", 
/* 102 */     "mus_musculus_core_18_30", "rattus_norvegicus_core_18_3", "danio_rerio_core_18_2" };
/* 103 */   public static String[] names = { "ENS", "", "", "SINFRU", "ENSMUS", "ENSRNO", "ENSDAR" };
/*     */   
/* 105 */   public static String[] vegadb = { "otter_merged_end_jul", "lachlan_ensembl_vega_ncbi34", 
/* 106 */     "homo_sapiens_vega_18_34" };
/*     */   
/*     */   public SequenceIterator getBlastXHits(double evalue) throws Exception
/*     */   {
/* 110 */     org.ensembl.datamodel.Analysis analysis = this.driver.getAnalysisAdaptor().fetchByLogicalName("Swall");
/* 111 */     org.ensembl.driver.DnaProteinAlignmentAdaptor adaptor = this.driver.getDnaProteinAlignmentAdaptor();
/* 112 */     org.ensembl.datamodel.AssemblyLocation location = new org.ensembl.datamodel.AssemblyLocation();
/* 113 */     location.setChromosome("6");
/* 114 */     java.util.Collection alignmentSet = adaptor.fetch(location, analysis);
/*     */     
/* 116 */     Iterator aligns = alignmentSet.iterator();
/* 117 */     new SequenceIterator() {
/*     */       boolean hasNext;
/*     */       org.biojava.bio.seq.Sequence current;
/*     */       
/* 121 */       public boolean hasNext() { return this.hasNext; }
/*     */       
/*     */       public org.biojava.bio.seq.Sequence getNext() throws Exception
/*     */       {
/* 125 */         org.ensembl.datamodel.DnaProteinAlignment align = (org.ensembl.datamodel.DnaProteinAlignment)this.val$aligns.next();
/* 126 */         while ((this.val$aligns.hasNext()) && (align != null) && 
/* 127 */           (!align.getAnalysis().getLogicalName().equals("Swall")) && 
/* 128 */           (align.getEvalue() > this.val$evalue)) {
/* 129 */           align = (org.ensembl.datamodel.DnaProteinAlignment)this.val$aligns.next();
/*     */         }
/* 131 */         org.biojava.bio.seq.Sequence seq = 
/* 132 */           org.biojava.bio.seq.DNATools.createDNASequence(align.getSequence().getString(), 
/* 133 */           align.getInternalID() + " ");
/* 134 */         seq.getAnnotation().setProperty("SPTREMBL", align.getDisplayName());
/* 135 */         this.hasNext = (align != null);
/* 136 */         return seq;
/*     */       }
/*     */       
/* 139 */       public org.biojava.bio.seq.Sequence nextSequence() { org.biojava.bio.seq.Sequence previous = this.current;
/*     */         try {
/* 141 */           this.current = getNext();
/*     */         }
/*     */         catch (Exception exc) {
/* 144 */           exc.printStackTrace();
/*     */         }
/* 146 */         return previous;
/*     */       }
/*     */     };
/*     */   }
/*     */   
/*     */   public static void getExtendedSequenceDB(CommandLine params) throws Exception
/*     */   {
/* 153 */     File f = new File("cluster");
/* 154 */     File out = new File("extendedFasta");
/* 155 */     java.util.Map pseudo = new java.util.HashMap();
/* 156 */     SheetIO.toMap(SheetIO.read(new File(params.getOptionValue("input")), "\\s+"), 2, 0, pseudo);
/* 157 */     File[] files = f.listFiles();
/* 158 */     org.biojava.bio.seq.db.SequenceDB seqDB = new org.biojava.bio.seq.db.HashSequenceDB();
/* 159 */     for (int i = 0; i < files.length; i++) {
/* 160 */       if ((pseudo.containsKey(files[i].getName())) && 
/* 161 */         (Integer.parseInt((String)pseudo.get(files[i].getName())) == 3)) {
/* 162 */         SequenceIterator seqIt = 
/* 163 */           org.biojava.bio.seq.io.SeqIOTools.readFastaDNA(new java.io.BufferedReader(new java.io.FileReader(files[i])));
/* 164 */         while (seqIt.hasNext()) {
/* 165 */           org.biojava.bio.seq.Sequence seq1 = seqIt.nextSequence();
/* 166 */           if (!seqDB.ids().contains(seq1.getName())) {
/* 167 */             seqDB.addSequence(seq1);
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/* 172 */     java.io.OutputStream os = new java.io.BufferedOutputStream(new java.io.FileOutputStream(out));
/* 173 */     org.biojava.bio.seq.io.SeqIOTools.writeFasta(os, seqDB.sequenceIterator());
/*     */   }
/*     */   
/*     */   public SequenceIterator getGeneTranscripts(boolean swissprot, String chromosome) throws Exception {
/* 177 */     org.ensembl.driver.GeneAdaptor adaptor = this.driver.getGeneAdaptor();
/* 178 */     org.ensembl.datamodel.AssemblyLocation location = new org.ensembl.datamodel.AssemblyLocation();
/* 179 */     location.setChromosome(chromosome);
/*     */     
/* 181 */     org.ensembl.datamodel.Query query = new org.ensembl.datamodel.Query(location);
/*     */     
/*     */ 
/* 184 */     java.util.Collection genesSet = adaptor.fetch(query);
/*     */     
/* 186 */     Iterator genes = genesSet.iterator();
/* 187 */     new SequenceIterator()
/*     */     {
/*     */       Iterator transcripts;
/*     */       
/*     */       String id;
/*     */       
/*     */       Gene gene;
/*     */       String type;
/*     */       int strand;
/*     */       
/* 197 */       public boolean hasNext() { return (this.val$genes.hasNext()) || ((this.transcripts != null) && (this.transcripts.hasNext())); }
/*     */       
/*     */       public org.biojava.bio.seq.Sequence nextSequence() {
/*     */         try {
/* 201 */           if ((this.transcripts == null) || (!this.transcripts.hasNext()))
/*     */           {
/* 203 */             this.gene = ((Gene)this.val$genes.next());
/* 204 */             this.strand = this.gene.getLocation().getStrand();
/* 205 */             this.transcripts = this.gene.getTranscripts().iterator();
/*     */           }
/*     */           
/* 208 */           Transcript transcript = (Transcript)this.transcripts.next();
/* 209 */           this.id = transcript.getAccessionID();
/*     */           
/*     */ 
/* 212 */           org.ensembl.datamodel.Translation trans = transcript.getTranslation();
/* 213 */           String st; String st; if ((trans != null) && (trans.getSequence() != null)) {
/* 214 */             st = trans.getSequence().getString();
/*     */ 
/*     */           }
/* 217 */           else if (this.gene.getType().equals("Pseudogene")) {
/* 218 */             st = EnsemblUtils.this.glueExons(transcript, this.strand);
/*     */           } else {
/* 220 */             return null;
/*     */           }
/*     */           
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 227 */           org.biojava.bio.seq.Sequence seq = org.biojava.bio.seq.DNATools.createDNASequence(st, this.id);
/* 228 */           seq.getAnnotation().setProperty("type", this.gene.getType());
/* 229 */           if (this.val$swissprot) { String st;
/* 230 */             for (Iterator refs = transcript.getExternalRefs().iterator(); 
/* 231 */                   refs.hasNext();) {
/* 232 */               org.ensembl.datamodel.impl.ExternalRefImpl external = (org.ensembl.datamodel.impl.ExternalRefImpl)refs.next();
/* 233 */               if (external.getExternalDatabase().getName().equals("SPTREMBL")) {
/* 234 */                 seq.getAnnotation().setProperty("SPTREMBL", external.getDisplayID());
/* 235 */                 break;
/*     */               }
/*     */             }
/*     */           }
/* 239 */           return seq;
/*     */         } catch (org.biojava.bio.symbol.IllegalSymbolException exc) {
/* 241 */           exc.printStackTrace();
/* 242 */           return null;
/*     */         } catch (org.ensembl.driver.AdaptorException exc) {
/* 244 */           exc.printStackTrace();
/* 245 */           return null;
/*     */         }
/*     */         catch (org.biojava.utils.ChangeVetoException exc) {
/* 248 */           exc.printStackTrace(); }
/* 249 */         return null;
/*     */       }
/*     */     };
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static void main(CommandLine params)
/*     */     throws Exception
/*     */   {
/* 259 */     String[] mode = params.getOptionValues("mode");
/* 260 */     if (mode[1].equals("vega")) { getVegaGenes(params);
/* 261 */     } else if (mode[1].equals("splice")) { getSpliceSites(params);
/* 262 */     } else if (mode[1].equals("clusters")) { getClusters(params);
/* 263 */     } else if (mode[1].equals("align")) { alignClusters(params);
/* 264 */     } else if (mode[1].equals("extend")) { getExtendedSequenceDB(params);
/*     */     }
/* 266 */     else if (mode[1].equals("ensembl")) { getEnsemblGenes(params);
/* 267 */     } else if (mode[1].equals("process")) { processEnsemblGenes(params);
/* 268 */     } else if (mode[1].equals("translate")) { translateDB(params);
/* 269 */     } else if (mode[1].equals("blastx")) { getBlastXHits(params);
/* 270 */     } else if (mode[1].equals("indices")) { createIndices(params);
/* 271 */     } else if (mode[1].equals("clusterAndAlign")) clusterAndAlign(params); else
/* 272 */       throw new Exception("no mode options appropriate " + Print.toString(mode));
/*     */   }
/*     */   
/*     */   public String glueExons(Transcript transcript, int strand) throws org.ensembl.driver.AdaptorException {
/* 276 */     org.ensembl.driver.SequenceAdaptor seqAdaptor = this.sequenceDriver.getSequenceAdaptor();
/* 277 */     StringBuffer buffer = new StringBuffer(transcript.getLength());
/* 278 */     boolean first = true;
/* 279 */     List utrs3 = transcript.getThreePrimeUTR();
/* 280 */     List utrs5 = transcript.getFivePrimeUTR();
/*     */     
/*     */ 
/* 283 */     for (int j = 0; j < transcript.getExons().size(); j++) {
/* 284 */       int start_diff = 0;
/*     */       
/* 286 */       Exon exon = (Exon)transcript.getExons().get(j);
/* 287 */       if (utrs5 != null) {
/* 288 */         for (Iterator it = utrs5.iterator(); it.hasNext();) {
/* 289 */           Location loc = (Location)it.next();
/* 290 */           if (strand * Float.compare(loc.getEnd(), exon.getLocation().getStart()) > 0) {
/* 291 */             start_diff = (loc.getEnd() - exon.getLocation().getStart()) * strand;
/* 292 */             if (start_diff > exon.getSequence().getString().length()) {
/*     */               break;
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/* 298 */       String str = seqAdaptor.fetch(exon.getLocation()).getString();
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
/* 310 */       if (utrs3 != null) {
/* 311 */         for (Iterator it = utrs3.iterator(); it.hasNext();) {
/* 312 */           Location loc = (Location)it.next();
/*     */           
/* 314 */           if (Float.compare(exon.getLocation().getEnd(), loc.getStart()) * strand >= 0) {
/* 315 */             int end_diff = (exon.getLocation().getEnd() - loc.getStart()) * strand;
/*     */             
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 322 */             int end = str.length();
/* 323 */             if (end - end_diff <= start_diff)
/*     */               break;
/* 325 */             str = str.substring(start_diff, end - end_diff);
/* 326 */             buffer.append(str);
/* 327 */             break;
/*     */           }
/*     */         }
/*     */       }
/* 331 */       str = str.substring(start_diff);
/* 332 */       buffer.append(str);
/*     */     }
/* 334 */     return buffer.toString();
/*     */   }
/*     */   
/*     */   public static void getSpliceSites(CommandLine params) throws Exception {
/* 338 */     int[] tables = { 0, 3, 4, 5, 6 };
/* 339 */     File outpDir = new File(params.getOptionValue("dir"));
/* 340 */     Iterator sh = SheetIO.read(new File(outpDir, "ensembl_feat.csv"), ",");
/*     */     
/*     */ 
/* 343 */     for (int i = 0; i < tables.length; i++) {
/* 344 */       List list = new java.util.ArrayList();
/* 345 */       SheetIO.toCollection(SheetIO.getColumn(sh, i), list);
/* 346 */       EnsemblUtils ens = new EnsemblUtils(databases[tables[i]]);
/* 347 */       java.io.OutputStream os = new java.io.BufferedOutputStream(new java.io.FileOutputStream(new File(outpDir, databases[tables[i]])));
/* 348 */       SequenceIterator seqIt = ens.getSpliceDonorAcceptorSites(list);
/* 349 */       org.biojava.bio.seq.io.SeqIOTools.writeFasta(os, seqIt);
/*     */     }
/*     */   }
/*     */   
/*     */   private static void createIndices(CommandLine params) {
/* 354 */     File index = new File(params.getOptionValue("dir", ".") + 
/* 355 */       "/" + params.getOptionValue("input"));
/* 356 */     java.io.FileFilter ff = new java.io.FileFilter() {
/*     */       public boolean accept(File f) {
/* 358 */         return f.getName().endsWith(".fasta");
/*     */       }
/* 360 */     };
/* 361 */     createIndices(index, ff);
/*     */   }
/*     */   
/*     */   private static void createIndices(File index, java.io.FileFilter ff) {
/* 365 */     FlatFileTools.createIndex(index, "fasta", "dna");
/* 366 */     File[] files = index.getParentFile().listFiles(ff);
/* 367 */     FlatFileTools.addFilesToIndex(index, files);
/*     */   }
/*     */   
/*     */   public static void translateDB(CommandLine params) throws Exception {
/* 371 */     String[] inp = params.getOptionValues("input");
/* 372 */     File outpDir = new File(params.getOptionValue("dir"));
/* 373 */     File in_file = new File(outpDir, inp[0]);
/* 374 */     File out_file = new File(outpDir, inp[1]);
/* 375 */     boolean translate = true;
/* 376 */     SequenceIterator seqIt = org.biojava.bio.seq.io.SeqIOTools.readFastaDNA(new java.io.BufferedReader(new java.io.FileReader(in_file)));
/* 377 */     java.io.OutputStream os = new java.io.BufferedOutputStream(new java.io.FileOutputStream(out_file));
/* 378 */     org.biojava.bio.symbol.Symbol termSym = org.biojava.bio.seq.ProteinTools.getTAlphabet().getTokenization("token").parseToken("*");
/* 379 */     while (seqIt.hasNext()) {
/*     */       try {
/* 381 */         org.biojava.bio.seq.Sequence seq = seqIt.nextSequence();
/*     */         
/*     */         org.biojava.bio.symbol.SymbolList sl;
/* 384 */         if (translate) {
/* 385 */           org.biojava.bio.symbol.SymbolList sl = org.biojava.bio.seq.GeneticCodes.translate(org.biojava.bio.seq.GeneticCodes.transcribe(seq));
/* 386 */           if (sl.symbolAt(sl.length()).equals(termSym))
/* 387 */             sl = sl.subList(1, sl.length() - 1);
/*     */         } else {
/* 389 */           sl = seq;
/*     */         }
/* 391 */         String name = seq.getName();
/* 392 */         int idx = name.indexOf('|');
/* 393 */         name = idx > 0 ? name.substring(0, idx) : name;
/* 394 */         org.biojava.bio.seq.Sequence seqP = new org.biojava.bio.seq.impl.SimpleSequence(sl, 
/* 395 */           name, name, org.biojava.bio.Annotation.EMPTY_ANNOTATION);
/* 396 */         org.biojava.bio.seq.io.SeqIOTools.writeFasta(os, seqP);
/*     */       }
/*     */       catch (Exception exc)
/*     */       {
/* 400 */         exc.printStackTrace();
/* 401 */         os.flush();
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 406 */     os.flush();
/* 407 */     os.close();
/*     */   }
/*     */   
/*     */   public static void clusterAndAlign(CommandLine params) throws Exception {
/* 411 */     File outpDir = new File(params.getOptionValue("dir", "."));
/* 412 */     File repos = new File(params.getOptionValue("repository", "/nfs/team71/phd/lc1/Data/"));
/* 413 */     File db = new File(repos, "lc1/all_gene.fasta");
/* 414 */     File index = createIndices(db, "dna");
/* 415 */     String bin = params.getOptionValue("bin", "");
/* 416 */     String input = params.getOptionValue("input");
/* 417 */     SequenceIterator it = org.biojava.bio.seq.io.SeqIOTools.readFastaDNA(new java.io.BufferedReader(new java.io.FileReader(new File(input))));
/* 418 */     while (it.hasNext()) {
/* 419 */       org.biojava.bio.seq.Sequence localSequence = it.nextSequence();
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
/*     */   public static void getClusters(CommandLine params)
/*     */     throws Exception
/*     */   {
/* 440 */     File outpDir = new File(params.getOptionValue("dir", "."));
/* 441 */     File repos = new File(params.getOptionValue("repository", "/nfs/team71/phd/lc1/Data/"));
/* 442 */     File db = new File(repos, "lc1/all_gene.fasta");
/* 443 */     File index = createIndices(db, "dna");
/* 444 */     String bin = params.getOptionValue("bin", "");
/* 445 */     String[] input = params.getOptionValues("input");
/* 446 */     for (int i = 0; i < input.length; i++) {
/* 447 */       File localFile1 = new File(input[i]);
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
/*     */   public static void alignClusters(CommandLine params)
/*     */     throws Exception
/*     */   {
/* 461 */     File outpDir = new File(params.getOptionValue("dir", "."));
/*     */     
/* 463 */     File repos = new File(params.getOptionValue("repository", "/nfs/team71/phd/lc1/Data/"));
/* 464 */     String bin = params.getOptionValue("bin", "");
/* 465 */     int step = Integer.parseInt(params.getOptionValue("step", "100"));
/* 466 */     int min = step * (Integer.parseInt(params.getOptionValue("min", "1")) - 1);
/* 467 */     int max = min + step;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 476 */     String[] input = new File(outpDir, "cluster").list();
/*     */     
/* 478 */     for (int i = min; (i < max) && (i < input.length); i++) {
/*     */       try
/*     */       {
/* 481 */         file = new File(outpDir, "cluster/" + input[i]);
/*     */       } catch (Exception exc) {
/*     */         File file;
/* 484 */         exc.printStackTrace();
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public static File createIndices(File file, String type) {
/* 490 */     File index = new File(file.getAbsolutePath() + "_idx");
/* 491 */     if ((index.exists()) || (index.length() > 0L)) return index;
/* 492 */     FlatFileTools.createIndex(index, "fasta", type);
/* 493 */     FlatFileTools.addFilesToIndex(index, new File[] { file });
/* 494 */     return index;
/*     */   }
/*     */   
/*     */   private static org.biojava.bio.seq.db.SequenceDB getSequenceDB(CommandLine params) throws Exception {
/* 498 */     String repository = params.getOptionValue("repository");
/* 499 */     lc1.pfam.DomainAlphabet alph = lc1.pfam.PfamAlphabet.makeAlphabet(new File(repository));
/*     */     
/* 501 */     idToAcc = new java.util.HashMap();
/* 502 */     SheetIO.toMap(SheetIO.read(new File(repository, "lc1/pfamseq.list"), "\\s+"), 0, 1, idToAcc);
/* 503 */     Object key = idToAcc.keySet().iterator().next();
/*     */     
/* 505 */     return new lc1.pfam.IndexedPfamDB(new File(repository), alph);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/* 510 */   public static java.util.Map idToAcc = null;
/*     */   
/*     */   public static void getBlastXHits(CommandLine params) throws Exception
/*     */   {
/* 514 */     int[] tables = new int[1];
/* 515 */     double evalue = 0.01D;
/*     */     
/*     */ 
/*     */ 
/* 519 */     File outpDir = new File(params.getOptionValue("dir", "."));
/* 520 */     for (int i = 0; i < tables.length; i++) {
/* 521 */       EnsemblUtils ens = new EnsemblUtils(vegadb[tables[i]], databases[tables[i]]);
/* 522 */       SequenceIterator it = ens.getBlastXHits(evalue);
/* 523 */       printToFile(it, outpDir, vegadb[tables[i]]);
/*     */     }
/*     */   }
/*     */   
/*     */   public static void getVegaGenes(CommandLine params)
/*     */     throws Exception
/*     */   {
/* 530 */     int[] tables = { 1 };
/* 531 */     String[] chromosomes = params.getOptionValues("input");
/*     */     
/*     */ 
/*     */ 
/* 535 */     File outpDir = new File(params.getOptionValue("dir", "."));
/* 536 */     for (int i = 0; i < tables.length; i++) {
/* 537 */       for (int j = 0; j < chromosomes.length; j++) {
/* 538 */         EnsemblUtils ens = new EnsemblUtils(vegadb[tables[i]], 
/* 539 */           databases[tables[i]]);
/* 540 */         SequenceIterator it = ens.getGeneTranscripts(false, chromosomes[j]);
/*     */         
/*     */ 
/* 543 */         printToFile(it, outpDir, vegadb[tables[i]] + "_" + chromosomes[j]);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   private static void printToFile(SequenceIterator it, File outpDir, String name) throws Exception
/*     */   {
/* 550 */     java.util.Map os = new org.biojava.utils.SmallMap();
/*     */     
/* 552 */     while (it.hasNext()) {
/*     */       try {
/* 554 */         org.biojava.bio.seq.Sequence seq = it.nextSequence();
/* 555 */         if (seq != null) {
/* 556 */           org.biojava.bio.Annotation annot = seq.getAnnotation();
/* 557 */           String type = annot.containsProperty("type") ? (String)annot.getProperty("type") : "";
/* 558 */           if (!os.containsKey(type)) {
/* 559 */             os.put(type, 
/* 560 */               new java.io.PrintStream(new java.io.BufferedOutputStream(new java.io.FileOutputStream(new File(outpDir, name + "_" + type + ".fasta")))));
/*     */           }
/* 562 */           java.io.PrintStream ps = (java.io.PrintStream)os.get(type);
/* 563 */           ps.print(">" + seq.getName() + " ");
/* 564 */           if (annot.containsProperty("SPTREMBL")) {
/* 565 */             ps.print(annot.getProperty("SPTREMBL"));
/*     */           }
/*     */           
/*     */ 
/*     */ 
/*     */ 
/* 571 */           ps.println();
/* 572 */           ps.println(seq.seqString());
/* 573 */           ps.flush();
/*     */         }
/*     */       } catch (Exception exc) {
/* 576 */         exc.printStackTrace();
/*     */       }
/*     */     }
/*     */     
/* 580 */     for (Iterator it1 = os.values().iterator(); it1.hasNext();) {
/* 581 */       ((java.io.OutputStream)it1.next()).close();
/*     */     }
/*     */   }
/*     */   
/*     */   public static void processEnsemblGenes(CommandLine params) throws Exception {
/* 586 */     String[] input = params.getOptionValues("input");
/* 587 */     for (int i = 0; i < input.length; i++) {
/* 588 */       SequenceIterator seqIt = org.biojava.bio.seq.io.SeqIOTools.readFastaDNA(
/* 589 */         new java.io.BufferedReader(new java.io.FileReader(new File(input[i]))));
/* 590 */       java.io.PrintWriter pw = new java.io.PrintWriter(new java.io.BufferedWriter(new java.io.FileWriter(new File(input[i].split(".fasta")[0] + "_mod.fasta"))));
/* 591 */       while (seqIt.hasNext()) {
/* 592 */         org.biojava.bio.seq.Sequence seq = seqIt.nextSequence();
/* 593 */         pw.println(">" + seq.getName().split("\\|")[0]);
/* 594 */         pw.println(seq.seqString());
/*     */       }
/* 596 */       pw.flush();
/* 597 */       pw.close();
/*     */     }
/*     */   }
/*     */   
/*     */   public static void getEnsemblGenes(CommandLine params) throws Exception {
/* 602 */     int[] tables = { 0, 3, 4, 5, 6 };
/* 603 */     String[] chromosomes = params.getOptionValues("input");
/*     */     
/* 605 */     File outpDir = new File(params.getOptionValue("dir", "."));
/* 606 */     for (int i = 0; i < tables.length; i++) {
/* 607 */       for (int j = 0; j < chromosomes.length; j++) {
/* 608 */         EnsemblUtils ens = new EnsemblUtils(databases[tables[i]]);
/* 609 */         SequenceIterator it = ens.getGeneTranscripts(true, chromosomes[j]);
/* 610 */         printToFile(it, outpDir, databases[tables[i]] + "_" + chromosomes[j]);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public SequenceIterator getSpliceDonorAcceptorSites(List ids) throws Exception
/*     */   {
/* 617 */     int size = 15;
/* 618 */     org.ensembl.driver.GeneAdaptor adaptor = this.driver.getGeneAdaptor();
/*     */     
/* 620 */     Iterator genes = ids.iterator();
/* 621 */     java.util.Set done = new java.util.HashSet();
/* 622 */     new SequenceIterator()
/*     */     {
/*     */       Gene gene;
/*     */       
/*     */ 
/*     */ 
/*     */       String next;
/*     */       
/*     */ 
/*     */ 
/*     */       String id;
/*     */       
/*     */ 
/*     */       boolean hasNext;
/*     */       
/*     */ 
/*     */       boolean reverse;
/*     */       
/*     */ 
/* 641 */       public boolean hasNext() { return this.hasNext; }
/*     */       
/*     */       public org.biojava.bio.seq.Sequence nextSequence() {
/* 644 */         prepareNext();
/*     */         try {
/* 646 */           return org.biojava.bio.seq.DNATools.createDNASequence(this.next, this.id);
/*     */         } catch (org.biojava.bio.symbol.IllegalSymbolException exc) {
/* 648 */           exc.printStackTrace(); }
/* 649 */         return null;
/*     */       }
/*     */       
/*     */ 
/*     */       public void prepareNext()
/*     */       {
/* 655 */         List exons = null;
/*     */         
/* 657 */         while ((exons == null) || (1 >= exons.size()))
/*     */         {
/* 659 */           if (!this.val$genes.hasNext()) {
/* 660 */             this.hasNext = false;
/* 661 */             break;
/*     */           }
/*     */           try {
/* 664 */             String st = (String)this.val$genes.next();
/* 665 */             int ind = st.indexOf('.');
/* 666 */             st = st.substring(0, ind);
/* 667 */             if (this.val$done.contains(st)) continue;
/* 668 */             this.val$done.add(st);
/* 669 */             if (st.equals(""))
/*     */               continue;
/* 671 */             this.gene = this.val$adaptor.fetch(st);
/* 672 */             if (this.gene == null) {
/*     */               continue;
/*     */             }
/*     */           }
/*     */           catch (Exception exc)
/*     */           {
/*     */             continue;
/*     */           }
/*     */           
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 688 */           exons = this.gene.getExons();
/* 689 */           if (exons.size() > 1)
/*     */           {
/* 691 */             this.reverse = (((Exon)exons.get(1)).getLocation().getStart() < ((Exon)exons.get(0)).getLocation().getStart());
/* 692 */             if (this.reverse) {
/* 693 */               List exonsN = new java.util.ArrayList();
/* 694 */               for (int i = exons.size() - 1; i >= 0; i--) {
/* 695 */                 exonsN.add(exons.get(i));
/*     */               }
/* 697 */               exons = exonsN;
/*     */             }
/*     */           } }
/* 700 */         int[] intronStart = new int[exons.size() - 1];
/* 701 */         int[] intronEnd = new int[exons.size() - 1];
/* 702 */         this.id = this.gene.getAccessionID();
/* 703 */         for (int ie = 0; ie < intronStart.length; ie++) {
/* 704 */           int geneStart = this.gene.getLocation().getStart();
/* 705 */           Location locRight = ((Exon)exons.get(ie + 1)).getLocation();
/* 706 */           int posRight = locRight.getStart() - geneStart;
/* 707 */           Location locLeft = ((Exon)exons.get(ie)).getLocation();
/* 708 */           int posLeft = locLeft.getEnd() - geneStart;
/* 709 */           intronStart[ie] = posLeft;
/* 710 */           intronEnd[ie] = posRight;
/* 711 */           this.id = (this.id + "_" + posLeft + ":" + posRight);
/* 712 */           ie++;
/*     */         }
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
/* 730 */         this.next = this.gene.getSequence().getString();
/*     */       }
/*     */     };
/*     */   }
/*     */ }


/* Location:              /home/lachlan/Desktop/psilc1.21/psilc.jar!/lc1/util/EnsemblUtils.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */