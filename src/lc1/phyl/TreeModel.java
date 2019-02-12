/*     */ package lc1.phyl;
/*     */ 
/*     */ import java.io.PrintStream;
/*     */ import java.io.PushbackReader;
/*     */ import java.io.StringReader;
/*     */ import java.lang.reflect.Constructor;
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.HashSet;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import lc1.util.Print;
/*     */ import org.biojava.utils.SmallMap;
/*     */ import pal.alignment.Alignment;
/*     */ import pal.alignment.AlignmentParseException;
/*     */ import pal.alignment.AlignmentUtils;
/*     */ import pal.alignment.ConcatenatedAlignment;
/*     */ import pal.alignment.SimpleAlignment;
/*     */ import pal.alignment.SitePattern;
/*     */ import pal.alignment.StrippedAlignment;
/*     */ import pal.datatype.AminoAcids;
/*     */ import pal.datatype.Codons;
/*     */ import pal.datatype.DataType;
/*     */ import pal.distance.AlignmentDistanceMatrix;
/*     */ import pal.distance.DistanceMatrix;
/*     */ import pal.eval.LikelihoodValue;
/*     */ import pal.math.MultivariateFunction;
/*     */ import pal.math.MultivariateMinimum;
/*     */ import pal.math.OrthogonalSearch;
/*     */ import pal.misc.IdGroup;
/*     */ import pal.misc.Identifier;
/*     */ import pal.misc.Parameterized;
/*     */ import pal.misc.SimpleIdGroup;
/*     */ import pal.substmodel.AminoAcidModel;
/*     */ import pal.substmodel.GammaRates;
/*     */ import pal.substmodel.RateDistribution;
/*     */ import pal.substmodel.RateMatrix;
/*     */ import pal.substmodel.SubstitutionModel;
/*     */ import pal.substmodel.SubstitutionModel.Utils;
/*     */ import pal.substmodel.UniformRate;
/*     */ import pal.substmodel.YangCodonModel;
/*     */ import pal.tree.LogParameterizedTree;
/*     */ import pal.tree.Node;
/*     */ import pal.tree.NodeUtils;
/*     */ import pal.tree.ParameterizedTree;
/*     */ import pal.tree.ReadTree;
/*     */ import pal.tree.SimpleNode;
/*     */ import pal.tree.SimpleTree;
/*     */ import pal.tree.Tree;
/*     */ import pal.tree.TreeParseException;
/*     */ import pal.tree.TreeUtils;
/*     */ import pal.tree.UnconstrainedTree;
/*     */ 
/*     */ 
/*     */ public class TreeModel
/*     */   implements EvolutionaryModel
/*     */ {
/*     */   ParameterizedTree tree;
/*     */   final SubstitutionModel substM;
/*     */   SitePattern align;
/*     */   SitePattern translatedAlign;
/*     */   double range;
/*     */   TreeOptimizer lv;
/*  66 */   public Map collapsedNodes = new SmallMap();
/*     */   
/*     */   Alignment original;
/*     */   
/*     */   double[] collapseSimilar;
/*     */   
/*     */   String name;
/*     */   
/*  74 */   String treeType = "ScaledTree";
/*  75 */   boolean rebuildTree = false;
/*     */   Boolean flatPrior;
/*     */   double stdDTree;
/*     */   double stdDSubst;
/*     */   
/*     */   public ParameterizedTree getTree()
/*     */   {
/*  82 */     return this.tree;
/*     */   }
/*     */   
/*     */   public SubstitutionModel getSubstitutionModel() {
/*  86 */     return this.substM;
/*     */   }
/*     */   
/*     */   public Alignment getAlignment() {
/*  90 */     return this.align;
/*     */   }
/*     */   
/*     */ 
/*     */   public List split(boolean debug)
/*     */   {
/*     */     List l;
/*     */     
/*     */     List l;
/*     */     
/* 100 */     if (TreeUtils.getLeafIdGroup(this.tree).getIdCount() < this.collapseSimilar[1]) {
/* 101 */       l = Arrays.asList(new Object[] { this });
/*     */     } else {
/* 103 */       l = split(this.tree.getRoot(), (int)this.collapseSimilar[1], 3, debug);
/*     */     }
/*     */     
/* 106 */     return l;
/*     */   }
/*     */   
/*     */   private List split(Node root, int limit, int min_size, boolean debug)
/*     */   {
/* 111 */     int count = NodeUtils.getLeafCount(root);
/* 112 */     List l; if (count < limit) {
/* 113 */       List l = new ArrayList();
/*     */       try
/*     */       {
/* 116 */         Tree tree = new ReadTree(new PushbackReader(new StringReader(new SimpleTree(root).toString())));
/*     */         
/* 118 */         ParameterizedTree innerTree = 
/* 119 */           new LogParameterizedTree(new ScaledTree(tree, this.stdDTree * 5.0D));
/* 120 */         Alignment innerAlign = restrictAlignment(this.align, TreeUtils.getLeafIdGroup(innerTree), !debug);
/* 121 */         EvolutionaryModel evolM = 
/* 122 */           new EvolutionaryModel() {
/*     */             public ParameterizedTree getTree() {
/* 124 */               return this.val$innerTree;
/*     */             }
/*     */             
/* 127 */             public Alignment getAlignment() { return this.val$innerAlign; }
/*     */             
/*     */ 
/*     */ 
/* 131 */             public SubstitutionModel getSubstitutionModel() { return TreeModel.this.getSubstitutionModel(); }
/*     */             
/*     */             public void optimize() {}
/*     */             
/* 135 */             public String toString() { return new SimpleIdGroup(this.val$innerAlign).toString() + "\n";
/*     */             }
/* 137 */           };
/* 138 */           l.add(evolM);
/* 139 */         } catch (TreeParseException exc) { exc.printStackTrace();
/*     */         }
/*     */       } else {
/* 142 */         l = split(root.getChild(0), limit, min_size, debug);
/* 143 */         for (int i = 1; i < root.getChildCount(); i++) {
/* 144 */           l.addAll(split(root.getChild(i), limit, min_size, debug));
/*     */         }
/*     */       }
/*     */       
/* 148 */       return l;
/*     */     }
/*     */     
/*     */     public String toString()
/*     */     {
/* 153 */       return getClass().getName() + " " + new SimpleIdGroup(getAlignment()).toString() + "\n";
/*     */     }
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
/*     */     public TreeModel(Alignment align1, double[] freq, String rateMatrix, int no_rates, boolean gwF, double[] collapseSimilar, String name, double stdDTree, Tree originalTree, RateMatrix buildSubstM)
/*     */       throws AlignmentParseException, InvocationTargetException
/*     */     {
/* 172 */       this.stdDTree = stdDTree;
/* 173 */       this.flatPrior = new Boolean(true);
/* 174 */       this.rebuildTree = false;
/*     */       
/* 176 */       this.substM = SubstitutionModel.Utils.createSubstitutionModel(getRateMatrix(rateMatrix, gwF, freq), getRateDistribution(no_rates));
/* 177 */       this.original = align1;
/*     */       
/*     */ 
/* 180 */       this.collapseSimilar = collapseSimilar;
/* 181 */       this.name = name;
/* 182 */       initialize(originalTree == null ? null : new SimpleTree(originalTree), buildSubstM);
/* 183 */       if (freq.length != this.translatedAlign.getDataType().getNumStates())
/* 184 */         throw new AlignmentParseException("inconsistency between freqs and alignment " + freq.length + " " + 
/* 185 */           this.translatedAlign);
/* 186 */       if ((this.substM != null) && (this.translatedAlign.getDataType().getNumStates() != 
/* 187 */         this.substM.getDataType().getNumStates())) {
/* 188 */         throw new AlignmentParseException("substitution model incompatible with alignment " + this.substM.getDataType().getNumStates() + 
/* 189 */           " " + align1.getDataType().getNumStates());
/*     */       }
/*     */     }
/*     */     
/*     */     public TreeModel(Alignment align1, String rateMatrix, double[] collapseSimilar, String name) throws AlignmentParseException, InvocationTargetException
/*     */     {
/* 195 */       if (rateMatrix != null) this.substM = SubstitutionModel.Utils.createSubstitutionModel(
/* 196 */           getRateMatrix(rateMatrix, false, 
/* 197 */           AlignmentUtils.estimateFrequencies(align1)), 
/* 198 */           getRateDistribution(1)); else
/* 199 */         this.substM = null;
/* 200 */       if ((this.substM != null) && (!align1.getDataType().equals(this.substM.getDataType())))
/* 201 */         throw new AlignmentParseException("substitution model incompatible with alignment");
/* 202 */       this.original = align1;
/* 203 */       this.collapseSimilar = collapseSimilar;
/* 204 */       this.name = name;
/* 205 */       initialize(null, null);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     public TreeModel(EvolutionaryModel tm, double[] freq, double[] treeParameters, double[] substParameters)
/*     */       throws AlignmentParseException, InvocationTargetException
/*     */     {
/* 214 */       this(tm, freq, new double[] { 100.0D, 100.0D });
/* 215 */       if (this.tree.getNumParameters() != treeParameters.length) {
/* 216 */         throw new AlignmentParseException("inconsistency in number of parameters provided for tree ");
/*     */       }
/* 218 */       if (this.substM.getNumParameters() != substParameters.length) {
/* 219 */         throw new AlignmentParseException("inconsistency in number of parameters provided for subst model ");
/*     */       }
/* 221 */       for (int i = 0; i < treeParameters.length; i++) {
/* 222 */         this.tree.setParameter(treeParameters[i], i);
/*     */       }
/* 224 */       for (int i = 0; i < substParameters.length; i++) {
/* 225 */         this.substM.setParameter(substParameters[i], i);
/*     */       }
/*     */     }
/*     */     
/*     */     public TreeModel(EvolutionaryModel tm, double[] freq, Alignment align, double[] stdD) throws AlignmentParseException, InvocationTargetException
/*     */     {
/* 231 */       this(tm, freq, new double[] { 100.0D, 100.0D });
/* 232 */       if (!align.getDataType().equals(this.substM.getDataType())) throw new AlignmentParseException("inconsistency in alignment provided");
/* 233 */       this.original = align;
/* 234 */       initialize(tm.getTree(), null);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     public TreeModel(EvolutionaryModel tm, double[] freq, double[] stdD)
/*     */       throws InvocationTargetException
/*     */     {
/* 248 */       this.stdDTree = 100.0D;
/*     */       
/*     */ 
/* 251 */       this.flatPrior = new Boolean(false);
/* 252 */       this.rebuildTree = false;
/*     */       
/* 254 */       this.tree = new LogParameterizedTree(new ScaledTree(tm.getTree(), this.stdDTree * 5.0D));
/*     */       
/* 256 */       RateMatrix oldRM = tm.getSubstitutionModel().getRateMatrix();
/* 257 */       String[] rmName = oldRM.getUniqueName().split("[_@]");
/*     */       
/* 259 */       RateMatrix newRM = getRateMatrix(rmName[0], rmName.length > 2, freq);
/*     */       
/* 261 */       for (int i = 0; i < oldRM.getNumParameters(); i++) {
/* 262 */         newRM.setParameter(oldRM.getParameter(i), i);
/*     */       }
/* 264 */       if ((newRM instanceof WAG_GWF)) {
/* 265 */         RateMatrix newerRM = new OffsetGWFRateMatrix((WAG_GWF)newRM, 0.0D, stdD[1] * 5.0D);
/*     */         
/* 267 */         this.substM = SubstitutionModel.Utils.createSubstitutionModel(newerRM, new UniformRate());
/*     */       }
/*     */       else
/*     */       {
/* 271 */         this.substM = SubstitutionModel.Utils.createSubstitutionModel(newRM, new UniformRate());
/*     */       }
/*     */     }
/*     */     
/*     */     public boolean initialize(Tree originalTree1, RateMatrix buildSubstM)
/*     */       throws AlignmentParseException
/*     */     {
/* 278 */       Tree originalTree = null;
/* 279 */       if ((originalTree1 != null) && (TreeUtils.getLeafIdGroup(originalTree1).getIdCount() > this.original.getIdCount())) {
/* 280 */         originalTree = new SimpleTree(originalTree1);
/* 281 */         Set nodesS = new HashSet();
/* 282 */         for (int i = 0; i < originalTree.getExternalNodeCount(); i++) {
/* 283 */           if (this.original.whichIdNumber(originalTree.getExternalNode(i).getIdentifier().getName()) < 0)
/* 284 */             nodesS.add(originalTree.getExternalNode(i));
/*     */         }
/* 286 */         Node[] nodes = new Node[nodesS.size()];
/* 287 */         nodesS.toArray(nodes);
/*     */         
/* 289 */         AlignUtils.removeNodes(originalTree, nodes);
/*     */       }
/*     */       
/* 292 */       if (originalTree == null) { SitePattern sp;
/*     */         SitePattern sp;
/* 294 */         if ((this.substM != null) && (!this.substM.getDataType().getClass().getName().equals(this.original.getDataType().getClass().getName())))
/*     */         {
/*     */ 
/* 297 */           sp = AlignUtils.getDNAAlignmentFromCodons(this.original);
/*     */         }
/*     */         else {
/* 300 */           sp = SitePattern.getSitePattern(this.original);
/*     */         }
/*     */         
/* 303 */         DistanceMatrix dm = 
/* 304 */           new AlignmentDistanceMatrix(sp, 
/*     */           
/* 306 */           buildSubstM == null ? null : SubstitutionModel.Utils.createSubstitutionModel(buildSubstM));
/*     */         
/*     */ 
/* 309 */         dm = AlignUtils.collapseSimilar(dm, Math.max(this.collapseSimilar[0], 1.0E-5D), 
/* 310 */           this.name, this.collapsedNodes);
/*     */         
/*     */ 
/*     */ 
/* 314 */         if (dm.getIdCount() < 2) { throw new AlignmentParseException("dm count too small to continue");
/*     */         }
/*     */         
/*     */ 
/*     */ 
/* 319 */         if (dm.getIdCount() == 2) {
/* 320 */           double dist = dm.getDistance(0, 1);
/* 321 */           Node root = new SimpleNode();
/* 322 */           Node child1 = new SimpleNode(dm.getIdentifier(0).getName(), dist / 2.0D);
/* 323 */           Node child2 = new SimpleNode(dm.getIdentifier(1).getName(), dist / 2.0D);
/* 324 */           root.addChild(child1);child1.setParent(root);
/* 325 */           root.addChild(child2);child2.setParent(root);
/* 326 */           originalTree = new SimpleTree(root);
/*     */         }
/*     */         else
/*     */         {
/* 330 */           originalTree = AlignUtils.neighbourJoiningTree(dm);
/*     */         }
/*     */       }
/*     */       
/*     */ 
/* 335 */       this.tree = getParameterizedTree(originalTree, this.treeType, this.stdDTree * 5.0D);
/*     */       
/* 337 */       this.align = SitePattern.getSitePattern(restrictAlignment(this.original, TreeUtils.getLeafIdGroup(this.tree), false));
/* 338 */       if (this.align.getDataType().equals(Codons.DEFAULT_INSTANCE)) {
/* 339 */         this.translatedAlign = SitePattern.getSitePattern(AlignUtils.translate(this.align));
/*     */       }
/*     */       else {
/* 342 */         this.translatedAlign = this.align;
/*     */       }
/* 344 */       return true;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     public static double[] getParams(Parameterized params)
/*     */     {
/* 351 */       double[] oldParams = new double[params.getNumParameters()];
/* 352 */       for (int i = 0; i < oldParams.length; i++) {
/* 353 */         oldParams[i] = params.getParameter(i);
/*     */       }
/* 355 */       return oldParams;
/*     */     }
/*     */     
/*     */     public void optimize()
/*     */     {
/* 360 */       if ((this.substM == null) || (this.tree == null) || (this.align == null)) throw new NullPointerException("something is null");
/* 361 */       this.lv = new TreeOptimizer(this.translatedAlign, this.substM, this.tree, this.flatPrior);
/* 362 */       this.lv.optimize();
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     private static ParameterizedTree getParameterizedTree(Tree tree, String treeType, double range)
/*     */     {
/* 369 */       if (treeType.equals("ScaledTree")) {
/* 370 */         return new LogParameterizedTree(new ScaledTree(tree, range));
/*     */       }
/* 372 */       if (treeType.equals("UnconstrainedTree"))
/*     */       {
/* 374 */         return new LogParameterizedTree(new UnconstrainedTree(tree));
/*     */       }
/*     */       
/*     */       try
/*     */       {
/* 379 */         Class clazz = Class.forName("pal.tree." + treeType);
/* 380 */         Constructor constr = clazz.getConstructor(new Class[] { tree.getClass() });
/* 381 */         return new LogParameterizedTree((ParameterizedTree)constr.newInstance(new Object[] { tree }));
/*     */       } catch (Throwable t) {
/* 383 */         t.printStackTrace(); } return null;
/*     */     }
/*     */     
/*     */ 
/*     */     public static RateMatrix getNullYangCodonModel(double omega, double kappa)
/*     */     {
/* 389 */       double[] freqs = new double[64];
/* 390 */       double fill = 0.015625D;
/* 391 */       Arrays.fill(freqs, fill);
/* 392 */       return new YangCodonModel(omega, kappa, freqs);
/*     */     }
/*     */     
/*     */     public static RateMatrix getRateMatrix(String rateMatrix, boolean gwF, double[] freq) throws InvocationTargetException
/*     */     {
/*     */       try {
/* 398 */         if (rateMatrix.indexOf("Yang") != -1) {
/* 399 */           double omega = 1.0D;
/* 400 */           double kappa = 2.0D;
/* 401 */           return new YangCodonModel(omega, kappa, freq);
/*     */         }
/*     */         
/*     */ 
/* 405 */         rateMatrix = "pal.substmodel." + rateMatrix;
/* 406 */         Class clazz = Class.forName(rateMatrix);
/* 407 */         Constructor constr = clazz.getConstructor(new Class[] { freq.getClass() });
/* 408 */         RateMatrix aModel = (AminoAcidModel)constr.newInstance(new Object[] { freq });
/*     */         
/* 410 */         if (gwF) {
/*     */           try {
/* 412 */             aModel = new WAG_GWF(new double[] { 0.0D }, freq);
/* 413 */             aModel.setParameter(0.5D, aModel.getNumParameters() - 1);
/*     */           }
/*     */           catch (ArithmeticException e)
/*     */           {
/* 417 */             aModel = (AminoAcidModel)constr.newInstance(new Object[] { freq });
/*     */           }
/*     */         }
/*     */         
/* 421 */         return aModel;
/*     */       } catch (Throwable t) {
/* 423 */         t.printStackTrace(); } return null;
/*     */     }
/*     */     
/*     */     public static RateDistribution getRateDistribution(int n)
/*     */     {
/* 428 */       if (n > 1) {
/* 429 */         return new GammaRates(n, 1.0D);
/*     */       }
/*     */       
/* 432 */       return new UniformRate();
/*     */     }
/*     */     
/*     */     public static Alignment restrictAlignment(Alignment al, IdGroup leafIDs, boolean removeGaps)
/*     */     {
/* 437 */       return restrictAlignment(al, leafIDs, removeGaps, 0, al.getSiteCount() - 1);
/*     */     }
/*     */     
/*     */     public static Alignment trimToMatchMainString(Alignment align, String input) {
/* 441 */       int id = align.whichIdNumber(input);
/* 442 */       System.err.println(input);
/*     */       
/*     */ 
/* 445 */       if (id >= 0) {
/* 446 */         StrippedAlignment align1 = new StrippedAlignment(align);
/* 447 */         for (int i = 0; i < align.getSiteCount(); i++) {
/* 448 */           if ((align.getData(id, i) == '?') || (align.getData(id, i) == '-')) align1.dropSite(i);
/*     */         }
/* 450 */         align1.setDataType(align.getDataType());
/* 451 */         return align1;
/*     */       }
/* 453 */       return align;
/*     */     }
/*     */     
/*     */ 
/*     */     public static Alignment restrictAlignment(Alignment coreAlign, IdGroup leafIDs, boolean removeGaps, int start, int end)
/*     */     {
/* 459 */       Identifier[] ids = new Identifier[leafIDs.getIdCount()];
/* 460 */       String[] seqs = new String[leafIDs.getIdCount()];
/* 461 */       for (int i = 0; i < ids.length; i++) {
/* 462 */         Identifier id = leafIDs.getIdentifier(i);
/* 463 */         int id_j = coreAlign.whichIdNumber(id.getName());
/*     */         
/*     */ 
/*     */ 
/*     */ 
/* 468 */         ids[i] = id;
/* 469 */         seqs[i] = 
/* 470 */           coreAlign.getAlignedSequenceString(id_j)
/* 471 */           .substring(start, end + 1);
/*     */       }
/* 473 */       Alignment al1 = new SimpleAlignment(ids, seqs, "-", coreAlign.getDataType());
/* 474 */       if (removeGaps) {
/* 475 */         Alignment nullAlign = null;
/* 476 */         StrippedAlignment al2 = new StrippedAlignment(al1);
/* 477 */         al2.removeConstantSites('?');
/* 478 */         ConcatenatedAlignment al3 = new ConcatenatedAlignment(new Alignment[] { nullAlign, al2, nullAlign });
/* 479 */         al3.setDataType(al2.getDataType());
/* 480 */         al1 = al3;
/*     */       }
/* 482 */       return al1;
/*     */     }
/*     */     
/*     */     public static Alignment stripAlignmentOfParentGaps(Alignment align1, String parent)
/*     */     {
/* 487 */       StrippedAlignment alignS = new StrippedAlignment(align1);
/* 488 */       String s = align1.getAlignedSequenceString(align1.whichIdNumber(parent));
/* 489 */       for (int i = 0; i < s.length(); i++) {
/* 490 */         if (AminoAcids.DEFAULT_INSTANCE.getState(s.charAt(i)) >= 20) {
/* 491 */           alignS.dropSite(i);
/*     */         }
/*     */       }
/* 494 */       return alignS;
/*     */     }
/*     */     
/*     */ 
/*     */     public static class TreeOptimizer
/*     */     {
/*     */       final LikelihoodValue lhc;
/*     */       
/* 502 */       MultivariateMinimum mvm = new OrthogonalSearch();
/*     */       PriorDistribution prior;
/*     */       final int tree_params;
/*     */       final int subst_params;
/*     */       ParameterizedTree tree;
/*     */       SubstitutionModel substM;
/*     */       
/*     */       public TreeOptimizer(SitePattern translatedAlign, SubstitutionModel substM, ParameterizedTree tree) {
/* 510 */         this(translatedAlign, substM, tree, Boolean.FALSE);
/*     */       }
/*     */       
/*     */       TreeOptimizer(SitePattern translatedAlign, SubstitutionModel substM, ParameterizedTree tree, Boolean flatPrior) {
/* 514 */         this.tree = tree;
/* 515 */         this.substM = substM;
/*     */         
/* 517 */         this.lhc = new LikelihoodValue(translatedAlign);
/* 518 */         this.lhc.setModel(substM);
/* 519 */         this.lhc.setTree(tree);
/* 520 */         this.tree_params = tree.getNumParameters();
/* 521 */         this.subst_params = substM.getNumParameters();
/*     */         
/* 523 */         double[] std_dev = new double[this.tree_params + this.subst_params];
/* 524 */         for (int i = 0; i < this.tree_params; i++) {
/* 525 */           std_dev[i] = ((tree.getUpperLimit(i) - tree.getLowerLimit(i)) / 10.0D);
/*     */         }
/* 527 */         for (int i = 0; i < this.subst_params; i++) {
/* 528 */           std_dev[(i + this.tree_params)] = ((substM.getUpperLimit(i) - substM.getLowerLimit(i)) / 10.0D);
/*     */         }
/* 530 */         if (!flatPrior.booleanValue()) this.prior = new PriorDistribution(std_dev);
/*     */       }
/*     */       
/*     */       public void optimize() {
/* 534 */         ParameterizedTree tree1 = this.tree;
/* 535 */         MultivariateFunction mvf = new TreeModel.1(this, tree1);
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
/* 572 */         double[] xvec = new double[mvf.getNumArguments()];
/* 573 */         for (int i = 0; i < this.tree.getNumParameters(); i++) {
/* 574 */           xvec[i] = this.tree.getParameter(i);
/*     */         }
/* 576 */         for (int i = 0; i < this.substM.getNumParameters(); i++) {
/* 577 */           xvec[(i + this.tree_params)] = this.substM.getParameter(i);
/*     */         }
/* 579 */         System.out.println("Before " + Print.toString(xvec));
/* 580 */         double res = this.mvm.findMinimum(mvf, xvec, 2, 2);
/* 581 */         System.out.println("After " + Print.toString(xvec));
/* 582 */         for (int i = 0; i < this.tree_params; i++) {
/* 583 */           this.tree.setParameter(xvec[i], i);
/*     */         }
/* 585 */         for (int i = 0; i < this.subst_params; i++)
/*     */         {
/* 587 */           this.substM.setParameter(xvec[(i + this.tree_params)], i);
/*     */         }
/*     */       }
/*     */     }
/*     */   }


/* Location:              /home/lachlan/Desktop/psilc1.21/psilc.jar!/lc1/phyl/TreeModel.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */