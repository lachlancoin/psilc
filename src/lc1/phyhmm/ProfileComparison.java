/*     */ package lc1.phyhmm;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.util.Arrays;
/*     */ import lc1.pfam.PfamAlphabet;
/*     */ import lc1.pfam.StockholmIndex;
/*     */ import lc1.treefam.AlignTools;
/*     */ import org.apache.commons.cli.CommandLine;
/*     */ import org.apache.commons.cli.Parser;
/*     */ import org.apache.commons.cli.PosixParser;
/*     */ import org.biojava.bio.Annotation;
/*     */ import org.biojava.bio.seq.io.SymbolTokenization;
/*     */ import org.biojava.bio.symbol.Alphabet;
/*     */ import org.biojava.bio.symbol.Symbol;
/*     */ import pal.alignment.Alignment;
/*     */ import pal.substmodel.RateMatrix;
/*     */ import pal.tree.ReadTree;
/*     */ import pal.tree.Tree;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ProfileComparison
/*     */ {
/*     */   File repos;
/*     */   Alphabet alph;
/*     */   SymbolTokenization tokens;
/*     */   Symbol[] domains;
/*     */   File dir;
/*     */   StockholmIndex stockholmIndex;
/*     */   
/*     */   public static void main(String[] args)
/*     */     throws Exception
/*     */   {
/*  45 */     Parser DP_PARSER = new PosixParser();
/*  46 */     CommandLine params = DP_PARSER.parse(PhyHMM.OPTIONS, args);
/*  47 */     ProfileComparison prc = new ProfileComparison(params);
/*  48 */     prc.run();
/*     */   }
/*     */   
/*     */   ProfileComparison(CommandLine params) throws Exception {
/*  52 */     this.repos = new File(params.getOptionValue("repository", "."));
/*  53 */     this.dir = new File(params.getOptionValue("dir", "."));
/*  54 */     File pfamA = new File(this.repos, "pfamA");
/*     */     
/*  56 */     this.alph = PfamAlphabet.makeAlphabet(this.repos);
/*  57 */     this.tokens = this.alph.getTokenization("token");
/*  58 */     String[] doms = params.getOptionValues("domain");
/*  59 */     this.domains = new Symbol[doms.length];
/*  60 */     for (int i = 0; i < doms.length; i++) {
/*  61 */       this.domains[i] = this.tokens.parseToken(doms[i]);
/*     */     }
/*  63 */     File pfam_ls = new File(this.repos, "Pfam-A.seed");
/*  64 */     this.stockholmIndex = (pfam_ls.exists() ? new StockholmIndex(this.repos) : null);
/*  65 */     DPManager.useAll = true;
/*  66 */     DPManager.mix = false;
/*  67 */     DPManager.count = 0;
/*  68 */     DPManager.insertRates = 5;
/*  69 */     DPManager.matchRates = 5;
/*     */   }
/*     */   
/*     */   public void run()
/*     */     throws Exception
/*     */   {
/*  75 */     for (int i = 0; i < this.domains.length; i++) {
/*  76 */       String name = (String)this.domains[i].getAnnotation().getProperty("pfamA_id");
/*  77 */       File pfamF = new File(this.repos, "Pfam/" + name);
/*  78 */       File alignF = new File(pfamF, "/SEED");
/*  79 */       if ((!pfamF.exists()) || (pfamF.length() == 0L) || (!alignF.exists()))
/*  80 */         this.stockholmIndex.writeHMMFile(name);
/*  81 */       Alignment aln = PfamRates.readAlignment(alignF);
/*     */       
/*  83 */       File treeF = new File(pfamF, "tree.nhx");
/*  84 */       if ((!treeF.exists()) || (treeF.length() == 0L)) {
/*  85 */         AlignTools.phyml(treeF, aln);
/*     */       }
/*  87 */       Tree tree = new ReadTree(treeF.getAbsolutePath());
/*     */       
/*  89 */       File ratematrixF = new File(pfamF, "ratematrix");
/*     */       RateMatrix substMProt;
/*  91 */       RateMatrix substMProt; if ((!ratematrixF.exists()) || (ratematrixF.length() == 0L)) {
/*  92 */         substMProt = RateTreeBuild.build(name, tree, 
/*  93 */           aln, ratematrixF, DPManager.collapse, 
/*  94 */           false);
/*     */       }
/*     */       else {
/*  97 */         substMProt = RateTreeBuild.read(ratematrixF);
/*     */       }
/*     */       
/* 100 */       for (int j = 0; j < this.domains.length; j++) {
/* 101 */         String name_j = (String)this.domains[j].getAnnotation().getProperty("pfamA_id");
/* 102 */         File hits = new File(this.repos, "Pfam/" + name_j + "/hits");
/* 103 */         if (!hits.exists()) hits.mkdir();
/* 104 */         DPManager psm = new DPManager(name, this.repos, 
/* 105 */           Arrays.asList(new Object[] { this.domains[j] }), aln, tree, substMProt, hits);
/* 106 */         psm.run();
/*     */       }
/*     */     }
/*     */   }
/*     */ }


/* Location:              /home/lachlan/Desktop/psilc1.21/psilc.jar!/lc1/phyhmm/ProfileComparison.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */