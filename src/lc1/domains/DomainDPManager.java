/*     */ package lc1.domains;
/*     */ 
/*     */ import com.braju.format.Format;
/*     */ import com.braju.format.Parameters;
/*     */ import java.io.BufferedOutputStream;
/*     */ import java.io.BufferedReader;
/*     */ import java.io.BufferedWriter;
/*     */ import java.io.File;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.FileReader;
/*     */ import java.io.FileWriter;
/*     */ import java.io.OutputStream;
/*     */ import java.io.PrintStream;
/*     */ import java.io.PrintWriter;
/*     */ import java.io.StringWriter;
/*     */ import java.util.Arrays;
/*     */ import java.util.Iterator;
/*     */ import lc1.domainseq.Domain;
/*     */ import lc1.domainseq.Domain.MagicTemplate;
/*     */ import lc1.domainseq.FeatureUtils;
/*     */ import lc1.dp.HmmerLauncher;
/*     */ import lc1.pfam.DomainAlphabet;
/*     */ import lc1.pfam.IndexedPfamDB;
/*     */ import lc1.pfam.PfamAlphabet;
/*     */ import org.apache.commons.cli.CommandLine;
/*     */ import org.apache.commons.cli.OptionBuilder;
/*     */ import org.apache.commons.cli.Options;
/*     */ import org.apache.commons.cli.Parser;
/*     */ import org.apache.commons.cli.PosixParser;
/*     */ import org.biojava.bio.Annotation;
/*     */ import org.biojava.bio.seq.FeatureHolder;
/*     */ import org.biojava.bio.seq.Sequence;
/*     */ import org.biojava.bio.seq.SequenceIterator;
/*     */ import org.biojava.bio.seq.db.SequenceDB;
/*     */ import org.biojava.bio.seq.io.SeqIOTools;
/*     */ import org.biojava.bio.symbol.Symbol;
/*     */ import org.biojava.utils.ProcessTools;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class DomainDPManager
/*     */ {
/*  45 */   static final Options OPTIONS = new Options() {};
/*     */   
/*     */ 
/*     */   SequenceIterator seqIt;
/*     */   
/*     */ 
/*     */   DomainDP dp;
/*     */   
/*     */ 
/*     */   File outpDir;
/*     */   
/*     */ 
/*     */   public static void main(String[] args)
/*     */     throws Exception
/*     */   {
/*  60 */     Parser parser = new PosixParser();
/*  61 */     CommandLine params = parser.parse(OPTIONS, args);
/*     */     
/*  63 */     String[] context_files = (String[])null;
/*  64 */     String[] species_files = (String[])null;
/*  65 */     if (params.hasOption("context")) {
/*  66 */       context_files = params.getOptionValues("context");
/*     */     }
/*  68 */     if (params.hasOption("species")) {
/*  69 */       species_files = params.getOptionValues("species");
/*     */     }
/*  71 */     DomainDPManager dp_manager = new DomainDPManager(
/*  72 */       context_files, 
/*  73 */       species_files, 
/*  74 */       params);
/*     */     
/*  76 */     dp_manager.run();
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
/*  91 */   File hmm_dir = null;
/*  92 */   String hmmdb = null;
/*     */   CommandLine params;
/*     */   DomainAlphabet alph;
/*     */   static final String printStyle1 = "%-8s %8s %8s %7.2g \n";
/*     */   
/*     */   DomainDPManager(String[] context_files, String[] species_files, CommandLine params)
/*     */   {
/*  99 */     DomainDPFactory dpFact = new DomainDPFactory(params);
/* 100 */     this.dp = dpFact.create();
/*     */     try {
/* 102 */       this.params = params;
/* 103 */       this.outpDir = new File(params.getOptionValue("dir", "."));
/* 104 */       this.alph = PfamAlphabet.makeAlphabet(new File(params.getOptionValue("pfamA")));
/* 105 */       if (params.hasOption("pfamdb")) {
/* 106 */         SequenceDB seqs = new IndexedPfamDB(new File(params.getOptionValue("pfamdb")));
/* 107 */         this.seqIt = seqs.sequenceIterator();
/*     */       }
/* 109 */       else if ((params.hasOption("seqdb")) && (params.hasOption("hmmdb"))) {
/* 110 */         this.hmm_dir = new File(this.outpDir, "hmm");
/* 111 */         if (!this.hmm_dir.exists()) this.hmm_dir.mkdir();
/* 112 */         this.hmmdb = params.getOptionValue("hmmdb");
/* 113 */         this.seqIt = SeqIOTools.readFastaProtein(new BufferedReader(new FileReader(params.getOptionValue("seqdb"))));
/*     */       }
/*     */       
/*     */ 
/*     */ 
/* 118 */       params.hasOption("species");
/*     */ 
/*     */     }
/*     */     catch (Exception exc)
/*     */     {
/*     */ 
/* 124 */       exc.printStackTrace();
/*     */     }
/*     */   }
/*     */   
/*     */   public File runHmmer(Sequence seq)
/*     */     throws Exception
/*     */   {
/* 131 */     File tempSeq = new File(this.outpDir, "tmp_seq.fasta");
/* 132 */     tempSeq.deleteOnExit();
/* 133 */     OutputStream os = new BufferedOutputStream(new FileOutputStream(tempSeq));
/* 134 */     SeqIOTools.writeFasta(os, seq);
/* 135 */     os.close();
/* 136 */     File hmmerFile = new File(this.hmm_dir, seq.getName());
/* 137 */     if (!hmmerFile.exists()) {
/* 138 */       String[] command = { "hmmpfam", "--acc", "-E", "100", this.hmmdb, tempSeq.getAbsolutePath() };
/*     */       
/* 140 */       PrintWriter hmm_pw = new PrintWriter(new FileWriter(hmmerFile));
/* 141 */       StringWriter err = new StringWriter();
/* 142 */       System.err.println("excecuting " + Arrays.asList(command) + "....");
/* 143 */       ProcessTools.exec(command, null, hmm_pw, err);
/* 144 */       System.err.println("...done");
/* 145 */       String error = err.getBuffer().toString();
/* 146 */       if (error.length() > 0) {
/* 147 */         throw new Exception(error);
/*     */       }
/* 149 */       hmm_pw.close();
/*     */     }
/* 151 */     return hmmerFile;
/*     */   }
/*     */   
/*     */   public void run()
/*     */   {
/*     */     try {
/* 157 */       PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(new File(this.outpDir, "output"))));
/* 158 */       Iterator it; for (; this.seqIt.hasNext(); 
/*     */           
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 167 */           it.hasNext())
/*     */       {
/* 159 */         Sequence seq = this.seqIt.nextSequence();
/* 160 */         if (this.hmmdb != null) {
/* 161 */           File hmmerFile = runHmmer(seq);
/* 162 */           seq = HmmerLauncher.parseHmmerResult(new BufferedReader(new FileReader(hmmerFile)), seq, this.alph, 10.0D);
/*     */         }
/* 164 */         seq.createFeature(new Domain.MagicTemplate(this.alph, 0));
/* 165 */         seq.createFeature(new Domain.MagicTemplate(this.alph, seq.length()));
/* 166 */         Sequence ds = this.dp.getStatePath(seq);
/* 167 */         it = ds.filter(FeatureUtils.DOMAIN_FILTER).features(); continue;
/* 168 */         Domain dom = (Domain)it.next();
/* 169 */         print(dom, ds.getName(), 
/* 170 */           "", pw);
/* 171 */         pw.flush();
/*     */       }
/*     */       
/* 174 */       pw.close();
/*     */     } catch (Throwable t) {
/* 176 */       t.printStackTrace();
/* 177 */       System.exit(0);
/*     */     }
/*     */   }
/*     */   
/*     */   private void print(Domain sym, String seqNam, String spec, PrintWriter pw)
/*     */   {
/* 183 */     Annotation annot = sym.getAnnotation();
/*     */     
/* 185 */     pw.print(Format.sprintf("%-8s %8s %8s %7.2g \n", 
/* 186 */       new Parameters(seqNam)
/* 187 */       .add(sym.getSymbol().getName())
/* 188 */       .add(spec)
/* 189 */       .add(sym.getScore())));
/*     */     
/* 191 */     pw.flush();
/*     */   }
/*     */ }


/* Location:              /home/lachlan/Desktop/psilc1.21/psilc.jar!/lc1/domains/DomainDPManager.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */