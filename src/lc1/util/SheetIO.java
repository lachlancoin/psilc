/*     */ package lc1.util;
/*     */ 
/*     */ import java.io.BufferedReader;
/*     */ import java.io.File;
/*     */ import java.io.FileNotFoundException;
/*     */ import java.io.FileReader;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStreamReader;
/*     */ import java.net.URL;
/*     */ import java.nio.channels.Channels;
/*     */ import java.nio.channels.ReadableByteChannel;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collection;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import java.util.TreeMap;
/*     */ 
/*     */ public class SheetIO
/*     */ {
/*     */   public static Iterator read(File f, String split)
/*     */     throws FileNotFoundException, IOException
/*     */   {
/*  28 */     return read(new BufferedReader(new FileReader(f)), split);
/*     */   }
/*     */   
/*     */   public static Iterator read(URL uri, String split) throws FileNotFoundException, IOException
/*     */   {
/*  33 */     return read(
/*  34 */       new BufferedReader(new InputStreamReader(uri.openStream())), 
/*  35 */       split);
/*     */   }
/*     */   
/*     */   public static Iterator read(ReadableByteChannel sbc, String split) throws FileNotFoundException, IOException
/*     */   {
/*  40 */     return read(new BufferedReader(new InputStreamReader(
/*  41 */       Channels.newInputStream(sbc))), split);
/*     */   }
/*     */   
/*     */   public static Iterator read(BufferedReader br, String split) throws FileNotFoundException, IOException
/*     */   {
/*  46 */     new Iterator() {
/*     */       String s;
/*     */       
/*     */       public boolean hasNext() {
/*  50 */         if (this.s == null) {
/*     */           try {
/*  52 */             SheetIO.this.close();
/*     */           } catch (Exception exc) {
/*  54 */             exc.printStackTrace();
/*     */           }
/*  56 */           return false;
/*     */         }
/*  58 */         return true;
/*     */       }
/*     */       
/*     */       public void remove() {}
/*     */       
/*     */       public Object next()
/*     */       {
/*  65 */         String[] st = this.s.split(this.val$split);
/*     */         try {
/*  67 */           this.s = SheetIO.this.readLine();
/*     */         } catch (Exception exc) {
/*  69 */           this.s = null;
/*     */         }
/*  71 */         return Arrays.asList(st);
/*     */       }
/*     */       
/*     */       public void finalize() {
/*     */         try {
/*  76 */           SheetIO.this.close();
/*     */         } catch (Exception exc) {
/*  78 */           exc.printStackTrace();
/*     */         }
/*     */       }
/*     */     };
/*     */   }
/*     */   
/*     */   public static Iterator concatenateRows(Iterator it)
/*     */   {
/*  86 */     new Iterator() {
/*  87 */       Iterator row = ((List)SheetIO.this.next()).iterator();
/*     */       
/*     */       public boolean hasNext() {
/*  90 */         return (this.row.hasNext()) || (SheetIO.this.hasNext());
/*     */       }
/*     */       
/*     */       public void remove() {}
/*     */       
/*     */       public Object next()
/*     */       {
/*  97 */         if (!this.row.hasNext())
/*  98 */           this.row = ((List)SheetIO.this.next()).iterator();
/*  99 */         return this.row.next();
/*     */       }
/*     */     };
/*     */   }
/*     */   
/*     */   public static Iterator read(File[] f, String split) throws FileNotFoundException, IOException
/*     */   {
/* 106 */     new Iterator()
/*     */     {
/*     */       int mark;
/*     */       Iterator inner;
/*     */       
/*     */       public boolean hasNext() {
/* 112 */         return (this.inner.hasNext()) || (this.mark < SheetIO.this.length - 1);
/*     */       }
/*     */       
/*     */       public void remove() {
/* 116 */         this.inner.remove();
/*     */       }
/*     */       
/*     */       public Object next() {
/* 120 */         if (!this.inner.hasNext()) {
/* 121 */           this.mark += 1;
/*     */           try {
/* 123 */             this.inner = SheetIO.read(SheetIO.this[this.mark], this.val$split);
/*     */           } catch (Exception exc) {
/* 125 */             exc.printStackTrace();
/*     */           }
/*     */         }
/* 128 */         return this.inner.next();
/*     */       }
/*     */     };
/*     */   }
/*     */   
/*     */   public static List[] split(Iterator it, int col)
/*     */   {
/* 135 */     Map pos = new TreeMap();
/* 136 */     while (it.hasNext()) {
/* 137 */       List row = (List)it.next();
/* 138 */       Object o = row.get(col);
/*     */       List sh;
/* 140 */       List sh; if (pos.containsKey(o)) {
/* 141 */         sh = (List)pos.get(o);
/*     */       } else {
/* 143 */         sh = new ArrayList();
/* 144 */         pos.put(o, sh);
/*     */       }
/* 146 */       sh.add(row);
/*     */     }
/* 148 */     List[] sheet = new List[pos.values().size()];
/* 149 */     pos.values().toArray(sheet);
/*     */     
/*     */ 
/* 152 */     return sheet;
/*     */   }
/*     */   
/*     */   public static Iterator removeIfSizeNotEqual(Iterator it, int col) {
/* 156 */     new ExcludeIterator(it) {
/*     */       public boolean include(Object[] row) {
/* 158 */         return row.length == this.val$col;
/*     */       }
/*     */     };
/*     */   }
/*     */   
/*     */   public static Iterator onlyRows(Iterator it, int col, String ent)
/*     */   {
/* 165 */     new ExcludeIterator(it) {
/*     */       public boolean include(Object[] r) {
/* 167 */         return (r.length > this.val$col) && (r[this.val$col].equals(this.val$ent));
/*     */       }
/*     */     };
/*     */   }
/*     */   
/*     */   public static Iterator exclRowsWhichContain(Iterator it, int col, String ent)
/*     */   {
/* 174 */     new ExcludeIterator(it) {
/*     */       public boolean include(Object[] r) {
/* 176 */         return (r.length <= this.val$col) || (!r[this.val$col].equals(this.val$ent));
/*     */       }
/*     */     };
/*     */   }
/*     */   
/*     */   public static Iterator allColumnsGreaterThan(Iterator it, int i)
/*     */   {
/* 183 */     new RowFunctionIterator(it) {
/*     */       public Object rowFunction(List row) {
/* 185 */         String[] next = new String[row.size() - this.val$i];
/* 186 */         for (int j = 0; j < next.length; j++) {
/* 187 */           next[j] = ((String)row.get(this.val$i + j));
/*     */         }
/* 189 */         return Arrays.asList(next);
/*     */       }
/*     */     };
/*     */   }
/*     */   
/*     */   public static Iterator minMax(Iterator l, int min, int max) {
/* 195 */     for (int j = 0; j < min; j++) {
/* 196 */       if (!l.hasNext())
/*     */         break;
/* 198 */       l.next();
/*     */     }
/* 200 */     new Iterator() {
/*     */       int j;
/*     */       
/*     */       public boolean hasNext() {
/* 204 */         return (this.j < this.val$max) && (this.val$l.hasNext());
/*     */       }
/*     */       
/*     */       public void remove() {}
/*     */       
/*     */       public Object next()
/*     */       {
/* 211 */         return this.val$l.next();
/*     */       }
/*     */     };
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static File[] getFiles(File arg, String append)
/*     */     throws FileNotFoundException, IOException
/*     */   {
/* 223 */     String[] args = getArgs(arg);
/* 224 */     File[] files = new File[args.length];
/* 225 */     for (int i = 0; i < files.length; i++) {
/* 226 */       files[i] = new File(args[i] + append);
/*     */     }
/* 228 */     return files;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static String[] getArgs(File arg)
/*     */     throws FileNotFoundException, IOException
/*     */   {
/* 237 */     Iterator arg_sheet = read(arg, " ");
/* 238 */     List l = new ArrayList();
/* 239 */     while (arg_sheet.hasNext())
/*     */     {
/* 241 */       l.add(((List)arg_sheet.next()).get(0));
/*     */     }
/* 243 */     String[] st = new String[l.size()];
/* 244 */     l.toArray(st);
/* 245 */     return st;
/*     */   }
/*     */   
/*     */   public static File[] getFiles(File directory, String[] args) throws FileNotFoundException, IOException
/*     */   {
/* 250 */     File[] files = new File[args.length];
/* 251 */     for (int i = 0; i < files.length; i++) {
/* 252 */       files[i] = new File(directory, args[i]);
/*     */     }
/* 254 */     return files;
/*     */   }
/*     */   
/*     */   public static File[] getFiles(File arg) throws FileNotFoundException, IOException
/*     */   {
/* 259 */     return getFiles(arg, "");
/*     */   }
/*     */   
/*     */   public static Iterator[] readSep(File dir, String[] args, String spl) throws FileNotFoundException, IOException
/*     */   {
/* 264 */     Iterator[] sh = new Iterator[args.length];
/* 265 */     for (int i = 0; i < args.length; i++) {
/* 266 */       sh[i] = read(new File(dir, args[i]), spl);
/*     */     }
/* 268 */     return sh;
/*     */   }
/*     */   
/*     */   public static Iterator getColumn(Iterator it, int col) {
/* 272 */     new RowFunctionIterator(it)
/*     */     {
/*     */       public Object rowFunction(List obj) {
/* 275 */         return obj.get(this.val$col);
/*     */       }
/*     */     };
/*     */   }
/*     */   
/*     */   public static void toMap(Iterator it, int col1, int col2, Map emptyMap) {
/* 281 */     while (it.hasNext()) {
/* 282 */       List l = (List)it.next();
/* 283 */       emptyMap.put(l.get(col1), l.get(col2));
/*     */     }
/*     */   }
/*     */   
/*     */   public static Map toMapSet(Iterator it, int col1, int col2) {
/* 288 */     Map m = new HashMap();
/* 289 */     while (it.hasNext()) {
/* 290 */       List l = (List)it.next();
/* 291 */       Object key = l.get(col1);
/*     */       Set s;
/* 293 */       Set s; if (m.containsKey(key)) {
/* 294 */         s = (Set)m.get(key);
/*     */       } else {
/* 296 */         s = new HashSet();
/* 297 */         m.put(key, s);
/*     */       }
/* 299 */       s.add(l.get(col2));
/*     */     }
/*     */     
/* 302 */     return m;
/*     */   }
/*     */   
/*     */   public static Collection toCollection(Iterator it, Collection l) {
/* 306 */     while (it.hasNext()) {
/* 307 */       Object o = it.next();
/*     */       
/* 309 */       l.add(o);
/*     */     }
/*     */     
/* 312 */     return l;
/*     */   }
/*     */   
/*     */   public static abstract class ExcludeIterator implements Iterator
/*     */   {
/*     */     final Iterator it;
/*     */     Object[] row;
/*     */     
/*     */     public ExcludeIterator(Iterator it) {
/* 321 */       this.it = it;
/* 322 */       prepareNext();
/*     */     }
/*     */     
/*     */ 
/*     */     public boolean hasNext()
/*     */     {
/* 328 */       return this.row != null;
/*     */     }
/*     */     
/*     */     public void remove() {}
/*     */     
/*     */     public Object next()
/*     */     {
/* 335 */       Object[] res = (Object[])this.row.clone();
/* 336 */       prepareNext();
/* 337 */       return Arrays.asList(res);
/*     */     }
/*     */     
/*     */     public abstract boolean include(Object[] paramArrayOfObject);
/*     */     
/*     */     public void prepareNext() {
/* 343 */       List r = (List)this.it.next();
/* 344 */       while (!include(this.row)) {
/* 345 */         r = (List)this.it.next();
/* 346 */         if (r == null) {
/* 347 */           this.row = null;
/* 348 */           return;
/*     */         }
/*     */       }
/* 351 */       this.row = r.toArray();
/*     */     }
/*     */   }
/*     */   
/*     */   public static abstract class RowFunctionIterator implements Iterator {
/*     */     final Iterator it;
/*     */     
/*     */     public RowFunctionIterator(Iterator it) {
/* 359 */       this.it = it;
/*     */     }
/*     */     
/*     */     public boolean hasNext() {
/* 363 */       return this.it.hasNext();
/*     */     }
/*     */     
/*     */     public void remove() {}
/*     */     
/*     */     public Object next()
/*     */     {
/* 370 */       return rowFunction((List)this.it.next());
/*     */     }
/*     */     
/*     */     public abstract Object rowFunction(List paramList);
/*     */   }
/*     */ }


/* Location:              /home/lachlan/Desktop/psilc1.21/psilc.jar!/lc1/util/SheetIO.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */