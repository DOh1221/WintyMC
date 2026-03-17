package ru.armlix.winty.game.counters;

public class TimeCounter implements Counter<Long> {

    private long time;

    public TimeCounter(long time) {
        this.time = time;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    @Override
    public Long increment() {
        return time++;
    }

    @Override
    public Long decrement() {
        return time--;
    }

    @Override
    public Long increment(Long i) {
        return time += i;
    }

    @Override
    public Long decrement(Long i) {
        return time -= i;
    }
}
