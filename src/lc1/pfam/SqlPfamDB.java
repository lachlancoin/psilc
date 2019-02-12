/*    */ package lc1.pfam;
/*    */ 
/*    */ import java.sql.ResultSet;
/*    */ import java.util.ArrayList;
/*    */ import java.util.HashSet;
/*    */ import java.util.Iterator;
/*    */ import java.util.List;
/*    */ import java.util.Set;
/*    */ import lc1.util.SheetIO;
/*    */ import org.biojava.bio.Annotation;
/*    */ import org.biojava.bio.seq.Sequence;
/*    */ import org.biojava.bio.seq.db.AbstractSequenceDB;
/*    */ import org.biojava.bio.seq.db.IllegalIDException;
/*    */ import org.biojava.bio.seq.impl.SimpleSequence;
/*    */ import org.biojava.bio.symbol.SymbolList;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class SqlPfamDB
/*    */   extends AbstractSequenceDB
/*    */ {
/*    */   PfamSqlRead pfamT;
/*    */   DomainAlphabet alph;
/*    */   String name;
/*    */   Set ids;
/*    */   
/*    */   public String getName()
/*    */   {
/* 32 */     return this.name;
/*    */   }
/*    */   
/*    */   public Set ids() {
/* 36 */     if (this.ids == null) {
/*    */       try {
/* 38 */         this.pfamT.query = new String("select pfamseq_acc FROM pfamseq");
/* 39 */         this.pfamT.executeQuery();
/* 40 */         this.ids = new HashSet();
/* 41 */         while (this.pfamT.RS.next()) {
/* 42 */           this.ids.add(this.pfamT.RS.getString(1));
/*    */         }
/*    */       } catch (Exception exc) {
/* 45 */         exc.printStackTrace();
/*    */       }
/*    */     }
/* 48 */     return this.ids;
/*    */   }
/*    */   
/* 51 */   static final String[] select1 = { "pfamseq.auto_pfamseq", 
/* 52 */     "pfamA.pfamA_acc", "seq_start", "seq_end", "domain_bits_score", 
/* 53 */     "mode", "significant", "domain_evalue_score" };
/*    */   
/* 55 */   static final String query1 = PfamSqlRead.constructQueryInner(select1) + 
/* 56 */     " FROM pfamA, pfamA_reg_full, pfamseq where pfamseq_acc = '";
/*    */   static final String query2 = "' AND pfamseq.auto_pfamseq = pfamA_reg_full.auto_pfamseq AND significant = 1   AND pfamA.auto_pfamA = pfamA_reg_full.auto_pfamA order by seq_start";
/*    */   
/*    */   public Sequence getSequence(String id)
/*    */     throws IllegalIDException
/*    */   {
/* 62 */     if (!this.ids.contains(id))
/* 63 */       throw new IllegalIDException("doesn't contain id " + id);
/*    */     try {
/* 65 */       this.pfamT.query = (query1 + id + "' AND pfamseq.auto_pfamseq = pfamA_reg_full.auto_pfamseq AND significant = 1   AND pfamA.auto_pfamA = pfamA_reg_full.auto_pfamA order by seq_start");
/* 66 */       this.pfamT.prepareStatement();
/* 67 */       this.pfamT.executeQuery();
/* 68 */       Iterator sh = this.pfamT.getQuery();
/* 69 */       List l = new ArrayList();
/* 70 */       SheetIO.toCollection(sh, l);
/* 71 */       return PfamDB.buildList(l, 
/* 72 */         new SimpleSequence(SymbolList.EMPTY_LIST, "", "", 
/* 73 */         Annotation.EMPTY_ANNOTATION), this.alph);
/*    */     }
/*    */     catch (Throwable t)
/*    */     {
/* 77 */       t.printStackTrace();
/* 78 */       System.exit(0); }
/* 79 */     return null;
/*    */   }
/*    */ }


/* Location:              /home/lachlan/Desktop/psilc1.21/psilc.jar!/lc1/pfam/SqlPfamDB.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */