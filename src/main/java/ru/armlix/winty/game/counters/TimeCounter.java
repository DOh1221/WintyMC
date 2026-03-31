package ru.armlix.winty.game.counters;

import lombok.Getter;

public class TimeCounter implements Counter<Long> {

    @Getter
    private long time;
    private long previousTime;

    public TimeCounter(long time) {
        this.time = time;
    }

    public void setTime(long time) {
        this.previousTime = time;
        this.time = time;
    }

    @Override
    public Long increment() {
        previousTime = time;
        return time++;
    }

    @Override
    public Long decrement() {
        previousTime = time;
        return time--;
    }

    @Override
    public Long increment(Long i) {
        previousTime = time;
        return time += i;
    }

    @Override
    public Long decrement(Long i) {
        previousTime = time;
        return time -= i;
    }

    @Override
    public Long getPrevious() {
        return previousTime;
    }
}
