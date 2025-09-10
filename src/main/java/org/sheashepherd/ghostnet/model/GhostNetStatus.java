package org.sheashepherd.ghostnet.model;

public enum GhostNetStatus {
	REPORTED("Gemeldet"), 
	CLAIMED("Bergung bevorstehend"), 
	MISSING("Verschollen"), 
	RECOVERED("Geborgen");

	private final String label;

	GhostNetStatus(String label) {
		this.label = label;
	}

	public String getLabel() {
		return label;
	}
}
