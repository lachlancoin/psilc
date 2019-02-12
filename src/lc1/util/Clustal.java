/*     */ package lc1.util;
/*     */ 
/*     */ import java.io.BufferedInputStream;
/*     */ import java.io.BufferedOutputStream;
/*     */ import java.io.BufferedWriter;
/*     */ import java.io.File;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.FileWriter;
/*     */ import java.io.OutputStream;
/*     */ import java.io.PrintStream;
/*     */ import java.io.PrintWriter;
/*     */ import java.io.PushbackReader;
/*     */ import java.io.StringReader;
/*     */ import java.io.StringWriter;
/*     */ import org.biojava.bio.BioException;
/*     */ import org.biojava.bio.seq.DNATools;
/*     */ import org.biojava.bio.seq.RNATools;
/*     */ import org.biojava.bio.seq.Sequence;
/*     */ import org.biojava.bio.seq.SequenceIterator;
/*     */ import org.biojava.bio.seq.db.SequenceDB;
/*     */ import org.biojava.bio.seq.impl.SimpleSequence;
/*     */ import org.biojava.bio.seq.io.SeqIOTools;
/*     */ import org.biojava.bio.symbol.Alphabet;
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
/*     */ import pal.datatype.Nucleotides;
/*     */ import pal.distance.AlignmentDistanceMatrix;
/*     */ import pal.distance.DistanceMatrix;
/*     */ import pal.misc.IdGroup;
/*     */ import pal.misc.Identifier;
/*     */ import pal.misc.SimpleIdGroup;
/*     */ import pal.tree.NeighborJoiningTree;
/*     */ import pal.tree.Tree;
/*     */ import pal.tree.TreeDistanceMatrix;
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
/*     */ public class Clustal
/*     */ {
/*  63 */   public static String alignmentCommand = "probcons";
/*     */   
/*     */   static File clusterDir;
/*     */   
/*     */   static File alignDir;
/*     */   
/*     */   static File tmpDir;
/*  70 */   public static boolean dna = true;
/*     */   
/*     */   static final boolean deleteOnExit = true;
/*     */   
/*     */   static SequenceIterator getProteinSequencesFromDNA(SequenceIterator seqDB_in)
/*     */     throws Exception
/*     */   {
/*  77 */     new SequenceIterator() {
/*     */       public boolean hasNext() {
/*  79 */         return Clustal.this.hasNext();
/*     */       }
/*     */       
/*     */       public Sequence nextSequence() throws BioException {
/*  83 */         Sequence dna = Clustal.this.nextSequence();
/*     */         try {
/*     */           SymbolList amino;
/*     */           SymbolList amino;
/*  87 */           if (dna.getAlphabet().equals(DNATools.getDNA())) {
/*  88 */             amino = 
/*  89 */               RNATools.translate(RNATools.transcribe(dna));
/*     */           }
/*     */           else {
/*  92 */             amino = dna;
/*     */           }
/*  94 */           String st = amino.seqString();
/*  95 */           String dnaSt = dna.seqString();
/*  96 */           return new SimpleSequence(amino, dna.getName(), dna.getName(), dna.getAnnotation());
/*     */         }
/*     */         catch (IllegalAlphabetException exc) {
/*  99 */           exc.printStackTrace(); }
/* 100 */         return null;
/*     */       }
/*     */     };
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   static String getMinDist(Tree tree, String seq1)
/*     */   {
/* 110 */     DistanceMatrix dm = new TreeDistanceMatrix(tree);
/* 111 */     int i = dm.whichIdNumber(seq1);
/* 112 */     int k = -1;
/* 113 */     for (int j = 0; j < dm.getIdCount(); j++) {
/* 114 */       if ((j != i) && (
/* 115 */         (k == -1) || (dm.getDistance(i, j) < dm.getDistance(i, k)))) {
/* 116 */         k = j;
/*     */       }
/*     */     }
/* 119 */     return dm.getIdentifier(k).getName();
/*     */   }
/*     */   
/*     */   static Alignment resolveAlignment(Alignment overall, Alignment sub, String pseudo, String brother) throws Exception
/*     */   {
/* 124 */     if (Math.IEEEremainder(overall.getSiteCount(), 3.0D) != 0.0D) { throw new Exception("overall alignment length not divisible by 3 " + overall.getSiteCount());
/*     */     }
/* 126 */     int overallBrotherId = overall.whichIdNumber(brother);
/* 127 */     int subBrotherId = sub.whichIdNumber(brother);
/* 128 */     if (sub.getAlignedSequenceString(subBrotherId).indexOf('-') >= 0) throw new Exception("no gaps allowed in brother sequence in sub " + sub);
/* 129 */     String pseudoSeqString = sub.getAlignedSequenceString(sub.whichIdNumber(pseudo));
/*     */     
/*     */ 
/* 132 */     IdGroup new_ids = new SimpleIdGroup(overall, new SimpleIdGroup(new Identifier[] { sub.getIdentifier(sub.whichIdNumber(pseudo)) }));
/* 133 */     StringBuffer[] new_seqs = new StringBuffer[new_ids.getIdCount()];
/* 134 */     String[] overall_seqs = new String[overall.getIdCount()];
/* 135 */     int[] alias = new int[overall.getIdCount()];
/* 136 */     for (int i = 0; i < overall.getIdCount(); i++) {
/* 137 */       alias[i] = new_ids.whichIdNumber(overall.getIdentifier(i).getName());
/* 138 */       overall_seqs[i] = overall.getAlignedSequenceString(i);
/*     */     }
/* 140 */     int new_pseudoId = new_ids.whichIdNumber(pseudo);
/* 141 */     for (int i = 0; i < new_seqs.length; i++) {
/* 142 */       new_seqs[i] = new StringBuffer(overall.getSiteCount());
/*     */     }
/* 144 */     int k = 0;
/* 145 */     int length = (int)Math.floor(overall.getSiteCount() / 3);
/* 146 */     for (int j = 0; j < length; j++) {
/* 147 */       for (int i = 0; i < alias.length; i++) {
/* 148 */         new_seqs[alias[i]].append(overall_seqs[i].substring(3 * j, 3 * j + 3));
/*     */       }
/* 150 */       if (overall_seqs[overallBrotherId].substring(3 * j, 3 * j + 3).equals("---")) {
/* 151 */         new_seqs[new_pseudoId].append("---");
/*     */       }
/*     */       else
/*     */       {
/* 155 */         String str = pseudoSeqString.substring(k * 3, k * 3 + 3);
/* 156 */         if (str.indexOf('-') >= 0) { str = "---";
/*     */         } else {
/* 158 */           int state = ct.getAminoAcidState(str.toCharArray());
/* 159 */           if ((state >= 20) || (state < 0)) str = "---";
/*     */         }
/* 161 */         new_seqs[new_pseudoId].append(str);
/* 162 */         k++;
/*     */       }
/*     */     }
/* 165 */     String[] sequs = new String[new_seqs.length];
/* 166 */     for (int i = 0; i < new_seqs.length; i++) {
/* 167 */       sequs[i] = new_seqs[i].toString();
/*     */     }
/* 169 */     return new SimpleAlignment(new_ids, sequs, "_-?.", overall.getDataType());
/*     */   }
/*     */   
/*     */   private static void makeProteinAlignment1(File clusterFile, File outP, Alphabet alpha, String pseudo_id)
/*     */   {
/*     */     try
/*     */     {
/* 176 */       String seq1 = clusterFile.getName();
/* 177 */       File target = clusterFile;
/* 178 */       ProcessTools.exec(new String[] { alignmentCommand, target.getAbsolutePath() }, null, null, null);
/* 179 */       new File(target.getAbsolutePath() + ".dnd").deleteOnExit();
/* 180 */       File dnd = new File(target.getAbsolutePath() + ".aln");
/* 181 */       dnd.deleteOnExit();
/* 182 */       Tree tree = new NeighborJoiningTree(new AlignmentDistanceMatrix(SitePattern.getSitePattern(new ReadAlignment(dnd.getAbsolutePath()))));
/* 183 */       String brother = getMinDist(tree, pseudo_id);
/*     */       
/* 185 */       SequenceDB seqDB = 
/* 186 */         SeqIOTools.readFasta(new BufferedInputStream(new FileInputStream(clusterFile)), alpha);
/* 187 */       File toAlignAsProt = new File(tmpDir, seq1 + "_prot");
/* 188 */       File toAlignAsDNA = new File(tmpDir, seq1 + "_dna");
/* 189 */       OutputStream osProt = new BufferedOutputStream(new FileOutputStream(toAlignAsProt));
/* 190 */       OutputStream osDNA = new BufferedOutputStream(new FileOutputStream(toAlignAsDNA));
/* 191 */       toAlignAsProt.deleteOnExit();
/* 192 */       toAlignAsDNA.deleteOnExit();
/* 193 */       SequenceIterator seqIt_prot = getProteinSequencesFromDNA(seqDB.sequenceIterator());
/* 194 */       for (SequenceIterator seqIt = seqDB.sequenceIterator(); seqIt.hasNext();) {
/* 195 */         Sequence seq_dna = seqIt.nextSequence();
/* 196 */         Sequence seq_prot = seqIt_prot.nextSequence();
/* 197 */         if (!seq_prot.getName().equals(seq_dna.getName())) throw new Exception("name mistmatch");
/* 198 */         if (seq_dna.getName().equals(pseudo_id)) {
/* 199 */           SeqIOTools.writeFasta(osDNA, seq_dna);
/*     */         }
/*     */         else {
/* 202 */           if (seq_dna.getName().equals(brother)) {
/* 203 */             SeqIOTools.writeFasta(osDNA, seq_dna);
/*     */           }
/* 205 */           SeqIOTools.writeFasta(osProt, seq_prot);
/*     */         }
/*     */       }
/* 208 */       osProt.close();
/* 209 */       osDNA.close();
/*     */       
/* 211 */       ProcessTools.exec(new String[] { alignmentCommand, toAlignAsProt.getAbsolutePath() }, null, null, null);
/*     */       
/* 213 */       new File(toAlignAsProt.getAbsolutePath() + ".dnd").deleteOnExit();
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 218 */       ProcessTools.exec(new String[] { alignmentCommand, toAlignAsDNA.getAbsolutePath() }, null, null, null);
/*     */       
/*     */ 
/* 221 */       new File(toAlignAsDNA.getAbsolutePath() + ".dnd").deleteOnExit();
/*     */       
/* 223 */       File aln_prot = new File(toAlignAsProt.getAbsolutePath() + ".aln");
/* 224 */       aln_prot.deleteOnExit();
/* 225 */       File aln_dna = new File(toAlignAsDNA.getAbsolutePath() + ".aln");
/* 226 */       aln_dna.deleteOnExit();
/*     */       
/* 228 */       Alignment align_prot = 
/* 229 */         inferDNAAlignmentFromProteinAlignment(seqDB, new ReadAlignment(aln_prot.getAbsolutePath()));
/* 230 */       Alignment align_dna = new ReadAlignment(aln_dna.getAbsolutePath());
/*     */       
/* 232 */       StrippedAlignment align_dna1 = new StrippedAlignment(align_dna);
/* 233 */       int br_id = align_dna1.whichIdNumber(brother);
/* 234 */       for (int i = 0; i < align_dna.getSiteCount(); i++) {
/* 235 */         if (align_dna.getData(br_id, i) == '-') {
/* 236 */           align_dna1.dropSite(i);
/*     */         }
/*     */       }
/* 239 */       align_dna = align_dna1;
/*     */       
/* 241 */       Alignment align = resolveAlignment(align_prot, align_dna, pseudo_id, brother);
/*     */       
/* 243 */       PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(outP)));
/* 244 */       AlignmentUtils.printCLUSTALW(align, pw);
/* 245 */       pw.close();
/*     */     } catch (Exception exc) {
/* 247 */       exc.printStackTrace();
/*     */     }
/*     */   }
/*     */   
/*     */   private static void makeProteinAlignment(File clusterFile, File outP, Alphabet alpha)
/*     */   {
/*     */     try
/*     */     {
/* 255 */       String seq1 = clusterFile.getName();
/* 256 */       File alignDir = new File(clusterDir.getParentFile(), "align");
/*     */       
/* 258 */       SequenceDB seqDB = null;
/* 259 */       File target; if (dna) {
/* 260 */         File target = new File(tmpDir, seq1);
/*     */         
/* 262 */         seqDB = 
/* 263 */           SeqIOTools.readFasta(new BufferedInputStream(new FileInputStream(clusterFile)), alpha);
/* 264 */         SequenceIterator seqDB_out = getProteinSequencesFromDNA(seqDB.sequenceIterator());
/* 265 */         OutputStream os = new BufferedOutputStream(new FileOutputStream(target));
/* 266 */         SeqIOTools.writeFasta(os, seqDB_out);
/* 267 */         os.close();
/*     */       }
/*     */       else {
/* 270 */         target = clusterFile;
/*     */       }
/* 272 */       if (target.getName().indexOf('.') >= 0) target = new File(target.getParentFile(), target.getName().split("\\.")[0]);
/* 273 */       String[] command = 
/*     */       
/* 275 */         { alignmentCommand, alignmentCommand.endsWith("probcons") ? new String[] { alignmentCommand, "-clustalw", target.getAbsolutePath() } : target.getAbsolutePath() };
/*     */       
/* 277 */       new File(target.getAbsolutePath() + ".dnd").deleteOnExit();
/*     */       
/* 279 */       StringWriter stw = new StringWriter();
/* 280 */       StringWriter stw1 = new StringWriter();
/* 281 */       ProcessTools.exec(command, null, stw, stw1);
/*     */       
/* 283 */       System.out.println(stw1.getBuffer().toString());
/*     */       
/*     */ 
/*     */ 
/* 287 */       PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(outP)));
/*     */       
/* 289 */       if ((dna) && (alpha.equals(DNATools.getDNA()))) {
/* 290 */         Alignment align = new ReadAlignment(new PushbackReader(new StringReader(stw.getBuffer().toString())));
/* 291 */         align = inferDNAAlignmentFromProteinAlignment(seqDB, align);
/* 292 */         AlignmentUtils.printCLUSTALW(align, pw);
/*     */       }
/*     */       else
/*     */       {
/* 296 */         pw.print(stw.getBuffer().toString());
/*     */       }
/* 298 */       pw.close();
/*     */     } catch (Exception exc) {
/* 300 */       exc.printStackTrace();
/*     */     } }
/*     */   
/* 303 */   static CodonTable ct = CodonTableFactory.createUniversalTranslator();
/*     */   
/*     */   public static Alignment inferDNAAlignmentFromProteinAlignment(SequenceDB dna, Alignment prot_al) throws Exception
/*     */   {
/* 307 */     String[] seq = new String[prot_al.getIdCount()];
/* 308 */     for (int i = 0; i < prot_al.getIdCount(); i++) {
/* 309 */       String dna_string = dna.getSequence(prot_al.getIdentifier(i).getName()).seqString();
/* 310 */       String prot_string = prot_al.getAlignedSequenceString(i);
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 316 */       StringBuffer sb = new StringBuffer(prot_al.getSiteCount() * 3);
/* 317 */       int beginIndex = 0;
/* 318 */       for (int j = 0; j < prot_string.length(); j++) {
/*     */         try {
/* 320 */           if (prot_string.charAt(j) == '-') {
/* 321 */             sb.append("???");
/*     */           }
/*     */           else {
/* 324 */             int state = ct.getAminoAcidState(new char[] { dna_string.charAt(beginIndex * 3), 
/* 325 */               dna_string.charAt(beginIndex * 3 + 1), 
/* 326 */               dna_string.charAt(beginIndex * 3 + 2) });
/* 327 */             char c = AminoAcids.DEFAULT_INSTANCE.getChar(state);
/* 328 */             if ((state < 0) || (state >= 20)) {
/* 329 */               sb.append("???");
/* 330 */               beginIndex++;
/*     */             }
/*     */             else {
/* 333 */               sb.append(dna_string.substring(beginIndex * 3, beginIndex * 3 + 3));
/* 334 */               beginIndex++;
/*     */             }
/*     */           }
/*     */         }
/*     */         catch (Exception exc) {
/* 339 */           exc.printStackTrace();
/* 340 */           System.err.println(beginIndex * 3 + " " + j + " " + dna_string.length() + " " + prot_string.length());
/* 341 */           System.exit(0);
/*     */         }
/*     */       }
/* 344 */       seq[i] = sb.toString();
/*     */     }
/* 346 */     return new SimpleAlignment(prot_al, seq, "_-?.", Nucleotides.DEFAULT_INSTANCE);
/*     */   }
/*     */   
/*     */ 
/* 350 */   static boolean setalignmentcommand = false;
/*     */   
/*     */   public static File alignHomologs(File clusterOut, File outpDir, String blast_bin, int alignMethod) throws Exception
/*     */   {
/* 354 */     Alphabet alph = DNATools.getDNA();
/* 355 */     if (!setalignmentcommand) {
/* 356 */       setalignmentcommand = true;
/* 357 */       alignmentCommand = blast_bin + alignmentCommand;
/*     */     }
/*     */     
/* 360 */     initialiseStaticVariables(outpDir);
/* 361 */     File alignOut = new File(alignDir, clusterOut.getName() + ".align");
/* 362 */     if ((alignOut.exists()) && (alignOut.length() > 0L)) {
/* 363 */       System.err.println("using existing alignment");
/* 364 */       return alignOut;
/*     */     }
/* 366 */     if (alignMethod == 0) { makeProteinAlignment(clusterOut, alignOut, alph);
/*     */     } else {
/* 368 */       SequenceDB seqDB = 
/* 369 */         SeqIOTools.readFasta(new BufferedInputStream(new FileInputStream(clusterOut)), alph);
/* 370 */       String pseudo_id = null;
/* 371 */       for (SequenceIterator it = seqDB.sequenceIterator(); it.hasNext();) {
/* 372 */         Sequence seq = it.nextSequence();
/* 373 */         String str = seq.seqString();
/* 374 */         if (str.indexOf('*') >= 0) {
/* 375 */           if (pseudo_id == null) pseudo_id = seq.getName(); else
/* 376 */             throw new Exception("at least two sequences have in frame stop codons: " + pseudo_id + " " + seq.getName());
/*     */         }
/*     */       }
/* 379 */       if (pseudo_id == null) {
/* 380 */         makeProteinAlignment(clusterOut, alignOut, alph);
/*     */       } else
/* 382 */         makeProteinAlignment1(clusterOut, alignOut, alph, pseudo_id);
/*     */     }
/* 384 */     if (alignOut.length() == 0L) {
/* 385 */       throw new Exception("clustal call did not work properly " + 
/* 386 */         clusterOut);
/*     */     }
/* 388 */     return alignOut;
/*     */   }
/*     */   
/*     */   private static void initialiseStaticVariables(File outpDir) {
/* 392 */     if ((clusterDir == null) || (alignDir == null)) {
/* 393 */       clusterDir = dna ? new File(outpDir, "cluster_dna") : new File(outpDir, "cluster");
/* 394 */       alignDir = new File(outpDir, dna ? "align_dna" : "align");
/* 395 */       tmpDir = new File(outpDir, "cluster_prot");
/* 396 */       if (!tmpDir.exists())
/*     */       {
/* 398 */         tmpDir.mkdir();
/*     */       }
/* 400 */       if (!clusterDir.exists()) clusterDir.mkdir();
/* 401 */       if (!alignDir.exists()) alignDir.mkdir();
/*     */     }
/*     */   }
/*     */ }


/* Location:              /home/lachlan/Desktop/psilc1.21/psilc.jar!/lc1/util/Clustal.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */