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
	private boolean priorityList;
	private Hunter firstBackup, secondBackup, thirdBackup;
	
	public SaveState() {
		backupPriorityList = null;
		backupHunterList = null;
		priorityList = MHStreamAssistant.priorityListPresent;
		firstBackup = HiredHunterFrame.firstHunter.getHunter();
		secondBackup = HiredHunterFrame.secondHunter.getHunter();
		thirdBackup = HiredHunterFrame.thirdHunter.getHunter();
	}
	
	public SaveState(PriorityList priority, Vector<Hunter> hunters) {
		backupPriorityList = priority;
		backupHunterList = hunters;
		
		priorityList = MHStreamAssistant.priorityListPresent;
		firstBackup = HiredHunterFrame.firstHunter.getHunter();
		secondBackup = HiredHunterFrame.secondHunter.getHunter();
		thirdBackup = HiredHunterFrame.thirdHunter.getHunter();
	}
	
	public PriorityList getBackupPriority() {
		return backupPriorityList;
	}
	
	public Vector<Hunter> getBackupHunters() {
		return backupHunterList;
	}
	
	public boolean getUsedPriority() {
		return priorityList;
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
	
	public void doSaveState() {
		try {
			FileOutputStream fileOut = new FileOutputStream("backup.ser");
			ObjectOutputStream listOut = new ObjectOutputStream(fileOut);
			listOut.writeObject(this);
			listOut.close();
		} catch (IOException i) {
			i.printStackTrace();
		}
	}
	
	public void doLoadState() {
		try {
			FileInputStream fileIn = new FileInputStream("backup.ser");
			ObjectInputStream listIn = new ObjectInputStream(fileIn);
			SaveState loadedState = (SaveState) listIn.readObject();
			backupHunterList = loadedState.getBackupHunters();
			priorityList = loadedState.getUsedPriority();
			backupPriorityList = loadedState.getBackupPriority();
			PriorityListFrame.backupPriorityListRestore(backupPriorityList);
			if (priorityList) {
				System.out.println("doot");
				
				MHStreamAssistant.priorityListPresent = true;
				PriorityListFrame.disableLoadListButton();
			}
			
			HunterTableFrame.getTableModel().setHunterList(backupHunterList);
			
			firstBackup = loadedState.getFirstHunter();
			secondBackup = loadedState.getSecondHunter();
			thirdBackup = loadedState.getThirdHunter();
			
			HiredHunterFrame.firstHunter.setHunter(firstBackup);
			HiredHunterFrame.secondHunter.setHunter(secondBackup);
			HiredHunterFrame.thirdHunter.setHunter(thirdBackup);
			
			listIn.close();
		} catch (IOException i) {
			i.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
}
