/*     */ package lc1.treefam;
/*     */ 
/*     */ import forester.tree.TreeHelper;
/*     */ import java.io.BufferedWriter;
/*     */ import java.io.File;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.FileWriter;
/*     */ import java.io.FilenameFilter;
/*     */ import java.io.ObjectInputStream;
/*     */ import java.io.PrintWriter;
/*     */ import java.io.PushbackReader;
/*     */ import java.io.StringReader;
/*     */ import java.lang.ref.Reference;
/*     */ import java.lang.ref.SoftReference;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collection;
/*     */ import java.util.Enumeration;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import java.util.SortedSet;
/*     */ import java.util.Stack;
/*     */ import java.util.TreeMap;
/*     */ import java.util.TreeSet;
/*     */ import java.util.Vector;
/*     */ import javax.swing.JTable;
/*     */ import javax.swing.table.AbstractTableModel;
/*     */ import javax.swing.table.TableModel;
/*     */ import lc1.phyl.AlignUtils;
/*     */ import lc1.phyl.MaxLikelihoodTree;
/*     */ import org.apache.commons.cli.CommandLine;
/*     */ import pal.alignment.Alignment;
/*     */ import pal.alignment.ReadAlignment;
/*     */ import pal.alignment.SitePattern;
/*     */ import pal.datatype.AminoAcids;
/*     */ import pal.misc.Identifier;
/*     */ import pal.misc.SimpleIdGroup;
/*     */ import pal.tree.NodeUtils;
/*     */ import pal.tree.ReadTree;
/*     */ import pal.tree.TreeUtils;
/*     */ import pal.tree.UnconstrainedTree;
/*     */ 
/*     */ 
/*     */ 
/*     */ public class SpeciesTreeSearch
/*     */   extends TreeSearch
/*     */ {
/*     */   Set leaves;
/*  53 */   static Map fileIdToPosterior = new HashMap();
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
/*     */   public static boolean consistencyCheck(SitePattern align, TreePosterior tp, Duplet root, String key, File dir)
/*     */     throws Exception
/*     */   {
/*  79 */     return true;
/*     */   }
/*     */   
/*     */   public static void main(CommandLine params) throws Exception {
/*  83 */     File outDir = new File(params.getOptionValue("dir"), ".");
/*     */     
/*  85 */     boolean simulate = params.hasOption("simulate");
/*     */     
/*     */ 
/*     */ 
/*  89 */     Set idSet = new HashSet();
/*  90 */     FilenameFilter ff = new FilenameFilter() {
/*     */       public boolean accept(File f, String name) {
/*  92 */         return !name.equals("overall");
/*     */       }
/*     */       
/*  95 */     };
/*  96 */     Set leaves = new HashSet();
/*  97 */     List l = Arrays.asList(new File(outDir, "posterior").list(ff));
/*  98 */     int min = Integer.parseInt(params.getOptionValue("min"));
/*  99 */     int max = Math.min(Integer.parseInt(params.getOptionValue("max")) + 1, l.size());
/* 100 */     Map nodeSet = new HashMap();
/* 101 */     TreeSearch.posterior_dir = new File(outDir, "posterior");
/*     */     
/* 103 */     for (Iterator it = l.subList(min, max).iterator(); it.hasNext();) {
/* 104 */       String key = (String)it.next();
/* 105 */       TreePosterior tp = getPosterior(key);
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 110 */       for (Iterator it1 = tp.ids.iterator(); it1.hasNext();) {
/* 111 */         pal.tree.Node nextN = (pal.tree.Node)it1.next();
/* 112 */         idSet.add(nextN.getIdentifier().getName());
/* 113 */         nodeSet.put(nextN.getIdentifier().getName(), TreeSearch.makeDuplet(nextN));
/*     */       }
/* 115 */       leaves.add(key);
/*     */     }
/* 117 */     pal.tree.Tree testTree = null;
/* 118 */     File repos = new File(params.getOptionValue("repository"));
/*     */     Duplet root;
/* 120 */     if (params.hasOption("tree")) {
/* 121 */       pal.tree.Tree testTree1 = simulate ? new ReadTree(params.getOptionValue("tree")) : 
/* 122 */         MaxLikelihoodTree.convert(TreeHelper.readNHtree(new File(outDir, params.getOptionValue("tree"))));
/* 123 */       testTree = simulate ? testTree1 : new TaxonomyTree(new File(repos, "nodes.dmp"), new File(repos, "names.dmp"), "Eukaryota");
/* 124 */       root = TreeSearch.makeDuplet(testTree.getRoot());
/*     */     }
/*     */     else {
/* 127 */       Duplet root = TreeSearch.makeDuplet(new ArrayList(nodeSet.values()));
/* 128 */       testTree = new ReadTree(new PushbackReader(new StringReader(root.toString() + ";")));
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 133 */     Identifier[] ids = new Identifier[idSet.size()];
/* 134 */     Iterator iter = idSet.iterator();
/* 135 */     for (int i = 0; i < ids.length; i++) {
/* 136 */       ids[i] = new Identifier((String)iter.next());
/*     */     }
/* 138 */     Duplet dupl = TreeSearch.getIntersection(TreeSearch.makeDuplet(testTree.getRoot()), new SimpleIdGroup(ids));
/* 139 */     pal.tree.Tree startTree = new ReadTree(new PushbackReader(new StringReader(dupl.toString() + ";")));
/* 140 */     TreeSearch.annotateTree(testTree.getRoot(), startTree, TreeUtils.getLeafIdGroup(startTree));
/*     */     
/* 142 */     Object[] obj = TreePosteriorSearch.getRateMatrixDetails(params);
/* 143 */     SpeciesTreeSearch sts = new SpeciesTreeSearch(outDir, dupl, leaves, 
/* 144 */       (String[])obj[0], (double[][])obj[1]);
/* 145 */     double bestPrevScore = Double.NEGATIVE_INFINITY;
/*     */     
/*     */ 
/* 148 */     Duplet[] dupletL = sts.getTree();
/* 149 */     if (simulate) {
/* 150 */       Duplet random = TreeSearch.makeDuplet(new ReadTree("randomTree").getRoot());
/* 151 */       Set right = new TreeSet(Duplet.NODE_COMP);
/* 152 */       Set wrong = new TreeSet(Duplet.NODE_COMP);
/*     */       
/* 154 */       int[] count1 = TreeSearch.getRightCount(TreeSearch.getIntersection(random, new SimpleIdGroup(ids)), dupletL[0], sts.perturbations, right, wrong);
/*     */       
/*     */       Object localObject;
/* 157 */       for (Iterator it = right.iterator(); it.hasNext();) {
/* 158 */         localObject = it.next();
/*     */       }
/*     */       
/*     */ 
/* 162 */       for (Iterator it = wrong.iterator(); it.hasNext();) {
/* 163 */         localObject = it.next();
/*     */       }
/*     */     }
/*     */     
/* 167 */     pal.tree.Tree[] tree = new pal.tree.Tree[dupletL.length];
/* 168 */     printModel(sts.tp, sts.out_dir);
/*     */     
/* 170 */     for (int i = 0; i < tree.length; i++) {
/* 171 */       tree[i] = new ReadTree(new PushbackReader(new StringReader(dupletL[i].toString() + ";")));
/* 172 */       PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(new File(outDir, "results_0_" + min + "_" + max + ".nhx"))));
/* 173 */       Duplet dupl_new = dupletL[i];
/* 174 */       TreeSearch.annotateTree(startTree.getRoot(), 
/* 175 */         tree[i], TreeUtils.getLeafIdGroup(tree[i]));
/* 176 */       TreeSearch.annotateTree(sts.perturbations, tree[i].getRoot());
/* 177 */       TreeSearch.collapseTree(tree[i].getRoot());
/* 178 */       TreeUtils.printNH(tree[i], pw);
/* 179 */       pw.close();
/* 180 */       sts.graphTree(tree[i], sts.nodeToSupport, outDir, false);
/*     */     }
/*     */   }
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
/*     */   SpeciesTreeSearch(File dir, Duplet tree, Set leavesL, String[] rmClass, double[][] rmParams)
/*     */     throws Exception
/*     */   {
/* 198 */     super(dir, tree, "overall", rmClass, rmParams);
/* 199 */     this.leaves = leavesL;
/*     */   }
/*     */   
/*     */   Map getMCMCCounts(Duplet duplets, Duplet position, UnconstrainedTree currentTree, Map resultArray) throws Exception {
/* 203 */     return null;
/*     */   }
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
/*     */   public static TreePosterior getPosterior(String align_id)
/*     */     throws Exception
/*     */   {
/* 232 */     Reference treeP = 
/* 233 */       (Reference)fileIdToPosterior.get(align_id);
/* 234 */     if ((treeP == null) || (treeP.get() == null))
/*     */     {
/* 236 */       ObjectInputStream p = new ObjectInputStream(new FileInputStream(new File(TreeSearch.posterior_dir, align_id)));
/* 237 */       treeP = new SoftReference(p.readObject());
/* 238 */       p.close();
/* 239 */       fileIdToPosterior.put(align_id, treeP);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 244 */     TreePosterior tree = (TreePosterior)treeP.get();
/* 245 */     if (tree == null) throw new Exception("tree is null ! " + align_id);
/* 246 */     return tree;
/*     */   }
/*     */   
/*     */   public static void refreshPosterior(String align_id) throws Exception
/*     */   {
/* 251 */     fileIdToPosterior.remove(align_id);
/*     */   }
/*     */   
/* 254 */   Map support = new TreeMap(Duplet.NODE_COMP_PAIR);
/*     */   
/*     */   private boolean contains(Duplet position, Duplet node)
/*     */   {
/* 258 */     return false;
/*     */     
/* 260 */     int i = 0; while (i < node.getChildCount()) {}
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private int getCount(Duplet node, Iterator it1)
/*     */     throws Exception
/*     */   {
/* 271 */     int count = 0;
/* 272 */     while (it1.hasNext()) {
/* 273 */       String key = (String)it1.next();
/* 274 */       TreePosterior treeP = getPosterior(key);
/* 275 */       Duplet node1 = treeP.getIntersection(node);
/* 276 */       if (node1 != null)
/* 277 */         count++;
/*     */     }
/* 279 */     return count;
/*     */   }
/*     */   
/*     */   protected double getLogProb(Duplet duplets, Duplet prev_duplets, Duplet node, Duplet position, Stack parentsC, pal.tree.Node nextTree, double[] hotspot) throws Exception {
/* 283 */     if (position == null) return 0.0D;
/* 284 */     return getLogProb(duplets, prev_duplets, node, position, parentsC, this.leaves.iterator());
/*     */   }
/*     */   
/*     */   protected double[] getHotSpot() {
/* 288 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */   protected void putHotSpotInMap(pal.tree.Node node, double[] best, double[] nbest) {}
/*     */   
/*     */   private double getLogProb(Duplet duplets, Duplet currentTree, Duplet node, Duplet position, Stack parentsC, Iterator it1)
/*     */     throws Exception
/*     */   {
/* 297 */     double incr = 0.0D;
/* 298 */     Set completedSet = new HashSet();
/* 299 */     Set ignoreSet = new HashSet();
/* 300 */     boolean finished = false;
/* 301 */     for (; !finished; 
/*     */         
/*     */ 
/* 304 */         it1.hasNext())
/*     */     {
/* 303 */       finished = true;
/* 304 */       continue;
/* 305 */       String key = (String)it1.next();
/* 306 */       if ((!completedSet.contains(key)) && 
/* 307 */         (!ignoreSet.contains(key))) {
/* 308 */         TreePosterior treeP = getPosterior(key);
/*     */         
/* 310 */         for (int i = 0; i < position.getChildCount(); i++) {
/* 311 */           if (treeP.getIntersection((Duplet)position.getChild(i)) == null) {
/* 312 */             ignoreSet.add(key);
/* 313 */             break;
/*     */           }
/*     */         }
/* 316 */         Duplet node1 = treeP.getIntersection(node);
/* 317 */         Duplet position1 = treeP.getIntersection(position);
/*     */         
/* 319 */         Duplet[] dupl = { node1, position1 };
/* 320 */         Double res = treeP.getTreeEvidence(dupl);
/* 321 */         if (res == null) res = treeP.getTreeEvidence(dupl);
/* 322 */         if (res == null) {
/* 323 */           Duplet duplets1 = treeP.getIntersection(duplets);
/* 324 */           treeP.updateForReducedSet(duplets1);
/* 325 */           res = treeP.getTreeEvidence(dupl);
/* 326 */           if (res == null)
/*     */           {
/* 328 */             finished = false;
/* 329 */             Stack parents_new = new Stack();
/* 330 */             Duplet previous = null;
/* 331 */             for (int j = parentsC.size() - 1; j >= 0; j--) {
/* 332 */               Duplet current = treeP.getIntersection((Duplet)parentsC.get(j));
/* 333 */               if (current != previous) {
/* 334 */                 previous = current;
/* 335 */                 parents_new.add(0, current);
/*     */               }
/*     */             }
/*     */             
/* 339 */             Duplet currentTree1 = treeP.getIntersection(currentTree);
/*     */             
/* 341 */             pal.tree.Tree tree1 = new ReadTree(new PushbackReader(new StringReader(currentTree1.toString() + ";")));
/* 342 */             Alignment align1 = AlignUtils.restrictAlignment(new ReadAlignment(this.align_dir.getAbsolutePath() + "/" + key), tree1);
/* 343 */             SitePattern siteP = SitePattern.getSitePattern(align1);
/* 344 */             siteP.setDataType(AminoAcids.DEFAULT_INSTANCE);
/*     */             
/* 346 */             TreePosteriorSearch.computeSingleLayer(treeP, duplets1, 
/* 347 */               tree1.getRoot(), 
/* 348 */               position1, parents_new, 
/* 349 */               siteP);
/*     */           }
/* 351 */           TreeSearch.printModel(treeP, this.out_dir);
/* 352 */           refreshPosterior(key);
/*     */         }
/*     */         else
/*     */         {
/* 356 */           incr += res.doubleValue();
/* 357 */           completedSet.add(key);
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 362 */     return incr;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/* 367 */   Map nodeToSupport = new TreeMap(Duplet.NODE_COMP);
/*     */   
/*     */   static void annotateTreeWithSupport(Duplet duplets, forester.tree.Tree tree, pal.tree.Tree tree1, Map nodeToSupport) throws Exception
/*     */   {
/* 371 */     Collection idSet = new TreeSet(Duplet.NODE_COMP);
/* 372 */     TreePosterior.addLeafIds(duplets, idSet);
/* 373 */     List keys = new ArrayList();List vals = new ArrayList();
/* 374 */     for (Iterator it = nodeToSupport.keySet().iterator(); it.hasNext();) {
/* 375 */       Duplet key = (Duplet)it.next();
/* 376 */       Duplet inter = key.getIntersection(idSet);
/* 377 */       if (inter != null) {
/* 378 */         vals.add(nodeToSupport.get(key));
/* 379 */         keys.add(inter);
/*     */       } }
/* 381 */     for (int i = 0; i < keys.size(); i++) {
/* 382 */       nodeToSupport.put(keys.get(i), vals.get(i));
/*     */     }
/* 384 */     Enumeration it = tree.getRoot().getAllChildren().elements();
/* 385 */     while (it.hasMoreElements()) {
/* 386 */       forester.tree.Node node = (forester.tree.Node)it.nextElement();
/* 387 */       if (!node.isExternal()) {
/* 388 */         Vector v = node.getAllExternalChildren();
/* 389 */         pal.tree.Node[] extNodes = new pal.tree.Node[v.size()];
/* 390 */         for (int i = 0; i < v.size(); i++) {
/* 391 */           extNodes[i] = NodeUtils.findByIdentifier(tree1.getRoot(), 
/* 392 */             ((forester.tree.Node)v.get(i)).getSeqName());
/*     */         }
/*     */         
/* 395 */         pal.tree.Node intNode = NodeUtils.getFirstCommonAncestor(extNodes);
/* 396 */         JTable analysis = getAnalysis(intNode, nodeToSupport);
/* 397 */         if (analysis != null)
/*     */         {
/* 399 */           node.setAnalysis(analysis);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   static JTable getAnalysis(pal.tree.Node node, Map nodeToSupport)
/*     */     throws Exception
/*     */   {
/* 408 */     Map nodeMap = (Map)nodeToSupport.get(TreeSearch.makeDuplet(node));
/* 409 */     if (nodeMap == null) return null;
/* 410 */     List keys = new ArrayList(nodeMap.keySet());
/*     */     
/* 412 */     TableModel tm = new AbstractTableModel() {
/*     */       public int getColumnCount() {
/* 414 */         return 4;
/*     */       }
/*     */       
/* 417 */       public int getRowCount() { return SpeciesTreeSearch.this.size(); }
/*     */       
/*     */       public Object getValueAt(int row, int col)
/*     */       {
/* 421 */         Object key = SpeciesTreeSearch.this.get(row);
/* 422 */         Object[] value = (Object[])this.val$nodeMap.get(key);
/* 423 */         if (col == 0) return key;
/* 424 */         return value[(col - 1)];
/*     */       }
/* 426 */     };
/* 427 */     return new JTable(tm);
/*     */   }
/*     */   
/*     */   Double fillSupport(Duplet[] bestnodes, Duplet position) throws Exception
/*     */   {
/* 432 */     Duplet bestnode = bestnodes[0];
/* 433 */     Duplet nbestnode = bestnodes[1];
/* 434 */     Map nodeMap = new TreeMap();
/* 435 */     double diff = 0.0D;
/* 436 */     for (Iterator it = this.leaves.iterator(); it.hasNext();) {
/* 437 */       String key = (String)it.next();
/*     */       
/* 439 */       TreePosterior treeP = getPosterior(key);
/*     */       
/* 441 */       Double bestSc = null;
/* 442 */       Double nbestSc = null;
/* 443 */       Duplet bestnode1 = treeP.getIntersection(bestnode);
/* 444 */       Duplet nbestnode1 = treeP.getIntersection(nbestnode);
/* 445 */       Duplet position1 = treeP.getIntersection(position);
/* 446 */       if ((bestnode1 != null) && (position1 != null)) {
/* 447 */         bestSc = treeP.getTreeEvidence(new Duplet[] { bestnode1, position1 });
/* 448 */         if ((bestSc != null) && 
/* 449 */           (nbestnode1 != null)) {
/* 450 */           nbestSc = treeP.getTreeEvidence(new Duplet[] { nbestnode1, position1 });
/* 451 */           if (nbestSc != null) {
/* 452 */             diff += bestSc.doubleValue() - nbestSc.doubleValue();
/* 453 */             Double difference = new Double(bestSc.doubleValue() - nbestSc.doubleValue());
/* 454 */             while (nodeMap.containsKey(difference)) {
/* 455 */               difference = new Double(difference.doubleValue() + 1.0E-4D * (Math.random() - 0.5D));
/*     */             }
/* 457 */             nodeMap.put(difference, 
/* 458 */               new Object[] { key, 
/* 459 */               bestnode1.toString(), nbestnode1.toString() });
/* 460 */             key.equals("326");
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 466 */     this.nodeToSupport.put(bestnode, nodeMap);
/* 467 */     if (nodeMap.keySet().size() == 0) { throw new Exception("size of node map should not be zero " + bestnode + " " + nbestnode);
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
/* 482 */     return new Double(diff);
/*     */   }
/*     */   
/*     */   protected Object[] removeTaxaWithLeastAlignments(Duplet position, Stack parentsC) throws Exception {
/* 486 */     int[] avg_count = new int[position.getChildCount()];
/* 487 */     Arrays.fill(avg_count, 0);
/* 488 */     for (int i = 0; i < position.getChildCount(); i++) {
/* 489 */       avg_count[i] = getCount((Duplet)position.getChild(i), this.leaves.iterator());
/*     */     }
/* 491 */     int min_id = 0;
/* 492 */     for (int i = 1; i < avg_count.length; i++) {
/* 493 */       if (avg_count[i] < avg_count[min_id]) {
/* 494 */         min_id = i;
/*     */       }
/*     */     }
/* 497 */     if (((Duplet)position.getChild(min_id)).num_children > 2) {
/* 498 */       this.discarded.add((Duplet)position.getChild(min_id));
/*     */     }
/*     */     
/*     */ 
/* 502 */     int removed_id = ((Duplet)position.getChild(min_id)).min_id;
/* 503 */     Stack stack = new Stack();
/*     */     
/* 505 */     Stack parents = (Stack)parentsC.clone();
/* 506 */     parents.pop();
/* 507 */     List child_newL = new ArrayList();
/* 508 */     for (int k = 0; k < position.getChildCount(); k++) {
/* 509 */       if (k != min_id) {
/* 510 */         child_newL.add(position.getChild(k));
/*     */       }
/*     */     }
/* 513 */     Duplet duplets_new = TreeSearch.makeDuplet(child_newL);
/*     */     
/* 515 */     stack.add(0, duplets_new);
/* 516 */     while (parents.size() > 0) {
/* 517 */       Duplet parent = (Duplet)parents.pop();
/* 518 */       child_newL = new ArrayList();
/* 519 */       for (int k = 0; k < parent.getChildCount(); k++) {
/* 520 */         Duplet child_k = (Duplet)parent.getChild(k);
/* 521 */         if (child_k.min_id == Math.min(duplets_new.min_id, removed_id)) {
/* 522 */           child_newL.add(duplets_new);
/*     */         } else
/* 524 */           child_newL.add(child_k);
/*     */       }
/* 526 */       duplets_new = TreeSearch.makeDuplet(child_newL);
/* 527 */       stack.add(0, duplets_new);
/*     */     }
/*     */     
/* 530 */     return new Object[] { duplets_new, stack };
/*     */   }
/*     */   
/*     */   Object[] getBestListBase(Duplet duplets, Stack parentsC, Duplet position, int clust_size, Collection keys) throws Exception {
/* 534 */     Object[] result = super.getBestListBase(duplets, parentsC, position, clust_size, keys);
/* 535 */     Double log_odds = fillSupport((Duplet[])result[2], position);
/* 536 */     return result;
/*     */   }
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
/*     */   public void addDistance(SortedSet duplets, Duplet s1, Duplet s2, Duplet dupl) {}
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
/*     */   public double getLogProbImpl(Duplet duplets, Duplet prev_duplets, Duplet node, Duplet position, Stack parentsC)
/*     */     throws Exception
/*     */   {
/* 588 */     return getLogProb(duplets, prev_duplets, node, position, parentsC, this.leaves.iterator());
/*     */   }
/*     */ }


/* Location:              /home/lachlan/Desktop/psilc1.21/psilc.jar!/lc1/treefam/SpeciesTreeSearch.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */