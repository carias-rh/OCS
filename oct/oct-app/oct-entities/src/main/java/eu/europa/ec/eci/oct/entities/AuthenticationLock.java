package eu.europa.ec.eci.oct.entities;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Cacheable(false)
@Table(name = "OCT_AUTHENTICATION_LOCK")
public class AuthenticationLock implements Serializable {
	private static final long serialVersionUID = 1478686930183645399L;
	@Id
	private String username;
	@Column(nullable = false)
	private int maxAttempts;
	private Timestamp timeOfLastFailedAttempt;
	@Column(nullable = false)
	private int lockPeriod;

	public String getUsername() {
		return username;
	}

	public void setUsername(String name) {
		this.username = name;
	}

	public int getMaxAttempts() {
		return maxAttempts;
	}

	public void setMaxAttempts(int maxAttempts) {
		this.maxAttempts = maxAttempts;
	}

	public Timestamp getTimeOfLastFailedAttempt() {
		return timeOfLastFailedAttempt;
	}

	public void setTimeOfLastFailedAttempt(Timestamp timeOfLastFailedAttempt) {
		this.timeOfLastFailedAttempt = timeOfLastFailedAttempt;
	}

	public int getLockPeriod() {
		return lockPeriod;
	}

	public void setLockPeriod(int lockPeriod) {
		this.lockPeriod = lockPeriod;
	}

	@Override
	public String toString() {
		return "AuthenticationLock [username=" + username + ", maxAttempts=" + maxAttempts
				+ ", timeOfLastFailedAttempt=" + timeOfLastFailedAttempt + ", lockPeriod=" + lockPeriod + "]";
	}

}
