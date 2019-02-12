/*    */ package lc1.pfam;
/*    */ 
/*    */ import java.util.ArrayList;
/*    */ import java.util.Comparator;
/*    */ import java.util.HashMap;
/*    */ import java.util.Iterator;
/*    */ import java.util.Map;
/*    */ import org.apache.commons.cli.CommandLine;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ class ProteinComparator
/*    */   implements Comparator
/*    */ {
/* 41 */   Map familyKey = new HashMap();
/*    */   
/*    */   ProteinComparator(CommandLine params)
/*    */   {
/* 45 */     PfamSqlRead sqlr = null;
/* 46 */     Iterator i = sqlr.getQuery();
/*    */     
/*    */ 
/* 49 */     while (i.hasNext()) {
/* 50 */       ArrayList row = (ArrayList)i.next();
/*    */       
/* 52 */       this.familyKey.put(new Integer((String)row.get(1)), 
/* 53 */         new Integer((String)row.get(0)));
/*    */     }
/*    */   }
/*    */   
/*    */   public int compare(Object o1, Object o2) {
/* 58 */     if ((!(o1 instanceof Integer)) || (!(o2 instanceof Integer))) {
/* 59 */       return 0;
/*    */     }
/* 61 */     if (this.familyKey.containsKey((Integer)o1)) {
/* 62 */       if (this.familyKey.containsKey((Integer)o2)) {
/* 63 */         if (this.familyKey.get(o1).equals(this.familyKey.get(o2))) {
/* 64 */           return ((Integer)o1).compareTo((Integer)o2);
/*    */         }
/* 66 */         return ((Integer)this.familyKey.get(o1)).compareTo((Integer)this.familyKey.get(o2));
/*    */       }
/*    */       
/* 69 */       return -1;
/*    */     }
/*    */     
/* 72 */     return ((Integer)o1).compareTo((Integer)o2);
/*    */   }
/*    */ }


/* Location:              /home/lachlan/Desktop/psilc1.21/psilc.jar!/lc1/pfam/ProteinComparator.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */