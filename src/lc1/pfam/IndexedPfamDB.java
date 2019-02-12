/*     */ package lc1.pfam;
/*     */ 
/*     */ import gnu.trove.TObjectLongHashMap;
/*     */ import gnu.trove.TObjectProcedure;
/*     */ import java.io.BufferedOutputStream;
/*     */ import java.io.BufferedReader;
/*     */ import java.io.BufferedWriter;
/*     */ import java.io.File;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.FileReader;
/*     */ import java.io.FileWriter;
/*     */ import java.io.PrintStream;
/*     */ import java.io.PrintWriter;
/*     */ import java.io.RandomAccessFile;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import lc1.domainseq.Domain;
/*     */ import lc1.domainseq.Domain.Template;
/*     */ import lc1.domainseq.DomainList;
/*     */ import lc1.domainseq.FeatureUtils;
/*     */ import lc1.util.SheetIO;
/*     */ import org.biojava.bio.Annotation;
/*     */ import org.biojava.bio.BioException;
/*     */ import org.biojava.bio.SimpleAnnotation;
/*     */ import org.biojava.bio.seq.FeatureHolder;
/*     */ import org.biojava.bio.seq.Sequence;
/*     */ import org.biojava.bio.seq.SequenceIterator;
/*     */ import org.biojava.bio.seq.db.AbstractSequenceDB;
/*     */ import org.biojava.bio.seq.db.IllegalIDException;
/*     */ import org.biojava.bio.seq.db.SequenceDB;
/*     */ import org.biojava.bio.seq.impl.SimpleSequence;
/*     */ import org.biojava.bio.seq.io.SeqIOTools;
/*     */ import org.biojava.bio.seq.io.SymbolTokenization;
/*     */ import org.biojava.bio.symbol.Alphabet;
/*     */ import org.biojava.bio.symbol.IllegalSymbolException;
/*     */ import org.biojava.bio.symbol.Location;
/*     */ import org.biojava.bio.symbol.LocationTools;
/*     */ import org.biojava.bio.symbol.Symbol;
/*     */ import org.biojava.bio.symbol.SymbolList;
/*     */ 
/*     */ public class IndexedPfamDB
/*     */   extends AbstractSequenceDB
/*     */ {
/*     */   File pfam;
/*     */   int id_col;
/*     */   int pfam_col;
/*     */   String split;
/*  54 */   static final Double zeroD = new Double(0.0D);
/*  55 */   static final Integer zero = new Integer(0);
/*  56 */   boolean print = true;
/*     */   
/*     */   RandomAccessFile raf;
/*     */   DomainAlphabet alph;
/*     */   String name;
/*     */   
/*     */   public String getName()
/*     */   {
/*  64 */     return this.name;
/*     */   }
/*     */   
/*     */   public static void printSequence(PrintWriter pw, Sequence dl) {
/*  68 */     for (Iterator it = dl.features(); it.hasNext();) {
/*  69 */       printDomainLine(pw, (Domain)it.next(), dl);
/*     */     }
/*     */   }
/*     */   
/*     */   private static void printDomainLine(PrintWriter pw, Domain d, Sequence dl) {
/*  74 */     String species = (String)dl.getAnnotation().getProperty("species");
/*     */     
/*  76 */     pw.print(dl.getName());pw.print("\t");
/*  77 */     pw.print(d.getSymbol().getName());pw.print("\t");
/*  78 */     pw.print(d.getLocation().getMin());pw.print("\t");
/*  79 */     pw.print(d.getLocation().getMax());pw.print("\t");
/*  80 */     pw.print(d.getScore());pw.print("\t");
/*  81 */     pw.print("ls");pw.print("\t");
/*  82 */     pw.print(dl.getAnnotation().getProperty("pfamseq_id"));pw.print("\t");
/*  83 */     pw.print(species);pw.println("\t");
/*     */   }
/*     */   
/*     */   public static void main(String[] args)
/*     */     throws Exception
/*     */   {
/*  89 */     extractFromFastaFile(args);
/*     */   }
/*     */   
/*     */ 
/*     */   public static void splitIntoRandomSubSets(File repos, double[] cumulativeProbs)
/*     */     throws Exception
/*     */   {
/*  96 */     File outpDir = new File(".");
/*  97 */     PrintWriter[] pw = new PrintWriter[cumulativeProbs.length];
/*  98 */     for (int i = 0; i < cumulativeProbs.length; i++) {
/*  99 */       pw[i] = new PrintWriter(new BufferedWriter(new FileWriter(new File(outpDir, "output_" + i))));
/*     */     }
/* 101 */     PfamAlphabet alph = PfamAlphabet.makeAlphabet(new File(repos, "pfamA"));
/* 102 */     SequenceIterator db = SequenceIterator(new File(repos, "pfamA_reg_full_ls_sorted"), alph, "\t", 0, 1);
/*     */     Iterator it;
/* 104 */     for (; db.hasNext(); 
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 111 */         it.hasNext())
/*     */     {
/* 105 */       double rand = Math.random();
/* 106 */       int index = 0;
/* 107 */       while (cumulativeProbs[index] < rand) {
/* 108 */         index++;
/*     */       }
/* 110 */       Sequence dl = db.nextSequence();
/* 111 */       it = dl.features(); continue;
/* 112 */       Domain d = (Domain)it.next();
/* 113 */       printDomainLine(pw[index], d, dl);
/*     */     }
/*     */     
/* 116 */     for (int i = 0; i < cumulativeProbs.length; i++) {
/* 117 */       pw[i].close();
/*     */     }
/*     */   }
/*     */   
/*     */   public static void reorder(String[] args) throws Exception {
/* 122 */     File f = new File("/home/lc/Data/lc1/pfamA_reg_full_ls");
/* 123 */     PrintWriter pw = new PrintWriter(
/* 124 */       new BufferedWriter(new FileWriter("/home/lc/Data/lc1/pfamA_reg_full1")));
/* 125 */     for (Iterator it = SheetIO.read(f, "\t"); it.hasNext();) {
/* 126 */       List row = (List)it.next();
/* 127 */       Object acc = row.get(0);
/* 128 */       row.set(0, row.get(6));
/* 129 */       row.set(6, acc);
/* 130 */       for (int i = 0; i < row.size() - 1; i++) {
/* 131 */         pw.print(row.get(i));pw.print("\t");
/*     */       }
/* 133 */       pw.println(row.get(row.size() - 1));
/*     */     }
/* 135 */     pw.close();
/*     */   }
/*     */   
/*     */   public static void fixTaxonomy(String[] args) throws Exception {
/* 139 */     Map map = new HashMap();
/* 140 */     File repos = new File(args[0]);
/* 141 */     File outpDir = new File(".");
/* 142 */     Map nullSet = new HashMap();
/* 143 */     Map sprotToAstral = new HashMap();
/* 144 */     Map astralToSpec = new HashMap();
/* 145 */     SequenceIterator fasta = SeqIOTools.readFastaProtein(new BufferedReader(new FileReader(new File("sprot.fasta"))));
/* 146 */     SequenceIterator astral = SeqIOTools.readFastaProtein(new BufferedReader(new FileReader(new File("astral"))));
/* 147 */     while (astral.hasNext()) {
/* 148 */       Sequence seq = astral.nextSequence();
/* 149 */       String desc = ((String)seq.getAnnotation().getProperty("description")).trim();
/* 150 */       String spec = desc.substring(desc.indexOf('{') + 1, desc.indexOf('}'));
/* 151 */       int start = spec.indexOf('(');
/* 152 */       if (start >= 0) {
/* 153 */         spec = spec.substring(start + 1, spec.indexOf(')'));
/*     */       }
/*     */       
/* 156 */       astralToSpec.put(seq.getName(), spec);
/*     */     }
/* 158 */     while (fasta.hasNext()) {
/* 159 */       Sequence seq = fasta.nextSequence();
/* 160 */       sprotToAstral.put(((String)seq.getAnnotation().getProperty("description")).trim().split("\\s+")[0], seq.getName());
/*     */     }
/* 162 */     for (Iterator it = SheetIO.read(new File(outpDir, "sprot2ncbi"), "\t"); it.hasNext();) {
/* 163 */       List row = (List)it.next();
/* 164 */       map.put(row.get(0), row.get(1));
/* 165 */       if (row.get(1).equals("null")) {
/* 166 */         Object spec = astralToSpec.get(sprotToAstral.get(row.get(0)));
/* 167 */         Collection coll = (Collection)nullSet.get(spec);
/* 168 */         if (coll == null) {
/* 169 */           coll = new ArrayList();
/* 170 */           nullSet.put(spec, coll);
/*     */         }
/* 172 */         coll.add(row.get(0));
/*     */       }
/*     */     }
/*     */     
/* 176 */     System.err.println(nullSet);
/* 177 */     for (Iterator it = SheetIO.read(new File(repos, "names.dmp"), "\\|"); it.hasNext();) {
/* 178 */       List row = (List)it.next();
/* 179 */       String spec = ((String)row.get(1)).trim();
/*     */       
/* 181 */       if (nullSet.containsKey(spec)) {
/* 182 */         String taxonomy = ((String)row.get(0)).trim();
/* 183 */         System.err.println(taxonomy);
/* 184 */         for (Iterator it1 = ((Collection)nullSet.get(spec)).iterator(); it1.hasNext();) {
/* 185 */           map.put(it1.next(), taxonomy);
/*     */         }
/*     */       }
/*     */     }
/* 189 */     PrintWriter pw1 = new PrintWriter(new BufferedWriter(new FileWriter(new File(outpDir, "sprot2ncbi2"))));
/* 190 */     for (Iterator it = map.keySet().iterator(); it.hasNext();) {
/* 191 */       Object key = it.next();
/* 192 */       pw1.println(key + "\t" + map.get(key));
/*     */     }
/* 194 */     pw1.close();
/*     */   }
/*     */   
/*     */   public static void extractFromFastaFile(String[] args) throws Exception
/*     */   {
/* 199 */     File outpDir = new File(".");
/* 200 */     File repos = new File(args[0]);
/* 201 */     PfamAlphabet alph = PfamAlphabet.makeAlphabet(repos);
/* 202 */     SequenceIterator fasta = SeqIOTools.readFastaProtein(
/* 203 */       new BufferedReader(new FileReader(
/* 204 */       new File(args[1]))));
/* 205 */     IndexedPfamDB db = new IndexedPfamDB(new File(repos, "pfamA_reg_full_ls"), alph, "\t", 0, 1);
/* 206 */     PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(new File(outpDir, "pfamA_astral_ls"))));
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 213 */     Set completed = new HashSet();
/* 214 */     while (fasta.hasNext()) {
/* 215 */       Sequence seq = fasta.nextSequence();
/*     */       
/* 217 */       String[] desc = ((String)seq.getAnnotation().getProperty("description")).split("\\s+");
/* 218 */       if (!completed.contains(desc[1])) {
/* 219 */         completed.add(desc[1]);
/*     */         
/*     */ 
/* 222 */         if (db.indices.containsKey(desc[1])) {
/* 223 */           Sequence dl = db.getSequence(desc[1]);
/* 224 */           for (Iterator it = dl.features(); it.hasNext();) {
/* 225 */             Domain d = (Domain)it.next();
/* 226 */             printDomainLine(pw, d, dl);
/*     */           }
/*     */         }
/*     */       } }
/* 230 */     pw.close();
/* 231 */     File out = new File(outpDir, "pfamA_astral_ls_training");
/* 232 */     PrintWriter pw2 = new PrintWriter(new BufferedWriter(new FileWriter(out)));
/* 233 */     db.indices.forEachKey(
/* 234 */       new TObjectProcedure() {
/*     */         public boolean execute(Object key) {
/*     */           try {
/* 237 */             Sequence dl = IndexedPfamDB.this.getSequence((String)key);
/*     */             
/* 239 */             if (this.val$completed.contains(dl.getName())) return true;
/* 240 */             for (Iterator it = dl.features(); it.hasNext();) {
/* 241 */               Domain d = (Domain)it.next();
/* 242 */               IndexedPfamDB.printDomainLine(this.val$pw2, d, dl);
/* 243 */               this.val$pw2.flush();
/*     */             }
/*     */           }
/*     */           catch (Exception exc) {
/* 247 */             exc.printStackTrace();
/*     */           }
/* 249 */           return true;
/*     */         }
/* 251 */       });
/* 252 */     pw2.close();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/* 257 */   public static final SequenceDB EMPTY_DB = new AbstractSequenceDB() {
/* 258 */     Set empty = new HashSet();
/*     */     
/* 260 */     public Set ids() { return this.empty; }
/*     */     
/*     */ 
/* 263 */     public String getName() { return "EMPTY_DB"; }
/*     */     
/*     */     public Sequence getSequence(String seq) {
/*     */       try {
/* 267 */         throw new Exception("null method");
/*     */       } catch (Exception exc) {
/* 269 */         exc.printStackTrace(); } return null;
/*     */     }
/*     */   };
/*     */   
/*     */ 
/*     */   TObjectLongHashMap indices;
/*     */   
/*     */ 
/*     */ 
/*     */   public Set ids()
/*     */   {
/* 280 */     throw new RuntimeException("should avoid using this");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void finalize()
/*     */   {
/*     */     try
/*     */     {
/* 289 */       this.raf.close();
/*     */     } catch (Exception exc) {
/* 291 */       exc.printStackTrace();
/*     */     }
/*     */   }
/*     */   
/*     */   public Sequence getSequence(long index) {
/*     */     try {
/* 297 */       this.raf.seek(index);
/*     */       
/* 299 */       String[] name = new String[2];
/* 300 */       DomainList seq = new DomainList(SymbolList.EMPTY_LIST, name[0], name[1], new SimpleAnnotation());
/* 301 */       read(this.raf, this.id_col, this.pfam_col, this.split, seq, this.alph);
/*     */       
/*     */ 
/*     */ 
/* 305 */       return seq;
/*     */     } catch (Throwable t) {
/* 307 */       t.printStackTrace();
/*     */     }
/*     */     
/*     */ 
/* 311 */     return new SimpleSequence(SymbolList.EMPTY_LIST, "id", "id", new SimpleAnnotation());
/*     */   }
/*     */   
/*     */   public boolean contains(Object arg)
/*     */   {
/* 316 */     return this.indices.containsKey(arg);
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
/*     */   public Sequence getSequence(String id)
/*     */     throws IllegalIDException
/*     */   {
/* 334 */     if (!this.indices.contains(id)) throw new IllegalIDException("doesn't contain " + id);
/* 335 */     return getSequence(this.indices.get(id));
/*     */   }
/*     */   
/*     */   public static void print(SequenceDB db, PrintWriter pw)
/*     */   {
/* 340 */     for (SequenceIterator seqIt = db.sequenceIterator(); seqIt.hasNext();) {
/*     */       try {
/* 342 */         Sequence seq = seqIt.nextSequence();
/*     */         
/* 344 */         for (Iterator it = seq.features(); it.hasNext(); 
/*     */             
/*     */ 
/* 347 */             pw.println())
/*     */         {
/* 345 */           Domain dom = (Domain)it.next();
/* 346 */           pw.print(seq.getName());
/* 347 */           dom.print(pw);
/*     */         }
/*     */       } catch (BioException exc) {
/* 350 */         exc.printStackTrace();
/*     */       }
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
/*     */   private static void read(RandomAccessFile raf, int id_col, int pfam_col, String split, DomainList seq, Alphabet alph)
/*     */     throws Exception
/*     */   {
/* 396 */     SymbolTokenization tokenizer = alph.getTokenization("token");
/* 397 */     String s = raf.readLine();
/* 398 */     seq.removeAllFeatures();
/*     */     
/*     */ 
/*     */ 
/* 402 */     String[] st = s.split(split);
/* 403 */     String id = st[id_col];
/* 404 */     seq.setName(id);
/* 405 */     String species = "";
/* 406 */     if (st.length > 6) {
/* 407 */       species = st[7];
/*     */       
/* 409 */       seq.getAnnotation().setProperty("species", species);
/* 410 */       seq.getAnnotation().setProperty("pfamseq_id", st[6]);
/*     */     }
/*     */     
/* 413 */     long pos = raf.getFilePointer();
/* 414 */     for (int i = 0; (id.equals(st[id_col])) && ((st.length <= 6) || (species.equals(st[7]))); i++) {
/*     */       try {
/* 416 */         Domain.Template dom = new Domain.Template();
/* 417 */         if (st.length > pfam_col) {
/* 418 */           dom.symbol = tokenizer.parseToken(st[pfam_col]);
/* 419 */           if (st.length > 1 + pfam_col)
/*     */           {
/* 421 */             dom.location = LocationTools.makeLocation(Integer.parseInt(st[(1 + pfam_col)]), Integer.parseInt(st[(2 + pfam_col)]));
/* 422 */             dom.score = Double.parseDouble(st[(3 + pfam_col)]);
/*     */           }
/*     */           else {
/* 425 */             dom.location = LocationTools.makeLocation(0, 0);
/*     */           }
/* 427 */           seq.createFeature(dom);
/*     */         }
/*     */       } catch (IllegalSymbolException exc) {
/* 430 */         exc.printStackTrace();
/*     */       }
/* 432 */       pos = raf.getFilePointer();
/* 433 */       s = raf.readLine();
/* 434 */       if (s == null) break;
/* 435 */       st = s.split(split);
/*     */     }
/*     */     
/* 438 */     raf.seek(pos);
/*     */   }
/*     */   
/*     */   public IndexedPfamDB(File repository) throws Exception
/*     */   {
/* 443 */     this(new File(repository + "/lc1/pfamA_reg_full"), 
/* 444 */       PfamAlphabet.makeAlphabet(repository), 
/* 445 */       "\\t", 0, 1);
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
/*     */   public IndexedPfamDB(File repository, DomainAlphabet alph)
/*     */     throws Exception
/*     */   {
/* 462 */     this(new File(repository, "pfamA_reg_full_ls"), 
/* 463 */       alph, 
/* 464 */       "\\t", 0, 1);
/*     */   }
/*     */   
/*     */   public static void print(SequenceIterator seqIt, PrintWriter pw)
/*     */     throws Exception
/*     */   {
/* 470 */     while (seqIt.hasNext()) {
/* 471 */       Sequence seq = seqIt.nextSequence();
/*     */       
/* 473 */       FeatureHolder fh = seq.filter(FeatureUtils.DOMAIN_FILTER);
/*     */       
/* 475 */       for (Iterator it = fh.features(); it.hasNext();) {
/* 476 */         Domain domain = (Domain)it.next();
/* 477 */         double sc = (float)domain.getScore() - ((Float)domain.getSymbol().getAnnotation().getProperty("ls_seq_thresh")).floatValue();
/* 478 */         double evalue = 0.0D;
/* 479 */         int sig = sc > 0.0D ? 1 : 0;
/* 480 */         pw.print(seq.getName() + "\t");
/* 481 */         pw.print(domain.getSymbol().getName() + "\t");
/* 482 */         pw.print("0\t0\t");
/* 483 */         pw.print(domain.getScore() + "\t");
/* 484 */         pw.print("ls\t");
/* 485 */         pw.print(sig + "\t");
/* 486 */         pw.print(domain.getEvalue() + "\t");
/* 487 */         pw.println(domain.getScore());
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public IndexedPfamDB(File pfam, DomainAlphabet alph, String split, int id_col, int pfam_col) throws Exception
/*     */   {
/* 494 */     this(pfam, alph, split, id_col, pfam_col, true);
/*     */   }
/*     */   
/*     */   public IndexedPfamDB(File pfam, DomainAlphabet alph, String split, int id_col, int pfam_col, boolean generateIndices) throws Exception
/*     */   {
/* 499 */     if (id_col == 0) this.print = false;
/* 500 */     this.pfam = pfam;
/*     */     
/* 502 */     this.raf = new RandomAccessFile(pfam, "r");
/* 503 */     this.id_col = id_col;
/* 504 */     this.pfam_col = pfam_col;
/* 505 */     this.split = split;
/* 506 */     this.alph = alph;
/* 507 */     this.name = alph.getName();
/* 508 */     File indicesF = new File(pfam.getAbsolutePath() + "_idx");
/*     */     
/* 510 */     if (generateIndices) {
/* 511 */       if ((indicesF == null) || (!indicesF.exists()) || (indicesF.length() == 0L)) {
/* 512 */         makeIndex(this.raf, 
/* 513 */           split, id_col);
/* 514 */         if (indicesF != null) writeMap(indicesF);
/*     */       }
/*     */       else {
/* 517 */         Iterator it = SheetIO.read(indicesF, "\\s+");
/* 518 */         if ((this.indices == null) || (this.indices.size() == 0)) {
/* 519 */           this.indices = 
/* 520 */             new TObjectLongHashMap();
/* 521 */           while (it.hasNext()) {
/* 522 */             List row = (List)it.next();
/*     */             
/* 524 */             this.indices.put(row.get(0), 
/* 525 */               Long.parseLong((String)row.get(1)));
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static SequenceIterator SequenceIterator(File pfam, DomainAlphabet alph, String split, int id_col, int pfam_col)
/*     */     throws Exception
/*     */   {
/* 537 */     RandomAccessFile br = new RandomAccessFile(pfam, "r");
/* 538 */     br.seek(0L);
/* 539 */     new SequenceIterator() {
/*     */       DomainList seq;
/*     */       
/*     */       public boolean hasNext() {
/* 543 */         try { return IndexedPfamDB.this.getFilePointer() < IndexedPfamDB.this.length();
/*     */         } catch (Exception exc) {
/* 545 */           exc.printStackTrace(); }
/* 546 */         return true;
/*     */       }
/*     */       
/*     */       public Sequence nextSequence()
/*     */       {
/*     */         try {
/* 552 */           String name = this.seq.getName();
/* 553 */           IndexedPfamDB.read(IndexedPfamDB.this, this.val$id_col, this.val$pfam_col, this.val$split, this.seq, this.val$alph);
/*     */           
/*     */ 
/* 556 */           return this.seq;
/*     */         } catch (Exception exc) {
/* 558 */           exc.printStackTrace(); }
/* 559 */         return null;
/*     */       }
/*     */     };
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private void makeIndex(RandomAccessFile db, String split, int id_col)
/*     */     throws Exception
/*     */   {
/* 569 */     long start_i = db.getFilePointer();
/*     */     
/* 571 */     this.indices = 
/* 572 */       new TObjectLongHashMap();
/* 573 */     int i = 0;
/* 574 */     String id = " ";
/* 575 */     String s; while ((s = db.readLine()) != null) {
/*     */       String s;
/* 577 */       String[] line = s.split(split);
/* 578 */       if (line.length > id_col) {
/* 579 */         if (!id.equals(line[id_col])) {
/* 580 */           this.indices.put(line[id_col], start_i);
/*     */           
/* 582 */           id = line[id_col];
/*     */         }
/* 584 */         start_i = db.getFilePointer();
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   private void writeMap(File indexFile) throws Exception {
/* 590 */     PrintStream op1 = 
/* 591 */       new PrintStream(new BufferedOutputStream(new FileOutputStream(indexFile)));
/* 592 */     this.indices.forEachKey(
/* 593 */       new TObjectProcedure() {
/*     */         public boolean execute(Object key) {
/* 595 */           long coords = IndexedPfamDB.this.indices.get(key);
/*     */           
/* 597 */           this.val$op1.println(key + " " + coords);
/* 598 */           return true;
/*     */ 
/*     */         }
/*     */         
/*     */ 
/*     */ 
/* 604 */       });
/* 605 */     op1.close();
/*     */   }
/*     */ }


/* Location:              /home/lachlan/Desktop/psilc1.21/psilc.jar!/lc1/pfam/IndexedPfamDB.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */