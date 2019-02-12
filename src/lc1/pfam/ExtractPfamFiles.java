/*    */ package lc1.pfam;
/*    */ 
/*    */ import java.io.File;
/*    */ import java.io.PrintStream;
/*    */ import java.util.Iterator;
/*    */ import java.util.List;
/*    */ import lc1.util.SheetIO;
/*    */ import org.biojava.utils.ProcessTools;
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
/*    */ public class ExtractPfamFiles
/*    */ {
/*    */   public static void main(String[] args)
/*    */     throws Exception
/*    */   {
/* 26 */     File f = new File(args[0]);
/* 27 */     File repos = new File(args[1]);
/* 28 */     File pfam = new File("Pfam");
/* 29 */     if (!pfam.exists()) pfam.mkdir();
/* 30 */     for (Iterator it = SheetIO.read(f, "__"); it.hasNext();) {
/* 31 */       List l = (List)it.next();
/* 32 */       System.err.println(l);
/* 33 */       File s = new File(repos, (String)l.get(0));
/* 34 */       String[] command = { "cp", "-r", s.getAbsolutePath(), 
/* 35 */         pfam.getAbsolutePath() };
/* 36 */       ProcessTools.exec(command, null, null, null);
/*    */     }
/*    */   }
/*    */ }


/* Location:              /home/lachlan/Desktop/psilc1.21/psilc.jar!/lc1/pfam/ExtractPfamFiles.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */