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
import java.awt.Color;
import java.awt.Component;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableRowSorter;
import javax.swing.RowSorter;
import javax.swing.SortOrder;

import hunterbot.BotMessageQueue;
import hunterbot.Hunter;
import hunterbot.HunterTableModel;
import hunterbot.MHStreamAssistant;

public class HunterTableFrame {

	private JFrame frame;
	private static HunterTableModel hunterTableModel;
	private static JTable hunterTable;
	private static Vector<String> playedViewers;
	
	public HunterTableFrame() {
		frame = new JFrame("Viewer List");
		frame.setSize(800, 800);
		frame.setVisible(false);
		frame.setBackground(Color.CYAN);
		
		playedViewers = new Vector<String>();
		
		JScrollPane hunterScroll = new JScrollPane();
		hunterTableModel = new HunterTableModel();
		hunterTable = new JTable(hunterTableModel);
		hunterScroll.setViewportView(hunterTable);
		frame.add(hunterScroll);
		
		TableRowSorter<HunterTableModel> sorter = new TableRowSorter<HunterTableModel>(hunterTableModel);
		List<RowSorter.SortKey> sortKeys = new ArrayList<RowSorter.SortKey>();
		sortKeys.add(new RowSorter.SortKey(1, SortOrder.ASCENDING));
		sortKeys.add(new RowSorter.SortKey(0, SortOrder.ASCENDING));
		sorter.setSortKeys(sortKeys);
		for (int i = 0; i < 6; i++) {
			sorter.setSortable(i, false);
		}
		
		hunterTable.setRowSorter(sorter);
		
		hunterTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			public Component getTableCellRendererComponent(JTable table,
		            Object value, boolean isSelected, boolean hasFocus, int row, int col) {
				
		        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
		        HunterTableModel model = (HunterTableModel) table.getModel();
		        Hunter hunter = model.getHunter(table.convertRowIndexToModel(row));
		        Hunter.State state = hunter.getState();
		        
		        if (state == Hunter.State.PRIORITYWAITING){
					setBackground(Color.CYAN);
					setForeground(Color.BLACK);
				}
				else if (state == Hunter.State.SKIPPED) {
					setBackground(Color.ORANGE);
					setForeground(Color.BLACK);
				}
				else if (state == Hunter.State.PLAYING) {
					setBackground(Color.YELLOW);
					setForeground(Color.BLACK);
				}
				else if (state == Hunter.State.PLAYED) {
					setBackground(Color.GREEN);
					setForeground(Color.BLACK);
				}
				else if (state == Hunter.State.BAILED) {
					setBackground(Color.BLACK);
					setForeground(Color.RED);
				}
				else {
					setBackground(table.getBackground());
					setForeground(table.getForeground());
				}
		        return this;
		    }
		});
		
		JPanel hunterControls = new JPanel();
		hunterControls.setLayout(new BorderLayout(15, 15));
		
		JPanel topRowControls = new JPanel();
		JButton hireNextButton = new JButton("Pick Next");
		hireNextButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				hireHunter(1);
			}
		});
		JButton hireSelectedButton = new JButton("Pick Selected");
		hireSelectedButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				hireHunter(0);
			}
		});
		JButton addHunterButton = new JButton("Add New Viewer");
		addHunterButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				JTextField nameField = new JTextField();
				JTextField hunterNameField = new JTextField();
				JTextField hunterRankField = new JTextField();
				JPanel newHunterEntry = new JPanel();
				newHunterEntry.setLayout(new BoxLayout(newHunterEntry, BoxLayout.Y_AXIS));
				newHunterEntry.add(new JLabel("Twitch name:"));
				newHunterEntry.add(nameField);
				newHunterEntry.add(new JLabel("Hunter name:"));
				newHunterEntry.add(hunterNameField);
				newHunterEntry.add(new JLabel("Hunter rank:"));
				newHunterEntry.add(hunterRankField);
				int result = JOptionPane.showConfirmDialog(frame, newHunterEntry, "Enter the new hunter.", JOptionPane.OK_CANCEL_OPTION);
				if (result == JOptionPane.OK_OPTION) {
					String twitchName = nameField.getText();
					String hunterName = hunterNameField.getText();
					String rankString = hunterRankField.getText();
					if (MHStreamAssistant.isInteger(rankString, 10)) {
						addHunter(twitchName, hunterName, Integer.parseInt(rankString));
					}
				}
			}
		});
		topRowControls.add(hireSelectedButton);
		topRowControls.add(hireNextButton);
		topRowControls.add(addHunterButton);
		hunterControls.add(topRowControls,BorderLayout.PAGE_START);
		
		JPanel middleRowControls = new JPanel();
		JButton hireBreakButton = new JButton("Pick HR Break");
		hireBreakButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				hireHunter(2);
			}
		});
		JButton hireHRButton = new JButton("Pick HR");
		hireHRButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				hireHunter(3);
			}
		});
		JButton hireLRButton = new JButton("Pick LR");
		hireLRButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				hireHunter(4);
			}
		});
		
		middleRowControls.add(hireBreakButton);
		middleRowControls.add(hireHRButton);
		middleRowControls.add(hireLRButton);
		hunterControls.add(middleRowControls, BorderLayout.CENTER);
		
		JPanel bottomRowControls = new JPanel();
		JButton toggleSkipButton = new JButton("Toggle Skip");
		toggleSkipButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				toggleStatus(0);
			}
		});
		JButton toggleBailButton = new JButton("Toggle Bailed");
		toggleBailButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				toggleStatus(1);
			}
		});
		JButton cleanListButton = new JButton("Clear Played Viewers");
		cleanListButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				hunterTableModel.clearDoneEntries();
			}
		});
		
		bottomRowControls.add(toggleSkipButton);
		bottomRowControls.add(toggleBailButton);
		bottomRowControls.add(cleanListButton);
		hunterControls.add(bottomRowControls, BorderLayout.PAGE_END);
		frame.add(hunterControls, BorderLayout.PAGE_END);
	}
	
	public void setVisible(boolean b) {
		frame.setVisible(b);
	}
	
	public void setLocation(Point loc) {
		frame.setLocation(loc);
	}
	
	private boolean hireHunter(int hireType) {
		if (HiredHunterFrame.getHunterCount() > 2) {
			JOptionPane.showMessageDialog(frame, "Hunter capacity already at max!");
			return false;
		}
		Hunter hunter = null;
		int selectedRow = -1;
		if (hireType == 0) {
			selectedRow = hunterTable.convertRowIndexToModel(hunterTable.getSelectedRow());
			if (selectedRow != -1) {
				hunter = hunterTableModel.getHunter(selectedRow);
				System.out.println("Hunter: " + hunter);
			} else {
				JOptionPane.showMessageDialog(frame, "No hunter selected!");
				return false;
			}
		} else {
			Vector<Hunter> hunterList = hunterTableModel.getHunterList();
			boolean hunterFound = false;
			for (int i = 0; i < hunterList.size(); i++) {
				Hunter currentHunter = hunterList.get(i);
				Hunter.State state = currentHunter.getState();
				if (state == Hunter.State.WAITING || state == Hunter.State.PRIORITYWAITING) {
					switch (hireType) {
					case 1:
						hunter = currentHunter;
						selectedRow = i;
						hunterFound = true;
						break;
					case 2:
						if (currentHunter.getHunterRank() >= 8) {
							hunter = currentHunter;
							selectedRow = i;
							hunterFound = true;
						}
						break;
					case 3:
						if (currentHunter.getHunterRank() >= 4 && currentHunter.getHunterRank() < 8) {
							hunter = currentHunter;
							selectedRow = i;
							hunterFound = true;
						}
						break;
					case 4:
						if (currentHunter.getHunterRank() < 4) {
							hunter = currentHunter;
							selectedRow = i;
							hunterFound = true;
						}
						break;
					}
				}
				if (hunterFound) {
					break;
				}
			}
			if (hunter == null) {
				JOptionPane.showMessageDialog(frame, "Unable to find a hunter in this category!");
				return false;
			}
		}
		hunter.setState(Hunter.State.PLAYING);
		hunterTableModel.updateHunter(selectedRow, hunter);
		HiredHunterFrame.hireHunter(hunter);
		return true;
	}
	
	private boolean toggleStatus(int status) {
		int selectedRow = hunterTable.convertRowIndexToModel(hunterTable.getSelectedRow());
		if (selectedRow != -1) {
			Hunter hunter = hunterTableModel.getHunter(selectedRow);
			Hunter.State state = hunter.getState();
			switch (status) {
			case 0:
				if (state == Hunter.State.SKIPPED) {
					int priorityLevel = hunter.getPriority().getPriorityLevel();
					if (priorityLevel > 0) {
						hunter.setState(Hunter.State.PRIORITYWAITING);
					} else {
						hunter.setState(Hunter.State.WAITING);
					}
					BotMessageQueue.addRegularMessage(BotMessageQueue.MessageType.TABLEUNMARKSKIP, hunter.getTwitchName());
				} else {
					hunter.setState(Hunter.State.SKIPPED);
					BotMessageQueue.addRegularMessage(BotMessageQueue.MessageType.TABLEMARKSKIP, hunter.getTwitchName());
				}
				break;
			case 1:
				if (state == Hunter.State.BAILED) {
					int priorityLevel = hunter.getPriority().getPriorityLevel();
					if (priorityLevel > 0) {
						hunter.setState(Hunter.State.PRIORITYWAITING);
					} else {
						hunter.setState(Hunter.State.WAITING);
					}
					BotMessageQueue.addRegularMessage(BotMessageQueue.MessageType.TABLEUNMARKBAIL, hunter.getTwitchName());
				} else {
					hunter.setState(Hunter.State.BAILED);
					BotMessageQueue.addRegularMessage(BotMessageQueue.MessageType.TABLEMARKBAIL, hunter.getTwitchName());
				}
				break;
			}
			hunterTableModel.updateHunter(selectedRow, hunter);
			return true;
		} else {
			JOptionPane.showMessageDialog(frame, "No hunter selected!");
			return false;
		}
	}
	
	public static void addHunter(String name, String hunter, int rank) {
		for (int i = 0; i < hunterTableModel.getRowCount(); i++) {
			Hunter thisHunter = hunterTableModel.getHunter(i);
			String tableNameLower = thisHunter.getTwitchName().toLowerCase();
			String newNameLower = name.toLowerCase();
			if (tableNameLower.equals(newNameLower)) {
				if (OptionsFrame.getRepeatSignups() == false) {
					BotMessageQueue.addRegularMessage(BotMessageQueue.MessageType.QUEUEALREADYPRESENT, name);
					return;
				}
				Hunter.State state = thisHunter.getState();
				if (state != Hunter.State.PLAYED) {
					BotMessageQueue.addRegularMessage(BotMessageQueue.MessageType.QUEUEALREADYPRESENT, name);
					return;
				}
			}
		}
		int priority = PriorityListFrame.searchPriorityList(name);
		int queue = getCurrentQueueNumber(priority);
		hunterTableModel.addHunter(new Hunter(name, hunter, rank, priority, queue));
		BotMessageQueue.addRegularMessage(BotMessageQueue.MessageType.QUEUEADD, name);
	}
	
	public static void updateHunter(Hunter h) {
		int lastIndex = -1;
		for (int i = 0; i < hunterTableModel.getRowCount(); i++) {
			Hunter thisHunter = hunterTableModel.getHunter(i);
			String tableNameLower = thisHunter.getTwitchName().toLowerCase();
			String newNameLower = h.getTwitchName().toLowerCase();
			if (tableNameLower.equals(newNameLower)) {
				lastIndex = i;
			}
		}
		hunterTableModel.updateHunter(lastIndex, h);
	}
	
	public static Vector<Hunter> getHunterList() {
		return hunterTableModel.getHunterList();
	}
	
	public static void setHunterList(Vector<Hunter> h) {
		hunterTableModel.setHunterList(h);
	}
	
	public static HunterTableModel getTableModel() {
		return hunterTableModel;
	}
	
	public static int getCurrentQueueNumber(int level) {
		int result = hunterTableModel.checkQueueAtPriorityLevel(level) + 1;
		return result;
	}
	
	public static Vector<String> getPlayedViewers() {
		return playedViewers;
	}
	
	public static void addPlayedViewer(String name) {
		playedViewers.add(name);
	}
	
	public static Object[] getSpreadsheetOutput() {
		return hunterTableModel.getSpreadsheetOutput();
	}
	
	public static Object[] getClearOutput() {
		return hunterTableModel.clearList();
	}
	
	public static int getMaxEntries() {
		return hunterTableModel.getMaxEntries();
	}
	
}
