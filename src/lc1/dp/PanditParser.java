/*     */ package lc1.dp;
/*     */ 
/*     */ import java.io.BufferedOutputStream;
/*     */ import java.io.BufferedReader;
/*     */ import java.io.File;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.FileReader;
/*     */ import java.io.OutputStream;
/*     */ import java.io.PrintWriter;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import org.biojava.bio.seq.GappedSequence;
/*     */ import org.biojava.bio.seq.ProteinTools;
/*     */ import org.biojava.bio.seq.impl.SimpleSequence;
/*     */ import org.biojava.bio.seq.io.SeqIOTools;
/*     */ import pal.alignment.Alignment;
/*     */ import pal.alignment.SimpleAlignment;
/*     */ import pal.datatype.DataType;
/*     */ import pal.datatype.Nucleotides;
/*     */ import pal.misc.Identifier;
/*     */ import pal.misc.SimpleIdGroup;
/*     */ 
/*     */ public class PanditParser
/*     */ {
/*  25 */   static File alignDir = null;
/*  26 */   static String wsp = " ";
/*     */   
/*  28 */   public static void main(String[] args) throws Exception { File input = new File(args[0]);
/*  29 */     alignDir = new File("align");
/*  30 */     if (!alignDir.exists()) {} alignDir.mkdir();
/*  31 */     OutputStream os = new BufferedOutputStream(new FileOutputStream(new File(args[1])));
/*  32 */     BufferedReader in = new BufferedReader(new FileReader(input));
/*  33 */     parse(in, os);
/*     */   }
/*     */   
/*     */   public static void parse(BufferedReader in, OutputStream os) throws Exception {
/*  37 */     char backslash = '\\';
/*  38 */     wsp = backslash + "w+";
/*  39 */     String s = "";
/*  40 */     boolean continueParse = true;
/*     */     
/*  42 */     while (continueParse) {
/*  43 */       continueParse = parseFamily(in, os);
/*     */     }
/*     */   }
/*     */   
/*     */   public static boolean parseFamily(BufferedReader in, OutputStream os) throws Exception
/*     */   {
/*  49 */     String s = in.readLine();
/*     */     
/*  51 */     if (s == null) { return false;
/*     */     }
/*     */     
/*  54 */     String[] family = s.split(" ");
/*  55 */     File outp = new File(alignDir, family[(family.length - 1)]);
/*  56 */     s = in.readLine();
/*  57 */     PrintWriter pw = null;
/*     */     
/*     */ 
/*  60 */     pw = new PrintWriter(new BufferedOutputStream(new FileOutputStream(outp)));
/*     */     
/*  62 */     while (!s.startsWith("NAM")) {
/*  63 */       s = in.readLine();
/*     */     }
/*     */     
/*  66 */     Alignment align = parseAlignment(in, s, os);
/*  67 */     if (pw != null) {
/*  68 */       pal.alignment.AlignmentUtils.printInterleaved(align, pw);
/*  69 */       pw.flush();
/*  70 */       pw.close();
/*     */     }
/*  72 */     return true;
/*     */   }
/*     */   
/*     */   public static Alignment parseAlignment(BufferedReader in, String s, OutputStream os) throws Exception {
/*  76 */     List ids = new ArrayList();
/*  77 */     List seqs = new ArrayList();
/*  78 */     boolean dna = false;
/*  79 */     while ((s != null) && (!s.startsWith("//")))
/*     */     {
/*  81 */       String[] str = s.split(" ");
/*  82 */       Identifier id = new Identifier(str[(str.length - 1)]);
/*  83 */       s = in.readLine();
/*  84 */       String[] st = s.split(" ");
/*  85 */       s = in.readLine();
/*  86 */       if ((s != null) && (!s.startsWith("//")) && (!s.startsWith("NAM")))
/*     */       {
/*     */ 
/*  89 */         if (s.startsWith("DSQ")) {
/*  90 */           dna = true;
/*  91 */           seqs.add(st[(st.length - 1)]);
/*  92 */           ids.add(id);
/*  93 */           st = s.split(" ");
/*  94 */           GappedSequence seq = ProteinTools.createGappedProteinSequence(st[(st.length - 1)], id.getName());
/*  95 */           org.biojava.bio.seq.Sequence seq1 = new SimpleSequence(seq.getSourceSymbolList(), 
/*  96 */             seq.getName(), seq.getName(), seq.getAnnotation());
/*  97 */           SeqIOTools.writeFasta(os, seq1);
/*  98 */           in.readLine();
/*     */         }
/* 100 */         s = in.readLine();
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 109 */     Identifier[] idArray = new Identifier[ids.size()];
/* 110 */     ids.toArray(idArray);
/* 111 */     String[] seqsArray = new String[seqs.size()];
/* 112 */     seqs.toArray(seqsArray);
/* 113 */     pal.misc.IdGroup idg = new SimpleIdGroup(idArray);
/*     */     DataType dt;
/* 115 */     DataType dt; if (dna) dt = Nucleotides.DEFAULT_INSTANCE; else
/* 116 */       dt = pal.datatype.AminoAcids.DEFAULT_INSTANCE;
/* 117 */     Alignment align = new SimpleAlignment(idg, seqsArray, "-", dt);
/*     */     
/* 119 */     return align;
/*     */   }
/*     */ }


/* Location:              /home/lachlan/Desktop/psilc1.21/psilc.jar!/lc1/dp/PanditParser.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */