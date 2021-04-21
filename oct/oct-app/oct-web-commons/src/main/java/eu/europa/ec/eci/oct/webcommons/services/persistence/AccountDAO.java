package eu.europa.ec.eci.oct.webcommons.services.persistence;


import org.springframework.transaction.annotation.Transactional;

import eu.europa.ec.eci.oct.entities.admin.Account;

public interface AccountDAO {

	Account getAccountByName(String userName) throws PersistenceException;

    @Transactional()
    Account save(String username);
}
