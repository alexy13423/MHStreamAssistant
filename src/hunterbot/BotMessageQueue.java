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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.Vector;

import javax.swing.Timer;

import org.pircbotx.output.OutputChannel;

import hunterbot.Panel.HunterTableFrame;

public class BotMessageQueue implements Runnable{
	
	public static enum MessageType {
		QUEUEDIRECTIONS("To sign up, use !hire <hunter name> <HR>. Please only put in a number for HR.", false),
		QUEUEADD("You've been added to queue!", true),
		QUEUEALREADYPRESENT("You're already in the queue!", true),
		QUEUEINVALID("Invalid syntax; please use !hire <hunter name> <HR>!", true),
		QUEUEJOKERANK("You can't have a hunter rank less than 1, come on.", true),
		QUEUENOTOPEN("Signups are currently not open!", false),
		SKIPFAIL("You cannot currently skip your turn!", true),
		SKIPSUCCESS("Thanks for checking in! You've been skipped.", true),
		UNSKIPSUCCESS("Welcome back! You've been unskipped.", true),
		UNSKIPFAIL("You aren't currently skipped!", true),
		LISTEMP("There isn't a list currently, sadly, but hopefully pretty soon, there should be one again!", false),
		TURNREADY("It's your turn to hunt!", true),
		TURNDONE("Thanks for hunting!", true),
		TURNSKIPPED("You've been skipped by the streamer.", true),
		TURNUNDONE("You've been moved back into the queue.", true),
		TURNBAILED("You've apparently bailed on the stream!", true),
		
		TABLEMARKSKIP("You've been marked as skipped by the streamer!", true),
		TABLEUNMARKSKIP("You've been unmarked as skipped by the streamer.", true),
		
		TABLEMARKBAIL("You've apparently bailed on the stream!", true),
		TABLEUNMARKBAIL("You've been unmarked as bailed by the streamer.", true),
		
		UPNEXT("", false);
		
		private String message;
		private boolean hasUser;
		
		private MessageType(String message, boolean hasUser) {
			this.message = message;
			this.hasUser = hasUser;
		}
		
		public String getMessage() {
			return message;
		}
		
		public boolean getHasUser() {
			return hasUser;
		}
		
	}
	
	public static OutputChannel messageOutput;
	
	private static LinkedList<String> priorityMessageQueue;
	private static Vector<Vector<String>> userMessageArray;
	private static int[] regularMessageOrder;
	public static Timer priorityMessageTimer, regularMessageTimer;
	
	private ActionListener priorityMessageOutputFunction, regularMessageOutputFunction;
	private int priorityDelay, regularDelay;
	
	private static int ubwCount;
	
	public BotMessageQueue() {
		messageOutput = MyListener.getOutput();
		
		priorityMessageQueue = new LinkedList<String>();
		userMessageArray = new Vector<Vector<String>>();
		for (int i = 0; i < 20; i++) {
			userMessageArray.add(new Vector<String>());
		}
		regularMessageOrder = new int[21];
		for (int i = 0; i < 21; i++) {
			regularMessageOrder[i] = -1;
		}
		priorityMessageOutputFunction = new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				String message = priorityMessageQueue.poll();
				if (message != null)
					messageOutput.message(message);
			}
		};
		
		regularMessageOutputFunction = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int messageIndex = -1;
				int lowestMessagePriority = 3000;
				for (int i = 0; i < regularMessageOrder.length; i++) {
					if (regularMessageOrder[i] != -1) {
						if (regularMessageOrder[i] < lowestMessagePriority) {
							messageIndex = i;
							lowestMessagePriority = regularMessageOrder[i];
						}
					}
				}
				switch(messageIndex) {
				case 0:
					outputRegularMessage(MessageType.QUEUEDIRECTIONS);
					break;
				case 1:
					outputRegularMessage(MessageType.QUEUEADD);
					break;
				case 2:
					outputRegularMessage(MessageType.QUEUEALREADYPRESENT);
					break;
				case 3:
					outputRegularMessage(MessageType.QUEUEINVALID);
					break;
				case 4:
					outputRegularMessage(MessageType.QUEUEJOKERANK);
					break;
				case 5:
					outputRegularMessage(MessageType.QUEUENOTOPEN);
					break;
				case 6:
					outputRegularMessage(MessageType.SKIPFAIL);
					break;
				case 7:
					outputRegularMessage(MessageType.SKIPSUCCESS);
					break;
				case 8:
					outputRegularMessage(MessageType.UNSKIPSUCCESS);
					break;
				case 9:
					outputRegularMessage(MessageType.UNSKIPFAIL);
					break;
				case 10:
					outputRegularMessage(MessageType.LISTEMP);
					break;
				case 11:
					outputRegularMessage(MessageType.TURNREADY);
					break;
				case 12:
					outputRegularMessage(MessageType.TURNDONE);
					break;
				case 13:
					outputRegularMessage(MessageType.TURNSKIPPED);
					break;
				case 14:
					outputRegularMessage(MessageType.TURNUNDONE);
					break;
				case 15:
					outputRegularMessage(MessageType.TURNBAILED);
					break;
				case 16:
					outputRegularMessage(MessageType.TABLEMARKSKIP);
					break;
				case 17:
					outputRegularMessage(MessageType.TABLEUNMARKSKIP);
					break;
				case 18:
					outputRegularMessage(MessageType.TABLEMARKBAIL);
					break;
				case 19:
					outputRegularMessage(MessageType.TABLEUNMARKBAIL);
					break;
				case 20:
					doNextUpCommand();
					break;
				default:
					return;
				}
				regularMessageOrder[messageIndex] = -1;
			}
		};
		
		priorityDelay = 2000;
		regularDelay = 3000;
		priorityMessageTimer = new Timer(priorityDelay, priorityMessageOutputFunction);
		priorityMessageTimer.setRepeats(true);
		regularMessageTimer = new Timer(regularDelay, regularMessageOutputFunction);
		regularMessageTimer.setRepeats(true);
		
		ubwCount = 0;
		
		priorityMessageTimer.start();
		regularMessageTimer.start();
	}
	
	@Override
	public void run() {
	}
	
	public static void addPriorityMessage(String msg) {
		priorityMessageQueue.add(msg);
	}
	
	public static void addRegularMessage(MessageType msg, String name) {
		int currentMessagePriority = 0;
		int messageOrdinal = msg.ordinal();
		System.out.println("Message ordinal: " + messageOrdinal);
		for (int i = 0; i < regularMessageOrder.length; i++) {
			if (i == messageOrdinal)
				continue;
			int thisMessagePriority = regularMessageOrder[i];
			if (currentMessagePriority <= thisMessagePriority) {
				currentMessagePriority = thisMessagePriority + 1;
			}
		}
		System.out.println("Old message priority: " + regularMessageOrder[messageOrdinal]);
		System.out.println("New message priority: " + currentMessagePriority);
		if (regularMessageOrder[messageOrdinal] < currentMessagePriority) {
			regularMessageOrder[messageOrdinal] = currentMessagePriority;
		}
		
		if (messageOrdinal != 0 && messageOrdinal != 5 && messageOrdinal != 10 && messageOrdinal != 20) {
			Vector<String> names = userMessageArray.get(messageOrdinal);
			for (int i = 0; i < names.size(); i++) {
				if (names.get(i).equals(name))
					return;
			}
			userMessageArray.get(messageOrdinal).add(name);
		}
	}
	
	private static void outputRegularMessage(MessageType msg) {		
		if (msg.getHasUser()) {
			String userNameString = "";
			int messageOrdinal = msg.ordinal();
			synchronized(userMessageArray) {
				Vector<String> names = userMessageArray.get(messageOrdinal);
				System.out.println("Size of names: " + names.size());
				for (int i = names.size() - 1; i >= 0; i--) {
					userNameString += names.remove(i);
					if (i != 0) {
						userNameString += ", ";
					}
				}
				userNameString += ": ";
				messageOutput.message(userNameString + msg.getMessage());
				System.out.println("New size of names: " + names.size());
				userMessageArray.set(messageOrdinal, names);
			}
		}
		else {
			messageOutput.message(msg.getMessage());
		}
	}
	
	private void doNextUpCommand() {
		HunterTableModel model = HunterTableFrame.getTableModel();
		Vector<Hunter> hunters = model.getNextThreeHunters();
		int numHunters = hunters.size();
		switch (numHunters) {
		case 0:
			messageOutput.message("There are no hunters for the next rotation currently. Why not sign up now?");
			break;
		case 1:
			messageOutput.message("The only hunter currently up to hunt next is " + hunters.get(0).getTwitchName() + ".");
			break;
		case 2:
			messageOutput.message("The next two hunters up to hunt are " + hunters.get(0).getTwitchName() + " and " + hunters.get(1).getTwitchName() + ".");
			break;
		case 3:
			messageOutput.message("The next three hunters up to hunt are " + hunters.get(0).getTwitchName() + ", " + hunters.get(1).getTwitchName() + ", and " + hunters.get(2).getTwitchName() + ".");
			break;
		}
	}
	
	public static void doUBW() {
		switch(ubwCount) {
		case 0:
			messageOutput.message("I am the shaft of my arrow.");
			break;
		case 1:
			messageOutput.message("Bow is my weapon and aerial is my style.");
			break;
		case 2:
			messageOutput.message("I have fired over a thousand shots.");
			break;
		case 3:
			messageOutput.message("Unknown to village, nor known to guild.");
			break;
		case 4:
			messageOutput.message("Have withstood pain to slay many dragons.");
			break;
		case 5:
			messageOutput.message("Yet, those monsters will never drop anything.");
			break;
		case 6:
			messageOutput.message("So as I pray, unlimited bow works.");
			break;
		}
		ubwCount++;
		ubwCount = ubwCount % 7;
	}

}
