/*     */ package lc1.phyhmm;
/*     */ 
/*     */ import java.awt.Container;
/*     */ import java.awt.Dimension;
/*     */ import java.io.BufferedOutputStream;
/*     */ import java.io.BufferedReader;
/*     */ import java.io.BufferedWriter;
/*     */ import java.io.File;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.FileReader;
/*     */ import java.io.PipedReader;
/*     */ import java.io.PipedWriter;
/*     */ import java.io.PrintStream;
/*     */ import java.io.PrintWriter;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.Comparator;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import javax.swing.BoxLayout;
/*     */ import javax.swing.JFrame;
/*     */ import lc1.dp.AlignmentHMM;
/*     */ import lc1.dp.AlignmentProfileParser;
/*     */ import lc1.dp.DomainAnnotation;
/*     */ import lc1.dp.EmissionState;
/*     */ import lc1.dp.ProfileDP;
/*     */ import lc1.pfam.PfamAlphabet;
/*     */ import lc1.pfam.PfamIndex;
/*     */ import lc1.phyl.AlignUtils;
/*     */ import lc1.phyl.AminoStyle;
/*     */ import lc1.treefam.SDI;
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
/*     */ import pal.alignment.SimpleAlignment;
/*     */ import pal.alignment.SitePattern;
/*     */ import pal.alignment.StrippedAlignment;
/*     */ import pal.datatype.AminoAcids;
/*     */ import pal.datatype.DataType;
/*     */ import pal.distance.DistanceMatrix;
/*     */ import pal.misc.Identifier;
/*     */ import pal.substmodel.RateMatrix;
/*     */ import pal.tree.Node;
/*     */ import pal.tree.NodeUtils;
/*     */ import pal.tree.SimpleTree;
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
/*     */ public class DPManager
/*     */ {
/*     */   static PfamAlphabet alpha;
/*     */   File hitsDir;
/*  75 */   Collection sl = null;
/*     */   
/*     */   PfamIndex pfam_index;
/*     */   String input;
/*  79 */   static final double log2 = Math.log(2.0D);
/*     */   static File REPOS;
/*  81 */   static final DataType treeBuildMatrix = AminoAcids.DEFAULT_INSTANCE;
/*     */   
/*     */   Tree tree;
/*     */   
/*     */   File treeF;
/*     */   
/*     */   RateMatrix substMProt;
/*     */   Alignment prot_align;
/*     */   static final boolean modelRates = false;
/*     */   static final boolean trainNullModel = false;
/*  91 */   static int insertRates = 1;
/*  92 */   static int matchRates = 3;
/*  93 */   static boolean mix = true;
/*  94 */   static boolean useClosest = false;
/*  95 */   static boolean useAll = false;
/*  96 */   static int count = useAll ? 0 : 3;
/*  97 */   static double collapse = Double.parseDouble("0.01");
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private Distribution getDistribution(double[] freq, Alphabet alpha, SymbolTokenization token, DataType dt)
/*     */     throws Exception
/*     */   {
/* 109 */     Distribution dist = DistributionFactory.DEFAULT.createDistribution(alpha);
/* 110 */     double sum = 0.0D;
/* 111 */     for (int i = 0; i < freq.length; i++) {
/* 112 */       sum += freq[i];
/*     */     }
/* 114 */     for (int i = 0; i < freq.length; i++) {
/* 115 */       dist.setWeight(token.parseToken(dt.getChar(i)), freq[i] / sum);
/*     */     }
/* 117 */     return dist;
/*     */   }
/*     */   
/*     */ 
/*     */   public DPManager(String input, File repos, Collection symbols, Alignment prot_alignIn, Tree treeIn, RateMatrix substMProtIn, File dom_hits_dir)
/*     */     throws Exception
/*     */   {
/* 124 */     this.input = input;
/* 125 */     REPOS = repos;
/* 126 */     File pfam_ls = new File(REPOS, "Pfam_ls");
/* 127 */     this.pfam_index = (pfam_ls.exists() ? new PfamIndex(REPOS) : null);
/* 128 */     alpha = PfamAlphabet.makeAlphabet(REPOS);
/* 129 */     Map collapsedNodes = new HashMap();
/* 130 */     this.sl = symbols;
/* 131 */     StrippedAlignment prot_align1 = 
/* 132 */       new StrippedAlignment(prot_alignIn);
/* 133 */     prot_align1.setDataType(AminoAcids.DEFAULT_INSTANCE);
/* 134 */     int sites = prot_alignIn.getSiteCount();
/* 135 */     int j = prot_alignIn.whichIdNumber(input);
/* 136 */     if (j >= 0) {
/* 137 */       for (int i = 0; i < sites; i++) {
/* 138 */         if ("_-?.".indexOf(prot_alignIn.getData(j, i)) >= 0)
/* 139 */           prot_align1.dropSite(i);
/*     */       }
/*     */     }
/* 142 */     this.prot_align = prot_align1;
/*     */     
/* 144 */     if (!useAll)
/*     */     {
/* 146 */       this.tree = (useClosest ? restrictToClosest(treeIn, count, NodeUtils.findByIdentifier(treeIn.getRoot(), input)) : 
/* 147 */         restrictToFurthest(treeIn, count, NodeUtils.findByIdentifier(treeIn.getRoot(), input)));
/* 148 */       this.prot_align = AlignUtils.restrictAlignment(this.prot_align, TreeUtils.getLeafIdGroup(this.tree));
/*     */     }
/*     */     else {
/* 151 */       this.tree = treeIn;
/*     */     }
/* 153 */     int nodes = this.tree.getExternalNodeCount();
/* 154 */     this.substMProt = substMProtIn;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 159 */     this.hitsDir = dom_hits_dir;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private Tree restrictToFurthest(Tree tree, int no, Node node)
/*     */   {
/* 166 */     if (tree.getExternalNodeCount() <= 3) return tree;
/* 167 */     Node root = tree.getRoot();
/* 168 */     return new SimpleTree(SDI.trim(root, 
/* 169 */       (Node[])restrictToFurthest(root, no, node).toArray(new Node[0])));
/*     */   }
/*     */   
/*     */   private Collection restrictToFurthest(Node root, int no, Node node)
/*     */   {
/* 174 */     Node[] leafs = NodeUtils.getExternalNodes(root);
/* 175 */     if (leafs.length <= no) { return Arrays.asList(leafs);
/*     */     }
/*     */     
/* 178 */     int diff = no - root.getChildCount();
/* 179 */     Collection nodes = new HashSet();
/* 180 */     if (no == 1) {
/* 181 */       nodes.add(node);
/* 182 */       return nodes;
/*     */     }
/*     */     
/* 185 */     for (int i = 0; i < root.getChildCount(); i++) {
/* 186 */       if (NodeUtils.isAncestor(root.getChild(i), node)) {
/* 187 */         if ((diff > 0) && (!root.getChild(i).isLeaf())) {
/* 188 */           Collection inner = restrictToFurthest(root.getChild(i), diff + 1, node);
/* 189 */           nodes.addAll(inner);
/* 190 */           diff -= inner.size() - 1;
/*     */         }
/*     */         else {
/* 193 */           nodes.add(node);
/*     */         }
/*     */       }
/*     */       else {
/* 197 */         nodes.add(NodeUtils.getExternalNodes(root.getChild(i))[0]);
/*     */       }
/*     */     }
/* 200 */     if (diff > 0) {
/* 201 */       for (int i = 0; (i < root.getChildCount()) && (diff > 0); i++) {
/* 202 */         if (!root.getChild(i).isLeaf()) {
/* 203 */           Collection inner = restrictToFurthest(root.getChild(i), diff + 1, node);
/*     */           
/* 205 */           nodes.addAll(inner);
/* 206 */           break;
/*     */         }
/*     */       }
/*     */     }
/*     */     
/*     */     try
/*     */     {
/* 213 */       if (nodes.size() != no) throw new Exception("length mismatch " + nodes.size() + " " + no);
/*     */     } catch (Exception exc) {
/* 215 */       exc.printStackTrace();
/* 216 */       System.err.println(node);
/* 217 */       System.exit(0);
/*     */     }
/*     */     
/* 220 */     return nodes;
/*     */   }
/*     */   
/*     */   private Tree restrictToClosest(Tree tree, int no, Node node) {
/* 224 */     DistanceMatrix dm = new TreeDistanceMatrix(tree);
/* 225 */     if (tree.getExternalNodeCount() <= no) return tree;
/* 226 */     List ids = new ArrayList();
/* 227 */     int j = dm.whichIdNumber(node.getIdentifier().getName());
/* 228 */     for (int i = 0; i < dm.getIdCount(); i++) {
/* 229 */       ids.add(new Object[] { new Double(dm.getDistance(i, j)), dm.getIdentifier(i) });
/*     */     }
/* 231 */     Collections.sort(ids, new Comparator() {
/*     */       public int compare(Object o1, Object o2) {
/* 233 */         double dist1 = ((Double)((Object[])o1)[0]).doubleValue();
/* 234 */         double dist2 = ((Double)((Object[])o2)[0]).doubleValue();
/* 235 */         return 
/* 236 */           dist1 > dist2 ? 1 : dist1 < dist2 ? -1 : 0;
/*     */       }
/* 238 */     });
/* 239 */     Identifier[] idents = new Identifier[no];
/* 240 */     for (int i = 0; i < idents.length; i++) {
/* 241 */       idents[i] = ((Identifier)((Object[])ids.get(i))[1]);
/*     */     }
/* 243 */     return new SimpleTree(SDI.trim(tree.getRoot(), NodeUtils.findByIdentifier(tree.getRoot(), idents)));
/*     */   }
/*     */   
/*     */   public void run()
/*     */   {
/*     */     try
/*     */     {
/* 250 */       if (this.tree.getExternalNodeCount() < count) { return;
/*     */       }
/* 252 */       for (Iterator it = this.sl.iterator(); it.hasNext();)
/*     */       {
/* 254 */         RunThread run = new RunThread((Symbol)it.next());
/* 255 */         run.run();
/*     */       }
/*     */     }
/*     */     catch (Exception exc) {
/* 259 */       exc.printStackTrace();
/* 260 */       System.exit(0);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   class RunThread
/*     */     implements Runnable
/*     */   {
/*     */     AlignmentHMM hmmA;
/*     */     Symbol symbol;
/*     */     PrintWriter op1;
/*     */     File hitsFile;
/* 272 */     double[][] posteriorProbs = null;
/* 273 */     double posteriorIncludedCount = 0.0D;
/*     */     
/*     */ 
/*     */     RunThread(Symbol symbol)
/*     */     {
/*     */       try
/*     */       {
/* 280 */         pfamName = (String)symbol.getAnnotation().getProperty("pfamA_id");
/* 281 */         File pfamF = new File(DPManager.REPOS, "Pfam/" + pfamName);
/*     */         BufferedReader br;
/* 283 */         BufferedReader br; if ((pfamF.exists()) && (new File(pfamF, "HMM_ls").exists())) {
/* 284 */           File hmmF = new File(pfamF, "HMM_ls");
/* 285 */           br = new BufferedReader(new FileReader(hmmF));
/*     */         }
/*     */         else
/*     */         {
/* 289 */           PipedWriter pw = new PipedWriter();
/* 290 */           BufferedWriter stw = new BufferedWriter(pw);
/* 291 */           PipedReader pr = new PipedReader(pw);
/* 292 */           br = new BufferedReader(pr);
/*     */           
/* 294 */           DPManager.this.pfam_index.writeHMMFile(symbol.getName(), stw);
/*     */         }
/* 296 */         AlignmentProfileParser parser = 
/* 297 */           AlignmentProfileParser.makeParser(br, true, DPManager.insertRates, DPManager.matchRates);
/*     */         
/* 299 */         this.hmmA = ((AlignmentHMM)parser.parse());
/* 300 */         br.close();
/* 301 */         if ((DPManager.this.tree == null) || (DPManager.this.substMProt == null)) throw new NullPointerException("tree and/or substMProt is null " + DPManager.this.tree + " " + DPManager.this.substMProt);
/* 302 */         this.hmmA.setTreeModel(DPManager.this.tree, DPManager.this.substMProt);
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
/* 314 */         this.symbol = symbol;
/*     */         
/*     */ 
/* 317 */         this.hitsFile = new File(DPManager.this.hitsDir, DPManager.this.input);
/*     */       } catch (Exception exc) { String pfamName;
/* 319 */         exc.printStackTrace();
/*     */       }
/*     */     }
/*     */     
/*     */     public void run()
/*     */     {
/*     */       try {
/* 326 */         this.op1 = new PrintWriter(new BufferedOutputStream(new FileOutputStream(this.hitsFile)));
/* 327 */         double[] sc = runDP1(true);
/*     */         
/* 329 */         runDP();
/* 330 */         this.hmmA.release();
/* 331 */         this.op1.close();
/*     */       }
/*     */       catch (Throwable t) {
/* 334 */         t.printStackTrace();
/*     */       }
/*     */     }
/*     */     
/*     */     private void graphLogo(List states, String name)
/*     */       throws Exception
/*     */     {
/* 341 */       JFrame f = new JFrame(name);
/*     */       
/* 343 */       Container pane = f.getContentPane();
/* 344 */       f.setContentPane(pane);
/*     */       
/*     */ 
/* 347 */       pane.setLayout(new BoxLayout(pane, 0));
/* 348 */       Alphabet alpha = ProteinTools.getAlphabet();
/* 349 */       SymbolTokenization token = alpha.getTokenization("token");
/* 350 */       DataType dt = AminoAcids.DEFAULT_INSTANCE;
/* 351 */       Distribution[] dist = new Distribution[states.size()];
/* 352 */       for (int i = 0; i < states.size(); i++) {
/* 353 */         dist[i] = DPManager.this.getDistribution(((EmissionState)states.get(i)).getDistribution(), alpha, token, dt);
/*     */         
/* 355 */         DistributionLogo sLogo = new DistributionLogo();
/* 356 */         sLogo.setDistribution(dist[i]);
/* 357 */         sLogo.setLogoPainter(new TextLogoPainter());
/*     */         
/* 359 */         sLogo.setStyle(new AminoStyle());
/* 360 */         sLogo.setMinimumSize(new Dimension(10, 100));
/*     */         
/* 362 */         sLogo.setPreferredSize(new Dimension(30, 100));
/*     */         
/* 364 */         pane.add(sLogo);
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 370 */       f.pack();
/* 371 */       f.paint(f.getGraphics());
/* 372 */       f.setSize(300, 300);
/* 373 */       f.setVisible(true);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     private Sequence runDP()
/*     */     {
/* 381 */       ProfileDP dp = new ProfileDP(this.symbol, this.hmmA, SitePattern.getSitePattern(DPManager.this.prot_align), "hmm_" + DPManager.this.input, DPManager.mix);
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
/* 405 */       dp.search(false);
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
/* 418 */       dp.print(this.op1, 0.0D, null);
/* 419 */       this.op1.flush();
/* 420 */       return dp.domainList;
/*     */     }
/*     */     
/*     */ 
/*     */     private double[] runDP1(boolean full)
/*     */     {
/* 426 */       double bestSc = Double.NEGATIVE_INFINITY;
/* 427 */       double worstSc = Double.POSITIVE_INFINITY;
/* 428 */       double seq_thresh = this.symbol.getAnnotation().containsProperty("ls_seq_thresh") ? 
/* 429 */         ((Float)this.symbol.getAnnotation().getProperty("ls_seq_thresh")).floatValue() : 
/* 430 */         Double.NEGATIVE_INFINITY;
/* 431 */       DomainAnnotation.printHeader(this.op1);
/* 432 */       for (int i = 0; i < DPManager.this.prot_align.getIdCount(); i++) {
/* 433 */         Alignment sl = new SimpleAlignment(DPManager.this.prot_align.getIdentifier(i), 
/* 434 */           DPManager.this.prot_align.getAlignedSequenceString(i), DPManager.this.prot_align.getDataType());
/* 435 */         ProfileDP dp = new ProfileDP(this.symbol, this.hmmA, SitePattern.getSitePattern(sl), sl.getIdentifier(0).getName(), DPManager.mix);
/* 436 */         double score = dp.search(false);
/* 437 */         if (score > bestSc) {
/* 438 */           bestSc = score;
/*     */ 
/*     */         }
/* 441 */         else if (score < worstSc) {
/* 442 */           worstSc = score;
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */ 
/* 448 */         if (i == 0) { this.posteriorProbs = dp.getPosteriorMatch(this.hmmA.matchIndices);
/*     */         }
/*     */         else {
/* 451 */           double[][] posteriorProbsInner = dp.getPosteriorMatch(this.hmmA.matchIndices);
/* 452 */           for (int j = 0; j < this.posteriorProbs.length; j++) {
/* 453 */             for (int ik = 0; ik < this.posteriorProbs[0].length; ik++) {
/* 454 */               this.posteriorProbs[j][ik] += posteriorProbsInner[j][ik];
/*     */             }
/*     */           }
/*     */         }
/*     */         
/* 459 */         this.posteriorIncludedCount += 1.0D;
/* 460 */         dp.print(this.op1, 0.0D, null);
/* 461 */         this.op1.flush();
/*     */       }
/*     */       
/*     */ 
/* 465 */       this.op1.flush();
/* 466 */       for (int j = 0; j < this.posteriorProbs.length; j++) {
/* 467 */         for (int ik = 0; ik < this.posteriorProbs[0].length; ik++) {
/* 468 */           this.posteriorProbs[j][ik] /= this.posteriorIncludedCount;
/*     */         }
/*     */       }
/*     */       
/*     */ 
/* 473 */       return new double[] { bestSc, worstSc };
/*     */     }
/*     */   }
/*     */ }


/* Location:              /home/lachlan/Desktop/psilc1.21/psilc.jar!/lc1/phyhmm/DPManager.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */