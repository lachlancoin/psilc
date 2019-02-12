/*     */ package lc1.domains;
/*     */ 
/*     */ import java.io.BufferedReader;
/*     */ import java.io.BufferedWriter;
/*     */ import java.io.File;
/*     */ import java.io.FileReader;
/*     */ import java.io.FileWriter;
/*     */ import java.io.PrintStream;
/*     */ import java.io.PrintWriter;
/*     */ import java.util.Iterator;
/*     */ import java.util.Properties;
/*     */ import lc1.domainseq.Domain;
/*     */ import lc1.pfam.DomainAlphabet;
/*     */ import lc1.pfam.PfamIndex;
/*     */ import lc1.pfam.SpeciesAlphabet;
/*     */ import org.apache.commons.cli.CommandLine;
/*     */ import org.apache.commons.cli.OptionBuilder;
/*     */ import org.apache.commons.cli.Options;
/*     */ import org.apache.commons.cli.Parser;
/*     */ import org.apache.commons.cli.PosixParser;
/*     */ import org.biojava.bio.Annotation;
/*     */ import org.biojava.bio.seq.FeatureFilter.OverlapsLocation;
/*     */ import org.biojava.bio.seq.FeatureHolder;
/*     */ import org.biojava.bio.seq.Sequence;
/*     */ import org.biojava.bio.seq.io.SymbolTokenization;
/*     */ import org.biojava.bio.symbol.Location;
/*     */ import org.biojava.bio.symbol.Symbol;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ContextPfam
/*     */ {
/*  60 */   static final Options OPTIONS = new Options() {};
/*     */   DomainDP dp;
/*     */   File outpDir;
/*     */   File repos;
/*     */   File resultsDir;
/*     */   PrintWriter output;
/*     */   PrintWriter mismatchOverlap;
/*     */   PfamIndex pfam_index;
/*     */   CommandLine params;
/*     */   DomainAlphabet alph;
/*     */   SpeciesAlphabet spec_al;
/*     */   SqlQuery sql;
/*     */   
/*     */   public static void main(String[] args)
/*     */     throws Exception
/*     */   {
/*  76 */     Parser parser = new PosixParser();
/*  77 */     CommandLine params = parser.parse(OPTIONS, args);
/*  78 */     ContextPfam dp_manager = new ContextPfam(params);
/*  79 */     String[] smoothing_params = 
/*  80 */       { "0.7", params.hasOption("smoothing") ? params.getOptionValues("smoothing") : "0.35" };
/*  81 */     ContextTransitionScores.CONTEXT = Double.parseDouble(smoothing_params[0]);
/*  82 */     ContextTransitionScores.SPECIES = Double.parseDouble(smoothing_params[0]);
/*  83 */     dp_manager.run();
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
/* 100 */   int min = 0;
/*     */   SymbolTokenization speciesTokenizer;
/*     */   static final String printStyle1 = "%-15s %15s %15s %15.3f %15.3g  %15s\n";
/*     */   static final String headerStr = "%-15s %15s %15s %15s, %15s \n";
/*     */   
/* 105 */   ContextPfam(CommandLine params) { try { this.params = params;
/* 106 */       DomainDPFactory dpFact = new DomainDPFactory(params);
/* 107 */       this.dp = dpFact.create();
/* 108 */       Properties sqlParams = new Properties();
/* 109 */       sqlParams.setProperty("host", params.getOptionValue("host", "pfam"));
/* 110 */       sqlParams.setProperty("user", params.getOptionValue("host", "pfam"));
/* 111 */       sqlParams.setProperty("database", params.getOptionValue("database", "pfam"));
/* 112 */       sqlParams.setProperty("password", params.getOptionValue("database", "mafp1"));
/* 113 */       this.repos = new File(params.getOptionValue("repository"));
/* 114 */       this.pfam_index = new PfamIndex(this.repos);
/* 115 */       this.outpDir = new File(params.getOptionValue("dir", "."));
/* 116 */       File outputF = new File(this.outpDir, params.getOptionValue("out", "pfamOut"));
/* 117 */       File mismatchF = new File(this.outpDir, "mismatch");
/* 118 */       if ((outputF.exists()) && (outputF.length() > 0L)) {
/* 119 */         BufferedReader br = new BufferedReader(new FileReader(outputF));
/* 120 */         String s = "";
/* 121 */         String prev = "";
/* 122 */         while ((s = br.readLine()) != null) {
/* 123 */           prev = s;
/*     */         }
/* 125 */         this.min = Math.max(this.min, Integer.parseInt(prev.split("\t")[0]));
/* 126 */         br.close();
/*     */       }
/* 128 */       if ((mismatchF.exists()) && (mismatchF.length() > 0L)) {
/* 129 */         BufferedReader br = new BufferedReader(new FileReader(mismatchF));
/* 130 */         String s = "";
/* 131 */         String prev = "";
/* 132 */         while ((s = br.readLine()) != null) {
/* 133 */           prev = s;
/*     */         }
/* 135 */         this.min = Math.max(this.min, Integer.parseInt(prev.split("\t")[0]));
/* 136 */         br.close();
/*     */       }
/* 138 */       System.err.println("min is " + this.min);
/* 139 */       this.output = new PrintWriter(new BufferedWriter(new FileWriter(outputF, outputF.exists())));
/* 140 */       this.mismatchOverlap = new PrintWriter(new BufferedWriter(new FileWriter(mismatchF, mismatchF.exists())));
/*     */       
/* 142 */       this.alph = dpFact.model.getFrequency().getAlphabet();
/* 143 */       this.spec_al = dpFact.model.getFrequency().getSpeciesAlphabet();
/* 144 */       this.sql = new SqlQuery(sqlParams, this.alph);
/* 145 */       this.speciesTokenizer = this.spec_al.getTokenization("token");
/* 146 */       System.err.println("getting pfamdb");
/* 147 */       System.err.println("alphabet size " + this.alph.size());
/* 148 */       System.err.println("done");
/*     */     } catch (Exception exc) {
/* 150 */       exc.printStackTrace();
/*     */     }
/*     */   }
/*     */   
/*     */   public void run() throws Exception
/*     */   {
/* 156 */     for (Iterator it = this.sql.getSequences(this.min); 
/* 157 */           it.hasNext();) {
/*     */       try {
/* 159 */         Sequence[] seq = (Sequence[])it.next();
/* 160 */         if (seq[0].countFeatures() != 0) {
/* 161 */           Sequence result = this.dp.getStatePath(seq[0]);
/* 162 */           Sequence old = seq[1];
/* 163 */           System.err.println(seq[1].getName());
/* 164 */           for (Iterator feat = result.features(); feat.hasNext();) {
/* 165 */             Domain feature = (Domain)feat.next();
/* 166 */             FeatureHolder overlap = old.filter(new FeatureFilter.OverlapsLocation(feature.getLocation()));
/*     */             
/* 168 */             if (overlap.countFeatures() > 0) {
/* 169 */               for (Iterator it1 = overlap.features(); it1.hasNext();) {
/* 170 */                 if (((Domain)it1.next()).getSymbol() != feature.getSymbol()) {
/* 171 */                   print(feature, seq[0].getName(), (String)seq[0].getAnnotation().getProperty("species"), this.mismatchOverlap);
/*     */                 }
/*     */               }
/*     */             } else
/* 175 */               print(feature, seq[0].getName(), (String)seq[0].getAnnotation().getProperty("species"), this.output);
/*     */           }
/*     */         }
/*     */       } catch (Exception exc) {
/* 179 */         exc.printStackTrace();
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private void print(Domain sym, String seqNam, String spec, PrintWriter pw)
/*     */   {
/* 190 */     Annotation annot = sym.getAnnotation();
/* 191 */     pw.print(seqNam);pw.print("\t");
/* 192 */     pw.print(sym.getSymbol().getAnnotation().getProperty("auto_pfamA"));pw.print("\t");
/* 193 */     pw.print(sym.getLocation().getMin());pw.print("\t");
/* 194 */     pw.print(sym.getLocation().getMax());pw.print("\t");
/* 195 */     pw.print(sym.getScore());pw.print("\n");
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 204 */     pw.flush();
/*     */   }
/*     */ }


/* Location:              /home/lachlan/Desktop/psilc1.21/psilc.jar!/lc1/domains/ContextPfam.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */