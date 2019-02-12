/*     */ package lc1.treefam;
/*     */ 
/*     */ import forester.atv.ATVjframe;
/*     */ import forester.tree.TreeHelper;
/*     */ import java.io.BufferedReader;
/*     */ import java.io.BufferedWriter;
/*     */ import java.io.File;
/*     */ import java.io.FileReader;
/*     */ import java.io.FileWriter;
/*     */ import java.io.PrintStream;
/*     */ import java.io.PrintWriter;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import lc1.util.Print;
/*     */ import org.apache.commons.cli.CommandLine;
/*     */ import org.apache.commons.cli.OptionBuilder;
/*     */ import org.apache.commons.cli.Options;
/*     */ import org.apache.commons.cli.Parser;
/*     */ import org.apache.commons.cli.PosixParser;
/*     */ import pal.tree.Node;
/*     */ import pal.tree.SimpleNode;
/*     */ import pal.tree.SimpleTree;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class TaxonomyTree
/*     */   extends SimpleTree
/*     */ {
/*  30 */   static final Options OPTIONS = new Options() {};
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
/*  41 */   Map deletedNodesToChild = new HashMap();
/*     */   
/*     */   public static void main(String[] args)
/*     */     throws Exception
/*     */   {
/*  46 */     Parser parser = new PosixParser();
/*  47 */     CommandLine params = parser.parse(OPTIONS, args);
/*  48 */     File repos = new File(params.getOptionValue("repository"));
/*  49 */     File nodes = new File(repos, "nodes.dmp");
/*  50 */     File names = new File(repos, "names.dmp");
/*  51 */     String root = params.getOptionValue("root");
/*  52 */     pal.tree.Tree geneTree = null;
/*     */     
/*  54 */     TaxonomyTree t = new TaxonomyTree(nodes, names, root);
/*     */     
/*  56 */     if (params.hasOption("input")) {
/*  57 */       geneTree = new ReadTree(params.getOptionValue("input"));
/*  58 */       SDI sdi = new SDI(t, geneTree);
/*     */       
/*  60 */       File tmp_tree1 = new File("tmp1.nhx");
/*  61 */       PrintWriter pw1 = new PrintWriter(new BufferedWriter(new FileWriter(tmp_tree1)));
/*     */       
/*  63 */       NodeUtils.printNH(
/*  64 */         new SimpleTree(fixTree(sdi.species.getRoot(), false)), pw1, true, true, true);
/*  65 */       pw1.close();
/*  66 */       forester.tree.Tree treeF1 = TreeHelper.readNHtree(tmp_tree1);
/*  67 */       new ATVjframe(treeF1).showWhole();
/*     */       
/*  69 */       GeneTree gt = new GeneTree(sdi, geneTree, "tree1", 1.0D, 0.1D);
/*     */       
/*  71 */       System.err.println(Print.toString(gt.logUnnormalizedPdf()));
/*     */     }
/*     */     
/*  74 */     if (params.hasOption("output")) {
/*  75 */       File output = new File(params.getOptionValue("output"));
/*  76 */       PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(output)));
/*  77 */       NodeUtils.printNH(t, pw, false, true);
/*  78 */       pw.close();
/*     */     }
/*     */     
/*  81 */     if ((params.getOptionValue("graph", "false").equals("true")) && (geneTree != null))
/*     */     {
/*  83 */       File tmp_tree = new File("tmp.nhx");
/*  84 */       PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(tmp_tree)));
/*  85 */       Node root1 = fixTree(geneTree.getRoot(), false);
/*  86 */       Node root2 = new SimpleNode();
/*  87 */       root2.addChild(root1);
/*  88 */       Node outg = new SimpleNode();
/*  89 */       root2.addChild(outg);
/*  90 */       outg.setParent(root2);
/*  91 */       root1.setParent(root2);
/*  92 */       NodeUtils.printNH(
/*  93 */         new SimpleTree(root2), pw, true, true, true);
/*  94 */       pw.close();
/*  95 */       forester.tree.Tree treeF = TreeHelper.readNHtree(tmp_tree);
/*  96 */       new ATVjframe(treeF).showWhole();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private static Node makeRootNode(File nodes, File names, String rootName)
/*     */   {
/* 105 */     Node root = null;
/*     */     try {
/* 107 */       BufferedReader br1 = new BufferedReader(new FileReader(names));
/* 108 */       String str = "";
/* 109 */       Map nameToNode = new HashMap();
/* 110 */       while ((str = br1.readLine()) != null) {
/* 111 */         String[] row = str.split("\\|");
/*     */         
/* 113 */         if (row[3].trim().startsWith("scientific")) {
/* 114 */           String id = row[0].trim();
/* 115 */           AttributeIdentifier ident = new AttributeIdentifier(id);
/* 116 */           Node node = new SimpleNode();
/* 117 */           node.setIdentifier(ident);
/* 118 */           nameToNode.put(id, node);
/* 119 */           String name = row[1].trim();
/* 120 */           ident.setAttribute("S", name);
/* 121 */           if (name.equals(rootName)) {
/* 122 */             root = node;
/*     */           }
/*     */         }
/*     */       }
/*     */       
/*     */ 
/*     */ 
/* 129 */       if (root == null) throw new Exception("root does not exist " + rootName);
/* 130 */       br1.close();
/*     */       
/* 132 */       BufferedReader br = new BufferedReader(new FileReader(nodes));
/* 133 */       while ((str = br.readLine()) != null) {
/* 134 */         String[] row = str.split("\\|");
/* 135 */         String taxon = row[0].trim();
/* 136 */         Node node = (Node)nameToNode.get(taxon);
/* 137 */         String parent = null;
/* 138 */         if (node != root) {
/* 139 */           parent = row[1].trim();
/* 140 */           Node parentN = (Node)nameToNode.get(parent);
/* 141 */           parentN.addChild(node);
/* 142 */           node.setParent(parentN);
/*     */         }
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 150 */       br.close();
/*     */     } catch (Exception exc) {
/* 152 */       exc.printStackTrace();
/*     */     }
/* 154 */     return root;
/*     */   }
/*     */   
/*     */   public static pal.tree.Tree withoutSingleChildNodes(pal.tree.Tree tree)
/*     */   {
/* 159 */     return new SimpleTree(fixTree(tree.getRoot(), false));
/*     */   }
/*     */   
/*     */ 
/*     */   public static Node fixTree(Node root, boolean useLowerIdentifier)
/*     */   {
/* 165 */     if (root.getChildCount() == 1)
/*     */     {
/* 167 */       Node node = fixTree(root.getChild(0), useLowerIdentifier);
/* 168 */       node.setBranchLength(node.getBranchLength() + root.getBranchLength());
/* 169 */       if (useLowerIdentifier) node.setIdentifier(root.getChild(0).getIdentifier()); else
/* 170 */         node.setIdentifier(root.getIdentifier());
/* 171 */       return node;
/*     */     }
/* 173 */     for (int i = 0; i < root.getChildCount(); i++) {
/* 174 */       root.setChild(i, fixTree(root.getChild(i), useLowerIdentifier));
/*     */     }
/* 176 */     return root;
/*     */   }
/*     */   
/*     */   public TaxonomyTree(File nodes, File names, String root)
/*     */   {
/* 181 */     super(makeRootNode(nodes, names, root));
/*     */   }
/*     */ }


/* Location:              /home/lachlan/Desktop/psilc1.21/psilc.jar!/lc1/treefam/TaxonomyTree.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */