/*     */ package lc1.domains;
/*     */ 
/*     */ import com.braju.format.Format;
/*     */ import com.braju.format.Parameters;
/*     */ import java.io.BufferedReader;
/*     */ import java.io.BufferedWriter;
/*     */ import java.io.File;
/*     */ import java.io.FileReader;
/*     */ import java.io.FileWriter;
/*     */ import java.io.IOException;
/*     */ import java.io.PrintStream;
/*     */ import java.io.PrintWriter;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import lc1.domainseq.Domain;
/*     */ import lc1.domainseq.Domain.MagicTemplate;
/*     */ import lc1.dp.EVD;
/*     */ import lc1.dp.HmmerLauncher;
/*     */ import lc1.dp.ROC;
/*     */ import lc1.pfam.DomainAlphabet;
/*     */ import lc1.pfam.IndexedPfamDB;
/*     */ import lc1.pfam.PfamIndex;
/*     */ import lc1.pfam.SpeciesAlphabet;
/*     */ import lc1.util.FlatFileTools;
/*     */ import lc1.util.Print;
/*     */ import lc1.util.SheetIO;
/*     */ import org.apache.commons.cli.CommandLine;
/*     */ import org.apache.commons.cli.OptionBuilder;
/*     */ import org.apache.commons.cli.Options;
/*     */ import org.apache.commons.cli.Parser;
/*     */ import org.apache.commons.cli.PosixParser;
/*     */ import org.biojava.bio.Annotation;
/*     */ import org.biojava.bio.seq.Feature;
/*     */ import org.biojava.bio.seq.Sequence;
/*     */ import org.biojava.bio.seq.SequenceIterator;
/*     */ import org.biojava.bio.seq.db.SequenceDB;
/*     */ import org.biojava.bio.seq.impl.SimpleSequence;
/*     */ import org.biojava.bio.seq.io.SymbolTokenization;
/*     */ import org.biojava.bio.symbol.Location;
/*     */ import org.biojava.bio.symbol.Symbol;
/*     */ import org.biojava.bio.symbol.SymbolList;
/*     */ import pal.math.MultivariateFunction;
/*     */ import pal.math.MultivariateMinimum;
/*     */ import pal.math.OrthogonalHints;
/*     */ import pal.math.OrthogonalSearch;
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ContextSCOPTest
/*     */ {
/*  57 */   static final Options OPTIONS = new Options() {};
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
/*  72 */   static boolean context = true;
/*  73 */   static boolean species = true;
/*     */   
/*     */ 
/*     */   DomainDP dp;
/*     */   
/*     */ 
/*     */   File outpDir;
/*     */   
/*     */ 
/*     */   File repos;
/*     */   
/*     */ 
/*     */   File resultsDir;
/*     */   
/*     */ 
/*     */ 
/*     */   public static void main(String[] args)
/*     */     throws Exception
/*     */   {
/*  92 */     Parser parser = new PosixParser();
/*  93 */     CommandLine params = parser.parse(OPTIONS, args);
/*  94 */     int contextLength = Integer.parseInt(params.getOptionValue("context"));
/*  95 */     boolean train = params.getOptionValue("mode", "score").equals("train");
/*  96 */     ContextCount.contextLength = contextLength;
/*  97 */     ContextCount.variableOrder = true;
/*  98 */     PrintWriter summary = new PrintWriter(new BufferedWriter(new FileWriter("summary_" + contextLength)));
/*     */     
/* 100 */     ContextSCOPTest dp_manager = new ContextSCOPTest(
/*     */     
/* 102 */       params);
/*     */     double[] xvec;
/* 104 */     if (params.hasOption("smoothing")) {
/* 105 */       String[] interp = params.getOptionValues("smoothing");
/*     */       
/* 107 */       double cont = Double.parseDouble(interp[0]);
/* 108 */       double spec = Double.parseDouble(interp[1]);
/* 109 */       double[] xvec = 
/* 110 */         { cont, spec == 1.0D ? new double[] { cont } : cont == 1.0D ? new double[] { spec } : spec };
/* 111 */       ContextTransitionScores.CONTEXT = cont;
/* 112 */       ContextTransitionScores.SPECIES = spec;
/*     */     }
/*     */     else
/*     */     {
/* 116 */       xvec = new double[] { 0.7D, 0.35D };
/* 117 */       ContextTransitionScores.CONTEXT = xvec[0];
/* 118 */       ContextTransitionScores.SPECIES = xvec[1];
/*     */     }
/* 120 */     context = ContextTransitionScores.CONTEXT != 1.0D;
/* 121 */     species = ContextTransitionScores.SPECIES != 1.0D;
/* 122 */     int length = (context) && (species) ? 2 : 1;
/* 123 */     MultivariateFunction mvf = new MultivariateFunction()
/*     */     {
/*     */       public int getNumArguments()
/*     */       {
/* 127 */         return this.val$length;
/*     */       }
/*     */       
/* 130 */       public double getLowerBound(int i) { return 0.01D; }
/*     */       
/*     */       public double getUpperBound(int i) {
/* 133 */         return 0.99D;
/*     */       }
/*     */       
/* 136 */       public OrthogonalHints getOrthogonalHints() { return null; }
/*     */       
/*     */       public double evaluate(double[] args)
/*     */       {
/*     */         try {
/* 141 */           if (ContextSCOPTest.context) {
/* 142 */             ContextTransitionScores.CONTEXT = args[0];
/* 143 */             if (ContextSCOPTest.species) {
/* 144 */               ContextTransitionScores.SPECIES = args[1];
/*     */             }
/* 146 */           } else if (ContextSCOPTest.species) {
/* 147 */             ContextTransitionScores.SPECIES = args[0]; }
/* 148 */           this.val$summary.println("evaluating " + Print.toString(args));
/* 149 */           this.val$summary.flush();
/* 150 */           int[] score = this.val$dp_manager.run(this.val$train);
/*     */           
/* 152 */           this.val$summary.println("area " + score[0] + " " + score[1]);
/* 153 */           this.val$summary.flush();
/*     */           
/*     */ 
/* 156 */           return score[1] * -1.0D;
/*     */         } catch (Exception exc) {
/* 158 */           exc.printStackTrace();
/* 159 */           System.exit(0);
/*     */         }
/* 161 */         return 0.0D;
/*     */       }
/*     */     };
/* 164 */     if (!train) { mvf.evaluate(xvec);
/*     */     } else {
/* 166 */       MultivariateMinimum mvm = new OrthogonalSearch();
/* 167 */       mvm.optimize(mvf, xvec, 10.0D, 0.02D);
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
/* 178 */   File hmm_dir = null;
/*     */   SequenceDB pfamDB;
/*     */   SequenceDB astralDB;
/*     */   File astralDBIndex;
/*     */   File scopDir;
/*     */   Map sprot2Ncbi;
/*     */   Map astralToSprot;
/*     */   DomainAlphabet alph;
/*     */   PfamIndex pfam_index;
/*     */   CommandLine params;
/*     */   SpeciesAlphabet spec_al;
/*     */   SymbolTokenization speciesTokenizer;
/*     */   static final String printStyle1 = "%-15s %15.3f %15.3g %15.3f %15.3g  %15s\n";
/*     */   static final String headerStr = "%-15s %15s %15s %15s, %15s \n";
/*     */   
/*     */   ContextSCOPTest(CommandLine params) {
/*     */     try {
/* 195 */       this.params = params;
/* 196 */       DomainDPFactory dpFact = new DomainDPFactory(params);
/* 197 */       this.dp = dpFact.create();
/*     */       
/* 199 */       this.sprot2Ncbi = new HashMap();
/*     */       
/*     */ 
/* 202 */       this.repos = new File(params.getOptionValue("repository"));
/* 203 */       this.pfam_index = new PfamIndex(this.repos);
/* 204 */       this.outpDir = new File(params.getOptionValue("dir", "."));
/* 205 */       for (Iterator it = SheetIO.read(new File(this.outpDir, "sprot2ncbi"), "\t"); it.hasNext();) {
/* 206 */         List row = (List)it.next();
/* 207 */         this.sprot2Ncbi.put(row.get(0), row.get(1));
/*     */       }
/*     */       
/* 210 */       this.alph = dpFact.model.getFrequency().getAlphabet();
/* 211 */       this.spec_al = dpFact.model.getFrequency().getSpeciesAlphabet();
/*     */       
/* 213 */       this.speciesTokenizer = this.spec_al.getTokenization("token");
/* 214 */       System.err.println("getting pfamdb");
/* 215 */       System.err.println("alphabet size " + this.alph.size());
/* 216 */       this.pfamDB = new IndexedPfamDB(new File(this.outpDir, "pfamA_astral_ls"), this.alph, "\t", 0, 1);
/* 217 */       System.err.println("done");
/* 218 */       this.hmm_dir = new File(this.outpDir, "hmmerTest");
/* 219 */       this.scopDir = new File(this.outpDir, "scop");
/* 220 */       this.resultsDir = new File(this.outpDir, "results_" + ContextCount.contextLength);
/* 221 */       if (!this.resultsDir.exists()) this.resultsDir.mkdir();
/* 222 */       System.err.println("results dir is " + this.resultsDir);
/* 223 */       this.astralDBIndex = new File(params.getOptionValue("seqdb") + "_idx");
/* 224 */       if ((!this.astralDBIndex.exists()) || (this.astralDBIndex.length() == 0L)) {
/* 225 */         System.err.println("indexing ...");
/* 226 */         FlatFileTools.createIndex(this.astralDBIndex, "fasta", "protein");
/* 227 */         FlatFileTools.addFilesToIndex(this.astralDBIndex, new File[] { new File(params.getOptionValue("seqdb")) });
/* 228 */         System.err.println("...done");
/*     */       }
/* 230 */       this.astralDB = FlatFileTools.open(this.astralDBIndex);
/* 231 */       this.astralToSprot = new HashMap();
/* 232 */       for (SequenceIterator seqIt = this.astralDB.sequenceIterator(); 
/* 233 */             seqIt.hasNext();) {
/* 234 */         Sequence seq = seqIt.nextSequence();
/* 235 */         String sprot = seq.getName();
/* 236 */         String[] desc = ((String)seq.getAnnotation().getProperty("description")).trim().split("\\s+");
/* 237 */         this.astralToSprot.put(seq.getName(), desc[0]);
/*     */       }
/*     */       
/*     */ 
/*     */     }
/*     */     catch (Exception exc)
/*     */     {
/* 244 */       exc.printStackTrace();
/*     */     }
/*     */   }
/*     */   
/*     */   public int[] run(boolean shorten) throws Exception
/*     */   {
/* 250 */     File[] parent = { this.resultsDir };
/* 251 */     int[] count = new int[2];
/* 252 */     Set better = new HashSet();
/* 253 */     Set worse = new HashSet();
/* 254 */     File seqdb = new File(this.params.getOptionValue("seqdb"));
/* 255 */     Map totals = ParseContextResults.getTotals(new File(this.outpDir, "scop_totals"));
/* 256 */     if (this.params.hasOption("domain")) {
/* 257 */       File f = new File(this.hmm_dir, this.params.getOptionValue("domain"));
/* 258 */       File resultsFile = new File(this.resultsDir, f.getName());
/* 259 */       boolean success = run(f);
/* 260 */       if (success) {
/* 261 */         int[] d = ParseContextResults.getROC(parent, new String[] { f.getName() }).OTS();
/* 262 */         System.err.println(d);
/*     */       }
/*     */       
/*     */ 
/*     */     }
/*     */     else
/*     */     {
/* 269 */       File[] f = this.scopDir.listFiles();
/*     */       
/* 271 */       int len = shorten ? 500 : f.length;
/* 272 */       int start = shorten ? 0 : 500;
/* 273 */       for (int i = start; i < len; i++) {
/* 274 */         File hmmF = new File(this.hmm_dir, f[i].getName());
/*     */         
/* 276 */         if ((hmmF.exists()) && (hmmF.length() != 0L)) {
/* 277 */           boolean success = run(hmmF);
/* 278 */           if (success) {
/* 279 */             d = ParseContextResults.getROC(parent, new String[] { f[i].getName() }).OTS();
/* 280 */             if (d[0] != d[1]) {
/* 281 */               System.out.println(f[i].getName() + " " + d[0] + " " + d[1] + "\t" + count[0] + " " + count[1]);
/* 282 */               if (d[1] > d[0]) better.add(f[i].getName()); else
/* 283 */                 worse.add(f[i].getName());
/*     */             }
/* 285 */             count[0] += d[0];
/* 286 */             count[1] += d[1];
/* 287 */             if ((count[1] < count[0] - 10) && (count[1] > 100)) return new int[2];
/*     */           }
/* 289 */           int[] d = new File(this.resultsDir, f[i].getName());
/*     */         }
/*     */       }
/*     */       
/* 293 */       System.out.println("better " + better.size() + " " + better);
/* 294 */       System.err.println("worse " + worse.size() + " " + worse);
/*     */     }
/* 296 */     return count;
/*     */   }
/*     */   
/*     */   public static Collection[] getSfCf(File scopDir, String name) throws IOException {
/* 300 */     Set sf = new HashSet();
/* 301 */     Set cf = new HashSet();
/* 302 */     File scopFile = new File(scopDir, name);
/* 303 */     boolean fold = false;
/* 304 */     for (Iterator it = SheetIO.getColumn(SheetIO.read(scopFile, "\\s+"), 0); it.hasNext();) {
/* 305 */       String st = (String)it.next();
/* 306 */       if (st.startsWith("#")) {
/* 307 */         fold = true;
/*     */       }
/* 309 */       else if (fold) {
/* 310 */         cf.add(st);
/*     */       }
/*     */       else {
/* 313 */         sf.add(st);
/*     */       }
/*     */     }
/* 316 */     return new Collection[] { sf, cf };
/*     */   }
/*     */   
/*     */   public boolean run(File f)
/*     */     throws Exception
/*     */   {
/* 322 */     File scopFile = new File(this.scopDir, f.getName());
/* 323 */     File resultsFile = new File(this.resultsDir, f.getName());
/*     */     
/*     */ 
/* 326 */     Collection[] sf_cf = getSfCf(this.scopDir, f.getName());
/* 327 */     Collection sf = sf_cf[0];
/* 328 */     Collection cf = sf_cf[1];
/* 329 */     if (sf.size() == 0) return false;
/* 330 */     PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(resultsFile)));
/* 331 */     pw.print(Format.sprintf("%-15s %15s %15s %15s, %15s \n", new Object[] { "Sequence", "Score", "E-value", "HMMER score", "HMMER evalue" }));
/* 332 */     pw.println("--------------------------------------------------------------------");
/*     */     try {
/*     */       Symbol sym;
/*     */       try {
/* 336 */         sym = this.alph.nameToSymbolList(new String[] { f.getName() }).symbolAt(1);
/*     */       } catch (Exception exc) { Symbol sym;
/* 338 */         sym = this.alph.nameToSymbolList(new String[] { f.getName() + "_1" }).symbolAt(1);
/*     */       }
/* 340 */       SequenceDB astralDB1 = HmmerLauncher.parseHmmerResult(new BufferedReader(new FileReader(f)), this.astralDB, sym, this.alph, 1000.0D);
/*     */       
/* 342 */       List scores = new ArrayList();
/* 343 */       for (SequenceIterator it = astralDB1.sequenceIterator(); it.hasNext();) {
/* 344 */         Sequence seq = it.nextSequence();
/* 345 */         System.err.println(seq.getName());
/* 346 */         if (seq.getName().equals("d1ccza2")) {
/* 347 */           String[] desc = ((String)seq.getAnnotation().getProperty("description")).trim().split("\\s+");
/*     */           
/*     */ 
/*     */ 
/* 351 */           String id = desc[0].trim();
/* 352 */           Sequence pfamseq = ((IndexedPfamDB)this.pfamDB).contains(id) ? this.pfamDB.getSequence(id) : 
/* 353 */             new SimpleSequence(SymbolList.EMPTY_LIST, id, id, Annotation.EMPTY_ANNOTATION);
/* 354 */           double hmmScore = 0.0D;
/* 355 */           List testFeatureList = new ArrayList();
/* 356 */           for (Iterator feat = seq.features(); feat.hasNext();) {
/* 357 */             Domain dom = (Domain)feat.next();
/* 358 */             testFeatureList.add(dom);
/* 359 */             hmmScore += dom.getScore();
/*     */           }
/* 361 */           Domain[] testFeatures = (Domain[])testFeatureList.toArray(new Domain[0]);
/* 362 */           seq.createFeature(new Domain.MagicTemplate(this.alph, 0));
/* 363 */           if (context)
/*     */           {
/* 365 */             seq.createFeature(new Domain.MagicTemplate(this.alph, Integer.MAX_VALUE));
/*     */             
/*     */ 
/* 368 */             for (Iterator feat = pfamseq.features(); feat.hasNext();) {
/* 369 */               Feature feature = (Feature)feat.next();
/*     */               
/* 371 */               int j = 0;
/* 372 */               while (!feature.getLocation().overlaps(testFeatures[j].getLocation()))
/*     */               {
/* 371 */                 j++; if (j >= testFeatures.length)
/*     */                 {
/*     */ 
/* 374 */                   seq.createFeature(feature.makeTemplate()); }
/*     */               }
/*     */             } }
/* 377 */           Object spec = this.sprot2Ncbi.get(id);
/* 378 */           if (spec == null) throw new RuntimeException("species is null for " + id);
/* 379 */           if (spec.equals("null")) spec = "1";
/* 380 */           seq.getAnnotation().setProperty("species", spec);
/*     */           
/* 382 */           double score1 = this.dp.score(seq);
/*     */           
/* 384 */           for (int j = 0; j < testFeatures.length; j++) {
/* 385 */             seq.removeFeature(testFeatures[j]);
/*     */           }
/* 387 */           double score = score1 - this.dp.score(seq);
/* 388 */           double mu = Double.parseDouble((String)sym.getAnnotation().getProperty("mu"));
/* 389 */           double lambda = Double.parseDouble((String)sym.getAnnotation().getProperty("lambda"));
/* 390 */           int totalSeqs = Integer.parseInt((String)sym.getAnnotation().getProperty("total seqs"));
/* 391 */           EVD evd = new EVD(lambda, mu);
/* 392 */           double evalue = evd.extremeValueP(score) * totalSeqs;
/* 393 */           double hmmEvalue = evd.extremeValueP(hmmScore) * totalSeqs;
/* 394 */           int no = cf.contains(seq.getName()) ? 0 : sf.contains(seq.getName()) ? 1 : -1;
/* 395 */           Object[] obj = { seq.getName(), new Double(score), new Double(evalue), 
/* 396 */             new Double(hmmScore), new Double(hmmEvalue), 
/* 397 */             no, f.getName() };
/* 398 */           pw.print(Format.sprintf("%-15s %15.3f %15.3g %15.3f %15.3g  %15s\n", obj));
/* 399 */           pw.flush();
/*     */         }
/*     */       }
/*     */       
/* 403 */       pw.close();
/* 404 */       return true;
/*     */     } catch (Throwable t) {
/* 406 */       t.printStackTrace(); }
/* 407 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   static Symbol getFirstCommonAncestor(SpeciesAlphabet spec_al, String[] strings)
/*     */     throws Exception
/*     */   {
/* 420 */     SymbolTokenization speciesTokenizer = spec_al.getTokenization("token");
/* 421 */     SymbolList[] sl = new SymbolList[strings.length];
/* 422 */     int min_length = Integer.MAX_VALUE;
/* 423 */     for (int i = 0; i < sl.length; i++) {
/* 424 */       sl[i] = spec_al.taxaToList(speciesTokenizer.parseToken(strings[i]));
/*     */       
/* 426 */       if (sl[i].length() < min_length) min_length = sl[i].length();
/*     */     }
/* 428 */     Symbol prev_sym = null;
/* 429 */     for (int j = 0; j < min_length; j++) {
/* 430 */       Symbol sym = sl[0].symbolAt(sl[0].length() - j);
/* 431 */       for (int i = 1; i < sl.length; i++) {
/* 432 */         if (sl[i].symbolAt(sl[i].length() - i) != sym) return prev_sym;
/*     */       }
/* 434 */       prev_sym = sym;
/*     */     }
/*     */     
/* 437 */     return prev_sym;
/*     */   }
/*     */   
/*     */   private void print(Domain sym, String seqNam, String spec, PrintWriter pw)
/*     */   {
/* 442 */     Annotation annot = sym.getAnnotation();
/*     */     
/* 444 */     pw.print(Format.sprintf("%-15s %15.3f %15.3g %15.3f %15.3g  %15s\n", 
/* 445 */       new Parameters(seqNam)
/* 446 */       .add(sym.getSymbol().getName())
/* 447 */       .add(spec)
/* 448 */       .add(sym.getScore())));
/*     */     
/* 450 */     pw.flush();
/*     */   }
/*     */ }


/* Location:              /home/lachlan/Desktop/psilc1.21/psilc.jar!/lc1/domains/ContextSCOPTest.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */