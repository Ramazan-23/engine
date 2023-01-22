package com.bitvavo.service.id;

public class TimeBasedIdGenerator implements IdGenerator {
    private long lastTime;
    private int delta;

    public long getNext() {
        long currentTimeMillis = System.currentTimeMillis();
        if (currentTimeMillis > lastTime) {
            lastTime = currentTimeMillis;
            delta = 0;
        }

        // occurs from time to time even in a single thread
        if (currentTimeMillis < lastTime) {
            while (System.currentTimeMillis() <= lastTime);
        }

        // we definitely do not expect to process > 10M orders per second
        // potential granularity of System#currentTimeMillis is also taken into account
        // shifting 20 bits multiplies by ~1M thus taking us to approximately nanoseconds order
        return (lastTime << 20) + delta++;
    }
}
