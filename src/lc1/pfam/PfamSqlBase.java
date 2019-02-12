/*    */ package lc1.pfam;
/*    */ 
/*    */ import java.sql.Connection;
/*    */ import java.sql.DriverManager;
/*    */ import java.sql.ResultSet;
/*    */ import java.sql.SQLException;
/*    */ import java.sql.Statement;
/*    */ import java.util.Properties;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class PfamSqlBase
/*    */ {
/*    */   String host;
/*    */   protected String database;
/*    */   static Properties parameters;
/*    */   static Connection C;
/*    */   Statement Stmt;
/*    */   protected ResultSet RS;
/*    */   protected String query;
/* 25 */   boolean closed = false;
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public PfamSqlBase(Properties params)
/*    */   {
/* 33 */     this.host = params.getProperty("host");
/* 34 */     this.database = params.getProperty("database");
/* 35 */     parameters = params;
/* 36 */     prepareStatement();
/*    */   }
/*    */   
/*    */ 
/*    */   PfamSqlBase() {}
/*    */   
/*    */   void prepareStatement()
/*    */   {
/*    */     try
/*    */     {
/* 46 */       if (C == null)
/*    */       {
/* 48 */         Class.forName("com.mysql.jdbc.Driver").newInstance();
/*    */         
/*    */ 
/* 51 */         C = DriverManager.getConnection("jdbc:mysql://" + this.host + "/" + this.database, parameters);
/*    */       }
/* 53 */       this.Stmt = C.createStatement();
/*    */     }
/*    */     catch (Throwable E) {
/* 56 */       E.printStackTrace();
/* 57 */       System.exit(0);
/*    */     }
/*    */   }
/*    */   
/*    */   protected void executeQuery() throws SQLException
/*    */   {
/* 63 */     this.RS = this.Stmt.executeQuery(this.query);
/*    */   }
/*    */   
/*    */   protected void closeConnection() throws SQLException
/*    */   {
/* 68 */     this.RS.close();
/* 69 */     this.closed = true;
/*    */     
/* 71 */     this.Stmt.close();
/*    */   }
/*    */   
/*    */   public String toString()
/*    */   {
/* 76 */     String str = new String();
/* 77 */     str = str + "host: " + this.host;
/* 78 */     str = str + " database: " + this.database;
/* 79 */     str = str + "\n    ";
/* 80 */     str = str + this.query;
/* 81 */     return str;
/*    */   }
/*    */   
/*    */   String sqlError(SQLException E) {
/* 85 */     String str = new String();
/* 86 */     str = str + "SQLException: " + E.getMessage() + "\n";
/* 87 */     str = str + "SQLState:     " + E.getSQLState() + "\n";
/* 88 */     str = str + "VendorError:  " + E.getErrorCode();
/* 89 */     E.printStackTrace();
/* 90 */     return str;
/*    */   }
/*    */ }


/* Location:              /home/lachlan/Desktop/psilc1.21/psilc.jar!/lc1/pfam/PfamSqlBase.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */