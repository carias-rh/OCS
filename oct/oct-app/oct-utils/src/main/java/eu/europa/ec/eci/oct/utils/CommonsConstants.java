package eu.europa.ec.eci.oct.utils;


public class CommonsConstants {

	/**
	 * Session attribute for an expected request token. The request token
	 * mechanism is used for securing form submissions against cross-site
	 * request forgery attacks.
	 */
	public static final String SESSION_ATTR_EXPECTED_REQUEST_TOKEN = "__expected_request_token__";
	/**
	 * Request parameter for transmitting the current request token in a form.
	 * The request token mechanism is used for securing form submissions against
	 * cross-site request forgery attacks.
	 */
	public static final String REQUEST_PARAM_REQUEST_TOKEN = "oct_request_token";

	public static final int CURRENT_ANNEX_REVISION_NUMBER = 2;
	
	public static final int TOTAL_COUNTRIES_NUMBER = 28;
	public static final Long FAST_SIGNATURE_COUNT_TOTAL_COUNTRY_ID = 99L;
	public static final int DAYS_LEFT_DEFAULT = 365;
	
	public static final String ALL_COUNTRIES_KEY = "all";
	public static final int MONTH_IN_A_YEAR = 12;
	public static final int TOP_COUNTRIES_CHART_SIZE = 7;
	public static final String EXPORT_PATH = "export";
	public static final String MYSQL_DB_PRODUCT_NAME = "MySQL";
	public static final String ORACLE_DB_PRODUCT_NAME = "Oracle";

}
