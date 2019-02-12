/*     */ package lc1.phyl;
/*     */ 
/*     */ import java.io.BufferedReader;
/*     */ import java.io.BufferedWriter;
/*     */ import java.io.File;
/*     */ import java.io.FileReader;
/*     */ import java.io.FileWriter;
/*     */ import java.io.PrintStream;
/*     */ import java.io.PrintWriter;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.Comparator;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import java.util.SortedMap;
/*     */ import java.util.TreeMap;
/*     */ import lc1.util.SheetIO;
/*     */ import org.biojava.bio.Annotation;
/*     */ import org.biojava.bio.BioException;
/*     */ import org.biojava.bio.seq.Sequence;
/*     */ import org.biojava.bio.seq.SequenceIterator;
/*     */ import org.biojava.bio.seq.io.SeqIOTools;
/*     */ import org.biojava.bio.symbol.Location;
/*     */ import org.biojava.bio.symbol.LocationTools;
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
/*     */ public class ScopTree
/*     */ {
/*  54 */   SortedMap scop2astral = new TreeMap();
/*     */   
/*     */ 
/*     */   private void pfamToPdb(File pdbmap)
/*     */     throws Exception, Exception, Exception
/*     */   {
/*  60 */     for (Iterator it = SheetIO.read(pdbmap, ";"); it.hasNext();) {
/*  61 */       List row = (List)it.next();
/*     */       
/*  63 */       String pdb_id = ((String)row.get(0)).trim();
/*  64 */       String chain = ((String)row.get(1)).trim();
/*  65 */       String[] loc_pdb = ((String)row.get(2)).trim().split("-");
/*  66 */       String pfam = ((String)row.get(3)).trim();
/*  67 */       if (!pfam.startsWith("Pfam-B")) {
/*  68 */         Location location = LocationTools.makeLocation(Integer.parseInt(loc_pdb[0]), Integer.parseInt(loc_pdb[1]));
/*  69 */         Collection pdbL = (Collection)this.pfam2pdb.get(pfam);
/*  70 */         if (pdbL == null) {
/*  71 */           pdbL = new ArrayList();
/*  72 */           this.pfam2pdb.put(pfam, pdbL);
/*     */         }
/*  74 */         pdbL.add(new Keys(pdb_id, chain, location));
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*  79 */   private void astralToScop(SequenceIterator astral_seq) throws BioException { while (astral_seq.hasNext()) {
/*  80 */       Sequence seq = astral_seq.nextSequence();
/*  81 */       String[] row = ((String)seq.getAnnotation().getProperty("description")).split("\\s+");
/*     */       
/*  83 */       String astral = seq.getName();
/*  84 */       String scop_id = row[1];
/*     */       
/*  86 */       Collection astralL = (Collection)this.scop2astral.get(scop_id);
/*  87 */       if (astralL == null) {
/*  88 */         astralL = new ArrayList();
/*  89 */         this.scop2astral.put(scop_id, astralL);
/*     */       }
/*  91 */       astralL.add(astral);
/*     */     }
/*     */   }
/*     */   
/*     */   private void pdbToscop(Iterator it)
/*     */     throws Exception, Exception
/*     */   {
/*  98 */     while (it.hasNext()) {
/*  99 */       List row = (List)it.next();
/* 100 */       if (!((String)row.get(0)).startsWith("#"))
/*     */       {
/* 102 */         String astral = ((String)row.get(0)).trim();
/* 103 */         String pdb_id = ((String)row.get(1)).trim();
/* 104 */         String locString = ((String)row.get(2)).trim();
/* 105 */         String[] pos = { locString.indexOf(',') >= 0 ? locString.split(",") : locString };
/* 106 */         String scop_id = ((String)row.get(3)).trim();
/*     */         
/* 108 */         for (int i = 0; i < pos.length; i++)
/*     */         {
/* 110 */           Location chain_loc = null;
/* 111 */           String chain; if (pos[i].indexOf(':') > 0) {
/* 112 */             String[] loc = pos[i].trim().split(":");
/* 113 */             String chain = loc[0];
/* 114 */             if (chain.equals("-")) { chain = "";
/*     */             }
/* 116 */             if ((loc.length > 1) && (loc[1].length() > 1)) {
/* 117 */               int index = loc[1].indexOf('-', 1);
/*     */               
/* 119 */               int min = 0;
/*     */               try {
/* 121 */                 min = Integer.parseInt(loc[1].substring(0, index));
/*     */               } catch (Exception exc) {
/* 123 */                 exc.printStackTrace();
/* 124 */                 min = Integer.parseInt(loc[1].substring(0, index - 1));
/*     */               }
/* 126 */               int max = Integer.MAX_VALUE;
/*     */               try {
/* 128 */                 max = Integer.parseInt(loc[1].substring(index + 1));
/*     */               } catch (Exception exc) {
/* 130 */                 exc.printStackTrace();
/* 131 */                 max = Integer.parseInt(loc[1].substring(index + 1, loc[1].length() - 1));
/*     */               }
/* 133 */               chain_loc = LocationTools.makeLocation(min, max);
/*     */             }
/*     */           }
/*     */           else {
/* 137 */             chain = "";
/* 138 */             if (!pos[0].equals("-")) {
/* 139 */               int index = pos[0].indexOf('-', 1);
/*     */               
/*     */ 
/* 142 */               int min = 0;
/*     */               try {
/* 144 */                 min = Integer.parseInt(pos[0].substring(0, index));
/*     */               } catch (Exception exc) {
/* 146 */                 exc.printStackTrace();
/* 147 */                 min = Integer.parseInt(pos[0].substring(0, index - 1));
/*     */               }
/* 149 */               int max = Integer.MAX_VALUE;
/*     */               try {
/* 151 */                 max = Integer.parseInt(pos[0].substring(index + 1));
/*     */               } catch (Exception exc) {
/* 153 */                 exc.printStackTrace();
/* 154 */                 max = Integer.parseInt(pos[0].substring(index + 1, pos[0].length() - 1));
/*     */               }
/* 156 */               chain_loc = LocationTools.makeLocation(min, max);
/*     */             }
/*     */           }
/* 159 */           Keys key = new Keys(pdb_id, chain, chain_loc);
/* 160 */           this.pdb2scop.put(key, scop_id);
/*     */         }
/*     */         
/* 163 */         Collection astralL = (Collection)this.scop2astral.get(scop_id);
/* 164 */         if (astralL == null) {
/* 165 */           astralL = new ArrayList();
/* 166 */           this.scop2astral.put(scop_id, astralL);
/*     */         }
/* 168 */         astralL.add(astral);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/* 173 */   static class Keys { public Keys(String pdb_id, String chain, Location chain_loc) { this.pdb_id = pdb_id;
/* 174 */       this.chain = chain;
/* 175 */       this.chain_loc = chain_loc; }
/*     */     
/* 177 */     Double zero = new Double(0.0D);
/* 178 */     Double one = new Double(1.0D);
/*     */     String pdb_id;
/*     */     String chain;
/*     */     Location chain_loc;
/*     */     
/* 183 */     public String toString() { return this.pdb_id + " " + this.chain + " " + this.chain_loc.toString(); }
/*     */     
/*     */     public Double overlaps(Keys key)
/*     */     {
/* 187 */       if (!key.pdb_id.equals(this.pdb_id)) return this.zero;
/* 188 */       if (!key.chain.equals(this.chain)) return this.zero;
/* 189 */       Location loc1 = this.chain_loc;
/* 190 */       Location loc2 = key.chain_loc;
/* 191 */       if ((loc1 == null) || (loc2 == null)) return this.one;
/* 192 */       if (loc1.overlaps(loc2)) {
/* 193 */         Location intersection = loc1.intersection(loc2);
/* 194 */         Double result = new Double((intersection.getMax() - intersection.getMin()) / (this.chain_loc.getMax() - this.chain_loc.getMin()));
/* 195 */         return result;
/*     */       }
/* 197 */       return this.zero;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/* 202 */   Comparator SCOP_COMP = new Comparator() {
/*     */     public int compare(Object o1, Object o2) {
/* 204 */       ScopTree.Keys ob1 = (ScopTree.Keys)o1;
/* 205 */       ScopTree.Keys ob2 = (ScopTree.Keys)o2;
/* 206 */       String pdb_id1 = ob1.pdb_id;
/* 207 */       String pdb_id2 = ob2.pdb_id;
/* 208 */       if (!pdb_id1.equals(pdb_id2)) return pdb_id1.compareTo(pdb_id2);
/* 209 */       String chain1 = ob1.chain;
/* 210 */       String chain2 = ob2.chain;
/* 211 */       if (!chain1.equals(chain2)) return chain1.compareTo(chain2);
/* 212 */       Location loc1 = ob1.chain_loc;
/* 213 */       Location loc2 = ob2.chain_loc;
/* 214 */       if (loc1 != loc2) {
/* 215 */         if (loc1 == null) return -1;
/* 216 */         if (loc2 == null) return 1;
/* 217 */         int min1 = loc1.getMin();
/* 218 */         int min2 = loc2.getMin();
/* 219 */         if (min1 != min2) return min1 < min2 ? -1 : 1;
/* 220 */         int max1 = loc1.getMax();
/* 221 */         int max2 = loc2.getMax();
/* 222 */         if (max1 != max2) return max1 < max2 ? -1 : 1;
/*     */       }
/* 224 */       return 0;
/*     */     }
/*     */   };
/*     */   
/* 228 */   SortedMap pdb2scop = new TreeMap(this.SCOP_COMP);
/*     */   
/* 230 */   Map pfam2pdb = new HashMap();
/*     */   
/*     */   public ScopTree(File pdbmap, File scop, File astral) throws Exception {
/* 233 */     pdbToscop(SheetIO.read(scop, "\t"));
/* 234 */     pfamToPdb(pdbmap);
/* 235 */     astralToScop(SeqIOTools.readFastaProtein(new BufferedReader(new FileReader(astral))));
/*     */   }
/*     */   
/*     */ 
/*     */   public static void main(String[] args)
/*     */     throws Exception
/*     */   {
/* 242 */     ScopTree st = new ScopTree(new File(args[0]), new File(args[1]), new File(args[2]));
/* 243 */     Set astral_ids = new HashSet();
/*     */     
/*     */ 
/* 246 */     SequenceIterator it1 = SeqIOTools.readFastaProtein(new BufferedReader(new FileReader(new File(args[2]))));
/* 247 */     while (it1.hasNext()) {
/* 248 */       astral_ids.add(it1.nextSequence().getName());
/*     */     }
/*     */     
/* 251 */     PrintWriter totals = new PrintWriter(new BufferedWriter(new FileWriter("scop_totals")));
/* 252 */     File outDir = new File(args[3]);
/* 253 */     Set done = new HashSet();
/* 254 */     for (Iterator it = st.pfam2pdb.keySet().iterator(); (it.hasNext()) && (done.size() < st.pfam2pdb.keySet().size());)
/*     */     {
/* 256 */       String domain = (String)it.next();
/* 257 */       done.add(domain);
/*     */       
/*     */ 
/* 260 */       Collection sf = st.getAstralForPfam(domain, 0);
/*     */       
/* 262 */       if (sf != null) {
/* 263 */         sf.retainAll(astral_ids);
/*     */         
/* 265 */         Collection fold = st.getAstralForPfam(domain, 1);
/* 266 */         if (fold != null)
/* 267 */           fold.retainAll(astral_ids);
/* 268 */         totals.println(domain + "\t" + sf.size() + "\t" + (astral_ids.size() - fold.size()));
/* 269 */         if (fold != null) fold.removeAll(sf);
/* 270 */         if (sf.size() != 0) {
/* 271 */           PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(new File(outDir, domain))));
/*     */           
/* 273 */           for (Iterator it1 = sf.iterator(); it1.hasNext();) {
/* 274 */             pw.println(it1.next());
/*     */           }
/* 276 */           pw.println("#fold");
/* 277 */           if (fold != null) {
/* 278 */             for (Iterator it1 = fold.iterator(); it1.hasNext();) {
/* 279 */               pw.println(it1.next());
/*     */             }
/*     */           }
/* 282 */           totals.flush();
/* 283 */           pw.close();
/*     */         }
/*     */       } }
/* 286 */     totals.close();
/*     */   }
/*     */   
/*     */   SortedMap getOverlappingKeys(Keys pdbst) {
/* 290 */     Iterator tailMap = this.pdb2scop.tailMap(pdbst).keySet().iterator();
/* 291 */     SortedMap l = new TreeMap();
/* 292 */     while (tailMap.hasNext()) {
/* 293 */       Keys key = (Keys)tailMap.next();
/* 294 */       Double overl = pdbst.overlaps(key);
/*     */       
/* 296 */       if (overl.doubleValue() <= 0.0D) break;
/* 297 */       while (l.containsKey(overl)) {
/* 298 */         overl = new Double(overl.doubleValue() + Math.random() * 0.1D);
/*     */       }
/* 300 */       l.put(overl, key); continue;
/*     */       
/*     */ 
/* 303 */       break;
/*     */     }
/*     */     
/* 306 */     SortedMap headMap = this.pdb2scop.headMap(pdbst);
/* 307 */     label252: while (headMap.size() > 0)
/*     */     {
/* 309 */       Keys key = (Keys)(headMap.size() == 1 ? headMap.keySet().iterator().next() : headMap.lastKey());
/* 310 */       Double overl = pdbst.overlaps(key);
/* 311 */       if (overl.doubleValue() <= 0.0D) break;
/* 312 */       while (l.containsKey(overl)) {
/* 313 */         overl = new Double(overl.doubleValue() + Math.random() * 0.1D);
/*     */       }
/* 315 */       l.put(overl, key);
/*     */       
/*     */       break label252;
/* 318 */       break;
/*     */       
/* 320 */       headMap = headMap.headMap(key);
/*     */     }
/* 322 */     return l;
/*     */   }
/*     */   
/*     */   public Collection getAstralForPfam(String domain, int level) {
/* 326 */     Collection pdb = (Collection)this.pfam2pdb.get(domain);
/*     */     
/*     */ 
/* 329 */     Collection result = new HashSet();
/* 330 */     for (Iterator it = pdb.iterator(); it.hasNext();) {
/* 331 */       Object pdbst = it.next();
/* 332 */       SortedMap l = getOverlappingKeys((Keys)pdbst);
/* 333 */       if (l.size() != 0) {
/* 334 */         Keys repr = (Keys)l.get(l.lastKey());
/* 335 */         String scop_id = (String)this.pdb2scop.get(repr);
/*     */         
/*     */ 
/* 338 */         for (Iterator i = l.keySet().iterator(); i.hasNext();) {
/* 339 */           Double overl = (Double)i.next();
/* 340 */           Keys pdbkey = (Keys)l.get(overl);
/* 341 */           if ((pdbkey != repr) && 
/* 342 */             (differentSuperFamily(scop_id, (String)this.pdb2scop.get(pdbkey))) && (overl.doubleValue() > 0.1D)) {
/* 343 */             System.err.println("disallowing " + domain + " " + 
/* 344 */               repr + " " + pdbkey + " " + pdbst + " " + overl);
/* 345 */             return null;
/*     */           }
/*     */         }
/* 348 */         String head = new String(scop_id);
/* 349 */         for (int i = 0; i <= level; i++) {
/* 350 */           head = head.substring(0, head.lastIndexOf('.'));
/*     */         }
/* 352 */         head = head + ".";
/* 353 */         String tail = head + "Z";
/*     */         
/* 355 */         Map c = this.scop2astral.tailMap(head).headMap(tail);
/* 356 */         for (Iterator i1 = c.values().iterator(); i1.hasNext();) {
/* 357 */           result.addAll((Collection)i1.next());
/*     */         }
/*     */       }
/*     */     }
/* 361 */     if (result.size() == 0) return null;
/* 362 */     return result;
/*     */   }
/*     */   
/*     */   public boolean differentSuperFamily(String id1, String id2) {
/* 366 */     String[] st1 = id1.split("\\.");
/* 367 */     String[] st2 = id2.split("\\.");
/*     */     boolean res;
/* 369 */     boolean res; if ((st1[0].equals(st2[0])) && (st1[1].equals(st2[1]))) res = false; else
/* 370 */       res = true;
/* 371 */     return res;
/*     */   }
/*     */ }


/* Location:              /home/lachlan/Desktop/psilc1.21/psilc.jar!/lc1/phyl/ScopTree.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */