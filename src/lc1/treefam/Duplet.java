/*     */ package lc1.treefam;
/*     */ 
/*     */ import java.io.Serializable;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.Comparator;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Stack;
/*     */ import pal.misc.Identifier;
/*     */ import pal.tree.Node;
/*     */ 
/*     */ 
/*     */ class Duplet
/*     */   implements Serializable, Node
/*     */ {
/*  19 */   int min_id = Integer.MAX_VALUE;
/*  20 */   int num_children = 0;
/*     */   private List children;
/*  22 */   static final int[] primes = { 3, 5, 7, 11, 13, 17, 23, 31 };
/*     */   Node parent;
/*     */   int index;
/*  25 */   double branch_length = 0.0D;
/*  26 */   boolean isroot = false;
/*     */   
/*     */   Duplet(int min_id)
/*     */   {
/*  30 */     this.min_id = min_id;
/*  31 */     this.num_children = 1;
/*  32 */     this.children = Arrays.asList(new Duplet[0]);
/*  33 */     this.ident = new Identifier(min_id);
/*     */   }
/*     */   
/*     */   Duplet(List duplets)
/*     */   {
/*  38 */     this.children = duplets;
/*  39 */     Collections.sort(duplets, NODE_COMP);
/*  40 */     for (int i = 1; i < this.children.size(); i++) {
/*     */       try {
/*  42 */         if (((Duplet)this.children.get(i - 1)).min_id == ((Duplet)this.children.get(i)).min_id) throw new Exception("children should be non-equal " + toString());
/*     */       }
/*     */       catch (Exception exc) {
/*  45 */         exc.printStackTrace();
/*  46 */         System.exit(0);
/*     */       }
/*     */     }
/*  49 */     for (Iterator it = this.children.iterator(); it.hasNext();) {
/*  50 */       Duplet d = (Duplet)it.next();
/*  51 */       this.num_children += d.num_children;
/*  52 */       if (d.min_id < this.min_id) { this.min_id = d.min_id;
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   Duplet getIntersection(Collection ids)
/*     */   {
/*  64 */     if (isLeaf()) {
/*  65 */       if (ids.contains(this)) {
/*  66 */         return this;
/*     */       }
/*  68 */       return null;
/*     */     }
/*  70 */     List s = new ArrayList();
/*  71 */     for (int i = 0; i < getChildCount(); i++) {
/*  72 */       Duplet next = ((Duplet)getChild(i)).getIntersection(ids);
/*  73 */       if (next != null) s.add(next); }
/*     */     Duplet res;
/*     */     Duplet res;
/*  76 */     if (s.size() == 1) { res = (Duplet)s.get(0); } else { Duplet res;
/*  77 */       if (s.size() == 0) res = null; else
/*  78 */         res = new Duplet(s); }
/*  79 */     return res;
/*     */   }
/*     */   
/*     */   Stack getParents(Duplet node)
/*     */   {
/*  84 */     return getParents(this, node);
/*     */   }
/*     */   
/*     */   static Stack getParents(Duplet root, Duplet node) {
/*  88 */     if (root.equals(node)) {
/*  89 */       return new Stack();
/*     */     }
/*  91 */     if (root.num_children <= node.num_children) { return null;
/*     */     }
/*  93 */     for (int i = 0; i < root.getChildCount(); i++) {
/*  94 */       Stack st = getParents((Duplet)root.getChild(i), node);
/*  95 */       if (st != null) {
/*  96 */         st.push(root);
/*  97 */         return st;
/*     */       }
/*     */     }
/* 100 */     return null;
/*     */   }
/*     */   
/*     */   Duplet rerootAbove(Duplet rootedAbove) throws NullPointerException
/*     */   {
/* 105 */     if (rootedAbove == null) throw new NullPointerException("rootedAbove shouldn't be null ");
/* 106 */     Stack parents = getParents(rootedAbove);
/* 107 */     if (parents == null) { return null;
/*     */     }
/*     */     
/* 110 */     Duplet newChild = null;
/* 111 */     while (parents.size() > 0) {
/* 112 */       Duplet root1 = (Duplet)parents.pop();
/* 113 */       List children = new ArrayList();
/* 114 */       for (int i = 0; i < root1.getChildCount(); i++) {
/* 115 */         if ((parents.size() == 0) || (!root1.getChild(i).equals(parents.lastElement()))) {
/* 116 */           children.add(root1.getChild(i));
/*     */         }
/*     */       }
/* 119 */       if (newChild != null) children.add(newChild);
/* 120 */       newChild = new Duplet(children);
/*     */     }
/* 122 */     return newChild;
/*     */   }
/*     */   
/*     */   public void sort() throws Exception {
/* 126 */     for (int i = 0; i < this.children.size(); i++) {
/* 127 */       ((Duplet)this.children.get(i)).sort();
/*     */     }
/* 129 */     Collections.sort(this.children, NODE_COMP);
/* 130 */     for (int i = 1; i < this.children.size(); i++) {
/* 131 */       if (((Duplet)this.children.get(i - 1)).min_id == ((Duplet)this.children.get(i)).min_id) throw new Exception("children should be non-equal " + toString());
/*     */     }
/*     */   }
/*     */   
/*     */   public int binary() {
/* 136 */     if (isLeaf()) { return 0;
/*     */     }
/* 138 */     int num = getChildCount() - 2;
/* 139 */     for (int i = 0; i < getChildCount(); i++) {
/* 140 */       num += ((Duplet)getChild(i)).binary();
/*     */     }
/* 142 */     return num;
/*     */   }
/*     */   
/*     */ 
/*     */   public void removeChild(Duplet child)
/*     */   {
/* 148 */     this.children.remove(child);
/*     */   }
/*     */   
/*     */   public Duplet get(Duplet d)
/*     */   {
/* 153 */     if (equals(d)) { return this;
/*     */     }
/* 155 */     for (Iterator it = this.children.iterator(); it.hasNext();) {
/* 156 */       Duplet next = (Duplet)it.next();
/* 157 */       Duplet res = next.get(d);
/* 158 */       if (res != null) return res;
/*     */     }
/* 160 */     return null;
/*     */   }
/*     */   
/*     */ 
/* 164 */   static boolean print = false;
/*     */   
/*     */   public int calculateNumTrees()
/*     */   {
/* 168 */     int num = this.children.size() > 2 ? calculateNumRootedTrees(this.children.size()) : 1;
/* 169 */     for (int i = 0; i < this.children.size(); i++) {
/* 170 */       num *= ((Duplet)this.children.get(i)).calculateNumTrees();
/*     */     }
/* 172 */     return num;
/*     */   }
/*     */   
/*     */   private static int calculateNumRootedTrees(int no_leaves) {
/* 176 */     int ceil = 2 * no_leaves - 3;
/* 177 */     int res = 1;
/* 178 */     int j = 3;
/* 179 */     while (j <= ceil) {
/* 180 */       res *= j;
/* 181 */       j += 2;
/*     */     }
/* 183 */     return res;
/*     */   }
/*     */   
/*     */   Duplet coalesce(Duplet left, Duplet right) throws Exception {
/* 187 */     if ((this.children.contains(left)) && (this.children.contains(right))) {
/* 188 */       List duplets_new = new ArrayList(this.children);
/* 189 */       List coal = Arrays.asList(new Duplet[] { left, right });
/* 190 */       duplets_new.remove(left);
/* 191 */       duplets_new.remove(right);
/* 192 */       duplets_new.add(new Duplet(coal));
/* 193 */       return new Duplet(duplets_new);
/*     */     }
/*     */     
/* 196 */     throw new Exception("children not contain left and/or right " + this.children + " " + left + " " + right);
/*     */   }
/*     */   
/*     */   public int hashCode()
/*     */   {
/* 201 */     if (this.children.size() == 0) { return this.min_id;
/*     */     }
/* 203 */     int res = 0;
/* 204 */     int k = 0;
/* 205 */     for (Iterator it = this.children.iterator(); it.hasNext(); k++) {
/* 206 */       if (k == primes.length) k = 0;
/* 207 */       res += primes[k] * this.children.hashCode();
/*     */     }
/* 209 */     return res;
/*     */   }
/*     */   
/*     */   public boolean equals(Object o)
/*     */   {
/* 214 */     return NODE_COMP.compare(this, o) == 0;
/*     */   }
/*     */   
/* 217 */   static final Comparator NODE_COMP_PAIR = new NodeComparator() {
/*     */     public int compare(Object o1, Object o2) {
/* 219 */       Duplet[] s1 = (Duplet[])o1;
/* 220 */       Duplet[] s2 = (Duplet[])o2;
/* 221 */       if (s1.length != s2.length) return s1.length < s2.length ? -1 : 1;
/* 222 */       for (int i = 0; i < s1.length; i++) {
/* 223 */         int comp = Duplet.NODE_COMP.compare(s1[i], s2[i]);
/* 224 */         if (comp != 0) return comp;
/*     */       }
/* 226 */       return 0;
/*     */     }
/*     */   };
/*     */   
/* 230 */   static final Comparator NODE_COMP = new NodeComparator();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   static class NodeComparator
/*     */     implements Comparator, Serializable
/*     */   {
/*     */     public int compare(Object o1, Object o2)
/*     */     {
/* 240 */       Duplet s1 = (Duplet)o1;
/* 241 */       Duplet s2 = (Duplet)o2;
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 246 */       if (s1.min_id != s2.min_id) {
/* 247 */         return s1.min_id < s2.min_id ? -1 : 1;
/*     */       }
/* 249 */       if (s1.children.size() != s2.children.size())
/*     */       {
/* 251 */         return s1.children.size() < s2.children.size() ? -1 : 1;
/*     */       }
/* 253 */       if (s1.num_children != s2.num_children)
/* 254 */         return s1.num_children < s2.num_children ? -1 : 1;
/* 255 */       if (s1.children.size() != 0) {
/* 256 */         int comp = 0;
/* 257 */         Iterator it1 = s1.children.iterator();
/* 258 */         Iterator it2 = s2.children.iterator();
/* 259 */         while ((comp == 0) && (it1.hasNext())) {
/* 260 */           comp = compare(it1.next(), it2.next());
/*     */         }
/* 262 */         return comp;
/*     */       }
/* 264 */       return 0;
/*     */     }
/*     */     
/*     */     public int compare1(Object o1, Object o2) {
/* 268 */       Duplet s1 = (Duplet)o1;
/* 269 */       Duplet s2 = (Duplet)o2;
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 274 */       if (s1.children.size() != s2.children.size())
/*     */       {
/* 276 */         return s1.children.size() < s2.children.size() ? -1 : 1;
/*     */       }
/* 278 */       if (s1.num_children != s2.num_children)
/* 279 */         return s1.num_children < s2.num_children ? -1 : 1;
/* 280 */       if (s1.children.size() != 0) {
/* 281 */         int comp = 0;
/* 282 */         Iterator it1 = s1.children.iterator();
/* 283 */         Iterator it2 = s2.children.iterator();
/* 284 */         while ((comp == 0) && (it1.hasNext())) {
/* 285 */           comp = compare(it1.next(), it2.next());
/*     */         }
/* 287 */         return comp;
/*     */       }
/* 289 */       if (s1.min_id != s2.min_id) {
/* 290 */         return s1.min_id < s2.min_id ? -1 : 1;
/*     */       }
/* 292 */       return 0;
/*     */     }
/*     */     
/* 295 */     public boolean equals(Object o) { return o == this; }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public Node getParent()
/*     */   {
/* 303 */     return this.parent;
/*     */   }
/*     */   
/*     */   public void setParent(Node node)
/*     */   {
/* 308 */     this.parent = node;
/*     */   }
/*     */   
/*     */   public byte[] getSequence()
/*     */   {
/* 313 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */   public void setSequence(byte[] array) {}
/*     */   
/*     */   public int getNumber()
/*     */   {
/* 321 */     return this.index;
/*     */   }
/*     */   
/*     */   public void setNumber(int number)
/*     */   {
/* 326 */     this.index = number;
/*     */   }
/*     */   
/*     */   public double getBranchLength()
/*     */   {
/* 331 */     return this.branch_length;
/*     */   }
/*     */   
/*     */   public void setBranchLength(double value)
/*     */   {
/* 336 */     this.branch_length = value;
/*     */   }
/*     */   
/*     */   public double getBranchLengthSE() {
/* 340 */     return 0.0D;
/*     */   }
/*     */   
/*     */   public void setBranchLengthSE(double value) {}
/*     */   
/* 345 */   public double getNodeHeight() { return 0.0D; }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setNodeHeight(double value) {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 358 */   private Identifier ident = null;
/*     */   
/*     */   public void setNodeHeight(double value, boolean adjustChildBranchLengths) {}
/*     */   
/* 362 */   public Identifier getIdentifier() { return this.ident; }
/*     */   
/*     */   public void setIdentifier(Identifier id)
/*     */   {
/* 366 */     this.ident = id;
/*     */   }
/*     */   
/*     */   public int getChildCount()
/*     */   {
/* 371 */     return this.children.size();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean isLeaf()
/*     */   {
/* 382 */     return this.children.size() == 0;
/*     */   }
/*     */   
/*     */   public boolean isRoot() {
/* 386 */     return this.isroot;
/*     */   }
/*     */   
/*     */   public Node getChild(int n)
/*     */   {
/* 391 */     return (Node)this.children.get(n);
/*     */   }
/*     */   
/*     */   public void setChild(int n, Node node)
/*     */   {
/* 396 */     addChild(node);
/*     */   }
/*     */   
/*     */   public void addChild(Node c)
/*     */   {
/* 401 */     this.children.add(c);
/* 402 */     Collections.sort(this.children, NODE_COMP);
/* 403 */     for (int i = 1; i < this.children.size(); i++) {
/*     */       try {
/* 405 */         if (((Duplet)this.children.get(i - 1)).min_id == ((Duplet)this.children.get(i)).min_id) throw new Exception("children should be non-equal " + toString());
/*     */       } catch (Exception exc) {
/* 407 */         exc.printStackTrace();
/* 408 */         System.exit(0);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public void insertChild(Node c, int pos)
/*     */   {
/* 415 */     addChild(c);
/*     */   }
/*     */   
/*     */   public Node removeChild(int n)
/*     */   {
/* 420 */     Node node = getChild(n);
/* 421 */     this.children.remove(node);
/* 422 */     return node;
/*     */   }
/*     */   
/*     */   public String toString() {
/* 426 */     if (isLeaf()) { return this.min_id;
/*     */     }
/* 428 */     StringBuffer sb = new StringBuffer();
/* 429 */     sb.append("(");
/* 430 */     for (Iterator it = this.children.iterator(); it.hasNext();) {
/* 431 */       sb.append(it.next().toString());
/* 432 */       if (it.hasNext()) sb.append(",");
/*     */     }
/* 434 */     sb.append(")");
/* 435 */     return sb.toString();
/*     */   }
/*     */ }


/* Location:              /home/lachlan/Desktop/psilc1.21/psilc.jar!/lc1/treefam/Duplet.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */