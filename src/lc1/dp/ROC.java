/*     */ package lc1.dp;
/*     */ 
/*     */ import com.braju.format.Format;
/*     */ import com.braju.format.Parameters;
/*     */ import java.awt.Color;
/*     */ import java.io.BufferedWriter;
/*     */ import java.io.File;
/*     */ import java.io.FileWriter;
/*     */ import java.io.IOException;
/*     */ import java.io.PrintStream;
/*     */ import java.io.PrintWriter;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.Comparator;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import java.util.SortedSet;
/*     */ import java.util.TreeSet;
/*     */ import javax.swing.table.AbstractTableModel;
/*     */ import lc1.pfam.PfamAlphabet;
/*     */ import org.biojava.bio.seq.io.SymbolTokenization;
/*     */ import org.jfree.data.XYSeries;
/*     */ import org.jfree.data.XYSeriesCollection;
/*     */ import org.omegahat.Simulation.MCMC.Listeners.Histogram;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ROC
/*     */ {
/*  37 */   static Map pseudo = null;
/*     */   
/*     */ 
/*     */   static SymbolTokenization parser;
/*     */   
/*     */ 
/*  43 */   static String[] mode = new String[2];
/*     */   
/*     */   public List[] hmm;
/*  46 */   boolean sorted = false;
/*     */   
/*     */   public void add(Entry[] entry)
/*     */   {
/*  50 */     for (int i = 0; i < entry.length; i++) {
/*  51 */       if (entry[i] == null) throw new NullPointerException(entry[0] + "\n" + i);
/*  52 */       this.hmm[i].add(entry[i]);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*  57 */   public String[] desc = { "false", "true" };
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   static PfamAlphabet alph;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  69 */   static final String[] colors = { "gray", "red", "black", 
/*  70 */     "black", "dark_grey" };
/*     */   
/*     */ 
/*  73 */   public static int neg = 0;
/*     */   
/*  75 */   public static int pos = 0;
/*     */   
/*  77 */   boolean log = false;
/*     */   
/*     */   public XYSeriesCollection[] getErrorCurveData(int tp, double[] bounds) throws Exception {
/*  80 */     XYSeriesCollection[] dataset = { new XYSeriesCollection(), new XYSeriesCollection() };
/*  81 */     for (int i = 0; i < this.hmm.length; i++) {
/*  82 */       XYSeries[] series = getErrorData(i, tp, bounds);
/*  83 */       dataset[0].addSeries(series[0]);
/*  84 */       dataset[1].addSeries(series[1]);
/*     */     }
/*  86 */     return dataset;
/*     */   }
/*     */   
/*     */   public void sortResults() {
/*  90 */     if (this.sorted) return;
/*  91 */     for (int i = 0; i < this.hmm.length; i++) {
/*  92 */       Collections.sort(this.hmm[i], COMPARISON);
/*     */     }
/*  94 */     this.sorted = true;
/*     */   }
/*     */   
/*     */   public XYSeries[] getErrorData(int i, int tp, double[] bounds) throws Exception {
/*  98 */     sortResults();
/*  99 */     XYSeries falseP = new XYSeries(this.desc[i] + " false positive ");
/* 100 */     XYSeries falseN = new XYSeries(this.desc[i] + " false negative ");
/* 101 */     int neg = 0;
/* 102 */     int j = 0;
/* 103 */     double fp = 0.0D;
/* 104 */     double fn = tp;
/* 105 */     System.err.println("first " + this.hmm[i].get(0) + " last " + this.hmm[i].get(this.hmm[i].size() - 1));
/* 106 */     for (Iterator it1 = this.hmm[i].iterator(); it1.hasNext();) {
/* 107 */       Entry ss = (Entry)it1.next();
/* 108 */       double evalue = -1.0D * ss.score;
/*     */       
/* 110 */       if (evalue > bounds[1]) break;
/* 111 */       if (ss.correct > pos) {
/* 112 */         fn -= 1.0D;
/* 113 */       } else if (ss.correct < neg)
/* 114 */         fp += 1.0D;
/* 115 */       if (evalue > bounds[0]) {
/* 116 */         falseP.add(evalue, fp);
/* 117 */         falseN.add(evalue, fn);
/*     */       }
/*     */     }
/* 120 */     return new XYSeries[] { falseN, falseP };
/*     */   }
/*     */   
/*     */   public XYSeriesCollection getDirectComparisonData(int x, int y, int mult_factor, double min, double max) {
/* 124 */     if (this.sorted) throw new RuntimeException("already sorted - must do this before sorting");
/* 125 */     XYSeries[] res = new XYSeries[3];
/* 126 */     XYSeriesCollection collection = new XYSeriesCollection();
/* 127 */     String[] names = { "different fold", "same fold, different superfamily", "same superfamily" };
/* 128 */     for (int i = 0; i < res.length; i++) {
/* 129 */       res[i] = new XYSeries(names[i]);
/*     */     }
/*     */     
/* 132 */     for (int i = 0; i < this.hmm[0].size(); i++) {
/* 133 */       Entry[] entry = new Entry[this.hmm.length];
/* 134 */       for (int j = 0; j < entry.length; j++) {
/* 135 */         entry[j] = ((Entry)this.hmm[j].get(i));
/*     */       }
/* 137 */       int correct = entry[0].correct;
/* 138 */       if (correct != 0) correct = correct < 0 ? -1 : 1;
/* 139 */       double X = mult_factor * entry[x].score;
/* 140 */       double Y = mult_factor * entry[y].score;
/* 141 */       if ((X < max) && (X > min) && 
/* 142 */         (Y < max) && (Y > min))
/*     */       {
/* 144 */         res[(correct + 1)].add(X, Y);
/*     */       }
/*     */     }
/* 147 */     for (int i = 0; i < res.length; i++) {
/* 148 */       collection.addSeries(res[i]);
/*     */     }
/* 150 */     return collection;
/*     */   }
/*     */   
/*     */ 
/*     */   public XYSeriesCollection getRocCurveData(int totalP, int totalF)
/*     */   {
/* 156 */     XYSeriesCollection datas = new XYSeriesCollection();
/*     */     
/* 158 */     for (int i = 0; i < this.hmm.length; i++) {
/* 159 */       Collection l1 = percentageFalse(i);
/* 160 */       if (l1.size() > 0) {
/* 161 */         XYSeries data = new XYSeries(this.desc[i]);
/* 162 */         for (Iterator it = l1.iterator(); it.hasNext();) {
/* 163 */           int[] res = (int[])it.next();
/*     */           
/*     */ 
/* 166 */           data.add(res[0], res[1]);
/*     */         }
/*     */         
/* 169 */         datas.addSeries(data);
/*     */       }
/*     */     }
/* 172 */     return datas;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static class Entry
/*     */   {
/*     */     public float score;
/*     */     
/*     */ 
/* 182 */     public int correct = 0;
/*     */     public String name;
/*     */     
/*     */     public String toString() {
/* 186 */       return 
/*     */       
/* 188 */         this.name + " " + this.score + " " + this.correct;
/*     */     }
/*     */     
/* 191 */     void print(PrintWriter pw) { pw.println(toString()); }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public int[] getTPFPAboveEvalue(double evalue, int i)
/*     */   {
/* 199 */     sortResults();
/* 200 */     int[] res = new int[2];
/* 201 */     for (Iterator it = this.hmm[i].iterator(); it.hasNext();) {
/* 202 */       Entry ss = (Entry)it.next();
/* 203 */       double score = ss.score;
/* 204 */       if (score < evalue) {
/*     */         break;
/*     */       }
/*     */       
/*     */ 
/* 209 */       if (ss.correct > pos)
/* 210 */         res[0] += 1;
/* 211 */       if (ss.correct < neg)
/* 212 */         res[1] += 1;
/*     */     }
/* 214 */     return res;
/*     */   }
/*     */   
/*     */ 
/*     */   int[] numberPosAboveHighestFalse()
/*     */   {
/* 220 */     sortResults();
/*     */     
/* 222 */     int[] ots = new int[this.hmm.length];
/* 223 */     for (int i = 0; i < this.hmm.length; i++) {
/* 224 */       int j = 0;
/* 225 */       for (Iterator it = this.hmm[i].iterator(); it.hasNext();) {
/* 226 */         Entry ss = (Entry)it.next();
/*     */         
/*     */ 
/* 229 */         if (ss.correct < neg)
/*     */           break;
/* 231 */         if (ss.correct > pos)
/* 232 */           j++;
/*     */       }
/* 234 */       ots[i] = j;
/*     */     }
/* 236 */     return ots;
/*     */   }
/*     */   
/*     */   void print(File summary, String outp) throws Exception
/*     */   {
/* 241 */     File domjSc = new File(summary, outp + "." + this.desc[0]);
/* 242 */     File domjSc1 = new File(summary, outp + "." + this.desc[1]);
/* 243 */     PrintWriter psHMM = new PrintWriter(new BufferedWriter(new FileWriter(
/* 244 */       domjSc)));
/* 245 */     print(psHMM, this.hmm[0].iterator());
/* 246 */     psHMM.close();
/* 247 */     PrintWriter psHMM1 = new PrintWriter(new BufferedWriter(new FileWriter(
/* 248 */       domjSc1)));
/* 249 */     print(psHMM1, this.hmm[1].iterator());
/* 250 */     psHMM1.close();
/*     */   }
/*     */   
/*     */   public void printExtra(File dir, String name, int i) throws Exception {
/* 254 */     PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(
/* 255 */       new File(dir, name))));
/* 256 */     Collection s = overTheTopScores(i);
/* 257 */     s.removeAll(overTheTopScores(1 - i));
/* 258 */     for (Iterator it = s.iterator(); it.hasNext();) {
/* 259 */       pw.print(it.next());
/* 260 */       pw.println();
/*     */     }
/* 262 */     pw.close();
/*     */   }
/*     */   
/* 265 */   int[] ott = null;
/*     */   String name;
/*     */   
/*     */   public int[] OTS() {
/* 269 */     if (this.ott == null) {
/* 270 */       this.ott = new int[this.hmm.length];
/* 271 */       for (int i = 0; i < this.ott.length; i++) {
/* 272 */         this.ott[i] = overTheTopScores(i).size();
/*     */       }
/*     */     }
/* 275 */     return this.ott;
/*     */   }
/*     */   
/*     */   public Collection overTheTopScores(int i) {
/* 279 */     sortResults();
/* 280 */     Collection s = new ArrayList();
/* 281 */     for (Iterator it = this.hmm[i].iterator(); it.hasNext();) {
/* 282 */       Entry dom = (Entry)it.next();
/*     */       
/* 284 */       if (dom.correct < 0) {
/*     */         break;
/*     */       }
/* 287 */       if (dom.correct > 0) s.add(dom);
/*     */     }
/* 289 */     return s;
/*     */   }
/*     */   
/*     */   void print(PrintWriter psHMM, Iterator it) {
/* 293 */     int pos = 0;
/* 294 */     while (it.hasNext()) {
/* 295 */       Entry dom = (Entry)it.next();
/* 296 */       psHMM.print(pos + " ");
/* 297 */       dom.print(psHMM);
/* 298 */       psHMM.print(" : ");
/* 299 */       psHMM.print(" " + (
/* 300 */         dom.correct < 0 ? "false" : dom.correct > 0 ? "true" : ""));
/* 301 */       psHMM.println();
/* 302 */       pos++;
/*     */     }
/*     */   }
/*     */   
/*     */   double positionOfFirstNegative() {
/* 307 */     int i = 0;
/* 308 */     double total = 0.0D;
/* 309 */     int number = 0;
/* 310 */     for (Iterator it = this.hmm[0].iterator(); it.hasNext();) {
/* 311 */       int positive = ((Entry)it.next()).correct;
/* 312 */       if (positive < 0) {
/* 313 */         total += i / Math.exp(number);
/* 314 */         number++;
/*     */       }
/* 316 */       i++;
/*     */     }
/* 318 */     return total;
/*     */   }
/*     */   
/*     */   static Entry firstNegative(SortedSet sm) {
/* 322 */     for (Iterator it = sm.iterator(); it.hasNext();) {
/* 323 */       Entry ss = (Entry)it.next();
/* 324 */       if (ss.correct < 0)
/* 325 */         return ss;
/*     */     }
/* 327 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public String getName()
/*     */   {
/* 334 */     return this.name;
/*     */   }
/*     */   
/*     */   public ROC(String[] desc, String name) {
/* 338 */     this.desc = desc;
/* 339 */     this.name = name;
/* 340 */     this.hmm = new List[desc.length];
/* 341 */     for (int i = 0; i < this.hmm.length; i++) {
/* 342 */       this.hmm[i] = new ArrayList();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void print(File file)
/*     */     throws IOException
/*     */   {
/* 351 */     Double zero = new Double(0.0D);
/* 352 */     String[] header = new String[4 + this.desc.length];
/* 353 */     System.arraycopy(this.desc, 0, header, 2, this.desc.length);
/* 354 */     header[0] = "Name";
/* 355 */     header[1] = "Correct";
/* 356 */     String printDesc = "-%20s %7s";
/* 357 */     String rowDesc = "%-20s %7s ";
/* 358 */     for (int i = 0; i < this.desc.length; i++) {
/* 359 */       printDesc = printDesc + " %12s";
/* 360 */       rowDesc = rowDesc + " %12.2f";
/* 361 */       header[(2 + i)] = this.desc[i];
/*     */     }
/* 363 */     int cols = header.length;
/* 364 */     for (int i = 0; i < this.hmm.length; i++) {
/* 365 */       List display = new ArrayList();
/* 366 */       PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(
/* 367 */         new File(file.getAbsolutePath() + "." + this.desc[i]))));
/* 368 */       pw.println(Format.sprintf(printDesc, header));
/* 369 */       display.add(header);
/*     */       
/* 371 */       for (Iterator it = this.hmm[i].iterator(); it.hasNext();) {
/* 372 */         Entry ss = (Entry)it.next();
/* 373 */         Object[] row = new Object[header.length];
/*     */         
/* 375 */         row[1] = new Integer(ss.correct);
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 382 */         pw.println(Format.sprintf(rowDesc, row));
/* 383 */         display.add(row);
/*     */       }
/* 385 */       if (i == 0) {
/* 386 */         it = new AbstractTableModel() {
/*     */           public int getColumnCount() {
/* 388 */             return this.val$cols;
/*     */           }
/*     */           
/*     */           public int getRowCount() {
/* 392 */             return this.val$display.size();
/*     */           }
/*     */           
/*     */           public Object getValueAt(int row, int col) {
/* 396 */             return ((Object[])this.val$display.get(row))[col];
/*     */           }
/*     */         };
/*     */       }
/* 400 */       pw.close();
/*     */     }
/*     */   }
/*     */   
/*     */   public XYSeries[] getPercentageAboveThreshold(int i) {
/* 405 */     List[] l = { new ArrayList(), 
/* 406 */       new ArrayList() };
/* 407 */     XYSeries[] res = { new XYSeries("genes"), new XYSeries("pseudogenes") };
/* 408 */     for (Iterator it = this.hmm[i].iterator(); it.hasNext();) {
/* 409 */       Entry entry = (Entry)it.next();
/* 410 */       if ((!Double.isNaN(entry.score)) && (!Double.isInfinite(entry.score)))
/*     */       {
/* 412 */         if (entry.correct > 0) { l[1].add(entry);
/* 413 */         } else if (entry.correct < 0) l[0].add(entry); }
/*     */     }
/* 415 */     for (int j = 0; j < l.length; j++) {
/* 416 */       Collections.sort(l[j], COMPARISON);
/* 417 */       int length = l[j].size();
/* 418 */       for (int k = 0; k < length; k++) {
/* 419 */         Entry entry = (Entry)l[j].get(k);
/* 420 */         if (entry.score >= 0.001D)
/* 421 */           if (entry.score >= 999.0F) {
/* 422 */             res[j].add(entry.score, 0.0D);
/*     */           } else
/* 424 */             res[j].add(entry.score, k / length);
/*     */       }
/*     */     }
/* 427 */     return res;
/*     */   }
/*     */   
/*     */   public Histogram[] getHistograms(int i, int num_bins) {
/* 431 */     int numTrue = 0;
/* 432 */     int numFalse = 0;
/* 433 */     int min = Integer.MAX_VALUE;
/* 434 */     int max = Integer.MIN_VALUE;
/* 435 */     for (Iterator it = this.hmm[i].iterator(); it.hasNext();) {
/* 436 */       Entry ss = (Entry)it.next();
/* 437 */       if (ss.correct > 0)
/*     */       {
/* 439 */         numTrue++;
/* 440 */       } else if (ss.correct < 0)
/* 441 */         numFalse++;
/*     */     }
/* 443 */     double[] dataT = new double[numTrue];
/* 444 */     double[] dataF = new double[numFalse];
/* 445 */     int iT = 0;
/* 446 */     int iF = 0;
/*     */     
/* 448 */     for (Iterator it = this.hmm[i].iterator(); it.hasNext();) {
/* 449 */       Entry ss = (Entry)it.next();
/* 450 */       if (ss.correct > 0) {
/* 451 */         dataT[iT] = ss.score;
/* 452 */         iT++;
/* 453 */       } else { if (ss.correct >= 0) continue;
/* 454 */         dataF[iF] = ss.score;
/* 455 */         iF++;
/*     */         break label176;
/* 457 */         continue; }
/* 458 */       label176: if (ss.score < min) {
/* 459 */         min = (int)Math.floor(ss.score);
/*     */       }
/* 461 */       if (ss.score > max) {
/* 462 */         max = (int)Math.ceil(ss.score);
/*     */       }
/*     */     }
/* 465 */     Histogram[] hist = { new Histogram(min, max, num_bins), 
/* 466 */       new Histogram(min, max, num_bins) };
/* 467 */     hist[0].update(dataT);
/* 468 */     hist[1].update(dataF);
/* 469 */     return hist;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   static void print(Set sm, PrintWriter pw)
/*     */   {
/* 478 */     for (Iterator it = sm.iterator(); it.hasNext(); pos += 1) {
/* 479 */       pw.print(pos + " ");
/* 480 */       ((Entry)it.next()).print(pw);
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
/*     */   public int[] minimumErrorRate()
/*     */   {
/* 505 */     int[] res = new int[this.hmm.length];
/* 506 */     for (int i = 0; i < res.length; i++) {
/* 507 */       res[i] = minimumErrorRate(i);
/*     */     }
/* 509 */     return res;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private int minimumErrorRate(int i)
/*     */   {
/* 533 */     sortResults();
/* 534 */     List sm = this.hmm[i];
/* 535 */     int totalP = 0;
/* 536 */     int totalN = 0;
/* 537 */     for (Iterator it = sm.iterator(); it.hasNext();) {
/* 538 */       Entry ss = (Entry)it.next();
/* 539 */       if (ss.correct > pos) {
/* 540 */         totalP++;
/* 541 */       } else if (ss.correct < neg)
/* 542 */         totalN++;
/*     */     }
/* 544 */     int falseP = 0;
/* 545 */     int falseN = totalP;
/* 546 */     int min = falseP + falseN;
/* 547 */     for (Iterator it = sm.iterator(); it.hasNext();) {
/* 548 */       Entry ss = (Entry)it.next();
/* 549 */       if (ss.correct > neg) {
/* 550 */         falseN--;
/* 551 */       } else if (ss.correct < pos)
/* 552 */         falseP++;
/* 553 */       int misclass = falseP + falseN;
/* 554 */       if (misclass < min)
/* 555 */         min = misclass;
/*     */     }
/* 557 */     return min;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   Collection percentageFalse(int index)
/*     */   {
/* 567 */     sortResults();
/* 568 */     int[] size = new int[this.hmm.length];
/* 569 */     for (int i = 0; i < this.hmm.length; i++) {
/* 570 */       size[i] = this.hmm[i].size();
/* 571 */       if (this.hmm[i].size() != this.hmm[0].size()) throw new RuntimeException("lengths not same");
/*     */     }
/* 573 */     int no_false_pos = 0;
/* 574 */     int no_true_pos = 0;
/* 575 */     boolean print = index == 1;
/* 576 */     Comparator comp = new Comparator() {
/*     */       public int compare(Object o1, Object o2) {
/* 578 */         int[] res1 = (int[])o1;
/* 579 */         int[] res2 = (int[])o2;
/*     */         
/* 581 */         return res1[0] - res2[0];
/*     */       }
/*     */       
/*     */ 
/* 585 */     };
/* 586 */     List spec = new ArrayList();
/* 587 */     SortedSet l = new TreeSet(comp);
/*     */     
/* 589 */     for (Iterator it = this.hmm[index].iterator(); it.hasNext();) {
/* 590 */       Entry ss = (Entry)it.next();
/* 591 */       if (ss.correct > pos) {
/* 592 */         no_true_pos++;
/* 593 */       } else if (ss.correct < neg) {
/* 594 */         no_false_pos++;
/*     */       }
/* 596 */       if (no_false_pos > 2000) break;
/* 597 */       int[] res = { no_false_pos, no_true_pos };
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 602 */       l.add(res);
/*     */     }
/*     */     
/* 605 */     return l;
/*     */   }
/*     */   
/*     */ 
/* 609 */   public static final Comparator COMPARISON = new Comparator() {
/*     */     public int compare(Object o1, Object o2) {
/* 611 */       ROC.Entry ss1 = (ROC.Entry)o1;
/* 612 */       ROC.Entry ss2 = (ROC.Entry)o2;
/*     */       
/* 614 */       if (ss1.score != ss2.score) {
/* 615 */         return ss1.score < ss2.score ? 1 : -1;
/*     */       }
/* 617 */       if (ss1.correct != ss2.correct)
/* 618 */         return ss1.correct < ss2.correct ? -1 : 1;
/* 619 */       return 0;
/*     */     }
/*     */     
/*     */     public boolean equals(Object o)
/*     */     {
/* 624 */       return this == o;
/*     */     }
/*     */   };
/*     */   
/*     */   static final String format = "%8s %-20s %6.2f %5.1e %4d %4s";
/*     */   
/*     */   static final String format1 = "%8s %-20s %6s %6s %4s %4s";
/*     */   
/* 632 */   static final Color[] color = { Color.BLUE, Color.RED, Color.BLACK, Color.RED, Color.GREEN };
/*     */   
/* 634 */   static final String header = Format.sprintf("%8s %-20s %6s %6s %4s %4s", 
/* 635 */     new Parameters("Name").add("Description").add("Score").add("Evalue").add("N").add(
/* 636 */     "modelId"));
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public List getFirstFalse(int i, int no)
/*     */   {
/* 644 */     List l = new ArrayList();
/* 645 */     for (Iterator it = this.hmm[i].iterator(); it.hasNext();) {
/* 646 */       Entry dom = (Entry)it.next();
/*     */       
/* 648 */       if (dom.correct < 0) {
/* 649 */         l.add(dom);
/* 650 */         if (l.size() > no) {
/*     */           break;
/*     */         }
/*     */       }
/*     */     }
/* 655 */     return l;
/*     */   }
/*     */ }


/* Location:              /home/lachlan/Desktop/psilc1.21/psilc.jar!/lc1/dp/ROC.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */