/*     */ package lc1.pfam;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import org.biojava.bio.Annotation;
/*     */ import org.biojava.bio.SimpleAnnotation;
/*     */ import org.biojava.bio.seq.io.NameTokenization;
/*     */ import org.biojava.bio.symbol.AtomicSymbol;
/*     */ import org.biojava.bio.symbol.FiniteAlphabet;
/*     */ import org.biojava.bio.symbol.FundamentalAtomicSymbol;
/*     */ import org.biojava.bio.symbol.IllegalSymbolException;
/*     */ import org.biojava.bio.symbol.SimpleAlphabet;
/*     */ import org.biojava.bio.symbol.SimpleSymbolList;
/*     */ import org.biojava.bio.symbol.Symbol;
/*     */ import org.biojava.bio.symbol.SymbolList;
/*     */ import org.biojava.utils.ChangeVetoException;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class DomainAlphabet
/*     */   extends SimpleAlphabet
/*     */ {
/*     */   private Map nameToSymbols;
/*     */   protected Symbol magicSymbol;
/*     */   Map nameToClan;
/*     */   Symbol[] domains;
/*     */   
/*     */   private DomainAlphabet(DomainAlphabet al1, DomainAlphabet al2)
/*     */   {
/*  90 */     this(symbols(al1, al2), 
/*  91 */       "Compound alphabet: " + al1.getName() + " " + al2.getName());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Symbol getSuccessor(Symbol sym)
/*     */   {
/* 100 */     int index = sym.hashCode();
/* 101 */     if (index < this.domains.length - 1) return this.domains[(index + 1)];
/* 102 */     return null;
/*     */   }
/*     */   
/*     */   protected DomainAlphabet(Iterator iter, Map pfamAccToClanAcc, String name)
/*     */   {
/* 107 */     super(name);
/* 108 */     this.nameToSymbols = new HashMap();
/* 109 */     this.nameToClan = pfamAccToClanAcc;
/* 110 */     List l = new ArrayList();
/*     */     try
/*     */     {
/* 113 */       SimpleAnnotation ann = new SimpleAnnotation();
/* 114 */       ann.setProperty("Name", "!-1");
/* 115 */       Float zero = new Float(0.0F);
/* 116 */       ann.setProperty("ls_dom_thresh", zero);
/* 117 */       ann.setProperty("ls_seq_thresh", zero);
/* 118 */       ann.setProperty("fs_dom_thresh", zero);
/* 119 */       ann.setProperty("ls_seq_thresh", zero);
/* 120 */       ann.setProperty("modelLength", new Integer(0));
/* 121 */       ann.setProperty("pfamA_id", "END state");
/* 122 */       Symbol mSymbol = new FundamentalAtomicSymbol("!-1", ann) {
/* 123 */         int index = 0;
/*     */         
/* 125 */         public int hashCode() { return this.index; }
/*     */         
/*     */         public boolean equals(Object o) {
/* 128 */           if (!(o instanceof Symbol)) return false;
/* 129 */           return ((Symbol)o).hashCode() == 0;
/*     */         }
/* 131 */       };
/* 132 */       this.nameToSymbols.put("!-1", mSymbol);
/* 133 */       l.add(mSymbol);
/*     */       
/* 135 */       addSymbol(mSymbol);
/* 136 */       Map addedClans = new HashMap();
/* 137 */       for (int i = 0; iter.hasNext(); i++) {
/* 138 */         List row = (List)iter.next();
/* 139 */         Object pfam_acc = row.get(0);
/* 140 */         Object clan_acc = pfamAccToClanAcc.get(pfam_acc);
/*     */         
/*     */         Symbol sym;
/* 143 */         if (clan_acc != null) {
/* 144 */           List clan_members = (List)addedClans.get(clan_acc);
/* 145 */           if (clan_members == null) {
/* 146 */             clan_members = new ArrayList();
/* 147 */             addedClans.put(clan_acc, clan_members);
/*     */           }
/*     */           
/* 150 */           Symbol sym = createSymbol(row, i + 1);
/* 151 */           addSymbol(sym);
/* 152 */           l.add(sym);
/* 153 */           clan_members.add(sym);
/* 154 */           Annotation annot = sym.getAnnotation();
/*     */           
/* 156 */           sym.getAnnotation().setProperty("clan", clan_members.get(0));
/* 157 */           sym.getAnnotation().setProperty("clan_id", clan_acc);
/* 158 */           this.nameToClan.put(pfam_acc, sym);
/*     */         }
/*     */         else {
/* 161 */           sym = createSymbol(row, i + 1);
/* 162 */           addSymbol(sym);
/* 163 */           l.add(sym);
/*     */         }
/* 165 */         this.nameToSymbols.put(row.get(1), sym);
/*     */       }
/* 167 */       this.domains = ((Symbol[])l.toArray(new Symbol[0]));
/* 168 */       NameTokenization tokenizer = new NameTokenization(this) {
/*     */         public Symbol parseToken(String str) throws IllegalSymbolException {
/* 170 */           Symbol sym = (Symbol)DomainAlphabet.this.nameToClan.get(str);
/* 171 */           if (sym == null) {
/* 172 */             return super.parseToken(str);
/*     */           }
/* 174 */           return sym;
/*     */         }
/* 176 */       };
/* 177 */       putTokenization("token", tokenizer);
/*     */       
/* 179 */       this.magicSymbol = mSymbol;
/*     */     }
/*     */     catch (Throwable e) {
/* 182 */       e.printStackTrace();
/*     */     }
/*     */   }
/*     */   
/*     */   abstract AtomicSymbol createSymbol(List paramList, int paramInt);
/*     */   
/*     */   public Set symbols() {
/* 189 */     Set l = new HashSet();
/* 190 */     Iterator i = iterator();
/* 191 */     while (i.hasNext()) {
/* 192 */       l.add(i.next());
/*     */     }
/* 194 */     return l;
/*     */   }
/*     */   
/*     */   private static Set symbols(DomainAlphabet al1, DomainAlphabet al2)
/*     */   {
/* 199 */     Set s = new HashSet(al1.symbols());
/* 200 */     s.addAll(new HashSet(al2.symbols()));
/* 201 */     return s;
/*     */   }
/*     */   
/*     */   private DomainAlphabet(Set symbols, String name)
/*     */   {
/* 206 */     super(symbols, name);
/*     */     
/* 208 */     NameTokenization tokenizer = new NameTokenization(this);
/* 209 */     putTokenization("token", tokenizer);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public abstract List magicContext(int paramInt);
/*     */   
/*     */ 
/*     */ 
/*     */   public Symbol getMagicalState()
/*     */   {
/* 220 */     return this.magicSymbol;
/*     */   }
/*     */   
/*     */ 
/*     */   public String toStr()
/*     */   {
/* 226 */     Iterator it = symbols().iterator();
/* 227 */     Annotation ann = ((Symbol)it.next()).getAnnotation();
/* 228 */     String str = new String();
/* 229 */     while (it.hasNext()) {
/* 230 */       Symbol sym = (Symbol)it.next();
/* 231 */       ann = sym.getAnnotation();
/* 232 */       Iterator it2 = ann.keys().iterator();
/* 233 */       while (it2.hasNext()) {
/* 234 */         str = str + sym.getName() + " " + ann.getProperty((String)it2.next());
/*     */       }
/* 236 */       str = str + "\n";
/*     */     }
/* 238 */     return str;
/*     */   }
/*     */   
/*     */   public String stringifySymbol(Symbol sym)
/*     */   {
/* 243 */     return (String)sym.getAnnotation().getProperty("Name");
/*     */   }
/*     */   
/*     */   public List symbolsToNameList(Collection symbols) {
/* 247 */     List l = new ArrayList();
/* 248 */     for (Iterator it = symbols.iterator(); it.hasNext();) {
/* 249 */       l.add(((Symbol)it.next()).getAnnotation().getProperty("Name"));
/*     */     }
/* 251 */     return l;
/*     */   }
/*     */   
/*     */   public SymbolList nameToSymbolList(String[] args) throws IllegalSymbolException {
/* 255 */     SimpleSymbolList sl = new SimpleSymbolList(this);
/*     */     try {
/* 257 */       for (int i = 0; i < args.length; i++) {
/* 258 */         if (!this.nameToSymbols.containsKey(args[i]))
/*     */         {
/* 260 */           throw new IllegalSymbolException("warning Alphabet does not contain this name " + args[i]);
/*     */         }
/*     */         
/*     */ 
/*     */ 
/* 265 */         sl.addSymbol((Symbol)this.nameToSymbols.get(args[i]));
/*     */       }
/* 267 */     } catch (ChangeVetoException exc) { exc.printStackTrace(); }
/* 268 */     return sl;
/*     */   }
/*     */ }


/* Location:              /home/lachlan/Desktop/psilc1.21/psilc.jar!/lc1/pfam/DomainAlphabet.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */