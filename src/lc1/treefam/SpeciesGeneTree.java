/*     */ package lc1.treefam;
/*     */ 
/*     */ import pal.misc.Parameterized;
/*     */ import pal.tree.NodeUtils;
/*     */ import pal.tree.ParameterizedTree;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ class SpeciesGeneTree
/*     */   implements Parameterized
/*     */ {
/*     */   int taxonParams;
/*     */   int[] treeP;
/*     */   int treeParams;
/*     */   ParameterizedTree taxTree;
/*     */   ParameterizedTree[] tree;
/*     */   
/*     */   SpeciesGeneTree(ParameterizedTree taxonTree, ParameterizedTree[] tree)
/*     */   {
/*  23 */     this.taxTree = taxonTree;
/*  24 */     this.tree = tree;
/*  25 */     this.taxonParams = this.taxTree.getNumParameters();
/*  26 */     this.treeP = new int[tree.length + 1];
/*  27 */     this.treeP[0] = 0;
/*  28 */     for (int i = 1; i <= tree.length; i++) {
/*  29 */       this.treeP[i] = (tree[(i - 1)].getNumParameters() + this.treeP[(i - 1)]);
/*     */     }
/*  31 */     this.treeParams = this.treeP[tree.length];
/*     */   }
/*     */   
/*  34 */   public double[] treePrior() { double[] prior = { 0.0D, 0.0D };
/*  35 */     for (int i = 0; i < this.tree.length; i++)
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  41 */       double[] logUn = ((GeneTree)this.tree[i]).logUnnormalizedPdf();
/*  42 */       prior[0] += logUn[0];
/*  43 */       prior[1] += logUn[1];
/*     */     }
/*  45 */     return prior;
/*     */   }
/*     */   
/*  48 */   public int getNumParameters() { return this.treeParams + this.taxonParams; }
/*     */   
/*     */   private int getIndex(int n)
/*     */   {
/*  52 */     int i = 0;
/*  53 */     while (this.treeP[(i + 1)] <= n) {
/*  54 */       i++;
/*     */     }
/*  56 */     return i;
/*     */   }
/*     */   
/*  59 */   public double getDefaultValue(int n) { if (n < this.treeParams) {
/*  60 */       int i = getIndex(n);
/*  61 */       return this.tree[i].getDefaultValue(n - this.treeP[i]);
/*     */     }
/*     */     
/*  64 */     return this.taxTree.getDefaultValue(n - this.treeParams);
/*     */   }
/*     */   
/*     */   public double getUpperLimit(int n) {
/*  68 */     if (n < this.treeParams) {
/*  69 */       int i = getIndex(n);
/*  70 */       return this.tree[i].getUpperLimit(n - this.treeP[i]);
/*     */     }
/*     */     
/*  73 */     return this.taxTree.getUpperLimit(n - this.treeParams);
/*     */   }
/*     */   
/*     */   public double getLowerLimit(int n) {
/*  77 */     if (n < this.treeParams) {
/*  78 */       int i = getIndex(n);
/*  79 */       return this.tree[i].getLowerLimit(n - this.treeP[i]);
/*     */     }
/*     */     
/*  82 */     return Math.max(this.taxTree.getLowerLimit(n - this.treeParams), 0.001D);
/*     */   }
/*     */   
/*     */   public double getParameter(int n) {
/*  86 */     if (n < this.treeParams) {
/*  87 */       int i = getIndex(n);
/*  88 */       return this.tree[i].getParameter(n - this.treeP[i]);
/*     */     }
/*     */     
/*  91 */     return this.taxTree.getParameter(n - this.treeParams);
/*     */   }
/*     */   
/*     */ 
/*     */   public void setParameter(double d, int n)
/*     */   {
/*  97 */     if (n < this.treeParams) {
/*  98 */       int i = getIndex(n);
/*  99 */       this.tree[i].setParameter(d, n - this.treeP[i]);
/*     */ 
/*     */     }
/*     */     else
/*     */     {
/*     */ 
/* 105 */       this.taxTree.setParameter(d, n - this.treeParams);
/* 106 */       NodeUtils.lengths2Heights(this.taxTree.getRoot());
/* 107 */       for (int i = 0; i < this.tree.length; i++) {
/* 108 */         ((GeneTree)this.tree[i]).recalculateTree();
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public String getParameterizationInfo()
/*     */   {
/* 115 */     return "";
/*     */   }
/*     */   
/*     */   public void setParameterSE(double paramSE, int n) {}
/*     */ }


/* Location:              /home/lachlan/Desktop/psilc1.21/psilc.jar!/lc1/treefam/SpeciesGeneTree.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */