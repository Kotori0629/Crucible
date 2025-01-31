package co.aikar.timings;

import org.jetbrains.annotations.NotNull;

public class FullServerTickHandler extends TimingHandler {
    private static final TimingIdentifier IDENTITY = new TimingIdentifier("Minecraft", "Full Server Tick", null);
    final TimingData minuteData;
    double avgFreeMemory = -1D;
    double avgUsedMemory = -1D;
    FullServerTickHandler() {
        super(IDENTITY);
        minuteData = new TimingData(id);

        co.aikar.timings.TimingsManager.TIMING_MAP.put(IDENTITY, this);
    }

    @NotNull
    @Override
    public Timing startTiming() {
        if (TimingsManager.needsFullReset) {
            TimingsManager.resetTimings();
        } else if (TimingsManager.needsRecheckEnabled) {
            TimingsManager.recheckEnabled();
        }
        return super.startTiming();
    }

    @Override
    public void stopTiming() {
        super.stopTiming();
        if (!isEnabled()) {
            return;
        }
        if (TimingHistory.timedTicks % 20 == 0) {
            final Runtime runtime = Runtime.getRuntime();
            double usedMemory = runtime.totalMemory() - runtime.freeMemory();
            double freeMemory = runtime.maxMemory() - usedMemory;
            if (this.avgFreeMemory == -1) {
                this.avgFreeMemory = freeMemory;
            } else {
                this.avgFreeMemory = (this.avgFreeMemory * (59 / 60D)) + (freeMemory * (1 / 60D));
            }

            if (this.avgUsedMemory == -1) {
                this.avgUsedMemory = usedMemory;
            } else {
                this.avgUsedMemory = (this.avgUsedMemory * (59 / 60D)) + (usedMemory * (1 / 60D));
            }
        }

        long start = System.nanoTime();
        TimingsManager.tick();
        long diff = System.nanoTime() - start;
        co.aikar.timings.TimingsManager.TIMINGS_TICK.addDiff(diff, null);
        // addDiff for TIMINGS_TICK incremented this, bring it back down to 1 per tick.
        record.setCurTickCount(record.getCurTickCount()-1);

        minuteData.setCurTickTotal(record.getCurTickTotal());
        minuteData.setCurTickCount(1);

        boolean violated = isViolated();
        minuteData.processTick(violated);
        co.aikar.timings.TimingsManager.TIMINGS_TICK.processTick(violated);
        processTick(violated);


        if (TimingHistory.timedTicks % 1200 == 0) {
            co.aikar.timings.TimingsManager.MINUTE_REPORTS.add(new TimingHistory.MinuteReport());
            TimingHistory.resetTicks(false);
            minuteData.reset();
        }
        if (TimingHistory.timedTicks % Timings.getHistoryInterval() == 0) {
            TimingsManager.HISTORY.add(new TimingHistory());
            TimingsManager.resetTimings();
        }
        TimingsExport.reportTimings();
    }

    boolean isViolated() {
        return record.getCurTickTotal() > 50000000;
    }
}
