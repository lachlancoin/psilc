/*     */ package lc1.pfam;
/*     */ 
/*     */ import gnu.trove.TObjectIntHashMap;
/*     */ import gnu.trove.TObjectIntProcedure;
/*     */ import java.io.BufferedWriter;
/*     */ import java.io.File;
/*     */ import java.io.FileWriter;
/*     */ import java.io.PrintStream;
/*     */ import java.io.PrintWriter;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import lc1.treefam.SDI;
/*     */ import lc1.treefam.TaxonomyTree;
/*     */ import lc1.util.SheetIO;
/*     */ import org.biojava.bio.Annotation;
/*     */ import org.biojava.bio.seq.Sequence;
/*     */ import org.biojava.bio.seq.SequenceIterator;
/*     */ import org.biojava.bio.seq.io.NameTokenization;
/*     */ import org.biojava.bio.symbol.FiniteAlphabet;
/*     */ import org.biojava.bio.symbol.FundamentalAtomicSymbol;
/*     */ import org.biojava.bio.symbol.IllegalSymbolException;
/*     */ import org.biojava.bio.symbol.SimpleAlphabet;
/*     */ import org.biojava.bio.symbol.SimpleSymbolList;
/*     */ import org.biojava.bio.symbol.Symbol;
/*     */ import org.biojava.bio.symbol.SymbolList;
/*     */ import pal.misc.Identifier;
/*     */ import pal.tree.Node;
/*     */ import pal.tree.NodeUtils;
/*     */ import pal.tree.SimpleTree;
/*     */ import pal.tree.Tree;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class SpeciesAlphabet
/*     */   extends SimpleAlphabet
/*     */ {
/*     */   private static SpeciesAlphabet alphabet;
/*     */   Tree tree;
/*     */   
/*     */   public static void reset(SpeciesAlphabet alph)
/*     */   {
/*  50 */     alphabet = alph;
/*     */   }
/*     */   
/*     */   private static void mapToNode(Map m, Node n, String parent) {
/*  54 */     String name = n.getIdentifier().getName();
/*  55 */     if (!m.containsKey(name)) {
/*  56 */       m.put(name, parent);
/*  57 */       for (int i = 0; i < n.getChildCount(); i++) {
/*  58 */         mapToNode(m, n.getChild(i), parent);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public static SpeciesAlphabet makeAlphabet(File repos, SequenceIterator it) throws Exception {
/*  64 */     Map m = new HashMap();
/*  65 */     if (alphabet == null) {
/*  66 */       Tree taxa = new TaxonomyTree(new File(repos, "nodes.dmp"), new File(repos, "names.dmp"), "root");
/*  67 */       Node root = taxa.getRoot();
/*  68 */       List toKeep = new ArrayList();
/*  69 */       File allowed = new File(repos, "allowedNodes");
/*  70 */       TObjectIntHashMap count = new TObjectIntHashMap();
/*  71 */       if ((allowed.exists()) && (allowed.length() > 0L)) {
/*  72 */         for (Iterator it1 = SheetIO.read(allowed, "\t"); it1.hasNext();) {
/*  73 */           List row = (List)it1.next();
/*  74 */           count.put(row.get(0), Integer.parseInt((String)row.get(1)));
/*     */         }
/*     */       }
/*     */       else
/*     */       {
/*  79 */         Node n = taxa.getExternalNode(0);
/*     */         
/*  81 */         System.err.println("count sequences ");
/*  82 */         while (it.hasNext()) {
/*  83 */           Sequence seq = it.nextSequence();
/*  84 */           String species = (String)seq.getAnnotation().getProperty("species");
/*  85 */           count.put(species, count.get(species) + 1);
/*     */         }
/*     */         
/*  88 */         PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(allowed)));
/*  89 */         count.forEachEntry(new TObjectIntProcedure() {
/*     */           public boolean execute(Object obj, int key) {
/*  91 */             if (key < 1000) return true;
/*  92 */             SpeciesAlphabet.this.print(obj.toString());
/*  93 */             SpeciesAlphabet.this.print("\t");
/*  94 */             SpeciesAlphabet.this.print(key + "\n");
/*  95 */             return true;
/*     */           }
/*  97 */         });
/*  98 */         pw.close();
/*     */       }
/*     */       
/* 101 */       System.err.println("generating map");
/* 102 */       Node n = taxa.getExternalNode(0);
/*     */       for (;;) {
/* 104 */         int cnt = count.get(n.getIdentifier().getName());
/* 105 */         if ((n.isRoot()) || (cnt >= threshold)) {
/* 106 */           toKeep.add(n);
/* 107 */           mapToNode(m, n, n.getIdentifier().getName());
/*     */         }
/*     */         else {
/* 110 */           String parentName = n.getParent().getIdentifier().getName();
/* 111 */           count.put(parentName, count.get(parentName) + cnt);
/*     */         }
/* 113 */         if (n.isRoot())
/*     */           break;
/* 115 */         n = NodeUtils.postorderSuccessor(n);
/*     */       }
/*     */       
/*     */ 
/* 119 */       if (threshold > 0) {
/* 120 */         Node[] nodes = (Node[])toKeep.toArray(new Node[0]);
/* 121 */         taxa = new SimpleTree(SDI.trim(taxa.getRoot(), nodes));
/*     */       }
/* 123 */       System.err.println("node counts " + taxa.getInternalNodeCount() + taxa.getExternalNodeCount());
/* 124 */       for (Iterator it1 = SheetIO.read(new File(repos, "merged.dmp"), "\t"); it1.hasNext();) {
/* 125 */         List row = (List)it1.next();
/* 126 */         m.put(((String)row.get(0)).trim(), m.get(((String)row.get(1)).trim()));
/*     */       }
/* 128 */       if (threshold > 0) taxa.createNodeList();
/* 129 */       System.err.println("node count " + taxa.getExternalNodeCount() + taxa.getInternalNodeCount());
/* 130 */       alphabet = new SpeciesAlphabet(taxa, m);
/*     */     }
/* 132 */     return alphabet;
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
/* 143 */   static int threshold = 10000;
/*     */   
/*     */ 
/*     */   public void postOrderList(Symbol sym, List l)
/*     */   {
/* 148 */     SpeciesSymbol ss = (SpeciesSymbol)sym;
/* 149 */     for (int i = 0; i < ss.getChildCount(); i++) {
/* 150 */       postOrderList(ss.getChild(i), l);
/*     */     }
/* 152 */     l.add(sym);
/*     */   }
/*     */   
/*     */   public Set symbols()
/*     */   {
/* 157 */     Set l = new HashSet();
/* 158 */     Iterator i = iterator();
/* 159 */     while (i.hasNext()) {
/* 160 */       l.add(i.next());
/*     */     }
/* 162 */     return l;
/*     */   }
/*     */   
/* 165 */   Map nameToSymbol = new HashMap();
/*     */   
/*     */   Symbol root;
/*     */   
/*     */ 
/* 170 */   public Symbol root() { return this.root; }
/*     */   
/*     */   public class SpeciesSymbol extends FundamentalAtomicSymbol { SpeciesSymbol parent;
/*     */     private SpeciesSymbol[] children;
/*     */     int i;
/*     */     boolean isRoot;
/*     */     
/* 177 */     SpeciesSymbol(String id, SpeciesSymbol[] children, boolean isRoot) { super(Annotation.EMPTY_ANNOTATION);
/* 178 */       this.i = Integer.parseInt(id);
/* 179 */       this.isRoot = isRoot;
/* 180 */       this.children = children;
/*     */     }
/*     */     
/*     */     public String toString() {
/* 184 */       return this.i;
/*     */     }
/*     */     
/* 187 */     public Symbol getChild(int j) { return this.children[j]; }
/*     */     
/*     */     public int getChildCount() {
/* 190 */       return this.children.length;
/*     */     }
/*     */     
/*     */     public boolean isRoot() {
/* 194 */       return this.isRoot;
/*     */     }
/*     */     
/* 197 */     public Symbol getParent() { return this.parent; }
/*     */     
/*     */     public int hashCode() {
/* 200 */       return this.i;
/*     */     }
/*     */     
/* 203 */     public boolean equals(Object o) { return o.hashCode() == this.i; }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private SpeciesSymbol addSymbol(Node node)
/*     */     throws Exception
/*     */   {
/* 211 */     SpeciesSymbol[] children = new SpeciesSymbol[node.getChildCount()];
/* 212 */     for (int i = 0; i < node.getChildCount(); i++) {
/* 213 */       children[i] = addSymbol(node.getChild(i));
/*     */     }
/* 215 */     String id = node.getIdentifier().getName();
/* 216 */     SpeciesSymbol symb = new SpeciesSymbol(id, children, node.isRoot());
/* 217 */     for (int i = 0; i < node.getChildCount(); i++) {
/* 218 */       children[i].parent = symb;
/*     */     }
/* 220 */     addSymbol(symb);
/*     */     
/* 222 */     return symb;
/*     */   }
/*     */   
/*     */   static String nameFromRow(String[] row) {
/* 226 */     StringBuffer buffer = new StringBuffer(row[1].length() + 
/* 227 */       row[2].length() + row[4].length() + 5);
/* 228 */     buffer.append(row[1]);
/* 229 */     buffer.append(" ");
/* 230 */     buffer.append(row[2]);
/* 231 */     buffer.append(" (");
/* 232 */     buffer.append(row[4]);
/* 233 */     buffer.append(").");
/* 234 */     return buffer.toString();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private SpeciesAlphabet(Tree taxonomy, Map nameToSymbol)
/*     */     throws Exception
/*     */   {
/* 245 */     super("Species Alphabet");
/* 246 */     this.tree = taxonomy;
/* 247 */     this.root = addSymbol(this.tree.getRoot());
/* 248 */     this.nameToSymbol = nameToSymbol;
/* 249 */     NameTokenization restricted = new NameTokenization(this)
/*     */     {
/*     */       public Symbol parseToken(String token) throws IllegalSymbolException {
/* 252 */         String node = (String)this.val$nameToSymbol.get(token);
/* 253 */         if (node == null) {
/* 254 */           System.err.println("warning no symbol for " + token);
/* 255 */           return SpeciesAlphabet.this.root;
/*     */         }
/* 257 */         Symbol sym = super.parseToken(node);
/* 258 */         if (sym == null) throw new NullPointerException("is null " + token + " " + node);
/* 259 */         return sym;
/*     */       }
/* 261 */     };
/* 262 */     putTokenization("token", restricted);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public SymbolList taxaToList(Symbol sym)
/*     */   {
/*     */     try
/*     */     {
/* 275 */       List l = new ArrayList();
/* 276 */       l.add(sym);
/* 277 */       while (!((SpeciesSymbol)sym).isRoot()) {
/* 278 */         sym = ((SpeciesSymbol)sym).getParent();
/* 279 */         l.add(sym);
/*     */       }
/* 281 */       SymbolList res = new SimpleSymbolList(this, l);
/* 282 */       if (res == null) throw new Exception("is null " + sym.getName());
/* 283 */       return res;
/*     */     }
/*     */     catch (Exception exc) {
/* 286 */       exc.printStackTrace();
/* 287 */       System.exit(0); }
/* 288 */     return null;
/*     */   }
/*     */ }


/* Location:              /home/lachlan/Desktop/psilc1.21/psilc.jar!/lc1/pfam/SpeciesAlphabet.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */