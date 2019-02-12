/*      */ package lc1.phyl;
/*      */ 
/*      */ import jalview.AlignFrame;
/*      */ import java.io.BufferedWriter;
/*      */ import java.io.File;
/*      */ import java.io.FileWriter;
/*      */ import java.io.IOException;
/*      */ import java.io.PrintStream;
/*      */ import java.io.PrintWriter;
/*      */ import java.lang.reflect.Constructor;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Arrays;
/*      */ import java.util.HashMap;
/*      */ import java.util.HashSet;
/*      */ import java.util.Iterator;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Set;
/*      */ import java.util.SortedSet;
/*      */ import java.util.TreeSet;
/*      */ import lc1.domainseq.Domain;
/*      */ import lc1.util.PAML1;
/*      */ import lc1.util.Print;
/*      */ import org.biojava.bio.dist.Distribution;
/*      */ import org.biojava.bio.seq.FeatureHolder;
/*      */ import org.biojava.bio.seq.RNATools;
/*      */ import org.biojava.bio.seq.io.SymbolTokenization;
/*      */ import org.biojava.bio.symbol.Alphabet;
/*      */ import org.biojava.bio.symbol.BasisSymbol;
/*      */ import org.biojava.bio.symbol.FiniteAlphabet;
/*      */ import org.biojava.bio.symbol.Location;
/*      */ import org.biojava.bio.symbol.LocationTools;
/*      */ import org.biojava.bio.symbol.Symbol;
/*      */ import org.biojava.utils.SmallSet;
/*      */ import pal.alignment.Alignment;
/*      */ import pal.alignment.AlignmentParseException;
/*      */ import pal.alignment.AlignmentUtils;
/*      */ import pal.alignment.ConcatenatedAlignment;
/*      */ import pal.alignment.ReadAlignment;
/*      */ import pal.alignment.SimpleAlignment;
/*      */ import pal.alignment.SitePattern;
/*      */ import pal.alignment.StrippedAlignment;
/*      */ import pal.datatype.AminoAcids;
/*      */ import pal.datatype.CodonTable;
/*      */ import pal.datatype.CodonTableFactory;
/*      */ import pal.datatype.Codons;
/*      */ import pal.datatype.DataType;
/*      */ import pal.datatype.Nucleotides;
/*      */ import pal.distance.AlignmentDistanceMatrix;
/*      */ import pal.distance.DistanceMatrix;
/*      */ import pal.misc.IdGroup;
/*      */ import pal.misc.Identifier;
/*      */ import pal.misc.SimpleIdGroup;
/*      */ import pal.substmodel.AminoAcidModel;
/*      */ import pal.substmodel.RateMatrix;
/*      */ import pal.substmodel.SubstitutionModel;
/*      */ import pal.substmodel.SubstitutionModel.Utils;
/*      */ import pal.substmodel.UniformRate;
/*      */ import pal.substmodel.WAG;
/*      */ import pal.tree.Node;
/*      */ import pal.tree.NodeUtils;
/*      */ import pal.tree.SimpleTree;
/*      */ import pal.tree.Tree;
/*      */ import pal.tree.TreeDistanceMatrix;
/*      */ import pal.tree.TreeUtils;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class AlignUtils
/*      */ {
/*      */   static String TYPE;
/*      */   
/*      */   public static void main(String[] args)
/*      */     throws Exception
/*      */   {
/*   81 */     Alignment align = new ReadAlignment(args[0]);
/*   82 */     int j = align.whichIdNumber(args[0].split("\\.")[0]);
/*   83 */     StrippedAlignment prot_align1 = new StrippedAlignment(align);
/*   84 */     prot_align1.setDataType(AminoAcids.DEFAULT_INSTANCE);
/*   85 */     int sites = align.getSiteCount();
/*   86 */     for (int i = 0; i < sites; i++) {
/*   87 */       if ("_-?.".indexOf(align.getData(j, i)) >= 0) {
/*   88 */         prot_align1.dropSite(i);
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  101 */     display(prot_align1);
/*      */   }
/*      */   
/*      */   public static boolean isAllGap(Alignment align, int i) {
/*  105 */     for (int j = 0; j < align.getSequenceCount(); j++) {
/*  106 */       char c = align.getData(j, i);
/*  107 */       DataType dt = align.getDataType();
/*  108 */       if ((!dt.isGapChar(c)) && (!dt.isUnknownChar(c))) return false;
/*      */     }
/*  110 */     return true;
/*      */   }
/*      */   
/*      */   public static Object[] getAlias(Alignment align)
/*      */   {
/*  115 */     StrippedAlignment sl1 = new StrippedAlignment(align);
/*  116 */     sl1.setDataType(align.getDataType());
/*  117 */     int[] alias1 = new int[align.getSiteCount()];
/*  118 */     int ik = 0;
/*  119 */     for (int i = 0; i < align.getSiteCount(); i++) {
/*  120 */       if (isAllGap(align, i)) {
/*  121 */         sl1.dropSite(i);
/*      */       }
/*      */       else {
/*  124 */         alias1[ik] = i;
/*  125 */         ik++;
/*      */       }
/*      */     }
/*  128 */     int[] alias = new int[ik];
/*      */     
/*  130 */     System.arraycopy(alias1, 0, alias, 0, ik);
/*  131 */     return new Object[] { sl1, alias };
/*      */   }
/*      */   
/*      */   public static void display(Alignment align) throws Exception
/*      */   {
/*  136 */     File tmp = new File("tempor");
/*  137 */     tmp.deleteOnExit();
/*  138 */     PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(tmp)));
/*  139 */     AlignmentUtils.printCLUSTALW(align, pw);
/*  140 */     pw.close();
/*  141 */     AlignFrame.main(new String[] { tmp.getAbsolutePath(), "File", "CLUSTAL" });
/*      */   }
/*      */   
/*      */ 
/*  145 */   static boolean GWF = false;
/*  146 */   public static final CodonTable table = CodonTableFactory.createUniversalTranslator();
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static SubstitutionModel getInsertModel(double[] freq, DataType dt, boolean varRates, double range)
/*      */   {
/*      */     try
/*      */     {
/*  160 */       Class clazz = Class.forName(TYPE);
/*  161 */       Constructor constr = clazz.getConstructor(new Class[] { freq.getClass() });
/*      */       
/*  163 */       RateMatrix aModel = (AminoAcidModel)constr.newInstance(new Object[] { freq });
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  169 */       if (GWF) {
/*      */         try {
/*  171 */           aModel = new WAG_GWF(new double[] { 0.0D }, freq);
/*      */ 
/*      */         }
/*      */         catch (ArithmeticException e)
/*      */         {
/*      */ 
/*  177 */           aModel = (AminoAcidModel)constr.newInstance(new Object[] { freq });
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  185 */       RateMatrix insertModel = aModel;
/*      */       
/*  187 */       if (varRates) {
/*  188 */         return SubstitutionModel.Utils.createSubstitutionModel(insertModel, new VariableUniformRate(1.0D, range));
/*      */       }
/*  190 */       return SubstitutionModel.Utils.createSubstitutionModel(insertModel, new UniformRate());
/*      */     }
/*      */     catch (Throwable t)
/*      */     {
/*  194 */       t.printStackTrace();
/*      */     }
/*  196 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */   public static SubstitutionModel getInsertModel(Distribution dist, DataType dt, boolean varRates, double range)
/*      */   {
/*  202 */     return getInsertModel(getFrequencies(dist), dt, varRates, range);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static double[] getFrequencies(Distribution dist)
/*      */   {
/*      */     try
/*      */     {
/*  219 */       FiniteAlphabet alph = (FiniteAlphabet)dist.getAlphabet();
/*  220 */       SymbolTokenization tokenizer = ((Alphabet)alph.getAlphabets().get(0)).getTokenization("token");
/*  221 */       int size = alph.size();
/*      */       DataType dt;
/*  223 */       DataType dt; if (alph.getName().toUpperCase().startsWith("PROTEIN")) {
/*  224 */         dt = AminoAcids.DEFAULT_INSTANCE;
/*      */       } else { DataType dt;
/*  226 */         if (alph.getName().toUpperCase().startsWith("DNA")) {
/*  227 */           dt = Nucleotides.DEFAULT_INSTANCE;
/*      */         }
/*      */         else
/*  230 */           dt = Codons.DEFAULT_INSTANCE; }
/*  231 */       double[] res = new double[dt.getNumStates()];
/*  232 */       FiniteAlphabet codonAlph = RNATools.getCodonAlphabet();
/*  233 */       Symbol[] syms = new Symbol[alph.size()];
/*  234 */       Iterator it = alph.iterator();
/*  235 */       Arrays.fill(res, 0.0D);
/*  236 */       while (it.hasNext())
/*      */       {
/*  238 */         Symbol sym = (Symbol)it.next();
/*      */         int j;
/*  240 */         int j; if (dt.equals(Codons.DEFAULT_INSTANCE)) {
/*  241 */           List l = ((BasisSymbol)sym).getSymbols();
/*  242 */           int[] dna = new int[l.size()];
/*  243 */           for (int i = 0; i < dna.length; i++) {
/*  244 */             dna[i] = Nucleotides.DEFAULT_INSTANCE.getState(tokenizer.tokenizeSymbol((Symbol)l.get(i)).charAt(0));
/*      */           }
/*  246 */           j = Codons.getCodonIndexFromNucleotideStates(dna);
/*      */ 
/*      */         }
/*      */         else
/*      */         {
/*  251 */           j = dt.getState(tokenizer.tokenizeSymbol(sym).charAt(0));
/*      */         }
/*      */         
/*      */ 
/*  255 */         if ((j < dt.getNumStates()) && (j >= 0))
/*  256 */           res[j] = dist.getWeight(sym);
/*      */       }
/*  258 */       checkSumNormal(res);
/*  259 */       return res;
/*      */     }
/*      */     catch (Exception ise)
/*      */     {
/*  263 */       ise.printStackTrace(System.err);
/*      */     }
/*  265 */     return null;
/*      */   }
/*      */   
/*      */   public static Alignment keepClosest(Alignment align, String seq, int max) throws Exception {
/*  269 */     if (align.getIdCount() <= max) return align;
/*  270 */     SitePattern sp = SitePattern.getSitePattern(align);
/*      */     
/*      */ 
/*      */ 
/*  274 */     DistanceMatrix dm = new AlignmentDistanceMatrix(sp, 
/*  275 */       SubstitutionModel.Utils.createSubstitutionModel(new WAG(AlignmentUtils.estimateFrequencies(sp))));
/*  276 */     Tree tree = new NeighborJoiningTree(dm);
/*  277 */     Node node = null;
/*  278 */     for (int i = 0; i < tree.getExternalNodeCount(); i++)
/*      */     {
/*  280 */       if (tree.getExternalNode(i).getIdentifier().getName().startsWith(seq)) {
/*  281 */         node = tree.getExternalNode(i);
/*  282 */         break;
/*      */       }
/*      */     }
/*      */     
/*  286 */     if (node != null) {
/*  287 */       IdGroup idG = new SimpleIdGroup(PAML1.getClosestExcludingNode(tree, node, max));
/*  288 */       return restrictAlignment(align, 
/*  289 */         new SimpleIdGroup(new SimpleIdGroup(new Identifier[] { node.getIdentifier() }), idG));
/*      */     }
/*  291 */     return align;
/*      */   }
/*      */   
/*      */   public static Alignment keepFurthest(Alignment align, String seq, int max) throws Exception
/*      */   {
/*  296 */     if (align.getIdCount() <= max) return align;
/*  297 */     SitePattern sp = SitePattern.getSitePattern(align);
/*      */     
/*      */ 
/*      */ 
/*  301 */     DistanceMatrix dm = new AlignmentDistanceMatrix(sp, 
/*  302 */       SubstitutionModel.Utils.createSubstitutionModel(new WAG(AlignmentUtils.estimateFrequencies(sp))));
/*  303 */     Tree tree = new NeighborJoiningTree(dm);
/*  304 */     Node node = null;
/*  305 */     for (int i = 0; i < tree.getExternalNodeCount(); i++)
/*      */     {
/*  307 */       if (tree.getExternalNode(i).getIdentifier().getName().startsWith(seq)) {
/*  308 */         node = tree.getExternalNode(i);
/*  309 */         break;
/*      */       }
/*      */     }
/*      */     
/*  313 */     if (node != null) {
/*  314 */       IdGroup idG = new SimpleIdGroup(PAML1.getClosestExcludingNode(tree, node, max));
/*  315 */       return restrictAlignment(align, 
/*  316 */         new SimpleIdGroup(new SimpleIdGroup(new Identifier[] { node.getIdentifier() }), idG));
/*      */     }
/*  318 */     return align;
/*      */   }
/*      */   
/*      */ 
/*      */   public static void displayModel(EvolutionaryModel em)
/*      */   {
/*      */     try
/*      */     {
/*  326 */       Tree tree = new SimpleTree(em.getTree());
/*  327 */       for (int i = 0; i < tree.getExternalNodeCount(); i++) {
/*  328 */         Identifier id = tree.getExternalNode(i).getIdentifier();
/*  329 */         int j = em.getAlignment().whichIdNumber(id.getName());
/*  330 */         tree.setAttribute(tree.getExternalNode(i), "S", em.getAlignment().getData(j, 124));
/*      */       }
/*      */       
/*      */ 
/*  334 */       File f = new File("temp" + Math.random());
/*  335 */       f.deleteOnExit();
/*  336 */       PrintWriter pw = new PrintWriter(new FileWriter(f));
/*  337 */       Alignment align = em.getAlignment();
/*  338 */       if ((align.getDataType() instanceof Codons))
/*      */       {
/*  340 */         AlignmentUtils.printCLUSTALW(translate(em.getAlignment()), pw);
/*      */       }
/*      */       else
/*  343 */         AlignmentUtils.printCLUSTALW(em.getAlignment(), pw);
/*  344 */       pw.close();
/*      */     }
/*      */     catch (IOException exc)
/*      */     {
/*  348 */       exc.printStackTrace();
/*      */     }
/*      */   }
/*      */   
/*      */   public static Alignment getCodonAlignmentFromDNA(Alignment align) throws AlignmentParseException
/*      */   {
/*  354 */     String[] seqs = new String[align.getSequenceCount()];
/*  355 */     Codons dt = Codons.DEFAULT_INSTANCE;
/*  356 */     CodonTable ct = CodonTableFactory.createUniversalTranslator();
/*      */     
/*  358 */     for (int i = 0; i < align.getIdCount(); i++) {
/*  359 */       seqs[i] = "";
/*  360 */       for (int j = 0; 
/*  361 */           j < align.getSiteCount(); 
/*  362 */           j += 3) {
/*  363 */         char[] c = align.getAlignedSequenceString(i).substring(j, j + 3).toCharArray();
/*  364 */         int ind = Codons.getCodonIndexFromNucleotides(c);
/*  365 */         if ((ind < 64) && (ind >= 0) && (table.getAminoAcidCharFromCodonIndex(ind) == '*')) {
/*  366 */           throw new RuntimeException("stop codon " + Print.toString(c) + " " + 
/*  367 */             align.getIdentifier(i) + " " + align.getAlignedSequenceString(i));
/*      */         }
/*  369 */         if (ind >= 0) {
/*  370 */           int tmp164_162 = i; String[] tmp164_161 = seqs;tmp164_161[tmp164_162] = (tmp164_161[tmp164_162] + dt.getChar(ind));
/*  371 */           int amino = ct.getAminoAcidStateFromCodonIndex(ind);
/*  372 */           if (amino < 0) throw new NullPointerException("is stop codon " + c[0] + c[1] + c[2]);
/*      */         }
/*      */         else {
/*  375 */           int tmp253_251 = i; String[] tmp253_250 = seqs;tmp253_250[tmp253_251] = (tmp253_250[tmp253_251] + '?');
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*  380 */     SimpleAlignment alignOut = new SimpleAlignment(align, seqs, null, dt);
/*  381 */     return alignOut;
/*      */   }
/*      */   
/*      */   public static Alignment translate(Alignment align) {
/*  385 */     DataType dt = align.getDataType();
/*      */     try {
/*  387 */       if (!(dt instanceof Codons)) throw new AlignmentParseException("align must have codons: " + dt);
/*      */     } catch (AlignmentParseException exc) {
/*  389 */       exc.printStackTrace();
/*  390 */       return null;
/*      */     }
/*  392 */     CodonTable ct = CodonTableFactory.createUniversalTranslator();
/*  393 */     String[] seqs = new String[align.getSequenceCount()];
/*  394 */     for (int i = 0; i < align.getIdCount(); i++) {
/*  395 */       seqs[i] = "";
/*  396 */       for (int j = 0; j < align.getSiteCount(); j++) {
/*  397 */         char c = align.getData(i, j);
/*  398 */         if ((dt.getState(c) < 0) || (dt.getState(c) >= 64)) {
/*  399 */           int tmp121_119 = i; String[] tmp121_118 = seqs;tmp121_118[tmp121_119] = (tmp121_118[tmp121_119] + c);
/*      */         }
/*      */         else {
/*  402 */           int tmp149_147 = i; String[] tmp149_146 = seqs;tmp149_146[tmp149_147] = (tmp149_146[tmp149_147] + ct.getAminoAcidCharFromCodonIndex(dt.getState(c)));
/*      */         }
/*      */       }
/*  405 */       seqs[i] = seqs[i].replace('*', '?');
/*      */     }
/*      */     
/*  408 */     return 
/*  409 */       new SimpleAlignment(align, seqs, null, AminoAcids.DEFAULT_INSTANCE);
/*      */   }
/*      */   
/*      */   public static void checkSumNormal(double[] codons)
/*      */     throws ArithmeticException
/*      */   {
/*  415 */     double sum = 0.0D;
/*  416 */     for (int j = 0; j < codons.length; j++)
/*      */     {
/*  418 */       sum += codons[j];
/*  419 */       if (Double.isNaN(codons[j])) { throw new ArithmeticException("nan :" + codons[j]);
/*      */       }
/*      */     }
/*  422 */     if (StrictMath.abs(1.0D - sum) > 0.01D) { throw new ArithmeticException("something gone wrong " + sum);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public static void removeNodes(Tree tree1, Node[] nodes)
/*      */   {
/*  430 */     for (int i = 0; i < nodes.length; i++) {
/*  431 */       Node parent = nodes[i].getParent();
/*      */       
/*  433 */       int j = 0;
/*  434 */       while (!parent.getChild(j).equals(nodes[i])) {
/*  435 */         j++;
/*      */       }
/*  437 */       parent.removeChild(j);
/*  438 */       tree1.createNodeList();
/*  439 */       if (parent.getChildCount() == 1) {
/*  440 */         Node child = parent.getChild(0);
/*      */         
/*  442 */         if (!parent.isRoot()) {
/*  443 */           Node grand = parent.getParent();
/*  444 */           double length = child.getBranchLength() + parent.getBranchLength();
/*  445 */           int j1 = 0;
/*  446 */           while (!parent.equals(grand.getChild(j1))) {
/*  447 */             j1++;
/*      */           }
/*  449 */           grand.removeChild(j1);
/*  450 */           child.setParent(grand);
/*  451 */           grand.addChild(child);
/*  452 */           child.setBranchLength(length);
/*      */         }
/*      */         else {
/*  455 */           parent.removeChild(0);
/*  456 */           for (int il = 0; il < child.getChildCount(); il++) {
/*  457 */             Node grandchild = child.getChild(il);
/*  458 */             double length = grandchild.getBranchLength() + child.getBranchLength();
/*  459 */             parent.addChild(grandchild);
/*  460 */             grandchild.setParent(parent);
/*  461 */             grandchild.setBranchLength(length);
/*      */           }
/*      */         }
/*  464 */         tree1.createNodeList();
/*      */       }
/*  466 */       while (parent.getChildCount() == 0) {
/*  467 */         int j1 = 0;
/*  468 */         Node grandparent = parent.getParent();
/*  469 */         while (!parent.equals(grandparent.getChild(j1))) {
/*  470 */           j1++;
/*      */         }
/*  472 */         grandparent.removeChild(j1);
/*  473 */         parent = grandparent;
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   public static SitePattern getDNAAlignmentFromCodons(Alignment align) {
/*  479 */     Codons dt = (Codons)align.getDataType();
/*  480 */     String[] seqs = new String[align.getSequenceCount()];
/*  481 */     for (int i = 0; i < align.getIdCount(); i++) {
/*  482 */       int state = dt.getState(align.getData(i, 0));
/*  483 */       if ((state >= 0) && (state < 64) && (table.getAminoAcidCharFromCodonIndex(dt.getState(align.getData(i, 0))) == '*'))
/*  484 */         throw new RuntimeException("stop codon " + Print.toString(Codons.getNucleotidesFromCodonIndex(state)));
/*  485 */       if ((state >= 0) && (state < 64)) {
/*  486 */         int[] c1 = Codons.getNucleotideStatesFromCodonIndex(dt.getState(align.getData(i, 0)));
/*  487 */         char[] c = new char[c1.length];
/*  488 */         for (int ik = 0; ik < c.length; ik++) {
/*  489 */           c[ik] = Nucleotides.DEFAULT_INSTANCE.getChar(c1[ik]);
/*      */         }
/*  491 */         seqs[i] = new String(c);
/*      */       }
/*      */       else {
/*  494 */         seqs[i] = "???";
/*      */       }
/*  496 */       for (int j = 1; j < align.getSiteCount(); j++) {
/*  497 */         state = dt.getState(align.getData(i, j));
/*  498 */         if ((state < 64) && (state >= 0) && (table.getAminoAcidCharFromCodonIndex(dt.getState(align.getData(i, j))) == '*')) {
/*  499 */           seqs[i] = "???";
/*      */         }
/*  501 */         else if ((state >= 0) && (state < 64)) {
/*  502 */           int[] c1 = Codons.getNucleotideStatesFromCodonIndex(dt.getState(align.getData(i, j)));
/*  503 */           char[] c = new char[c1.length];
/*  504 */           for (int ik = 0; ik < c.length; ik++) {
/*  505 */             c[ik] = Nucleotides.DEFAULT_INSTANCE.getChar(c1[ik]);
/*      */           }
/*  507 */           int tmp338_337 = i; String[] tmp338_336 = seqs;tmp338_336[tmp338_337] = (tmp338_336[tmp338_337] + new String(c));
/*      */         }
/*      */         else {
/*  510 */           int tmp372_371 = i; String[] tmp372_370 = seqs;tmp372_370[tmp372_371] = (tmp372_370[tmp372_371] + "???");
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*  516 */     SitePattern sp = SitePattern.getSitePattern(new SimpleAlignment(align, seqs, null, Nucleotides.DEFAULT_INSTANCE));
/*  517 */     return sp;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static Tree neighbourJoiningTree(Alignment align, boolean sample)
/*      */   {
/*      */     try
/*      */     {
/*  535 */       long time = System.currentTimeMillis();
/*      */       
/*      */ 
/*  538 */       SitePattern sp = SitePattern.getSitePattern(align);
/*  539 */       SubstitutionModel substM = getInsertModel(AlignmentUtils.estimateFrequencies(align), 
/*  540 */         align.getDataType(), false, 1.0D);
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*  545 */       DistanceMatrix dm = new AlignmentDistanceMatrix(sp);
/*      */       
/*      */ 
/*      */ 
/*  549 */       return new NeighborJoiningTree(dm);
/*      */ 
/*      */ 
/*      */     }
/*      */     catch (Throwable t)
/*      */     {
/*      */ 
/*      */ 
/*  557 */       t.printStackTrace(); }
/*  558 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */   public static Tree neighbourJoiningTree(Alignment align, double[] collapse, String name)
/*      */   {
/*  564 */     DistanceMatrix dm = new AlignmentDistanceMatrix(SitePattern.getSitePattern(align));
/*  565 */     Map coll = new HashMap();
/*  566 */     if (collapse[0] > 0.0D) dm = collapseSimilar(dm, collapse[0], name, coll);
/*  567 */     if (collapse[1] > 3.0D) dm = collapseSimilar(dm, (int)collapse[1], name, coll);
/*  568 */     return new NeighborJoiningTree(dm);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public static Tree neighbourJoiningTree(Alignment align, SubstitutionModel substM, double[] collapse, String name)
/*      */   {
/*  575 */     DistanceMatrix dm = new AlignmentDistanceMatrix(SitePattern.getSitePattern(align), substM);
/*  576 */     Map coll = new HashMap();
/*  577 */     if (collapse[0] > 0.0D) dm = collapseSimilar(dm, collapse[0], name, coll);
/*  578 */     if (collapse[1] > 3.0D) dm = collapseSimilar(dm, (int)collapse[1], name, coll);
/*  579 */     return neighbourJoiningTree(dm);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public static Tree neighbourJoiningTree(DistanceMatrix dm)
/*      */   {
/*  586 */     Tree tr = new pal.tree.NeighborJoiningTree(dm);
/*  587 */     tr.createNodeList();
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  608 */     return tr;
/*      */   }
/*      */   
/*      */ 
/*      */   public static DistanceMatrix collapseSimilar(DistanceMatrix dm, double thresh, String name, Map coll)
/*      */   {
/*  614 */     int id = name == null ? -1 : dm.whichIdNumber(name);
/*  615 */     List identifiers = new ArrayList();
/*  616 */     if (id >= 0) {
/*  617 */       identifiers.add(dm.getIdentifier(id));
/*  618 */       Set s_i = (Set)coll.get(dm.getIdentifier(id));
/*  619 */       if (s_i == null) {
/*  620 */         s_i = new SmallSet();
/*  621 */         s_i.add(dm.getIdentifier(id).getName());
/*  622 */         coll.put(dm.getIdentifier(id).getName(), s_i);
/*      */       }
/*      */     }
/*      */     
/*  626 */     for (int i = 0; i < dm.getSize(); i++)
/*      */     {
/*  628 */       if (i != id)
/*      */       {
/*      */ 
/*      */ 
/*  632 */         Set s_i = (Set)coll.get(dm.getIdentifier(i));
/*  633 */         if (s_i == null) {
/*  634 */           s_i = new SmallSet();
/*  635 */           s_i.add(dm.getIdentifier(i).getName());
/*  636 */           coll.put(dm.getIdentifier(i).getName(), s_i);
/*      */         }
/*      */         
/*  639 */         for (int j = 0; j < i; j++) {
/*  640 */           if (dm.getDistance(i, j) < thresh)
/*      */           {
/*  642 */             Set s_j = (Set)coll.get(dm.getIdentifier(j).getName());
/*      */             
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  648 */             s_j.addAll(s_i);
/*  649 */             coll.put(dm.getIdentifier(j), s_j);
/*  650 */             break;
/*      */           }
/*      */         }
/*  653 */         if ((id > 0) && (dm.getDistance(i, id) < thresh))
/*      */         {
/*  655 */           Set s_j = (Set)coll.get(dm.getIdentifier(id).getName());
/*      */           
/*  657 */           s_j.addAll(s_i);
/*  658 */           coll.put(dm.getIdentifier(id), s_j);
/*      */ 
/*      */         }
/*      */         else
/*      */         {
/*  663 */           identifiers.add(dm.getIdentifier(i));
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*  669 */     Identifier[] ids = new Identifier[identifiers.size()];
/*      */     
/*  671 */     identifiers.toArray(ids);
/*      */     
/*  673 */     IdGroup idg = new SimpleIdGroup(ids);
/*  674 */     DistanceMatrix dm1 = new DistanceMatrix(dm, idg);
/*      */     
/*      */ 
/*  677 */     return dm1;
/*      */   }
/*      */   
/*      */   public static DistanceMatrix collapseSimilar(DistanceMatrix dm, int max_no, String name, Map coll) {
/*  681 */     double thresh = 0.01D;
/*  682 */     DistanceMatrix dm1 = new DistanceMatrix(dm);
/*  683 */     while ((dm1 == null) || (dm1.getSize() > max_no)) {
/*  684 */       System.err.println("thresh " + thresh);
/*  685 */       dm1 = collapseSimilar(dm, thresh, name, coll);
/*  686 */       thresh += 0.01D;
/*      */     }
/*  688 */     return dm1;
/*      */   }
/*      */   
/*      */   public static Tree collapseSimilar(Tree tree1, double thresh, String primary) {
/*  692 */     Tree tree = new SimpleTree(tree1);
/*  693 */     Node primary_node = getExternalNodeByName(tree, primary);
/*  694 */     int leaf_count = TreeUtils.getLeafIdGroup(tree).getIdCount();
/*  695 */     double removed = 0.0D;
/*      */     
/*  697 */     Set leafParents = new HashSet();
/*  698 */     for (int i = 0; i < tree.getExternalNodeCount(); i++) {
/*  699 */       leafParents.add(tree.getExternalNode(i).getParent());
/*      */     }
/*  701 */     for (Iterator it = leafParents.iterator(); it.hasNext();) {
/*  702 */       Node n = (Node)it.next();
/*      */       
/*  704 */       Node c1 = n.getChild(0);
/*  705 */       Node c2 = n.getChild(1);
/*  706 */       if ((primary_node == null) || (
/*  707 */         ((!c1.isLeaf()) || (!c1.getIdentifier().getName().equals(primary))) && 
/*  708 */         (!NodeUtils.isAncestor(c1, primary_node)) && 
/*  709 */         ((!c2.isLeaf()) || (!c2.getIdentifier().getName().equals(primary))) && 
/*  710 */         (!NodeUtils.isAncestor(c2, primary_node))))
/*      */       {
/*  712 */         double dist = c1.getBranchLength() + c2.getBranchLength();
/*  713 */         double totalDist = c2.getBranchLength() + n.getBranchLength();
/*  714 */         if (dist < thresh)
/*      */         {
/*  716 */           double count1 = NodeUtils.getLeafCount(c1);
/*  717 */           double count2 = NodeUtils.getLeafCount(c2);
/*  718 */           if (count1 < count2) {
/*  719 */             n.removeChild(0);
/*  720 */             removed += count1;
/*      */           }
/*      */           else {
/*  723 */             n.removeChild(1);
/*  724 */             removed += count2;
/*      */           }
/*  726 */           if ((n.getChildCount() == 1) && (!n.isRoot())) {
/*  727 */             NodeUtils.removeBranch(n);
/*  728 */             c2.setBranchLength(totalDist);
/*      */           }
/*  730 */           if (leaf_count - removed <= 3.0D) break;
/*      */         }
/*      */       }
/*      */     }
/*  734 */     tree.createNodeList();
/*  735 */     return tree;
/*      */   }
/*      */   
/*      */   public static Tree collapseSimilar(Tree tree, int max_left, String primary) {
/*  739 */     tree = collapseSimilar(tree, 0.01D, primary);
/*  740 */     int count = TreeUtils.getLeafIdGroup(tree).getIdCount();
/*  741 */     double threshL = 0.01D;
/*  742 */     while (count > max_left)
/*      */     {
/*  744 */       double thresh = StrictMath.max(getThresh(tree, count - max_left), threshL + 0.01D);
/*  745 */       tree = collapseSimilar(tree, thresh, primary);
/*  746 */       count = TreeUtils.getLeafIdGroup(tree).getIdCount();
/*      */       
/*  748 */       threshL = thresh;
/*      */     }
/*  750 */     return tree;
/*      */   }
/*      */   
/*      */   static Node getExternalNodeByName(Tree tree, String primary) {
/*  754 */     IdGroup idg = TreeUtils.getLeafIdGroup(tree);
/*  755 */     int i = idg.whichIdNumber(primary);
/*  756 */     if (i > 0) {
/*  757 */       Node n = tree.getExternalNode(i);
/*      */       
/*  759 */       return n;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*  764 */     return null;
/*      */   }
/*      */   
/*      */   static double getThresh(Tree tree, int count)
/*      */   {
/*  769 */     double num = 10000.0D;
/*  770 */     SortedSet s = new TreeSet();
/*  771 */     for (int i = 0; i < count; i++) {
/*  772 */       s.add(new Double(num + i));
/*      */     }
/*  774 */     DistanceMatrix dm = new TreeDistanceMatrix(tree);
/*  775 */     for (int i = 0; i < dm.getSize(); i++) {
/*  776 */       for (int j = 0; j < dm.getSize(); j++) {
/*  777 */         Double d = new Double(dm.getDistance(i, j));
/*  778 */         if ((s.tailSet(d).size() > 0) && (!s.contains(d))) {
/*  779 */           s.add(d);
/*  780 */           s.remove(s.last());
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*  785 */     return ((Double)s.last()).doubleValue();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static Alignment subAlignment(Alignment align, FeatureHolder seq)
/*      */   {
/*  867 */     Alignment[] aligns = new Alignment[seq.countFeatures()];
/*  868 */     SortedSet s = new TreeSet(Location.naturalOrder);
/*  869 */     for (Iterator it = seq.features(); it.hasNext();) {
/*  870 */       s.add(((Domain)it.next()).getLocation());
/*      */     }
/*  872 */     Location[] loc = (Location[])s.toArray(new Location[0]);
/*      */     
/*      */ 
/*  875 */     for (int i = 0; i < loc.length; i++) {
/*  876 */       Location l = loc[i];
/*  877 */       LocationTools.makeLocation(l.getMin(), l.getMax());
/*  878 */       aligns[i] = subAlignment(align, l);
/*      */     }
/*      */     
/*      */ 
/*  882 */     ConcatenatedAlignment ret = new ConcatenatedAlignment(aligns);
/*  883 */     ret.setDataType(align.getDataType());
/*  884 */     return ret;
/*      */   }
/*      */   
/*      */   public static Alignment subAlignment(Alignment align, Location locO) {
/*  888 */     List aligns = new ArrayList();
/*      */     
/*  890 */     for (Iterator it = locO.blockIterator(); it.hasNext();) {
/*  891 */       Location loc = (Location)it.next();
/*  892 */       String[] st = new String[align.getIdCount()];
/*  893 */       for (int i = 0; i < st.length; i++) {
/*  894 */         String str = align.getAlignedSequenceString(i);
/*  895 */         st[i] = str.substring(Math.max(0, loc.getMin()), Math.min(str.length(), loc.getMax() + 1));
/*      */       }
/*  897 */       aligns.add(new SimpleAlignment(align, st, "_-?.", align.getDataType()));
/*      */     }
/*  899 */     Alignment[] al = new Alignment[aligns.size()];
/*  900 */     aligns.toArray(al);
/*  901 */     ConcatenatedAlignment conc = new ConcatenatedAlignment(al);
/*  902 */     conc.setDataType(align.getDataType());
/*  903 */     return conc;
/*      */   }
/*      */   
/*      */   public static Alignment restrictAlignment(Alignment align, IdGroup leafIDs)
/*      */   {
/*  908 */     List sequ = new ArrayList();
/*  909 */     List ids = new ArrayList();
/*  910 */     for (int i = 0; i < leafIDs.getIdCount(); i++) {
/*  911 */       int j = align.whichIdNumber(leafIDs.getIdentifier(i).getName());
/*  912 */       if (j >= 0) {
/*  913 */         sequ.add(align.getAlignedSequenceString(j));
/*  914 */         ids.add(leafIDs.getIdentifier(i));
/*      */       }
/*      */     }
/*  917 */     Identifier[] idL = new Identifier[ids.size()];
/*  918 */     ids.toArray(idL);
/*  919 */     String[] seqs = new String[sequ.size()];
/*  920 */     sequ.toArray(seqs);
/*  921 */     Alignment align1 = new SimpleAlignment(idL, seqs, null, align.getDataType());
/*  922 */     return align1;
/*      */   }
/*      */   
/*      */   public static SitePattern[] splitSitePattern(Alignment sp)
/*      */   {
/*  927 */     SitePattern[] sps = new SitePattern[sp.getSiteCount()];
/*  928 */     for (int i = 0; i < sps.length; i++) {
/*  929 */       char[][] seqs = new char[sp.getSequenceCount()][1];
/*  930 */       for (int j = 0; j < sp.getSequenceCount(); j++) {
/*  931 */         seqs[j][0] = sp.getData(j, i);
/*      */       }
/*  933 */       sps[i] = SitePattern.getSitePattern(new SimpleAlignment(sp, seqs, "-", sp.getDataType()));
/*  934 */       sps[i].setDataType(sp.getDataType());
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*  939 */     return sps;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public static void transferProbabilities(double[] amino, double[] codons)
/*      */     throws ArithmeticException
/*      */   {
/*  948 */     if ((amino.length != 20) || (codons.length != 64)) { throw new ArithmeticException("wrong lengths " + amino.length + " " + codons.length);
/*      */     }
/*  950 */     checkSumNormal(amino);
/*  951 */     checkSumNormal(codons);
/*  952 */     double[] codons1 = new double[64];
/*  953 */     Arrays.fill(codons1, 0.0D);
/*  954 */     for (int i = 0; i < amino.length; i++) {
/*  955 */       int[] codonStates = getCodonStatesFromAminoState(i);
/*  956 */       double codonProbForAmino = sum(codons, codonStates);
/*      */       
/*  958 */       if (codonProbForAmino == 0.0D) {
/*  959 */         for (int k = 0; k < codonStates.length; k++) {
/*  960 */           codons1[codonStates[k]] = (amino[i] / codonStates.length);
/*      */         }
/*      */       }
/*      */       else {
/*  964 */         double ratio = amino[i] / codonProbForAmino;
/*  965 */         for (int k = 0; k < codonStates.length; k++) {
/*  966 */           codons[codonStates[k]] *= ratio;
/*      */         }
/*      */       }
/*      */     }
/*  970 */     checkSumNormal(codons1);
/*  971 */     System.arraycopy(codons1, 0, codons, 0, codons.length);
/*      */   }
/*      */   
/*      */ 
/*      */   public static double[] getCodonFreqsFromAminoFreqs(double[] freq)
/*      */   {
/*  977 */     double[] d = new double[64];
/*  978 */     Arrays.fill(d, 0.0D);
/*  979 */     for (int i = 0; i < freq.length; i++) {
/*  980 */       int[] codonStates = getCodonStatesFromAminoState(i);
/*  981 */       for (int j = 0; j < codonStates.length; j++) {
/*  982 */         d[codonStates[j]] = (freq[i] / codonStates.length);
/*      */       }
/*      */     }
/*      */     
/*  986 */     return d;
/*      */   }
/*      */   
/*      */   public static int[] getCodonStatesFromAminoState(int i)
/*      */   {
/*  991 */     char[][] cFrom = table.getCodonsFromAminoAcidState(i);
/*  992 */     int[] from = new int[cFrom.length];
/*  993 */     for (int k = 0; k < from.length; k++) {
/*  994 */       from[k] = Codons.getCodonIndexFromNucleotides(cFrom[k]);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1002 */     return from;
/*      */   }
/*      */   
/*      */   public static double sum(double[] codonFreqs, int[] iC) {
/* 1006 */     double sum = 0.0D;
/* 1007 */     for (int i = 0; i < iC.length; i++) {
/* 1008 */       sum += codonFreqs[iC[i]];
/*      */     }
/*      */     
/*      */ 
/* 1012 */     return sum;
/*      */   }
/*      */   
/*      */   public static SitePattern[] getSitePatterns(Alignment align) {
/*      */     try {
/* 1017 */       IdGroup idg = align;
/*      */       
/* 1019 */       SitePattern[] sp = new SitePattern[align.getSiteCount()];
/* 1020 */       for (int i = 0; i < align.getSiteCount(); i++) {
/* 1021 */         String[] seqs_i = new String[idg.getIdCount()];
/* 1022 */         for (int j = 0; j < idg.getIdCount(); j++) {
/* 1023 */           seqs_i[j] = align.getData(j, i);
/*      */         }
/*      */         
/* 1026 */         SimpleAlignment align_i = new SimpleAlignment(idg, seqs_i, "_-?.", 
/* 1027 */           align.getDataType());
/* 1028 */         align_i.setDataType(align.getDataType());
/* 1029 */         sp[i] = SitePattern.getSitePattern(align_i);
/*      */       }
/* 1031 */       return sp;
/*      */     }
/*      */     catch (Throwable t)
/*      */     {
/* 1035 */       t.printStackTrace(); }
/* 1036 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static DistanceMatrix keepClosestOrthologs(DistanceMatrix dm1, int ik)
/*      */   {
/* 1045 */     Set specs = new HashSet();
/* 1046 */     String baseSp = dm1.getIdentifier(ik).getName().split("0")[0];
/* 1047 */     for (int i = 0; i < dm1.getIdCount(); i++) {
/* 1048 */       specs.add(dm1.getIdentifier(i).getName().split("0")[0]);
/*      */     }
/* 1050 */     String[] spl = (String[])specs.toArray(new String[0]);
/*      */     
/* 1052 */     int[] toKeep = new int[spl.length];
/* 1053 */     Arrays.fill(toKeep, -1);
/* 1054 */     for (int i = 0; i < toKeep.length; i++) {
/* 1055 */       for (int j = 0; j < dm1.getIdCount(); j++) {
/* 1056 */         if ((dm1.getIdentifier(j).getName().startsWith(spl[i])) && (
/* 1057 */           (toKeep[i] < 0) || (dm1.getDistance(ik, j) < dm1.getDistance(ik, toKeep[i])))) {
/* 1058 */           toKeep[i] = j;
/*      */         }
/*      */       }
/*      */     }
/*      */     
/* 1063 */     Identifier[] ids = new Identifier[spl.length];
/* 1064 */     double[][] d = new double[specs.size()][specs.size()];
/* 1065 */     for (int i = 0; i < d.length; i++) {
/* 1066 */       ids[i] = dm1.getIdentifier(toKeep[i]);
/* 1067 */       for (int j = 0; j < i; j++) {
/* 1068 */         d[i][j] = (d[j][i] = dm1.getDistance(toKeep[i], toKeep[j]));
/*      */       }
/*      */     }
/* 1071 */     DistanceMatrix dm = new DistanceMatrix(d, new SimpleIdGroup(ids));
/*      */     
/*      */ 
/* 1074 */     return dm;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static Alignment trimAlignment(Alignment codon_align, int i)
/*      */   {
/* 1083 */     StrippedAlignment align = new StrippedAlignment(codon_align);
/* 1084 */     align.setDataType(codon_align.getDataType());
/* 1085 */     for (int j = 0; j < codon_align.getSiteCount(); j++) {
/* 1086 */       if ("_-?.".indexOf(codon_align.getData(i, j)) >= 0) {
/* 1087 */         align.dropSite(j);
/*      */       }
/*      */     }
/*      */     
/* 1091 */     return align;
/*      */   }
/*      */ }


/* Location:              /home/lachlan/Desktop/psilc1.21/psilc.jar!/lc1/phyl/AlignUtils.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */