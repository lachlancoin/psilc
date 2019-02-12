/*     */ package lc1.pfam;
/*     */ 
/*     */ import java.sql.ResultSet;
/*     */ import java.sql.SQLException;
/*     */ import java.util.Properties;
/*     */ 
/*     */ class PfamSqlRead extends PfamSqlBase
/*     */ {
/*     */   int[] type;
/*     */   
/*     */   PfamSqlRead(Properties paramas)
/*     */   {
/*  13 */     super(paramas);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   PfamSqlRead(Properties params, String[] select, String restrictions)
/*     */   {
/*  22 */     this(params, select, "", restrictions, "");
/*     */   }
/*     */   
/*     */   PfamSqlRead(Properties params, String[] select, String prefix, String restrictions)
/*     */   {
/*  27 */     this(params, select, prefix, restrictions, "");
/*     */   }
/*     */   
/*     */ 
/*     */   PfamSqlRead(Properties params, String[] select, String prefix, String restrictions, String preprefix)
/*     */   {
/*  33 */     super(params);
/*  34 */     constructQuery(select, restrictions);
/*  35 */     int len = this.query.length();
/*     */     
/*  37 */     this.type = getType(select);
/*  38 */     this.query = (preprefix + " SELECT " + prefix + this.query.substring(6, len));
/*     */     try
/*     */     {
/*  41 */       PfamSqlManager.executeQuery(this);
/*     */     }
/*     */     catch (SQLException t) {
/*  44 */       t.printStackTrace();
/*  45 */       System.exit(0);
/*     */     }
/*     */   }
/*     */   
/*     */   static String constructQueryInner(String[] select) {
/*  50 */     String queryIn = new String("SELECT " + select[0]);
/*  51 */     for (int i = 1; i < select.length; i++) {
/*  52 */       queryIn = queryIn.concat(", " + select[i]);
/*     */     }
/*  54 */     return queryIn;
/*     */   }
/*     */   
/*     */   void constructQuery(String[] select, String restrictions) {
/*  58 */     this.query = (constructQueryInner(select) + " " + restrictions);
/*     */   }
/*     */   
/*     */   Object[] getRow()
/*     */   {
/*  63 */     Object[] row = new Object[this.type.length];
/*     */     try {
/*  65 */       for (int i = 0; i < this.type.length; i++) {
/*  66 */         if (this.type[i] == 0) {
/*  67 */           row[i] = this.RS.getString(i + 1);
/*  68 */         } else if (this.type[i] == 1) {
/*  69 */           row[i] = new Integer(this.RS.getInt(i + 1));
/*  70 */         } else if (this.type[i] == 2) {
/*  71 */           row[i] = new Float(this.RS.getFloat(i + 1));
/*     */         }
/*     */       }
/*     */     } catch (SQLException t) {
/*  75 */       t.printStackTrace();
/*  76 */       System.exit(0);
/*     */     }
/*  78 */     return row;
/*     */   }
/*     */   
/*     */   static int[] getType(String[] select)
/*     */   {
/*  83 */     int[] type = new int[select.length];
/*  84 */     for (int i = 0; i < select.length; i++) {
/*  85 */       String ele = select[i];
/*  86 */       if (ele.startsWith("auto_")) { type[i] = 0;
/*  87 */       } else if (ele.startsWith("seq_")) { type[i] = 1;
/*  88 */       } else if (ele.startsWith("model_")) { type[i] = 1;
/*  89 */       } else if (ele.startsWith("num_")) { type[i] = 1;
/*  90 */       } else if (ele.endsWith("score")) { type[i] = 2;
/*  91 */       } else if (ele.startsWith("ls_")) { type[i] = 2;
/*  92 */       } else if (ele.startsWith("fs_")) { type[i] = 2;
/*  93 */       } else if (ele == "significant") { type[i] = 1;
/*  94 */       } else if (ele == "significant") type[i] = 1; else {
/*  95 */         type[i] = 0;
/*     */       }
/*     */     }
/*  98 */     return type;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   java.util.Iterator getQuery()
/*     */   {
/* 108 */     new java.util.Iterator() {
/* 109 */       boolean hasNext = true;
/*     */       
/* 111 */       public boolean hasNext() { return this.hasNext; }
/*     */       
/*     */       public Object next() {
/*     */         try {
/* 115 */           this.hasNext = PfamSqlRead.this.RS.next();
/*     */           
/* 117 */           Object[] s = new Object[PfamSqlRead.this.type.length];
/* 118 */           for (int i = 0; i < PfamSqlRead.this.type.length; i++) {
/* 119 */             if (PfamSqlRead.this.type[i] == 0) {
/* 120 */               s[i] = PfamSqlRead.this.RS.getString(i + 1);
/*     */             }
/* 122 */             else if (PfamSqlRead.this.type[i] == 1) {
/* 123 */               s[i] = new Integer(PfamSqlRead.this.RS.getInt(i + 1));
/*     */             }
/*     */             else {
/* 126 */               s[i] = new Float(PfamSqlRead.this.RS.getFloat(i + 1));
/*     */             }
/*     */           }
/* 129 */           if (!this.hasNext) {
/* 130 */             PfamSqlRead.this.closeConnection();
/*     */           }
/* 132 */           return java.util.Arrays.asList(s);
/*     */         } catch (Exception exc) {
/* 134 */           exc.printStackTrace(); }
/* 135 */         return null;
/*     */       }
/*     */       
/*     */       public void remove() {}
/*     */     };
/*     */   }
/*     */ }


/* Location:              /home/lachlan/Desktop/psilc1.21/psilc.jar!/lc1/pfam/PfamSqlRead.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */