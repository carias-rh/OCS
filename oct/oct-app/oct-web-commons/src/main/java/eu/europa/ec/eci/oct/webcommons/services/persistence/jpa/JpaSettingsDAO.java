/** ====================================================================
 * Licensed under the European Union Public Licence (EUPL v1.2) 
 * https://joinup.ec.europa.eu/community/eupl/topic/public-consultation-draft-eupl-v12
 * ====================================================================
 *
 * @author Daniel CHIRITA
 * @created: 23/05/2013
 *
 */
package eu.europa.ec.eci.oct.webcommons.services.persistence.jpa;

import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import eu.europa.ec.eci.oct.entities.ConfigurationParameter;
import eu.europa.ec.eci.oct.webcommons.services.persistence.PersistenceException;
import eu.europa.ec.eci.oct.webcommons.services.persistence.SettingsDAO;

@Repository
@Transactional
@SuppressWarnings("unchecked")
public class JpaSettingsDAO extends AbstractJpaDAO implements SettingsDAO {

	@Override
	@Transactional(readOnly = true)
	public List<ConfigurationParameter> getAllSettings() throws PersistenceException {
		try {
			logger.debug("Retrieving all configuration parameters");

			List<ConfigurationParameter> confs = this.sessionFactory.getCurrentSession().createQuery("FROM ConfigurationParameter").list();

			return confs;
		} catch (Exception e) {
			throw wrapException("getAllSettings()", e);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public ConfigurationParameter findConfigurationParametereByKey(String key) throws PersistenceException {
		try {
			ConfigurationParameter result =
					(ConfigurationParameter) this.sessionFactory.getCurrentSession().createQuery("FROM ConfigurationParameter cp WHERE cp.param = :key").setParameter("key", key).uniqueResult();

			return result;
		} catch (Exception e) {
			throw wrapException("getAllSettings()", e);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false, rollbackFor = Exception.class)
	public ConfigurationParameter updateParameter(ConfigurationParameter param) throws PersistenceException {
		try {
			ConfigurationParameter alreadyExistentParam = findConfigurationParametereByKey(param.getParam());
			if (alreadyExistentParam != null) {
				alreadyExistentParam.setValue(param.getValue());
				this.sessionFactory.getCurrentSession().update(alreadyExistentParam);
			} else {
				this.sessionFactory.getCurrentSession().save(param);
			}
			this.sessionFactory.getCurrentSession().flush();
		} catch (Exception e) {
			throw wrapException("updateParameter(" + param + ")", e);
		}
		return param;
	}
}
