/*     */ package lc1.phyl;
/*     */ 
/*     */ import pal.tree.LogParameterizedTree;
/*     */ import pal.tree.Node;
/*     */ import pal.tree.NodeUtils;
/*     */ import pal.tree.ParameterizedTree;
/*     */ import pal.tree.ParameterizedTree.ParameterizedTreeBase;
/*     */ import pal.tree.SimpleTree;
/*     */ import pal.tree.Tree;
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
/*     */ public class ScaledTree
/*     */   extends ParameterizedTree.ParameterizedTreeBase
/*     */   implements ParameterizedTree
/*     */ {
/*  34 */   double scale = 1.0D;
/*  35 */   double scaleSE = 1.0D;
/*  36 */   double min_scale = 0.01D;
/*  37 */   double max_scale = 100.0D;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public ScaledTree(Tree t, double range)
/*     */   {
/*  45 */     if ((t instanceof LogParameterizedTree)) t = ((LogParameterizedTree)t).getBaseTree();
/*  46 */     setBaseTree(new SimpleTree(t));
/*  47 */     this.min_scale = (1.0D / range);
/*  48 */     this.max_scale = range;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  57 */     for (int i = 0; i < getNumParameters(); i++)
/*     */     {
/*  59 */       setParameter(getDefaultValue(i), i);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public int getNumParameters()
/*     */   {
/*  68 */     return 1;
/*     */   }
/*     */   
/*     */ 
/*     */   public void setParameter(double param, int n)
/*     */   {
/*  74 */     scale(param / this.scale);
/*     */     
/*  76 */     this.scale = param;
/*     */   }
/*     */   
/*     */   protected void scale(double scaling)
/*     */   {
/*  81 */     for (int i = 0; i < getExternalNodeCount(); i++)
/*     */     {
/*  83 */       Node n = getExternalNode(i);
/*  84 */       n.setBranchLength(scaling * n.getBranchLength());
/*     */     }
/*     */     
/*  87 */     for (int i = 0; i < getInternalNodeCount(); i++)
/*     */     {
/*  89 */       Node node = getInternalNode(i);
/*  90 */       node.setBranchLength(scaling * node.getBranchLength());
/*     */     }
/*  92 */     NodeUtils.lengths2Heights(getRoot());
/*     */   }
/*     */   
/*     */   public String getParameterizationInfo() {
/*  96 */     return "";
/*     */   }
/*     */   
/*     */   public double getParameter(int n)
/*     */   {
/* 101 */     return this.scale;
/*     */   }
/*     */   
/*     */   public void setParameterSE(double paramSE, int n)
/*     */   {
/* 106 */     this.scaleSE = paramSE;
/*     */   }
/*     */   
/*     */   public double getLowerLimit(int n)
/*     */   {
/* 111 */     return this.min_scale;
/*     */   }
/*     */   
/*     */   public double getUpperLimit(int n)
/*     */   {
/* 116 */     return this.max_scale;
/*     */   }
/*     */   
/*     */   public double getDefaultValue(int n)
/*     */   {
/* 121 */     return 1.0D;
/*     */   }
/*     */ }


/* Location:              /home/lachlan/Desktop/psilc1.21/psilc.jar!/lc1/phyl/ScaledTree.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */