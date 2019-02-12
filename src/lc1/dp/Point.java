/*    */ package lc1.dp;
/*    */ 
/*    */ import lc1.util.Print;
/*    */ 
/*    */ public class Point
/*    */ {
/*    */   public int[] perm;
/*    */   public int[] position;
/*    */   public double score;
/* 10 */   public static final Point NULL_TRACE = new Point(null, null, Double.NEGATIVE_INFINITY);
/*    */   
/*    */   public Point(int[] perm, int[] position, double score) {
/* 13 */     this.perm = perm;
/* 14 */     this.position = position;
/* 15 */     this.score = score;
/*    */   }
/*    */   
/*    */   public String toString() {
/* 19 */     return 
/*    */     
/* 21 */       (this.perm == null ? "null " : new StringBuffer(String.valueOf(Print.toString(this.perm))).append(" ").toString()) + (this.position == null ? "null " : Print.toString(this.position)) + " score " + this.score;
/*    */   }
/*    */ }


/* Location:              /home/lachlan/Desktop/psilc1.21/psilc.jar!/lc1/dp/Point.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */