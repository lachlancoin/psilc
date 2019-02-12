/*     */ package lc1.phyl;
/*     */ 
/*     */ import pal.misc.IdGroup;
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
/*     */ public class CoalescentTree
/*     */   extends SimpleTree
/*     */ {
/*  20 */   double totalHeight = 0.0D;
/*     */   
/*     */   private int numClusters;
/*     */   private Node newCluster;
/*     */   private int besti;
/*     */   private int abi;
/*     */   private int bestj;
/*     */   private int abj;
/*     */   private int[] alias;
/*     */   
/*     */   public CoalescentTree(IdGroup m)
/*     */   {
/*  32 */     if (m.getIdCount() < 3)
/*     */     {
/*  34 */       new IllegalArgumentException("LESS THAN 3 TAXA IN DISTANCE MATRIX");
/*     */     }
/*     */     
/*  37 */     init(m);
/*     */     for (;;)
/*     */     {
/*  40 */       findNextPair();
/*  41 */       if (this.numClusters == 3) {
/*     */         break;
/*     */       }
/*     */       
/*     */ 
/*  46 */       newCluster();
/*     */     }
/*     */     
/*     */ 
/*  50 */     finish();
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
/*     */   private void init(IdGroup m)
/*     */   {
/*  66 */     this.numClusters = m.getIdCount();
/*  67 */     for (int i = 0; i < this.numClusters; i++)
/*     */     {
/*  69 */       Node tmp = NodeFactory.createNode();
/*  70 */       tmp.setIdentifier(m.getIdentifier(i));
/*  71 */       getRoot().addChild(tmp);
/*     */     }
/*     */     
/*  74 */     this.alias = new int[this.numClusters];
/*  75 */     for (int i = 0; i < this.numClusters; i++)
/*     */     {
/*  77 */       this.alias[i] = i;
/*     */     }
/*     */   }
/*     */   
/*     */   private void finish()
/*     */   {
/*  83 */     this.totalHeight += -Math.log(Math.random());
/*  84 */     getRoot().setNodeHeight(this.totalHeight);
/*     */     
/*  86 */     NodeUtils.heights2Lengths(getRoot());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private void findNextPair()
/*     */   {
/*  96 */     this.besti = ((int)Math.floor(Math.random() * this.numClusters));
/*  97 */     this.bestj = ((int)Math.floor(Math.random() * (this.numClusters - 1)));
/*  98 */     if (this.bestj == this.besti) { this.bestj += 1;
/*     */     }
/* 100 */     this.abi = this.alias[this.besti];
/* 101 */     this.abj = this.alias[this.bestj];
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private void newCluster()
/*     */   {
/* 108 */     this.totalHeight += -Math.log(Math.random());
/* 109 */     NodeUtils.joinChilds(getRoot(), this.besti, this.bestj);
/* 110 */     getRoot().getChild(Math.min(this.besti, this.bestj)).setNodeHeight(this.totalHeight);
/*     */     
/* 112 */     for (int i = this.bestj; i < this.numClusters - 1; i++)
/*     */     {
/* 114 */       this.alias[i] = this.alias[(i + 1)];
/*     */     }
/* 116 */     this.numClusters -= 1;
/*     */   }
/*     */ }


/* Location:              /home/lachlan/Desktop/psilc1.21/psilc.jar!/lc1/phyl/CoalescentTree.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */