/*     */ package lc1.treefam;
/*     */ 
/*     */ import java.io.PrintStream;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Comparator;
/*     */ import java.util.HashSet;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Set;
/*     */ import java.util.TreeSet;
/*     */ import pal.misc.IdGroup;
/*     */ import pal.misc.Identifier;
/*     */ import pal.tree.AttributeNode;
/*     */ import pal.tree.Node;
/*     */ import pal.tree.NodeUtils;
/*     */ import pal.tree.SimpleNode;
/*     */ import pal.tree.SimpleTree;
/*     */ import pal.tree.Tree;
/*     */ import pal.tree.TreeParseException;
/*     */ import pal.tree.TreeUtils;
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
/*     */ public class SDI
/*     */ {
/*     */   Tree species;
/*     */   
/*     */   SDI(Tree speciesTree, IdGroup familyIds)
/*     */     throws TreeParseException
/*     */   {
/*  41 */     this.species = speciesTree;
/*  42 */     NodeUtils.lengths2Heights(this.species.getRoot());
/*     */   }
/*     */   
/*  45 */   public static void fakeLength(Node root) { for (int i = 0; i < root.getChildCount(); i++) {
/*  46 */       root.getChild(i).setBranchLength(Math.random());
/*  47 */       fakeLength(root.getChild(i));
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public static Tree trimSpeciesTree(IdGroup ids, Node species_root)
/*     */     throws TreeParseException
/*     */   {
/*  55 */     Node[] nodes = new Node[ids.getIdCount()];
/*  56 */     for (int i = 0; i < ids.getIdCount(); i++) {
/*  57 */       AttributeIdentifier id = (AttributeIdentifier)ids.getIdentifier(i);
/*  58 */       nodes[i] = TreeUtils.getNodeByName(species_root, id.getAttribute("S"));
/*  59 */       if (nodes[i] == null) throw new TreeParseException("no node for name " + id + " " + id.getAttribute("S"));
/*     */     }
/*  61 */     Node lca = 
/*  62 */       trim(NodeUtils.getFirstCommonAncestor(nodes), nodes);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  71 */     return 
/*     */     
/*  73 */       new SimpleTree(lca);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static Node trim(Node node, Node[] nodes)
/*     */   {
/*  83 */     boolean hasAncestor = false;
/*  84 */     for (int i = 0; i < nodes.length; i++) {
/*  85 */       if (nodes[i] == null) throw new NullPointerException("");
/*  86 */       if (NodeUtils.isAncestor(node, nodes[i])) {
/*  87 */         hasAncestor = true;
/*  88 */         break;
/*     */       }
/*     */     }
/*  91 */     if (!hasAncestor) return null;
/*  92 */     List newChildren = new ArrayList();
/*     */     
/*  94 */     for (int i = 0; i < node.getChildCount(); i++) {
/*  95 */       Node child_i = trim(node.getChild(i), nodes);
/*  96 */       if (child_i != null) newChildren.add(child_i);
/*     */     }
/*  98 */     if ((newChildren.size() == 1) && 
/*  99 */       (!Arrays.asList(nodes).contains(node)))
/* 100 */       return (Node)newChildren.get(0);
/* 101 */     Node n = new SimpleNode((Node[])newChildren.toArray(new Node[0]));
/* 102 */     n.setBranchLength(node.getBranchLength());
/* 103 */     n.setIdentifier(node.getIdentifier());
/*     */     
/* 105 */     return n;
/*     */   }
/*     */   
/* 108 */   static final Comparator node_comp = new Comparator() {
/*     */     public int compare(Object o1, Object o2) {
/* 110 */       return ((Node)o1).getIdentifier().getName().compareTo(
/* 111 */         ((Node)o2).getIdentifier().getName());
/*     */     }
/*     */   };
/*     */   
/*     */   static class NodeSet extends TreeSet { Node lca;
/*     */     
/* 117 */     NodeSet() { super(); }
/*     */     
/*     */     NodeSet(Node node)
/*     */     {
/* 121 */       this();
/* 122 */       add(node);
/* 123 */       this.lca = node;
/*     */     }
/*     */     
/* 126 */     NodeSet(Node[] nodes) { this();
/* 127 */       addAll(Arrays.asList(nodes));
/* 128 */       this.lca = NodeUtils.getFirstCommonAncestor(nodes);
/*     */     }
/*     */     
/*     */     public Node lca() {
/* 132 */       if (this.lca == null) {
/* 133 */         if (size() == 0) throw new NullPointerException("size is zero");
/* 134 */         this.lca = NodeUtils.getFirstCommonAncestor((Node[])toArray(new Node[size()]));
/*     */       }
/* 136 */       return this.lca;
/*     */     }
/*     */     
/*     */     public boolean equals(Object o) {
/* 140 */       NodeSet s1 = (NodeSet)o;
/* 141 */       if (s1.size() != size()) return false;
/* 142 */       Iterator it1 = iterator();
/* 143 */       Iterator it2 = s1.iterator();
/* 144 */       while (it1.hasNext()) {
/* 145 */         if (it1.next() != it2.next()) return false;
/*     */       }
/* 147 */       return true;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   Object[] inferDuplications(Tree geneTree, String treeName)
/*     */   {
/* 157 */     Set duplications = new HashSet();
/* 158 */     AttributeNode node = (AttributeNode)geneTree.getExternalNode(0);
/* 159 */     Node root = geneTree.getRoot();
/* 160 */     while (node != null)
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/* 165 */       if (node.isLeaf()) {
/* 166 */         String taxonomy = ((AttributeIdentifier)node.getIdentifier()).getAttribute("S");
/* 167 */         AttributeNode specNode = (AttributeNode)TreeUtils.getNodeByName(this.species.getRoot(), taxonomy);
/* 168 */         if (specNode == null) { System.err.println("couldn't find node " + taxonomy);
/*     */         }
/* 170 */         node.setAttribute("species", new NodeSet(specNode));
/* 171 */         specNode.setAttribute(treeName, node);
/* 172 */         ((AttributeIdentifier)node.getIdentifier()).setAttribute("E", ((AttributeIdentifier)specNode.getIdentifier()).getAttribute("S"));
/*     */       }
/*     */       else {
/* 175 */         NodeSet[] child_species_nodes = new NodeSet[node.getChildCount()];
/* 176 */         Set child_species = new HashSet();
/*     */         
/* 178 */         for (int i = 0; i < node.getChildCount(); i++) {
/* 179 */           NodeSet child_nodes = (NodeSet)((AttributeNode)node.getChild(i)).getAttribute("species");
/* 180 */           if (child_nodes.size() == 0) throw new RuntimeException("size should not be zero");
/* 181 */           child_species_nodes[i] = child_nodes;
/*     */           
/* 183 */           child_species.addAll(child_nodes);
/*     */         }
/*     */         
/* 186 */         Node lca = NodeUtils.getFirstCommonAncestor((Node[])child_species.toArray(new Node[0]));
/* 187 */         ((AttributeIdentifier)node.getIdentifier()).setAttribute("S", lca.getIdentifier().getName());
/* 188 */         String spec = ((AttributeIdentifier)lca.getIdentifier()).getAttribute("S");
/* 189 */         if (spec == null) throw new NullPointerException("species tree not annotated at" + lca.getIdentifier().getName());
/* 190 */         ((AttributeIdentifier)node.getIdentifier()).setAttribute("E", spec);
/*     */         
/* 192 */         double height = lca.getNodeHeight();
/* 193 */         NodeSet childrenUsed = lca.isLeaf() ? new NodeSet(lca) : new NodeSet();
/* 194 */         for (int j = 0; j < lca.getChildCount(); j++) {
/* 195 */           Node child_j = lca.getChild(j);
/* 196 */           for (Iterator it = child_species.iterator(); it.hasNext();) {
/* 197 */             Node child_i = (Node)it.next();
/* 198 */             boolean ancestor = NodeUtils.isAncestor(child_j, child_i);
/* 199 */             if (ancestor) {
/* 200 */               childrenUsed.add(child_j);
/*     */               
/* 202 */               break;
/*     */             }
/*     */           }
/*     */         }
/*     */         
/*     */ 
/* 208 */         NodeSet result = childrenUsed;
/* 209 */         if (result.size() == 0) { throw new RuntimeException("no children used !" + node);
/*     */         }
/* 211 */         node.setAttribute("species", result);
/* 212 */         result.lca();
/*     */         
/* 214 */         boolean duplication = false;
/* 215 */         boolean speciation = true;
/* 216 */         for (int i = 0; i < child_species_nodes.length; i++) {
/*     */           try
/*     */           {
/* 219 */             AttributeNode child_lca = (AttributeNode)child_species_nodes[i].lca();
/* 220 */             if (lca == child_lca) {
/* 221 */               speciation = false;
/* 222 */               duplications.add(node);
/* 223 */               if ((!duplication) && (result.equals(child_species_nodes[i]))) {
/* 224 */                 duplication = true;
/*     */                 
/* 226 */                 if (node.isRoot()) {
/* 227 */                   System.err.println("warning - duplication at root");
/*     */                 }
/*     */               }
/*     */             }
/*     */           } catch (Exception exc) {
/* 232 */             System.err.println("problem with " + child_species_nodes[i]);
/* 233 */             exc.printStackTrace();
/* 234 */             System.exit(0);
/*     */           }
/*     */         }
/*     */         
/*     */ 
/* 239 */         if ((duplication) || (speciation)) {
/* 240 */           node.setAttribute("duplication", duplication ? Boolean.TRUE : Boolean.FALSE);
/* 241 */           ((AttributeIdentifier)node.getIdentifier()).setAttribute("D", duplication ? "Y" : "N");
/*     */         }
/* 243 */         if (speciation)
/*     */         {
/* 245 */           for (int i = 0; i < node.getChildCount(); i++) {
/* 246 */             AttributeNode child_lca = (AttributeNode)child_species_nodes[i].lca();
/* 247 */             child_lca.setAttribute(treeName, node.getChild(i));
/*     */           }
/*     */         }
/*     */       }
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
/* 275 */       if (node == root) break;
/* 276 */       node = (AttributeNode)NodeUtils.postorderSuccessor(node);
/*     */     }
/* 278 */     geneTree = new SimpleTree(geneTree.getRoot());
/*     */     
/* 280 */     return new Object[] { geneTree, duplications };
/*     */   }
/*     */ }


/* Location:              /home/lachlan/Desktop/psilc1.21/psilc.jar!/lc1/treefam/SDI.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */