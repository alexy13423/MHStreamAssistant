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

import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.Timer;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import hunterbot.BotMessageQueue;
import hunterbot.Hunter;
import hunterbot.MyListener;
import hunterbot.OptionsConfig;
import hunterbot.PriorityList;
import hunterbot.SaveState;

public class OptionsFrame {

	private JFrame frame;
	
	private static boolean signupsActive;
	private static boolean repeatSignups;
	private static Timer backupTimer;
	
	public OptionsFrame() {
		frame = new JFrame("Options");
		frame.setSize(450, 200);
		frame.setVisible(false);
		frame.setResizable(false);
		signupsActive = false;
		repeatSignups = true;
		
		int backupDelay = 300000;
		ActionListener backupTimerAction = new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				saveBackup(false);
			}
		};
		backupTimer = new Timer(backupDelay, backupTimerAction);
		JPanel optionsMain = new JPanel();
		optionsMain.setLayout(new BoxLayout(optionsMain, BoxLayout.Y_AXIS));
		
		JPanel optionsButtons = new JPanel();
		
		JButton toggleBotButton = new JButton("Enable Signups");
		toggleBotButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if (signupsActive) {
					signupsActive = false;
					toggleBotButton.setText("Enable Signups");
					BotMessageQueue.addPriorityMessage("Signups are now closed.");
				}
				else {
					signupsActive = true;
					toggleBotButton.setText("Disable Signups");
					BotMessageQueue.addPriorityMessage("Signups are now open! Use !" + MyListener.getHireCommand() + " to join!");
				}
			}
		});
		
		JButton saveOptions = new JButton("Save Options");
		
		
		JButton saveStateButton = new JButton("Save State");
		saveStateButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				saveBackup(true);
			}
		});
		
		JButton loadStateButton = new JButton("Load State");
		loadStateButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				loadBackup();
			}
		});
		
		optionsButtons.add(toggleBotButton);
		optionsButtons.add(saveOptions);
		optionsButtons.add(saveStateButton);
		optionsButtons.add(loadStateButton);
		optionsMain.add(optionsButtons);
		
		optionsMain.add(new JSeparator(JSeparator.HORIZONTAL));
		
		JPanel customHirePanel = new JPanel();
		
		JLabel hireCustomLabel = new JLabel("Hire command:");
		customHirePanel.add(hireCustomLabel);
		
		JTextField hireCustom = new JTextField("hire");
		hireCustom.setPreferredSize(new Dimension(140, 30));
		hireCustom.setHorizontalAlignment(JTextField.CENTER);
		hireCustom.getDocument().addDocumentListener(new DocumentListener() {
			
			@Override
			public void removeUpdate(DocumentEvent e) {
				String command = hireCustom.getText();
				MyListener.setHireCommand(command);
			}
			
			@Override
			public void insertUpdate(DocumentEvent e) {
				String command = hireCustom.getText();
				MyListener.setHireCommand(command);
			}
			
			@Override
			public void changedUpdate(DocumentEvent e) {
				// Doesn't work with JTextField?
			}
		});
		customHirePanel.add(hireCustom);
		optionsMain.add(customHirePanel);
		
		optionsMain.add(new JSeparator(JSeparator.HORIZONTAL));
		
		JPanel optionsChecks = new JPanel();
		
		JCheckBox testButton = new JCheckBox("Enable Repeat Signups");
		testButton.setSelected(true);
		testButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				JCheckBox box = (JCheckBox) e.getSource();
				repeatSignups = box.isSelected();
			}
		});
		optionsChecks.add(testButton);
		optionsMain.add(optionsChecks);
		
		saveOptions.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String hireCommand = hireCustom.getText();
				boolean allowRepeats = testButton.isSelected();
				OptionsConfig config = new OptionsConfig(hireCommand, allowRepeats);
				try (FileOutputStream fileOut = new FileOutputStream("options.ser"); ObjectOutputStream configOut = new ObjectOutputStream(fileOut)) {
					configOut.writeObject(config);
				} catch (IOException i) {
					JOptionPane.showMessageDialog(frame, "Unable to save options!", "Options Save Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		frame.add(optionsMain);
		try {
			OptionsConfig config = loadOptions();
			String hire = config.getHireCommand();
			boolean repeat = config.getAllowRepeat();
			hireCustom.setText(hire);
			testButton.setSelected(repeat);
			MyListener.setHireCommand(hire);
			repeatSignups = repeat;
		} catch (Exception e) {
		}
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
	
	public static boolean getRepeatSignups() {
		return repeatSignups;
	}
	
	private void saveBackup(boolean userInitiated) {
		Vector<Hunter> hunterList = HunterTableFrame.getHunterList();
		PriorityList priorityList = PriorityListFrame.getPriorityList();
		SaveState backupState = new SaveState(priorityList, hunterList);
		try {
			backupState.doSaveState();
		} catch (IOException e) {
			if (userInitiated) {
				JOptionPane.showMessageDialog(frame, "Unable to save backup!", "Backup Save Error", JOptionPane.ERROR_MESSAGE);
			}
		}
		
	}
	
	private void loadBackup() {
		SaveState backupState = new SaveState();
		try {
			backupState.doLoadState();
		} catch (Exception e) {
			JOptionPane.showMessageDialog(frame, "Unable to load backup!", "Backup Load Error", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	private OptionsConfig loadOptions() throws IOException, ClassNotFoundException {
		OptionsConfig config;
		try (FileInputStream fileIn = new FileInputStream("options.ser"); ObjectInputStream configIn = new ObjectInputStream(fileIn)) {
			config = (OptionsConfig) configIn.readObject();
		}
		return config;
	}
	
	public static void startBackupTimer() {
		backupTimer.start();
	}
	
}
