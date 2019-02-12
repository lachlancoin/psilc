/*     */ package lc1.util;
/*     */ 
/*     */ import java.io.BufferedReader;
/*     */ import java.io.BufferedWriter;
/*     */ import java.io.File;
/*     */ import java.io.FileReader;
/*     */ import java.io.FileWriter;
/*     */ import java.io.PrintStream;
/*     */ import java.io.PrintWriter;
/*     */ import java.io.PushbackReader;
/*     */ import java.io.StringReader;
/*     */ import java.io.StringWriter;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collections;
/*     */ import java.util.Comparator;
/*     */ import java.util.List;
/*     */ import lc1.phyl.AlignUtils;
/*     */ import lc1.treefam.SDI;
/*     */ import org.apache.commons.cli.CommandLine;
/*     */ import org.apache.commons.cli.OptionBuilder;
/*     */ import org.apache.commons.cli.Options;
/*     */ import org.apache.commons.cli.Parser;
/*     */ import org.apache.commons.cli.PosixParser;
/*     */ import org.biojava.utils.ProcessTools;
/*     */ import pal.alignment.Alignment;
/*     */ import pal.alignment.AlignmentUtils;
/*     */ import pal.alignment.ReadAlignment;
/*     */ import pal.distance.DistanceMatrix;
/*     */ import pal.misc.IdGroup;
/*     */ import pal.misc.Identifier;
/*     */ import pal.misc.SimpleIdGroup;
/*     */ import pal.tree.Node;
/*     */ import pal.tree.NodeUtils;
/*     */ import pal.tree.ReadTree;
/*     */ import pal.tree.SimpleTree;
/*     */ import pal.tree.Tree;
/*     */ import pal.tree.TreeDistanceMatrix;
/*     */ import pal.tree.TreeUtils;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class PAML1
/*     */ {
/*  47 */   static final Options OPTIONS = new Options() {};
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static void main(String[] args)
/*     */     throws Exception
/*     */   {
/*  59 */     Parser DP_PARSER = new PosixParser();
/*  60 */     CommandLine params = DP_PARSER.parse(OPTIONS, args);
/*  61 */     String[] filesL = params.getOptionValues("file");
/*  62 */     File dir = new File(params.getOptionValue("dir"));
/*  63 */     for (int i = 0; i < filesL.length; i++) {
/*  64 */       File files = new File(dir, filesL[i]);
/*  65 */       String bin = params.getOptionValue("bin", "");
/*     */       
/*     */       try
/*     */       {
/*  69 */         System.err.println(files.getAbsolutePath());
/*  70 */         File dna_aln = new File(files, "seed.dna.align");
/*  71 */         File dn_ds = new File(files, "dn_ds");
/*  72 */         PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(dn_ds)));
/*  73 */         File treeF = new File(files, "seed.nh");
/*  74 */         if ((dna_aln.exists()) && (dna_aln.length() > 0L)) {
/*  75 */           Tree treeOrig = new ReadTree(treeF.getAbsolutePath());
/*     */           
/*  77 */           Alignment orig = new ReadAlignment(dna_aln.getAbsolutePath());
/*  78 */           orig = AlignUtils.restrictAlignment(orig, treeOrig);
/*     */           
/*  80 */           if (treeOrig.getIdCount() != orig.getIdCount()) throw new Exception("counts must agree");
/*  81 */           Object[] obj = getReducedAlignment(orig, treeOrig, 
/*  82 */             Integer.parseInt(params.getOptionValue("max")) - 1);
/*  83 */           Alignment align = (Alignment)obj[0];
/*  84 */           Tree tree = (Tree)obj[1];
/*     */           
/*  86 */           if (tree.getIdCount() != align.getIdCount()) throw new Exception("counts must agree");
/*  87 */           getDnDs(align, tree, files, bin);
/*     */           
/*     */ 
/*  90 */           File mlc = new File(files, "mlc");
/*  91 */           if (mlc.exists()) {
/*  92 */             double[] dnds = parse(mlc);
/*  93 */             System.err.println("here ! " + Print.toString(dnds));
/*  94 */             pw.println("\n" + dnds[0] + "\n" + dnds[1]);
/*     */             
/*     */ 
/*     */ 
/*  98 */             pw.close();
/*     */           }
/*     */           
/*     */         }
/*     */       }
/*     */       catch (Exception exc)
/*     */       {
/* 105 */         exc.printStackTrace();
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public static Identifier[] getClosestExcludingNode(Tree tree, Node target, int n) {
/* 111 */     DistanceMatrix dm = new TreeDistanceMatrix(tree);
/* 112 */     int id = dm.whichIdNumber(target.getIdentifier().getName());
/* 113 */     List l = new ArrayList();
/* 114 */     for (int i = 0; i < dm.getIdCount(); i++) {
/* 115 */       if (i != id)
/* 116 */         l.add(new double[] { dm.getDistance(id, i), i });
/*     */     }
/* 118 */     Collections.sort(l, new Comparator() {
/*     */       public int compare(Object o1, Object o2) {
/* 120 */         double d1 = ((double[])o1)[0];
/* 121 */         double d2 = ((double[])o2)[0];
/* 122 */         if (d1 != d2) return d1 < d2 ? -1 : 1;
/* 123 */         return 0;
/*     */       }
/* 125 */     });
/* 126 */     Identifier[] ids = new Identifier[Math.min(n, l.size())];
/* 127 */     for (int i = 0; i < ids.length; i++) {
/* 128 */       ids[i] = dm.getIdentifier((int)((double[])l.get(i))[1]);
/*     */     }
/* 130 */     return ids;
/*     */   }
/*     */   
/*     */   public static Object[] getReducedAlignment(Alignment align, Tree tree, int max) throws Exception {
/* 134 */     int i = -1;
/* 135 */     for (int j = 0; j < align.getIdCount(); j++) {
/* 136 */       if (align.getIdentifier(j).getName().startsWith("OTT")) {
/* 137 */         i = j;
/* 138 */         break;
/*     */       }
/*     */     }
/* 141 */     Node node = NodeUtils.findByIdentifier(tree.getRoot(), align.getIdentifier(i));
/* 142 */     Identifier[] br_leaves = getClosestExcludingNode(tree, node, max);
/* 143 */     IdGroup restIdG = new SimpleIdGroup(
/* 144 */       new SimpleIdGroup(new SimpleIdGroup(new Identifier[] { node.getIdentifier() }), 
/* 145 */       new SimpleIdGroup(br_leaves)));
/* 146 */     Node[] nodes = new Node[br_leaves.length + 1];
/* 147 */     System.arraycopy(NodeUtils.findByIdentifier(tree.getRoot(), br_leaves), 0, nodes, 0, br_leaves.length);
/* 148 */     nodes[br_leaves.length] = node;
/* 149 */     Tree tree_rest = new SimpleTree(SDI.trim(tree.getRoot(), nodes));
/*     */     
/*     */ 
/*     */ 
/* 153 */     Alignment align_inner = 
/* 154 */       AlignUtils.restrictAlignment(align, restIdG);
/* 155 */     return new Object[] { align_inner, tree_rest };
/*     */   }
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
/*     */ 
/*     */ 
/*     */   public static void getDnDs(Alignment align, Tree tree, File dest, String paml)
/*     */     throws Exception
/*     */   {
/* 182 */     File tmp = new File(".");
/* 183 */     if (!tmp.exists()) tmp.mkdir();
/* 184 */     File treeFile = new File(tmp, "tree_tmp1234");
/* 185 */     File alignFile = new File(tmp, "align_tmp1234");
/*     */     
/* 187 */     PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(alignFile)));
/* 188 */     System.err.println(align);
/* 189 */     AlignmentUtils.printSequential(align, pw);
/* 190 */     pw.close();
/* 191 */     PrintWriter pw1 = new PrintWriter(new BufferedWriter(new FileWriter(treeFile)));
/* 192 */     TreeUtils.printNH(tree, pw1, false, false, false);
/* 193 */     pw1.println();
/* 194 */     pw1.close();
/* 195 */     boolean treeExists = true;
/* 196 */     String[] command = { paml + "/codeml", 
/* 197 */       paml + "/codonml.ctl_" + treeExists };
/* 198 */     System.err.println(Arrays.asList(command));
/*     */     try {
/* 200 */       StringWriter out = new StringWriter();
/* 201 */       StringWriter err = new StringWriter();
/*     */       
/* 203 */       ProcessTools.exec(command, null, 
/* 204 */         out, err);
/*     */       
/* 206 */       System.err.print(err.getBuffer().toString());
/* 207 */       System.err.print(out.getBuffer().toString());
/* 208 */       command = new String[] { "cp", tmp.getAbsolutePath() + "/mlc", dest.getAbsolutePath() };
/* 209 */       ProcessTools.exec(command, null, 
/* 210 */         null, null);
/*     */     } catch (Exception exc) {
/* 212 */       exc.printStackTrace();
/*     */     }
/*     */   }
/*     */   
/*     */   public static double[] parse(File mlc)
/*     */   {
/*     */     try {
/* 219 */       BufferedReader in = new BufferedReader(new FileReader(mlc));
/* 220 */       for (int i = 0; i < 3; i++) {
/* 221 */         in.readLine();
/*     */       }
/* 223 */       int length = Integer.parseInt(in.readLine().split("=")[1].trim().split("\\s+")[0]);
/* 224 */       String st = in.readLine();
/*     */       
/* 226 */       while (!st.startsWith("Use runmode")) {
/* 227 */         st = in.readLine();
/*     */       }
/* 229 */       in.readLine();
/* 230 */       int ind = -1;
/* 231 */       double[][] dist = new double[length][length];
/* 232 */       for (int i = 0; i < length; i++) {
/* 233 */         dist[i][i] = 0.0D;
/* 234 */         st = in.readLine().replace(')', ' ').replace('(', ' ').replace('-', ' ');
/*     */         
/*     */ 
/*     */ 
/*     */ 
/* 239 */         String[] line = st.split("\\s+");
/*     */         try {
/* 241 */           if (line[0].startsWith("OTT")) {
/* 242 */             ind = i;
/*     */           }
/* 244 */           for (int j = 0; j * 3 + 1 < line.length; j++) {
/* 245 */             double dn = Double.parseDouble(line[(j * 3 + 2)]);
/* 246 */             double ds = Double.parseDouble(line[(j * 3 + 3)]);
/* 247 */             dist[i][j] = (dn / ds);
/* 248 */             dist[j][i] = dist[i][j];
/*     */           }
/*     */         } catch (Exception exc) {
/* 251 */           exc.printStackTrace();
/* 252 */           System.err.println(Arrays.asList(line));
/* 253 */           System.exit(0);
/*     */         }
/*     */       }
/* 256 */       double nei_dist = 0.0D;
/* 257 */       for (int i = 0; i < length; i++) {
/* 258 */         nei_dist += dist[ind][i];
/*     */       }
/* 260 */       nei_dist /= (dist.length - 1);
/*     */       
/*     */ 
/* 263 */       while (!st.startsWith("tree length")) {
/* 264 */         st = in.readLine();
/*     */       }
/* 266 */       in.readLine();
/* 267 */       Tree tree1 = new ReadTree(new PushbackReader(new StringReader(in.readLine())));
/* 268 */       in.readLine();
/* 269 */       Tree tree2 = new ReadTree(new PushbackReader(new StringReader(in.readLine())));
/*     */       
/*     */ 
/* 272 */       int index = -1;
/* 273 */       for (int j = 0; j < tree2.getExternalNodeCount(); j++) {
/* 274 */         if (tree2.getExternalNode(j).getIdentifier().getName().startsWith("OTT")) {
/* 275 */           Node n = tree1.getExternalNode(j);
/* 276 */           if (n.getBranchLength() != tree2.getExternalNode(j).getBranchLength()) throw new Exception("something wrong");
/* 277 */           index = Integer.parseInt(n.getIdentifier().getName());
/* 278 */           break;
/*     */         }
/*     */       }
/*     */       
/*     */ 
/*     */ 
/* 284 */       while (!st.startsWith(" branch")) {
/* 285 */         st = in.readLine();
/*     */       }
/*     */       
/* 288 */       in.readLine();
/* 289 */       double yang_dnds = 0.0D;
/* 290 */       while ((st = in.readLine()) != null) {
/* 291 */         String[] str = st.split("\\s+");
/* 292 */         String[] id = str[1].split("\\.\\.");
/*     */         
/* 294 */         if (Integer.parseInt(id[1]) == index) {
/* 295 */           yang_dnds = Double.parseDouble(str[5]);
/* 296 */           break;
/*     */         }
/*     */       }
/* 299 */       return new double[] { yang_dnds, nei_dist };
/*     */     } catch (Exception exc) {
/* 301 */       exc.printStackTrace();
/* 302 */       System.exit(0);
/*     */     }
/* 304 */     return null;
/*     */   }
/*     */ }


/* Location:              /home/lachlan/Desktop/psilc1.21/psilc.jar!/lc1/util/PAML1.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */