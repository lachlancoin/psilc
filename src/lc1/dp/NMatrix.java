/*     */ package lc1.dp;
/*     */ 
/*     */ import java.util.Iterator;
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
/*     */ class NMatrix
/*     */ {
/*     */   Object[] values;
/*     */   int[] lims;
/*     */   
/*     */   NMatrix(int[] lims)
/*     */   {
/* 198 */     this.lims = lims;
/* 199 */     this.values = new Object[lims[0] + 1];
/* 200 */     initialise(1, this.values);
/*     */   }
/*     */   
/*     */   private void initialise(int mark, Object[] valm) {
/* 204 */     if (mark == this.lims.length - 1) {
/* 205 */       for (int i = 0; i < valm.length; i++) {
/* 206 */         valm[i] = new double[this.lims[mark] + 1];
/*     */       }
/*     */       
/*     */     } else {
/* 210 */       for (int i = 0; i < valm.length; i++) {
/* 211 */         valm[i] = new Object[this.lims[mark] + 1];
/* 212 */         initialise(mark + 1, (Object[])valm[i]);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public String toString() {
/* 218 */     String str = new String();
/* 219 */     Iterator i = Combinatorics.count(this.lims);
/* 220 */     while (i.hasNext()) {
/* 221 */       int[] pos = (int[])i.next();
/* 222 */       str = str + "\n" + Print.toString(pos);
/* 223 */       str = str + ": " + get(pos);
/*     */     }
/* 225 */     return str;
/*     */   }
/*     */   
/*     */ 
/*     */   private double[] get(int mark, int[] pos, Object[] valM)
/*     */   {
/* 231 */     if (mark == pos.length - 2) {
/* 232 */       return (double[])valM[pos[mark]];
/*     */     }
/*     */     
/* 235 */     return get(mark + 1, pos, (Object[])valM[pos[mark]]);
/*     */   }
/*     */   
/*     */   public void set(int[] pos, double val)
/*     */   {
/* 240 */     get(0, pos, this.values)[pos[(pos.length - 1)]] = val;
/*     */   }
/*     */   
/*     */   public double get(int[] pos) {
/* 244 */     return get(0, pos, this.values)[pos[(pos.length - 1)]];
/*     */   }
/*     */ }


/* Location:              /home/lachlan/Desktop/psilc1.21/psilc.jar!/lc1/dp/NMatrix.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */