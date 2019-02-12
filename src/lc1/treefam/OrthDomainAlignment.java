/*     */ package lc1.treefam;
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
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Properties;
/*     */ import java.util.Set;
/*     */ import java.util.SortedMap;
/*     */ import java.util.TreeMap;
/*     */ import lc1.pfam.DomainAlphabet;
/*     */ import lc1.pfam.PfamAlignmentParser;
/*     */ import lc1.pfam.PfamAlphabet;
/*     */ import lc1.util.SheetIO;
/*     */ import org.biojava.bio.Annotation;
/*     */ import org.biojava.bio.seq.io.SymbolTokenization;
/*     */ import org.biojava.bio.symbol.Symbol;
/*     */ import pal.alignment.Alignment;
/*     */ import pal.alignment.AlignmentUtils;
/*     */ import pal.alignment.SimpleAlignment;
/*     */ import pal.datatype.AminoAcids;
/*     */ import pal.datatype.DataType;
/*     */ import pal.misc.Identifier;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ class OrthDomainAlignment
/*     */ {
/*     */   Iterator architecture;
/*     */   File aligns;
/*     */   File repository;
/*     */   DomainAlphabet alph;
/*     */   SqlQuery query;
/*     */   
/*     */   public static void main(String[] args)
/*     */     throws Exception
/*     */   {
/* 276 */     OrthDomainAlignment od = new OrthDomainAlignment(new File(args[0]), new File(args[1]), new File(args[2]), new File(args[3]));
/* 277 */     od.printAlignmentToFiles();
/*     */   }
/*     */   
/*     */   public void printAlignmentToFiles()
/*     */     throws Exception
/*     */   {
/* 283 */     while (this.architecture.hasNext()) {
/*     */       try {
/* 285 */         Object[] obj = (Object[])this.architecture.next();
/* 286 */         System.err.println(Arrays.asList((String[])obj[0]));
/* 287 */         List idList = new ArrayList();
/* 288 */         List pfamList = new ArrayList();
/* 289 */         for (Iterator pfamseq_it = (Iterator)obj[1]; pfamseq_it.hasNext();) {
/* 290 */           Object obj1 = pfamseq_it.next();
/* 291 */           if (obj1 == null) break;
/* 292 */           System.err.println(obj1);
/* 293 */           String[] st1 = ((String)obj1).split("\t");
/* 294 */           pfamList.add(st1[0]);
/* 295 */           idList.addAll(this.query.getTaxonomyId(st1[0]));
/*     */         }
/* 297 */         String[] sl = (String[])obj[0];
/* 298 */         String id = ((String)obj[2]).split("\\s+")[0];
/* 299 */         File f = new File(this.aligns, id);
/* 300 */         if (((!f.exists()) || (f.length() <= 0L)) && 
/* 301 */           (pfamList.size() > 3))
/*     */         {
/* 303 */           Identifier[] ids = new Identifier[idList.size()];
/* 304 */           idList.toArray(ids);
/* 305 */           String[] pfamL = new String[pfamList.size()];
/* 306 */           pfamList.toArray(pfamL);
/* 307 */           Alignment[] align = getAlignment(ids, pfamL, sl, id);
/* 308 */           if ((align != null) && (align[0] != null) && (align[0].getIdCount() >= 3)) {
/* 309 */             for (int i = 0; i < align.length; i++) {
/* 310 */               PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(f.getAbsolutePath() + "_" + i)));
/* 311 */               AlignmentUtils.print(align[i], pw);
/* 312 */               pw.close();
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */       catch (Exception exc) {
/* 318 */         exc.printStackTrace();
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static Iterator getArchitectureIterator(File pfamseq_architecture, File architecture)
/*     */     throws Exception
/*     */   {
/* 328 */     BufferedReader pf_arch = new BufferedReader(new FileReader(pfamseq_architecture));
/* 329 */     Iterator arch = SheetIO.read(architecture, "\t");
/* 330 */     new Iterator()
/*     */     {
/*     */ 
/* 333 */       public boolean hasNext() { return OrthDomainAlignment.this.hasNext(); }
/*     */       
/*     */       public void remove() {}
/*     */       
/*     */       public Object next() {
/* 338 */         try { List nextArch = (List)OrthDomainAlignment.this.next();
/* 339 */           String id = (String)nextArch.get(0);
/* 340 */           int id_i = Integer.parseInt(id);
/* 341 */           String[] st = ((String)nextArch.get(1)).split(" ");
/* 342 */           Iterator previous_iterator = new OrthDomainAlignment.2(this, this.val$pf_arch, id_i);
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
/* 376 */           return new Object[] { st, previous_iterator, id };
/*     */         } catch (Exception exc) {
/* 378 */           exc.printStackTrace(); }
/* 379 */         return null;
/*     */       }
/*     */     };
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   OrthDomainAlignment(File repos, File architectureF, File pfamseq_architecture, File pfam_sheet)
/*     */     throws Exception
/*     */   {
/* 393 */     this.repository = repos;
/* 394 */     this.aligns = new File("overallAlign");
/* 395 */     this.alph = PfamAlphabet.makeAlphabet(repos);
/* 396 */     this.architecture = getArchitectureIterator(pfamseq_architecture, architectureF);
/*     */     
/* 398 */     Properties props = new Properties();
/* 399 */     props.setProperty("host", "pfam");
/* 400 */     props.setProperty("database", "pfam");
/* 401 */     props.setProperty("password", "mafp1");
/* 402 */     props.setProperty("user", "pfam");
/* 403 */     this.query = new SqlQuery(props);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private Alignment[] getAlignment(Identifier[] ids, String[] pfamList, String[] sl, String id)
/*     */     throws Exception
/*     */   {
/* 416 */     Map slMap = new HashMap();
/* 417 */     for (int i = 0; i < sl.length; i++) {
/* 418 */       if (slMap.containsKey(sl[i])) slMap.put(sl[i], 
/* 419 */           new Integer(((Integer)slMap.get(sl[i])).intValue() + 1)); else {
/* 420 */         slMap.put(sl[i], new Integer(1));
/*     */       }
/*     */     }
/*     */     
/* 424 */     DataType dt = AminoAcids.DEFAULT_INSTANCE;
/* 425 */     Map aligns = new HashMap();
/* 426 */     int currentStart = 0;
/* 427 */     Object[] keys = slMap.keySet().toArray();
/* 428 */     for (int i = 0; i < keys.length; i++) {
/* 429 */       Symbol sym = this.alph.getTokenization("token").parseToken((String)keys[i]);
/* 430 */       Alignment alignPfam = PfamAlignmentParser.parse(new File(this.repository, 
/* 431 */         sym.getAnnotation().getProperty("pfamA_id") + 
/* 432 */         "/ALIGN"), 
/* 433 */         "amino");
/* 434 */       Alignment[] align = new Alignment[((Integer)slMap.get(keys[i])).intValue()];
/*     */       
/*     */ 
/* 437 */       for (int k = 0; k < ((Integer)slMap.get(keys[i])).intValue(); k++) {
/* 438 */         String[] seqs = new String[ids.length];
/* 439 */         for (int j = 0; j < ids.length; j++) {
/* 440 */           String pfam_id = pfamList[j];
/*     */           
/* 442 */           SortedMap startToIdent = new TreeMap();
/* 443 */           for (int l = 0; l < alignPfam.getIdCount(); l++) {
/* 444 */             if (alignPfam.getIdentifier(l).getName().startsWith(pfam_id)) {
/* 445 */               Integer start = new Integer(Integer.parseInt(alignPfam.getIdentifier(l).getName().split("/")[1].split("-")[0]));
/* 446 */               startToIdent.put(start, new Integer(l));
/*     */             }
/*     */           }
/*     */           
/* 450 */           Iterator startIt = startToIdent.values().iterator();
/*     */           
/* 452 */           for (int count = 0; count < k; count++)
/*     */           {
/* 454 */             startIt.next();
/*     */           }
/* 456 */           seqs[j] = alignPfam.getAlignedSequenceString(((Integer)startIt.next()).intValue());
/*     */         }
/* 458 */         if (ids.length == 0) return null;
/* 459 */         align[k] = new SimpleAlignment(ids, seqs, dt);
/*     */       }
/*     */       
/* 462 */       aligns.put(keys[i], align);
/*     */     }
/* 464 */     Alignment[] align = new Alignment[sl.length];
/* 465 */     slMap = new HashMap();
/* 466 */     for (int i = 0; i < sl.length; i++) {
/* 467 */       if (slMap.containsKey(sl[i])) slMap.put(sl[i], new Integer(((Integer)slMap.get(sl[i])).intValue() + 1)); else {
/* 468 */         slMap.put(sl[i], new Integer(0));
/*     */       }
/* 470 */       align[i] = ((Alignment[])aligns.get(sl[i]))[((Integer)slMap.get(sl[i])).intValue()];
/*     */     }
/* 472 */     return align;
/*     */   }
/*     */ }


/* Location:              /home/lachlan/Desktop/psilc1.21/psilc.jar!/lc1/treefam/OrthDomainAlignment.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */