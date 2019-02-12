/*    */ package lc1.pfam;
/*    */ 
/*    */ import java.io.PrintStream;
/*    */ import java.sql.ResultSet;
/*    */ import java.sql.SQLException;
/*    */ import java.util.ArrayList;
/*    */ import java.util.HashMap;
/*    */ import java.util.Iterator;
/*    */ import java.util.Map;
/*    */ import java.util.Properties;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ class PfamSqlWrite
/*    */   extends PfamSqlBase
/*    */ {
/*    */   String table;
/*    */   
/*    */   PfamSqlWrite(Properties params, String table)
/*    */   {
/* 22 */     super(params);
/* 23 */     this.table = table;
/* 24 */     this.query = ("SELECT * FROM " + table);
/*    */     try
/*    */     {
/* 27 */       executeQuery();
/* 28 */       if ((table.indexOf("lc1") == -1) && (table.indexOf("context") == -1))
/* 29 */         throw new Exception("Cannot modify non-lc1 tables");
/*    */     } catch (Throwable t) {
/* 31 */       t.printStackTrace();
/* 32 */       System.exit(0);
/*    */     }
/*    */   }
/*    */   
/* 36 */   static Map openSessions = new HashMap();
/*    */   
/*    */   public static PfamSqlWrite make(Properties params, String table) {
/* 39 */     if (openSessions.containsKey(table)) {
/* 40 */       return (PfamSqlWrite)openSessions.get(table);
/*    */     }
/* 42 */     PfamSqlWrite sqlw = new PfamSqlWrite(params, table);
/* 43 */     openSessions.put(table, sqlw);
/* 44 */     return sqlw;
/*    */   }
/*    */   
/*    */ 
/*    */   void constructQuery(String row)
/*    */   {
/* 50 */     this.query = new String("INSERT INTO ");
/* 51 */     this.query = (this.query + this.table + " ");
/* 52 */     this.query = (this.query + "VALUES " + row);
/*    */   }
/*    */   
/*    */   void nextQuery(String row) {
/*    */     try {
/* 57 */       constructQuery(row);
/* 58 */       PfamSqlManager.executeQuery(this);
/*    */     } catch (Throwable t) {
/* 60 */       System.out.println(t);
/*    */     }
/*    */   }
/*    */   
/*    */ 
/*    */   void addStringEntries(Object[] data)
/*    */   {
/*    */     try
/*    */     {
/* 69 */       this.RS.moveToInsertRow();
/* 70 */       for (int i = 0; i < data.length; i++) {
/* 71 */         if ((data[i] instanceof String)) {
/* 72 */           this.RS.updateString(i + 1, (String)data[i]);
/* 73 */         } else if ((data[i] instanceof Integer)) {
/* 74 */           this.RS.updateInt(i + 1, ((Integer)data[i]).intValue());
/* 75 */         } else if ((data[i] instanceof Float)) {
/* 76 */           this.RS.updateFloat(i + 1, ((Float)data[i]).floatValue());
/* 77 */         } else if ((data[i] instanceof Double)) {
/* 78 */           this.RS.updateDouble(i + 1, ((Double)data[i]).doubleValue());
/* 79 */         } else if ((data[i] instanceof Boolean)) {
/* 80 */           this.RS.updateBoolean(i + 1, ((Boolean)data[i]).booleanValue());
/*    */         }
/*    */       }
/* 83 */       this.RS.insertRow();
/*    */     }
/*    */     catch (SQLException E) {
/* 86 */       E.printStackTrace();
/*    */     }
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   void addAllEntries(Iterator i)
/*    */   {
/* 94 */     while (i.hasNext()) {
/* 95 */       ArrayList row = (ArrayList)i.next();
/* 96 */       addStringEntries(row.toArray());
/*    */     }
/*    */   }
/*    */ }


/* Location:              /home/lachlan/Desktop/psilc1.21/psilc.jar!/lc1/pfam/PfamSqlWrite.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */