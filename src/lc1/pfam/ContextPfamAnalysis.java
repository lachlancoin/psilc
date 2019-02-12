/*     */ package lc1.pfam;
/*     */ 
/*     */ import com.braju.format.Format;
/*     */ import forester.atv.ATVjframe;
/*     */ import forester.tree.Node;
/*     */ import forester.tree.TreeHelper;
/*     */ import gnu.trove.TIntIntHashMap;
/*     */ import gnu.trove.TObjectIntHashMap;
/*     */ import java.awt.Color;
/*     */ import java.awt.Container;
/*     */ import java.awt.Dimension;
/*     */ import java.io.BufferedWriter;
/*     */ import java.io.File;
/*     */ import java.io.FileWriter;
/*     */ import java.io.PrintStream;
/*     */ import java.io.PrintWriter;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.Comparator;
/*     */ import java.util.Enumeration;
/*     */ import java.util.HashSet;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Properties;
/*     */ import java.util.Set;
/*     */ import java.util.Vector;
/*     */ import javax.swing.JFrame;
/*     */ import lc1.domains.ContextCount;
/*     */ import lc1.domainseq.Domain;
/*     */ import lc1.treefam.TaxonomyTree;
/*     */ import lc1.util.SheetIO;
/*     */ import org.apache.commons.cli.CommandLine;
/*     */ import org.apache.commons.cli.OptionBuilder;
/*     */ import org.apache.commons.cli.Options;
/*     */ import org.apache.commons.cli.Parser;
/*     */ import org.apache.commons.cli.PosixParser;
/*     */ import org.biojava.bio.Annotation;
/*     */ import org.biojava.bio.BioException;
/*     */ import org.biojava.bio.seq.Sequence;
/*     */ import org.biojava.bio.seq.SequenceIterator;
/*     */ import org.biojava.bio.seq.io.SymbolTokenization;
/*     */ import org.biojava.bio.symbol.FiniteAlphabet;
/*     */ import org.biojava.bio.symbol.Location;
/*     */ import org.biojava.bio.symbol.Symbol;
/*     */ import org.biojava.bio.symbol.SymbolList;
/*     */ import org.jfree.chart.ChartFactory;
/*     */ import org.jfree.chart.ChartPanel;
/*     */ import org.jfree.chart.JFreeChart;
/*     */ import org.jfree.chart.axis.AxisLocation;
/*     */ import org.jfree.chart.axis.CategoryAxis;
/*     */ import org.jfree.chart.axis.CategoryLabelPositions;
/*     */ import org.jfree.chart.axis.LogarithmicAxis;
/*     */ import org.jfree.chart.axis.NumberAxis;
/*     */ import org.jfree.chart.axis.ValueAxis;
/*     */ import org.jfree.chart.labels.StandardCategoryToolTipGenerator;
/*     */ import org.jfree.chart.labels.StandardXYToolTipGenerator;
/*     */ import org.jfree.chart.labels.XYToolTipGenerator;
/*     */ import org.jfree.chart.plot.CategoryPlot;
/*     */ import org.jfree.chart.plot.DatasetRenderingOrder;
/*     */ import org.jfree.chart.plot.PlotOrientation;
/*     */ import org.jfree.chart.plot.XYPlot;
/*     */ import org.jfree.chart.renderer.LineAndShapeRenderer;
/*     */ import org.jfree.chart.renderer.StandardXYItemRenderer;
/*     */ import org.jfree.chart.urls.StandardXYURLGenerator;
/*     */ import org.jfree.chart.urls.XYURLGenerator;
/*     */ import org.jfree.data.CategoryDataset;
/*     */ import org.jfree.data.DefaultCategoryDataset;
/*     */ import org.jfree.data.XYSeries;
/*     */ import org.jfree.data.XYSeriesCollection;
/*     */ import org.jfree.ui.ApplicationFrame;
/*     */ import org.jfree.ui.RefineryUtilities;
/*     */ import pal.tree.AttributeNode;
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
/*     */ public class ContextPfamAnalysis
/*     */ {
/*  88 */   static final Options OPTIONS = new Options() {};
/*     */   
/*     */   static final String base = "select pfamA_acc,  count(pfamA_acc), model_length from lc1_test, pfamA where pfamA.auto_pfamA = lc1_test.auto_pfamA group by pfamA_acc";
/*     */   
/*     */   static final String base1 = "select pfamA_acc,  count(pfamA_acc), model_length from pfamA_reg_full, pfamA where pfamA.auto_pfamA = pfamA_reg_full.auto_pfamA and significant = 1 and mode = 'ls' group by pfamA_acc";
/*     */   
/*     */   SymbolTokenization token;
/*     */   
/*     */   Properties params;
/*     */   
/*     */   FiniteAlphabet alph;
/*     */   
/*     */   SpeciesAlphabet spec_al;
/*     */   
/*     */   SymbolTokenization spec_tok;
/*     */   
/*     */   pal.tree.Tree taxon;
/*     */   
/*     */   ContextPfamAnalysis(Properties params, FiniteAlphabet alph, SpeciesAlphabet spec_alph)
/*     */     throws BioException
/*     */   {
/* 109 */     for (int i = 0; i < this.counts.length; i++)
/* 110 */       this.counts[i] = new TObjectIntHashMap();
/* 111 */     this.params = params;
/* 112 */     this.alph = alph;
/* 113 */     this.spec_al = spec_alph;
/* 114 */     this.token = alph.getTokenization("token");
/* 115 */     this.spec_tok = this.spec_al.getTokenization("token");
/*     */   }
/*     */   
/*     */   public static void main(String[] args) throws Exception {
/* 119 */     Parser parser = new PosixParser();
/* 120 */     CommandLine params = parser.parse(OPTIONS, args);
/* 121 */     Properties sqlParams = new Properties();
/* 122 */     sqlParams.setProperty("host", params.getOptionValue("host", "pfam"));
/* 123 */     sqlParams.setProperty("user", params.getOptionValue("host", "pfam"));
/* 124 */     sqlParams.setProperty("database", params.getOptionValue("database", "pfam"));
/* 125 */     sqlParams.setProperty("password", params.getOptionValue("database", "mafp1"));
/* 126 */     repos = new File(params.getOptionValue("repository", "/home/lc/Data/lc1/"));
/* 127 */     ContextCount cc = new ContextCount(new File("."), repos, 4, true);
/*     */     
/* 129 */     DomainAlphabet alph = cc.getAlphabet();
/*     */     
/* 131 */     SpeciesAlphabet spec_al = cc.getSpeciesAlphabet();
/* 132 */     System.err.println("spec _al null " + spec_al == null);
/* 133 */     ContextPfamAnalysis analy = new ContextPfamAnalysis(sqlParams, alph, spec_al);
/* 134 */     File f = new File("pfamDetails_" + species);
/* 135 */     if ((f.exists()) && (f.length() > 0L)) {
/* 136 */       analy.readData(f);
/*     */     }
/*     */     else {
/* 139 */       analy.readData();
/*     */     }
/* 141 */     analy.taxon = new TaxonomyTree(new File(repos, "nodes.dmp"), new File(repos, "names.dmp"), "root");
/* 142 */     analy.printToFile(f);
/* 143 */     analy.graphNewPostives();
/* 144 */     analy.graphTree();
/*     */   }
/*     */   
/*     */   class SymbolComp
/*     */     implements Comparator
/*     */   {
/*     */     int index;
/*     */     
/* 152 */     SymbolComp(int index) { this.index = index; }
/*     */     
/*     */     public int compare(Object o1, Object o2) {
/* 155 */       int i1 = ContextPfamAnalysis.this.counts[this.index].get(o1);
/* 156 */       int i2 = ContextPfamAnalysis.this.counts[this.index].get(o2);
/* 157 */       if (i1 != i2) return i1 < i2 ? 1 : -1;
/* 158 */       return 0;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/* 163 */   List l = new ArrayList();
/*     */   
/*     */   class GraphLengthHistogram { GraphLengthHistogram() {}
/*     */     
/* 167 */     public TIntIntHashMap[] getHistograms(Iterator it, int width) { TIntIntHashMap[] hist = { new TIntIntHashMap(), 
/* 168 */         new TIntIntHashMap() };
/* 169 */       int[] totals = new int[2];
/* 170 */       int[] totC = new int[2];
/* 171 */       while (it.hasNext()) {
/* 172 */         Symbol sym = (Symbol)it.next();
/* 173 */         int count = 
/* 174 */           ContextPfamAnalysis.this.counts[1].get(sym);
/* 175 */         int countT = 
/* 176 */           ContextPfamAnalysis.this.counts[0].get(sym);
/*     */         
/* 178 */         int modelL = ((Integer)sym.getAnnotation().getProperty("modelLength")).intValue();
/* 179 */         totals[0] += count * modelL;totC[0] += count;
/* 180 */         totals[1] += countT * modelL;totC[1] += countT;
/* 181 */         modelL = width * (int)Math.floor(modelL / width);
/*     */         
/* 183 */         hist[0].put(modelL, hist[0].get(modelL) + count);
/* 184 */         hist[1].put(modelL, hist[1].get(modelL) + countT);
/*     */       }
/* 186 */       System.err.println("weighted average length: context " + totals[0] / totC[0]);
/* 187 */       System.err.println("weighted average length: all " + totals[1] / totC[1]);
/* 188 */       return hist;
/*     */     }
/*     */     
/*     */     public XYSeriesCollection getGraph(TIntIntHashMap[] hist, String[] names, double width)
/*     */     {
/* 193 */       XYSeriesCollection collection = new XYSeriesCollection();
/* 194 */       for (int i = 0; i < hist.length; i++) {
/* 195 */         XYSeries res = new XYSeries(names[i]);
/* 196 */         int total = 0;
/* 197 */         int[] vals = hist[i].getValues();
/* 198 */         for (int j = 0; j < vals.length; j++) {
/* 199 */           total += vals[j];
/*     */         }
/* 201 */         double tot = total;
/* 202 */         hist[i].forEachEntry(new ContextPfamAnalysis.2(this, res, width, tot));
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 209 */         collection.addSeries(res);
/*     */       }
/* 211 */       return collection;
/*     */     }
/*     */     
/*     */     public JFreeChart getGraph(XYSeriesCollection datas) throws Exception {
/* 215 */       JFreeChart chart = ChartFactory.createXYLineChart(
/* 216 */         "Frequency Distribution of Model Length", 
/* 217 */         "Model length", 
/* 218 */         "Frequency", 
/* 219 */         datas, 
/* 220 */         PlotOrientation.VERTICAL, 
/* 221 */         true, 
/* 222 */         true, 
/* 223 */         false);
/*     */       
/*     */ 
/* 226 */       XYPlot plot = chart.getXYPlot();
/* 227 */       StandardXYItemRenderer renderer = (StandardXYItemRenderer)plot.getRenderer();
/* 228 */       renderer.setPlotShapes(false);
/* 229 */       return chart;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public void graphFrequencyDistribution(Iterator it)
/*     */     throws Exception
/*     */   {
/* 237 */     GraphLengthHistogram glh = new GraphLengthHistogram();
/* 238 */     JFreeChart chart = glh.getGraph(glh.getGraph(glh.getHistograms(it, 10), new String[] { "Context domains", "All domains" }, 10.0D));
/* 239 */     chart.setBackgroundPaint(Color.white);
/* 240 */     ChartPanel chartPanel = new ChartPanel(chart);
/* 241 */     chartPanel.setPreferredSize(new Dimension(500, 270));
/* 242 */     JFrame jp = new JFrame();
/* 243 */     jp.setSize(800, 800);
/* 244 */     jp.getContentPane().add(chartPanel);
/* 245 */     jp.setVisible(true);
/*     */   }
/*     */   
/*     */   class GraphZipfCurve
/*     */   {
/*     */     GraphZipfCurve() {}
/*     */     
/*     */     private XYSeriesCollection getGraphData(List l) {
/* 253 */       XYSeriesCollection collection = new XYSeriesCollection();
/* 254 */       XYSeries res = new XYSeries("frequency");
/* 255 */       for (int i = 0; i < l.size(); i++) {
/* 256 */         Symbol sym = (Symbol)l.get(i);
/* 257 */         int count = ContextPfamAnalysis.this.counts[0].get(sym);
/* 258 */         res.add(i + 1, count);
/*     */       }
/*     */       
/* 261 */       collection.addSeries(res);
/*     */       
/* 263 */       return collection;
/*     */     }
/*     */     
/* 266 */     private double log10(double a) { return Math.log(a) / Math.log(10.0D); }
/*     */     
/*     */     private double exp10(double a) {
/* 269 */       return Math.pow(10.0D, a);
/*     */     }
/*     */     
/*     */     public JFreeChart getGraph(List l) throws Exception
/*     */     {
/* 274 */       int last_index = l.size();
/* 275 */       Symbol first = (Symbol)l.get(0);Symbol last = (Symbol)l.get(last_index - 1);
/* 276 */       int[] min = { 1, ContextPfamAnalysis.this.counts[0].get(first) };
/* 277 */       int[] max1 = { last_index, ContextPfamAnalysis.this.counts[0].get(last) };
/*     */       
/* 279 */       double alpha = 1.0D;
/* 280 */       double[] max = { l.size(), exp10(log10(min[1]) - alpha * log10(l.size())) };
/* 281 */       XYSeriesCollection dataset = getGraphData(l);
/* 282 */       NumberAxis xAxis = new LogarithmicAxis("Rank");
/* 283 */       xAxis.setAutoRange(false);
/* 284 */       XYSeries straight = new XYSeries(Format.sprintf("log count = %4.2f - %4.2f * log rank", 
/* 285 */         new Object[] { new Double(log10(min[1])), new Double(alpha) }));
/* 286 */       straight.add(min[0], min[1]);
/* 287 */       straight.add(max[0], max[1]);
/* 288 */       XYSeriesCollection straightC = new XYSeriesCollection();
/* 289 */       straightC.addSeries(straight);
/* 290 */       NumberAxis yAxis = new LogarithmicAxis("Count");
/* 291 */       yAxis.setAutoRange(false);
/* 292 */       xAxis.setRange(min[0], max[0]);
/* 293 */       yAxis.setRange(max[1], min[1]);
/* 294 */       XYPlot plot = new XYPlot(dataset, xAxis, yAxis, null);
/* 295 */       XYToolTipGenerator toolTipGenerator = new StandardXYToolTipGenerator();
/* 296 */       XYURLGenerator urlGenerator = new StandardXYURLGenerator();
/* 297 */       StandardXYItemRenderer renderer = new StandardXYItemRenderer(
/* 298 */         1, toolTipGenerator, urlGenerator);
/*     */       
/* 300 */       renderer.setShapesFilled(Boolean.TRUE);
/* 301 */       renderer.setPlotShapes(true);
/* 302 */       plot.setRenderer(renderer);
/* 303 */       plot.setOrientation(PlotOrientation.VERTICAL);
/* 304 */       JFreeChart chart = new JFreeChart("Frequency of domain usage vs rank", JFreeChart.DEFAULT_TITLE_FONT, plot, true);
/* 305 */       plot.setDataset(1, straightC);
/*     */       
/*     */ 
/*     */ 
/* 309 */       StandardXYItemRenderer renderer2 = new StandardXYItemRenderer();
/* 310 */       renderer2.setToolTipGenerator(new StandardXYToolTipGenerator());
/* 311 */       plot.setRenderer(1, renderer2);
/* 312 */       renderer2.setPlotShapes(false);
/* 313 */       renderer2.setPlotLines(true);
/* 314 */       plot.setDatasetRenderingOrder(DatasetRenderingOrder.REVERSE);
/* 315 */       chart.setBackgroundPaint(Color.WHITE);
/* 316 */       return chart;
/*     */     }
/*     */   }
/*     */   
/*     */   public void graphZipfCurve() throws Exception
/*     */   {
/* 322 */     Collections.sort(this.l, new SymbolComp(0));
/* 323 */     GraphZipfCurve glh = new GraphZipfCurve();
/* 324 */     JFreeChart chart = glh.getGraph(this.l);
/* 325 */     chart.setBackgroundPaint(Color.white);
/* 326 */     ChartPanel chartPanel = new ChartPanel(chart);
/* 327 */     chartPanel.setPreferredSize(new Dimension(500, 270));
/* 328 */     JFrame jp = new JFrame();
/* 329 */     jp.setSize(800, 800);
/* 330 */     jp.getContentPane().add(chartPanel);
/* 331 */     jp.setVisible(true);
/*     */   }
/*     */   
/*     */   public void graphNewPostives() throws Exception
/*     */   {
/* 336 */     GraphNewPositives gnp = new GraphNewPositives();
/* 337 */     JFreeChart chart = gnp.getChart(gnp.getData());
/* 338 */     ApplicationFrame demo1 = new ApplicationFrame("") {};
/* 345 */     demo1.pack();
/* 346 */     RefineryUtilities.centerFrameOnScreen(demo1);
/* 347 */     demo1.setVisible(true);
/*     */   }
/*     */   
/*     */   public void graphTree() throws Exception
/*     */   {
/* 352 */     File tmp = new File("tmpTree");
/* 353 */     PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(tmp)));
/* 354 */     lc1.treefam.NodeUtils.printNH(TaxonomyTree.withoutSingleChildNodes(this.spec_al.tree), pw, true, true, true);
/* 355 */     pw.close();
/* 356 */     forester.tree.Tree tree = TreeHelper.readNHtree(tmp);
/* 357 */     annotate(tree.getRoot());
/*     */     
/* 359 */     new ATVjframe(tree).showWhole();
/*     */   }
/*     */   
/* 362 */   private void annotate(Node node) throws Exception { String name = node.isRoot() ? "1" : node.getSeqName();
/* 363 */     Symbol sym = this.spec_tok.parseToken(name);
/* 364 */     if (!node.isRoot()) {
/* 365 */       AttributeNode n = (AttributeNode)pal.tree.NodeUtils.findByIdentifier(this.taxon.getRoot(), name);
/* 366 */       node.setSpecies((String)n.getAttribute("name"));
/*     */     }
/* 368 */     double ratio = this.counts[1].get(sym) / this.counts[0].get(sym);
/* 369 */     node.setBootstrap((int)Math.floor(100.0D * ratio + 0.5D));
/* 370 */     node.setLnLonParentBranch((float)ratio);
/* 371 */     if (node.isExternal()) return;
/* 372 */     for (Enumeration en = node.getAllChildren().elements(); en.hasMoreElements();) {
/* 373 */       annotate((Node)en.nextElement());
/*     */     }
/*     */   }
/*     */   
/*     */   class GraphNewPositives {
/*     */     GraphNewPositives() {}
/*     */     
/*     */     public JFreeChart getChart(CategoryDataset[] data) {
/* 381 */       JFreeChart chart = ChartFactory.createBarChart(
/* 382 */         "New domain predictions, top 20 domains ", 
/* 383 */         "Domain", 
/* 384 */         "# No. new predictions", 
/* 385 */         data[0], 
/* 386 */         PlotOrientation.VERTICAL, 
/* 387 */         false, 
/* 388 */         true, 
/* 389 */         false);
/*     */       
/*     */ 
/* 392 */       chart.setBackgroundPaint(Color.white);
/*     */       
/* 394 */       CategoryPlot plot = chart.getCategoryPlot();
/* 395 */       plot.setDomainAxisLocation(AxisLocation.BOTTOM_OR_RIGHT);
/* 396 */       plot.setDataset(1, data[1]);
/* 397 */       plot.mapDatasetToRangeAxis(1, 1);
/* 398 */       CategoryAxis domainAxis = plot.getDomainAxis();
/* 399 */       domainAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_90);
/* 400 */       ValueAxis axis2 = new NumberAxis("New domain predictions as %age of existing domains");
/* 401 */       plot.setRangeAxis(1, axis2);
/* 402 */       LineAndShapeRenderer renderer2 = new LineAndShapeRenderer();
/* 403 */       renderer2.setToolTipGenerator(new StandardCategoryToolTipGenerator());
/* 404 */       plot.setRenderer(1, renderer2);
/* 405 */       plot.setDatasetRenderingOrder(DatasetRenderingOrder.FORWARD);
/* 406 */       return chart;
/*     */     }
/*     */     
/*     */     public CategoryDataset[] getData() throws Exception {
/* 410 */       DefaultCategoryDataset total = new DefaultCategoryDataset();
/* 411 */       DefaultCategoryDataset perc = new DefaultCategoryDataset();
/* 412 */       Collections.sort(ContextPfamAnalysis.this.l, new ContextPfamAnalysis.SymbolComp(ContextPfamAnalysis.this, 1));
/* 413 */       int tot = 0;
/* 414 */       for (Iterator it = ContextPfamAnalysis.this.l.iterator(); it.hasNext();) {
/* 415 */         Symbol sym = (Symbol)it.next();
/* 416 */         tot += ContextPfamAnalysis.this.counts[1].get(sym);
/*     */       }
/* 418 */       int j = 0;
/* 419 */       int cum = 0;
/* 420 */       for (Iterator it = ContextPfamAnalysis.this.l.iterator(); it.hasNext(); j++) {
/* 421 */         Symbol sym = (Symbol)it.next();
/* 422 */         int count = ContextPfamAnalysis.this.counts[1].get(sym);
/* 423 */         cum += count;
/* 424 */         double cumu = cum / tot;
/* 425 */         if (count > 0) System.err.println(j + " " + cumu);
/* 426 */         int totalCount = ContextPfamAnalysis.this.counts[0].get(sym);
/* 427 */         if (j < 20) {
/* 428 */           Annotation annot = sym.getAnnotation();
/* 429 */           total.addValue(count, "total new", 
/*     */           
/*     */ 
/* 432 */             annot.containsProperty("pfamA_id") ? 
/* 433 */             (String)annot.getProperty("pfamA_id") : annot.containsProperty("clan_id") ? (String)annot.getProperty("clan_id") : sym.getName());
/* 434 */           perc.addValue(100.0D * (count / totalCount), "total as percentage of current", sym.getName());
/*     */         } }
/* 436 */       return new CategoryDataset[] { total, perc };
/*     */     }
/*     */   }
/*     */   
/*     */   public void printToFile(File f) throws Exception
/*     */   {
/* 442 */     PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(f)));
/* 443 */     for (Iterator it = species ? this.spec_al.iterator() : this.alph.iterator(); it.hasNext();) {
/* 444 */       Symbol sym = (Symbol)it.next();
/* 445 */       pw.println(sym.getName() + "\t" + this.counts[1].get(sym) + 
/* 446 */         "\t" + this.counts[0].get(sym) + "\t" + this.counts[3].get(sym) + "\t" + this.counts[2].get(sym));
/*     */     }
/*     */     
/*     */ 
/* 450 */     pw.close();
/*     */   }
/*     */   
/*     */   public void readData(File f) throws Exception {
/* 454 */     for (Iterator it = SheetIO.read(f, "\t"); it.hasNext();) {
/* 455 */       List row = (List)it.next();
/* 456 */       Symbol sym = species ? this.spec_tok.parseToken((String)row.get(0)) : 
/* 457 */         this.token.parseToken((String)row.get(0));
/* 458 */       if (sym.getAnnotation().containsProperty("clan"))
/* 459 */         sym = (Symbol)sym.getAnnotation().getProperty("clan");
/* 460 */       int count = Integer.parseInt((String)row.get(1));
/* 461 */       int totalCount = Integer.parseInt((String)row.get(2));
/* 462 */       if (totalCount != 0)
/*     */       {
/* 464 */         this.counts[0].put(sym, totalCount);
/* 465 */         this.counts[1].put(sym, count);
/* 466 */         this.l.add(sym);
/*     */       } } }
/*     */   
/* 469 */   static boolean species = true;
/*     */   
/*     */   static File repos;
/* 472 */   TObjectIntHashMap[] counts = new TObjectIntHashMap[4];
/*     */   
/*     */   public void readData() throws Exception {
/* 475 */     System.err.println("species " + this.spec_al.size());
/* 476 */     FiniteAlphabet alphabet = species ? this.spec_al : this.alph;
/* 477 */     for (Iterator it = alphabet.iterator(); it.hasNext();) {
/* 478 */       Object obj = it.next();
/* 479 */       this.l.add(obj);
/* 480 */       System.err.println(obj);
/* 481 */       this.counts[0].put(obj, 0);
/* 482 */       this.counts[1].put(obj, 0);
/* 483 */       this.counts[2].put(obj, 0);
/* 484 */       this.counts[3].put(obj, 0);
/*     */     }
/* 486 */     int[] coverage = new int[2];
/* 487 */     Set context_seqs = new HashSet();
/*     */     
/* 489 */     SequenceIterator seqIt = IndexedPfamDB.SequenceIterator(new File(repos, "pfamA_reg_full_context"), (DomainAlphabet)this.alph, "\\s+", 0, 1);
/* 488 */     while (
/*     */     
/* 490 */       seqIt.hasNext()) {
/* 491 */       Sequence seq = seqIt.nextSequence();
/* 492 */       Annotation annot = seq.getAnnotation();
/* 493 */       context_seqs.add(seq.getName());
/* 494 */       if (species) {
/* 495 */         Symbol sym = 
/* 496 */           annot.containsProperty("species") ? this.spec_tok.parseToken((String)annot.getProperty("species")) : this.spec_al.root();
/* 497 */         SymbolList sl = this.spec_al.taxaToList(sym);
/* 498 */         for (int i = 1; i <= sl.length(); i++) {
/* 499 */           this.counts[3].increment(sl.symbolAt(i));
/* 500 */           this.counts[1].put(sl.symbolAt(i), this.counts[1].get(sl.symbolAt(i)) + seq.countFeatures());
/*     */         }
/*     */       }
/*     */       else {
/* 504 */         for (Iterator feat = seq.features(); feat.hasNext();) {
/* 505 */           Domain dom = (Domain)feat.next();
/* 506 */           Symbol sym = 
/* 507 */             dom.getSymbol();
/*     */           
/*     */ 
/*     */ 
/* 511 */           this.counts[1].increment(sym);
/* 512 */           coverage[1] += dom.getLocation().getMax() - dom.getLocation().getMin();
/*     */         }
/*     */       }
/*     */     }
/* 516 */     System.err.println("annotated " + context_seqs.size() + " proteins");
/*     */     
/* 518 */     SequenceIterator seqIt = IndexedPfamDB.SequenceIterator(new File(repos, "pfamA_reg_full_ls"), (DomainAlphabet)this.alph, "\\s+", 0, 1);
/* 517 */     while (
/*     */     
/* 519 */       seqIt.hasNext()) {
/* 520 */       Sequence seq = seqIt.nextSequence();
/* 521 */       context_seqs.remove(seq.getName());
/* 522 */       Annotation annot = seq.getAnnotation();
/* 523 */       if (species) {
/* 524 */         Symbol sym = 
/* 525 */           annot.containsProperty("species") ? this.spec_tok.parseToken((String)annot.getProperty("species")) : this.spec_al.root();
/* 526 */         SymbolList sl = this.spec_al.taxaToList(sym);
/* 527 */         for (int i = 1; i <= sl.length(); i++) {
/* 528 */           this.counts[2].increment(sl.symbolAt(i));
/* 529 */           this.counts[0].put(sl.symbolAt(i), this.counts[0].get(sl.symbolAt(i)) + seq.countFeatures());
/*     */         }
/*     */       }
/*     */       else {
/* 533 */         for (Iterator feat = seq.features(); feat.hasNext();) {
/* 534 */           Domain dom = (Domain)feat.next();
/* 535 */           Symbol sym = dom.getSymbol();
/*     */           
/*     */ 
/* 538 */           this.counts[0].increment(sym);
/* 539 */           coverage[0] += dom.getLocation().getMax() - dom.getLocation().getMin();
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 544 */     System.err.println("coverage context " + coverage[1]);
/* 545 */     System.err.println("coverage all " + coverage[0]);
/* 546 */     System.err.println("new proteins " + context_seqs.size());
/*     */   }
/*     */ }


/* Location:              /home/lachlan/Desktop/psilc1.21/psilc.jar!/lc1/pfam/ContextPfamAnalysis.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */