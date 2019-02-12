/*    */ package lc1.dp;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public abstract class State
/*    */ {
/*    */   final String name;
/*    */   
/*    */ 
/*    */ 
/*    */   public final int adv;
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public String getName()
/*    */   {
/* 19 */     return this.name;
/*    */   }
/*    */   
/*    */ 
/* 23 */   public String toString() { return this.name; }
/*    */   
/*    */   State(String name1, int adv) {
/* 26 */     this.name = name1;
/* 27 */     this.adv = adv;
/*    */   }
/*    */ }


/* Location:              /home/lachlan/Desktop/psilc1.21/psilc.jar!/lc1/dp/State.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */