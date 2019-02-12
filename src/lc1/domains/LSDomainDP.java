/*     */ package lc1.domains;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.PrintStream;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Comparator;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.Set;
/*     */ import java.util.SortedSet;
/*     */ import java.util.TreeSet;
/*     */ import lc1.domainseq.Domain;
/*     */ import lc1.domainseq.Domain.Template;
/*     */ import lc1.domainseq.DomainList;
/*     */ import lc1.domainseq.DomainList.SymbolMap;
/*     */ import lc1.domainseq.FSDomain;
/*     */ import lc1.domainseq.FeatureUtils;
/*     */ import lc1.domainseq.MultipleDomainList;
/*     */ import lc1.domainseq.SequenceScore;
/*     */ import lc1.pfam.DomainAlphabet;
/*     */ import lc1.pfam.SpeciesAlphabet;
/*     */ import org.apache.commons.cli.CommandLine;
/*     */ import org.biojava.bio.Annotation;
/*     */ import org.biojava.bio.seq.FeatureHolder;
/*     */ import org.biojava.bio.seq.Sequence;
/*     */ import org.biojava.bio.symbol.Location;
/*     */ import org.biojava.bio.symbol.Symbol;
/*     */ 
/*     */ public class LSDomainDP implements DomainDP
/*     */ {
/*     */   protected TransitionScores model;
/*     */   protected Symbol mState;
/*  37 */   protected Comparator comparator = FeatureUtils.END_INCREASING;
/*     */   
/*  39 */   protected int contextLength = 4;
/*     */   
/*     */ 
/*     */   CommandLine params;
/*     */   
/*     */   SpeciesAlphabet spec_al;
/*     */   
/*     */ 
/*     */   public TransitionScores getModel()
/*     */   {
/*  49 */     return this.model;
/*     */   }
/*     */   
/*     */ 
/*     */   public void setModel(TransitionScores model)
/*     */   {
/*  55 */     this.model = model;
/*  56 */     this.mState = model.getFrequency().getAlphabet().getMagicalState();
/*     */   }
/*     */   
/*     */   public void setTable(CommandLine params)
/*     */   {
/*  61 */     this.params = params;
/*     */     try {
/*  63 */       this.spec_al = 
/*  64 */         SpeciesAlphabet.makeAlphabet(new File(params.getOptionValue("repository")), null);
/*     */ 
/*     */     }
/*     */     catch (Exception exc)
/*     */     {
/*  69 */       exc.printStackTrace();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Sequence getStatePath(Sequence test_ds)
/*     */   {
/*  79 */     Trace tr = new Trace(test_ds, 1);
/*  80 */     tr.dynamicSearch();
/*     */     
/*     */ 
/*  83 */     int[] result = tr.best_sentences[(tr.best_sentences.length - 1)].sequenceAt(0);
/*     */     
/*  85 */     Domain.Template[] template = new Domain.Template[result.length];
/*  86 */     for (int i = 0; i < result.length; i++) {
/*  87 */       template[i] = ((Domain.Template)tr.domains[result[i]].makeTemplate());
/*     */     }
/*     */     
/*     */     try
/*     */     {
/*  92 */       DomainList seq = new DomainList(test_ds, test_ds.getName(), test_ds.getName(), test_ds.getAnnotation(), template);
/*  93 */       seq.getAnnotation().setProperty("score", new Double(tr.best_sentences[(tr.best_sentences.length - 1)].getScore(0)));
/*  94 */       SymbolCount sc = new SymbolCount(seq, tr.best_sentences[(tr.best_sentences.length - 1)].getScore(0));
/*  95 */       List l = sc.filterDomainList();
/*  96 */       for (int i = 0; i < l.size(); i++) {
/*  97 */         seq.removeFeature((Domain)l.get(i));
/*     */       }
/*  99 */       return seq;
/*     */     } catch (Exception exc) {
/* 101 */       exc.printStackTrace();
/*     */     }
/* 103 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public double score(Sequence test_ds)
/*     */   {
/* 111 */     Trace tr = new Trace(test_ds, 1);
/* 112 */     return tr.score();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   class Trace
/*     */   {
/* 123 */     int no_best = 2;
/*     */     
/*     */     protected Domain[] domains;
/*     */     
/*     */     protected MultipleDomainList[] best_sentences;
/*     */     
/*     */     org.biojava.bio.symbol.SymbolList species;
/*     */     
/*     */     String name;
/*     */     
/*     */     public double score()
/*     */     {
/* 135 */       double[] score = new double[this.domains.length];
/* 136 */       score[0] = 0.0D;
/* 137 */       for (int i = 1; i < this.domains.length; i++) {
/* 138 */         int[] contextIndices = new int[i];
/* 139 */         for (int j = 0; j < i; j++) {
/* 140 */           contextIndices[j] = j;
/*     */         }
/* 142 */         DomainList.SymbolMap sm = new DomainList.SymbolMap(this.species, this.domains, contextIndices, 
/* 143 */           i, LSDomainDP.this.model.getFrequency().getAlphabet());
/* 144 */         score[i] = (score[(i - 1)] + this.domains[i].getScore());
/*     */         try
/*     */         {
/* 147 */           score[i] += LSDomainDP.this.model.getTransitionScore(sm);
/*     */         }
/*     */         catch (Exception exc)
/*     */         {
/* 151 */           exc.printStackTrace();
/* 152 */           System.exit(0);
/*     */         }
/*     */       }
/* 155 */       return score[(this.domains.length - 1)];
/*     */     }
/*     */     
/*     */     double domainContribution(Domain dom)
/*     */     {
/* 160 */       return dom.getScore() - ((Float)dom.getSymbol().getAnnotation().getProperty("ls_dom_thresh")).floatValue();
/*     */     }
/*     */     
/*     */     double domainContribution(Domain dom, int pos_i) {
/* 164 */       return ((FSDomain)dom).getScores(pos_i).full();
/*     */     }
/*     */     
/*     */     Trace(Sequence ds, int no_best)
/*     */     {
/*     */       try
/*     */       {
/* 171 */         spec = (String)ds.getAnnotation().getProperty("species");
/* 172 */         this.species = LSDomainDP.this.spec_al.taxaToList(LSDomainDP.this.spec_al.getTokenization("token").parseToken(spec));
/*     */         
/* 174 */         if (this.species == null) { throw new Exception("species is null: " + spec);
/*     */         }
/* 176 */         Iterator it = FeatureUtils.iterator(ds, FeatureUtils.END_INCREASING, 1);
/* 177 */         List doms = new ArrayList(ds.countFeatures());
/* 178 */         while (it.hasNext()) {
/* 179 */           doms.add(it.next());
/*     */         }
/* 181 */         this.domains = ((Domain[])doms.toArray(new Domain[0]));
/* 182 */         this.best_sentences = new MultipleDomainList[this.domains.length];
/* 183 */         this.best_sentences[0] = MultipleDomainList.BEGIN;
/* 184 */         this.name = ds.getName();
/* 185 */         this.no_best = no_best;
/*     */       } catch (Throwable t) {
/*     */         String spec;
/* 188 */         t.printStackTrace();
/* 189 */         System.err.println(ds.getName() + " " + ds.countFeatures());
/* 190 */         System.exit(0);
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     public void dynamicSearch()
/*     */     {
/*     */       try
/*     */       {
/* 205 */         for (int i = 1; i < this.domains.length; i++) {
/* 206 */           SortedSet pointers = dynamicSearchInner(i);
/* 207 */           this.best_sentences[i] = new MultipleDomainList(pointers.size());
/* 208 */           for (Iterator it = pointers.iterator(); it.hasNext();) {
/* 209 */             Pointer point = (Pointer)it.next();
/* 210 */             double score = point.score;
/* 211 */             int[] best = this.best_sentences[point.dom].sequenceAt(point.position);
/* 212 */             int[] new_B = new int[best.length + 1];
/* 213 */             System.arraycopy(best, 0, new_B, 0, best.length);
/* 214 */             new_B[(new_B.length - 1)] = i;
/* 215 */             this.best_sentences[i].addSequence(new_B, score, point.t_score);
/*     */           }
/*     */         }
/*     */       }
/*     */       catch (Throwable t) {
/* 220 */         t.printStackTrace();
/* 221 */         System.exit(0);
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     SortedSet dynamicSearchInner(int i)
/*     */     {
/* 233 */       SortedSet pointers = new TreeSet(Pointer.comparator());
/* 234 */       double domainCont = domainContribution(this.domains[i]);
/* 235 */       for (int j = 0; j < i; j++)
/*     */       {
/* 237 */         if (!this.domains[i].getLocation().overlaps(this.domains[j].getLocation())) {
/* 238 */           MultipleDomainList bestS = this.best_sentences[j];
/* 239 */           for (int k = 0; k < bestS.size(); k++) {
/* 240 */             int[] contextIndices = bestS.sequenceAt(k);
/* 241 */             double t_score = LSDomainDP.this.model.getTransitionScore(new DomainList.SymbolMap(this.species, this.domains, contextIndices, 
/* 242 */               i, LSDomainDP.this.model.getFrequency().getAlphabet()));
/* 243 */             double score = bestS.getScore(k) + t_score + domainCont;
/* 244 */             if (pointers.size() < this.no_best) {
/* 245 */               Pointer p = new Pointer(j, score, k, 1);
/* 246 */               p.t_score = t_score;
/* 247 */               pointers.add(p);
/*     */             }
/* 249 */             else if (score > ((Pointer)pointers.last()).score) {
/* 250 */               Pointer p = new Pointer(j, score, k, 1);
/* 251 */               p.t_score = t_score;
/* 252 */               pointers.remove(pointers.last());
/* 253 */               pointers.add(p);
/*     */             }
/*     */           }
/*     */         } }
/* 257 */       return pointers;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   class SymbolCount
/*     */   {
/*     */     Sequence seq;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 311 */     final Comparator comp = new LSDomainDP.1(this);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     Map.Entry[] entries;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 321 */     double trans_score = 0.0D;
/* 322 */     int total = 0;
/*     */     
/* 324 */     SymbolCount(Sequence seq, double totalScore) { Map count = new HashMap();
/* 325 */       this.seq = seq;
/* 326 */       this.trans_score = totalScore;
/* 327 */       for (Iterator it = seq.filter(FeatureUtils.DOMAIN_FILTER).features(); 
/* 328 */             it.hasNext();) {
/* 329 */         Domain domi = (Domain)it.next();
/* 330 */         Symbol symi = domi.getSymbol();
/* 331 */         this.trans_score -= domi.getScore() - ((Float)symi.getAnnotation().getProperty("ls_dom_thresh")).doubleValue();
/* 332 */         double incr_count = domi.getScore();
/* 333 */         if (count.containsKey(symi))
/* 334 */           incr_count += ((Double)count.get(symi)).doubleValue();
/* 335 */         count.put(symi, new Double(incr_count));
/*     */       }
/* 337 */       for (Iterator it = count.keySet().iterator(); it.hasNext();) {
/* 338 */         Symbol sym = (Symbol)it.next();
/* 339 */         count.put(sym, 
/* 340 */           new Double(((Double)count.get(sym)).doubleValue() - 
/* 341 */           ((Float)sym.getAnnotation().getProperty("ls_seq_thresh")).doubleValue()));
/*     */       }
/* 343 */       this.entries = ((Map.Entry[])count.entrySet().toArray(new Map.Entry[0]));
/* 344 */       Arrays.sort(this.entries, this.comp);
/* 345 */       if (this.trans_score > 0.0D) {
/* 346 */         for (int i = 0; i < this.entries.length; i++) {
/* 347 */           double val = ((Double)this.entries[i].getValue()).doubleValue();
/* 348 */           if (val < 0.0D) {
/* 349 */             double impr = Math.min(-1.0D * val, this.trans_score);
/* 350 */             this.trans_score -= impr;
/* 351 */             this.entries[i].setValue(new Double(val + impr));
/*     */           }
/* 353 */           if (this.trans_score == 0.0D) {
/*     */             break;
/*     */           }
/*     */           
/*     */         }
/* 358 */       } else if (this.trans_score < 0.0D) {
/* 359 */         for (int i = 0; i < this.entries.length; i++) {
/* 360 */           double val = ((Double)this.entries[i].getValue()).doubleValue();
/* 361 */           if (val > 0.0D) {
/* 362 */             double decr = Math.min(val, -1.0D * this.trans_score);
/* 363 */             this.trans_score += decr;
/* 364 */             this.entries[i].setValue(new Double(val - decr));
/*     */           }
/* 366 */           if (this.trans_score == 0.0D)
/*     */             break;
/*     */         }
/*     */       }
/* 370 */       if (this.trans_score != 0.0D) { this.entries[(this.entries.length - 1)].setValue(
/* 371 */           new Double(((Double)this.entries[(this.entries.length - 1)].getValue()).doubleValue() + this.trans_score));
/*     */       }
/*     */     }
/*     */     
/*     */     List filterDomainList()
/*     */     {
/* 377 */       List l = new ArrayList();
/* 378 */       boolean removed = false;
/* 379 */       for (int j = 0; j < this.entries.length; j++) {
/* 380 */         Symbol sym = (Symbol)this.entries[j].getKey();
/* 381 */         double val = ((Double)this.entries[j].getValue()).doubleValue();
/* 382 */         FeatureHolder fh = this.seq.filter(FeatureUtils.sameSymbol(sym));
/* 383 */         if (val >= 0.0D) {
/* 384 */           double incr = val / fh.countFeatures();
/* 385 */           for (Iterator i = fh.features(); i.hasNext();) {
/* 386 */             Domain prop = (Domain)i.next();
/* 387 */             prop.incrScore(incr);
/*     */           }
/*     */         }
/*     */         else {
/* 391 */           for (Iterator i = fh.features(); i.hasNext();) {
/* 392 */             Domain prop = (Domain)i.next();
/* 393 */             if (val < 0.0D)
/*     */             {
/* 395 */               removed = true;
/* 396 */               l.add(prop);
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/* 401 */       return l;
/*     */     }
/*     */   }
/*     */ }


/* Location:              /home/lachlan/Desktop/psilc1.21/psilc.jar!/lc1/domains/LSDomainDP.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */