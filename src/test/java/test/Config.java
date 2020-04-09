package test;

/**
 * @author Bright Lee
 */
public class Config {
	
	private String pk;
	private String configKey;
	private String configValue;
	
	public Config() {
	}

	public String getPk() {
		return pk;
	}

	public void setPk(String pk) {
		this.pk = pk;
	}

	public String getConfigKey() {
		return configKey;
	}

	public void setConfigKey(String configKey) {
		this.configKey = configKey;
	}

	public String getConfigValue() {
		return configValue;
	}

	public void setConfigValue(String configValue) {
		this.configValue = configValue;
	}

	@Override
	public String toString() {
		return "Config[pk=" + getPk() + 
				", configKey=" + getConfigKey() + 
				", configValue=" + getConfigValue() + "]";
	}

}
