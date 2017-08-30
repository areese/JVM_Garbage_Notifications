# JVM Garbage notifications
This is example code for how to be notified when you're getting memory pressure.

There is 1 thread that just generates garbage, and a listener that will listen for the event 
that the usage threshhold has been exceeded.

When the usage threshold is exceeded, it dumps the references in the landfill and forces a gc.

The idea behind this code is, you can set a usage threshold that's before you'll hit a real gc, 
and then dump your caches and exhibit a preference to not get traffic.


The command line I've recently used is:
mvn verify && java -XX:+PrintGCDetails -XX:+PrintGCDateStamps -verbose:gc -Xms1g -Xmx1g -cp target/classes/ MakeGarbage  10000 20000 2>&1 | tee log  | perl convert.pl 

The 10k of bytes and 20 seconds of sleeps seems to be just right to look at what would be going on before trying to use this other ways.

This might be affected by http://bugs.java.com/view_bug.do?bug_id=8183967  which is fixed in 8u141 BPR builds, and maybe in 8u144: http://www.oracle.com/technetwork/java/javaseproducts/documentation/8u141-revision-builds-relnotes-3834989.html
