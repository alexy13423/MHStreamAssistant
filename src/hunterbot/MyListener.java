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

import java.util.StringTokenizer;
import java.util.Vector;

import org.pircbotx.Channel;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.JoinEvent;
import org.pircbotx.hooks.events.NoticeEvent;
import org.pircbotx.hooks.types.GenericMessageEvent;
import org.pircbotx.output.OutputChannel;

import hunterbot.Panel.HunterTableFrame;
import hunterbot.Panel.OptionsFrame;
public class MyListener extends ListenerAdapter {
	
	private static OutputChannel output;
	
	public MyListener() {
	}
	
	public void onGenericMessage(GenericMessageEvent event) {
		String name = event.getUser().getNick();
    	String message = event.getMessage();
    	System.out.println(name + ": " + message);
    	StringTokenizer messageTokens = new StringTokenizer(message);
    	String command = messageTokens.nextToken();
    	if (command.equals("!hire")) {
	    	if (OptionsFrame.getSignupsActive()) {
	    		StringTokenizer token = new StringTokenizer(message);
	    		if (token.countTokens() == 1) {
	    			System.out.println("poop");
	    			BotMessageQueue.addRegularMessage(BotMessageQueue.MessageType.QUEUEDIRECTIONS, "");
	    			return;
	    		}
	    		
	    		token.nextToken();
	    		String hunterName = "";
	    		int hunterRank = -1;
	    		Vector<String> inputTokens = new Vector<String>();
	    		while (token.hasMoreTokens()) {
	    			inputTokens.add(token.nextToken());
	    		}
	    		if (inputTokens.size() < 2) {
	    			BotMessageQueue.addRegularMessage(BotMessageQueue.MessageType.QUEUEINVALID, name);
	    			return;
	    		}
	    		boolean rankPresent = MHStreamAssistant.isInteger(inputTokens.get(inputTokens.size() - 1), 10);
	    		if (!rankPresent) {
	    			BotMessageQueue.addRegularMessage(BotMessageQueue.MessageType.QUEUEINVALID, name);
	    			return;
	    		}
	    		else {
	    			for (int i = 0; i < inputTokens.size() - 1; i++) {
	    				hunterName += inputTokens.get(i);
	    				if (i != inputTokens.size() - 2)
	    					hunterName += " ";
	    			}
	    			hunterRank = Integer.parseInt(inputTokens.get(inputTokens.size() - 1));
	    			
	    		}
	    		if (hunterRank <= 0) {
	    			BotMessageQueue.addRegularMessage(BotMessageQueue.MessageType.QUEUEJOKERANK, name);
	    			return;
	    		}
	    		
	    		HunterTableFrame.addHunter(name, hunterName, hunterRank);
	    	}
	    	else {
	    		BotMessageQueue.addRegularMessage(BotMessageQueue.MessageType.QUEUENOTOPEN, "");
	    	}
    	}
    	else if (command.equals("!skip")) {
    		HunterTableModel model = HunterTableFrame.getTableModel();
    		int row = model.searchRowForNameWithState(name, Hunter.State.WAITING);
    		if (row == -1) {
    			row = model.searchRowForNameWithState(name, Hunter.State.PRIORITYWAITING);
    			if (row == -1) {
    				BotMessageQueue.addRegularMessage(BotMessageQueue.MessageType.SKIPFAIL, name);
    				return;
    			}
    		}
    		Hunter hunter = model.getHunter(row);
    		hunter.setState(Hunter.State.SKIPPED);
    		model.updateHunter(row, hunter);
    		BotMessageQueue.addRegularMessage(BotMessageQueue.MessageType.SKIPSUCCESS, name);
    	}
    	else if (command.equals("!unskip")) {
    		HunterTableModel model = HunterTableFrame.getTableModel();
    		int row = model.searchRowForNameWithState(name, Hunter.State.SKIPPED);
    		if (row != -1) {
    			Hunter hunter = model.getHunter(row);
    			int priorityLevel = hunter.getPriority().getPriorityLevel();
    			if (priorityLevel > 0) {
    				hunter.setState(Hunter.State.PRIORITYWAITING);
    			}
    			else {
    				hunter.setState(Hunter.State.WAITING);
    			}
    			model.updateHunter(row, hunter);
    			BotMessageQueue.addRegularMessage(BotMessageQueue.MessageType.UNSKIPSUCCESS, name);
    		}
    		else {
    			BotMessageQueue.addRegularMessage(BotMessageQueue.MessageType.UNSKIPFAIL, name);
    		}
    	}
    	else if (command.equals("!upnext")) {
    		BotMessageQueue.addRegularMessage(BotMessageQueue.MessageType.UPNEXT, "");
    	}
    	else if (command.equals("!list")) {
    		BotMessageQueue.addRegularMessage(BotMessageQueue.MessageType.LISTEMP, "");
    	}
    	else if (command.equals("!ubw")) {
    		if (name.equals("cmdrrose")) {
    			BotMessageQueue.doUBW();
    		}
    	}
	}
	
	public void onJoin(JoinEvent event) {
		System.out.println("Join success!");
		Channel thing = event.getChannel();
		output = thing.send();
		MHStreamAssistant.connectionSuccess();
		//BotMessageQueue.messageOutput = thing.send();
		//BotMessageQueue.priorityMessageTimer.start();
		//BotMessageQueue.regularMessageTimer.start();
	}
	/*
	public void onConnectAttemptFailed(ConnectAttemptFailedEvent event) {
		System.out.println("Connection failed!");
		
		
	}*/
	
	public void onNotice(NoticeEvent event) {
		String notice = event.getNotice();
		if (notice.equals("Login authentication failed") || notice.equals("Improperly formatted auth")) {
			System.out.println("Bloop!");
			MHStreamAssistant.connectionFail();
		}
	}
	
	public static OutputChannel getOutput() {
		return output;
	}
	
}
