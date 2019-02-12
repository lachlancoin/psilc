/*     */ package lc1.phyl;
/*     */ 
/*     */ import forester.tree.TreeHelper;
/*     */ import java.io.BufferedReader;
/*     */ import java.io.BufferedWriter;
/*     */ import java.io.File;
/*     */ import java.io.FileReader;
/*     */ import java.io.FileWriter;
/*     */ import java.io.PrintStream;
/*     */ import java.io.PrintWriter;
/*     */ import java.util.Collection;
/*     */ import java.util.HashSet;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import java.util.TreeMap;
/*     */ import java.util.TreeSet;
/*     */ import lc1.util.SheetIO;
/*     */ import org.biojava.bio.Annotation;
/*     */ import org.biojava.bio.seq.Sequence;
/*     */ import org.biojava.bio.seq.SequenceIterator;
/*     */ import org.biojava.bio.seq.io.SeqIOTools;
/*     */ import pal.alignment.Alignment;
/*     */ import pal.alignment.AlignmentUtils;
/*     */ import pal.alignment.ReadAlignment;
/*     */ import pal.alignment.SimpleAlignment;
/*     */ import pal.misc.Identifier;
/*     */ import pal.tree.Node;
/*     */ import pal.tree.NodeUtils;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class OrthDomains
/*     */ {
/*     */   public static void main1(String[] args)
/*     */     throws Exception
/*     */   {
/*  70 */     File f = new File(args[2]);
/*  71 */     if ((f.exists()) && (f.length() > 0L)) throw new Exception(f + " alread exists");
/*  72 */     filterFragmentedProteins(
/*  73 */       getFragmentedProteinsFromPfamseq(new File(args[0])), 
/*  74 */       SheetIO.read(new File(args[1]), "\t"), 
/*  75 */       new PrintWriter(new BufferedWriter(new FileWriter(args[2]))));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static void main2(String[] args)
/*     */     throws Exception
/*     */   {
/*  88 */     OrthDomainAlignment.main(args);
/*     */   }
/*     */   
/*     */ 
/*     */   public static void main(String[] args)
/*     */     throws Exception
/*     */   {
/*  95 */     forester.tree.Tree tree = TreeHelper.readNHtree(new File(args[1]));
/*  96 */     Set allowedTaxa = new TreeSet();
/*  97 */     SheetIO.toCollection(SheetIO.getColumn(SheetIO.read(new File(args[0]), "\t"), 0), allowedTaxa);
/*     */     
/*     */ 
/* 100 */     File out = new File("align");
/* 101 */     File[] files = new File("overallAlign").listFiles();
/* 102 */     for (int i = 0; i < files.length; i++) {
/* 103 */       Alignment align = new ReadAlignment(files[i].getAbsolutePath());
/* 104 */       Alignment align1 = getReducedAlignment(allowedTaxa, MaxLikelihoodTree.convert(tree), align);
/* 105 */       if (align1 == null) {
/* 106 */         System.err.println("nothing left for " + files[i]);
/*     */       }
/*     */       else {
/* 109 */         PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(new File(out, files[i].getName()))));
/* 110 */         AlignmentUtils.print(align1, pw);
/* 111 */         pw.close();
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static void removeLCADescendantsDuplication(Map map, pal.tree.Tree tree)
/*     */   {
/* 122 */     Set duplSet = new HashSet();
/* 123 */     for (Iterator it = map.keySet().iterator(); it.hasNext();) {
/* 124 */       Object key = it.next();
/* 125 */       if (map.get(key) == null) duplSet.add(key);
/*     */     }
/* 127 */     if (duplSet.size() > 0) {
/* 128 */       System.err.println("duplication " + duplSet);
/* 129 */       String[] names = new String[duplSet.size()];
/* 130 */       duplSet.toArray(names);
/* 131 */       Node[] extNodes = NodeUtils.findByIdentifier(tree.getRoot(), names);
/* 132 */       Node lca = NodeUtils.getFirstCommonAncestor(extNodes);
/* 133 */       Node[] external = NodeUtils.getExternalNodes(lca);
/* 134 */       for (int i = 0; i < external.length; i++) {
/* 135 */         if (map.containsKey(external[i].getIdentifier().getName())) {
/* 136 */           duplSet.add(external[i].getIdentifier().getName());
/* 137 */           map.remove(external[i].getIdentifier().getName());
/*     */         }
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
/*     */   private static Alignment getReducedAlignment(Collection allowedTaxa, pal.tree.Tree taxonTree, Alignment align)
/*     */   {
/*     */     try
/*     */     {
/* 158 */       Map map = new TreeMap();
/* 159 */       for (int j = 0; j < align.getIdCount(); j++) {
/* 160 */         if (allowedTaxa.contains(align.getIdentifier(j).getName())) {
/* 161 */           if (map.containsKey(align.getIdentifier(j).getName())) {
/* 162 */             map.put(align.getIdentifier(j).getName(), null);
/*     */           }
/*     */           else
/* 165 */             map.put(align.getIdentifier(j).getName(), align.getAlignedSequenceString(j));
/*     */         }
/*     */       }
/* 168 */       removeLCADescendantsDuplication(map, taxonTree);
/*     */       
/* 170 */       if (map.keySet().size() > 3) {
/* 171 */         String[] st = new String[map.keySet().size()];
/* 172 */         Identifier[] ids = new Identifier[map.keySet().size()];
/* 173 */         Iterator it = map.keySet().iterator();
/* 174 */         for (int j = 0; j < ids.length; j++) {
/* 175 */           String key = (String)it.next();
/* 176 */           ids[j] = new Identifier(key);
/* 177 */           st[j] = ((String)map.get(key));
/*     */         }
/* 179 */         return new SimpleAlignment(ids, st, align.getDataType());
/*     */       }
/*     */     }
/*     */     catch (Exception exc) {
/* 183 */       exc.printStackTrace();
/*     */     }
/*     */     
/* 186 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static void filterFragmentedProteins(Set fragmentedProtIds, Iterator pfamseqToArch, PrintWriter out)
/*     */   {
/* 196 */     while (pfamseqToArch.hasNext()) {
/* 197 */       List l = (List)pfamseqToArch.next();
/*     */       try {
/* 199 */         if (!fragmentedProtIds.contains(l.get(0))) {
/* 200 */           for (Iterator it = l.iterator(); it.hasNext();)
/*     */           {
/* 202 */             out.print(it.next());
/* 203 */             out.print(it.hasNext() ? "\t" : "\n");
/*     */           }
/*     */         }
/*     */       } catch (Exception exc) {
/* 207 */         exc.printStackTrace();
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public static Set getFragmentedProteinsFromPfamseq(File pfamseq)
/*     */     throws Exception
/*     */   {
/* 215 */     Set ids = new HashSet();
/* 216 */     BufferedReader br = new BufferedReader(new FileReader(pfamseq));
/* 217 */     System.err.println(pfamseq);
/* 218 */     for (SequenceIterator seqIt = SeqIOTools.readFastaProtein(br); seqIt.hasNext();) {
/* 219 */       Sequence seq = seqIt.nextSequence();
/* 220 */       if (((String)seq.getAnnotation().getProperty("description")).indexOf("Fragment") >= 0)
/*     */       {
/* 222 */         ids.add(seq.getName());
/*     */       }
/*     */     }
/*     */     
/* 226 */     return ids;
/*     */   }
/*     */ }


/* Location:              /home/lachlan/Desktop/psilc1.21/psilc.jar!/lc1/phyl/OrthDomains.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */