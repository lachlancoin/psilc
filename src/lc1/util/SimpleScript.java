/*    */ package lc1.util;
/*    */ 
/*    */ import java.io.File;
/*    */ 
/*    */ public class SimpleScript {
/*    */   public static void main(String[] args) throws Exception {
/*  7 */     File repos = new File(args[0]);
/*  8 */     File[] list = repos.listFiles();
/*  9 */     for (int i = 0; i < list.length; i++) {
/* 10 */       File dir = new File(list[i].getName());
/* 11 */       String[] command1 = { "mkdir", dir.getAbsolutePath() };
/* 12 */       String[] command2 = { "rcp", list[i].getAbsolutePath() + "/HMM_ls", "dir.getAbsolutePath()" };
/* 13 */       System.err.println(Print.toString(command1));
/* 14 */       System.err.println(Print.toString(command2));
/*    */     }
/*    */   }
/*    */ }


/* Location:              /home/lachlan/Desktop/psilc1.21/psilc.jar!/lc1/util/SimpleScript.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */