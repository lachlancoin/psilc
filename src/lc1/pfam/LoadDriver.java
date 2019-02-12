/*    */ package lc1.pfam;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ class LoadDriver
/*    */ {
/*    */   public static void runDriver()
/*    */   {
/*    */     try
/*    */     {
/* 13 */       Class.forName("org.gjt.mm.mysql.Driver").newInstance();
/*    */ 
/*    */     }
/*    */     catch (Exception E)
/*    */     {
/* 18 */       E.printStackTrace();
/*    */     }
/*    */   }
/*    */ }


/* Location:              /home/lachlan/Desktop/psilc1.21/psilc.jar!/lc1/pfam/LoadDriver.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */