/*     */ package lc1.dp;
/*     */ 
/*     */ import java.io.PrintStream;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import org.biojava.utils.SmallMap;
/*     */ import pal.datatype.DataType;
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
/*     */ public abstract class MarkovModel
/*     */ {
/*     */   private static final double sumCheckThreshold = 0.001D;
/*     */   DataType dt;
/*  33 */   Map weights = new HashMap();
/*  34 */   private Map statesToMap = new HashMap();
/*     */   
/*     */   public State[] states;
/*     */   
/*     */   public int[][] statesFrom;
/*     */   
/*     */   public int[][] statesTo;
/*     */   
/*     */   public double[][] transitionsTo;
/*     */   public double[][] transitionsFrom;
/*  44 */   public final DotState MAGIC = new DotState("!-1");
/*     */   
/*     */ 
/*  47 */   private Collection statesL = new ArrayList();
/*     */   protected Map alias;
/*     */   
/*  50 */   protected State addState(State st) { this.statesL.add(st);
/*  51 */     if (st == null) throw new RuntimeException("cannot add null state!");
/*  52 */     return st;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void release()
/*     */   {
/*  60 */     this.transitionsTo = null;
/*  61 */     this.transitionsFrom = null;
/*  62 */     this.statesTo = null;
/*  63 */     this.statesFrom = null;
/*  64 */     this.states = null;
/*  65 */     this.weights = null;
/*  66 */     this.statesToMap = null;
/*  67 */     this.statesL = null;
/*     */   }
/*     */   
/*  70 */   public void set() { if (this.states != null) return;
/*     */     try {
/*  72 */       for (Iterator it = this.weights.values().iterator(); it.hasNext();) {
/*  73 */         validateMapSum((Map)it.next());
/*     */       }
/*  75 */       for (Iterator it = this.statesL.iterator(); it.hasNext();) {
/*  76 */         State state = (State)it.next();
/*  77 */         if ((state instanceof EmissionState)) {
/*  78 */           ((EmissionState)state).validateSum();
/*     */         }
/*     */       }
/*     */     }
/*     */     catch (Exception exc)
/*     */     {
/*  84 */       exc.printStackTrace();
/*  85 */       System.exit(0);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*  90 */     this.states = ((State[])this.statesL.toArray(new State[0]));
/*     */     
/*     */ 
/*  93 */     this.alias = new HashMap();
/*  94 */     this.transitionsTo = new double[this.states.length][0];
/*  95 */     this.transitionsFrom = new double[this.states.length][0];
/*  96 */     for (int i = 0; i < this.states.length; i++)
/*     */     {
/*  98 */       if (this.states[i] == null) throw new RuntimeException("should not be null");
/*  99 */       this.alias.put(this.states[i], new Integer(i));
/*     */     }
/* 101 */     this.magicIndex = ((Integer)this.alias.get(this.MAGIC)).intValue();
/* 102 */     this.statesFrom = new int[this.states.length][0];
/* 103 */     this.statesTo = new int[this.states.length][0];
/*     */     
/* 105 */     for (int j = 0; j < this.states.length; j++) {
/* 106 */       State state = this.states[j];
/* 107 */       State[] sFrom = transitionsFrom(state);
/* 108 */       if (sFrom == null) { this.statesFrom[j] = new int[0];
/*     */       } else {
/* 110 */         this.statesFrom[j] = new int[sFrom.length];
/* 111 */         this.transitionsFrom[j] = new double[sFrom.length];
/* 112 */         for (int k = 0; k < sFrom.length; k++)
/*     */         {
/* 114 */           this.statesFrom[j][k] = ((Integer)this.alias.get(sFrom[k])).intValue();
/* 115 */           this.transitionsFrom[j][k] = getTransition(state, sFrom[k]);
/*     */         }
/*     */       }
/* 118 */       State[] sTo = transitionsTo(state);
/* 119 */       if (sTo == null) { this.statesTo[j] = new int[0];
/*     */       } else {
/* 121 */         this.statesTo[j] = new int[sTo.length];
/* 122 */         this.transitionsTo[j] = new double[sTo.length];
/* 123 */         for (int k = 0; k < sTo.length; k++)
/*     */         {
/*     */ 
/*     */ 
/* 127 */           this.statesTo[j][k] = ((Integer)this.alias.get(sTo[k])).intValue();
/* 128 */           this.transitionsTo[j][k] = getTransition(sTo[k], state);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public DataType getDataType()
/*     */   {
/* 138 */     return this.dt;
/*     */   }
/*     */   
/*     */   private State[] transitionsTo(State state)
/*     */   {
/* 143 */     if (this.statesToMap.containsKey(state)) {
/* 144 */       return (State[])((Collection)this.statesToMap.get(state)).toArray(new State[0]);
/*     */     }
/* 146 */     return null;
/*     */   }
/*     */   
/*     */   private State[] transitionsFrom(State state)
/*     */   {
/* 151 */     if (this.weights.containsKey(state)) return (State[])((Map)this.weights.get(state)).keySet().toArray(new State[0]);
/* 152 */     return null;
/*     */   }
/*     */   
/*     */   public String toString() {
/* 156 */     StringBuffer sb = new StringBuffer();
/* 157 */     for (Iterator it = this.weights.entrySet().iterator(); it.hasNext(); 
/* 158 */         sb.append("\n")) { sb.append(it.next());
/*     */     }
/* 160 */     return sb.toString();
/*     */   }
/*     */   
/*     */ 
/*     */   protected double getTransition(State from, State to)
/*     */   {
/*     */     try
/*     */     {
/* 168 */       return ((Double)((Map)this.weights.get(from)).get(to)).doubleValue();
/*     */     } catch (NullPointerException exc) {
/* 170 */       exc.printStackTrace();
/* 171 */       System.err.println(from + "->" + to);
/* 172 */       System.exit(0); }
/* 173 */     return 0.0D;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getName()
/*     */   {
/* 182 */     return this.name;
/*     */   }
/*     */   
/*     */   public MarkovModel(String name, DataType dt) {
/* 186 */     this.name = name;
/* 187 */     this.dt = dt;
/* 188 */     this.statesL.add(this.MAGIC);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setTransition(State from, State to, Double prob)
/*     */   {
/*     */     Map map;
/*     */     
/* 197 */     if (!this.weights.containsKey(from)) {
/* 198 */       Map map = new SmallMap();
/* 199 */       this.weights.put(from, map);
/*     */     }
/*     */     else {
/* 202 */       map = (Map)this.weights.get(from);
/*     */     }
/* 204 */     map.put(to, prob);
/*     */     Collection toSet;
/* 206 */     if (!this.statesToMap.containsKey(to)) {
/* 207 */       Collection toSet = new ArrayList();
/* 208 */       this.statesToMap.put(to, toSet);
/*     */     }
/*     */     else {
/* 211 */       toSet = (Collection)this.statesToMap.get(to);
/*     */     }
/* 213 */     toSet.add(from);
/*     */   }
/*     */   
/*     */   public double getTransition(State from, State to, int i) {
/* 217 */     return getTransition(from, to);
/*     */   }
/*     */   
/*     */   public double transScore(State from, State to) throws ArithmeticException {
/* 221 */     double prob = getTransition(from, to);
/* 222 */     if ((Double.isNaN(prob)) || (prob <= 0.0D)) throw new ArithmeticException("log prob is Nan or prob<0 " + prob);
/* 223 */     double result = Math.log(prob);
/*     */     
/* 225 */     return result;
/*     */   }
/*     */   
/* 228 */   public double transScore(State from, State to, int position) throws ArithmeticException { double prob = getTransition(from, to);
/* 229 */     if ((Double.isNaN(prob)) || (prob == 0.0D)) throw new ArithmeticException("log prob is Nan " + prob);
/* 230 */     return Math.log(prob);
/*     */   }
/*     */   
/*     */   private void checkTransitionSum() throws Exception
/*     */   {
/* 235 */     for (Iterator it = this.weights.values().iterator(); it.hasNext();) {
/*     */       try {
/* 237 */         validateMapSum((Map)it.next());
/*     */       } catch (Exception exc) {
/* 239 */         exc.printStackTrace();
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   static double validateSum(double sum) throws Exception
/*     */   {
/* 246 */     if (Math.abs(sum - 1.0D) > 0.001D)
/* 247 */       throw new Exception("Map does not sum to 1.  Sums to " + 
/* 248 */         sum);
/* 249 */     return 1.0D - sum;
/*     */   }
/*     */   
/*     */   private void validateMapSum(Map dist) throws Exception
/*     */   {
/* 254 */     Iterator iter = dist.keySet().iterator();
/* 255 */     double sum = 0.0D;
/* 256 */     while (iter.hasNext()) {
/* 257 */       State to = (State)iter.next();
/* 258 */       sum += ((Double)dist.get(to)).doubleValue();
/*     */     }
/*     */     
/*     */ 
/* 262 */     double diff = validateSum(sum);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public int magicIndex;
/*     */   
/*     */   String name;
/*     */   
/*     */   static void validateMapSum(double[] dist)
/*     */     throws Exception
/*     */   {
/* 274 */     double sum = 0.0D;
/* 275 */     for (int i = 0; i < dist.length; i++) {
/* 276 */       sum += dist[i];
/*     */     }
/* 278 */     validateSum(sum);
/*     */   }
/*     */ }


/* Location:              /home/lachlan/Desktop/psilc1.21/psilc.jar!/lc1/dp/MarkovModel.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */