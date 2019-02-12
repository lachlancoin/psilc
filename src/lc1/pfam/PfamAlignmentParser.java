/*     */ package lc1.pfam;
/*     */ 
/*     */ import java.io.BufferedInputStream;
/*     */ import java.io.File;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.PrintStream;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashSet;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import java.util.TreeMap;
/*     */ import java.util.TreeSet;
/*     */ import lc1.util.Print;
/*     */ import lc1.util.SheetIO;
/*     */ import org.biojava.bio.Annotation;
/*     */ import org.biojava.bio.BioException;
/*     */ import org.biojava.bio.SimpleAnnotation;
/*     */ import org.biojava.bio.alignment.FlexibleAlignment;
/*     */ import org.biojava.bio.dist.Distribution;
/*     */ import org.biojava.bio.dist.DistributionTools;
/*     */ import org.biojava.bio.seq.DNATools;
/*     */ import org.biojava.bio.seq.GappedSequence;
/*     */ import org.biojava.bio.seq.ProteinTools;
/*     */ import org.biojava.bio.seq.RNATools;
/*     */ import org.biojava.bio.seq.Sequence;
/*     */ import org.biojava.bio.seq.db.SequenceDB;
/*     */ import org.biojava.bio.seq.impl.SimpleGappedSequence;
/*     */ import org.biojava.bio.seq.impl.SimpleSequence;
/*     */ import org.biojava.bio.seq.io.SeqIOTools;
/*     */ import org.biojava.bio.symbol.BasisSymbol;
/*     */ import org.biojava.bio.symbol.FiniteAlphabet;
/*     */ import org.biojava.bio.symbol.GappedSymbolList;
/*     */ import org.biojava.bio.symbol.SimpleSymbolList;
/*     */ import org.biojava.bio.symbol.Symbol;
/*     */ import org.biojava.bio.symbol.SymbolList;
/*     */ import org.biojava.bio.symbol.SymbolListViews;
/*     */ import pal.alignment.SimpleAlignment;
/*     */ import pal.datatype.AminoAcids;
/*     */ import pal.datatype.Codons;
/*     */ import pal.datatype.DataType;
/*     */ import pal.misc.Identifier;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class PfamAlignmentParser
/*     */ {
/*     */   public static pal.alignment.Alignment parse(Iterator seqIt, DataType dt)
/*     */   {
/*     */     try
/*     */     {
/*  63 */       List sequences = new ArrayList();
/*  64 */       List nameSet = new ArrayList();
/*  65 */       while (seqIt.hasNext())
/*     */       {
/*     */         try {
/*  68 */           String[] sequ = (String[])seqIt.next();
/*  69 */           if (sequ == null) throw new Exception("null sequence ");
/*     */         }
/*     */         catch (BioException e) {
/*  72 */           System.out.print("Warning sequence not included: ");e.printStackTrace();
/*  73 */           continue; }
/*     */         String[] sequ;
/*  75 */         if ((sequ[1].length() != 0) && (!sequ[0].startsWith("#")))
/*     */         {
/*     */ 
/*     */ 
/*     */ 
/*  80 */           nameSet.add(new Identifier(sequ[0]));
/*     */           
/*     */ 
/*  83 */           sequences.add(sequ[1]);
/*     */         } }
/*  85 */       String[] seqs = new String[sequences.size()];
/*  86 */       Identifier[] names = new Identifier[sequences.size()];
/*  87 */       sequences.toArray(seqs);
/*  88 */       nameSet.toArray(names);
/*  89 */       return new SimpleAlignment(names, seqs, "-", dt);
/*     */     }
/*     */     catch (Throwable t)
/*     */     {
/*  93 */       t.printStackTrace(); }
/*  94 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */   public static pal.alignment.Alignment dnaToCodons(FlexibleAlignment al1)
/*     */   {
/* 100 */     Iterator it = al1.getLabels().iterator();
/* 101 */     Iterator seqIt1 = new Iterator()
/*     */     {
/*     */ 
/* 104 */       public boolean hasNext() { return PfamAlignmentParser.this.hasNext(); }
/*     */       
/*     */       public void remove() {}
/*     */       
/*     */       public Object next() {
/* 109 */         try { String label = (String)PfamAlignmentParser.this.next();
/* 110 */           GappedSymbolList dna = (GappedSymbolList)this.val$al1.symbolListForLabel(label);
/* 111 */           if (!dna.getAlphabet().equals(DNATools.getDNA())) throw new Exception("incorrect alphabet");
/* 112 */           List symbols = new ArrayList();
/* 113 */           List gaps = new ArrayList();
/* 114 */           Symbol gap = DNATools.getDNA().getGapSymbol();
/* 115 */           Symbol pGap = DNATools.getCodonAlphabet().getGapSymbol();
/* 116 */           SymbolList wsl = SymbolListViews.windowedSymbolList(dna, 3);
/* 117 */           for (int i = 1; i <= dna.length(); i += 3) {
/* 118 */             int k = (i - 1) / 3 + 1;
/*     */             
/* 120 */             if ((dna.symbolAt(i).getName().equals("gap")) || (dna.symbolAt(i).getName().equals("[]")) || 
/* 121 */               (dna.symbolAt(i + 1).getName().equals("gap")) || (dna.symbolAt(i + 1).getName().equals("[]")) || 
/* 122 */               (dna.symbolAt(i + 2).getName().equals("gap")) || (dna.symbolAt(i + 2).getName().equals("[]"))) {
/* 123 */               gaps.add(new Integer(k));
/*     */             }
/* 125 */             else if ((dna.symbolAt(i).equals(DNATools.t())) && (dna.symbolAt(i + 1).equals(DNATools.g())) && 
/* 126 */               (dna.symbolAt(i + 2).equals(DNATools.a()))) {
/* 127 */               gaps.add(new Integer(k));
/*     */             }
/*     */             else {
/* 130 */               symbols.add(wsl.symbolAt(k));
/*     */             }
/*     */           }
/* 133 */           Symbol[] syms = new Symbol[symbols.size()];
/* 134 */           symbols.toArray(syms);
/* 135 */           GappedSequence dna3 = new SimpleGappedSequence(
/* 136 */             new SimpleSequence(
/* 137 */             new SimpleSymbolList(syms, syms.length, DNATools.getCodonAlphabet()), 
/* 138 */             label, label, new SimpleAnnotation()));
/* 139 */           for (Iterator gapIt = gaps.iterator(); gapIt.hasNext();) {
/* 140 */             int pos = ((Integer)gapIt.next()).intValue();
/* 141 */             dna3.addGapInView(pos);
/*     */           }
/*     */           
/*     */ 
/* 145 */           return new String[] { label, PfamAlignmentParser.getStringFromCodons(dna3) };
/* 146 */         } catch (Exception exc) { exc.printStackTrace(); } return null;
/*     */       }
/* 148 */     };
/* 149 */     pal.alignment.Alignment al = parse(seqIt1, Codons.DEFAULT_INSTANCE);
/*     */     
/* 151 */     return al;
/*     */   }
/*     */   
/*     */   private static String getStringFromCodons(SymbolList dna3) {
/* 155 */     char[] ch = new char[dna3.length()];
/* 156 */     for (int j = 1; j <= dna3.length(); j++) {
/* 157 */       if (!(dna3.symbolAt(j) instanceof BasisSymbol)) {
/* 158 */         ch[(j - 1)] = '?';
/*     */       }
/*     */       else {
/* 161 */         List symL = ((BasisSymbol)dna3.symbolAt(j)).getSymbols();
/* 162 */         char[] c = new char[3];
/* 163 */         for (int ij = 0; ij < 3; ij++) {
/* 164 */           c[ij] = ((Symbol)symL.get(ij)).getName().charAt(0);
/* 165 */           if (c[ij] == '[') {
/* 166 */             ch[(j - 1)] = '?';
/* 167 */             break;
/*     */           }
/*     */         }
/*     */         
/* 171 */         ch[(j - 1)] = Codons.DEFAULT_INSTANCE.getChar(Codons.getCodonIndexFromNucleotides(c));
/*     */       }
/*     */     }
/* 174 */     return new String(ch);
/*     */   }
/*     */   
/*     */   public static pal.alignment.Alignment parse(pal.alignment.Alignment al1, File dna_fasta_file)
/*     */     throws IOException, BioException
/*     */   {
/* 180 */     SequenceDB seqDB = SeqIOTools.readFasta(new BufferedInputStream(new FileInputStream(dna_fasta_file)), DNATools.getDNA());
/*     */     
/* 182 */     Iterator seqIt1 = new Iterator() {
/*     */       int i;
/*     */       
/* 185 */       public boolean hasNext() { return this.i < PfamAlignmentParser.this.getSequenceCount(); }
/*     */       
/*     */       public void remove() {}
/*     */       
/*     */       public Object next() {
/* 190 */         try { String label = PfamAlignmentParser.this.getIdentifier(this.i).getName();
/* 191 */           Sequence dna = this.val$seqDB.getSequence(label);
/*     */           
/* 193 */           String prot = PfamAlignmentParser.this.getAlignedSequenceString(this.i);
/* 194 */           SymbolList wsl = SymbolListViews.windowedSymbolList(dna, 3);
/* 195 */           String aminoStr = RNATools.translate(RNATools.transcribe(dna)).seqString();
/* 196 */           if (aminoStr.charAt(aminoStr.length() - 1) == '*') wsl = wsl.subList(1, wsl.length() - 1);
/* 197 */           GappedSequence dna3 = new SimpleGappedSequence(
/* 198 */             new SimpleSequence(wsl, 
/* 199 */             dna.getName(), dna.getName(), dna.getAnnotation()));
/*     */           
/* 201 */           int gap_start = 1;
/* 202 */           for (int i = 0; i < prot.length(); i++) {
/* 203 */             if ((prot.charAt(i) == '?') || (prot.charAt(i) == '-')) {
/* 204 */               dna3.addGapInView(i + 1);
/*     */             }
/*     */           }
/* 207 */           if (dna3.length() < prot.length()) {
/* 208 */             dna3.addGapsInView(dna3.length(), prot.length() - dna3.length());
/*     */           }
/* 210 */           this.i += 1;
/* 211 */           String str = PfamAlignmentParser.getStringFromCodons(dna3);
/* 212 */           return new String[] { label, str };
/* 213 */         } catch (Exception exc) { exc.printStackTrace(); } return null;
/*     */       }
/* 215 */     };
/* 216 */     pal.alignment.Alignment al = parse(seqIt1, Codons.DEFAULT_INSTANCE);
/*     */     
/* 218 */     return al;
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
/*     */   public static pal.alignment.Alignment parse(File alignment, String type)
/*     */   {
/* 232 */     String[] info_key = { "Name", "Start", "End" };
/*     */     
/*     */     try
/*     */     {
/* 236 */       if ((!type.equals("GB")) && (!type.startsWith("amino")) && (!type.startsWith("codons"))) {
/* 237 */         throw new Exception("type is not correct: " + type);
/*     */       }
/*     */       
/*     */ 
/*     */ 
/* 242 */       Iterator iter = new Iterator() {
/*     */         Iterator it;
/*     */         
/* 245 */         public boolean hasNext() { return this.it.hasNext(); }
/*     */         
/*     */         public void remove() {}
/*     */         
/* 249 */         public Object next() { List line = (List)this.it.next();
/*     */           try
/*     */           {
/* 252 */             String name = ((String)line.get(0)).trim();
/* 253 */             String prot_string = (String)line.get(1);
/* 254 */             prot_string = prot_string.replaceAll("\\p{Lower}", ".");
/* 255 */             Sequence seq = ProteinTools.createGappedProteinSequence(prot_string, name);
/* 256 */             Annotation annot = seq.getAnnotation();
/*     */             
/* 258 */             annot.setProperty(this.val$info_key[0], (String)line.get(0));
/*     */             
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 264 */             return new String[] { name, seq.seqString() };
/*     */           }
/*     */           catch (Exception e)
/*     */           {
/* 268 */             e.printStackTrace(); }
/* 269 */           return null;
/*     */         }
/*     */         
/*     */ 
/* 273 */       };
/* 274 */       return parse(iter, AminoAcids.DEFAULT_INSTANCE);
/*     */     }
/*     */     catch (Throwable t)
/*     */     {
/* 278 */       t.printStackTrace();
/*     */     }
/* 280 */     return null;
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
/*     */   public static void mutualInformation(org.biojava.bio.symbol.Alignment align, int start_col, PrintStream ps)
/*     */   {
/*     */     try
/*     */     {
/* 296 */       int len = align.length();
/* 297 */       Distribution[] d = DistributionTools.distOverAlignment(align, false, 0.0D);
/* 298 */       System.out.println(Print.toString(align));
/*     */       
/* 300 */       double[] ent = new double[len];
/* 301 */       for (int i = start_col; i <= len; i++)
/*     */       {
/* 303 */         ent[(i - 1)] = DistributionTools.bitsOfInformation(d[(i - 1)]);
/*     */       }
/* 305 */       for (int i = start_col; i <= len; i++)
/*     */       {
/* 307 */         double[] array = new double[len];
/* 308 */         for (int j = i + 1; j <= len; j++)
/*     */         {
/* 310 */           Distribution dij = DistributionTools.jointDistOverAlignment(align, false, 0.0D, new int[] { i, j });
/* 311 */           double m_ij = DistributionTools.bitsOfInformation(dij);
/* 312 */           array[(j - 1)] = (ent[(i - 1)] + ent[(j - 1)] - m_ij);
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 320 */         ps.println(Print.toString(array));
/* 321 */         ps.flush();
/*     */       }
/*     */     }
/*     */     catch (Throwable t)
/*     */     {
/* 326 */       t.printStackTrace();
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
/*     */   public static Group nonZeroElements(double[][] array, double thresh)
/*     */   {
/* 343 */     Group l = new Group();
/*     */     
/* 345 */     for (int i = 0; i < array.length; i++)
/*     */     {
/*     */ 
/* 348 */       for (int j = i + 1; j < array.length; j++)
/*     */       {
/*     */ 
/* 351 */         if (array[i][j] > thresh)
/*     */         {
/* 353 */           l.add(new ScoringPos(i, j, array[i][j]));
/*     */         }
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 360 */     return l;
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
/*     */   public static List partition(Group elements, int len)
/*     */   {
/* 373 */     List positions = new ArrayList();
/*     */     
/* 375 */     for (int i = 0; i < len; i++)
/*     */     {
/* 377 */       positions.add(new Integer(i + 1));
/*     */     }
/*     */     
/* 380 */     List groups = new ArrayList();
/* 381 */     int k = 0;
/*     */     
/* 383 */     while ((positions.size() > 0) && (elements.links.size() > 0))
/*     */     {
/*     */ 
/*     */ 
/* 387 */       Set group = new HashSet();
/* 388 */       List positions1 = new ArrayList();
/* 389 */       positions1.add(positions.get(0));
/* 390 */       group.add(positions.get(0));
/* 391 */       extend(elements, positions1, group);
/* 392 */       positions.removeAll(positions1);
/* 393 */       groups.add(group);
/*     */     }
/*     */     
/* 396 */     return groups;
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
/*     */   public static void extend(Group elements, List positions, Set inclPos)
/*     */   {
/* 410 */     if (elements.links.size() == 0)
/*     */     {
/*     */ 
/* 413 */       return;
/*     */     }
/*     */     
/*     */ 
/* 417 */     while (positions.size() > 0)
/*     */     {
/*     */ 
/* 420 */       Integer i = (Integer)positions.get(0);
/* 421 */       List eles1 = elements.getConnectedElements(i.intValue());
/* 422 */       positions.remove(i);
/*     */       
/* 424 */       if (eles1 != null)
/*     */       {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 430 */         inclPos.addAll(eles1);
/* 431 */         extend(elements, eles1, inclPos);
/* 432 */         positions.removeAll(eles1);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   static class ScoringPos
/*     */   {
/*     */     Integer pos1;
/*     */     
/*     */ 
/*     */ 
/*     */     Integer pos2;
/*     */     
/*     */ 
/*     */ 
/*     */     Integer[] position;
/*     */     
/*     */ 
/*     */     double score;
/*     */     
/*     */ 
/*     */ 
/*     */     ScoringPos(int p1, int p2, double s)
/*     */     {
/* 459 */       this.pos1 = new Integer(p1 + 1);
/* 460 */       this.pos2 = new Integer(p2 + 1);
/* 461 */       this.position = new Integer[] { this.pos1, this.pos2 };
/* 462 */       this.score = (Math.floor(s * 10.0D) / 10.0D);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     public String toString()
/*     */     {
/* 473 */       return new String("(" + this.pos1 + ", " + this.pos2 + ", " + this.score + ")");
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     public boolean equals(Object o)
/*     */     {
/* 487 */       return ((o instanceof ScoringPos)) && (((ScoringPos)o).pos1.equals(this.pos1)) && (((ScoringPos)o).pos2.equals(this.pos2));
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     public int hashCode()
/*     */     {
/* 498 */       return this.position.hashCode();
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
/*     */   static class Group
/*     */     extends HashSet
/*     */   {
/* 512 */     Map links = new TreeMap();
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     public String toString()
/*     */     {
/* 522 */       return this.links.toString();
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     public boolean add(Object o)
/*     */     {
/* 534 */       boolean res = super.add(o);
/* 535 */       PfamAlignmentParser.ScoringPos pos = (PfamAlignmentParser.ScoringPos)o;
/*     */       
/* 537 */       if (this.links.containsKey(pos.pos1))
/*     */       {
/* 539 */         ((Set)this.links.get(pos.pos1)).add(pos.pos2);
/*     */ 
/*     */       }
/*     */       else
/*     */       {
/* 544 */         Set s = new TreeSet();
/* 545 */         s.add(pos.pos2);
/* 546 */         this.links.put(pos.pos1, s);
/*     */       }
/*     */       
/* 549 */       return res;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     public void remove(int i, int j)
/*     */     {
/* 562 */       remove(new PfamAlignmentParser.ScoringPos(i, j, 0.0D));
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     public List getConnectedElements(int i)
/*     */     {
/* 578 */       Set set = (Set)this.links.get(new Integer(i));
/*     */       
/* 580 */       if (set == null)
/*     */       {
/*     */ 
/* 583 */         return null;
/*     */       }
/*     */       
/*     */ 
/* 587 */       Iterator it = set.iterator();
/* 588 */       this.links.remove(new Integer(i));
/*     */       
/* 590 */       while (it.hasNext())
/*     */       {
/* 592 */         remove(i, ((Integer)it.next()).intValue());
/*     */       }
/*     */       
/* 595 */       return new ArrayList(set);
/*     */     }
/*     */   }
/*     */ }


/* Location:              /home/lachlan/Desktop/psilc1.21/psilc.jar!/lc1/pfam/PfamAlignmentParser.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */