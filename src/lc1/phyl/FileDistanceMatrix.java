/*    */ package lc1.phyl;
/*    */ 
/*    */ import forester.tree.Node;
/*    */ import java.io.File;
/*    */ import java.io.PrintStream;
/*    */ import java.util.Enumeration;
/*    */ import java.util.HashMap;
/*    */ import java.util.Iterator;
/*    */ import java.util.List;
/*    */ import java.util.Map;
/*    */ import java.util.Vector;
/*    */ import lc1.util.SheetIO;
/*    */ import pal.alignment.ReadAlignment;
/*    */ import pal.distance.AlignmentDistanceMatrix;
/*    */ import pal.distance.DistanceMatrix;
/*    */ import pal.misc.IdGroup;
/*    */ import pal.misc.Identifier;
/*    */ import pal.misc.SimpleIdGroup;
/*    */ 
/*    */ public class FileDistanceMatrix extends DistanceMatrix
/*    */ {
/*    */   public static void main1(String[] args) throws Exception
/*    */   {
/* 24 */     AlignmentDistanceMatrix dist = new AlignmentDistanceMatrix(
/* 25 */       pal.alignment.SitePattern.getSitePattern(new ReadAlignment(args[0])));
/* 26 */     for (int i = 0; i < dist.getIdCount(); i++) {
/* 27 */       for (int j = i + 1; j < dist.getIdCount(); j++) {
/* 28 */         System.out.println(dist.getIdentifier(i) + 
/* 29 */           " " + dist.getIdentifier(j) + " " + dist.getDistance(i, j));
/*    */       }
/*    */     }
/*    */   }
/*    */   
/*    */   public static void main(String[] args) throws Exception {
/* 35 */     pal.tree.Tree tree = new NeighborJoiningTree(new FileDistanceMatrix(new File(args[0]).listFiles()));
/* 36 */     forester.tree.Tree treeF = MaxLikelihoodTree.convert(tree);
/* 37 */     Node root = treeF.getRoot();
/*    */     
/* 39 */     Map m = new HashMap();
/* 40 */     for (Enumeration en = root.getAllExternalChildren().elements(); en.hasMoreElements();) {
/* 41 */       Node node = (Node)en.nextElement();
/* 42 */       m.put(node.getSeqName(), node);
/*    */     }
/* 44 */     for (Iterator it = SheetIO.read(new File("taxonomyF"), "\t"); it.hasNext();) {
/* 45 */       List row = (List)it.next();
/* 46 */       if (m.containsKey(row.get(0))) {
/* 47 */         Node node = (Node)m.get(row.get(0));
/* 48 */         node.setSpecies((String)row.get(2));
/*    */       }
/*    */     }
/*    */     
/*    */ 
/* 53 */     MaxLikelihoodTree.graphTree(treeF);
/*    */   }
/*    */   
/*    */   public FileDistanceMatrix(File[] f)
/*    */     throws Exception
/*    */   {
/* 59 */     super(new double[f.length][f.length], getIdGroup(f));
/* 60 */     for (int i = 0; i < f.length; i++) {
/* 61 */       for (int j = 0; j < f.length; j++) {
/* 62 */         setDistance(i, j, 1.0D);
/*    */       }
/* 64 */       setDistance(i, i, 0.0D);
/*    */     }
/* 66 */     for (int i = 0; i < f.length; i++) {
/* 67 */       for (Iterator it = SheetIO.read(f[i], "\t"); it.hasNext();) {
/* 68 */         List row = (List)it.next();
/*    */         
/* 70 */         int k = whichIdNumber((String)row.get(0));
/* 71 */         int j = whichIdNumber((String)row.get(1));
/* 72 */         double dist = Double.parseDouble((String)row.get(2));
/* 73 */         setDistance(k, j, dist);
/* 74 */         setDistance(j, k, dist);
/*    */       }
/*    */     }
/*    */   }
/*    */   
/*    */   private static IdGroup getIdGroup(File[] c)
/*    */   {
/* 81 */     Identifier[] ids = new Identifier[c.length];
/* 82 */     for (int i = 0; i < c.length; i++) {
/* 83 */       ids[i] = new Identifier(c[i].getName());
/*    */     }
/* 85 */     return new SimpleIdGroup(ids);
/*    */   }
/*    */ }


/* Location:              /home/lachlan/Desktop/psilc1.21/psilc.jar!/lc1/phyl/FileDistanceMatrix.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */