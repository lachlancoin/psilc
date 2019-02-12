/*     */ package lc1.util;
/*     */ 
/*     */ import java.io.BufferedOutputStream;
/*     */ import java.io.BufferedReader;
/*     */ import java.io.File;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.FileReader;
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStream;
/*     */ import java.io.PrintStream;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Set;
/*     */ import java.util.TreeSet;
/*     */ import org.biojava.bio.Annotation;
/*     */ import org.biojava.bio.BioException;
/*     */ import org.biojava.bio.seq.DNATools;
/*     */ import org.biojava.bio.seq.ProteinTools;
/*     */ import org.biojava.bio.seq.Sequence;
/*     */ import org.biojava.bio.seq.SequenceIterator;
/*     */ import org.biojava.bio.seq.db.IndexStore;
/*     */ import org.biojava.bio.seq.db.IndexedSequenceDB;
/*     */ import org.biojava.bio.seq.db.SequenceDB;
/*     */ import org.biojava.bio.seq.db.TabIndexStore;
/*     */ import org.biojava.bio.seq.io.EmblLikeFormat;
/*     */ import org.biojava.bio.seq.io.EmblProcessor.Factory;
/*     */ import org.biojava.bio.seq.io.FastaDescriptionLineParser.Factory;
/*     */ import org.biojava.bio.seq.io.FastaFormat;
/*     */ import org.biojava.bio.seq.io.SeqIOTools;
/*     */ import org.biojava.bio.seq.io.SequenceBuilderFactory;
/*     */ import org.biojava.bio.seq.io.SequenceFormat;
/*     */ import org.biojava.bio.seq.io.SimpleSequenceBuilder;
/*     */ import org.biojava.bio.seq.io.SwissprotProcessor.Factory;
/*     */ import org.biojava.bio.seq.io.SymbolTokenization;
/*     */ import org.biojava.bio.symbol.Alphabet;
/*     */ 
/*     */ 
/*     */ 
/*     */ public class FlatFileTools
/*     */ {
/*     */   public static void main1(String[] args)
/*     */     throws Exception
/*     */   {
/*  46 */     File pfamseq = new File(args[0]);
/*     */     
/*  48 */     File indexFile = new File(args[0] + "_idx");
/*  49 */     if (!indexFile.exists()) {
/*  50 */       createIndex(indexFile, "fasta", "protein");
/*  51 */       addFilesToIndex(indexFile, new File[] { pfamseq });
/*     */     }
/*     */     
/*  54 */     SequenceDB db = open(indexFile);
/*  55 */     Sequence seq = db.getSequence(args[1]);
/*  56 */     System.err.println(seq);
/*  57 */     File out = new File(args[1]);
/*  58 */     if ((out.exists()) && (out.length() > 0L)) { throw new Exception("output already exists");
/*     */     }
/*  60 */     OutputStream os = new BufferedOutputStream(new FileOutputStream(out));
/*  61 */     SeqIOTools.writeFasta(os, seq);
/*  62 */     os.close();
/*     */   }
/*     */   
/*     */   public static void main2(String[] args) throws Exception
/*     */   {
/*  67 */     SequenceIterator db = SeqIOTools.readFastaProtein(new BufferedReader(new FileReader(args[0])));
/*  68 */     OutputStream os = new BufferedOutputStream(new FileOutputStream(args[0] + "_mod"));
/*     */     
/*     */ 
/*  71 */     while (db.hasNext()) {
/*  72 */       Sequence seq = db.nextSequence();
/*  73 */       System.err.println(seq.getAnnotation());
/*  74 */       if (((String)seq.getAnnotation().getProperty("description")).indexOf("FXP2") >= 0) {
/*  75 */         SeqIOTools.writeFasta(os, seq);
/*     */       }
/*     */     }
/*  78 */     os.close();
/*     */   }
/*     */   
/*     */   public static void main(String[] args) throws Exception {
/*  82 */     SequenceIterator dna = SeqIOTools.readFastaDNA(new BufferedReader(new FileReader(args[0])));
/*  83 */     SequenceIterator prot = Clustal.getProteinSequencesFromDNA(dna);
/*  84 */     OutputStream os = new BufferedOutputStream(new FileOutputStream(new File(args[1])));
/*  85 */     while (prot.hasNext()) {
/*     */       try {
/*  87 */         SeqIOTools.writeFasta(os, prot.nextSequence());
/*     */       } catch (Exception exc) {
/*  89 */         exc.printStackTrace();
/*     */       }
/*     */     }
/*  92 */     os.close();
/*     */   }
/*     */   
/*     */   public static void createFileIndex(String directory, String indexName) throws Exception
/*     */   {
/*  97 */     File[] files = new File(directory).listFiles();
/*  98 */     PrintStream ps = new PrintStream(new BufferedOutputStream(new FileOutputStream(indexName)));
/*  99 */     for (int i = 0; i < files.length; i++) {
/* 100 */       String fileName = files[i].getName();
/* 101 */       SequenceIterator seqIt = SeqIOTools.readFastaProtein(new BufferedReader(new FileReader(files[i])));
/* 102 */       while (seqIt.hasNext()) {
/* 103 */         ps.println(fileName + "\t" + seqIt.nextSequence().getName());
/*     */       }
/* 105 */       ps.flush();
/*     */     }
/* 107 */     ps.close();
/*     */   }
/*     */   
/*     */   public static void createIndex(File indexFile, String formatName, String alphaName)
/*     */   {
/*     */     try {
/* 113 */       File indexList = new File(indexFile.getAbsolutePath() + ".list");
/* 114 */       Alphabet alpha = resolveAlphabet(alphaName);
/* 115 */       SymbolTokenization sParser = alpha.getTokenization("token");
/*     */       
/* 117 */       SequenceFormat sFormat = null;
/* 118 */       SequenceBuilderFactory sFact = null;
/* 119 */       if (formatName.equals("fasta")) {
/* 120 */         sFormat = new FastaFormat();
/* 121 */         sFact = new FastaDescriptionLineParser.Factory(SimpleSequenceBuilder.FACTORY);
/* 122 */       } else if (formatName.equals("embl")) {
/* 123 */         sFormat = new EmblLikeFormat();
/* 124 */         sFact = new EmblProcessor.Factory(SimpleSequenceBuilder.FACTORY);
/* 125 */       } else if (formatName.equals("swissprot")) {
/* 126 */         sFormat = new EmblLikeFormat();
/* 127 */         sFact = new SwissprotProcessor.Factory(SimpleSequenceBuilder.FACTORY);
/*     */       } else {
/* 129 */         throw new Exception("Format must be one of {embl, fasta, swissprot}");
/*     */       }
/*     */       
/* 132 */       tis = new TabIndexStore(
/* 133 */         indexFile, 
/* 134 */         indexList, 
/* 135 */         indexFile.getName(), 
/* 136 */         sFormat, 
/* 137 */         sFact, 
/* 138 */         sParser);
/*     */     } catch (Throwable t) {
/*     */       TabIndexStore tis;
/* 141 */       t.printStackTrace();
/* 142 */       System.exit(1);
/*     */     }
/*     */   }
/*     */   
/*     */   private static Alphabet resolveAlphabet(String alphaName) throws IllegalArgumentException
/*     */   {
/* 148 */     alphaName = alphaName.toLowerCase();
/* 149 */     if (alphaName.equals("dna"))
/* 150 */       return DNATools.getDNA();
/* 151 */     if (alphaName.equals("protein"))
/* 152 */       return ProteinTools.getAlphabet();
/* 153 */     if (alphaName.equals("proteint")) {
/* 154 */       return ProteinTools.getTAlphabet();
/*     */     }
/* 156 */     throw new IllegalArgumentException("Could not find alphabet for " + alphaName);
/*     */   }
/*     */   
/*     */   public static void addFilesToIndex(File indexFile, File[] filesToAddToIndex)
/*     */   {
/*     */     try {
/* 162 */       TabIndexStore tis = TabIndexStore.open(indexFile);
/*     */       
/* 164 */       IndexedSequenceDB seqDB = new IndexedSequenceDB(tis);
/*     */       
/* 166 */       for (int i = 0; i < filesToAddToIndex.length; i++) {
/* 167 */         System.out.print("Adding: " + filesToAddToIndex[i] + "...");
/* 168 */         seqDB.addFile(filesToAddToIndex[i]);
/* 169 */         System.out.println(" Done");
/*     */       }
/*     */     } catch (Throwable t) {
/* 172 */       t.printStackTrace();
/* 173 */       System.err.println(Print.toString(filesToAddToIndex));
/* 174 */       System.exit(1);
/*     */     }
/*     */   }
/*     */   
/*     */   public static SequenceIterator fetchSequence(SequenceDB seqDB, Iterator ids)
/*     */   {
/*     */     try {
/* 181 */       Set idSet = seqDB.ids();
/* 182 */       new SequenceIterator()
/*     */       {
/*     */         int count;
/*     */         
/*     */         boolean hasNext;
/*     */         String next;
/*     */         
/*     */         public boolean hasNext()
/*     */         {
/* 191 */           return this.hasNext;
/*     */         }
/*     */         
/*     */         public Sequence nextSequence() throws BioException {
/* 195 */           if (this.hasNext) {
/* 196 */             String current = new String(this.next);
/* 197 */             prepareNext();
/* 198 */             return FlatFileTools.this.getSequence(current);
/*     */           }
/*     */           
/* 201 */           return null;
/*     */         }
/*     */         
/*     */         void prepareNext() throws BioException {
/* 205 */           this.next = ((String)this.val$ids.next());
/* 206 */           while ((!this.val$idSet.contains(this.next)) && (this.next != null)) {
/* 207 */             this.next = ((String)this.val$ids.next());
/*     */           }
/* 209 */           if (this.next == null) {
/* 210 */             this.hasNext = false;
/*     */           }
/*     */           else {
/* 213 */             this.hasNext = this.val$ids.hasNext();
/*     */           }
/*     */         }
/*     */       };
/*     */     }
/*     */     catch (Throwable t) {
/* 219 */       t.printStackTrace(); }
/* 220 */     return null;
/*     */   }
/*     */   
/*     */   public static IndexedSequenceDB open(File indexFile) throws IOException
/*     */   {
/* 225 */     IndexStore tis = TabIndexStore.open(indexFile);
/* 226 */     return new IndexedSequenceDB(tis);
/*     */   }
/*     */   
/*     */   public static void main19(String[] args) throws Exception {
/* 230 */     openWebView(new File(args[0]), true);
/*     */   }
/*     */   
/*     */   public static void openWebView(File ffFile, boolean fasta) throws Exception
/*     */   {
/* 235 */     String s = "http://intweb.sanger.ac.uk/cgi-bin/Pfam/many_swisspfamget.pl?all=";
/* 236 */     if (fasta) {
/* 237 */       SequenceIterator seqIt = SeqIOTools.readFastaProtein(new BufferedReader(new FileReader(ffFile)));
/*     */       
/* 239 */       s = s + seqIt.nextSequence().getName();
/*     */       
/* 241 */       while (seqIt.hasNext()) {
/* 242 */         s = s + "~" + seqIt.nextSequence().getName();
/*     */       }
/*     */     }
/*     */     else
/*     */     {
/* 247 */       List l = new ArrayList();
/* 248 */       SheetIO.toCollection(SheetIO.getColumn(SheetIO.read(ffFile, "\\s+"), 0), l);
/* 249 */       for (Iterator it = l.iterator(); it.hasNext();) {
/* 250 */         s = s + "~" + it.next();
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public static void ListSeqsInIndex(File indexFile)
/*     */   {
/*     */     try
/*     */     {
/* 259 */       TabIndexStore tis = TabIndexStore.open(indexFile);
/* 260 */       IndexedSequenceDB seqDB = new IndexedSequenceDB(tis);
/* 261 */       for (Iterator i = seqDB.ids().iterator(); i.hasNext();) {
/* 262 */         System.out.println(i.next());
/*     */       }
/*     */     } catch (Throwable t) {
/* 265 */       t.printStackTrace();
/* 266 */       System.exit(1);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private static Iterator getIdIterator(Set protIds, File clusters)
/*     */     throws Exception
/*     */   {
/* 275 */     BufferedReader in = new BufferedReader(new FileReader(clusters));
/* 276 */     Set clustIds = new TreeSet();
/*     */     String s;
/* 278 */     while (((s = in.readLine()) != null) && (protIds.size() > 0)) { String s;
/* 279 */       List row = Arrays.asList(s.split("\\s+"));
/* 280 */       String name = (String)row.get(1);
/* 281 */       if (protIds.contains(name)) {
/* 282 */         Integer clust = new Integer(Integer.parseInt((String)row.get(0)));
/* 283 */         clustIds.add(clust);
/* 284 */         protIds.remove(name);
/*     */       }
/*     */     }
/*     */     
/* 288 */     return clustIds.iterator();
/*     */   }
/*     */ }


/* Location:              /home/lachlan/Desktop/psilc1.21/psilc.jar!/lc1/util/FlatFileTools.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */