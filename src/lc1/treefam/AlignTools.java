/*     */ package lc1.treefam;
/*     */ 
/*     */ import java.io.BufferedInputStream;
/*     */ import java.io.BufferedOutputStream;
/*     */ import java.io.BufferedReader;
/*     */ import java.io.BufferedWriter;
/*     */ import java.io.File;
/*     */ import java.io.FileFilter;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.FileReader;
/*     */ import java.io.FileWriter;
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStream;
/*     */ import java.io.PrintStream;
/*     */ import java.io.PrintWriter;
/*     */ import java.io.StringWriter;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Set;
/*     */ import lc1.util.Print;
/*     */ import org.biojava.bio.BioException;
/*     */ import org.biojava.bio.seq.DNATools;
/*     */ import org.biojava.bio.seq.ProteinTools;
/*     */ import org.biojava.bio.seq.RNATools;
/*     */ import org.biojava.bio.seq.Sequence;
/*     */ import org.biojava.bio.seq.SequenceIterator;
/*     */ import org.biojava.bio.seq.db.SequenceDB;
/*     */ import org.biojava.bio.seq.impl.SimpleSequence;
/*     */ import org.biojava.bio.seq.io.SeqIOTools;
/*     */ import org.biojava.bio.seq.io.SymbolTokenization;
/*     */ import org.biojava.bio.symbol.FiniteAlphabet;
/*     */ import org.biojava.bio.symbol.IllegalAlphabetException;
/*     */ import org.biojava.bio.symbol.SymbolList;
/*     */ import org.biojava.utils.ProcessTools;
/*     */ import pal.alignment.Alignment;
/*     */ import pal.alignment.AlignmentUtils;
/*     */ import pal.alignment.ReadAlignment;
/*     */ import pal.alignment.SimpleAlignment;
/*     */ import pal.alignment.SitePattern;
/*     */ import pal.alignment.StrippedAlignment;
/*     */ import pal.datatype.AminoAcids;
/*     */ import pal.datatype.CodonTable;
/*     */ import pal.datatype.CodonTableFactory;
/*     */ import pal.datatype.DataType;
/*     */ import pal.datatype.Nucleotides;
/*     */ import pal.distance.AlignmentDistanceMatrix;
/*     */ import pal.distance.DistanceMatrix;
/*     */ import pal.misc.IdGroup;
/*     */ import pal.misc.Identifier;
/*     */ import pal.misc.SimpleIdGroup;
/*     */ import pal.substmodel.SubstitutionModel.Utils;
/*     */ import pal.substmodel.WAG;
/*     */ import pal.tree.Tree;
/*     */ import pal.tree.TreeDistanceMatrix;
/*     */ import pal.tree.TreeUtils;
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
/*     */ public class AlignTools
/*     */ {
/*  75 */   static CodonTable ct = ;
/*     */   static final boolean deleteOnExit = true;
/*     */   static File dir;
/*  78 */   public static String[] bin = new String[0];
/*     */   
/*     */   public static void printMFA(Alignment align, PrintWriter pw) throws Exception {
/*  81 */     for (int i = 0; i < align.getIdCount(); i++) {
/*  82 */       Identifier id = align.getIdentifier(i);
/*  83 */       pw.print(">");pw.print(id.getName());pw.print(" ");
/*  84 */       if ((id instanceof AttributeIdentifier)) {
/*  85 */         for (Iterator it = ((AttributeIdentifier)id).getProperties().iterator(); it.hasNext(); 
/*     */             
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  91 */             pw.print(" "))
/*     */         {
/*  86 */           Object key = it.next();
/*  87 */           Object val = ((AttributeIdentifier)id).getAttribute((String)key);
/*  88 */           if (key.equals("S")) {
/*  89 */             pw.print("TAXID");pw.print("=");pw.print(val);pw.print(" ");
/*     */           }
/*  91 */           pw.print(key);pw.print("=");pw.print(val);
/*     */         }
/*     */       }
/*  94 */       pw.println();
/*  95 */       pw.println(align.getAlignedSequenceString(i));
/*     */     }
/*     */   }
/*     */   
/*     */   public static Alignment readMFA(File f, DataType dt) throws Exception {
/* 100 */     BufferedReader br = new BufferedReader(new FileReader(f));
/* 101 */     br.mark(2);
/* 102 */     if (br.read() != 62) return new ReadAlignment(f.getAbsolutePath());
/* 103 */     br.reset();
/* 104 */     String st = "";
/* 105 */     List ids = new ArrayList();
/* 106 */     List string = new ArrayList();
/* 107 */     StringBuffer sb = null;
/* 108 */     while ((st = br.readLine()) != null) {
/* 109 */       if (st.startsWith(">")) {
/* 110 */         if (sb != null)
/*     */         {
/* 112 */           string.add(sb.toString());
/*     */         }
/* 114 */         sb = new StringBuffer();
/* 115 */         String[] str = st.split("\\s+");
/* 116 */         AttributeIdentifier id = new AttributeIdentifier(str[0].substring(1));
/* 117 */         for (int i = 0; i < str.length; i++) {
/* 118 */           if (str[i].startsWith("TAXID"))
/* 119 */             id.setAttribute("S", str[i].split("=")[1]);
/*     */         }
/* 121 */         ids.add(id);
/*     */       }
/*     */       else
/*     */       {
/* 125 */         sb.append(st);
/*     */       }
/*     */     }
/* 128 */     string.add(sb.toString());
/* 129 */     if (string.size() != ids.size()) throw new Exception("should be equal");
/* 130 */     Alignment align = new SimpleAlignment((Identifier[])ids.toArray(new Identifier[ids.size()]), 
/* 131 */       (String[])string.toArray(new String[ids.size()]), "-", dt);
/* 132 */     return align;
/*     */   }
/*     */   
/*     */   private static void probcons(File fastaProt, File alignmentOut)
/*     */     throws Exception
/*     */   {
/* 138 */     String com = "";
/* 139 */     for (int i = 0; i < bin.length; i++) {
/* 140 */       if (new File(bin[i] + "probcons").exists()) {
/* 141 */         com = bin[i];
/* 142 */         break;
/*     */       }
/*     */     }
/* 145 */     String[] command = 
/* 146 */       { com + "probcons", "-clustalw", fastaProt.getAbsolutePath() };
/* 147 */     new File(fastaProt.getAbsolutePath() + ".dnd").deleteOnExit();
/* 148 */     StringWriter stw = new StringWriter();
/* 149 */     StringWriter stw1 = new StringWriter();
/* 150 */     ProcessTools.exec(command, null, stw, stw1);
/* 151 */     System.out.println(stw1.getBuffer().toString());
/* 152 */     PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(alignmentOut)));
/* 153 */     pw.print(stw.getBuffer().toString());
/* 154 */     pw.close();
/*     */   }
/*     */   
/*     */ 
/*     */   public static void muscle(File fastaProt, File alignmentOut)
/*     */     throws Exception
/*     */   {
/* 161 */     String com = "";
/* 162 */     for (int i = 0; i < bin.length; i++) {
/* 163 */       if (new File(bin[i] + "muscle").exists()) {
/* 164 */         com = bin[i];
/* 165 */         break;
/*     */       }
/*     */     }
/* 168 */     String[] command = 
/* 169 */       { com + "muscle", "-in", fastaProt.getAbsolutePath() };
/* 170 */     new File(fastaProt.getAbsolutePath() + ".dnd").deleteOnExit();
/* 171 */     StringWriter stw = new StringWriter();
/* 172 */     StringWriter stw1 = new StringWriter();
/* 173 */     ProcessTools.exec(command, null, stw, stw1);
/* 174 */     System.out.println(stw1.getBuffer().toString());
/* 175 */     PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(alignmentOut)));
/* 176 */     pw.print(stw.getBuffer().toString());
/* 177 */     pw.close();
/*     */   }
/*     */   
/*     */   public static Alignment inferDNAAlignmentFromProteinAlignment(File fastaDna, File protAl) throws Exception
/*     */   {
/* 182 */     SequenceDB seqDB = 
/* 183 */       SeqIOTools.readFasta(new BufferedInputStream(new FileInputStream(fastaDna)), DNATools.getDNA());
/* 184 */     return inferDNAAlignmentFromProteinAlignment(seqDB, readMFA(protAl, AminoAcids.DEFAULT_INSTANCE));
/*     */   }
/*     */   
/*     */ 
/*     */   public static void makeDNAAlignment(File fastaDNA, File fastaAlignment, String pseudo_id1)
/*     */   {
/* 190 */     String com = "";
/* 191 */     for (int i = 0; i < bin.length; i++) {
/* 192 */       if (new File(bin[i] + "clustalw").exists()) {
/* 193 */         com = bin[i];
/* 194 */         break;
/*     */       }
/*     */     }
/*     */     try {
/* 198 */       String seq1 = fastaDNA.getName();
/* 199 */       if (seq1.indexOf('.') > 0) seq1 = seq1.substring(0, seq1.indexOf('.'));
/* 200 */       File target = fastaDNA;
/* 201 */       ProcessTools.exec(new String[] { com + "clustalw", target.getAbsolutePath() }, null, null, null);
/* 202 */       String nm = target.getName();
/* 203 */       if (nm.indexOf('.') > 0) nm = nm.substring(0, nm.lastIndexOf('.'));
/* 204 */       File aln = new File(target.getParentFile().getAbsolutePath() + "/" + nm + ".aln");
/* 205 */       File dnd = new File(target.getParentFile().getAbsolutePath() + "/" + nm + ".dnd");
/*     */       
/* 207 */       aln.deleteOnExit();
/* 208 */       dnd.deleteOnExit();
/*     */       
/* 210 */       Tree tree = new lc1.phyl.NeighborJoiningTree(new AlignmentDistanceMatrix(SitePattern.getSitePattern(new ReadAlignment(aln.getAbsolutePath()))));
/* 211 */       String pseudo_id = "";
/* 212 */       for (int i = 0; i < tree.getIdCount(); i++) {
/* 213 */         if (tree.getIdentifier(i).getName().startsWith(pseudo_id1)) {
/* 214 */           pseudo_id = tree.getIdentifier(i).getName();
/*     */         }
/*     */       }
/* 217 */       String brother = getMinDist(tree, pseudo_id);
/* 218 */       SequenceDB seqDB = 
/* 219 */         SeqIOTools.readFasta(new BufferedInputStream(new FileInputStream(fastaDNA)), DNATools.getDNA());
/* 220 */       File toAlignAsProt = new File(fastaDNA.getParentFile(), seq1 + "_prot");
/* 221 */       File toAlignAsDNA = new File(fastaDNA.getParentFile(), seq1 + "_dna");
/* 222 */       OutputStream osProt = new BufferedOutputStream(new FileOutputStream(toAlignAsProt));
/* 223 */       OutputStream osDNA = new BufferedOutputStream(new FileOutputStream(toAlignAsDNA));
/* 224 */       toAlignAsProt.deleteOnExit();
/* 225 */       toAlignAsDNA.deleteOnExit();
/* 226 */       SequenceIterator seqIt_prot = getProteinSequencesFromDNA(seqDB.sequenceIterator(), pseudo_id);
/* 227 */       int prot_count = 0;
/* 228 */       for (SequenceIterator seqIt = seqDB.sequenceIterator(); seqIt.hasNext();) {
/* 229 */         Sequence seq_dna = seqIt.nextSequence();
/* 230 */         Sequence seq_prot = seqIt_prot.nextSequence();
/* 231 */         if (!seq_prot.getName().equals(seq_dna.getName())) throw new Exception("name mistmatch");
/* 232 */         if (seq_dna.getName().equals(pseudo_id)) {
/* 233 */           SeqIOTools.writeFasta(osDNA, seq_dna);
/*     */         }
/*     */         else {
/* 236 */           if (seq_dna.getName().equals(brother)) {
/* 237 */             SeqIOTools.writeFasta(osDNA, seq_dna);
/*     */           }
/* 239 */           SeqIOTools.writeFasta(osProt, seq_prot);
/* 240 */           prot_count++;
/*     */         }
/*     */       }
/*     */       
/* 244 */       osProt.close();
/* 245 */       osDNA.close();
/* 246 */       StringWriter stw = new StringWriter();
/*     */       
/* 248 */       Alignment align_prot = null;
/* 249 */       if (prot_count == 1) {
/* 250 */         BufferedReader br = new BufferedReader(new FileReader(toAlignAsProt));
/* 251 */         Sequence seq = SeqIOTools.readFastaProtein(br).nextSequence();
/* 252 */         br.close();
/* 253 */         String[] str = { seq.seqString() };
/* 254 */         Identifier[] ident = { new Identifier(seq.getName()) };
/* 255 */         Alignment al = new SimpleAlignment(ident, str, AminoAcids.DEFAULT_INSTANCE);
/* 256 */         File pr = new File(toAlignAsProt.getAbsolutePath() + ".aln");
/* 257 */         pr.deleteOnExit();
/* 258 */         PrintWriter op = new PrintWriter(new FileWriter(pr));
/* 259 */         AlignmentUtils.printCLUSTALW(al, op);
/* 260 */         op.close();
/*     */       }
/*     */       else {
/* 263 */         ProcessTools.exec(new String[] { com + "clustalw", toAlignAsProt.getAbsolutePath() }, null, stw, stw);
/*     */       }
/*     */       
/* 266 */       new File(toAlignAsProt.getAbsolutePath() + ".dnd").deleteOnExit();
/* 267 */       ProcessTools.exec(new String[] { com + "clustalw", toAlignAsDNA.getAbsolutePath() }, null, stw, stw);
/*     */       
/* 269 */       new File(toAlignAsDNA.getAbsolutePath() + ".dnd").deleteOnExit();
/* 270 */       System.out.println(stw.getBuffer().toString());
/* 271 */       File aln_prot = new File(toAlignAsProt.getAbsolutePath() + ".aln");
/* 272 */       aln_prot.deleteOnExit();
/* 273 */       File aln_dna = new File(toAlignAsDNA.getAbsolutePath() + ".aln");
/* 274 */       aln_dna.deleteOnExit();
/*     */       
/*     */ 
/* 277 */       if (align_prot == null) align_prot = inferDNAAlignmentFromProteinAlignment(seqDB, new ReadAlignment(aln_prot.getAbsolutePath()));
/* 278 */       Alignment align_dna = new ReadAlignment(aln_dna.getAbsolutePath());
/*     */       
/* 280 */       StrippedAlignment align_dna1 = new StrippedAlignment(align_dna);
/* 281 */       int br_id = align_dna1.whichIdNumber(brother);
/* 282 */       for (int i = 0; i < align_dna.getSiteCount(); i++) {
/* 283 */         if (align_dna.getData(br_id, i) == '-') {
/* 284 */           align_dna1.dropSite(i);
/*     */         }
/*     */       }
/* 287 */       align_dna = align_dna1;
/*     */       
/* 289 */       Alignment align = resolveAlignment(align_prot, align_dna, pseudo_id, brother);
/*     */       
/* 291 */       PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(fastaAlignment)));
/* 292 */       printMFA(align, pw);
/* 293 */       pw.close();
/*     */     } catch (Exception exc) {
/* 295 */       exc.printStackTrace();
/*     */     }
/*     */   }
/*     */   
/*     */   public static void writeTranslatedFasta(File fastaDNA, File target) throws Exception
/*     */   {
/* 301 */     SequenceDB seqDB = 
/* 302 */       SeqIOTools.readFasta(new BufferedInputStream(new FileInputStream(fastaDNA)), DNATools.getDNA());
/* 303 */     OutputStream os = new BufferedOutputStream(new FileOutputStream(target));
/* 304 */     for (SequenceIterator seqDB_out = getProteinSequencesFromDNA(seqDB.sequenceIterator(), null); 
/* 305 */           seqDB_out.hasNext();) {
/*     */       try {
/* 307 */         Sequence seq = seqDB_out.nextSequence();
/* 308 */         SeqIOTools.writeFasta(os, seq);
/*     */       }
/*     */       catch (Exception exc) {
/* 311 */         System.err.println("didnt translate ");
/*     */       }
/*     */     }
/*     */     
/* 315 */     os.close();
/*     */   }
/*     */   
/*     */   private static String getMinDist(Tree tree, String seq1) {
/* 319 */     DistanceMatrix dm = new TreeDistanceMatrix(tree);
/* 320 */     int i = dm.whichIdNumber(seq1);
/* 321 */     int k = -1;
/* 322 */     for (int j = 0; j < dm.getIdCount(); j++) {
/* 323 */       if ((j != i) && (
/* 324 */         (k == -1) || (dm.getDistance(i, j) < dm.getDistance(i, k)))) {
/* 325 */         k = j;
/*     */       }
/*     */     }
/* 328 */     return dm.getIdentifier(k).getName();
/*     */   }
/*     */   
/*     */   public static SequenceIterator getProteinSequencesFromDNA(SequenceIterator seqDB_in, String pseudo_id)
/*     */     throws Exception
/*     */   {
/* 334 */     new SequenceIterator() {
/*     */       public boolean hasNext() {
/* 336 */         return AlignTools.this.hasNext();
/*     */       }
/*     */       
/*     */       public Sequence nextSequence() throws BioException {
/* 340 */         Sequence dna = AlignTools.this.nextSequence();
/*     */         
/* 342 */         if ((this.val$pseudo_id != null) && (dna.getName().equals(this.val$pseudo_id))) return dna;
/*     */         try { SymbolList amino;
/* 344 */           SymbolList amino; if (dna.getAlphabet().equals(DNATools.getDNA())) {
/* 345 */             amino = 
/* 346 */               RNATools.translate(RNATools.transcribe(dna));
/*     */           }
/*     */           else {
/* 349 */             amino = dna;
/*     */           }
/* 351 */           SymbolTokenization tok = ProteinTools.getTAlphabet().getTokenization("token");
/* 352 */           if (tok.tokenizeSymbol(amino.symbolAt(amino.length())).charAt(0) == '*') {
/* 353 */             amino = amino.subList(1, amino.length() - 1);
/*     */           }
/*     */           
/*     */ 
/* 357 */           return new SimpleSequence(amino, dna.getName(), dna.getName(), dna.getAnnotation());
/*     */         }
/*     */         catch (IllegalAlphabetException exc)
/*     */         {
/* 361 */           exc.printStackTrace(); }
/* 362 */         return null;
/*     */       }
/*     */     };
/*     */   }
/*     */   
/*     */ 
/*     */   public static Alignment inferDNAAlignmentFromProteinAlignment(SequenceDB dna, Alignment prot_al)
/*     */     throws Exception
/*     */   {
/* 371 */     List seq = new ArrayList();
/* 372 */     List ids = new ArrayList();
/*     */     
/* 374 */     for (int i = 0; i < prot_al.getIdCount(); i++)
/* 375 */       if (dna.ids().contains(prot_al.getIdentifier(i).getName())) {
/* 376 */         Sequence dnaSeq = dna.getSequence(prot_al.getIdentifier(i).getName());
/* 377 */         String dna_string = dnaSeq.seqString();
/* 378 */         String prot_string = prot_al.getAlignedSequenceString(i);
/* 379 */         StringBuffer sb = new StringBuffer(prot_al.getSiteCount() * 3);
/* 380 */         int beginIndex = 0;
/* 381 */         for (int j = 0; j < prot_string.length(); j++) {
/*     */           try {
/* 383 */             if (prot_string.charAt(j) == '-') {
/* 384 */               sb.append("???");
/*     */             }
/*     */             else {
/* 387 */               int state = ct.getAminoAcidState(new char[] { dna_string.charAt(beginIndex * 3), 
/* 388 */                 dna_string.charAt(beginIndex * 3 + 1), 
/* 389 */                 dna_string.charAt(beginIndex * 3 + 2) });
/* 390 */               char c = AminoAcids.DEFAULT_INSTANCE.getChar(state);
/* 391 */               if ((state < 0) || (state >= 20)) {
/* 392 */                 sb.append("???");
/* 393 */                 beginIndex++;
/*     */               }
/*     */               else {
/* 396 */                 sb.append(dna_string.substring(beginIndex * 3, beginIndex * 3 + 3));
/* 397 */                 beginIndex++;
/*     */               }
/*     */             }
/*     */           }
/*     */           catch (Exception exc) {
/* 402 */             exc.printStackTrace();
/* 403 */             System.err.println(beginIndex * 3 + " " + j + " " + dna_string.length() + " " + prot_string.length());
/* 404 */             System.exit(0);
/*     */           }
/*     */         }
/* 407 */         seq.add(sb.toString());
/*     */         
/* 409 */         ids.add(prot_al.getIdentifier(i));
/*     */       }
/* 411 */     return new SimpleAlignment((Identifier[])ids.toArray(new Identifier[ids.size()]), 
/* 412 */       (String[])seq.toArray(new String[seq.size()]), 
/* 413 */       "_-?.", Nucleotides.DEFAULT_INSTANCE);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private static Alignment resolveAlignment(Alignment overall, Alignment sub, String pseudo, String brother)
/*     */     throws Exception
/*     */   {
/* 421 */     if (Math.IEEEremainder(overall.getSiteCount(), 3.0D) != 0.0D) { throw new Exception("overall alignment length not divisible by 3 " + overall.getSiteCount());
/*     */     }
/* 423 */     int overallBrotherId = overall.whichIdNumber(brother);
/* 424 */     int subBrotherId = sub.whichIdNumber(brother);
/* 425 */     if (sub.getAlignedSequenceString(subBrotherId).indexOf('-') >= 0) throw new Exception("no gaps allowed in brother sequence in sub " + sub);
/* 426 */     String pseudoSeqString = sub.getAlignedSequenceString(sub.whichIdNumber(pseudo));
/*     */     
/*     */ 
/* 429 */     IdGroup new_ids = new SimpleIdGroup(overall, new SimpleIdGroup(new Identifier[] { sub.getIdentifier(sub.whichIdNumber(pseudo)) }));
/* 430 */     StringBuffer[] new_seqs = new StringBuffer[new_ids.getIdCount()];
/* 431 */     String[] overall_seqs = new String[overall.getIdCount()];
/* 432 */     int[] alias = new int[overall.getIdCount()];
/* 433 */     for (int i = 0; i < overall.getIdCount(); i++) {
/* 434 */       alias[i] = new_ids.whichIdNumber(overall.getIdentifier(i).getName());
/* 435 */       overall_seqs[i] = overall.getAlignedSequenceString(i);
/*     */     }
/* 437 */     int new_pseudoId = new_ids.whichIdNumber(pseudo);
/* 438 */     for (int i = 0; i < new_seqs.length; i++) {
/* 439 */       new_seqs[i] = new StringBuffer(overall.getSiteCount());
/*     */     }
/* 441 */     int k = 0;
/* 442 */     int length = (int)Math.floor(overall.getSiteCount() / 3);
/* 443 */     for (int j = 0; j < length; j++) {
/* 444 */       for (int i = 0; i < alias.length; i++) {
/* 445 */         new_seqs[alias[i]].append(overall_seqs[i].substring(3 * j, 3 * j + 3));
/*     */       }
/* 447 */       if (overall_seqs[overallBrotherId].substring(3 * j, 3 * j + 3).equals("---")) {
/* 448 */         new_seqs[new_pseudoId].append("---");
/*     */       }
/*     */       else
/*     */       {
/* 452 */         String str = pseudoSeqString.substring(k * 3, k * 3 + 3);
/* 453 */         if (str.indexOf('-') >= 0) { str = "---";
/*     */         } else {
/* 455 */           int state = ct.getAminoAcidState(str.toCharArray());
/* 456 */           if ((state >= 20) || (state < 0)) str = "---";
/*     */         }
/* 458 */         new_seqs[new_pseudoId].append(str);
/* 459 */         k++;
/*     */       }
/*     */     }
/* 462 */     String[] sequs = new String[new_seqs.length];
/* 463 */     for (int i = 0; i < new_seqs.length; i++) {
/* 464 */       sequs[i] = new_seqs[i].toString();
/*     */     }
/* 466 */     return new SimpleAlignment(new_ids, sequs, "_-?.", overall.getDataType());
/*     */   }
/*     */   
/*     */   public static void phyml(File treeF, Alignment align)
/*     */     throws Exception
/*     */   {
/* 472 */     String com = "";
/* 473 */     for (int i = 0; i < bin.length; i++) {
/* 474 */       if (new File(bin[i] + "phyml").exists()) {
/* 475 */         com = bin[i];
/* 476 */         break;
/*     */       }
/*     */     }
/*     */     
/* 480 */     File dir = treeF.getParentFile();
/* 481 */     File tmp = new File(dir, "tempor");
/* 482 */     FileFilter ff = new FileFilter() {
/*     */       public boolean accept(File f) {
/* 484 */         return f.getName().startsWith("tempor");
/*     */       }
/* 486 */     };
/* 487 */     tmp.deleteOnExit();
/* 488 */     PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(tmp)));
/* 489 */     AlignmentUtils.printInterleaved(align, pw);
/* 490 */     pw.close();
/* 491 */     String[] command = 
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 497 */       {
/* 498 */       com + "phyml", tmp.getAbsolutePath(), 
/* 499 */       "0", "i", "1", "0", 
/* 500 */       "HKY", "e", "0", 
/* 501 */       "4", "1.0", "BIONJ", "y", align.getDataType().getNumStates() == 20 ? new String[] {com + "phyml", tmp.getAbsolutePath(), "1", "i", "1", "1", "WAG", "0.0", "4", "1.0", "BIONJ", "y", "y" } : "y" };
/*     */     
/* 503 */     StringWriter stw1 = new StringWriter();
/* 504 */     StringWriter stw2 = new StringWriter();
/* 505 */     System.err.println("executing " + Print.toString(command));
/*     */     try {
/* 507 */       ProcessTools.exec(command, null, stw1, stw2);
/*     */       
/* 509 */       System.err.println(stw1.getBuffer().toString());
/* 510 */       System.err.println(stw2.getBuffer().toString());
/* 511 */       System.err.println("done");
/* 512 */       File phymlTree = new File(tmp.getAbsolutePath() + "_phyml_tree.txt");
/*     */       
/* 514 */       command = new String[] {
/* 515 */         "mv", phymlTree.getAbsolutePath(), 
/* 516 */         treeF.getAbsolutePath() };
/* 517 */       ProcessTools.exec(command, null, null, null);
/*     */     } catch (IOException exc) {
/* 519 */       System.err.println("warning - did not find phyml " + exc.getMessage());
/* 520 */       System.err.println("doing nj tree with ml distances");
/* 521 */       Tree tree = new pal.tree.NeighborJoiningTree(
/* 522 */         new AlignmentDistanceMatrix(SitePattern.getSitePattern(align), 
/* 523 */         SubstitutionModel.Utils.createSubstitutionModel(
/* 524 */         new WAG(AlignmentUtils.estimateFrequencies(align)))));
/* 525 */       PrintWriter pw1 = new PrintWriter(new BufferedWriter(new FileWriter(treeF)));
/* 526 */       TreeUtils.printNH(tree, pw1);
/* 527 */       pw1.close();
/*     */     }
/* 529 */     File[] toDelete = treeF.getParentFile().listFiles(ff);
/* 530 */     for (int i = 0; i < toDelete.length; i++) {
/* 531 */       toDelete[i].deleteOnExit();
/*     */     }
/*     */   }
/*     */ }


/* Location:              /home/lachlan/Desktop/psilc1.21/psilc.jar!/lc1/treefam/AlignTools.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */