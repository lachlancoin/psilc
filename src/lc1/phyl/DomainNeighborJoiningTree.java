/*     */ package lc1.phyl;
/*     */ 
/*     */ import com.braju.format.Format;
/*     */ import com.braju.format.Parameters;
/*     */ import java.io.BufferedWriter;
/*     */ import java.io.File;
/*     */ import java.io.FileNotFoundException;
/*     */ import java.io.FileWriter;
/*     */ import java.io.IOException;
/*     */ import java.io.PrintWriter;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Comparator;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import java.util.TreeSet;
/*     */ import javax.swing.JTable;
/*     */ import javax.swing.table.AbstractTableModel;
/*     */ import javax.swing.table.TableModel;
/*     */ import org.biojava.bio.Annotation;
/*     */ import org.biojava.bio.dist.Distribution;
/*     */ import org.biojava.bio.symbol.FiniteAlphabet;
/*     */ import org.biojava.bio.symbol.IllegalSymbolException;
/*     */ import org.biojava.bio.symbol.Symbol;
/*     */ import pal.misc.IdGroup;
/*     */ import pal.misc.Identifier;
/*     */ import pal.tree.Node;
/*     */ import pal.tree.NodeFactory;
/*     */ import pal.tree.NodeUtils;
/*     */ import pal.tree.SimpleTree;
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
/*     */ public class DomainNeighborJoiningTree
/*     */   extends SimpleTree
/*     */ {
/*     */   File outpDir;
/*     */   private IdGroup m;
/*     */   private int numClusters;
/*     */   private Node newCluster;
/*     */   private int besti;
/*     */   private int abi;
/*     */   private int bestj;
/*     */   private int abj;
/*     */   private int[] alias;
/*     */   private double[][] distance;
/*     */   private Map[][] distanceDom;
/*     */   FiniteAlphabet alph;
/*     */   private double[] r;
/*     */   private Map[] rDom;
/*     */   private double scale;
/*     */   
/*     */   public DomainNeighborJoiningTree(Distribution[] dist, FiniteAlphabet alph, IdGroup m, File outpDir)
/*     */   {
/*  70 */     this.m = m;
/*  71 */     this.outpDir = outpDir;
/*  72 */     if (dist.length < 3)
/*     */     {
/*  74 */       new IllegalArgumentException("LESS THAN 3 TAXA IN DISTANCE MATRIX");
/*     */     }
/*     */     
/*  77 */     this.alph = alph;
/*  78 */     init(dist);
/*  79 */     int count = 0;
/*     */     
/*     */ 
/*     */     for (;;)
/*     */     {
/*  84 */       findNextPair();
/*  85 */       newBranchLengths();
/*  86 */       if (this.numClusters == 3) {
/*     */         break;
/*     */       }
/*     */       
/*  90 */       newCluster();
/*  91 */       count++;
/*     */     }
/*     */     
/*  94 */     finish();
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private double getDist(int a, int b)
/*     */   {
/* 123 */     return this.distance[this.alias[a]][this.alias[b]];
/*     */   }
/*     */   
/*     */ 
/*     */   private Map getDistMap(int a, int b)
/*     */   {
/* 129 */     return this.distanceDom[this.alias[a]][this.alias[b]];
/*     */   }
/*     */   
/*     */   private void init(Distribution[] dist)
/*     */   {
/* 134 */     this.numClusters = dist.length;
/*     */     
/* 136 */     this.distance = new double[this.numClusters][this.numClusters];
/* 137 */     this.distanceDom = new HashMap[this.numClusters][this.numClusters];
/* 138 */     this.rDom = new HashMap[this.numClusters];
/* 139 */     for (int i = 0; i < this.numClusters; i++) {
/* 140 */       this.distance[i][i] = 0.0D;
/* 141 */       this.distanceDom[i][i] = new HashMap();
/* 142 */       for (int j = 0; j < i; j++) {
/* 143 */         this.distanceDom[i][j] = new HashMap();
/* 144 */         this.distanceDom[j][i] = this.distanceDom[i][j];
/* 145 */         double total = 0.0D;
/* 146 */         for (Iterator it = this.alph.iterator(); it.hasNext();) {
/*     */           try {
/* 148 */             Symbol sym = (Symbol)it.next();
/* 149 */             double p = dist[j].getWeight(sym);
/* 150 */             double q = dist[i].getWeight(sym);
/* 151 */             if (q != p) {
/* 152 */               double score = p * Math.log(p / q) + q * Math.log(q / p);
/* 153 */               this.distanceDom[i][j].put(sym, new Double(score));
/* 154 */               total += score;
/*     */             }
/* 156 */           } catch (IllegalSymbolException exc) { exc.printStackTrace();
/*     */           }
/*     */         }
/*     */         
/* 160 */         this.distance[i][j] = total;
/* 161 */         this.distance[j][i] = total;
/*     */       }
/* 163 */       this.rDom[i] = new HashMap();
/*     */     }
/* 165 */     for (int i = 0; i < this.numClusters; i++)
/*     */     {
/* 167 */       Node tmp = NodeFactory.createNode();
/* 168 */       tmp.setIdentifier(this.m.getIdentifier(i));
/* 169 */       getRoot().addChild(tmp);
/*     */     }
/*     */     
/* 172 */     this.alias = new int[this.numClusters];
/* 173 */     for (int i = 0; i < this.numClusters; i++)
/*     */     {
/* 175 */       this.alias[i] = i;
/*     */     }
/*     */     
/* 178 */     this.r = new double[this.numClusters];
/*     */   }
/*     */   
/*     */   private void finish()
/*     */   {
/* 183 */     if ((this.besti != 0) && (this.bestj != 0))
/*     */     {
/* 185 */       getRoot().getChild(0).setBranchLength(updatedDistance(this.besti, this.bestj, 0));
/*     */     }
/* 187 */     else if ((this.besti != 1) && (this.bestj != 1))
/*     */     {
/* 189 */       getRoot().getChild(1).setBranchLength(updatedDistance(this.besti, this.bestj, 1));
/*     */     }
/*     */     else
/*     */     {
/* 193 */       getRoot().getChild(2).setBranchLength(updatedDistance(this.besti, this.bestj, 2));
/*     */     }
/* 195 */     this.distance = null;
/*     */     
/*     */ 
/* 198 */     NodeUtils.lengths2Heights(getRoot());
/*     */   }
/*     */   
/*     */   private void findNextPair()
/*     */   {
/* 203 */     for (int i = 0; i < this.numClusters; i++)
/*     */     {
/* 205 */       this.r[i] = 0.0D;
/* 206 */       this.rDom[i] = new HashMap();
/* 207 */       for (int j = 0; j < this.numClusters; j++)
/*     */       {
/* 209 */         this.r[i] += getDist(i, j);
/* 210 */         Map distM = getDistMap(i, j);
/* 211 */         Object[] keys = distM.keySet().toArray();
/* 212 */         for (int il = 0; il < keys.length; il++) {
/* 213 */           double val = ((Double)distM.get(keys[il])).doubleValue();
/* 214 */           if (this.rDom[i].containsKey(keys[il]))
/* 215 */             val += ((Double)this.rDom[i].get(keys[il])).doubleValue();
/* 216 */           this.rDom[i].put(keys[il], new Double(val));
/*     */         }
/*     */       }
/*     */     }
/* 220 */     this.besti = 0;
/* 221 */     this.bestj = 1;
/* 222 */     double smax = -1.0D;
/*     */     
/* 224 */     this.scale = (this.numClusters == 2 ? 1.0D : 1.0D / (this.numClusters - 2));
/* 225 */     for (int i = 0; i < this.numClusters - 1; i++)
/*     */     {
/* 227 */       for (int j = i + 1; j < this.numClusters; j++)
/*     */       {
/* 229 */         double sij = (this.r[i] + this.r[j]) * this.scale - getDist(i, j);
/*     */         
/* 231 */         if (sij > smax)
/*     */         {
/* 233 */           smax = sij;
/* 234 */           this.besti = i;
/* 235 */           this.bestj = j;
/*     */         }
/*     */       }
/*     */     }
/* 239 */     this.abi = this.alias[this.besti];
/* 240 */     this.abj = this.alias[this.bestj];
/*     */   }
/*     */   
/*     */   private void newBranchLengths()
/*     */   {
/* 245 */     double dij = getDist(this.besti, this.bestj);
/* 246 */     double li = (dij + (this.r[this.besti] - this.r[this.bestj]) * this.scale) * 0.5D;
/*     */     
/* 248 */     if (li > dij) { li = dij - 1.0E-8D;
/* 249 */     } else if (li < 0.0D) { li = 1.0E-8D;
/*     */     }
/* 251 */     double lj = dij - li;
/*     */     
/* 253 */     getRoot().getChild(this.besti).setBranchLength(li);
/* 254 */     getRoot().getChild(this.bestj).setBranchLength(lj);
/* 255 */     Node childi = getRoot().getChild(this.besti);
/* 256 */     Node childj = getRoot().getChild(this.bestj);
/* 257 */     Set sm_i = new TreeSet(DoubleStringComparator);
/* 258 */     Set sm_j = new TreeSet(DoubleStringComparator);
/* 259 */     Map map_ij = getDistMap(this.besti, this.bestj);
/* 260 */     Object[] keys = map_ij.keySet().toArray();
/* 261 */     for (int ik = 0; ik < keys.length; ik++) {
/* 262 */       String name = (String)((Symbol)keys[ik]).getAnnotation().getProperty("pfamA_id");
/* 263 */       String id = ((Symbol)keys[ik]).getName();
/* 264 */       double value = ((Double)map_ij.get(keys[ik])).doubleValue();
/* 265 */       double liM = (value + (((Double)this.rDom[this.besti].get(keys[ik])).doubleValue() - ((Double)this.rDom[this.bestj].get(keys[ik])).doubleValue() * this.scale)) * 0.5D;
/* 266 */       sm_i.add(new DoubleString(name, id, liM));
/* 267 */       sm_j.add(new DoubleString(name, id, value - liM));
/*     */     }
/*     */     
/*     */     try
/*     */     {
/* 272 */       printAnalysis(childi, sm_i);
/* 273 */       printAnalysis(childj, sm_j);
/*     */     } catch (Exception exc) {
/* 275 */       exc.printStackTrace();
/*     */     }
/*     */   }
/*     */   
/*     */   static class DoubleString
/*     */   {
/*     */     Double d;
/*     */     String s;
/*     */     String s1;
/*     */     
/*     */     DoubleString(String s, String s1, double d)
/*     */     {
/* 287 */       this.s1 = s1;
/* 288 */       this.d = new Double(d);
/* 289 */       this.s = s;
/*     */     }
/*     */     
/* 292 */     public String toString() { return Format.sprintf("%-15s %10s %4.2e", new Object[] { this.s, this.s1, this.d }); }
/*     */   }
/*     */   
/* 295 */   static final Comparator DoubleStringComparator = new Comparator() {
/*     */     public int compare(Object o1, Object o2) {
/* 297 */       DomainNeighborJoiningTree.DoubleString ds1 = (DomainNeighborJoiningTree.DoubleString)o1;
/* 298 */       DomainNeighborJoiningTree.DoubleString ds2 = (DomainNeighborJoiningTree.DoubleString)o2;
/* 299 */       int res = Double.compare(ds1.d.doubleValue(), ds2.d.doubleValue());
/* 300 */       if (res != 0) return -1 * res;
/* 301 */       return Double.compare(ds1.s.hashCode(), ds2.s.hashCode());
/*     */     }
/*     */     
/* 304 */     public boolean compare(Object o) { return o == this; }
/*     */   };
/*     */   
/*     */   public void printAnalysis(Node n, Set s)
/*     */     throws IOException, FileNotFoundException
/*     */   {
/* 310 */     PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(new File(this.outpDir, n.getIdentifier().getName() + ".distance"))));
/* 311 */     for (Iterator it = s.iterator(); it.hasNext();) {
/* 312 */       pw.println(it.next());
/*     */     }
/*     */   }
/*     */   
/*     */   public static JTable getAnalysis(Set s)
/*     */   {
/* 318 */     List l = new ArrayList();
/* 319 */     int i = 0;
/* 320 */     for (Iterator it = s.iterator(); (it.hasNext()) && (i < 100); i++) {
/* 321 */       l.add(it.next());
/*     */     }
/* 323 */     int rows = l.size() + 1;
/* 324 */     TableModel tm = new AbstractTableModel() {
/*     */       public int getColumnCount() {
/* 326 */         return 3;
/*     */       }
/*     */       
/* 329 */       public int getRowCount() { return this.val$rows; }
/*     */       
/*     */       public Object getValueAt(int row, int col) {
/* 332 */         if (row == 0) return col == 1 ? "pfamA_acc" : col == 0 ? "pfamA_id" : "length";
/* 333 */         DomainNeighborJoiningTree.DoubleString ds = (DomainNeighborJoiningTree.DoubleString)this.val$l.get(row - 1);
/* 334 */         if (col == 0) return ds.s;
/* 335 */         if (col == 1) return ds.s1;
/* 336 */         return Format.sprintf("%4.2e", new Parameters(ds.d));
/*     */       }
/* 338 */     };
/* 339 */     return new JTable(tm);
/*     */   }
/*     */   
/*     */ 
/*     */   private void newCluster()
/*     */   {
/* 345 */     for (int k = 0; k < this.numClusters; k++)
/*     */     {
/* 347 */       if ((k != this.besti) && (k != this.bestj))
/*     */       {
/* 349 */         int ak = this.alias[k];
/* 350 */         this.distance[ak][this.abi] = (this.distance[this.abi][ak] = updatedDistance(this.besti, this.bestj, k));
/* 351 */         Map dist_ak = new HashMap();
/* 352 */         Map dist_ki = getDistMap(k, this.besti);
/* 353 */         Map dist_kj = getDistMap(k, this.bestj);
/* 354 */         Map dist_ij = getDistMap(this.besti, this.bestj);
/* 355 */         for (Iterator it = this.alph.iterator(); it.hasNext();) {
/* 356 */           Object sym = it.next();
/* 357 */           double result = 0.5D * (((Double)dist_ki.get(sym)).doubleValue() + ((Double)dist_kj.get(sym)).doubleValue() - ((Double)dist_ij.get(sym)).doubleValue());
/* 358 */           dist_ak.put(sym, new Double(result));
/*     */         }
/* 360 */         this.distanceDom[ak][this.abi] = dist_ak;
/* 361 */         this.distanceDom[this.abi][ak] = dist_ak;
/*     */       }
/*     */     }
/* 364 */     this.distance[this.abi][this.abi] = 0.0D;
/* 365 */     this.distanceDom[this.abi][this.abi] = new HashMap();
/*     */     
/*     */ 
/* 368 */     NodeUtils.joinChilds(getRoot(), this.besti, this.bestj);
/*     */     
/*     */ 
/* 371 */     for (int i = this.bestj; i < this.numClusters - 1; i++)
/*     */     {
/* 373 */       this.alias[i] = this.alias[(i + 1)];
/*     */     }
/*     */     
/* 376 */     this.numClusters -= 1;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private double updatedDistance(int i, int j, int k)
/*     */   {
/* 385 */     return (getDist(k, i) + getDist(k, j) - getDist(i, j)) * 0.5D;
/*     */   }
/*     */ }


/* Location:              /home/lachlan/Desktop/psilc1.21/psilc.jar!/lc1/phyl/DomainNeighborJoiningTree.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */