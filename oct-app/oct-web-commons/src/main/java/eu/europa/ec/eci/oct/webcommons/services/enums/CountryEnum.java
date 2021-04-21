package eu.europa.ec.eci.oct.webcommons.services.enums;

public enum CountryEnum {

	// @ formatter:off
	POLAND(1, "pl", "oct.country.pl", 36660), GERMANY(2, "de", "oct.country.de", 67680),
	FRANCE(4, "fr", "oct.country.fr", 55695), BELGIUM(5, "be", "oct.country.be", 14805),
	ROMANIA(6, "ro", "oct.country.ro", 23265), AUSTRIA(8, "at", "oct.country.at", 13395),
	LATVIA(9, "lv", "oct.country.lv", 5640), BULGARIA(10, "bg", "oct.country.bg", 11985),
	CYPRUS(11, "cy", "oct.country.cy", 4230), LITHUANIA(12, "lt", "oct.country.lt", 7755),
	LUXEMBURG(13, "lu", "oct.country.lu", 4230), MALTA(14, "mt", "oct.country.mt", 4230),
	NETHERLANDS(15, "nl", "oct.country.nl", 20445), PORTUGAL(16, "pt", "oct.country.pt", 14805),
	SLOVAKIA(17, "sk", "oct.country.sk", 9870), SLOVENIA(18, "si", "oct.country.si", 5640),
	CZECH_REPUBLIC(19, "cz", "oct.country.cz", 14805), DENMARK(20, "dk", "oct.country.dk", 9870),
	ESTONIA(21, "ee", "oct.country.ee", 4935), FINLAND(22, "fi", "oct.country.fi", 9870),
	GREECE(23, "gr", "oct.country.gr", 14805), SPAIN(24, "es", "oct.country.es", 41595),
	HUNGARY(25, "hu", "oct.country.hu", 14805), IRELAND(26, "ie", "oct.country.ie", 9165),
	SWEDEN(27, "se", "oct.country.se", 14805), ITALY(28, "it", "oct.country.it", 53580),
	CROATIA(29, "hr", "oct.country.hr", 8460);
	// @ formatter:on

	// id code name threshold
	private long id;
	private String code;
	private String name;
	private long treshold;

	CountryEnum(long id, String code, String name, long treshold) {
		this.id = id;
		this.code = code;
		this.name = name;
		this.treshold = treshold;
	}

	public static CountryEnum getByCode(String code) throws Exception {
		for (CountryEnum c : CountryEnum.values()) {
			if (c.code.equalsIgnoreCase(code)) {
				return c;
			}
		}
		throw new Exception("No country found for code " + code);
	}

	public static CountryEnum getById(long id) throws Exception {
		for (CountryEnum c : CountryEnum.values()) {
			if (c.id == id) {
				return c;
			}
		}
		throw new Exception("No country found for id " + id);
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getTreshold() {
		return treshold;
	}

	public void setTreshold(long treshold) {
		this.treshold = treshold;
	}

}
