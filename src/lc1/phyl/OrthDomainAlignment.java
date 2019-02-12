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
/* 275 */     OrthDomainAlignment od = new OrthDomainAlignment(new File(args[0]), new File(args[1]), new File(args[2]), new File(args[3]));
/* 276 */     od.printAlignmentToFiles();
/*     */   }
/*     */   
/*     */   public void printAlignmentToFiles()
/*     */     throws Exception
/*     */   {
/* 282 */     while (this.architecture.hasNext()) {
/*     */       try {
/* 284 */         Object[] obj = (Object[])this.architecture.next();
/* 285 */         System.err.println(Arrays.asList((String[])obj[0]));
/* 286 */         List idList = new ArrayList();
/* 287 */         List pfamList = new ArrayList();
/* 288 */         for (Iterator pfamseq_it = (Iterator)obj[1]; pfamseq_it.hasNext();) {
/* 289 */           Object obj1 = pfamseq_it.next();
/* 290 */           if (obj1 == null) break;
/* 291 */           System.err.println(obj1);
/* 292 */           String[] st1 = ((String)obj1).split("\t");
/* 293 */           pfamList.add(st1[0]);
/* 294 */           idList.addAll(this.query.getTaxonomyId(st1[0]));
/*     */         }
/* 296 */         String[] sl = (String[])obj[0];
/* 297 */         String id = ((String)obj[2]).split("\\s+")[0];
/* 298 */         File f = new File(this.aligns, id);
/* 299 */         if (((!f.exists()) || (f.length() <= 0L)) && 
/* 300 */           (pfamList.size() > 3))
/*     */         {
/* 302 */           Identifier[] ids = new Identifier[idList.size()];
/* 303 */           idList.toArray(ids);
/* 304 */           String[] pfamL = new String[pfamList.size()];
/* 305 */           pfamList.toArray(pfamL);
/* 306 */           Alignment[] align = getAlignment(ids, pfamL, sl, id);
/* 307 */           if ((align != null) && (align[0] != null) && (align[0].getIdCount() >= 3)) {
/* 308 */             for (int i = 0; i < align.length; i++) {
/* 309 */               PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(f.getAbsolutePath() + "_" + i)));
/* 310 */               AlignmentUtils.print(align[i], pw);
/* 311 */               pw.close();
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */       catch (Exception exc) {
/* 317 */         exc.printStackTrace();
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static Iterator getArchitectureIterator(File pfamseq_architecture, File architecture)
/*     */     throws Exception
/*     */   {
/* 327 */     BufferedReader pf_arch = new BufferedReader(new FileReader(pfamseq_architecture));
/* 328 */     Iterator arch = SheetIO.read(architecture, "\t");
/* 329 */     new Iterator()
/*     */     {
/*     */ 
/* 332 */       public boolean hasNext() { return OrthDomainAlignment.this.hasNext(); }
/*     */       
/*     */       public void remove() {}
/*     */       
/*     */       public Object next() {
/* 337 */         try { List nextArch = (List)OrthDomainAlignment.this.next();
/* 338 */           String id = (String)nextArch.get(0);
/* 339 */           int id_i = Integer.parseInt(id);
/* 340 */           String[] st = ((String)nextArch.get(1)).split(" ");
/* 341 */           Iterator previous_iterator = new OrthDomainAlignment.2(this, this.val$pf_arch, id_i);
/*     */           
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 375 */           return new Object[] { st, previous_iterator, id };
/*     */         } catch (Exception exc) {
/* 377 */           exc.printStackTrace(); }
/* 378 */         return null;
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
/* 392 */     this.repository = repos;
/* 393 */     this.aligns = new File("overallAlign");
/* 394 */     this.alph = PfamAlphabet.makeAlphabet(repos);
/* 395 */     this.architecture = getArchitectureIterator(pfamseq_architecture, architectureF);
/*     */     
/* 397 */     Properties props = new Properties();
/* 398 */     props.setProperty("host", "pfam");
/* 399 */     props.setProperty("database", "pfam");
/* 400 */     props.setProperty("password", "mafp1");
/* 401 */     props.setProperty("user", "pfam");
/* 402 */     this.query = new SqlQuery(props);
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
/* 415 */     Map slMap = new HashMap();
/* 416 */     for (int i = 0; i < sl.length; i++) {
/* 417 */       if (slMap.containsKey(sl[i])) slMap.put(sl[i], 
/* 418 */           new Integer(((Integer)slMap.get(sl[i])).intValue() + 1)); else {
/* 419 */         slMap.put(sl[i], new Integer(1));
/*     */       }
/*     */     }
/*     */     
/* 423 */     DataType dt = AminoAcids.DEFAULT_INSTANCE;
/* 424 */     Map aligns = new HashMap();
/* 425 */     int currentStart = 0;
/* 426 */     Object[] keys = slMap.keySet().toArray();
/* 427 */     for (int i = 0; i < keys.length; i++) {
/* 428 */       Symbol sym = this.alph.getTokenization("token").parseToken((String)keys[i]);
/* 429 */       Alignment alignPfam = PfamAlignmentParser.parse(new File(this.repository, 
/* 430 */         sym.getAnnotation().getProperty("pfamA_id") + 
/* 431 */         "/ALIGN"), 
/* 432 */         "amino");
/* 433 */       Alignment[] align = new Alignment[((Integer)slMap.get(keys[i])).intValue()];
/*     */       
/*     */ 
/* 436 */       for (int k = 0; k < ((Integer)slMap.get(keys[i])).intValue(); k++) {
/* 437 */         String[] seqs = new String[ids.length];
/* 438 */         for (int j = 0; j < ids.length; j++) {
/* 439 */           String pfam_id = pfamList[j];
/*     */           
/* 441 */           SortedMap startToIdent = new TreeMap();
/* 442 */           for (int l = 0; l < alignPfam.getIdCount(); l++) {
/* 443 */             if (alignPfam.getIdentifier(l).getName().startsWith(pfam_id)) {
/* 444 */               Integer start = new Integer(Integer.parseInt(alignPfam.getIdentifier(l).getName().split("/")[1].split("-")[0]));
/* 445 */               startToIdent.put(start, new Integer(l));
/*     */             }
/*     */           }
/*     */           
/* 449 */           Iterator startIt = startToIdent.values().iterator();
/*     */           
/* 451 */           for (int count = 0; count < k; count++)
/*     */           {
/* 453 */             startIt.next();
/*     */           }
/* 455 */           seqs[j] = alignPfam.getAlignedSequenceString(((Integer)startIt.next()).intValue());
/*     */         }
/* 457 */         if (ids.length == 0) return null;
/* 458 */         align[k] = new SimpleAlignment(ids, seqs, dt);
/*     */       }
/*     */       
/* 461 */       aligns.put(keys[i], align);
/*     */     }
/* 463 */     Alignment[] align = new Alignment[sl.length];
/* 464 */     slMap = new HashMap();
/* 465 */     for (int i = 0; i < sl.length; i++) {
/* 466 */       if (slMap.containsKey(sl[i])) slMap.put(sl[i], new Integer(((Integer)slMap.get(sl[i])).intValue() + 1)); else {
/* 467 */         slMap.put(sl[i], new Integer(0));
/*     */       }
/* 469 */       align[i] = ((Alignment[])aligns.get(sl[i]))[((Integer)slMap.get(sl[i])).intValue()];
/*     */     }
/* 471 */     return align;
/*     */   }
/*     */ }


/* Location:              /home/lachlan/Desktop/psilc1.21/psilc.jar!/lc1/phyl/OrthDomainAlignment.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */