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

package hunterbot.Panel;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

import hunterbot.BotMessageQueue;
import hunterbot.Hunter;
import hunterbot.PriorityList;
import hunterbot.SaveState;

public class OptionsFrame {

	private JFrame frame;
	
	private static boolean signupsActive;
	
	private static Timer backupTimer;
	
	public OptionsFrame() {
		frame = new JFrame("Options");
		frame.setSize(400, 80);
		frame.setVisible(false);
		
		signupsActive = false;
		
		int backupDelay = 300000;
		ActionListener backupTimerAction = new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				System.out.println("Doing automatic backup!");
				saveBackup();
			}
		};
		backupTimer = new Timer(backupDelay, backupTimerAction);
		JPanel options = new JPanel();
		
		JButton toggleBotButton = new JButton("Turn Bot On");
		toggleBotButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if (signupsActive) {
					signupsActive = false;
					toggleBotButton.setText("Turn Bot On");
					BotMessageQueue.addPriorityMessage("Signups are now closed.");
				}
				else {
					signupsActive = true;
					toggleBotButton.setText("Turn Bot Off");
					BotMessageQueue.addPriorityMessage("Signups are now open!");
				}
			}
		});
		JButton saveStateButton = new JButton("Save State");
		saveStateButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				saveBackup();
			}
		});
		
		JButton loadStateButton = new JButton("Load State");
		loadStateButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				loadBackup();
			}
		});
		
		options.add(toggleBotButton);
		options.add(saveStateButton);
		options.add(loadStateButton);
		frame.add(options);
	}
	
	public void setVisible(boolean b) {
		frame.setVisible(b);
	}
	
	public void setLocation(Point loc) {
		frame.setLocation(loc);
	}
	
	public static boolean getSignupsActive() {
		return signupsActive;
	}
	
	private void saveBackup() {
		Vector<Hunter> hunterList = HunterTableFrame.getHunterList();
		PriorityList priorityList = PriorityListFrame.getPriorityList();
		SaveState backupState = new SaveState(priorityList, hunterList);
		backupState.doSaveState();
	}
	
	private void loadBackup() {
		SaveState backupState = new SaveState();
		backupState.doLoadState();
		HunterTableFrame.setHunterList(backupState.getBackupHunters());
		PriorityListFrame.setPriorityList(backupState.getBackupPriority());
	}
	
	public static void startBackupTimer() {
		backupTimer.start();
	}
	
}
