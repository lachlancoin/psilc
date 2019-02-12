/*     */ package lc1.util;
/*     */ 
/*     */ import JSci.maths.statistics.BinomialDistribution;
/*     */ import JSci.maths.statistics.NormalDistribution;
/*     */ import JSci.maths.statistics.PoissonDistribution;
/*     */ import JSci.maths.statistics.ProbabilityDistribution;
/*     */ import com.braju.format.Format;
/*     */ import gnu.trove.TObjectIntHashMap;
/*     */ import java.io.BufferedReader;
/*     */ import java.io.BufferedWriter;
/*     */ import java.io.File;
/*     */ import java.io.FileFilter;
/*     */ import java.io.FileReader;
/*     */ import java.io.FileWriter;
/*     */ import java.io.PrintStream;
/*     */ import java.io.PrintWriter;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.Comparator;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import lc1.pfam.DomainAlphabet;
/*     */ import lc1.pfam.PfamAlphabet;
/*     */ import org.apache.commons.cli.CommandLine;
/*     */ import org.apache.commons.cli.OptionBuilder;
/*     */ import org.apache.commons.cli.Options;
/*     */ import org.apache.commons.cli.Parser;
/*     */ import org.apache.commons.cli.PosixParser;
/*     */ import org.biojava.bio.Annotation;
/*     */ import org.biojava.bio.seq.io.SymbolTokenization;
/*     */ import org.biojava.bio.symbol.FiniteAlphabet;
/*     */ import org.biojava.bio.symbol.IllegalSymbolException;
/*     */ import org.biojava.bio.symbol.Symbol;
/*     */ import org.ensembl.datamodel.Analysis;
/*     */ import org.ensembl.datamodel.Gene;
/*     */ import org.ensembl.datamodel.Location;
/*     */ import org.ensembl.datamodel.Transcript;
/*     */ import org.ensembl.datamodel.Translation;
/*     */ import org.ensembl.datamodel.impl.ExternalRefImpl;
/*     */ import org.ensembl.datamodel.impl.SimplePeptideFeatureImpl;
/*     */ import org.ensembl.driver.AdaptorException;
/*     */ import org.ensembl.driver.Driver;
/*     */ import org.ensembl.driver.SimplePeptideAdaptor;
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
/*     */ public class ParsePSILC
/*     */ {
/*     */   static final int SEL = 3;
/*     */   static final int PSE = 4;
/*     */   static final int PSE_PSILC = 1;
/*     */   static final double SEL_THRESH = 0.5D;
/*     */   static final double PS_THRESH_PSILC = 50.0D;
/*     */   static final double PS_THRESH = 1.0D;
/*  73 */   static boolean pseudo = true;
/*  74 */   static boolean selection = true;
/*     */   static final String printStr = "%-35s %6d %6d %6.2g %6.2g %6.2g\n";
/*  76 */   static final Integer zero = new Integer(0);
/*  77 */   static final Options OPTIONS = new Options() {};
/*     */   
/*     */ 
/*     */   SymbolTokenization token;
/*     */   
/*     */ 
/*     */   FiniteAlphabet alph;
/*     */   
/*     */ 
/*  86 */   Map speciesData = new HashMap();
/*     */   
/*  88 */   static boolean write = false;
/*     */   static File repos;
/*     */   
/*     */   ParsePSILC(FiniteAlphabet alph) throws Exception {
/*  92 */     File f = new File("pfamDetailsDirectory");
/*  93 */     f.mkdir();
/*     */     
/*  95 */     this.drivers = Ensembl.getDBNames(new File("/nfs/team71/phd/lc1/Data/lc1/ensembldb"), null);
/*  96 */     Iterator it = this.drivers.keySet().iterator();
/*     */     try {
/*  98 */       for (int i = 0; it.hasNext(); i++) {
/*  99 */         String taxa = (String)it.next();
/* 100 */         this.speciesData.put(taxa, new SpeciesData(f, taxa));
/*     */       }
/*     */     }
/*     */     catch (Exception exc) {
/* 104 */       exc.printStackTrace();
/* 105 */       System.exit(0);
/*     */     }
/* 107 */     this.alph = alph;
/* 108 */     this.token = alph.getTokenization("token");
/*     */   }
/*     */   
/*     */   public static void main(String[] args) throws Exception {
/* 112 */     Parser parser = new PosixParser();
/* 113 */     CommandLine params = parser.parse(OPTIONS, args);
/* 114 */     repos = new File(params.getOptionValue("repository", "/nfs/team71/phd/lc1/Data/lc1/"));
/* 115 */     DomainAlphabet alph = PfamAlphabet.makeAlphabet(repos);
/* 116 */     ParsePSILC analy = new ParsePSILC(alph);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 126 */     if (write) { analy.readData1();
/*     */     }
/*     */     
/* 129 */     for (Iterator it = analy.speciesData.values().iterator(); it.hasNext();) {
/* 130 */       SpeciesData sd = (SpeciesData)it.next();
/* 131 */       if (!write) sd.readData1();
/* 132 */       System.err.println("doing " + sd.taxa);
/* 133 */       sd.printToFile("pfam");
/* 134 */       sd.printToFile("go");
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
/* 154 */   static final Comparator comp = new Comparator() {
/*     */     public int compare(Object o1, Object o2) {
/* 156 */       double d1 = ((Double)((Object[])o1)[0]).doubleValue();
/* 157 */       double d2 = ((Double)((Object[])o1)[0]).doubleValue();
/* 158 */       if (d1 == d2) return 0;
/* 159 */       return d1 < d2 ? -1 : 1;
/*     */     }
/*     */   };
/*     */   Map drivers;
/*     */   
/* 164 */   private static int min(int[] l) { if (l.length == 0) return -1;
/* 165 */     int min = Integer.MAX_VALUE;
/* 166 */     for (int i = 0; i < l.length; i++) {
/* 167 */       if (l[i] < min) min = l[i];
/*     */     }
/* 169 */     return min;
/*     */   }
/*     */   
/*     */   public void readData1() throws Exception
/*     */   {
/* 174 */     File f = new File("families_phigs");
/* 175 */     List selection = new ArrayList();
/* 176 */     List pseudogene = new ArrayList();
/* 177 */     File[] files = f.listFiles(new FileFilter() {
/*     */       public boolean accept(File fi) {
/* 179 */         return !fi.getName().endsWith("err");
/*     */       }
/*     */     });
/*     */     
/* 183 */     for (int ik = 0; ik < files.length; ik++) {
/* 184 */       int i = ik;
/* 185 */       Runnable run = new Runnable()
/*     */       {
/*     */         public void run() {
/* 188 */           System.err.println("running " + this.val$i);
/*     */           try
/*     */           {
/* 191 */             File psilc = new File(this.val$files[this.val$i], "PSILC_WAG_HKY_recursive");
/* 192 */             File ps = new File(psilc, "summary");
/*     */             
/* 194 */             if ((ps.exists()) && (ps.length() > 0L)) {
/* 195 */               BufferedReader br = new BufferedReader(new FileReader(ps));
/*     */               
/* 197 */               String st = br.readLine();
/* 198 */               List l = new ArrayList();
/* 199 */               List peptideFeatures = new ArrayList();
/* 200 */               List extRefs = new ArrayList();
/* 201 */               while ((st = br.readLine()) != null)
/*     */               {
/* 203 */                 String[] str = st.split("\\s+");
/* 204 */                 String id = str[5].split("&&")[0];
/* 205 */                 String taxId = Ensembl.getTaxaId(id);
/* 206 */                 if (taxId.equals("9606")) {
/* 207 */                   ParsePSILC.SpeciesData specC = (ParsePSILC.SpeciesData)ParsePSILC.this.speciesData.get(taxId);
/* 208 */                   if (specC == null) {
/* 209 */                     System.err.println("no db for " + id + " " + taxId + " in" + this.val$files[this.val$i]);
/*     */                   }
/*     */                   else {
/* 212 */                     String idst = str[5].split("&&")[0];
/*     */                     try {
/* 214 */                       idst = (!idst.startsWith("C")) && (idst.indexOf('.') >= 0) ? idst.substring(0, idst.indexOf('.')) : idst;
/*     */                       
/* 216 */                       Gene tr = Ensembl.fetchByGeneID(specC.driver, idst);
/* 217 */                       if (tr == null) {
/* 218 */                         System.err.println("no gene found for " + idst + " " + taxId);
/* 219 */                         return;
/*     */                       }
/* 221 */                       Transcript trans = (Transcript)tr.getTranscripts().get(0);
/* 222 */                       peptideFeatures.addAll(specC.driver.getSimplePeptideAdaptor().fetch(trans.getTranslation()));
/* 223 */                       extRefs.addAll(trans.getExternalRefs());
/* 224 */                       l.add(new Object[] { specC, str, tr });
/*     */                     } catch (AdaptorException e) {
/* 226 */                       System.err.println("problem with " + idst + " taxa " + taxId);
/* 227 */                       e.printStackTrace();
/* 228 */                       System.exit(0);
/*     */                     }
/*     */                   } } }
/* 231 */               for (Iterator it = l.iterator(); it.hasNext();) {
/* 232 */                 Object[] obj = (Object[])it.next();
/* 233 */                 ((ParsePSILC.SpeciesData)obj[0]).count((String[])obj[1], (Gene)obj[2], this.val$files[this.val$i].getName(), peptideFeatures, extRefs);
/*     */               }
/* 235 */               br.close();
/*     */             }
/*     */           } catch (Exception exc) {
/* 238 */             exc.printStackTrace();
/* 239 */             System.exit(0);
/*     */           }
/*     */         }
/*     */       };
/*     */       do {
/* 244 */         Thread.currentThread();Thread.sleep(100L);
/* 245 */         System.err.println("sleeping");Thread.currentThread();
/* 243 */       } while (Thread.activeCount() >= 5);
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 248 */       run.run();
/*     */     }
/*     */     
/* 251 */     PrintWriter pseudo = new PrintWriter(new BufferedWriter(new FileWriter(new File(f, "pseudogenes"))));
/* 252 */     Collections.sort(pseudogene, comp);
/*     */     
/* 254 */     for (Iterator it = pseudogene.iterator(); it.hasNext();) {
/* 255 */       Object[] obj = (Object[])it.next();
/* 256 */       pseudo.println(obj[1]);
/*     */     }
/* 258 */     pseudo.close();
/* 259 */     PrintWriter selO = new PrintWriter(new BufferedWriter(new FileWriter(new File(f.getParent(), "selection"))));
/* 260 */     Collections.sort(selection, comp);
/* 261 */     for (Iterator it = selection.iterator(); it.hasNext();) {
/* 262 */       Object[] obj = (Object[])it.next();
/* 263 */       selO.println(obj[1]);
/*     */     }
/*     */     
/* 266 */     selO.close();
/*     */   }
/*     */   
/*     */ 
/*     */   class SpeciesData
/*     */   {
/*     */     Driver driver;
/*     */     
/*     */     File resultsDir;
/*     */     
/*     */     PrintWriter log;
/*     */     
/*     */     File logFile;
/* 279 */     int totalSel = 0;
/* 280 */     int totalPse = 0;
/* 281 */     int totalENS = 0;
/*     */     String taxa;
/* 283 */     HashMap[] countPfam = { new HashMap(), new HashMap() };
/* 284 */     HashMap[] countGo = { new HashMap(), new HashMap() };
/* 285 */     HashMap[] countMinIntrLength = { new HashMap(), new HashMap() };
/*     */     
/*     */     PrintWriter pseudoPW;
/*     */     PrintWriter selPW;
/* 289 */     List genes = new ArrayList();
/* 290 */     TObjectIntHashMap indices = new TObjectIntHashMap();
/*     */     
/*     */     SpeciesData(File parent, String taxa) throws Exception
/*     */     {
/* 294 */       this.driver = ((Driver)ParsePSILC.this.drivers.get(taxa));
/* 295 */       this.taxa = taxa;
/* 296 */       if (this.driver == null) {
/* 297 */         throw new Exception("no driver for " + taxa);
/*     */       }
/* 299 */       this.resultsDir = new File(parent, taxa);
/* 300 */       this.resultsDir.mkdir();
/* 301 */       this.pseudoPW = new PrintWriter(new FileWriter(new File(this.resultsDir, "pseudogenes")));
/* 302 */       this.selPW = new PrintWriter(new FileWriter(new File(this.resultsDir, "selection")));
/* 303 */       this.logFile = new File(this.resultsDir, "log");
/* 304 */       if (ParsePSILC.write) this.log = new PrintWriter(new BufferedWriter(new FileWriter(this.logFile)));
/*     */     }
/*     */     
/*     */     public void readDataForRandomization() throws Exception
/*     */     {
/* 309 */       BufferedReader br = new BufferedReader(new FileReader(this.logFile));
/* 310 */       for (String st = br.readLine(); st != null; st = br.readLine())
/*     */       {
/* 312 */         String[] str = st.split("\\s+");
/* 313 */         String id = str[3].split(":")[0];
/* 314 */         int index = this.genes.size();
/*     */         Collection l;
/* 316 */         Collection l; if (this.indices.contains(id)) {
/* 317 */           index = this.indices.get(id);
/* 318 */           l = (Collection)this.genes.get(index);
/*     */         }
/*     */         else {
/* 321 */           l = new ArrayList();
/* 322 */           this.indices.put(id, index);
/* 323 */           this.genes.add(l);
/*     */         }
/* 325 */         String name = str[2];
/* 326 */         for (int i = 3; i < str.length - 1; i++) {
/* 327 */           name = name + "_" + str[i];
/*     */         }
/* 329 */         l.add(name);
/* 330 */         if (str[2].startsWith("GO:"))
/*     */         {
/* 332 */           Integer count0 = (Integer)this.countGo[0].get(str[2]);
/* 333 */           this.countGo[0].put(str[2], count0 == null ? new Integer(1) : new Integer(count0.intValue() + 1));
/*     */         }
/*     */         else
/*     */         {
/* 337 */           Integer count0 = (Integer)this.countPfam[0].get(str[2]);
/* 338 */           this.countPfam[0].put(str[2], count0 == null ? new Integer(1) : new Integer(count0.intValue() + 1));
/*     */         }
/*     */       }
/*     */     }
/*     */     
/*     */     public void readData1()
/*     */       throws Exception
/*     */     {
/* 346 */       BufferedReader br = new BufferedReader(new FileReader(this.logFile));
/* 347 */       Set sel = new HashSet();
/* 348 */       Set all = new HashSet();
/* 349 */       for (String st = br.readLine(); st != null; st = br.readLine())
/*     */       {
/*     */ 
/* 352 */         String[] str = st.split("\\s+");
/* 353 */         double sc_sel = Double.parseDouble(str[0]);
/* 354 */         String id = str[3].split(":")[0];
/* 355 */         all.add(id);
/* 356 */         if (sc_sel > 0.5D) { sel.add(id);
/*     */         }
/* 358 */         if (str[2].startsWith("GO:"))
/*     */         {
/* 360 */           Integer count0 = (Integer)this.countGo[0].get(str[2]);
/* 361 */           Integer count1 = (Integer)this.countGo[1].get(str[2]);
/* 362 */           this.countGo[0].put(str[2], count0 == null ? new Integer(1) : new Integer(count0.intValue() + 1));
/* 363 */           if (sc_sel > 0.5D) {
/* 364 */             this.countGo[1].put(str[2], count1 == null ? new Integer(1) : new Integer(count1.intValue() + 1));
/*     */           }
/*     */         }
/*     */         else
/*     */         {
/* 369 */           String name = str[2];
/* 370 */           for (int i = 3; i < str.length - 1; i++) {
/* 371 */             name = name + "_" + str[i];
/*     */           }
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
/* 384 */           Integer count0 = (Integer)this.countPfam[0].get(name);
/* 385 */           Integer count1 = (Integer)this.countPfam[1].get(name);
/* 386 */           this.countPfam[0].put(name, count0 == null ? new Integer(1) : new Integer(count0.intValue() + 1));
/* 387 */           if (sc_sel > 0.5D) {
/* 388 */             this.countPfam[1].put(name, count1 == null ? new Integer(1) : new Integer(count1.intValue() + 1));
/*     */           }
/*     */         }
/*     */       }
/* 392 */       this.totalSel = sel.size();
/* 393 */       this.totalENS = all.size();
/*     */     }
/*     */     
/*     */     public void printToFile(String nam)
/*     */       throws Exception
/*     */     {
/* 399 */       File f = new File(this.resultsDir, nam);
/* 400 */       HashMap[] counts = 
/* 401 */         nam.equals("intr") ? this.countMinIntrLength : nam.equals("go") ? this.countGo : nam.equals("pfam") ? this.countPfam : null;
/* 402 */       PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(f)));
/* 403 */       pw.println("total\t" + this.totalSel + "\t" + this.totalENS);
/*     */       
/* 405 */       for (Iterator it = counts[0].keySet().iterator(); it.hasNext();)
/*     */       {
/* 407 */         String obj = ((String)it.next()).replaceAll(" ", "_");
/* 408 */         Integer count1 = (Integer)counts[1].get(obj);
/* 409 */         Integer count0 = (Integer)counts[0].get(obj);
/*     */         
/*     */ 
/* 412 */         double signif = getSignificance(count0, count1, nam.equals("intr") ? this.totalPse : this.totalSel);
/* 413 */         count1 = count1 == null ? ParsePSILC.zero : count1;
/* 414 */         if ((nam.equals("intr")) || (count1.intValue() != 0)) {
/* 415 */           double adj = signif * counts[1].size();
/* 416 */           Object[] toPrint = { obj, count1, count0, new Double(signif), new Double(adj), 
/* 417 */             new Double(signif == 0.0D ? -99.0D : Math.log(signif)) };
/* 418 */           pw.print(Format.sprintf("%-35s %6d %6d %6.2g %6.2g %6.2g\n", toPrint));
/*     */         } }
/* 420 */       pw.close();
/*     */     }
/*     */     
/* 423 */     public double getSignificance(Integer count0, Integer count1, int n) { if (count1 == null) return 1.0D;
/* 424 */       double p = count0.doubleValue() / this.totalENS;
/*     */       
/* 426 */       double q = 1.0D - p;
/* 427 */       double npq = n * p * q;
/*     */       ProbabilityDistribution binomial;
/*     */       ProbabilityDistribution binomial;
/* 430 */       if ((p < 0.1D) && (n * p < 10.0D)) {
/* 431 */         binomial = new PoissonDistribution(n * p);
/*     */       } else { ProbabilityDistribution binomial;
/* 433 */         if ((npq > 25.0D) || ((npq > 5.0D) && (p > 0.1D) && (p < 0.9D))) {
/* 434 */           binomial = new NormalDistribution(n * p, StrictMath.sqrt(npq));
/*     */         }
/*     */         else
/*     */         {
/* 438 */           binomial = new BinomialDistribution(n, p);
/*     */         }
/*     */       }
/* 441 */       return Math.max(0.0D, 1.0D - binomial.cumulative(count1.doubleValue() - 1.0D));
/*     */     }
/*     */     
/*     */     private Set getPfamSymbols(List peptideFeatures, int i) throws AdaptorException {
/* 445 */       Set pfam = new HashSet();
/* 446 */       for (Iterator it = peptideFeatures.iterator(); it.hasNext();) {
/* 447 */         SimplePeptideFeatureImpl impl = (SimplePeptideFeatureImpl)it.next();
/* 448 */         if (impl.getAnalysis().getLogicalName().equals("Pfam")) {
/* 449 */           String name = impl.getDisplayName();
/*     */           try
/*     */           {
/* 452 */             Symbol sym = ParsePSILC.this.token.parseToken(name);
/* 453 */             if (sym.getAnnotation().containsProperty("clan")) {
/* 454 */               name = (String)((Symbol)sym.getAnnotation().getProperty("clan")).getAnnotation().getProperty("clan_id");
/*     */             } else {
/* 456 */               name = (String)sym.getAnnotation().getProperty("pfamA_id");
/*     */             }
/*     */           }
/*     */           catch (IllegalSymbolException exc) {
/* 460 */             System.err.println(exc.getMessage());
/*     */           }
/* 462 */           pfam.add(name);
/*     */         }
/*     */       }
/*     */       
/* 466 */       for (Iterator it = pfam.iterator(); it.hasNext();) {
/* 467 */         Object name = it.next();
/* 468 */         Integer count = (Integer)this.countPfam[i].get(name);
/* 469 */         this.countPfam[i].put(name, count == null ? new Integer(1) : new Integer(count.intValue() + 1));
/*     */       }
/* 471 */       return pfam;
/*     */     }
/*     */     
/*     */     private Set getGoTerms(List extRefs, int i) throws AdaptorException {
/* 475 */       Set pfam = new HashSet();
/* 476 */       for (Iterator it = extRefs.iterator(); it.hasNext();) {
/* 477 */         ExternalRefImpl impl = (ExternalRefImpl)it.next();
/*     */         
/*     */ 
/* 480 */         if (impl.getDisplayID().startsWith("GO")) {
/* 481 */           String id = impl.getDisplayID();
/*     */           
/* 483 */           pfam.add(id);
/*     */         }
/*     */       }
/*     */       
/* 487 */       for (Iterator it = pfam.iterator(); it.hasNext();) {
/* 488 */         Object name = it.next();
/* 489 */         Integer count = (Integer)this.countGo[i].get(name);
/*     */         
/* 491 */         this.countGo[i].put(name, count == null ? new Integer(1) : new Integer(count.intValue() + 1));
/*     */       }
/* 493 */       return pfam;
/*     */     }
/*     */     
/*     */     private int getMinIntronLength(Gene tr, int i) {
/* 497 */       List cl = ((Transcript)tr.getTranscripts().get(0)).getTranslation().getCodingLocations();
/* 498 */       int[] int_size = new int[cl.size() - 1];
/* 499 */       int strand = ((Location)cl.get(0)).getStrand();
/* 500 */       for (int ik = 1; ik < cl.size(); ik++) {
/* 501 */         int_size[(ik - 1)] = (strand == 1 ? 
/* 502 */           ((Location)cl.get(ik)).getStart() - ((Location)cl.get(ik - 1)).getEnd() : 
/* 503 */           ((Location)cl.get(ik - 1)).getStart() - ((Location)cl.get(ik)).getEnd());
/*     */       }
/* 505 */       Integer min = new Integer((int)Math.floor(ParsePSILC.min(int_size) / 10.0D) * 10 + 5);
/* 506 */       Integer count = (Integer)this.countMinIntrLength[i].get(min);
/* 507 */       this.countMinIntrLength[i].put(min, count == null ? new Integer(1) : new Integer(count.intValue() + 1));
/* 508 */       return min.intValue();
/*     */     }
/*     */     
/*     */     public void count(String[] str, Gene tr, String clusterId, List peptideFeatures, List extRefs) throws Exception {
/* 512 */       double sc_sel = Double.parseDouble(str[3]);
/* 513 */       double sc_pseudo_psilc = Double.parseDouble(str[1]);
/* 514 */       double sc_pseudo = Double.parseDouble(str[4]);
/* 515 */       this.totalENS += 1;
/*     */       
/*     */ 
/*     */ 
/* 519 */       if (ParsePSILC.selection)
/*     */       {
/* 521 */         Set pfam = getPfamSymbols(peptideFeatures, 0);
/* 522 */         for (Iterator it = pfam.iterator(); it.hasNext();) {
/* 523 */           this.log.println(sc_sel + " " + clusterId + " " + it.next() + " " + str[1]);
/*     */         }
/* 525 */         Set go = getGoTerms(extRefs, 0);
/* 526 */         for (Iterator it = go.iterator(); it.hasNext();) {
/* 527 */           this.log.println(sc_sel + " " + clusterId + " " + it.next() + " " + str[1]);
/*     */         }
/* 529 */         this.log.flush();
/*     */         
/* 531 */         if (sc_sel > 0.5D)
/*     */         {
/* 533 */           this.totalSel += 1;
/* 534 */           getPfamSymbols(peptideFeatures, 1);
/* 535 */           getGoTerms(extRefs, 1);
/*     */           
/* 537 */           this.selPW.print(Format.sprintf("%6.4g, %30s \n", 
/* 538 */             new Object[] { new Double(sc_sel), clusterId + " " + str[1] + " " + str[4] + " " + tr.getDescription() }));
/* 539 */           this.selPW.flush();
/*     */         }
/*     */       }
/*     */       
/*     */ 
/*     */ 
/* 545 */       if (ParsePSILC.pseudo) {
/* 546 */         int minIntronLength = getMinIntronLength(tr, 0);
/* 547 */         if (sc_pseudo >= 1.0D) {
/* 548 */           this.totalPse += 1;
/*     */           
/*     */ 
/*     */ 
/* 552 */           getMinIntronLength(tr, 1);
/* 553 */           this.pseudoPW.print(Format.sprintf("%6.4g, %30s \n", 
/* 554 */             new Object[] { new Double(sc_pseudo), clusterId + " " + Arrays.asList(str) + " intr " + minIntronLength }));
/* 555 */           this.pseudoPW.flush();
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ }


/* Location:              /home/lachlan/Desktop/psilc1.21/psilc.jar!/lc1/util/ParsePSILC.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */