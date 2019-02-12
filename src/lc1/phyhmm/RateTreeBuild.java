/*     */ package lc1.phyhmm;
/*     */ 
/*     */ import java.io.BufferedReader;
/*     */ import java.io.BufferedWriter;
/*     */ import java.io.File;
/*     */ import java.io.FileReader;
/*     */ import java.io.FileWriter;
/*     */ import java.io.IOException;
/*     */ import java.io.PrintStream;
/*     */ import java.io.PrintWriter;
/*     */ import java.lang.reflect.Constructor;
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import lc1.phyl.AlignUtils;
/*     */ import lc1.phyl.ParameterizedRateMatrix;
/*     */ import lc1.phyl.ScaledRateMatrix;
/*     */ import lc1.phyl.WAG_GWF;
/*     */ import lc1.treefam.SDI;
/*     */ import org.apache.commons.cli.OptionBuilder;
/*     */ import org.apache.commons.cli.Options;
/*     */ import pal.alignment.Alignment;
/*     */ import pal.alignment.AlignmentUtils;
/*     */ import pal.alignment.SitePattern;
/*     */ import pal.distance.DistanceMatrix;
/*     */ import pal.eval.ModelParameters;
/*     */ import pal.substmodel.AbstractRateMatrix;
/*     */ import pal.substmodel.RateMatrix;
/*     */ import pal.substmodel.SubstitutionModel.Utils;
/*     */ import pal.substmodel.WAG;
/*     */ import pal.tree.Node;
/*     */ import pal.tree.NodeUtils;
/*     */ import pal.tree.SimpleTree;
/*     */ import pal.tree.Tree;
/*     */ import pal.tree.TreeDistanceMatrix;
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
/*     */ public class RateTreeBuild
/*     */ {
/*     */   public static ScaledRateMatrix optimisedRateMatrix(Alignment align, RateMatrix rm, Tree tree)
/*     */   {
/*  59 */     SitePattern sp = SitePattern.getSitePattern(align);
/*  60 */     double[] freq = AlignmentUtils.estimateFrequencies(sp);
/*  61 */     ScaledRateMatrix scaledRM = new ScaledRateMatrix(rm);
/*  62 */     ModelParameters mp = new ModelParameters(sp, 
/*  63 */       SubstitutionModel.Utils.createSubstitutionModel(scaledRM));
/*  64 */     System.err.print("before ");
/*  65 */     for (int i = 0; i < scaledRM.getNumParameters(); i++) {
/*  66 */       System.err.print(scaledRM.getParameter(i) + "\t");
/*     */     }
/*  68 */     System.err.println();
/*  69 */     mp.estimateFromTree(tree);
/*  70 */     System.err.print("after ");
/*  71 */     for (int i = 0; i < scaledRM.getNumParameters(); i++) {
/*  72 */       System.err.print(scaledRM.getParameter(i) + "\t");
/*     */     }
/*  74 */     System.err.println();
/*  75 */     return scaledRM;
/*     */   }
/*     */   
/*  78 */   static final Options OPTIONS = new Options() {};
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
/*     */   static final String collapseDefault = "0.01";
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
/*     */   private static Tree trimTree(Tree tree, String name, double thresh, Map coll)
/*     */   {
/* 110 */     DistanceMatrix dm = new TreeDistanceMatrix(tree);
/* 111 */     DistanceMatrix dm1 = AlignUtils.collapseSimilar(dm, thresh, name, coll);
/* 112 */     if (dm1.getIdCount() < 3) dm1 = dm;
/* 113 */     Node[] nodes = new Node[dm1.getIdCount()];
/* 114 */     for (int i = 0; i < dm1.getIdCount(); i++) {
/* 115 */       nodes[i] = NodeUtils.findByIdentifier(tree.getRoot(), dm1.getIdentifier(i));
/*     */     }
/* 117 */     Tree tree1 = new SimpleTree(SDI.trim(tree.getRoot(), nodes));
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
/* 130 */     return tree1;
/*     */   }
/*     */   
/*     */   public static RateMatrix build(String input, Tree tree1, Alignment prot_align, File rateF, double min_dist, boolean train) {
/*     */     try {
/* 135 */       Map collapsedNodes = new HashMap();
/*     */       
/*     */ 
/*     */ 
/* 139 */       Tree tree = trimTree(tree1, input, min_dist, collapsedNodes);
/* 140 */       prot_align = AlignUtils.restrictAlignment(prot_align, tree);
/*     */       
/* 142 */       RateMatrix substMProt = train ? 
/* 143 */         optimisedRateMatrix(prot_align, 
/* 144 */         new WAG_GWF(new double[] { 0.0D }, AlignmentUtils.estimateFrequencies(prot_align)), tree) : 
/* 145 */         new WAG(AlignmentUtils.estimateFrequencies(prot_align));
/* 146 */       PrintWriter rmOut = new PrintWriter(new BufferedWriter(new FileWriter(rateF)));
/* 147 */       rmOut.print(getRateMatrixString(substMProt));
/* 148 */       rmOut.close();
/* 149 */       return substMProt;
/*     */     } catch (Exception exc) {
/* 151 */       exc.printStackTrace(); }
/* 152 */     return null;
/*     */   }
/*     */   
/*     */   public static RateMatrix read(File rmFile)
/*     */     throws IOException, ClassNotFoundException, IllegalArgumentException, SecurityException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException
/*     */   {
/* 158 */     BufferedReader br = new BufferedReader(new FileReader(rmFile));
/* 159 */     RateMatrix rm = null;
/*     */     
/* 161 */     String s = br.readLine();
/* 162 */     String[] st = s.split("\t");
/* 163 */     Class clazz = Class.forName(st[0]);
/* 164 */     double[] params = new double[st.length - 1];
/* 165 */     for (int i = 1; i < st.length; i++) {
/* 166 */       params[(i - 1)] = Double.parseDouble(st[i]);
/*     */     }
/*     */     
/* 169 */     st = br.readLine().split("\\s+");
/* 170 */     double[] freq = new double[st.length];
/* 171 */     for (int i = 0; i < freq.length; i++) {
/* 172 */       freq[i] = Double.parseDouble(st[i]);
/*     */     }
/* 174 */     rm = (RateMatrix)clazz.getConstructor(new Class[] { new double[0].getClass() }).newInstance(new Object[] { freq });
/* 175 */     for (int i = 1; i < params.length; i++) {
/* 176 */       ((AbstractRateMatrix)rm).setParameter(params[i], i);
/*     */     }
/*     */     
/* 179 */     String s = "";
/* 180 */     String[] st; int i; for (; (s = br.readLine()) != null; 
/*     */         
/*     */ 
/*     */ 
/* 184 */         i < st.length)
/*     */     {
/* 181 */       st = s.split("\t");
/* 182 */       Class clazz = Class.forName(st[0]);
/* 183 */       rm = (RateMatrix)clazz.getConstructor(new Class[] { RateMatrix.class }).newInstance(new Object[] { rm });
/* 184 */       i = 1; continue;
/* 185 */       ((ParameterizedRateMatrix)rm).setParameter(Double.parseDouble(st[i]), i - 1);i++;
/*     */     }
/*     */     
/*     */ 
/* 188 */     br.close();
/* 189 */     return rm;
/*     */   }
/*     */   
/*     */   static StringBuffer getRateMatrixString(RateMatrix rm) {
/* 193 */     if ((rm instanceof ParameterizedRateMatrix)) {
/* 194 */       StringBuffer st = getRateMatrixString(((ParameterizedRateMatrix)rm).getBaseRateMatrix());
/* 195 */       st.append(rm.getClass().getName());
/* 196 */       for (int i = 0; i < rm.getNumParameters(); i++) {
/* 197 */         st.append("\t");
/* 198 */         st.append(rm.getParameter(i));
/*     */       }
/* 200 */       st.append("\n");
/* 201 */       return st;
/*     */     }
/*     */     
/* 204 */     StringBuffer sb = new StringBuffer(rm.getClass().getName());
/* 205 */     for (int i = 0; i < rm.getNumParameters(); i++) {
/* 206 */       sb.append("\t");
/* 207 */       sb.append(rm.getParameter(i));
/*     */     }
/* 209 */     sb.append("\n");
/* 210 */     double[] freq = rm.getEquilibriumFrequencies();
/* 211 */     for (int i = 0; i < freq.length; i++) {
/* 212 */       sb.append(freq[i]);sb.append(" ");
/*     */     }
/* 214 */     sb.append("\n");
/* 215 */     return sb;
/*     */   }
/*     */ }


/* Location:              /home/lachlan/Desktop/psilc1.21/psilc.jar!/lc1/phyhmm/RateTreeBuild.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */