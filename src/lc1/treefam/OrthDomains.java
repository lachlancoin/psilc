/*     */ package lc1.treefam;
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
/*     */ import lc1.phyl.MaxLikelihoodTree;
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
/*  71 */     File f = new File(args[2]);
/*  72 */     if ((f.exists()) && (f.length() > 0L)) throw new Exception(f + " alread exists");
/*  73 */     filterFragmentedProteins(
/*  74 */       getFragmentedProteinsFromPfamseq(new File(args[0])), 
/*  75 */       SheetIO.read(new File(args[1]), "\t"), 
/*  76 */       new PrintWriter(new BufferedWriter(new FileWriter(args[2]))));
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
/*  89 */     OrthDomainAlignment.main(args);
/*     */   }
/*     */   
/*     */ 
/*     */   public static void main(String[] args)
/*     */     throws Exception
/*     */   {
/*  96 */     forester.tree.Tree tree = TreeHelper.readNHtree(new File(args[1]));
/*  97 */     Set allowedTaxa = new TreeSet();
/*  98 */     SheetIO.toCollection(SheetIO.getColumn(SheetIO.read(new File(args[0]), "\t"), 0), allowedTaxa);
/*     */     
/*     */ 
/* 101 */     File out = new File("align");
/* 102 */     File[] files = new File("overallAlign").listFiles();
/* 103 */     for (int i = 0; i < files.length; i++) {
/* 104 */       Alignment align = new ReadAlignment(files[i].getAbsolutePath());
/* 105 */       Alignment align1 = getReducedAlignment(allowedTaxa, MaxLikelihoodTree.convert(tree), align);
/* 106 */       if (align1 == null) {
/* 107 */         System.err.println("nothing left for " + files[i]);
/*     */       }
/*     */       else {
/* 110 */         PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(new File(out, files[i].getName()))));
/* 111 */         AlignmentUtils.print(align1, pw);
/* 112 */         pw.close();
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
/* 123 */     Set duplSet = new HashSet();
/* 124 */     for (Iterator it = map.keySet().iterator(); it.hasNext();) {
/* 125 */       Object key = it.next();
/* 126 */       if (map.get(key) == null) duplSet.add(key);
/*     */     }
/* 128 */     if (duplSet.size() > 0) {
/* 129 */       System.err.println("duplication " + duplSet);
/* 130 */       String[] names = new String[duplSet.size()];
/* 131 */       duplSet.toArray(names);
/* 132 */       Node[] extNodes = NodeUtils.findByIdentifier(tree.getRoot(), names);
/* 133 */       Node lca = NodeUtils.getFirstCommonAncestor(extNodes);
/* 134 */       Node[] external = NodeUtils.getExternalNodes(lca);
/* 135 */       for (int i = 0; i < external.length; i++) {
/* 136 */         if (map.containsKey(external[i].getIdentifier().getName())) {
/* 137 */           duplSet.add(external[i].getIdentifier().getName());
/* 138 */           map.remove(external[i].getIdentifier().getName());
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
/* 159 */       Map map = new TreeMap();
/* 160 */       for (int j = 0; j < align.getIdCount(); j++) {
/* 161 */         if (allowedTaxa.contains(align.getIdentifier(j).getName())) {
/* 162 */           if (map.containsKey(align.getIdentifier(j).getName())) {
/* 163 */             map.put(align.getIdentifier(j).getName(), null);
/*     */           }
/*     */           else
/* 166 */             map.put(align.getIdentifier(j).getName(), align.getAlignedSequenceString(j));
/*     */         }
/*     */       }
/* 169 */       removeLCADescendantsDuplication(map, taxonTree);
/*     */       
/* 171 */       if (map.keySet().size() > 3) {
/* 172 */         String[] st = new String[map.keySet().size()];
/* 173 */         Identifier[] ids = new Identifier[map.keySet().size()];
/* 174 */         Iterator it = map.keySet().iterator();
/* 175 */         for (int j = 0; j < ids.length; j++) {
/* 176 */           String key = (String)it.next();
/* 177 */           ids[j] = new Identifier(key);
/* 178 */           st[j] = ((String)map.get(key));
/*     */         }
/* 180 */         return new SimpleAlignment(ids, st, align.getDataType());
/*     */       }
/*     */     }
/*     */     catch (Exception exc) {
/* 184 */       exc.printStackTrace();
/*     */     }
/*     */     
/* 187 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static void filterFragmentedProteins(Set fragmentedProtIds, Iterator pfamseqToArch, PrintWriter out)
/*     */   {
/* 197 */     while (pfamseqToArch.hasNext()) {
/* 198 */       List l = (List)pfamseqToArch.next();
/*     */       try {
/* 200 */         if (!fragmentedProtIds.contains(l.get(0))) {
/* 201 */           for (Iterator it = l.iterator(); it.hasNext();)
/*     */           {
/* 203 */             out.print(it.next());
/* 204 */             out.print(it.hasNext() ? "\t" : "\n");
/*     */           }
/*     */         }
/*     */       } catch (Exception exc) {
/* 208 */         exc.printStackTrace();
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public static Set getFragmentedProteinsFromPfamseq(File pfamseq)
/*     */     throws Exception
/*     */   {
/* 216 */     Set ids = new HashSet();
/* 217 */     BufferedReader br = new BufferedReader(new FileReader(pfamseq));
/* 218 */     System.err.println(pfamseq);
/* 219 */     for (SequenceIterator seqIt = SeqIOTools.readFastaProtein(br); seqIt.hasNext();) {
/* 220 */       Sequence seq = seqIt.nextSequence();
/* 221 */       if (((String)seq.getAnnotation().getProperty("description")).indexOf("Fragment") >= 0)
/*     */       {
/* 223 */         ids.add(seq.getName());
/*     */       }
/*     */     }
/*     */     
/* 227 */     return ids;
/*     */   }
/*     */ }


/* Location:              /home/lachlan/Desktop/psilc1.21/psilc.jar!/lc1/treefam/OrthDomains.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */