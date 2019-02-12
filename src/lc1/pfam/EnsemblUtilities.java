/*     */ package lc1.pfam;
/*     */ 
/*     */ import java.sql.Connection;
/*     */ import java.sql.DriverManager;
/*     */ import java.sql.ResultSet;
/*     */ import java.sql.SQLException;
/*     */ import java.sql.Statement;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Properties;
/*     */ import org.apache.commons.cli.CommandLine;
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
/*     */ public class EnsemblUtilities
/*     */ {
/*     */   String host;
/*     */   protected String database;
/*     */   static Properties parameters;
/*     */   static Connection C;
/*     */   Statement Stmt;
/*     */   protected ResultSet RS;
/*     */   protected String query;
/*  36 */   boolean closed = false;
/*     */   
/*     */   void prepareStatement()
/*     */   {
/*     */     try
/*     */     {
/*  42 */       if (C == null)
/*     */       {
/*  44 */         Class.forName("com.mysql.jdbc.Driver").newInstance();
/*     */         
/*     */ 
/*  47 */         C = DriverManager.getConnection("jdbc:mysql://" + this.host + "/" + 
/*  48 */           this.database, parameters);
/*     */       }
/*  50 */       this.Stmt = C.createStatement();
/*     */     } catch (Throwable E) {
/*  52 */       E.printStackTrace();
/*  53 */       System.exit(0);
/*     */     }
/*     */   }
/*     */   
/*     */   void executeQuery()
/*     */     throws SQLException
/*     */   {}
/*     */   
/*     */   void closeConnection()
/*     */     throws SQLException
/*     */   {
/*  64 */     this.RS.close();
/*  65 */     this.closed = true;
/*     */     
/*  67 */     this.Stmt.close();
/*     */   }
/*     */   
/*     */   public String toString()
/*     */   {
/*  72 */     String str = new String();
/*  73 */     str = str + "host: " + this.host;
/*  74 */     str = str + " database: " + this.database;
/*  75 */     str = str + "\n    ";
/*  76 */     str = str + this.query;
/*  77 */     return str;
/*     */   }
/*     */   
/*     */   String sqlError(SQLException E) {
/*  81 */     String str = new String();
/*  82 */     str = str + "SQLException: " + E.getMessage() + "\n";
/*  83 */     str = str + "SQLState:     " + E.getSQLState() + "\n";
/*  84 */     str = str + "VendorError:  " + E.getErrorCode();
/*  85 */     E.printStackTrace();
/*  86 */     return str;
/*     */   }
/*     */   
/*     */   public static List getEnsemblGenes(CommandLine params, List transcript_ids)
/*     */     throws Exception
/*     */   {
/*  92 */     EnsemblUtilities eu = new EnsemblUtilities(params);
/*  93 */     List l = new ArrayList();
/*  94 */     for (Iterator it = transcript_ids.iterator(); it.hasNext();) {
/*  95 */       String id = eu.getSwissProtIdForGene((String)it.next());
/*  96 */       if (id != null)
/*  97 */         l.add(id);
/*     */     }
/*  99 */     return l;
/*     */   }
/*     */   
/*     */   EnsemblUtilities(CommandLine params)
/*     */   {
/* 104 */     this.host = "kaka.sanger.ac.uk";
/* 105 */     this.database = "ensembl_mart_15_1";
/* 106 */     parameters = new Properties();
/* 107 */     parameters.setProperty("user", "anonymous");
/*     */     
/*     */ 
/* 110 */     prepareStatement();
/*     */   }
/*     */   
/*     */   String getSwissProtIdForGene(String e_id)
/*     */   {
/*     */     try
/*     */     {
/* 117 */       this.query = ("select display_id from hsapiens_ensemblgene_xref_SWISSPROT_dm where transcript_stable_id = '" + e_id + "'");
/* 118 */       executeQuery();
/* 119 */       if (!this.RS.next()) {
/* 120 */         return null;
/*     */       }
/* 122 */       return this.RS.getString(1);
/*     */     }
/*     */     catch (SQLException E) {}
/* 125 */     return null;
/*     */   }
/*     */ }


/* Location:              /home/lachlan/Desktop/psilc1.21/psilc.jar!/lc1/pfam/EnsemblUtilities.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */