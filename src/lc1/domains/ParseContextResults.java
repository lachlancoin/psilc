/*     */ package lc1.domains;
/*     */ 
/*     */ import java.awt.Color;
/*     */ import java.io.BufferedReader;
/*     */ import java.io.File;
/*     */ import java.io.FileReader;
/*     */ import java.io.FilenameFilter;
/*     */ import java.io.PrintStream;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.Comparator;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import lc1.dp.ROC;
/*     */ import lc1.dp.ROC.Entry;
/*     */ import lc1.util.Graphing;
/*     */ import lc1.util.Print;
/*     */ import org.apache.commons.cli.CommandLine;
/*     */ import org.apache.commons.cli.OptionBuilder;
/*     */ import org.apache.commons.cli.Options;
/*     */ import org.apache.commons.cli.Parser;
/*     */ import org.apache.commons.cli.PosixParser;
/*     */ import org.jfree.chart.ChartFactory;
/*     */ import org.jfree.chart.JFreeChart;
/*     */ import org.jfree.chart.Legend;
/*     */ import org.jfree.chart.axis.AxisLocation;
/*     */ import org.jfree.chart.axis.CategoryAxis;
/*     */ import org.jfree.chart.axis.CategoryLabelPositions;
/*     */ import org.jfree.chart.axis.NumberAxis;
/*     */ import org.jfree.chart.axis.ValueAxis;
/*     */ import org.jfree.chart.labels.StandardCategoryToolTipGenerator;
/*     */ import org.jfree.chart.plot.CategoryPlot;
/*     */ import org.jfree.chart.plot.DatasetRenderingOrder;
/*     */ import org.jfree.chart.plot.PlotOrientation;
/*     */ import org.jfree.chart.renderer.LineAndShapeRenderer;
/*     */ import org.jfree.data.CategoryDataset;
/*     */ import org.jfree.data.DefaultCategoryDataset;
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
/*     */ 
/*     */ public class ParseContextResults
/*     */ {
/*  63 */   static final Options OPTIONS = new Options() {};
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
/*  76 */   static boolean selection = true;
/*     */   
/*     */   static class RocOTSComparator implements Comparator
/*     */   {
/*     */     public int compare(Object o1, Object o2)
/*     */     {
/*  82 */       int[] ott1 = ((ROC)o1).OTS();
/*  83 */       int[] ott2 = ((ROC)o2).OTS();
/*  84 */       int diff1 = ott1[1] - ott1[0];
/*  85 */       int diff2 = ott2[1] - ott2[0];
/*  86 */       if (diff1 != diff2) return diff1 > diff2 ? -1 : 1;
/*  87 */       return 0;
/*     */     }
/*     */   }
/*     */   
/*     */   public static void main(String[] args) throws Exception {
/*  92 */     Parser DP_PARSER = new PosixParser();
/*  93 */     CommandLine params = DP_PARSER.parse(OPTIONS, args);
/*     */     
/*  95 */     System.err.println(params.hasOption("cols"));
/*  96 */     if (params.hasOption("cols")) {
/*  97 */       String[] columns = params.getOptionValues("cols");
/*  98 */       cols = new int[columns.length];
/*  99 */       for (int i = 0; i < cols.length; i++) {
/* 100 */         cols[i] = Integer.parseInt(columns[i]);
/*     */       }
/*     */     }
/* 103 */     if (params.hasOption("type")) {
/* 104 */       type = params.getOptionValues("type");
/*     */     }
/* 106 */     mult = Integer.parseInt(params.getOptionValue("mult", "-1"));
/* 107 */     log_log = mult == -1;
/*     */     
/* 109 */     MultipleDomains.main(params);
/*     */   }
/*     */   
/*     */   static class MultipleDomains
/*     */   {
/*     */     public static void main(CommandLine params) throws Exception {
/* 115 */       Comparator comp = new ParseContextResults.RocOTSComparator();
/* 116 */       String[] args = params.getOptionValues("file");
/* 117 */       File[] f = new File[args.length];
/*     */       
/* 119 */       for (int i = 0; i < args.length; i++) {
/* 120 */         f[i] = new File(args[i]);
/*     */       }
/*     */       
/* 123 */       String[] res = 
/* 124 */         f[0].list(ParseContextResults.ff1);
/* 125 */       List rocs = new ArrayList();
/* 126 */       Map totals = ParseContextResults.getTotals(new File("scop_totals"));
/* 127 */       int[] betterOts = new int[f.length + 1];
/* 128 */       int[] worseOts = new int[f.length + 1];
/* 129 */       int[] totalOts = new int[f.length + 1];
/* 130 */       int[] totalMer = new int[f.length + 1];
/* 131 */       for (int i = 0; i < res.length; i++) {
/* 132 */         ROC roc = ParseContextResults.getROC(f, new String[] { res[i] });
/* 133 */         int[] ots = roc.OTS();
/* 134 */         int[] mer = roc.minimumErrorRate();
/* 135 */         for (int j = 0; j < ots.length; j++) {
/* 136 */           totalOts[j] += ots[j];
/* 137 */           totalMer[j] += mer[j];
/* 138 */           if (ots[j] > ots[0]) { betterOts[j] += 1;
/* 139 */           } else if (ots[j] < ots[0]) worseOts[j] += 1;
/*     */         }
/* 141 */         if (ots[0] != ots[1]) {
/* 142 */           rocs.add(roc);
/* 143 */           if (ots[1] > ots[0])
/* 144 */             System.err.println("id different " + roc.getName());
/*     */         }
/*     */       }
/* 147 */       System.err.println("better OTT " + Print.toString(betterOts));
/* 148 */       System.err.println("worse OTT " + Print.toString(worseOts));
/* 149 */       System.err.println("sum OTT " + Print.toString(totalOts));
/* 150 */       System.err.println("sum MER " + Print.toString(totalMer));
/* 151 */       Collections.sort(rocs, comp);
/* 152 */       graph(getDataSet(rocs.subList(0, 10), f), getDataSet(rocs.subList(rocs.size() - 10, rocs.size()), f));
/*     */     }
/*     */     
/*     */     static CategoryDataset[] getDataSet(List rocs, File[] series) {
/* 156 */       String[] category = new String[rocs.size()];
/* 157 */       for (int i = 0; i < category.length; i++) {
/* 158 */         category[i] = ((ROC)rocs.get(i)).getName();
/*     */       }
/* 160 */       DefaultCategoryDataset dataset = new DefaultCategoryDataset();
/* 161 */       DefaultCategoryDataset dataset1 = new DefaultCategoryDataset();
/* 162 */       for (int i = 0; i < category.length; i++) {
/* 163 */         int[] ott = ((ROC)rocs.get(i)).OTS();
/* 164 */         dataset1.addValue((ott[1] - ott[0]) / ott[0], series[0], category[i]);
/* 165 */         for (int j = 1; j < ott.length; j++) {
/* 166 */           dataset.addValue(ott[j] - ott[0], series[(j - 1)].getName(), category[i]);
/*     */         }
/*     */       }
/*     */       
/* 170 */       return new CategoryDataset[] { dataset, dataset1 };
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     public static void graph(CategoryDataset[] dataset0, CategoryDataset[] dataset1)
/*     */     {
/* 177 */       JFreeChart chart1 = getChart(dataset0[0], dataset0[1], true);
/* 178 */       JFreeChart chart2 = getChart(dataset1[0], dataset1[1], false);
/* 179 */       ApplicationFrame demo1 = new ParseContextResults.4("", chart1);
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 186 */       ApplicationFrame demo2 = new ParseContextResults.5("", chart2);
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 193 */       demo1.pack();
/* 194 */       RefineryUtilities.centerFrameOnScreen(demo1);
/* 195 */       demo1.setVisible(true);
/* 196 */       demo2.pack();
/* 197 */       RefineryUtilities.centerFrameOnScreen(demo2);
/* 198 */       demo2.setVisible(true);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     public static JFreeChart getChart(CategoryDataset dataset1, CategoryDataset dataset2, boolean top)
/*     */     {
/* 205 */       JFreeChart chart = ChartFactory.createBarChart(
/* 206 */         "OTS improvement relative to HMMER - " + (
/* 207 */         top ? " best " : " worst ") + "10 domains", 
/* 208 */         "Domain", 
/* 209 */         "OTS improvement relative to HMMER", 
/* 210 */         dataset1, 
/* 211 */         PlotOrientation.VERTICAL, 
/* 212 */         true, 
/* 213 */         true, 
/* 214 */         false);
/*     */       
/*     */ 
/*     */ 
/* 218 */       chart.setBackgroundPaint(Color.white);
/* 219 */       chart.getLegend().setAnchor(3);
/*     */       
/*     */ 
/* 222 */       CategoryPlot plot = chart.getCategoryPlot();
/*     */       
/* 224 */       plot.setDomainAxisLocation(AxisLocation.BOTTOM_OR_RIGHT);
/*     */       
/*     */ 
/* 227 */       plot.setDataset(1, dataset2);
/* 228 */       plot.mapDatasetToRangeAxis(1, 1);
/*     */       
/* 230 */       CategoryAxis domainAxis = plot.getDomainAxis();
/* 231 */       domainAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_90);
/* 232 */       ValueAxis axis2 = new NumberAxis("OTS Improvement as %age of  HMMER OTS");
/* 233 */       plot.setRangeAxis(1, axis2);
/*     */       
/* 235 */       LineAndShapeRenderer renderer2 = new LineAndShapeRenderer();
/* 236 */       renderer2.setToolTipGenerator(new StandardCategoryToolTipGenerator());
/* 237 */       plot.setRenderer(1, renderer2);
/* 238 */       plot.setDatasetRenderingOrder(DatasetRenderingOrder.REVERSE);
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 244 */       return chart;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/* 251 */   static FilenameFilter ff1 = new FilenameFilter() {
/*     */     public boolean accept(File f, String name) {
/* 253 */       return new File(f, name).length() > 0L;
/*     */     }
/*     */   };
/*     */   
/* 257 */   static FilenameFilter ff = new FilenameFilter() {
/* 258 */     Set s = new HashSet(Arrays.asList(new File("scop").list()).subList(0, 500));
/*     */     
/* 260 */     public boolean accept(File f, String name) { return !this.s.contains(name); }
/*     */   };
/*     */   
/*     */   public static void singleDirectory(CommandLine params) throws Exception
/*     */   {
/* 265 */     String[] args = params.getOptionValues("file");
/*     */     
/* 267 */     ROC.pos = 
/* 268 */       params.getOptionValue("pos", "sf").equals("sf") ? 0 : -1;
/*     */     
/* 270 */     File[] f = new File[args.length];
/*     */     
/* 272 */     for (int i = 0; i < args.length; i++) {
/* 273 */       f[i] = new File(args[i]);
/*     */     }
/* 275 */     String[] list = params.hasOption("domain") ? 
/* 276 */       params.getOptionValues("domain") : f[0].list();
/* 277 */     boolean graph = false;
/* 278 */     Map totals = getTotals(new File("scop_totals"));
/* 279 */     int[] totalPF = getTotals(list, totals);
/*     */     
/*     */ 
/* 282 */     System.err.println(Arrays.asList(f));
/*     */     
/* 284 */     ROC roc = getROC(f, list);
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
/* 298 */     int[] totalOTS = new int[roc.hmm.length];
/*     */     
/*     */ 
/* 301 */     for (int j = 0; j < list.length; j++) {
/* 302 */       ROC roc_1 = getROC(f, new String[] { list[j] });
/*     */       
/* 304 */       int[] ots = roc_1.OTS();
/*     */       
/*     */ 
/* 307 */       for (int k = 0; k < ots.length; k++) {
/* 308 */         totalOTS[k] += ots[k];
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 315 */     System.err.println("mer " + Print.toString(roc.minimumErrorRate()));
/* 316 */     int[] ots = new int[roc.hmm.length];
/* 317 */     for (int ik = 0; ik < roc.hmm.length; ik++) {
/* 318 */       ots[ik] = roc.overTheTopScores(ik).size();
/*     */     }
/* 320 */     System.err.println("ots " + Print.toString(ots));
/*     */   }
/*     */   
/*     */   public static Map getTotals(File f)
/*     */     throws Exception
/*     */   {
/* 326 */     BufferedReader br = new BufferedReader(new FileReader(f));
/* 327 */     String st = "";
/* 328 */     Map m = new HashMap();
/* 329 */     while ((st = br.readLine()) != null) {
/* 330 */       String[] str = st.split("\\s+");
/* 331 */       m.put(str[0], new int[] { Integer.parseInt(str[1]), Integer.parseInt(str[2]) });
/*     */     }
/* 333 */     return m;
/*     */   }
/*     */   
/*     */   public static int[] getTotals(String[] res, Map totals) {
/* 337 */     int totalP = 0;int totalF = 0;
/*     */     
/* 339 */     for (int i = 0; i < res.length; i++) {
/* 340 */       String id = res[i];
/*     */       
/* 342 */       System.err.println(id);
/* 343 */       int[] tot = (int[])totals.get(id);
/* 344 */       totalP += tot[0];
/* 345 */       totalF += tot[1];
/*     */     }
/* 347 */     return new int[] { totalP, totalF }; }
/*     */   
/* 349 */   static int[] cols = { 1, 3, 5, 7 };
/* 350 */   static String[] type = { "" };
/*     */   
/* 352 */   static int mult = 1;
/* 353 */   public static boolean log_log = false;
/*     */   
/* 355 */   public static ROC getROC(File[] parent, String[] res) throws Exception { String[] header = new String[parent.length * (cols.length - 1) + 1];
/* 356 */     header[0] = "HMMER";
/* 357 */     for (int j = 0; j < parent.length; j++) {
/* 358 */       for (int k = 0; k < cols.length - 1; k++) {
/* 359 */         header[(1 + j * (cols.length - 1) + k)] = (parent[j].getName() + type[k]);
/*     */       }
/*     */     }
/* 362 */     Iterator it = new Iterator()
/*     */     {
/*     */       int i;
/*     */       
/*     */       Iterator entries;
/*     */       Map m;
/*     */       String fileName;
/*     */       
/*     */       void prepareNext()
/*     */       {
/* 372 */         this.i += 1;
/*     */         try
/*     */         {
/* 375 */           this.m.clear();
/* 376 */           this.fileName = ParseContextResults.this[this.i];
/*     */           
/*     */ 
/* 379 */           for (int j = 0; j < this.val$parent.length; j++)
/*     */           {
/*     */ 
/* 382 */             BufferedReader br = new BufferedReader(new FileReader(new File(this.val$parent[j], ParseContextResults.this[this.i])));
/* 383 */             String st = br.readLine();
/* 384 */             if (st != null) {
/* 385 */               if (st.startsWith("Sequence")) {
/* 386 */                 br.readLine();
/* 387 */                 st = br.readLine();
/*     */               }
/* 389 */               while (st != null)
/*     */               {
/*     */ 
/* 392 */                 String[] split = st.split("\\s+");
/*     */                 
/* 394 */                 int correct = Integer.parseInt(split[(split.length - 1)]);
/* 395 */                 ROC.Entry[] entry; if (j == 0) {
/* 396 */                   ROC.Entry[] entry = new ROC.Entry[1 + this.val$parent.length * (ParseContextResults.cols.length - 1)];
/* 397 */                   if (this.m.containsKey(split[0])) {
/* 398 */                     System.err.println("contains " + split[0]);
/* 399 */                     System.exit(0);
/*     */                   }
/* 401 */                   this.m.put(split[0], entry);
/* 402 */                   entry[0] = new ROC.Entry();
/*     */                   
/* 404 */                   entry[0].score = (ParseContextResults.mult * Float.parseFloat(split[ParseContextResults.cols[0]]));
/*     */                   
/*     */ 
/* 407 */                   entry[0].correct = correct;
/*     */ 
/*     */                 }
/*     */                 else
/*     */                 {
/* 412 */                   entry = (ROC.Entry[])this.m.get(split[0]);
/*     */                 }
/* 414 */                 for (int k = 0; k < ParseContextResults.cols.length - 1; k++)
/*     */                 {
/* 416 */                   entry[(1 + j * (ParseContextResults.cols.length - 1) + k)] = new ROC.Entry();
/* 417 */                   entry[(1 + j * (ParseContextResults.cols.length - 1) + k)].score = (ParseContextResults.mult * Float.parseFloat(split[ParseContextResults.cols[(k + 1)]]));
/*     */                   
/* 419 */                   entry[(1 + j * (ParseContextResults.cols.length - 1) + k)].correct = correct;
/*     */                 }
/*     */                 
/*     */ 
/* 423 */                 st = br.readLine();
/*     */               }
/* 425 */               br.close();
/*     */             }
/*     */           }
/*     */           
/* 429 */           this.entries = this.m.values().iterator();
/*     */         } catch (Exception exc) {
/* 431 */           exc.printStackTrace();
/* 432 */           System.err.println(this.fileName);
/* 433 */           System.exit(0);
/*     */         }
/*     */       }
/*     */       
/*     */       public boolean hasNext() {
/* 438 */         return (this.i < ParseContextResults.this.length - 1) || (this.entries.hasNext());
/*     */       }
/*     */       
/*     */       public void remove() {}
/*     */       
/*     */       public Object next() {
/* 444 */         if (!this.entries.hasNext()) prepareNext();
/* 445 */         if (!this.entries.hasNext()) return null;
/* 446 */         return this.entries.next();
/*     */       }
/*     */       
/* 449 */     };
/* 450 */     return getROC(it, res.length == 1 ? res[0] : "multi", header);
/*     */   }
/*     */   
/*     */   public static ROC getROC(Iterator it, String domain, String[] header) throws Exception
/*     */   {
/* 455 */     ROC roc = new ROC(header, domain);
/* 456 */     while (it.hasNext()) {
/* 457 */       ROC.Entry[] row = (ROC.Entry[])it.next();
/*     */       
/* 459 */       if (row == null) break;
/* 460 */       roc.add(row);
/*     */     }
/*     */     
/* 463 */     return roc;
/*     */   }
/*     */   
/*     */ 
/*     */   public static void plotROC(ROC roc, int[] totalPF)
/*     */     throws Exception
/*     */   {
/* 470 */     System.err.println("plotting roc");
/* 471 */     XYSeriesCollection datas = roc.getRocCurveData(totalPF[0], totalPF[0]);
/* 472 */     Graphing.plotROCCurves(datas, 
/* 473 */       null, null, true);
/*     */     
/* 475 */     double[] area = Graphing.getAreaUnderCurve(datas);
/* 476 */     for (int i = 0; i < roc.desc.length; i++) {
/* 477 */       System.err.println(roc.desc[i] + ": " + area[i]);
/*     */     }
/*     */   }
/*     */ }


/* Location:              /home/lachlan/Desktop/psilc1.21/psilc.jar!/lc1/domains/ParseContextResults.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */