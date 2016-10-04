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

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.table.AbstractTableModel;

import com.google.api.services.sheets.v4.model.CellData;
import com.google.api.services.sheets.v4.model.CellFormat;
import com.google.api.services.sheets.v4.model.Color;
import com.google.api.services.sheets.v4.model.GridRange;
import com.google.api.services.sheets.v4.model.RepeatCellRequest;
import com.google.api.services.sheets.v4.model.Request;
import com.google.api.services.sheets.v4.model.TextFormat;
import com.google.api.services.sheets.v4.model.ValueRange;

public class HunterTableModel extends AbstractTableModel {
	
	private static final long serialVersionUID = 1L;
	private static Vector<Hunter> hunterList;
	private String[] columnNames = {"Queue #", "Status", "Twitch Name", "Hunter Name", "HR", "HR Group"};

	private static int maxEntries;
	
	public HunterTableModel() {
		hunterList = new Vector<Hunter>();
		maxEntries = 0;
	}
	
	public void addHunter(Hunter newHunter) {
		int newPriority = newHunter.getPriority().getPriorityLevel();
		boolean added = false;
		if (newPriority > 0) {
			
			for (int i = 0; i < hunterList.size(); i++) {
				int thisPriority = hunterList.get(i).getPriority().getPriorityLevel();
				if (newPriority > thisPriority) {
					added = true;
					hunterList.add(i, newHunter);
					maxEntries++;
					break;
				}
			}
		}
		if (newPriority == 0 || !added) {
			hunterList.add(newHunter);
			maxEntries++;
		}
		this.fireTableDataChanged();
	}
	
	public void clearDoneEntries() {
		for (int i = hunterList.size() - 1; i >= 0; i--) {
			Hunter.State currentState = hunterList.get(i).getState();
			if (currentState.equals(Hunter.State.PLAYED)){
				hunterList.remove(i);
			}
		}
		
		this.fireTableDataChanged();
	}
	
	public Hunter getHunter(int row) {
		return hunterList.get(row);
	}
	
	public Vector<Hunter> getHunterList() {
		return hunterList;
	}
	
	public Vector<Hunter> getNextThreeHunters() {
		Vector<Hunter> result = new Vector<Hunter>();
		for (int i = 0; i < hunterList.size(); i++) {
			Hunter hunter = hunterList.get(i);
			Hunter.State hunterState = hunter.getState();
			if (hunterState == Hunter.State.PRIORITYWAITING || hunterState == Hunter.State.WAITING) {
				result.add(hunter);
				if (result.size() == 3)
					break;
			}
		}
		return result;
	}
	
	public int getQueuePosition(String twitchName) {
		int spaces = 0;
		for (int i = 0; i < hunterList.size(); i++) {
			Hunter hunter = hunterList.get(i);
			Hunter.State hunterState = hunter.getState();
			if (hunterState == Hunter.State.PRIORITYWAITING || hunterState == Hunter.State.WAITING) {
				String name = hunter.getTwitchName().toLowerCase();
				if (twitchName.equals(name)) {
					return spaces;
				}
				else spaces++;
			}
		}
		return -1;
	}
	
	public void setHunterList(Vector<Hunter> backupList) {
		hunterList = backupList;
		this.fireTableDataChanged();
	}
	
	public void updateHunter(int row, Hunter hunter) {
		hunterList.set(row, hunter);
		this.fireTableDataChanged();
	}
	
	public int searchRowForNameWithState(String twitchName, Hunter.State state) {
		for (int i = 0; i < hunterList.size(); i++) {
			Hunter hunter = hunterList.get(i);
			if (hunter.getState() == state){
				String thisTwitchName = hunterList.get(i).getTwitchName().toLowerCase();
				if (thisTwitchName.equals(twitchName))
					return i;
			}
		}
		return -1;
	}
	
	public int checkQueueAtPriorityLevel(int priorityLevel) {
		
		int result = 0;
		
		for (int i = 0; i < hunterList.size(); i++) {
			Hunter hunter = hunterList.get(i);
			PriorityNumber number = hunter.getPriority();
			int priority = number.getPriorityLevel();
			int queue = number.getQueueNumber();
			if (priorityLevel > priority)
				break;
			else if (priorityLevel == priority)
				result = queue;
		}
		
		return result;
	}

	public Class<?> getColumnClass(int arg0) {
		switch(arg0) {
			case 0:
				return PriorityNumber.class;
			case 1:
				return Hunter.State.class;
			case 2:
				return String.class;
			case 3:
				return String.class;
			case 4:
				return int.class;
			case 5:
				return String.class;
			default:
				return null;
		}
	}

	public int getColumnCount() {
		return 6;
	}

	public String getColumnName(int arg0) {
		return columnNames[arg0];
	}

	public int getRowCount() {
		return hunterList.size();
	}

	public Object getValueAt(int arg0, int arg1) {
		switch (arg1) {
			case 0:
				return hunterList.get(arg0).getPriority();
			case 1:
				return hunterList.get(arg0).getState();
			case 2:
				return hunterList.get(arg0).getTwitchName();
			case 3:
				return hunterList.get(arg0).getHunterName();
			case 4:
				return hunterList.get(arg0).getHunterRank();
			case 5:
				int hunterRank = hunterList.get(arg0).getHunterRank();
				if (hunterRank < 4)
					return "LR";
				else if (hunterRank < 8)
					return "HR";
				else return "HR Break";
			default:
				return null;
		}
	}

	public boolean isCellEditable(int arg0, int arg1) {
		return false;
	}
	
	public Object[] getSpreadsheetOutput() {
		ValueRange tableData = new ValueRange();
		
		List<List<Object>> values = new ArrayList<List<Object>>();
		List<Request> reqs = new ArrayList<Request>();
		
		for (int i = -1; i < maxEntries; i++) {
			List<Object> row = new ArrayList<Object>();
			Request rowFormatRequest = new Request();
			RepeatCellRequest rowFormatRepeatRequest = new RepeatCellRequest();
			GridRange rowRange = new GridRange();
			rowRange.setSheetId(0);
			rowRange.setStartRowIndex(i + 1);
			rowRange.setEndRowIndex(i + 2);
			rowRange.setStartColumnIndex(0);
			rowRange.setEndColumnIndex(6);
			rowFormatRepeatRequest.setRange(rowRange);
			CellData rowData = new CellData();
			CellFormat rowFormat = new CellFormat();
			Color rowBackgroundColor = new Color();
			TextFormat rowTextFormat = new TextFormat();
			Color rowForegroundColor = new Color();
			rowTextFormat.setFontSize(10);
			if (i == -1) {
				row.add("Queue #");
				row.add("Viewer Status");
				row.add("Twitch Name");
				row.add("Hunter Name");
				row.add("Hunter Rank");
				row.add("Rank Category");
				rowBackgroundColor.setRed((float) 1);
				rowBackgroundColor.setGreen((float) 1);
				rowBackgroundColor.setBlue((float) 1);
				rowTextFormat.setBold(true);
				rowForegroundColor.setRed((float) 0);
				rowForegroundColor.setGreen((float) 0);
				rowForegroundColor.setBlue((float) 0);
			}
			else if (i < hunterList.size()) {
				Hunter hunter = hunterList.get(i);
				Hunter.State state = hunter.getState();
				row.add(hunter.getPriority().toString());
				row.add(state.toString());
				row.add(hunter.getTwitchName());
				row.add(hunter.getHunterName());
				int rank = hunter.getHunterRank();
				row.add(rank);
				if (rank < 4) {
					row.add("LR");
				} else if (rank < 8) {
					row.add("HR");
				} else {
					row.add("HR Break");
				}
				rowTextFormat.setBold(false);
				switch(state) {
				case PRIORITYWAITING:
					rowBackgroundColor.setRed((float) 0);
					rowBackgroundColor.setGreen((float) 1);
					rowBackgroundColor.setBlue((float) 1);
					rowForegroundColor.setRed((float) 0);
					rowForegroundColor.setGreen((float) 0);
					rowForegroundColor.setBlue((float) 0);
					break;
				case WAITING:
					rowBackgroundColor.setRed((float) 1);
					rowBackgroundColor.setGreen((float) 1);
					rowBackgroundColor.setBlue((float) 1);
					rowForegroundColor.setRed((float) 0);
					rowForegroundColor.setGreen((float) 0);
					rowForegroundColor.setBlue((float) 0);
					break;
				case PLAYING:
					rowBackgroundColor.setRed((float) 1);
					rowBackgroundColor.setGreen((float) 1);
					rowBackgroundColor.setBlue((float) 0);
					rowForegroundColor.setRed((float) 0);
					rowForegroundColor.setGreen((float) 0);
					rowForegroundColor.setBlue((float) 0);
					break;
				case PLAYED:
					rowBackgroundColor.setRed((float) 0);
					rowBackgroundColor.setGreen((float) 1);
					rowBackgroundColor.setBlue((float) 0);
					rowForegroundColor.setRed((float) 0);
					rowForegroundColor.setGreen((float) 0);
					rowForegroundColor.setBlue((float) 0);
					break;
				case BAILED:
					rowBackgroundColor.setRed((float) 0);
					rowBackgroundColor.setGreen((float) 0);
					rowBackgroundColor.setBlue((float) 0);
					rowForegroundColor.setRed((float) 1);
					rowForegroundColor.setGreen((float) 0);
					rowForegroundColor.setBlue((float) 0);
					break;
				case SKIPPED:
					rowBackgroundColor.setRed((float) 1);
					rowBackgroundColor.setGreen((float) .647);
					rowBackgroundColor.setBlue((float) 0);
					rowForegroundColor.setRed((float) 0);
					rowForegroundColor.setGreen((float) 0);
					rowForegroundColor.setBlue((float) 0);
					break;
				}
				
			} else {
				for (int j = 0; j < 6; j++) {
					row.add("");
				}
				rowBackgroundColor.setRed((float) 1);
				rowBackgroundColor.setGreen((float) 1);
				rowBackgroundColor.setBlue((float) 1);
				rowForegroundColor.setRed((float) 0);
				rowForegroundColor.setGreen((float) 0);
				rowForegroundColor.setBlue((float) 0);
				rowTextFormat.setBold(false);
			}
			values.add(row);
			rowFormat.setBackgroundColor(rowBackgroundColor);
			rowFormat.setHorizontalAlignment("CENTER");
			rowTextFormat.setForegroundColor(rowForegroundColor);
			rowFormat.setTextFormat(rowTextFormat);
			rowData.setUserEnteredFormat(rowFormat);
			rowFormatRepeatRequest.setCell(rowData);
			rowFormatRepeatRequest.setFields("userEnteredFormat(backgroundColor,textFormat,horizontalAlignment)");
			rowFormatRequest.setRepeatCell(rowFormatRepeatRequest);
			reqs.add(rowFormatRequest);
		}
		tableData.setValues(values);
		Object[] returnValue = new Object[2];
		returnValue[0] = tableData;
		returnValue[1] = reqs;
		return returnValue;
	}
	
	public void setMaxEntries(int entries) {
		maxEntries = entries;
	}
	
	public int getMaxEntries() {
		return maxEntries;
	}

}
