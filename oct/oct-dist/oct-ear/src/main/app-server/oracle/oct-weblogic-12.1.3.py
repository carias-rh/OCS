# ====================================================
# This script creates a Weblogic domain configuration
# with all JEE items required for running the
# Online Collection Software.
#
# Underlying database engine: Oracle or MySQL.
#
# Tested on Weblogic 10.3.4.
# 
# Prerequisites:
#
# - a running installation of the Weblogic application
#   server
#
# Usage:
#
# - customise the section below
# - launch WLST (WL_HOME/common/bin/wlst.cmd or .sh)
# - execute this script:
#   > execfile('<PATH_TO_THIS_SCRIPT>')
# - exit the WLST console:
#   > exit()
# ====================================================

# === CUSTOMISE START ===

# The following values should be adjusted to match the
# configuration parameters of the Weblogic server and the
# underlying database.

# Weblogic server details
SERVER_HOST 	= 'localhost'
SERVER_PORT 	= '7001'
WL_ADMIN_USER	= 'weblogic'
WL_ADMIN_PASS   = 'weblogic1'
WL_INSTANCE		= 'AdminServer'

# Database details
DB_DIALECT		= 'ORACLE' # supported values: ORACLE, MYSQL

DB_HOST			= 'HOST' #i.e.: olrdev3.cc.cec.eu.int
DB_PORT			= 'PORT' #i.e.: 1597
DB_NAME			= 'DB' # DB service on ORACLE, DB on MYSQL, etc.  i.e.: ECISGD_TAF.cc.cec.eu.int
DB_USER			= 'USER' #i.e.: ECI_OCT_DEV
DB_PASS			= 'PASS'

# === CUSTOMISE END ===

DB_DETAILS = {
	'ORACLE' : {
		'driver' : 'oracle.jdbc.xa.client.OracleXADataSource',
		'url'	 : 'jdbc:oracle:thin:@' + DB_HOST + ':' + DB_PORT + '/' + DB_NAME,
		'testsql': 'SQL SELECT 1 FROM DUAL'
	},
	'MYSQL' : {
		'driver' : 'com.mysql.jdbc.jdbc2.optional.MysqlXADataSource',
		'url'	 : 'jdbc:mysql://' + DB_HOST + ':' + DB_PORT + '/' + DB_NAME,
		'testsql': 'SQL SELECT 1'
	}
}

# connect

connect(WL_ADMIN_USER, WL_ADMIN_PASS, "t3://" + SERVER_HOST + ":" + SERVER_PORT)
edit()
startEdit()

# JDBC DataSource

cd('/')
cmo.createJDBCSystemResource('OctJdbcDataSource')

cd('/JDBCSystemResources/OctJdbcDataSource/JDBCResource/OctJdbcDataSource')
cmo.setName('OctJdbcDataSource')

cd('/JDBCSystemResources/OctJdbcDataSource/JDBCResource/OctJdbcDataSource/JDBCDataSourceParams/OctJdbcDataSource')
set('JNDINames',jarray.array([String('jdbc/oct')], String))

cd('/JDBCSystemResources/OctJdbcDataSource/JDBCResource/OctJdbcDataSource/JDBCDriverParams/OctJdbcDataSource')
cmo.setUrl(DB_DETAILS[DB_DIALECT]['url'])
cmo.setDriverName(DB_DETAILS[DB_DIALECT]['driver'])
cmo.setPassword(DB_PASS)

cd('/JDBCSystemResources/OctJdbcDataSource/JDBCResource/OctJdbcDataSource/JDBCDriverParams/OctJdbcDataSource/Properties/OctJdbcDataSource')
cmo.createProperty('user')

cd('/JDBCSystemResources/OctJdbcDataSource/JDBCResource/OctJdbcDataSource/JDBCDriverParams/OctJdbcDataSource/Properties/OctJdbcDataSource/Properties/user')
cmo.setValue(DB_USER)

cd('/JDBCSystemResources/OctJdbcDataSource/JDBCResource/OctJdbcDataSource/JDBCDataSourceParams/OctJdbcDataSource')
cmo.setGlobalTransactionsProtocol('TwoPhaseCommit')

cd('/JDBCSystemResources/OctJdbcDataSource/JDBCResource/OctJdbcDataSource/JDBCConnectionPoolParams/OctJdbcDataSource')
cmo.setMaxCapacity(15)
#cmo.setCapacityIncrement(5)
cmo.setStatementCacheType('LRU')
cmo.setStatementCacheSize(10)
cmo.setInitialCapacity(5)
cmo.setHighestNumWaiters(2147483647)
cmo.setWrapTypes(true)
cmo.setShrinkFrequencySeconds(900)
cmo.setIgnoreInUseConnectionsEnabled(true)
cmo.setConnectionReserveTimeoutSeconds(10)
cmo.setInactiveConnectionTimeoutSeconds(15)
cmo.setPinnedToThread(false)
cmo.setStatementTimeout(-1)
cmo.setRemoveInfectedConnections(true)
cmo.setConnectionCreationRetryFrequencySeconds(0)
cmo.setSecondsToTrustAnIdlePoolConnection(10)
cmo.setTestConnectionsOnReserve(true)
cmo.setTestFrequencySeconds(120)
cmo.setTestTableName(DB_DETAILS[DB_DIALECT]['testsql'])
cmo.setLoginDelaySeconds(0)

cd('/SystemResources/OctJdbcDataSource')
set('Targets',jarray.array([ObjectName('com.bea:Name=' + WL_INSTANCE + ',Type=Server')], ObjectName))

# save and disconnect

save()
activate(block = 'true')
disconnect()
