/*     */ package lc1.dp;
/*     */ 
/*     */ import forester.tree.Node;
/*     */ import forester.tree.PostorderTreeIterator;
/*     */ import java.io.BufferedReader;
/*     */ import java.io.File;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Comparator;
/*     */ import java.util.List;
/*     */ import java.util.Vector;
/*     */ import org.biojava.bio.seq.ProteinTools;
/*     */ import pal.datatype.AminoAcids;
/*     */ import pal.datatype.DataType;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class AlignmentProfileParser
/*     */   extends HmmerProfileParser
/*     */ {
/*     */   static File REPOSITORY1;
/*     */   static File RATE_REPOS;
/*     */   
/*     */   public static AlignmentProfileParser makeParser(BufferedReader in, boolean common_transitions, int insertRates, int matchRates)
/*     */   {
/*  32 */     return new AlignmentProfileParser(in, common_transitions, insertRates, matchRates);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private AlignmentProfileParser(BufferedReader in, boolean common_transitions, int insertRates, int matchRates)
/*     */   {
/*  44 */     super(ProteinTools.getTAlphabet(), AminoAcids.DEFAULT_INSTANCE);
/*     */     try {
/*  46 */       in.readLine();
/*  47 */       String[] str = in.readLine().split("\\s+");
/*     */       
/*  49 */       setName(str[1]);
/*     */       
/*     */ 
/*  52 */       parseModel(in, insertRates, matchRates);
/*  53 */       setProfileHMM();
/*  54 */       AlignmentHMM hmm = (AlignmentHMM)parse();
/*     */       
/*  56 */       hmm.COMMON_TRANSITIONS = common_transitions;
/*     */     } catch (Exception exc) {
/*  58 */       exc.printStackTrace();
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
/*     */   public static void modify(HmmerProfileHMM hmm, AlignmentHMM hmm1)
/*     */   {
/*  88 */     for (int i = 1; i <= hmm.columns(); i++) {
/*  89 */       for (int j = 0; i < hmm.getDataType().getNumStates(); j++) {
/*  90 */         hmm.getMatch(i).getDistribution()[j] = hmm1.getMatch(i).getDistribution()[j];
/*     */       }
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 149 */   private static final double log2 = Math.log(2.0D);
/*     */   
/* 151 */   public static double entropy(double[] freq) { double ent = 0.0D;
/* 152 */     for (int i = 0; i < freq.length; i++) {
/* 153 */       ent += -freq[i] * Math.log(freq[i]) / log2;
/*     */     }
/* 155 */     return ent;
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
/*     */   protected HmmerProfileHMM initialiseProfileHMM(int len, int insertRates, int matchRates)
/*     */   {
/*     */     try
/*     */     {
/* 173 */       return new AlignmentHMM(this.domain1, len, this.dt, insertRates, matchRates);
/*     */     }
/*     */     catch (Throwable t) {
/* 176 */       t.printStackTrace(); }
/* 177 */     return null;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   pal.tree.Tree tree;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 291 */   public static final Comparator NODE_COMPARATOR = new Comparator()
/*     */   {
/*     */     public int compare(Object o1, Object o2) {
/* 294 */       if (o1 == null) {
/* 295 */         if (o2 == null) return 0;
/* 296 */         return -1;
/*     */       }
/* 298 */       if (o2 == null) {
/* 299 */         return 1;
/*     */       }
/* 301 */       if (((o1 instanceof Node)) && 
/* 302 */         ((o2 instanceof Node))) {
/* 303 */         Node n1 = (Node)o1;
/* 304 */         Node n2 = (Node)o2;
/* 305 */         if ((n1.isExternal()) && (!n2.isExternal())) {
/* 306 */           return -1;
/*     */         }
/* 308 */         if ((!n1.isExternal()) && (n2.isExternal())) {
/* 309 */           return 1;
/*     */         }
/*     */         
/* 312 */         int num1 = n1.getID();
/* 313 */         int num2 = n2.getID();
/* 314 */         return num1 > num2 ? 1 : num1 < num2 ? -1 : 0;
/*     */       }
/*     */       
/* 317 */       return 0;
/*     */     }
/*     */     
/*     */     public boolean equals(Object o) {
/* 321 */       if (o == this) return true;
/* 322 */       return false;
/*     */     }
/*     */   };
/*     */   
/*     */ 
/*     */   private static Vector getSmallestSuperOrthologSet(forester.tree.Tree tree)
/*     */     throws Exception
/*     */   {
/* 330 */     List s = new ArrayList();
/* 331 */     PostorderTreeIterator it = new PostorderTreeIterator(tree);
/* 332 */     while (!it.isDone()) {
/* 333 */       if (it.currentNode().isExternal()) {
/* 334 */         s.add(it.currentNode());
/*     */       }
/* 336 */       it.next();
/*     */     }
/*     */     
/* 339 */     Vector smallest = new Vector();
/* 340 */     int min_size = Integer.MAX_VALUE;
/*     */     
/* 342 */     while (s.size() > 0)
/*     */     {
/* 344 */       Vector v = tree.getSuperOrthologousNodes((Node)s.get(0));
/* 345 */       v.add(s.get(0));
/* 346 */       if (v.size() == 1) {
/* 347 */         smallest = v;
/* 348 */         break;
/*     */       }
/*     */       
/* 351 */       for (int i = 0; i < v.size(); i++) {
/* 352 */         s.remove(v.elementAt(i));
/*     */       }
/* 354 */       if (v.size() < min_size)
/*     */       {
/*     */ 
/* 357 */         min_size = v.size();
/* 358 */         smallest = v;
/*     */       }
/*     */     }
/*     */     
/* 362 */     return smallest;
/*     */   }
/*     */ }


/* Location:              /home/lachlan/Desktop/psilc1.21/psilc.jar!/lc1/dp/AlignmentProfileParser.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */