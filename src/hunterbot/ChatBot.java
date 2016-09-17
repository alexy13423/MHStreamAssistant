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

import org.pircbotx.Configuration;
import org.pircbotx.PircBotX;
import org.pircbotx.cap.EnableCapHandler;
import org.pircbotx.exception.IrcException;

public class ChatBot {

	private String name;
	private String authKey;
	private String target;
	
	private Configuration configuration;
	private PircBotX bot;
	
	public ChatBot(String name, String key, String target) {
		this.name = name;
		this.authKey = key;
		this.target = target;
		
		configuration = new Configuration.Builder()
		.setAutoNickChange(false)
		.setOnJoinWhoEnabled(false)
		.setCapEnabled(true)
		.addCapHandler(new EnableCapHandler("twitch.tv/membership"))
		.addServer("irc.twitch.tv")
		.setName(this.name)
		.setServerPassword(authKey)
		.addAutoJoinChannel("#" + this.target)
		.addListener(new MyListener())
		.buildConfiguration();
		
		bot = new PircBotX(configuration);
	}
	
	public void startBot() throws IrcException, IOException{
		bot.startBot();
	}
	
	public void closeBot() {
		bot.close();
	}
}
