package eu.europa.ec.eci.oct.webcommons.services.security;

import java.io.Serializable;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * User: franzmh
 * Date: 23/02/17
 */
@Component
@Scope("prototype")
public class Lock implements Serializable {
    private static final long serialVersionUID = 3419006566877522715L;
    private int attempts = 0;
    private int maxAttempts;
    private long timeOfLastFailedAttempt = 0;
    private long lockPeriod;

    public int getAttempts() {
        return attempts;
    }

    public void setAttempts(int attempts) {
        this.attempts = attempts;
    }

    public int getMaxAttempts() {
        return maxAttempts;
    }

    public void setMaxAttempts(int maxAttempts) {
        this.maxAttempts = maxAttempts;
    }

    public long getTimeOfLastFailedAttempt() {
        return timeOfLastFailedAttempt;
    }

    public void setTimeOfLastFailedAttempt(long timeOfLastFailedAttempt) {
        this.timeOfLastFailedAttempt = timeOfLastFailedAttempt;
    }

    public long getLockPeriod() {
        return lockPeriod;
    }

    public void setLockPeriod(long lockPeriod) {
        this.lockPeriod = lockPeriod;
    }

    public void increaseAttempts(){
        attempts++;
    }

    public void resetLock(){
        attempts=0;
        timeOfLastFailedAttempt=0;
    }
}
