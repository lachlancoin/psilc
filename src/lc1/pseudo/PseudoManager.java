/*     */ package lc1.pseudo;
/*     */ 
/*     */ import com.braju.format.Format;
/*     */ import com.braju.format.Parameters;
/*     */ import forester.atv.ATVgraphic;
/*     */ import forester.atv.ATVjframe;
/*     */ import forester.atv.ATVpanel;
/*     */ import java.awt.Container;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.LayoutManager;
/*     */ import java.beans.PropertyChangeEvent;
/*     */ import java.beans.PropertyVetoException;
/*     */ import java.beans.VetoableChangeListener;
/*     */ import java.io.BufferedOutputStream;
/*     */ import java.io.BufferedReader;
/*     */ import java.io.BufferedWriter;
/*     */ import java.io.File;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.FileReader;
/*     */ import java.io.FileWriter;
/*     */ import java.io.FilenameFilter;
/*     */ import java.io.PrintStream;
/*     */ import java.io.PrintWriter;
/*     */ import java.lang.reflect.Constructor;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collection;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import java.util.Vector;
/*     */ import javax.swing.BoxLayout;
/*     */ import javax.swing.JFrame;
/*     */ import lc1.dp.AlignmentHMM;
/*     */ import lc1.dp.AlignmentProfileParser;
/*     */ import lc1.dp.DomainAnnotation;
/*     */ import lc1.dp.EVD;
/*     */ import lc1.dp.EmissionState;
/*     */ import lc1.dp.ProfileDP;
/*     */ import lc1.dp.ProfileHMM;
/*     */ import lc1.pfam.PfamAlphabet;
/*     */ import lc1.pfam.PfamIndex;
/*     */ import lc1.phyl.AlignUtils;
/*     */ import lc1.phyl.AminoStyle;
/*     */ import lc1.phyl.WAG_GWF;
/*     */ import lc1.treefam.ExtendedChart;
/*     */ import lc1.treefam.TreeCurationTool;
/*     */ import org.biojava.bio.Annotation;
/*     */ import org.biojava.bio.dist.Distribution;
/*     */ import org.biojava.bio.dist.DistributionFactory;
/*     */ import org.biojava.bio.gui.DistributionLogo;
/*     */ import org.biojava.bio.gui.TextLogoPainter;
/*     */ import org.biojava.bio.seq.ProteinTools;
/*     */ import org.biojava.bio.seq.Sequence;
/*     */ import org.biojava.bio.seq.io.SymbolTokenization;
/*     */ import org.biojava.bio.symbol.Alphabet;
/*     */ import org.biojava.bio.symbol.Symbol;
/*     */ import pal.alignment.Alignment;
/*     */ import pal.alignment.AlignmentUtils;
/*     */ import pal.alignment.SimpleAlignment;
/*     */ import pal.alignment.SitePattern;
/*     */ import pal.datatype.AminoAcids;
/*     */ import pal.datatype.DataType;
/*     */ import pal.eval.ModelParameters;
/*     */ import pal.misc.Identifier;
/*     */ import pal.misc.SimpleIdGroup;
/*     */ import pal.substmodel.RateMatrix;
/*     */ import pal.substmodel.SubstitutionModel.Utils;
/*     */ import pal.tree.AttributeNode;
/*     */ import pal.tree.NodeUtils;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class PseudoManager
/*     */ {
/*  81 */   static boolean graph = false;
/*     */   
/*  83 */   static double evd_thresh = 1.0E-5D;
/*     */   static PfamAlphabet alpha;
/*  85 */   Collection sl = null;
/*     */   
/*     */   PfamIndex pfam_index;
/*     */   
/*  89 */   static boolean allNodes = true;
/*  90 */   static boolean lnLPseudo = false;
/*     */   
/*     */   boolean recalc;
/*  93 */   static final double log2 = Math.log(2.0D);
/*     */   static File REPOS;
/*     */   File dir;
/*  96 */   public boolean truePseudoGene = false;
/*     */   
/*     */   AttributeNode[] seq_name;
/*     */   final pal.tree.Tree tree;
/* 100 */   forester.tree.Tree foresterTree = null;
/*     */   
/*     */   File treeF;
/*     */   Map foresterToNode;
/*     */   RateMatrix proteinModel;
/*     */   RateMatrix dnaModel;
/*     */   Alignment dna_align;
/* 107 */   int insertRates = 1;
/* 108 */   int matchRates = 1;
/*     */   
/*     */   Alignment codon_align;
/*     */   
/*     */   public Alignment prot_align;
/*     */   
/*     */   File hitsDir;
/*     */   
/*     */ 
/*     */   private Distribution getDistribution(double[] freq, Alphabet alpha, SymbolTokenization token, DataType dt)
/*     */     throws Exception
/*     */   {
/* 120 */     Distribution dist = DistributionFactory.DEFAULT.createDistribution(alpha);
/* 121 */     double sum = 0.0D;
/* 122 */     for (int i = 0; i < freq.length; i++) {
/* 123 */       sum += freq[i];
/*     */     }
/* 125 */     for (int i = 0; i < freq.length; i++) {
/* 126 */       dist.setWeight(token.parseToken(dt.getChar(i)), freq[i] / sum);
/*     */     }
/*     */     
/* 129 */     return dist;
/*     */   }
/*     */   
/*     */   private RateMatrix optimisedRateMatrix(Alignment align, RateMatrix rm) {
/* 133 */     if (rm.getNumParameters() == 0) return rm;
/* 134 */     if (!this.recalc) return rm;
/* 135 */     SitePattern sp = SitePattern.getSitePattern(align);
/*     */     
/*     */ 
/* 138 */     ModelParameters mp = new ModelParameters(sp, 
/* 139 */       SubstitutionModel.Utils.createSubstitutionModel(rm));
/* 140 */     System.err.println("Rate matrix parameters before optimizing (default) for " + rm.getDataType() + " matrix");
/* 141 */     for (int i = 0; i < rm.getNumParameters(); i++) {
/* 142 */       System.err.print(rm.getParameter(i) + " ");
/*     */     }
/* 144 */     System.err.println(this.tree);
/* 145 */     mp.estimateFromTree(this.tree);
/* 146 */     System.err.println("\n..after optimizing ");
/* 147 */     for (int i = 0; i < rm.getNumParameters(); i++) {
/* 148 */       System.err.print(rm.getParameter(i) + " ");
/*     */     }
/* 150 */     System.err.println();
/* 151 */     return rm;
/*     */   }
/*     */   
/*     */ 
/*     */   public static Map nodeToForester(pal.tree.Tree tree, forester.tree.Tree treeF, Map m)
/*     */   {
/* 157 */     foresterToNode(tree, treeF.getRoot().getChild1(), m);
/* 158 */     foresterToNode(tree, treeF.getRoot().getChild2(), m);
/* 159 */     return m;
/*     */   }
/*     */   
/* 162 */   private static void foresterToNode(pal.tree.Tree tree, forester.tree.Node nodeF, Map m) { Vector v = nodeF.getAllExternalChildren();
/* 163 */     String[] extNodes = new String[v.size()];
/* 164 */     for (int i = 0; i < extNodes.length; i++) {
/* 165 */       extNodes[i] = ((forester.tree.Node)v.get(i)).getSeqName();
/*     */     }
/* 167 */     m.put(NodeUtils.getFirstCommonAncestor(NodeUtils.findByIdentifier(tree.getRoot(), extNodes)), nodeF);
/* 168 */     if (!nodeF.isExternal()) {
/* 169 */       foresterToNode(tree, nodeF.getChild1(), m);
/* 170 */       foresterToNode(tree, nodeF.getChild2(), m);
/*     */     }
/*     */   }
/*     */   
/*     */   private static int[] getStartEnd(Alignment align, int id) {
/* 175 */     char[] chars = align.getAlignedSequenceString(id).toCharArray();
/* 176 */     int max = 0;
/* 177 */     int min = chars.length - 1;
/* 178 */     for (int i = 0; i < chars.length; i++) {
/* 179 */       if ("_-?.".indexOf(chars[i]) < 0) {
/* 180 */         min = i;
/* 181 */         break;
/*     */       }
/*     */     }
/* 184 */     for (int i = chars.length - 1; i >= 0; i--) {
/* 185 */       if ("_-?.".indexOf(chars[i]) < 0) {
/* 186 */         max = i;
/* 187 */         break;
/*     */       }
/*     */     }
/* 190 */     return new int[] { min, max };
/*     */   }
/*     */   
/* 193 */   public static String protrm = "WAG";
/* 194 */   public static String nucrm = "HKY";
/* 195 */   public static String seqN = null;
/* 196 */   public static boolean restrict = false;
/*     */   
/* 198 */   public PseudoManager(File repos, File dir, Collection sl, Alignment aln, pal.tree.Tree tree, forester.tree.Tree forester) throws Exception { this.tree = tree;
/* 199 */     this.foresterTree = forester;
/* 200 */     REPOS = repos;
/* 201 */     File pfam_ls = new File(REPOS, "Pfam_ls");
/* 202 */     System.err.println("here " + pfam_ls);
/* 203 */     this.pfam_index = (pfam_ls.exists() ? new PfamIndex(REPOS) : null);
/* 204 */     alpha = PfamAlphabet.makeAlphabet(REPOS);
/* 205 */     System.err.println("finished indexing");
/* 206 */     this.sl = sl;
/* 207 */     this.dir = dir;
/*     */     
/* 209 */     this.hitsDir = new File(dir, "PSILC_" + protrm + "_" + nucrm + (NodeProbabilityCalculator.recursive ? "_recursive" : ""));
/* 210 */     System.err.println("hits dir is " + this.hitsDir.getAbsolutePath());
/* 211 */     if (!this.hitsDir.exists()) this.hitsDir.mkdir();
/* 212 */     File domdom = new File(this.hitsDir, "domdom");
/* 213 */     this.recalc = ((!domdom.exists()) || (domdom.length() == 0L));
/* 214 */     File posterior = new File(this.hitsDir, "posterior");
/* 215 */     if (!this.hitsDir.exists()) this.hitsDir.mkdir();
/* 216 */     boolean dna = aln.getDataType().getNumStates() == 4;
/* 217 */     if (dna) {
/* 218 */       this.dna_align = aln;
/* 219 */       this.codon_align = AlignUtils.getCodonAlignmentFromDNA(this.dna_align);
/* 220 */       this.prot_align = AlignUtils.translate(this.codon_align);
/*     */     }
/*     */     else {
/* 223 */       this.prot_align = aln;
/*     */     }
/*     */     
/*     */ 
/* 227 */     if (seqN != null) {
/* 228 */       for (int i = 0; i < this.prot_align.getIdCount(); i++) {
/* 229 */         if (this.prot_align.getIdentifier(i).getName().startsWith(seqN)) {
/* 230 */           seqN = this.prot_align.getIdentifier(i).getName();
/*     */         }
/*     */       }
/*     */     }
/* 234 */     RateMatrix substMDNA = null;
/* 235 */     RateMatrix substMProt = null;
/*     */     
/* 237 */     double[] a_freq = AlignmentUtils.estimateFrequencies(this.prot_align);
/* 238 */     System.err.println("using " + protrm + " " + nucrm);
/* 239 */     if (protrm.startsWith("WAG_GWF")) {
/* 240 */       substMProt = new WAG_GWF(new double[] { 0.0D }, a_freq);
/*     */     }
/*     */     else
/*     */     {
/* 244 */       Class clazz = Class.forName("pal.substmodel." + protrm);
/* 245 */       substMProt = (RateMatrix)clazz.getConstructor(new Class[] { a_freq.getClass() }).newInstance(new Object[] { a_freq });
/*     */     }
/*     */     
/*     */ 
/* 249 */     Class clazz = Class.forName("pal.substmodel." + nucrm);
/* 250 */     double[] nuc_freq = AlignmentUtils.estimateFrequencies(this.dna_align);
/* 251 */     double[] nuc_param = new double[
/*     */     
/* 253 */       nucrm.equals("GTR") ? 5 : nucrm.equals("TN") ? 2 : 
/* 254 */       1];
/* 255 */     substMDNA = dna ? (RateMatrix)clazz.getConstructor(
/* 256 */       new Class[] { nuc_param.getClass(), nuc_freq.getClass() }).newInstance(new Object[] { nuc_param, nuc_freq }) : 
/* 257 */       null;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 263 */     this.dnaModel = optimisedRateMatrix(this.dna_align, substMDNA);
/* 264 */     this.proteinModel = optimisedRateMatrix(this.prot_align, substMProt);
/* 265 */     if (this.foresterTree != null)
/* 266 */       this.foresterToNode = nodeToForester(tree, this.foresterTree, new HashMap());
/* 267 */     if ((restrict) && (seqN != null)) {
/* 268 */       List nodL = new ArrayList();
/* 269 */       for (int i = 0; i < tree.getExternalNodeCount(); i++) {
/* 270 */         if (tree.getExternalNode(i).getIdentifier().getName().startsWith(seqN)) {
/* 271 */           nodL.add(tree.getExternalNode(i));
/*     */         }
/*     */       }
/* 274 */       this.seq_name = ((AttributeNode[])nodL.toArray(new AttributeNode[0]));
/*     */     }
/*     */     else
/*     */     {
/* 278 */       this.seq_name = new AttributeNode[tree.getExternalNodeCount() + (allNodes ? tree.getInternalNodeCount() - 1 : 0)];
/*     */       
/* 280 */       int ik = 0;
/* 281 */       for (int i = 0; i < this.seq_name.length; i++) {
/* 282 */         if (i < tree.getExternalNodeCount()) { this.seq_name[ik] = ((AttributeNode)tree.getExternalNode(i));
/* 283 */         } else { if (tree.getInternalNode(i - tree.getExternalNodeCount()).isRoot()) continue;
/* 284 */           this.seq_name[ik] = ((AttributeNode)tree.getInternalNode(i - tree.getExternalNodeCount())); }
/* 285 */         ik++;
/*     */       }
/*     */     }
/* 288 */     System.err.println(Arrays.asList(this.seq_name));
/* 289 */     if (this.seq_name.length == 0) throw new RuntimeException("seq name should not be zero");
/*     */   }
/*     */   
/* 292 */   public List hmmList = new ArrayList();
/* 293 */   public List posteriorProbs = new ArrayList();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   int[][] node_alias;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static String readLine(BufferedReader[][] br, double[][][] st)
/*     */     throws Exception
/*     */   {
/* 313 */     String id = null;
/* 314 */     for (int i = 1; i < st.length; i++) {
/* 315 */       st[i] = new double[br[i].length][];
/* 316 */       for (int k = 0; k < br[i].length; k++) {
/* 317 */         String line = br[i][k].readLine();
/*     */         
/* 319 */         if (line != null) {
/* 320 */           String[] split = line.split("\\s+");
/* 321 */           st[i][k] = new double[split.length - 1];
/* 322 */           for (int j = 1; j < split.length; j++) {
/* 323 */             st[i][k][(j - 1)] = Double.parseDouble(split[j]);
/*     */           }
/* 325 */           if (id == null) { id = split[0];
/* 326 */           } else if (!split[0].equals(id)) throw new Exception("doesnt match");
/*     */         }
/*     */         else {
/* 329 */           st[i] = null;
/* 330 */           return null;
/*     */         }
/*     */       }
/*     */     }
/* 334 */     return id;
/*     */   }
/*     */   
/*     */ 
/* 338 */   public static String[][] outputF = {
/* 339 */     { "posteriorP", "posteriorN" }, 
/* 340 */     { "psilcP", "psilcN" }, 
/* 341 */     new String[0] };
/*     */   
/*     */ 
/*     */   public void run()
/*     */     throws Exception
/*     */   {
/* 347 */     this.node_alias = new int[this.seq_name.length][];
/* 348 */     this.alias = new int[this.prot_align.getSequenceCount()][];
/* 349 */     this.stripped = new Alignment[this.prot_align.getSequenceCount()];
/* 350 */     for (int i = 0; i < this.seq_name.length; i++) {
/* 351 */       pal.tree.Node[] extNodes = NodeUtils.getExternalNodes(this.seq_name[i]);
/* 352 */       Identifier[] ids = new Identifier[extNodes.length];
/* 353 */       for (int j = 0; j < extNodes.length; j++) {
/* 354 */         ids[j] = extNodes[j].getIdentifier();
/*     */       }
/* 356 */       Alignment sub = AlignUtils.restrictAlignment(this.prot_align, new SimpleIdGroup(ids));
/* 357 */       Object[] strip = AlignUtils.getAlias(sub);
/* 358 */       this.node_alias[i] = ((int[])strip[1]);
/* 359 */       if (this.seq_name[i].isLeaf()) {
/* 360 */         int index = this.prot_align.whichIdNumber(this.seq_name[i].getIdentifier().getName());
/* 361 */         this.alias[index] = this.node_alias[i];
/* 362 */         this.stripped[index] = ((Alignment)strip[0]);
/*     */       }
/*     */     }
/*     */     
/* 366 */     for (int i = 0; i < this.prot_align.getSequenceCount(); i++)
/* 367 */       if (this.stripped[i] == null) {
/* 368 */         Alignment sl = new SimpleAlignment(this.prot_align.getIdentifier(i), 
/* 369 */           this.prot_align.getAlignedSequenceString(i), this.prot_align.getDataType());
/* 370 */         Object[] strip = AlignUtils.getAlias(sl);
/* 371 */         this.alias[i] = ((int[])strip[1]);
/* 372 */         this.stripped[i] = ((Alignment)strip[0]);
/*     */       }
/* 374 */     if (!this.hitsDir.exists()) this.hitsDir.mkdir();
/* 375 */     File summary = new File(this.hitsDir, "summary" + NodeProbabilityCalculator.SelectionModel.getHMMString());
/*     */     
/*     */ 
/* 378 */     double[] domdom = (double[])null;
/* 379 */     boolean redo = true;
/* 380 */     File domdomFile = new File(this.hitsDir, "domdom");
/* 381 */     int hmmIndex = 0;
/* 382 */     System.err.println("scoring sequences under hmm");
/* 383 */     for (Iterator it = this.sl.iterator(); it.hasNext();) {
/* 384 */       RunThread run = new RunThread((Symbol)it.next());
/* 385 */       run.run();
/* 386 */       hmmIndex++;
/*     */     }
/* 388 */     if (!this.recalc)
/*     */     {
/* 390 */       outputF[2 = this.hitsDir.list(new FilenameFilter() {
/*     */         public boolean accept(File f, String name) {
/* 392 */           return name.endsWith("_post");
/*     */         }
/*     */       });
/* 395 */       if (redo) {
/* 396 */         BufferedReader domdomF = new BufferedReader(new FileReader(new File(this.hitsDir, "domdom")));
/* 397 */         String[] line = domdomF.readLine().trim().split("\\s+");
/* 398 */         domdom = new double[line.length];
/* 399 */         for (int i = 0; i < line.length; i++) {
/* 400 */           domdom[i] = Double.parseDouble(line[i]);
/*     */         }
/*     */       }
/* 403 */       BufferedReader[][] op3 = new BufferedReader[outputF.length][];
/* 404 */       for (int i = 0; i < outputF.length; i++) {
/* 405 */         op3[i] = new BufferedReader[outputF[i].length];
/* 406 */         for (int j = 0; j < op3[i].length; j++) {
/* 407 */           op3[i][j] = new BufferedReader(new FileReader(new File(this.hitsDir, outputF[i][j])));
/*     */         }
/*     */       }
/*     */       
/* 411 */       double[][][] graph = new double[outputF.length][][];
/* 412 */       for (String name = readLine(op3, graph); name != null; name = readLine(op3, graph)) {
/*     */         try {
/* 414 */           String[] extNodes = name.split("&&");
/*     */           
/* 416 */           pal.tree.Node[] nds = NodeUtils.findByIdentifier(this.tree.getRoot(), extNodes);
/* 417 */           pal.tree.Node nde = NodeUtils.getFirstCommonAncestor(nds);
/* 418 */           forester.tree.Node foresterNode = null;
/* 419 */           if (this.foresterToNode != null) foresterNode = (forester.tree.Node)this.foresterToNode.get(nde);
/* 420 */           double[][][] res = new double[graph.length][][];
/* 421 */           for (int i = 1; i < res.length; i++) {
/* 422 */             res[i] = new double[graph[i].length][];
/* 423 */             System.arraycopy(graph[i], 0, res[i], 0, graph[i].length);
/*     */           }
/* 425 */           int index = -1;
/* 426 */           for (int k = 0; k < this.seq_name.length; k++) {
/* 427 */             if (this.seq_name[k] == nde) {
/* 428 */               index = k;
/*     */             }
/*     */           }
/* 431 */           if (redo)
/*     */           {
/* 433 */             double[] avgProtDomEmiss = new double[domdom.length];
/* 434 */             double[] avgNucDomEmiss = new double[domdom.length];
/* 435 */             for (int i = 0; i < domdom.length; i++) {
/* 436 */               domdom[i] += graph[1][0][i];
/* 437 */               domdom[i] += graph[1][1][i];
/*     */             }
/*     */             
/* 440 */             double[][] posteri = NodeProbabilityCalculator.getSitePosterior(avgProtDomEmiss, avgNucDomEmiss, domdom, this.node_alias[index]);
/* 441 */             res[0] = posteri;
/*     */           }
/*     */           
/* 444 */           if (foresterNode != null) foresterNode.setGraph(res, outputF);
/* 445 */           for (int i = 0; i < res.length; i++) {
/* 446 */             for (int j = 0; j < res[i].length; j++) {
/* 447 */               this.seq_name[index].setAttribute(outputF[i][j] + "_max", new Double(getMax(res[i][j])));
/* 448 */               this.seq_name[index].setAttribute(outputF[i][j] + "_avg", new Double(getAverage(res[i][j], false)));
/* 449 */               this.seq_name[index].setAttribute(outputF[i][j] + "_sum", new Double(getAverage(res[i][j], true)));
/*     */             }
/*     */           }
/*     */         } catch (Exception exc) {
/* 453 */           exc.printStackTrace();
/* 454 */           System.err.println("something wrong with " + name);
/*     */         }
/*     */       }
/* 457 */       for (int i = 0; i < outputF.length; i++) {
/* 458 */         for (int j = 0; j < op3[i].length; j++) {
/* 459 */           op3[i][j].close();
/*     */         }
/*     */       }
/*     */     }
/*     */     else
/*     */     {
/* 465 */       if (domdomFile.exists()) { throw new RuntimeException("domdom exists");
/*     */       }
/* 467 */       PrintWriter op2 = new PrintWriter(new BufferedWriter(new FileWriter(summary)));
/*     */       
/* 469 */       PrintWriter[][] op3 = new PrintWriter[outputF.length][];
/* 470 */       outputF[2] = new String[this.hmmList.size()];
/* 471 */       for (int i = 0; i < this.hmmList.size(); i++) {
/* 472 */         outputF[2][i] = (((AlignmentHMM)this.hmmList.get(i)).getName() + "_post");
/*     */       }
/* 474 */       for (int i = 0; i < outputF.length; i++) {
/* 475 */         op3[i] = new PrintWriter[outputF[i].length];
/* 476 */         for (int j = 0; j < op3[i].length; j++) {
/* 477 */           op3[i][j] = new PrintWriter(new BufferedWriter(new FileWriter(new File(this.hitsDir, outputF[i][j]))));
/*     */         }
/*     */       }
/* 480 */       NodeProbabilityCalculator npc = new NodeProbabilityCalculator(this.seq_name, 
/* 481 */         this.codon_align != null ? 
/* 482 */         SitePattern.getSitePattern(this.codon_align) : 
/* 483 */         SitePattern.getSitePattern(this.prot_align), this.dnaModel);
/*     */       
/* 485 */       System.err.println("getting emissions under different models... ");
/* 486 */       double[][] post_sum = new double[this.posteriorProbs.size()][this.prot_align.getSiteCount()];
/*     */       
/* 488 */       for (int l = 0; l < this.posteriorProbs.size(); l++) {
/* 489 */         Arrays.fill(post_sum[l], 0.0D);
/*     */       }
/* 491 */       for (int i = 0; i < this.prot_align.getSiteCount(); i++) {
/* 492 */         double sumP = 0.0D;
/* 493 */         for (int l = 0; l < this.posteriorProbs.size(); l++) {
/* 494 */           for (int j = 0; j < ((double[][])this.posteriorProbs.get(l)).length; j++) {
/* 495 */             post_sum[l][i] += ((double[][])this.posteriorProbs.get(l))[j][i];
/*     */           }
/*     */           
/* 498 */           sumP += post_sum[l][i];
/*     */         }
/* 500 */         if (sumP > 1.01D) {
/* 501 */           System.err.println("posterior at " + i + " is " + sumP + "> 1 - are you using two Pfam models from the same clan??");
/* 502 */           for (int l = 0; l < this.posteriorProbs.size(); l++) {
/* 503 */             for (int j = 0; j < ((double[][])this.posteriorProbs.get(l)).length; j++) {
/* 504 */               ((double[][])this.posteriorProbs.get(l))[j][i] /= sumP;
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/* 509 */       npc.getAverageEmissions(
/* 510 */         this.tree, 
/* 511 */         (AlignmentHMM[])this.hmmList.toArray(new AlignmentHMM[this.hmmList.size()]), 
/* 512 */         (double[][][])this.posteriorProbs.toArray(new double[this.posteriorProbs.size()][][]), 
/* 513 */         this.proteinModel);
/*     */       
/*     */ 
/* 516 */       System.err.println(" .. finished");
/*     */       
/*     */ 
/* 519 */       NodeProbabilityCalculator.printHeader(op2);
/*     */       
/* 521 */       for (int k = 0; k < this.seq_name.length; k++) {
/* 522 */         double[] overall = npc.calculateOverallPsilcScores(k);
/* 523 */         double[][] posterior = NodeProbabilityCalculator.getSitePosterior(npc.avgProtDomEmiss[k], npc.avgNucDomEmiss[k], 
/* 524 */           npc.avgDomDomEmiss, this.node_alias[k]);
/* 525 */         npc.print(op2, k, overall, NodeProbabilityCalculator.getMax(posterior));
/* 526 */         op2.flush();
/* 527 */         pal.tree.Node[] external = NodeUtils.getExternalNodes(this.seq_name[k]);
/* 528 */         String[] str = new String[external.length];
/* 529 */         for (int x = 0; x < str.length; x++) {
/* 530 */           str[x] = this.prot_align.getAlignedSequenceString(this.prot_align.whichIdNumber(external[x].getIdentifier().getName()));
/*     */         }
/*     */         
/* 533 */         double[][][] graph = {
/* 534 */           { posterior[0], posterior[1] }, 
/* 535 */           { npc.getPsilcScore(k, 0), npc.getPsilcScore(k, 1) }, 
/* 536 */           post_sum };
/*     */         
/* 538 */         if (this.foresterTree != null) {
/* 539 */           forester.tree.Node foresterNode = (forester.tree.Node)this.foresterToNode.get(this.seq_name[k]);
/* 540 */           foresterNode.setGraph(graph, outputF);
/* 541 */           for (int i = 0; i < graph.length; i++) {
/* 542 */             for (int j = 0; j < graph[i].length; j++) {
/* 543 */               this.seq_name[k].setAttribute(outputF[i][j], 
/* 544 */                 new Double(
/*     */                 
/* 546 */                 (i == 0) && (j == 1) ? getAverage(graph[i][j], false) : ((i == 0) && (j == 0)) || (j == 2) ? getMax(graph[i][j]) : 
/* 547 */                 getAverage(graph[i][j], true)));
/*     */             }
/*     */           }
/*     */         }
/*     */         
/*     */ 
/* 553 */         for (int i = 0; i < op3.length; i++) {
/* 554 */           for (int j = 0; j < op3[i].length; j++) {
/* 555 */             op3[i][j].print(NodeProbabilityCalculator.getIdString(this.seq_name[k]));op3[i][j].print(" ");
/* 556 */             for (int l = 0; l < graph[i][j].length; l++) {
/* 557 */               op3[i][j].print(Format.sprintf("%4.2g ", new Parameters(graph[i][j][l])));
/*     */             }
/* 559 */             op3[i][j].println();
/*     */           }
/*     */         }
/*     */       }
/*     */       
/*     */ 
/* 565 */       op2.close();
/* 566 */       for (int i = 0; i < outputF.length; i++) {
/* 567 */         for (int j = 0; j < op3[i].length; j++) {
/* 568 */           op3[i][j].close();
/*     */         }
/*     */       }
/* 571 */       PrintWriter domdom_pw = new PrintWriter(new PrintWriter(new BufferedWriter(new FileWriter(new File(this.hitsDir, "domdom")))));
/* 572 */       for (int i = 0; i < npc.avgDomDomEmiss.length; i++) {
/* 573 */         domdom_pw.print(Format.sprintf("%4.2g ", new Parameters(npc.avgDomDomEmiss[i])));
/*     */       }
/* 575 */       domdom_pw.close();
/*     */     }
/*     */     
/* 578 */     if (graph) {
/* 579 */       ATVjframe atvpanel = new ATVjframe(this.foresterTree);
/* 580 */       TreeCurationTool.switchColor(this.tree, this.foresterTree.getRoot(), "posteriorN_max", true);
/* 581 */       atvpanel.showWhole();
/* 582 */       atvpanel.getATVpanel().getATVgraphic().addVetoableChangeListener(new VetoableChangeListener() {
/*     */         public void vetoableChange(PropertyChangeEvent pce) throws PropertyVetoException {
/* 584 */           if (PseudoManager.this.selectionFrame == null) PseudoManager.this.selectionFrame = new JFrame();
/* 585 */           ExtendedChart.displayGraph(0, (forester.tree.Node)pce.getNewValue(), null, "", null, new HashSet(), PseudoManager.this.selectionFrame);
/*     */         }
/*     */       });
/*     */     }
/*     */   }
/*     */   
/*     */ 
/* 592 */   JFrame selectionFrame = null;
/*     */   public int[][] alias;
/*     */   
/* 595 */   private static double getAverage(double[] data, boolean sum) { double avg = 0.0D;
/* 596 */     int count = 0;
/* 597 */     for (int i = 0; i < data.length; i++)
/* 598 */       if (data[i] != Double.NEGATIVE_INFINITY) {
/* 599 */         avg += data[i];
/* 600 */         count++;
/*     */       }
/* 602 */     if (!sum) return avg / count;
/* 603 */     return avg;
/*     */   }
/*     */   
/*     */   private static double getMax(double[] data) {
/* 607 */     double max = Double.NEGATIVE_INFINITY;
/* 608 */     for (int i = 0; i < data.length; i++) {
/* 609 */       double y = data[i];
/* 610 */       if (y > max) max = y;
/*     */     }
/* 612 */     return max;
/*     */   }
/*     */   
/*     */   Alignment[] stripped;
/*     */   class RunThread
/*     */     implements Runnable
/*     */   {
/*     */     AlignmentHMM hmmA;
/*     */     Symbol symbol;
/*     */     PrintWriter op1;
/*     */     File hitsFile;
/*     */     EVD evd;
/*     */     
/*     */     RunThread(Symbol symbol)
/*     */     {
/*     */       try
/*     */       {
/* 629 */         pfamName = (String)symbol.getAnnotation().getProperty("pfamA_id");
/* 630 */         File pfamDir = PseudoManager.REPOS.getName().endsWith("CURRENT") ? PseudoManager.REPOS : 
/* 631 */           new File(PseudoManager.REPOS, "Pfam/");
/* 632 */         if (!pfamDir.exists()) pfamDir.mkdir();
/* 633 */         File pfamF = new File(pfamDir, pfamName);
/*     */         BufferedReader br;
/* 635 */         BufferedReader br; if ((pfamF.exists()) && (new File(pfamF, "HMM_ls").exists())) {
/* 636 */           File hmmF = new File(pfamF, "HMM_ls");
/* 637 */           br = new BufferedReader(new FileReader(hmmF));
/*     */         }
/*     */         else {
/* 640 */           File tmpHMM_ls = new File(PseudoManager.this.dir, "tmpHMM_ls");
/* 641 */           BufferedWriter bw = new BufferedWriter(new FileWriter(tmpHMM_ls));
/*     */           
/*     */ 
/* 644 */           PseudoManager.this.pfam_index.writeHMMFile(symbol.getName(), bw);
/* 645 */           br = new BufferedReader(new FileReader(tmpHMM_ls));
/* 646 */           tmpHMM_ls.deleteOnExit();
/*     */         }
/*     */         
/* 649 */         AlignmentProfileParser parser = AlignmentProfileParser.makeParser(br, true, PseudoManager.this.insertRates, PseudoManager.this.matchRates);
/* 650 */         double[] evdparams = parser.getEvalueParams();
/* 651 */         this.evd = new EVD(evdparams[1], evdparams[0]);
/* 652 */         this.hmmA = ((AlignmentHMM)parser.parse());
/* 653 */         br.close();
/* 654 */         this.hmmA.setTreeModel(PseudoManager.this.tree, PseudoManager.this.proteinModel);
/* 655 */         this.symbol = symbol;
/*     */         
/* 657 */         this.hitsFile = new File(PseudoManager.this.hitsDir, (String)symbol.getAnnotation().getProperty("pfamA_id"));
/*     */       } catch (Exception exc) { String pfamName;
/* 659 */         exc.printStackTrace();
/*     */       }
/*     */     }
/*     */     
/*     */     public void run() {
/*     */       try {
/* 665 */         if ((this.hitsFile.exists()) && (this.hitsFile.length() > 0L)) {
/* 666 */           Set completedIds = new HashSet();
/* 667 */           BufferedReader br = new BufferedReader(new FileReader(this.hitsFile));
/* 668 */           String s = "";
/* 669 */           while ((s = br.readLine()) != null) {
/* 670 */             String[] str = s.split("\\s+");
/* 671 */             str[0].startsWith("ENS");
/*     */           }
/*     */           
/*     */ 
/* 675 */           br.close();
/* 676 */           boolean containsAllIds = true;
/* 677 */           for (int i = 0; i < PseudoManager.this.prot_align.getIdCount(); i++) {
/* 678 */             if (!completedIds.contains(PseudoManager.this.prot_align.getIdentifier(i).getName())) {
/* 679 */               containsAllIds = false;
/* 680 */               break;
/*     */             }
/*     */           }
/* 683 */           if (containsAllIds) {
/* 684 */             return;
/*     */           }
/*     */         }
/* 687 */         this.op1 = new PrintWriter(new BufferedOutputStream(new FileOutputStream(this.hitsFile)));
/*     */         
/*     */ 
/*     */ 
/* 691 */         runDP1(true);
/* 692 */         this.hmmA.release();
/*     */       }
/*     */       catch (Throwable t) {
/* 695 */         t.printStackTrace();
/*     */       }
/* 697 */       this.op1.close();
/*     */     }
/*     */     
/*     */     private void graphLogo(List states, String name) throws Exception
/*     */     {
/* 702 */       JFrame f = new JFrame(name);
/* 703 */       Container container = f.getContentPane();
/* 704 */       LayoutManager gridbag = new BoxLayout(container, 0);
/* 705 */       f.getContentPane().setLayout(gridbag);
/* 706 */       Alphabet alpha = ProteinTools.getAlphabet();
/* 707 */       SymbolTokenization token = alpha.getTokenization("token");
/* 708 */       DataType dt = AminoAcids.DEFAULT_INSTANCE;
/* 709 */       Distribution[] dist = new Distribution[states.size()];
/* 710 */       for (int i = 0; i < states.size(); i++) {
/* 711 */         dist[i] = PseudoManager.this.getDistribution(((EmissionState)states.get(i)).getDistribution(), alpha, token, dt);
/*     */         
/* 713 */         DistributionLogo sLogo = new DistributionLogo();
/* 714 */         sLogo.setDistribution(dist[i]);
/* 715 */         sLogo.setLogoPainter(new TextLogoPainter());
/* 716 */         sLogo.setStyle(new AminoStyle());
/* 717 */         sLogo.setPreferredSize(new Dimension(10, 100));
/* 718 */         container.add(sLogo);
/*     */       }
/* 720 */       f.pack();
/*     */       
/*     */ 
/*     */ 
/* 724 */       f.setVisible(true);
/*     */     }
/*     */     
/*     */ 
/*     */     private Sequence runDP()
/*     */     {
/* 730 */       ProfileDP dp = new ProfileDP(this.symbol, this.hmmA, SitePattern.getSitePattern(PseudoManager.this.prot_align), "hmm", true);
/* 731 */       dp.search(true);
/* 732 */       dp.print(this.op1, 0.0D, null);
/* 733 */       this.op1.flush();
/* 734 */       return dp.domainList;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     private void runDP1(boolean full)
/*     */     {
/* 741 */       double bestSc = Double.NEGATIVE_INFINITY;
/* 742 */       double[][] posteriorProb = new double[this.hmmA.columns()][PseudoManager.this.prot_align.getSiteCount()];
/* 743 */       double[] posteriorIncludedCount = new double[PseudoManager.this.prot_align.getSiteCount()];
/* 744 */       for (int j = 0; j < posteriorProb.length; j++) {
/* 745 */         Arrays.fill(posteriorProb[j], 0.0D);
/*     */       }
/* 747 */       DomainAnnotation.printHeader(this.op1);
/* 748 */       for (int i = 0; i < PseudoManager.this.prot_align.getIdCount(); i++)
/*     */       {
/* 750 */         SitePattern sp = SitePattern.getSitePattern(
/*     */         
/* 752 */           PseudoManager.this.stripped[i]);
/* 753 */         ProfileDP dp = new ProfileDP(this.symbol, this.hmmA, sp, sp.getIdentifier(0).getName(), true);
/* 754 */         double score = dp.search(true);
/* 755 */         if (score > bestSc) {
/* 756 */           bestSc = score;
/*     */         }
/* 758 */         double evalue_i = this.evd.extremeValueP(score / PseudoManager.log2);
/* 759 */         if (evalue_i < PseudoManager.evd_thresh) {
/* 760 */           double[][] posteriorProbsInner = dp.getPosteriorMatch(this.hmmA.matchIndices);
/* 761 */           for (int ik = 0; ik < posteriorProbsInner[0].length; ik++) {
/* 762 */             for (int j = 0; j < posteriorProbsInner.length; j++) {
/* 763 */               posteriorProb[j][PseudoManager.this.alias[i][ik]] += posteriorProbsInner[j][ik];
/*     */             }
/* 765 */             posteriorIncludedCount[PseudoManager.this.alias[i][ik]] += 1.0D;
/*     */           }
/*     */         }
/*     */         else
/*     */         {
/* 770 */           System.err.println("not including " + PseudoManager.this.prot_align.getIdentifier(i) + " for posterior for symbol " + this.symbol.getAnnotation().getProperty("pfamA_id") + 
/* 771 */             " evd is " + evalue_i + " threshold is " + PseudoManager.evd_thresh);
/*     */         }
/* 773 */         dp.print(this.op1, evalue_i, PseudoManager.this.alias[i]);
/* 774 */         this.op1.flush();
/*     */       }
/*     */       
/* 777 */       this.hmmA.release();
/* 778 */       this.op1.flush();
/* 779 */       double evalue = this.evd.extremeValueP(bestSc / PseudoManager.log2);
/* 780 */       if (evalue > PseudoManager.evd_thresh) {
/* 781 */         System.err.println("not including " + this.symbol.getAnnotation().getProperty("pfamA_id") + " in calculation - evalue of " + evalue + " is lower than threshold " + PseudoManager.evd_thresh);
/*     */       }
/*     */       else {
/* 784 */         for (int j = 0; j < posteriorProb.length; j++) {
/* 785 */           for (int ik = 0; ik < posteriorProb[j].length; ik++) {
/* 786 */             posteriorProb[j][ik] = (posteriorIncludedCount[ik] == 0.0D ? 0.0D : posteriorProb[j][ik] / posteriorIncludedCount[ik]);
/*     */           }
/*     */         }
/*     */         
/* 790 */         PseudoManager.this.posteriorProbs.add(posteriorProb);
/* 791 */         PseudoManager.this.hmmList.add(this.hmmA);
/*     */       }
/*     */     }
/*     */   }
/*     */ }


/* Location:              /home/lachlan/Desktop/psilc1.21/psilc.jar!/lc1/pseudo/PseudoManager.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */