/*     */ package lc1.dp;
/*     */ 
/*     */ import java.util.Map;
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
/*     */ public class ProfileHMM
/*     */   extends MarkovModel
/*     */ {
/*     */   private final int columns;
/*     */   EmissionState j;
/*     */   EmissionState c;
/*     */   EmissionState n;
/*     */   DotState begin;
/*     */   DotState end;
/*     */   private final EmissionState[] matchStates;
/*     */   private final EmissionState[] insertStates;
/*     */   private final DotState[] deleteStates;
/*     */   public int[] matchIndices;
/*     */   public int[] deleteIndices;
/*     */   public int[] insertIndices;
/*     */   int nIndex;
/*     */   int cIndex;
/*     */   int jIndex;
/*     */   int beginIndex;
/*     */   int endIndex;
/*     */   
/*     */   public int columns()
/*     */   {
/*  75 */     return this.columns;
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
/*     */   public EmissionState getMatch(int indx)
/*     */     throws IndexOutOfBoundsException
/*     */   {
/*  92 */     if ((indx < 0) || (indx >= this.columns)) {
/*  93 */       throw new IndexOutOfBoundsException(
/*  94 */         "Match-state index must be within (0.." + this.columns + "), not " + indx);
/*     */     }
/*     */     
/*     */ 
/*  98 */     return this.matchStates[indx];
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
/*     */   public EmissionState getInsert(int indx)
/*     */     throws IndexOutOfBoundsException
/*     */   {
/* 120 */     if ((indx < 0) || (indx >= this.columns)) {
/* 121 */       throw new IndexOutOfBoundsException(
/* 122 */         "Insert-state index must be within (0.." + this.columns + "), not " + indx);
/*     */     }
/*     */     
/*     */ 
/* 126 */     return this.insertStates[indx];
/*     */   }
/*     */   
/*     */   public DotState getDelete(int indx)
/*     */     throws IndexOutOfBoundsException
/*     */   {
/* 132 */     if ((indx < 0) || (indx >= this.columns)) {
/* 133 */       throw new IndexOutOfBoundsException(
/* 134 */         "delete-state index must be within (1.." + this.columns + "), not " + indx);
/*     */     }
/*     */     
/*     */ 
/* 138 */     return this.deleteStates[indx];
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public ProfileHMM(String name, int columns, DataType dt, int insertRates, int matchRates)
/*     */   {
/* 147 */     super(name, dt);
/* 148 */     this.columns = columns;
/* 149 */     this.matchStates = new EmissionState[columns];
/* 150 */     this.insertStates = new EmissionState[columns];
/* 151 */     this.deleteStates = new DotState[columns];
/* 152 */     this.begin = new DotState("BEGIN");
/* 153 */     EmissionState iO = new EmissionState.InsertState("i-0", dt, insertRates);
/* 154 */     DotState dO = new DotState("d-0");
/* 155 */     EmissionState mO = new EmissionState.MatchDeleteState("m-0", dt, matchRates);
/* 156 */     this.j = new EmissionState.InsertState("j", dt, insertRates);
/* 157 */     this.c = new EmissionState.InsertState("c", dt, insertRates);
/* 158 */     this.n = new EmissionState.InsertState("n", dt, insertRates);
/* 159 */     this.insertStates[0] = iO;this.matchStates[0] = mO;this.deleteStates[0] = dO;
/* 160 */     addState(this.n);
/* 161 */     addState(this.j);
/* 162 */     addState(this.begin);
/* 163 */     addState(mO);
/* 164 */     addState(iO);
/* 165 */     addState(dO);
/*     */     
/* 167 */     for (int i = 1; i < columns; i++) {
/* 168 */       EmissionState mN = new EmissionState.MatchDeleteState("m-" + i, dt, matchRates);
/* 169 */       EmissionState iN = new EmissionState.InsertState("i-" + i, dt, insertRates);
/* 170 */       DotState dN = new DotState("d-" + i);
/* 171 */       addState(mN);addState(iN);addState(dN);
/* 172 */       this.matchStates[i] = mN;
/* 173 */       this.insertStates[i] = iN;
/* 174 */       this.deleteStates[i] = dN;
/* 175 */       mO = mN;
/* 176 */       iO = iN;
/* 177 */       dO = dN;
/*     */     }
/* 179 */     this.end = new DotState("END");
/* 180 */     addState(this.end);
/* 181 */     addState(this.c);
/*     */   }
/*     */   
/*     */   public EmissionState jState()
/*     */   {
/* 186 */     return this.j;
/*     */   }
/*     */   
/*     */   public EmissionState cState()
/*     */   {
/* 191 */     return this.c;
/*     */   }
/*     */   
/*     */   public EmissionState nState()
/*     */   {
/* 196 */     return this.n;
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
/*     */   public void set()
/*     */   {
/* 210 */     super.set();
/* 211 */     if (this.matchIndices == null) {
/* 212 */       this.matchIndices = new int[columns()];
/* 213 */       this.deleteIndices = new int[columns()];
/* 214 */       this.insertIndices = new int[columns()];
/* 215 */       for (int j = 0; j < columns(); j++) {
/* 216 */         this.matchIndices[j] = ((Integer)this.alias.get(getMatch(j))).intValue();
/* 217 */         this.insertIndices[j] = ((Integer)this.alias.get(getInsert(j))).intValue();
/* 218 */         this.deleteIndices[j] = ((Integer)this.alias.get(getDelete(j))).intValue();
/*     */       }
/* 220 */       this.nIndex = ((Integer)this.alias.get(nState())).intValue();
/* 221 */       this.cIndex = ((Integer)this.alias.get(cState())).intValue();
/* 222 */       this.jIndex = ((Integer)this.alias.get(jState())).intValue();
/* 223 */       this.beginIndex = ((Integer)this.alias.get(this.begin)).intValue();
/* 224 */       this.endIndex = ((Integer)this.alias.get(this.end)).intValue();
/*     */     }
/*     */   }
/*     */ }


/* Location:              /home/lachlan/Desktop/psilc1.21/psilc.jar!/lc1/dp/ProfileHMM.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */