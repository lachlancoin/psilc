/*     */ package lc1.domains;
/*     */ 
/*     */ import com.braju.format.Format;
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
/*     */ import java.util.HashSet;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Properties;
/*     */ import java.util.Set;
/*     */ import javax.swing.JFrame;
/*     */ import lc1.domainseq.Domain;
/*     */ import lc1.pfam.DomainAlphabet;
/*     */ import lc1.pfam.IndexedPfamDB;
/*     */ import lc1.pfam.PfamAlphabet;
/*     */ import lc1.pfam.SpeciesAlphabet;
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
/*  84 */   static final Options OPTIONS = new Options() {};
/*     */   
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
/*     */ 
/*     */   ContextPfamAnalysis(Properties params, FiniteAlphabet alph, SpeciesAlphabet spec_alph)
/*     */     throws BioException
/*     */   {
/* 105 */     this.params = params;
/* 106 */     this.alph = alph;
/* 107 */     this.spec_al = spec_alph;
/* 108 */     this.token = alph.getTokenization("token");
/* 109 */     this.spec_tok = this.spec_al.getTokenization("token");
/*     */   }
/*     */   
/*     */   public static void main(String[] args) throws Exception {
/* 113 */     Parser parser = new PosixParser();
/* 114 */     CommandLine params = parser.parse(OPTIONS, args);
/* 115 */     Properties sqlParams = new Properties();
/* 116 */     sqlParams.setProperty("host", params.getOptionValue("host", "pfam"));
/* 117 */     sqlParams.setProperty("user", params.getOptionValue("host", "pfam"));
/* 118 */     sqlParams.setProperty("database", params.getOptionValue("database", "pfam"));
/* 119 */     sqlParams.setProperty("password", params.getOptionValue("database", "mafp1"));
/* 120 */     repos = new File(params.getOptionValue("repository", "/nfs/team71/phd/lc1/Data/lc1/"));
/* 121 */     DomainAlphabet alph = PfamAlphabet.makeAlphabet(repos);
/* 122 */     SpeciesAlphabet spec_al = SpeciesAlphabet.makeAlphabet(repos, null);
/* 123 */     SymbolList sl1 = spec_al.taxaToList(spec_al.getTokenization("token").parseToken("6239"));
/* 124 */     System.err.println(sl1.seqString());
/*     */     
/* 126 */     sl1 = spec_al.taxaToList(spec_al.getTokenization("token").parseToken("7227"));
/* 127 */     System.err.println(sl1.seqString());
/* 128 */     System.exit(0);
/* 129 */     System.err.println("spec _al null " + spec_al == null);
/* 130 */     ContextPfamAnalysis analy = new ContextPfamAnalysis(sqlParams, alph, spec_al);
/* 131 */     File f = new File("pfamDetails");
/* 132 */     if ((f.exists()) && (f.length() > 0L)) {
/* 133 */       analy.readData(f);
/*     */     }
/*     */     else {
/* 136 */       analy.readData();
/*     */     }
/* 138 */     analy.printToFile(f);
/* 139 */     analy.graphNewPostives();
/*     */   }
/*     */   
/*     */   class SymbolComp
/*     */     implements Comparator
/*     */   {
/*     */     int index;
/*     */     
/* 147 */     SymbolComp(int index) { this.index = index; }
/*     */     
/*     */     public int compare(Object o1, Object o2) {
/* 150 */       int i1 = ContextPfamAnalysis.this.counts[this.index].get(o1);
/* 151 */       int i2 = ContextPfamAnalysis.this.counts[this.index].get(o2);
/* 152 */       if (i1 != i2) return i1 < i2 ? 1 : -1;
/* 153 */       return 0;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/* 158 */   List l = new ArrayList();
/*     */   
/*     */   class GraphLengthHistogram { GraphLengthHistogram() {}
/*     */     
/* 162 */     public TIntIntHashMap[] getHistograms(Iterator it, int width) { TIntIntHashMap[] hist = { new TIntIntHashMap(), 
/* 163 */         new TIntIntHashMap() };
/* 164 */       int[] totals = new int[2];
/* 165 */       int[] totC = new int[2];
/* 166 */       while (it.hasNext()) {
/* 167 */         Symbol sym = (Symbol)it.next();
/* 168 */         int count = 
/* 169 */           ContextPfamAnalysis.this.counts[1].get(sym);
/* 170 */         int countT = 
/* 171 */           ContextPfamAnalysis.this.counts[0].get(sym);
/*     */         
/* 173 */         int modelL = ((Integer)sym.getAnnotation().getProperty("modelLength")).intValue();
/* 174 */         totals[0] += count * modelL;totC[0] += count;
/* 175 */         totals[1] += countT * modelL;totC[1] += countT;
/* 176 */         modelL = width * (int)Math.floor(modelL / width);
/*     */         
/* 178 */         hist[0].put(modelL, hist[0].get(modelL) + count);
/* 179 */         hist[1].put(modelL, hist[1].get(modelL) + countT);
/*     */       }
/* 181 */       System.err.println("weighted average length: context " + totals[0] / totC[0]);
/* 182 */       System.err.println("weighted average length: all " + totals[1] / totC[1]);
/* 183 */       return hist;
/*     */     }
/*     */     
/*     */     public XYSeriesCollection getGraph(TIntIntHashMap[] hist, String[] names, double width)
/*     */     {
/* 188 */       XYSeriesCollection collection = new XYSeriesCollection();
/* 189 */       for (int i = 0; i < hist.length; i++) {
/* 190 */         XYSeries res = new XYSeries(names[i]);
/* 191 */         int total = 0;
/* 192 */         int[] vals = hist[i].getValues();
/* 193 */         for (int j = 0; j < vals.length; j++) {
/* 194 */           total += vals[j];
/*     */         }
/* 196 */         double tot = total;
/* 197 */         hist[i].forEachEntry(new ContextPfamAnalysis.2(this, res, width, tot));
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 204 */         collection.addSeries(res);
/*     */       }
/* 206 */       return collection;
/*     */     }
/*     */     
/*     */     public JFreeChart getGraph(XYSeriesCollection datas) throws Exception {
/* 210 */       JFreeChart chart = ChartFactory.createXYLineChart(
/* 211 */         "Frequency Distribution of Model Length", 
/* 212 */         "Model length", 
/* 213 */         "Frequency", 
/* 214 */         datas, 
/* 215 */         PlotOrientation.VERTICAL, 
/* 216 */         true, 
/* 217 */         true, 
/* 218 */         false);
/*     */       
/*     */ 
/* 221 */       XYPlot plot = chart.getXYPlot();
/* 222 */       StandardXYItemRenderer renderer = (StandardXYItemRenderer)plot.getRenderer();
/* 223 */       renderer.setPlotShapes(false);
/* 224 */       return chart;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public void graphFrequencyDistribution(Iterator it)
/*     */     throws Exception
/*     */   {
/* 232 */     GraphLengthHistogram glh = new GraphLengthHistogram();
/* 233 */     JFreeChart chart = glh.getGraph(glh.getGraph(glh.getHistograms(it, 10), new String[] { "Context domains", "All domains" }, 10.0D));
/* 234 */     chart.setBackgroundPaint(Color.white);
/* 235 */     ChartPanel chartPanel = new ChartPanel(chart);
/* 236 */     chartPanel.setPreferredSize(new Dimension(500, 270));
/* 237 */     JFrame jp = new JFrame();
/* 238 */     jp.setSize(800, 800);
/* 239 */     jp.getContentPane().add(chartPanel);
/* 240 */     jp.setVisible(true);
/*     */   }
/*     */   
/*     */   class GraphZipfCurve
/*     */   {
/*     */     GraphZipfCurve() {}
/*     */     
/*     */     private XYSeriesCollection getGraphData(List l) {
/* 248 */       XYSeriesCollection collection = new XYSeriesCollection();
/* 249 */       XYSeries res = new XYSeries("frequency");
/* 250 */       for (int i = 0; i < l.size(); i++) {
/* 251 */         Symbol sym = (Symbol)l.get(i);
/* 252 */         int count = ContextPfamAnalysis.this.counts[0].get(sym);
/* 253 */         res.add(i + 1, count);
/*     */       }
/*     */       
/* 256 */       collection.addSeries(res);
/*     */       
/* 258 */       return collection;
/*     */     }
/*     */     
/* 261 */     private double log10(double a) { return Math.log(a) / Math.log(10.0D); }
/*     */     
/*     */     private double exp10(double a) {
/* 264 */       return Math.pow(10.0D, a);
/*     */     }
/*     */     
/*     */     public JFreeChart getGraph(List l) throws Exception
/*     */     {
/* 269 */       int last_index = l.size();
/* 270 */       Symbol first = (Symbol)l.get(0);Symbol last = (Symbol)l.get(last_index - 1);
/* 271 */       int[] min = { 1, ContextPfamAnalysis.this.counts[0].get(first) };
/* 272 */       int[] max1 = { last_index, ContextPfamAnalysis.this.counts[0].get(last) };
/*     */       
/* 274 */       double alpha = 1.0D;
/* 275 */       double[] max = { l.size(), exp10(log10(min[1]) - alpha * log10(l.size())) };
/* 276 */       XYSeriesCollection dataset = getGraphData(l);
/* 277 */       NumberAxis xAxis = new LogarithmicAxis("Rank");
/* 278 */       xAxis.setAutoRange(false);
/* 279 */       XYSeries straight = new XYSeries(Format.sprintf("log count = %4.2f - %4.2f * log rank", 
/* 280 */         new Object[] { new Double(log10(min[1])), new Double(alpha) }));
/* 281 */       straight.add(min[0], min[1]);
/* 282 */       straight.add(max[0], max[1]);
/* 283 */       XYSeriesCollection straightC = new XYSeriesCollection();
/* 284 */       straightC.addSeries(straight);
/* 285 */       NumberAxis yAxis = new LogarithmicAxis("Count");
/* 286 */       yAxis.setAutoRange(false);
/* 287 */       xAxis.setRange(min[0], max[0]);
/* 288 */       yAxis.setRange(max[1], min[1]);
/* 289 */       XYPlot plot = new XYPlot(dataset, xAxis, yAxis, null);
/* 290 */       XYToolTipGenerator toolTipGenerator = new StandardXYToolTipGenerator();
/* 291 */       XYURLGenerator urlGenerator = new StandardXYURLGenerator();
/* 292 */       StandardXYItemRenderer renderer = new StandardXYItemRenderer(
/* 293 */         1, toolTipGenerator, urlGenerator);
/*     */       
/* 295 */       renderer.setShapesFilled(Boolean.TRUE);
/* 296 */       renderer.setPlotShapes(true);
/* 297 */       plot.setRenderer(renderer);
/* 298 */       plot.setOrientation(PlotOrientation.VERTICAL);
/* 299 */       JFreeChart chart = new JFreeChart("Frequency of domain usage vs rank", JFreeChart.DEFAULT_TITLE_FONT, plot, true);
/* 300 */       plot.setDataset(1, straightC);
/*     */       
/*     */ 
/*     */ 
/* 304 */       StandardXYItemRenderer renderer2 = new StandardXYItemRenderer();
/* 305 */       renderer2.setToolTipGenerator(new StandardXYToolTipGenerator());
/* 306 */       plot.setRenderer(1, renderer2);
/* 307 */       renderer2.setPlotShapes(false);
/* 308 */       renderer2.setPlotLines(true);
/* 309 */       plot.setDatasetRenderingOrder(DatasetRenderingOrder.REVERSE);
/* 310 */       chart.setBackgroundPaint(Color.WHITE);
/* 311 */       return chart;
/*     */     }
/*     */   }
/*     */   
/*     */   public void graphZipfCurve() throws Exception
/*     */   {
/* 317 */     Collections.sort(this.l, new SymbolComp(0));
/* 318 */     GraphZipfCurve glh = new GraphZipfCurve();
/* 319 */     JFreeChart chart = glh.getGraph(this.l);
/* 320 */     chart.setBackgroundPaint(Color.white);
/* 321 */     ChartPanel chartPanel = new ChartPanel(chart);
/* 322 */     chartPanel.setPreferredSize(new Dimension(500, 270));
/* 323 */     JFrame jp = new JFrame();
/* 324 */     jp.setSize(800, 800);
/* 325 */     jp.getContentPane().add(chartPanel);
/* 326 */     jp.setVisible(true);
/*     */   }
/*     */   
/*     */   public void graphNewPostives() throws Exception
/*     */   {
/* 331 */     GraphNewPositives gnp = new GraphNewPositives();
/* 332 */     JFreeChart chart = gnp.getChart(gnp.getData());
/* 333 */     ApplicationFrame demo1 = new ApplicationFrame("") {};
/* 340 */     demo1.pack();
/* 341 */     RefineryUtilities.centerFrameOnScreen(demo1);
/* 342 */     demo1.setVisible(true);
/*     */   }
/*     */   
/*     */   class GraphNewPositives
/*     */   {
/*     */     GraphNewPositives() {}
/*     */     
/*     */     public JFreeChart getChart(CategoryDataset[] data) {
/* 350 */       JFreeChart chart = ChartFactory.createBarChart(
/* 351 */         "New domain predictions, top 20 domains ", 
/* 352 */         "Domain", 
/* 353 */         "# No. new predictions", 
/* 354 */         data[0], 
/* 355 */         PlotOrientation.VERTICAL, 
/* 356 */         false, 
/* 357 */         true, 
/* 358 */         false);
/*     */       
/*     */ 
/* 361 */       chart.setBackgroundPaint(Color.white);
/*     */       
/* 363 */       CategoryPlot plot = chart.getCategoryPlot();
/* 364 */       plot.setDomainAxisLocation(AxisLocation.BOTTOM_OR_RIGHT);
/* 365 */       plot.setDataset(1, data[1]);
/* 366 */       plot.mapDatasetToRangeAxis(1, 1);
/* 367 */       CategoryAxis domainAxis = plot.getDomainAxis();
/* 368 */       domainAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_90);
/* 369 */       ValueAxis axis2 = new NumberAxis("New domain predictions as %age of existing domains");
/* 370 */       plot.setRangeAxis(1, axis2);
/* 371 */       LineAndShapeRenderer renderer2 = new LineAndShapeRenderer();
/* 372 */       renderer2.setToolTipGenerator(new StandardCategoryToolTipGenerator());
/* 373 */       plot.setRenderer(1, renderer2);
/* 374 */       plot.setDatasetRenderingOrder(DatasetRenderingOrder.FORWARD);
/* 375 */       return chart;
/*     */     }
/*     */     
/*     */     public CategoryDataset[] getData() throws Exception {
/* 379 */       DefaultCategoryDataset total = new DefaultCategoryDataset();
/* 380 */       DefaultCategoryDataset perc = new DefaultCategoryDataset();
/* 381 */       Collections.sort(ContextPfamAnalysis.this.l, new ContextPfamAnalysis.SymbolComp(ContextPfamAnalysis.this, 1));
/* 382 */       int tot = 0;
/* 383 */       for (Iterator it = ContextPfamAnalysis.this.l.iterator(); it.hasNext();) {
/* 384 */         Symbol sym = (Symbol)it.next();
/* 385 */         tot += ContextPfamAnalysis.this.counts[1].get(sym);
/*     */       }
/* 387 */       int j = 0;
/* 388 */       int cum = 0;
/* 389 */       for (Iterator it = ContextPfamAnalysis.this.l.iterator(); it.hasNext(); j++) {
/* 390 */         Symbol sym = (Symbol)it.next();
/* 391 */         int count = ContextPfamAnalysis.this.counts[1].get(sym);
/* 392 */         cum += count;
/* 393 */         double cumu = cum / tot;
/* 394 */         if (count > 0) System.err.println(j + " " + cumu);
/* 395 */         int totalCount = ContextPfamAnalysis.this.counts[0].get(sym);
/* 396 */         if (j < 20) {
/* 397 */           total.addValue(count, "total new", 
/* 398 */             sym.getAnnotation().containsProperty("clan_id") ? 
/* 399 */             (String)sym.getAnnotation().getProperty("clan_id") : 
/*     */             
/* 401 */             (String)sym.getAnnotation().getProperty("pfamA_id"));
/* 402 */           perc.addValue(100.0D * (count / totalCount), "total as percentage of current", sym.getName());
/*     */         } }
/* 404 */       return new CategoryDataset[] { total, perc };
/*     */     }
/*     */   }
/*     */   
/*     */   public void printToFile(File f) throws Exception
/*     */   {
/* 410 */     PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(f)));
/* 411 */     for (Iterator it = this.alph.iterator(); it.hasNext();) {
/* 412 */       Symbol sym = (Symbol)it.next();
/* 413 */       pw.println(sym.getName() + "\t" + this.counts[1].get(sym) + 
/* 414 */         "\t" + this.counts[0].get(sym) + "\t" + 
/* 415 */         sym.getAnnotation().getProperty("modelLength"));
/*     */     }
/* 417 */     pw.close();
/*     */   }
/*     */   
/*     */   public void readData(File f) throws Exception {
/* 421 */     for (Iterator it = SheetIO.read(f, "\t"); it.hasNext();) {
/* 422 */       List row = (List)it.next();
/* 423 */       Symbol sym = this.token.parseToken((String)row.get(0));
/* 424 */       if (sym.getAnnotation().containsProperty("clan"))
/* 425 */         sym = (Symbol)sym.getAnnotation().getProperty("clan");
/* 426 */       int count = Integer.parseInt((String)row.get(1));
/* 427 */       int totalCount = Integer.parseInt((String)row.get(2));
/* 428 */       if (totalCount != 0) {
/* 429 */         int modelLength = Integer.parseInt((String)row.get(3));
/* 430 */         this.counts[0].put(sym, totalCount);
/* 431 */         this.counts[1].put(sym, count);
/* 432 */         this.l.add(sym);
/*     */       } } }
/*     */   
/* 435 */   static boolean species = true;
/*     */   
/*     */   static File repos;
/* 438 */   TObjectIntHashMap[] counts = { new TObjectIntHashMap(), 
/* 439 */     new TObjectIntHashMap() };
/*     */   
/*     */   public void readData() throws Exception {
/* 442 */     System.err.println("species " + this.spec_al.size());
/* 443 */     FiniteAlphabet alphabet = species ? this.spec_al : this.alph;
/* 444 */     for (Iterator it = alphabet.iterator(); it.hasNext();) {
/* 445 */       Object obj = it.next();
/* 446 */       if (species) obj = ((Symbol)obj).getName();
/* 447 */       this.l.add(obj);
/* 448 */       System.err.println(obj);
/* 449 */       this.counts[0].put(obj, 0);
/* 450 */       this.counts[1].put(obj, 0);
/*     */     }
/* 452 */     int[] coverage = new int[2];
/* 453 */     Set context_seqs = new HashSet();
/*     */     
/* 455 */     SequenceIterator seqIt = IndexedPfamDB.SequenceIterator(new File(repos, "pfamA_reg_full_context"), (DomainAlphabet)this.alph, "\\s+", 0, 1);
/*     */     Iterator feat;
/* 454 */     for (; 
/*     */         
/* 456 */           seqIt.hasNext(); 
/*     */         
/*     */ 
/*     */ 
/* 460 */         feat.hasNext())
/*     */     {
/* 457 */       Sequence seq = seqIt.nextSequence();
/* 458 */       Annotation annot = seq.getAnnotation();
/* 459 */       context_seqs.add(seq.getName());
/* 460 */       feat = seq.features(); continue;
/* 461 */       Domain dom = (Domain)feat.next();
/* 462 */       Symbol sym = 
/* 463 */         dom.getSymbol();
/*     */       
/*     */ 
/*     */ 
/* 467 */       this.counts[1].increment(species ? 
/* 468 */         this.spec_al.root() : annot.containsProperty("species") ? this.spec_tok.parseToken((String)annot.getProperty("species")) : 
/* 469 */         sym);
/* 470 */       coverage[1] += dom.getLocation().getMax() - dom.getLocation().getMin();
/*     */     }
/*     */     
/* 473 */     System.err.println("annotated " + context_seqs.size() + " proteins");
/*     */     
/* 475 */     SequenceIterator seqIt = IndexedPfamDB.SequenceIterator(new File(repos, "pfamA_reg_full_ls"), (DomainAlphabet)this.alph, "\\s+", 0, 1);
/*     */     Iterator feat;
/* 474 */     for (; 
/*     */         
/* 476 */           seqIt.hasNext(); 
/*     */         
/*     */ 
/*     */ 
/* 480 */         feat.hasNext())
/*     */     {
/* 477 */       Sequence seq = seqIt.nextSequence();
/* 478 */       context_seqs.remove(seq.getName());
/* 479 */       Annotation annot = seq.getAnnotation();
/* 480 */       feat = seq.features(); continue;
/* 481 */       Domain dom = (Domain)feat.next();
/* 482 */       Symbol sym = dom.getSymbol();
/*     */       
/*     */ 
/* 485 */       this.counts[0].increment(species ? 
/* 486 */         this.spec_al.root() : annot.containsProperty("species") ? this.spec_tok.parseToken((String)annot.getProperty("species")) : sym);
/* 487 */       coverage[0] += dom.getLocation().getMax() - dom.getLocation().getMin();
/*     */     }
/*     */     
/*     */ 
/* 491 */     System.err.println("coverage context " + coverage[1]);
/* 492 */     System.err.println("coverage all " + coverage[0]);
/* 493 */     System.err.println("new proteins " + context_seqs.size());
/*     */   }
/*     */ }


/* Location:              /home/lachlan/Desktop/psilc1.21/psilc.jar!/lc1/domains/ContextPfamAnalysis.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */