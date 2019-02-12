/*    */ package lc1.util;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ class MemorySampler
/*    */   implements Runnable
/*    */ {
/* 11 */   long[] freeMemory = new long['Ϩ'];
/* 12 */   long[] totalMemory = new long['Ϩ'];
/* 13 */   int sampleSize = 0;
/* 14 */   long max = 0L;
/* 15 */   boolean keepGoing = true;
/*    */   
/*    */   MemorySampler() {
/* 18 */     Thread t = new Thread(this);
/* 19 */     t.setDaemon(true);
/* 20 */     t.setPriority(10);
/* 21 */     t.start();
/*    */   }
/*    */   
/*    */   public void stop() {
/* 25 */     this.keepGoing = false;
/*    */   }
/*    */   
/*    */   public void run() {
/* 29 */     Runtime runtime = Runtime.getRuntime();
/* 30 */     while (this.keepGoing) {
/* 31 */       try { Thread.sleep(30L); } catch (InterruptedException localInterruptedException) {}
/* 32 */       addSample(runtime);
/*    */     }
/*    */   }
/*    */   
/*    */   public void addSample(Runtime runtime) {
/* 37 */     if (this.sampleSize >= this.freeMemory.length) {
/* 38 */       long[] tmp = new long[2 * this.freeMemory.length];
/* 39 */       System.arraycopy(this.freeMemory, 0, tmp, 0, this.freeMemory.length);
/* 40 */       this.freeMemory = tmp;
/* 41 */       tmp = new long[2 * this.totalMemory.length];
/* 42 */       System.arraycopy(this.totalMemory, 0, tmp, 0, this.totalMemory.length);
/* 43 */       this.totalMemory = tmp;
/*    */     }
/* 45 */     this.freeMemory[this.sampleSize] = runtime.freeMemory();
/* 46 */     this.totalMemory[this.sampleSize] = runtime.totalMemory();
/* 47 */     if (this.max < this.totalMemory[this.sampleSize])
/* 48 */       this.max = this.totalMemory[this.sampleSize];
/* 49 */     this.sampleSize += 1;
/*    */   }
/*    */ }


/* Location:              /home/lachlan/Desktop/psilc1.21/psilc.jar!/lc1/util/MemorySampler.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */