/*      */ package lc1.domains;
/*      */ 
/*      */ import gnu.trove.TObjectIntHashMap;
/*      */ import gnu.trove.TObjectIntProcedure;
/*      */ import java.awt.Color;
/*      */ import java.awt.Container;
/*      */ import java.awt.Dimension;
/*      */ import java.io.BufferedWriter;
/*      */ import java.io.File;
/*      */ import java.io.FileInputStream;
/*      */ import java.io.FileOutputStream;
/*      */ import java.io.FileWriter;
/*      */ import java.io.ObjectInputStream;
/*      */ import java.io.ObjectOutputStream;
/*      */ import java.io.PrintStream;
/*      */ import java.io.PrintWriter;
/*      */ import java.io.Serializable;
/*      */ import java.lang.ref.SoftReference;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Arrays;
/*      */ import java.util.Collections;
/*      */ import java.util.Comparator;
/*      */ import java.util.HashMap;
/*      */ import java.util.Iterator;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.NoSuchElementException;
/*      */ import java.util.Set;
/*      */ import java.util.SortedMap;
/*      */ import java.util.TreeMap;
/*      */ import javax.swing.JFrame;
/*      */ import lc1.domainseq.Domain;
/*      */ import lc1.domainseq.Domain.MagicTemplate;
/*      */ import lc1.domainseq.DomainList;
/*      */ import lc1.domainseq.FeatureUtils;
/*      */ import lc1.pfam.DomainAlphabet;
/*      */ import lc1.pfam.IndexedPfamDB;
/*      */ import lc1.pfam.PfamAlphabet;
/*      */ import lc1.pfam.SpeciesAlphabet;
/*      */ import lc1.pfam.SpeciesAlphabet.SpeciesSymbol;
/*      */ import lc1.util.SheetIO;
/*      */ import org.apache.commons.cli.CommandLine;
/*      */ import org.apache.commons.cli.OptionBuilder;
/*      */ import org.apache.commons.cli.Options;
/*      */ import org.apache.commons.cli.Parser;
/*      */ import org.apache.commons.cli.PosixParser;
/*      */ import org.biojava.bio.Annotation;
/*      */ import org.biojava.bio.BioException;
/*      */ import org.biojava.bio.seq.Sequence;
/*      */ import org.biojava.bio.seq.SequenceIterator;
/*      */ import org.biojava.bio.seq.io.SymbolTokenization;
/*      */ import org.biojava.bio.symbol.Symbol;
/*      */ import org.biojava.bio.symbol.SymbolList;
/*      */ import org.jfree.chart.ChartPanel;
/*      */ import org.jfree.chart.JFreeChart;
/*      */ import org.jfree.chart.StandardLegend;
/*      */ import org.jfree.chart.axis.CategoryAxis;
/*      */ import org.jfree.chart.axis.NumberAxis;
/*      */ import org.jfree.chart.axis.ValueAxis;
/*      */ import org.jfree.chart.labels.StandardCategoryToolTipGenerator;
/*      */ import org.jfree.chart.plot.CategoryPlot;
/*      */ import org.jfree.chart.plot.PlotOrientation;
/*      */ import org.jfree.chart.renderer.LineAndShapeRenderer;
/*      */ import org.jfree.data.DefaultCategoryDataset;
/*      */ import pal.math.MultivariateFunction;
/*      */ import pal.math.MultivariateMinimum;
/*      */ import pal.math.OrthogonalHints;
/*      */ import pal.math.OrthogonalSearch;
/*      */ 
/*      */ 
/*      */ 
/*      */ public class ContextCount
/*      */ {
/*   74 */   static final Options OPTIONS = new Options() {};
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  110 */   public static boolean countEachOccurence = true;
/*      */   
/*  112 */   public static final Parser PARSER = new PosixParser();
/*      */   
/*      */   public static void graph(CommandLine params) throws Exception {
/*  115 */     File repos = new File(params.getOptionValue("repository"));
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  128 */     ContextCount cc = new ContextCount(new File("."), repos, 4, true);
/*  129 */     DomainAlphabet alph = cc.getAlphabet();
/*  130 */     SpeciesAlphabet spec_al = cc.getSpeciesAlphabet();
/*  131 */     SymbolTokenization tokS = spec_al.getTokenization("token");
/*  132 */     Symbol root = spec_al.root();
/*  133 */     Symbol[] spec_syms = { root, tokS.parseToken("2"), tokS.parseToken("2157"), tokS.parseToken("2759") };
/*  134 */     String[] names = { "Root", "Bacteria", "Archaea", "Eukaryota" };
/*  135 */     System.err.println(Arrays.asList(spec_syms));
/*  136 */     String domain = params.getOptionValue("domain");
/*  137 */     SymbolTokenization tokenizer = alph.getTokenization("token");
/*  138 */     Symbol sym = tokenizer.parseToken(domain);
/*  139 */     List l = new ArrayList();
/*  140 */     for (Iterator it = cc.contMap.keySet().iterator(); it.hasNext();) {
/*  141 */       List sl = (List)it.next();
/*  142 */       if ((sl.size() == 2) && (sl.get(sl.size() - 1) == sym)) {
/*  143 */         Frequency freq = (Frequency)cc.contMap.get(sl);
/*  144 */         Frequency background = (Frequency)cc.contMap.get(sl.subList(0, sl.size() - 1));
/*  145 */         double[] count = new double[spec_syms.length];
/*  146 */         for (int i = 0; i < spec_syms.length; i++) {
/*  147 */           count[i] = freq.get(spec_syms[i]);
/*      */           
/*  149 */           System.err.println(spec_syms[i] + " " + count[i]);
/*  150 */           if (count[i] > 0.0D) count[i] /= background.getCount(spec_syms[i]);
/*      */         }
/*  152 */         StringBuffer sb = new StringBuffer();
/*  153 */         for (int i = 0; i < sl.size() - 1; i++) {
/*  154 */           Symbol sy = (Symbol)sl.get(i);
/*  155 */           if (sy == alph.getMagicalState()) sb.append("Begin"); else
/*  156 */             sb.append(sy.getAnnotation().getProperty("pfamA_id").toString());
/*  157 */           sb.append("\t");
/*      */         }
/*      */         
/*  160 */         l.add(new ContextCount.1.ObjDoub(sb.toString(), count));
/*      */       }
/*      */     }
/*  163 */     Collections.sort(l, new Comparator() {
/*      */       public int compare(Object o1, Object o2) {
/*  165 */         double d1 = ((ContextCount.1.ObjDoub)o1).vals[0];
/*  166 */         double d2 = ((ContextCount.1.ObjDoub)o2).vals[0];
/*  167 */         if (d1 == d2) return 0;
/*  168 */         return d1 < d2 ? 1 : -1;
/*      */       }
/*  170 */     });
/*  171 */     double backgroundP = ((Frequency)cc.contMap.get(Arrays.asList(new Symbol[] { sym }))).get(root) / 
/*  172 */       ((Frequency)cc.contMap.get(Arrays.asList(new Symbol[0]))).get(root);
/*      */     
/*  174 */     DefaultCategoryDataset data = new DefaultCategoryDataset();
/*      */     
/*  176 */     for (int ik = 0; (ik < l.size()) && (ik < 40); ik++) {
/*  177 */       ContextCount.1.ObjDoub val = (ContextCount.1.ObjDoub)l.get(ik);
/*  178 */       String name = val.name;
/*  179 */       double[] prob = val.vals;
/*  180 */       for (int i = 1; i < names.length; i++)
/*      */       {
/*      */ 
/*  183 */         double valu = prob[i];
/*      */         
/*  185 */         data.addValue(valu, names[i], name);
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*  190 */     String chart_name = (String)sym.getAnnotation().getProperty("pfamA_id");
/*  191 */     CategoryAxis categoryAxis = new CategoryAxis("Domain");
/*  192 */     ValueAxis valueAxis = new NumberAxis("Probability");
/*  193 */     valueAxis.setAutoRange(false);
/*  194 */     valueAxis.setMinimumAxisValue(0.0D);
/*  195 */     valueAxis.setMaximumAxisValue(1.0D);
/*  196 */     LineAndShapeRenderer renderer = new LineAndShapeRenderer();
/*  197 */     renderer.setDrawLines(true);
/*  198 */     renderer.setDrawShapes(false);
/*  199 */     renderer.setToolTipGenerator(new StandardCategoryToolTipGenerator());
/*  200 */     CategoryPlot plot = new CategoryPlot(data, categoryAxis, valueAxis, renderer);
/*  201 */     plot.setOrientation(PlotOrientation.VERTICAL);
/*  202 */     JFreeChart chart = new JFreeChart("Probability of " + chart_name + " following domain", JFreeChart.DEFAULT_TITLE_FONT, plot, true);
/*      */     
/*  204 */     StandardLegend legend = (StandardLegend)chart.getLegend();
/*  205 */     legend.setDisplaySeriesShapes(true);
/*  206 */     legend.setShapeScaleX(1.5D);
/*  207 */     legend.setShapeScaleY(1.5D);
/*  208 */     legend.setDisplaySeriesLines(true);
/*      */     
/*  210 */     chart.setBackgroundPaint(Color.white);
/*      */     
/*  212 */     plot.setBackgroundPaint(Color.WHITE);
/*  213 */     plot.setRangeGridlinePaint(Color.GRAY);
/*  214 */     CategoryAxis domainAxis = plot.getDomainAxis();
/*  215 */     domainAxis.setVerticalCategoryLabels(true);
/*      */     
/*      */ 
/*  218 */     NumberAxis rangeAxis = (NumberAxis)plot.getRangeAxis();
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  244 */     ChartPanel chartPanel = new ChartPanel(chart);
/*  245 */     chartPanel.setPreferredSize(new Dimension(500, 270));
/*  246 */     JFrame jp = new JFrame();
/*  247 */     jp.setSize(800, 800);
/*  248 */     jp.getContentPane().add(chartPanel);
/*  249 */     jp.setVisible(true);
/*      */   }
/*      */   
/*      */   public static void main(String[] args) throws Exception
/*      */   {
/*  254 */     CommandLine params = PARSER.parse(OPTIONS, args);
/*      */     
/*  256 */     String mode = params.getOptionValue("mode", "train");
/*  257 */     if (mode.equals("count")) { countFromDatabase(params);
/*  258 */     } else if (mode.equals("train")) { train(params);
/*  259 */     } else if (mode.equals("graph")) { graph(params);
/*      */     }
/*      */   }
/*      */   
/*  263 */   Comparator comp = FeatureUtils.END_INCREASING;
/*      */   protected PfamAlphabet alph;
/*      */   private SpeciesAlphabet spec_al;
/*      */   private SymbolTokenization token;
/*      */   
/*      */   public static void train(CommandLine params) throws Exception
/*      */   {
/*  270 */     File repos = new File(params.getOptionValue("repository"));
/*  271 */     File dir = new File(params.getOptionValue("dir"));
/*  272 */     String[] context_files = { "0", "1", "2", "3", "4" };
/*  273 */     System.err.println(Arrays.asList(params.getOptionValues("smoothing")));
/*  274 */     ContextCount cc = new ContextCount(dir, repos, 4, true);
/*  275 */     DomainDPFactory dpFact = new DomainDPFactory(cc, params);
/*  276 */     System.err.println("training ");
/*      */     
/*  278 */     double[] smoothing_params = train(new File(dir, "pfamA_reg_full_ls_training_held"), cc.alph, dpFact.create());
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static void countFromDatabase(CommandLine params)
/*      */     throws Exception
/*      */   {
/*  365 */     File repos = new File(params.getOptionValue("repository"));
/*  366 */     File dir = new File(params.getOptionValue("dir"));
/*  367 */     System.err.println("making domain alphabet");
/*  368 */     ContextCount cc = new ContextCount(dir, repos, 4, false);
/*  369 */     String[] context_files = { "0", "1", "2", "3", "4" };
/*  370 */     SequenceIterator seqI = 
/*  371 */       IndexedPfamDB.SequenceIterator(new File(dir, "pfamA_astral_ls_training"), cc.alph, "\t", 0, 1);
/*  372 */     System.err.println("adding counts from db ");
/*  373 */     cc.addCounts(seqI);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public static double[] train(File held, DomainAlphabet alph, DomainDP dp)
/*      */   {
/*  380 */     MultivariateFunction mvf = new MultivariateFunction()
/*      */     {
/*      */       public int getNumArguments() {
/*  383 */         return 2;
/*      */       }
/*      */       
/*  386 */       public double getLowerBound(int i) { return 0.01D; }
/*      */       
/*      */       public double getUpperBound(int i) {
/*  389 */         return 0.99D;
/*      */       }
/*      */       
/*  392 */       public OrthogonalHints getOrthogonalHints() { return null; }
/*      */       
/*      */       public double evaluate(double[] args) {
/*      */         try {
/*  396 */           SequenceIterator seqIt = 
/*  397 */             IndexedPfamDB.SequenceIterator(ContextCount.this, this.val$alph, "\t", 0, 1);
/*  398 */           System.err.println("evaluating " + args[0] + " " + args[1]);
/*  399 */           ContextTransitionScores.CONTEXT = args[0];
/*  400 */           ContextTransitionScores.SPECIES = args[1];
/*  401 */           int j = 0;
/*  402 */           double score = 0.0D;
/*  403 */           while ((seqIt.hasNext()) && (j < 1000))
/*      */           {
/*  405 */             Sequence seq = seqIt.nextSequence();
/*  406 */             seq.createFeature(new Domain.MagicTemplate(this.val$alph, 0));
/*  407 */             seq.createFeature(new Domain.MagicTemplate(this.val$alph, Integer.MAX_VALUE));
/*  408 */             score += this.val$dp.score(seq);
/*      */             
/*  410 */             j++;
/*      */           }
/*  412 */           System.err.println("total score " + score);
/*  413 */           return -1.0D * score;
/*      */         } catch (Exception exc) {
/*  415 */           exc.printStackTrace();
/*  416 */           System.exit(0);
/*      */         }
/*  418 */         return 0.0D;
/*      */       }
/*  420 */     };
/*  421 */     double[] xvec = { 0.364D, 0.01D };
/*  422 */     MultivariateMinimum mvm = new OrthogonalSearch();
/*  423 */     mvm.optimize(mvf, xvec, 10.0D, 0.02D);
/*  424 */     System.out.println("result is " + xvec[0] + " " + xvec[1]);
/*  425 */     return xvec;
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
/*  439 */   public static int contextLength = 3;
/*  440 */   public static boolean variableOrder = true;
/*  441 */   final Comparator CONT_COMP = new ContextComparator();
/*      */   
/*      */   static class ContextComparator implements Comparator, Serializable {
/*  444 */     public int compare(Object o1, Object o2) { List sl1 = (List)o1;
/*  445 */       List sl2 = (List)o2;
/*  446 */       int length1 = sl1.size();
/*  447 */       int length2 = sl2.size();
/*      */       
/*  449 */       int min = Math.min(length1, length2);
/*  450 */       for (int i = 2; i <= min; i++) {
/*  451 */         Object s1 = sl1.get(length1 - i);
/*  452 */         Object s2 = sl2.get(length2 - i);
/*      */         
/*  454 */         if (s1 != s2) {
/*  455 */           int i1 = s1 == null ? Integer.MAX_VALUE : s1.hashCode();
/*  456 */           int i2 = s2 == null ? Integer.MAX_VALUE : s2.hashCode();
/*      */           
/*  458 */           return i1 < i2 ? -1 : 1;
/*      */         }
/*      */       }
/*  461 */       if (length1 != length2) return length1 < length2 ? -1 : 1;
/*  462 */       if (min > 0) {
/*  463 */         Object s1 = sl1.get(length1 - 1);
/*  464 */         Object s2 = sl2.get(length2 - 1);
/*      */         
/*  466 */         if (s1 != s2) {
/*  467 */           int i1 = s1.hashCode();
/*  468 */           int i2 = s2.hashCode();
/*      */           
/*  470 */           return i1 < i2 ? -1 : 1;
/*      */         }
/*      */       }
/*  473 */       return 0;
/*      */     }
/*      */   }
/*      */   
/*  477 */   protected SortedMap contMap = new TreeMap(this.CONT_COMP);
/*      */   
/*      */   PrintWriter[] pw;
/*      */   static final double seqCoverage = 0.7D;
/*      */   
/*      */   public ContextCount(File dir, File repos, int contextLength, boolean useSerialize)
/*      */     throws Exception
/*      */   {
/*  485 */     contextMap = new File(dir, "contextObjects");
/*  486 */     if (useSerialize) {
/*  487 */       ObjectInputStream p = new ObjectInputStream(new FileInputStream(contextMap));
/*  488 */       Object[] obj = (Object[])p.readObject();
/*  489 */       this.contMap = ((SortedMap)obj[0]);
/*  490 */       int size = this.contMap.keySet().size();
/*  491 */       this.alph = ((PfamAlphabet)obj[1]);
/*  492 */       this.spec_al = ((SpeciesAlphabet)obj[2]);
/*  493 */       SpeciesAlphabet.reset(this.spec_al);
/*  494 */       PfamAlphabet.reset(this.alph);
/*      */       
/*      */ 
/*      */ 
/*  498 */       p.close();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     }
/*      */     else
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/*  509 */       if ((contextMap.exists()) && (contextMap.length() > 0L)) { throw new RuntimeException(" delete " + contextMap.getAbsolutePath() + " first");
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  520 */       this.alph = PfamAlphabet.makeAlphabet(repos);
/*  521 */       this.spec_al = SpeciesAlphabet.makeAlphabet(repos, IndexedPfamDB.SequenceIterator(new File(repos, "pfamA_reg_full_ls"), this.alph, "\t", 0, 1));
/*      */       
/*  523 */       System.err.println("done");
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  535 */       System.err.println("making species alphabet");
/*  536 */       this.alph = PfamAlphabet.makeAlphabet(SheetIO.read(new File(repos, "pfamA"), "\t"), SheetIO.read(new File(repos, "clans"), "\t"));
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*  541 */       this.token = null;
/*      */       try {
/*  543 */         this.token = this.spec_al.getTokenization("token");
/*      */       } catch (NoSuchElementException e1) {
/*  545 */         e1.printStackTrace();
/*      */       } catch (BioException e1) {
/*  547 */         e1.printStackTrace();
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   private void fillUpTree()
/*      */   {
/*  554 */     List postOrderList = new ArrayList();
/*  555 */     this.spec_al.postOrderList(this.spec_al.root(), postOrderList);
/*  556 */     System.err.println("post order size " + postOrderList.size());
/*  557 */     for (Iterator it = this.contMap.keySet().iterator(); it.hasNext();) {
/*  558 */       Object sl = it.next();
/*      */       
/*  560 */       Frequency freq = (Frequency)this.contMap.get(sl);
/*  561 */       if (freq != null)
/*      */       {
/*  563 */         for (Iterator it1 = postOrderList.iterator(); it1.hasNext();) {
/*  564 */           SpeciesAlphabet.SpeciesSymbol species = (SpeciesAlphabet.SpeciesSymbol)it1.next();
/*      */           
/*  566 */           int count = freq.getCount(species);
/*  567 */           if ((count != 0) && 
/*  568 */             (!species.isRoot())) {
/*  569 */             Symbol parent = species.getParent();
/*  570 */             freq.put(parent, freq.get(parent) + count);
/*      */           }
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
/*      */   public void addCounts(SequenceIterator db)
/*      */   {
/*      */     try
/*      */     {
/*  602 */       int j = 0;
/*  603 */       int k = 0;
/*  604 */       while (db.hasNext())
/*      */       {
/*  606 */         if (k == 1000) {
/*  607 */           System.err.println(j);
/*  608 */           k = 0;
/*      */         }
/*  610 */         j++;
/*  611 */         k++;
/*  612 */         Sequence seq = db.nextSequence();
/*  613 */         countSequence((DomainList)seq);
/*      */       }
/*      */       
/*  616 */       this.pw = new PrintWriter[contextLength + 1];
/*  617 */       for (int i = 0; i <= contextLength; i++)
/*      */       {
/*  619 */         this.pw[i] = new PrintWriter(new BufferedWriter(new FileWriter(i)));
/*      */       }
/*      */       
/*  622 */       serialize();
/*      */       
/*  624 */       for (int i = 0; i <= contextLength; i++)
/*      */       {
/*  626 */         this.pw[i].close();
/*      */       }
/*      */       
/*      */ 
/*      */     }
/*      */     catch (Throwable t)
/*      */     {
/*      */ 
/*  634 */       t.printStackTrace();
/*      */     }
/*      */   }
/*      */   
/*  638 */   TObjectIntHashMap speciesOverallCounts = new TObjectIntHashMap();
/*      */   
/*      */   public void serialize() throws Exception
/*      */   {
/*  642 */     Frequency freq = (Frequency)this.contMap.get(this.alph.magicContext(1));
/*  643 */     Frequency freq1 = new Frequency();
/*  644 */     this.contMap.put(this.alph.magicContext(2), freq1);
/*  645 */     Frequency freq0 = (Frequency)this.contMap.get(Arrays.asList(new Object[0]));
/*  646 */     this.speciesOverallCounts.forEachEntry(new TObjectIntProcedure()
/*      */     {
/*      */       public boolean execute(Object sym, int species_count) {
/*  649 */         int species_empty = (int)(species_count * 0.42857142857142855D);
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*  654 */         this.val$freq.put(sym, species_empty + this.val$freq.getImpl((Symbol)sym));
/*  655 */         this.val$freq1.put(sym, species_empty);
/*  656 */         this.val$freq0.put(sym, species_empty + this.val$freq0.getImpl((Symbol)sym));
/*  657 */         return true;
/*      */       }
/*  659 */     });
/*  660 */     System.err.println("filling up tree");
/*  661 */     fillUpTree();
/*  662 */     System.err.println("... done filling up tree");
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  680 */     ObjectOutputStream os = new ObjectOutputStream(
/*  681 */       new FileOutputStream(contextMap));
/*  682 */     os.writeObject(new Object[] { this.contMap, this.alph, this.spec_al });
/*  683 */     os.flush();
/*  684 */     os.close();
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
/*      */   private void getCountsFull(Frequency freq, SymbolList species, int[] result)
/*      */   {
/*  700 */     for (int i = 0; i < result.length; i++) {
/*  701 */       result[i] = freq.get(species.symbolAt(i + 1));
/*      */     }
/*      */   }
/*      */   
/*      */   private void getCountsContext(Frequency freq, SymbolList species, int[] resultFull, int[] result)
/*      */   {
/*  707 */     for (int i = 0; i < result.length; i++) {
/*  708 */       result[i] = (resultFull[i] == 0 ? 1 : freq.get(species.symbolAt(i + 1)));
/*  709 */       if (result[i] == 0) { throw new RuntimeException("this cannot happen! ");
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   public int[][][] getCounts(List sl, SymbolList species)
/*      */   {
/*  716 */     List l1 = longestExtendable(sl);
/*      */     
/*  718 */     int length = l1.size();
/*  719 */     int[][] resultFull = new int[length][0];
/*  720 */     int[][] resultContext = new int[length][0];
/*  721 */     for (int i = 0; i < l1.size(); i++) {
/*      */       try {
/*  723 */         List subl = l1.subList(i, length);
/*  724 */         Frequency freq = (Frequency)this.contMap.get(subl);
/*  725 */         resultFull[i] = new int[species.length()];
/*  726 */         resultContext[i] = new int[species.length()];
/*  727 */         if (freq == null) {
/*  728 */           Arrays.fill(resultFull[i], 0);
/*  729 */           Arrays.fill(resultContext[i], 1);
/*      */         }
/*      */         else {
/*  732 */           getCountsFull(freq, species, resultFull[i]);
/*  733 */           List context = l1.subList(i, length - 1);
/*  734 */           Frequency freqContext = (Frequency)this.contMap.get(context);
/*  735 */           getCountsContext(freqContext, species, resultFull[i], resultContext[i]);
/*      */         }
/*      */       } catch (Exception exc) {
/*  738 */         exc.printStackTrace();
/*      */       }
/*      */     }
/*      */     
/*  742 */     return new int[][][] { resultFull, resultContext };
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public List longestExtendable(List sl)
/*      */   {
/*  752 */     int size = sl.size();
/*  753 */     if (!variableOrder) {
/*  754 */       return sl.size() > contextLength + 1 ? 
/*  755 */         sl.subList(size - (contextLength + 1), size) : 
/*  756 */         sl;
/*      */     }
/*      */     
/*  759 */     List upperBound = new ArrayList();
/*  760 */     upperBound.add(this.alph.getMagicalState());
/*  761 */     SortedMap innerMap = this.contMap;
/*  762 */     List previous = sl.subList(size - 1, size);
/*  763 */     for (int i = 2; (i <= size) && (i <= contextLength + 1); i++) {
/*      */       try {
/*  765 */         upperBound.add(0, this.alph.getSuccessor((Symbol)sl.get(size - i)));
/*  766 */         List sl_i = new ArrayList(sl.subList(size - i, size - 1));
/*  767 */         sl_i.add(this.alph.getMagicalState());
/*  768 */         innerMap = innerMap.tailMap(sl_i).headMap(upperBound);
/*  769 */         List firstKey = (List)innerMap.firstKey();
/*  770 */         if (firstKey.size() != i) break;
/*  771 */         for (int j = 0; j < i - 1; j++)
/*  772 */           if (firstKey.get(j) != sl_i.get(j))
/*      */             break;
/*  774 */         previous = sl.subList(size - i, size);
/*      */       } catch (Exception exc) {
/*      */         break;
/*      */       }
/*      */     }
/*  779 */     return previous;
/*      */   }
/*      */   
/*      */ 
/*  783 */   static File contextMap = null;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private Map makeNewContextMap(Symbol species)
/*      */   {
/*  845 */     Map contextMap = new HashMap();
/*  846 */     this.contMap.put(species, new SoftReference(contextMap));
/*  847 */     return contextMap;
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void countSequence(DomainList ds)
/*      */   {
/*      */     try
/*      */     {
/*  951 */       Symbol spec = this.token.parseToken((String)ds.getAnnotation().getProperty("species"));
/*  952 */       this.speciesOverallCounts.put(spec, this.speciesOverallCounts.get(spec) + 1);
/*  953 */       ds.createFeature(new Domain.MagicTemplate(this.alph, 0));
/*  954 */       ds.createFeature(new Domain.MagicTemplate(this.alph, Integer.MAX_VALUE));
/*  955 */       Domain[] symL = new Domain[ds.countFeatures()];
/*  956 */       Iterator it = ds.features();
/*  957 */       for (int i = 0; i < symL.length; i++) {
/*  958 */         symL[i] = ((Domain)it.next());
/*      */       }
/*      */       
/*  961 */       Arrays.sort(symL, this.comp);
/*  962 */       Symbol[] symList = new Symbol[symL.length];
/*  963 */       for (int i = 0; i < symL.length; i++) {
/*  964 */         Symbol sym = symL[i].getSymbol();
/*  965 */         symList[i] = (sym.getAnnotation().containsProperty("clan") ? (Symbol)sym.getAnnotation().getProperty("clan") : sym);
/*      */       }
/*  967 */       List sl = Arrays.asList(symList);
/*      */       
/*      */ 
/*  970 */       for (int i = 0; i < sl.size(); i++)
/*      */       {
/*      */ 
/*      */ 
/*  974 */         for (int j = -1; (j <= contextLength) && (i - j >= 0); j++)
/*      */         {
/*  976 */           List context = new ArrayList(sl.subList(i - j, i + 1));
/*  977 */           if ((i >= 1) || (j > 0))
/*      */           {
/*  979 */             Frequency speciesMap = (Frequency)this.contMap.get(context);
/*  980 */             if (speciesMap == null) {
/*  981 */               speciesMap = new Frequency();
/*  982 */               this.contMap.put(context, speciesMap);
/*  983 */               speciesMap.put(spec, 1);
/*      */             }
/*      */             else {
/*  986 */               speciesMap.put(spec, 1 + speciesMap.getImpl(spec));
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     catch (Exception exc) {
/*  993 */       exc.printStackTrace();
/*  994 */       System.err.println(ds.getName());
/*  995 */       System.err.println(ds.getAnnotation().asMap());
/*  996 */       System.exit(0);
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
/*      */   public DomainAlphabet getAlphabet()
/*      */   {
/* 1012 */     return this.alph;
/*      */   }
/*      */   
/*      */   public SpeciesAlphabet getSpeciesAlphabet() {
/* 1016 */     return this.spec_al;
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
/*      */   public int getCount(Symbol species, List sl)
/*      */   {
/* 1080 */     Frequency freq = (Frequency)this.contMap.get(sl);
/* 1081 */     return freq == null ? 0 : freq.getCount(species);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public int getCount(Symbol species, List sl, Symbol s)
/*      */   {
/* 1093 */     Map Map = (Map)this.contMap.get(sl);
/* 1094 */     if (Map == null) return 0;
/* 1095 */     Frequency freq = (Frequency)Map.get(species);
/* 1096 */     if (freq == null) return 0;
/* 1097 */     return freq.getCount(s);
/*      */   }
/*      */   
/* 1100 */   int total = 0;
/* 1101 */   List EMPTY_LIST = Arrays.asList(new Object[0]);
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public int getTotal()
/*      */   {
/* 1108 */     if (this.total == 0)
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1115 */       Frequency freq = (Frequency)this.contMap.get(this.EMPTY_LIST);
/* 1116 */       this.total = freq.get(this.spec_al.root());
/*      */     }
/* 1118 */     return this.total;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public Frequency getFrequency(Symbol symbol, List empty_list)
/*      */   {
/* 1127 */     return (Frequency)((Map)this.contMap.get(empty_list)).get(symbol);
/*      */   }
/*      */ }


/* Location:              /home/lachlan/Desktop/psilc1.21/psilc.jar!/lc1/domains/ContextCount.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */