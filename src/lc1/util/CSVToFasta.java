/*     */ package lc1.util;
/*     */ 
/*     */ import java.io.BufferedOutputStream;
/*     */ import java.io.File;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.FileWriter;
/*     */ import java.io.OutputStream;
/*     */ import java.io.PrintStream;
/*     */ import java.io.PrintWriter;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Set;
/*     */ import lc1.domainseq.Domain;
/*     */ import lc1.pfam.IndexedPfamDB;
/*     */ import lc1.pfam.PfamAlphabet;
/*     */ import org.apache.commons.cli.CommandLine;
/*     */ import org.apache.commons.cli.OptionBuilder;
/*     */ import org.apache.commons.cli.Options;
/*     */ import org.apache.commons.cli.Parser;
/*     */ import org.apache.commons.cli.PosixParser;
/*     */ import org.biojava.bio.Annotation;
/*     */ import org.biojava.bio.seq.DNATools;
/*     */ import org.biojava.bio.seq.RNATools;
/*     */ import org.biojava.bio.seq.Sequence;
/*     */ import org.biojava.bio.seq.SequenceIterator;
/*     */ import org.biojava.bio.seq.db.SequenceDB;
/*     */ import org.biojava.bio.seq.impl.SimpleSequence;
/*     */ import org.biojava.bio.seq.io.SeqIOTools;
/*     */ import org.biojava.bio.symbol.IllegalSymbolException;
/*     */ import org.biojava.bio.symbol.Symbol;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class CSVToFasta
/*     */ {
/*     */   public static void main(String[] args)
/*     */     throws Exception
/*     */   {
/*  44 */     File blast = new File("blast.fasta");
/*  45 */     OutputStream os1 = new BufferedOutputStream(new FileOutputStream(blast));
/*  46 */     Iterator it = SheetIO.read(new File(args[0]), ",");
/*  47 */     List header = (List)it.next();
/*  48 */     File fasta = new File("cluster_dna");
/*  49 */     while (it.hasNext()) {
/*  50 */       List row = (List)it.next();
/*  51 */       String name = (String)row.get(0);
/*  52 */       String id = name.split("SPA")[1];
/*  53 */       System.err.println(name);
/*  54 */       OutputStream os = new BufferedOutputStream(new FileOutputStream(new File(fasta, name)));
/*  55 */       SequenceIterator seqIt = new SequenceIterator() {
/*     */         int i;
/*     */         
/*  58 */         public boolean hasNext() { return this.i < CSVToFasta.this.size(); }
/*     */         
/*     */         public Sequence nextSequence() {
/*  61 */           String st = (String)CSVToFasta.this.get(this.i);
/*  62 */           String nam = this.val$header.get(this.i).toString().split("\\s+")[0] + this.val$id;
/*  63 */           this.i += 1;
/*  64 */           if (st.length() == 0) { return null;
/*     */           }
/*     */           
/*     */           try
/*     */           {
/*  69 */             return new SimpleSequence(DNATools.createDNA(st), 
/*  70 */               nam, nam, Annotation.EMPTY_ANNOTATION);
/*     */           }
/*     */           catch (IllegalSymbolException exc) {
/*  73 */             exc.printStackTrace(); }
/*  74 */           return null;
/*     */         }
/*     */       };
/*     */       
/*  78 */       while (seqIt.hasNext()) {
/*  79 */         Sequence seq = seqIt.nextSequence();
/*  80 */         if ((seq != null) && (seq.length() > 0)) {
/*  81 */           SeqIOTools.writeFasta(os, seq);
/*     */           try
/*     */           {
/*  84 */             if (seq.getName().startsWith("STY")) {
/*  85 */               SeqIOTools.writeFasta(os1, 
/*  86 */                 new SimpleSequence(RNATools.translate(RNATools.transcribe(seq)), seq.getName(), seq.getName(), Annotation.EMPTY_ANNOTATION));
/*     */             }
/*     */           } catch (Exception exc) {
/*  89 */             exc.printStackTrace();
/*     */           }
/*     */         }
/*     */       }
/*  93 */       os.close();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*  99 */   static final Options OPTIONS = new Options() {};
/*     */   
/*     */ 
/*     */ 
/*     */   public static void main1(String[] args)
/*     */     throws Exception
/*     */   {
/* 106 */     Parser PARSER = new PosixParser();
/* 107 */     CommandLine params = PARSER.parse(OPTIONS, args);
/* 108 */     File repos = new File(params.getOptionValue("repository"));
/* 109 */     PfamAlphabet alph = PfamAlphabet.makeAlphabet(repos);
/* 110 */     SequenceDB seqDB = new IndexedPfamDB(new File(params.getOptionValue("repository"), "pfamA_reg_full_ls"), 
/* 111 */       alph, "\t", 0, 1);
/* 112 */     PrintWriter pw = new PrintWriter(new FileWriter(new File("output")));
/* 113 */     for (Iterator it = SheetIO.read(new File(params.getOptionValue("input")), ","); it.hasNext();) {
/* 114 */       List row = (List)it.next();
/* 115 */       String id = (String)row.get(2);
/* 116 */       System.err.println(id);
/* 117 */       String b_id = (String)row.get(0);
/* 118 */       if (seqDB.ids().contains(id)) {
/* 119 */         Sequence seq = seqDB.getSequence(id);
/* 120 */         for (Iterator it1 = seq.features(); it1.hasNext();) {
/* 121 */           Domain dom = (Domain)it1.next();
/* 122 */           pw.print(b_id);pw.print("\t");
/* 123 */           pw.print(dom.getSymbol().getName());pw.print("\t");
/* 124 */           pw.println("0\t0\t0\tls\t1");
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 129 */     pw.close();
/*     */   }
/*     */ }


/* Location:              /home/lachlan/Desktop/psilc1.21/psilc.jar!/lc1/util/CSVToFasta.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */