
public enum StringConstants {
	RUINED("@@@"),
	RELOAD("reload"),
	MOVEMENT("movement");
	
	private String value;
	
	private StringConstants(String value) {
		this.value = value;
	}
	
	@Override
	public String toString() {
		return this.value;
	}
}

