/*     */ package lc1.util;
/*     */ 
/*     */ import java.io.BufferedOutputStream;
/*     */ import java.io.BufferedReader;
/*     */ import java.io.BufferedWriter;
/*     */ import java.io.File;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.FileReader;
/*     */ import java.io.FileWriter;
/*     */ import java.io.PrintStream;
/*     */ import java.io.StringWriter;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collection;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import lc1.treefam.AttributeIdentifier;
/*     */ import org.biojava.bio.Annotation;
/*     */ import org.biojava.bio.seq.Sequence;
/*     */ import org.biojava.bio.seq.SequenceIterator;
/*     */ import org.biojava.bio.seq.io.SeqIOTools;
/*     */ import org.biojava.utils.ProcessTools;
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
/*     */ public class GetEMBL
/*     */ {
/*     */   public static void main(String[] args)
/*     */     throws Exception
/*     */   {
/*  43 */     File repos = new File("/nfs/team71/phd/lc1/Data/lc1");
/*  44 */     File nodes = new File(repos, "nodes.dmp");
/*  45 */     File names = new File(repos, "names.dmp");
/*  46 */     String root = "Primates";
/*     */     
/*  48 */     File tmpFile = new File("tempF");
/*  49 */     File res = new File("apobec.seed.dna");
/*     */     
/*  51 */     if (!tmpFile.exists()) {
/*  52 */       for (int i = 514; i <= 604; i++) {
/*  53 */         String[] command = { "getz", "-e", "[embl:AY622" + i + "]" };
/*     */         
/*  55 */         System.err.println(tmpFile.getAbsolutePath());
/*     */         
/*  57 */         System.err.println(Arrays.asList(command));
/*  58 */         BufferedWriter out = new BufferedWriter(new FileWriter(tmpFile, true));
/*  59 */         StringWriter err = new StringWriter();
/*  60 */         ProcessTools.exec(command, null, out, err);
/*  61 */         out.close();
/*  62 */         System.out.println(err.getBuffer().toString());
/*     */       }
/*     */     }
/*     */     
/*  66 */     BufferedReader br = new BufferedReader(new FileReader(tmpFile));
/*  67 */     SequenceIterator setIt = SeqIOTools.readEmblNucleotide(br);
/*  68 */     PrintStream os = new PrintStream(new BufferedOutputStream(new FileOutputStream(res)));
/*  69 */     Map m = new HashMap();
/*  70 */     while (setIt.hasNext()) {
/*  71 */       Sequence seq = setIt.nextSequence();
/*  72 */       String taxName = (String)seq.getAnnotation().getProperty("OS");
/*     */       
/*  74 */       String desc = (String)seq.getAnnotation().getProperty("DE");
/*  75 */       AttributeIdentifier taxId = null;
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  83 */       taxName = taxName.replace(' ', '_');
/*  84 */       seq.getAnnotation().setProperty("S", taxName);
/*  85 */       desc = desc.substring(desc.indexOf("Apobec") - 1);
/*  86 */       int exon = Integer.parseInt(
/*  87 */         desc.substring(desc.indexOf("exon"), desc.indexOf(46)).split("\\s+")[1]);
/*  88 */       System.err.println("exon is " + exon);
/*  89 */       desc = desc.substring(0, desc.indexOf(" gene")).trim();
/*  90 */       String desc1 = desc + "_" + (String)seq.getAnnotation().getProperty("OS");
/*  91 */       System.err.println(desc1);
/*  92 */       List l = (List)m.get(desc1);
/*  93 */       seq.getAnnotation().setProperty("name", desc1);
/*  94 */       if (l == null) l = new ArrayList();
/*  95 */       m.put(desc1, l);
/*  96 */       l.add(seq);
/*     */     }
/*  98 */     for (Iterator it = m.values().iterator(); it.hasNext();) {
/*  99 */       Collection l = (Collection)it.next();
/* 100 */       StringBuffer sb = new StringBuffer();
/* 101 */       Sequence seq = null;
/* 102 */       for (Iterator it1 = l.iterator(); it1.hasNext();) {
/* 103 */         seq = (Sequence)it1.next();
/* 104 */         sb.append(seq.seqString());
/*     */       }
/* 106 */       System.err.println(l.size() + " exons for " + seq.getName());
/* 107 */       os.print(">" + seq.getAnnotation().getProperty("name") + "_" + seq.getAnnotation().getProperty("S"));
/* 108 */       os.println(" TAXID=" + seq.getAnnotation().getProperty("S"));
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 116 */       os.println(seq.seqString());
/* 117 */       os.flush();
/*     */     }
/* 119 */     os.close();
/*     */   }
/*     */ }


/* Location:              /home/lachlan/Desktop/psilc1.21/psilc.jar!/lc1/util/GetEMBL.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */