/*     */ package lc1.dp;
/*     */ 
/*     */ import java.io.PrintStream;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import lc1.util.Print;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ class NMap
/*     */ {
/*     */   private double[][] tScore;
/*     */   private double[] bScore;
/*     */   private double[] eScore;
/*     */   public final int[][] perms;
/*     */   public final int[] limits;
/*     */   public final int[] match;
/*     */   public final int[] first_pos;
/*  27 */   private Map pointers = new HashMap();
/*     */   
/*     */ 
/*     */ 
/*     */   public static void main(String[] args)
/*     */   {
/*  33 */     int[] limits = { 2, 3, 4 };
/*  34 */     NMap nm = new NMap(limits);
/*  35 */     Iterator it = Combinatorics.count(limits);
/*  36 */     it.next();
/*  37 */     while (it.hasNext()) {
/*  38 */       int[] pos = (int[])it.next();
/*  39 */       nm.set(nm.perms[1], pos, new Point(nm.perms[3], new int[] { 0, 1 }, 100.0D * Math.random()));
/*     */     }
/*  41 */     it = Combinatorics.count(limits);
/*  42 */     it.next();
/*  43 */     while (it.hasNext()) {
/*  44 */       int[] pos = (int[])it.next();
/*  45 */       System.out.println(nm.get(nm.perms[1], pos));
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   NMap(int[] limits)
/*     */   {
/*  54 */     this.limits = limits;
/*  55 */     int len = limits.length;
/*  56 */     this.perms = Combinatorics.combinations(len);
/*  57 */     this.match = Combinatorics.match(len);
/*  58 */     this.first_pos = Combinatorics.first_pos(len);
/*  59 */     for (int i = 0; i < this.perms.length; i++)
/*     */     {
/*     */ 
/*     */ 
/*  63 */       this.pointers.put(this.perms[i], new PMatrix(limits));
/*     */     }
/*  65 */     this.pointers.put(this.match, new PMatrix(limits));
/*     */   }
/*     */   
/*     */   public void initialiseMatrix(double[] bScore, double[][] tScore, double[] eScore) {
/*  69 */     this.bScore = bScore;
/*  70 */     this.tScore = tScore;
/*  71 */     this.eScore = eScore;
/*     */   }
/*     */   
/*     */   public String toString() {
/*  75 */     String str = new String();
/*  76 */     Iterator i = this.pointers.keySet().iterator();
/*  77 */     while (i.hasNext()) {
/*  78 */       int[] key = (int[])i.next();
/*  79 */       str = str + "\n" + Print.toString(key) + ": " + this.pointers.get(key).toString();
/*     */     }
/*  81 */     return str;
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
/*     */   public Point get(int[] perm, int[] pos)
/*     */   {
/*  96 */     boolean zero = true;
/*  97 */     for (int i = 0; i < pos.length; i++) {
/*  98 */       if (pos[i] < 0) return Point.NULL_TRACE;
/*  99 */       if (pos[i] > 0) {
/* 100 */         zero = false;
/*     */       }
/*     */     }
/* 103 */     if (zero) {
/* 104 */       return new Point(perm, this.first_pos, this.bScore[(countOnes(perm) - 1)]);
/*     */     }
/*     */     
/*     */ 
/* 108 */     Point p = ((PMatrix)this.pointers.get(perm)).get(pos);
/* 109 */     if (p == null) return Point.NULL_TRACE;
/* 110 */     return p;
/*     */   }
/*     */   
/*     */ 
/*     */   public void set(int[] perm, int[] pos, Point val)
/*     */   {
/* 116 */     ((PMatrix)this.pointers.get(perm)).set(pos, val);
/*     */   }
/*     */   
/*     */   public boolean include(int[] state, int[] prev_state)
/*     */   {
/* 121 */     if (state.equals(this.match)) return true;
/* 122 */     for (int i = 0; i < state.length; i++) {
/* 123 */       if ((state[i] == 1) && (prev_state[i] == 0))
/* 124 */         return false;
/*     */     }
/* 126 */     return true;
/*     */   }
/*     */   
/*     */   int countOnes(int[] model)
/*     */   {
/* 131 */     int res = 0;
/* 132 */     for (int i = 0; i < model.length; i++) {
/* 133 */       if (model[i] == 1) {
/* 134 */         res++;
/*     */       }
/*     */     }
/* 137 */     return res;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Point getBest(int[] model, int[] pos)
/*     */   {
/* 149 */     Point lastTrace = get(this.match, pos);
/* 150 */     Point trace = new Point(this.match, pos, lastTrace.score + this.tScore[(this.match.length - 1)][(countOnes(model) - 1)]);
/* 151 */     for (int i = 0; i < this.perms.length; i++) {
/* 152 */       if (include(model, this.perms[i])) {
/* 153 */         Point point = get(this.perms[i], pos);
/* 154 */         if (point != null) {
/* 155 */           double score = point.score + this.tScore[(countOnes(this.perms[i]) - 1)][(countOnes(model) - 1)];
/* 156 */           if (score > trace.score) {
/* 157 */             trace.perm = this.perms[i];
/* 158 */             trace.score = score;
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/* 163 */     return trace;
/*     */   }
/*     */   
/*     */   public Point getBestFinal()
/*     */   {
/* 168 */     Point trace = new Point(this.match, this.limits, get(this.match, this.limits).score);
/* 169 */     for (int i = 0; i < this.perms.length; i++) {
/* 170 */       Point point = get(this.perms[i], this.limits);
/* 171 */       if (point != null) {
/* 172 */         double score = point.score + this.eScore[(countOnes(this.perms[i]) - 1)];
/* 173 */         if (score > trace.score) {
/* 174 */           trace.perm = this.perms[i];
/* 175 */           trace.score = score;
/*     */         }
/*     */       }
/*     */     }
/* 179 */     return trace;
/*     */   }
/*     */ }


/* Location:              /home/lachlan/Desktop/psilc1.21/psilc.jar!/lc1/dp/NMap.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */