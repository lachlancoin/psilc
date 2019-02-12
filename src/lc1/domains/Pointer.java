/*    */ package lc1.domains;
/*    */ 
/*    */ import java.util.Comparator;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class Pointer
/*    */ {
/*    */   public int dom;
/*    */   public double score;
/*    */   double t_score;
/*    */   public int offset;
/*    */   public int position;
/*    */   
/*    */   Pointer(int dom, double score, int position, int offset)
/*    */   {
/* 21 */     this.dom = dom;
/* 22 */     this.score = score;
/* 23 */     this.offset = offset;
/* 24 */     this.position = position;
/*    */   }
/*    */   
/*    */   public String toString() {
/* 28 */     String str = new String();
/* 29 */     str = str + "Domain " + this.dom;
/* 30 */     str = str + " score " + this.score;
/* 31 */     str = str + " offset " + this.offset;
/* 32 */     str = str + " position " + this.position;
/* 33 */     return str;
/*    */   }
/*    */   
/*    */   static Comparator comparator()
/*    */   {
/* 38 */     new Comparator() {
/*    */       public int compare(Object o1, Object o2) {
/* 40 */         if ((!(o1 instanceof Pointer)) || (!(o2 instanceof Pointer))) { return 0;
/*    */         }
/*    */         
/* 43 */         if (((Pointer)o1).score > ((Pointer)o2).score) return -1;
/* 44 */         if (((Pointer)o1).score < ((Pointer)o2).score) return 1;
/* 45 */         return 0;
/*    */       }
/*    */     };
/*    */   }
/*    */ }


/* Location:              /home/lachlan/Desktop/psilc1.21/psilc.jar!/lc1/domains/Pointer.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */