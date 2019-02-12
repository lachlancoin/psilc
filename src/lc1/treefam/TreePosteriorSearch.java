/*     */ package lc1.treefam;
/*     */ 
/*     */ import forester.atv.ATVjframe;
/*     */ import forester.tree.TreeHelper;
/*     */ import java.io.BufferedWriter;
/*     */ import java.io.File;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.FileWriter;
/*     */ import java.io.ObjectInputStream;
/*     */ import java.io.PrintStream;
/*     */ import java.io.PrintWriter;
/*     */ import java.io.PushbackReader;
/*     */ import java.io.StringReader;
/*     */ import java.lang.reflect.Constructor;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collection;
/*     */ import java.util.Enumeration;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import java.util.SortedMap;
/*     */ import java.util.Stack;
/*     */ import java.util.TreeMap;
/*     */ import java.util.TreeSet;
/*     */ import java.util.Vector;
/*     */ import lc1.phyl.AlignUtils;
/*     */ import lc1.phyl.CoalescentTree;
/*     */ import lc1.phyl.DomainCodonModel;
/*     */ import lc1.phyl.MaxLikelihoodTree;
/*     */ import lc1.phyl.ScaledTree;
/*     */ import org.apache.commons.cli.CommandLine;
/*     */ import org.apache.commons.cli.PosixParser;
/*     */ import pal.alignment.Alignment;
/*     */ import pal.alignment.AlignmentUtils;
/*     */ import pal.alignment.BootstrappedAlignment;
/*     */ import pal.alignment.ConcatenatedAlignment;
/*     */ import pal.alignment.ReadAlignment;
/*     */ import pal.alignment.SimpleAlignment;
/*     */ import pal.alignment.SitePattern;
/*     */ import pal.alignment.StrippedAlignment;
/*     */ import pal.datatype.DataType;
/*     */ import pal.distance.AlignmentDistanceMatrix;
/*     */ import pal.distance.DistanceMatrix;
/*     */ import pal.eval.FastLikelihoodCalculator;
/*     */ import pal.math.ConjugateDirectionSearch;
/*     */ import pal.math.MultivariateFunction;
/*     */ import pal.math.MultivariateMinimum;
/*     */ import pal.math.OrthogonalHints;
/*     */ import pal.misc.IdGroup;
/*     */ import pal.misc.Identifier;
/*     */ import pal.misc.Parameterized;
/*     */ import pal.misc.SimpleIdGroup;
/*     */ import pal.substmodel.RateMatrix;
/*     */ import pal.substmodel.SubstitutionModel.Utils;
/*     */ import pal.substmodel.WAG;
/*     */ import pal.tree.ParameterizedTree;
/*     */ import pal.tree.SimpleNode;
/*     */ import pal.tree.SimpleTree;
/*     */ import pal.tree.TreeUtils;
/*     */ import pal.tree.UnconstrainedTree;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class TreePosteriorSearch
/*     */   extends TreeSearch
/*     */ {
/*     */   SitePattern siteP;
/*     */   DistanceMatrix dm;
/*     */   static RateMatrix substM;
/*  77 */   Map hotSpot = new TreeMap(Duplet.NODE_COMP);
/*     */   static DataType dataType;
/*     */   static final boolean print = false;
/*     */   String[] architecture;
/*     */   int[] end;
/*     */   int[] alias;
/*     */   
/*  84 */   public static double getLogProbImpl(Duplet[] duplets, pal.tree.Node tree, SitePattern siteP) throws Exception { Object[] obj = getMVM(duplets, siteP, substM, 
/*  85 */       new UnconstrainedTree(new SimpleTree(tree)), "");
/*  86 */     ExtendedMVM func = (ExtendedMVM)obj[0];
/*  87 */     double[] xvec = (double[])obj[1];
/*  88 */     double res = getLogProb(func, xvec);
/*  89 */     func.release();
/*  90 */     return res;
/*     */   }
/*     */   
/*     */   protected void putHotSpotInMap(pal.tree.Node node, double[] bestHotSpot, double[] nbestHotSpot) {
/*  94 */     double[] diff = new double[this.alias.length];
/*  95 */     int end_index = 0;
/*  96 */     Arrays.fill(diff, 0.0D);
/*  97 */     for (int i = 0; i < diff.length; i++) {
/*  98 */       if (i == this.end[end_index]) end_index++;
/*  99 */       int averaging = 2;
/* 100 */       int min = Math.max(end_index == 0 ? 0 : this.end[(end_index - 1)], i - averaging);
/* 101 */       int max = Math.min(this.end[end_index], i + averaging);
/* 102 */       diff[i] = 0.0D;
/* 103 */       for (int j = min; j < max; j++) {
/* 104 */         if (this.alias[j] > 0)
/* 105 */           diff[i] += nbestHotSpot[this.siteP.alias[this.alias[j]]] - 
/* 106 */             bestHotSpot[this.siteP.alias[this.alias[j]]];
/*     */       }
/* 108 */       diff[i] /= (min - max);
/*     */     }
/* 110 */     this.hotSpot.put(node, diff);
/*     */   }
/*     */   
/*     */   public double getLogProbImpl(Duplet[] duplets, pal.tree.Node tree, double[] hotspot) throws Exception
/*     */   {
/* 115 */     Object[] obj = getMVM(duplets, this.siteP, substM, 
/* 116 */       new UnconstrainedTree(new SimpleTree(tree)), "");
/* 117 */     ExtendedMVM func = (ExtendedMVM)obj[0];
/* 118 */     double[] xvec = (double[])obj[1];
/* 119 */     double res = getLogProb(func, xvec);
/* 120 */     if ((this.accumulateHotSpotInfo) && (hotspot != null)) System.arraycopy(func.getSiteLikelihood(), 0, hotspot, 0, hotspot.length);
/* 121 */     func.release();
/* 122 */     return res;
/*     */   }
/*     */   
/*     */   public double getLogProbImpl(MultivariateFunction mvm, double[] xvec) throws Exception {
/* 126 */     return getLogProb(mvm, xvec);
/*     */   }
/*     */   
/*     */   Map getMCMCCounts(Duplet duplets, Duplet position, pal.tree.Node currentTree, Map resultArray) throws Exception
/*     */   {
/* 131 */     Object[] parentMVM = getMVM(new Duplet[] { duplets, position }, currentTree, "parent");
/* 132 */     MultipleMCMCSampler sampler = new MultipleMCMCSampler(resultArray);
/* 133 */     Map counts = sampler.run();
/* 134 */     if (counts == null) { return null;
/*     */     }
/* 136 */     return counts;
/*     */   }
/*     */   
/*     */   public Object[] getMVM(Duplet[] duplets, pal.tree.Node tree, String id) throws Exception
/*     */   {
/* 141 */     return getMVM(duplets, this.siteP, substM, new UnconstrainedTree(new SimpleTree(tree)), id);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static Object[] getMVM(Duplet[] duplets, SitePattern siteP, RateMatrix substM, UnconstrainedTree tree, String id)
/*     */     throws Exception
/*     */   {
/* 149 */     MultivariateFunction func = null;
/* 150 */     System.err.println("getting mvm for " + Arrays.asList(duplets));
/* 151 */     double[] xvec = (double[])null;
/* 152 */     if (siteP.getSequenceCount() != tree.getExternalNodeCount()) throw new Exception("tree and siteP do not have same no of leaf nodes " + tree.getExternalNodeCount() + " " + siteP.getSequenceCount());
/* 153 */     if (siteP.getDataType().getNumStates() != substM.getDataType().getNumStates()) throw new Exception("alignment and substM do not have same datatype " + substM.getDataType() + " " + siteP.getDataType());
/* 154 */     FastLikelihoodCalculator lv = new FastLikelihoodCalculator(siteP, tree, substM);
/* 155 */     if (duplets.length == 1) {
/* 156 */       func = new ExtendedMVM(tree, lv) {
/*     */         public double evaluate(double[] argument) {
/* 158 */           for (int i = 0; i < argument.length; i++) {
/* 159 */             this.tree.setParameter(argument[i], i);
/*     */           }
/*     */           
/* 162 */           return -1.0D * this.lv.calculateLogLikelihood();
/*     */         }
/*     */         
/* 165 */       };
/* 166 */       xvec = new double[func.getNumArguments()];
/* 167 */       for (int i = 0; i < xvec.length; i++) {
/* 168 */         xvec[i] = tree.getParameter(i);
/*     */       }
/*     */       
/*     */     }
/*     */     else
/*     */     {
/* 174 */       int[] freeVariables = getFreeVariables(tree, duplets[1]);
/*     */       
/*     */ 
/*     */ 
/* 178 */       func = new ExtendedMVM(tree, lv) {
/*     */         int count;
/*     */         
/*     */         public double evaluate(double[] argument) {
/* 182 */           for (int i = 0; i < argument.length; i++) {
/* 183 */             this.tree.setParameter(argument[i], this.val$freeVariables[i]);
/*     */           }
/* 185 */           double res = -1.0D * this.lv.calculateLogLikelihood();
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
/* 198 */           return res;
/*     */         }
/*     */         
/* 201 */         public double getLowerBound(int n) { return 0.001D; }
/*     */         
/*     */         public double getUpperBound(int n) {
/* 204 */           return this.tree.getUpperLimit(this.val$freeVariables[n]);
/*     */         }
/*     */         
/* 207 */         public int getNumArguments() { return this.val$freeVariables.length; }
/*     */         
/*     */         public OrthogonalHints getOrthogonalHints() {
/* 210 */           return null;
/*     */         }
/*     */         
/* 213 */         public void finalize() { this.lv.release();
/*     */         }
/* 215 */       };
/* 216 */       xvec = new double[freeVariables.length];
/* 217 */       for (int i = 0; i < freeVariables.length; i++) {
/* 218 */         xvec[i] = tree.getParameter(freeVariables[i]);
/*     */       }
/*     */     }
/* 221 */     return new Object[] { func, xvec };
/*     */   }
/*     */   
/*     */   public static double getLogProb(MultivariateFunction func, double[] xvec) throws Exception
/*     */   {
/* 226 */     double min = 0.0D;
/*     */     
/*     */ 
/* 229 */     long tim = System.currentTimeMillis();
/*     */     
/*     */ 
/*     */ 
/* 233 */     MultivariateMinimum mvm = new ConjugateDirectionSearch();
/* 234 */     System.err.println("optimising function over  " + func.getNumArguments() + " arguments");
/* 235 */     mvm.optimize(func, xvec, 0.05D, 0.01D);
/*     */     
/* 237 */     min = -1.0D * func.evaluate(xvec) - 1.0D * func.getNumArguments();
/* 238 */     System.err.println("... min is " + min);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 246 */     return min;
/*     */   }
/*     */   
/*     */   static pal.tree.Node getNode(pal.tree.Node node, pal.tree.Node root) {
/* 250 */     pal.tree.Node[] extNodes = pal.tree.NodeUtils.getExternalNodes(node);
/* 251 */     String[] names = new String[extNodes.length];
/* 252 */     for (int ij = 0; ij < names.length; ij++) {
/* 253 */       names[ij] = extNodes[ij].getIdentifier().getName();
/*     */     }
/*     */     
/* 256 */     return pal.tree.NodeUtils.getFirstCommonAncestor(pal.tree.NodeUtils.findByIdentifier(root, names));
/*     */   }
/*     */   
/*     */   static int[] getFreeVariables(UnconstrainedTree tree, pal.tree.Node duplets) throws Exception {
/* 260 */     SortedMap freeVar = new TreeMap();
/* 261 */     Map nodes = new HashMap();
/* 262 */     int extra = -1;
/* 263 */     for (int i = 0; i < duplets.getChildCount(); i++) {
/* 264 */       Duplet n = (Duplet)duplets.getChild(i);
/* 265 */       nodes.put(getNode(n, tree.getRoot()), new Integer(n.min_id));
/*     */     }
/* 267 */     for (int i = 0; i < tree.getExternalNodeCount(); i++) {
/* 268 */       pal.tree.Node treeNode = tree.getExternalNode(i);
/* 269 */       if (nodes.keySet().contains(treeNode)) {
/* 270 */         freeVar.put(nodes.get(treeNode), new Integer(i));
/*     */       }
/*     */     }
/*     */     
/* 274 */     for (int i = tree.getExternalNodeCount(); i < tree.getNumParameters(); i++) {
/* 275 */       pal.tree.Node treeNode = tree.getInternalNode(i - tree.getExternalNodeCount());
/* 276 */       if (nodes.keySet().contains(treeNode)) {
/* 277 */         freeVar.put(nodes.get(treeNode), new Integer(i));
/*     */       }
/* 279 */       else if (treeNode.getChildCount() == 2) {
/* 280 */         int count = 0;
/* 281 */         for (int k = 0; k < treeNode.getChildCount(); k++) {
/* 282 */           if (nodes.keySet().contains(treeNode.getChild(k))) count++;
/*     */         }
/* 284 */         if (count >= 2) {
/* 285 */           extra = i;
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 290 */     if (freeVar.keySet().size() != duplets.getChildCount()) throw new Exception("somehitng wrong did not contains all nodse " + tree + "\n" + duplets);
/* 291 */     if (extra >= 0) {
/* 292 */       int[] res = new int[freeVar.size() + 1];
/* 293 */       int i = 1;
/* 294 */       for (Iterator it = freeVar.values().iterator(); it.hasNext(); i++) {
/* 295 */         res[i] = ((Integer)it.next()).intValue();
/*     */       }
/* 297 */       res[0] = extra;
/* 298 */       return res;
/*     */     }
/*     */     
/* 301 */     int[] res = new int[freeVar.size()];
/* 302 */     int i = 0;
/* 303 */     for (Iterator it = freeVar.values().iterator(); it.hasNext(); i++) {
/* 304 */       res[i] = ((Integer)it.next()).intValue();
/*     */     }
/*     */     
/* 307 */     return res;
/*     */   }
/*     */   
/*     */ 
/*     */   static pal.tree.Tree randomizeBranchLengths(ParameterizedTree t)
/*     */   {
/* 313 */     for (int i = 0; i < t.getNumParameters(); i++) {
/* 314 */       t.setParameter(Math.random(), i);
/*     */     }
/* 316 */     return t;
/*     */   }
/*     */   
/*     */   static pal.tree.Tree modelBranchDropOut(pal.tree.Tree t, double prob) {
/* 320 */     pal.tree.Node n = modelBranchDropOut(new SimpleTree(t).getRoot(), prob);
/*     */     
/* 322 */     if (n == null) { return null;
/*     */     }
/* 324 */     if (n.getChildCount() == 2) {
/* 325 */       int i = n.getChild(0).getChildCount() >= 2 ? 0 : 1;
/* 326 */       if (n.getChild(i).getChildCount() < 2) return null;
/* 327 */       pal.tree.Node newN = new SimpleNode();
/* 328 */       newN.addChild(n.getChild(1 - i));
/* 329 */       n.getChild(1 - i).setParent(newN);
/* 330 */       for (int j = 0; j < n.getChild(i).getChildCount(); j++) {
/* 331 */         pal.tree.Node child = n.getChild(i).getChild(j);
/* 332 */         newN.addChild(child);
/* 333 */         double bl = n.getChild(i).getBranchLength() + n.getChild(i).getChild(j).getBranchLength();
/* 334 */         child.setBranchLength(bl);
/* 335 */         child.setParent(newN);
/*     */       }
/* 337 */       return new SimpleTree(newN);
/*     */     }
/*     */     
/* 340 */     return new SimpleTree(n);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   static pal.tree.Node modelBranchDropOut(pal.tree.Node root, double prob)
/*     */   {
/* 348 */     double ran = Math.random();
/* 349 */     pal.tree.Node result; pal.tree.Node result; if (ran < prob) {
/* 350 */       result = null;
/*     */     } else { pal.tree.Node result;
/* 352 */       if (root.isLeaf())
/*     */       {
/* 354 */         result = root;
/*     */       }
/*     */       else
/*     */       {
/* 358 */         pal.tree.Node newN = new SimpleNode();
/* 359 */         newN.setBranchLength(root.getBranchLength());
/* 360 */         for (int i = 0; i < root.getChildCount(); i++) {
/* 361 */           pal.tree.Node child = modelBranchDropOut(root.getChild(i), prob);
/* 362 */           if (child != null) {
/* 363 */             newN.addChild(child);
/* 364 */             child.setParent(newN);
/*     */           } }
/*     */         pal.tree.Node result;
/* 367 */         if (newN.getChildCount() == 0) { result = null; } else { pal.tree.Node result;
/* 368 */           if (newN.getChildCount() == 1) result = newN.getChild(0); else
/* 369 */             result = newN;
/*     */         }
/*     */       } }
/* 372 */     return result;
/*     */   }
/*     */   
/*     */   static ParameterizedTree getCoalescentTree(int n)
/*     */   {
/* 377 */     Identifier[] ids = new Identifier[n];
/* 378 */     for (int i = 1; i <= ids.length; i++) {
/* 379 */       ids[(i - 1)] = new Identifier(i);
/*     */     }
/* 381 */     IdGroup idG = new SimpleIdGroup(ids);
/* 382 */     ScaledTree sTree = new ScaledTree(new CoalescentTree(idG), 100.0D);
/* 383 */     sTree.setParameter(0.1D, 1);
/* 384 */     ParameterizedTree t = new UnconstrainedTree(sTree);
/* 385 */     return t;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   static pal.tree.Tree getStartTree(pal.tree.Tree tree, double prob)
/*     */   {
/* 392 */     pal.tree.Node n1 = tree.getRoot();
/* 393 */     for (int i = 0; i < 2; i++) {
/* 394 */       n1 = getStartTree(n1, prob);
/*     */     }
/* 396 */     return new SimpleTree(n1);
/*     */   }
/*     */   
/*     */   static pal.tree.Node getStartTree(pal.tree.Node n, double prob)
/*     */   {
/* 401 */     if (n.isLeaf()) return n;
/* 402 */     pal.tree.Node newN = new SimpleNode();
/* 403 */     for (int i = 0; i < n.getChildCount(); i++) {
/* 404 */       pal.tree.Node ch_i = n.getChild(i);
/* 405 */       if ((ch_i.getChildCount() > 0) && (Math.random() < prob))
/*     */       {
/* 407 */         for (int j = 0; j < ch_i.getChildCount(); j++) {
/* 408 */           pal.tree.Node child = getStartTree(ch_i.getChild(j), prob);
/* 409 */           newN.addChild(child);
/* 410 */           child.setParent(newN);
/*     */         }
/*     */       }
/*     */       else {
/* 414 */         newN.addChild(ch_i);
/* 415 */         ch_i.setParent(newN);
/*     */       }
/*     */     }
/* 418 */     return newN;
/*     */   }
/*     */   
/*     */   public static Object[] getRateMatrixDetails(CommandLine params) {
/* 422 */     String[] models = params.getOptionValues("substmodel");
/* 423 */     if (models == null) return new Object[2];
/* 424 */     String[] rateMatrixClass = new String[models.length];
/* 425 */     double[][] rateMatrixParams = new double[models.length][0];
/* 426 */     for (int i = 0; i < models.length; i++) {
/* 427 */       String[] str = models[i].split(";");
/* 428 */       rateMatrixClass[i] = str[0];
/* 429 */       rateMatrixParams[i] = new double[str.length - 1];
/* 430 */       for (int j = 0; j < rateMatrixParams[i].length; j++) {
/* 431 */         rateMatrixParams[i][j] = Double.parseDouble(str[(j + 1)]);
/*     */       }
/*     */     }
/* 434 */     return new Object[] { rateMatrixClass, rateMatrixParams };
/*     */   }
/*     */   
/*     */   public static void main(String[] args) throws Exception
/*     */   {
/* 439 */     CommandLine params = new PosixParser().parse(TreeSearch.OPTIONS, args);
/* 440 */     File dir = new File(params.getOptionValue("dir"), ".");
/* 441 */     File alignDir = new File(dir, "align");
/* 442 */     File treeDir = new File(dir, "tree");
/* 443 */     if (!treeDir.exists()) treeDir.mkdir();
/* 444 */     String[] input = params.getOptionValues("input");
/* 445 */     String[] architecture = params.getOptionValues("architecture");
/* 446 */     pal.tree.Tree speciesTree = params.hasOption("taxonomy") ? 
/* 447 */       new pal.tree.ReadTree(dir + "/" + params.getOptionValue("taxonomy")) : null;
/* 448 */     Alignment[] align = new Alignment[input.length];
/*     */     
/* 450 */     for (int i = 0; i < input.length; i++) {
/* 451 */       align[i] = new ReadAlignment(input[i]);
/*     */     }
/*     */     
/* 454 */     long time = System.currentTimeMillis();
/* 455 */     String[] aliasToName = (String[])null;
/*     */     try {
/* 457 */       Integer.parseInt(align[0].getIdentifier(0).getName());
/*     */     }
/*     */     catch (NumberFormatException exc) {
/* 460 */       aliasToName = new String[align[0].getIdCount()];
/* 461 */       Identifier[] ids = new Identifier[align[0].getSequenceCount()];
/* 462 */       String[] seqs = new String[align[0].getSequenceCount()];
/*     */       
/* 464 */       for (int j = 0; j < align[0].getIdCount(); j++) {
/* 465 */         aliasToName[j] = new String(align[0].getIdentifier(j).getName());
/* 466 */         ids[j] = new Identifier(j);
/* 467 */         seqs[j] = align[0].getAlignedSequenceString(j);
/*     */       }
/* 469 */       align = new Alignment[] { new SimpleAlignment(ids, seqs, "_-?.", align[0].getDataType()) };
/*     */     }
/*     */     
/* 472 */     TreeSearch.dupletAlphabet = new TreeSet(Duplet.NODE_COMP);
/* 473 */     Duplet simDuplet = null;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 478 */     Duplet speciesDuplet = speciesTree == null ? null : TreeSearch.makeDuplet(speciesTree.getRoot());
/*     */     
/* 480 */     Duplet sl = speciesDuplet != null ? TreeSearch.getIntersection(speciesDuplet, align[0]) : null;
/* 481 */     File posterior = new File(dir, "posterior/" + input[0]);
/* 482 */     String id = input[0].indexOf('/') > 0 ? input[0].split("/")[1] : input[0];
/* 483 */     Object[] obj = getRateMatrixDetails(params);
/* 484 */     TreePosteriorSearch tps = new TreePosteriorSearch(dir, id, align, architecture, sl, params.hasOption("graph"), 
/* 485 */       (String[])obj[0], (double[][])obj[1], Double.parseDouble(params.getOptionValue("collapse")), 
/* 486 */       Integer.parseInt(params.getOptionValue("keep")));
/* 487 */     Duplet trees = sl;
/* 488 */     printModel(tps.tp, tps.out_dir);
/* 489 */     PrintWriter treeFile = new PrintWriter(new BufferedWriter(new FileWriter(new File(treeDir, id))));
/* 490 */     pal.tree.Tree finalTree = new ReadTree(new PushbackReader(new StringReader(trees.toString() + ";")));
/* 491 */     if (aliasToName != null) {
/* 492 */       for (int j = 0; j < finalTree.getExternalNodeCount(); j++) {
/* 493 */         Identifier ident = 
/* 494 */           finalTree.getExternalNode(j).getIdentifier();
/* 495 */         ident.setName(aliasToName[Integer.parseInt(ident.getName())]);
/*     */       }
/*     */     }
/*     */     try {
/* 499 */       TreeSearch.annotateTree(tps.perturbations, finalTree.getRoot());
/*     */     } catch (Exception exc) {
/* 501 */       exc.printStackTrace();
/*     */     }
/* 503 */     NodeUtils.printNH(finalTree, treeFile, true, true, true);
/* 504 */     treeFile.close();
/*     */     
/*     */ 
/* 507 */     if (params.hasOption("graph"))
/*     */     {
/* 509 */       if (params.hasOption("taxonomy")) {
/* 510 */         pal.tree.Tree startTree = new pal.tree.ReadTree(dir.getAbsolutePath() + "/" + params.getOptionValue("taxonomy"));
/* 511 */         TreeSearch.annotateTree(startTree.getRoot(), 
/* 512 */           finalTree, TreeUtils.getLeafIdGroup(finalTree));
/*     */         
/*     */ 
/* 515 */         tps.graphTree(finalTree, tps.hotSpot, dir, true);
/*     */       }
/*     */       else {
/* 518 */         forester.tree.Tree tree1 = TreeHelper.readNHtree(new File(treeDir, id));
/* 519 */         new ATVjframe(tree1).showWhole();
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static pal.tree.Node getNJTree(double d, DistanceMatrix distM)
/*     */   {
/* 531 */     pal.tree.Tree tree = new pal.tree.NeighborJoiningTree(distM);
/* 532 */     MaxLikelihoodTree.graphTree(tree);
/* 533 */     pal.tree.Node node = (pal.tree.Node)collapseNodes(tree.getRoot(), d).get(0);
/* 534 */     MaxLikelihoodTree.graphTree(new SimpleTree(node));
/* 535 */     return node;
/*     */   }
/*     */   
/*     */   private static List collapseNodes(pal.tree.Node n, double d) {
/* 539 */     if (n.isLeaf()) return Arrays.asList(new pal.tree.Node[] { n });
/* 540 */     List nodes = new ArrayList();
/* 541 */     for (int i = 0; i < n.getChildCount(); i++) {
/* 542 */       nodes.addAll(collapseNodes(n.getChild(i), d));
/*     */     }
/* 544 */     if ((!n.isRoot()) && (n.getBranchLength() < d)) {
/* 545 */       return nodes;
/*     */     }
/*     */     
/* 548 */     pal.tree.Node node = new SimpleNode();
/* 549 */     node.setIdentifier(n.getIdentifier());
/* 550 */     node.setBranchLength(n.getBranchLength());
/* 551 */     for (Iterator it = nodes.iterator(); it.hasNext();) {
/* 552 */       pal.tree.Node child = (pal.tree.Node)it.next();
/* 553 */       child.setParent(node);
/* 554 */       node.addChild(child);
/*     */     }
/* 556 */     return Arrays.asList(new pal.tree.Node[] { node }); }
/*     */   
/*     */   protected double getLogProb(Duplet duplets1, Duplet prev_duplets, Duplet node, Duplet position, Stack parentsC, pal.tree.Node nextTree, double[] hotspot) throws Exception { Duplet[] record;
/*     */     Duplet[] duplets;
/*     */     Duplet[] record;
/* 561 */     if (position != null) {
/* 562 */       Duplet[] duplets = { duplets1, position };
/* 563 */       record = new Duplet[] { node, position };
/*     */     }
/*     */     else {
/* 566 */       duplets = new Duplet[] { duplets1 };
/* 567 */       record = new Duplet[] { node };
/*     */     }
/* 569 */     return getLogProb(duplets, record, nextTree, hotspot);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private double getLogProb(Duplet[] duplets, Duplet[] record, pal.tree.Node nextTree, double[] hotspot)
/*     */     throws Exception
/*     */   {
/* 577 */     Double result = this.tp.getTreeEvidence(record);
/*     */     
/* 579 */     if ((result == null) || ((this.accumulateHotSpotInfo) && (hotspot != null))) {
/* 580 */       double score_ij = getLogProbImpl(duplets, nextTree, hotspot) - this.tp.log_sc;
/*     */       
/* 582 */       this.tp.setLogProb(record, new Double(score_ij));
/* 583 */       return score_ij;
/*     */     }
/*     */     
/* 586 */     return result.doubleValue();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private static void getLeafIdGroup(pal.tree.Node dupl, List ids)
/*     */   {
/* 593 */     if (dupl.isLeaf()) { ids.add(new Identifier(((Duplet)dupl).min_id));
/*     */     } else {
/* 595 */       for (int i = 0; i < dupl.getChildCount(); i++) {
/* 596 */         getLeafIdGroup(dupl.getChild(i), ids);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static Duplet neighborJoining(SitePattern sp)
/*     */   {
/* 608 */     DistanceMatrix dm = new AlignmentDistanceMatrix(sp, 
/* 609 */       SubstitutionModel.Utils.createSubstitutionModel(new WAG(AlignmentUtils.estimateFrequencies(sp))));
/* 610 */     pal.tree.Tree tree = new lc1.phyl.NeighborJoiningTree(dm);
/*     */     try
/*     */     {
/* 613 */       if (tree.getRoot().getChildCount() != 2) throw new Exception("child count is not 2 " + tree.getRoot().getChildCount());
/*     */     }
/*     */     catch (Exception exc) {
/* 616 */       exc.printStackTrace();
/*     */     }
/* 618 */     return TreeSearch.makeDuplet(tree.getRoot());
/*     */   }
/*     */   
/*     */   private static Iterator getBootstrapIterations(Alignment align1, int no_iterations) {
/* 622 */     BootstrappedAlignment align = new BootstrappedAlignment(align1);
/* 623 */     align.setDataType(align1.getDataType());
/* 624 */     new Iterator() {
/*     */       int i;
/*     */       
/* 627 */       public boolean hasNext() { return this.i < this.val$no_iterations; }
/*     */       
/*     */       public Object next() {
/* 630 */         this.i += 1;
/* 631 */         Duplet d = TreePosteriorSearch.neighborJoining(SitePattern.getSitePattern(this.val$align));
/* 632 */         this.val$align.bootstrap();
/* 633 */         return d;
/*     */       }
/*     */       
/*     */       public void remove() {}
/*     */     };
/*     */   }
/*     */   
/*     */   private static Duplet getDuplets(IdGroup align) {
/* 641 */     Duplet[] dupl = new Duplet[align.getIdCount()];
/* 642 */     for (int i = 0; i < align.getIdCount(); i++) {
/* 643 */       dupl[i] = new Duplet(Integer.parseInt(align.getIdentifier(i).getName()));
/*     */     }
/* 645 */     return new Duplet(Arrays.asList(dupl));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   TreePosteriorSearch(File dir, String id, Alignment[] align1, String[] architecture, Duplet tree, boolean hotSpot, String[] rateMatrix, double[][] params, double collapse, int to_keep)
/*     */     throws Exception
/*     */   {
/* 657 */     super(dir, 
/* 658 */       tree == null ? getDuplets(align1[0]) : tree, 
/* 659 */       id, rateMatrix, params);
/*     */     
/*     */ 
/* 662 */     this.architecture = architecture;
/* 663 */     this.no_to_consider = to_keep;
/* 664 */     this.end = new int[align1.length];
/* 665 */     int count = 0;
/* 666 */     for (int i = 0; i < align1.length; i++) {
/* 667 */       count += align1[i].getSiteCount();
/* 668 */       this.end[i] = count;
/*     */     }
/*     */     
/* 671 */     ConcatenatedAlignment conc = new ConcatenatedAlignment(align1);
/* 672 */     conc.setDataType(align1[0].getDataType());
/* 673 */     setSubstM(conc, rateMatrix, params, true);
/* 674 */     DistanceMatrix distM = new AlignmentDistanceMatrix(SitePattern.getSitePattern(conc), SubstitutionModel.Utils.createSubstitutionModel(substM));
/* 675 */     if ((collapse > 0.0D) && (tree == null)) {
/* 676 */       this.start = TreeSearch.makeDuplet(getNJTree(collapse, distM));
/*     */     }
/* 678 */     else if (tree == null) {
/* 679 */       TreeSearch.makeDuplet(getNJTree(collapse, distM));
/*     */       
/* 681 */       tree = TreeSearch.makeDuplet(conc);
/* 682 */       this.distances = new double[distM.getIdCount()][distM.getIdCount()];
/* 683 */       for (int i = 0; i < tree.getChildCount(); i++) {
/* 684 */         int index_i = distM.whichIdNumber(tree.getChild(i).getIdentifier().getName());
/* 685 */         for (int j = 0; j < i; j++) {
/* 686 */           int index_j = distM.whichIdNumber(tree.getChild(j).getIdentifier().getName());
/* 687 */           this.distances[i][j] = distM.getDistance(index_i, index_j);
/*     */         }
/*     */       }
/*     */     }
/* 691 */     StrippedAlignment align = new StrippedAlignment(conc);
/* 692 */     align.setDataType(align1[0].getDataType());
/* 693 */     int new_align_count = 0;
/* 694 */     this.alias = new int[align.getSiteCount()];
/* 695 */     for (int i = 0; i < align.getSiteCount(); i++) {
/* 696 */       char c = align.getData(0, i);
/* 697 */       for (int j = 1; j < align.getSequenceCount(); j++) {
/* 698 */         if (align.getData(j, i) != c) {
/* 699 */           this.alias[i] = new_align_count;
/* 700 */           new_align_count++;
/* 701 */           break;
/*     */         }
/* 703 */         this.alias[i] = -1;
/*     */       }
/*     */     }
/*     */     
/* 707 */     if (substM.getDataType().getNumStates() == align.getDataType().getNumStates()) {
/* 708 */       this.siteP = SitePattern.getSitePattern(align);
/* 709 */     } else if ((substM.getDataType().getNumStates() == 64) && 
/* 710 */       (align.getDataType().getNumStates() == 4)) {
/* 711 */       this.siteP = SitePattern.getSitePattern(AlignUtils.getCodonAlignmentFromDNA(align));
/* 712 */     } else if ((substM.getDataType().getNumStates() == 20) && 
/* 713 */       (align.getDataType().getNumStates() == 4))
/* 714 */       this.siteP = SitePattern.getSitePattern(AlignUtils.translate(AlignUtils.getCodonAlignmentFromDNA(align)));
/* 715 */     this.accumulateHotSpotInfo = hotSpot;
/*     */     
/* 717 */     File posterior = new File(TreeSearch.posterior_dir, id);
/*     */   }
/*     */   
/*     */   static Object[] readSerialised(File dir, String key) throws Exception {
/* 721 */     Object[] res = new Object[3];
/* 722 */     for (int i = 0; i < res.length; i++) {
/* 723 */       ObjectInputStream p = new ObjectInputStream(new FileInputStream(new File(dir, key + "_" + i)));
/* 724 */       res[i] = p.readObject();
/* 725 */       p.close();
/*     */     }
/* 727 */     return res;
/*     */   }
/*     */   
/*     */ 
/*     */   static void computeSingleLayer(TreePosterior tp, Duplet duplets, pal.tree.Node currentTree, Duplet position, Stack parentsC, SitePattern siteP)
/*     */     throws Exception
/*     */   {
/* 734 */     setSubstM(siteP, TreeSearch.rateMatrixClass, TreeSearch.rateMatrixParams, true);
/* 735 */     Duplet[] pair = { duplets };
/* 736 */     double thresh = getLogProbImpl(pair, currentTree, siteP) - tp.log_sc;
/* 737 */     List keys = TreeSearch.getClusterIterator(2, position.getChildCount(), (duplets.equals(position)) && (position.getChildCount() <= 4), null, Integer.MAX_VALUE);
/* 738 */     Map resultArray = new TreeMap(TreeSearch.INT_COMP);
/* 739 */     Map nodeArray = new TreeMap(TreeSearch.INT_COMP);
/* 740 */     Map scoreArray = new TreeMap(TreeSearch.INT_COMP);
/* 741 */     Map treeArray = new TreeMap(TreeSearch.INT_COMP);
/* 742 */     Map stackArray = new TreeMap(TreeSearch.INT_COMP);
/* 743 */     tp.setLogProb(new Duplet[] { position, position }, new Double(thresh));
/* 744 */     TreeSearch.fillArrays(duplets, parentsC, position, keys, resultArray, treeArray, nodeArray, stackArray, currentTree);
/* 745 */     for (Iterator it = keys.iterator(); it.hasNext();) {
/* 746 */       Object key = it.next();
/*     */       
/* 748 */       double d1 = getLogProbImpl(new Duplet[] { (Duplet)resultArray.get(key), 
/* 749 */         position }, 
/* 750 */         (pal.tree.Node)treeArray.get(key), siteP) - 
/* 751 */         tp.log_sc;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected Object[] removeTaxaWithLeastAlignments(Duplet position, Stack parentsC)
/*     */     throws Exception
/*     */   {
/* 760 */     throw new Exception("shouldn't use this method");
/*     */   }
/*     */   
/*     */   protected double[] getHotSpot() {
/* 764 */     return new double[this.siteP.numPatterns];
/*     */   }
/*     */   
/*     */   void annotateTreeWithHotSpots(Duplet duplets, forester.tree.Tree tree, pal.tree.Tree tree1, Map hotSpot) throws Exception
/*     */   {
/* 769 */     Enumeration it = tree.getRoot().getAllChildren().elements();
/* 770 */     while (it.hasMoreElements()) {
/* 771 */       forester.tree.Node node = (forester.tree.Node)it.nextElement();
/* 772 */       if (!node.isExternal()) {
/* 773 */         Vector v = node.getAllExternalChildren();
/* 774 */         pal.tree.Node[] extNodes = new pal.tree.Node[v.size()];
/* 775 */         for (int i = 0; i < v.size(); i++) {
/* 776 */           extNodes[i] = pal.tree.NodeUtils.findByIdentifier(tree1.getRoot(), 
/* 777 */             ((forester.tree.Node)v.get(i)).getSeqName());
/*     */         }
/*     */         
/* 780 */         pal.tree.Node intNode = pal.tree.NodeUtils.getFirstCommonAncestor(extNodes);
/* 781 */         double[] hotspot = (double[])hotSpot.get(TreeSearch.makeDuplet(intNode));
/* 782 */         if (hotspot != null) {
/* 783 */           node.setGraph(new double[][][] { { hotspot } }, new String[][] { { "name" } });
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   static void setSubstM(Alignment siteP, String[] rateMatrixClass, double[][] params, boolean update)
/*     */     throws Exception
/*     */   {
/* 793 */     if (rateMatrixClass == null) {
/* 794 */       substM = new WAG(AlignmentUtils.estimateFrequencies(siteP));
/* 795 */       return;
/*     */     }
/* 797 */     RateMatrix[] rm = new RateMatrix[rateMatrixClass.length];
/* 798 */     dataType = siteP.getDataType();
/* 799 */     double[] freq = AlignmentUtils.estimateFrequencies(SitePattern.getSitePattern(siteP));
/* 800 */     double[] pfreq = freq;
/*     */     
/* 802 */     Class aminoModel = Class.forName("pal.substmodel.AminoAcidModel");
/* 803 */     if (dataType.getNumStates() == 4) {
/* 804 */       Alignment siteD = AlignUtils.translate(AlignUtils.getCodonAlignmentFromDNA(siteP));
/* 805 */       pfreq = AlignmentUtils.estimateFrequencies(SitePattern.getSitePattern(siteD));
/*     */     }
/* 807 */     for (int i = 0; i < rm.length; i++) {
/* 808 */       System.err.println(params[i].length + " " + freq.length);
/* 809 */       Class clazz = Class.forName(rateMatrixClass[i]);
/* 810 */       if (!aminoModel.isAssignableFrom(clazz)) {
/* 811 */         Constructor constr = clazz.getConstructor(new Class[] { params[i].getClass(), 
/* 812 */           freq.getClass() });
/*     */         
/* 814 */         rm[i] = ((RateMatrix)constr.newInstance(new Object[] { params[i], freq }));
/*     */       }
/*     */       else {
/* 817 */         Constructor constr = clazz.getConstructor(new Class[] { freq.getClass() });
/*     */         
/*     */ 
/* 820 */         rm[i] = ((RateMatrix)constr.newInstance(new Object[] { pfreq }));
/*     */       }
/*     */     }
/* 823 */     if (rm.length == 1) { substM = rm[0];
/*     */     } else {
/* 825 */       substM = new DomainCodonModel(rm[0], rm[1]);
/*     */     }
/*     */   }
/*     */ }


/* Location:              /home/lachlan/Desktop/psilc1.21/psilc.jar!/lc1/treefam/TreePosteriorSearch.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */