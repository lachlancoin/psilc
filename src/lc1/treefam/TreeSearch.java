/*     */ package lc1.treefam;
/*     */ 
/*     */ import forester.atv.ATVjframe;
/*     */ import java.io.BufferedWriter;
/*     */ import java.io.File;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.FileWriter;
/*     */ import java.io.ObjectOutputStream;
/*     */ import java.io.PrintStream;
/*     */ import java.io.PrintWriter;
/*     */ import java.io.PushbackReader;
/*     */ import java.io.StringReader;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collection;
/*     */ import java.util.Comparator;
/*     */ import java.util.Enumeration;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import java.util.SortedMap;
/*     */ import java.util.SortedSet;
/*     */ import java.util.Stack;
/*     */ import java.util.TreeMap;
/*     */ import java.util.TreeSet;
/*     */ import java.util.Vector;
/*     */ import lc1.phyl.MaxLikelihoodTree;
/*     */ import org.apache.commons.cli.OptionBuilder;
/*     */ import org.apache.commons.cli.Options;
/*     */ import pal.misc.IdGroup;
/*     */ import pal.misc.Identifier;
/*     */ import pal.tree.NodeUtils;
/*     */ import pal.tree.ReadTree;
/*     */ import pal.tree.SimpleNode;
/*     */ import pal.tree.SimpleTree;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ abstract class TreeSearch
/*     */ {
/*  45 */   static final Options OPTIONS = new Options() {};
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
/*  63 */   private static final Integer zero = new Integer(0);
/*     */   
/*  65 */   static File posterior_dir = new File("posterior");
/*  66 */   File out_dir = new File("vis");
/*     */   
/*     */   File align_dir;
/*     */   Duplet start;
/*  70 */   Duplet best = null;
/*     */   
/*     */ 
/*  73 */   boolean accumulateHotSpotInfo = false;
/*  74 */   double bestScore = Double.NEGATIVE_INFINITY;
/*     */   TreePosterior tp;
/*  76 */   SortedMap perturbations = new TreeMap(Duplet.NODE_COMP);
/*  77 */   List discarded = new ArrayList();
/*  78 */   pal.tree.Node currentTree = null;
/*     */   double[][] distances;
/*     */   int no_to_consider;
/*     */   static String[] rateMatrixClass;
/*     */   static double[][] rateMatrixParams;
/*     */   
/*     */   static Object[] makeNextTree(pal.tree.Node[] node_ab, pal.tree.Node currentTree) {
/*  85 */     System.err.println("joining " + Arrays.asList(node_ab));
/*  86 */     if (currentTree == null)
/*  87 */       throw new NullPointerException("current tree should not be null");
/*  88 */     pal.tree.Node root = new SimpleNode(currentTree);
/*  89 */     pal.tree.Node[] tNode_ab = new pal.tree.Node[node_ab.length];
/*  90 */     for (int i = 0; i < node_ab.length; i++) {
/*  91 */       tNode_ab[i] = TreePosteriorSearch.getNode(node_ab[i], root);
/*     */     }
/*  93 */     pal.tree.Node parent = tNode_ab[0].getParent();
/*  94 */     pal.tree.Node newN = new SimpleNode();
/*  95 */     for (int j = 0; j < tNode_ab.length; j++) {
/*  96 */       for (int i = 0; i < parent.getChildCount(); i++) {
/*  97 */         if (parent.getChild(i).equals(tNode_ab[j]))
/*  98 */           parent.removeChild(i);
/*     */       }
/* 100 */       newN.addChild(tNode_ab[j]);
/* 101 */       tNode_ab[j].setParent(newN);
/*     */     }
/* 103 */     parent.addChild(newN);
/* 104 */     return new Object[] { root, newN };
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   TreeSearch(File dir, Duplet duplets, String name, String[] rateMatrixClass, double[][] rateMatrixParams)
/*     */     throws Exception
/*     */   {
/* 112 */     rateMatrixClass = rateMatrixClass;
/* 113 */     rateMatrixParams = rateMatrixParams;
/* 114 */     posterior_dir = new File(dir, "posterior");
/* 115 */     this.align_dir = new File(dir, "align");
/* 116 */     this.out_dir = new File(dir, "vis");
/* 117 */     if (!posterior_dir.exists()) posterior_dir.mkdir();
/* 118 */     if (!this.out_dir.exists()) this.out_dir.mkdir();
/* 119 */     File postFile = new File(posterior_dir, "name");
/* 120 */     this.tp = new TreePosterior(duplets, name);
/* 121 */     this.start = duplets;
/*     */   }
/*     */   
/*     */   protected static void printModel(TreePosterior tp, File out_dir) throws Exception
/*     */   {
/* 126 */     PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(
/* 127 */       new File(out_dir, tp.name))));
/* 128 */     tp.print(pw);
/* 129 */     pw.close();
/* 130 */     printObject(tp, new File(out_dir.getParentFile(), "posterior"));
/*     */   }
/*     */   
/*     */   protected static void printObject(TreePosterior tp, File posterior_dir) throws Exception
/*     */   {
/* 135 */     File f = new File(posterior_dir, tp.name);
/* 136 */     f.delete();
/*     */     
/* 138 */     ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(
/* 139 */       new File(posterior_dir, tp.name)));
/* 140 */     os.writeObject(tp);
/* 141 */     os.flush();
/* 142 */     os.close();
/*     */   }
/*     */   
/*     */   private void graphCurrentBestTree()
/*     */     throws Exception
/*     */   {
/* 148 */     if (((this instanceof SpeciesTreeSearch)) && 
/* 149 */       (System.currentTimeMillis() - this.lastTime > 200000L))
/*     */     {
/* 151 */       pal.tree.Node tree = new ReadTree(new PushbackReader(
/* 152 */         new StringReader(this.best.toString() + ";")))
/* 153 */         .getRoot();
/* 154 */       forester.tree.Tree tr = 
/* 155 */         MaxLikelihoodTree.convert(new SimpleTree(tree));
/* 156 */       if (this.jframe != null) {
/* 157 */         this.jframe.dispose();
/*     */       }
/* 159 */       this.jframe = new ATVjframe(tr);
/* 160 */       this.jframe.showWhole();
/*     */       
/* 162 */       for (Iterator it = this.frames.iterator(); it.hasNext();) {
/* 163 */         ((ATVjframe)it.next()).dispose();
/*     */       }
/* 165 */       for (Iterator it = this.discarded.iterator(); it.hasNext();) {
/* 166 */         pal.tree.Node tree = new ReadTree(new PushbackReader(
/* 167 */           new StringReader(it.next().toString() + ";")))
/* 168 */           .getRoot();
/* 169 */         forester.tree.Tree tr = 
/* 170 */           MaxLikelihoodTree.convert(new SimpleTree(tree));
/* 171 */         ATVjframe frame = new ATVjframe(
/* 172 */           tr);
/* 173 */         frame.showWhole();
/* 174 */         this.frames.add(frame);
/*     */       }
/* 176 */       this.lastTime = System.currentTimeMillis();
/*     */     }
/*     */   }
/*     */   
/* 180 */   private void disposeFrames() { for (Iterator it = this.frames.iterator(); it.hasNext();) {
/* 181 */       ((ATVjframe)it.next()).dispose();
/*     */     }
/* 183 */     if (this.jframe != null)
/* 184 */       this.jframe.dispose(); }
/*     */   
/* 186 */   ATVjframe jframe = null;
/* 187 */   long lastTime = System.currentTimeMillis();
/* 188 */   List frames = new ArrayList();
/*     */   
/*     */   public Duplet[] getTree()
/*     */     throws Exception
/*     */   {
/* 193 */     initialise(this.start);
/* 194 */     this.best = this.start;
/*     */     for (;;) {
/* 196 */       Stack st = new Stack();
/* 197 */       st.push(this.best);
/* 198 */       Duplet best_new = getBestList(this.best, st);
/*     */       
/* 200 */       if (best_new == null) {
/*     */         break;
/*     */       }
/* 203 */       this.best = best_new;
/* 204 */       graphCurrentBestTree();
/*     */     }
/* 206 */     disposeFrames();
/* 207 */     Duplet[] dupl = new Duplet[this.discarded.size() + 1];
/* 208 */     for (int i = 0; i < this.discarded.size(); i++) {
/* 209 */       dupl[(i + 1)] = ((Duplet)this.discarded.get(i));
/*     */     }
/* 211 */     dupl[0] = this.best;
/* 212 */     return dupl;
/*     */   }
/*     */   
/*     */   protected void initialise(Duplet duplets) throws Exception
/*     */   {
/* 217 */     this.tp.setRootedAbove((Duplet)duplets.getChild(0));
/* 218 */     pal.tree.Node tree = new ReadTree(new PushbackReader(new StringReader(
/* 219 */       duplets.toString() + 
/* 220 */       ";"))).getRoot();
/* 221 */     Double res = new Double(getLogProb(duplets, null, duplets, null, null, tree, null));
/* 222 */     this.currentTree = tree;
/* 223 */     Double zeroD = new Double(0.0D);
/* 224 */     this.tp.log_sc = res.doubleValue();
/* 225 */     this.tp.setLogProb(new Duplet[] { duplets }, zeroD);
/*     */   }
/*     */   
/*     */ 
/*     */   protected abstract double getLogProb(Duplet paramDuplet1, Duplet paramDuplet2, Duplet paramDuplet3, Duplet paramDuplet4, Stack paramStack, pal.tree.Node paramNode, double[] paramArrayOfDouble)
/*     */     throws Exception;
/*     */   
/*     */ 
/*     */   protected static List getClusterIterator(int clust_size, int num_children, boolean rootAtFirst, double[][] dist, int to_keep)
/*     */   {
/* 235 */     Iterator it = new Iterator()
/*     */     {
/*     */       int[] index;
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       public boolean hasNext()
/*     */       {
/* 245 */         for (int i = 0; i < this.index.length; i++) {
/* 246 */           if (this.index[i] + 1 < this.val$num_children - (this.val$clust_size - (i + 1)))
/* 247 */             return true;
/*     */         }
/* 249 */         return false;
/*     */       }
/*     */       
/*     */       public Object next() {
/* 253 */         for (int i = this.index.length - 1; i >= 0; i--) {
/* 254 */           if (this.index[i] + 1 < this.val$num_children - (this.val$clust_size - (i + 1))) {
/* 255 */             this.index[i] += 1;
/* 256 */             for (int j = i + 1; j < this.index.length; j++) {
/* 257 */               this.index[j] = (this.index[(j - 1)] + 1);
/*     */             }
/* 259 */             break;
/*     */           }
/*     */         }
/* 262 */         return this.index.clone();
/*     */       }
/*     */       
/*     */ 
/*     */       public void remove() {}
/* 267 */     };
/* 268 */     double[] r = (double[])null;
/* 269 */     double scale = 1.0D;
/* 270 */     if (dist != null) {
/* 271 */       r = new double[dist.length];
/* 272 */       for (int i = 0; i < r.length; i++) {
/* 273 */         for (int j = 0; j < r.length; j++) {
/* 274 */           r[i] += dist[i][j];
/*     */         }
/*     */       }
/* 277 */       scale = r.length == 2 ? 1.0D : 1.0D / (r.length - 2);
/*     */     }
/* 279 */     SortedMap l = new TreeMap();
/* 280 */     int i = 0;
/*     */     
/* 282 */     while (it.hasNext()) {
/* 283 */       int[] index = (int[])it.next();
/* 284 */       if ((clust_size != 2) || (dist == null)) {
/* 285 */         l.put(new Integer(i), index);
/*     */       }
/*     */       else {
/* 288 */         double d = dist[index[0]][index[1]];
/* 289 */         Double distance = new Double(d - (r[index[0]] + r[index[1]]) * scale);
/* 290 */         while (l.containsKey(distance)) {
/* 291 */           distance = new Double(distance.doubleValue() + (Math.random() - 0.5D) * 1.0E-4D);
/*     */         }
/* 293 */         l.put(distance, index);
/*     */       }
/* 295 */       i++;
/*     */     }
/*     */     
/* 298 */     return new ArrayList(l.values()).subList(0, Math.min(to_keep, l.size()));
/*     */   }
/*     */   
/*     */ 
/*     */   protected abstract Object[] removeTaxaWithLeastAlignments(Duplet paramDuplet, Stack paramStack)
/*     */     throws Exception;
/*     */   
/*     */   protected abstract double[] getHotSpot();
/*     */   
/*     */   protected abstract void putHotSpotInMap(pal.tree.Node paramNode, double[] paramArrayOfDouble1, double[] paramArrayOfDouble2);
/*     */   
/*     */   Object[] getBestListBase(Duplet duplets, Stack parentsC, Duplet position, int clust_size, Collection keys)
/*     */     throws Exception
/*     */   {
/* 312 */     Map resultArray = new TreeMap(INT_COMP);
/* 313 */     Map nodeArray = new TreeMap(INT_COMP);
/* 314 */     Map scoreArray = new TreeMap(INT_COMP);
/* 315 */     Map hotSpotArray = new TreeMap(INT_COMP);
/* 316 */     boolean approx = true;
/* 317 */     boolean adjust = false;
/* 318 */     Map treeArray = new TreeMap(INT_COMP);
/* 319 */     Map stackArray = new TreeMap(INT_COMP);
/* 320 */     boolean foundBetter = false;
/* 321 */     fillArrays(duplets, parentsC, position, keys, resultArray, treeArray, 
/* 322 */       nodeArray, stackArray, this.currentTree);
/* 323 */     double best_score = Double.NEGATIVE_INFINITY;
/* 324 */     int[] bestkey = (int[])null;
/* 325 */     for (Iterator it = keys.iterator(); it.hasNext();) {
/* 326 */       Object key = it.next();
/* 327 */       double[] hotspot = getHotSpot();
/* 328 */       double score_ij = getLogProb((Duplet)resultArray.get(key), 
/* 329 */         duplets, (Duplet)nodeArray.get(key), position, 
/* 330 */         (Stack)stackArray.get(key), (pal.tree.Node)treeArray.get(key), 
/* 331 */         hotspot);
/* 332 */       if (hotspot != null)
/* 333 */         hotSpotArray.put(key, hotspot);
/* 334 */       scoreArray.put(key, new Double(score_ij));
/* 335 */       if (score_ij > best_score) {
/* 336 */         bestkey = (int[])key;
/* 337 */         best_score = score_ij;
/*     */       }
/*     */     }
/*     */     
/* 341 */     Object nbestkey = null;
/* 342 */     double nbest_score = Double.NEGATIVE_INFINITY;
/* 343 */     for (Iterator it = keys.iterator(); it.hasNext();) {
/* 344 */       int[] key = (int[])it.next();
/* 345 */       if (key != bestkey)
/*     */       {
/* 347 */         boolean matched = false;
/* 348 */         for (int i = 0; i < key.length; i++) {
/* 349 */           for (int j = 0; j < bestkey.length; j++) {
/* 350 */             if (key[i] == bestkey[j]) {
/* 351 */               matched = true;
/* 352 */               break;
/*     */             }
/*     */           }
/*     */         }
/*     */         
/* 357 */         if (matched)
/*     */         {
/* 359 */           double score_i = ((Double)scoreArray.get(key)).doubleValue();
/* 360 */           if (score_i > nbest_score) {
/* 361 */             nbest_score = score_i;
/* 362 */             nbestkey = key;
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/* 367 */     Double log_odds = new Double(best_score - nbest_score);
/* 368 */     this.perturbations.put(nodeArray.get(bestkey), log_odds);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 376 */     return new Object[] {
/* 377 */       resultArray.get(bestkey), 
/* 378 */       treeArray.get(bestkey), 
/* 379 */       { (Duplet)nodeArray.get(bestkey), 
/* 380 */       nbestkey == null ? null : 
/* 381 */       (Duplet)nodeArray.get(nbestkey) }, 
/* 382 */       stackArray.get(bestkey), new Double(best_score), bestkey };
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected static void fillArrays(Duplet duplets, Stack parentsC, Duplet position, Collection keys, Map resultArray, Map treeArray, Map nodeArray, Map stackArray, pal.tree.Node currentTree)
/*     */   {
/* 390 */     for (Iterator it = keys.iterator(); it.hasNext();) {
/* 391 */       int[] key = (int[])it.next();
/* 392 */       pal.tree.Node[] duplets_ij = new Duplet[key.length];
/* 393 */       for (int i = 0; i < key.length; i++) {
/* 394 */         duplets_ij[i] = position.getChild(key[i]);
/*     */       }
/* 396 */       Object[] obj = makeNextTree(duplets_ij, currentTree);
/* 397 */       if (treeArray != null)
/* 398 */         treeArray.put(key, obj[0]);
/* 399 */       Stack stack = new Stack();
/* 400 */       Stack parents = (Stack)parentsC.clone();
/* 401 */       parents.pop();
/* 402 */       Duplet duplets_new = null;
/* 403 */       Duplet coal = makeDuplet(Arrays.asList(duplets_ij));
/* 404 */       nodeArray.put(key, coal);
/* 405 */       List child_newL = new ArrayList();
/* 406 */       child_newL.add(coal);
/* 407 */       for (int k = -1; k < key.length; k++) {
/* 408 */         for (int kj = k < 0 ? 0 : key[k] + 1; 
/*     */             
/* 410 */             kj < (k + 1 == key.length ? position.getChildCount() : key[(k + 1)]); kj++) {
/* 411 */           child_newL.add(position.getChild(kj));
/*     */         }
/*     */       }
/* 414 */       duplets_new = makeDuplet(child_newL);
/* 415 */       stack.add(0, duplets_new);
/* 416 */       while (parents.size() > 0) {
/* 417 */         Duplet parent = (Duplet)parents.pop();
/* 418 */         child_newL = new ArrayList();
/* 419 */         for (int k = 0; k < parent.getChildCount(); k++) {
/* 420 */           Duplet child_k = (Duplet)parent.getChild(k);
/* 421 */           if (child_k.min_id == duplets_new.min_id) {
/* 422 */             child_newL.add(duplets_new);
/*     */           } else
/* 424 */             child_newL.add(child_k);
/*     */         }
/* 426 */         duplets_new = makeDuplet(child_newL);
/* 427 */         stack.add(0, duplets_new);
/*     */       }
/* 429 */       resultArray.put(key, duplets_new);
/* 430 */       stackArray.put(key, stack);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public Object[] getBestListInner(Duplet duplets, Stack parentsC, Duplet position)
/*     */     throws Exception
/*     */   {
/* 439 */     double threshold = getLogProb(duplets, duplets, position, position, 
/* 440 */       parentsC, this.currentTree, null);
/* 441 */     if ((threshold == 0.0D) && ((this instanceof SpeciesTreeSearch))) {
/* 442 */       return removeTaxaWithLeastAlignments(position, parentsC);
/*     */     }
/* 444 */     int max_size = 2;
/* 445 */     Object[][] object = new Object[max_size - 1][0];
/* 446 */     Object[] obj = (Object[])null;
/*     */     
/* 448 */     for (int clust_size = 2; clust_size <= max_size; clust_size++) {
/* 449 */       object[(clust_size - 2)] = getBestListBase(duplets, parentsC, 
/* 450 */         position, clust_size, getClusterIterator(clust_size, 
/* 451 */         position.getChildCount(), (duplets.equals(position)) && 
/* 452 */         (position.getChildCount() <= 4) ? 1 : false, this.distances, this.no_to_consider));
/* 453 */       if (((Double)object[(clust_size - 2)][4]).doubleValue() - threshold > 10.0D) {
/*     */         break;
/*     */       }
/*     */     }
/* 457 */     if (obj == null) {
/* 458 */       double best_score = Double.NEGATIVE_INFINITY;
/* 459 */       int best_index = 0;
/* 460 */       for (int i = 0; i < object.length; i++) {
/* 461 */         if ((object[i].length != 0) && 
/* 462 */           (((Double)object[i][4]).doubleValue() > best_score)) {
/* 463 */           obj = object[i];
/* 464 */           best_index = i;
/* 465 */           best_score = ((Double)object[i][4]).doubleValue();
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 470 */     this.currentTree = ((pal.tree.Node)obj[1]);
/*     */     
/* 472 */     if (this.distances != null) {
/* 473 */       int[] index = (int[])obj[5];
/* 474 */       double[][] new_dist = new double[this.distances.length - 1][this.distances.length - 1];
/* 475 */       int new_i = 0;
/* 476 */       for (int i = 0; i < this.distances.length; i++)
/* 477 */         if (i != index[1]) {
/* 478 */           if (i == index[0]) {
/* 479 */             int new_j = 0;
/* 480 */             for (int j = 0; j < i; j++) {
/* 481 */               if (j != index[1]) {
/* 482 */                 new_dist[new_j][new_i] = (new_dist[new_i][new_j] = 0.5D * (this.distances[index[0]][j] + this.distances[index[1]][j] - this.distances[index[0]][index[1]]));
/* 483 */                 new_j++;
/*     */               }
/*     */             }
/*     */           } else {
/* 487 */             int new_j = 0;
/* 488 */             for (int j = 0; j < i; j++)
/* 489 */               if (j != index[1]) {
/* 490 */                 new_dist[new_j][new_i] = (new_dist[new_i][new_j] = this.distances[i][j]);
/* 491 */                 new_j++;
/*     */               }
/*     */           }
/* 494 */           new_i++;
/*     */         }
/* 496 */       this.distances = new_dist;
/*     */     }
/* 498 */     return new Object[] { (Duplet)obj[0], obj[3] };
/*     */   }
/*     */   
/*     */ 
/*     */   public static void getLeafSet(Duplet position, Collection positionSet)
/*     */   {
/* 504 */     if (position.isLeaf()) {
/* 505 */       positionSet.add(new Integer(position.min_id));
/*     */     } else {
/* 507 */       for (int i = 0; i < position.getChildCount(); i++) {
/* 508 */         getLeafSet((Duplet)position.getChild(i), positionSet);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void getMultificutingNodeLeafSets(Duplet position, Collection leafSets, boolean root)
/*     */   {
/* 519 */     if (((root) && (position.getChildCount() > 3)) || 
/* 520 */       (position.getChildCount() > 2)) {
/* 521 */       Set leafSet = new TreeSet();
/* 522 */       getLeafSet(position, leafSet);
/* 523 */       leafSets.add(leafSet);
/*     */     }
/* 525 */     for (int i = 0; i < position.getChildCount(); i++) {
/* 526 */       getMultificutingNodeLeafSets((Duplet)position.getChild(i), 
/* 527 */         leafSets, false);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Duplet getBestList(Duplet duplets, Stack parentsC)
/*     */     throws Exception
/*     */   {
/* 538 */     Duplet bestD = null;
/* 539 */     Duplet position = (Duplet)parentsC.lastElement();
/*     */     
/* 541 */     for (int i = 0; (i < position.getChildCount()) && (bestD == null); i++) {
/* 542 */       parentsC.push(position.getChild(i));
/* 543 */       bestD = getBestList(duplets, parentsC);
/* 544 */       parentsC.pop();
/* 545 */       if (bestD != null) {
/* 546 */         return bestD;
/*     */       }
/*     */     }
/* 549 */     Stack parentsC_inner = (Stack)parentsC.clone();
/* 550 */     Duplet duplets_inner = duplets;
/* 551 */     Duplet position_inner = (Duplet)parentsC_inner.lastElement();
/* 552 */     while ((position_inner.getChildCount() > 2) && (
/* 553 */       (!position_inner.equals(duplets_inner)) || (position_inner.getChildCount() > 3))) {
/* 554 */       Object[] obj = getBestListInner(duplets_inner, parentsC_inner, 
/* 555 */         position_inner);
/* 556 */       Duplet duplets_inner_next = (Duplet)obj[0];
/* 557 */       duplets_inner = duplets_inner_next;
/* 558 */       parentsC_inner = (Stack)obj[1];
/* 559 */       position_inner = (Duplet)parentsC_inner.lastElement();
/*     */     }
/*     */     
/* 562 */     return duplets_inner.equals(duplets) ? null : duplets_inner;
/*     */   }
/*     */   
/*     */   private static int whichIndex(Object o, Set[] s)
/*     */   {
/* 567 */     for (int i = 0; i < s.length; i++) {
/* 568 */       if (s[i].contains(o))
/* 569 */         return i;
/*     */     }
/* 571 */     return -1;
/*     */   }
/*     */   
/*     */   private static Collection externalNames(pal.tree.Node node) {
/* 575 */     pal.tree.Node[] nodes = NodeUtils.getExternalNodes(node);
/* 576 */     String[] st = new String[nodes.length];
/* 577 */     for (int j = 0; j < nodes.length; j++) {
/* 578 */       st[j] = nodes[j].getIdentifier().getName();
/*     */     }
/* 580 */     return Arrays.asList(st);
/*     */   }
/*     */   
/*     */   private static pal.tree.Node[] findByIdentifier(pal.tree.Node n, Collection names) {
/* 584 */     pal.tree.Node[] nodes = NodeUtils.getExternalNodes(n);
/* 585 */     List l = new ArrayList();
/* 586 */     for (int i = 0; i < nodes.length; i++) {
/* 587 */       if (names.contains(nodes[i].getIdentifier().getName())) {
/* 588 */         l.add(nodes[i]);
/*     */       }
/*     */     }
/* 591 */     pal.tree.Node[] res = new pal.tree.Node[l.size()];
/* 592 */     l.toArray(res);
/* 593 */     return res;
/*     */   }
/*     */   
/*     */   static boolean consistent(pal.tree.Node multifur, pal.tree.Node bifur)
/*     */   {
/* 598 */     for (int i = 0; i < multifur.getChildCount(); i++)
/* 599 */       if (!multifur.getChild(i).isLeaf())
/*     */       {
/* 601 */         Collection m_st = externalNames(multifur.getChild(i));
/* 602 */         pal.tree.Node b_ancest = NodeUtils.getFirstCommonAncestor(findByIdentifier(
/* 603 */           bifur, m_st));
/* 604 */         if (m_st.size() != NodeUtils.getExternalNodes(b_ancest).length)
/* 605 */           return false;
/* 606 */         if (!consistent(multifur.getChild(i), b_ancest))
/* 607 */           return false;
/*     */       }
/* 609 */     return true;
/*     */   }
/*     */   
/*     */   private String formatString(int length) {
/* 613 */     StringBuffer b = new StringBuffer(length * 4);
/* 614 */     b.append("%8.4g");
/* 615 */     for (int i = 1; i < length; i++) {
/* 616 */       b.append("|%8.4g");
/*     */     }
/* 618 */     return b.toString();
/*     */   }
/*     */   
/*     */   static Duplet makeDuplet(int n) {
/* 622 */     List l = new ArrayList();
/* 623 */     for (int i = 0; i < n; i++) {
/* 624 */       Duplet d = new Duplet(i);
/* 625 */       if (dupletAlphabet.contains(d)) {
/* 626 */         d = (Duplet)dupletAlphabet.tailSet(d).first();
/*     */       } else
/* 628 */         dupletAlphabet.add(d);
/* 629 */       l.add(d);
/*     */     }
/* 631 */     return makeDuplet(l);
/*     */   }
/*     */   
/*     */   static Duplet makeDuplet(IdGroup idg) {
/* 635 */     List l = new ArrayList();
/* 636 */     for (int i = 0; i < idg.getIdCount(); i++) {
/* 637 */       l.add(new Duplet(Integer.parseInt(idg.getIdentifier(i).getName())));
/*     */     }
/* 639 */     return makeDuplet(l);
/*     */   }
/*     */   
/*     */   static Duplet makeDuplet(List l) {
/* 643 */     Duplet d = new Duplet(l);
/* 644 */     if (dupletAlphabet.contains(d)) {
/* 645 */       return (Duplet)dupletAlphabet.tailSet(d).first();
/*     */     }
/* 647 */     return d;
/*     */   }
/*     */   
/*     */   static Duplet makeDupletInner(pal.tree.Node root)
/*     */   {
/* 652 */     if (root.isLeaf()) {
/* 653 */       return new Duplet(Integer.parseInt(root.getIdentifier().getName()));
/*     */     }
/* 655 */     List ss = new ArrayList();
/* 656 */     for (int i = 0; i < root.getChildCount(); i++) {
/* 657 */       ss.add(makeDuplet(root.getChild(i)));
/*     */     }
/* 659 */     return new Duplet(ss);
/*     */   }
/*     */   
/*     */   static Duplet makeDuplet(pal.tree.Node root)
/*     */   {
/* 664 */     Duplet d = makeDupletInner(root);
/* 665 */     if (dupletAlphabet.contains(d)) {
/* 666 */       return (Duplet)
/* 667 */         dupletAlphabet.tailSet(d).first();
/*     */     }
/* 669 */     dupletAlphabet.add(d);
/* 670 */     return d;
/*     */   }
/*     */   
/*     */   static boolean containsNode(Duplet root, Set nodeLeaves)
/*     */   {
/* 675 */     boolean result = false;
/* 676 */     Set rootLeaves = new HashSet();
/* 677 */     getLeafSet(root, rootLeaves);
/* 678 */     if (rootLeaves.equals(nodeLeaves)) {
/* 679 */       result = true;
/*     */     } else {
/* 681 */       for (int i = 0; i < root.getChildCount(); i++) {
/* 682 */         result = (result) || 
/* 683 */           (containsNode((Duplet)root.getChild(i), nodeLeaves));
/*     */       }
/*     */     }
/* 686 */     return result;
/*     */   }
/*     */   
/*     */ 
/*     */   static int[] getRightCount(Duplet random, Duplet tree, Map pert, Collection right, Collection wrong)
/*     */   {
/* 692 */     int[] result = new int[2];
/* 693 */     if (random.num_children < tree.num_children)
/* 694 */       return result;
/* 695 */     if (pert.containsKey(tree)) {
/* 696 */       result[1] += 1;
/* 697 */       Set nodeLeaves = new HashSet();
/* 698 */       getLeafSet(tree, nodeLeaves);
/* 699 */       if (containsNode(random, nodeLeaves)) {
/* 700 */         result[0] += 1;
/* 701 */         right.add(tree);
/*     */       } else {
/* 703 */         wrong.add(tree);
/*     */       }
/*     */     }
/* 706 */     for (int i = 0; i < tree.getChildCount(); i++) {
/* 707 */       int[] res_i = getRightCount(random, (Duplet)tree.getChild(i), 
/* 708 */         pert, right, wrong);
/* 709 */       for (int j = 0; j < res_i.length; j++) {
/* 710 */         result[j] += res_i[j];
/*     */       }
/*     */     }
/* 713 */     return result;
/*     */   }
/*     */   
/*     */ 
/* 717 */   static SortedSet dupletAlphabet = new TreeSet(Duplet.NODE_COMP);
/*     */   
/* 719 */   static final Comparator INT_COMP = new Comparator() {
/*     */     public int compare(Object o1, Object o2) {
/* 721 */       int[] i1 = (int[])o1;
/* 722 */       int[] i2 = (int[])o2;
/* 723 */       if (i1.length != i2.length)
/* 724 */         return i1.length < i2.length ? -1 : 1;
/* 725 */       for (int i = 0; i < i1.length; i++) {
/* 726 */         if (i1[i] != i2[i])
/* 727 */           return i1[i] < i2[i] ? -1 : 1;
/*     */       }
/* 729 */       return 0;
/*     */     }
/*     */     
/*     */     public boolean equals(Object o) {
/* 733 */       return o == this;
/*     */     }
/*     */   };
/*     */   
/*     */   static void annotateTree(Map m, pal.tree.Node node) throws Exception {
/* 738 */     if (node.isLeaf())
/* 739 */       return;
/* 740 */     Object obj = m.get(makeDuplet(node));
/* 741 */     if (obj != null) {
/* 742 */       ((AttributeIdentifier)node.getIdentifier()).setAttribute("L", obj.toString());
/*     */       
/* 744 */       ((AttributeIdentifier)node.getIdentifier()).setAttribute("B", new Integer(
/* 745 */         (int)Math.floor(((Double)obj).doubleValue())).toString());
/*     */     }
/* 747 */     for (int i = 0; i < node.getChildCount(); i++) {
/* 748 */       annotateTree(m, node.getChild(i));
/*     */     }
/*     */   }
/*     */   
/*     */   static void collapseTree(pal.tree.Node node) {
/* 753 */     if ((node.getIdentifier() != null) && 
/* 754 */       (node.getIdentifier().getName() != null) && 
/* 755 */       (!node.getIdentifier().getName().equals(""))) {
/* 756 */       ((AttributeIdentifier)node.getIdentifier()).setAttribute("Co", "Y");
/*     */     }
/* 758 */     for (int i = 0; i < node.getChildCount(); i++) {
/* 759 */       collapseTree(node.getChild(i));
/*     */     }
/*     */   }
/*     */   
/*     */   static void annotateTree(pal.tree.Node node, pal.tree.Tree target, IdGroup idG) throws Exception
/*     */   {
/* 765 */     if (node.isLeaf()) {
/* 766 */       return;
/*     */     }
/* 768 */     if ((node.getIdentifier() != null) && 
/* 769 */       (!node.getIdentifier().getName().equals(""))) {
/* 770 */       Iterator extNodes = Arrays.asList(NodeUtils.getExternalNodes(node))
/* 771 */         .iterator();
/* 772 */       List idL = new ArrayList();
/* 773 */       while (extNodes.hasNext()) {
/* 774 */         String name = ((pal.tree.Node)extNodes.next()).getIdentifier()
/* 775 */           .getName();
/* 776 */         if (idG.whichIdNumber(name) >= 0) {
/* 777 */           idL.add(name);
/*     */         }
/*     */       }
/* 780 */       if (idL.size() > 0) {
/* 781 */         String[] ids = new String[idL.size()];
/* 782 */         idL.toArray(ids);
/* 783 */         pal.tree.Node[] tExtNodes = NodeUtils.findByIdentifier(target.getRoot(), 
/* 784 */           ids);
/* 785 */         if (tExtNodes != null) {
/* 786 */           pal.tree.Node tNode = NodeUtils.getFirstCommonAncestor(tExtNodes);
/* 787 */           if ((tNode != null) && (!tNode.isLeaf())) {
/* 788 */             String name = tNode.getIdentifier() != null ? 
/* 789 */               tNode.getIdentifier().getName() : "";
/* 790 */             name = name + node.getIdentifier().getName();
/*     */             
/*     */ 
/* 793 */             tNode.setIdentifier(new Identifier(name));
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/* 798 */     for (int i = 0; i < node.getChildCount(); i++) {
/* 799 */       annotateTree(node.getChild(i), target, idG);
/*     */     }
/*     */   }
/*     */   
/*     */   void graphTree(pal.tree.Tree tree, Map support, File outDir, boolean hotspots) throws Exception
/*     */   {
/* 805 */     forester.tree.Tree tree1 = MaxLikelihoodTree.convert(tree);
/*     */     
/*     */     try
/*     */     {
/* 809 */       if (!hotspots) {
/* 810 */         SpeciesTreeSearch.annotateTreeWithSupport(
/* 811 */           makeDuplet(tree.getRoot()), tree1, tree, support);
/*     */       } else {
/* 813 */         ((TreePosteriorSearch)this).annotateTreeWithHotSpots(
/* 814 */           makeDuplet(tree.getRoot()), tree1, tree, support);
/*     */       }
/*     */     } catch (Exception exc) {
/* 817 */       exc.printStackTrace();
/*     */     }
/* 819 */     new ATVjframe(tree1).showWhole();
/*     */   }
/*     */   
/*     */   static void annotateTree(forester.tree.Tree tree, Iterator it) {
/* 823 */     Map nodes = new HashMap();
/* 824 */     for (Enumeration en = tree.getRoot().getAllExternalChildren()
/* 825 */           .elements(); 
/* 825 */           en.hasMoreElements();) {
/* 826 */       forester.tree.Node node = (forester.tree.Node)en.nextElement();
/* 827 */       nodes.put(node.getSeqName(), node);
/*     */     }
/* 829 */     while (it.hasNext()) {
/* 830 */       List row = (List)it.next();
/* 831 */       if (nodes.containsKey(row.get(0))) {
/* 832 */         forester.tree.Node node = (forester.tree.Node)nodes.get(
/* 833 */           row.get(0));
/* 834 */         String name = (String)row.get(2);
/* 835 */         name = name.replace('(', '_');
/* 836 */         name = name.replace(',', '_');
/* 837 */         name = name.replace('/', '_');
/* 838 */         name = name.replace(')', '_');
/* 839 */         name = name.replace('.', '_');
/* 840 */         node.setSpecies(name);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   static Duplet getIntersection(Duplet node, IdGroup ids) {
/* 846 */     if (node.isLeaf()) {
/* 847 */       if (ids.whichIdNumber(node.min_id) >= 0) {
/* 848 */         return node;
/*     */       }
/* 850 */       return null;
/*     */     }
/* 852 */     List s = new ArrayList();
/* 853 */     for (int i = 0; i < node.getChildCount(); i++) {
/* 854 */       Duplet next = getIntersection((Duplet)node.getChild(i), ids);
/* 855 */       if (next != null)
/* 856 */         s.add(next); }
/*     */     Duplet res;
/*     */     Duplet res;
/* 859 */     if (s.size() == 1) {
/* 860 */       res = (Duplet)s.get(0); } else { Duplet res;
/* 861 */       if (s.size() == 0) {
/* 862 */         res = null;
/*     */       } else
/* 864 */         res = new Duplet(s); }
/* 865 */     return res;
/*     */   }
/*     */ }


/* Location:              /home/lachlan/Desktop/psilc1.21/psilc.jar!/lc1/treefam/TreeSearch.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */