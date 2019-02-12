/*     */ package lc1.phyl;
/*     */ 
/*     */ import java.io.BufferedWriter;
/*     */ import java.io.File;
/*     */ import java.io.FileWriter;
/*     */ import java.io.IOException;
/*     */ import java.io.PrintWriter;
/*     */ import java.io.RandomAccessFile;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collection;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import java.util.SortedMap;
/*     */ import java.util.TreeMap;
/*     */ import lc1.pfam.PfamAlphabet;
/*     */ import lc1.util.SheetIO;
/*     */ import org.apache.commons.cli.CommandLine;
/*     */ import org.biojava.bio.seq.io.SymbolTokenization;
/*     */ import org.biojava.bio.symbol.SimpleSymbolList;
/*     */ import org.biojava.bio.symbol.SymbolList;
/*     */ import pal.alignment.Alignment;
/*     */ import pal.alignment.AlignmentUtils;
/*     */ import pal.alignment.ConcatenatedAlignment;
/*     */ import pal.alignment.ReadAlignment;
/*     */ import pal.alignment.SimpleAlignment;
/*     */ import pal.alignment.SitePattern;
/*     */ import pal.alignment.StrippedAlignment;
/*     */ import pal.datatype.AminoAcids;
/*     */ import pal.distance.AlignmentDistanceMatrix;
/*     */ import pal.distance.DistanceMatrix;
/*     */ import pal.distance.PairwiseDistance;
/*     */ import pal.misc.IdGroup;
/*     */ import pal.misc.Identifier;
/*     */ import pal.misc.SimpleIdGroup;
/*     */ import pal.substmodel.SubstitutionModel.Utils;
/*     */ import pal.substmodel.WAG;
/*     */ import pal.tree.Tree;
/*     */ import pal.tree.TreeUtils;
/*     */ 
/*     */ public class SpeciesDistanceMatrix
/*     */   extends DistanceMatrix
/*     */ {
/*     */   Map taxonMap;
/*     */   File directory;
/*     */   RandomAccessFile archFile;
/*     */   Map id2Arch;
/*     */   PfamAlphabet alph;
/*     */   SymbolTokenization parser;
/*     */   
/*     */   static void printThinnedtaxonFileMap(File out, Map m, int min, Iterator it)
/*     */     throws Exception
/*     */   {
/*  58 */     PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(out)));
/*  59 */     while (it.hasNext()) {
/*  60 */       Object key = it.next();
/*  61 */       if (m.containsKey(key)) {
/*  62 */         Collection l = (Collection)m.get(key);
/*  63 */         if (l.size() > min) {
/*  64 */           pw.print(key);pw.print("\t");
/*  65 */           for (Iterator it1 = l.iterator(); it1.hasNext(); 
/*  66 */               pw.print(" ")) { pw.print(it1.next());
/*     */           }
/*  68 */           pw.println();
/*     */         }
/*     */       } }
/*  71 */     pw.close();
/*     */   }
/*     */   
/*  74 */   static File taxonFileMap = new File("taxon_file");
/*     */   
/*  76 */   static SortedMap getTaxonToFileMap(File f) throws Exception { SortedMap m = new TreeMap();
/*     */     
/*  78 */     if ((taxonFileMap.exists()) && (taxonFileMap.length() > 0L)) {
/*  79 */       for (Iterator it = SheetIO.read(taxonFileMap, "\t"); it.hasNext();) {
/*  80 */         List row = (List)it.next();
/*     */         
/*     */ 
/*  83 */         m.put(row.get(0), Arrays.asList(((String)row.get(1)).split(" ")));
/*     */       }
/*     */     }
/*     */     else
/*     */     {
/*  88 */       File[] files = f.listFiles();
/*  89 */       for (int i = 0; i < files.length; i++) {
/*     */         try
/*     */         {
/*  92 */           Alignment align = new ReadAlignment(files[i].getAbsolutePath());
/*  93 */           if ((align != null) && (align.getIdCount() >= 3))
/*  94 */             for (int j = 0; j < align.getIdCount(); j++) {
/*  95 */               if (align.getIdentifier(j) == null) break;
/*  96 */               String taxon = align.getIdentifier(j).getName();
/*  97 */               if (!m.containsKey(taxon)) m.put(taxon, new HashSet());
/*  98 */               String id = files[i].getName().split("_")[0];
/*  99 */               ((Collection)m.get(taxon)).add(id);
/*     */             }
/*     */         } catch (Exception exc) {
/* 102 */           exc.printStackTrace();
/*     */         }
/*     */       }
/*     */       
/* 106 */       PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(taxonFileMap)));
/* 107 */       for (Iterator it = m.keySet().iterator(); it.hasNext();) {
/* 108 */         Object key = it.next();
/* 109 */         pw.print(key);pw.print("\t");
/* 110 */         Collection l = (Collection)m.get(key);
/* 111 */         for (Iterator it1 = l.iterator(); it1.hasNext(); 
/* 112 */             pw.print(" ")) { pw.print(it1.next());
/*     */         }
/* 114 */         pw.println();
/*     */       }
/* 116 */       pw.close();
/*     */     }
/* 118 */     return m;
/*     */   }
/*     */   
/*     */   private static IdGroup getIdGroup(Collection c) {
/* 122 */     Identifier[] ids = new Identifier[c.size()];
/* 123 */     int i = 0;
/* 124 */     for (Iterator it = c.iterator(); it.hasNext(); i++) {
/* 125 */       ids[i] = new Identifier((String)it.next());
/*     */     }
/* 127 */     return new SimpleIdGroup(ids);
/*     */   }
/*     */   
/*     */   private Map getArchitectureToPositionMap(RandomAccessFile f) {
/* 131 */     Map map = new HashMap();
/*     */     try {
/* 133 */       long pos = f.getFilePointer();
/*     */       
/* 135 */       String s = f.readLine();
/* 136 */       while (s != null) {
/* 137 */         map.put(new Integer(Integer.parseInt(s.split("\t")[0])), new Long(pos));
/* 138 */         pos = f.getFilePointer();
/* 139 */         s = f.readLine();
/*     */       }
/*     */     } catch (IOException exc) {
/* 142 */       exc.printStackTrace();
/*     */     }
/* 144 */     return map;
/*     */   }
/*     */   
/*     */   private SymbolList getSymbolListForId(int id) throws Exception
/*     */   {
/* 149 */     this.archFile.seek(((Integer)this.id2Arch.get(new Integer(id))).intValue());
/* 150 */     String[] syms = this.archFile.readLine().split("\t")[0].split(" ");
/* 151 */     List l = new ArrayList(syms.length);
/* 152 */     for (int i = 0; i < syms.length; i++) {
/* 153 */       l.add(this.parser.parseToken(syms[i]));
/*     */     }
/* 155 */     return new SimpleSymbolList(this.alph, l);
/*     */   }
/*     */   
/*     */   private static SitePattern getConcatenatedAlignment(File directory, Collection files, Identifier[] ids) throws Exception {
/* 159 */     Alignment[] align = new Alignment[files.size()];
/* 160 */     int i = 0;
/* 161 */     for (Iterator it = files.iterator(); it.hasNext(); i++) {
/* 162 */       Object next = it.next();
/* 163 */       Alignment align_inner = new ReadAlignment(directory.getAbsolutePath() + "/" + next);
/*     */       
/*     */ 
/*     */ 
/* 167 */       String[] seqs = {
/* 168 */         align_inner.getAlignedSequenceString(align_inner.whichIdNumber(ids[0].getName())), 
/* 169 */         align_inner.getAlignedSequenceString(align_inner.whichIdNumber(ids[1].getName())) };
/*     */       
/* 171 */       align[i] = 
/* 172 */         new SimpleAlignment(ids, seqs, AminoAcids.DEFAULT_INSTANCE);
/*     */     }
/* 174 */     StrippedAlignment result = new StrippedAlignment(new ConcatenatedAlignment(align));
/* 175 */     result.removeGaps();
/* 176 */     return SitePattern.getSitePattern(result);
/*     */   }
/*     */   
/*     */   public static void main1(String[] args) throws Exception
/*     */   {
/* 181 */     File[] align = new File("align").listFiles();
/* 182 */     File treeF = new File("tree");
/* 183 */     for (int i = 0; i < align.length; i++) {
/*     */       try {
/* 185 */         Alignment align_i = new ReadAlignment(align[i].getAbsolutePath());
/* 186 */         if (align_i.getIdCount() > 3) {
/* 187 */           Tree tree = new NeighborJoiningTree(new AlignmentDistanceMatrix(SitePattern.getSitePattern(align_i), 
/* 188 */             SubstitutionModel.Utils.createSubstitutionModel(new WAG(AlignmentUtils.estimateFrequencies(align_i)))));
/* 189 */           PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(new File(treeF, align[i].getName()))));
/* 190 */           TreeUtils.printNH(tree, pw);
/* 191 */           pw.close();
/*     */         }
/*     */       }
/*     */       catch (Exception exc) {
/* 195 */         exc.printStackTrace();
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static void main(String[] args)
/*     */     throws Exception
/*     */   {
/* 207 */     File f = new File("align");
/* 208 */     Map taxonMap = getTaxonToFileMap(f);
/*     */     
/* 210 */     printThinnedtaxonFileMap(new File("thinnedTaxonMap"), taxonMap, 500, taxonMap.keySet().iterator());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static void main(CommandLine params)
/*     */     throws Exception
/*     */   {
/* 219 */     String[] outputSt = params.getOptionValues("dir");
/*     */     
/* 221 */     File output = new File(outputSt[0]);
/* 222 */     int start = Integer.parseInt(params.getOptionValue("min")) - 1;
/* 223 */     int step = Integer.parseInt(params.getOptionValue("step"));
/* 224 */     start *= step;
/* 225 */     int end = start + step;
/* 226 */     File distM = new File(output, outputSt.length > 1 ? outputSt[1] : "distM");
/* 227 */     taxonFileMap = new File(output, "thinnedTaxonMap");
/*     */     
/* 229 */     File f = new File(output, "align");
/* 230 */     Map taxonMap = getTaxonToFileMap(f);
/*     */     
/* 232 */     SpeciesDistanceMatrix dm = new SpeciesDistanceMatrix(f, taxonMap);
/*     */     
/*     */ 
/*     */ 
/* 236 */     for (int i = start; i < Math.min(end, dm.getIdCount()); i++)
/*     */     {
/* 238 */       PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(new File(distM, dm.getIdentifier(i).getName()))));
/* 239 */       for (int j = i + 1; j < dm.getIdCount(); j++) {
/* 240 */         double dist = dm.getDistanceNew(i, j);
/* 241 */         if ((dist == 100.0D) && 
/* 242 */           (dist < 100.0D))
/*     */         {
/*     */ 
/*     */ 
/* 246 */           pw.print(dm.getIdentifier(i));pw.print("\t");
/* 247 */           pw.print(dm.getIdentifier(j));pw.print("\t");
/* 248 */           pw.println(dist);
/* 249 */           pw.flush();
/*     */         }
/*     */       }
/*     */       
/* 253 */       pw.close();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public Collection getCommonAlignments(String id1, String id2)
/*     */   {
/* 262 */     Collection files_i = (Collection)this.taxonMap.get(id1);
/* 263 */     Collection files_j = new HashSet((Collection)this.taxonMap.get(id2));
/* 264 */     files_j.retainAll(files_i);
/* 265 */     return files_j;
/*     */   }
/*     */   
/*     */   public double getDistanceNew(int i, int j) {
/* 269 */     if (i == j) return 0.0D;
/* 270 */     Collection files_j = getCommonAlignments(getIdentifier(i).getName(), getIdentifier(j).getName());
/*     */     try {
/* 272 */       if (files_j.size() != 0)
/*     */       {
/*     */ 
/* 275 */         SitePattern sp = getConcatenatedAlignment(this.directory, files_j, 
/* 276 */           new Identifier[] { getIdentifier(i), getIdentifier(j) });
/*     */         
/* 278 */         if (sp.getSiteCount() < 10) return 100.0D;
/* 279 */         PairwiseDistance pwd = new PairwiseDistance(sp);
/* 280 */         return pwd.getDistance(0, 1);
/*     */       }
/*     */     } catch (Exception exc) {
/* 283 */       exc.printStackTrace();
/* 284 */       System.exit(0);
/*     */     }
/* 286 */     return 100.0D;
/*     */   }
/*     */   
/*     */   private SpeciesDistanceMatrix(File directory, Map taxonMap, IdGroup ids) throws Exception {
/* 290 */     super(new double[ids.getIdCount()][ids.getIdCount()], ids);
/*     */     
/*     */ 
/* 293 */     this.taxonMap = taxonMap;
/* 294 */     this.directory = directory;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public SpeciesDistanceMatrix(File directory, Map taxonMap)
/*     */     throws Exception
/*     */   {
/* 302 */     this(directory, taxonMap, getIdGroup(taxonMap.keySet()));
/*     */   }
/*     */ }


/* Location:              /home/lachlan/Desktop/psilc1.21/psilc.jar!/lc1/phyl/SpeciesDistanceMatrix.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */