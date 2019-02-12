/*     */ package lc1.dp;
/*     */ 
/*     */ import java.io.PrintStream;
/*     */ import java.lang.reflect.Constructor;
/*     */ import java.util.Arrays;
/*     */ import java.util.List;
/*     */ import lc1.phyl.FastLikelihoodCalculator;
/*     */ import lc1.phyl.ParameterizedRateMatrix;
/*     */ import lc1.phyl.ScaledRateMatrix;
/*     */ import lc1.phyl.WAG_GWF;
/*     */ import lc1.util.Print;
/*     */ import org.biojava.bio.symbol.IllegalSymbolException;
/*     */ import pal.alignment.SitePattern;
/*     */ import pal.datatype.DataType;
/*     */ import pal.substmodel.RateDistribution;
/*     */ import pal.substmodel.RateMatrix;
/*     */ import pal.tree.Tree;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class AlignmentHMM
/*     */   extends HmmerProfileHMM
/*     */ {
/*     */   Tree tree;
/*  28 */   FastLikelihoodCalculator lhc = null;
/*     */   
/*  30 */   private final char GAP = '-';
/*     */   
/*     */   String name;
/*     */   
/*  34 */   boolean COMMON_TRANSITIONS = true;
/*     */   ConversionMap cMap;
/*     */   NodeSum[] nodeSums;
/*     */   
/*     */   public Tree getTree()
/*     */   {
/*  40 */     return this.tree;
/*     */   }
/*     */   
/*     */   public double getTransition(State from, State to, int pos) {
/*  44 */     return getTransition(from, to);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public double[] prob(EmissionState emiss, SitePattern sp)
/*     */   {
/*  52 */     if (sp.getSequenceCount() <= 1)
/*  53 */       return super.prob(emiss, sp);
/*  54 */     ScaledRateMatrix rm = (ScaledRateMatrix)emiss.getSubstitutionModel();
/*  55 */     RateDistribution rates = emiss.getRateDistribution();
/*     */     
/*  57 */     double[] rate_categories = rates.getCategoryProbabilities();
/*     */     
/*  59 */     double[] result = new double[sp.numPatterns];
/*  60 */     Arrays.fill(result, 0.0D);
/*     */     try {
/*  62 */       for (int i = 0; i < rate_categories.length; i++) {
/*  63 */         rm.setParameter(rates.rate[i], rm.getNumParameters() - 1);
/*  64 */         if (this.lhc == null) {
/*  65 */           this.lhc = new FastLikelihoodCalculator(sp, this.tree);
/*     */         }
/*     */         else {
/*  68 */           this.lhc.updateSitePattern(sp);
/*     */         }
/*  70 */         this.lhc.setModel(rm);
/*  71 */         double[] resultInner = this.lhc.calculateSiteLogLikelihood();
/*  72 */         for (int j = 0; j < result.length; j++) {
/*  73 */           result[j] += 
/*  74 */             rate_categories[i] * Math.exp(resultInner[j]);
/*     */         }
/*     */       }
/*  77 */       double[] siteRes = new double[sp.getSiteCount()];
/*  78 */       for (int i = 0; i < siteRes.length; i++) {
/*  79 */         siteRes[i] = result[sp.alias[i]];
/*     */       }
/*  81 */       return siteRes;
/*     */     } catch (Throwable t) {
/*  83 */       t.printStackTrace();
/*  84 */       System.err.println(Print.toString(emiss.getDistribution()));
/*  85 */       System.err.println(this.tree);
/*  86 */       System.exit(0);
/*     */     }
/*  88 */     return null;
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
/*     */   AlignmentHMM(String name, int no_columns, DataType dt, int insertRates, int matchRates)
/*     */   {
/* 104 */     super(name, no_columns, dt, insertRates, matchRates);
/* 105 */     for (int i = 0; i < columns(); i++) {
/* 106 */       EmissionState m_i = getMatch(i);
/* 107 */       EmissionState i_i = getInsert(i);
/* 108 */       m_i.setAssociatedState(getDelete(i));
/* 109 */       i_i.setAssociatedState(getMatch(i));
/*     */     }
/* 111 */     this.nodeSums = new NodeSum[4];
/* 112 */     for (int i = 0; i < 4; i++) {
/* 113 */       this.nodeSums[i] = new NodeSum();
/*     */     }
/*     */   }
/*     */   
/*     */   public void setTransitions(SitePattern sitePatterns) throws IllegalSymbolException
/*     */   {
/* 119 */     if ((!this.COMMON_TRANSITIONS) && (sitePatterns.getSequenceCount() > 1)) {
/* 120 */       this.cMap = new ConversionMap(sitePatterns, this.tree);
/*     */     } else
/* 122 */       this.cMap = null;
/*     */   }
/*     */   
/*     */   public void setTreeModel(Tree tree, RateMatrix nullM) throws Exception {
/* 126 */     this.tree = tree;
/* 127 */     EmissionState i_0 = getInsert(0);
/* 128 */     EmissionState g = this.nullModel.gState();
/* 129 */     EmissionState c = cState();
/* 130 */     EmissionState j = jState();
/* 131 */     EmissionState n = nState();
/* 132 */     RateMatrix insertModel = getModifiedRateMatrix(nullM, 
/* 133 */       i_0.getDistribution());
/* 134 */     RateMatrix nullModel = getModifiedRateMatrix(nullM, g.getDistribution());
/*     */     
/* 136 */     g.setSubstModel(nullModel);
/* 137 */     c.setSubstModel(nullModel);
/* 138 */     j.setSubstModel(nullModel);
/* 139 */     n.setSubstModel(nullModel);
/* 140 */     for (int i = 0; i < columns(); i++) {
/* 141 */       EmissionState m_i = getMatch(i);
/* 142 */       EmissionState i_i = getInsert(i);
/* 143 */       RateMatrix rm = 
/* 144 */         getModifiedRateMatrix(nullM, 
/* 145 */         m_i.getDistribution());
/*     */       
/* 147 */       m_i.setSubstModel(rm);
/* 148 */       i_i.setSubstModel(insertModel);
/*     */     }
/*     */   }
/*     */   
/*     */   RateMatrix getModifiedRateMatrix(RateMatrix nullM, double[] d) throws Exception
/*     */   {
/* 154 */     Class clazz = nullM.getClass();
/* 155 */     if ((nullM instanceof ScaledRateMatrix)) {
/* 156 */       RateMatrix rm = getModifiedRateMatrix(((ScaledRateMatrix)nullM).getBaseRateMatrix(), d);
/* 157 */       ParameterizedRateMatrix result = new ScaledRateMatrix(rm);
/* 158 */       result.setParameter(nullM.getParameter(nullM.getNumParameters() - 1), result.getNumParameters() - 1);
/* 159 */       return result;
/*     */     }
/* 161 */     if ((nullM instanceof WAG_GWF)) {
/* 162 */       WAG_GWF rm = new WAG_GWF(new double[] { 0.0D }, d);
/*     */       
/* 164 */       return rm;
/*     */     }
/* 166 */     RateMatrix rm = (RateMatrix)clazz.getConstructor(new Class[] { d.getClass() }).newInstance(new Object[] { d });
/* 167 */     for (int i = 0; i < nullM.getNumParameters(); i++) {
/* 168 */       rm.setParameter(nullM.getParameter(i), i);
/*     */     }
/* 170 */     return rm;
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
/*     */   private void update(int i, State from, State to)
/*     */   {
/* 218 */     this.nodeSums[i].from = from;
/* 219 */     this.nodeSums[i].to = to;
/*     */   }
/*     */   
/*     */   private void update(int i, State from, State to, int number) {
/* 223 */     this.nodeSums[i].from = from;
/* 224 */     this.nodeSums[i].to = to;
/* 225 */     this.nodeSums[i].number = number;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public double transScore(State from, State to, int position)
/*     */   {
/* 233 */     return super.transScore(from, to);
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
/*     */   public boolean getNodeSums(State from, State to, int position)
/*     */   {
/* 255 */     int[] times = this.cMap.get(position, this.nodeSums);
/* 256 */     if ((from instanceof EmissionState.MatchDeleteState)) {
/* 257 */       State delete = ((EmissionState)from).getAssociatedState();
/* 258 */       if ((to instanceof EmissionState.MatchDeleteState)) {
/* 259 */         State deleteTo = ((EmissionState)to).getAssociatedState();
/* 260 */         update(0, from, to, 1);
/* 261 */         update(1, from, deleteTo);
/* 262 */         update(2, delete, to);
/* 263 */         update(3, delete, deleteTo);
/* 264 */       } else if ((to instanceof EmissionState.InsertState)) {
/* 265 */         if (times[2] > 0) {
/* 266 */           return false;
/*     */         }
/* 268 */         update(0, from, to);
/*     */       }
/*     */       else
/*     */       {
/* 272 */         update(1, from, to, 1);
/* 273 */         update(3, delete, to);
/*     */       }
/* 275 */     } else if ((from instanceof EmissionState.InsertState)) {
/* 276 */       State match = ((EmissionState)from).getAssociatedState();
/* 277 */       State delete = ((EmissionState)match).getAssociatedState();
/*     */       
/* 279 */       if ((to instanceof EmissionState.MatchDeleteState)) {
/* 280 */         if (times[1] > 0)
/*     */         {
/* 282 */           return false;
/*     */         }
/* 284 */         State deleteTo = ((EmissionState)to).getAssociatedState();
/* 285 */         update(0, from, to);
/* 286 */         update(2, match, to, 1);
/*     */         
/* 288 */         update(3, delete, deleteTo);
/*     */       }
/* 290 */       else if ((to instanceof EmissionState.InsertState)) {
/* 291 */         update(0, from, to);
/* 292 */         update(2, from, to);
/*     */       }
/*     */       else {
/* 295 */         return false;
/*     */       }
/*     */       
/*     */     }
/* 299 */     else if ((to instanceof EmissionState.MatchDeleteState)) {
/* 300 */       State delete = ((EmissionState)to).getAssociatedState();
/* 301 */       update(2, from, to, 1);
/* 302 */       if ((from instanceof DotState))
/* 303 */         update(3, from, delete);
/* 304 */     } else if ((to instanceof EmissionState.InsertState))
/*     */     {
/* 306 */       update(2, from, to, 1);
/*     */     }
/*     */     else
/*     */     {
/* 310 */       update(3, from, to, 1);
/*     */     }
/*     */     
/* 313 */     return true;
/*     */   }
/*     */   
/*     */   static class NodeSum
/*     */   {
/*     */     State from;
/*     */     State to;
/*     */     int number;
/*     */     List nodes;
/*     */   }
/*     */ }


/* Location:              /home/lachlan/Desktop/psilc1.21/psilc.jar!/lc1/dp/AlignmentHMM.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */