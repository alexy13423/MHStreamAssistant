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
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;

import hunterbot.BotMessageQueue;
import hunterbot.HiredHunter;
import hunterbot.Hunter;

public class HiredHunterFrame {
	
	private JFrame frame;
	public static HiredHunter firstHunter, secondHunter, thirdHunter;
	public static int currentHunters;
	
	private CreditsFrame cFrame;
	private HunterTableFrame htFrame;
	private OptionsFrame oFrame;
	private PriorityListFrame plFrame;
	
	public HiredHunterFrame(Point p) {
		frame = new JFrame("Hired Hunters");
		frame.setSize(450, 850);
		frame.setLocation(p);
		frame.setVisible(false);
		frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		
		frame.addWindowListener(new WindowListener() {
			
			@Override
			public void windowOpened(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void windowIconified(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void windowDeiconified(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void windowDeactivated(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void windowClosing(WindowEvent e) {
				// TODO Auto-generated method stub
				Object[] options = {"Yes", "No"};
				int n = JOptionPane.showOptionDialog(frame, "Are you sure you want to close Monster Hunter Stream Assistant?",
						"Close Confirmation",
						JOptionPane.YES_NO_OPTION,
						JOptionPane.QUESTION_MESSAGE,
						null,
						options,
						options[1]);
				if (n == 1) {
					return;
				}
				
				int save = JOptionPane.showOptionDialog(frame, "Save the priority list?", "Save List Confirmation", 
						JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
				if (save == 0) {
					boolean saveSuccess = PriorityListFrame.savePrioritylist();
					if (!saveSuccess)
						return;
				}
				System.exit(0);
			}
			
			@Override
			public void windowClosed(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void windowActivated(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		
		firstHunter = new HiredHunter();
		secondHunter = new HiredHunter();
		thirdHunter = new HiredHunter();
		currentHunters = 0;
		
		JPanel menuPanel = new JPanel();
		
		cFrame = new CreditsFrame();
		htFrame = new HunterTableFrame();
		oFrame = new OptionsFrame();
		plFrame = new PriorityListFrame();
		
		JButton htButton = new JButton("Hunter Table");
		htButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				htFrame.setVisible(true);
			}
		});
		
		JButton plButton = new JButton("Priority List");
		plButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				plFrame.setVisible(true);
			}
		});
		
		JButton oButton = new JButton("Options");
		oButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				oFrame.setVisible(true);
			}
		});
		
		JButton cButton = new JButton("About");
		cButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				cFrame.setVisible(true);
			}
		});
		
		menuPanel.add(htButton);
		menuPanel.add(plButton);
		menuPanel.add(oButton);
		menuPanel.add(cButton);
		frame.add(menuPanel);
		
		frame.add(new JSeparator(JSeparator.HORIZONTAL));
		
		frame.add(firstHunter.getPanel(), BorderLayout.PAGE_START);
		frame.add(secondHunter.getPanel(), BorderLayout.CENTER);
		frame.add(thirdHunter.getPanel(), BorderLayout.PAGE_END);
	}
	
	public void setVisible(boolean b) {
		frame.setVisible(b);
	}
	
	public void setLocation(Point loc) {
		frame.setLocation(loc);
	}
	
	public static void hireHunter(Hunter h) {
		String twitchName = h.getTwitchName();
		BotMessageQueue.addRegularMessage(BotMessageQueue.MessageType.TURNREADY, twitchName);
		if (firstHunter.getHasHunter() == false) {
			firstHunter.setHunter(h);
		}
		else if (secondHunter.getHasHunter() == false) {
			secondHunter.setHunter(h);
		}
		else {
			thirdHunter.setHunter(h);
		}
	}
	
	public static int getHunterCount() {
		return currentHunters;
	}
	
	public static void doBackup() {
		
	}
	
}
