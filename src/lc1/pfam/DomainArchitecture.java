/*     */ package lc1.pfam;
/*     */ 
/*     */ import java.io.BufferedReader;
/*     */ import java.io.BufferedWriter;
/*     */ import java.io.File;
/*     */ import java.io.FileReader;
/*     */ import java.io.FileWriter;
/*     */ import java.io.PrintStream;
/*     */ import java.io.PrintWriter;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collection;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import java.util.SortedMap;
/*     */ import java.util.TreeMap;
/*     */ import lc1.domainseq.Domain;
/*     */ import lc1.util.SheetIO;
/*     */ import org.biojava.bio.seq.Sequence;
/*     */ import org.biojava.bio.symbol.Location;
/*     */ import org.biojava.bio.symbol.SimpleSymbolList;
/*     */ import org.biojava.bio.symbol.Symbol;
/*     */ import org.biojava.bio.symbol.SymbolList;
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
/*     */ public class DomainArchitecture
/*     */ {
/*  44 */   IndexedPfamDB[] db = new IndexedPfamDB[3];
/*  45 */   String[] dbNames = { "ls", "fs", "context" };
/*  46 */   Map archToPfamseqId = new HashMap();
/*     */   
/*     */   PfamAlphabet alph;
/*     */   
/*     */ 
/*     */   public static void main(String[] args)
/*     */     throws Exception
/*     */   {
/*  54 */     File arch = new File(args[4]);
/*  55 */     File pfamseq_arch = new File(args[5]);
/*  56 */     if ((pfamseq_arch.exists()) && (pfamseq_arch.length() > 0L)) throw new Exception("output file exists");
/*  57 */     if ((arch.exists()) && (arch.length() > 0L)) throw new Exception("output file exists");
/*  58 */     DomainArchitecture domArch = new DomainArchitecture(new File(args[0]), 
/*  59 */       new File[] { new File(args[1]), new File(args[2]), new File(args[3]) });
/*  60 */     domArch.print(new PrintWriter(new BufferedWriter(new FileWriter(arch))), 
/*  61 */       new PrintWriter(new BufferedWriter(new FileWriter(pfamseq_arch))));
/*     */   }
/*     */   
/*     */   public static void getAMLines(String repos, String output) throws Exception
/*     */   {
/*  66 */     File rep = new File(repos);
/*  67 */     PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(new File(output))));
/*  68 */     for (Iterator it = Arrays.asList(rep.listFiles()).iterator(); it.hasNext();) {
/*  69 */       File pfam = (File)it.next();
/*  70 */       System.err.println(pfam);
/*  71 */       BufferedReader br = new BufferedReader(new FileReader(new File(pfam, "DESC")));
/*  72 */       String s = "";
/*  73 */       while (!(s = br.readLine()).startsWith("AM")) {}
/*     */       
/*     */ 
/*  76 */       out.println(pfam.getName() + "\t" + s.split("\\s+")[1]);
/*     */     }
/*  78 */     out.close();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public DomainArchitecture(File pfamA, File[] pfamA_reg_full)
/*     */     throws Exception
/*     */   {
/*  88 */     this.alph = PfamAlphabet.makeAlphabet(SheetIO.read(pfamA, "\\t"), SheetIO.read(new File(pfamA.getParentFile(), "clans"), "\\t"));
/*  89 */     System.err.println(this.alph.size());
/*  90 */     for (int i = 0; i < pfamA_reg_full.length; i++) {
/*  91 */       System.err.println(i + " " + pfamA_reg_full[i] + " " + this.alph);
/*  92 */       this.db[i] = new IndexedPfamDB(pfamA_reg_full[i], 
/*  93 */         this.alph, 
/*  94 */         "\t", 0, 1);
/*     */     }
/*  96 */     fillArchitectureMap();
/*     */   }
/*     */   
/*     */   private void print(PrintWriter arch, PrintWriter pfamseq_arch) {
/* 100 */     int i = 0;
/* 101 */     for (Iterator it = this.archToPfamseqId.keySet().iterator(); it.hasNext(); i++) {
/* 102 */       SymbolList sl = (SymbolList)it.next();
/* 103 */       arch.println(i + "\t" + sl.seqString());
/* 104 */       Collection ids = (Collection)this.archToPfamseqId.get(sl);
/* 105 */       for (Iterator it1 = ids.iterator(); it1.hasNext();) {
/* 106 */         pfamseq_arch.println(it1.next() + "\t" + i);
/*     */       }
/*     */     }
/*     */     
/* 110 */     arch.close();
/* 111 */     pfamseq_arch.close();
/*     */   }
/*     */   
/*     */   private void fillArchitectureMap() throws Exception {
/* 115 */     Set ids = new HashSet(this.db[0].ids());
/* 116 */     ids.addAll(this.db[1].ids());ids.addAll(this.db[2].ids());
/* 117 */     int k = 0;
/* 118 */     for (Iterator it = ids.iterator(); it.hasNext(); k++) {
/* 119 */       String id = (String)it.next();
/* 120 */       SortedMap locToDomain = new TreeMap(Location.naturalOrder);
/* 121 */       for (int i = 0; i < this.db.length; i++) {
/* 122 */         Sequence seq = this.db[i].getSequence(id);
/* 123 */         if (seq != null) {
/* 124 */           for (Iterator feat = seq.features(); feat.hasNext();) {
/* 125 */             Domain dom = (Domain)feat.next();
/* 126 */             Location loc = dom.getLocation();
/* 127 */             SortedMap headMap = locToDomain.headMap(loc);
/* 128 */             SortedMap tailMap = locToDomain.tailMap(loc);
/* 129 */             if (((tailMap.size() == 0) || (!loc.overlaps((Location)tailMap.firstKey()))) && (
/* 130 */               (headMap.size() == 0) || (!loc.overlaps((Location)headMap.lastKey())))) {
/* 131 */               locToDomain.put(dom.getLocation(), dom.getSymbol());
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */       
/*     */ 
/*     */ 
/* 139 */       Symbol[] sym = new Symbol[locToDomain.keySet().size()];
/*     */       
/* 141 */       locToDomain.values().toArray(sym);
/* 142 */       SymbolList sl = new SimpleSymbolList(this.alph, Arrays.asList(sym));
/* 143 */       if (Math.IEEEremainder(k, 100.0D) == 0.0D) System.err.println(k + "\t");
/*     */       Collection idSet;
/*     */       Collection idSet;
/* 146 */       if (this.archToPfamseqId.containsKey(sl)) {
/* 147 */         idSet = (Collection)this.archToPfamseqId.get(sl);
/*     */       } else {
/* 149 */         idSet = new ArrayList();
/* 150 */         this.archToPfamseqId.put(sl, idSet);
/*     */       }
/* 152 */       idSet.add(id);
/*     */     }
/*     */   }
/*     */ }


/* Location:              /home/lachlan/Desktop/psilc1.21/psilc.jar!/lc1/pfam/DomainArchitecture.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */