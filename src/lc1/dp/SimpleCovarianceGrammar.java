/*    */ package lc1.dp;
/*    */ 
/*    */ import java.util.Iterator;
/*    */ import org.biojava.bio.dist.Distribution;
/*    */ import org.biojava.bio.dist.DistributionFactory;
/*    */ import org.biojava.bio.dp.EmissionState;
/*    */ import org.biojava.bio.dp.IllegalTransitionException;
/*    */ import org.biojava.bio.seq.RNATools;
/*    */ import org.biojava.bio.symbol.Alignment;
/*    */ import org.biojava.bio.symbol.FiniteAlphabet;
/*    */ import org.biojava.bio.symbol.IllegalAlphabetException;
/*    */ import org.biojava.bio.symbol.IllegalSymbolException;
/*    */ import org.biojava.bio.symbol.Symbol;
/*    */ 
/*    */ public class SimpleCovarianceGrammar extends AbstractCovarianceGrammar
/*    */ {
/*    */   private EmissionState pair;
/*    */   private EmissionState loop;
/*    */   private org.biojava.bio.dp.DotState pair_pair;
/*    */   
/*    */   public static void main(String[] args) throws Exception
/*    */   {
/* 23 */     SimpleCovarianceGrammar cfg = new SimpleCovarianceGrammar(
/* 24 */       RNATools.getRNA());
/*    */     
/* 26 */     cfg.printSample(cfg.sample());
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/* 37 */   private final double PAIR_TO_LOOP = 0.1D;
/*    */   
/* 39 */   private final double PAIR_TO_BIFUR = 0.1D;
/*    */   
/* 41 */   private final double LOOP_TO_LOOP = 0.1D;
/*    */   
/* 43 */   private final double MATCH_PROB = 0.9D;
/*    */   
/*    */   public SimpleCovarianceGrammar(FiniteAlphabet alpha) throws Exception
/*    */   {
/* 47 */     super(alpha, "", null);
/*    */   }
/*    */   
/*    */   protected void makeAndAddAllStates(String struct, Alignment align)
/*    */     throws Exception
/*    */   {
/* 53 */     this.pair = makeAndAddLeftRightStates(uniformDistribution(this.pair_alpha));
/* 54 */     this.loop = makeAndAddLoopState(uniformDistribution(this.alpha));
/* 55 */     this.pair_pair = new DoubletState(this.pair, this.pair);
/* 56 */     addState(this.pair_pair);
/* 57 */     connectModel();
/* 58 */     setTransitionWeights();
/*    */   }
/*    */   
/*    */   private Distribution uniformDistribution(FiniteAlphabet alpha) throws Exception
/*    */   {
/* 63 */     Distribution dist = AbstractCovarianceGrammar.distFactory.createDistribution(alpha);
/* 64 */     for (Iterator it = alpha.iterator(); it.hasNext();) {
/* 65 */       dist.setWeight((Symbol)it.next(), 1.0D / alpha.size());
/*    */     }
/* 67 */     return dist;
/*    */   }
/*    */   
/*    */   protected void setTransitionWeights() throws Exception {
/* 71 */     getWeights(this.pair).setWeight(this.loop, 0.1D);
/* 72 */     getWeights(this.pair).setWeight(this.pair_pair, 0.1D);
/* 73 */     getWeights(this.pair).setWeight(this.pair, 0.8D);
/* 74 */     getWeights(this.loop).setWeight(magicalState(), 0.9D);
/* 75 */     getWeights(this.loop).setWeight(this.loop, 0.1D);
/* 76 */     getWeights(magicalState()).setWeight(this.pair, 1.0D);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   protected void connectModel()
/*    */     throws org.biojava.utils.ChangeVetoException, IllegalSymbolException, IllegalTransitionException, IllegalAlphabetException
/*    */   {
/* 86 */     createTransition(this.pair, this.loop);
/* 87 */     createTransition(this.pair, this.pair);
/* 88 */     createTransition(this.pair, this.pair_pair);
/* 89 */     createTransition(this.loop, magicalState());
/* 90 */     createTransition(this.loop, this.loop);
/* 91 */     createTransition(magicalState(), this.pair);
/*    */   }
/*    */ }


/* Location:              /home/lachlan/Desktop/psilc1.21/psilc.jar!/lc1/dp/SimpleCovarianceGrammar.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */