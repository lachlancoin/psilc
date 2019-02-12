/*    */ package lc1.util;
/*    */ 
/*    */ import java.io.BufferedWriter;
/*    */ import java.io.File;
/*    */ import java.io.FileWriter;
/*    */ import java.io.PrintStream;
/*    */ import java.io.PrintWriter;
/*    */ import pal.alignment.Alignment;
/*    */ import pal.alignment.AlignmentUtils;
/*    */ import pal.alignment.ReadAlignment;
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
/*    */ public class Reformat
/*    */ {
/*    */   public static void main(String[] args)
/*    */     throws Exception
/*    */   {
/* 27 */     File[] f = new File("align").listFiles();
/* 28 */     File out = new File("align_phylip");
/* 29 */     if (!out.exists()) out.mkdir();
/* 30 */     for (int i = 0; i < f.length; i++) {
/*    */       try {
/* 32 */         File outF = new File(out, f[i].getName());
/* 33 */         if ((!outF.exists()) || (outF.length() <= 0L)) {
/* 34 */           Alignment align = new ReadAlignment(f[i].getAbsolutePath());
/*    */           
/*    */ 
/* 37 */           PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(outF)));
/* 38 */           AlignmentUtils.printInterleaved(align, pw);
/* 39 */           pw.close();
/*    */         }
/*    */       } catch (Exception exc) {
/* 42 */         System.err.println("didn't work for " + f[i]);
/*    */       }
/*    */     }
/*    */   }
/*    */ }


/* Location:              /home/lachlan/Desktop/psilc1.21/psilc.jar!/lc1/util/Reformat.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */