/*     */ package lc1.pseudo;
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
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collection;
/*     */ import java.util.HashSet;
/*     */ import java.util.List;
/*     */ import java.util.Set;
/*     */ import lc1.pfam.PfamAlphabet;
/*     */ import lc1.phyl.AlignUtils;
/*     */ import lc1.phyl.MaxLikelihoodTree;
/*     */ import lc1.treefam.AlignTools;
/*     */ import lc1.treefam.SDI;
/*     */ import lc1.util.PAML1;
/*     */ import org.apache.commons.cli.CommandLine;
/*     */ import org.apache.commons.cli.OptionBuilder;
/*     */ import org.apache.commons.cli.Options;
/*     */ import org.apache.commons.cli.Parser;
/*     */ import org.apache.commons.cli.PosixParser;
/*     */ import org.biojava.bio.seq.io.SymbolTokenization;
/*     */ import pal.alignment.Alignment;
/*     */ import pal.alignment.AlignmentUtils;
/*     */ import pal.alignment.SitePattern;
/*     */ import pal.datatype.Nucleotides;
/*     */ import pal.distance.AlignmentDistanceMatrix;
/*     */ import pal.distance.DistanceMatrix;
/*     */ import pal.misc.Identifier;
/*     */ import pal.substmodel.RateMatrix;
/*     */ import pal.substmodel.SubstitutionModel.Utils;
/*     */ import pal.substmodel.WAG;
/*     */ import pal.tree.NeighborJoiningTree;
/*     */ import pal.tree.Node;
/*     */ import pal.tree.NodeUtils;
/*     */ import pal.tree.ReadTree;
/*     */ import pal.tree.SimpleNode;
/*     */ import pal.tree.SimpleTree;
/*     */ import pal.tree.TreeUtils;
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
/*     */ public class Pseudo
/*     */ {
/*     */   static final String usageString = "java -jar psilc.jar --align <alignment_name> --fasta <fasta_file> --seq <sequence_to_test> --domain <domain>";
/*     */   static final String collapseDefault = "0.01";
/*     */   static CommandLine params;
/*  69 */   static final Options OPTIONS = new Options() {};
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
/*     */ 
/*     */ 
/*     */   public static void main(String[] args)
/*     */     throws Exception
/*     */   {
/*  97 */     Parser DP_PARSER = new PosixParser();
/*  98 */     main(DP_PARSER.parse(OPTIONS, args));
/*     */   }
/*     */   
/*     */   public static void main(CommandLine params)
/*     */   {
/*     */     try
/*     */     {
/* 105 */       NodeProbabilityCalculator.VERBOSE = Integer.parseInt(params.getOptionValue("verbose", "0")) > 0;
/* 106 */       File repository = new File(params.getOptionValue("repository", "."));
/* 107 */       params = params;
/*     */       
/* 109 */       File pfamls = new File(repository, "Pfam_ls");
/* 110 */       File pfam = new File(repository, "Pfam");
/* 111 */       if (((!pfamls.exists()) || (pfamls.length() == 0L)) && (
/* 112 */         (!pfam.exists()) || (!pfam.isDirectory()))) {
/* 113 */         throw new Exception("one of " + pfamls.getAbsolutePath() + " or " + pfam.getAbsolutePath() + " must exist if you specify repository as " + repository.getAbsolutePath());
/*     */       }
/*     */       
/* 116 */       System.err.println("using repository " + repository);
/* 117 */       if (params.hasOption("hmm")) {
/* 118 */         String[] hmm = params.getOptionValues("hmm");
/* 119 */         if (hmm.length != 8) throw new Exception("exactly 8 selection parameters must be specified");
/* 120 */         NodeProbabilityCalculator.SelectionModel.setVals(new double[][] {
/* 121 */           { Double.parseDouble(hmm[0]), Double.parseDouble(hmm[1]) }, 
/* 122 */           { Double.parseDouble(hmm[2]), Double.parseDouble(hmm[3]) }, 
/* 123 */           { Double.parseDouble(hmm[4]), Double.parseDouble(hmm[5]) }, 
/* 124 */           { Double.parseDouble(hmm[6]), Double.parseDouble(hmm[7]) } });
/*     */       }
/*     */       
/*     */ 
/* 128 */       PseudoManager.seqN = params.hasOption("seq") ? params.getOptionValue("seq") : null;
/* 129 */       PseudoManager.protrm = params.getOptionValue("protrm", "WAG");
/* 130 */       Collection allowedProtRm = Arrays.asList(new String[] { "WAG", "JTT", "WAG_GWF" });
/* 131 */       Collection allowedNucRm = Arrays.asList(new String[] { "TN", "HKY", "GTR", "F84" });
/* 132 */       if (!allowedProtRm.contains(PseudoManager.protrm)) throw new Exception(" allowed protein rate matrices are " + allowedProtRm);
/* 133 */       PseudoManager.nucrm = params.getOptionValue("nucrm", "HKY");
/* 134 */       if (!allowedNucRm.contains(PseudoManager.nucrm)) throw new Exception(" allowed protein rate matrices are " + allowedNucRm);
/* 135 */       PseudoManager.graph = params.getOptionValue("graph", "0").equals("1");
/* 136 */       PseudoManager.restrict = params.getOptionValue("restrict", "0").equals("1");
/* 137 */       if ((PseudoManager.restrict) && (PseudoManager.seqN == null)) throw new Exception("can only specify --restrict 1 when a sequence is also specified");
/* 138 */       PseudoManager.lnLPseudo = params.getOptionValue("color", "selection").equals("pseudo");
/* 139 */       PseudoManager.evd_thresh = Double.parseDouble(params.getOptionValue("pvalue", "1e-4"));
/* 140 */       if (params.hasOption("bin")) {
/* 141 */         AlignTools.bin = params.getOptionValues("bin");
/* 142 */         for (int i = 0; i < AlignTools.bin.length; i++) {
/* 143 */           int tmp585_583 = i; String[] tmp585_580 = AlignTools.bin;tmp585_580[tmp585_583] = (tmp585_580[tmp585_583] + "/");
/*     */         }
/*     */       }
/* 146 */       File parent = params.hasOption("dir") ? 
/* 147 */         new File(params.getOptionValue("dir")) : 
/* 148 */         new File(".").getParentFile();
/* 149 */       String[] files = 
/* 150 */         {params.hasOption("file") ? params.getOptionValues("file") : new File(".").getName() };
/*     */       
/*     */ 
/* 153 */       for (int ij = 0; ij < files.length; ij++) {
/* 154 */         File dir = new File(parent, files[ij]);
/* 155 */         PfamAlphabet.resetAlphabet();
/* 156 */         PfamAlphabet alph = PfamAlphabet.makeAlphabet(dir);
/* 157 */         PseudoManager.allNodes = params.getOptionValue("allNodes", "0").equals("1");
/* 158 */         SymbolTokenization tokenizer = alph.getTokenization("token");
/*     */         Alignment dna_align;
/*     */         Alignment dna_align;
/*     */         String id;
/* 162 */         if (params.hasOption("align")) {
/* 163 */           File alignments = new File(dir, params.getOptionValue("align"));
/* 164 */           String id = params.getOptionValue("align");
/* 165 */           dna_align = AlignTools.readMFA(alignments, Nucleotides.DEFAULT_INSTANCE);
/*     */         }
/*     */         else {
/* 168 */           File fastaFile = new File(dir, params.getOptionValue("fasta"));
/* 169 */           File alignments = getAlignments(fastaFile, true);
/* 170 */           dna_align = AlignTools.readMFA(alignments, Nucleotides.DEFAULT_INSTANCE);
/* 171 */           id = fastaFile.getName();
/*     */         }
/* 173 */         Alignment prot_align = AlignUtils.translate(AlignUtils.getCodonAlignmentFromDNA(dna_align));
/* 174 */         Object[] sym = getSymbol(dir, alph);
/* 175 */         if (id.indexOf('.') >= 0) id = id.substring(0, id.indexOf('.'));
/* 176 */         File treeF = new File(dir, id + ".nhx");
/* 177 */         int[] max_nodes = { 100, 100 };
/* 178 */         if (params.hasOption("max_nodes")) {
/* 179 */           String[] st = params.getOptionValues("max_nodes");
/* 180 */           for (int i = 0; i < st.length; i++) {
/* 181 */             max_nodes[i] = Integer.parseInt(st[i]);
/*     */           }
/*     */         }
/* 184 */         if (prot_align.getIdCount() == 2) {
/* 185 */           SitePattern sp = SitePattern.getSitePattern(prot_align);
/* 186 */           DistanceMatrix dm = new AlignmentDistanceMatrix(sp, 
/* 187 */             SubstitutionModel.Utils.createSubstitutionModel(new WAG(AlignmentUtils.estimateFrequencies(prot_align))));
/* 188 */           pal.tree.Tree tree = null;
/* 189 */           if (params.hasOption("seq")) {
/* 190 */             Node[] node = {
/* 191 */               new SimpleNode(sp.getIdentifier(0).getName(), 0.0D), 
/* 192 */               new SimpleNode(sp.getIdentifier(1).getName(), 0.0D) };
/*     */             
/* 194 */             String seq = params.getOptionValue("seq");
/* 195 */             if (sp.getIdentifier(0).getName().startsWith(seq)) {
/* 196 */               node[0].setBranchLength(dm.getDistance(0, 1));
/*     */             }
/* 198 */             else if (sp.getIdentifier(1).getName().startsWith(seq)) {
/* 199 */               node[1].setBranchLength(dm.getDistance(0, 1));
/*     */             }
/*     */             else {
/* 202 */               node[0].setBranchLength(dm.getDistance(0, 1) / 2.0D);
/* 203 */               node[1].setBranchLength(dm.getDistance(0, 1) / 2.0D);
/*     */             }
/* 205 */             Node root = new SimpleNode();
/* 206 */             root.addChild(node[0]);root.addChild(node[1]);
/* 207 */             node[0].setParent(root);node[1].setParent(root);
/* 208 */             tree = new SimpleTree(root);
/*     */           }
/*     */           else {
/* 211 */             System.err.println("unreliable on two genes without target node specified - not computing");
/* 212 */             return;
/*     */           }
/* 214 */           PrintWriter pw = new PrintWriter(new FileWriter(treeF));
/* 215 */           TreeUtils.printNH(tree, pw);
/* 216 */           pw.close();
/*     */         }
/* 218 */         else if ((!treeF.exists()) || (treeF.length() == 0L))
/*     */         {
/* 220 */           System.err.println("running PHYML ");
/* 221 */           String seq = params.hasOption("seq") ? params.getOptionValue("seq") : null;
/* 222 */           if (seq != null) prot_align = AlignUtils.keepClosest(prot_align, seq, max_nodes[0]);
/* 223 */           AlignTools.phyml(treeF, prot_align);
/* 224 */           System.err.println("done");
/*     */         }
/*     */         
/* 227 */         pal.tree.Tree tree = new ReadTree(treeF.getAbsolutePath());
/* 228 */         if ((params.hasOption("seq")) && 
/* 229 */           (tree.getExternalNodeCount() > max_nodes[1])) {
/* 230 */           String seq = params.getOptionValue("seq");
/* 231 */           Node node = null;
/* 232 */           for (int i = 0; i < tree.getExternalNodeCount(); i++)
/*     */           {
/* 234 */             if (tree.getExternalNode(i).getIdentifier().getName().startsWith(seq)) {
/* 235 */               node = tree.getExternalNode(i);
/* 236 */               break;
/*     */             }
/*     */           }
/*     */           
/* 240 */           if (node != null) {
/* 241 */             Node[] nodes = NodeUtils.findByIdentifier(tree.getRoot(), PAML1.getClosestExcludingNode(tree, node, max_nodes[1]));
/* 242 */             Node[] allnodes = new Node[nodes.length + 1];
/* 243 */             System.arraycopy(nodes, 0, allnodes, 0, nodes.length);
/* 244 */             allnodes[nodes.length] = node;
/* 245 */             tree = new ReadTree(new PushbackReader(new StringReader(
/* 246 */               new SimpleTree(SDI.trim(tree.getRoot(), allnodes)).toString())));
/*     */           }
/*     */         }
/* 249 */         if (tree.getExternalNodeCount() < dna_align.getIdCount()) {
/* 250 */           dna_align = AlignUtils.restrictAlignment(dna_align, tree);
/*     */         }
/* 252 */         forester.tree.Tree foresterTree = PseudoManager.graph ? MaxLikelihoodTree.convert(tree) : null;
/* 253 */         PseudoManager psm = new PseudoManager(repository, dir, Arrays.asList(sym), dna_align, 
/* 254 */           tree, foresterTree);
/* 255 */         psm.run();
/*     */       }
/*     */     } catch (Exception exc) {
/* 258 */       exc.printStackTrace();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public static List split(Node root, int max_no_leaves)
/*     */   {
/* 265 */     if (NodeUtils.getLeafCount(root) <= max_no_leaves) { return Arrays.asList(new Node[] { root });
/*     */     }
/* 267 */     List l = new ArrayList();
/* 268 */     Node[] tree = new Node[root.getChildCount()];
/* 269 */     for (int i = 0; i < root.getChildCount(); i++) {
/* 270 */       l.addAll(split(root.getChild(i), max_no_leaves));
/*     */     }
/* 272 */     return l;
/*     */   }
/*     */   
/*     */   public static pal.tree.Tree getNHTree(Alignment prot_align) {
/* 276 */     RateMatrix substMProt = new WAG(AlignmentUtils.estimateFrequencies(prot_align));
/* 277 */     DistanceMatrix dm = 
/* 278 */       new AlignmentDistanceMatrix(SitePattern.getSitePattern(prot_align), SubstitutionModel.Utils.createSubstitutionModel(substMProt));
/*     */     
/* 280 */     return new NeighborJoiningTree(dm);
/*     */   }
/*     */   
/*     */   static int countSeqs(File fastaFile) throws Exception
/*     */   {
/* 285 */     int count = 0;
/* 286 */     BufferedReader br = new BufferedReader(new FileReader(fastaFile));
/* 287 */     String st = "";
/* 288 */     while ((st = br.readLine()) != null) {
/* 289 */       if (st.startsWith(">")) {
/* 290 */         count++;
/*     */       }
/*     */     }
/* 293 */     br.close();
/* 294 */     return count;
/*     */   }
/*     */   
/*     */   static Object[] getSymbol(File dir, PfamAlphabet alph) throws Exception {
/* 298 */     Set l = new HashSet();
/* 299 */     BufferedReader br = new BufferedReader(new FileReader(new File(dir, "pfamA")));
/* 300 */     String st = "";
/* 301 */     while ((st = br.readLine()) != null) {
/* 302 */       l.add(alph.getTokenization("token").parseToken(st.split("\\s+")[0]));
/*     */     }
/* 304 */     return l.toArray();
/*     */   }
/*     */   
/*     */ 
/*     */   static File getAlignments(File fasta, boolean dna)
/*     */     throws Exception
/*     */   {
/* 311 */     String id = fasta.getName();
/* 312 */     File parent = fasta.getParentFile();
/* 313 */     if (id.indexOf('.') >= 0) {
/* 314 */       id = id.substring(0, id.lastIndexOf('.'));
/*     */     }
/* 316 */     File alignmentOut = new File(fasta.getParent(), id + ".mfa");
/* 317 */     if ((alignmentOut.exists()) && (alignmentOut.length() > 0L)) return alignmentOut;
/* 318 */     if (dna) {
/* 319 */       int alignMethod = Integer.parseInt(params.getOptionValue("alignMethod", "0"));
/* 320 */       if (alignMethod == 1) {
/* 321 */         System.err.println("making alignment -method 1 ...");
/* 322 */         AlignTools.makeDNAAlignment(fasta, alignmentOut, params.getOptionValue("seq"));
/* 323 */         System.err.println(" ...done");
/*     */       }
/*     */       else {
/* 326 */         String id1 = new String(id);
/* 327 */         if (id1.indexOf('.') >= 0) {
/* 328 */           id1 = id1.substring(0, id1.lastIndexOf('.'));
/*     */         }
/* 330 */         File protAlign = new File(parent, id1 + ".pep.mfa");
/* 331 */         if ((!protAlign.exists()) || (protAlign.length() == 0L)) {
/* 332 */           File fastaProt = new File(parent, id1 + ".pep.fa");
/* 333 */           AlignTools.writeTranslatedFasta(fasta, fastaProt);
/* 334 */           System.err.println("making protein alignment ...");
/* 335 */           AlignTools.muscle(fastaProt, protAlign);
/* 336 */           System.err.println("...done");
/*     */         }
/* 338 */         System.err.println("inferring dna alignment ...");
/* 339 */         Alignment align = AlignTools.inferDNAAlignmentFromProteinAlignment(fasta, protAlign);
/* 340 */         PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(alignmentOut)));
/* 341 */         AlignTools.printMFA(align, pw);
/* 342 */         pw.close();
/* 343 */         System.err.println("...done");
/*     */       }
/*     */     }
/*     */     else {
/* 347 */       System.err.println("making protein alignment ...");
/* 348 */       AlignTools.muscle(fasta, alignmentOut);
/*     */     }
/* 350 */     return alignmentOut;
/*     */   }
/*     */ }


/* Location:              /home/lachlan/Desktop/psilc1.21/psilc.jar!/lc1/pseudo/Pseudo.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */