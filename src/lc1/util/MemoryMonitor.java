/*     */ package lc1.util;
/*     */ 
/*     */ import java.awt.Color;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.Frame;
/*     */ import java.awt.Graphics;
/*     */ import java.awt.event.WindowEvent;
/*     */ import java.awt.event.WindowListener;
/*     */ import java.io.PrintStream;
/*     */ import java.lang.reflect.Method;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class MemoryMonitor
/*     */   extends Frame
/*     */   implements WindowListener, Runnable
/*     */ {
/*     */   MemorySampler sampler;
/*     */   long interval;
/*  58 */   static Color freeColor = Color.red;
/*  59 */   static Color totalColor = Color.blue;
/*  60 */   int[] xpoints = new int['ߐ'];
/*  61 */   int[] yfrees = new int['ߐ'];
/*  62 */   int[] ytotals = new int['ߐ'];
/*     */   
/*     */   public static void main(String[] args) {
/*     */     try {
/*  66 */       MemoryMonitor m = new MemoryMonitor(500L);
/*  67 */       String classname = args[0];
/*  68 */       String[] argz = new String[args.length - 1];
/*  69 */       System.arraycopy(args, 1, argz, 0, argz.length);
/*  70 */       Class clazz = Class.forName(classname);
/*  71 */       Class[] mainParamType = { args.getClass() };
/*  72 */       Method main = clazz.getMethod("main", mainParamType);
/*  73 */       Object[] mainParams = { argz };
/*  74 */       main.invoke(null, mainParams);
/*  75 */       m.testStopped();
/*     */     }
/*     */     catch (Exception e) {
/*  78 */       e.printStackTrace();
/*     */     }
/*     */   }
/*     */   
/*     */   public MemoryMonitor(long updateInterval) {
/*  83 */     super("Memory monitor");
/*  84 */     this.interval = updateInterval;
/*  85 */     addWindowListener(this);
/*  86 */     setSize(600, 200);
/*  87 */     show();
/*  88 */     this.sampler = new MemorySampler();
/*  89 */     new Thread(this).start();
/*     */   }
/*     */   
/*     */   public void run()
/*     */   {
/*  94 */     int sampleSize = this.sampler.sampleSize;
/*     */     for (;;) {
/*  96 */       try { Thread.sleep(this.interval); } catch (InterruptedException localInterruptedException) {}
/*  97 */       if (sampleSize != this.sampler.sampleSize)
/*     */         try {
/*  99 */           update(getGraphics());
/*     */         } catch (Exception e) {
/* 101 */           e.printStackTrace();
/*     */         }
/*     */     }
/*     */   }
/*     */   
/*     */   public void testStopped() {
/* 107 */     this.sampler.stop();
/*     */   }
/*     */   
/*     */   public void paint(Graphics g) {
/*     */     try {
/* 112 */       Dimension d = getSize();
/* 113 */       int width = d.width - 20;
/* 114 */       int height = d.height - 40;
/* 115 */       long max = this.sampler.max;
/* 116 */       int sampleSize = this.sampler.sampleSize;
/* 117 */       if (sampleSize < 20) {
/* 118 */         return;
/*     */       }
/* 120 */       int highIdx = width < sampleSize - 1 ? width : sampleSize - 1;
/* 121 */       int idx = sampleSize - highIdx - 1;
/* 122 */       for (int x = 0; x < highIdx; idx++) {
/* 123 */         this.xpoints[x] = (x + 10);
/* 124 */         this.yfrees[x] = (height - (int)(this.sampler.freeMemory[idx] * height / max) + 40);
/* 125 */         this.ytotals[x] = (height - 
/* 126 */           (int)(this.sampler.totalMemory[idx] * height / max) + 40);x++;
/*     */       }
/*     */       
/*     */ 
/*     */ 
/* 128 */       g.setColor(freeColor);
/* 129 */       g.drawPolyline(this.xpoints, this.yfrees, highIdx);
/* 130 */       g.setColor(totalColor);
/* 131 */       g.drawPolyline(this.xpoints, this.ytotals, highIdx);
/* 132 */       g.setColor(Color.black);
/* 133 */       g.drawString("maximum: " + Math.floor(max / 1048576L) + " mega-bytes (total memory - blue line | free memory - red line)", 9, 35);
/*     */     }
/*     */     catch (Exception e) {
/* 136 */       System.out.println("Memory monitor: " + e.getMessage());
/*     */     }
/*     */   }
/*     */   
/*     */   public void windowActivated(WindowEvent e) {}
/*     */   
/*     */   public void windowClosed(WindowEvent e) {}
/*     */   
/*     */   public void windowClosing(WindowEvent e) {}
/*     */   
/*     */   public void windowDeactivated(WindowEvent e) {}
/*     */   
/*     */   public void windowDeiconified(WindowEvent e) {}
/*     */   
/*     */   public void windowIconified(WindowEvent e) {}
/*     */   
/*     */   public void windowOpened(WindowEvent e) {}
/*     */ }


/* Location:              /home/lachlan/Desktop/psilc1.21/psilc.jar!/lc1/util/MemoryMonitor.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */