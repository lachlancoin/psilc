/*     */ package lc1.treefam;
/*     */ 
/*     */ import java.awt.BorderLayout;
/*     */ import java.awt.Color;
/*     */ import java.awt.Component;
/*     */ import java.awt.Container;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.Font;
/*     */ import java.awt.LayoutManager;
/*     */ import java.awt.event.WindowEvent;
/*     */ import java.awt.event.WindowListener;
/*     */ import java.beans.PropertyChangeEvent;
/*     */ import java.beans.PropertyChangeListener;
/*     */ import java.beans.PropertyVetoException;
/*     */ import java.io.PrintStream;
/*     */ import java.util.Collection;
/*     */ import java.util.Iterator;
/*     */ import javax.swing.BoxLayout;
/*     */ import javax.swing.JComponent;
/*     */ import javax.swing.JFrame;
/*     */ import javax.swing.JPanel;
/*     */ import org.jfree.chart.ChartMouseListener;
/*     */ import org.jfree.chart.ChartPanel;
/*     */ import org.jfree.chart.JFreeChart;
/*     */ import org.jfree.chart.StandardLegend;
/*     */ import org.jfree.chart.axis.NumberAxis;
/*     */ import org.jfree.chart.axis.ValueAxis;
/*     */ import org.jfree.chart.labels.StandardXYToolTipGenerator;
/*     */ import org.jfree.chart.plot.DatasetRenderingOrder;
/*     */ import org.jfree.chart.plot.PlotOrientation;
/*     */ import org.jfree.chart.plot.XYPlot;
/*     */ import org.jfree.chart.renderer.StandardXYItemRenderer;
/*     */ import org.jfree.data.XYSeries;
/*     */ import org.jfree.data.XYSeriesCollection;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ExtendedChart
/*     */   extends JFreeChart
/*     */ {
/*     */   forester.tree.Node fNode;
/*     */   pal.tree.Node node;
/*     */   int index;
/*     */   static final double display_thres = 0.3D;
/*     */   
/*     */   public boolean equals(Object o)
/*     */   {
/*  51 */     if (o == null) return false;
/*  52 */     if (!(o instanceof ExtendedChart)) {
/*  53 */       return false;
/*     */     }
/*     */     
/*  56 */     if (((ExtendedChart)o).node == null) return false;
/*  57 */     return ((ExtendedChart)o).node.equals(this.node);
/*     */   }
/*     */   
/*     */   public ExtendedChart(forester.tree.Node fNode, pal.tree.Node node, String title, ChartMouseListener component, int index)
/*     */     throws PropertyVetoException
/*     */   {
/*  63 */     super(title, JFreeChart.DEFAULT_TITLE_FONT, new XYPlot(), true);
/*  64 */     this.node = node;
/*  65 */     this.index = index;
/*     */     
/*  67 */     this.fNode = fNode;
/*  68 */     calculatePlot();
/*  69 */     if ((component != null) && ((component instanceof Component))) ((Component)component).addPropertyChangeListener("psilc", new PropertyChangeListener() {
/*     */         public void propertyChange(PropertyChangeEvent pce) {
/*     */           try {
/*  72 */             ExtendedChart.this.calculatePlot();
/*     */           } catch (PropertyVetoException pve) {
/*  74 */             pve.printStackTrace();
/*     */           }
/*  76 */           System.err.println("recalculated plot");
/*  77 */           ExtendedChart.this.fireChartChanged();
/*     */         }
/*     */       });
/*     */   }
/*     */   
/*  82 */   public void calculatePlot() throws PropertyVetoException { double[][][] results = this.fNode.getGraph();
/*  83 */     if (results == null) throw new PropertyVetoException("no graph", null);
/*  84 */     String[][] name = this.fNode.getGraphNames();
/*  85 */     int[] alias = this.fNode.getAlias();
/*  86 */     XYSeriesCollection[] collection = new XYSeriesCollection[2];
/*  87 */     for (int l = 0; l < collection.length; l++) {
/*  88 */       collection[l] = new XYSeriesCollection();
/*  89 */       for (int k = 0; k < results[l].length; k++) {
/*  90 */         XYSeries series = new XYSeries(name[l][k]);
/*  91 */         if (alias == null) {
/*  92 */           for (int i = 0; i < results[l][k].length; i++) {
/*  93 */             if ((results[l][k][i] != Double.NEGATIVE_INFINITY) && ((l == 0) || (Math.abs(results[l][k][i]) > 0.3D))) {
/*  94 */               series.add(i + 1, results[l][k][i]);
/*     */             }
/*     */             
/*     */           }
/*     */         } else {
/*  99 */           for (int i = 0; i < alias.length; i++) {
/* 100 */             if ((results[l][k][alias[i]] != Double.NEGATIVE_INFINITY) && ((l == 0) || (Math.abs(results[l][k][alias[i]]) > 0.3D))) {
/* 101 */               series.add(i + 1, results[l][k][alias[i]]);
/*     */             }
/*     */           }
/*     */         }
/* 105 */         collection[l].addSeries(series);
/*     */       }
/*     */     }
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
/* 125 */     for (int l = 2; l < results.length; l++) {
/* 126 */       for (int k = 0; k < results[l].length; k++) {
/* 127 */         XYSeries series = new XYSeries(name[l][k]);
/* 128 */         if (alias == null) {
/* 129 */           for (int i = 0; i < results[l][k].length; i++) {
/* 130 */             if (results[l][k][i] > 0.5D) {
/* 131 */               series.add(i + 1, results[l][k][i]);
/*     */             }
/*     */             
/*     */           }
/*     */         } else {
/* 136 */           for (int i = 0; i < alias.length; i++) {
/* 137 */             if (results[l][k][alias[i]] > 0.5D) {
/* 138 */               series.add(i + 1, results[l][k][alias[i]]);
/*     */             }
/*     */           }
/*     */         }
/* 142 */         collection[0].addSeries(series);
/*     */       }
/*     */     }
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
/* 156 */     double max = 0.0D;
/* 157 */     for (int k = 0; k < collection[1].getSeriesCount(); k++) {
/* 158 */       for (int j = 0; j < collection[1].getSeries(k).getItemCount(); j++) {
/* 159 */         if (Math.abs(collection[1].getY(k, j)) > max) max = Math.abs(collection[1].getY(k, j));
/*     */       }
/*     */     }
/* 162 */     Font f = new Font("SansSerif", 0, 16);
/* 163 */     NumberAxis xAxis = new NumberAxis("Position");
/* 164 */     xAxis.setTickLabelFont(f);
/* 165 */     xAxis.setLabelFont(f);
/* 166 */     xAxis.setAutoRangeIncludesZero(false);
/* 167 */     NumberAxis yAxis = new NumberAxis("PSILC-nuc/dom ; prot/dom site scores");
/* 168 */     yAxis.centerRange(0.0D);
/* 169 */     yAxis.setLabelFont(f);
/* 170 */     yAxis.setTickLabelFont(f);
/* 171 */     yAxis.setMaximumAxisValue(max + 1.0D);
/* 172 */     yAxis.setMinimumAxisValue(-max - 1.0D);
/* 173 */     StandardXYItemRenderer renderer = new StandardXYItemRenderer(1);
/* 174 */     XYPlot plot = getXYPlot();
/*     */     
/* 176 */     plot.setDataset(0, collection[1]);
/* 177 */     plot.setDomainAxis(0, xAxis);
/* 178 */     plot.setRangeAxis(0, yAxis);
/* 179 */     plot.setRenderer(0, renderer);
/* 180 */     plot.setOrientation(PlotOrientation.VERTICAL);
/* 181 */     renderer.setToolTipGenerator(new StandardXYToolTipGenerator());
/* 182 */     renderer.setPlotShapes(true);
/* 183 */     renderer.setShapesFilled(false);
/* 184 */     renderer.setPlotLines(false);
/* 185 */     renderer.setSeriesPaint(0, Color.ORANGE);
/* 186 */     renderer.setSeriesPaint(1, Color.GREEN);
/* 187 */     renderer.setSeriesPaint(2, Color.CYAN);
/* 188 */     ValueAxis axis2 = new NumberAxis("Probability of being under differential selection");
/* 189 */     axis2.centerRange(0.0D);
/* 190 */     axis2.setMaximumAxisValue(1.1D);
/* 191 */     axis2.setLabelFont(f);
/* 192 */     axis2.setTickLabelFont(f);
/* 193 */     axis2.setMinimumAxisValue(-1.1D);
/* 194 */     plot.setRangeAxis(1, axis2);
/* 195 */     plot.setDataset(1, collection[0]);
/* 196 */     plot.mapDatasetToRangeAxis(1, 1);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 201 */     StandardXYItemRenderer renderer2 = new StandardXYItemRenderer(10);
/*     */     
/* 203 */     renderer2.setGapThreshold(5.0D);
/*     */     
/* 205 */     renderer2.setSeriesPaint(0, Color.BLACK);
/* 206 */     renderer2.setSeriesPaint(1, Color.MAGENTA);
/*     */     
/*     */ 
/* 209 */     plot.setRenderer(1, renderer2);
/* 210 */     plot.setDatasetRenderingOrder(DatasetRenderingOrder.REVERSE);
/*     */   }
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
/*     */   public static boolean displayGraph(int index, forester.tree.Node node1, pal.tree.Node node, String title, ChartMouseListener listener, Collection activeCharts, JFrame jp)
/*     */     throws PropertyVetoException
/*     */   {
/* 227 */     for (Iterator it = activeCharts.iterator(); it.hasNext();) {
/* 228 */       ExtendedChart ch = (ExtendedChart)it.next();
/* 229 */       if (ch.node.equals(node)) return false;
/*     */     }
/* 231 */     JFreeChart chart = new ExtendedChart(node1, node, title, listener, index);
/* 232 */     chart.setBackgroundPaint(Color.WHITE);
/* 233 */     activeCharts.add(chart);
/* 234 */     StandardLegend legend = (StandardLegend)chart.getLegend();
/* 235 */     Font f = new Font("SansSerif", 0, 16);
/*     */     
/*     */ 
/* 238 */     jp.getContentPane().removeAll();
/* 239 */     JPanel plotPanel = new JPanel();
/* 240 */     jp.getContentPane().setLayout(new BorderLayout());
/* 241 */     double y = 0.0D;
/* 242 */     jp.setSize(800, 800);
/* 243 */     jp.addWindowListener(new WindowListener() {
/*     */       public void windowOpened(WindowEvent e) {}
/*     */       
/*     */       public void windowClosing(WindowEvent e) {
/* 247 */         try { ExtendedChart.this.clear();
/*     */         } catch (Exception exc) {
/* 249 */           exc.printStackTrace();
/*     */         }
/*     */       }
/*     */       
/*     */       public void windowClosed(WindowEvent e) {
/* 254 */         try { ExtendedChart.this.clear();
/*     */         } catch (Exception exc) {
/* 256 */           exc.printStackTrace(); } }
/*     */       
/*     */       public void windowIconified(WindowEvent e) {}
/*     */       
/*     */       public void windowDeiconified(WindowEvent e) {}
/*     */       
/*     */       public void windowActivated(WindowEvent e) {}
/* 263 */       public void windowDeactivated(WindowEvent e) {} });
/* 264 */     LayoutManager gridy = new BoxLayout(plotPanel, 1);
/* 265 */     plotPanel.setLayout(gridy);
/* 266 */     double sqrt = Math.sqrt(activeCharts.size());
/* 267 */     int x_length = (int)Math.floor(sqrt);
/* 268 */     int y_length = (int)Math.ceil(activeCharts.size() / x_length);
/* 269 */     double[] xext = { ((XYPlot)chart.getPlot()).getDomainAxis().getMinimumAxisValue(), 
/* 270 */       ((XYPlot)chart.getPlot()).getDomainAxis().getMaximumAxisValue() };
/*     */     
/* 272 */     for (Iterator it = activeCharts.iterator(); it.hasNext();) {
/* 273 */       ExtendedChart ch = (ExtendedChart)it.next();
/* 274 */       double ymax = ((XYPlot)ch.getPlot()).getRangeAxis().getMaximumAxisValue();
/* 275 */       double xmin = ((XYPlot)ch.getPlot()).getDomainAxis().getMinimumAxisValue();
/* 276 */       double xmax = ((XYPlot)ch.getPlot()).getDomainAxis().getMaximumAxisValue();
/* 277 */       if (xmax > xext[1]) xext[1] = xmax;
/* 278 */       if (xmin > xext[1]) xext[0] = xmin;
/* 279 */       if (ymax > y) y = ymax;
/*     */     }
/* 281 */     for (Iterator it = activeCharts.iterator(); it.hasNext();) {
/* 282 */       ExtendedChart ch = (ExtendedChart)it.next();
/* 283 */       ((XYPlot)ch.getPlot()).getRangeAxis().setMaximumAxisValue(y);
/* 284 */       ((XYPlot)ch.getPlot()).getRangeAxis().setMinimumAxisValue(-y);
/* 285 */       ((XYPlot)ch.getPlot()).getDomainAxis().setMaximumAxisValue(xext[1]);
/* 286 */       ((XYPlot)ch.getPlot()).getDomainAxis().setMinimumAxisValue(xext[0]);
/*     */     }
/* 288 */     Iterator it = activeCharts.iterator();
/* 289 */     for (int i = 0; (i < x_length) && (it.hasNext()); i++) {
/* 290 */       JPanel panel = new JPanel();
/* 291 */       LayoutManager gridx = new BoxLayout(panel, 0);
/* 292 */       panel.setLayout(gridx);
/* 293 */       for (int j = 0; j < y_length; j++) {
/* 294 */         JComponent comp = null;
/* 295 */         if (it.hasNext()) {
/* 296 */           comp = new ChartPanel((ExtendedChart)it.next());
/* 297 */           ((ChartPanel)comp).addChartMouseListener(listener);
/* 298 */           if (i < x_length - 1) { ((ChartPanel)comp).getChart().setLegend(null);
/*     */           } else {
/* 300 */             ((ChartPanel)comp).getChart().setLegend(legend);
/*     */           }
/*     */         } else {
/* 303 */           comp = new JPanel(); }
/* 304 */         comp.setPreferredSize(new Dimension(300, 150));
/* 305 */         panel.add(comp);
/*     */       }
/*     */       
/* 308 */       plotPanel.add(panel);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 316 */     jp.getContentPane().add(plotPanel, "Center");
/*     */     
/* 318 */     jp.setVisible(true);
/* 319 */     return true;
/*     */   }
/*     */ }


/* Location:              /home/lachlan/Desktop/psilc1.21/psilc.jar!/lc1/treefam/ExtendedChart.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */