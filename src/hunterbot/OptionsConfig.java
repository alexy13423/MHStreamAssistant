package hunterbot;

import java.io.Serializable;

public class OptionsConfig implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String hireCommand;
	private boolean allowRepeatViewers;
	
	public OptionsConfig(String command, boolean allow) {
		hireCommand = command;
		allowRepeatViewers = allow;
	}
	
	public String getHireCommand() {
		return hireCommand;
	}
	
	public boolean getAllowRepeat() {
		return allowRepeatViewers;
	}
	
}
