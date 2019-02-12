/*     */ package lc1.treefam;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.PrintStream;
/*     */ import java.io.PushbackReader;
/*     */ import pal.io.FormattedInput;
/*     */ import pal.io.InputSource;
/*     */ import pal.tree.Node;
/*     */ import pal.tree.NodeFactory;
/*     */ import pal.tree.NodeUtils;
/*     */ import pal.tree.SimpleTree;
/*     */ import pal.tree.TreeParseException;
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
/*     */ 
/*     */ 
/*     */ public class ReadTree
/*     */   extends SimpleTree
/*     */ {
/*     */   public ReadTree(PushbackReader input)
/*     */     throws TreeParseException
/*     */   {
/*  46 */     readNH(input);
/*     */     
/*     */ 
/*  49 */     NodeUtils.lengths2Heights(getRoot());
/*     */     
/*  51 */     createNodeList();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public ReadTree(String file)
/*     */     throws TreeParseException, IOException
/*     */   {
/*  63 */     PushbackReader input = InputSource.openFile(file);
/*  64 */     readNH(input);
/*  65 */     input.close();
/*     */     
/*     */ 
/*  68 */     NodeUtils.lengths2Heights(getRoot());
/*  69 */     createNodeList();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  77 */   private FormattedInput fi = FormattedInput.getInstance();
/*     */   
/*     */ 
/*     */   private void readNH(PushbackReader input, Node currentNode)
/*     */     throws TreeParseException
/*     */   {
/*     */     try
/*     */     {
/*  85 */       int c = this.fi.readNextChar(input);
/*     */       
/*  87 */       if (c == 40)
/*     */       {
/*  89 */         int count = 0;
/*     */         do
/*     */         {
/*  92 */           Node newNode = NodeFactory.createNode();
/*  93 */           currentNode.addChild(newNode);
/*  94 */           readNH(input, newNode);
/*  95 */           count++;
/*     */           
/*  97 */           c = this.fi.readNextChar(input);
/*     */         }
/*  99 */         while (c == 44);
/*     */         
/* 101 */         if (c != 41) {
/* 102 */           throw new TreeParseException("Missing closing bracket ");
/*     */         }
/*     */         
/* 105 */         if (count < 2) {
/* 106 */           throw new TreeParseException("Node with single child enountered");
/*     */         }
/*     */         
/*     */       }
/*     */       else
/*     */       {
/* 112 */         input.unread(c);
/*     */       }
/*     */       
/*     */ 
/* 116 */       currentNode.setIdentifier(new AttributeIdentifier(this.fi.readLabel(input, -1)));
/*     */       
/*     */ 
/* 119 */       c = this.fi.readNextChar(input);
/*     */       
/* 121 */       if (c == 58)
/*     */       {
/* 123 */         currentNode.setBranchLength(this.fi.readDouble(input, true));
/* 124 */         c = this.fi.readNextChar(input);
/*     */       }
/* 126 */       if ((c == 91) || (c == 38))
/*     */       {
/* 128 */         this.fi.readLabel(input, -1);
/* 129 */         int c1 = input.read();
/* 130 */         while (c1 != 93) {
/* 131 */           String st = this.fi.readLabel(input, -1);
/* 132 */           int c2 = input.read();
/* 133 */           while (c2 == 32) {
/* 134 */             st = st + " ";
/* 135 */             st = st + this.fi.readLabel(input, -1);
/* 136 */             c2 = input.read();
/*     */           }
/* 138 */           System.err.println(st);
/* 139 */           input.unread(c2);
/* 140 */           int index = st.indexOf('=');
/* 141 */           ((AttributeIdentifier)currentNode.getIdentifier()).setAttribute(st.substring(0, index), st.substring(index + 1));
/* 142 */           c1 = input.read();
/*     */         }
/*     */       }
/*     */       else
/*     */       {
/* 147 */         input.unread(c);
/*     */       }
/*     */     }
/*     */     catch (IOException e)
/*     */     {
/* 152 */       throw new TreeParseException("IO error");
/*     */     }
/*     */     catch (NumberFormatException e) {
/* 155 */       throw new TreeParseException("Error while parsing number");
/*     */     }
/*     */   }
/*     */   
/*     */   private void readNH(PushbackReader input)
/*     */     throws TreeParseException
/*     */   {
/*     */     try
/*     */     {
/* 164 */       readNH(input, getRoot());
/*     */       
/*     */ 
/* 167 */       int c = this.fi.readNextChar(input);
/* 168 */       if (c != 59)
/*     */       {
/* 170 */         throw new TreeParseException("Missing terminating semicolon");
/*     */       }
/*     */       
/*     */     }
/*     */     catch (IOException e)
/*     */     {
/* 176 */       throw new TreeParseException();
/*     */     }
/*     */   }
/*     */ }


/* Location:              /home/lachlan/Desktop/psilc1.21/psilc.jar!/lc1/treefam/ReadTree.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */