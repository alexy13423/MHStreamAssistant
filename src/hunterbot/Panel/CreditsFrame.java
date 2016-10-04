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

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;

public class CreditsFrame {

	private JFrame frame;
	
	public CreditsFrame() {
		frame = new JFrame("About");
		frame.setSize(400, 400);
		frame.setVisible(false);
		frame.setResizable(false);
		
		JPanel creditsPanel = new JPanel();
		creditsPanel.setLayout(new BoxLayout(creditsPanel, BoxLayout.Y_AXIS));
		JTextArea authorText = new JTextArea();
		authorText.setText("Monster Hunter Stream Assistant (MHStreamAssistant) ver 3 \n"
				+ "Copyright © 2016 Alexander Le (alexyle@gmail.com) \n"
				+ "\n"
				+ "Special thanks to the Twitch streamers: \n"
				+ "GaryFaceman (twitch.tv/garyfaceman) \n"
				+ "ViperAS (twitch.tv/viperas) \n"
				+ "for helping test the bot during development! \n"
				+ "\n"
				+ "This bot utilizes PircBotX (github.com/TheLQ/pircbotx) \n"
				+ "for its internal workings. Therefore, this \n"
				+ "program is licensed under the GNU GPL v3 to be \n"
				+ "compatible with the license for PircBotX. \n"
				+ "\n"
				+ "The source code for this program can be found at \n"
				+ "github.com/alexy13423/MHStreamAssistant.");
		authorText.setBackground(frame.getBackground());
		authorText.setEditable(false);
		
		creditsPanel.add(authorText);
		frame.add(creditsPanel);
	}
	
	public void setVisible(boolean b) {
		frame.setVisible(b);
	}
	
	public void setLocation(Point loc) {
		frame.setLocation(loc);
	}
	
}
