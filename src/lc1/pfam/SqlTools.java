/*    */ package lc1.pfam;
/*    */ 
/*    */ import java.util.Properties;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class SqlTools
/*    */ {
/*    */   static void removeTable(Properties params, String table)
/*    */     throws Exception
/*    */   {
/* 14 */     Sql sql = new Sql(params);
/* 15 */     if (sql.hasTable(table)) {
/* 16 */       sql.removeTable(table);
/*    */     }
/*    */   }
/*    */   
/*    */   static void createTable(Properties params, String table, String requirements) throws Exception {
/* 21 */     new Sql(params).createTable(table, requirements);
/*    */   }
/*    */   
/*    */   static void clearTable(Properties params, String table) throws Exception {
/* 25 */     new Sql(params).clearTable(table);
/*    */   }
/*    */   
/*    */   static boolean hasTable(Properties params, String table) throws Exception {
/* 29 */     return new Sql(params).hasTable(table);
/*    */   }
/*    */   
/*    */   static void removeSequence(Properties params, String table, String auto_pfamseq, String type) throws Exception
/*    */   {
/* 34 */     new Sql(params).removeSequence(table, auto_pfamseq, type);
/*    */   }
/*    */   
/*    */   static int sizeTable(Properties params, String table) throws Exception {
/* 38 */     return new Sql(params).sizeTable(table);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public static boolean createAndCheck(Properties params, String table, String desc)
/*    */   {
/*    */     try
/*    */     {
/* 47 */       boolean res = hasTable(params, table);
/* 48 */       if (!res) {
/* 49 */         createTable(params, table, desc);
/*    */       }
/* 51 */       return res;
/*    */     } catch (Throwable t) {
/* 53 */       t.printStackTrace(); }
/* 54 */     return false;
/*    */   }
/*    */ }


/* Location:              /home/lachlan/Desktop/psilc1.21/psilc.jar!/lc1/pfam/SqlTools.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */