/*     */ package lc1.dp;
/*     */ 
/*     */ import java.util.Comparator;
/*     */ import java.util.Iterator;
/*     */ import java.util.SortedSet;
/*     */ import java.util.TreeSet;
/*     */ import lc1.phyl.AlignUtils;
/*     */ import org.biojava.bio.symbol.IllegalSymbolException;
/*     */ import pal.alignment.SitePattern;
/*     */ import pal.tree.Node;
/*     */ import pal.tree.Tree;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ConversionMap
/*     */ {
/*     */   Tree tree;
/*     */   int[][] cMapP;
/*     */   
/*     */   public ConversionMap(SitePattern sp1, Tree tree)
/*     */     throws IllegalSymbolException
/*     */   {
/*  24 */     SitePattern[] sp = AlignUtils.getSitePatterns(sp1);
/*  25 */     this.tree = tree;
/*  26 */     this.cMapP = new int[sp.length + 1][4];
/*  27 */     int[] trans = countTransitions(sp[0], null);
/*  28 */     this.cMapP[0][0] = trans[0];
/*  29 */     this.cMapP[0][2] = trans[1];
/*  30 */     for (int i = 1; i < sp.length; i++) {
/*  31 */       int[] transP = countTransitions(sp[(i - 1)], sp[i]);
/*  32 */       System.arraycopy(transP, 0, this.cMapP[i], 0, transP.length);
/*     */     }
/*  34 */     trans = countTransitions(sp[(sp.length - 1)], null);
/*  35 */     this.cMapP[sp.length][0] = trans[0];
/*  36 */     this.cMapP[sp.length][1] = trans[1];
/*     */   }
/*     */   
/*     */   public int[] get(int symTo, AlignmentHMM.NodeSum[] nodeSums) {
/*  40 */     int[] res = this.cMapP[(symTo - 1)];
/*  41 */     for (int i = 0; i < res.length; i++) {
/*  42 */       nodeSums[i].number = res[i];
/*     */     }
/*  44 */     return res;
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
/*     */   private SortedSet[] getMutatedNodes(SitePattern s1, SitePattern s2)
/*     */   {
/*  62 */     SortedSet[] nodes = new SortedSet[4];
/*  63 */     for (int i = 0; i < nodes.length; i++) {
/*  64 */       nodes[i] = new TreeSet(NODE_COMPARATOR);
/*     */     }
/*  66 */     for (int i = 0; i < s1.pattern.length; i++) {
/*  67 */       if (s1.pattern[i][0] == 20) {
/*  68 */         if (s2.pattern[i][0] == 20) {
/*  69 */           nodes[3].add(this.tree.getExternalNode(i));
/*     */         }
/*     */         else {
/*  72 */           nodes[2].add(this.tree.getExternalNode(i));
/*     */         }
/*     */         
/*     */       }
/*  76 */       else if (s2.pattern[i][0] == 20) {
/*  77 */         nodes[1].add(this.tree.getExternalNode(i));
/*     */       }
/*     */       else {
/*  80 */         nodes[0].add(this.tree.getExternalNode(i));
/*     */       }
/*     */     }
/*     */     
/*  84 */     return nodes;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private SortedSet[] getMutatedNodes(SitePattern s1)
/*     */   {
/*  96 */     SortedSet[] nodes = new SortedSet[2];
/*  97 */     for (int i = 0; i < nodes.length; i++) {
/*  98 */       nodes[i] = new TreeSet(NODE_COMPARATOR);
/*     */     }
/* 100 */     for (int i = 0; i < s1.getSequenceCount(); i++)
/*     */     {
/* 102 */       if (s1.pattern[i][0] == 20) {
/* 103 */         nodes[1].add(this.tree.getExternalNode(i));
/*     */       }
/*     */       else {
/* 106 */         nodes[0].add(this.tree.getExternalNode(i));
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 111 */     return nodes;
/*     */   }
/*     */   
/*     */ 
/*     */   private int[] countTransitions(SitePattern s1, SitePattern s2)
/*     */     throws IllegalSymbolException
/*     */   {
/* 118 */     if ((s1 == null) && (s2 == null)) { throw new IllegalSymbolException("both cannot be null");
/*     */     }
/*     */     
/*     */     SortedSet[] gapNodes;
/*     */     
/*     */     SortedSet[] gapNodes;
/* 124 */     if (s2 == null) {
/* 125 */       gapNodes = getMutatedNodes(s1);
/*     */     } else {
/*     */       SortedSet[] gapNodes;
/* 128 */       if (s1 == null) {
/* 129 */         gapNodes = getMutatedNodes(s2);
/*     */       }
/*     */       else
/*     */       {
/* 133 */         gapNodes = getMutatedNodes(s1, s2);
/*     */       }
/*     */     }
/*     */     
/* 137 */     int[] res = new int[gapNodes.length];
/*     */     
/* 139 */     for (int i = 0; i < gapNodes.length; i++)
/*     */     {
/* 141 */       res[i] = minimumExplanationNodes(gapNodes[i]).size();
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
/* 152 */     return res;
/*     */   }
/*     */   
/*     */   private String print(SortedSet nodes) {
/* 156 */     String st = "";
/* 157 */     for (Iterator it = nodes.iterator(); it.hasNext();) {
/* 158 */       Node n = (Node)it.next();
/* 159 */       st = st + n.getNumber() + "_" + n.isLeaf() + " ";
/*     */     }
/* 161 */     return st;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static SortedSet minimumExplanationNodes(SortedSet nodes)
/*     */   {
/* 171 */     SortedSet subset = nodes.tailSet(null);
/*     */     
/* 173 */     while (subset.size() > 1) {
/* 174 */       Node n = (Node)subset.first();
/* 175 */       Node p = n.getParent();
/* 176 */       Node b1 = p.getChild(0);
/* 177 */       Node b2 = p.getChild(1);
/* 178 */       Node b = b1 != n ? b1 : b2;
/*     */       
/*     */ 
/* 181 */       boolean removed = false;
/* 182 */       if (nodes.contains(b))
/*     */       {
/* 184 */         nodes.add(p);
/* 185 */         nodes.remove(n);
/* 186 */         nodes.remove(b);
/* 187 */         removed = true;
/*     */       }
/* 189 */       Iterator it = nodes.tailSet(n).iterator();
/*     */       
/* 191 */       if (!removed) it.next();
/* 192 */       if (!it.hasNext()) break;
/* 193 */       subset = nodes.tailSet(it.next()); continue;
/*     */       
/*     */ 
/* 196 */       break;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 203 */     return nodes;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/* 208 */   public static final Comparator NODE_COMPARATOR = new Comparator()
/*     */   {
/*     */     public int compare(Object o1, Object o2) {
/* 211 */       if (o1 == null) {
/* 212 */         if (o2 == null) return 0;
/* 213 */         return -1;
/*     */       }
/* 215 */       if (o2 == null) {
/* 216 */         return 1;
/*     */       }
/* 218 */       if (((o1 instanceof Node)) && ((o2 instanceof Node))) {
/* 219 */         Node n1 = (Node)o1;
/* 220 */         Node n2 = (Node)o2;
/* 221 */         if ((n1.isLeaf()) && (!n2.isLeaf())) {
/* 222 */           return -1;
/*     */         }
/* 224 */         if ((!n1.isLeaf()) && (n2.isLeaf())) {
/* 225 */           return 1;
/*     */         }
/*     */         
/* 228 */         int num1 = n1.getNumber();
/* 229 */         int num2 = n2.getNumber();
/* 230 */         return num1 > num2 ? 1 : num1 < num2 ? -1 : 0;
/*     */       }
/*     */       
/* 233 */       return 0;
/*     */     }
/*     */     
/*     */     public boolean equals(Object o) {
/* 237 */       if (o == this) return true;
/* 238 */       return false;
/*     */     }
/*     */   };
/*     */ }


/* Location:              /home/lachlan/Desktop/psilc1.21/psilc.jar!/lc1/dp/ConversionMap.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */