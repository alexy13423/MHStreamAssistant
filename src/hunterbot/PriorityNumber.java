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

public class PriorityNumber implements Serializable, Comparable<PriorityNumber>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int priorityLevel;
	private int queueNumber;
	
	public PriorityNumber(int level, int number) {
		priorityLevel = level;
		queueNumber = number;
	}
	
	public int getPriorityLevel() {
		return priorityLevel;
	}
	
	public int getQueueNumber() {
		return queueNumber;
	}
	
	public String toString() {
		return "L" + priorityLevel + "#" + queueNumber;
	}

	@Override
	public int compareTo(PriorityNumber arg0) {
		
		int level = arg0.getPriorityLevel();
		if (priorityLevel > level) {
			return -1;
		} else if (priorityLevel < level) {
			return 1;
		}
		
		int number = arg0.getQueueNumber();
		
		if (queueNumber > number) {
			return 1;
		}
		else if (queueNumber < number) {
			return -1;
		}
		
		return 0;
	}
	
}
