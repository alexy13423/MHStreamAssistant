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
import java.awt.Component;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Scanner;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import hunterbot.Panel.HiredHunterFrame;

public class MHStreamAssistant {
	
	private static JFrame frame;
	
	private static String accountName;
	private static String authKey;
	private static String targetChannelName;
	
	private static Thread botThread;
	private static Thread messageThread;
	
	public static boolean priorityListPresent;
	
	public static void main(String[] args) {
		
		priorityListPresent = false;
		
		frame = new JFrame("Stream Assistant!");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(300,  300);
		
		LoginConfig config = null;
		try {
			FileInputStream fileIn = new FileInputStream("login.ser");
			ObjectInputStream configIn = new ObjectInputStream(fileIn);
			config = (LoginConfig) configIn.readObject();
			configIn.close();
		} catch (IOException e) {
			
		} catch (ClassNotFoundException e) {
			
		}
		
		JPanel loginPanel = new JPanel();
		loginPanel.setLayout(null);
		
		JLabel titleLabel = new JLabel("Monster Hunter Stream Assistant!");
		titleLabel.setHorizontalAlignment(JLabel.CENTER);
		titleLabel.setBounds(50, 5, 200, 30);
		
		JLabel nameLabel = new JLabel("Twitch Username");
		nameLabel.setBounds(90, 18, 120, 30);
		nameLabel.setHorizontalAlignment(JLabel.CENTER);
		nameLabel.setVerticalAlignment(JLabel.BOTTOM);
		final JTextField nameEntry = new JTextField();
		if (config != null) {
			nameEntry.setText(config.getAccount());
		}
		nameEntry.setBounds(60, 50, 180, 30);
		nameEntry.setHorizontalAlignment(JTextField.CENTER);
		
		JLabel passwordLabel = new JLabel("Twitch Authkey");
		passwordLabel.setBounds(90, 68, 120, 30);
		passwordLabel.setHorizontalAlignment(JLabel.CENTER);
		passwordLabel.setVerticalAlignment(JLabel.BOTTOM);
		final JPasswordField passwordEntry = new JPasswordField();
		if (config != null) {
			passwordEntry.setText(config.getAuthKey());
		}
		passwordEntry.setBounds(60, 100, 180, 30);
		passwordEntry.setHorizontalAlignment(JPasswordField.CENTER);
		
		JLabel targetLabel = new JLabel("Target Twitch Channel");
		targetLabel.setBounds(70, 118, 160, 30);
		targetLabel.setHorizontalAlignment(JLabel.CENTER);
		targetLabel.setVerticalAlignment(JLabel.BOTTOM);
		final JTextField channelName = new JTextField();
		if (config != null) {
			channelName.setText(config.getTargetChannel());
		}
		channelName.setBounds(60, 150, 180, 30);
		channelName.setHorizontalAlignment(JTextField.CENTER);
		
		JButton startButton = new JButton("Start!");
		startButton.setBounds(110, 200, 80, 30);
		
		titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		loginPanel.add(titleLabel);
		
		loginPanel.add(nameLabel);
		loginPanel.add(nameEntry);
		loginPanel.add(passwordLabel);
		loginPanel.add(passwordEntry);
		loginPanel.add(targetLabel);
		loginPanel.add(channelName);
		loginPanel.add(startButton);
		
		frame.add(loginPanel, BorderLayout.CENTER);
		loginPanel.setVisible(true);
		
		frame.setVisible(true);
		startButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				accountName = nameEntry.getText().toLowerCase();
				char[] authKeyEntry = passwordEntry.getPassword();
				authKey = String.copyValueOf(authKeyEntry);
				targetChannelName = channelName.getText().toLowerCase();
				
				LoginConfig newConfig = new LoginConfig(accountName, authKey, targetChannelName);
				try {
					FileOutputStream fileOut = new FileOutputStream("login.ser");
					ObjectOutputStream configOut = new ObjectOutputStream(fileOut);
					configOut.writeObject(newConfig);
					configOut.close();
				} catch (IOException i) {
					i.printStackTrace();
				}
				
				botThread = new Thread(new ChatBotThread(accountName, authKey, targetChannelName));
				messageThread = new Thread(new BotMessageQueue());
				botThread.start();
				messageThread.start();
				Point p = frame.getLocation();
				frame.setVisible(false);
				HiredHunterFrame hhFrame = new HiredHunterFrame(p);
				hhFrame.setVisible(true);
			}
		});
	}
	
	public static boolean isInteger(String s, int radix) {
	    Scanner sc = new Scanner(s.trim());
	    if(!sc.hasNextInt(radix)) {
	    	sc.close();
	    	return false;
	    }
	    sc.nextInt(radix);
	    boolean result = !sc.hasNext();
	    sc.close();
	    return result;
	}
}