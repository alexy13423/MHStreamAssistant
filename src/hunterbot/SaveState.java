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

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Vector;

import hunterbot.Panel.HiredHunterFrame;
import hunterbot.Panel.HunterTableFrame;
import hunterbot.Panel.PriorityListFrame;

public class SaveState implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private PriorityList backupPriorityList;
	private Vector<Hunter> backupHunterList;
	private int maxEntries;
	private boolean priorityList;
	private boolean firstPresent, secondPresent, thirdPresent;
	private Hunter firstBackup, secondBackup, thirdBackup;
	
	public SaveState() {
	}
	
	public SaveState(PriorityList priority, Vector<Hunter> hunters) {
		backupPriorityList = priority;
		backupHunterList = hunters;
		maxEntries = HunterTableFrame.getMaxEntries();
		
		priorityList = MHStreamAssistant.priorityListPresent;

		firstPresent = HiredHunterFrame.firstHunter.getHasHunter();
		if (firstPresent) {
			firstBackup = HiredHunterFrame.firstHunter.getHunter();
		}
		
		secondPresent = HiredHunterFrame.secondHunter.getHasHunter();
		if (secondPresent) {
			secondBackup = HiredHunterFrame.secondHunter.getHunter();
		}
		
		thirdPresent = HiredHunterFrame.thirdHunter.getHasHunter();
		if (thirdPresent) {
			thirdBackup = HiredHunterFrame.thirdHunter.getHunter();
		}
	}
	
	public PriorityList getBackupPriority() {
		return backupPriorityList;
	}
	
	public Vector<Hunter> getBackupHunters() {
		return backupHunterList;
	}
	
	public int getMaxEntries() {
		return maxEntries;
	}
	
	public boolean getUsedPriority() {
		return priorityList;
	}
	
	public boolean getHasFirst() {
		return firstPresent;
	}
	
	public boolean getHasSecond() {
		return secondPresent;
	}
	
	public boolean getHasThird() {
		return thirdPresent;
	}
	
	public Hunter getFirstHunter () {
		return firstBackup;
	}
	
	public Hunter getSecondHunter() {
		return secondBackup;
	}
	
	public Hunter getThirdHunter() {
		return thirdBackup;
	}
	
	public void doSaveState() throws IOException {
		try (FileOutputStream fileOut = new FileOutputStream("backup.ser"); ObjectOutputStream listOut = new ObjectOutputStream(fileOut)) {
			listOut.writeObject(this);
		}
	}
	
	public void doLoadState() throws IOException, ClassNotFoundException {
		try (FileInputStream fileIn = new FileInputStream("backup.ser"); ObjectInputStream listIn = new ObjectInputStream(fileIn)) {
			SaveState loadedState = (SaveState) listIn.readObject();
			backupHunterList = loadedState.getBackupHunters();
			priorityList = loadedState.getUsedPriority();
			backupPriorityList = loadedState.getBackupPriority();
			PriorityListFrame.backupPriorityListRestore(backupPriorityList);
			if (priorityList) {
				MHStreamAssistant.priorityListPresent = true;
				PriorityListFrame.disableLoadListButton();
			}
			
			HunterTableFrame.getTableModel().setHunterList(backupHunterList);
			System.out.println("Entries: " + loadedState.getMaxEntries());
			HunterTableFrame.getTableModel().setMaxEntries(loadedState.getMaxEntries());
			
			boolean first = loadedState.getHasFirst();
			if (first) {
				firstBackup = loadedState.getFirstHunter();
				HiredHunterFrame.firstHunter.setHunter(firstBackup);
			}
			boolean second = loadedState.getHasSecond();
			if (second) {
				secondBackup = loadedState.getSecondHunter();
				HiredHunterFrame.secondHunter.setHunter(secondBackup);
			}
			boolean third = loadedState.getHasThird();
			if (third) {
				thirdBackup = loadedState.getThirdHunter();
				HiredHunterFrame.thirdHunter.setHunter(thirdBackup);
			}
			listIn.close();
		}
	}
	
}
