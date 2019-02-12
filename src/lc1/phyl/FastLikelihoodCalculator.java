/*     */ package lc1.phyl;
/*     */ 
/*     */ import java.util.Arrays;
/*     */ import java.util.Collection;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map;
/*     */ import java.util.Vector;
/*     */ import pal.alignment.SitePattern;
/*     */ import pal.datatype.DataType;
/*     */ import pal.eval.LikelihoodCalculator;
/*     */ import pal.misc.Identifier;
/*     */ import pal.misc.PalObjectEvent;
/*     */ import pal.misc.PalObjectListener;
/*     */ import pal.misc.Utils;
/*     */ import pal.substmodel.RateMatrix;
/*     */ import pal.tree.Node;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class FastLikelihoodCalculator
/*     */   implements LikelihoodCalculator
/*     */ {
/*     */   RootNode root_;
/*  50 */   double scaleParam = 1.0D;
/*     */   
/*  52 */   public void setScaleParam(double scale) { this.scaleParam = scale;
/*  53 */     for (Iterator it = this.treeNodeToLikelihoodNode.values().iterator(); it.hasNext();) {
/*  54 */       ((NNode)it.next()).modelChanged_ = true;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   int numberOfSites_;
/*     */   
/*     */   int numberOfStates_;
/*     */   
/*  63 */   Map treeNodeToLikelihoodNode = new HashMap();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   SitePattern sitePattern_;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  75 */   private static double THRESHOLD = 1.0E-12D;
/*     */   
/*     */ 
/*     */   private static final double MIN_PROB = 1.0E-40D;
/*     */   
/*     */ 
/*     */   private static final double MIN_TARGET_PROB = 1.0E-10D;
/*     */   
/*     */ 
/*     */   private FastLikelihoodCalculator(SitePattern pattern)
/*     */   {
/*  86 */     this.sitePattern_ = pattern;
/*  87 */     this.numberOfSites_ = pattern.getNumberOfPatterns();
/*  88 */     this.numberOfStates_ = pattern.getDataType().getNumStates();
/*     */   }
/*     */   
/*  91 */   public int getNumStates() { return this.numberOfStates_; }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public FastLikelihoodCalculator(SitePattern pattern, Tree tree)
/*     */   {
/*  98 */     this(pattern);
/*  99 */     setTree(tree);
/*     */   }
/*     */   
/*     */   public void setModel(RateMatrix model) {
/* 103 */     this.root_.setModel(model);
/* 104 */     for (Iterator it = this.treeNodeToLikelihoodNode.values().iterator(); it.hasNext();) {
/* 105 */       ((NNode)it.next()).setModel(model);
/*     */     }
/*     */   }
/*     */   
/*     */   public void setModel(Node node, RateMatrix model) {
/* 110 */     ((NNode)this.treeNodeToLikelihoodNode.get(node)).setModel(model);
/*     */   }
/*     */   
/*     */   public RateMatrix getModel(Node node) {
/* 114 */     return ((NNode)this.treeNodeToLikelihoodNode.get(node)).model_;
/*     */   }
/*     */   
/*     */   public void release() {
/*     */     try {
/* 119 */       this.root_.release();
/*     */     }
/*     */     catch (NullPointerException localNullPointerException) {}
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
/*     */   public final void setTree(Tree t)
/*     */   {
/* 139 */     if (this.root_ == null) {
/* 140 */       this.root_ = new RootNode(t.getRoot());
/*     */     } else {
/* 142 */       NNode newNode = this.root_.switchNodes(t.getRoot());
/* 143 */       if (newNode != this.root_) {
/* 144 */         throw new RuntimeException("Assertion error : new tree generates new Root NNode (tree probably contains only one branch)");
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 150 */     this.root_.setUp();
/*     */     
/* 152 */     this.root_.setupSequences(this.sitePattern_);
/*     */   }
/*     */   
/*     */   public final void updateSitePattern(SitePattern pattern) {
/* 156 */     this.sitePattern_ = pattern;
/* 157 */     this.root_.setupSequences(pattern);
/* 158 */     if (pattern.numPatterns != this.numberOfSites_) {
/* 159 */       this.numberOfSites_ = pattern.numPatterns;
/* 160 */       this.root_.setUp();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public double calculateLogLikelihood()
/*     */   {
/* 168 */     double lkl = this.root_.computeLikelihood();
/* 169 */     return lkl;
/*     */   }
/*     */   
/*     */ 
/* 173 */   public double[] calculateSiteLogLikelihood() { return this.root_.siteLikelihood(); }
/*     */   
/*     */   final NNode create(NNode parent, Node peer) {
/*     */     NNode newNode;
/*     */     NNode newNode;
/* 178 */     if (peer.getChildCount() == 0) {
/* 179 */       newNode = new LeafNode(parent, peer);
/*     */     }
/*     */     else {
/* 182 */       newNode = new InternalNode(parent, peer);
/*     */     }
/* 184 */     this.treeNodeToLikelihoodNode.put(peer, newNode);
/* 185 */     return newNode;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   abstract class NNode
/*     */     implements PalObjectListener
/*     */   {
/*     */     private double[][] transitionProbs_;
/*     */     
/*     */ 
/*     */ 
/* 198 */     double lastLength_ = Double.NEGATIVE_INFINITY;
/*     */     
/*     */     Node peer_;
/*     */     private byte[] sequence_;
/* 202 */     boolean modelChanged_ = false;
/*     */     RateMatrix model_;
/*     */     NNode parent_;
/*     */     
/* 206 */     public void parametersChanged(PalObjectEvent pe) { this.modelChanged_ = true; }
/*     */     
/*     */     public void structureChanged(PalObjectEvent pe)
/*     */     {
/* 210 */       this.modelChanged_ = true;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     public NNode(NNode parent, Node peer)
/*     */     {
/* 222 */       this.peer_ = peer;
/* 223 */       this.parent_ = parent;
/*     */     }
/*     */     
/*     */     public double[] getLogScaling() {
/* 227 */       return this.logscaling;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     public void setUp()
/*     */     {
/* 238 */       this.modelChanged_ = true;
/*     */       
/*     */ 
/* 241 */       if ((this.transitionProbs_ == null) || (FastLikelihoodCalculator.this.numberOfStates_ != this.transitionProbs_.length)) {
/* 242 */         this.transitionProbs_ = new double[FastLikelihoodCalculator.this.numberOfStates_][FastLikelihoodCalculator.this.numberOfStates_];
/*     */         
/* 244 */         this.siteStateProbabilities_ = new double[FastLikelihoodCalculator.this.numberOfSites_][FastLikelihoodCalculator.this.numberOfStates_];
/* 245 */         this.logscaling = new double[FastLikelihoodCalculator.this.numberOfSites_];
/* 246 */         Arrays.fill(this.logscaling, 0.0D);
/*     */       }
/*     */     }
/*     */     
/*     */     public void setModel(RateMatrix rm) {
/* 251 */       if ((this.model_ == null) || (this.model_ != rm)) {
/* 252 */         if (this.model_ != null) this.model_.removePalObjectListener(this);
/* 253 */         this.model_ = rm;
/* 254 */         this.model_.addPalObjectListener(this);
/*     */         
/* 256 */         this.modelChanged_ = true;
/*     */         
/*     */ 
/* 259 */         if (!(this instanceof FastLikelihoodCalculator.RootNode)) this.parent_.modelChanged_ = true;
/*     */       }
/*     */     }
/*     */     
/*     */     protected void setPeer(Node newPeer)
/*     */     {
/* 265 */       this.peer_ = newPeer;
/* 266 */       FastLikelihoodCalculator.this.treeNodeToLikelihoodNode.put(newPeer, this);
/*     */     }
/*     */     
/*     */     public final boolean isBranchLengthChanged() {
/* 270 */       return Math.abs(this.peer_.getBranchLength() - this.lastLength_) > FastLikelihoodCalculator.THRESHOLD;
/*     */     }
/*     */     
/*     */     protected final double[][] getSiteStateProbabilities()
/*     */     {
/* 275 */       return this.siteStateProbabilities_;
/*     */     }
/*     */     
/*     */     public final void setSequence(byte[] sequence)
/*     */     {
/* 280 */       this.sequence_ = Utils.getCopy(sequence);
/* 281 */       for (int i = 0; i < this.sequence_.length; i++) {
/* 282 */         if (sequence[i] >= FastLikelihoodCalculator.this.numberOfStates_) {
/* 283 */           this.sequence_[i] = -1;
/*     */         }
/*     */       }
/*     */     }
/*     */     
/*     */     public final boolean hasSequence() {
/* 289 */       return this.sequence_ != null;
/*     */     }
/*     */     
/* 292 */     public final byte[] getSequence() { return this.sequence_; }
/*     */     
/*     */ 
/*     */ 
/*     */     protected double[][] getTransitionProbabilities()
/*     */     {
/* 298 */       if ((this.modelChanged_) || (isBranchLengthChanged())) {
/* 299 */         double distance = this.peer_.getBranchLength();
/*     */         
/* 301 */         this.model_.setDistance(distance * FastLikelihoodCalculator.this.scaleParam);
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
/* 333 */         this.model_.getTransitionProbabilities(this.transitionProbs_);
/* 334 */         this.lastLength_ = distance;
/*     */       }
/*     */       
/* 337 */       return this.transitionProbs_;
/*     */     }
/*     */     
/* 340 */     protected double[][] getTransitionProbabilitiesReverse() { if ((this.modelChanged_) || (isBranchLengthChanged())) {
/* 341 */         double distance = this.peer_.getBranchLength();
/*     */         
/* 343 */         this.model_.setDistance(distance * FastLikelihoodCalculator.this.scaleParam);
/* 344 */         this.model_.getTransitionProbabilities(this.transitionProbs_);
/* 345 */         this.lastLength_ = distance;
/*     */       }
/* 347 */       return this.transitionProbs_;
/*     */     }
/*     */     
/*     */     private String toString(byte[] bs) {
/* 351 */       char[] cs = new char[bs.length];
/* 352 */       for (int i = 0; i < cs.length; i++) {
/* 353 */         cs[i] = ((char)(65 + bs[i]));
/*     */       }
/* 355 */       return new String(cs);
/*     */     }
/*     */     
/*     */     public void setupSequences(SitePattern sp) {
/* 359 */       Identifier id = this.peer_.getIdentifier();
/* 360 */       if (id != null) {
/* 361 */         int number = sp.whichIdNumber(id.getName());
/* 362 */         if (number >= 0) {
/* 363 */           if (this.sequence_ == null) {
/* 364 */             this.sequence_ = new byte[sp.pattern[number].length];
/*     */           }
/*     */           
/* 367 */           byte[] pattern = sp.pattern[number];
/* 368 */           for (int i = 0; i < this.sequence_.length; i++) {
/* 369 */             if (pattern[i] >= FastLikelihoodCalculator.this.numberOfStates_) {
/* 370 */               this.sequence_[i] = -1;
/*     */             } else {
/* 372 */               this.sequence_[i] = pattern[i];
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */     public abstract double[][] calculateSiteStateProbabilities();
/*     */     
/*     */ 
/*     */     public abstract FastLikelihoodCalculator.LeafNode[] getLeafNodes();
/*     */     
/*     */     public abstract NNode switchNodes(Node paramNode);
/*     */     
/*     */     public void release()
/*     */     {
/*     */       try
/*     */       {
/* 391 */         this.model_.removePalObjectListener(this);
/* 392 */         this.model_ = null;
/*     */       }
/*     */       catch (NullPointerException localNullPointerException) {}
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     private double[] logscaling;
/*     */     
/*     */ 
/*     */     private double[][] siteStateProbabilities_;
/*     */   }
/*     */   
/*     */ 
/*     */   class LeafNode
/*     */     extends FastLikelihoodCalculator.NNode
/*     */   {
/*     */     public LeafNode(FastLikelihoodCalculator.NNode parent, Node peer)
/*     */     {
/* 411 */       super(parent, peer);
/*     */     }
/*     */     
/*     */     public double computeLikelihood() {
/* 415 */       return 0.0D;
/*     */     }
/*     */     
/* 418 */     public boolean isLeaf() { return true; }
/*     */     
/*     */ 
/*     */     protected final void setPeer(Node newPeer)
/*     */     {
/* 423 */       if (!this.peer_.getIdentifier().getName().equals(newPeer.getIdentifier().getName())) {
/* 424 */         this.lastLength_ = Double.NEGATIVE_INFINITY;
/*     */       }
/* 426 */       this.peer_ = newPeer;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     public FastLikelihoodCalculator.NNode switchNodes(Node n)
/*     */     {
/* 434 */       if (n.getChildCount() == 0) {
/* 435 */         setPeer(n);
/* 436 */         return this;
/*     */       }
/* 438 */       return FastLikelihoodCalculator.this.create(this.parent_, n);
/*     */     }
/*     */     
/*     */ 
/*     */     public LeafNode[] getLeafNodes()
/*     */     {
/* 444 */       return new LeafNode[] { this };
/*     */     }
/*     */     
/*     */     public double[][] calculateSiteStateProbabilities() {
/* 448 */       if ((!this.modelChanged_) && (!isBranchLengthChanged())) {
/* 449 */         return null;
/*     */       }
/*     */       
/* 452 */       byte[] sequence = getSequence();
/* 453 */       double[][] probs = getTransitionProbabilitiesReverse();
/* 454 */       double[][] siteStateProbs = getSiteStateProbabilities();
/* 455 */       for (int site = 0; site < sequence.length; site++) {
/* 456 */         int endState = sequence[site];
/* 457 */         if (endState < 0) {
/* 458 */           for (int startState = 0; startState < FastLikelihoodCalculator.this.numberOfStates_; startState++) {
/* 459 */             siteStateProbs[site][startState] = 1.0D;
/*     */           }
/*     */           
/*     */         } else {
/* 463 */           for (int startState = 0; startState < FastLikelihoodCalculator.this.numberOfStates_; startState++) {
/* 464 */             siteStateProbs[site][startState] = probs[startState][endState];
/*     */           }
/*     */         }
/*     */       }
/* 468 */       return siteStateProbs;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   class InternalNode
/*     */     extends FastLikelihoodCalculator.NNode
/*     */   {
/*     */     private FastLikelihoodCalculator.NNode[] children_;
/*     */     
/*     */     private double[][][] childSiteStateProbs_;
/*     */     private double[][] childSiteLogScaling_;
/*     */     double[] endStateProbs_;
/*     */     
/*     */     public void release()
/*     */     {
/* 484 */       super.release();
/* 485 */       for (int i = 0; i < this.children_.length; i++) {
/* 486 */         this.children_[i].release();
/*     */       }
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
/*     */     public InternalNode(FastLikelihoodCalculator.NNode parent, Node peer)
/*     */     {
/* 500 */       super(parent, peer);
/* 501 */       this.parent_ = parent;
/* 502 */       this.children_ = new FastLikelihoodCalculator.NNode[peer.getChildCount()];
/* 503 */       for (int i = 0; i < this.children_.length; i++) {
/* 504 */         this.children_[i] = FastLikelihoodCalculator.this.create(this, peer.getChild(i));
/*     */       }
/* 506 */       this.childSiteStateProbs_ = new double[this.children_.length][][];
/* 507 */       this.childSiteLogScaling_ = new double[this.children_.length][];
/*     */     }
/*     */     
/*     */     public void setUp() {
/* 511 */       super.setUp();
/* 512 */       if ((this.endStateProbs_ == null) || (FastLikelihoodCalculator.this.numberOfStates_ != this.endStateProbs_.length)) {
/* 513 */         this.endStateProbs_ = new double[FastLikelihoodCalculator.this.numberOfStates_];
/*     */       }
/* 515 */       for (int i = 0; i < this.children_.length; i++) {
/* 516 */         this.children_[i].setUp();
/*     */       }
/*     */     }
/*     */     
/*     */     public void setupSequences(SitePattern sp) {
/* 521 */       super.setupSequences(sp);
/* 522 */       for (int i = 0; i < this.children_.length; i++) {
/* 523 */         this.children_[i].setupSequences(sp);
/*     */       }
/*     */     }
/*     */     
/*     */     public boolean isLeaf() {
/* 528 */       return false;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     private final boolean populateChildSiteStateProbs()
/*     */     {
/* 535 */       boolean allNull = true;
/* 536 */       for (int i = 0; i < this.children_.length; i++) {
/* 537 */         double[][] ss = this.children_[i].calculateSiteStateProbabilities();
/* 538 */         if (ss != null) {
/* 539 */           this.childSiteStateProbs_[i] = ss;
/* 540 */           this.childSiteLogScaling_[i] = this.children_[i].getLogScaling();
/* 541 */           allNull = false;
/*     */         }
/* 543 */         else if (this.childSiteStateProbs_[i] == null) {
/* 544 */           throw new RuntimeException("Assertion error : Not as should be!");
/*     */         }
/*     */       }
/*     */       
/* 548 */       return allNull;
/*     */     }
/*     */     
/*     */     protected final int getNumberOfChildren() {
/* 552 */       return this.children_.length;
/*     */     }
/*     */     
/*     */     public FastLikelihoodCalculator.NNode switchNodes(Node n) {
/* 556 */       int nc = n.getChildCount();
/* 557 */       if (nc == 0)
/*     */       {
/* 559 */         return FastLikelihoodCalculator.this.create(this.parent_, n);
/*     */       }
/* 561 */       if (nc != this.children_.length) {
/* 562 */         FastLikelihoodCalculator.NNode[] newChildren = new FastLikelihoodCalculator.NNode[nc];
/* 563 */         for (int i = 0; i < nc; i++) {
/* 564 */           if (i < this.children_.length) {
/* 565 */             newChildren[i] = this.children_[i].switchNodes(n.getChild(i));
/*     */           } else {
/* 567 */             newChildren[i] = FastLikelihoodCalculator.this.create(this.parent_, n.getChild(i));
/*     */           }
/*     */         }
/* 570 */         this.children_ = newChildren;
/*     */       } else {
/* 572 */         for (int i = 0; i < nc; i++) {
/* 573 */           this.children_[i] = this.children_[i].switchNodes(n.getChild(i));
/*     */         }
/*     */       }
/* 576 */       setPeer(n);
/* 577 */       return this;
/*     */     }
/*     */     
/*     */     public double[][] calculateSiteStateProbabilities()
/*     */     {
/* 582 */       if ((populateChildSiteStateProbs()) && (!this.modelChanged_) && (!isBranchLengthChanged())) {
/* 583 */         return null;
/*     */       }
/* 585 */       double[][] probs = getTransitionProbabilities();
/* 586 */       double[][] siteStateProbs = getSiteStateProbabilities();
/* 587 */       double[] logscal = getLogScaling();
/* 588 */       Arrays.fill(logscal, 0.0D);
/* 589 */       for (int i = 0; i < this.children_.length; i++) {
/* 590 */         for (int j = 0; j < logscal.length; j++) {
/* 591 */           logscal[j] += this.childSiteLogScaling_[i][j];
/*     */         }
/*     */       }
/*     */       
/* 595 */       for (int site = 0; site < siteStateProbs.length; site++) {
/* 596 */         for (int endState = 0; endState < FastLikelihoodCalculator.this.numberOfStates_; endState++) {
/* 597 */           double probOfEndState = this.childSiteStateProbs_[0][site][endState];
/* 598 */           for (int i = 1; i < this.childSiteStateProbs_.length; i++) {
/* 599 */             probOfEndState *= this.childSiteStateProbs_[i][site][endState];
/*     */           }
/* 601 */           this.endStateProbs_[endState] = probOfEndState;
/*     */         }
/* 603 */         double min_state = Double.POSITIVE_INFINITY;
/*     */         
/* 605 */         for (int startState = 0; startState < FastLikelihoodCalculator.this.numberOfStates_; startState++) {
/* 606 */           double probOfStartState = 0.0D;
/* 607 */           for (int endState = 0; endState < FastLikelihoodCalculator.this.numberOfStates_; endState++) {
/* 608 */             probOfStartState += probs[startState][endState] * this.endStateProbs_[endState];
/*     */           }
/* 610 */           siteStateProbs[site][startState] = probOfStartState;
/*     */           
/* 612 */           if ((probOfStartState != 0.0D) && (probOfStartState < min_state) && (probOfStartState < 1.0E-40D)) {
/* 613 */             min_state = probOfStartState;
/*     */           }
/*     */         }
/*     */         
/*     */ 
/*     */ 
/* 619 */         if (min_state < 1.0E-40D) {
/* 620 */           double scaling = 1.0E-10D / min_state;
/*     */           
/* 622 */           logscal[site] += Math.log(scaling);
/*     */           
/* 624 */           for (int startState = 0; startState < FastLikelihoodCalculator.this.numberOfStates_; startState++)
/*     */           {
/* 626 */             siteStateProbs[site][startState] *= scaling;
/*     */           }
/*     */         }
/*     */       }
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
/* 640 */       return siteStateProbs;
/*     */     }
/*     */     
/*     */     public double calculateFinal(double[] equilibriumProbs, int[] siteWeights) {
/* 644 */       double[] siteLikelihood = calculateFinal(equilibriumProbs);
/* 645 */       double logSum = 0.0D;
/* 646 */       for (int site = 0; site < FastLikelihoodCalculator.this.numberOfSites_; site++)
/*     */       {
/* 648 */         logSum += siteLikelihood[site] * siteWeights[site];
/*     */       }
/*     */       
/* 651 */       return logSum;
/*     */     }
/*     */     
/*     */     public double[] calculateFinal(double[] equilibriumProbs) {
/* 655 */       populateChildSiteStateProbs();
/* 656 */       double[] siteLikelihood = new double[FastLikelihoodCalculator.this.numberOfSites_];
/* 657 */       double[] logscaling = getLogScaling();
/* 658 */       for (int site = 0; site < FastLikelihoodCalculator.this.numberOfSites_; site++) {
/* 659 */         double total = 0.0D;
/* 660 */         for (int state = 0; state < FastLikelihoodCalculator.this.numberOfStates_; state++) {
/* 661 */           double stateProb = this.childSiteStateProbs_[0][site][state];
/*     */           
/* 663 */           logscaling[site] = this.childSiteLogScaling_[0][site];
/* 664 */           for (int i = 1; i < this.childSiteStateProbs_.length; i++) {
/* 665 */             stateProb *= this.childSiteStateProbs_[i][site][state];
/*     */             
/* 667 */             logscaling[site] += this.childSiteLogScaling_[i][site];
/*     */           }
/*     */           
/* 670 */           total += equilibriumProbs[state] * stateProb;
/*     */         }
/*     */         
/* 673 */         siteLikelihood[site] = (Math.log(total) - logscaling[site]);
/*     */         
/*     */ 
/* 676 */         if (Double.isInfinite(siteLikelihood[site])) {
/* 677 */           throw new RuntimeException("is infinite " + total + " " + logscaling[site]);
/*     */         }
/*     */       }
/*     */       
/*     */ 
/* 682 */       return siteLikelihood;
/*     */     }
/*     */     
/*     */ 
/*     */     public FastLikelihoodCalculator.LeafNode[] getLeafNodes()
/*     */     {
/* 688 */       Vector v = new Vector();
/* 689 */       for (int i = 0; i < this.children_.length; i++) {
/* 690 */         FastLikelihoodCalculator.LeafNode[] clns = this.children_[i].getLeafNodes();
/* 691 */         for (int j = 0; j < clns.length; j++) {
/* 692 */           v.addElement(clns[j]);
/*     */         }
/*     */       }
/* 695 */       FastLikelihoodCalculator.LeafNode[] lns = new FastLikelihoodCalculator.LeafNode[v.size()];
/* 696 */       v.copyInto(lns);
/* 697 */       return lns;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   class RootNode
/*     */     extends FastLikelihoodCalculator.InternalNode
/*     */   {
/*     */     double[] equilibriumProbabilities_;
/*     */     
/*     */     int[] siteWeightings_;
/*     */     
/*     */ 
/*     */     public RootNode(Node peer)
/*     */     {
/* 712 */       this(peer, null);
/*     */     }
/*     */     
/*     */     public RootNode(Node peer, double[] equilibriumProbabilities) {
/* 716 */       super(null, peer);
/* 717 */       this.equilibriumProbabilities_ = equilibriumProbabilities;
/*     */     }
/*     */     
/* 720 */     public double[] siteLikelihood() { return calculateFinal(this.equilibriumProbabilities_); }
/*     */     
/*     */     public double computeLikelihood()
/*     */     {
/* 724 */       double lh = calculateFinal(this.equilibriumProbabilities_, this.siteWeightings_);
/* 725 */       this.modelChanged_ = false;
/* 726 */       return lh;
/*     */     }
/*     */     
/*     */     public void setModel(RateMatrix rm) {
/* 730 */       super.setModel(rm);
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
/* 754 */       this.equilibriumProbabilities_ = rm.getEquilibriumFrequencies();
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     public void setupSequences(SitePattern sp)
/*     */     {
/* 761 */       super.setupSequences(sp);
/* 762 */       this.siteWeightings_ = sp.weight;
/*     */     }
/*     */   }
/*     */ }


/* Location:              /home/lachlan/Desktop/psilc1.21/psilc.jar!/lc1/phyl/FastLikelihoodCalculator.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */