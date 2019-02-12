/*     */ package lc1.phyhmm;
/*     */ 
/*     */ import com.braju.format.Format;
/*     */ import gnu.trove.TObjectDoubleHashMap;
/*     */ import gnu.trove.TObjectDoubleProcedure;
/*     */ import java.io.BufferedOutputStream;
/*     */ import java.io.BufferedReader;
/*     */ import java.io.BufferedWriter;
/*     */ import java.io.File;
/*     */ import java.io.FileNotFoundException;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.FileReader;
/*     */ import java.io.FileWriter;
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStream;
/*     */ import java.io.PrintStream;
/*     */ import java.io.PrintWriter;
/*     */ import java.io.StringWriter;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collection;
/*     */ import java.util.HashSet;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Set;
/*     */ import lc1.domains.ContextSCOPTest;
/*     */ import lc1.domainseq.Domain;
/*     */ import lc1.dp.EVD;
/*     */ import lc1.dp.HmmerLauncher;
/*     */ import lc1.pfam.IndexedPfamDB;
/*     */ import lc1.pfam.PfamAlphabet;
/*     */ import lc1.util.BlastDB;
/*     */ import lc1.util.FlatFileTools;
/*     */ import lc1.util.SheetIO;
/*     */ import org.apache.commons.cli.CommandLine;
/*     */ import org.apache.commons.cli.OptionBuilder;
/*     */ import org.apache.commons.cli.Options;
/*     */ import org.apache.commons.cli.Parser;
/*     */ import org.apache.commons.cli.PosixParser;
/*     */ import org.biojava.bio.Annotation;
/*     */ import org.biojava.bio.seq.Sequence;
/*     */ import org.biojava.bio.seq.SequenceIterator;
/*     */ import org.biojava.bio.seq.db.HashSequenceDB;
/*     */ import org.biojava.bio.seq.db.SequenceDB;
/*     */ import org.biojava.bio.seq.io.SeqIOTools;
/*     */ import org.biojava.bio.seq.io.SymbolTokenization;
/*     */ import org.biojava.bio.symbol.Symbol;
/*     */ import org.biojava.utils.ProcessTools;
/*     */ import pal.alignment.Alignment;
/*     */ import pal.alignment.ReadAlignment;
/*     */ import pal.misc.Identifier;
/*     */ import pal.substmodel.RateMatrix;
/*     */ import pal.tree.ReadTree;
/*     */ import pal.tree.Tree;
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
/*     */ public class PhyHMM
/*     */ {
/*     */   CommandLine params;
/*     */   File dir;
/*     */   File repos;
/*  75 */   SequenceDB db = null;
/*     */   PfamAlphabet alph;
/*     */   SymbolTokenization tokenizer;
/*     */   File clusterDir;
/*     */   File blastDB;
/*     */   File pfamseq_idx;
/*     */   File fastaDir;
/*     */   File alignDir;
/*     */   File blastDir;
/*     */   File rateDir;
/*     */   File scopDir;
/*     */   File resultsDir;
/*     */   File hmmerDir;
/*     */   File hitsDir;
/*     */   File treeDir;
/*  90 */   SequenceDB seqDB = null;
/*     */   
/*  92 */   static final Options OPTIONS = new Options() {};
/*     */   
/*     */ 
/*     */ 
/*     */   Symbol domain;
/*     */   
/*     */ 
/*     */ 
/*     */   SequenceDB astralDB;
/*     */   
/*     */ 
/*     */ 
/*     */   static final String printStyle1 = "%-15s %15.3f %15.3g %15.3f %15.3g  %15.3f %15.3g %15.3f %15.3g %15s\n";
/*     */   
/*     */ 
/*     */ 
/*     */   public static void main(String[] args)
/*     */     throws Exception
/*     */   {
/* 111 */     Parser DP_PARSER = new PosixParser();
/* 112 */     CommandLine params = DP_PARSER.parse(OPTIONS, args);
/* 113 */     PhyHMM phyhmm = new PhyHMM(params);
/*     */     
/*     */ 
/* 116 */     phyhmm.iterateOverAlignments(false);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   PhyHMM(CommandLine params1)
/*     */   {
/*     */     try
/*     */     {
/* 127 */       this.repos = new File(params1.getOptionValue("repository", "."));
/* 128 */       this.dir = new File(params1.getOptionValue("dir", "."));
/* 129 */       this.params = params1;
/* 130 */       File pfamA = new File(this.repos, "pfamA");
/*     */       
/* 132 */       this.alph = PfamAlphabet.makeAlphabet(this.repos);
/* 133 */       this.clusterDir = new File(this.dir, "cluster");
/* 134 */       this.alignDir = new File(this.dir, "align");
/* 135 */       this.rateDir = new File(this.dir, "ratematrix");
/* 136 */       this.treeDir = new File(this.dir, "tree");
/* 137 */       this.pfamseq_idx = new File(this.repos, "pfamseq/pfamseq_idx");
/* 138 */       this.blastDB = new File(this.repos, "pfamseq/pfamseq");
/* 139 */       this.fastaDir = new File(this.dir, "fasta");
/* 140 */       this.blastDir = new File(this.dir, "blast");
/* 141 */       this.scopDir = new File(this.dir, "scop");
/* 142 */       String extension = "__f_f_" + (
/*     */       
/*     */ 
/* 145 */         DPManager.useClosest ? "t" : "f") + "_" + 
/* 146 */         DPManager.insertRates + "_" + 
/* 147 */         DPManager.matchRates + "_" + 
/* 148 */         DPManager.count;
/* 149 */       this.resultsDir = new File(this.dir, "results" + extension);
/* 150 */       if (!this.resultsDir.exists()) this.resultsDir.mkdir();
/* 151 */       this.hmmerDir = new File(this.dir, "hmmerTest");
/* 152 */       this.hitsDir = new File(this.dir, "hits" + extension);
/* 153 */       if (!this.hitsDir.exists()) {
/* 154 */         this.hitsDir.mkdir();
/*     */       }
/* 156 */       File astralDBIndex = new File(this.params.getOptionValue("seqdb") + "_idx");
/* 157 */       if ((!astralDBIndex.exists()) || (astralDBIndex.length() == 0L)) {
/* 158 */         System.err.println("indexing ...");
/* 159 */         FlatFileTools.createIndex(astralDBIndex, "fasta", "protein");
/* 160 */         FlatFileTools.addFilesToIndex(astralDBIndex, new File[] { new File(this.params.getOptionValue("seqdb")) });
/* 161 */         System.err.println("...done");
/*     */       }
/* 163 */       this.astralDB = FlatFileTools.open(astralDBIndex);
/* 164 */       this.tokenizer = this.alph.getTokenization("token");
/* 165 */       this.domain = (this.params.hasOption("domain") ? this.tokenizer.parseToken(this.params.getOptionValue("domain")) : null);
/*     */     }
/*     */     catch (Exception exc) {
/* 168 */       exc.printStackTrace();
/*     */     }
/*     */   }
/*     */   
/*     */   public File makeCluster(String id)
/*     */     throws IOException
/*     */   {
/* 175 */     if (this.seqDB == null) {
/* 176 */       if ((!this.pfamseq_idx.exists()) || (this.pfamseq_idx.length() == 0L)) {
/* 177 */         FlatFileTools.createIndex(this.pfamseq_idx, "fasta", "proteinT");
/* 178 */         FlatFileTools.addFilesToIndex(this.pfamseq_idx, new File[] { new File(this.repos, "pfamseq/pfamseq") });
/*     */       }
/*     */       
/* 181 */       this.seqDB = FlatFileTools.open(this.pfamseq_idx);
/*     */     }
/*     */     try {
/* 184 */       System.err.println(this.pfamseq_idx);
/* 185 */       Sequence seq = this.seqDB.getSequence(id);
/* 186 */       this.seqDB = null;
/* 187 */       File fastaFile = new File(this.fastaDir, seq.getName());
/* 188 */       OutputStream os = new BufferedOutputStream(new FileOutputStream(fastaFile));
/* 189 */       SeqIOTools.writeFasta(os, seq);
/* 190 */       os.close();
/* 191 */       File bin = new File("/home/lc/bin/ncbi/bin/");
/*     */       
/* 193 */       String[] command = { bin.getAbsolutePath() + "/blastall", "-p", 
/*     */       
/* 195 */         "blastp", 
/* 196 */         "-d", this.blastDB.getAbsolutePath(), "-i", fastaFile.getAbsolutePath(), 
/* 197 */         "-m", "8" };
/* 198 */       File blastFile = new File(this.blastDir, seq.getName());
/* 199 */       if (!blastFile.exists()) {
/* 200 */         StringWriter err = new StringWriter();
/* 201 */         System.err.println(Arrays.asList(command));
/* 202 */         PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(blastFile)));
/* 203 */         ProcessTools.exec(command, null, pw, err);
/* 204 */         System.err.println(err.getBuffer().toString());
/*     */       }
/* 206 */       return BlastDB.getHomologs(fastaFile, this.blastDB, blastFile, this.dir, false);
/*     */     }
/*     */     catch (Exception exc) {
/* 209 */       exc.printStackTrace(); }
/* 210 */     return null;
/*     */   }
/*     */   
/*     */   public void iterateOverAlignments(boolean alignOnly)
/*     */     throws Exception
/*     */   {
/* 216 */     String pfamA_id = (String)this.domain.getAnnotation().getProperty("pfamA_id");
/* 217 */     File outF = new File(this.resultsDir, pfamA_id);
/* 218 */     Set done = new HashSet();
/* 219 */     if (outF.exists()) {
/* 220 */       for (Iterator it = SheetIO.read(outF, "\\s+"); it.hasNext();) {
/* 221 */         done.add(((List)it.next()).get(0));
/*     */       }
/*     */     }
/* 224 */     PrintWriter out = new PrintWriter(new FileWriter(outF, true));
/* 225 */     File dom_hits_dir = new File(this.hitsDir, pfamA_id);
/* 226 */     if (!dom_hits_dir.exists()) dom_hits_dir.mkdir();
/* 227 */     Collection[] sf_cf = ContextSCOPTest.getSfCf(this.scopDir, pfamA_id);
/*     */     
/* 229 */     int k = 0;
/* 230 */     SequenceDB astralDB1 = 
/* 231 */       HmmerLauncher.parseHmmerResult(new BufferedReader(new FileReader(new File(this.hmmerDir, pfamA_id))), this.astralDB, this.domain, this.alph, 1000.0D);
/* 232 */     Set s1 = new HashSet(astralDB1.ids());
/* 233 */     SequenceIterator it = new SequenceIterator() {
/*     */       Iterator it;
/*     */       boolean astralSet;
/*     */       
/* 237 */       public Sequence nextSequence() { if (!this.it.hasNext()) {
/* 238 */           this.it = this.val$s1.iterator();
/* 239 */           this.astralSet = true;
/*     */         }
/* 241 */         String id = (String)this.it.next();
/* 242 */         if (!this.astralSet) {
/* 243 */           this.val$s1.remove(id);
/*     */         }
/*     */         try {
/* 246 */           return PhyHMM.this.astralDB.getSequence(id);
/*     */         } catch (Exception e) {
/* 248 */           e.printStackTrace();
/*     */         }
/* 250 */         return null;
/*     */       }
/*     */       
/* 253 */       public boolean hasNext() { return (!this.astralSet) || (this.it.hasNext()); }
/*     */     };
/*     */     
/* 256 */     if (this.params.hasOption("seq")) {
/* 257 */       SequenceDB db = new HashSequenceDB();
/* 258 */       db.addSequence(this.astralDB.getSequence(this.params.getOptionValue("seq")));
/* 259 */       it = db.sequenceIterator();
/*     */     }
/* 261 */     while (it.hasNext()) {
/* 262 */       Sequence seq = it.nextSequence();
/* 263 */       if (!done.contains(seq.getName()))
/*     */       {
/*     */ 
/* 266 */         String id = seq.getName();
/* 267 */         File ratematrixF = new File(this.rateDir, id);
/* 268 */         k++;
/*     */         try {
/* 270 */           File cluster = new File(this.alignDir, id + ".align");
/* 271 */           if (cluster.exists()) {
/* 272 */             int countSeqs = countSeqs(cluster);
/* 273 */             if (!alignOnly) {
/* 274 */               Alignment aln = new ReadAlignment(getAlignments(this.dir, cluster, this.params.getOptionValue("bin", "")).getAbsolutePath());
/*     */               
/* 276 */               Tree tree = new ReadTree(new File(this.treeDir, id + ".align" + "_phyml_tree.txt").getAbsolutePath());
/* 277 */               RateMatrix substMProt; RateMatrix substMProt; if ((!ratematrixF.exists()) || (ratematrixF.length() == 0L)) {
/* 278 */                 substMProt = RateTreeBuild.build(id, tree, 
/* 279 */                   aln, ratematrixF, DPManager.collapse, 
/* 280 */                   false);
/*     */               }
/*     */               else {
/* 283 */                 substMProt = RateTreeBuild.read(ratematrixF);
/*     */               }
/*     */               
/*     */ 
/* 287 */               DPManager psm = new DPManager(id, new File(this.params.getOptionValue("repository", ".")), 
/* 288 */                 Arrays.asList(new Object[] { this.domain }), aln, tree, substMProt, dom_hits_dir);
/* 289 */               psm.run();
/* 290 */               printSummary(out, new File(dom_hits_dir, id), 
/* 291 */                 sf_cf[1].contains(id) ? "0" : sf_cf[0].contains(id) ? "1" : "-1", id, this.domain);
/*     */             }
/* 293 */           } } catch (Exception exc) { exc.printStackTrace();
/*     */         }
/*     */       } }
/* 296 */     out.close();
/*     */   }
/*     */   
/*     */   public void printSummary(PrintWriter pw, File in, String no, String id, Symbol sym)
/*     */     throws FileNotFoundException, IOException
/*     */   {
/* 302 */     double[] evalue = { 0.0D, 0.0D, 0.0D, 0.0D };
/* 303 */     System.err.println("in " + in);
/* 304 */     double mu = Double.parseDouble((String)sym.getAnnotation().getProperty("mu"));
/* 305 */     double lambda = Double.parseDouble((String)sym.getAnnotation().getProperty("lambda"));
/* 306 */     int totalSeqs = Integer.parseInt((String)sym.getAnnotation().getProperty("total seqs"));
/* 307 */     EVD evd = new EVD(lambda, mu);
/* 308 */     double[] score = { 0.0D, 0.0D, Double.NEGATIVE_INFINITY, 0.0D };
/* 309 */     TObjectDoubleHashMap m = new TObjectDoubleHashMap();
/* 310 */     BufferedReader br = new BufferedReader(new FileReader(in));
/* 311 */     String str = br.readLine();br.readLine();
/* 312 */     while ((str = br.readLine()) != null) {
/* 313 */       String[] row = str.split("\\s+");
/* 314 */       String st = row[0];
/* 315 */       double sc = Double.parseDouble(row[4]);
/* 316 */       m.put(st, m.get(st) + sc);
/*     */     }
/* 318 */     m.forEachEntry(new TObjectDoubleProcedure() {
/*     */       public boolean execute(Object obj, double sc) {
/* 320 */         String st = (String)obj;
/*     */         
/* 322 */         if (st.startsWith("hmm")) {
/* 323 */           this.val$score[1] = sc;
/*     */         }
/*     */         else {
/* 326 */           if (st.startsWith(this.val$id)) {
/* 327 */             this.val$score[0] = sc;
/*     */           }
/* 329 */           if (sc > this.val$score[2]) this.val$score[2] = sc;
/* 330 */           this.val$score[3] += sc;
/*     */         }
/* 332 */         return true;
/*     */       }
/*     */     });
/*     */     
/* 336 */     if ((score[0] == 0.0D) || (score[1] == 0.0D)) return;
/* 337 */     score[3] /= (m.size() - 1.0D);
/* 338 */     for (int i = 0; i < score.length; i++) {
/* 339 */       evalue[i] = (evd.extremeValueP(score[i]) * totalSeqs);
/*     */     }
/* 341 */     Object[] obj = { id, 
/* 342 */       new Double(score[0]), new Double(evalue[0]), 
/* 343 */       new Double(score[1]), new Double(evalue[1]), 
/* 344 */       new Double(score[2]), new Double(evalue[2]), 
/* 345 */       new Double(score[3]), new Double(evalue[3]), 
/* 346 */       no };
/* 347 */     pw.print(Format.sprintf("%-15s %15.3f %15.3g %15.3f %15.3g  %15.3f %15.3g %15.3f %15.3g %15s\n", obj));
/* 348 */     pw.flush();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   static int countSeqs(File fastaFile)
/*     */     throws Exception
/*     */   {
/* 356 */     int count = 0;
/*     */     
/* 358 */     BufferedReader br = new BufferedReader(new FileReader(fastaFile));
/* 359 */     String st = "";
/* 360 */     while ((st = br.readLine()) != null) {
/* 361 */       if (st.startsWith(">")) {
/* 362 */         count++;
/*     */       }
/*     */     }
/* 365 */     return count;
/*     */   }
/*     */   
/*     */   Object[] getSymbol(File dir, PfamAlphabet alph, File alignments) throws Exception {
/* 369 */     System.err.println(alignments.getAbsolutePath());
/* 370 */     Alignment align = new ReadAlignment(alignments.getAbsolutePath());
/* 371 */     if (this.db == null)
/*     */     {
/*     */ 
/* 374 */       this.db = new IndexedPfamDB(new File(this.repos, "pfamA_reg_full_ls"), alph, "\t", 0, 1); }
/* 375 */     Set l = new HashSet();
/*     */     
/*     */ 
/* 378 */     for (int i = 0; i < align.getIdCount(); i++) {
/* 379 */       String name = align.getIdentifier(i).getName();
/* 380 */       if (this.db.ids().contains(name)) {
/* 381 */         Sequence seq = this.db.getSequence(name);
/* 382 */         for (Iterator it = seq.features(); it.hasNext();)
/* 383 */           l.add(((Domain)it.next()).getSymbol());
/*     */       }
/*     */     }
/* 386 */     System.err.println("symbols " + l);
/* 387 */     return l.toArray();
/*     */   }
/*     */   
/*     */   static File getAlignments(File dir, File input1, String bin) throws Exception
/*     */   {
/* 392 */     String fileName = input1.getName();
/* 393 */     if (fileName.indexOf('.') >= 0) fileName = fileName.split("\\.")[0];
/* 394 */     File input = new File(input1.getParentFile(), fileName);
/* 395 */     File alnDir = new File(dir + "/align/");
/* 396 */     return new File(alnDir, input.getName() + ".align");
/*     */   }
/*     */ }


/* Location:              /home/lachlan/Desktop/psilc1.21/psilc.jar!/lc1/phyhmm/PhyHMM.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */