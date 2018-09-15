# Jie Tao - G01723372
**1. Is the counter always equal to n * 1000000, where n is the number of threads created?  Explain why, or why not.**

NO. Program A is without thread synchronization. Each thread can access `counter` object at a time, which makes counter less than n * 1000000. 

**2. Is the counter always equal to n * 1000000, where n is the number of threads created?  Explain why, or why not.**

Yes. Since Program B is thread synchronized. It only has one thread executing inside them at a time. All other threads attempting to enter the synchronized block are blocked until the thread inside the synchronized block exits the block.

**3. Analyze the differences in elapsed time between Program A and Program B.  Is there a significant difference?  Explain why or why not.**

Program B is slower than Program A, since threads in program B are synchronized, they can’t access `counter` until the thread inside exits. However, the threads in program A, can access `counter` anytime.  
