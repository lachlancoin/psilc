/*     */ package lc1.pfam;
/*     */ 
/*     */ import java.io.PrintStream;
/*     */ import java.sql.ResultSet;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Comparator;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Properties;
/*     */ import java.util.Set;
/*     */ import lc1.domainseq.Domain.Template;
/*     */ import lc1.domainseq.DomainList;
/*     */ import org.biojava.bio.Annotation;
/*     */ import org.biojava.bio.seq.Sequence;
/*     */ import org.biojava.bio.seq.db.SequenceDB;
/*     */ import org.biojava.bio.seq.io.NameTokenization;
/*     */ import org.biojava.bio.symbol.Location;
/*     */ import org.biojava.bio.symbol.LocationTools;
/*     */ import org.biojava.utils.ChangeEvent;
/*     */ import org.biojava.utils.ChangeListener;
/*     */ import org.biojava.utils.ChangeSupport;
/*     */ import org.biojava.utils.ChangeType;
/*     */ import org.biojava.utils.ChangeVetoException;
/*     */ import org.biojava.utils.Changeable;
/*     */ import org.biojava.utils.SmallMap;
/*     */ import org.biojava.utils.SmallSet;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class PfamDB
/*     */   implements TiedSequenceIterator
/*     */ {
/*     */   PfamDBInner db;
/*     */   DomainAlphabet alpha;
/*     */   Properties params;
/*     */   String[] select;
/*     */   String[] protein_select;
/*     */   String restrictions;
/*     */   String prot_rest;
/*     */   boolean ls;
/*     */   boolean pfamB;
/*     */   SequenceDB seqDB;
/*  47 */   private boolean incrFlag = false;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private PfamDB(Properties params, String[] select, String[] protein_select, String restrictions, String prot_rest, boolean ls, boolean ortho, boolean pfamB)
/*     */   {
/*  59 */     changeSupport = new ChangeSupport();
/*  60 */     this.pfamB = pfamB;
/*  61 */     this.select = select;
/*  62 */     this.protein_select = protein_select;
/*  63 */     this.restrictions = restrictions;
/*  64 */     this.prot_rest = prot_rest;
/*  65 */     this.params = params;
/*  66 */     this.db = makeDBInner(restrictions, prot_rest);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  74 */     this.ls = ls;
/*     */   }
/*     */   
/*  77 */   static final Comparator templateComp = new Comparator() {
/*     */     public int compare(Object o1, Object o2) {
/*  79 */       Domain.Template f1 = (Domain.Template)o1;
/*  80 */       Domain.Template f2 = (Domain.Template)o2;
/*  81 */       if (f2.symbol != f1.symbol) {
/*  82 */         return f1.symbol.hashCode() < f2.symbol.hashCode() ? -1 : 1;
/*     */       }
/*  84 */       if (f2.location.overlaps(f1.location)) {
/*  85 */         return 0;
/*     */       }
/*     */       
/*     */ 
/*  89 */       return f1.location.getMin() < f2.location.getMin() ? -1 : 1;
/*     */     }
/*     */     
/*     */     public boolean equals(Object o) {
/*  93 */       return o == this;
/*     */     }
/*     */   };
/*     */   private static transient ChangeSupport changeSupport;
/*     */   
/*     */   public static DomainList buildList(List sh, Sequence protSeq, DomainAlphabet alpha) {
/*  99 */     Map m = new SmallMap();
/* 100 */     Object pfamseq_id = null;
/* 101 */     Object species = null;
/*     */     
/*     */ 
/* 104 */     for (int i = 0; i < sh.size(); i++)
/*     */     {
/* 106 */       Object[] row = (Object[])sh.get(i);
/*     */       
/*     */ 
/* 109 */       m.put(row[1], 
/* 110 */         new Integer(m.containsKey(row[1]) ? ((Integer)m.get(row[1])).intValue() + 1 : 1));
/* 111 */       if (species == null) {
/* 112 */         species = row[7];
/* 113 */         pfamseq_id = row[6];
/*     */       }
/* 115 */       else if (!species.equals(row[7])) { System.err.println("warning different species for same protein ! " + protSeq.getName());
/*     */       }
/*     */     }
/* 118 */     try { if (pfamseq_id != null) protSeq.getAnnotation().setProperty("pfamseq_id", pfamseq_id);
/* 119 */       protSeq.getAnnotation().setProperty("species", species);
/*     */     } catch (ChangeVetoException exc) {
/* 121 */       exc.printStackTrace();
/*     */     }
/*     */     try
/*     */     {
/* 125 */       NameTokenization tokenizer = (NameTokenization)alpha.getTokenization("token");
/* 126 */       Set templates = new SmallSet();
/* 127 */       for (int i = 0; i < sh.size(); i++) {
/* 128 */         Object[] row = (Object[])sh.get(i);
/*     */         
/*     */ 
/* 131 */         Domain.Template ft = new Domain.Template();
/* 132 */         ft.symbol = tokenizer.parseToken((String)row[1]);
/* 133 */         ft.location = 
/* 134 */           LocationTools.makeLocation(((Integer)row[2]).intValue(), 
/* 135 */           ((Integer)row[3]).intValue());
/* 136 */         ft.score = ((Double)row[4]).doubleValue();
/* 137 */         ft.evalue = ((Double)row[5]).doubleValue();
/* 138 */         ft.mode = ((String)row[6]).charAt(0);
/* 139 */         templates.add(ft);
/*     */       }
/*     */       
/*     */ 
/* 143 */       Domain.Template[] fts = new Domain.Template[templates.size()];
/* 144 */       templates.toArray(fts);
/*     */       
/*     */ 
/*     */ 
/* 148 */       return new DomainList(protSeq, protSeq.getURN(), protSeq.getName(), protSeq.getAnnotation(), fts);
/*     */     }
/*     */     catch (Throwable t) {
/* 151 */       t.printStackTrace(); }
/* 152 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   PfamDBInner makeDBInner(String rest, String prot_rest)
/*     */   {
/* 159 */     return new PfamDBInner(rest);
/*     */   }
/*     */   
/*     */   public ChangeType getChangeType() {
/* 163 */     return this.db.ctype;
/*     */   }
/*     */   
/*     */   public void registerIncrementForwarder(ChangeType ct, boolean skip) {
/* 167 */     this.db.registerIncrementForwarder(ct, skip);
/*     */   }
/*     */   
/* 170 */   public boolean hasNext() { return this.db.hasNext(); }
/*     */   
/*     */   public Sequence nextSequence()
/*     */   {
/* 174 */     if (!this.incrFlag) {
/* 175 */       this.db.incrProtein();
/* 176 */       this.incrFlag = true;
/*     */     }
/* 178 */     this.incrFlag = false;
/* 179 */     return this.db.currentProtein;
/*     */   }
/*     */   
/*     */   public Sequence nextSequence(int i) {
/* 183 */     if (!this.incrFlag)
/*     */     {
/* 185 */       this.db.incrProtein(i);
/*     */     }
/* 187 */     this.incrFlag = false;
/* 188 */     return this.db.currentProtein;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   DomainList currentProtein()
/*     */   {
/* 195 */     return this.db.currentProtein;
/*     */   }
/*     */   
/*     */ 
/*     */   class PfamDBInner
/*     */     extends PfamSqlRead
/*     */     implements Changeable
/*     */   {
/*     */     DomainList currentProtein;
/*     */     
/*     */     private boolean hasNext;
/*     */     
/*     */     private transient ChangeListener incrementForwarder;
/*     */     public ChangeType ctype;
/*     */     private ChangeType ct;
/*     */     
/*     */     PfamDBInner(String rest)
/*     */     {
/* 213 */       super(PfamDB.this.select, "", rest);
/*     */       
/*     */ 
/*     */ 
/* 217 */       this.ctype = new ChangeType(getClass().getName(), getClass().getName(), "ctype");
/*     */       try {
/* 219 */         this.hasNext = this.RS.next();
/*     */       }
/*     */       catch (Throwable t) {
/* 222 */         t.printStackTrace();
/*     */       }
/*     */     }
/*     */     
/*     */     boolean hasNext() {
/* 227 */       return this.hasNext;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     void incrProtein()
/*     */     {
/* 240 */       if (!this.hasNext) {
/* 241 */         this.currentProtein = null;
/* 242 */         return;
/*     */       }
/*     */       try {
/* 245 */         boolean isNewRow = true;
/* 246 */         ArrayList current = new ArrayList();
/* 247 */         String protein = this.RS.getString(1);
/* 248 */         ChangeEvent cev = new ChangeEvent(PfamDB.this.db, this.ctype, protein);
/* 249 */         synchronized (PfamDB.changeSupport)
/*     */         {
/* 251 */           PfamDB.changeSupport.firePreChangeEvent(cev);
/* 252 */           while ((isNewRow) && (!this.closed) && (protein.equals(this.RS.getString(1)))) {
/* 253 */             Object[] row = getRow();
/* 254 */             current.add(row);
/* 255 */             isNewRow = this.RS.next();
/*     */           }
/* 257 */           this.currentProtein = buildList(current, PfamDB.this.seqDB.getSequence(protein));
/*     */         }
/* 259 */         this.hasNext = isNewRow;
/* 260 */         if (!this.hasNext) { closeConnection();
/*     */         }
/* 262 */         PfamDB.changeSupport.firePostChangeEvent(cev);
/*     */       }
/*     */       catch (Throwable t) {
/* 265 */         t.printStackTrace();
/*     */       }
/*     */     }
/*     */     
/*     */     void incrProtein(int id)
/*     */     {
/* 271 */       if (!this.hasNext)
/*     */       {
/* 273 */         this.currentProtein = null;
/* 274 */         return;
/*     */       }
/* 276 */       boolean isNewRow = true;
/*     */       try
/*     */       {
/* 279 */         while ((isNewRow) && (!this.closed) && (Integer.parseInt(this.RS.getString(1)) < id)) {
/* 280 */           isNewRow = this.RS.next();
/*     */         }
/* 282 */         if ((isNewRow) && (!this.closed) && (Integer.parseInt(this.RS.getString(1)) == id)) {
/* 283 */           incrProtein();
/*     */         } else {
/* 285 */           this.currentProtein = null;
/*     */         }
/* 287 */         this.hasNext = isNewRow;
/* 288 */         if (!this.hasNext) closeConnection();
/*     */       }
/*     */       catch (Throwable t) {
/* 291 */         t.printStackTrace();
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */     private String str(Iterator iter)
/*     */     {
/* 298 */       String stri = new String();
/* 299 */       while (iter.hasNext()) {
/* 300 */         stri = stri + ((Object[])iter.next())[1].toString() + " ";
/*     */       }
/* 302 */       return stri;
/*     */     }
/*     */     
/*     */     private DomainList buildList(ArrayList sh, Sequence protSeq) {
/* 306 */       return PfamDB.buildList(sh, protSeq, PfamDB.this.alpha);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     protected void registerIncrementForwarder(ChangeType ct, boolean skip)
/*     */     {
/* 315 */       this.ct = ct;
/*     */       
/* 317 */       if (skip)
/*     */       {
/* 319 */         this.incrementForwarder = new SkipIncrementForwarder();
/*     */       }
/*     */       else
/* 322 */         this.incrementForwarder = new NullIncrementForwarder();
/* 323 */       addChangeListener(this.incrementForwarder, ct);
/*     */     }
/*     */     
/*     */     private class NullIncrementForwarder implements ChangeListener {
/*     */       NullIncrementForwarder() {}
/*     */       
/*     */       public void preChange(ChangeEvent cev) throws ChangeVetoException {
/* 330 */         PfamDB.this.db.incrProtein();
/* 331 */         PfamDB.this.incrFlag = true;
/* 332 */         Integer.parseInt(PfamDB.this.db.currentProtein.getName());
/* 333 */         Integer.parseInt(((DomainList)cev.getChange()).getName());
/*     */       }
/*     */       
/*     */       public void postChange(ChangeEvent cev)
/*     */       {
/* 338 */         System.out.println("Wrong change ");
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */     private class SkipIncrementForwarder
/*     */       implements ChangeListener
/*     */     {
/*     */       SkipIncrementForwarder() {}
/*     */       
/*     */ 
/*     */       public void preChange(ChangeEvent cev)
/*     */         throws ChangeVetoException
/*     */       {}
/*     */       
/*     */       public void postChange(ChangeEvent cev)
/*     */       {
/* 355 */         PfamDB.PfamDBInner.this.incrProtein(Integer.parseInt((String)cev.getChange()));
/*     */         
/* 357 */         PfamDB.this.incrFlag = true;
/*     */       }
/*     */     }
/*     */     
/*     */     public void addChangeListener(ChangeListener cl)
/*     */     {
/* 363 */       addChangeListener(cl, ChangeType.UNKNOWN);
/*     */     }
/*     */     
/*     */     public void addChangeListener(ChangeListener cl, ChangeType ct) {
/* 367 */       PfamDB.changeSupport.addChangeListener(cl, ct);
/*     */     }
/*     */     
/*     */     public void removeChangeListener(ChangeListener cl) {
/* 371 */       removeChangeListener(cl, ChangeType.UNKNOWN);
/*     */     }
/*     */     
/*     */     public void removeChangeListener(ChangeListener cl, ChangeType ct) {
/* 375 */       PfamDB.changeSupport.removeChangeListener(cl, ct);
/*     */     }
/*     */     
/*     */     public boolean isUnchanging(ChangeType ct)
/*     */     {
/* 380 */       return false;
/*     */     }
/*     */   }
/*     */ }


/* Location:              /home/lachlan/Desktop/psilc1.21/psilc.jar!/lc1/pfam/PfamDB.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */