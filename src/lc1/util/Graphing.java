/*     */ package lc1.util;
/*     */ 
/*     */ import java.awt.Color;
/*     */ import java.awt.Container;
/*     */ import java.awt.Dimension;
/*     */ import java.io.File;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import javax.swing.JFrame;
/*     */ import lc1.domainseq.Domain;
/*     */ import org.jfree.chart.ChartPanel;
/*     */ import org.jfree.chart.JFreeChart;
/*     */ import org.jfree.chart.axis.LogarithmicAxis;
/*     */ import org.jfree.chart.axis.NumberAxis;
/*     */ import org.jfree.chart.labels.StandardXYToolTipGenerator;
/*     */ import org.jfree.chart.labels.XYToolTipGenerator;
/*     */ import org.jfree.chart.plot.DatasetRenderingOrder;
/*     */ import org.jfree.chart.plot.PlotOrientation;
/*     */ import org.jfree.chart.plot.XYPlot;
/*     */ import org.jfree.chart.renderer.StandardXYItemRenderer;
/*     */ import org.jfree.chart.urls.StandardXYURLGenerator;
/*     */ import org.jfree.chart.urls.XYURLGenerator;
/*     */ import org.jfree.data.XYSeries;
/*     */ import org.jfree.data.XYSeriesCollection;
/*     */ import org.omegahat.Simulation.MCMC.Listeners.Histogram;
/*     */ 
/*     */ 
/*     */ 
/*     */ public class Graphing
/*     */ {
/*  34 */   public static void main(String[] args) throws Exception { main1(new File(args[0]), 3, "PSILC-prot/dom"); }
/*     */   
/*     */   public static void main1(File f, int col, String title) throws Exception {
/*  37 */     Iterator it = SheetIO.read(f, "\\s+");
/*  38 */     int min = 65036;
/*  39 */     int max = 500;
/*  40 */     int step = 1000;
/*  41 */     Histogram human = new Histogram(min, max, step);
/*  42 */     Histogram mouse = new Histogram(min, max, step);
/*  43 */     Histogram rat = new Histogram(min, max, step);
/*  44 */     Histogram chimp = new Histogram(min, max, step);
/*  45 */     while (it.hasNext()) {
/*  46 */       List row = (List)it.next();
/*  47 */       String name = (String)row.get(1);
/*  48 */       double sc = Double.parseDouble((String)row.get(col));
/*     */       
/*     */ 
/*  51 */       if (name.startsWith("ENST")) {
/*  52 */         human.update(sc);
/*     */       }
/*  54 */       else if (name.startsWith("ENSMUST")) {
/*  55 */         mouse.update(sc);
/*     */       }
/*  57 */       else if (name.startsWith("ENSRNOT")) {
/*  58 */         rat.update(sc);
/*     */       }
/*  60 */       else if (name.startsWith("ENSP")) {
/*  61 */         chimp.update(sc);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static Histogram getHistogramFromFile(File f)
/*     */   {
/*     */     try
/*     */     {
/*  75 */       List sh = (List)SheetIO.toCollection(SheetIO.read(f, "\\s+"), 
/*  76 */         new ArrayList());
/*     */       
/*  78 */       int tot = Integer.parseInt((String)((List)sh.get(sh.size() - 3))
/*  79 */         .get(6));
/*  80 */       double min = Double.parseDouble((String)((List)sh.get(1)).get(0));
/*  81 */       double step = 
/*  82 */         Double.parseDouble((String)((List)sh.get(1)).get(2));
/*  83 */       double max = Double.parseDouble((String)((List)sh
/*  84 */         .get(sh.size() - 3)).get(2));
/*  85 */       double[] data = new double[tot];
/*  86 */       int j = 0;
/*  87 */       for (int i = 1; i < sh.size() - 2; i++) {
/*  88 */         double data_1 = Double.parseDouble((String)((List)sh.get(i))
/*  89 */           .get(0));
/*  90 */         double data_2 = Double.parseDouble((String)((List)sh.get(i))
/*  91 */           .get(2));
/*  92 */         double data_i = (data_1 + data_2) / 2.0D;
/*  93 */         int count = 
/*  94 */           Integer.parseInt((String)((List)sh.get(i)).get(4));
/*  95 */         for (int k = 0; k < count; k++) {
/*  96 */           data[(j + k)] = data_i;
/*     */         }
/*  98 */         j += count;
/*     */       }
/* 100 */       Histogram hist = new Histogram(min, max, step);
/* 101 */       hist.update(data);
/*     */       
/* 103 */       return hist;
/*     */     } catch (Exception exc) {
/* 105 */       exc.printStackTrace(); }
/* 106 */     return null;
/*     */   }
/*     */   
/*     */   public static Histogram getHistogramFromHmmerFile(File f)
/*     */   {
/*     */     try
/*     */     {
/* 113 */       List sh = (List)SheetIO.toCollection(SheetIO.read(f, "\\s+"), 
/* 114 */         new ArrayList());
/* 115 */       double min = Double.parseDouble((String)((List)sh.get(3)).get(1));
/* 116 */       double step = 1.0D;
/* 117 */       double max = Double.parseDouble((String)((List)sh
/* 118 */         .get(sh.size() - 9)).get(1));
/* 119 */       int total = 0;
/* 120 */       for (int i = 3; i <= sh.size() - 9; i++) {
/* 121 */         total += Integer.parseInt((String)((List)sh.get(i)).get(2));
/*     */       }
/* 123 */       double[] data = new double[total];
/* 124 */       int j = 0;
/* 125 */       for (int i = 3; i <= sh.size() - 9; i++) {
/* 126 */         double data_i = Double.parseDouble((String)((List)sh.get(i))
/* 127 */           .get(1));
/* 128 */         int count = 
/* 129 */           Integer.parseInt((String)((List)sh.get(i)).get(2));
/* 130 */         for (int k = 0; k < count; k++) {
/* 131 */           data[(j + k)] = data_i;
/*     */         }
/* 133 */         j += count;
/*     */       }
/* 135 */       Histogram hist = new Histogram(min, max, step);
/* 136 */       hist.update(data);
/* 137 */       return hist;
/*     */     } catch (Exception exc) {
/* 139 */       exc.printStackTrace(); }
/* 140 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */   public static Histogram getHistogramFromHmmerFileOut(File f)
/*     */     throws Exception
/*     */   {
/* 147 */     Map m = lc1.dp.HmmerLauncher.parseHmmer(new java.io.BufferedReader(
/* 148 */       new java.io.FileReader(f)), 
/* 149 */       org.biojava.bio.seq.ProteinTools.getAlphabet());
/* 150 */     double[] data2 = new double[m.values().size()];
/* 151 */     int i2 = 0;
/* 152 */     int min = 0;
/* 153 */     int max = 0;
/* 154 */     for (Iterator it = m.values().iterator(); it.hasNext();) {
/* 155 */       double score = ((Domain)it.next()).getScore();
/*     */       
/* 157 */       data2[i2] = score;
/* 158 */       i2++;
/*     */       
/* 160 */       if (score < min)
/* 161 */         min = (int)Math.floor(score);
/* 162 */       if (score > max) {
/* 163 */         max = (int)Math.floor(score) + 1;
/*     */       }
/*     */     }
/* 166 */     Histogram hist2 = new Histogram(min, max, 1.0D);
/* 167 */     hist2.update(data2);
/* 168 */     return hist2;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static double[][] toDoubleArray(List l)
/*     */   {
/* 175 */     double[][] res = new double[l.size()][2];
/* 176 */     for (int i = 0; i < l.size(); i++) {
/* 177 */       System.arraycopy((double[])l.get(i), 0, res[i], 0, 2);
/*     */     }
/* 179 */     return res;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static XYSeries plot(Histogram hist, String name)
/*     */   {
/* 190 */     double[][] cumArray = hist.cumarray();
/*     */     
/* 192 */     XYSeries newD = new XYSeries(name);
/*     */     
/* 194 */     int zero_count = 0;
/*     */     
/* 196 */     for (int i = 0; i < cumArray.length; i++) {
/* 197 */       if (cumArray[i][0] < 0.0D) {
/* 198 */         zero_count++;
/*     */       } else {
/* 200 */         newD.add(cumArray[i][0], 
/* 201 */           1.0D - cumArray[i][3]);
/*     */       }
/*     */     }
/*     */     
/* 205 */     return newD;
/*     */   }
/*     */   
/*     */   public static double[] getAreaUnderCurve(XYSeriesCollection datas) {
/* 209 */     double[] area = new double[datas.getSeriesCount()];
/* 210 */     for (int i = 0; i < area.length; i++) {
/* 211 */       area[i] = getArea(datas.getSeries(i));
/*     */     }
/* 213 */     return area;
/*     */   }
/*     */   
/*     */   public static void plotROCDirectComparison(XYSeriesCollection dataset, String x, String y, boolean log_log) throws Exception {
/* 217 */     NumberAxis xAxis = log_log ? new LogarithmicAxis(x) : new NumberAxis(x);
/*     */     
/*     */ 
/* 220 */     NumberAxis yAxis = log_log ? new LogarithmicAxis(y) : new NumberAxis(y);
/*     */     
/* 222 */     if (log_log) {
/* 223 */       XYSeries straight = new XYSeries("x=y");
/* 224 */       straight.add(0.001D, 0.001D);
/* 225 */       straight.add(1000.0D, 1000.0D);
/*     */       
/* 227 */       xAxis.setAutoRange(false);
/* 228 */       yAxis.setAutoRange(false);
/* 229 */       xAxis.setRange(0.001D, 1000.0D);
/* 230 */       yAxis.setRange(0.001D, 1000.0D);
/*     */     }
/*     */     else {
/* 233 */       double min = Double.POSITIVE_INFINITY;
/* 234 */       double max = Double.NEGATIVE_INFINITY;
/* 235 */       for (int i = 0; i < dataset.getSeriesCount(); i++) {
/* 236 */         XYSeries s = dataset.getSeries(i);
/* 237 */         for (int j = 0; j < s.getItemCount(); j++) {
/* 238 */           double x_j = s.getXValue(j).doubleValue();
/* 239 */           double y_j = s.getYValue(j).doubleValue();
/* 240 */           if (x_j > max) max = x_j;
/* 241 */           if (y_j > max) max = y_j;
/* 242 */           if (x_j < min) min = x_j;
/* 243 */           if (y_j < min) min = y_j;
/*     */         }
/*     */       }
/* 246 */       XYSeries straight = new XYSeries("x=y");
/* 247 */       straight.add(min, min);
/* 248 */       straight.add(max, max);
/*     */     }
/* 250 */     XYSeriesCollection straightC = new XYSeriesCollection();
/*     */     
/* 252 */     XYPlot plot = new XYPlot(dataset, xAxis, yAxis, null);
/*     */     
/* 254 */     XYToolTipGenerator toolTipGenerator = new StandardXYToolTipGenerator();
/* 255 */     XYURLGenerator urlGenerator = new StandardXYURLGenerator();
/* 256 */     StandardXYItemRenderer renderer = new StandardXYItemRenderer(
/* 257 */       1, toolTipGenerator, urlGenerator);
/*     */     
/* 259 */     renderer.setShapesFilled(Boolean.TRUE);
/* 260 */     renderer.setPlotShapes(true);
/* 261 */     plot.setRenderer(renderer);
/* 262 */     plot.setOrientation(PlotOrientation.VERTICAL);
/* 263 */     JFreeChart chart = new JFreeChart("HMMER+context+taxonomy vs HMMER evalues for C2-set", JFreeChart.DEFAULT_TITLE_FONT, plot, true);
/* 264 */     if (straightC != null) { plot.setDataset(1, straightC);
/*     */     }
/*     */     
/*     */ 
/* 268 */     StandardXYItemRenderer renderer2 = new StandardXYItemRenderer();
/* 269 */     renderer2.setToolTipGenerator(new StandardXYToolTipGenerator());
/* 270 */     plot.setRenderer(1, renderer2);
/* 271 */     renderer2.setPlotShapes(false);
/* 272 */     renderer2.setPlotLines(true);
/* 273 */     chart.setBackgroundPaint(Color.WHITE);
/* 274 */     plot.setDatasetRenderingOrder(DatasetRenderingOrder.REVERSE);
/*     */     
/* 276 */     ChartPanel chartPanel = new ChartPanel(chart);
/* 277 */     chartPanel.setPreferredSize(new Dimension(500, 270));
/* 278 */     JFrame jp = new JFrame();
/* 279 */     jp.setSize(800, 800);
/* 280 */     jp.getContentPane().add(chartPanel);
/* 281 */     jp.setVisible(true);
/*     */   }
/*     */   
/*     */   public static void plotErrorCurves(XYSeriesCollection[] dataset) throws Exception {
/* 285 */     NumberAxis xAxis = new LogarithmicAxis("E-value");
/* 286 */     xAxis.setAutoRange(false);
/* 287 */     NumberAxis yAxis = new NumberAxis("False Negative");
/* 288 */     yAxis.setAutoRangeIncludesZero(false);
/* 289 */     xAxis.setRange(0.001D, 1.0D);
/*     */     
/* 291 */     XYPlot plot = new XYPlot(dataset[0], xAxis, yAxis, null);
/* 292 */     plot.mapDatasetToRangeAxis(0, 0);
/* 293 */     XYToolTipGenerator toolTipGenerator = new StandardXYToolTipGenerator();
/* 294 */     XYURLGenerator urlGenerator = new StandardXYURLGenerator();
/* 295 */     StandardXYItemRenderer renderer = new StandardXYItemRenderer(
/* 296 */       1, toolTipGenerator, urlGenerator);
/*     */     
/* 298 */     renderer.setShapesFilled(Boolean.TRUE);
/* 299 */     renderer.setPlotShapes(false);
/* 300 */     renderer.setPlotLines(true);
/* 301 */     plot.setRenderer(renderer);
/* 302 */     plot.setOrientation(PlotOrientation.VERTICAL);
/* 303 */     JFreeChart chart = new JFreeChart("Error curves", JFreeChart.DEFAULT_TITLE_FONT, plot, true);
/*     */     
/* 305 */     plot.setDataset(1, dataset[1]);
/*     */     
/* 307 */     NumberAxis axis2 = new NumberAxis("False Positive");
/* 308 */     plot.setRangeAxis(1, axis2);
/* 309 */     plot.mapDatasetToRangeAxis(1, 1);
/* 310 */     StandardXYItemRenderer renderer2 = new StandardXYItemRenderer();
/* 311 */     renderer2.setToolTipGenerator(new StandardXYToolTipGenerator());
/* 312 */     plot.setRenderer(1, renderer);
/* 313 */     renderer2.setPlotShapes(false);
/* 314 */     chart.setBackgroundPaint(Color.white);
/* 315 */     plot.setDatasetRenderingOrder(DatasetRenderingOrder.REVERSE);
/* 316 */     ChartPanel chartPanel = new ChartPanel(chart);
/* 317 */     chartPanel.setPreferredSize(new Dimension(500, 270));
/* 318 */     JFrame jp = new JFrame();
/* 319 */     jp.setSize(800, 800);
/* 320 */     jp.getContentPane().add(chartPanel);
/* 321 */     jp.setVisible(true);
/*     */   }
/*     */   
/*     */   public static void plotROCCurves(XYSeriesCollection datas, double[] min, double[] max, boolean log) {
/* 325 */     NumberAxis xAxis = log ? 
/* 326 */       new LogarithmicAxis("Error") : 
/* 327 */       new NumberAxis("Error");
/*     */     
/* 329 */     NumberAxis yAxis = new NumberAxis("Coverage");
/* 330 */     if (min != null) {
/* 331 */       xAxis.setAutoRange(false);
/* 332 */       yAxis.setAutoRangeIncludesZero(false);
/* 333 */       xAxis.setRange(min[0], max[0]);
/* 334 */       yAxis.setRange(min[1], max[1]);
/*     */     }
/*     */     
/* 337 */     XYPlot plot = new XYPlot(datas, xAxis, yAxis, null);
/* 338 */     plot.mapDatasetToRangeAxis(0, 0);
/* 339 */     XYToolTipGenerator toolTipGenerator = new StandardXYToolTipGenerator();
/* 340 */     XYURLGenerator urlGenerator = new StandardXYURLGenerator();
/* 341 */     StandardXYItemRenderer renderer = new StandardXYItemRenderer(
/* 342 */       1, toolTipGenerator, urlGenerator);
/*     */     
/* 344 */     renderer.setShapesFilled(Boolean.TRUE);
/* 345 */     renderer.setPlotShapes(false);
/* 346 */     renderer.setPlotLines(true);
/* 347 */     plot.setRenderer(renderer);
/* 348 */     plot.setOrientation(PlotOrientation.VERTICAL);
/* 349 */     JFreeChart chart = new JFreeChart("Coverage vs Error", JFreeChart.DEFAULT_TITLE_FONT, plot, true);
/* 350 */     chart.setBackgroundPaint(Color.white);
/* 351 */     ChartPanel chartPanel = new ChartPanel(chart);
/* 352 */     chartPanel.setPreferredSize(new Dimension(500, 270));
/* 353 */     JFrame jp = new JFrame();
/* 354 */     jp.setDefaultCloseOperation(3);
/* 355 */     jp.setSize(800, 800);
/* 356 */     jp.getContentPane().add(chartPanel);
/* 357 */     jp.setVisible(true);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static double getArea(XYSeries data)
/*     */   {
/* 367 */     double area = 0.0D;
/* 368 */     int totalA = data.getYValue(data.getItemCount() - 1).intValue() * data.getXValue(data.getItemCount() - 1).intValue();
/* 369 */     for (int j = 1; j < data.getItemCount(); j++) {
/* 370 */       double height = 0.5D * (data.getYValue(j).doubleValue() + data.getYValue(j - 1).doubleValue());
/* 371 */       double width = data.getXValue(j).doubleValue() - data.getXValue(j - 1).doubleValue();
/* 372 */       area += height * width;
/*     */     }
/* 374 */     return area / totalA;
/*     */   }
/*     */   
/*     */ 
/*     */   public static ChartPanel plotPercentageAbove(XYSeries[] percentageAboveThreshold, String title)
/*     */   {
/* 380 */     XYSeriesCollection dataset = new XYSeriesCollection();
/* 381 */     for (int i = 0; i < percentageAboveThreshold.length; i++) {
/* 382 */       dataset.addSeries(percentageAboveThreshold[i]);
/*     */     }
/* 384 */     NumberAxis xAxis = 
/* 385 */       new LogarithmicAxis("E-value");
/* 386 */     xAxis.setAutoRange(false);
/* 387 */     NumberAxis yAxis = new NumberAxis("Percentage of scores above threshold");
/*     */     
/* 389 */     xAxis.setRange(0.001D, 1000.0D);
/*     */     
/* 391 */     XYPlot plot = new XYPlot(dataset, xAxis, yAxis, null);
/* 392 */     plot.mapDatasetToRangeAxis(0, 0);
/* 393 */     XYToolTipGenerator toolTipGenerator = new StandardXYToolTipGenerator();
/* 394 */     XYURLGenerator urlGenerator = new StandardXYURLGenerator();
/* 395 */     StandardXYItemRenderer renderer = new StandardXYItemRenderer(
/* 396 */       1, toolTipGenerator, urlGenerator);
/*     */     
/* 398 */     renderer.setShapesFilled(Boolean.TRUE);
/* 399 */     renderer.setPlotShapes(false);
/* 400 */     renderer.setPlotLines(true);
/* 401 */     plot.setRenderer(renderer);
/* 402 */     plot.setOrientation(PlotOrientation.VERTICAL);
/* 403 */     JFreeChart chart = new JFreeChart("Percentage above threshold for " + title, JFreeChart.DEFAULT_TITLE_FONT, plot, true);
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
/* 415 */     chart.setBackgroundPaint(Color.white);
/* 416 */     ChartPanel chartPanel = new ChartPanel(chart);
/* 417 */     chartPanel.setPreferredSize(new Dimension(500, 270));
/* 418 */     return chartPanel;
/*     */   }
/*     */ }


/* Location:              /home/lachlan/Desktop/psilc1.21/psilc.jar!/lc1/util/Graphing.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */