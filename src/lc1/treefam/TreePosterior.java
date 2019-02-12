/*     */ package lc1.treefam;
/*     */ 
/*     */ import java.io.PrintWriter;
/*     */ import java.io.Serializable;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.HashSet;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import java.util.TreeMap;
/*     */ import java.util.TreeSet;
/*     */ import pal.tree.Node;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ class TreePosterior
/*     */   implements Serializable
/*     */ {
/*     */   Map treeEvidenceApprox;
/*  24 */   Collection ids = new HashSet();
/*     */   String name;
/*     */   Duplet rootedAbove;
/*     */   
/*     */   void setRootedAbove(Duplet node)
/*     */   {
/*  30 */     this.rootedAbove = node;
/*     */   }
/*     */   
/*  33 */   double log_sc = 0.0D;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   void print(PrintWriter pw)
/*     */   {
/*  40 */     pw.println("rootedAbove " + this.rootedAbove);
/*  41 */     Map m = new TreeMap();
/*  42 */     Set keySet = new HashSet(this.treeEvidenceApprox.keySet());
/*  43 */     for (Iterator it = keySet.iterator(); it.hasNext();) {
/*  44 */       Object key = it.next();
/*  45 */       Double logProb = (Double)this.treeEvidenceApprox.get(key);
/*  46 */       if (m.containsKey(logProb)) {
/*  47 */         m.put(new Double(logProb.doubleValue() + 1.0E-4D * (Math.random() - 0.5D)), key);
/*     */       }
/*     */       else {
/*  50 */         m.put(logProb, key);
/*     */       }
/*     */     }
/*  53 */     for (Iterator it = m.keySet().iterator(); it.hasNext();) {
/*  54 */       Object logProb = it.next();
/*  55 */       Duplet[] key = (Duplet[])m.get(logProb);
/*  56 */       for (int i = 0; i < key.length; i++) {
/*  57 */         pw.print(key[i] + "\t");
/*     */       }
/*  59 */       pw.println(logProb);
/*     */     }
/*  61 */     pw.close();
/*     */   }
/*     */   
/*     */   public Double getTreeEvidence(Duplet[] node)
/*     */   {
/*  66 */     Map tE = this.treeEvidenceApprox;
/*  67 */     if (tE.containsKey(node)) {
/*  68 */       return (Double)tE.get(node);
/*     */     }
/*  70 */     Duplet[] rooted = getRooted(node);
/*  71 */     if (tE.containsKey(rooted))
/*  72 */       return (Double)tE.get(rooted);
/*  73 */     return null;
/*     */   }
/*     */   
/*     */   private Duplet[] getRooted(Duplet[] node)
/*     */   {
/*  78 */     Duplet[] rooted = new Duplet[node.length];
/*  79 */     for (int i = 0; i < rooted.length; i++) {
/*  80 */       rooted[i] = node[i].rerootAbove(this.rootedAbove);
/*  81 */       if (rooted[i] == null) rooted[i] = node[i];
/*     */     }
/*  83 */     return rooted;
/*     */   }
/*     */   
/*     */   public void setLogProb(Duplet[] node, Double log_prob) {
/*  87 */     Duplet[] rooted = getRooted(node);
/*  88 */     this.treeEvidenceApprox.put(node, log_prob);
/*  89 */     this.treeEvidenceApprox.put(rooted, log_prob);
/*     */   }
/*     */   
/*     */   Duplet[] getIntersection(Duplet[] node) {
/*  93 */     return getIntersection(node, this.ids);
/*     */   }
/*     */   
/*     */   Duplet getIntersection(Duplet node)
/*     */   {
/*  98 */     return node.getIntersection(this.ids);
/*     */   }
/*     */   
/*     */   static Duplet[] getIntersection(Duplet[] node, Collection ids) throws NullPointerException {
/* 102 */     Duplet[] res = new Duplet[node.length];
/* 103 */     for (int i = 0; i < node.length; i++) {
/* 104 */       res[i] = node[i].getIntersection(ids);
/*     */     }
/* 106 */     return res;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   static void addLeafIds(Node node, Collection ids)
/*     */   {
/* 117 */     if (node.isLeaf()) { ids.add(node);
/*     */     } else {
/* 119 */       for (int i = 0; i < node.getChildCount(); i++) {
/* 120 */         addLeafIds(node.getChild(i), ids);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   TreePosterior(Duplet dupl, String name) {
/* 126 */     this.name = name;
/* 127 */     addLeafIds(dupl, this.ids);
/* 128 */     this.treeEvidenceApprox = 
/* 129 */       new TreeMap(Duplet.NODE_COMP_PAIR);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   void updateForReducedSet(Duplet duplets)
/*     */   {
/* 137 */     Collection idSet = new TreeSet(Duplet.NODE_COMP);
/* 138 */     addLeafIds(duplets, idSet);
/* 139 */     List keysApprox = new ArrayList();
/* 140 */     List valuesApprox = new ArrayList();
/* 141 */     for (Iterator it = this.treeEvidenceApprox.keySet().iterator(); it.hasNext();) {
/* 142 */       Duplet[] dupl = (Duplet[])it.next();
/* 143 */       Duplet[] dupl_new = new Duplet[dupl.length];
/* 144 */       dupl_new = getIntersection(dupl, idSet);
/* 145 */       if ((dupl_new[0] != null) && ((dupl_new.length == 1) || (dupl_new[1] != null))) {
/* 146 */         keysApprox.add(dupl_new);
/* 147 */         valuesApprox.add(this.treeEvidenceApprox.get(dupl));
/*     */       }
/*     */     }
/* 150 */     for (int i = 0; i < keysApprox.size(); i++) {
/* 151 */       this.treeEvidenceApprox.put(keysApprox.get(i), valuesApprox.get(i));
/*     */     }
/*     */   }
/*     */ }


/* Location:              /home/lachlan/Desktop/psilc1.21/psilc.jar!/lc1/treefam/TreePosterior.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */