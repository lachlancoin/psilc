/*     */ package lc1.pfam;
/*     */ 
/*     */ import java.sql.ResultSet;
/*     */ import java.util.Properties;
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
/*     */ class Sql
/*     */   extends PfamSqlBase
/*     */ {
/*     */   Sql(Properties params)
/*     */   {
/*  63 */     super(params);
/*     */   }
/*     */   
/*     */   void removeSequence(String table, String auto_pfamseq, String type) throws Exception
/*     */   {
/*  68 */     this.query = 
/*  69 */       ("DELETE FROM " + table + " WHERE " + type + "  = '" + auto_pfamseq + "'");
/*     */     
/*  71 */     executeQuery();
/*  72 */     closeConnection();
/*     */   }
/*     */   
/*     */   void removeTable(String table) throws Exception {
/*  76 */     String query = "DROP TABLE " + table;
/*  77 */     this.query = query;
/*  78 */     executeQuery();
/*  79 */     closeConnection();
/*     */   }
/*     */   
/*     */   void clearTable(String table) throws Exception {
/*  83 */     String query = "DELETE FROM " + table;
/*  84 */     this.query = query;
/*  85 */     executeQuery();
/*  86 */     closeConnection();
/*     */   }
/*     */   
/*     */   void createTable(String table, String requirements) throws Exception {
/*  90 */     String query = "CREATE TABLE " + table + " ( " + requirements + " )";
/*  91 */     this.query = query;
/*  92 */     executeQuery();
/*  93 */     closeConnection();
/*     */   }
/*     */   
/*     */   boolean hasTable(String table) throws Exception {
/*  97 */     String query = "show tables";
/*  98 */     this.query = query;
/*  99 */     executeQuery();
/* 100 */     while (this.RS.next()) {
/* 101 */       if (this.RS.getString(1).equals(table))
/* 102 */         return true;
/*     */     }
/* 104 */     closeConnection();
/* 105 */     return false;
/*     */   }
/*     */   
/*     */   int sizeTable(String table) throws Exception
/*     */   {
/* 110 */     String query = "Select count(*) from " + table;
/* 111 */     this.query = query;
/* 112 */     executeQuery();
/* 113 */     this.RS.next();
/* 114 */     int result = Integer.parseInt(this.RS.getString(1));
/* 115 */     closeConnection();
/* 116 */     return result;
/*     */   }
/*     */ }


/* Location:              /home/lachlan/Desktop/psilc1.21/psilc.jar!/lc1/pfam/Sql.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */