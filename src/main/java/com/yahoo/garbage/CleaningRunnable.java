package com.yahoo.garbage;

public class CleaningRunnable implements Runnable {
    private volatile boolean clean;
    private final Landfill landfill;

    public CleaningRunnable(Landfill landfill) {
        this.landfill = landfill;
        clean = false;
    }

    public void setClean() {
        this.clean = true;
    }

    @Override
    public void run() {
        while (true) {
            if (clean) {
                landfill.clear();
                clean = false;
            }
            Thread.yield();
        }
    }

}
