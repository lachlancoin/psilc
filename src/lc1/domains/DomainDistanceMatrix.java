/*     */ package lc1.domains;
/*     */ 
/*     */ import java.io.BufferedReader;
/*     */ import java.io.File;
/*     */ import java.io.FileFilter;
/*     */ import java.io.FileReader;
/*     */ import java.io.FileWriter;
/*     */ import java.io.PrintWriter;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collection;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import java.util.SortedMap;
/*     */ import java.util.TreeMap;
/*     */ import javax.swing.JTable;
/*     */ import lc1.pfam.DomainAlphabet;
/*     */ import lc1.pfam.PfamAlphabet;
/*     */ import lc1.phyl.NeighborJoiningTree;
/*     */ import org.apache.commons.cli.CommandLine;
/*     */ import org.apache.commons.cli.OptionBuilder;
/*     */ import org.apache.commons.cli.Options;
/*     */ import org.apache.commons.cli.PosixParser;
/*     */ import org.biojava.bio.dist.AbstractDistribution;
/*     */ import org.biojava.bio.dist.Distribution;
/*     */ import org.biojava.bio.seq.io.SymbolTokenization;
/*     */ import org.biojava.bio.symbol.Alphabet;
/*     */ import org.biojava.bio.symbol.AtomicSymbol;
/*     */ import org.biojava.bio.symbol.FiniteAlphabet;
/*     */ import org.biojava.bio.symbol.IllegalAlphabetException;
/*     */ import org.biojava.bio.symbol.IllegalSymbolException;
/*     */ import org.biojava.bio.symbol.Symbol;
/*     */ import org.biojava.utils.ChangeVetoException;
/*     */ import pal.distance.DistanceMatrix;
/*     */ import pal.misc.IdGroup;
/*     */ import pal.misc.Identifier;
/*     */ import pal.misc.SimpleIdGroup;
/*     */ import pal.tree.TreeGenerator;
/*     */ import pal.tree.TreeUtils;
/*     */ import pal.util.AlgorithmCallback;
/*     */ import pal.util.AlgorithmCallback.Utils;
/*     */ 
/*     */ public class DomainDistanceMatrix
/*     */   implements TreeGenerator
/*     */ {
/*  50 */   static final Options OPTIONS = new Options() {};
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  58 */   static FileFilter countFilter = new FileFilter()
/*     */   {
/*     */     public boolean accept(File f) {
/*  61 */       return (f.getName().endsWith(".count")) && (!f.getName().startsWith("back"));
/*     */     }
/*     */   };
/*     */   
/*  65 */   static FileFilter ff = new FileFilter() {
/*     */     public boolean accept(File f) {
/*  67 */       return f.getName().endsWith(".spacc");
/*     */     }
/*     */   };
/*     */   
/*     */   Distribution[] freq;
/*     */   
/*     */   Set domainSet;
/*     */   
/*     */   IdGroup idg;
/*     */   
/*     */   String method;
/*     */   
/*     */   double pow;
/*     */   
/*     */   double percRemove;
/*     */   
/*     */   DomainAlphabet alph;
/*     */   
/*     */   public DomainDistanceMatrix(File dir, DomainAlphabet alph, String method, double pow, double percRemove, double pseudo_count)
/*     */     throws Exception
/*     */   {
/*  88 */     this.alph = alph;
/*  89 */     this.pow = pow;
/*  90 */     this.method = method;
/*  91 */     this.percRemove = percRemove;
/*     */     
/*  93 */     SymbolTokenization tokenP = alph.getTokenization("token");
/*  94 */     File[] files = dir.listFiles(countFilter);
/*  95 */     this.freq = new Distribution[files.length];
/*  96 */     Identifier[] ids = new Identifier[files.length];
/*  97 */     this.domainSet = new HashSet();
/*  98 */     for (int i = 0; i < files.length; i++)
/*     */     {
/* 100 */       Frequency freq_i = new Frequency();
/* 101 */       ids[i] = new Identifier(files[i].getName().split("\\.")[0]);
/* 102 */       String s = "";
/* 103 */       BufferedReader br = new BufferedReader(new FileReader(files[i]));
/* 104 */       while ((s = br.readLine()) != null) {
/* 105 */         String[] st = s.split("\\s+");
/* 106 */         freq_i.setCount(tokenP.parseToken(st[0]), 
/* 107 */           Integer.parseInt(st[1]));
/*     */       }
/* 109 */       this.freq[i] = freq_i.getDistribution(alph, pseudo_count);
/* 110 */       this.domainSet.addAll(Arrays.asList(freq_i.keys()));
/*     */     }
/* 112 */     this.idg = new SimpleIdGroup(ids);
/*     */   }
/*     */   
/*     */   public pal.tree.Tree calculateTree(Set excl) {
/*     */     try {
/* 117 */       double[][] dist = new double[this.freq.length][this.freq.length];
/* 118 */       for (int i = 0; i < dist.length; i++) {
/* 119 */         dist[i][i] = 0.0D;
/* 120 */         for (int j = 0; j < i; j++) {
/* 121 */           Distribution dist1 = exclude(this.freq[i], excl);
/* 122 */           Distribution dist2 = exclude(this.freq[j], excl);
/*     */           
/* 124 */           if (this.method.equals("squares")) {
/* 125 */             dist[i][j] = getSquaredDistance(dist1, dist2, this.pow);
/*     */           }
/* 127 */           else if (this.method.equals("rank")) {
/* 128 */             dist[i][j] = getRankDistance(dist1, dist2, this.pow);
/* 129 */           } else if (this.method.startsWith("entropy"))
/* 130 */             dist[i][j] = ((relativeEntropy(dist1, dist2) + relativeEntropy(
/* 131 */               dist2, dist1)) / 2.0D);
/* 132 */           dist[j][i] = dist[i][j];
/*     */         }
/*     */       }
/*     */       
/* 136 */       return new NeighborJoiningTree(new DistanceMatrix(dist, 
/* 137 */         this.idg));
/*     */     } catch (Exception exc) {
/* 139 */       exc.printStackTrace(); }
/* 140 */     return null;
/*     */   }
/*     */   
/*     */   public pal.tree.Tree getNextTree(pal.tree.Tree baseTree, AlgorithmCallback callback)
/*     */   {
/* 145 */     List keys = new ArrayList(this.domainSet);
/* 146 */     Set excl = new HashSet();
/* 147 */     int num = (int)Math.floor(this.percRemove * this.domainSet.size());
/* 148 */     while (excl.size() < num) {
/* 149 */       int index = (int)Math.floor(Math.random() * keys.size());
/* 150 */       excl.add(keys.get(index));
/* 151 */       keys.remove(index);
/*     */     }
/*     */     
/* 154 */     pal.tree.Tree tree = calculateTree(excl);
/*     */     
/* 156 */     return tree;
/*     */   }
/*     */   
/*     */   public static void buildTree(String[] args) throws Exception {
/* 160 */     CommandLine params = new PosixParser().parse(OPTIONS, args);
/* 161 */     File repos = new File(params.getOptionValue("repository"));
/* 162 */     File outpDir = new File(params.getOptionValue("dir", "."));
/* 163 */     DomainAlphabet alph = PfamAlphabet.makeAlphabet(repos);
/* 164 */     String[] input = { "squares", "2" };
/* 165 */     if (params.hasOption("input")) {
/* 166 */       input = params.getOptionValues("input");
/*     */     }
/* 168 */     String[] rand = params.getOptionValues("randomize");
/* 169 */     int repeats = Integer.parseInt(rand[0]);
/* 170 */     double perc = Double.parseDouble(rand[1]);
/*     */     
/* 172 */     DomainDistanceMatrix ddm = new DomainDistanceMatrix(outpDir, alph, 
/* 173 */       input[0], input.length == 1 ? 2.0D : 
/* 174 */       Double.parseDouble(input[1]), perc, Double.parseDouble(
/* 175 */       params.getOptionValue("pseudo", "0.0")));
/*     */     
/* 177 */     pal.tree.Tree baseTree = input[0].equals("entropy_all") ? null : 
/*     */     
/*     */ 
/* 180 */       ddm.calculateTree(new HashSet());
/*     */     
/*     */ 
/* 183 */     pal.tree.Tree tree = TreeUtils.getReplicateCladeSupport("B", baseTree, ddm, 
/* 184 */       repeats, AlgorithmCallback.Utils.getNullCallback());
/*     */     
/* 186 */     PrintWriter pw = new PrintWriter(new FileWriter(new File(outpDir, 
/* 187 */       "domainTree.nh")));
/* 188 */     TreeUtils.printNH(tree, pw);
/* 189 */     pw.close();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private static forester.tree.Tree getForesterTree(pal.tree.Tree tree)
/*     */     throws Exception
/*     */   {
/* 197 */     forester.tree.Tree treeF = new forester.tree.Tree(tree.toString());
/* 198 */     for (int i = 0; i < tree.getExternalNodeCount(); i++) {
/* 199 */       String name = tree.getExternalNode(i).getIdentifier().getName();
/* 200 */       forester.tree.Node node = treeF.getNode(name);
/* 201 */       pal.tree.Node node_t = tree.getExternalNode(i);
/* 202 */       while (!node_t.isRoot())
/*     */       {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 213 */         node.setAnalysis((JTable)tree.getAttribute(node_t, 
/* 214 */           "analysis"));
/*     */         
/* 216 */         node = node.getParent();
/* 217 */         node_t = node_t.getParent();
/*     */       }
/*     */     }
/* 220 */     return treeF;
/*     */   }
/*     */   
/*     */   public static double relativeEntropy(Distribution dist1, Distribution dist2) throws Exception
/*     */   {
/* 225 */     double ent = 0.0D;
/* 226 */     for (Iterator it = ((FiniteAlphabet)dist1.getAlphabet()).iterator(); it
/* 227 */           .hasNext();) {
/* 228 */       Symbol sym = (Symbol)it.next();
/* 229 */       double weight = dist1.getWeight(sym);
/* 230 */       if (weight != 0.0D)
/*     */       {
/* 232 */         ent += weight * Math.log(weight / dist2.getWeight(sym)); }
/*     */     }
/* 234 */     return ent;
/*     */   }
/*     */   
/*     */   public static double getSquaredDistance(Distribution freq1, Distribution freq2, double pow)
/*     */     throws Exception
/*     */   {
/* 240 */     double dist = 0.0D;
/* 241 */     for (Iterator it = ((FiniteAlphabet)freq1.getAlphabet()).iterator(); it
/* 242 */           .hasNext();) {
/* 243 */       Symbol sym = (Symbol)it.next();
/* 244 */       if ((freq1.getWeight(sym) != 0.0D) || (freq2.getWeight(sym) != 0.0D))
/*     */       {
/*     */ 
/*     */ 
/* 248 */         dist = dist + Math.abs(Math.pow(freq1.getWeight(sym) - freq2.getWeight(sym), pow));
/*     */       }
/*     */     }
/*     */     
/* 252 */     return dist;
/*     */   }
/*     */   
/*     */ 
/*     */   public static double getRankDistance(Distribution freq1, Distribution freq2, double pow)
/*     */   {
/* 258 */     Map m1 = getRankedMap(freq1);
/* 259 */     Map m2 = getRankedMap(freq2);
/* 260 */     double d = 0.0D;
/* 261 */     for (Iterator it = ((FiniteAlphabet)freq1.getAlphabet()).iterator(); it
/* 262 */           .hasNext();) {
/* 263 */       Symbol sym = (Symbol)it.next();
/* 264 */       d += Math.pow(((Double)m1.get(sym)).doubleValue() - ((Double)m2.get(sym)).doubleValue(), pow);
/*     */     }
/* 266 */     return d;
/*     */   }
/*     */   
/*     */   private static Map getRankedMap(Distribution dist)
/*     */   {
/* 271 */     SortedMap m = new TreeMap();
/* 272 */     for (Iterator it = ((FiniteAlphabet)dist.getAlphabet()).iterator(); it
/* 273 */           .hasNext();) {
/*     */       try {
/* 275 */         Symbol sym = (Symbol)it.next();
/* 276 */         Double weight = new Double(dist.getWeight(sym));
/* 277 */         if (m.containsKey(weight)) {
/* 278 */           ((List)m.get(weight)).add(sym);
/*     */         } else {
/* 280 */           List l = new ArrayList();
/* 281 */           l.add(sym);
/* 282 */           m.put(weight, l);
/*     */         }
/*     */       } catch (IllegalSymbolException exc) {
/* 285 */         exc.printStackTrace();
/*     */       }
/*     */     }
/* 288 */     Map results = new HashMap();
/* 289 */     int rank = 0;
/* 290 */     Iterator it1; for (Iterator it = m.keySet().iterator(); it.hasNext(); 
/*     */         
/*     */ 
/* 293 */         it1.hasNext())
/*     */     {
/* 291 */       List symbols = (List)m.get(it.next());
/* 292 */       rank += symbols.size();
/* 293 */       it1 = symbols.iterator(); continue;
/* 294 */       results.put(it1.next(), new Integer(rank));
/*     */     }
/*     */     
/* 297 */     return results;
/*     */   }
/*     */   
/*     */   private static Distribution exclude(Distribution dist, Collection excl)
/*     */   {
/* 302 */     new AbstractDistribution()
/*     */     {
/*     */       double totalWeight;
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       public double getWeightImpl(AtomicSymbol sym)
/*     */         throws IllegalSymbolException
/*     */       {
/* 317 */         if (DomainDistanceMatrix.this.contains(sym))
/* 318 */           return 0.0D;
/* 319 */         double weight = this.val$dist.getWeight(sym);
/* 320 */         return weight / this.totalWeight;
/*     */       }
/*     */       
/*     */       public void setWeightImpl(AtomicSymbol sym, double weight)
/*     */         throws IllegalSymbolException, ChangeVetoException
/*     */       {
/* 326 */         this.val$dist.setWeight(sym, weight);
/*     */       }
/*     */       
/*     */       public void setNullModelImpl(Distribution dist1)
/*     */         throws IllegalAlphabetException, ChangeVetoException
/*     */       {
/* 332 */         this.val$dist.setNullModel(dist1);
/*     */       }
/*     */       
/*     */       public Distribution getNullModel() {
/* 336 */         return this.val$dist.getNullModel();
/*     */       }
/*     */       
/*     */       public Alphabet getAlphabet() {
/* 340 */         return this.val$dist.getAlphabet();
/*     */       }
/*     */     };
/*     */   }
/*     */ }


/* Location:              /home/lachlan/Desktop/psilc1.21/psilc.jar!/lc1/domains/DomainDistanceMatrix.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */