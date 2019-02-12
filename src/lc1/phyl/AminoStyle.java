/*     */ package lc1.phyl;
/*     */ 
/*     */ import java.awt.Color;
/*     */ import java.awt.Paint;
/*     */ import java.util.HashMap;
/*     */ import java.util.Hashtable;
/*     */ import java.util.Map;
/*     */ import java.util.Vector;
/*     */ import org.biojava.bio.gui.SymbolStyle;
/*     */ import org.biojava.bio.seq.ProteinTools;
/*     */ import org.biojava.bio.seq.io.SymbolTokenization;
/*     */ import org.biojava.bio.symbol.Alphabet;
/*     */ import org.biojava.bio.symbol.IllegalSymbolException;
/*     */ import org.biojava.bio.symbol.Symbol;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class AminoStyle
/*     */   implements SymbolStyle
/*     */ {
/*     */   Alphabet alph;
/*     */   Map colorMap;
/*     */   Hashtable[] cons;
/*     */   int[][] cons2;
/*     */   Color[] colours;
/*     */   Color[] Color;
/*     */   int size;
/*     */   Hashtable colhash;
/*     */   String[] syms;
/*     */   Vector colourTable;
/*     */   
/*     */   public Paint fillPaint(Symbol s)
/*     */     throws IllegalSymbolException
/*     */   {
/*  36 */     Color c = (Color)this.colorMap.get(s);
/*  37 */     if (c == null) throw new IllegalSymbolException(s.getName() + " is illegal");
/*  38 */     return c;
/*     */   }
/*     */   
/*     */   public Paint outlinePaint(Symbol s)
/*     */     throws IllegalSymbolException
/*     */   {
/*  44 */     Color c = (Color)this.colorMap.get(s);
/*  45 */     if (c == null) throw new IllegalSymbolException(s.getName() + " is illegal");
/*  46 */     return c;
/*     */   }
/*     */   
/*  49 */   public AminoStyle() { this.alph = ProteinTools.getAlphabet();
/*  50 */     this.colorMap = new HashMap();
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  56 */     this.colhash = new Hashtable();
/*  57 */     this.syms = new String[20];
/*     */     
/*  59 */     this.colourTable = new Vector();
/*     */     
/*  61 */     this.colhash.put("RED", new Color(0.9F, 0.2F, 0.1F));
/*  62 */     this.colhash.put("BLUE", new Color(0.5F, 0.7F, 0.9F));
/*  63 */     this.colhash.put("GREEN", new Color(0.1F, 0.8F, 0.1F));
/*  64 */     this.colhash.put("ORANGE", new Color(0.9F, 0.6F, 0.3F));
/*  65 */     this.colhash.put("CYAN", new Color(0.1F, 0.7F, 0.7F));
/*  66 */     this.colhash.put("PINK", new Color(0.9F, 0.5F, 0.5F));
/*  67 */     this.colhash.put("MAGENTA", new Color(0.8F, 0.3F, 0.8F));
/*  68 */     this.colhash.put("YELLOW", new Color(0.8F, 0.8F, 0.0F));
/*     */     
/*  70 */     this.colours = new Color[11];
/*     */     
/*     */ 
/*  73 */     this.colours[7] = ((Color)this.colhash.get("ORANGE"));
/*  74 */     this.colours[8] = ((Color)this.colhash.get("YELLOW"));
/*  75 */     this.colours[9] = ((Color)this.colhash.get("PINK"));
/*  76 */     this.colours[0] = ((Color)this.colhash.get("BLUE"));
/*     */     
/*  78 */     this.colours[10] = ((Color)this.colhash.get("CYAN"));
/*  79 */     this.colours[1] = ((Color)this.colhash.get("GREEN"));
/*     */     
/*  81 */     this.colours[2] = ((Color)this.colhash.get("GREEN"));
/*     */     
/*  83 */     this.colours[3] = ((Color)this.colhash.get("GREEN"));
/*  84 */     this.colours[4] = ((Color)this.colhash.get("RED"));
/*     */     
/*  86 */     this.colours[5] = ((Color)this.colhash.get("MAGENTA"));
/*     */     
/*     */ 
/*  89 */     this.colours[6] = ((Color)this.colhash.get("MAGENTA"));
/*     */     
/*  91 */     this.Color = new Color[20];
/*  92 */     this.Color[0] = this.colours[0];
/*  93 */     this.Color[1] = this.colours[4];
/*  94 */     this.Color[2] = this.colours[2];
/*  95 */     this.Color[3] = this.colours[6];
/*  96 */     this.Color[4] = this.colours[0];
/*  97 */     this.Color[5] = this.colours[3];
/*  98 */     this.Color[6] = this.colours[5];
/*  99 */     this.Color[7] = this.colours[7];
/* 100 */     this.Color[8] = this.colours[10];
/* 101 */     this.Color[9] = this.colours[0];
/* 102 */     this.Color[10] = this.colours[0];
/* 103 */     this.Color[11] = this.colours[4];
/* 104 */     this.Color[12] = this.colours[0];
/* 105 */     this.Color[13] = this.colours[0];
/* 106 */     this.Color[14] = this.colours[8];
/* 107 */     this.Color[15] = this.colours[1];
/* 108 */     this.Color[16] = this.colours[1];
/* 109 */     this.Color[17] = this.colours[0];
/* 110 */     this.Color[18] = this.colours[10];
/* 111 */     this.Color[19] = this.colours[0];
/* 112 */     this.syms = new String[] { "A", "R", "N", "D", "C", "Q", "E", "G", "H", "I", "L", "K", "M", "F", "P", "S", "T", "W", "Y", "V" };
/*     */     try {
/* 114 */       SymbolTokenization token = this.alph.getTokenization("token");
/* 115 */       for (int i = 0; i < this.Color.length; i++) {
/* 116 */         this.colorMap.put(token.parseToken(this.syms[i]), this.Color[i]);
/*     */       }
/*     */     }
/*     */     catch (Exception localException) {}
/*     */   }
/*     */ }


/* Location:              /home/lachlan/Desktop/psilc1.21/psilc.jar!/lc1/phyl/AminoStyle.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */