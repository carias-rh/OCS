package eu.europa.ec.eci.oct.webcommons.services.persistence;

import java.util.List;

import eu.europa.ec.eci.oct.entities.AuthenticationLock;

public interface AuthenticationLockDAO {
    List<AuthenticationLock> getAllAuthenticationLock() throws PersistenceException;
    AuthenticationLock getAuthenticationLockForUser(String username) throws PersistenceException;
}
