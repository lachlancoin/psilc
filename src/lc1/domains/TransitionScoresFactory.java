/*     */ package lc1.domains;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.ObjectInputStream;
/*     */ import java.io.ObjectOutputStream;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import org.apache.commons.cli.CommandLine;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class TransitionScoresFactory
/*     */ {
/*  19 */   private static Map context_scoresInUse = new HashMap();
/*  20 */   private static Map species_scoresInUse = new HashMap();
/*     */   
/*  22 */   public static double seqCoverage = 0.7D;
/*     */   
/*     */ 
/*     */   private static final float held = 0.0F;
/*     */   
/*     */ 
/*     */ 
/*     */   public static void writeModel(String output, Object w)
/*     */   {
/*     */     try
/*     */     {
/*  33 */       ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("dir"));
/*  34 */       out.writeObject(w);
/*  35 */       out.close();
/*     */     }
/*     */     catch (Throwable t) {
/*  38 */       t.printStackTrace();
/*     */     }
/*     */   }
/*     */   
/*     */   public static Object readModel(String input)
/*     */   {
/*     */     try {
/*  45 */       ObjectInputStream in = new ObjectInputStream(new FileInputStream(input));
/*  46 */       return in.readObject();
/*     */     }
/*     */     catch (Throwable t) {
/*  49 */       t.printStackTrace(); }
/*  50 */     return null;
/*     */   }
/*     */   
/*     */   public static TransitionScores getContextModel(CommandLine params) throws Exception
/*     */   {
/*  55 */     File dir = new File(params.getOptionValue("dir", "."));
/*  56 */     File repos = new File(params.getOptionValue("repos", "."));
/*  57 */     ContextCount freq = new ContextCount(dir, repos, 4, true);
/*  58 */     return getContextModel(freq, params);
/*     */   }
/*     */   
/*     */ 
/*     */   public static TransitionScores getContextModel(ContextCount freq, CommandLine params)
/*     */     throws Exception
/*     */   {
/*  65 */     String[] smoothing = params.getOptionValues("smoothing");
/*     */     
/*  67 */     Smoothing method = makeInterpolationMethod(freq);
/*     */     
/*     */ 
/*  70 */     ContextTransitionScores model = new ContextTransitionScores(method, freq);
/*  71 */     ContextTransitionScores.CONTEXT = Double.parseDouble(smoothing[0]);
/*  72 */     ContextTransitionScores.SPECIES = Double.parseDouble(smoothing[1]);
/*  73 */     return model;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static Smoothing makeInterpolationMethod(ContextCount freq)
/*     */     throws Exception
/*     */   {
/*  84 */     Interpolation method = new Interpolation();
/*     */     
/*     */ 
/*     */ 
/*  88 */     method.freq = freq;
/*     */     
/*  90 */     return method;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private static Smoothing makePseudoCountMethod(ContextCount freq)
/*     */     throws Exception
/*     */   {
/*  98 */     Pseudocount method = new Pseudocount();
/*     */     
/* 100 */     method.freq = freq;
/*     */     
/*     */ 
/* 103 */     return method;
/*     */   }
/*     */ }


/* Location:              /home/lachlan/Desktop/psilc1.21/psilc.jar!/lc1/domains/TransitionScoresFactory.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */