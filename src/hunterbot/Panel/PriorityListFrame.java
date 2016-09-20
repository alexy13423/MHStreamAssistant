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

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;

import hunterbot.Hunter;
import hunterbot.PriorityEntry;
import hunterbot.PriorityList;
import hunterbot.MHStreamAssistant;

public class PriorityListFrame {

	private static JFrame frame;
	private static PriorityList priorityList;
	private static JButton loadPriorityList;
	private static JList<PriorityEntry> priorityListView;
	
	public PriorityListFrame() {
		frame = new JFrame("Priority List");
		frame.setSize(800, 400);
		frame.setVisible(false);
		
		JScrollPane priorityScroll = new JScrollPane();
		priorityListView = new JList<PriorityEntry>();
		priorityList = new PriorityList();
		priorityListView.setModel(priorityList);
		priorityScroll.setViewportView(priorityListView);
		LineBorder priorityBorder = new LineBorder(frame.getBackground(), 30);
		priorityScroll.setBorder(priorityBorder);
		frame.add(priorityScroll);
		
		JPanel priorityControls = new JPanel();
		priorityControls.setLayout(new FlowLayout());
		
		JButton savePriorityList = new JButton("Save Priority List");
		savePriorityList.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				if (HiredHunterFrame.getHunterCount() == 0) {
					Vector<Hunter> hunterList = HunterTableFrame.getHunterList();
					try {
						priorityList.savePriorityList(hunterList);
					} catch (IOException i) {
						JOptionPane.showMessageDialog(frame, "Unable to save priority list!", "Save Error", JOptionPane.ERROR_MESSAGE);
					}
					
				}
				else {
					JOptionPane.showMessageDialog(frame, "Please dismiss all hunters before saving the list!");
				}
			}
		});
		
		loadPriorityList = new JButton("Load Priority List");
		loadPriorityList.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				priorityList.loadPriorityList();
				loadPriorityList.setEnabled(false);
				MHStreamAssistant.priorityListPresent = true;
			}
		});
		
		JButton addPriorityList = new JButton("Add to Priority List");
		addPriorityList.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				JTextField nameField = new JTextField();
				JTextField levelField = new JTextField();
				JPanel newPriorityEntry = new JPanel();
				newPriorityEntry.setLayout(new BoxLayout(newPriorityEntry, BoxLayout.Y_AXIS));
				newPriorityEntry.add(new JLabel("Name:"));
				newPriorityEntry.add(nameField);
				newPriorityEntry.add(new JLabel("Level:"));
				newPriorityEntry.add(levelField);
				int result = JOptionPane.showConfirmDialog(frame, newPriorityEntry, "Enter the new hunter.", JOptionPane.OK_CANCEL_OPTION);
				if (result == JOptionPane.OK_OPTION) {
					String name = nameField.getText();
					String levelString = levelField.getText();
					int level = -1;
					if (MHStreamAssistant.isInteger(levelString, 10)) {
						level = Integer.parseInt(levelString);
					}
					if (level > 0) {
						PriorityEntry newEntry = new PriorityEntry(name, level);
						priorityList.addElement(newEntry);
					}
					
				}
			}
		});
		
		JButton removePriorityList = new JButton("Remove Selected");
		removePriorityList.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				int[] selectedRows = priorityListView.getSelectedIndices();
				if (selectedRows.length > 0) {
					for (int i = selectedRows.length - 1; i >= 0; i--) {
						priorityList.remove(selectedRows[i]);
					}
				}
			}
		});
		
		priorityControls.add(savePriorityList);
		priorityControls.add(loadPriorityList);
		priorityControls.add(addPriorityList);
		priorityControls.add(removePriorityList);
		
		frame.add(priorityControls, BorderLayout.PAGE_END);
	}
	
	public void setVisible(boolean b) {
		frame.setVisible(b);
	}
	
	public void setLocation(Point loc) {
		frame.setLocation(loc);
	}
	
	public static void disableLoadListButton() {
		loadPriorityList.setEnabled(false);
	}
	
	public static int searchPriorityList(String name) {
		int result = priorityList.searchAndPop(name);
		return result;
	}
	
	public static PriorityList getPriorityList() {
		return priorityList;
	}
	
	public static void setPriorityList(PriorityList p) {
		priorityList = p;
	}
	
	public static void backupPriorityListRestore(PriorityList backup) {
		priorityList = backup;
		priorityListView.setModel(priorityList);
	}
	
	public static boolean savePriorityList() {
		if (HiredHunterFrame.getHunterCount() == 0) {
			Vector<Hunter> hunterList = HunterTableFrame.getHunterList();
			try {
				priorityList.savePriorityList(hunterList);
			} catch (IOException e) {
				JOptionPane.showMessageDialog(frame, "Unable to save priority list!", "Save Error", JOptionPane.ERROR_MESSAGE);
				return false;
			}
			
			return true;
		}
		else {
			JOptionPane.showMessageDialog(frame, "Please dismiss all hunters before saving the list!");
			return false;
		}
	}
	
}
