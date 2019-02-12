/*     */ package lc1.pfam;
/*     */ 
/*     */ import java.io.BufferedReader;
/*     */ import java.io.File;
/*     */ import java.io.FileReader;
/*     */ import java.io.PrintStream;
/*     */ import java.util.Arrays;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import lc1.util.SheetIO;
/*     */ import org.biojava.bio.Annotation;
/*     */ import org.biojava.bio.SimpleAnnotation;
/*     */ import org.biojava.bio.symbol.AtomicSymbol;
/*     */ import org.biojava.bio.symbol.FundamentalAtomicSymbol;
/*     */ import org.biojava.bio.symbol.Symbol;
/*     */ 
/*     */ 
/*     */ public class PfamAlphabet
/*     */   extends DomainAlphabet
/*     */ {
/*     */   private static PfamAlphabet alphabet;
/*  24 */   public static boolean usePfACC = true;
/*     */   List magicOne;
/*     */   
/*  27 */   public static void reset(PfamAlphabet alph) { alphabet = alph; }
/*     */   
/*     */   public static PfamAlphabet makeAlphabet(File repos)
/*     */   {
/*  31 */     System.err.println(repos);
/*  32 */     if (alphabet == null) {
/*     */       try {
/*  34 */         File pfamA = new File(repos, "pfamA");
/*  35 */         if (pfamA.exists()) {
/*  36 */           File clans = new File(repos, "clans");
/*     */           
/*  38 */           System.err.println("Using " + pfamA.getAbsolutePath() + " to generate list of pfam families");
/*  39 */           alphabet = makeAlphabet(SheetIO.read(pfamA, "\\s+"), 
/*  40 */             clans.exists() ? SheetIO.read(clans, "\t") : 
/*  41 */             Arrays.asList(new Object[0]).iterator());
/*  42 */           System.err.println("done");
/*     */         }
/*     */         else {
/*  45 */           File pfam_ls = new File(repos, "Pfam_ls");
/*  46 */           PfamIndex index = new PfamIndex(repos);
/*  47 */           System.err.println("Using " + pfam_ls.getAbsolutePath() + " to generate pfam family list");
/*  48 */           alphabet = makeAlphabet(index.getNameIterator(), null);
/*  49 */           System.err.println(alphabet.size());
/*     */         }
/*     */       } catch (Exception exc) {
/*  52 */         exc.printStackTrace();
/*  53 */         System.exit(0);
/*     */       }
/*     */     }
/*  56 */     return alphabet;
/*     */   }
/*     */   
/*     */   private static Iterator getAlphabetInformationFromFiles(File repos, boolean desc)
/*     */   {
/*  61 */     File[] files = repos.listFiles();
/*  62 */     new Iterator() {
/*     */       int i;
/*     */       
/*  65 */       public boolean hasNext() { return this.i < PfamAlphabet.this.length; }
/*     */       
/*     */       public void remove() {}
/*     */       
/*  69 */       public Object next() { Object[] row = new Object[4];
/*  70 */         row[0] = PfamAlphabet.this[this.i].getName();
/*  71 */         row[1] = PfamAlphabet.this[this.i].getName();
/*  72 */         row[2] = "-2147483648";
/*  73 */         row[3] = "-2147483648";
/*     */         
/*  75 */         if (this.val$desc) {
/*  76 */           try { File f = new File(PfamAlphabet.this[this.i], "DESC");
/*  77 */             if ((f.exists()) && (f.length() > 0L)) {
/*  78 */               BufferedReader hmm = new BufferedReader(new FileReader(f));
/*  79 */               row[0] = hmm.readLine().split("\\s+")[1];
/*     */               
/*     */ 
/*  82 */               while (!(s = hmm.readLine()).startsWith("GA")) {}
/*     */               
/*  84 */               String s = s.replace(';', ' ');
/*  85 */               String[] st = s.split("\\s+");
/*  86 */               row[3] = st[1];
/*  87 */               row[2] = st[2];
/*  88 */               hmm.close();
/*     */             }
/*     */           }
/*     */           catch (Exception e) {
/*  92 */             e.printStackTrace();
/*     */           }
/*     */         }
/*  95 */         this.i += 1;
/*  96 */         return Arrays.asList(row);
/*     */       }
/*     */     };
/*     */   }
/*     */   
/*     */   public static PfamAlphabet makeAlphabet(Iterator sh, Iterator clans) {
/* 102 */     if (alphabet == null) {
/* 103 */       alphabet = new PfamAlphabet(sh, pfamAccToClanAcc(clans));
/*     */     }
/* 105 */     return alphabet;
/*     */   }
/*     */   
/*     */   private static Map pfamAccToClanAcc(Iterator clans) {
/* 109 */     Map m = new HashMap();
/* 110 */     while ((clans != null) && (clans.hasNext())) {
/* 111 */       List l = (List)clans.next();
/* 112 */       m.put(l.get(0), l.get(2));
/*     */     }
/* 114 */     return m;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private PfamAlphabet(Iterator sh, Map m)
/*     */   {
/* 124 */     super(modify(sh), m, "PfamA");
/*     */   }
/*     */   
/*     */   private static Iterator modify(Iterator sh1) {
/* 128 */     Float zero = new Float(0.0F);
/* 129 */     Integer one = new Integer(1);
/* 130 */     new Iterator()
/*     */     {
/* 132 */       public boolean hasNext() { return PfamAlphabet.this.hasNext(); }
/*     */       
/*     */       public Object next() {
/* 135 */         List row1 = (List)PfamAlphabet.this.next();
/* 136 */         Object[] row = new Object[5];
/* 137 */         row[0] = row1.get(0);
/* 138 */         row[1] = row1.get(1);
/* 139 */         if (row1.size() > 2) {
/* 140 */           row[2] = new Float(Float.parseFloat((String)row1.get(2)));
/* 141 */           row[3] = new Float(Float.parseFloat((String)row1.get(3)));
/*     */         }
/*     */         else
/*     */         {
/* 145 */           row[2] = (row[3] = new Float(0.0F));
/* 146 */           row[4] = new Integer(0);
/*     */         }
/* 148 */         return Arrays.asList(row);
/*     */       }
/*     */       
/*     */       public void remove() {}
/*     */     };
/*     */   }
/*     */   
/*     */   private static Iterator listToSheet(String[] domains) {
/* 156 */     Float zero = new Float(0.0F);
/* 157 */     Integer one = new Integer(1);
/* 158 */     new Iterator() {
/*     */       int i;
/*     */       
/* 161 */       public boolean hasNext() { return this.i < PfamAlphabet.this.length; }
/*     */       
/*     */       public void remove() {}
/*     */       
/* 165 */       public Object next() { Object[] row = new Object[5];
/* 166 */         row[0] = this.i;
/* 167 */         row[1] = PfamAlphabet.this[this.i];
/* 168 */         row[2] = this.val$zero;
/* 169 */         row[3] = this.val$zero;
/* 170 */         row[4] = this.val$one;
/* 171 */         this.i += 1;
/* 172 */         return Arrays.asList(row);
/*     */       }
/*     */     };
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   AtomicSymbol createSymbol(List row, int i)
/*     */   {
/* 182 */     SimpleAnnotation ann = new SimpleAnnotation();
/*     */     try
/*     */     {
/* 185 */       ann.setProperty("Name", row.get(0));
/* 186 */       ann.setProperty("pfamA_id", row.get(1));
/* 187 */       ann.setProperty("ls_dom_thresh", row.get(2));
/* 188 */       ann.setProperty("ls_seq_thresh", row.get(3));
/* 189 */       ann.setProperty("modelLength", row.get(4));
/*     */     }
/*     */     catch (Exception exc)
/*     */     {
/* 193 */       exc.printStackTrace(); }
/* 194 */     AtomicSymbol sym = new FundamentalAtomicSymbol((String)row.get(0), ann) {
/*     */       int index;
/*     */       
/* 197 */       public int hashCode() { return this.val$i; }
/*     */       
/*     */       public boolean equals(Object o) {
/* 200 */         if (!(o instanceof Symbol)) return false;
/* 201 */         return ((Symbol)o).hashCode() == this.val$i;
/*     */       }
/*     */       
/* 204 */     };
/* 205 */     return sym;
/*     */   }
/*     */   
/*     */ 
/*     */   public String stringifySymbol(Symbol sym)
/*     */   {
/* 211 */     return 
/*     */     
/* 213 */       super.stringifySymbol(sym) + " " + sym.getAnnotation().getProperty("ls_dom_thresh") + " " + sym.getAnnotation().getProperty("ls_seq_thresh") + " ";
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public List magicContext(int length)
/*     */   {
/* 221 */     if (length == 1) {
/* 222 */       if (this.magicOne == null) this.magicOne = Arrays.asList(new Symbol[] { getMagicalState() });
/* 223 */       return this.magicOne;
/*     */     }
/* 225 */     if (length == 2) {
/* 226 */       if (this.magicTwo == null) this.magicTwo = Arrays.asList(new Symbol[] { getMagicalState(), getMagicalState() });
/* 227 */       return this.magicTwo;
/*     */     }
/* 229 */     Symbol[] sym = new Symbol[length];
/* 230 */     for (int i = 0; i < length; i++) {
/* 231 */       sym[i] = getMagicalState();
/*     */     }
/* 233 */     return Arrays.asList(sym);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   List magicTwo;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Symbol getMagicalState()
/*     */   {
/* 251 */     return this.magicSymbol;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static void resetAlphabet()
/*     */   {
/* 259 */     alphabet = null;
/*     */   }
/*     */ }


/* Location:              /home/lachlan/Desktop/psilc1.21/psilc.jar!/lc1/pfam/PfamAlphabet.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */