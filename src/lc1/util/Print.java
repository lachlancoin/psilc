/*     */ package lc1.util;
/*     */ 
/*     */ import com.braju.format.Format;
/*     */ import com.braju.format.Parameters;
/*     */ import java.util.Enumeration;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import org.biojava.bio.dist.Distribution;
/*     */ import org.biojava.bio.dp.EmissionState;
/*     */ import org.biojava.bio.dp.ProfileHMM;
/*     */ import org.biojava.bio.symbol.Alignment;
/*     */ import org.biojava.bio.symbol.Alphabet;
/*     */ import org.biojava.bio.symbol.FiniteAlphabet;
/*     */ import org.biojava.bio.symbol.Symbol;
/*     */ import org.biojava.bio.symbol.SymbolList;
/*     */ import pal.datatype.AminoAcids;
/*     */ import pal.datatype.Codons;
/*     */ import pal.datatype.DataType;
/*     */ import pal.datatype.Nucleotides;
/*     */ 
/*     */ 
/*     */ public class Print
/*     */ {
/*     */   public static String toString(Enumeration v)
/*     */   {
/*  26 */     String st = "";
/*  27 */     while (v.hasMoreElements())
/*     */     {
/*  29 */       st = st + v.nextElement();
/*     */     }
/*  31 */     return st;
/*     */   }
/*     */   
/*     */   public static String toString(DataType dt) {
/*  35 */     String[] c = new String[dt.getNumStates()];
/*  36 */     for (int i = 0; i < dt.getNumStates(); i++) {
/*  37 */       if ((dt instanceof Codons)) {
/*  38 */         c[i] = new String(Codons.getNucleotidesFromCodonIndex(i));
/*     */       }
/*     */       else {
/*  41 */         c[i] = dt.getChar(i);
/*     */       }
/*     */     }
/*  44 */     String format = getFormatString(c.length, "%5s");
/*  45 */     return Format.sprintf(format, c);
/*     */   }
/*     */   
/*     */   public static String getFormatString(int length, String format) {
/*  49 */     String st = "";
/*  50 */     for (int i = 0; i < length - 1; i++) {
/*  51 */       st = st + format + " ";
/*     */     }
/*  53 */     return st + format + "\n";
/*     */   }
/*     */   
/*     */   public static double[] subtract(double[] a, double[] b) {
/*  57 */     double[] r = new double[a.length];
/*  58 */     for (int i = 0; i < r.length; i++) {
/*  59 */       a[i] -= b[i];
/*     */     }
/*  61 */     return r;
/*     */   }
/*     */   
/*     */   public static String toString(Iterator v) {
/*  65 */     String st = "";
/*  66 */     while (v.hasNext())
/*     */     {
/*  68 */       st = st + v.next();
/*     */     }
/*  70 */     return st;
/*     */   }
/*     */   
/*     */   public static String toString(Distribution d) {
/*  74 */     return toString(d, d.getAlphabet());
/*     */   }
/*     */   
/*     */   public static String toString(Distribution d, Alphabet alph)
/*     */   {
/*     */     try {
/*  80 */       Iterator alpha = ((FiniteAlphabet)d.getAlphabet()).iterator();
/*  81 */       HashMap entropy = new HashMap(((FiniteAlphabet)d.getAlphabet()).size());
/*  82 */       while (alpha.hasNext()) {
/*  83 */         Symbol s = (Symbol)alpha.next();
/*  84 */         double obs = d.getWeight(s);
/*  85 */         if (obs != 0.0D) {
/*  86 */           entropy.put(s.getName(), new Double(obs));
/*     */         }
/*     */       }
/*     */       
/*     */ 
/*  91 */       return entropy.toString();
/*     */     } catch (Exception e) {
/*  93 */       e.printStackTrace(); }
/*  94 */     return null;
/*     */   }
/*     */   
/*     */   public static String toString(Object[] o)
/*     */   {
/*  99 */     String str = new String();
/* 100 */     for (int i = 0; i < o.length; i++) {
/* 101 */       if (o[i] == null) {
/* 102 */         str = str + "[null]";
/*     */       } else
/* 104 */         str = str + " " + o[i].toString() + " ";
/*     */     }
/* 106 */     return str;
/*     */   }
/*     */   
/*     */   public static String toString(Object[][] o) {
/* 110 */     String str = new String();
/* 111 */     for (int i = 0; i < o.length; i++) {
/* 112 */       for (int j = 0; j < o[i].length; j++) {
/* 113 */         if (o[i][j] == null) {
/* 114 */           str = str + "null ";
/*     */         } else
/* 116 */           str = str + o[i][j].toString();
/*     */       }
/* 118 */       str = str + "\n";
/*     */     }
/* 120 */     str = str + "\n";
/* 121 */     return str;
/*     */   }
/*     */   
/*     */   public static String toString(ProfileHMM hmm) {
/*     */     try {
/* 126 */       String st = new String();
/* 127 */       Alphabet alph = hmm.stateAlphabet();
/* 128 */       for (int i = 0; i <= hmm.columns(); i++) {
/* 129 */         st = st + "Match  Emission" + i + " " + toString(hmm.getMatch(i).getDistribution(), alph) + "\n";
/* 130 */         if ((i > 0) && (i < hmm.columns()))
/* 131 */           st = st + "Insert Emission" + i + " " + toString(hmm.getInsert(i).getDistribution(), alph) + "\n";
/* 132 */         st = st + "Match  Transitions" + i + " " + toString(hmm.getWeights(hmm.getMatch(i)), alph) + "\n";
/* 133 */         if ((i > 0) && (i < hmm.columns())) {
/* 134 */           st = st + "Insert Transitions" + i + " " + toString(hmm.getWeights(hmm.getInsert(i)), alph) + "\n";
/* 135 */           st = st + "Delete Transitions" + i + " " + toString(hmm.getWeights(hmm.getDelete(i)), alph) + "\n";
/*     */         }
/*     */       }
/* 138 */       return st;
/*     */     }
/*     */     catch (Throwable t) {
/* 141 */       t.printStackTrace(); }
/* 142 */     return "";
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static String toString(int[] o)
/*     */   {
/* 150 */     String str = new String();
/* 151 */     for (int i = 0; i < o.length; i++) {
/* 152 */       str = str + o[i] + ",";
/*     */     }
/*     */     
/* 155 */     return str;
/*     */   }
/*     */   
/*     */   public static String toString(char[] o) {
/* 159 */     String str = new String();
/* 160 */     for (int i = 0; i < o.length; i++) {
/* 161 */       str = str + Format.sprintf("% 8s", 
/* 162 */         new Parameters(o[i]));
/*     */     }
/*     */     
/*     */ 
/* 166 */     return str;
/*     */   }
/*     */   
/*     */   public static String toString(long[] o)
/*     */   {
/* 171 */     String str = new String();
/* 172 */     for (int i = 0; i < o.length; i++) {
/* 173 */       str = str + o[i] + ",";
/*     */     }
/*     */     
/* 176 */     return str;
/*     */   }
/*     */   
/*     */   public static String toString(byte[] o)
/*     */   {
/* 181 */     String str = new String();
/* 182 */     for (int i = 0; i < o.length; i++) {
/* 183 */       str = str + o[i] + ",";
/*     */     }
/*     */     
/* 186 */     return str;
/*     */   }
/*     */   
/*     */ 
/*     */   public static String toString(double[] o)
/*     */   {
/* 192 */     if (o.length == 0) return "";
/* 193 */     String str = new String();
/*     */     
/* 195 */     String format = getFormatString(o.length, "%4.2g");
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 202 */     Object[] obj = new Object[o.length];
/* 203 */     for (int i = 0; i < o.length; i++) {
/* 204 */       obj[i] = new Double(o[i]);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 210 */     if (obj.length == 0) return "";
/* 211 */     return Format.sprintf(format, obj);
/*     */   }
/*     */   
/*     */   public static String toStringDT(double[] o) {
/* 215 */     String str = new String();
/*     */     DataType dt;
/* 217 */     DataType dt; if (o.length == 20) { dt = AminoAcids.DEFAULT_INSTANCE; } else { DataType dt;
/* 218 */       if (o.length == 4) dt = Nucleotides.DEFAULT_INSTANCE; else
/* 219 */         dt = Codons.DEFAULT_INSTANCE; }
/* 220 */     for (int i = 0; i < o.length; i++)
/*     */     {
/*     */ 
/* 223 */       if ((dt instanceof Codons)) {
/* 224 */         str = str + new String(Codons.getNucleotidesFromCodonIndex(i));
/*     */       }
/*     */       else {
/* 227 */         str = str + dt.getChar(i);
/*     */       }
/* 229 */       str = str + ":" + Math.floor(o[i] * 1000.0D) / 1000.0D + " ";
/*     */     }
/*     */     
/* 232 */     return str;
/*     */   }
/*     */   
/*     */   public static String toStringSum(double[] o)
/*     */   {
/* 237 */     double str = 0.0D;
/* 238 */     for (int i = 0; i < o.length; i++) {
/* 239 */       str += o[i];
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 244 */     return str;
/*     */   }
/*     */   
/*     */   public static String toStringExp(double[] o) {
/* 248 */     String str = new String();
/* 249 */     for (int i = 0; i < o.length; i++)
/*     */     {
/*     */ 
/* 252 */       str = str + Math.floor(Math.exp(o[i]) * 1000.0D) / 1000.0D + " ";
/*     */     }
/*     */     
/* 255 */     return str;
/*     */   }
/*     */   
/*     */   public static String toString(boolean[] o)
/*     */   {
/* 260 */     String str = new String();
/* 261 */     for (int i = 0; i < o.length; i++) {
/* 262 */       str = str + o[i] + ",";
/*     */     }
/* 264 */     str = str + "\n";
/* 265 */     return str;
/*     */   }
/*     */   
/*     */   public static String toStringI(int[][] o) {
/* 269 */     String str = new String();
/* 270 */     for (int i = 0; i < o.length; i++) {
/* 271 */       for (int j = 0; j < o[i].length; j++) {
/* 272 */         str = str + o[i][j] + ",";
/*     */       }
/* 274 */       str = str + "\n";
/*     */     }
/* 276 */     str = str + "\n";
/* 277 */     return str;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static String toStringD(double[][] o)
/*     */   {
/* 285 */     String str = new String();
/* 286 */     for (int i = 0; i < o.length; i++) {
/* 287 */       for (int j = 0; j < o[i].length; j++) {
/* 288 */         if (o[i][j] == Double.NEGATIVE_INFINITY) {
/* 289 */           str = str + "-!! ";
/* 290 */         } else if (o[i][j] == Double.POSITIVE_INFINITY) {
/* 291 */           str = str + "+!! ";
/*     */         } else
/* 293 */           str = str + Math.floor(o[i][j] * 10.0D) / 10.0D + " ";
/*     */       }
/* 295 */       str = str + "\n";
/*     */     }
/* 297 */     str = str + "\n";
/* 298 */     return str;
/*     */   }
/*     */   
/*     */   public static String toString(boolean[][] o)
/*     */   {
/* 303 */     String str = new String();
/* 304 */     for (int i = 0; i < o.length; i++) {
/* 305 */       str = str + "[";
/* 306 */       for (int j = 0; j < o[i].length; j++) {
/* 307 */         str = str + "[" + o[i][j] + "]";
/*     */       }
/* 309 */       str = str + "]\n";
/*     */     }
/* 311 */     str = str + "\n";
/* 312 */     return str;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static String toString(SymbolList align)
/*     */   {
/* 324 */     if ((align instanceof Alignment)) {
/* 325 */       String st = "";
/* 326 */       Iterator it = ((Alignment)align).symbolListIterator();
/* 327 */       while (it.hasNext()) {
/* 328 */         st = st + ((SymbolList)it.next()).seqString() + "\n";
/*     */       }
/* 330 */       return st;
/*     */     }
/*     */     
/* 333 */     return align.seqString();
/*     */   }
/*     */ }


/* Location:              /home/lachlan/Desktop/psilc1.21/psilc.jar!/lc1/util/Print.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */