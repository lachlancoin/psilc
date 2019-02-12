/*    */ package lc1.phyl;
/*    */ 
/*    */ import java.util.Stack;
/*    */ 
/*    */ public class TreeIterator
/*    */ {
/*    */   private pal.tree.Tree tree;
/*    */   private pal.tree.Node node;
/*    */   private StackItem si;
/*    */   private boolean is_done;
/*    */   private Stack stack;
/*    */   
/*    */   public TreeIterator(pal.tree.Tree t) throws Exception {
/* 14 */     if (t.getExternalNodeCount() == 0) {
/* 15 */       String message = "PostorderTreeIterator: Tree is empty.";
/* 16 */       throw new Exception(message);
/*    */     }
/*    */     
/* 19 */     this.tree = t;
/* 20 */     first();
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public void first()
/*    */   {
/* 32 */     this.is_done = false;
/* 33 */     this.node = null;
/* 34 */     this.stack = new Stack();
/* 35 */     this.stack.push(new StackItem(this.tree.getRoot(), 1));
/* 36 */     next();
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public void next()
/*    */   {
/* 49 */     if ((this.node != null) && (this.node == this.tree.getRoot())) {
/* 50 */       this.is_done = true;
/*    */     }
/*    */     else {
/*    */       for (;;)
/*    */       {
/* 55 */         this.si = ((StackItem)this.stack.pop());
/* 56 */         if (this.si.getNode() != null) {
/* 57 */           switch (this.si.getPhase()) {
/*    */           case 1: 
/* 59 */             this.stack.push(new StackItem(this.si.getNode(), 2));
/* 60 */             this.stack.push(new StackItem(this.si.getNode().getChild(1), 1));
/* 61 */             break;
/*    */           case 2: 
/* 63 */             this.stack.push(new StackItem(this.si.getNode(), 3));
/* 64 */             this.stack.push(new StackItem(this.si.getNode().getChild(2), 1));
/* 65 */             break;
/*    */           case 3: 
/* 67 */             this.node = this.si.getNode();
/* 68 */             return;
/*    */           }
/*    */           
/*    */         }
/*    */       }
/*    */     }
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public boolean isDone()
/*    */   {
/* 82 */     return this.is_done;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public pal.tree.Node currentNode()
/*    */   {
/* 95 */     if (isDone()) {
/* 96 */       return null;
/*    */     }
/*    */     
/* 99 */     return this.node;
/*    */   }
/*    */ }


/* Location:              /home/lachlan/Desktop/psilc1.21/psilc.jar!/lc1/phyl/TreeIterator.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */