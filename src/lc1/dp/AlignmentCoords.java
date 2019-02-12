/*    */ package lc1.dp;
/*    */ 
/*    */ import java.util.Map;
/*    */ import org.biojava.bio.symbol.Alignment;
/*    */ 
/*    */ 
/*    */ 
/*    */ class AlignmentCoords
/*    */ {
/*    */   Alignment align;
/*    */   Map positions;
/*    */   
/*    */   public AlignmentCoords(Alignment align, Map positions)
/*    */   {
/* 15 */     this.positions = positions;
/* 16 */     this.align = align;
/*    */   }
/*    */   
/*    */   public String toString() {
/* 20 */     return "Alignment Coords \n" + this.align.getLabels();
/*    */   }
/*    */ }


/* Location:              /home/lachlan/Desktop/psilc1.21/psilc.jar!/lc1/dp/AlignmentCoords.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */