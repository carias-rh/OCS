package eu.europa.ec.eci.oct.webcommons.services.enums;

import eu.europa.ec.eci.oct.entities.system.Language;

public enum LanguageEnum {
	
	// @formatter:off
	CZECH(1, "cs", "oct.lang.Czech", 2), 
	DANISH(2, "da", "oct.lang.Danish", 3), 
	GERMAN(3, "de", "oct.lang.German", 4), 
	ESTONIAN(4, "et", "oct.lang.Estonian", 5), 
	GREEK(5, "el", "oct.lang.Greek", 6),
	ENGLISH(6, "en", "oct.lang.English", 7),
	SPANISH(7,"es", "oct.lang.Spanish", 8), 
	FRENCH(8, "fr", "oct.lang.French", 9), 
	ITALIAN(9, "it", "oct.lang.Italian", 111),
	LATVIAN(10, "lv", "oct.lang.Latvian", 112), 
	LITHUANIAN(11, "lt", "oct.lang.Lithuanian", 113),
	GAELIC(12, "ga", "oct.lang.Gaelic", 10), 
	HUNGARIAN(13, "hu", "oct.lang.Hungarian", 114), 
	MALTESE(14, "mt", "oct.lang.Maltese", 115), 
	DUTCH(15, "nl", "oct.lang.Dutch", 116), 
	POLISH(16, "pl", "oct.lang.Polish", 117), 
	PORTUGUESE(17, "pt", "oct.lang.Portuguese", 118), 
	ROMANIAN(18, "ro", "oct.lang.Romanian", 119), 
	SLOVAK(19, "sk", "oct.lang.Slovak", 120), 
	SLOVENIAN(20, "sl", "oct.lang.Slovenian", 121), 
	FINNISH(21, "fi", "oct.lang.Finnish", 122), 
	SWEDISH(22, "sv", "oct.lang.Swedish", 123),
	BULGARIAN(23, "bg", "oct.lang.Bulgarian", 1), 
	CROATIAN(24, "hr", "oct.lang.Croatian", 11);
	// @formatter:on
	
	private long id;
	private String langCode;
	private String name;
	private long displayOrder;
	
	LanguageEnum(long id, String langCode, String name, long displayOrder){
		this.setId(id);
		this.setLangCode(langCode);
		this.setName(name);
		this.setDisplayOrder(displayOrder);
	}
	
	public static Language getLanguageFromEnum(LanguageEnum languageEnum){
		Language language = new Language();
		language.setCode(languageEnum.getLangCode());
		language.setDisplayOrder(languageEnum.getDisplayOrder());
//		language.setLabel(languageEnum.getLabel());
		language.setId(languageEnum.getId());
		language.setName(languageEnum.getName());
		return language;
	}
	
	public static boolean contains(String languageCode) {

	    for (LanguageEnum l : LanguageEnum.values()) {
	        if (l.getLangCode().equals(languageCode)) {
	            return true;
	        }
	    }

	    return false;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getLangCode() {
		return langCode;
	}

	public void setLangCode(String langCode) {
		this.langCode = langCode;
	}
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public long getDisplayOrder() {
		return displayOrder;
	}

	public void setDisplayOrder(long displayOrder) {
		this.displayOrder = displayOrder;
	}

}
