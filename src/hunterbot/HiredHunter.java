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

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Serializable;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import hunterbot.Panel.HiredHunterFrame;
import hunterbot.Panel.HunterTableFrame;

public class HiredHunter implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private transient JPanel hunterPanel;
	
	private boolean hasHunter;
	private Hunter myHunter;
	
	private transient JTextField twitchNameField;
	private transient JTextField hunterNameField;
	private transient JTextField hunterRankField;
	private transient JTextField huntCountField;
	private transient JTextField huntPickedField;
	
	private transient JButton incrementHunt;
	private transient JButton rotateOut;
	private transient JButton pickedHunt;
	private transient JButton skipHunter;
	private transient JButton bailHunter;
	private transient JButton returnToQueue;
	
	public HiredHunter() {
		hasHunter = false;
		myHunter = null;
		
		twitchNameField = new JTextField();
		twitchNameField.setEditable(false);
		hunterNameField = new JTextField();
		hunterNameField.setEditable(false);
		hunterRankField = new JTextField();
		hunterRankField.setEditable(false);
		huntCountField = new JTextField();
		huntCountField.setEditable(false);
		huntPickedField = new JTextField();
		huntPickedField.setEditable(false);
		
		incrementHunt = new JButton("Increment Hunts");
		incrementHunt.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				incrementHunts();
			}
		});
		rotateOut = new JButton("Rotate Out");
		rotateOut.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				Hunter h = clearHunter();
				HiredHunterFrame.currentHunters--;
				h.setState(Hunter.State.PLAYED);
				HunterTableFrame.updateHunter(h);
				String name = h.getTwitchName();
				BotMessageQueue.addRegularMessage(BotMessageQueue.MessageType.TURNDONE, name);
				HunterTableFrame.addPlayedViewer(h.getTwitchName());
			}
		});
		pickedHunt = new JButton("Toggle Picked");
		pickedHunt.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				toggleHuntPicked();
			}
		});
		skipHunter = new JButton("Skip Hunter");
		skipHunter.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				Hunter h = clearHunter();
				HiredHunterFrame.currentHunters--;
				h.setState(Hunter.State.SKIPPED);
				HunterTableFrame.updateHunter(h);
				String name = h.getTwitchName();
				BotMessageQueue.addRegularMessage(BotMessageQueue.MessageType.TURNSKIPPED, name);
			}
		});
		returnToQueue = new JButton("Undo Hire");
		returnToQueue.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				Hunter h = clearHunter();
				HiredHunterFrame.currentHunters--;
				PriorityNumber p = h.getPriority();
				int pLevel = p.getPriorityLevel();
				if (pLevel > 0) {
					h.setState(Hunter.State.PRIORITYWAITING);
				}
				else h.setState(Hunter.State.WAITING);
				HunterTableFrame.updateHunter(h);
				String name = h.getTwitchName();
				BotMessageQueue.addRegularMessage(BotMessageQueue.MessageType.TURNUNDONE, name);
			}
		});
		bailHunter = new JButton("Hunter Bailed!");
		bailHunter.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				Hunter h = clearHunter();
				HiredHunterFrame.currentHunters--;
				h.setState(Hunter.State.BAILED);
				HunterTableFrame.updateHunter(h);
				String name = h.getTwitchName();
				BotMessageQueue.addRegularMessage(BotMessageQueue.MessageType.TURNBAILED, name);
			}
		});
		
		createPanel();
	}
	
	public void createPanel() {
		hunterPanel = new JPanel();
		hunterPanel.setLayout(new BoxLayout(hunterPanel, BoxLayout.Y_AXIS));
		JPanel hunterInformation = new JPanel();
		hunterInformation.setLayout(new BoxLayout(hunterInformation, BoxLayout.Y_AXIS));
		JLabel twitchLabel = new JLabel("Twitch Name");
		JLabel hunterLabel = new JLabel("Hunter Name");
		JLabel rankLabel = new JLabel("Hunter Rank");
		JLabel countLabel = new JLabel("Hunts Done");
		JLabel pickLabel = new JLabel("Hunt Picked");
		
		hunterInformation.add(twitchLabel);
		hunterInformation.add(twitchNameField);
		hunterInformation.add(hunterLabel);
		hunterInformation.add(hunterNameField);
		hunterInformation.add(rankLabel);
		hunterInformation.add(hunterRankField);
		hunterInformation.add(countLabel);
		hunterInformation.add(huntCountField);
		hunterInformation.add(pickLabel);
		hunterInformation.add(huntPickedField);
		
		JPanel hunterControls = new JPanel();
		hunterControls.setLayout(new BorderLayout());
		
		JPanel hunterControlsTop = new JPanel();
		JPanel hunterControlsBottom = new JPanel();
		hunterControlsTop.add(incrementHunt);
		hunterControlsTop.add(pickedHunt);
		hunterControlsTop.add(rotateOut);
		hunterControlsBottom.add(skipHunter);
		hunterControlsBottom.add(returnToQueue);
		hunterControlsBottom.add(bailHunter);
		hunterControls.add(hunterControlsTop, BorderLayout.PAGE_START);
		hunterControls.add(hunterControlsBottom, BorderLayout.PAGE_END);
		
		hunterPanel.add(hunterInformation, BorderLayout.CENTER);
		hunterPanel.add(hunterControls, BorderLayout.PAGE_END);
	}
	
	public JPanel getPanel() {
		return hunterPanel;
	}
	
	public void setHunter(Hunter h) {
		if (h != null) {
			hasHunter = true;
			myHunter = h;
			twitchNameField.setText(myHunter.getTwitchName());
			hunterNameField.setText(myHunter.getHunterName());
			hunterRankField.setText(Integer.toString(myHunter.getHunterRank()));
			huntCountField.setText(Integer.toString(myHunter.getHunts()));
			if (myHunter.pickedHunt()) {
				huntPickedField.setText("yes");
			}
			else huntPickedField.setText("no");
			HiredHunterFrame.currentHunters++;
		}
	}
	
	private Hunter clearHunter() {
		hasHunter = false;
		twitchNameField.setText("");
		hunterNameField.setText("");
		hunterRankField.setText("");
		huntCountField.setText("");
		huntPickedField.setText("");
		return myHunter;
	}
	
	private void incrementHunts() {
		myHunter.incrementHunts();
		huntCountField.setText(Integer.toString(myHunter.getHunts()));
	}
	
	private void toggleHuntPicked() {
		myHunter.togglePickedHunt();
		if (myHunter.pickedHunt()) {
			huntPickedField.setText("yes");
		}
		else huntPickedField.setText("no");
	}
	
	public boolean getHasHunter() {
		return hasHunter;
	}
	
	public Hunter getHunter() {
		return myHunter;
	}
	
	public void doBackup(Hunter backup) {
		setHunter(backup);
	}
	
}
