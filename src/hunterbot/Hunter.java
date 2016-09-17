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

public class Hunter implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public enum State {
		PRIORITYWAITING("Waiting"), WAITING("Waiting"), PLAYING("Playing"), PLAYED("Done"), SKIPPED("Skipped"), BAILED("Bailed");
		
		private String outputString;
		
		private State(String blah) {
			outputString = blah;
		}
		
		public String toString() {
			return outputString;
		}
	}

	private String twitchName;
	private String hunterName;
	private int hunterRank;
	private PriorityNumber priority;
	private State hunterState;
	
	private int huntsDone;
	private boolean pickedHunt;
	
	public Hunter(String twitchName, String hunterName, int rank, int priorityNumber, int queueNumber) {
		this.twitchName = twitchName;
		this.hunterName = hunterName;
		hunterRank = rank;
		priority = new PriorityNumber(priorityNumber, queueNumber);
		if (priorityNumber > 0)
			hunterState = State.PRIORITYWAITING;
		else hunterState = State.WAITING;
		
		huntsDone = 0;
		pickedHunt = false;
	}
	
	public String getTwitchName() {
		return twitchName;
	}
	
	public String getHunterName() {
		return hunterName;
	}
	
	public int getHunterRank() {
		return hunterRank;
	}
	
	public PriorityNumber getPriority() {
		return priority;
	}
	
	public String toString() {
		return twitchName + " - Hunter Name: " + hunterName + " - Hunter Rank: " + hunterRank;
	}
	
	public boolean equals(Object object) {
		return false;
	}
	
	public int getHunts() { return huntsDone; }
	
	public void incrementHunts() { huntsDone++; }
	
	public boolean pickedHunt() { return pickedHunt; }
	
	public void togglePickedHunt() { pickedHunt = !pickedHunt; }
	
	public void setState(State newState) {
		hunterState = newState;
	}
	
	public State getState() {
		return hunterState;
	}
	
}
