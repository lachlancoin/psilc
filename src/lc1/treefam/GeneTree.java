/*     */ package lc1.treefam;
/*     */ 
/*     */ import JSci.maths.statistics.PoissonDistribution;
/*     */ import gnu.trove.TObjectDoubleHashMap;
/*     */ import java.io.PushbackReader;
/*     */ import java.io.StringReader;
/*     */ import java.util.HashSet;
/*     */ import java.util.Set;
/*     */ import pal.alignment.SimpleAlignment;
/*     */ import pal.alignment.SitePattern;
/*     */ import pal.datatype.TwoStates;
/*     */ import pal.eval.SimpleLikelihoodCalculator;
/*     */ import pal.misc.Identifier;
/*     */ import pal.misc.SimpleIdGroup;
/*     */ import pal.substmodel.TwoStateModel;
/*     */ import pal.tree.AttributeNode;
/*     */ import pal.tree.Node;
/*     */ import pal.tree.NodeUtils;
/*     */ import pal.tree.ParameterizedTree;
/*     */ import pal.tree.ParameterizedTree.ParameterizedTreeBase;
/*     */ import pal.tree.ReadTree;
/*     */ import pal.tree.SimpleTree;
/*     */ import pal.tree.Tree;
/*     */ import pal.tree.TreeParseException;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class GeneTree
/*     */   extends ParameterizedTree.ParameterizedTreeBase
/*     */   implements ParameterizedTree
/*     */ {
/*     */   TwoStateModel substM;
/*     */   double mean_dupl_time;
/*     */   double log_mean_dupl_time;
/*  36 */   TObjectDoubleHashMap duplNodes = new TObjectDoubleHashMap();
/*     */   AttributeNode[] duplNodeList;
/*     */   static final boolean VERBOSE = false;
/*     */   int dupl_node_count;
/*     */   String name;
/*     */   
/*  42 */   public double[] logUnnormalizedPdf() { return logUnnormalizedPdf((AttributeNode)getRoot()); }
/*     */   
/*     */ 
/*     */ 
/*     */   private double averageNoDuplicationsBeforeNextSpeciation(AttributeNode node)
/*     */   {
/*  48 */     Boolean dupl = (Boolean)node.getAttribute("duplication");
/*  49 */     if ((dupl == null) || (!dupl.booleanValue()) || (node.isLeaf())) return 0.0D;
/*  50 */     double child_tot = 0.0D;
/*  51 */     for (int i = 0; i < node.getChildCount(); i++) {
/*  52 */       child_tot += averageNoDuplicationsBeforeNextSpeciation((AttributeNode)node.getChild(i));
/*     */     }
/*  54 */     return 1.0D + child_tot / node.getChildCount();
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
/*     */   private double duplicationLogOdds(Node node)
/*     */   {
/*  67 */     Node lca = ((SDI.NodeSet)((AttributeNode)node).getAttribute("species")).lca();
/*  68 */     double height = lca.getNodeHeight();
/*  69 */     double lscore = 0.0D;
/*  70 */     for (int i = 0; i < node.getChildCount(); i++) {
/*  71 */       Node child_lca = ((SDI.NodeSet)((AttributeNode)node.getChild(i)).getAttribute("species")).lca();
/*  72 */       if (child_lca == lca) throw new RuntimeException("this is not a proper speciation");
/*  73 */       double child_height = child_lca.getNodeHeight();
/*  74 */       if (child_height >= height) return Double.POSITIVE_INFINITY;
/*  75 */       PoissonDistribution pois = new PoissonDistribution((height - child_height) / this.mean_dupl_time);
/*  76 */       double avg_dupl = 0.0D;
/*  77 */       avg_dupl += averageNoDuplicationsBeforeNextSpeciation((AttributeNode)node.getChild(i));
/*  78 */       lscore += Math.log(pois.probability(avg_dupl));
/*     */     }
/*     */     
/*  81 */     return lscore;
/*     */   }
/*     */   
/*     */   private double[] logUnnormalizedPdf(AttributeNode node) {
/*  85 */     double[] res = { 0.0D, 0.0D };
/*  86 */     Boolean dupl = (Boolean)node.getAttribute("duplication");
/*  87 */     for (int i = 0; i < node.getChildCount(); i++) {
/*  88 */       double[] res1 = logUnnormalizedPdf((AttributeNode)node.getChild(i));
/*  89 */       res[0] += res1[0];
/*  90 */       res[1] += res1[1];
/*     */     }
/*  92 */     double log_dupl_prob = 0.0D;
/*  93 */     double log_del_prob = 0.0D;
/*  94 */     if ((!node.isLeaf()) && (dupl != null) && (!dupl.booleanValue()))
/*     */     {
/*     */ 
/*  97 */       log_dupl_prob = duplicationLogOdds(node);
/*     */     }
/*     */     try
/*     */     {
/* 101 */       if ((dupl != null) && (dupl.booleanValue()))
/*     */       {
/* 103 */         Node species = ((SDI.NodeSet)node.getAttribute("species")).lca();
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
/* 115 */         Set left = new HashSet();
/* 116 */         Set right = new HashSet();
/* 117 */         addSpeciesBelowNode(node.getChild(0), left);
/* 118 */         addSpeciesBelowNode(node.getChild(1), right);
/*     */         
/* 120 */         Tree sub_tree = new ReadTree(new PushbackReader(
/* 121 */           new StringReader(new SimpleTree(species).toString())));
/* 122 */         sub_tree.createNodeList();
/*     */         
/*     */ 
/* 125 */         Identifier[] ids = new Identifier[sub_tree.getExternalNodeCount()];
/* 126 */         char[][] alignC = new char[ids.length][2];
/* 127 */         for (int i = 0; i < sub_tree.getExternalNodeCount(); i++) {
/* 128 */           Node leaf = sub_tree.getExternalNode(i);
/* 129 */           alignC[i][0] = 
/* 130 */             (left.contains(leaf.getIdentifier().getName()) ? 49 : 
/* 131 */             48);
/* 132 */           alignC[i][1] = (right.contains(
/* 133 */             leaf.getIdentifier().getName()) ? 49 : 48);
/* 134 */           ids[i] = leaf.getIdentifier();
/*     */         }
/*     */         
/* 137 */         SitePattern align = 
/* 138 */           SitePattern.getSitePattern(new SimpleAlignment(new SimpleIdGroup(
/* 139 */           ids), alignC, TwoStates.DEFAULT_INSTANCE));
/* 140 */         if (align.getSequenceCount() >= 2) {
/* 141 */           SimpleLikelihoodCalculator lh = new SimpleLikelihoodCalculator(
/* 142 */             align, sub_tree, this.substM);
/* 143 */           double likelihood = lh.calculateLogLikelihood();
/*     */           
/* 145 */           for (int i = 0; i < align.numPatterns; i++) {
/* 146 */             log_del_prob += 
/* 147 */               Math.log(lh.getPartial(sub_tree.getRoot(), i)[1]);
/*     */           }
/*     */         }
/*     */       }
/*     */       
/*     */ 
/* 153 */       res[0] += log_dupl_prob;
/* 154 */       res[1] += log_del_prob;
/* 155 */       double sum = log_dupl_prob + log_del_prob;
/* 156 */       if (sum != 0.0D) {
/* 157 */         ((AttributeIdentifier)node.getIdentifier()).setAttribute("L", sum);
/*     */       }
/*     */     }
/*     */     catch (TreeParseException exc)
/*     */     {
/* 162 */       exc.printStackTrace();
/*     */     }
/*     */     
/*     */ 
/* 166 */     return res;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public int getNumParameters()
/*     */   {
/* 173 */     return this.duplNodes.size();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private void recalibrateDuplNodeHeights(AttributeNode node)
/*     */   {
/* 184 */     if (this.duplNodes.containsKey(node))
/*     */     {
/* 186 */       double lengths = this.duplNodes.get(node);
/*     */       
/* 188 */       Node species_lca = ((SDI.NodeSet)node.getAttribute("species")).lca();
/* 189 */       double speciesHeight = species_lca.getNodeHeight();
/*     */       double currentHeight;
/* 191 */       double currentHeight; if (!node.isRoot()) {
/* 192 */         double parentHeight = node.getParent().getNodeHeight();
/*     */         
/*     */ 
/*     */ 
/* 196 */         currentHeight = speciesHeight + 
/* 197 */           lengths * (parentHeight - speciesHeight);
/*     */       }
/*     */       else {
/* 200 */         currentHeight = speciesHeight + lengths;
/*     */       }
/*     */       
/* 203 */       node.setNodeHeight(currentHeight);
/*     */     }
/* 205 */     for (int i = 0; i < node.getChildCount(); i++)
/*     */     {
/*     */ 
/* 208 */       AttributeNode child = (AttributeNode)node.getChild(i);
/*     */       
/*     */ 
/* 211 */       recalibrateDuplNodeHeights(child);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setParameter(double param, int n)
/*     */   {
/* 220 */     AttributeNode node = this.duplNodeList[n];
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 225 */     this.duplNodes.put(node, param);
/* 226 */     recalibrateDuplNodeHeights(node);
/* 227 */     NodeUtils.heights2Lengths(getRoot());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getParameterizationInfo()
/*     */   {
/* 237 */     return "";
/*     */   }
/*     */   
/*     */   public double getParameter(int n) {
/* 241 */     return this.duplNodes.get(this.duplNodeList[n]);
/*     */   }
/*     */   
/*     */ 
/*     */   public void setParameterSE(double paramSE, int n) {}
/*     */   
/*     */   public double getLowerLimit(int n)
/*     */   {
/* 249 */     return 0.001D;
/*     */   }
/*     */   
/*     */   public double getUpperLimit(int n)
/*     */   {
/* 254 */     if (this.duplNodeList[n].isRoot()) return 100.0D;
/* 255 */     return 0.99D;
/*     */   }
/*     */   
/*     */   public double getDefaultValue(int n) {
/* 259 */     return 0.5D;
/*     */   }
/*     */   
/*     */   private void addSpeciesBelowNode(Node node, Set s)
/*     */   {
/* 264 */     if (node.isLeaf()) {
/* 265 */       s.add(((AttributeIdentifier)node.getIdentifier()).getAttribute("S"));
/*     */     } else {
/* 267 */       for (int i = 0; i < node.getChildCount(); i++) {
/* 268 */         addSpeciesBelowNode(node.getChild(i), s);
/*     */       }
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public GeneTree(SDI sdi, Tree familyTree, String treeName, double mean_dupl_time, double perc_deleted)
/*     */     throws Exception
/*     */   {
/* 293 */     this.substM = new TwoStateModel(new double[] { perc_deleted, 1.0D - perc_deleted });
/* 294 */     this.mean_dupl_time = mean_dupl_time;
/* 295 */     this.name = treeName;
/* 296 */     this.log_mean_dupl_time = Math.log(mean_dupl_time);
/* 297 */     Object[] obj = sdi.inferDuplications(familyTree, treeName);
/* 298 */     Set duplications = (Set)obj[1];
/* 299 */     familyTree = (Tree)obj[0];
/*     */     
/*     */ 
/* 302 */     setBaseTree(familyTree);
/* 303 */     resetHeightsToSpeciesHeights();
/* 304 */     this.duplNodeList = ((AttributeNode[])duplications.toArray(new AttributeNode[0]));
/* 305 */     this.dupl_node_count = this.duplNodeList.length;
/* 306 */     for (int i = 0; i < this.duplNodeList.length; i++)
/*     */     {
/*     */ 
/* 309 */       this.duplNodes.put(this.duplNodeList[i], 0.0D);
/*     */     }
/*     */     
/*     */ 
/* 313 */     for (int i = 0; i < this.dupl_node_count; i++)
/*     */     {
/* 315 */       setParameter(getDefaultValue(i), i);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public void recalculateTree()
/*     */   {
/* 322 */     resetHeightsToSpeciesHeights();
/* 323 */     for (int i = 0; i < this.dupl_node_count; i++) {
/* 324 */       setParameter(getParameter(i), i);
/*     */     }
/*     */   }
/*     */   
/*     */   private void resetHeightsToSpeciesHeights() {
/* 329 */     for (int i = 0; i < getExternalNodeCount(); i++) {
/* 330 */       AttributeNode node = (AttributeNode)getExternalNode(i);
/* 331 */       Node species_lca = ((SDI.NodeSet)node.getAttribute("species")).lca();
/* 332 */       node.setNodeHeight(species_lca.getNodeHeight());
/*     */     }
/* 334 */     for (int i = 0; i < getInternalNodeCount(); i++) {
/* 335 */       AttributeNode node = (AttributeNode)getInternalNode(i);
/* 336 */       Node species_lca = ((SDI.NodeSet)node.getAttribute("species")).lca();
/* 337 */       node.setNodeHeight(species_lca.getNodeHeight());
/* 338 */       if (this.duplNodes.contains(node)) { this.duplNodes.put(node, 0.0D);
/*     */       }
/*     */     }
/* 341 */     AttributeNode node = (AttributeNode)getRoot();
/* 342 */     Node species_lca = ((SDI.NodeSet)node.getAttribute("species")).lca();
/* 343 */     node.setNodeHeight(species_lca.getNodeHeight());
/* 344 */     NodeUtils.heights2Lengths(getRoot());
/*     */   }
/*     */ }


/* Location:              /home/lachlan/Desktop/psilc1.21/psilc.jar!/lc1/treefam/GeneTree.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */