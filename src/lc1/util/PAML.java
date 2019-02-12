/*     */ package lc1.util;
/*     */ 
/*     */ import java.io.BufferedReader;
/*     */ import java.io.BufferedWriter;
/*     */ import java.io.File;
/*     */ import java.io.FileReader;
/*     */ import java.io.FileWriter;
/*     */ import java.io.IOException;
/*     */ import java.io.PrintStream;
/*     */ import java.io.PrintWriter;
/*     */ import java.io.PushbackReader;
/*     */ import java.io.StringReader;
/*     */ import java.io.StringWriter;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collections;
/*     */ import java.util.Comparator;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import lc1.phyl.AlignUtils;
/*     */ import org.biojava.utils.ProcessTools;
/*     */ import pal.alignment.Alignment;
/*     */ import pal.alignment.AlignmentUtils;
/*     */ import pal.alignment.ReadAlignment;
/*     */ import pal.alignment.SimpleAlignment;
/*     */ import pal.alignment.StrippedAlignment;
/*     */ import pal.datatype.Codons;
/*     */ import pal.distance.DistanceMatrix;
/*     */ import pal.misc.IdGroup;
/*     */ import pal.misc.Identifier;
/*     */ import pal.misc.SimpleIdGroup;
/*     */ import pal.tree.AttributeNode;
/*     */ import pal.tree.Node;
/*     */ import pal.tree.NodeUtils;
/*     */ import pal.tree.ReadTree;
/*     */ import pal.tree.Tree;
/*     */ import pal.tree.TreeDistanceMatrix;
/*     */ import pal.tree.TreeUtils;
/*     */ 
/*     */ 
/*     */ 
/*     */ public class PAML
/*     */ {
/*     */   public static void initialise()
/*     */   {
/*     */     try
/*     */     {
/*  49 */       File f = new File("codeml.ctl");
/*  50 */       if (f.exists()) {
/*  51 */         BufferedReader br = new BufferedReader(new FileReader(f));
/*     */         String st;
/*  53 */         while ((st = br.readLine()) != null) { String st;
/*  54 */           if (st.indexOf('=') >= 0) {
/*  55 */             String[] str = st.trim().split("=");
/*  56 */             String key = str[0].trim().split("\\s+")[0];
/*  57 */             String val = str[1].trim().split("\\s+")[0];
/*  58 */             for (int i = 0; i < pamlparams.length; i++) {
/*  59 */               if (pamlparams[i][0].equals(key)) {
/*  60 */                 pamlparams[i][1] = val;
/*  61 */                 break;
/*     */               }
/*     */             }
/*     */           }
/*     */         }
/*     */         
/*  67 */         br.close();
/*     */       }
/*     */     } catch (Exception exc) {
/*  70 */       exc.printStackTrace();
/*     */     }
/*     */   }
/*     */   
/*  74 */   public static void main(String[] args) throws Exception { File f = new File(".");
/*  75 */     File[] files = f.listFiles();
/*  76 */     for (int i = 0; i < files.length; i++) {
/*     */       try {
/*  78 */         System.err.println(files[i].getAbsolutePath());
/*  79 */         File dna_aln = new File(files[i], "seed.dna.align");
/*  80 */         File dn_ds = new File(files[i], "dn_ds");
/*  81 */         PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(dn_ds)));
/*  82 */         File treeF = new File(files[i], "seed.nh");
/*  83 */         if ((dna_aln.exists()) && (dna_aln.length() > 0L)) {
/*  84 */           Tree treeOrig = new ReadTree(treeF.getAbsolutePath());
/*  85 */           Alignment orig = new ReadAlignment(dna_aln.getAbsolutePath());
/*  86 */           System.err.println(treeOrig);
/*  87 */           if (treeOrig.getIdCount() != orig.getIdCount()) throw new Exception("counts must agree");
/*  88 */           boolean[] collapse = { true };
/*  89 */           for (int k = 1; k < collapse.length; k++)
/*     */           {
/*     */ 
/*     */ 
/*  93 */             PAML paml = new PAML(orig, treeOrig);
/*  94 */             double[] dnds = paml.getDnDs("OTT");
/*     */             
/*  96 */             pw.println(collapse[k] + "\n" + dnds[0] + "\n" + dnds[1]);
/*     */           }
/*     */           
/*     */ 
/* 100 */           pw.close();
/*     */         }
/*     */         
/*     */       }
/*     */       catch (Exception exc)
/*     */       {
/* 106 */         exc.printStackTrace();
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public static Identifier[] getClosestExcludingNode(Tree tree, Node target, int n)
/*     */   {
/* 114 */     DistanceMatrix dm = new TreeDistanceMatrix(tree);
/* 115 */     int id = dm.whichIdNumber(target.getIdentifier().getName());
/* 116 */     List l = new ArrayList();
/* 117 */     for (int i = 0; i < dm.getIdCount(); i++) {
/* 118 */       if (i != id)
/* 119 */         l.add(new double[] { dm.getDistance(id, i), i });
/*     */     }
/* 121 */     Collections.sort(l, new Comparator() {
/*     */       public int compare(Object o1, Object o2) {
/* 123 */         double d1 = ((double[])o1)[0];
/* 124 */         double d2 = ((double[])o2)[0];
/* 125 */         if (d1 != d2) return d1 < d2 ? -1 : 1;
/* 126 */         return 0;
/*     */       }
/* 128 */     });
/* 129 */     Identifier[] ids = new Identifier[n];
/* 130 */     for (int i = 0; i < ids.length; i++) {
/* 131 */       ids[i] = dm.getIdentifier((int)((double[])l.get(i))[1]);
/*     */     }
/* 133 */     return ids;
/*     */   }
/*     */   
/*     */   public static Alignment getReducedAlignment(Alignment align, Tree tree, int max) throws Exception {
/* 137 */     int i = -1;
/* 138 */     for (int j = 0; j < align.getIdCount(); j++) {
/* 139 */       if (align.getIdentifier(j).getName().startsWith("OTT")) {
/* 140 */         i = j;
/* 141 */         break;
/*     */       }
/*     */     }
/* 144 */     Node node = NodeUtils.findByIdentifier(tree.getRoot(), align.getIdentifier(i));
/* 145 */     Identifier[] br_leaves = getClosestExcludingNode(tree, node, max);
/* 146 */     IdGroup restIdG = new SimpleIdGroup(new Identifier[] {
/* 147 */       node.getIdentifier(), br_leaves[0] });
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 153 */     Alignment align_inner = 
/* 154 */       AlignUtils.restrictAlignment(align, restIdG);
/* 155 */     StrippedAlignment sub_align = new StrippedAlignment(AlignUtils.getCodonAlignmentFromDNA(align_inner));
/* 156 */     sub_align.setDataType(Codons.DEFAULT_INSTANCE);
/* 157 */     align = AlignUtils.getCodonAlignmentFromDNA(align);
/* 158 */     int[] alias = new int[br_leaves.length];
/*     */     
/* 160 */     for (int j = 0; j < br_leaves.length; j++) {
/* 161 */       alias[j] = align.whichIdNumber(br_leaves[j].getName());
/*     */     }
/*     */     
/* 164 */     for (int j = 0; j < align.getSiteCount(); j++) {
/* 165 */       char c = align.getData(alias[0], j);
/* 166 */       for (int k = 1; k < alias.length; k++) {
/* 167 */         if (align.getData(alias[k], j) != c) {
/* 168 */           sub_align.dropSite(j);
/* 169 */           break;
/*     */         }
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 176 */     return AlignUtils.getDNAAlignmentFromCodons(sub_align);
/*     */   }
/*     */   
/* 179 */   static final File paml = new File("/nfs/team71/phd/lc1/bin/paml3.13d");
/*     */   
/*     */   private static Alignment convertGaps(Alignment align) {
/* 182 */     String[] st = new String[align.getIdCount()];
/* 183 */     for (int i = 0; i < st.length; i++) {
/* 184 */       st[i] = align.getAlignedSequenceString(i).replace('?', '-');
/*     */     }
/* 186 */     return new SimpleAlignment(align, 
/* 187 */       st, "-", align.getDataType());
/*     */   }
/*     */   
/*     */   public PAML(Alignment align, Tree tree) throws Exception {
/* 191 */     this.align = align;
/* 192 */     this.tree = tree;
/* 193 */     File treeFile = new File("tree_tmp1234");
/* 194 */     File alignFile = new File("align_tmp1234");
/* 195 */     this.codonml = new File("codonml.ctl");
/* 196 */     this.mlc = getOutfile();
/* 197 */     if ((!this.mlc.exists()) || (this.mlc.length() == 0L)) {
/* 198 */       printCodeml();
/* 199 */       PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(alignFile)));
/* 200 */       AlignmentUtils.printSequential(convertGaps(align), pw);
/* 201 */       pw.close();
/* 202 */       PrintWriter pw1 = new PrintWriter(new BufferedWriter(new FileWriter(treeFile)));
/* 203 */       TreeUtils.printNH(tree, pw1, false, false);
/* 204 */       pw1.close();
/* 205 */       getDnDs();
/*     */     }
/* 207 */     parse();
/*     */   }
/*     */   
/*     */   private void getDnDs() throws Exception {
/* 211 */     boolean treeExists = true;
/* 212 */     String[] command = { "codeml", 
/* 213 */       "codonml.ctl" };
/* 214 */     System.err.println(Arrays.asList(command));
/*     */     
/* 216 */     StringWriter out = new StringWriter();
/* 217 */     StringWriter err = new StringWriter();
/*     */     
/* 219 */     int result = ProcessTools.exec(command, null, 
/* 220 */       out, err);
/* 221 */     String error = err.getBuffer().toString();
/* 222 */     String outp = out.getBuffer().toString();
/* 223 */     System.err.println(result);
/* 224 */     if (result < 1) throw new Exception(outp + "\n" + error);
/*     */   }
/*     */   
/*     */   public static File getOutfile()
/*     */   {
/* 229 */     StringBuffer sb = new StringBuffer("mlc");
/* 230 */     for (int i = 0; i < pamlparams.length; i++) {
/* 231 */       sb.append(pamlparams[i][1]);
/* 232 */       sb.append("_");
/*     */     }
/* 234 */     return new File(sb.toString()); }
/*     */   
/* 236 */   public static String[][] pamlparams = {
/*     */   
/* 238 */     { "noisy", "0", "0,1,2,3,9: how much rubbish on the screen" }, 
/* 239 */     { "verbose", "0", "0: concise; 1: detailed, 2: too much" }, 
/* 240 */     { "runmode", "0", "0: user tree;  1: semi-automatic;  2: automatic,3: StepwiseAddition; (4,5):PerturbationNNI; -2: pairwise" }, 
/* 241 */     { "seqtype", "1", "1:codons; 2:AAs; 3:codons-->AAs" }, 
/* 242 */     { "CodonFreq", "3", "0:1/61 each, 1:F1X4, 2:F3X4, 3:codon table" }, 
/* 243 */     { "aaDist", "0", "0:equal, +:geometric; -:linear, 1-6:G1974,Miyata,c,p,v,a" }, 
/* 244 */     { "model", "1", "0:one, 1:b, 2:2 or more dN/dS ratios for branches" }, 
/* 245 */     { "NSsites", "0", "0:one w;1:neutral;2:selection; 3:discrete;4:freqs  5:gamma;6:2gamma;7:beta;8:beta&w;9:beta&gamma 10:beta&gamma+1; 11:beta&normal>1; 12:0&2normal>1 13:3normal>0" }, 
/* 246 */     { "icode", "0", "*  * 0:universal code; 1:mammalian mt; 2-10:see below" }, 
/* 247 */     { "Mgene", "0", "0:rates, 1:separate; 2:diff pi, 3:diff kapa, 4:all diff" }, 
/* 248 */     { "fix_kappa", "0", "1: kappa fixed, 0: kappa to be estimated" }, 
/* 249 */     { "kappa", "2", "initial or fixed kappa" }, 
/* 250 */     { "fix_omega", "0", "1: omega or omega_1 fixed, 0: estimate" }, 
/* 251 */     { "omega", "3.14159", "initial or fixed omega, for codons" }, 
/* 252 */     { "fix_alpha", "1", "0:  estimate gamma shape parameter; 1: fix it at alpha" }, 
/* 253 */     { "alpha", "0", "*. * initial or fixed alpha, 0:infinity (constant rate)" }, 
/* 254 */     { "Malpha", "0", "different alphas for genes" }, 
/* 255 */     { "ncatG", "2", " # of categories in dG of NSsites models" }, 
/* 256 */     { "clock", "0", "0:no clock, 1:global clock; 2:local clock; 3:TipDate" }, 
/* 257 */     { "getSE", "0", " 0: don't want them, 1: want S.E.s of estimates" }, 
/* 258 */     { "RateAncestor", "0", "(0,1,2): rates (alpha>0) or ancestral states (1 or 2)" }, 
/* 259 */     { "Small_Diff", "3e-7", "" }, 
/* 260 */     { "cleandata", "0", "remove sites with ambiguity data (1:yes, 0:no)" }, 
/* 261 */     { "method", "1", "0: simultaneous; 1: one branch at a time" } };
/*     */   
/*     */   DistanceMatrix dn;
/*     */   
/*     */   DistanceMatrix ds;
/*     */   File mlc;
/*     */   Tree tree;
/*     */   Tree ml_tree;
/*     */   Alignment align;
/*     */   File codonml;
/*     */   
/*     */   private void parse()
/*     */   {
/*     */     try
/*     */     {
/* 276 */       BufferedReader in = new BufferedReader(new FileReader(this.mlc));
/* 277 */       for (int i = 0; i < 3; i++) {
/* 278 */         in.readLine();
/*     */       }
/* 280 */       int length = Integer.parseInt(in.readLine().split("=")[1].trim().split("\\s+")[0]);
/* 281 */       String st = in.readLine();
/*     */       
/*     */ 
/* 284 */       while (!st.startsWith("Codon position")) {
/* 285 */         st = in.readLine();
/*     */       }
/* 287 */       in.readLine();
/* 288 */       Map nodeToIndex = new HashMap();
/* 289 */       Node[] extNodes = new Node[this.tree.getExternalNodeCount() + this.tree.getInternalNodeCount() + 1];
/* 290 */       for (int i = 1; i <= length; i++) {
/* 291 */         while (!st.startsWith("#")) {
/* 292 */           st = in.readLine();
/*     */         }
/* 294 */         String[] str = st.substring(1).split(":");
/* 295 */         Node node = NodeUtils.findByIdentifier(this.tree.getRoot(), str[1].trim());
/* 296 */         int index = Integer.parseInt(str[0].trim());
/* 297 */         extNodes[index] = node;
/* 298 */         nodeToIndex.put(node, new Integer(index));
/* 299 */         st = in.readLine();
/*     */       }
/*     */       
/*     */ 
/*     */ 
/* 304 */       while (!st.startsWith("Nei &")) {
/* 305 */         st = in.readLine();
/*     */       }
/* 307 */       while (st.length() > 2) {
/* 308 */         st = in.readLine();
/*     */       }
/* 310 */       in.readLine();
/* 311 */       double[][] dist_dn = new double[length][length];
/* 312 */       double[][] dist_ds = new double[length][length];
/* 313 */       for (int i1 = 1; i1 <= length; i1++) {
/* 314 */         int i = this.tree.whichIdNumber(extNodes[i1].getIdentifier().getName());
/* 315 */         dist_dn[i][i] = 0.0D;
/* 316 */         dist_ds[i][i] = 0.0D;
/* 317 */         st = in.readLine().replace(')', ' ').replace('(', ' ').replace('-', ' ');
/* 318 */         String[] line = st.split("\\s+");
/* 319 */         for (int j1 = 0; j1 * 3 + 1 < line.length; j1++) {
/* 320 */           int j = this.tree.whichIdNumber(extNodes[(j1 + 1)].getIdentifier().getName());
/* 321 */           dist_dn[i][j] = (dist_dn[j][i] = Double.parseDouble(line[(j1 * 3 + 2)]));
/* 322 */           dist_ds[i][j] = (dist_ds[j][i] = Double.parseDouble(line[(j1 * 3 + 3)]));
/*     */         }
/*     */       }
/* 325 */       this.dn = new DistanceMatrix(dist_dn, this.tree);
/* 326 */       this.ds = new DistanceMatrix(dist_ds, this.tree);
/*     */       
/*     */ 
/*     */ 
/* 330 */       while (!st.startsWith("tree length")) {
/* 331 */         st = in.readLine();
/*     */       }
/* 333 */       in.readLine();
/* 334 */       in.readLine();
/* 335 */       in.readLine();
/* 336 */       this.ml_tree = new ReadTree(new PushbackReader(new StringReader(in.readLine())));
/*     */       
/*     */ 
/*     */ 
/* 340 */       while (!st.startsWith(" branch")) {
/* 341 */         st = in.readLine();
/*     */       }
/* 343 */       String[] header = st.trim().split("\\s+");
/* 344 */       in.readLine();
/* 345 */       List indexToString = new ArrayList();
/* 346 */       while ((st = in.readLine()).length() > 1) {
/* 347 */         String[] str = st.trim().split("\\s+");
/* 348 */         indexToString.add(str);
/*     */       }
/* 350 */       for (int k = indexToString.size() - 1; k >= 0; k--) {
/* 351 */         String[] str = (String[])indexToString.get(k);
/* 352 */         String[] id = str[0].split("\\.\\.");
/* 353 */         int from = Integer.parseInt(id[0]);
/* 354 */         int to = Integer.parseInt(id[1]);
/* 355 */         AttributeNode toN = (AttributeNode)extNodes[to];
/*     */         
/* 357 */         extNodes[from] = extNodes[to].getParent();
/*     */         
/* 359 */         for (int i = 1; i < str.length; i++) {
/* 360 */           toN.setAttribute(header[i].trim(), new Double(Double.parseDouble(str[i])));
/*     */         }
/*     */       }
/*     */     } catch (Exception exc) {
/* 364 */       exc.printStackTrace();
/* 365 */       System.exit(0);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public double[] getDnDs(String seq)
/*     */   {
/* 373 */     int index = -1;
/* 374 */     for (int i = 0; i < this.dn.getIdCount(); i++) {
/* 375 */       if (this.dn.getIdentifier(i).getName().startsWith(seq)) {
/* 376 */         index = i;
/* 377 */         break;
/*     */       }
/*     */     }
/* 380 */     double sum = 0.0D;
/* 381 */     for (int i = 0; i < this.dn.getIdCount(); i++) {
/* 382 */       sum += this.dn.getDistance(index, i) / this.ds.getDistance(index, i);
/*     */     }
/* 384 */     return new double[] { sum / this.dn.getIdCount(), 
/* 385 */       ((Double)((AttributeNode)NodeUtils.findByIdentifier(this.tree.getRoot(), this.dn.getIdentifier(index))).getAttribute("dN/dS")).doubleValue() };
/*     */   }
/*     */   
/*     */   public void printCodeml() throws IOException
/*     */   {
/* 390 */     PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(this.codonml)));
/* 391 */     pw.println("seqfile = align_tmp1234");
/* 392 */     pw.println("treefile = tree_tmp1234");
/* 393 */     pw.println("outfile = " + this.mlc.getName());
/* 394 */     for (int i = 0; i < pamlparams.length; i++) {
/* 395 */       pw.println(pamlparams[i][0] + " = " + pamlparams[i][1]);
/*     */     }
/* 397 */     pw.close();
/*     */   }
/*     */ }


/* Location:              /home/lachlan/Desktop/psilc1.21/psilc.jar!/lc1/util/PAML.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */