/*      */ package lc1.treefam;
/*      */ 
/*      */ import forester.atv.ATVgraphic;
/*      */ import forester.atv.ATVjframe;
/*      */ import forester.atv.ATVpanel;
/*      */ import forester.tree.PreorderTreeIterator;
/*      */ import forester.tree.TreeHelper;
/*      */ import jalview.AlignFrame;
/*      */ import java.awt.BorderLayout;
/*      */ import java.awt.Color;
/*      */ import java.awt.Container;
/*      */ import java.awt.Dimension;
/*      */ import java.awt.GridBagConstraints;
/*      */ import java.awt.GridBagLayout;
/*      */ import java.awt.LayoutManager;
/*      */ import java.awt.event.ActionEvent;
/*      */ import java.awt.event.ActionListener;
/*      */ import java.awt.event.ContainerEvent;
/*      */ import java.awt.event.MouseEvent;
/*      */ import java.beans.PropertyChangeEvent;
/*      */ import java.beans.PropertyChangeListener;
/*      */ import java.beans.PropertyVetoException;
/*      */ import java.beans.VetoableChangeListener;
/*      */ import java.io.BufferedInputStream;
/*      */ import java.io.BufferedReader;
/*      */ import java.io.BufferedWriter;
/*      */ import java.io.File;
/*      */ import java.io.FileInputStream;
/*      */ import java.io.FileReader;
/*      */ import java.io.FileWriter;
/*      */ import java.io.PrintStream;
/*      */ import java.io.PrintWriter;
/*      */ import java.lang.reflect.Constructor;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Arrays;
/*      */ import java.util.Collection;
/*      */ import java.util.Enumeration;
/*      */ import java.util.HashMap;
/*      */ import java.util.HashSet;
/*      */ import java.util.Iterator;
/*      */ import java.util.List;
/*      */ import java.util.Set;
/*      */ import java.util.Vector;
/*      */ import javax.swing.BoxLayout;
/*      */ import javax.swing.JButton;
/*      */ import javax.swing.JComboBox;
/*      */ import javax.swing.JComponent;
/*      */ import javax.swing.JFormattedTextField;
/*      */ import javax.swing.JFrame;
/*      */ import javax.swing.JLabel;
/*      */ import javax.swing.JOptionPane;
/*      */ import javax.swing.JPanel;
/*      */ import javax.swing.JScrollPane;
/*      */ import javax.swing.JSplitPane;
/*      */ import javax.swing.JTable;
/*      */ import javax.swing.JToolBar;
/*      */ import javax.swing.event.MouseInputAdapter;
/*      */ import javax.swing.table.AbstractTableModel;
/*      */ import javax.swing.table.TableModel;
/*      */ import lc1.dp.AlignmentHMM;
/*      */ import lc1.dp.EmissionState;
/*      */ import lc1.pfam.PfamAlphabet;
/*      */ import lc1.phyl.AlignUtils;
/*      */ import lc1.phyl.AminoStyle;
/*      */ import lc1.phyl.DomainCodonModel;
/*      */ import lc1.phyl.FastLikelihoodCalculator;
/*      */ import lc1.pseudo.NodeProbabilityCalculator;
/*      */ import lc1.pseudo.NodeProbabilityCalculator.SelectionModel;
/*      */ import lc1.pseudo.PseudoManager;
/*      */ import lc1.util.Ensembl;
/*      */ import lc1.util.Ensembl.Phigs;
/*      */ import lc1.util.PAML;
/*      */ import lc1.util.PDBView;
/*      */ import lc1.util.Print;
/*      */ import org.apache.commons.cli.CommandLine;
/*      */ import org.apache.commons.cli.OptionBuilder;
/*      */ import org.apache.commons.cli.Options;
/*      */ import org.apache.commons.cli.Parser;
/*      */ import org.apache.commons.cli.PosixParser;
/*      */ import org.biojava.bio.Annotation;
/*      */ import org.biojava.bio.dist.Distribution;
/*      */ import org.biojava.bio.dist.DistributionFactory;
/*      */ import org.biojava.bio.gui.DistributionLogo;
/*      */ import org.biojava.bio.gui.TextLogoPainter;
/*      */ import org.biojava.bio.seq.DNATools;
/*      */ import org.biojava.bio.seq.ProteinTools;
/*      */ import org.biojava.bio.seq.Sequence;
/*      */ import org.biojava.bio.seq.SequenceIterator;
/*      */ import org.biojava.bio.seq.db.HashSequenceDB;
/*      */ import org.biojava.bio.seq.db.SequenceDB;
/*      */ import org.biojava.bio.seq.io.SeqIOTools;
/*      */ import org.biojava.bio.seq.io.SymbolTokenization;
/*      */ import org.biojava.bio.symbol.FiniteAlphabet;
/*      */ import org.biojava.bio.symbol.Symbol;
/*      */ import org.jfree.chart.ChartFactory;
/*      */ import org.jfree.chart.ChartMouseEvent;
/*      */ import org.jfree.chart.ChartMouseListener;
/*      */ import org.jfree.chart.ChartPanel;
/*      */ import org.jfree.chart.JFreeChart;
/*      */ import org.jfree.chart.Legend;
/*      */ import org.jfree.chart.axis.AxisLocation;
/*      */ import org.jfree.chart.axis.CategoryAxis;
/*      */ import org.jfree.chart.axis.CategoryLabelPositions;
/*      */ import org.jfree.chart.axis.NumberAxis;
/*      */ import org.jfree.chart.axis.ValueAxis;
/*      */ import org.jfree.chart.entity.XYItemEntity;
/*      */ import org.jfree.chart.labels.StandardCategoryToolTipGenerator;
/*      */ import org.jfree.chart.plot.CategoryPlot;
/*      */ import org.jfree.chart.plot.DatasetRenderingOrder;
/*      */ import org.jfree.chart.plot.PlotOrientation;
/*      */ import org.jfree.chart.renderer.LineAndShapeRenderer;
/*      */ import org.jfree.data.DefaultCategoryDataset;
/*      */ import org.jfree.data.XYDataset;
/*      */ import org.jfree.data.XYSeriesCollection;
/*      */ import pal.alignment.Alignment;
/*      */ import pal.alignment.AlignmentUtils;
/*      */ import pal.alignment.SitePattern;
/*      */ import pal.alignment.StrippedAlignment;
/*      */ import pal.datatype.AminoAcids;
/*      */ import pal.datatype.CodonTable;
/*      */ import pal.datatype.CodonTableFactory;
/*      */ import pal.datatype.DataType;
/*      */ import pal.datatype.Nucleotides;
/*      */ import pal.distance.AlignmentDistanceMatrix;
/*      */ import pal.distance.DistanceMatrix;
/*      */ import pal.math.ConjugateDirectionSearch;
/*      */ import pal.math.MultivariateFunction;
/*      */ import pal.math.MultivariateMinimum;
/*      */ import pal.math.OrthogonalHints;
/*      */ import pal.math.OrthogonalSearch;
/*      */ import pal.misc.IdGroup;
/*      */ import pal.misc.Identifier;
/*      */ import pal.misc.Parameterized;
/*      */ import pal.misc.SimpleIdGroup;
/*      */ import pal.substmodel.GammaRates;
/*      */ import pal.substmodel.RateDistribution;
/*      */ import pal.substmodel.RateMatrix;
/*      */ import pal.substmodel.SubstitutionModel.Utils;
/*      */ import pal.substmodel.UniformRate;
/*      */ import pal.substmodel.WAG;
/*      */ import pal.tree.AttributeNode;
/*      */ import pal.tree.NeighborJoiningTree;
/*      */ import pal.tree.ParameterizedTree;
/*      */ import pal.tree.ParameterizedTree.ParameterizedTreeBase;
/*      */ import pal.tree.SimpleNode;
/*      */ import pal.tree.SimpleTree;
/*      */ import pal.tree.UnconstrainedTree;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class TreeCurationTool
/*      */   extends JPanel
/*      */   implements ChartMouseListener
/*      */ {
/*      */   public class TreeOptimizer
/*      */   {
/*      */     final boolean branch;
/*      */     int length;
/*      */     final FastLikelihoodCalculator[][] lhc;
/*      */     Parameterized[] opt;
/*      */     double[][] siteLikelihood;
/*      */     
/*      */     class EMF
/*      */       implements MultivariateFunction
/*      */     {
/*      */       int j;
/*      */       
/*      */       EMF() {}
/*      */       
/*      */       public double evaluate(double[] argument)
/*      */       {
/*  174 */         double[] priors = evaluatePriors(argument);
/*  175 */         double result = -priors[0] - priors[1];
/*      */         
/*      */ 
/*  178 */         return result;
/*      */       }
/*      */       
/*      */       public double[] evaluatePriors(double[] argument) {
/*  182 */         for (int i = 0; i < TreeCurationTool.TreeOptimizer.this.opt[this.j].getNumParameters(); i++) {
/*  183 */           TreeCurationTool.TreeOptimizer.this.opt[this.j].setParameter(argument[i], i);
/*      */         }
/*  185 */         if (!TreeCurationTool.TreeOptimizer.this.branch) TreeCurationTool.TreeOptimizer.this.updateRates();
/*  186 */         double[] result = { 0.0D, 0.0D };
/*  187 */         for (int i = 0; i < TreeCurationTool.TreeOptimizer.this.length; i++)
/*      */         {
/*  189 */           if ((TreeCurationTool.TreeOptimizer.this.opt[this.j] instanceof SpeciesGeneTree)) {
/*  190 */             for (int k = 0; k < TreeCurationTool.TreeOptimizer.this.lhc.length; k++) {
/*  191 */               result[0] += TreeCurationTool.this.rates.probability[i] * TreeCurationTool.TreeOptimizer.this.lhc[k][i].calculateLogLikelihood();
/*      */             }
/*      */             
/*      */           } else {
/*  195 */             result[0] += TreeCurationTool.this.rates.probability[i] * TreeCurationTool.TreeOptimizer.this.lhc[this.j][i].calculateLogLikelihood();
/*      */           }
/*      */         }
/*  198 */         if ((TreeCurationTool.TreeOptimizer.this.branch) && (TreeCurationTool.this.includePrior > 0)) {
/*  199 */           if (((TreeCurationTool.TreeOptimizer.this.opt[this.j] instanceof SpeciesGeneTree)) && (TreeCurationTool.this.includePrior > 0)) {
/*  200 */             double[] prior = ((SpeciesGeneTree)TreeCurationTool.TreeOptimizer.this.opt[this.j]).treePrior();
/*  201 */             result[1] = 
/*  202 */               (TreeCurationTool.this.includePrior == 2 ? prior[1] : TreeCurationTool.this.includePrior == 1 ? prior[0] : prior[0] + prior[1]);
/*      */           }
/*  204 */           else if (((TreeCurationTool.TreeOptimizer.this.opt[this.j] instanceof GeneTree)) && (TreeCurationTool.this.includePrior > 0)) {
/*  205 */             double[] prior = ((GeneTree)TreeCurationTool.TreeOptimizer.this.opt[this.j]).logUnnormalizedPdf();
/*  206 */             result[1] = 
/*  207 */               (TreeCurationTool.this.includePrior == 2 ? prior[1] : TreeCurationTool.this.includePrior == 1 ? prior[0] : prior[0] + prior[1]);
/*      */           }
/*      */         }
/*      */         
/*      */ 
/*  212 */         return result;
/*      */       }
/*      */       
/*  215 */       public double getLowerBound(int n) { return TreeCurationTool.TreeOptimizer.this.opt[this.j].getLowerLimit(n); }
/*      */       
/*      */ 
/*      */ 
/*  219 */       public int getNumArguments() { return TreeCurationTool.TreeOptimizer.this.opt[this.j].getNumParameters(); }
/*      */       
/*  221 */       public OrthogonalHints getOrthogonalHints() { return null; }
/*      */       
/*      */       public double getUpperBound(int n) {
/*  224 */         return TreeCurationTool.TreeOptimizer.this.opt[this.j].getUpperLimit(n);
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
/*      */     TreeOptimizer(SitePattern[] siteP, boolean branch)
/*      */     {
/*  237 */       this.branch = branch;
/*  238 */       this.length = TreeCurationTool.this.rates.getCategoryProbabilities().length;
/*      */       int i;
/*  240 */       if (branch) {
/*  241 */         if (((TreeCurationTool.this.tree[0] instanceof GeneTree)) && ((TreeCurationTool.this.taxonTree instanceof Parameterized))) {
/*  242 */           this.opt = new Parameterized[] { new SpeciesGeneTree(TreeCurationTool.this.taxonTree, TreeCurationTool.this.tree) };
/*      */         }
/*      */         else {
/*  245 */           this.opt = new Parameterized[siteP.length];
/*  246 */           for (i = 0; i < this.opt.length; i++) {
/*  247 */             this.opt[i] = TreeCurationTool.this.tree[i];
/*      */           }
/*      */         }
/*      */       }
/*      */       else {
/*  252 */         this.opt = new Parameterized[] { new SubstModel(TreeCurationTool.this.rm, TreeCurationTool.this.rates) };
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*  257 */       this.lhc = new FastLikelihoodCalculator[siteP.length][this.length];
/*  258 */       for (int i = 0; i < this.length; i++)
/*      */       {
/*  260 */         for (int j = 0; j < this.lhc.length; j++) {
/*  261 */           this.lhc[j][i] = new FastLikelihoodCalculator(siteP[j], TreeCurationTool.this.tree[j]);
/*  262 */           this.lhc[j][i].setModel(TreeCurationTool.this.rm);
/*      */         }
/*      */       }
/*  265 */       updateRates();
/*      */     }
/*      */     
/*      */ 
/*      */     public double[] optimize()
/*      */     {
/*  271 */       double[] res = { 0.0D, 0.0D };
/*  272 */       EMF mvf = new EMF();
/*  273 */       for (int j = 0; j < this.opt.length; j++) {
/*  274 */         mvf.j = j;
/*      */         
/*  276 */         double[] xvec = new double[mvf.getNumArguments()];
/*  277 */         for (int i = 0; i < this.opt[j].getNumParameters(); i++) {
/*  278 */           xvec[i] = this.opt[j].getParameter(i);
/*      */         }
/*  280 */         MultivariateMinimum mvm = this.opt[j].getNumParameters() > 1 ? new ConjugateDirectionSearch() : 
/*  281 */           new OrthogonalSearch();
/*  282 */         if (xvec.length > 0) System.out.println("Before " + Print.toString(xvec));
/*  283 */         if (this.opt[j].getNumParameters() > 0) {
/*  284 */           mvm.optimize(mvf, xvec, 1.0D, 0.1D);
/*      */         }
/*  286 */         double[] min = mvf.evaluatePriors(xvec);
/*  287 */         res[0] += min[0];res[1] += min[1];
/*  288 */         if (xvec.length > 0) System.out.println("After " + Print.toString(xvec) + " with min " + Print.toString(min));
/*  289 */         for (int i = 0; i < xvec.length; i++) {
/*  290 */           this.opt[j].setParameter(xvec[i], i);
/*      */         }
/*      */       }
/*      */       
/*  294 */       for (int j = 0; j < this.lhc.length; j++)
/*      */       {
/*  296 */         for (int i = 0; i < this.lhc[j].length; i++) {
/*  297 */           this.lhc[j][i].release();
/*      */         }
/*      */       }
/*  300 */       return res;
/*      */     }
/*      */     
/*      */     private void updateRates() {
/*  304 */       System.err.println("rates " + Print.toString(TreeCurationTool.this.rates.rate) + " " + Print.toString(TreeCurationTool.this.rates.probability));
/*  305 */       for (int i = 0; i < this.length; i++) {
/*  306 */         for (int j = 0; j < this.lhc.length; j++) {
/*  307 */           this.lhc[j][i].setScaleParam(TreeCurationTool.this.rates.rate[i]);
/*      */         }
/*      */       }
/*      */     }
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
/*  349 */   private static final String[] codonRM = { "pal.substmodel.YangCodonModel" };
/*  350 */   private static final String[] dnaRM = { "pal.substmodel.TN", "pal.substmodel.HKY", 
/*  351 */     "pal.substmodel.GTR", "pal.substmodel.F84" };
/*  352 */   private static final String[] protRM = { "pal.substmodel.WAG", "pal.substmodel.JTT", "lc1.phyl.WAG_GWF" };
/*  353 */   private static final String[] allowedRateMatrices = new String[protRM.length + dnaRM.length + codonRM.length + dnaRM.length * protRM.length];
/*  354 */   private static final String[] allowedTreeType = { "pal.tree.ClockTree", "pal.tree.UnconstrainedTree", "lc1.treefam.GeneTree", 
/*  355 */     "lc1.treefam.GeneTree:pal.tree.ClockTree", "lc1.treefam.GeneTree:pal.tree.UnconstrainedTree" };
/*  356 */   private static final String[] treePriorOptions = { "none", "penalise duplications", "penalise deletions", "penalise both" };
/*  357 */   private static final String[] pamlAttr = { "t", "S", "N", "dN/dS", "dN", "dS", "S*dS", "N*dN" };
/*  358 */   private static final String[] greaterThanOpts = { "greater", "less", "greater_sum", "less_sum" };
/*  359 */   private static final int[] codonNumParams = { 2 };
/*  360 */   private static final int[] dnaNumParams = { 2, 1, 5, 1 };
/*  361 */   private static final int[] protNumParams = { 001 };
/*  362 */   private static final int[] rmNumParams = new int[protRM.length + dnaRM.length + codonRM.length + dnaRM.length * protRM.length];
/*  363 */   private static final int[] rmNumStates = new int[protRM.length + dnaRM.length + codonRM.length + dnaRM.length * protRM.length];
/*      */   
/*      */   public static final boolean VERBOSE = false;
/*      */   
/*  367 */   static final Options OPTIONS = new Options() {};
/*      */   
/*      */   final PDBView[] pdbview;
/*      */   private Collection activeCharts;
/*      */   private int[][] alias;
/*      */   JButton alignDisplay;
/*      */   String[] allowedBranchColors;
/*      */   String[] allowedCoordinates;
/*      */   
/*      */   static boolean constantZero(double[] result, int i)
/*      */   {
/*  378 */     for (int j = Math.max(i - 2, 0); (j < i + 2) && (j < result.length); j++) {
/*  379 */       if (result[j] > 0.1D) return false;
/*      */     }
/*  381 */     return true;
/*      */   }
/*      */   
/*      */   private static pal.tree.Tree convert(forester.tree.Tree tree) throws Exception {
/*  385 */     File f = new File("/tmp/tmp" + System.currentTimeMillis());
/*  386 */     f.deleteOnExit();
/*      */     
/*  388 */     TreeHelper.writeNHtree(tree, f, true, true, true);
/*      */     
/*  390 */     pal.tree.Tree tr = new ReadTree(f.getAbsolutePath());
/*      */     
/*  392 */     return tr;
/*      */   }
/*      */   
/*      */   private static forester.tree.Tree convert(pal.tree.Tree tree) throws Exception {
/*  396 */     File f = new File("/tmp/tmp" + System.currentTimeMillis());
/*  397 */     f.deleteOnExit();
/*      */     
/*  399 */     PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(f)));
/*      */     
/*  401 */     NodeUtils.printNH(tree, 
/*      */     
/*  403 */       pw, true, true, true);
/*  404 */     pw.close();
/*      */     
/*  406 */     return TreeHelper.readNHtree(f);
/*      */   }
/*      */   
/*      */   private static forester.tree.Node findNode(forester.tree.Tree tree, int x, int y) throws Exception {
/*  410 */     PreorderTreeIterator it = new PreorderTreeIterator(tree);
/*  411 */     int HALF_BOX_SIZE = 5;
/*  412 */     while (!it.isDone()) {
/*  413 */       it.next();
/*  414 */       forester.tree.Node node2 = it.currentNode();
/*  415 */       if (node2 != null)
/*      */       {
/*  417 */         if ((node2.getXcoord() - HALF_BOX_SIZE <= x) && 
/*  418 */           (node2.getXcoord() + HALF_BOX_SIZE >= x) && 
/*  419 */           (node2.getYcoord() - HALF_BOX_SIZE <= y) && 
/*  420 */           (node2.getYcoord() + HALF_BOX_SIZE >= y))
/*  421 */           return node2;
/*      */       }
/*      */     }
/*  424 */     return null;
/*      */   }
/*      */   
/*      */   private static Distribution getDistribution(double[] freq) throws Exception
/*      */   {
/*  429 */     FiniteAlphabet alpha = ProteinTools.getAlphabet();
/*  430 */     SymbolTokenization token = alpha.getTokenization("token");
/*  431 */     DataType dt = AminoAcids.DEFAULT_INSTANCE;
/*  432 */     Distribution dist = DistributionFactory.DEFAULT.createDistribution(alpha);
/*  433 */     for (Iterator it = alpha.iterator(); it.hasNext();) {
/*  434 */       dist.setWeight((Symbol)it.next(), 0.0D);
/*      */     }
/*  436 */     double sum = 0.0D;
/*  437 */     for (int i = 0; i < freq.length; i++) {
/*  438 */       sum += freq[i];
/*      */     }
/*  440 */     for (int i = 0; i < freq.length; i++) {
/*  441 */       dist.setWeight(token.parseToken(dt.getChar(i)), freq[i] / sum);
/*      */     }
/*      */     
/*  444 */     return dist;
/*      */   }
/*      */   
/*      */   public static pal.tree.Node getNode(pal.tree.Tree tree, forester.tree.Node fNode)
/*      */   {
/*  449 */     Vector leaves = fNode.getAllExternalChildren();
/*  450 */     String[] names = new String[leaves.size()];
/*  451 */     for (int i = 0; i < leaves.size(); i++) {
/*  452 */       names[i] = ((forester.tree.Node)leaves.elementAt(i)).getSeqName();
/*      */     }
/*  454 */     return pal.tree.NodeUtils.getFirstCommonAncestor(pal.tree.NodeUtils.findByIdentifier(tree.getRoot(), names));
/*      */   }
/*      */   
/*      */   private static double getRatio(forester.tree.Tree tree) {
/*  458 */     forester.tree.Node node = tree.getExtNode0();
/*  459 */     double xmax = 0.0D;
/*  460 */     for (Enumeration en = tree.getRoot().getAllExternalChildren().elements(); en.hasMoreElements();) {
/*  461 */       double x = ((forester.tree.Node)en.nextElement()).getXcoord();
/*  462 */       if (x > xmax) {
/*  463 */         xmax = x;
/*      */       }
/*      */     }
/*  466 */     double xR = tree.getRoot().getXcoord();
/*  467 */     double height = tree.getRealHeight();
/*  468 */     return height / (xmax - xR);
/*      */   }
/*      */   
/*      */   public static void main(String[] args) throws Exception {
/*  472 */     File dir = new File(".");
/*  473 */     Parser DP_PARSER = new PosixParser();
/*  474 */     CommandLine params = DP_PARSER.parse(OPTIONS, args);
/*  475 */     String[] f = params.hasOption("file") ? 
/*  476 */       params.getOptionValues("file") : null;
/*  477 */     if (f == null) {
/*  478 */       File p = dir.getParentFile();
/*  479 */       f = new String[] { dir.getName() };
/*  480 */       dir = p;
/*      */     }
/*      */     
/*  483 */     File repos = new File(params.getOptionValue("repository", "."));
/*  484 */     boolean full = false;
/*  485 */     JFrame.setDefaultLookAndFeelDecorated(true);
/*  486 */     TreeCurationTool tc = new TreeCurationTool(dir, f, "seed", repos);
/*  487 */     tc.setLocation(500, 120);
/*  488 */     ATVjframe[] atvpanel = new ATVjframe[tc.fTree.length];
/*  489 */     for (int i = 0; i < atvpanel.length; i++) {
/*  490 */       atvpanel[i] = new ATVjframe(tc.fTree[i]);
/*  491 */       atvpanel[i].setTitle("Gene Tree " + tc.dir[i].getName());
/*      */     }
/*      */     
/*      */ 
/*  495 */     tc.addPropertyChangeListener("tree", new PropertyChangeListener() {
/*      */       public void propertyChange(PropertyChangeEvent pce) {
/*  497 */         System.err.println("heard change ");
/*  498 */         forester.tree.Tree[] trees = (forester.tree.Tree[])pce.getNewValue();
/*  499 */         for (int i = 0; i < trees.length; i++) {
/*  500 */           if (trees[i] != null) {
/*  501 */             TreeCurationTool.this[i].getATVpanel().getATVgraphic().setTree(trees[i]);
/*  502 */             TreeCurationTool.this[i].getATVpanel().getATVgraphic().getTree().recalculateAndReset();
/*  503 */             TreeCurationTool.this[i].getATVpanel().getATVgraphic().getTree().adjustNodeCount(true);
/*  504 */             TreeCurationTool.this[i].getATVpanel().getATVgraphic().resetPreferredSize();
/*  505 */             TreeCurationTool.this[i].getATVpanel().adjustJScrollPane();
/*  506 */             TreeCurationTool.this[i].getATVpanel().getATVgraphic().repaint();
/*      */           }
/*      */         }
/*      */       }
/*  510 */     });
/*  511 */     tc.addPropertyChangeListener("repaint", new PropertyChangeListener() {
/*      */       public void propertyChange(PropertyChangeEvent pce) {
/*  513 */         System.err.println("heard change -repaint");
/*      */         
/*  515 */         for (int i = 0; i < TreeCurationTool.this.length; i++)
/*      */         {
/*      */ 
/*  518 */           TreeCurationTool.this[i].getATVpanel().getATVgraphic().getTree().findExtremeLnL();
/*  519 */           TreeCurationTool.this[i].getATVpanel().getATVgraphic().getTree().adjustNodeCount(true);
/*  520 */           TreeCurationTool.this[i].getATVpanel().getATVgraphic().resetPreferredSize();
/*  521 */           TreeCurationTool.this[i].getATVpanel().adjustJScrollPane();
/*  522 */           TreeCurationTool.this[i].getATVpanel().getATVgraphic().repaint();
/*      */         }
/*      */       }
/*      */     });
/*  526 */     for (int i = 0; i < atvpanel.length; i++) {
/*  527 */       int index = i;
/*  528 */       atvpanel[i].getATVpanel().getATVgraphic().addPropertyChangeListener("tree", new PropertyChangeListener() {
/*      */         public void propertyChange(PropertyChangeEvent pce) {
/*      */           try {
/*  531 */             TreeCurationTool.this.updateTree((forester.tree.Tree)pce.getNewValue(), this.val$index);
/*      */           } catch (Exception e) {
/*  533 */             e.printStackTrace();
/*      */           }
/*      */         }
/*  536 */       });
/*  537 */       atvpanel[i].getATVpanel().getATVgraphic().addVetoableChangeListener(new VetoableChangeListener() {
/*      */         public void vetoableChange(PropertyChangeEvent pce) throws PropertyVetoException {
/*  539 */           System.err.println("heard graph change ");
/*      */           
/*  541 */           forester.tree.Node node = (forester.tree.Node)pce.getNewValue();
/*  542 */           pal.tree.Node node1 = TreeCurationTool.getNode(TreeCurationTool.this.tree[this.val$index], node);
/*  543 */           String title = NodeProbabilityCalculator.getSpeciesString(node1);
/*  544 */           if (title.equals("n.a.")) {
/*  545 */             String[] str = NodeProbabilityCalculator.getIdString(node1).split("&&");
/*  546 */             title = str[0] + (str.length > 1 ? "->" + str[(str.length - 1)] : "");
/*      */           }
/*  548 */           String tit1 = NodeProbabilityCalculator.getIdString(node1);
/*  549 */           if (tit1.indexOf("_N") >= 0) title = title + "_N";
/*  550 */           if (tit1.indexOf("_C") >= 0) title = title + "_C";
/*  551 */           boolean newG = ExtendedChart.displayGraph(this.val$index, node, node1, title, TreeCurationTool.this, TreeCurationTool.this.activeCharts, TreeCurationTool.this.selectionFrame);
/*  552 */           if (newG) {
/*      */             try {
/*  554 */               if (TreeCurationTool.this.coordBox.getSelectedIndex() == 1) {
/*  555 */                 List l = new ArrayList();
/*  556 */                 String blueItem = (String)TreeCurationTool.this.blueGreen.getSelectedItem();
/*  557 */                 String redItem = (String)TreeCurationTool.this.blueRed.getSelectedItem();
/*  558 */                 double[][][] results = node.getGraph();
/*  559 */                 String[][] names = node.getGraphNames();
/*  560 */                 for (int i = 0; i < names.length; i++) {
/*  561 */                   for (int j = 0; j < names[i].length; j++) {
/*  562 */                     if ((blueItem.startsWith(names[i][j])) || (redItem.startsWith(names[i][j]))) {
/*  563 */                       l.add(results[i][j]);
/*      */                     }
/*      */                   }
/*      */                 }
/*  567 */                 TreeCurationTool.this.pdbview[this.val$index].run((double[][])l.toArray(new double[l.size()][]), 
/*  568 */                   Double.parseDouble((String)TreeCurationTool.this.structureThresh.getValue()), 
/*  569 */                   (String)TreeCurationTool.this.showGreater.getSelectedItem(), 
/*  570 */                   TreeCurationTool.this.selectionFrame, title);
/*      */               }
/*      */             }
/*      */             catch (Exception exc)
/*      */             {
/*  575 */               exc.printStackTrace();
/*      */             }
/*      */             
/*      */           }
/*      */           
/*      */         }
/*  581 */       });
/*  582 */       atvpanel[i].getATVpanel().getATVgraphic().addMouseListener(tc.getMouseInputAdapter(atvpanel[index], false, index));
/*      */     }
/*      */     
/*  585 */     tc.setSize(500, 300);
/*      */     
/*  587 */     ATVjframe jframeTaxon = new ATVjframe(tc.fTaxonTree);
/*  588 */     jframeTaxon.getATVpanel().getATVgraphic().addMouseListener(tc.getMouseInputAdapter(jframeTaxon, true, -1));
/*  589 */     tc.addPropertyChangeListener("taxon", new PropertyChangeListener() {
/*      */       public void propertyChange(PropertyChangeEvent pce) {
/*  591 */         TreeCurationTool.this.getATVpanel().getATVgraphic().setTree((forester.tree.Tree)pce.getNewValue());
/*  592 */         TreeCurationTool.this.getATVpanel().getATVgraphic().getTree().adjustNodeCount(true);
/*  593 */         TreeCurationTool.this.getATVpanel().getATVgraphic().resetPreferredSize();
/*  594 */         TreeCurationTool.this.getATVpanel().adjustJScrollPane();
/*  595 */         TreeCurationTool.this.getATVpanel().getATVgraphic().repaint();
/*      */       }
/*  597 */     });
/*  598 */     jframeTaxon.getATVpanel().getATVgraphic().addPropertyChangeListener("tree", new PropertyChangeListener() {
/*      */       public void propertyChange(PropertyChangeEvent pce) {
/*      */         try {
/*  601 */           TreeCurationTool.this.updateTaxonTree((forester.tree.Tree)pce.getNewValue());
/*      */         } catch (Exception e) {
/*  603 */           e.printStackTrace();
/*      */         }
/*      */       }
/*  606 */     });
/*  607 */     jframeTaxon.setTitle("Species Tree");
/*  608 */     jframeTaxon.setLocation(20, 120);
/*  609 */     for (int i = 0; i < atvpanel.length; i++) {
/*  610 */       atvpanel[i].showWhole();
/*      */     }
/*  612 */     jframeTaxon.showWhole();
/*  613 */     tc.setOpaque(true);
/*      */     
/*      */ 
/*      */ 
/*  617 */     JFrame frame = new JFrame("Curation tool");
/*  618 */     frame.setContentPane(tc);
/*  619 */     tc.addPropertyChangeListener("change", new PropertyChangeListener()
/*      */     {
/*      */ 
/*  622 */       public void componentRemoved(ContainerEvent pce) { TreeCurationTool.this.repaint(); }
/*      */       
/*      */       public void propertyChange(PropertyChangeEvent pce) {
/*  625 */         TreeCurationTool.this.repaint();
/*  626 */         TreeCurationTool.this.paint(TreeCurationTool.this.getGraphics());
/*  627 */         TreeCurationTool.this.setContentPane(this.val$tc);
/*      */         
/*  629 */         TreeCurationTool.this.setVisible(true);
/*  630 */       } });
/*  631 */     frame.pack();
/*  632 */     frame.setVisible(true);
/*      */   }
/*      */   
/*      */   private static void mapTaxonNodes(pal.tree.Node root) {
/*  636 */     if (root.getIdentifier().getName().equals("")) {
/*  637 */       String st = NodeProbabilityCalculator.getIdString(root);
/*  638 */       AttributeIdentifier id = new AttributeIdentifier(st);
/*  639 */       root.setIdentifier(id);
/*  640 */       id.setAttribute("S", st);
/*      */     }
/*  642 */     for (int i = 0; i < root.getChildCount(); i++) {
/*  643 */       mapTaxonNodes(root.getChild(i));
/*      */     }
/*      */   }
/*      */   
/*      */   private static void transferSpeciesTags(pal.tree.Tree tree, IdGroup prot)
/*      */     throws Exception
/*      */   {
/*  650 */     for (int i = 0; i < tree.getIdCount(); i++) {
/*  651 */       pal.tree.Node node = tree.getExternalNode(i);
/*      */       
/*      */ 
/*      */       try
/*      */       {
/*  656 */         AttributeIdentifier ident = (AttributeIdentifier)prot.getIdentifier(prot.whichIdNumber(node.getIdentifier().getName()));
/*  657 */         node.setIdentifier(ident);
/*      */       }
/*      */       catch (Exception exc) {
/*  660 */         System.err.println("couldn't find node for " + node.getIdentifier().getName());
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   PfamAlphabet alph;
/*      */   
/*      */   int coords;
/*      */   
/*      */   int likelihoodIndex;
/*      */   
/*      */   int branchColorIndex;
/*      */   
/*      */   int includePrior;
/*      */   
/*      */   CodonTable ct;
/*      */   
/*      */   final File[] dir;
/*      */   
/*      */   final File[] dnaAlignF;
/*      */   
/*      */   final File[] dnaFasta;
/*      */   final File[] nhx;
/*      */   final File[] protAlignF;
/*      */   final File[] protFasta;
/*      */   final SitePattern[] codon;
/*      */   final SitePattern[] dna;
/*      */   final SitePattern[] prot;
/*      */   DefaultCategoryDataset likelihood;
/*      */   DefaultCategoryDataset priorlikelihood;
/*      */   XYSeriesCollection siteSpecificLikelihood;
/*      */   AlignmentHMM[] matchHMM;
/*      */   double[][][] matchStatePosterior;
/*      */   public double mean_dupl_time;
/*      */   public double perc_deleted;
/*      */   JButton njTree;
/*      */   JButton optModel;
/*      */   JButton optBranch;
/*      */   JComboBox rateMatrixType;
/*      */   JComboBox treeType;
/*      */   JLabel rateParamsLabel;
/*      */   JLabel rmParamsLabel;
/*      */   final JFrame selectionFrame;
/*      */   final ChartPanel chartPanel;
/*      */   File phigsdb;
/*      */   File repository;
/*      */   File taxonomy;
/*      */   final ParameterizedTree[] protTree;
/*      */   RateDistribution rates;
/*      */   RateMatrix rm;
/*      */   ParameterizedTree taxonTree;
/*      */   final ParameterizedTree[] tree;
/*      */   pal.tree.Tree taxonOrig;
/*      */   forester.tree.Tree fTaxonTree;
/*      */   final forester.tree.Tree[] fTree;
/*      */   JComboBox coordBox;
/*      */   JComboBox blueGreen;
/*      */   JComboBox showGreater;
/*      */   JFormattedTextField structureThresh;
/*      */   JComboBox blueRed;
/*      */   private SequenceDB getSequenceDB(SequenceIterator it)
/*      */     throws Exception
/*      */   {
/*  724 */     SequenceDB db = new HashSequenceDB();
/*  725 */     while (it.hasNext()) {
/*  726 */       db.addSequence(it.nextSequence());
/*      */     }
/*  728 */     return db;
/*      */   }
/*      */   
/*      */   public TreeCurationTool(File parent, String[] f, String base, File repos)
/*      */     throws Exception
/*      */   {
/*  315 */     System.arraycopy(protRM, 0, allowedRateMatrices, 0, protRM.length);
/*  316 */     System.arraycopy(protNumParams, 0, rmNumParams, 0, protRM.length);
/*  317 */     Arrays.fill(rmNumStates, 0, protRM.length, 20);
/*      */     
/*  319 */     System.arraycopy(dnaRM, 0, allowedRateMatrices, protRM.length, dnaRM.length);
/*  320 */     System.arraycopy(dnaNumParams, 0, rmNumParams, protRM.length, dnaRM.length);
/*  321 */     Arrays.fill(rmNumStates, protRM.length, dnaRM.length + protRM.length, 4);
/*      */     
/*  323 */     System.arraycopy(codonRM, 0, allowedRateMatrices, protRM.length + dnaRM.length, codonRM.length);
/*  324 */     System.arraycopy(codonNumParams, 0, rmNumParams, protRM.length + dnaRM.length, codonRM.length);
/*  325 */     Arrays.fill(rmNumStates, protRM.length + dnaRM.length, allowedRateMatrices.length, 64);
/*      */     
/*  327 */     int st = protRM.length + dnaRM.length + codonRM.length;
/*  328 */     for (int i = 0; i < protRM.length; i++) {
/*  329 */       for (int j = 0; j < dnaRM.length; j++) {
/*  330 */         allowedRateMatrices[(st + i * dnaRM.length + j)] = (protRM[i] + ":" + dnaRM[j]);
/*      */       }
/*      */     }
/*      */     
/*  334 */     String[][] graphNames = PseudoManager.outputF;
/*  335 */     List b = new ArrayList(5);
/*  336 */     b.add("none");
/*  337 */     for (int i = 0; i < graphNames.length; i++) {
/*  338 */       for (int j = 0; j < graphNames[i].length; j++) {
/*  339 */         b.add(graphNames[i][j] + "_max");
/*  340 */         b.add(graphNames[i][j] + "_avg");
/*  341 */         b.add(graphNames[i][j] + "_sum");
/*      */       }
/*      */     }
/*  344 */     b.addAll(Arrays.asList(pamlAttr));
/*  345 */     this.allowedBranchColors = ((String[])b.toArray(new String[b.size()]));
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
/*  668 */     this.activeCharts = new ArrayList();
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
/*  681 */     this.coords = 0;
/*  682 */     this.likelihoodIndex = 0;
/*  683 */     this.branchColorIndex = 0;
/*  684 */     this.includePrior = 0;
/*  685 */     this.ct = CodonTableFactory.createUniversalTranslator();
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  692 */     this.likelihood = new DefaultCategoryDataset();
/*  693 */     this.priorlikelihood = new DefaultCategoryDataset();
/*  694 */     this.siteSpecificLikelihood = new XYSeriesCollection();
/*      */     
/*  696 */     this.matchHMM = null;
/*  697 */     this.matchStatePosterior = null;
/*      */     
/*      */ 
/*  700 */     this.mean_dupl_time = 1.0D;
/*  701 */     this.perc_deleted = 0.1D;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  707 */     this.selectionFrame = new JFrame("PSILC");
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
/*  732 */     this.dir = new File[f.length];
/*  733 */     this.protFasta = new File[f.length];
/*  734 */     this.pdbview = new PDBView[f.length];
/*  735 */     this.fTree = new forester.tree.Tree[f.length];
/*  736 */     this.dnaFasta = new File[f.length];
/*  737 */     this.protAlignF = new File[f.length];
/*  738 */     this.dnaAlignF = new File[f.length];
/*  739 */     this.tree = new ParameterizedTree[f.length];
/*  740 */     this.protTree = new ParameterizedTree[f.length];
/*  741 */     this.nhx = new File[f.length];
/*  742 */     this.prot = new SitePattern[f.length];
/*  743 */     this.dna = new SitePattern[f.length];
/*  744 */     this.codon = new SitePattern[f.length];
/*  745 */     this.alph = PfamAlphabet.makeAlphabet(repos);
/*  746 */     this.phigsdb = new File(repos, "ensembldb");
/*  747 */     this.repository = repos;
/*      */     
/*      */ 
/*  750 */     double[][] eq_freqs = new double[f.length][0];
/*  751 */     for (int i = 0; i < f.length; i++) {
/*  752 */       this.dir[i] = new File(parent, f[i]);
/*  753 */       this.protFasta[i] = new File(this.dir[i], base + ".fa");
/*  754 */       this.dnaFasta[i] = new File(this.dir[i], base + ".dna.fa");
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*  759 */       this.protAlignF[i] = new File(this.dir[i], base + ".mfa");
/*  760 */       this.dnaAlignF[i] = new File(this.dir[i], base + ".dna.mfa");
/*  761 */       this.nhx[i] = new File(this.dir[i], base + ".nhx");
/*  762 */       File treeF = this.nhx[i];
/*  763 */       if ((!this.protAlignF[i].exists()) || (this.dnaAlignF[i].exists())) {
/*  764 */         this.dna[i] = getSitePattern(i, 4);
/*      */         
/*  766 */         this.prot[i] = getSitePattern(i, 20);
/*  767 */         PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(this.protAlignF[i])));
/*  768 */         AlignTools.printMFA(this.prot[i], pw);
/*  769 */         pw.close();
/*      */       }
/*      */       else {
/*  772 */         this.prot[i] = getSitePattern(i, 20);
/*      */         
/*  774 */         this.dna[i] = getSitePattern(i, 4);
/*      */       }
/*  776 */       if (this.prot[i].getSequenceCount() > 50) {
/*  777 */         IdGroup idg = null;
/*  778 */         if ((!treeF.exists()) || (treeF.length() == 0L)) {
/*  779 */           DistanceMatrix dm = new AlignmentDistanceMatrix(this.prot[i], null);
/*  780 */           dm = AlignUtils.collapseSimilar(dm, 50, null, new HashMap());
/*  781 */           idg = dm;
/*      */         }
/*      */         else {
/*  784 */           idg = new ReadTree(treeF.getAbsolutePath());
/*      */         }
/*  786 */         this.codon[i] = SitePattern.getSitePattern(AlignUtils.restrictAlignment(this.codon[i], idg));
/*      */         
/*  788 */         this.prot[i] = SitePattern.getSitePattern(AlignUtils.translate(this.codon[i]));
/*  789 */         System.err.println("count " + this.prot[i].getSequenceCount());
/*      */         
/*  791 */         this.dna[i] = SitePattern.getSitePattern(AlignUtils.getDNAAlignmentFromCodons(this.codon[i]));
/*      */       }
/*      */       
/*  794 */       if ((!treeF.exists()) || (treeF.length() == 0L)) {
/*  795 */         AlignTools.phyml(this.nhx[i], this.prot[i]);
/*      */       }
/*      */       
/*      */ 
/*  799 */       this.tree[i] = new UnconstrainedTree(new ReadTree(treeF.getAbsolutePath()));
/*  800 */       this.fTree[i] = TreeHelper.readNHtree(treeF);
/*      */       
/*      */ 
/*  803 */       this.protTree[i] = this.tree[i];
/*  804 */       resetInternalIdentifiers(i);
/*      */       
/*  806 */       eq_freqs[i] = AlignmentUtils.estimateFrequencies(this.prot[i]);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*  811 */     this.rates = new UniformRate();
/*  812 */     this.rm = new WAG(average(eq_freqs));
/*      */     
/*  814 */     this.taxonomy = new File(this.dir[0], base + ".species.nhx");
/*  815 */     System.err.println("getting taxonomy into " + this.taxonomy.getAbsolutePath());
/*      */     
/*  817 */     this.taxonTree = getTaxonomy();
/*  818 */     System.err.println(" ...done ");
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  827 */     if (this.taxonTree != null) { updateForesterTree(true, -1);
/*      */     }
/*  829 */     JToolBar toolBar = new JToolBar(0);
/*      */     
/*  831 */     setLayout(new BorderLayout());
/*  832 */     this.optBranch = getBranchOptimizeButton();
/*  833 */     this.optBranch.setEnabled(false);
/*  834 */     this.njTree = rebuildNJTree();
/*  835 */     this.njTree.setEnabled(true);
/*  836 */     this.optModel = getModelOptimizeButton();
/*  837 */     this.optModel.setEnabled(true);
/*  838 */     this.alignDisplay = getAlignDisplayButton();
/*      */     
/*  840 */     toolBar.add(this.optModel);
/*  841 */     toolBar.add(this.optBranch);
/*  842 */     toolBar.add(getPSILCButton());
/*  843 */     toolBar.add(getPAMLButton());
/*  844 */     toolBar.add(this.alignDisplay);
/*  845 */     toolBar.add(this.njTree);
/*  846 */     add(toolBar, "Last");
/*  847 */     this.chartPanel = new ChartPanel(getChart());
/*  848 */     this.chartPanel.setPreferredSize(new Dimension(500, 270));
/*  849 */     JSplitPane optPanel1 = new JSplitPane(0, getOptionPanel(), getPSILCOptionPanel());
/*  850 */     JSplitPane optPanel2 = new JSplitPane(1, getPAMLOptionPanel(), optPanel1);
/*  851 */     JSplitPane optPanel3 = new JSplitPane(1, this.chartPanel, optPanel2);
/*  852 */     optPanel3.setOneTouchExpandable(true);
/*  853 */     optPanel1.setOneTouchExpandable(true);
/*  854 */     optPanel2.setOneTouchExpandable(true);
/*  855 */     add(optPanel3, 
/*  856 */       "Center");
/*  857 */     this.optModel.doClick();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private void addLabelTextRows(JComponent[] labels, JComponent[] textFields, GridBagLayout gridbag, Container container)
/*      */   {
/*  865 */     GridBagConstraints c = new GridBagConstraints();
/*  866 */     c.anchor = 13;
/*  867 */     int numLabels = labels.length;
/*      */     
/*  869 */     for (int i = 0; i < numLabels; i++) {
/*  870 */       c.gridwidth = -1;
/*  871 */       c.fill = 0;
/*  872 */       c.weightx = 0.0D;
/*  873 */       container.add(labels[i], c);
/*      */       
/*  875 */       c.gridwidth = 0;
/*  876 */       c.fill = 2;
/*  877 */       c.weightx = 1.0D;
/*  878 */       container.add(textFields[i], c);
/*      */     }
/*      */   }
/*      */   
/*      */   private double[] average(double[][] d) {
/*  883 */     double[] res = new double[d[0].length];
/*  884 */     for (int j = 0; j < res.length; j++) {
/*  885 */       for (int i = 0; i < d.length; i++) {
/*  886 */         res[j] += d[i][j];
/*      */       }
/*  888 */       res[j] /= d.length;
/*      */     }
/*  890 */     return res;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void chartMouseClicked(ChartMouseEvent event)
/*      */   {
/*  897 */     XYItemEntity entity = (XYItemEntity)event.getEntity();
/*  898 */     if (entity == null) {
/*  899 */       System.err.println("is null");
/*  900 */       return;
/*      */     }
/*  902 */     int x1 = entity.getDataset().getXValue(entity.getSeries(), entity.getItem()).intValue() - 1;
/*      */     
/*      */ 
/*  905 */     ExtendedChart chart = (ExtendedChart)event.getChart();
/*  906 */     int[] alias = chart.fNode.getAlias();
/*  907 */     int x = alias != null ? alias[x1] : x1;
/*      */     
/*      */ 
/*      */ 
/*  911 */     Set below = new HashSet();
/*  912 */     Set others = new HashSet();
/*  913 */     pal.tree.Node node = chart.node;
/*      */     
/*  915 */     pal.tree.Node[] bel = pal.tree.NodeUtils.getExternalNodes(chart.node);
/*  916 */     pal.tree.Node root = node.getParent();
/*  917 */     while (!root.isRoot()) {
/*  918 */       root = root.getParent();
/*      */     }
/*  920 */     pal.tree.Node[] belE = pal.tree.NodeUtils.getExternalNodes(root);
/*  921 */     for (int i = 0; i < belE.length; i++) {
/*  922 */       others.add(belE[i].getIdentifier());
/*      */     }
/*  924 */     for (int i = 0; i < bel.length; i++) {
/*  925 */       below.add(bel[i].getIdentifier());
/*      */     }
/*  927 */     others.removeAll(below);
/*      */     
/*      */ 
/*      */ 
/*  931 */     IdGroup[] ids = {
/*  932 */       new SimpleIdGroup((Identifier[])below.toArray(new Identifier[0])), 
/*  933 */       new SimpleIdGroup((Identifier[])others.toArray(new Identifier[0])) };
/*  934 */     Alignment[][] aligns = {
/*  935 */       {
/*  936 */       AlignUtils.restrictAlignment(this.prot[0], ids[0]), 
/*  937 */       AlignUtils.restrictAlignment(this.dna[0], ids[0]) }, 
/*      */       
/*  939 */       {
/*  940 */       AlignUtils.restrictAlignment(this.prot[0], ids[1]), 
/*  941 */       AlignUtils.restrictAlignment(this.dna[0], ids[1]) } };
/*      */     
/*      */ 
/*  944 */     TableModel tm = new AbstractTableModel() {
/*      */       public int getColumnCount() {
/*  946 */         return 3;
/*      */       }
/*      */       
/*  949 */       public int getRowCount() { return this.val$others.size() + this.val$below.size() + 3; }
/*      */       
/*      */       public Object getValueAt(int row, int col) {
/*  952 */         if (row == 0) {
/*  953 */           if (col == 0) return "Below node ";
/*  954 */           return "";
/*      */         }
/*  956 */         if (row < this.val$below.size() + 1) {
/*  957 */           if (col == 0) return this.val$ids[0].getIdentifier(row - 1);
/*  958 */           if (col == 1) return this.val$aligns[0][(col - 1)].getAlignedSequenceString(row - 1).substring(this.val$x, this.val$x + 1);
/*  959 */           return this.val$aligns[0][(col - 1)].getAlignedSequenceString(row - 1).substring(3 * this.val$x, 3 * this.val$x + 3);
/*      */         }
/*  961 */         if (row == this.val$below.size() + 1) {
/*  962 */           if (col == 0) return "Other nodes";
/*  963 */           return "";
/*      */         }
/*  965 */         if (row < this.val$below.size() + this.val$others.size() + 2) {
/*  966 */           if (col == 0) return this.val$ids[1].getIdentifier(row - 2 - this.val$below.size());
/*  967 */           if (col == 1) return this.val$aligns[1][(col - 1)].getAlignedSequenceString(row - 2 - this.val$below.size()).substring(this.val$x, this.val$x + 1);
/*  968 */           return this.val$aligns[1][(col - 1)].getAlignedSequenceString(row - 2 - this.val$below.size()).substring(3 * this.val$x, 3 * this.val$x + 3);
/*      */         }
/*      */         
/*  971 */         if (TreeCurationTool.this.coordBox.getSelectedItem().equals(TreeCurationTool.this.allowedCoordinates[1])) {
/*  972 */           if (col == 0) return TreeCurationTool.this.pdbview[0].getName();
/*  973 */           return TreeCurationTool.this.pdbview[0].getSymbolAt(this.val$x1);
/*      */         }
/*  975 */         return "";
/*      */       }
/*      */       
/*  978 */     };
/*  979 */     JTable table = new JTable(tm);
/*  980 */     table.setAutoResizeMode(4);
/*  981 */     JScrollPane jsp = new JScrollPane(table);
/*  982 */     JFrame jp = new JFrame("Position " + (x1 + 1) + " at " + NodeProbabilityCalculator.getSpeciesString(node) + ": " + 
/*  983 */       NodeProbabilityCalculator.getIdString(node));
/*      */     
/*  985 */     Dimension d = new Dimension(200, Math.min(800, table.getRowCount() * 30));
/*  986 */     table.setPreferredSize(d);
/*  987 */     System.err.println("displaying table");
/*  988 */     jp.getContentPane().setLayout(new BorderLayout());
/*      */     
/*  990 */     JPanel container = new JPanel();
/*  991 */     LayoutManager gridbag = new BoxLayout(container, 0);
/*  992 */     container.setLayout(gridbag);
/*      */     
/*  994 */     DistributionLogo sLogo = new DistributionLogo();
/*      */     try {
/*  996 */       sLogo.setDistribution(getDistribution(((DomainCodonModel)this.rm).getProteinModel().getEquilibriumFrequencies()));
/*      */     } catch (Exception exc) {
/*  998 */       exc.printStackTrace();
/*      */     }
/* 1000 */     sLogo.setLogoPainter(new TextLogoPainter());
/* 1001 */     sLogo.setStyle(new AminoStyle());
/* 1002 */     sLogo.setScaleByInformation(false);
/*      */     
/* 1004 */     container.add(sLogo);
/*      */     
/* 1006 */     System.out.println(Print.toString(AminoAcids.DEFAULT_INSTANCE));
/* 1007 */     for (int l = 0; l < this.matchStatePosterior.length; l++) {
/* 1008 */       for (int j = 0; j < this.matchStatePosterior[l].length; j++) {
/* 1009 */         if (this.matchStatePosterior[l][j][x] > 0.1D) {
/* 1010 */           System.out.println("match state with prob" + this.matchStatePosterior[l][j][x]);
/* 1011 */           System.out.println(Print.toString(this.matchHMM[l].getMatch(j).getDistribution()));
/* 1012 */           DistributionLogo sLogo = new DistributionLogo();
/*      */           try {
/* 1014 */             sLogo.setDistribution(getDistribution(this.matchHMM[l].getMatch(j).getDistribution()));
/*      */           } catch (Exception exc) {
/* 1016 */             exc.printStackTrace();
/*      */           }
/* 1018 */           sLogo.setLogoPainter(new TextLogoPainter());
/* 1019 */           sLogo.setScaleByInformation(false);
/* 1020 */           sLogo.setStyle(new AminoStyle());
/*      */           
/*      */ 
/* 1023 */           container.add(sLogo);
/*      */         }
/*      */       }
/*      */     }
/* 1027 */     jp.getContentPane().add("Center", jsp);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1034 */     container.setBackground(Color.WHITE);
/* 1035 */     jp.getContentPane().add("East", container);
/* 1036 */     container.setPreferredSize(new Dimension(d.width / 5, d.height));
/* 1037 */     jp.pack();
/* 1038 */     jp.setVisible(true);
/*      */   }
/*      */   
/*      */ 
/*      */   public void chartMouseMoved(ChartMouseEvent event) {}
/*      */   
/*      */ 
/*      */   public JComboBox coords()
/*      */   {
/* 1047 */     String key = "Coordinates";
/*      */     
/* 1049 */     this.allowedCoordinates = new String[2 + (this.dna[0] == null ? 0 : this.dna[0].getSequenceCount())];
/* 1050 */     this.allowedCoordinates[0] = "alignment";
/* 1051 */     this.allowedCoordinates[1] = "pdb";
/* 1052 */     for (int i = 2; i < this.allowedCoordinates.length; i++) {
/* 1053 */       this.allowedCoordinates[i] = this.dna[0].getIdentifier(i - 2).getName();
/*      */     }
/*      */     
/* 1056 */     JComboBox field = new JComboBox(this.allowedCoordinates);
/* 1057 */     field.setSelectedIndex(this.coords);
/* 1058 */     field.setActionCommand(key);
/* 1059 */     field.setEnabled(true);
/* 1060 */     field.addActionListener(new ActionListener() {
/*      */       public void actionPerformed(ActionEvent ae) {
/*      */         try {
/* 1063 */           TreeCurationTool.this.coords = this.val$field.getSelectedIndex();
/* 1064 */           for (int i = 0; i < TreeCurationTool.this.fTree.length; i++) {
/* 1065 */             TreeCurationTool.this.switchCoords(TreeCurationTool.this.fTree[i].getRoot(), 
/* 1066 */               TreeCurationTool.this.coords == 1 ? TreeCurationTool.this.pdbview[i].alias : TreeCurationTool.this.coords == 0 ? null : 
/* 1067 */               TreeCurationTool.this.alias[(TreeCurationTool.this.coords - 2)]);
/*      */           }
/*      */           
/* 1070 */           TreeCurationTool.this.firePropertyChange("psilc", false, true);
/*      */         }
/*      */         catch (Exception exc) {
/* 1073 */           exc.printStackTrace();
/*      */         }
/*      */         
/*      */       }
/* 1077 */     });
/* 1078 */     return field;
/*      */   }
/*      */   
/*      */ 
/* 1082 */   private void disableAll() { List currentStates = new ArrayList(); }
/*      */   
/*      */   public void displayAlignment(int i) {
/* 1085 */     File[] alignFiles = this.rm.getDataType().getNumStates() == 4 ? 
/* 1086 */       this.dnaAlignF : this.protAlignF;
/* 1087 */     String[] args = { alignFiles[i].getAbsolutePath(), "File", "FASTA" };
/* 1088 */     AlignFrame.main(args);
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
/*      */   public JFormattedTextField duplicationTime()
/*      */   {
/* 1121 */     JFormattedTextField field = new JFormattedTextField(this.mean_dupl_time);
/* 1122 */     field.setActionCommand("dupl time");
/* 1123 */     field.setEnabled(true);
/* 1124 */     field.addPropertyChangeListener("value", new PropertyChangeListener() {
/*      */       public void propertyChange(PropertyChangeEvent ae) {
/*      */         try {
/* 1127 */           TreeCurationTool.this.mean_dupl_time = Double.parseDouble((String)this.val$field.getValue());
/* 1128 */           TreeCurationTool.this.optModel.setEnabled(false);
/*      */           
/* 1130 */           TreeCurationTool.this.optBranch.setEnabled(true);
/*      */         }
/*      */         catch (Exception exc) {
/* 1133 */           exc.printStackTrace();
/*      */         }
/*      */         
/*      */       }
/* 1137 */     });
/* 1138 */     return field;
/*      */   }
/*      */   
/*      */   public JFormattedTextField gammaRates()
/*      */   {
/* 1143 */     String key = "Number of gamma rates";
/* 1144 */     String value = this.rates.numRates;
/* 1145 */     JFormattedTextField field = new JFormattedTextField(value);
/* 1146 */     field.setActionCommand(key);
/* 1147 */     field.setEnabled(true);
/* 1148 */     field.addPropertyChangeListener("value", new PropertyChangeListener() {
/*      */       public void propertyChange(PropertyChangeEvent ae) {
/*      */         try {
/* 1151 */           int n = Integer.parseInt((String)this.val$field.getValue());
/* 1152 */           TreeCurationTool.this.rates = (n == 1 ? new UniformRate() : new GammaRates(n, 1.0D));
/* 1153 */           TreeCurationTool.this.optModel.setEnabled(true);
/* 1154 */           TreeCurationTool.this.optBranch.setEnabled(false);
/*      */         }
/*      */         catch (Exception exc)
/*      */         {
/* 1158 */           exc.printStackTrace();
/*      */         }
/*      */       }
/* 1161 */     });
/* 1162 */     return field;
/*      */   }
/*      */   
/*      */   public JButton getAlignDisplayButton() throws Exception {
/* 1166 */     JButton jv = new JButton("Display alignment");
/* 1167 */     jv.addActionListener(new ActionListener() {
/*      */       public void actionPerformed(ActionEvent e) {
/*      */         try {
/* 1170 */           File[] alignFiles = TreeCurationTool.this.rm.getDataType().getNumStates() == 4 ? 
/* 1171 */             TreeCurationTool.this.dnaAlignF : TreeCurationTool.this.protAlignF;
/* 1172 */           for (int i = 0; i < alignFiles.length; i++)
/*      */           {
/*      */ 
/* 1175 */             TreeCurationTool.this.displayAlignment(i);
/*      */           }
/*      */         }
/*      */         catch (Exception e1) {
/* 1179 */           e1.printStackTrace();
/*      */         }
/*      */       }
/* 1182 */     });
/* 1183 */     return jv;
/*      */   }
/*      */   
/*      */   public JButton getBranchOptimizeButton() throws Exception {
/* 1187 */     JButton optBranch = new JButton("optimize branch \n lengths");
/* 1188 */     optBranch.addActionListener(new ActionListener()
/*      */     {
/*      */       public void actionPerformed(ActionEvent e) {
/*      */         try {
/* 1192 */           TreeCurationTool.this.setEnabled(false);
/*      */           
/*      */ 
/* 1195 */           TreeCurationTool.this.modifyTree(TreeCurationTool.this.rm.getDataType().getNumStates());
/*      */           
/*      */ 
/*      */ 
/* 1199 */           SitePattern[] siteP = new SitePattern[TreeCurationTool.this.tree.length];
/* 1200 */           for (int i = 0; i < siteP.length; i++) {
/* 1201 */             siteP[i] = TreeCurationTool.this.getSitePattern(i, TreeCurationTool.this.rm.getDataType().getNumStates());
/*      */           }
/*      */           
/* 1204 */           TreeCurationTool.TreeOptimizer lv = new TreeCurationTool.TreeOptimizer(TreeCurationTool.this, siteP, true);
/*      */           
/* 1206 */           double[] min = lv.optimize();
/*      */           
/* 1208 */           String nm = TreeCurationTool.this.rm.getClass().getName();
/* 1209 */           nm = nm.substring(nm.lastIndexOf('.') + 1);
/* 1210 */           String nm1 = TreeCurationTool.this.getTreeType();
/* 1211 */           String[] nm1st = { nm1 };
/* 1212 */           if (nm1.indexOf(':') >= 0) {
/* 1213 */             nm1st = nm1.split(":");
/*      */           }
/* 1215 */           String id = "";
/* 1216 */           for (int k = 0; k < nm1st.length; k++) {
/* 1217 */             id = id + nm1st[0].substring(nm1st[0].lastIndexOf('.')) + ",";
/*      */           }
/*      */           
/*      */ 
/* 1221 */           TreeCurationTool.this.likelihood.addValue(min[0] + min[1], nm + "_" + TreeCurationTool.this.rates.numRates + TreeCurationTool.this.getParamsString(TreeCurationTool.this.rm) + "_" + TreeCurationTool.this.rates.numRates, 
/* 1222 */             id + "_" + TreeCurationTool.this.likelihoodIndex);
/* 1223 */           TreeCurationTool.this.priorlikelihood.addValue(min[1], nm + "_" + TreeCurationTool.this.rates.numRates + TreeCurationTool.this.getParamsString(TreeCurationTool.this.rm) + "_" + TreeCurationTool.this.rates.numRates, 
/* 1224 */             id + "_" + TreeCurationTool.this.likelihoodIndex);
/* 1225 */           for (int i = 0; i < TreeCurationTool.this.tree.length; i++) {
/* 1226 */             TreeCurationTool.this.updateForesterTree(false, i);
/*      */           }
/* 1228 */           TreeCurationTool.this.updateForesterTree(true, -1);
/* 1229 */           TreeCurationTool.this.setEnabled(true);
/* 1230 */           TreeCurationTool.this.optModel.setEnabled(true);
/* 1231 */           this.val$optBranch.setEnabled(false);
/* 1232 */           TreeCurationTool.this.chartPanel.setChart(TreeCurationTool.this.getChart());
/* 1233 */           TreeCurationTool.this.chartPanel.setVisible(true);
/* 1234 */           TreeCurationTool.this.repaint();
/* 1235 */           TreeCurationTool.this.likelihoodIndex += 1;
/*      */         } catch (Exception e1) {
/* 1237 */           e1.printStackTrace();
/*      */         }
/*      */         
/*      */       }
/* 1241 */     });
/* 1242 */     return optBranch;
/*      */   }
/*      */   
/*      */ 
/*      */   JFreeChart getChart()
/*      */   {
/* 1248 */     JFreeChart chart = ChartFactory.createLineChart(
/* 1249 */       "loglikelihood ", 
/* 1250 */       "Model", 
/* 1251 */       "Log-likelihood", 
/* 1252 */       this.likelihood, 
/* 1253 */       PlotOrientation.VERTICAL, 
/* 1254 */       true, 
/* 1255 */       true, 
/* 1256 */       false);
/*      */     
/* 1258 */     ((NumberAxis)((CategoryPlot)chart.getPlot()).getRangeAxis()).setAutoRangeIncludesZero(false);
/* 1259 */     chart.setBackgroundPaint(Color.white);
/* 1260 */     chart.getLegend().setAnchor(3);
/* 1261 */     CategoryPlot plot = chart.getCategoryPlot();
/* 1262 */     ((LineAndShapeRenderer)plot.getRenderer()).setDrawShapes(true);
/* 1263 */     plot.setDomainAxisLocation(AxisLocation.BOTTOM_OR_RIGHT);
/* 1264 */     CategoryAxis domainAxis = plot.getDomainAxis();
/* 1265 */     domainAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_90);
/*      */     
/* 1267 */     plot.setDataset(1, this.priorlikelihood);
/* 1268 */     plot.mapDatasetToRangeAxis(1, 1);
/*      */     
/* 1270 */     ValueAxis axis2 = new NumberAxis("Prior contribution");
/* 1271 */     plot.setRangeAxis(1, axis2);
/*      */     
/* 1273 */     LineAndShapeRenderer renderer2 = new LineAndShapeRenderer();
/* 1274 */     renderer2.setDrawShapes(true);
/* 1275 */     renderer2.setToolTipGenerator(new StandardCategoryToolTipGenerator());
/* 1276 */     plot.setRenderer(1, renderer2);
/* 1277 */     plot.setDatasetRenderingOrder(DatasetRenderingOrder.REVERSE);
/* 1278 */     return chart;
/*      */   }
/*      */   
/*      */   public JButton getModelOptimizeButton() {
/* 1282 */     JButton optModel = new JButton("optimize model parameters");
/* 1283 */     optModel.addActionListener(new ActionListener()
/*      */     {
/*      */       public void actionPerformed(ActionEvent e) {
/*      */         try {
/* 1287 */           TreeCurationTool.this.setEnabled(false);
/* 1288 */           TreeCurationTool.this.modifyTree(TreeCurationTool.this.rm.getDataType().getNumStates());
/* 1289 */           SitePattern[] siteP = new SitePattern[TreeCurationTool.this.tree.length];
/* 1290 */           for (int i = 0; i < siteP.length; i++) {
/* 1291 */             siteP[i] = TreeCurationTool.this.getSitePattern(i, TreeCurationTool.this.rm.getDataType().getNumStates());
/*      */           }
/*      */           
/*      */ 
/* 1295 */           TreeCurationTool.TreeOptimizer lv = new TreeCurationTool.TreeOptimizer(TreeCurationTool.this, siteP, false);
/* 1296 */           double[] min = lv.optimize();
/*      */           
/* 1298 */           TreeCurationTool.this.setEnabled(true);
/* 1299 */           this.val$optModel.setEnabled(false);
/* 1300 */           TreeCurationTool.this.optBranch.setEnabled(true);
/* 1301 */           TreeCurationTool.this.rmParamsLabel.setName(TreeCurationTool.this.getParamsString(TreeCurationTool.this.rm));
/* 1302 */           TreeCurationTool.this.rateParamsLabel.setName(TreeCurationTool.this.getParamsString(TreeCurationTool.this.rates));
/*      */         }
/*      */         catch (Exception e1) {
/* 1305 */           e1.printStackTrace();
/*      */         }
/*      */       }
/* 1308 */     });
/* 1309 */     return optModel;
/*      */   }
/*      */   
/*      */   private ParameterizedTree[] getModifiedTree(int numStates) throws Exception
/*      */   {
/* 1314 */     ParameterizedTree[] trees = new ParameterizedTree[this.tree.length];
/* 1315 */     for (int i = 0; i < this.tree.length; i++) {
/* 1316 */       SitePattern siteP = getSitePattern(i, numStates);
/*      */       
/* 1318 */       if (siteP.getIdCount() == this.tree[i].getIdCount()) return this.tree;
/* 1319 */       if (siteP.getIdCount() > this.tree[i].getIdCount()) {
/* 1320 */         trees[i] = this.protTree[i];
/*      */       }
/*      */       else {
/* 1323 */         Identifier[] ids = new Identifier[siteP.getIdCount()];
/* 1324 */         for (int j = 0; j < ids.length; j++) {
/* 1325 */           ids[j] = siteP.getIdentifier(j);
/*      */         }
/* 1327 */         pal.tree.Node[] nodes = pal.tree.NodeUtils.findByIdentifier(this.tree[i].getRoot(), ids);
/* 1328 */         pal.tree.Tree tr = new SimpleTree(SDI.trim(this.tree[i].getRoot(), nodes));
/*      */         
/* 1330 */         trees[i] = new UnconstrainedTree(tr);
/* 1331 */         transferSpeciesTags(this.tree[i], this.prot[i]);
/*      */       }
/*      */     }
/* 1334 */     return trees;
/*      */   }
/*      */   
/*      */   MouseInputAdapter getMouseInputAdapter(ATVjframe frame, boolean taxon, int k)
/*      */   {
/* 1339 */     new MouseInputAdapter() {
/*      */       forester.tree.Node start;
/*      */       double x_start;
/*      */       
/* 1343 */       public void mousePressed(MouseEvent e) { this.x_start = e.getX();
/*      */         
/* 1345 */         this.start = this.val$frame.getATVpanel().getATVgraphic().findNode(e.getX(), e.getY());
/* 1346 */         System.err.println("xstart " + this.x_start + " " + this.start == null);
/*      */       }
/*      */       
/*      */       public void mouseReleased(MouseEvent e) {
/* 1350 */         try { if (this.start == null) { return;
/*      */           }
/* 1352 */           forester.tree.Node dest = TreeCurationTool.findNode(this.val$frame.getATVpanel().getATVgraphic().getTree(), e.getX(), e.getY());
/* 1353 */           System.err.println((this.start == dest) + " " + (dest == null));
/* 1354 */           if ((dest != null) && (dest == this.start)) { this.val$frame.getATVpanel().getATVgraphic().MouseClicked(e);
/* 1355 */           } else if (dest != null) {
/* 1356 */             System.err.println("moving node " + this.start.getSeqName() + " to " + dest.getSeqName());
/* 1357 */             TreeCurationTool.this.modifyTree(this.val$k, this.start, dest, this.val$taxon);
/* 1358 */             TreeCurationTool.this.optBranch.setEnabled(true);
/* 1359 */             TreeCurationTool.this.optModel.setEnabled(false);
/*      */           }
/*      */           else {
/* 1362 */             double increase = e.getX() - this.x_start;
/* 1363 */             double ratio = TreeCurationTool.getRatio(this.val$frame.getATVpanel().getATVgraphic().getTree());
/*      */             
/* 1365 */             double curr_length = this.start.getXcoord() - this.start.getParent().getXcoord();
/* 1366 */             TreeCurationTool.this.updateDistance(this.val$k, this.val$frame.getATVpanel().getATVgraphic().getTree(), this.start, increase, this.val$taxon);
/*      */           }
/*      */         }
/*      */         catch (Exception e1)
/*      */         {
/* 1371 */           e1.printStackTrace();
/*      */         }
/*      */       }
/*      */     };
/*      */   }
/*      */   
/*      */ 
/*      */   JPanel getOptionPanel()
/*      */   {
/* 1380 */     JComponent[] formattedText = new JComponent[8];
/* 1381 */     JComponent[] textFieldLabel = new JComponent[8];
/* 1382 */     JPanel treeOptions = new JPanel(new BorderLayout());
/* 1383 */     this.rateMatrixType = rateMatrixType();
/* 1384 */     this.treeType = treeType();
/* 1385 */     formattedText[0] = this.rateMatrixType;
/* 1386 */     this.rmParamsLabel = new JLabel(getParamsString(this.rm));
/* 1387 */     formattedText[1] = this.rmParamsLabel;
/* 1388 */     formattedText[2] = gammaRates();
/* 1389 */     this.rateParamsLabel = new JLabel(getParamsString(this.rates));
/* 1390 */     formattedText[3] = this.rateParamsLabel;
/* 1391 */     formattedText[4] = this.treeType;
/* 1392 */     formattedText[5] = includeTreePrior();
/* 1393 */     formattedText[6] = duplicationTime();
/* 1394 */     formattedText[7] = percDeleted();
/* 1395 */     textFieldLabel[0] = new JLabel("RateMatrix: ");
/* 1396 */     textFieldLabel[1] = new JLabel("rate matrix parmams: ");
/* 1397 */     textFieldLabel[2] = new JLabel("no. gamma rates: ");
/* 1398 */     textFieldLabel[3] = new JLabel("rate  parmams: ");
/* 1399 */     textFieldLabel[4] = new JLabel("tree type: ");
/* 1400 */     textFieldLabel[5] = new JLabel("tree prior ");
/* 1401 */     textFieldLabel[6] = new JLabel("duplication time: ");
/* 1402 */     textFieldLabel[7] = new JLabel("perc deleted: ");
/*      */     
/* 1404 */     for (int i = 0; i < formattedText.length; i++) {
/* 1405 */       ((JLabel)textFieldLabel[i]).setLabelFor(formattedText[i]);
/* 1406 */       formattedText[i].setEnabled(true);
/*      */     }
/*      */     
/* 1409 */     GridBagConstraints c = new GridBagConstraints();
/* 1410 */     GridBagLayout gridbag = new GridBagLayout();
/* 1411 */     treeOptions.setLayout(gridbag);
/* 1412 */     addLabelTextRows(textFieldLabel, 
/* 1413 */       formattedText, 
/* 1414 */       gridbag, treeOptions);
/* 1415 */     return treeOptions;
/*      */   }
/*      */   
/*      */   private String getParamsString(Parameterized rm) {
/* 1419 */     double[] d = new double[rm.getNumParameters()];
/* 1420 */     for (int i = 0; i < d.length; i++) {
/* 1421 */       d[i] = rm.getParameter(i);
/*      */     }
/* 1423 */     return Print.toString(d);
/*      */   }
/*      */   
/*      */   private Ensembl.Phigs getPhig(int i) throws Exception {
/* 1427 */     BufferedReader br = new BufferedReader(new FileReader(this.protFasta[i]));
/* 1428 */     Ensembl.Phigs phig = new Ensembl.Phigs(this.dir[i].getName());
/* 1429 */     for (SequenceIterator seqIt = SeqIOTools.readFastaProtein(br); seqIt.hasNext();) {
/* 1430 */       Sequence seq = seqIt.nextSequence();
/* 1431 */       String[] desc = ((String)seq.getAnnotation().getProperty("description")).split("\\s+");
/* 1432 */       phig.genes.add(desc[1].split("=")[1]);
/* 1433 */       for (int j = 0; j < desc.length; j++) {
/* 1434 */         if (desc[j].startsWith("TAXID")) {
/* 1435 */           phig.taxa.add(desc[j].split("=")[1]);
/* 1436 */           break;
/*      */         }
/*      */       }
/*      */     }
/* 1440 */     br.close();
/* 1441 */     return phig;
/*      */   }
/*      */   
/*      */   public JButton getPSILCButton() throws Exception
/*      */   {
/* 1446 */     JButton PSILC = new JButton("Run PSILC");
/* 1447 */     PSILC.addActionListener(new ActionListener() {
/*      */       public void actionPerformed(ActionEvent e) {
/*      */         try {
/* 1450 */           TreeCurationTool.this.setEnabled(false);
/* 1451 */           if (TreeCurationTool.allowedRateMatrices[TreeCurationTool.this.rateMatrixType.getSelectedIndex()].indexOf(":") < 0) throw new Exception("Need to select a compound rate matrix");
/* 1452 */           String[] rateM = TreeCurationTool.allowedRateMatrices[TreeCurationTool.this.rateMatrixType.getSelectedIndex()].split(":");
/*      */           
/* 1454 */           TreeCurationTool.this.modifyTree(4);
/* 1455 */           PseudoManager.protrm = rateM[0].substring(rateM[0].lastIndexOf('.') + 1);
/* 1456 */           PseudoManager.nucrm = rateM[1].substring(rateM[1].lastIndexOf('.') + 1);
/* 1457 */           System.err.println("protrm " + PseudoManager.protrm + " " + PseudoManager.nucrm);
/* 1458 */           for (int i = 0; i < TreeCurationTool.this.dir.length; i++) {
/* 1459 */             Set l = new HashSet();
/* 1460 */             BufferedReader br = new BufferedReader(new FileReader(new File(TreeCurationTool.this.dir[i], "pfamA")));
/* 1461 */             String st = "";
/* 1462 */             while ((st = br.readLine()) != null) {
/* 1463 */               l.add(TreeCurationTool.this.alph.getTokenization("token").parseToken(st.split("\\s+")[0]));
/*      */             }
/* 1465 */             br.close();
/* 1466 */             SitePattern dna_align = TreeCurationTool.this.getSitePattern(i, 4);
/* 1467 */             PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(TreeCurationTool.this.nhx[i])));
/* 1468 */             NodeUtils.printNH(TreeCurationTool.this.tree[i], pw, true, true, true);
/* 1469 */             pw.close();
/* 1470 */             PseudoManager psm = new PseudoManager(TreeCurationTool.this.repository, TreeCurationTool.this.dir[i], l, dna_align, 
/* 1471 */               TreeCurationTool.this.tree[i], TreeCurationTool.this.fTree[i]);
/* 1472 */             psm.run();
/* 1473 */             TreeCurationTool.this.matchStatePosterior = ((double[][][])psm.posteriorProbs.toArray(new double[psm.posteriorProbs.size()][][]));
/* 1474 */             TreeCurationTool.this.matchHMM = ((AlignmentHMM[])psm.hmmList.toArray(new AlignmentHMM[psm.hmmList.size()]));
/* 1475 */             TreeCurationTool.this.alias = psm.alias;
/*      */             
/* 1477 */             TreeCurationTool.this.firePropertyChange("psilc", false, true);
/*      */           }
/* 1479 */           TreeCurationTool.this.setEnabled(true);
/*      */         } catch (Exception e1) {
/* 1481 */           e1.printStackTrace();
/* 1482 */           JOptionPane.showMessageDialog(null, 
/* 1483 */             "Problem running PSILC -  " + e1.getMessage(), 
/* 1484 */             e1.getClass() + "!", 
/* 1485 */             0);
/*      */         }
/*      */       }
/* 1488 */     });
/* 1489 */     return PSILC;
/*      */   }
/*      */   
/*      */   public JButton getPAMLButton() throws Exception {
/* 1493 */     JButton paml = new JButton("Run PAML");
/* 1494 */     paml.addActionListener(new ActionListener() {
/*      */       public void actionPerformed(ActionEvent e) {
/* 1496 */         for (int i = 0; i < TreeCurationTool.this.tree.length; i++) {
/*      */           try
/*      */           {
/* 1499 */             TreeCurationTool.this.setEnabled(false);
/* 1500 */             TreeCurationTool.this.modifyTree(4);
/* 1501 */             SitePattern dna_align = TreeCurationTool.this.getSitePattern(i, 4);
/*      */             
/* 1503 */             PAML pam = new PAML(dna_align, TreeCurationTool.this.tree[i]);
/* 1504 */             TreeCurationTool.this.firePropertyChange("paml", false, true);
/* 1505 */             TreeCurationTool.this.setEnabled(true);
/*      */           } catch (Exception e1) {
/* 1507 */             e1.printStackTrace();
/* 1508 */             JOptionPane.showMessageDialog(null, 
/* 1509 */               "Problem running PAML " + e1.getMessage(), 
/* 1510 */               "Exception!", 
/* 1511 */               0);
/*      */           }
/*      */         }
/*      */       }
/* 1515 */     });
/* 1516 */     return paml;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   JPanel getPSILCOptionPanel()
/*      */   {
/* 1526 */     JPanel psilcOptions = new JPanel();
/* 1527 */     JComponent[] formattedText = new JComponent[7 + NodeProbabilityCalculator.SelectionModel.fromStates.length];
/* 1528 */     JComponent[] textFieldLabel = new JComponent[7 + NodeProbabilityCalculator.SelectionModel.fromStates.length];
/* 1529 */     this.blueGreen = blueGreenColor(true);
/* 1530 */     formattedText[0] = this.blueGreen;
/* 1531 */     this.blueRed = blueGreenColor(false);
/* 1532 */     formattedText[1] = this.blueRed;
/* 1533 */     this.coordBox = coords();
/* 1534 */     formattedText[2] = this.coordBox;
/* 1535 */     formattedText[3] = recursive();
/* 1536 */     this.showGreater = showGreater();
/* 1537 */     formattedText[4] = this.showGreater;
/* 1538 */     this.structureThresh = structureThresh();
/* 1539 */     formattedText[5] = this.structureThresh;
/* 1540 */     formattedText[6] = pdbId();
/* 1541 */     for (int i = 0; i < NodeProbabilityCalculator.SelectionModel.fromStates.length; i++) {
/* 1542 */       formattedText[(7 + i)] = selectionParams(i);
/* 1543 */       textFieldLabel[(7 + i)] = new JLabel(NodeProbabilityCalculator.SelectionModel.fromStates[i] + " to " + 
/* 1544 */         Arrays.asList(NodeProbabilityCalculator.SelectionModel.paramNames[i]).toString());
/*      */     }
/*      */     
/*      */ 
/* 1548 */     textFieldLabel[0] = new JLabel("plot blue<-> green colours for: ");
/* 1549 */     textFieldLabel[1] = new JLabel("plot blue<->red colours for: ");
/* 1550 */     textFieldLabel[2] = new JLabel("plot in coords ");
/* 1551 */     textFieldLabel[3] = new JLabel("recursive ");
/* 1552 */     textFieldLabel[4] = new JLabel("show structures ");
/* 1553 */     textFieldLabel[5] = new JLabel("than threshold ");
/* 1554 */     textFieldLabel[6] = new JLabel("pdb id ");
/*      */     
/* 1556 */     for (int i = 0; i < formattedText.length; i++) {
/* 1557 */       ((JLabel)textFieldLabel[i]).setLabelFor(formattedText[i]);
/* 1558 */       formattedText[i].setEnabled(true);
/*      */     }
/*      */     
/* 1561 */     GridBagConstraints c = new GridBagConstraints();
/* 1562 */     GridBagLayout gridbag = new GridBagLayout();
/* 1563 */     psilcOptions.setLayout(gridbag);
/* 1564 */     addLabelTextRows(textFieldLabel, 
/* 1565 */       formattedText, 
/* 1566 */       gridbag, psilcOptions);
/* 1567 */     return psilcOptions;
/*      */   }
/*      */   
/*      */   JPanel getPAMLOptionPanel()
/*      */   {
/* 1572 */     JPanel pamlOptions = new JPanel();
/* 1573 */     JComponent[] formattedText = new JComponent[PAML.pamlparams.length];
/* 1574 */     JComponent[] textFieldLabel = new JComponent[PAML.pamlparams.length];
/*      */     
/* 1576 */     for (int i = 0; i < formattedText.length; i++) {
/* 1577 */       formattedText[i] = pamlParams(i);
/* 1578 */       textFieldLabel[i] = new JLabel(PAML.pamlparams[i][0]);
/* 1579 */       formattedText[i].setEnabled(true);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/* 1584 */     GridBagConstraints c = new GridBagConstraints();
/* 1585 */     GridBagLayout gridbag = new GridBagLayout();
/* 1586 */     pamlOptions.setLayout(gridbag);
/* 1587 */     addLabelTextRows(textFieldLabel, 
/* 1588 */       formattedText, 
/* 1589 */       gridbag, pamlOptions);
/* 1590 */     return pamlOptions;
/*      */   }
/*      */   
/*      */   private RateMatrix getRateMatrix(String type) throws Exception {
/* 1594 */     if (type.indexOf(":") < 0) {
/* 1595 */       int rm_index = -1;
/* 1596 */       for (int i = 0; i < allowedRateMatrices.length; i++) {
/* 1597 */         if (allowedRateMatrices[i].endsWith(type)) {
/* 1598 */           rm_index = i;
/* 1599 */           break;
/*      */         }
/*      */       }
/* 1602 */       if (rm_index == -1) { throw new Exception("did not find rate matrix for " + type);
/*      */       }
/* 1604 */       double[][] eq_freqs = new double[this.tree.length][0];
/* 1605 */       for (int i = 0; i < this.tree.length; i++) {
/* 1606 */         SitePattern siteP = getSitePattern(i, rmNumStates[rm_index]);
/* 1607 */         eq_freqs[i] = AlignmentUtils.estimateFrequencies(siteP);
/*      */       }
/* 1609 */       double[] freq = average(eq_freqs);
/* 1610 */       Class clazz = Class.forName(allowedRateMatrices[rm_index]);
/* 1611 */       int num_params = rmNumParams[rm_index];
/* 1612 */       if (num_params == 0) {
/* 1613 */         return (RateMatrix)clazz.getConstructor(new Class[] { freq.getClass() }).newInstance(new Object[] { freq });
/*      */       }
/*      */       
/* 1616 */       double[] params = new double[num_params];
/* 1617 */       return (RateMatrix)clazz.getConstructor(new Class[] { params.getClass(), freq.getClass() }).newInstance(new Object[] { params, freq });
/*      */     }
/*      */     
/*      */ 
/* 1621 */     String[] type1 = type.split(":");
/* 1622 */     return new DomainCodonModel(getRateMatrix(type1[0]), getRateMatrix(type1[1]));
/*      */   }
/*      */   
/*      */   private SitePattern getSitePattern(int i, int num)
/*      */   {
/*      */     try
/*      */     {
/* 1629 */       if (num == 4)
/*      */       {
/* 1631 */         if (this.dna[i] == null)
/*      */         {
/* 1633 */           if ((!this.dnaAlignF[i].exists()) || (this.dnaAlignF[i].length() == 0L))
/*      */           {
/*      */ 
/*      */ 
/*      */ 
/* 1638 */             if ((!this.dnaFasta[i].exists()) || (this.dnaFasta[i].length() == 0L)) {
/* 1639 */               Ensembl.printPhigs(getPhig(i), this.dir[i], Ensembl.getDBNames(this.phigsdb, null), null, this.alph);
/*      */             }
/* 1641 */             BufferedInputStream is = new BufferedInputStream(new FileInputStream(this.dnaFasta[i]));
/* 1642 */             SequenceDB seqDB = 
/* 1643 */               SeqIOTools.readFasta(is, DNATools.getDNA());
/* 1644 */             is.close();
/* 1645 */             Annotation annot = seqDB.sequenceIterator().nextSequence().getAnnotation();
/* 1646 */             if ((!annot.containsProperty("description")) || 
/* 1647 */               (((String)annot.getProperty("description")).indexOf("TAXID") < 0))
/*      */             {
/* 1649 */               for (SequenceIterator seqIt = seqDB.sequenceIterator(); seqIt.hasNext();) {
/* 1650 */                 Sequence seq = seqIt.nextSequence();
/* 1651 */                 String str = Ensembl.getTaxaId(seq.getName());
/*      */               }
/*      */             }
/*      */             
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1659 */             File protAlign = new File(this.dir[i], "seed.pep.mfa");
/* 1660 */             if ((!protAlign.exists()) || (protAlign.length() == 0L)) {
/* 1661 */               File fastaProt = new File(this.dir[i], "seed.pep.fa");
/* 1662 */               AlignTools.writeTranslatedFasta(this.dnaFasta[i], fastaProt);
/* 1663 */               AlignTools.muscle(fastaProt, protAlign);
/*      */             }
/*      */             
/*      */ 
/* 1667 */             Alignment dna_align = AlignTools.inferDNAAlignmentFromProteinAlignment(this.dnaFasta[i], protAlign);
/* 1668 */             PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(this.dnaAlignF[i])));
/* 1669 */             AlignTools.printMFA(dna_align, pw);
/* 1670 */             pw.close();
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
/* 1683 */             this.dna[i] = SitePattern.getSitePattern(dna_align);
/*      */           }
/*      */           else
/*      */           {
/* 1687 */             this.dna[i] = SitePattern.getSitePattern(AlignTools.readMFA(this.dnaAlignF[i], Nucleotides.DEFAULT_INSTANCE));
/*      */             
/* 1689 */             if (this.codon[i] == null) {
/* 1690 */               this.codon[i] = SitePattern.getSitePattern(AlignUtils.getCodonAlignmentFromDNA(this.dna[i]));
/*      */             }
/* 1692 */             if ((this.prot[i] != null) && (this.dna[i].getSiteCount() != this.prot[i].getSiteCount())) {
/* 1693 */               System.err.println("adjusting length of dna alignment");
/* 1694 */               StrippedAlignment newCodon = new StrippedAlignment(this.codon[i]);
/* 1695 */               newCodon.setDataType(this.codon[i].getDataType());
/* 1696 */               int l = 0;
/* 1697 */               for (int k = 0; k < newCodon.getSiteCount(); k++) {
/* 1698 */                 if (matches(newCodon, k, this.prot[i], l)) {
/* 1699 */                   l++;
/*      */                 }
/*      */                 else {
/* 1702 */                   newCodon.dropSite(k);
/*      */                 }
/*      */               }
/* 1705 */               this.dna[i] = SitePattern.getSitePattern(AlignUtils.getDNAAlignmentFromCodons(newCodon));
/* 1706 */               this.codon[i] = SitePattern.getSitePattern(newCodon);
/*      */             }
/*      */           }
/*      */         }
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
/* 1751 */         return this.dna[i];
/*      */       }
/* 1753 */       if (num == 20) {
/* 1754 */         if (this.prot[i] == null) {
/* 1755 */           if (this.dna[i] == null) {
/* 1756 */             this.prot[i] = SitePattern.getSitePattern(AlignTools.readMFA(this.protAlignF[i], AminoAcids.DEFAULT_INSTANCE));
/*      */           }
/*      */           else {
/* 1759 */             this.codon[i] = SitePattern.getSitePattern(AlignUtils.getCodonAlignmentFromDNA(this.dna[i]));
/* 1760 */             this.prot[i] = SitePattern.getSitePattern(AlignUtils.translate(this.codon[i]));
/*      */           }
/*      */         }
/*      */         
/* 1764 */         return this.prot[i];
/*      */       }
/*      */       
/* 1767 */       if (this.codon[0] == null) {
/* 1768 */         SitePattern innerdna = getSitePattern(i, 4);
/*      */         
/* 1770 */         this.codon[i] = SitePattern.getSitePattern(AlignUtils.getCodonAlignmentFromDNA(innerdna));
/*      */       }
/*      */       
/* 1773 */       return this.codon[i];
/*      */     }
/*      */     catch (Exception exc) {
/* 1776 */       exc.printStackTrace(); }
/* 1777 */     return null;
/*      */   }
/*      */   
/*      */   private ParameterizedTree getTaxonomy() throws Exception {
/* 1781 */     File nodedmp = new File(this.repository, "nodes.dmp");
/* 1782 */     if ((!nodedmp.exists()) || (nodedmp.length() == 0L)) {
/* 1783 */       System.err.println("to get a species tree, you must have the ncbi taxonomy files nodes.dmp and namse.dmp in the --repository directory");
/* 1784 */       System.err.println("you then need to specify a TAXID=9606 (for example) in the description line of the dna fasta file");
/* 1785 */       return null;
/*      */     }
/* 1787 */     if (this.taxonTree == null) {
/* 1788 */       if ((this.taxonomy.exists()) && (this.taxonomy.length() > 0L)) {
/* 1789 */         this.taxonTree = new UnconstrainedTree(new ReadTree(this.taxonomy.getAbsolutePath()));
/*      */       }
/*      */       else
/*      */       {
/* 1793 */         Set identifiers = new HashSet();
/* 1794 */         for (int i = 0; i < this.prot.length; i++) {
/* 1795 */           for (int j = 0; j < this.prot[i].getIdCount(); j++) {
/* 1796 */             identifiers.add(this.prot[i].getIdentifier(j));
/* 1797 */             if (((AttributeIdentifier)this.prot[i].getIdentifier(j)).getAttribute("S") == null) { return null;
/*      */             }
/*      */           }
/*      */         }
/* 1801 */         pal.tree.Tree tree1 = 
/* 1802 */           new TaxonomyTree(nodedmp, new File(this.repository, "names.dmp"), "Eukaryota");
/*      */         
/* 1804 */         tree1 = SDI.trimSpeciesTree(new SimpleIdGroup((Identifier[])identifiers.toArray(new Identifier[identifiers.size()])), tree1.getRoot());
/*      */         
/* 1806 */         SDI.fakeLength(tree1.getRoot());
/* 1807 */         PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(this.taxonomy)));
/* 1808 */         NodeUtils.printNH(tree1, pw, true, true, true);
/* 1809 */         pw.close();
/* 1810 */         this.taxonTree = new UnconstrainedTree(tree1);
/*      */       }
/* 1812 */       this.taxonOrig = this.taxonTree;
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
/*      */ 
/* 1825 */     return this.taxonTree;
/*      */   }
/*      */   
/*      */   public forester.tree.Tree getTree(int i) {
/* 1829 */     return this.fTree[i];
/*      */   }
/*      */   
/*      */   private void getTree(String type)
/*      */     throws Exception
/*      */   {
/* 1835 */     if (type.indexOf(':') > 0) { pal.tree.Tree taxonBase;
/*      */       pal.tree.Tree taxonBase;
/* 1837 */       if ((this.taxonTree instanceof ParameterizedTree)) {
/* 1838 */         taxonBase = ((ParameterizedTree.ParameterizedTreeBase)this.taxonTree).getBaseTree();
/*      */       }
/*      */       else {
/* 1841 */         taxonBase = this.taxonTree;
/*      */       }
/* 1843 */       String type2 = type.split(":")[1];
/* 1844 */       int tree_index = -1;
/* 1845 */       for (int i = 0; i < allowedTreeType.length; i++) {
/* 1846 */         if (allowedTreeType[i].endsWith(type2)) {
/* 1847 */           tree_index = i;
/* 1848 */           break;
/*      */         }
/*      */       }
/* 1851 */       if (tree_index == -1) throw new Exception("did not find rate matrix for " + type2);
/* 1852 */       Class clazz = Class.forName(allowedTreeType[tree_index]);
/* 1853 */       this.taxonTree = ((ParameterizedTree)clazz.getConstructor(new Class[] { Class.forName("pal.tree.Tree") }).newInstance(new Object[] { taxonBase }));
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 1859 */     for (int j = 0; j < this.tree.length; j++)
/*      */     {
/* 1861 */       pal.tree.Tree base = ((ParameterizedTree.ParameterizedTreeBase)this.tree[j]).getBaseTree();
/* 1862 */       if (type.startsWith("lc1.treefam.GeneTree")) {
/* 1863 */         if (((AttributeIdentifier)this.tree[j].getExternalNode(0).getIdentifier()).getAttribute("S") == null) throw new NullPointerException();
/* 1864 */         System.err.println("tax params " + getParamsString(this.taxonTree));
/* 1865 */         SDI sdi = new SDI(this.taxonTree, base);
/* 1866 */         this.tree[j] = new GeneTree(sdi, base, "GeneTree", this.mean_dupl_time, this.perc_deleted);
/*      */ 
/*      */ 
/*      */       }
/*      */       else
/*      */       {
/*      */ 
/* 1873 */         int tree_index = -1;
/* 1874 */         for (int i = 0; i < allowedTreeType.length; i++) {
/* 1875 */           if (allowedTreeType[i].endsWith(type)) {
/* 1876 */             tree_index = i;
/* 1877 */             break;
/*      */           }
/*      */         }
/* 1880 */         if (tree_index == -1) throw new Exception("did not find rate matrix for " + type);
/* 1881 */         Class clazz = Class.forName(allowedTreeType[tree_index]);
/* 1882 */         this.tree[j] = ((ParameterizedTree)clazz.getConstructor(new Class[] { Class.forName("pal.tree.Tree") }).newInstance(new Object[] { base }));
/*      */       }
/*      */       
/* 1885 */       updateForesterTree(false, j);
/*      */       
/* 1887 */       if (this.taxonTree != null) updateForesterTree(true, -1);
/*      */     }
/*      */   }
/*      */   
/*      */   String getTreeType() {
/* 1892 */     return 
/* 1893 */       this.tree[0].getClass().getName() + (((this.taxonTree instanceof Parameterized)) && ((this.tree[0] instanceof GeneTree)) ? ":" + this.taxonTree.getClass().getName() : "");
/*      */   }
/*      */   
/*      */   private boolean matches(Alignment codon, int k, Alignment prot, int l) {
/* 1897 */     DataType dt = codon.getDataType();
/* 1898 */     DataType amino = prot.getDataType();
/* 1899 */     for (int i = 0; i < codon.getIdCount(); i++) {
/* 1900 */       char c = codon.getData(i, k);
/*      */       
/* 1902 */       int index = prot.whichIdNumber(codon.getIdentifier(i).getName());
/* 1903 */       char p = prot.getData(index, l);
/* 1904 */       if ((dt.getState(c) < 0) || (dt.getState(c) >= 64)) {
/* 1905 */         if ((!amino.isGapChar(p)) || (!amino.isUnknownChar(p))) {
/* 1906 */           return false;
/*      */         }
/*      */         
/*      */       }
/* 1910 */       else if (p != this.ct.getAminoAcidCharFromCodonIndex(dt.getState(c))) { return false;
/*      */       }
/*      */     }
/* 1913 */     return true;
/*      */   }
/*      */   
/*      */   private void modifyTaxonomyTree() throws Exception {
/* 1917 */     Set identifiers = new HashSet();
/* 1918 */     for (int i = 0; i < this.tree.length; i++) {
/* 1919 */       for (int j = 0; j < this.tree[i].getIdCount(); j++) {
/* 1920 */         identifiers.add((AttributeIdentifier)this.tree[i].getIdentifier(j));
/*      */       }
/*      */     }
/* 1923 */     if (this.taxonOrig != null) { this.taxonTree = 
/* 1924 */         new UnconstrainedTree(SDI.trimSpeciesTree(new SimpleIdGroup((Identifier[])identifiers.toArray(new Identifier[identifiers.size()])), this.taxonOrig.getRoot()));
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   private void modifyTree(int numStates)
/*      */     throws Exception
/*      */   {
/* 1932 */     String clazz = getTreeType();
/* 1933 */     ParameterizedTree[] tr = getModifiedTree(numStates);
/* 1934 */     System.arraycopy(tr, 0, this.tree, 0, tr.length);
/* 1935 */     modifyTaxonomyTree();
/* 1936 */     getTree(clazz);
/*      */   }
/*      */   
/*      */   private void modifyTree(int k, forester.tree.Node fromF, forester.tree.Node toF, boolean taxon)
/*      */     throws Exception
/*      */   {
/* 1942 */     resetTreeType();
/* 1943 */     pal.tree.Node from = getNode(taxon ? this.taxonTree : this.tree[k], fromF);
/* 1944 */     pal.tree.Node to = getNode(taxon ? this.taxonTree : this.tree[k], toF);
/* 1945 */     if ((to.isRoot()) || (from.isRoot())) { return;
/*      */     }
/* 1947 */     pal.tree.Node toParent = to.getParent();
/* 1948 */     pal.tree.Node fromParent = from.getParent();
/* 1949 */     pal.tree.Node newNode = new SimpleNode();
/* 1950 */     AttributeIdentifier ai = new AttributeIdentifier("");
/* 1951 */     newNode.setIdentifier(ai);
/* 1952 */     if (taxon) ai.setAttribute("S", ((AttributeIdentifier)from.getIdentifier()).getAttribute("S") + 
/* 1953 */         ((AttributeIdentifier)to.getIdentifier()).getAttribute("S"));
/* 1954 */     for (int i = 0; i < toParent.getChildCount(); i++) {
/* 1955 */       if (toParent.getChild(i).equals(to)) {
/* 1956 */         toParent.removeChild(i);
/* 1957 */         break;
/*      */       }
/*      */     }
/* 1960 */     for (int i = 0; i < fromParent.getChildCount(); i++) {
/* 1961 */       if (fromParent.getChild(i).equals(from)) {
/* 1962 */         fromParent.removeChild(i);
/* 1963 */         break;
/*      */       }
/*      */     }
/* 1966 */     toParent.addChild(newNode);
/* 1967 */     newNode.setParent(toParent);
/* 1968 */     newNode.addChild(from);newNode.addChild(to);
/* 1969 */     from.setParent(newNode);
/* 1970 */     to.setParent(newNode);
/*      */     
/* 1972 */     if (taxon) this.taxonTree = new UnconstrainedTree(new SimpleTree(TaxonomyTree.fixTree(this.taxonTree.getRoot(), true))); else
/* 1973 */       this.tree[k] = new UnconstrainedTree(new SimpleTree(TaxonomyTree.fixTree(this.tree[k].getRoot(), true)));
/* 1974 */     updateForesterTree(taxon, k);
/*      */   }
/*      */   
/*      */   public JFormattedTextField percDeleted() {
/* 1978 */     JFormattedTextField field = new JFormattedTextField(this.perc_deleted);
/* 1979 */     field.setActionCommand("perc deleted");
/* 1980 */     field.setEnabled(true);
/* 1981 */     field.addPropertyChangeListener("value", new PropertyChangeListener() {
/*      */       public void propertyChange(PropertyChangeEvent ae) {
/*      */         try {
/* 1984 */           TreeCurationTool.this.perc_deleted = Double.parseDouble((String)this.val$field.getValue());
/* 1985 */           TreeCurationTool.this.optModel.setEnabled(false);
/* 1986 */           TreeCurationTool.this.optBranch.setEnabled(true);
/*      */         }
/*      */         catch (Exception exc)
/*      */         {
/* 1990 */           exc.printStackTrace();
/*      */         }
/*      */       }
/* 1993 */     });
/* 1994 */     return field;
/*      */   }
/*      */   
/*      */ 
/*      */   public JFormattedTextField pamlParams(int k)
/*      */   {
/* 2000 */     PAML.initialise();
/* 2001 */     JFormattedTextField field = new JFormattedTextField(PAML.pamlparams[k][1]);
/* 2002 */     field.setToolTipText(PAML.pamlparams[k][2]);
/* 2003 */     field.setEnabled(true);
/* 2004 */     field.addPropertyChangeListener("value", new PropertyChangeListener() {
/*      */       public void propertyChange(PropertyChangeEvent ae) {
/*      */         try {
/* 2007 */           PAML.pamlparams[this.val$k][1] = ((String)this.val$field.getValue());
/*      */         }
/*      */         catch (Exception exc) {
/* 2010 */           exc.printStackTrace();
/*      */         }
/*      */         
/*      */       }
/* 2014 */     });
/* 2015 */     return field;
/*      */   }
/*      */   
/*      */ 
/*      */   public JComboBox blueGreenColor(boolean blueGreen)
/*      */   {
/* 2021 */     String key = "Color branches";
/*      */     
/* 2023 */     JComboBox field = new JComboBox(this.allowedBranchColors);
/* 2024 */     field.setSelectedIndex(this.branchColorIndex);
/* 2025 */     field.setActionCommand(key);
/* 2026 */     field.setEnabled(true);
/* 2027 */     field.addActionListener(new ActionListener() {
/*      */       public void actionPerformed(ActionEvent ae) {
/*      */         try {
/* 2030 */           TreeCurationTool.this.branchColorIndex = this.val$field.getSelectedIndex();
/* 2031 */           for (int i = 0; i < TreeCurationTool.this.fTree.length; i++) {
/* 2032 */             TreeCurationTool.switchColor(TreeCurationTool.this.tree[i], TreeCurationTool.this.fTree[i].getRoot(), (String)this.val$field.getSelectedItem(), this.val$blueGreen);
/* 2033 */             TreeCurationTool.this.firePropertyChange("repaint", -1, i);
/*      */           }
/*      */         }
/*      */         catch (Exception exc) {
/* 2037 */           exc.printStackTrace();
/*      */         }
/*      */         
/*      */       }
/* 2041 */     });
/* 2042 */     return field;
/*      */   }
/*      */   
/*      */ 
/*      */   public JComboBox showGreater()
/*      */   {
/* 2048 */     JComboBox field = new JComboBox(greaterThanOpts);
/* 2049 */     field.setSelectedIndex(this.branchColorIndex);
/*      */     
/* 2051 */     field.setEnabled(true);
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
/* 2067 */     return field;
/*      */   }
/*      */   
/*      */   public JComboBox rateMatrixType()
/*      */   {
/* 2072 */     String key = "RateMatrix";
/* 2073 */     String value = this.rm.getClass().getName();
/* 2074 */     if ((this.rm instanceof DomainCodonModel)) {
/* 2075 */       value = 
/* 2076 */         ((DomainCodonModel)this.rm).getProteinModel().getClass().getName() + ":" + ((DomainCodonModel)this.rm).getDNAModel().getClass().getName();
/*      */     }
/* 2078 */     int index = 0;
/* 2079 */     for (int i = 0; i < allowedRateMatrices.length; i++) {
/* 2080 */       if (allowedRateMatrices[i].equals(value)) {
/* 2081 */         index = i;
/* 2082 */         break;
/*      */       }
/*      */     }
/* 2085 */     JComboBox field = new JComboBox(allowedRateMatrices);
/* 2086 */     field.setSelectedIndex(index);
/*      */     
/* 2088 */     field.setActionCommand(key);
/* 2089 */     field.setEnabled(true);
/* 2090 */     field.addActionListener(new ActionListener() {
/*      */       public void actionPerformed(ActionEvent ae) {
/*      */         try {
/* 2093 */           TreeCurationTool.this.rm = TreeCurationTool.this.getRateMatrix((String)this.val$field.getSelectedItem());
/* 2094 */           System.err.println("changing rate matrix to " + TreeCurationTool.this.rm.getClass());
/* 2095 */           TreeCurationTool.this.optModel.setEnabled(true);
/* 2096 */           TreeCurationTool.this.optBranch.setEnabled(false);
/*      */ 
/*      */         }
/*      */         catch (Exception exc)
/*      */         {
/* 2101 */           exc.printStackTrace();
/*      */         }
/*      */       }
/* 2104 */     });
/* 2105 */     return field;
/*      */   }
/*      */   
/*      */   public JButton rebuildNJTree() {
/*      */     try {
/* 2110 */       if ((this.tree[0] != null) && (!(this.tree[0] instanceof UnconstrainedTree))) {
/* 2111 */         resetTreeType();
/*      */       }
/*      */     }
/*      */     catch (Exception exc) {
/* 2115 */       exc.printStackTrace();
/*      */     }
/*      */     
/* 2118 */     JButton rebuild = new JButton("new neighbour joining tree");
/* 2119 */     rebuild.addActionListener(new ActionListener() {
/*      */       public void actionPerformed(ActionEvent e) {
/*      */         try {
/* 2122 */           TreeCurationTool.this.resetTreeType();
/* 2123 */           TreeCurationTool.this.setEnabled(false);
/*      */           
/*      */ 
/*      */ 
/* 2127 */           for (int i = 0; i < TreeCurationTool.this.tree.length; i++) {
/* 2128 */             SitePattern siteP = TreeCurationTool.this.getSitePattern(i, TreeCurationTool.this.rm.getDataType().getNumStates());
/* 2129 */             DistanceMatrix dm = new AlignmentDistanceMatrix(siteP, SubstitutionModel.Utils.createSubstitutionModel(TreeCurationTool.this.rm, TreeCurationTool.this.rates));
/* 2130 */             TreeCurationTool.this.tree[i] = new UnconstrainedTree(
/* 2131 */               new NeighborJoiningTree(dm));
/* 2132 */             TreeCurationTool.this.resetInternalIdentifiers(i);
/* 2133 */             TreeCurationTool.this.updateForesterTree(false, i);
/*      */           }
/* 2135 */           TreeCurationTool.this.getTree("pal.tree.UnconstrainedTree");
/* 2136 */           TreeCurationTool.this.resetTreeType();
/* 2137 */           TreeCurationTool.this.setEnabled(true);
/*      */         } catch (Exception e1) {
/* 2139 */           e1.printStackTrace();
/*      */         }
/*      */       }
/* 2142 */     });
/* 2143 */     return rebuild;
/*      */   }
/*      */   
/*      */   public JComboBox recursive() {
/* 2147 */     JComboBox field = new JComboBox(new String[] { "true", "false" });
/* 2148 */     field.setActionCommand("recurisve");
/* 2149 */     field.setSelectedIndex(0);
/* 2150 */     field.setEnabled(true);
/* 2151 */     field.addActionListener(new ActionListener() {
/*      */       public void actionPerformed(ActionEvent ae) {
/*      */         try {
/* 2154 */           NodeProbabilityCalculator.recursive = this.val$field.getSelectedItem().equals("true");
/* 2155 */           System.err.println("setting recursive - " + NodeProbabilityCalculator.recursive);
/* 2156 */           TreeCurationTool.this.optModel.setEnabled(false);
/* 2157 */           TreeCurationTool.this.optBranch.setEnabled(true);
/*      */         }
/*      */         catch (Exception exc)
/*      */         {
/* 2161 */           exc.printStackTrace();
/*      */         }
/*      */       }
/* 2164 */     });
/* 2165 */     return field;
/*      */   }
/*      */   
/*      */ 
/*      */   public JComboBox includeTreePrior()
/*      */   {
/* 2171 */     JComboBox field = new JComboBox(treePriorOptions);
/* 2172 */     field.setActionCommand("include tree prior");
/* 2173 */     field.setSelectedIndex(0);
/* 2174 */     field.setEnabled(true);
/* 2175 */     field.addActionListener(new ActionListener() {
/*      */       public void actionPerformed(ActionEvent ae) {
/*      */         try {
/* 2178 */           TreeCurationTool.this.includePrior = this.val$field.getSelectedIndex();
/*      */         }
/*      */         catch (Exception exc) {
/* 2181 */           exc.printStackTrace();
/*      */         }
/*      */       }
/* 2184 */     });
/* 2185 */     return field;
/*      */   }
/*      */   
/*      */   private void resetInternalIdentifiers(int k) throws Exception {
/* 2189 */     for (int i = 0; i < this.tree[k].getInternalNodeCount(); i++) {
/* 2190 */       if (!(this.tree[k].getInternalNode(i).getIdentifier() instanceof AttributeIdentifier))
/* 2191 */         this.tree[k].getInternalNode(i).setIdentifier(new AttributeIdentifier(this.tree[k].getInternalNode(i).getIdentifier().getName()));
/*      */     }
/* 2193 */     updateForesterTree(false, k);
/*      */   }
/*      */   
/* 2196 */   public void resetTreeType() throws Exception { getTree("UnconstrainedTree");
/* 2197 */     this.treeType.setSelectedIndex(1);
/* 2198 */     for (int i = 0; i < this.tree.length; i++) {
/* 2199 */       resetInternalIdentifiers(i);
/*      */     }
/* 2201 */     this.optBranch.setEnabled(true);
/* 2202 */     this.optModel.setEnabled(false);
/*      */   }
/*      */   
/*      */   public JFormattedTextField pdbId()
/*      */   {
/* 2207 */     JFormattedTextField field = new JFormattedTextField("");
/* 2208 */     field.setEnabled(true);
/* 2209 */     field.addPropertyChangeListener("value", new PropertyChangeListener() {
/*      */       public void propertyChange(PropertyChangeEvent ae) {
/*      */         try {
/* 2212 */           TreeCurationTool.this.setEnabled(false);
/* 2213 */           String id = (String)this.val$field.getValue();
/* 2214 */           String c = "*";
/* 2215 */           if (id.indexOf("_") >= 0) {
/* 2216 */             c = id.substring(0, id.length() - 1);
/* 2217 */             id = id.substring(0, id.indexOf("_"));
/*      */           }
/*      */           
/* 2220 */           for (int i = 0; i < TreeCurationTool.this.dir.length; i++) {
/* 2221 */             File outFa = new File(TreeCurationTool.this.dir[i], id + ".fa");
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
/* 2237 */             File pdb = new File(TreeCurationTool.this.dir[i], id);
/* 2238 */             TreeCurationTool.this.pdbview[i] = new PDBView(pdb, c, outFa, TreeCurationTool.this.getSitePattern(i, 20));
/* 2239 */             TreeCurationTool.this.setEnabled(true);
/*      */           }
/*      */           
/*      */         }
/*      */         catch (Exception e1)
/*      */         {
/* 2245 */           e1.printStackTrace();
/* 2246 */           JOptionPane.showMessageDialog(null, 
/* 2247 */             "Problem getting pdb  -  " + e1.getMessage(), 
/* 2248 */             e1.getClass() + "!", 
/* 2249 */             0);
/*      */         }
/*      */         
/*      */       }
/* 2253 */     });
/* 2254 */     return field;
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
/*      */   public JFormattedTextField selectionParams(int k)
/*      */   {
/* 2267 */     JFormattedTextField field = new JFormattedTextField(Print.toString(NodeProbabilityCalculator.SelectionModel.getVals(k)));
/* 2268 */     field.setActionCommand(NodeProbabilityCalculator.SelectionModel.fromStates[k] + " " + 
/* 2269 */       Arrays.asList(NodeProbabilityCalculator.SelectionModel.paramNames[k]).toString());
/* 2270 */     field.setEnabled(true);
/* 2271 */     field.addPropertyChangeListener("value", new PropertyChangeListener() {
/*      */       public void propertyChange(PropertyChangeEvent ae) {
/*      */         try {
/* 2274 */           String[] params = ((String)this.val$field.getValue()).trim().split("\\s+");
/* 2275 */           System.err.println(this.val$field.getValue());
/* 2276 */           System.err.println(Arrays.asList(params));
/* 2277 */           int length = params.length;
/* 2278 */           for (int i = 0; i < length; i++) {
/* 2279 */             NodeProbabilityCalculator.SelectionModel.setVals(this.val$k, i, 
/* 2280 */               Double.parseDouble(params[i]));
/*      */           }
/*      */         }
/*      */         catch (Exception exc) {
/* 2284 */           exc.printStackTrace();
/*      */         }
/*      */         
/*      */       }
/* 2288 */     });
/* 2289 */     return field;
/*      */   }
/*      */   
/*      */   public JFormattedTextField structureThresh()
/*      */   {
/* 2294 */     JFormattedTextField field = new JFormattedTextField("0.5");
/* 2295 */     field.setEnabled(true);
/*      */     
/* 2297 */     return field;
/*      */   }
/*      */   
/*      */   public static void switchColor(pal.tree.Tree tr, forester.tree.Node node, String color, boolean green)
/*      */   {
/* 2302 */     if (!node.isExternal()) {
/* 2303 */       switchColor(tr, node.getChild1(), color, green);
/* 2304 */       switchColor(tr, node.getChild2(), color, green);
/*      */     }
/* 2306 */     AttributeNode node1 = (AttributeNode)getNode(tr, node);
/* 2307 */     if (node1 == null) throw new NullPointerException("node1 is null");
/* 2308 */     if (node1.isRoot()) return;
/* 2309 */     Double col = (Double)node1.getAttribute(color);
/* 2310 */     if (green)
/*      */     {
/* 2312 */       node.setLnLonParentBranch((color == "none") || (col == null) ? 0.0F : col.floatValue());
/*      */     }
/*      */     else {
/* 2315 */       node.setSecondLnL((color == "none") || (col == null) ? null : col);
/*      */     }
/*      */   }
/*      */   
/*      */   private void switchCoords(forester.tree.Node node, int[] alias) {
/* 2320 */     if (!node.isExternal()) {
/* 2321 */       switchCoords(node.getChild1(), alias);
/* 2322 */       switchCoords(node.getChild2(), alias);
/*      */     }
/* 2324 */     node.setAlias(alias);
/*      */   }
/*      */   
/*      */   public JComboBox treeType()
/*      */   {
/* 2329 */     String key = "Tree type";
/* 2330 */     String value = getTreeType();
/* 2331 */     int index = -1;
/* 2332 */     for (int i = 0; i < allowedTreeType.length; i++) {
/* 2333 */       if (allowedTreeType[i].equals(value)) {
/* 2334 */         index = i;
/* 2335 */         break;
/*      */       }
/*      */     }
/* 2338 */     JComboBox field = new JComboBox(allowedTreeType);
/* 2339 */     field.setSelectedIndex(index);
/* 2340 */     field.setActionCommand(key);
/* 2341 */     field.setEnabled(true);
/* 2342 */     field.addActionListener(new ActionListener() {
/*      */       public void actionPerformed(ActionEvent ae) {
/*      */         try {
/* 2345 */           TreeCurationTool.this.getTree((String)this.val$field.getSelectedItem());
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2352 */           for (int i = 0; i < TreeCurationTool.this.tree.length; i++) {
/* 2353 */             if (TreeCurationTool.this.tree[i].getIdCount() == TreeCurationTool.this.protTree[i].getIdCount()) {
/* 2354 */               TreeCurationTool.this.protTree[i] = TreeCurationTool.this.tree[i];
/*      */             }
/*      */           }
/*      */           
/*      */ 
/* 2359 */           TreeCurationTool.this.optModel.setEnabled(false);
/* 2360 */           TreeCurationTool.this.optBranch.setEnabled(true);
/*      */         }
/*      */         catch (Exception exc) {
/* 2363 */           exc.printStackTrace();
/* 2364 */           System.err.println("Warning " + exc.getMessage());
/*      */         }
/*      */       }
/* 2367 */     });
/* 2368 */     return field;
/*      */   }
/*      */   
/*      */   private void updateDistance(int i, forester.tree.Tree treeF, forester.tree.Node fromF, double dist, boolean taxon) throws Exception
/*      */   {
/* 2373 */     resetTreeType();
/* 2374 */     pal.tree.Node from = getNode(taxon ? this.taxonTree : this.tree[i], fromF);
/* 2375 */     from.setBranchLength(from.getBranchLength() + getRatio(treeF) * dist);
/* 2376 */     updateForesterTree(taxon, i);
/*      */   }
/*      */   
/*      */   private void updateForesterTree(boolean taxon, int i) throws Exception {
/* 2380 */     if (taxon) {
/* 2381 */       if (this.taxonTree != null) {
/* 2382 */         forester.tree.Tree oldTree = this.fTaxonTree;
/* 2383 */         this.fTaxonTree = convert(this.taxonTree);
/* 2384 */         firePropertyChange("taxon", oldTree, this.fTaxonTree);
/*      */       }
/*      */     }
/*      */     else
/*      */     {
/* 2389 */       forester.tree.Tree[] oldTree = new forester.tree.Tree[this.fTree.length];
/* 2390 */       oldTree[i] = this.fTree[i];
/*      */       
/* 2392 */       this.fTree[i] = convert(this.tree[i]);
/*      */       
/* 2394 */       System.err.println("fired change");
/* 2395 */       firePropertyChange("tree", oldTree, this.fTree);
/*      */     }
/*      */   }
/*      */   
/*      */   private void updateTaxonTree(forester.tree.Tree treeF) throws Exception {
/* 2400 */     resetTreeType();
/* 2401 */     System.err.println("changing taxon tree ");
/* 2402 */     this.taxonTree = new UnconstrainedTree(convert(treeF));
/* 2403 */     mapTaxonNodes(this.taxonTree.getRoot());
/* 2404 */     updateForesterTree(true, -1);
/*      */   }
/*      */   
/* 2407 */   private void updateTree(forester.tree.Tree treeF, int i) throws Exception { resetTreeType();
/* 2408 */     this.tree[i] = new UnconstrainedTree(convert(treeF));
/* 2409 */     transferSpeciesTags(this.tree[i], this.prot[i]);
/* 2410 */     this.fTree[i] = treeF;
/* 2411 */     resetInternalIdentifiers(i);
/*      */   }
/*      */ }


/* Location:              /home/lachlan/Desktop/psilc1.21/psilc.jar!/lc1/treefam/TreeCurationTool.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */