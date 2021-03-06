/**
 *  Copyright (C) 2016 Alexander Le <alexyle@gmail.com>
 *  
 *  This file is part of MHStreamHelper.
 *  
 *  MHStreamHelper is free software: you can redistribute it and/or modify it under the
 *  terms of the GNU General Public License as published by the Free Software
 *  Foundation, either version 3 of the License, or (at your option) any later
 *  version.
 *  
 *  MHStreamHelper is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with MHStreamHelper.  If not, see <http://www.gnu.org/licenses/>.
 */

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
