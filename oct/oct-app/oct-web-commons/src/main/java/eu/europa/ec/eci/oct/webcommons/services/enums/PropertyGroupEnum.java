package eu.europa.ec.eci.oct.webcommons.services.enums;

public enum PropertyGroupEnum {

	// @formatter:on

	GENERAL(1, 0, "oct.group.general", 2), ADDRESS(2, 0, "oct.group.addess", 3), ID(3, 1, "oct.group.id", 1);

	// @formatter:off

	private long id;
	private int multiChoice;
	private String name;
	private long priority;

	PropertyGroupEnum(long id, int multiChoice, String name, long priority) {
		this.id = id;
		this.multiChoice = multiChoice;
		this.name = name;
		this.priority = priority;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public int getMultiChoice() {
		return multiChoice;
	}

	public void setMultiChoice(int multiChoice) {
		this.multiChoice = multiChoice;
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

}
