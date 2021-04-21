package eu.europa.ec.eci.oct.webcommons.services.enums;


import eu.europa.ec.eci.oct.entities.PropertyType;

public enum PropertyEnum {

	//@formatter:off
	RESIDENCE_STREET(1,"oct.property.street","street",87,PropertyType.LARGETEXT,2),
	RESIDENCE_POSTAL_CODE(2,"oct.property.postal.code","postal.code",85,PropertyType.ALPHANUMERIC,2),
	RESIDENCE_CITY(3,"oct.property.city","city",84,PropertyType.ALPHANUMERIC,2),
	RESIDENCE_COUNTRY(4,"oct.property.country","country",83,PropertyType.COUNTRY,2),
	DATE_OF_BIRTH(5,"oct.property.date.of.birth","date.of.birth",97,PropertyType.DATE,1),
	PASSPORT(7,"oct.property.passport","passport",0,PropertyType.ALPHANUMERIC,3),
	ID_CARD(8,"oct.property.id.card","id.card",0,PropertyType.ALPHANUMERIC,3),
	RESIDENCE_PERMIT(9,"oct.property.residence.permit","residence.permit",0,PropertyType.ALPHANUMERIC,3),
	PERSONAL_NUMBER(10,"oct.property.personal.number","personal.number",0,PropertyType.ALPHANUMERIC,3),
	PERSONAL_ID(11,"oct.property.personal.id","personal.id",0,PropertyType.ALPHANUMERIC,3),
	PERMANENT_RESIDENCE(12,"oct.property.permanent.residence","permanent.residence",0,PropertyType.ALPHANUMERIC,3),
	NATIONAL_ID_NUMBER(13,"oct.property.national.id.number","national.id.number",0,PropertyType.ALPHANUMERIC,3),
	REGISTRATION_CERTIFICATE(14,"oct.property.registration.certificate","registration.certificate",0,PropertyType.ALPHANUMERIC,3),
	CITIZEN_CARD(15,"oct.property.citizens.card","citizens.card",0,PropertyType.ALPHANUMERIC,3),
	FULL_FIRST_NAMES(16,"oct.property.full.first.names","full.first.names",99,PropertyType.ALPHANUMERIC,1),
	FAMILY_NAMES(17,"oct.property.family.names","family.names",98,PropertyType.ALPHANUMERIC,1),
	RESIDENCE_STREET_NUMBER(18,"oct.property.street.number","street.number",92,PropertyType.NATIONALITY,2);
	//@formatter:on

	private long id;
	private String dbName;
	private String name;
	private long priority;
	private PropertyType type;
	private long groupId;

	PropertyEnum(long id, String dbName, String name, long priority, PropertyType type, long groupId) {
		this.id = id;
		this.dbName = dbName;
		this.name = name;
		this.priority = priority;
		this.type = type;
		this.groupId = groupId;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
	
	public String getDbName() {
		return dbName;
	}
	
	public void setDbName(String dbName) {
		this.dbName = dbName;
	}

	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public long getPriority() {
		return priority;
	}

	public void setPriority(long priority) {
		this.priority = priority;
	}

	public PropertyType getType() {
		return type;
	}

	public void setType(PropertyType type) {
		this.type = type;
	}

	public long getGroupId() {
		return groupId;
	}

	public void setGroupId(long groupId) {
		this.groupId = groupId;
	}
	
}
