/*    */ package lc1.util;
/*    */ 
/*    */ import java.io.File;
/*    */ import org.apache.commons.cli.CommandLine;
/*    */ import org.apache.commons.cli.OptionBuilder;
/*    */ import org.apache.commons.cli.Options;
/*    */ import org.apache.commons.cli.PosixParser;
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
/*    */ public class MakeAlignments
/*    */ {
/* 24 */   static final Options OPTIONS = new Options() {};
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
/*    */   public static void main(String[] args)
/*    */     throws Exception
/*    */   {
/* 44 */     CommandLine params = new PosixParser().parse(OPTIONS, args);
/* 45 */     File clusterDir = new File(params.getOptionValue("dir"), "cluster");
/* 46 */     File[] files = clusterDir.listFiles();
/* 47 */     for (int i = 0; i < files.length; i++) {
/* 48 */       getAlignments(clusterDir.getParentFile(), files[i], params.getOptionValue("bin"));
/*    */     }
/*    */   }
/*    */   
/*    */   static File getAlignments(File dir, File input1, String bin) throws Exception
/*    */   {
/* 54 */     String fileName = input1.getName();
/* 55 */     if (fileName.indexOf('.') >= 0) fileName = fileName.split("\\.")[0];
/* 56 */     File input = new File(input1.getParentFile(), fileName);
/* 57 */     File check = new File("/nfs/farm/Pfam/lc1/scop_test1/align/" + fileName + ".align");
/* 58 */     if (check.exists()) return null;
/* 59 */     Clustal.dna = false;
/* 60 */     File alnDir = new File(dir + "/align/");
/* 61 */     if (!alnDir.exists()) alnDir.mkdir();
/*    */     try
/*    */     {
/* 64 */       return Clustal.alignHomologs(input, dir, bin, 
/* 65 */         0);
/*    */     }
/*    */     catch (Exception exc) {
/* 68 */       exc.printStackTrace();
/*    */     }
/* 70 */     return null;
/*    */   }
/*    */ }


/* Location:              /home/lachlan/Desktop/psilc1.21/psilc.jar!/lc1/util/MakeAlignments.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */