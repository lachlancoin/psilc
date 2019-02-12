/*     */ package lc1.dp;
/*     */ 
/*     */ import java.io.BufferedReader;
/*     */ import java.io.IOException;
/*     */ import java.util.StringTokenizer;
/*     */ import org.biojava.bio.BioException;
/*     */ import org.biojava.bio.seq.io.SymbolTokenization;
/*     */ import org.biojava.bio.symbol.FiniteAlphabet;
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
/*     */ public class HmmerProfileParser
/*     */ {
/*  62 */   private static final double log2 = Math.log(2.0D);
/*  63 */   private static final Double one = new Double(1.0D);
/*     */   
/*     */   protected FiniteAlphabet alph;
/*     */   
/*     */   protected DataType dt;
/*     */   SymbolTokenization tokenizer;
/*     */   
/*     */   protected HmmerProfileParser(FiniteAlphabet alph, DataType dt)
/*     */   {
/*  72 */     this.dt = dt;
/*     */     
/*  74 */     this.alph = alph;
/*     */     try {
/*  76 */       this.tokenizer = alph.getTokenization("token");
/*     */     } catch (BioException exc) {
/*  78 */       exc.printStackTrace();
/*     */     }
/*  80 */     this.dt = dt;
/*     */   }
/*     */   
/*  83 */   double[] evalueParams = null;
/*     */   protected String domain1;
/*     */   
/*  86 */   public double[] getEvalueParams() { return this.evalueParams; }
/*     */   
/*     */ 
/*     */ 
/*     */   public HmmerProfileParser(BufferedReader in, FiniteAlphabet alph, DataType dt, int insertRates, int matchRates)
/*     */   {
/*  92 */     this(alph, dt);
/*     */     try {
/*  94 */       in.readLine();
/*  95 */       String[] str = in.readLine().split("\\s+");
/*  96 */       setName(str[1]);
/*     */     } catch (IOException exc) {
/*  98 */       exc.printStackTrace();
/*     */     }
/* 100 */     parseModel(in, insertRates, matchRates);
/* 101 */     setProfileHMM();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public HmmerProfileHMM parse()
/*     */   {
/* 112 */     return getModel();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void setName(String domain)
/*     */   {
/* 121 */     this.domain1 = domain;
/*     */   }
/*     */   
/*     */   protected HmmerProfileHMM initialiseProfileHMM(int len, int insertRates, int matchRates) {
/*     */     try {
/* 126 */       return new HmmerProfileHMM(this.domain1, len, this.dt, insertRates, matchRates);
/*     */     } catch (Throwable t) {
/* 128 */       t.printStackTrace(); }
/* 129 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public HmmerProfileHMM getModel()
/*     */   {
/* 138 */     return this.hmm.hmm;
/*     */   }
/*     */   
/*     */ 
/*     */   public void setProfileHMM()
/*     */   {
/* 144 */     this.hmm.setProfileHMM();
/*     */   }
/*     */   
/*     */   protected void setFullProfileHMM() {
/* 148 */     this.hmm.setFullProfileHMM();
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
/*     */   protected HmmerModel hmm;
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
/*     */   public void parseModel(BufferedReader in, int insertRates, int matchRates)
/*     */   {
/*     */     try
/*     */     {
/* 230 */       boolean inModel = false;
/* 231 */       int seq_pos = 0;
/* 232 */       int rel_pos = 0;
/*     */       
/* 234 */       String s = new String();
/*     */       
/* 236 */       while ((s = in.readLine()) != null)
/*     */       {
/* 238 */         if (s.startsWith("//"))
/*     */           break;
/* 240 */         if (!inModel) {
/* 241 */           if (s.startsWith("LENG")) {
/* 242 */             int[] a = parseString(s.substring(5), 1);
/* 243 */             this.hmm = new HmmerModel(a[0], this.dt, insertRates, matchRates);
/* 244 */           } else if (s.startsWith("NULE")) {
/* 245 */             this.hmm.setNullEmissions(s.substring(5));
/* 246 */           } else if (s.startsWith("NULT")) {
/* 247 */             this.hmm.setNullTransitions(s.substring(5));
/*     */           }
/* 249 */           else if (s.startsWith("XT")) {
/* 250 */             this.hmm.setSpecialTransitions(s.substring(5));
/* 251 */           } else if (s.startsWith("EVD")) {
/* 252 */             String[] st = s.split("\\s+");
/* 253 */             this.evalueParams = new double[] {
/* 254 */               Double.parseDouble(st[1]), 
/* 255 */               Double.parseDouble(st[2]) };
/*     */ 
/*     */           }
/* 258 */           else if (s.startsWith("HMM ")) {
/* 259 */             inModel = true;
/* 260 */             this.hmm.setAlphList(s.substring(7));
/* 261 */             in.readLine();
/* 262 */             this.hmm.setBeginTransition(in.readLine());
/*     */           }
/* 264 */           else if (s.startsWith("EVD")) {
/* 265 */             String[] st = s.split("\\s+");
/* 266 */             this.hmm.evd = new double[] { Double.parseDouble(st[1]), Double.parseDouble(st[2]) };
/*     */           }
/*     */         } else {
/* 269 */           if (rel_pos == 0) {
/* 270 */             this.hmm.setEmissions(s.substring(7), seq_pos);
/* 271 */           } else if ((rel_pos == 1) && (seq_pos == 1)) {
/* 272 */             this.hmm.setInsertEmissions(s.substring(7));
/* 273 */           } else if (rel_pos == 2) {
/* 274 */             this.hmm.setTransitions(s.substring(7), seq_pos);
/*     */           }
/* 276 */           rel_pos++;
/* 277 */           if (rel_pos == 3) {
/* 278 */             rel_pos = 0;
/* 279 */             seq_pos++;
/*     */           }
/*     */         }
/*     */       }
/* 283 */       in.close();
/*     */     } catch (Throwable t) {
/* 285 */       t.printStackTrace();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   static int[] parseString(String s, int len)
/*     */   {
/* 292 */     s = s.trim();
/* 293 */     String[] s1 = s.split("\\s+");
/* 294 */     int[] s2 = new int[s1.length];
/* 295 */     for (int i = 0; i < s1.length; i++) {
/* 296 */       if (s1[i].indexOf("*") != -1) {
/* 297 */         s2[i] = Integer.MIN_VALUE;
/*     */       } else
/* 299 */         s2[i] = Integer.parseInt(s1[i]);
/*     */     }
/* 301 */     return s2;
/*     */   }
/*     */   
/*     */   static String[] parseStringA(String s, int len) {
/* 305 */     String[] s2 = new String[len];
/* 306 */     StringTokenizer st = new StringTokenizer(s);
/* 307 */     int i = 0;
/* 308 */     while ((st.hasMoreTokens()) && (i < len)) {
/* 309 */       s2[i] = st.nextToken();
/* 310 */       i++;
/*     */     }
/* 312 */     return s2;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   class HmmerModel
/*     */   {
/*     */     int[] nullEmissions;
/*     */     
/*     */ 
/*     */     double[] evd;
/*     */     
/*     */ 
/*     */     int[] nullTransitions;
/*     */     
/*     */ 
/*     */     int[][] emissions;
/*     */     
/*     */ 
/*     */     int[] insertEmissions;
/*     */     
/*     */ 
/*     */     int[][] transitions;
/*     */     
/*     */ 
/*     */     int[] beginTransition;
/*     */     
/*     */ 
/*     */     int[] specialTransitions;
/*     */     
/*     */ 
/*     */     int[] alphList;
/*     */     
/*     */     HmmerProfileHMM hmm;
/*     */     
/*     */ 
/*     */     HmmerModel(int length, DataType dt, int insertRates, int matchRates)
/*     */     {
/* 350 */       this.nullEmissions = new int[dt.getNumStates()];
/* 351 */       this.nullTransitions = new int[2];
/* 352 */       this.emissions = new int[length][dt.getNumStates()];
/* 353 */       this.insertEmissions = new int[dt.getNumStates()];
/* 354 */       this.specialTransitions = new int[8];
/* 355 */       this.transitions = new int[length + 1][9];
/* 356 */       this.alphList = new int[dt.getNumStates()];
/* 357 */       this.hmm = HmmerProfileParser.this.initialiseProfileHMM(this.emissions.length, insertRates, matchRates);
/*     */     }
/*     */     
/*     */     void setAlphList(String s) {
/*     */       try {
/* 362 */         s = s.trim();
/*     */         
/* 364 */         String[] list = s.split("\\s+");
/*     */         
/*     */ 
/* 367 */         for (int i = 0; i < list.length; i++) {
/* 368 */           this.alphList[i] = HmmerProfileParser.this.dt.getState(list[i].charAt(0));
/*     */         }
/*     */       }
/*     */       catch (Throwable t) {
/* 372 */         t.printStackTrace();
/*     */       }
/*     */     }
/*     */     
/*     */     void setEmissions(String s, int pos) {
/* 377 */       this.emissions[pos] = HmmerProfileParser.parseString(s, HmmerProfileParser.this.dt.getNumStates());
/*     */     }
/*     */     
/*     */     void setNullEmissions(String s) {
/* 381 */       this.nullEmissions = HmmerProfileParser.parseString(s, HmmerProfileParser.this.dt.getNumStates());
/*     */     }
/*     */     
/*     */     void setInsertEmissions(String s)
/*     */     {
/* 386 */       this.insertEmissions = HmmerProfileParser.parseString(s, HmmerProfileParser.this.dt.getNumStates());
/*     */     }
/*     */     
/*     */     void setNullTransitions(String s) {
/* 390 */       this.nullTransitions = HmmerProfileParser.parseString(s, 2);
/*     */     }
/*     */     
/*     */     void setTransitions(String s, int pos) {
/* 394 */       this.transitions[pos] = HmmerProfileParser.parseString(s, 9);
/*     */     }
/*     */     
/*     */     void setBeginTransition(String s) {
/* 398 */       this.beginTransition = HmmerProfileParser.parseString(s, 3);
/*     */     }
/*     */     
/*     */     void setSpecialTransitions(String s) {
/* 402 */       this.specialTransitions = HmmerProfileParser.parseString(s, 8);
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
/*     */     void setBeginEnd()
/*     */       throws Exception
/*     */     {
/* 458 */       this.hmm.setTransition(this.hmm.begin, 
/* 459 */         this.hmm.getInsert(0), 
/* 460 */         convertToProb(this.beginTransition[0]));
/* 461 */       this.hmm.setTransition(this.hmm.begin, 
/* 462 */         this.hmm.getInsert(0), 
/* 463 */         convertToProb(this.beginTransition[1]));
/* 464 */       this.hmm.setTransition(this.hmm.begin, this.hmm.getDelete(0), 
/* 465 */         convertToProb(this.beginTransition[2]));
/* 466 */       EmissionState match = this.hmm.getMatch(0);
/* 467 */       this.hmm.setTransition(this.hmm.begin, match, convertToProb(this.transitions[0][7]));
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     void setFullProfileHMM()
/*     */     {
/*     */       try
/*     */       {
/* 479 */         this.hmm.setTransition(this.hmm.MAGIC, this.hmm.nState(), HmmerProfileParser.one);
/*     */         
/* 481 */         this.hmm.setTransition(this.hmm.nState(), this.hmm.begin, 
/* 482 */           convertToProb(this.specialTransitions[0]));
/* 483 */         this.hmm.setTransition(this.hmm.nState(), this.hmm.nState(), 
/* 484 */           convertToProb(this.specialTransitions[1]));
/*     */         
/* 486 */         this.hmm.setTransition(this.hmm.end, this.hmm.cState(), 
/* 487 */           convertToProb(this.specialTransitions[2]));
/* 488 */         this.hmm.setTransition(this.hmm.end, this.hmm.jState(), 
/* 489 */           convertToProb(this.specialTransitions[3]));
/*     */         
/* 491 */         this.hmm.setTransition(this.hmm.cState(), this.hmm.MAGIC, 
/* 492 */           convertToProb(this.specialTransitions[4]));
/* 493 */         this.hmm.setTransition(this.hmm.cState(), this.hmm.cState(), 
/* 494 */           convertToProb(this.specialTransitions[5]));
/*     */         
/* 496 */         this.hmm.setTransition(this.hmm.jState(), this.hmm.begin, 
/* 497 */           convertToProb(this.specialTransitions[6]));
/* 498 */         this.hmm.setTransition(this.hmm.jState(), this.hmm.jState(), 
/* 499 */           convertToProb(this.specialTransitions[7]));
/*     */       } catch (Throwable t) {
/* 501 */         t.printStackTrace();
/*     */       }
/*     */     }
/*     */     
/* 505 */     Double zero = new Double(0.0D);
/*     */     
/*     */     void setProfileHMM() {
/*     */       try {
/* 509 */         HmmerProfileHMM.NullModel nullM = this.hmm.getNullModel();
/* 510 */         nullM.setTransition(nullM.gState(), nullM.fState(), 
/* 511 */           convertToProb(this.nullTransitions[1]));
/* 512 */         nullM.setTransition(nullM.gState(), nullM.gState(), 
/* 513 */           convertToProb(this.nullTransitions[0]));
/* 514 */         for (int i = 0; i < this.hmm.columns(); i++) {
/* 515 */           EmissionState match = this.hmm.getMatch(i);
/* 516 */           if (i < this.hmm.columns() - 1) {
/* 517 */             this.hmm.setTransition(match, this.hmm.getMatch(i + 1), 
/* 518 */               convertToProb(this.transitions[i][0]));
/* 519 */             this.hmm.setTransition(match, this.hmm.getInsert(i), 
/* 520 */               convertToProb(this.transitions[i][1]));
/*     */             
/* 522 */             this.hmm.setTransition(match, this.hmm.getDelete(i + 1), 
/* 523 */               convertToProb(this.transitions[i][2]));
/*     */           } else {
/* 525 */             this.hmm.setTransition(match, this.hmm.end, HmmerProfileParser.one);
/* 526 */             this.hmm.setTransition(match, this.hmm.getInsert(i), this.zero);
/*     */           }
/*     */         }
/* 529 */         for (int i = 0; i < this.hmm.columns(); i++) {
/* 530 */           EmissionState insert = this.hmm.getInsert(i);
/* 531 */           if (i < this.hmm.columns() - 1) {
/* 532 */             this.hmm.setTransition(insert, this.hmm.getMatch(i + 1), 
/* 533 */               convertToProb(this.transitions[i][3]));
/* 534 */             this.hmm.setTransition(insert, insert, convertToProb(this.transitions[i][4]));
/*     */           }
/*     */           else {
/* 537 */             this.hmm.setTransition(insert, this.hmm.end, HmmerProfileParser.one);
/* 538 */             this.hmm.setTransition(insert, insert, this.zero);
/*     */           }
/*     */         }
/* 541 */         for (int i = 0; i < this.hmm.columns(); i++) {
/* 542 */           DotState delete = this.hmm.getDelete(i);
/*     */           
/* 544 */           if (i < this.hmm.columns() - 1) {
/* 545 */             this.hmm.setTransition(delete, this.hmm.getMatch(i + 1), 
/* 546 */               convertToProb(this.transitions[i][5]));
/* 547 */             this.hmm.setTransition(delete, this.hmm.getDelete(i + 1), 
/* 548 */               convertToProb(this.transitions[i][6]));
/*     */           } else {
/* 550 */             this.hmm.setTransition(delete, this.hmm.end, HmmerProfileParser.one);
/*     */           }
/*     */         }
/*     */         
/* 554 */         setBeginEnd();
/* 555 */         setFullProfileHMM();
/*     */         
/*     */ 
/* 558 */         double[] insertEmission = this.hmm.getInsert(1).getDistribution();
/* 559 */         double[] nullModel = new double[insertEmission.length];
/* 560 */         for (int j = 0; j < insertEmission.length; j++)
/*     */         {
/*     */ 
/* 563 */           double null_prob = convertToProb(this.nullEmissions[j], 0.05D);
/* 564 */           double prob = convertToProb(this.insertEmissions[j], null_prob);
/* 565 */           insertEmission[this.alphList[j]] = prob;
/* 566 */           nullModel[this.alphList[j]] = null_prob;
/*     */         }
/*     */         
/*     */ 
/* 570 */         this.hmm.cState().setDistribution(insertEmission);
/* 571 */         this.hmm.jState().setDistribution(insertEmission);
/* 572 */         this.hmm.nState().setDistribution(insertEmission);
/* 573 */         double[][] match_dists = new double[this.hmm.columns()][20];
/* 574 */         for (int i = 0; i < this.hmm.columns(); i++) {
/* 575 */           double[] matchEmission = this.hmm.getMatch(i).getDistribution();
/*     */           
/* 577 */           this.hmm.getInsert(i).setDistribution(insertEmission);
/* 578 */           nullM.gState.setDistribution(nullModel);
/* 579 */           for (int j = 0; j < matchEmission.length; j++)
/*     */           {
/*     */ 
/* 582 */             double null_prob = convertToProb(this.nullEmissions[j], 0.05D);
/* 583 */             double prob = convertToProb(this.emissions[i][j], null_prob);
/*     */             
/*     */ 
/* 586 */             matchEmission[this.alphList[j]] = prob;
/*     */           }
/*     */           
/*     */ 
/*     */ 
/* 591 */           match_dists[i] = matchEmission;
/*     */         }
/*     */         
/*     */ 
/*     */       }
/*     */       catch (Throwable t)
/*     */       {
/*     */ 
/* 599 */         t.printStackTrace();
/*     */       }
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
/*     */     private int convertToScore(int prob)
/*     */     {
/* 626 */       return (int)Math.floor(1000.0D * Math.log(prob) / HmmerProfileParser.log2);
/*     */     }
/*     */     
/*     */     private int convertToScore(double prob, double nullprob) {
/* 630 */       return (int)Math.floor(1000.0D * Math.log(prob / nullprob) / HmmerProfileParser.log2);
/*     */     }
/*     */     
/*     */     private Double convertToProb(int score) {
/* 634 */       double result = 0.0D;
/* 635 */       if (score != Integer.MIN_VALUE) {
/* 636 */         result = 1.0D * Math.pow(2.0D, score / 1000.0D);
/*     */       }
/* 638 */       return new Double(result);
/*     */     }
/*     */     
/*     */     private double convertToProb(int score, double nullprob) {
/* 642 */       double result = 0.0D;
/* 643 */       if (score != Integer.MIN_VALUE)
/*     */       {
/*     */ 
/*     */ 
/* 647 */         result = nullprob * Math.pow(2.0D, score / 1000.0D);
/*     */       }
/*     */       
/* 650 */       return result;
/*     */     }
/*     */   }
/*     */ }


/* Location:              /home/lachlan/Desktop/psilc1.21/psilc.jar!/lc1/dp/HmmerProfileParser.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */