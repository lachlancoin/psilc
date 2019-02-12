/*     */ package lc1.dp;
/*     */ 
/*     */ import java.io.PrintStream;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Set;
/*     */ import lc1.util.Print;
/*     */ import org.biojava.bio.Annotation;
/*     */ import org.biojava.bio.dist.Distribution;
/*     */ import org.biojava.bio.dist.DistributionFactory;
/*     */ import org.biojava.bio.dp.EmissionState;
/*     */ import org.biojava.bio.dp.SimpleEmissionState;
/*     */ import org.biojava.bio.dp.SimpleMarkovModel;
/*     */ import org.biojava.bio.dp.State;
/*     */ import org.biojava.bio.seq.RNATools;
/*     */ import org.biojava.bio.seq.io.NameTokenization;
/*     */ import org.biojava.bio.seq.io.SymbolTokenization;
/*     */ import org.biojava.bio.symbol.Alignment;
/*     */ import org.biojava.bio.symbol.Alphabet;
/*     */ import org.biojava.bio.symbol.AlphabetManager;
/*     */ import org.biojava.bio.symbol.BasisSymbol;
/*     */ import org.biojava.bio.symbol.FiniteAlphabet;
/*     */ import org.biojava.bio.symbol.IllegalSymbolException;
/*     */ import org.biojava.bio.symbol.SimpleAlphabet;
/*     */ import org.biojava.bio.symbol.Symbol;
/*     */ import org.biojava.bio.symbol.SymbolList;
/*     */ import org.biojava.utils.SmallSet;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class AbstractCovarianceGrammar
/*     */   extends SimpleMarkovModel
/*     */ {
/*  38 */   protected static final DistributionFactory distFactory = DistributionFactory.DEFAULT;
/*     */   protected final FiniteAlphabet alpha;
/*     */   protected final FiniteAlphabet pair_alpha;
/*     */   protected SimpleAlphabet structuralAlphabet;
/*  42 */   protected static final int[] advance = { 1 };
/*     */   
/*     */   public void printSample(List[] sl) throws Exception
/*     */   {
/*  46 */     SymbolTokenization token = this.alpha.getTokenization("token");
/*  47 */     for (int i = 0; i < sl[0].size(); i++) {
/*  48 */       System.out.print(token.tokenizeSymbol((Symbol)sl[0].get(i)));
/*     */     }
/*  50 */     System.out.println();
/*  51 */     for (int i = 0; i < sl[1].size(); i++) {
/*  52 */       System.out.print(sl[1].get(i));
/*     */     }
/*  54 */     System.out.println();
/*     */   }
/*     */   
/*     */   protected EmissionState makeAndAddLoopState(Distribution dist)
/*     */     throws Exception
/*     */   {
/*  60 */     EmissionState loop = makeNewInsertState(".", 
/*  61 */       Annotation.EMPTY_ANNOTATION, 
/*  62 */       advance, 
/*  63 */       dist);
/*  64 */     addState(loop);
/*  65 */     return loop;
/*     */   }
/*     */   
/*     */   protected EmissionState makeAndAddLeftRightStates(Distribution dist) throws Exception {
/*  69 */     EmissionState pairState = makeNewInsertState("()", 
/*  70 */       Annotation.EMPTY_ANNOTATION, 
/*  71 */       advance, 
/*  72 */       dist);
/*  73 */     addState(pairState);
/*  74 */     return pairState;
/*     */   }
/*     */   
/*     */   protected void makeStructuralAlphabet() {
/*  78 */     Set emissionStates = new SmallSet();
/*  79 */     for (Iterator it = stateAlphabet().iterator(); it.hasNext();) {
/*  80 */       State st = (State)it.next();
/*  81 */       if ((st instanceof EmissionState)) emissionStates.add(st);
/*     */     }
/*  83 */     this.structuralAlphabet = new SimpleAlphabet(emissionStates);
/*  84 */     this.structuralAlphabet.putTokenization("token", 
/*  85 */       new NameTokenization(this.structuralAlphabet));
/*     */   }
/*     */   
/*     */   public AbstractCovarianceGrammar(FiniteAlphabet alpha, String struct, Alignment align) throws Exception {
/*  89 */     super(1, alpha, "Covariance grammar");
/*  90 */     this.alpha = alpha;
/*  91 */     this.pair_alpha = ((FiniteAlphabet)AlphabetManager.getCrossProductAlphabet(Arrays.asList(new Alphabet[] { alpha, alpha })));
/*  92 */     makeAndAddAllStates(struct, align);
/*  93 */     makeStructuralAlphabet();
/*     */   }
/*     */   
/*     */   public boolean canParse(String st) throws Exception {
/*  97 */     return canParse(RNATools.createRNA(st));
/*     */   }
/*     */   
/*     */   public boolean canParse(SymbolList sl) throws Exception {
/* 101 */     return canParse(sl, magicalState()).booleanValue();
/*     */   }
/*     */   
/*     */   private Boolean canParse(SymbolList sl, State st) throws Exception {
/* 105 */     Boolean canParse = null;
/* 106 */     System.err.println("push " + sl.seqString() + " " + st.getName());
/* 107 */     if ((st.equals(magicalState())) && (sl.length() == 0)) { canParse = new Boolean(true);
/* 108 */     } else if (sl.length() == 0) { canParse = new Boolean(false);
/*     */     } else {
/* 110 */       System.err.println("h1 " + sl.seqString());
/* 111 */       if (((st instanceof EmissionState)) && (!st.equals(magicalState()))) {
/* 112 */         System.err.println("h3 " + sl.seqString() + " " + 
/* 113 */           Print.toString(((EmissionState)st).getDistribution()));
/* 114 */         if (((EmissionState)st).getDistribution().getWeight(sl.symbolAt(1)) == 0.0D)
/* 115 */           canParse = new Boolean(false); else
/* 116 */           sl = sl.length() > 1 ? sl.subList(1, sl.length() - 1) : SymbolList.EMPTY_LIST;
/* 117 */         System.err.println("h4 " + sl.seqString());
/*     */       }
/* 119 */       System.err.println("h2 " + sl.seqString());
/* 120 */       if (canParse == null) {
/* 121 */         for (Iterator toStates = transitionsFrom(st).iterator(); toStates.hasNext();) {
/* 122 */           State next = (State)toStates.next();
/* 123 */           if ((next instanceof DoubletState)) {
/* 124 */             State next1 = ((DoubletState)next).getState(0);
/* 125 */             State next2 = ((DoubletState)next).getState(1);
/* 126 */             for (int i = sl.length(); i >= 1; i--) {
/* 127 */               if ((canParse(sl.subList(i, sl.length()), next2).booleanValue()) && 
/* 128 */                 (canParse(sl.subList(1, i), next1).booleanValue()))
/*     */               {
/* 130 */                 canParse = new Boolean(true);
/* 131 */                 break;
/*     */               }
/*     */               
/*     */             }
/*     */           }
/* 136 */           else if (canParse(sl, next).booleanValue()) {
/* 137 */             canParse = new Boolean(true);
/* 138 */             break;
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 144 */     if (canParse == null) canParse = new Boolean(false);
/* 145 */     System.err.println("pop " + sl.seqString() + " " + st.getName() + " " + canParse);
/* 146 */     return canParse;
/*     */   }
/*     */   
/*     */   protected EmissionState makeNewInsertState(String str, Annotation ann, int[] adv, Distribution dis) {
/* 150 */     return new SimpleEmissionState(str, ann, adv, dis);
/*     */   }
/*     */   
/*     */ 
/*     */   protected abstract void makeAndAddAllStates(String paramString, Alignment paramAlignment)
/*     */     throws Exception;
/*     */   
/*     */   protected List[] sample(State root)
/*     */     throws IllegalSymbolException
/*     */   {
/* 160 */     if (root.equals(magicalState())) {
/* 161 */       List symbols = new ArrayList();
/* 162 */       List structs = new ArrayList();
/* 163 */       return new List[] { symbols, structs };
/*     */     }
/* 165 */     if ((root instanceof DoubletState)) {
/* 166 */       DoubletState ds = (DoubletState)root;
/* 167 */       List[] left = sample(ds.getState(0));
/* 168 */       List[] right = sample(ds.getState(1));
/* 169 */       left[0].addAll(right[0]);
/* 170 */       left[1].addAll(right[1]);
/* 171 */       return left;
/*     */     }
/*     */     
/* 174 */     State next = (State)getWeights(root).sampleSymbol();
/* 175 */     List[] l = sample(next);
/* 176 */     if ((root instanceof EmissionState)) {
/* 177 */       Symbol sym = ((EmissionState)root).getDistribution().sampleSymbol();
/* 178 */       if (((sym instanceof BasisSymbol)) && (((BasisSymbol)sym).getSymbols().size() > 1)) {
/* 179 */         List syms = ((BasisSymbol)sym).getSymbols();
/*     */         
/* 181 */         l[0].add(syms.get(1));
/* 182 */         l[0].add(0, syms.get(0));
/* 183 */         l[1].add(")");
/* 184 */         l[1].add(0, "(");
/*     */       }
/*     */       else {
/* 187 */         l[0].add(sym);
/* 188 */         l[1].add(".");
/*     */       }
/*     */     }
/* 191 */     return l;
/*     */   }
/*     */   
/*     */   public List[] sample() throws IllegalSymbolException
/*     */   {
/* 196 */     return sample((State)getWeights(magicalState()).sampleSymbol());
/*     */   }
/*     */ }


/* Location:              /home/lachlan/Desktop/psilc1.21/psilc.jar!/lc1/dp/AbstractCovarianceGrammar.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */