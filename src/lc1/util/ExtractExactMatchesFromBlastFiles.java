/*     */ package lc1.util;
/*     */ 
/*     */ import java.io.BufferedOutputStream;
/*     */ import java.io.BufferedReader;
/*     */ import java.io.File;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.FileReader;
/*     */ import java.io.OutputStream;
/*     */ import java.util.HashSet;
/*     */ import java.util.Set;
/*     */ import org.apache.commons.cli.CommandLine;
/*     */ import org.apache.commons.cli.OptionBuilder;
/*     */ import org.apache.commons.cli.Options;
/*     */ import org.apache.commons.cli.Parser;
/*     */ import org.apache.commons.cli.PosixParser;
/*     */ import org.biojava.bio.Annotation;
/*     */ import org.biojava.bio.seq.Sequence;
/*     */ import org.biojava.bio.seq.db.SequenceDB;
/*     */ import org.biojava.bio.seq.impl.SimpleSequence;
/*     */ import org.biojava.bio.seq.io.SeqIOTools;
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
/*     */ public class ExtractExactMatchesFromBlastFiles
/*     */ {
/*  47 */   static final Options OPTIONS = new Options() {};
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static void main(String[] args)
/*     */     throws Exception
/*     */   {
/*  55 */     Parser p = new PosixParser();
/*  56 */     CommandLine params = p.parse(OPTIONS, args);
/*  57 */     File repos = new File(params.getOptionValue("repository"));
/*  58 */     File pfamseqIdx = new File(repos, "uniprot-1_idx");
/*  59 */     if (!pfamseqIdx.exists()) {
/*  60 */       FlatFileTools.createIndex(pfamseqIdx, "fasta", "protein");
/*  61 */       FlatFileTools.addFilesToIndex(pfamseqIdx, new File[] { new File(repos, "uniprot-1") });
/*     */     }
/*  63 */     SequenceDB seqDB = FlatFileTools.open(pfamseqIdx);
/*  64 */     File astralIdx = new File("astral_idx");
/*  65 */     if (!astralIdx.exists()) {
/*  66 */       FlatFileTools.createIndex(astralIdx, "fasta", "protein");
/*  67 */       FlatFileTools.addFilesToIndex(astralIdx, new File[] { new File("astral") });
/*     */     }
/*  69 */     SequenceDB astralDB = FlatFileTools.open(astralIdx);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  79 */     File blastDir = new File("blast");
/*  80 */     OutputStream os = new BufferedOutputStream(new FileOutputStream(new File("sprot.fasta")));
/*     */     
/*  82 */     BufferedReader br = new BufferedReader(new FileReader(new File("blast_res")));
/*  83 */     String st = "";
/*  84 */     Set done = new HashSet();
/*  85 */     while ((st = br.readLine()) != null) {
/*  86 */       String[] str = st.split("\\s+");
/*  87 */       String id = str[0];
/*  88 */       if (!done.contains(id)) {
/*  89 */         double sc = Double.parseDouble(str[2]);
/*  90 */         if (sc > 97.0D) {
/*  91 */           done.add(id);
/*  92 */           Sequence seq = seqDB.getSequence(str[1]);
/*  93 */           Sequence scop_seq = astralDB.getSequence(id);
/*  94 */           String name = scop_seq.getName() + "\t" + seq.getName() + "\t" + str[8] + "\t" + str[9];
/*  95 */           Sequence seq2 = new SimpleSequence(scop_seq, name, name, Annotation.EMPTY_ANNOTATION);
/*  96 */           SeqIOTools.writeFasta(os, seq2);
/*     */         }
/*     */       }
/*     */     }
/* 100 */     os.close();
/*     */   }
/*     */ }


/* Location:              /home/lachlan/Desktop/psilc1.21/psilc.jar!/lc1/util/ExtractExactMatchesFromBlastFiles.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */