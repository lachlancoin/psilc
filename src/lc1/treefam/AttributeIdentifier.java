/*    */ package lc1.treefam;
/*    */ 
/*    */ import java.util.Collection;
/*    */ import java.util.Properties;
/*    */ import pal.misc.Identifier;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class AttributeIdentifier
/*    */   extends Identifier
/*    */ {
/* 22 */   private Properties attributes = new Properties();
/*    */   
/* 24 */   AttributeIdentifier(String name) { super(name); }
/*    */   
/*    */   public void setAttribute(String attr, String obj) {
/* 27 */     this.attributes.setProperty(attr, obj);
/*    */   }
/*    */   
/*    */   public String getAttribute(String attr)
/*    */   {
/* 32 */     return this.attributes.getProperty(attr);
/*    */   }
/*    */   
/* 35 */   public Collection getProperties() { return this.attributes.keySet(); }
/*    */ }


/* Location:              /home/lachlan/Desktop/psilc1.21/psilc.jar!/lc1/treefam/AttributeIdentifier.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */