/*     */ package lc1.domains;
/*     */ 
/*     */ import java.io.PrintStream;
/*     */ import java.sql.ResultSet;
/*     */ import java.sql.SQLException;
/*     */ import java.util.Iterator;
/*     */ import java.util.Properties;
/*     */ import lc1.domainseq.Domain.MagicTemplate;
/*     */ import lc1.domainseq.Domain.Template;
/*     */ import lc1.domainseq.DomainList;
/*     */ import lc1.pfam.PfamSqlBase;
/*     */ import org.biojava.bio.Annotation;
/*     */ import org.biojava.bio.BioException;
/*     */ import org.biojava.bio.SimpleAnnotation;
/*     */ import org.biojava.bio.seq.Sequence;
/*     */ import org.biojava.bio.seq.io.SymbolTokenization;
/*     */ import org.biojava.bio.symbol.Alphabet;
/*     */ import org.biojava.bio.symbol.LocationTools;
/*     */ import org.biojava.bio.symbol.Symbol;
/*     */ import org.biojava.bio.symbol.SymbolList;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ class SqlQuery
/*     */   extends PfamSqlBase
/*     */ {
/*     */   static final String base_search = "select pfamA_acc, seq_start, seq_end, domain_bits_score, pfamA.auto_pfamA from pfamA_reg_full, pfamA where pfamA.auto_pfamA = pfamA_reg_full.auto_pfamA and mode = 'ls' and auto_pfamseq = ";
/*     */   static final String base_current = "select pfamA_acc, seq_start, seq_end, domain_bits_score, pfamA.auto_pfamA from pfamA_reg_full, pfamA where pfamA.auto_pfamA = pfamA_reg_full.auto_pfamA and significant = 1 and auto_pfamseq = ";
/*     */   static final String baseTaxon = "select ncbi_code from pfamseq_ncbi where auto_pfamseq = ";
/*     */   SymbolTokenization token;
/*     */   Properties params;
/*     */   Alphabet alph;
/*     */   
/*     */   SqlQuery(Properties params, Alphabet alph)
/*     */     throws BioException
/*     */   {
/* 223 */     super(params);
/* 224 */     this.params = params;
/* 225 */     this.alph = alph;
/* 226 */     this.token = alph.getTokenization("token");
/*     */   }
/*     */   
/*     */   class InnerQuery extends PfamSqlBase implements Iterator
/*     */   {
/*     */     InnerQuery(int min) {
/* 232 */       super();
/* 233 */       this.query = ("select auto_pfamseq from pfamseq where auto_pfamseq > " + min);
/*     */       try {
/* 235 */         executeQuery();
/*     */       }
/*     */       catch (SQLException e) {
/* 238 */         e.printStackTrace();
/*     */       }
/*     */     }
/*     */     
/*     */     public boolean hasNext() {
/* 243 */       try { return !this.RS.isAfterLast();
/*     */       }
/*     */       catch (SQLException e) {
/* 246 */         e.printStackTrace();
/*     */       }
/* 248 */       return false;
/*     */     }
/*     */     
/*     */     public void remove() {}
/*     */     
/* 253 */     public Object next() { try { this.RS.next();
/* 254 */         return new Integer(this.RS.getInt(1));
/*     */       }
/*     */       catch (SQLException e) {
/* 257 */         e.printStackTrace();
/*     */       }
/* 259 */       return null;
/*     */     }
/*     */   }
/*     */   
/*     */   public Iterator getSequences(int min)
/*     */     throws Exception
/*     */   {
/* 266 */     Iterator innerQuery = new InnerQuery(min);
/* 267 */     Annotation annot = new SimpleAnnotation();
/* 268 */     DomainList[] res = {
/* 269 */       new DomainList(SymbolList.EMPTY_LIST, "", "", annot), 
/* 270 */       new DomainList(SymbolList.EMPTY_LIST, "", "", annot) };
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 275 */     new Iterator()
/*     */     {
/* 277 */       public boolean hasNext() { return this.val$innerQuery.hasNext(); }
/*     */       
/*     */       public void remove() {}
/*     */       
/*     */       public Object next() {
/*     */         try {
/* 283 */           Integer auto_pfamseq = (Integer)this.val$innerQuery.next();
/* 284 */           String name = auto_pfamseq.toString();
/* 285 */           this.val$res[0].removeAllFeatures();this.val$res[1].removeAllFeatures();
/* 286 */           this.val$res[0].setName(name);this.val$res[1].setName(name);
/* 287 */           SqlQuery.this.getSequence(this.val$res[0], auto_pfamseq, true);
/* 288 */           SqlQuery.this.getSequence(this.val$res[1], auto_pfamseq, false);
/* 289 */           SqlQuery.this.getSequenceAnnotation(auto_pfamseq, this.val$annot);
/*     */           
/*     */ 
/* 292 */           return this.val$res;
/*     */         }
/*     */         catch (Exception exc) {
/* 295 */           exc.printStackTrace();
/*     */         }
/* 297 */         return null;
/*     */       }
/*     */     };
/*     */   }
/*     */   
/*     */   public Annotation getSequenceAnnotation(Integer auto_pfamseq, Annotation annot) throws Exception {
/* 303 */     this.query = ("select ncbi_code from pfamseq_ncbi where auto_pfamseq = " + auto_pfamseq);
/* 304 */     executeQuery();
/* 305 */     this.RS.next();
/* 306 */     int taxon = this.RS.getInt(1);
/* 307 */     this.RS.close();
/* 308 */     annot.setProperty("species", taxon);
/* 309 */     return annot;
/*     */   }
/*     */   
/*     */   public void getSequence(Sequence seq, Integer auto_pfamseq, boolean search)
/*     */     throws Exception
/*     */   {
/* 315 */     this.query = ((search ? "select pfamA_acc, seq_start, seq_end, domain_bits_score, pfamA.auto_pfamA from pfamA_reg_full, pfamA where pfamA.auto_pfamA = pfamA_reg_full.auto_pfamA and mode = 'ls' and auto_pfamseq = " : "select pfamA_acc, seq_start, seq_end, domain_bits_score, pfamA.auto_pfamA from pfamA_reg_full, pfamA where pfamA.auto_pfamA = pfamA_reg_full.auto_pfamA and significant = 1 and auto_pfamseq = ") + auto_pfamseq);
/* 316 */     seq.createFeature(new Domain.MagicTemplate(this.alph, 0));
/* 317 */     seq.createFeature(new Domain.MagicTemplate(this.alph, Integer.MAX_VALUE));
/*     */     
/* 319 */     executeQuery();
/*     */     
/* 321 */     if (!this.RS.next()) { return;
/*     */     }
/* 323 */     while (!this.RS.isAfterLast()) {
/*     */       try {
/* 325 */         Domain.Template dom = new Domain.Template();
/* 326 */         dom.symbol = this.token.parseToken(this.RS.getString(1));
/* 327 */         if (!dom.symbol.getAnnotation().containsProperty("auto_pfamA")) {
/* 328 */           dom.symbol.getAnnotation().setProperty("auto_pfamA", new Integer(this.RS.getInt(5)));
/*     */         }
/* 330 */         dom.location = LocationTools.makeLocation(this.RS.getInt(2), this.RS.getInt(3));
/* 331 */         dom.score = this.RS.getDouble(4);
/* 332 */         seq.createFeature(dom);
/*     */       }
/*     */       catch (Exception exc) {
/* 335 */         exc.printStackTrace();
/* 336 */         System.err.println(seq.getName() + "\n" + this.query + "\n" + seq.countFeatures());
/* 337 */         System.exit(0);
/*     */       }
/* 339 */       this.RS.next();
/*     */     }
/* 341 */     this.RS.close();
/*     */   }
/*     */   
/*     */ 
/*     */   public void finalize()
/*     */   {
/*     */     try
/*     */     {
/* 349 */       closeConnection();
/*     */     }
/*     */     catch (Exception localException) {}
/*     */   }
/*     */ }


/* Location:              /home/lachlan/Desktop/psilc1.21/psilc.jar!/lc1/domains/SqlQuery.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */