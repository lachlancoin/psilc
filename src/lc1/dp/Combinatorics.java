/*     */ package lc1.dp;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ 
/*     */ public class Combinatorics
/*     */ {
/*     */   public static int getMaxCol(double[] d)
/*     */   {
/*  11 */     double min = Double.NEGATIVE_INFINITY;
/*  12 */     int min_col = -1;
/*  13 */     for (int i = 0; i < d.length; i++) {
/*  14 */       if (d[i] > min) {
/*  15 */         min = d[i];
/*  16 */         min_col = i;
/*     */       }
/*     */     }
/*  19 */     return min_col;
/*     */   }
/*     */   
/*     */   public static double sum(double[] d) {
/*  23 */     double sum = 0.0D;
/*  24 */     for (int i = 0; i < d.length; i++) {
/*  25 */       sum += d[i];
/*     */     }
/*  27 */     return sum;
/*     */   }
/*     */   
/*     */   public static double sum(double[] d, double[] weights) {
/*  31 */     double sum = 0.0D;
/*  32 */     double weightsum = 0.0D;
/*  33 */     for (int i = 0; i < d.length; i++) {
/*  34 */       sum += d[i] * weights[i];
/*  35 */       weightsum += weights[i];
/*     */     }
/*  37 */     return sum / weightsum;
/*     */   }
/*     */   
/*     */   public static int getMinCol(double[] d) {
/*  41 */     double min = Double.POSITIVE_INFINITY;
/*  42 */     int min_col = -1;
/*  43 */     for (int i = 0; i < d.length; i++) {
/*  44 */       if (d[i] < min) {
/*  45 */         min = d[i];
/*  46 */         min_col = i;
/*     */       }
/*     */     }
/*  49 */     return min_col;
/*     */   }
/*     */   
/*     */   public static boolean hasNegative(int[] perm) {
/*  53 */     for (int i = 0; i < perm.length; i++) {
/*  54 */       if (perm[i] < 0) {
/*  55 */         return true;
/*     */       }
/*     */     }
/*  58 */     return false;
/*     */   }
/*     */   
/*     */   public static int[] product(int[] lims) {
/*  62 */     int[] size = new int[lims.length];
/*  63 */     lims[(lims.length - 1)] += 1;
/*  64 */     for (int i = lims.length - 2; i >= 0; i--) {
/*  65 */       size[i] = (size[(i + 1)] * (lims[i] + 1));
/*     */     }
/*  67 */     return size;
/*     */   }
/*     */   
/*     */   public static int[][] combinations(int n)
/*     */   {
/*  72 */     ArrayList l = new ArrayList();
/*  73 */     Iterator i = comb(n);
/*  74 */     while (i.hasNext()) {
/*  75 */       l.add(i.next());
/*     */     }
/*  77 */     int[][] arr = new int[l.size() - 2][n];
/*  78 */     for (int j = 1; j < l.size() - 1; j++) {
/*  79 */       arr[(j - 1)] = ((int[])l.get(j));
/*     */     }
/*     */     
/*  82 */     return arr;
/*     */   }
/*     */   
/*     */   public static int binomialCoeff(int n, int m)
/*     */   {
/*  87 */     if ((n == m) || (n == 0) || (m == 0))
/*  88 */       return 1;
/*  89 */     int res = 1;
/*  90 */     for (int i = m + 1; i <= n; i++) {
/*  91 */       res *= i;
/*     */     }
/*  93 */     for (int i = 1; i <= n - m; i++) {
/*  94 */       res /= i;
/*     */     }
/*  96 */     return res;
/*     */   }
/*     */   
/*     */   public static int[] nonZeroPositions(int[] perm, int[] pos)
/*     */   {
/* 101 */     int[] res = new int[perm.length];
/* 102 */     int k = 0;
/* 103 */     for (int i = 0; i < perm.length; i++) {
/* 104 */       if ((perm[i] != 0) && (pos[i] != 0)) {
/* 105 */         res[k] = i;
/* 106 */         k++;
/*     */       }
/*     */     }
/* 109 */     int[] res1 = new int[k];
/* 110 */     System.arraycopy(res, 0, res1, 0, k);
/* 111 */     return res1;
/*     */   }
/*     */   
/*     */   public static int[] subtract(int[] a, int[] b) {
/*     */     try {
/* 116 */       if (a.length != b.length) {
/* 117 */         throw new Exception("arrays must have equal length");
/*     */       }
/*     */     } catch (Throwable t) {
/* 120 */       t.printStackTrace();
/* 121 */       return a;
/*     */     }
/* 123 */     int[] res = new int[a.length];
/* 124 */     for (int i = 0; i < a.length; i++) {
/* 125 */       a[i] -= b[i];
/*     */     }
/* 127 */     return res;
/*     */   }
/*     */   
/*     */   public static int[] match(int n) {
/* 131 */     int[] arr = new int[n];
/* 132 */     java.util.Arrays.fill(arr, 1);
/* 133 */     return arr;
/*     */   }
/*     */   
/*     */   public static int[] limit(Object[][] arrays) {
/* 137 */     int n = arrays.length;
/* 138 */     int[] arr = new int[n];
/* 139 */     for (int i = 0; i < n; i++) {
/* 140 */       arr[i] = arrays[i].length;
/*     */     }
/* 142 */     return arr;
/*     */   }
/*     */   
/*     */   public static int[] first_pos(int n) {
/* 146 */     int[] arr = new int[n];
/* 147 */     java.util.Arrays.fill(arr, 0);
/* 148 */     return arr;
/*     */   }
/*     */   
/*     */   public static NMap scores(int[] limits) {
/* 152 */     return new NMap(limits);
/*     */   }
/*     */   
/*     */   public static Iterator comb(int n) {
/* 156 */     if (n == 1) {
/* 157 */       new Iterator() {
/* 158 */         int no = -1;
/*     */         
/*     */         public boolean hasNext() {
/* 161 */           return this.no < 1;
/*     */         }
/*     */         
/*     */         public Object next() {
/* 165 */           this.no += 1;
/* 166 */           return new int[] { this.no };
/*     */         }
/*     */         
/*     */ 
/*     */         public void remove() {}
/*     */       };
/*     */     }
/* 173 */     new Iterator()
/*     */     {
/*     */       Iterator sub_it;
/*     */       int first;
/*     */       int[] sub;
/*     */       
/*     */       public boolean hasNext()
/*     */       {
/* 181 */         return (this.first == 1) || (this.sub_it.hasNext());
/*     */       }
/*     */       
/*     */       public Object next() {
/* 185 */         int[] result = new int[this.val$n];
/* 186 */         result[0] = this.first;
/* 187 */         if (this.first == 0) {
/* 188 */           this.sub = ((int[])this.sub_it.next());
/*     */         }
/* 190 */         System.arraycopy(this.sub, 0, result, 1, this.sub.length);
/* 191 */         this.first = (1 - this.first);
/* 192 */         return result;
/*     */       }
/*     */       
/*     */ 
/*     */       public void remove() {}
/*     */     };
/*     */   }
/*     */   
/*     */   public static Iterator nIterator(Object[][] arrays)
/*     */   {
/* 202 */     int[] limit = subtract(limit(arrays), match(arrays.length));
/* 203 */     new Iterator() {
/*     */       Iterator it;
/*     */       
/*     */       public boolean hasNext() {
/* 207 */         return this.it.hasNext();
/*     */       }
/*     */       
/*     */       public void remove() {}
/*     */       
/*     */       public Object next()
/*     */       {
/* 214 */         int[] pos = (int[])this.it.next();
/* 215 */         Object[] result = new Object[this.val$arrays.length];
/* 216 */         for (int j = 0; j < this.val$arrays.length; j++) {
/* 217 */           result[j] = this.val$arrays[j][pos[j]];
/*     */         }
/* 219 */         return result;
/*     */       }
/*     */     };
/*     */   }
/*     */   
/*     */   public static double[][] merge(double[][] A, double[][] B) throws Exception {
/* 225 */     if ((A.length != B.length) || (A[0].length != B[0].length))
/* 226 */       throw new Exception("sizes of matrices do not agree");
/* 227 */     double[][] res = new double[A.length + B.length][A[0].length];
/* 228 */     System.arraycopy(A, 0, res, 0, A.length);
/* 229 */     System.arraycopy(B, 0, res, A.length, B.length);
/* 230 */     return res;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static double[][] deleteSparseColRows(double[][] input, double thresh)
/*     */   {
/* 239 */     ArrayList rowA = new ArrayList();
/* 240 */     ArrayList colA = new ArrayList();
/* 241 */     for (int i = 0; i < input.length; i++) {
/* 242 */       for (int j = 0; j < input[0].length; j++) {
/* 243 */         if (input[i][j] > thresh) {
/* 244 */           rowA.add(new Integer(i));
/* 245 */           break;
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 250 */     for (int j = 0; j < input[0].length; j++) {
/* 251 */       for (int i = 0; i < input.length; i++) {
/* 252 */         if (input[i][j] > thresh) {
/* 253 */           colA.add(new Integer(j));
/* 254 */           break;
/*     */         }
/*     */       }
/*     */     }
/* 258 */     System.out.println(rowA);
/* 259 */     System.out.println(colA);
/*     */     
/* 261 */     double[][] outp = new double[rowA.size() + 1][colA.size() + 1];
/* 262 */     for (int i = 0; i < rowA.size(); i++) {
/* 263 */       outp[(i + 1)][0] = (((Integer)rowA.get(i)).intValue() + 1);
/* 264 */       for (int j = 0; j < colA.size(); j++) {
/* 265 */         outp[(i + 1)][(j + 1)] = input[((Integer)rowA.get(i)).intValue()][((Integer)
/* 266 */           colA.get(j)).intValue()];
/*     */       }
/*     */     }
/* 269 */     for (int j = 0; j < colA.size(); j++) {
/* 270 */       outp[0][(j + 1)] = (((Integer)colA.get(j)).intValue() + 1);
/*     */     }
/* 272 */     return outp;
/*     */   }
/*     */   
/*     */   public static Iterator pairIterator(Object[] arr1, Object[] arr2) {
/* 276 */     new Iterator()
/*     */     {
/*     */       int i;
/*     */       
/*     */ 
/*     */       int j;
/*     */       
/*     */       Object[] dom_l;
/*     */       
/*     */ 
/*     */       public boolean hasNext()
/*     */       {
/* 288 */         return (this.i < Combinatorics.this.length) || (this.j < this.val$arr2.length);
/*     */       }
/*     */       
/*     */       public Object next() {
/* 292 */         if (this.j < this.val$arr2.length) {
/* 293 */           this.dom_l[1] = this.val$arr2[this.j];
/* 294 */           this.j += 1;
/* 295 */           return this.dom_l;
/*     */         }
/* 297 */         this.j = 0;
/* 298 */         this.dom_l[0] = Combinatorics.this[this.i];
/* 299 */         this.i += 1;
/* 300 */         return next();
/*     */       }
/*     */       
/*     */ 
/*     */       public void remove() {}
/*     */     };
/*     */   }
/*     */   
/*     */ 
/*     */   public static Iterator pairIterator(List arr1, List arr2)
/*     */   {
/* 311 */     new Iterator()
/*     */     {
/*     */       int i;
/*     */       
/*     */ 
/*     */       int j;
/*     */       
/*     */       Object[] dom_l;
/*     */       
/*     */ 
/*     */       public boolean hasNext()
/*     */       {
/* 323 */         return (this.i < Combinatorics.this.size()) || (this.j < this.val$arr2.size());
/*     */       }
/*     */       
/*     */       public Object next() {
/* 327 */         if (this.j < this.val$arr2.size()) {
/* 328 */           this.dom_l[1] = this.val$arr2.get(this.j);
/* 329 */           this.j += 1;
/* 330 */           return this.dom_l;
/*     */         }
/* 332 */         this.j = 0;
/* 333 */         this.dom_l[0] = Combinatorics.this.get(this.i);
/* 334 */         this.i += 1;
/* 335 */         return next();
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */       public void remove() {}
/*     */     };
/*     */   }
/*     */   
/*     */ 
/*     */   public static Iterator pairIterator(List arr1)
/*     */   {
/* 347 */     List newL = new ArrayList();
/* 348 */     for (int i = 0; i < arr1.size(); i++) {
/* 349 */       newL.add(new Integer(i));
/*     */     }
/* 351 */     Iterator pairs = pairIterator(newL);
/* 352 */     new Iterator()
/*     */     {
/*     */       Object[] res;
/*     */       
/*     */ 
/*     */       boolean hasNext;
/*     */       
/*     */ 
/*     */       Object[] prepareNext()
/*     */       {
/* 362 */         Object[] curr = this.res == null ? null : new Object[this.res.length];
/* 363 */         if (this.res != null) {
/* 364 */           for (int i = 0; i < curr.length; i++) {
/* 365 */             curr[i] = Combinatorics.this.get(((Integer)this.res[i]).intValue());
/*     */           }
/*     */         }
/* 368 */         if (this.val$pairs.hasNext()) {
/* 369 */           Object[] res = (Object[])this.val$pairs.next();
/* 370 */           while ((res[0].equals(res[1])) && (this.val$pairs.hasNext())) {
/* 371 */             res = (Object[])this.val$pairs.next();
/*     */           }
/*     */         }
/* 374 */         this.hasNext = this.val$pairs.hasNext();
/* 375 */         return curr;
/*     */       }
/*     */       
/*     */       public Object next() {
/* 379 */         return prepareNext();
/*     */       }
/*     */       
/*     */       public boolean hasNext() {
/* 383 */         return this.hasNext;
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */       public void remove() {}
/*     */     };
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static Iterator count(int[] n)
/*     */   {
/* 396 */     if (n.length == 1) {
/* 397 */       new Iterator() {
/* 398 */         int i = -1;
/*     */         
/*     */         public boolean hasNext() {
/* 401 */           return this.i < Combinatorics.this[0];
/*     */         }
/*     */         
/*     */         public Object next() {
/* 405 */           this.i += 1;
/* 406 */           return new int[] { this.i };
/*     */         }
/*     */         
/*     */ 
/*     */         public void remove() {}
/*     */       };
/*     */     }
/* 413 */     new Iterator()
/*     */     {
/*     */       int[] m;
/*     */       
/*     */ 
/*     */       Iterator sub_it;
/*     */       
/*     */       int first;
/*     */       
/*     */ 
/*     */       public boolean hasNext()
/*     */       {
/* 425 */         return (this.first < Combinatorics.this[0]) || (this.sub_it.hasNext());
/*     */       }
/*     */       
/*     */       public Object next() {
/* 429 */         int[] result = new int[Combinatorics.this.length];
/* 430 */         if ((this.sub_it == null) || (!this.sub_it.hasNext())) {
/* 431 */           this.first += 1;
/* 432 */           this.sub_it = Combinatorics.count(this.m);
/*     */         }
/* 434 */         System.arraycopy((int[])this.sub_it.next(), 0, result, 1, 
/* 435 */           Combinatorics.this.length - 1);
/* 436 */         result[0] = this.first;
/* 437 */         return result;
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */       public void remove() {}
/*     */     };
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static Iterator listIterator(int L, int l)
/*     */   {
/* 450 */     if (l == 1) {
/* 451 */       new Iterator()
/*     */       {
/*     */         int j;
/*     */         int[] array;
/*     */         
/*     */         public boolean hasNext() {
/* 457 */           return this.j < this.val$L;
/*     */         }
/*     */         
/*     */         public Object next() {
/* 461 */           this.j += 1;
/* 462 */           this.array[0] = this.j;
/* 463 */           return this.array;
/*     */         }
/*     */         
/*     */ 
/*     */         public void remove() {}
/*     */       };
/*     */     }
/*     */     
/* 471 */     new Iterator()
/*     */     {
/*     */       int i;
/*     */       
/*     */       Iterator sub_it;
/*     */       
/*     */       int[] array;
/*     */       
/*     */ 
/*     */       public boolean hasNext()
/*     */       {
/* 482 */         return (this.sub_it.hasNext()) || (this.i < this.val$L - this.val$l + 1);
/*     */       }
/*     */       
/*     */       public Object next() {
/* 486 */         if (!this.sub_it.hasNext()) {
/* 487 */           this.i += 1;
/* 488 */           this.sub_it = Combinatorics.listIterator(this.val$L - this.i, this.val$l - 1);
/* 489 */           this.array[0] = this.i;
/*     */         }
/* 491 */         System.arraycopy((int[])this.sub_it.next(), 0, this.array, 1, this.val$l - 1);
/* 492 */         for (int k = 1; k < this.val$l; k++) {
/* 493 */           this.array[k] += this.i;
/*     */         }
/* 495 */         return this.array;
/*     */       }
/*     */       
/*     */       public void remove() {}
/*     */     };
/*     */   }
/*     */ }


/* Location:              /home/lachlan/Desktop/psilc1.21/psilc.jar!/lc1/dp/Combinatorics.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */