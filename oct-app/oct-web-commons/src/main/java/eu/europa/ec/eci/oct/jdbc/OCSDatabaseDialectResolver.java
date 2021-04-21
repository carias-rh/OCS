package eu.europa.ec.eci.oct.jdbc;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.MySQL5Dialect;
import org.hibernate.dialect.Oracle10gDialect;
import org.hibernate.engine.jdbc.dialect.spi.DialectResolutionInfo;
import org.hibernate.engine.jdbc.dialect.spi.DialectResolver;
import org.hibernate.exception.JDBCConnectionException;

public class OCSDatabaseDialectResolver implements DialectResolver {

	private static final long serialVersionUID = -3913979485801583176L;

	private final Logger logger = LogManager.getLogger(OCSDatabaseDialectResolver.class);

	private static Map<String, Class<? extends Dialect>> DIALECT_MAP = new HashMap<String, Class<? extends Dialect>>();
	{
		DIALECT_MAP.put("Oracle", Oracle10gDialect.class);
		DIALECT_MAP.put("MySQL", MySQL5Dialect.class);
	}

	@Override
	public Dialect resolveDialect(DialectResolutionInfo arg0) throws JDBCConnectionException {
		String databaseName = arg0.getDatabaseName();
		Dialect dbDialect = lookupDialect(databaseName);
		return dbDialect;
	}

	private Dialect lookupDialect(String databaseName) {
		Class<? extends Dialect> dialectClass = DIALECT_MAP.get(databaseName);
		if (dialectClass == null) {
			logger.error("DatabaseProductName " + databaseName + " NOT FOUND in listed dialect classes");
			return null;
		}
		try {
			return dialectClass.newInstance();
		} catch (java.lang.InstantiationException e) {
			logger.error(e);
		} catch (IllegalAccessException e) {
			logger.error(e);
		}
		return null;
	}
}