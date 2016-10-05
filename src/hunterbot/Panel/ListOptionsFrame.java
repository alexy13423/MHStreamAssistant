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

import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.Timer;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.*;

import hunterbot.SpreadsheetConfig;

import com.google.api.services.sheets.v4.Sheets;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.List;

public class ListOptionsFrame {

	private static final String APPLICATION_NAME = "Monster Hunter Stream Helper";
	
	private static final java.io.File DATA_STORE_DIR = new java.io.File(
	        System.getProperty("user.home"), ".credentials/mhstreamhelper");
	
	private static FileDataStoreFactory DATA_STORE_FACTORY;
	
	private static final JsonFactory JSON_FACTORY =
	        JacksonFactory.getDefaultInstance();
	
	private static HttpTransport HTTP_TRANSPORT;
	
	private static final List<String> SCOPES =
	        Arrays.asList(SheetsScopes.SPREADSHEETS);
	
	private static Credential credential;
	
	static {
        try {
            HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            DATA_STORE_FACTORY = new FileDataStoreFactory(DATA_STORE_DIR);
        } catch (Throwable t) {
            t.printStackTrace();
            System.exit(1);
        }
    }
	
	private JFrame frame;
	private static Sheets service;
	private static String spreadsheetId;
	
	private Timer listWriteTimer;
	private int listWriteDelay;
	private ActionListener listTimerFunction;
	
	private static boolean listActive;
	
	public ListOptionsFrame() {
		frame = new JFrame("Google Sheet Link");
		frame.setSize(400, 200);
		frame.setVisible(false);
		frame.setResizable(false);
		
		spreadsheetId = "";
		listActive = false;
		
		listWriteDelay = 5000;
		listTimerFunction = new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				writeTableToList();
			}
		};
		listWriteTimer = new Timer(listWriteDelay, listTimerFunction);
		
		JPanel listPanel = new JPanel();
		listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
		
		JPanel authenticationPanel = new JPanel();
		JButton getAuthentication = new JButton("Start Sheets Service");
		
		authenticationPanel.add(getAuthentication);
		listPanel.add(authenticationPanel);
		listPanel.add(new JSeparator(JSeparator.HORIZONTAL));
		
		JPanel sheetPanel = new JPanel();
		JLabel sheetLabel = new JLabel("Spreadsheet ID");
		sheetPanel.add(sheetLabel);
		JTextField sheetID = new JTextField();
		
		SpreadsheetConfig config = null;
		try (FileInputStream fileIn = new FileInputStream("spreadsheet.ser"); ObjectInputStream configIn = new ObjectInputStream(fileIn)) {
			config = (SpreadsheetConfig) configIn.readObject();
			String id = config.getStoredId();
			sheetID.setText(id);
		} catch (IOException | ClassNotFoundException e) {
		}
		
		sheetID.setPreferredSize(new Dimension(280, 30));
		sheetID.setEnabled(false);
		sheetID.setEditable(false);
		sheetPanel.add(sheetID);
		listPanel.add(sheetPanel);
		
		JPanel sheetControls = new JPanel();
		JButton setSheet = new JButton("Get Sheet");
		setSheet.setEnabled(false);
		sheetControls.add(setSheet);
		
		listPanel.add(sheetControls);
		
		getAuthentication.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					service = getSheetsService();
					setSheet.setEnabled(true);
					sheetID.setEnabled(true);
					sheetID.setEditable(true);
				} catch (IOException e) {
					JOptionPane.showMessageDialog(frame, "Unable to initialize Google API.", "Google API Error", JOptionPane.ERROR_MESSAGE);
				}
				
			}
		});
		
		setSheet.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				String id = sheetID.getText();
				try {
					service.spreadsheets().get(id).execute();
				} catch (Exception ex) {
					JOptionPane.showMessageDialog(frame, "Unable to load sheet!", "Sheet Load Error", JOptionPane.ERROR_MESSAGE);
					return;
				}
				JOptionPane.showMessageDialog(frame, "Load success!", "Sheet Loaded", JOptionPane.INFORMATION_MESSAGE);
				spreadsheetId = id;
				listWriteTimer.start();
				listActive = true;
				SpreadsheetConfig newConfig = new SpreadsheetConfig(spreadsheetId);
				try (FileOutputStream fileOut = new FileOutputStream("spreadsheet.ser"); ObjectOutputStream configOut = new ObjectOutputStream(fileOut)) {
					configOut.writeObject(newConfig);
				} catch (IOException e1) {
				}
			}
		});
		
		frame.add(listPanel);
	}
	
	public void setVisible(boolean b) {
		frame.setVisible(b);
	}
	
	public void setLocation(Point loc) {
		frame.setLocation(loc);
	}
	
	public void writeTableToList() {
		Object[] obj = HunterTableFrame.getSpreadsheetOutput();
		ValueRange values = (ValueRange) obj[0];
		@SuppressWarnings("unchecked")
		List<Request> reqs = (List<Request>) obj[1];
		try {
			service.spreadsheets().values().update(spreadsheetId, "A1", values).setValueInputOption("USER_ENTERED").execute();
			BatchUpdateSpreadsheetRequest req = new BatchUpdateSpreadsheetRequest();
			req.setRequests(reqs);
			service.spreadsheets().batchUpdate(spreadsheetId, req).execute();
		} catch (IOException io) {
			io.printStackTrace();
		}
		
		long expirationTime = credential.getExpiresInSeconds();
		if (expirationTime < 300) {
			try {
				credential.refreshToken();
			} catch (IOException io) {
				io.printStackTrace();
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	public static void clearList() {
		Object[] clearStuff = HunterTableFrame.getClearOutput();
		ValueRange clearValues = (ValueRange) clearStuff[0];
		List<Request> clearReqs = (List<Request>) clearStuff[1];
		
		try {
			service.spreadsheets().values().update(spreadsheetId, "A1", clearValues).setValueInputOption("USER_ENTERED").execute();
			BatchUpdateSpreadsheetRequest request = new BatchUpdateSpreadsheetRequest();
			request.setRequests(clearReqs);
			service.spreadsheets().batchUpdate(spreadsheetId, request).execute();
		} catch (IOException io) {
			io.printStackTrace();
		}
	}
	
	/**
     * Creates an authorized Credential object.
     * @return an authorized Credential object.
     * @throws IOException
     */
    public static Credential authorize() throws IOException {
        // Load client secrets.
    	InputStream in = ListOptionsFrame.class.getResourceAsStream("/client_secret.json");
        GoogleClientSecrets clientSecrets =
            GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow =
                new GoogleAuthorizationCodeFlow.Builder(
                        HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(DATA_STORE_FACTORY)
                .setAccessType("offline")
                .build();
        Credential credential = new AuthorizationCodeInstalledApp(
            flow, new LocalServerReceiver()).authorize("user");
        //Credential credential = flow.createAndStoreCredential(response, userId);
        System.out.println(
                "Credentials saved to " + DATA_STORE_DIR.getAbsolutePath());
        return credential;
    }

    /**
     * Build and return an authorized Sheets API client service.
     * @return an authorized Sheets API client service
     * @throws IOException
     */
    public static Sheets getSheetsService() throws IOException {
        credential = authorize();
        return new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }
    
    public static boolean getListActive() {
    	return listActive;
    }
    
    public static String getSpreadsheetId() {
    	return spreadsheetId;
    }
	
}
