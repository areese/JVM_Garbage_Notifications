This is example code for how to be notified when you're getting memory pressure.

There is 1 thread that just generates garbage, and a listener that will listen for the event 
that the usage threshhold has been exceeded.

When the usage threshold is exceeded, it dumps the references in the landfill and forces a gc.

The idea behind this code is, you can set a usage threshold that's before you'll hit a real gc, 
and then dump your caches and exhibit a preference to not get traffic.
