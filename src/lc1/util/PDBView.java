/*     */ package lc1.util;
/*     */ 
/*     */ import gnu.trove.TIntIntHashMap;
/*     */ import gnu.trove.TObjectIntHashMap;
/*     */ import java.awt.Frame;
/*     */ import java.awt.event.WindowEvent;
/*     */ import java.awt.event.WindowListener;
/*     */ import java.io.BufferedOutputStream;
/*     */ import java.io.BufferedReader;
/*     */ import java.io.BufferedWriter;
/*     */ import java.io.File;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.FileReader;
/*     */ import java.io.FileWriter;
/*     */ import java.io.OutputStream;
/*     */ import java.io.PrintStream;
/*     */ import java.io.PrintWriter;
/*     */ import java.util.Arrays;
/*     */ import javax.swing.JFrame;
/*     */ import lc1.phyl.AlignUtils;
/*     */ import lc1.treefam.AlignTools;
/*     */ import org.biojava.bio.seq.ProteinTools;
/*     */ import org.biojava.bio.seq.Sequence;
/*     */ import org.biojava.bio.seq.SequenceIterator;
/*     */ import org.biojava.bio.seq.io.SeqIOTools;
/*     */ import org.biojava.bio.symbol.FiniteAlphabet;
/*     */ import org.openscience.jmol.app.Jmol;
/*     */ import pal.alignment.Alignment;
/*     */ import pal.datatype.AminoAcids;
/*     */ import pal.datatype.DataType;
/*     */ import pal.misc.Identifier;
/*     */ import pal.misc.SimpleIdGroup;
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
/*     */ public class PDBView
/*     */ {
/*     */   File script;
/*     */   File pdb;
/*  52 */   static double THRESH = 0.9D;
/*     */   
/*     */   public int[] alias;
/*     */   
/*  56 */   public String getName() { return this.pdb.getName(); }
/*     */   
/*  58 */   public static String bin = "";
/*     */   String sequence;
/*     */   String chain;
/*     */   
/*     */   public String getSymbolAt(int i) {
/*  63 */     return this.sequence.charAt(i);
/*     */   }
/*     */   
/*     */   private static void parsePDBSequence(File pdb, File fasta) throws Exception {
/*  67 */     BufferedReader br = new BufferedReader(new FileReader(pdb));
/*  68 */     StringBuffer sb = new StringBuffer(400);
/*  69 */     AminoAcids aa = AminoAcids.DEFAULT_INSTANCE;
/*  70 */     TObjectIntHashMap strToChar = new TObjectIntHashMap();
/*  71 */     for (int i = 0; i < aa.getNumStates(); i++) {
/*  72 */       strToChar.put(AminoAcids.getTLA(i), i);
/*     */     }
/*  74 */     int lastPos = 0;
/*  75 */     for (String st = br.readLine(); st != null; st = br.readLine())
/*  76 */       if (st.startsWith("ATOM")) {
/*  77 */         String[] str = st.split("\\s+");
/*  78 */         int pos = Integer.parseInt(str[4]);
/*     */         
/*  80 */         if (pos != lastPos) {
/*  81 */           lastPos = pos;
/*  82 */           char c = aa.getChar(strToChar.get(str[3]));
/*  83 */           System.err.println(pos + " " + str[3] + "->" + c);
/*  84 */           sb.append(c);
/*     */         }
/*     */       }
/*  87 */     PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(fasta)));
/*  88 */     pw.println(">" + pdb.getName());
/*  89 */     pw.println(sb.toString());
/*  90 */     pw.println();
/*  91 */     pw.close();
/*     */   }
/*     */   
/*     */   public PDBView(File pdb, String chain, File fasta, Alignment alignOrig) throws Exception {
/*  95 */     String pdbid = pdb.getName();
/*  96 */     this.pdb = pdb;
/*  97 */     this.script = new File("script");
/*  98 */     int[][] seqToAlignOrig = new int[alignOrig.getIdCount()][];
/*  99 */     if ((!fasta.exists()) || (fasta.length() == 0L)) parsePDBSequence(pdb, fasta);
/* 100 */     BufferedReader br = new BufferedReader(new FileReader(fasta));
/* 101 */     Sequence pdbseq = SeqIOTools.readFasta(br, ProteinTools.getAlphabet().getTokenization("token")).nextSequence();
/* 102 */     this.sequence = pdbseq.seqString();
/* 103 */     File tmp_align = new File(pdb.getAbsolutePath() + "_align.mfa");
/*     */     
/* 105 */     File temp = new File(pdb.getAbsolutePath() + "_align.fa");
/* 106 */     OutputStream tmp = new BufferedOutputStream(new FileOutputStream(temp));
/* 107 */     PrintWriter pw = new PrintWriter(tmp);
/* 108 */     for (int i = 0; i < alignOrig.getIdCount(); i++) {
/* 109 */       Object[] orig = AlignUtils.getAlias(AlignUtils.restrictAlignment(alignOrig, new SimpleIdGroup(new Identifier[] { alignOrig.getIdentifier(i) })));
/* 110 */       seqToAlignOrig[i] = ((int[])orig[1]);
/* 111 */       Alignment align_seq = (Alignment)orig[0];
/* 112 */       AlignTools.printMFA(align_seq, pw);
/*     */     }
/* 114 */     pw.flush();
/* 115 */     SeqIOTools.writeFasta(tmp, pdbseq);
/* 116 */     tmp.close();
/* 117 */     AlignTools.muscle(temp, tmp_align);
/*     */     
/* 119 */     Alignment align1 = AlignTools.readMFA(tmp_align, AminoAcids.DEFAULT_INSTANCE);
/* 120 */     int indexPdb = align1.whichIdNumber(pdbseq.getName());
/*     */     
/* 122 */     DataType dt = AminoAcids.DEFAULT_INSTANCE;
/* 123 */     int[] k = new int[align1.getSequenceCount()];
/* 124 */     Arrays.fill(k, 0);
/* 125 */     int[][] seqToAlign = new int[align1.getIdCount()][];
/* 126 */     for (int i = 0; i < alignOrig.getIdCount(); i++) {
/* 127 */       seqToAlign[align1.whichIdNumber(alignOrig.getIdentifier(i).getName())] = seqToAlignOrig[i];
/*     */     }
/* 129 */     seqToAlign[indexPdb] = new int[pdbseq.length()];
/* 130 */     for (int i = 0; i < align1.getSiteCount(); i++) {
/* 131 */       boolean[] gap = new boolean[align1.getSequenceCount()];
/* 132 */       TIntIntHashMap aliasToCount = new TIntIntHashMap();
/* 133 */       boolean allGap = true;
/* 134 */       for (int j = 0; j < align1.getSequenceCount(); j++) {
/* 135 */         char c = align1.getData(j, i);
/* 136 */         gap[j] = ((!dt.isGapChar(c)) && (!dt.isUnknownChar(c)) ? 0 : true);
/* 137 */         if (j != indexPdb) allGap = (allGap) && (gap[j] != 0);
/*     */       }
/* 139 */       for (int j = 0; j < align1.getSequenceCount(); j++)
/* 140 */         if (j != indexPdb) {
/* 141 */           int ali = seqToAlign[j][k[j]];
/* 142 */           aliasToCount.put(ali, 1 + aliasToCount.get(ali));
/* 143 */           if (gap[j] == 0) {
/* 144 */             k[j] += 1;
/* 145 */             if (k[j] == seqToAlign[j].length) k[j] -= 1;
/*     */           }
/*     */         }
/* 148 */       if (gap[indexPdb] == 0) {
/* 149 */         seqToAlign[indexPdb][k[indexPdb]] = getConsenus(aliasToCount);
/* 150 */         k[indexPdb] += 1;
/*     */       }
/*     */     }
/* 153 */     this.alias = seqToAlign[indexPdb];
/*     */   }
/*     */   
/* 156 */   private static int getConsenus(TIntIntHashMap map) { int[] keys = map.keys();
/* 157 */     int[] vals = map.getValues();
/* 158 */     int max = 0;
/* 159 */     for (int i = 1; i < keys.length; i++) {
/* 160 */       if (vals[i] > vals[max]) max = i;
/*     */     }
/* 162 */     return keys[max];
/*     */   }
/*     */   
/*     */   private void printScript(double[][] pos, double thresh, boolean lessThan) throws Exception
/*     */   {
/* 167 */     PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(this.script)));
/* 168 */     pw.println("background white;");
/* 169 */     pw.println("color orange;");
/* 170 */     pw.println("cartoon on;");
/* 171 */     pw.println("wireframe off;");
/* 172 */     pw.println("select all;");
/* 173 */     pw.println("cpk off;");
/* 174 */     String[] color = { "green", "red" };
/* 175 */     for (int j = 0; j < pos.length; j++) {
/* 176 */       StringBuffer sb = new StringBuffer("select (* && ");
/* 177 */       boolean printed = false;
/* 178 */       for (int i = 0; i < this.alias.length; i++) {
/* 179 */         boolean printSite = pos[j][this.alias[i]] < thresh;
/* 180 */         if (printSite) {
/* 181 */           sb.append(i + 1);sb.append(",");
/* 182 */           printed = true;
/*     */         }
/*     */       }
/*     */       
/*     */ 
/* 187 */       if (printed)
/*     */       {
/* 189 */         String st = sb.toString();
/* 190 */         st = st.substring(0, st.length() - 1) + ")";
/* 191 */         pw.println(st);
/* 192 */         pw.println("colour " + color[j]);
/* 193 */         pw.println("colour label black");
/* 194 */         pw.println("wireframe off");
/* 195 */         pw.println("spacefill on");
/* 196 */         pw.println("spacefill 300");
/* 197 */         pw.println("label off");
/*     */       }
/*     */     }
/* 200 */     pw.close();
/*     */   }
/*     */   
/*     */   public void run(double[][] pos, double thresh, String selection, JFrame chart, String name) throws Exception {
/* 204 */     boolean sum = selection.endsWith("sum");
/* 205 */     boolean lessThan = selection.startsWith("less");
/* 206 */     if (sum) {
/* 207 */       double[] tot = new double[pos[0].length];
/* 208 */       for (int i = 0; i < tot.length; i++) {
/* 209 */         tot[i] = 0.0D;
/* 210 */         for (int j = 0; j < pos.length; j++) {
/* 211 */           tot[i] += pos[j][i];
/*     */         }
/*     */       }
/* 214 */       pos = new double[][] { tot };
/*     */     }
/*     */     
/* 217 */     printScript(pos, thresh, lessThan);
/* 218 */     Thread th = new Thread() {
/*     */       public void run() {
/* 220 */         Jmol.main(new String[] { PDBView.this.pdb.getAbsolutePath(), "--script", PDBView.this.script.getAbsolutePath() });
/* 221 */         Frame[] frames = JFrame.getFrames();
/* 222 */         for (int i = 0; i < frames.length; i++) {
/* 223 */           String title = frames[i].getTitle();
/* 224 */           String name1 = PDBView.this.getName();
/* 225 */           System.err.println(title.startsWith(this.val$name));
/* 226 */           if (title.startsWith(name1)) {
/* 227 */             frames[i].setTitle(this.val$name);
/*     */             
/* 229 */             System.err.println("h");
/*     */           }
/*     */         }
/*     */       }
/* 233 */     };
/* 234 */     th.start();
/* 235 */     chart.addWindowListener(new WindowListener() {
/*     */       public void windowOpened(WindowEvent e) {}
/*     */       
/*     */       public void windowClosing(WindowEvent e) {
/* 239 */         try { this.val$th.stop();
/*     */         } catch (Exception exc) {
/* 241 */           exc.printStackTrace();
/*     */         }
/*     */       }
/*     */       
/*     */       public void windowClosed(WindowEvent e) {
/* 246 */         try { this.val$th.stop();
/*     */         } catch (Exception exc) {
/* 248 */           exc.printStackTrace();
/*     */         }
/*     */       }
/*     */       
/*     */       public void windowIconified(WindowEvent e) {}
/*     */       
/*     */       public void windowDeiconified(WindowEvent e) {}
/*     */       
/*     */       public void windowActivated(WindowEvent e) {}
/*     */       
/*     */       public void windowDeactivated(WindowEvent e) {}
/*     */     });
/*     */   }
/*     */ }


/* Location:              /home/lachlan/Desktop/psilc1.21/psilc.jar!/lc1/util/PDBView.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */