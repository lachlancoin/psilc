/*     */ package lc1.util;
/*     */ 
/*     */ import java.io.BufferedWriter;
/*     */ import java.io.File;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.FileWriter;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.PrintStream;
/*     */ import java.io.PrintWriter;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Properties;
/*     */ import java.util.Set;
/*     */ import org.biojava.bio.Annotation;
/*     */ import org.biojava.bio.program.blast2html.Blast2HTMLHandler;
/*     */ import org.biojava.bio.program.blast2html.DefaultURLGeneratorFactory;
/*     */ import org.biojava.bio.program.blast2html.HTMLRenderer;
/*     */ import org.biojava.bio.program.sax.BlastLikeSAXParser;
/*     */ import org.biojava.bio.program.ssbind.BlastLikeSearchBuilder;
/*     */ import org.biojava.bio.program.ssbind.SeqSimilarityAdapter;
/*     */ import org.biojava.bio.search.SearchContentHandler;
/*     */ import org.biojava.bio.search.SeqSimilaritySearchHit;
/*     */ import org.biojava.bio.search.SeqSimilaritySearchResult;
/*     */ import org.biojava.bio.seq.db.DummySequenceDB;
/*     */ import org.biojava.bio.seq.db.DummySequenceDBInstallation;
/*     */ import org.xml.sax.InputSource;
/*     */ import org.xml.sax.SAXException;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class BlastParser
/*     */ {
/*     */   public static void main(String[] args)
/*     */   {
/*     */     try
/*     */     {
/*  52 */       InputStream is = new FileInputStream(args[0]);
/*     */       
/*     */ 
/*  55 */       BlastLikeSAXParser parser = new BlastLikeSAXParser();
/*  56 */       parser.setModeLazy();
/*     */       
/*     */ 
/*  59 */       SeqSimilarityAdapter adapter = new SeqSimilarityAdapter();
/*  60 */       PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(new File(args[1]))));
/*  61 */       HTMLRenderer htmlR = new HTMLRenderer(
/*  62 */         pw, 
/*  63 */         null, 20, 
/*  64 */         new DefaultURLGeneratorFactory(), null, 
/*  65 */         new Properties());
/*  66 */       Blast2HTMLHandler b2html = new Blast2HTMLHandler(htmlR);
/*     */       
/*  68 */       parser.setContentHandler(b2html);
/*  69 */       parser.parse(new InputSource(is));
/*  70 */       pw.close();
/*  71 */       System.exit(0);
/*     */       
/*     */ 
/*  74 */       List results = new ArrayList();
/*     */       
/*     */ 
/*     */ 
/*  78 */       SearchContentHandler builder = new BlastLikeSearchBuilder(results, 
/*  79 */         new DummySequenceDB("queries"), new DummySequenceDBInstallation());
/*     */       
/*     */ 
/*  82 */       adapter.setSearchContentHandler(builder);
/*     */       
/*     */ 
/*     */ 
/*  86 */       parser.parse(new InputSource(is));
/*     */       
/*     */ 
/*  89 */       for (Iterator i = results.iterator(); i.hasNext();) {
/*  90 */         SeqSimilaritySearchResult result = 
/*  91 */           (SeqSimilaritySearchResult)i.next();
/*     */         
/*  93 */         Annotation anno = result.getAnnotation();
/*     */         
/*  95 */         for (Iterator j = anno.keys().iterator(); j.hasNext();) {
/*  96 */           Object key = j.next();
/*  97 */           Object property = anno.getProperty(key);
/*  98 */           System.out.println(key + " : " + property);
/*     */         }
/* 100 */         System.out.println("Hits: ");
/*     */         
/*     */ 
/* 103 */         for (Iterator k = result.getHits().iterator(); k.hasNext();) {
/* 104 */           SeqSimilaritySearchHit hit = 
/* 105 */             (SeqSimilaritySearchHit)k.next();
/* 106 */           System.out.print("\tmatch: " + hit.getSubjectID());
/* 107 */           System.out.println("\te score: " + hit.getEValue());
/*     */         }
/*     */         
/* 110 */         System.out.println("\n");
/*     */       }
/*     */       
/*     */     }
/*     */     catch (SAXException ex)
/*     */     {
/* 116 */       ex.printStackTrace();
/*     */     }
/*     */     catch (IOException ex) {
/* 119 */       ex.printStackTrace();
/*     */     }
/*     */   }
/*     */ }


/* Location:              /home/lachlan/Desktop/psilc1.21/psilc.jar!/lc1/util/BlastParser.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */