/*     */ package lc1.treefam;
/*     */ 
/*     */ import java.io.PrintWriter;
/*     */ import java.util.Collection;
/*     */ import java.util.Iterator;
/*     */ import pal.io.FormattedOutput;
/*     */ import pal.misc.Identifier;
/*     */ import pal.tree.Node;
/*     */ import pal.tree.Tree;
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
/*     */ public class NodeUtils
/*     */ {
/*     */   public static void printNH(Tree tree, PrintWriter out)
/*     */   {
/*  33 */     printNH(tree, out, true, true);
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
/*     */   public static void printNH(Tree tree, PrintWriter out, boolean printLengths, boolean printInternalLabels)
/*     */   {
/*  47 */     printNH(tree, out, printLengths, printInternalLabels, false);
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
/*     */   public static void printNH(Tree tree, PrintWriter out, boolean printLengths, boolean printInternalLabels, boolean nhx)
/*     */   {
/*  62 */     printNH(out, tree.getRoot(), 
/*  63 */       printLengths, printInternalLabels, 0, true, nhx);
/*  64 */     Identifier id = tree.getRoot().getIdentifier();
/*  65 */     if ((id instanceof AttributeIdentifier)) {
/*  66 */       AttributeIdentifier nodeA = (AttributeIdentifier)id;
/*     */       
/*  68 */       Iterator names = nodeA.getProperties().iterator();
/*     */       
/*  70 */       if ((nhx) && (names != null) && (names.hasNext())) {
/*  71 */         StringBuffer st = new StringBuffer("[&&NHX");
/*  72 */         while ((names != null) && (names.hasNext())) {
/*  73 */           String key = (String)names.next();
/*     */           
/*     */ 
/*  76 */           nhx = true;
/*  77 */           st.append(":");
/*  78 */           st.append(key);
/*  79 */           st.append("=");
/*  80 */           st.append(nodeA.getAttribute(key));
/*     */         }
/*  82 */         st.append("]");
/*  83 */         if (nhx) {
/*  84 */           out.print(st.toString());
/*     */         }
/*     */       }
/*     */     }
/*     */     
/*  89 */     out.println(";");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static int printNH(PrintWriter out, Node node, boolean printLengths, boolean printInternalLabels, int column, boolean breakLines, boolean nhx)
/*     */   {
/*  96 */     if (breakLines) column = breakLine(out, column);
/*  97 */     if (!node.isLeaf())
/*     */     {
/*  99 */       out.print("(");
/* 100 */       column++;
/*     */       
/* 102 */       for (int i = 0; i < node.getChildCount(); i++)
/*     */       {
/* 104 */         if (i != 0)
/*     */         {
/* 106 */           out.print(",");
/* 107 */           column++;
/*     */         }
/*     */         
/* 110 */         column = printNH(out, node.getChild(i), printLengths, printInternalLabels, column, breakLines, nhx);
/*     */       }
/*     */       
/* 113 */       out.print(")");
/* 114 */       column++;
/*     */     }
/*     */     
/* 117 */     if (!node.isRoot())
/*     */     {
/* 119 */       if ((node.isLeaf()) || (printInternalLabels))
/*     */       {
/* 121 */         if (breakLines) { column = breakLine(out, column);
/*     */         }
/* 123 */         String id = node.getIdentifier().toString();
/* 124 */         out.print(id);
/* 125 */         column += id.length();
/*     */       }
/*     */       
/* 128 */       if (printLengths)
/*     */       {
/* 130 */         out.print(":");
/* 131 */         column++;
/*     */         
/* 133 */         if (breakLines) { column = breakLine(out, column);
/*     */         }
/* 135 */         column += FormattedOutput.getInstance().displayDecimal(out, node.getBranchLength(), 7);
/*     */       }
/* 137 */       Identifier id = node.getIdentifier();
/* 138 */       if ((id instanceof AttributeIdentifier)) {
/* 139 */         AttributeIdentifier nodeA = (AttributeIdentifier)id;
/*     */         
/* 141 */         Iterator names = nodeA.getProperties().iterator();
/*     */         
/* 143 */         if ((nhx) && (names != null) && (names.hasNext())) {
/* 144 */           StringBuffer st = new StringBuffer("[&&NHX");
/* 145 */           while ((names != null) && (names.hasNext())) {
/* 146 */             String key = (String)names.next();
/*     */             
/*     */ 
/* 149 */             nhx = true;
/* 150 */             st.append(":");
/* 151 */             st.append(key);
/* 152 */             st.append("=");
/* 153 */             st.append(nodeA.getAttribute(key));
/*     */           }
/* 155 */           st.append("]");
/* 156 */           if (nhx) {
/* 157 */             out.print(st.toString());
/* 158 */             column += st.length();
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 164 */     return column;
/*     */   }
/*     */   
/*     */   private static int breakLine(PrintWriter out, int column)
/*     */   {
/* 169 */     if (column > 70)
/*     */     {
/* 171 */       out.println();
/* 172 */       column = 0;
/*     */     }
/*     */     
/* 175 */     return column;
/*     */   }
/*     */ }


/* Location:              /home/lachlan/Desktop/psilc1.21/psilc.jar!/lc1/treefam/NodeUtils.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */