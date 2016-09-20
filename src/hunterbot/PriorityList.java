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

import javax.swing.DefaultListModel;

import hunterbot.Panel.HunterTableFrame;

public class PriorityList extends DefaultListModel<PriorityEntry> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public PriorityList() {
	}
	
	public void savePriorityList(Vector<Hunter> hunterList) throws IOException{
		
		Vector<String> playedViewers = HunterTableFrame.getPlayedViewers();
		
		for (int i = 0; i < hunterList.size(); i++) {
			Hunter hunter = hunterList.get(i);
			String name = hunter.getTwitchName();
			boolean hasPlayed = false;
			for (int j = 0; j < playedViewers.size(); j++) {
				String playedName = playedViewers.get(j);
				if (name.equals(playedName)) {
					hasPlayed = true;
					break;
				}
			}
			if (hasPlayed)
				continue;
			
			Hunter.State hunterState = hunter.getState();
			if (hunterState == Hunter.State.WAITING || hunterState == Hunter.State.PRIORITYWAITING || hunterState == Hunter.State.SKIPPED) {
				PriorityNumber queueNumber = hunter.getPriority();
				int priorityLevel = queueNumber.getPriorityLevel();
				priorityLevel++;
				PriorityEntry newEntry = new PriorityEntry(name, priorityLevel);
				this.addElement(newEntry);
			}
		}
		
		Object[] priorityArray = this.toArray();
		
		//Write priority list to file.
		FileOutputStream fileOut = new FileOutputStream("priority.ser");
		ObjectOutputStream listOut = new ObjectOutputStream(fileOut);
		listOut.writeObject(priorityArray);
		listOut.close();
	}
	
	public void loadPriorityList() {
		//Make absolute certain the priority list is clear before starting!
		this.clear();
		
		//Read priority list from file.
		try {
			FileInputStream fileIn = new FileInputStream("priority.ser");
			ObjectInputStream listIn = new ObjectInputStream(fileIn);
			Object[] priorityArray = (Object[]) listIn.readObject();
			for (int i = 0; i < priorityArray.length; i++) {
				this.addElement((PriorityEntry) priorityArray[i]);
			}
			listIn.close();
		} catch (IOException i) {
			i.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public int searchAndPop(String twitchName) {
		
		for (int i = 0; i < this.size(); i++) {
			PriorityEntry entry = this.get(i);
			String name = entry.getName().toLowerCase();
			if (name.equals(twitchName)) {
				this.remove(i);
				return entry.getLevel();
			}
		}
		return 0;
	}
	
}
