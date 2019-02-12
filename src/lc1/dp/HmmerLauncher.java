/*     */ package lc1.dp;
/*     */ 
/*     */ import java.io.BufferedInputStream;
/*     */ import java.io.BufferedOutputStream;
/*     */ import java.io.BufferedReader;
/*     */ import java.io.BufferedWriter;
/*     */ import java.io.File;
/*     */ import java.io.FileFilter;
/*     */ import java.io.FileNotFoundException;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.FileReader;
/*     */ import java.io.FileWriter;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.io.PrintStream;
/*     */ import java.io.PrintWriter;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collection;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import java.util.SortedSet;
/*     */ import java.util.TreeSet;
/*     */ import lc1.domainseq.Domain;
/*     */ import lc1.domainseq.Domain.Template;
/*     */ import lc1.domainseq.DomainList;
/*     */ import lc1.pfam.DomainAlphabet;
/*     */ import lc1.pfam.PfamAlphabet;
/*     */ import lc1.util.FlatFileTools;
/*     */ import lc1.util.SheetIO;
/*     */ import org.apache.commons.cli.CommandLine;
/*     */ import org.biojava.bio.Annotation;
/*     */ import org.biojava.bio.seq.Sequence;
/*     */ import org.biojava.bio.seq.SequenceIterator;
/*     */ import org.biojava.bio.seq.db.HashSequenceDB;
/*     */ import org.biojava.bio.seq.db.SequenceDB;
/*     */ import org.biojava.bio.seq.io.SeqIOTools;
/*     */ import org.biojava.bio.seq.io.SymbolTokenization;
/*     */ import org.biojava.bio.symbol.Alphabet;
/*     */ import org.biojava.bio.symbol.Location;
/*     */ import org.biojava.bio.symbol.LocationTools;
/*     */ import org.biojava.bio.symbol.Symbol;
/*     */ import org.biojava.bio.symbol.SymbolList;
/*     */ import pal.misc.Identifier;
/*     */ import pal.tree.Node;
/*     */ import pal.tree.Tree;
/*     */ import pal.tree.TreeUtils;
/*     */ 
/*     */ public class HmmerLauncher
/*     */ {
/*     */   Tree tree;
/*     */   double evalue;
/*     */   File outpDir;
/*     */   String hmmName;
/*     */   SequenceDB seqDB;
/*     */   Identifier rootID;
/*     */   Alphabet alph;
/*     */   
/*     */   public static Sequence parseHmmerResult(BufferedReader in, Sequence seq1, Alphabet alph, double evalue) throws Exception
/*     */   {
/*  65 */     String s = "";
/*  66 */     boolean hits = false;
/*  67 */     Sequence seq = new DomainList(seq1, seq1.getName(), seq1.getName(), seq1.getAnnotation());
/*  68 */     while ((s = in.readLine()) != null) {
/*  69 */       if (s.startsWith("Parsed for domains:")) {
/*  70 */         in.readLine();in.readLine();
/*  71 */         s = in.readLine();
/*  72 */         hits = true;
/*     */       }
/*  74 */       if (hits) {
/*  75 */         String[] st = s.split("\\s+");
/*  76 */         if (st.length <= 1) {
/*     */           break;
/*     */         }
/*  79 */         String name = st[0];
/*  80 */         double score = Double.parseDouble(st[8]);
/*  81 */         double eval = Double.parseDouble(st[9]);
/*  82 */         if (eval <= evalue) {
/*  83 */           int start = Integer.parseInt(st[2]);
/*  84 */           int end = Integer.parseInt(st[3]);
/*  85 */           Domain.Template ss = new Domain.Template();
/*  86 */           ss.score = score;ss.evalue = eval;
/*  87 */           ss.symbol = alph.getTokenization("token").parseToken(name.split("\\.")[0]);
/*  88 */           ss.location = LocationTools.makeLocation(start, end);
/*  89 */           seq.createFeature(ss);
/*     */         }
/*     */       } }
/*  92 */     in.close();
/*  93 */     return seq;
/*     */   }
/*     */   
/*     */   public static SequenceDB parseHmmerResult(BufferedReader in, SequenceDB db_in, Symbol sym, Alphabet alph, double evalue) throws Exception
/*     */   {
/*  98 */     String s = "";
/*  99 */     boolean hits = false;
/*     */     
/* 101 */     Map idToTemplateList = new HashMap();
/* 102 */     while ((s = in.readLine()) != null) {
/* 103 */       if (s.startsWith("Parsed for domains:")) {
/* 104 */         in.readLine();in.readLine();
/* 105 */         s = in.readLine();
/* 106 */         hits = true;
/*     */       }
/* 108 */       if (hits) {
/* 109 */         String[] st = s.split("\\s+");
/* 110 */         if (st.length <= 1) {
/*     */           break;
/*     */         }
/* 113 */         String name = st[0];
/* 114 */         if (db_in.ids().contains(name)) {
/* 115 */           Sequence seq = db_in.getSequence(name);
/* 116 */           String[] desc = ((String)seq.getAnnotation().getProperty("description")).trim().split("\\s+");
/* 117 */           int start_pos = desc.length == 2 ? Integer.parseInt(desc[1]) - 1 : 0;
/* 118 */           double score = Double.parseDouble(st[8]);
/* 119 */           double eval = Double.parseDouble(st[9]);
/* 120 */           if (eval <= evalue) {
/* 121 */             int start = Integer.parseInt(st[2]) + start_pos;
/* 122 */             int end = Integer.parseInt(st[3]) + start_pos;
/* 123 */             Domain.Template ss = new Domain.Template();
/* 124 */             ss.score = score;ss.evalue = eval;
/* 125 */             ss.symbol = sym;
/* 126 */             ss.location = LocationTools.makeLocation(start, end);
/* 127 */             Collection coll = (Collection)idToTemplateList.get(seq.getName());
/* 128 */             if (idToTemplateList.containsKey(seq.getName())) {
/* 129 */               coll.add(ss);
/*     */             }
/*     */             else {
/* 132 */               coll = new ArrayList();
/* 133 */               idToTemplateList.put(seq.getName(), coll);
/* 134 */               coll.add(ss);
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 142 */     while ((s = in.readLine()) != null) {
/* 143 */       if (s.startsWith("% Statistical details")) {
/* 144 */         String[] str = in.readLine().trim().split("\\s+");
/* 145 */         sym.getAnnotation().setProperty(str[0], str[2]);
/* 146 */         str = in.readLine().trim().split("\\s+");
/* 147 */         sym.getAnnotation().setProperty(str[0], str[2]);
/*     */       }
/* 149 */       if (s.startsWith("Total sequences searched")) {
/* 150 */         String[] str = s.trim().split("\\s+");
/*     */         
/* 152 */         sym.getAnnotation().setProperty("total seqs", str[3].trim());
/*     */       }
/*     */     }
/* 155 */     SequenceDB dbout = new HashSequenceDB();
/* 156 */     for (Iterator it = idToTemplateList.keySet().iterator(); it.hasNext();) {
/* 157 */       String id = (String)it.next();
/* 158 */       Sequence seq = db_in.getSequence(id);
/* 159 */       Domain.Template[] templates = (Domain.Template[])((Collection)idToTemplateList.get(id)).toArray(new Domain.Template[0]);
/*     */       
/*     */ 
/* 162 */       DomainList dl = new DomainList(seq, seq.getName(), seq.getName(), seq.getAnnotation(), templates);
/* 163 */       dbout.addSequence(dl);
/*     */     }
/*     */     
/* 166 */     in.close();
/* 167 */     return dbout;
/*     */   }
/*     */   
/*     */   public static void main(CommandLine params) throws Exception {
/* 171 */     String[] mode = params.getOptionValues("mode");
/* 172 */     if (mode[1].equals("index")) {
/* 173 */       createIndexedDBFromHmmerFiles(params);
/*     */     }
/* 175 */     else if (mode[1].equals("run")) {
/* 176 */       runHmmerOnList(params);
/* 177 */     } else if (mode[0].equals("thmmsearch")) {
/* 178 */       runTreeHMM(params);
/*     */     } else {
/* 180 */       throw new Exception(mode[1] + " not valid option");
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
/* 191 */   String sequencePath = "pfamseq";
/*     */   
/*     */   String hmmPath;
/*     */   String treePath;
/* 195 */   Map bestHits = new HashMap();
/*     */   static String REPOSITORY;
/*     */   
/*     */   public static void runTreeHMM(CommandLine params)
/*     */   {
/* 200 */     File outpDir = new File(params.getOptionValue("output", "."));
/*     */     File index;
/*     */     File fastaFile;
/* 203 */     File index; if (params.hasOption("input")) {
/* 204 */       String fasta = params.getOptionValue("input");
/* 205 */       File fastaFile = new File(outpDir, fasta);
/* 206 */       index = new File(outpDir, fasta + "_idx");
/*     */     }
/*     */     else {
/* 209 */       String path = params.getOptionValue("repository");
/* 210 */       REPOSITORY = path + "/Pfam/CURRENT/";
/* 211 */       fastaFile = new File(path + "/pfamseq/pfamseq");
/* 212 */       index = new File(path + "/Pfam/pfamseq_idx");
/*     */     }
/* 214 */     if ((!index.exists()) || (index.length() == 0L)) {
/* 215 */       FlatFileTools.createIndex(index, "fasta", "PROTEIN");
/* 216 */       FlatFileTools.addFilesToIndex(index, new File[] { fastaFile });
/*     */     }
/*     */     
/*     */ 
/* 220 */     String[] symbols = params.getOptionValues("domain");
/* 221 */     double evalue = Double.parseDouble(params.getOptionValue("evalue"));
/* 222 */     File hitsDir = new File(outpDir, "hits/");
/* 223 */     if (!hitsDir.exists()) hitsDir.mkdir();
/* 224 */     for (int i = 0; i < symbols.length; i++) {
/* 225 */       HmmerLauncher hmmL = new HmmerLauncher(new File(outpDir, "mod_hmms/"), 
/* 226 */         hitsDir, 
/* 227 */         index, 
/* 228 */         fastaFile, 
/* 229 */         symbols[i], evalue, 
/* 230 */         params.getOptionValue("input_mode", ".").equals("subfamily"));
/*     */       
/*     */ 
/* 233 */       hmmL.runTreeHmmer();
/*     */     }
/*     */   }
/*     */   
/*     */   public static void createIndexedDBFromHmmerFiles(CommandLine params) throws Exception {
/* 238 */     File repos = new File(params.getOptionValue("repository"));
/* 239 */     DomainAlphabet alph = PfamAlphabet.makeAlphabet(repos);
/* 240 */     File hmmerDir = new File(params.getOptionValue("output", "."), "hmmerTest");
/* 241 */     BufferedInputStream fasta = new BufferedInputStream(new java.io.FileInputStream(new File(hmmerDir.getParent(), params.getOptionValue("input"))));
/* 242 */     SequenceDB seqs = SeqIOTools.readFasta(fasta, org.biojava.bio.seq.ProteinTools.getAlphabet());
/* 243 */     seqs = getHmmerAnnotation(seqs, hmmerDir, alph, 100.0D);
/* 244 */     PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(new File(hmmerDir.getParent(), 
/* 245 */       "pfamA_reg_full"))));
/* 246 */     lc1.pfam.IndexedPfamDB.print(seqs.sequenceIterator(), pw);
/* 247 */     pw.close();
/*     */   }
/*     */   
/*     */   public HmmerLauncher(File directory, File outpDir, File indexFile, File fastaFile, String hmmName, double evalue, boolean subFamilies)
/*     */   {
/*     */     try {
/* 253 */       this.seqDB = FlatFileTools.open(indexFile);
/*     */     }
/*     */     catch (IOException e) {
/* 256 */       e.printStackTrace();
/* 257 */       System.err.println("db is not indexed properly, please index it first");
/*     */     }
/*     */     
/* 260 */     this.evalue = evalue;
/* 261 */     this.outpDir = outpDir;
/* 262 */     this.hmmName = hmmName;
/* 263 */     this.alph = PfamAlphabet.makeAlphabet(new File("directory"));
/* 264 */     File treeFile = new File(directory, hmmName + ".nhx");
/* 265 */     this.hmmPath = fastaFile.getAbsolutePath();
/* 266 */     this.treePath = treeFile.getAbsolutePath();
/* 267 */     this.hmmPath = new File(directory, hmmName).getAbsolutePath();
/* 268 */     FileFilter ff = new FileFilter()
/*     */     {
/*     */       public boolean accept(File f)
/*     */       {
/* 272 */         return (f.getName().startsWith(this.val$hmmName)) && (!f.getName().endsWith("nhx")) && (!f.getName().equals(this.val$hmmName + "_"));
/*     */       }
/*     */       
/* 275 */       public String getDescription() { return ""; }
/*     */     };
/*     */     
/* 278 */     if (subFamilies) {
/*     */       try {
/* 280 */         File scores = new File(REPOSITORY + "/" + hmmName + "/scores");
/* 281 */         Map m = new HashMap();
/* 282 */         SheetIO.toMap(SheetIO.read(scores, "[ /]"), 1, 2, m);
/* 283 */         SequenceIterator seqIt = getSequenceIterator(m, 
/* 284 */           new HashMap());
/* 285 */         fastaFile = new File(outpDir, "tmp_" + hmmName + ".fasta");
/* 286 */         OutputStream os = new BufferedOutputStream(new FileOutputStream(fastaFile));
/* 287 */         SeqIOTools.writeFasta(os, seqIt);
/* 288 */         os.close();
/*     */       } catch (Exception ex) {
/* 290 */         ex.printStackTrace();
/*     */       }
/*     */     }
/*     */     try {
/* 294 */       this.tree = lc1.phyl.MaxLikelihoodTree.readNHXTree(treeFile);
/* 295 */       File[] files = directory.listFiles(ff);
/*     */       
/*     */ 
/* 298 */       System.out.println(this.tree.getRoot().getChild(0).getIdentifier().getName());
/* 299 */       for (int i = 0; i < files.length; i++) {
/* 300 */         Node node = TreeUtils.getNodeByName(this.tree, files[i].getName());
/*     */         
/* 302 */         this.tree.setAttribute(node, "hmm", files[i]);
/* 303 */         File fasta_out = new File(outpDir, files[i].getName() + ".fasta");
/* 304 */         for (int j = 0; j < node.getChildCount(); j++) {
/* 305 */           this.tree.setAttribute(node.getChild(j), "fasta_in", fasta_out);
/*     */         }
/*     */       }
/* 308 */       this.rootID = new Identifier();
/* 309 */       File rootFile = new File(directory, hmmName + "_");
/* 310 */       this.tree.setAttribute(this.tree.getRoot(), "hmm", rootFile);
/* 311 */       this.tree.setAttribute(this.tree.getRoot(), "fasta_in", fastaFile);
/* 312 */       File fasta_out = new File(outpDir, rootFile.getName() + ".fasta");
/* 313 */       for (int j = 0; j < this.tree.getRoot().getChildCount(); j++) {
/* 314 */         this.tree.setAttribute(this.tree.getRoot().getChild(j), "fasta_in", fasta_out);
/*     */       }
/*     */     }
/*     */     catch (Exception e) {
/* 318 */       e.printStackTrace();
/*     */     }
/*     */   }
/*     */   
/*     */   void runTreeHmmer() {
/* 323 */     runHmmerAtNode(this.tree.getRoot(), new HashMap());
/*     */     try {
/* 325 */       PrintWriter pw = new PrintWriter(System.out);
/* 326 */       print(pw);
/*     */     } catch (Exception exc) {
/* 328 */       exc.printStackTrace();
/*     */     }
/*     */   }
/*     */   
/*     */   void runHmmerAtNode(Node node, Map prevHits) {
/* 333 */     File hmmFile = (File)this.tree.getAttribute(node, "hmm");
/* 334 */     if (hmmFile != null) {
/* 335 */       File fasta_in = (File)this.tree.getAttribute(node, "fasta_in");
/* 336 */       File resultsFile = new File(this.outpDir, hmmFile.getName() + ".hmm");
/* 337 */       File fasta_out = (File)this.tree.getAttribute(node.getChild(0), "fasta_in");
/*     */       try
/*     */       {
/* 340 */         Map hasHits = parseHmmerIntoFasta(resultsFile, fasta_out, prevHits);
/* 341 */         putHitsInMap(hasHits, node.getIdentifier().getName());
/* 342 */         if (hasHits.keySet().size() > 0) {
/* 343 */           for (int i = 0; i < node.getChildCount(); i++) {
/* 344 */             runHmmerAtNode(node.getChild(i), hasHits);
/*     */           }
/*     */         }
/*     */       }
/*     */       catch (Exception e)
/*     */       {
/* 350 */         e.printStackTrace();
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   void print(PrintWriter pw) {
/* 356 */     pw.println("thmmsearch - search a sequence database with a profile HMM and");
/* 357 */     pw.println("classify with a tree hmm ");
/* 358 */     pw.println("HMMER 2.3.1 (June 2003)  with tree modifications");
/* 359 */     pw.println("- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -");
/* 360 */     pw.println("HMM file:                   " + this.hmmPath);
/* 361 */     pw.println("Sequence database:         " + this.sequencePath);
/* 362 */     pw.println("Tree:         " + this.treePath);
/* 363 */     pw.println("per-sequence score cutoff:  [none]");
/* 364 */     pw.println("per-domain score cutoff:    [none]");
/* 365 */     pw.println("per-sequence Eval cutoff:   <= " + this.evalue);
/* 366 */     pw.println("per-domain Eval cutoff:     [none]");
/* 367 */     pw.println("- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -");
/* 368 */     pw.println("Query HMM:   SEED");
/* 369 */     pw.println("Accession:   [none]");
/* 370 */     pw.println("Description: [none]");
/* 371 */     pw.println("  [HMM has been calibrated; E-values are empirical estimates]");
/* 372 */     pw.println();
/* 373 */     pw.println("Scores for complete sequences (score includes all domains): ");
/* 374 */     pw.println(ROC.header);
/* 375 */     pw.println("----------------------------------------------------------------------");
/* 376 */     SortedSet ss = new TreeSet(ROC.COMPARISON);
/* 377 */     for (Iterator it = this.bestHits.keySet().iterator(); it.hasNext();) {
/* 378 */       ss.add(this.bestHits.get(it.next()));
/*     */     }
/* 380 */     for (Iterator it = ss.iterator(); it.hasNext();) {
/* 381 */       pw.println(it.next());
/*     */     }
/* 383 */     pw.flush();
/*     */   }
/*     */   
/*     */ 
/*     */   private void putHitsInMap(Map m, String model)
/*     */     throws Exception
/*     */   {
/* 390 */     for (Iterator it = m.keySet().iterator(); it.hasNext();) {
/* 391 */       Object key = it.next();
/* 392 */       Domain score = (Domain)m.get(key);
/* 393 */       if (this.bestHits.containsKey(key)) {
/* 394 */         Domain value = (Domain)this.bestHits.get(key);
/* 395 */         if (value.getScore() < score.getScore()) {
/* 396 */           score.getAnnotation().setProperty("modelId", 
/* 397 */             model);
/* 398 */           this.bestHits.put(key, score);
/*     */         }
/*     */       }
/*     */       else {
/* 402 */         score.getAnnotation().setProperty("modelId", 
/* 403 */           model);
/* 404 */         this.bestHits.put(key, score);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public static void runHmmerOnList(CommandLine params) throws FileNotFoundException, IOException {
/* 410 */     File repos = new File(params.getOptionValue("repository"));
/* 411 */     File outpDir = new File(params.getOptionValue("output"));
/* 412 */     File hitsDir = new File(outpDir, "hmmerTest");
/* 413 */     if (!hitsDir.exists()) hitsDir.mkdir();
/* 414 */     String[] input = params.getOptionValues("input");
/* 415 */     System.err.println(lc1.util.Print.toString(input));
/* 416 */     Iterator names = 
/* 417 */       SheetIO.concatenateRows(SheetIO.read(new File(outpDir, input[0]), "\\s+"));
/* 418 */     double evalue = 1000.0D;
/* 419 */     File fasta = new File(outpDir, input[1]);
/* 420 */     runHmmerOnList(hitsDir, repos, names, fasta, evalue);
/*     */   }
/*     */   
/*     */   public static void runHmmerOnList(File outpDir, File repository, Iterator names, File fasta_seq, double evalue) {
/*     */     try {
/* 425 */       SymbolTokenization token = PfamAlphabet.makeAlphabet(repository).getTokenization("token");
/* 426 */       while (names.hasNext()) {
/*     */         try {
/* 428 */           String name = (String)token.parseToken((String)names.next()).getAnnotation().getProperty("pfamA_id");
/*     */           
/*     */ 
/* 431 */           File output = new File(outpDir, name);
/* 432 */           if ((output.exists()) && (output.length() > 0L)) {
/* 433 */             System.err.println("already exists " + output);
/*     */           }
/*     */           else {
/* 436 */             BufferedReader br = new BufferedReader(new FileReader(new File(repository, "name/HMM_ls")));
/* 437 */             AlignmentProfileParser.makeParser(br, false, 1, 1);
/* 438 */             File hmm = new File(repository, "/Pfam/CURRENT/" + name + 
/* 439 */               "/HMM_ls");
/* 440 */             br.close();
/*     */           }
/*     */         }
/*     */         catch (Exception exc) {
/* 444 */           exc.printStackTrace();
/*     */         }
/*     */       }
/*     */     } catch (Exception exc) {
/* 448 */       exc.printStackTrace();
/*     */     }
/*     */   }
/*     */   
/*     */   private static void printToFile(InputStream is, File output, String comment)
/*     */   {
/*     */     try {
/* 455 */       BufferedReader error = new BufferedReader(new java.io.InputStreamReader(is));
/*     */       
/* 457 */       PrintStream ps = new PrintStream(new BufferedOutputStream(new FileOutputStream(output)));
/* 458 */       String s; while ((s = error.readLine()) != null) {
/*     */         String s;
/* 460 */         ps.println(s);
/*     */       }
/* 462 */       ps.flush();
/* 463 */       ps.close();
/*     */     } catch (FileNotFoundException exc) {
/* 465 */       exc.printStackTrace();
/* 466 */     } catch (IOException exc) { exc.printStackTrace();
/*     */     }
/*     */   }
/*     */   
/* 470 */   private SequenceIterator getSequenceIterator(Map names, Map prev) { List l = new ArrayList();
/* 471 */     for (Iterator it = names.keySet().iterator(); it.hasNext();) {
/* 472 */       Object key = it.next();
/* 473 */       if (prev.containsKey(key))
/*     */       {
/* 475 */         if (((Domain)prev.get(key)).getScore() < ((Domain)names.get(key)).getScore())
/*     */         {
/* 477 */           l.add(key);
/*     */         }
/*     */         
/*     */ 
/*     */       }
/*     */       else {
/* 483 */         l.add(key);
/*     */       }
/*     */     }
/*     */     
/* 487 */     Iterator it = l.iterator();
/* 488 */     new SequenceIterator() {
/*     */       public Sequence nextSequence() {
/*     */         try {
/* 491 */           return HmmerLauncher.this.seqDB.getSequence((String)this.val$it.next());
/*     */         }
/*     */         catch (Exception t) {
/* 494 */           t.printStackTrace(); }
/* 495 */         return null;
/*     */       }
/*     */       
/*     */       public boolean hasNext() {
/* 499 */         return this.val$it.hasNext();
/*     */       }
/*     */     };
/*     */   }
/*     */   
/*     */   public Map parseHmmerIntoFasta(File results, File output, Map prev) {
/*     */     try {
/* 506 */       Map names = parseHmmer(new BufferedReader(new FileReader(results)), this.alph);
/* 507 */       if (names.keySet().size() > 0)
/*     */       {
/* 509 */         OutputStream os = new BufferedOutputStream(new FileOutputStream(output));
/* 510 */         SeqIOTools.writeFasta(os, getSequenceIterator(names, prev));
/* 511 */         return names;
/*     */       }
/* 513 */       return names;
/*     */     }
/*     */     catch (FileNotFoundException exc) {
/* 516 */       exc.printStackTrace();
/* 517 */       return null;
/*     */     }
/*     */     catch (Exception exc) {
/* 520 */       exc.printStackTrace(); }
/* 521 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static SortedSet getSortedHmmerResults(BufferedReader in, Alphabet alph)
/*     */     throws Exception
/*     */   {
/* 531 */     Map m = parseHmmer(in, alph);
/* 532 */     SortedSet s = new TreeSet(ROC.COMPARISON);
/* 533 */     s.addAll(m.values());
/* 534 */     return s;
/*     */   }
/*     */   
/*     */ 
/*     */   public static SequenceDB getHmmerAnnotation(SequenceDB seqs, File dir, DomainAlphabet alph, double e_thresh)
/*     */     throws Exception
/*     */   {
/* 541 */     Iterator files = Arrays.asList(dir.listFiles()).iterator();
/* 542 */     SymbolTokenization token = alph.getTokenization("token");
/* 543 */     org.biojava.bio.seq.FeatureRealizer fr = DomainList.domainFeatureRealizer();
/* 544 */     SequenceDB dbOut = new HashSequenceDB();
/* 545 */     while (files.hasNext()) {
/* 546 */       File file = (File)files.next();
/* 547 */       SymbolList sl = alph.nameToSymbolList(new String[] { file.getName() });
/* 548 */       if (sl.length() == 0) {
/* 549 */         System.err.println("didn't contain " + file.getName());
/*     */       }
/*     */       else {
/* 552 */         Symbol sym = sl.symbolAt(1);
/*     */         
/* 554 */         BufferedReader in = new BufferedReader(new FileReader(file));
/* 555 */         Map m = null;
/* 556 */         for (Iterator it = m.keySet().iterator(); it.hasNext();) {
/* 557 */           String name = (String)it.next();
/* 558 */           Domain ss = (Domain)m.get(name);
/*     */           
/* 560 */           Domain.Template feature = (Domain.Template)ss.makeTemplate();
/* 561 */           feature.symbol = sym;
/* 562 */           feature.location = Location.empty;
/*     */           Sequence seq;
/*     */           Sequence seq;
/* 565 */           if (dbOut.ids().contains(name)) {
/* 566 */             seq = dbOut.getSequence(name);
/*     */           }
/*     */           else {
/* 569 */             Sequence seq1 = seqs.getSequence(name);
/* 570 */             seq = new DomainList(seq1, name, name, seq1.getAnnotation());
/* 571 */             dbOut.addSequence(seq);
/*     */           }
/* 573 */           seq.createFeature(feature);
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 578 */     return dbOut;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static Map parseHmmer(BufferedReader in, Alphabet alph)
/*     */     throws Exception
/*     */   {
/* 586 */     return null;
/*     */   }
/*     */ }


/* Location:              /home/lachlan/Desktop/psilc1.21/psilc.jar!/lc1/dp/HmmerLauncher.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */