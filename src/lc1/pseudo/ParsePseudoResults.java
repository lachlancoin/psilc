/*     */ package lc1.pseudo;
/*     */ 
/*     */ import java.awt.Container;
/*     */ import java.io.BufferedReader;
/*     */ import java.io.File;
/*     */ import java.io.FileReader;
/*     */ import java.io.PrintStream;
/*     */ import java.util.Collection;
/*     */ import java.util.HashSet;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Set;
/*     */ import javax.swing.JFrame;
/*     */ import lc1.dp.ROC;
/*     */ import lc1.dp.ROC.Entry;
/*     */ import lc1.util.Graphing;
/*     */ import lc1.util.Print;
/*     */ import lc1.util.SheetIO;
/*     */ import org.biojava.bio.symbol.Location;
/*     */ import org.biojava.bio.symbol.LocationTools;
/*     */ import org.jfree.chart.ChartPanel;
/*     */ import org.jfree.data.XYSeriesCollection;
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
/*     */ public class ParsePseudoResults
/*     */ {
/*  44 */   static String[] header = { "PSILC-nuc/dom", "PSILC-prot/dom", "max PSILC posterior prot", 
/*  45 */     "PSILC posterior nuc", 
/*  46 */     "Goldman Yang dNdS", "Nei Gojobori dNdS" };
/*     */   
/*  48 */   static ROC roc = new ROC(header, "");
/*  49 */   static boolean selection = true;
/*     */   
/*     */   private static void printLoc(Location loc)
/*     */   {
/*  53 */     if (loc != null) {
/*  54 */       for (Iterator it = loc.blockIterator(); it.hasNext();) {
/*  55 */         Location loci = (Location)it.next();
/*  56 */         if (loci.getMin() == loci.getMax()) {
/*  57 */           System.err.print("'" + loci.getMin() + "'" + ",");
/*     */         }
/*     */         else
/*     */         {
/*  61 */           System.err.print("'" + loci.getMin() + "-" + loci.getMax() + "'" + ",");
/*     */         }
/*     */       }
/*     */       
/*  65 */       System.err.println();
/*     */     }
/*  67 */     if (loc != null) {
/*  68 */       for (Iterator it = loc.blockIterator(); it.hasNext();) {
/*  69 */         Location loci = (Location)it.next();
/*  70 */         System.err.print(loci.getMin() + ":");
/*     */       }
/*     */     }
/*  73 */     System.err.println();
/*     */   }
/*     */   
/*     */   public static void main(String[] args) throws Exception {
/*  77 */     int[] pfamCount = new int[2];
/*  78 */     File f = new File(".");
/*  79 */     Set pseudog = new HashSet();
/*  80 */     Set conf = new HashSet();
/*  81 */     for (Iterator it = SheetIO.read(new File("../lachlan_ensembl_vega_ncbi34_6.list"), "\\s+"); it.hasNext();) {
/*  82 */       List row = (List)it.next();
/*  83 */       if (row.get(0).equals("3")) {
/*  84 */         pseudog.add(row.get(2));
/*     */       }
/*     */       
/*  87 */       conf.add(row.get(2));
/*     */     }
/*     */     
/*     */ 
/*  91 */     System.err.println(pseudog.size());
/*  92 */     int totalP = 0;int totalF = 0;
/*     */     
/*  94 */     String[] res = f.list();
/*     */     
/*  96 */     Location loc = null;Location loc1 = null;
/*  97 */     for (int i = 0; i < res.length; i++) {
/*     */       try {
/*  99 */         String id = res[i];
/* 100 */         if (id.indexOf('.') >= 0) id = res[i].split("\\.")[1];
/* 101 */         File fil = new File(f, id);
/* 102 */         File psilc = new File(fil, "PSILC_WAG_HKY");
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
/* 114 */         File summ = new File(psilc, "summary");
/* 115 */         File fasta = new File(fil, "seed.dna.fa");
/*     */         
/*     */ 
/*     */ 
/*     */ 
/* 120 */         File pfam = new File(fil, "pfamA");
/*     */         
/* 122 */         File dnds = new File(fil, "dn_ds");
/* 123 */         double[] dNdS = new double[2];
/*     */         
/* 125 */         if ((!psilc.exists()) || (!summ.exists()) || (summ.length() == 0L)) {
/*     */           try {
/* 127 */             int st1 = Integer.parseInt(res[i]);
/* 128 */             Location loc_inner = LocationTools.makeLocation(st1, st1);
/* 129 */             loc = loc == null ? loc_inner : loc.union(loc_inner);
/*     */ 
/*     */ 
/*     */           }
/*     */           catch (Exception exc1) {}
/*     */ 
/*     */         }
/* 136 */         else if ((!dnds.exists()) || (dnds.length() == 0L)) {
/*     */           try {
/* 138 */             int st1 = Integer.parseInt(res[i]);
/* 139 */             Location loc_inner = LocationTools.makeLocation(st1, st1);
/* 140 */             loc1 = loc1 == null ? loc_inner : loc1.union(loc_inner);
/*     */           }
/*     */           catch (Exception exc1) {}
/*     */         }
/*     */         else {
/* 145 */           BufferedReader br1 = new BufferedReader(new FileReader(dnds));
/* 146 */           br1.readLine();
/*     */           
/* 148 */           dNdS[0] = Double.parseDouble(br1.readLine());
/*     */           
/* 150 */           dNdS[1] = Double.parseDouble(br1.readLine());
/*     */           
/*     */ 
/* 153 */           BufferedReader br = new BufferedReader(new FileReader(summ));
/*     */           
/* 155 */           for (String st = br.readLine(); st != null; st = br.readLine())
/*     */           {
/* 157 */             String[] row = st.split("\\s+");
/* 158 */             if (st.indexOf("OTT") >= 0)
/*     */             {
/*     */ 
/* 161 */               String name = row[1].split(":")[0];
/* 162 */               int correct; int correct; if (pseudog.contains(name)) {
/* 163 */                 correct = 1;
/*     */               } else { int correct;
/* 165 */                 if (conf.contains(name)) {
/* 166 */                   correct = -1;
/*     */                 }
/*     */                 else
/* 169 */                   correct = 0;
/*     */               }
/*     */               try {
/* 172 */                 add(row, correct, dNdS, fil.getName());
/*     */               }
/*     */               catch (Exception exc) {
/* 175 */                 exc.printStackTrace();
/*     */                 
/* 177 */                 System.exit(0);
/*     */               }
/* 179 */               if (correct == 1) totalP++; else
/* 180 */                 totalF++;
/* 181 */               if (pfam.length() != 0L) {
/* 182 */                 if (correct == 1) pfamCount[0] += 1; else {
/* 183 */                   pfamCount[1] += 1;
/*     */                 }
/*     */               }
/*     */             }
/*     */           }
/*     */           
/* 189 */           br.close();
/*     */         }
/*     */       } catch (Exception exc) {
/* 192 */         exc.printStackTrace();
/* 193 */         System.err.println("something wrong with " + res[i] + " " + exc.getMessage());
/*     */       }
/*     */     }
/*     */     
/* 197 */     System.err.println("total F" + totalF);
/* 198 */     System.err.println("tota T" + totalP + " " + pfamCount[0] + " " + pfamCount[1]);
/*     */     
/* 200 */     ROC roc1 = roc;
/* 201 */     int x = 0;
/* 202 */     int y = 2;
/* 203 */     System.err.println("plotting x:" + roc.desc[x] + " y:" + roc.desc[y]);
/*     */     
/* 205 */     Graphing.plotROCDirectComparison(roc.getDirectComparisonData(0, 2, 1, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY), roc.desc[x], roc.desc[y], false);
/* 206 */     Graphing.plotROCDirectComparison(roc.getDirectComparisonData(0, 1, 1, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY), roc.desc[x], roc.desc[y], false);
/* 207 */     Graphing.plotROCDirectComparison(roc.getDirectComparisonData(0, 3, 1, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY), roc.desc[x], roc.desc[y], false);
/*     */     
/* 209 */     System.err.println("mer " + Print.toString(roc.minimumErrorRate()));
/* 210 */     int[] ots = new int[roc.hmm.length];
/* 211 */     for (int ik = 0; ik < roc.hmm.length; ik++) {
/* 212 */       ots[ik] = roc.overTheTopScores(ik).size();
/* 213 */       System.err.println("first false " + roc.desc[ik] + " " + roc.getFirstFalse(ik, 2));
/*     */     }
/* 215 */     System.err.println("ots " + Print.toString(ots));
/* 216 */     printLoc(loc);
/* 217 */     printLoc(loc1);
/*     */     
/*     */ 
/*     */ 
/* 221 */     for (int i = 0; i < header.length; i++) {
/* 222 */       ChartPanel chartPanel = Graphing.plotPercentageAbove(roc.getPercentageAboveThreshold(i), header[i]);
/* 223 */       JFrame jp = new JFrame();
/* 224 */       jp.setSize(800, 800);
/* 225 */       jp.getContentPane().add(chartPanel);
/* 226 */       jp.setVisible(true);
/*     */     }
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
/*     */   public static void plotXY(int totalP, int totalF)
/*     */     throws Exception
/*     */   {
/* 242 */     XYSeriesCollection rocCurves = roc.getRocCurveData(totalF, totalF);
/* 243 */     double[] area = Graphing.getAreaUnderCurve(rocCurves);
/* 244 */     Graphing.plotROCCurves(rocCurves, new double[] { 1.0D, 1.0D }, new double[] { 1500.0D, 600.0D }, false);
/* 245 */     for (int i = 0; i < roc.desc.length; i++) {
/* 246 */       System.err.println(roc.desc[i] + ": " + area[i]);
/*     */     }
/*     */   }
/*     */   
/*     */   public static void add(String[] row, int correct, double[] dnds, String cluster)
/*     */     throws Exception
/*     */   {
/* 253 */     String name = row[1];
/*     */     
/* 255 */     ROC.Entry[] dt = new ROC.Entry[header.length];
/*     */     
/* 257 */     int start = header.length - dnds.length;
/* 258 */     for (int i = 0; i < start; i++) {
/* 259 */       int i1 = i < 3 ? i : i + 1;
/* 260 */       dt[i] = new ROC.Entry();
/* 261 */       dt[i].score = Float.parseFloat(row[(i1 + 2)]);
/* 262 */       dt[i].correct = correct;
/* 263 */       dt[i].name = cluster;
/*     */     }
/*     */     
/* 266 */     for (int i = start; i < header.length; i++) {
/* 267 */       dt[i] = new ROC.Entry();
/* 268 */       dt[i].score = ((float)dnds[(i - start)]);
/* 269 */       dt[i].correct = correct;
/* 270 */       dt[i].name = cluster;
/*     */     }
/* 272 */     roc.add(dt);
/*     */   }
/*     */ }


/* Location:              /home/lachlan/Desktop/psilc1.21/psilc.jar!/lc1/pseudo/ParsePseudoResults.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */