/*     */ package lc1.phyl;
/*     */ 
/*     */ import pal.distance.DistanceMatrix;
/*     */ import pal.tree.Node;
/*     */ import pal.tree.NodeFactory;
/*     */ import pal.tree.NodeUtils;
/*     */ import pal.tree.SimpleTree;
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
/*     */ public class NeighborJoiningTree
/*     */   extends SimpleTree
/*     */ {
/*     */   private int numClusters;
/*     */   private Node newCluster;
/*     */   private int besti;
/*     */   private int abi;
/*     */   private int bestj;
/*     */   private int abj;
/*     */   private int[] alias;
/*     */   private double[][] distance;
/*     */   private double[] r;
/*     */   private double scale;
/*     */   
/*     */   public NeighborJoiningTree(DistanceMatrix m)
/*     */   {
/*  35 */     if (m.getSize() < 3)
/*     */     {
/*  37 */       new IllegalArgumentException("LESS THAN 3 TAXA IN DISTANCE MATRIX");
/*     */     }
/*  39 */     if (!m.isSymmetric())
/*     */     {
/*  41 */       new IllegalArgumentException("UNSYMMETRIC DISTANCE MATRIX");
/*     */     }
/*     */     
/*  44 */     init(m);
/*     */     
/*  46 */     while (this.numClusters > 2)
/*     */     {
/*     */ 
/*  49 */       findNextPair();
/*  50 */       newBranchLengths();
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*  55 */       newCluster();
/*     */     }
/*     */     
/*     */ 
/*  59 */     findNextPair();
/*  60 */     newBranchLengths();
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
/*     */   private double getDist(int a, int b)
/*     */   {
/*  79 */     return this.distance[this.alias[a]][this.alias[b]];
/*     */   }
/*     */   
/*     */   private void init(DistanceMatrix m)
/*     */   {
/*  84 */     this.numClusters = m.getSize();
/*     */     
/*  86 */     this.distance = m.getClonedDistances();
/*     */     
/*  88 */     for (int i = 0; i < this.numClusters; i++)
/*     */     {
/*  90 */       Node tmp = NodeFactory.createNode();
/*  91 */       tmp.setIdentifier(m.getIdentifier(i));
/*  92 */       getRoot().addChild(tmp);
/*     */     }
/*     */     
/*  95 */     this.alias = new int[this.numClusters];
/*  96 */     for (int i = 0; i < this.numClusters; i++)
/*     */     {
/*  98 */       this.alias[i] = i;
/*     */     }
/*     */     
/* 101 */     this.r = new double[this.numClusters];
/*     */   }
/*     */   
/*     */   private void finish()
/*     */   {
/* 106 */     if ((this.besti != 0) && (this.bestj != 0))
/*     */     {
/* 108 */       getRoot().getChild(0).setBranchLength(updatedDistance(this.besti, this.bestj, 0));
/*     */     }
/* 110 */     else if ((this.besti != 1) && (this.bestj != 1))
/*     */     {
/* 112 */       getRoot().getChild(1).setBranchLength(updatedDistance(this.besti, this.bestj, 1));
/*     */     }
/*     */     else
/*     */     {
/* 116 */       getRoot().getChild(2).setBranchLength(updatedDistance(this.besti, this.bestj, 2));
/*     */     }
/* 118 */     this.distance = null;
/*     */     
/*     */ 
/* 121 */     NodeUtils.lengths2Heights(getRoot());
/*     */   }
/*     */   
/*     */   private void findNextPair()
/*     */   {
/* 126 */     for (int i = 0; i < this.numClusters; i++)
/*     */     {
/* 128 */       this.r[i] = 0.0D;
/* 129 */       for (int j = 0; j < this.numClusters; j++)
/*     */       {
/* 131 */         this.r[i] += getDist(i, j);
/*     */       }
/*     */     }
/*     */     
/* 135 */     this.besti = 0;
/* 136 */     this.bestj = 1;
/* 137 */     double smax = -1.0D;
/*     */     
/* 139 */     this.scale = (this.numClusters == 2 ? 1.0D : 1.0D / (this.numClusters - 2));
/* 140 */     for (int i = 0; i < this.numClusters - 1; i++)
/*     */     {
/* 142 */       for (int j = i + 1; j < this.numClusters; j++)
/*     */       {
/* 144 */         double sij = (this.r[i] + this.r[j]) * this.scale - getDist(i, j);
/*     */         
/* 146 */         if (sij > smax)
/*     */         {
/* 148 */           smax = sij;
/* 149 */           this.besti = i;
/* 150 */           this.bestj = j;
/*     */         }
/*     */       }
/*     */     }
/* 154 */     this.abi = this.alias[this.besti];
/* 155 */     this.abj = this.alias[this.bestj];
/*     */   }
/*     */   
/*     */   private void newBranchLengths()
/*     */   {
/* 160 */     double dij = getDist(this.besti, this.bestj);
/* 161 */     double li = (dij + (this.r[this.besti] - this.r[this.bestj]) * this.scale) * 0.5D;
/*     */     
/* 163 */     if (li > dij) { li = dij - Math.min(1.0E-8D, dij / 2.0D);
/* 164 */     } else if (li < 0.0D) { li = Math.min(dij / 2.0D, 1.0E-8D);
/*     */     }
/* 166 */     double lj = dij - li;
/*     */     
/* 168 */     getRoot().getChild(this.besti).setBranchLength(li);
/* 169 */     getRoot().getChild(this.bestj).setBranchLength(lj);
/*     */   }
/*     */   
/*     */ 
/*     */   private void newCluster()
/*     */   {
/* 175 */     for (int k = 0; k < this.numClusters; k++)
/*     */     {
/* 177 */       if ((k != this.besti) && (k != this.bestj))
/*     */       {
/* 179 */         int ak = this.alias[k];
/* 180 */         this.distance[ak][this.abi] = (this.distance[this.abi][ak] = updatedDistance(this.besti, this.bestj, k));
/*     */       }
/*     */     }
/* 183 */     this.distance[this.abi][this.abi] = 0.0D;
/*     */     
/*     */ 
/* 186 */     NodeUtils.joinChilds(getRoot(), this.besti, this.bestj);
/*     */     
/*     */ 
/* 189 */     for (int i = this.bestj; i < this.numClusters - 1; i++)
/*     */     {
/* 191 */       this.alias[i] = this.alias[(i + 1)];
/*     */     }
/*     */     
/* 194 */     this.numClusters -= 1;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private double updatedDistance(int i, int j, int k)
/*     */   {
/* 203 */     return (getDist(k, i) + getDist(k, j) - getDist(i, j)) * 0.5D;
/*     */   }
/*     */ }


/* Location:              /home/lachlan/Desktop/psilc1.21/psilc.jar!/lc1/phyl/NeighborJoiningTree.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */