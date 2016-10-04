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

import java.io.IOException;

import org.pircbotx.exception.IrcException;

public class ChatBotThread extends Thread {

	private ChatBot bot;
	private boolean running;
	
	private Thread t;
	
	public ChatBotThread(String a, String b, String c) {
		bot = new ChatBot(a, b, c);
		running = false;
	}
	
	@Override
	public void run() {
		while (running) {
			try {
				bot.startBot();
			} catch (IrcException | IOException e) {
				e.printStackTrace();
			}
		}
		//System.out.println("doop");
		return;
	}
	
	public void start() {
		if (t == null) {
			t = new Thread(this, "botThread");
			t.start();
			running = true;
		}
	}
	
	public void stopBot() {
		bot.closeBot();
		running = false;
		//System.out.println("deep");
	}

}
