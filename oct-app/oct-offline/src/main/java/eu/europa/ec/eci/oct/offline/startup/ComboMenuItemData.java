package eu.europa.ec.eci.oct.offline.startup;

public class ComboMenuItemData implements Comparable<ComboMenuItemData> {

	private String language = null;
	private int order = 0;
	private String description = null;
	
	public ComboMenuItemData(String language, int order, String description){
		this.language = language;
		this.order = order;
		this.description = description;
	}
	
	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public String toString() {
		return description;
	}

	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}

	@Override
	public int compareTo(ComboMenuItemData o) {
		if(this.getOrder() < o.getOrder()){
			return -1;
		}else if(this.getOrder() == o.getOrder()){
			return 0;
		}
		return 1;
	}
}
