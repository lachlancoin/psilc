/*      */ package lc1.phyl;
/*      */ 
/*      */ import forester.atv.ATVjframe;
/*      */ import forester.tree.TreeHelper;
/*      */ import java.io.BufferedOutputStream;
/*      */ import java.io.BufferedWriter;
/*      */ import java.io.File;
/*      */ import java.io.FileNotFoundException;
/*      */ import java.io.FileOutputStream;
/*      */ import java.io.FileWriter;
/*      */ import java.io.IOException;
/*      */ import java.io.PrintStream;
/*      */ import java.io.PrintWriter;
/*      */ import java.io.PushbackReader;
/*      */ import java.io.StringReader;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Arrays;
/*      */ import java.util.Enumeration;
/*      */ import java.util.HashMap;
/*      */ import java.util.HashSet;
/*      */ import java.util.Iterator;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Set;
/*      */ import java.util.Vector;
/*      */ import lc1.pfam.PfamAlignmentParser;
/*      */ import lc1.util.Print;
/*      */ import org.biojava.bio.dist.Distribution;
/*      */ import pal.alignment.Alignment;
/*      */ import pal.alignment.AlignmentParseException;
/*      */ import pal.alignment.AlignmentUtils;
/*      */ import pal.alignment.BootstrappedAlignment;
/*      */ import pal.alignment.ReadAlignment;
/*      */ import pal.alignment.SimpleAlignment;
/*      */ import pal.alignment.SitePattern;
/*      */ import pal.datatype.AminoAcids;
/*      */ import pal.datatype.Codons;
/*      */ import pal.datatype.DataType;
/*      */ import pal.distance.AlignmentDistanceMatrix;
/*      */ import pal.distance.DistanceMatrix;
/*      */ import pal.eval.ChiSquareValue;
/*      */ import pal.eval.LikelihoodValue;
/*      */ import pal.eval.ModelParameters;
/*      */ import pal.misc.IdGroup;
/*      */ import pal.misc.Identifier;
/*      */ import pal.misc.LabelMapping;
/*      */ import pal.misc.SimpleIdGroup;
/*      */ import pal.substmodel.SubstitutionModel;
/*      */ import pal.substmodel.SubstitutionModel.Utils;
/*      */ import pal.substmodel.WAG;
/*      */ import pal.tree.NeighborJoiningTree;
/*      */ import pal.tree.NodeUtils;
/*      */ import pal.tree.ParameterizedTree;
/*      */ import pal.tree.ReadTree;
/*      */ import pal.tree.SimpleTree;
/*      */ import pal.tree.TreeGenerator;
/*      */ import pal.tree.TreeParseException;
/*      */ import pal.tree.TreeUtils;
/*      */ import pal.util.AlgorithmCallback;
/*      */ import pal.util.AlgorithmCallback.Utils;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class MaxLikelihoodTree
/*      */ {
/*      */   public static final double A = 1.0D;
/*      */   public static final double F = 0.0D;
/*      */   public static final int N = 4;
/*      */   
/*      */   public static double[] addCodonFreqs(double[] codonFreqs)
/*      */   {
/*   87 */     double[] dnaFreqs = new double[4];
/*   88 */     Arrays.fill(dnaFreqs, 0.0D);
/*   89 */     for (int j = 0; j < codonFreqs.length; j++) {
/*   90 */       int[] states = Codons.getNucleotideStatesFromCodonIndex(j);
/*   91 */       for (int i = 0; i < states.length; i++) {
/*   92 */         dnaFreqs[states[i]] += codonFreqs[j] / 3.0D;
/*      */       }
/*      */     }
/*   95 */     AlignUtils.checkSumNormal(dnaFreqs);
/*   96 */     return dnaFreqs;
/*      */   }
/*      */   
/*      */   public static Alignment alFromSitePatterns(IdGroup idg1, List siteP, boolean reverse) {
/*  100 */     IdGroup idg = new SimpleIdGroup(idg1);
/*  101 */     char[][] seqs = new char[idg.getIdCount()][siteP.size()];
/*  102 */     for (int k = 0; k < siteP.size(); k++) {
/*  103 */       int j = k;
/*  104 */       if (reverse) {
/*  105 */         j = siteP.size() - k - 1;
/*      */       }
/*  107 */       SitePattern sitePj = (SitePattern)siteP.get(j);
/*  108 */       if (sitePj == null) {
/*  109 */         for (int i = 0; i < idg.getIdCount(); i++) {
/*  110 */           int tmp85_83 = k; char[] tmp85_82 = seqs[i];tmp85_82[tmp85_83] = ((char)(tmp85_82[tmp85_83] + '-'));
/*      */         }
/*      */         
/*      */       } else {
/*  114 */         for (int i = 0; i < idg.getIdCount(); i++) {
/*  115 */           int tmp122_120 = k; char[] tmp122_119 = seqs[i];tmp122_119[tmp122_120] = ((char)(tmp122_119[tmp122_120] + sitePj.getData(i, 0)));
/*      */         }
/*      */       }
/*      */     }
/*  119 */     return new SimpleAlignment(idg, seqs, "?", ((SitePattern)siteP.get(0)).getDataType());
/*      */   }
/*      */   
/*      */   public static pal.tree.Tree appendCharacter(pal.tree.Tree tree, SitePattern sp) {
/*  123 */     LabelMapping lm = new LabelMapping();
/*  124 */     IdGroup leafIds = TreeUtils.getLeafIdGroup(tree);
/*  125 */     for (int i = 0; i < leafIds.getIdCount(); i++) {
/*  126 */       Identifier id = leafIds.getIdentifier(i);
/*  127 */       int j = sp.whichIdNumber(id.getName());
/*  128 */       lm.addMapping(id, id.getName() + "/" + sp.getData(j, 0));
/*      */     }
/*  130 */     return new SimpleTree(tree, lm);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static void checkSum(double[] codons)
/*      */     throws ArithmeticException
/*      */   {
/*  175 */     double sum = 0.0D;
/*  176 */     for (int j = 0; j < codons.length; j++)
/*      */     {
/*  178 */       sum += StrictMath.exp(codons[j]);
/*  179 */       if (Double.isNaN(codons[j])) { throw new ArithmeticException("nan :" + Print.toString(codons));
/*      */       }
/*      */     }
/*  182 */     if (StrictMath.abs(1.0D - sum) > 0.01D) { throw new ArithmeticException("something gone wrong " + sum);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static void chiSquareTree(DistanceMatrix dm, ParameterizedTree tree)
/*      */   {
/*  192 */     System.out.println("Creating chi-square tree using alignment and tree (without branch length info)");
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  198 */     ChiSquareValue csv = new ChiSquareValue(dm, true);
/*  199 */     csv.setTree(tree);
/*      */     
/*  201 */     csv.optimiseParameters();
/*      */   }
/*      */   
/*      */ 
/*      */   static void connectNode(Set nodes, pal.tree.Node leaf)
/*      */   {
/*  207 */     pal.tree.Node parent = leaf.getParent();
/*  208 */     pal.tree.Node prev_parent = leaf;
/*  209 */     double cumBranchLength = leaf.getBranchLength();
/*  210 */     while (!nodes.contains(parent)) {
/*  211 */       prev_parent = parent;
/*  212 */       cumBranchLength += parent.getBranchLength();
/*  213 */       parent = parent.getParent();
/*      */     }
/*  215 */     if (prev_parent.equals(parent.getChild(0))) {
/*  216 */       parent.setChild(0, leaf);
/*      */     }
/*      */     else {
/*  219 */       parent.setChild(1, leaf);
/*      */     }
/*  221 */     leaf.setParent(parent);
/*  222 */     leaf.setBranchLength(cumBranchLength);
/*      */   }
/*      */   
/*      */   static void connectNodes(Set nodes, pal.tree.Node root)
/*      */   {
/*  227 */     Iterator it = nodes.iterator();
/*  228 */     while (it.hasNext()) {
/*  229 */       pal.tree.Node n = (pal.tree.Node)it.next();
/*  230 */       if (!n.equals(root)) {
/*  231 */         connectNode(nodes, n);
/*      */       }
/*      */     }
/*  234 */     for (int i = 0; i < root.getChildCount(); i++) {
/*  235 */       if (!nodes.contains(root.getChild(i))) {
/*  236 */         root.removeChild(i);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   public static pal.tree.Tree convert(forester.tree.Tree treeF) throws TreeParseException {
/*  242 */     pal.tree.Tree tree = new ReadTree(new PushbackReader(new StringReader(treeF.toNewHampshire(false))));
/*      */     
/*  244 */     forester.tree.Node n = treeF.getExtNode0();
/*  245 */     while (n != null)
/*      */     {
/*  247 */       String name = n.getSeqName();
/*  248 */       pal.tree.Node node = TreeUtils.getNodeByName(tree, name);
/*  249 */       String species = n.getSpecies();
/*  250 */       tree.setAttribute(node, "Species", species);
/*  251 */       n = n.getNextExtNode();
/*      */     }
/*      */     
/*  254 */     return tree;
/*      */   }
/*      */   
/*      */   public static forester.tree.Tree convert(pal.tree.Tree tree1) throws Exception {
/*  258 */     File f = new File("tmp12345567" + System.currentTimeMillis());
/*  259 */     f.deleteOnExit();
/*  260 */     PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(f)));
/*  261 */     TreeUtils.printNH(tree1, pw, true, true);
/*  262 */     pw.close();
/*  263 */     return TreeHelper.readNHtree(f);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected static double[] getBackGroundFrequencies(Alignment[] als)
/*      */   {
/*  274 */     double[] bFreq = AlignmentUtils.estimateFrequencies(als[0]);
/*      */     
/*  276 */     for (int i = 1; i < als.length; i++)
/*      */     {
/*      */ 
/*  279 */       double[] newFreq = AlignmentUtils.estimateFrequencies(als[i]);
/*      */       
/*  281 */       for (int j = 0; j < bFreq.length; j++)
/*      */       {
/*  283 */         bFreq[j] += newFreq[i];
/*      */       }
/*      */     }
/*      */     
/*  287 */     return bFreq;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static double[] getBranchLengths(pal.tree.Tree tree)
/*      */   {
/*      */     try
/*      */     {
/*  303 */       int no_nodes = tree.getExternalNodeCount() + 
/*  304 */         tree.getInternalNodeCount();
/*  305 */       double[] lengths = new double[no_nodes - 1];
/*  306 */       pal.tree.Node current = tree.getExternalNode(0);
/*  307 */       int i = 0;
/*      */       
/*  309 */       while (!current.isRoot())
/*      */       {
/*      */ 
/*  312 */         lengths[i] = current.getBranchLength();
/*  313 */         i++;
/*  314 */         current = NodeUtils.postorderSuccessor(current);
/*      */       }
/*      */       
/*  317 */       return lengths;
/*      */     }
/*      */     catch (Exception ise)
/*      */     {
/*  321 */       ise.printStackTrace(System.err);
/*      */     }
/*  323 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   static double getDistanceToAncestor(int id, int ancestorId, pal.tree.Tree tree)
/*      */   {
/*  353 */     int numLeaves = tree.getExternalNodeCount();
/*  354 */     pal.tree.Node n = id >= numLeaves ? tree.getInternalNode(id - numLeaves) : tree.getExternalNode(id);
/*  355 */     pal.tree.Node ancestor = tree.getInternalNode(ancestorId - numLeaves);
/*  356 */     double dist = 0.0D;
/*  357 */     while (!n.equals(ancestor)) {
/*  358 */       dist += n.getBranchLength();
/*  359 */       n = n.getParent();
/*      */     }
/*      */     
/*  362 */     return dist;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   static double getLikelihood(pal.tree.Tree tree, Alignment align, Distribution frequency)
/*      */     throws LikelihoodCalcException
/*      */   {
/*  381 */     return getLikelihood(tree, align, AlignUtils.getInsertModel(frequency, align.getDataType(), false, 2.0D));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static double getLikelihood(pal.tree.Tree tree, Alignment align, SubstitutionModel sMod)
/*      */     throws LikelihoodCalcException
/*      */   {
/*  397 */     if (tree.getExternalNodeCount() != align.getIdCount()) throw new LikelihoodCalcException("Number of nodes in tree must match number of nodes in alignment: " + tree.getExternalNodeCount() + " vs " + align.getIdCount());
/*  398 */     SitePattern sp = SitePattern.getSitePattern(align);
/*  399 */     LikelihoodValue lv = new LikelihoodValue(sp);
/*  400 */     lv.setTree(tree);
/*  401 */     lv.setModel(sMod);
/*      */     
/*      */ 
/*      */ 
/*  405 */     return lv.compute() / StrictMath.log(2.0D);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   static Set getNodes(pal.tree.Node[] leaves, pal.tree.Node root)
/*      */   {
/*  412 */     Set keptNodes = new HashSet();
/*  413 */     List leavesL = Arrays.asList(leaves);
/*  414 */     includedDescendants(leavesL, keptNodes, root);
/*  415 */     return keptNodes;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public static Iterator getScaledTrees(Iterator treeIterator, double[] scales)
/*      */   {
/*  422 */     new Iterator() {
/*      */       Object alignment;
/*      */       int j;
/*      */       Object[] object;
/*      */       pal.tree.Tree tree;
/*      */       
/*      */       public boolean hasNext() {
/*  429 */         return (this.j < MaxLikelihoodTree.this.length) || (this.val$treeIterator.hasNext());
/*      */       }
/*      */       
/*      */       public Object next() {
/*  433 */         if (this.j == MaxLikelihoodTree.this.length) {
/*  434 */           this.object = ((Object[])this.val$treeIterator.next());
/*  435 */           this.tree = ((pal.tree.Tree)this.object[0]);
/*  436 */           this.alignment = this.object[1];
/*  437 */           pal.tree.Tree tree1 = this.tree;
/*  438 */           this.j = 0;
/*      */         }
/*  440 */         pal.tree.Tree tree1 = TreeUtils.scale(new SimpleTree(this.tree), MaxLikelihoodTree.this[this.j], 1);
/*  441 */         Identifier id = this.tree.getRoot().getIdentifier();
/*  442 */         this.tree.getRoot().setIdentifier(new Identifier(id.getName() + "_" + this.j));
/*  443 */         this.j += 1;
/*  444 */         return new Object[] { tree1, this.alignment };
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       public void remove() {}
/*      */     };
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static void getTreeFromAlignment(File in_file, String[] out, String substModel, boolean graph, int repeats)
/*      */     throws FileNotFoundException
/*      */   {
/*      */     try
/*      */     {
/*  473 */       Alignment align = AlignUtils.translate(AlignUtils.getCodonAlignmentFromDNA(new ReadAlignment(in_file.getAbsolutePath())));
/*  474 */       SubstitutionModel substM = SubstitutionModel.Utils.createSubstitutionModel(
/*  475 */         new WAG(AlignmentUtils.estimateFrequencies(align)));
/*      */       
/*      */ 
/*  478 */       DistanceMatrix dm = new AlignmentDistanceMatrix(SitePattern.getSitePattern(align), substM);
/*  479 */       pal.tree.Tree baseTree = new NeighborJoiningTree(dm);
/*  480 */       BootstrappedAlignment alignB = new BootstrappedAlignment(SitePattern.getSitePattern(align));
/*  481 */       TreeGenerator tg = new TreeGenerator() {
/*      */         public pal.tree.Tree getNextTree(pal.tree.Tree baseTree, AlgorithmCallback cb) {
/*  483 */           MaxLikelihoodTree.this.bootstrap();
/*  484 */           return new NeighborJoiningTree(new AlignmentDistanceMatrix(SitePattern.getSitePattern(MaxLikelihoodTree.this), this.val$substM));
/*      */         }
/*  486 */       };
/*  487 */       pal.tree.Tree tree = TreeUtils.getReplicateCladeSupport("B", baseTree, tg, 
/*  488 */         repeats, AlgorithmCallback.Utils.getNullCallback());
/*      */       
/*  490 */       graphTree(tree);
/*      */ 
/*      */ 
/*      */ 
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/*      */ 
/*      */ 
/*  499 */       exc.printStackTrace();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public static void graphTree(forester.tree.Tree tree)
/*      */   {
/*      */     try
/*      */     {
/*  510 */       new ATVjframe(tree).showWhole();
/*      */     }
/*      */     catch (Throwable t)
/*      */     {
/*  514 */       t.printStackTrace();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public static void graphTree(forester.tree.Tree tree1, Iterator it)
/*      */   {
/*  523 */     Map ids = new HashMap();
/*  524 */     for (Enumeration en = tree1.getRoot().getAllExternalChildren().elements(); en.hasMoreElements();) {
/*  525 */       forester.tree.Node node = (forester.tree.Node)en.nextElement();
/*  526 */       ids.put(node.getSeqName(), node);
/*      */     }
/*  528 */     while (it.hasNext()) {
/*  529 */       List row = (List)it.next();
/*  530 */       if (ids.containsKey(row.get(0))) {
/*  531 */         ((forester.tree.Node)ids.get(row.get(0))).setSpecies((String)row.get(2));
/*      */       }
/*      */     }
/*  534 */     graphTree(tree1);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static void graphTree(pal.tree.Tree tree1)
/*      */   {
/*      */     try
/*      */     {
/*  547 */       graphTree(convert(tree1));
/*      */     }
/*      */     catch (Throwable t)
/*      */     {
/*  551 */       t.printStackTrace();
/*      */     }
/*      */   }
/*      */   
/*      */   static boolean includedDescendants(List leaves, Set keptNodes, pal.tree.Node node)
/*      */   {
/*  557 */     if ((node.isLeaf()) || (node.getChildCount() < 2)) {
/*  558 */       if (leaves.contains(node)) {
/*  559 */         keptNodes.add(node);
/*  560 */         return true;
/*      */       }
/*  562 */       return false;
/*      */     }
/*  564 */     boolean c1 = includedDescendants(leaves, keptNodes, node.getChild(0));
/*  565 */     boolean c2 = includedDescendants(leaves, keptNodes, node.getChild(1));
/*  566 */     if ((c1) && (c2)) {
/*  567 */       keptNodes.add(node);
/*  568 */       return true;
/*      */     }
/*  570 */     if ((c1) || (c2)) {
/*  571 */       return true;
/*      */     }
/*      */     
/*  574 */     return false;
/*      */   }
/*      */   
/*      */   public static void main(String[] args)
/*      */     throws Exception
/*      */   {
/*  580 */     graphTree(TreeHelper.readNHtree(new File(args[0])));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public static void main1(String[] args)
/*      */     throws AlignmentParseException, FileNotFoundException, IOException
/*      */   {
/*  588 */     Alignment align = new ReadAlignment(args[0]);
/*  589 */     PrintStream ps = new PrintStream(new BufferedOutputStream(new FileOutputStream(new File(args[1]))));
/*  590 */     for (int i = 0; i < align.getIdCount(); i++) {
/*  591 */       ps.println(align.getIdentifier(i) + "\t" + align.getAlignedSequenceString(i));
/*      */     }
/*      */     
/*  594 */     ps.close();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static void makePositionTree(pal.tree.Tree tree, SitePattern sp)
/*      */   {
/*  699 */     for (int i = 0; i < tree.getExternalNodeCount(); i++) {
/*  700 */       pal.tree.Node n = tree.getExternalNode(i);
/*  701 */       char c = sp.getData(sp.whichIdNumber(n.getIdentifier().getName()), 0);
/*  702 */       tree.setAttribute(n, "Species", c);
/*      */     }
/*      */   }
/*      */   
/*      */   static int[] modifyStartEnd(int start, int end, Alignment al) {
/*  707 */     while (start < al.getSiteCount()) {
/*  708 */       for (int i = 0; i < al.getIdCount(); i++) {
/*  709 */         if (al.getData(i, start) == 'X') {
/*  710 */           start++;
/*  711 */           break;
/*      */         }
/*      */       }
/*      */       
/*  715 */       break;
/*      */     }
/*  717 */     while (end > 0) {
/*  718 */       for (int i = 0; i < al.getIdCount(); i++) {
/*  719 */         if (al.getData(i, end) == 'X') {
/*  720 */           end--;
/*  721 */           break;
/*      */         }
/*      */       }
/*      */       
/*  725 */       break;
/*      */     }
/*  727 */     return new int[] { start, end };
/*      */   }
/*      */   
/*      */   public static int[] nonGaps(Alignment al)
/*      */   {
/*  732 */     int[] nonG = new int[al.getSiteCount()];
/*  733 */     int k = 0;
/*  734 */     for (int i = 0; i < al.getSiteCount(); i++) {
/*  735 */       for (int j = 0; j < al.getIdCount(); j++) {
/*  736 */         if (!AminoAcids.DEFAULT_INSTANCE.isUnknownChar(al.getData(j, i))) {
/*  737 */           nonG[k] = i;
/*  738 */           k++;
/*  739 */           break;
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*  744 */     int[] res = new int[k];
/*      */     
/*  746 */     System.arraycopy(nonG, 0, res, 0, k);
/*  747 */     return res;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static Alignment[] parse(String[] files, int len)
/*      */   {
/*  842 */     Alignment[] results = new Alignment[len];
/*      */     
/*  844 */     for (int i = 0; i < len; i++) {
/*      */       try
/*      */       {
/*  847 */         results[i] = PfamAlignmentParser.parse(new File(files[i]), "ALN");
/*      */       }
/*      */       catch (Exception ise)
/*      */       {
/*  851 */         ise.printStackTrace(System.err);
/*      */       }
/*      */     }
/*  854 */     return results;
/*      */   }
/*      */   
/*      */ 
/*      */   public static Alignment randomize(Alignment al, boolean colsOnly, boolean skipStartEnd, boolean replacement, int no_columns)
/*      */   {
/*  860 */     int start = 0;
/*  861 */     int end = al.getSiteCount() - 1;
/*  862 */     if (skipStartEnd) {
/*  863 */       int[] stEnd = modifyStartEnd(start, end, al);
/*  864 */       start = stEnd[0];
/*  865 */       end = stEnd[1];
/*      */     }
/*      */     
/*  868 */     int length = StrictMath.min(no_columns, end - start);
/*  869 */     int[] rel = randomize(length, end - start, replacement);
/*      */     
/*  871 */     List lab = new ArrayList();
/*  872 */     List seqs = new ArrayList();
/*  873 */     DataType dt = AminoAcids.DEFAULT_INSTANCE;
/*  874 */     for (int i = 0; i < al.getIdCount(); i++) {
/*  875 */       if (!colsOnly) {
/*  876 */         rel = randomize(length, end - start, replacement);
/*      */       }
/*  878 */       Identifier id = al.getIdentifier(i);
/*  879 */       seqs.add(randomize(al.getAlignedSequenceString(i), rel, start));
/*  880 */       lab.add(id);
/*      */     }
/*  882 */     Identifier[] lab1 = new Identifier[lab.size()];
/*  883 */     String[] seqs1 = new String[seqs.size()];
/*      */     
/*  885 */     lab.toArray(lab1);
/*  886 */     seqs.toArray(seqs1);
/*  887 */     IdGroup idg = new SimpleIdGroup(lab1);
/*  888 */     Alignment res = new SimpleAlignment(idg, 
/*  889 */       seqs1, "-", dt);
/*      */     
/*  891 */     return res;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public static int[] randomize(int length, int num_opts, boolean replacement)
/*      */   {
/*  898 */     List symbols = new ArrayList();
/*  899 */     for (int i = 0; i < num_opts; i++) {
/*  900 */       symbols.add(new Integer(i));
/*      */     }
/*  902 */     int[] result = new int[length];
/*  903 */     for (int i = 0; i < length; i++) {
/*  904 */       int ran = (int)StrictMath.floor(Math.random() * symbols.size());
/*  905 */       result[i] = ((Integer)symbols.get(ran)).intValue();
/*  906 */       if (!replacement) symbols.remove(new Integer(ran));
/*      */     }
/*  908 */     return result;
/*      */   }
/*      */   
/*      */ 
/*      */   public static String randomize(String st, int[] rel, int offset)
/*      */   {
/*  914 */     char[] result = new char[rel.length];
/*  915 */     for (int i = 0; i < rel.length; i++) {
/*  916 */       result[i] = st.charAt(rel[i] + offset);
/*      */     }
/*  918 */     return new String(result);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static pal.tree.Tree readNHXTree(File f)
/*      */     throws Exception
/*      */   {
/*  938 */     forester.tree.Tree tree1 = TreeHelper.readNHtree(f);
/*      */     
/*  940 */     pal.tree.Tree tree = new ReadTree(new PushbackReader(new StringReader(tree1.toNewHampshire(false))));
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  962 */     return tree;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public static Alignment removeGaps(Alignment al)
/*      */   {
/*  969 */     int[] rel = nonGaps(al);
/*      */     
/*  971 */     List lab = new ArrayList();
/*  972 */     List seqs = new ArrayList();
/*  973 */     DataType dt = AminoAcids.DEFAULT_INSTANCE;
/*  974 */     for (int i = 0; i < al.getIdCount(); i++) {
/*  975 */       Identifier id = al.getIdentifier(i);
/*  976 */       seqs.add(randomize(al.getAlignedSequenceString(i), rel, 0));
/*  977 */       lab.add(id);
/*      */     }
/*  979 */     Identifier[] lab1 = new Identifier[lab.size()];
/*  980 */     String[] seqs1 = new String[seqs.size()];
/*      */     
/*  982 */     lab.toArray(lab1);
/*  983 */     seqs.toArray(seqs1);
/*  984 */     IdGroup idg = new SimpleIdGroup(lab1);
/*  985 */     Alignment res = new SimpleAlignment(idg, 
/*  986 */       seqs1, "-", dt);
/*      */     
/*  988 */     return res;
/*      */   }
/*      */   
/*      */   public static void setType(String s) {
/*  992 */     int index = s.indexOf('+');
/*      */     
/*  994 */     AlignUtils.TYPE = "pal.substmodel." + (index > 0 ? s.substring(0, index) : s);
/*  995 */     if ((index > 0) && (s.substring(index).equals("+gwF"))) { AlignUtils.GWF = true;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static void train(SitePattern sp, SubstitutionModel substM)
/*      */   {
/* 1019 */     ModelParameters m = new ModelParameters(sp, substM);
/* 1020 */     double[] best = m.estimate();
/* 1021 */     for (int i = 0; i < best.length; i++) {
/* 1022 */       substM.setParameter(best[i], i);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static double[] trainRate(SitePattern sp, pal.tree.Tree tree, SubstitutionModel subst)
/*      */   {
/*      */     try
/*      */     {
/* 1048 */       if (tree == null) throw new Exception("conditions not right for training");
/* 1049 */     } catch (Exception exc) { exc.printStackTrace();System.exit(0);
/*      */     }
/*      */     
/* 1052 */     ModelParameters mvf = new ModelParameters(sp, subst);
/* 1053 */     double[] best = mvf.estimateFromTree((ParameterizedTree)tree);
/* 1054 */     for (int i = 0; i < best.length; i++) {
/* 1055 */       subst.setParameter(best[i], i);
/*      */     }
/*      */     
/* 1058 */     return best;
/*      */   }
/*      */   
/*      */   public static void transferAnnotation(forester.tree.Node n1, pal.tree.Node n, pal.tree.Tree tree)
/*      */   {
/* 1063 */     tree.setAttribute(n, "EC", n1.getECnumber());
/*      */     
/* 1065 */     if (n.getChildCount() >= 2) {
/* 1066 */       transferAnnotation(n1.getChild1(), n.getChild(0), tree);
/* 1067 */       transferAnnotation(n1.getChild2(), n.getChild(1), tree);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static void transferAnnotation(pal.tree.Node node, forester.tree.Node n, pal.tree.Tree tree)
/*      */   {
/* 1092 */     Object support = tree.getAttribute(node, "B");
/* 1093 */     if (support != null)
/*      */     {
/* 1095 */       n.setBootstrap(((Integer)support).intValue());
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1103 */     transferAnnotation(node.getChild(0), 
/* 1104 */       n.getChild1(), tree);
/* 1105 */     transferAnnotation(node.getChild(1), 
/* 1106 */       n.getChild2(), tree);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   static void updateTree(pal.tree.Tree tree, double[] lengths)
/*      */   {
/*      */     try
/*      */     {
/* 1122 */       pal.tree.Node current = tree.getExternalNode(0);
/* 1123 */       int i = 0;
/*      */       
/* 1125 */       while (!current.isRoot())
/*      */       {
/* 1127 */         current.setBranchLength(lengths[i]);
/* 1128 */         i++;
/* 1129 */         current = NodeUtils.postorderSuccessor(current);
/*      */       }
/*      */     }
/*      */     catch (Exception ise)
/*      */     {
/* 1134 */       ise.printStackTrace(System.err);
/*      */     }
/*      */   }
/*      */   
/*      */   public static void writeTree(pal.tree.Tree tree, File f) throws Exception
/*      */   {
/* 1140 */     TreeHelper.writeNHtree(new forester.tree.Tree(tree.toString()), f, true, true, true);
/*      */   }
/*      */ }


/* Location:              /home/lachlan/Desktop/psilc1.21/psilc.jar!/lc1/phyl/MaxLikelihoodTree.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */