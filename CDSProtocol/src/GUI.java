import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Enumeration;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;

import cdspcore.*;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Dimension;


public class GUI extends JFrame implements UserInterface, ActionListener {
	private Crypto crypto = new Crypto();
	private DataManager dataManager;
	private String PATH_PUBLICKEYFILE = "PublicKeys";
	
	private JButton buttonPrivateKeyChoose, buttonPublicKeyAdd, buttonPublicKeyDel, buttonPublicKeyChoose, buttonDataChoose;
	private JButton buttonStart, buttonStop;
	private JRadioButton dcRButton, rcRButton, drRButton, scRButton;
	private JTextField textPrivateKey, textLocID, textLocAddress, textDstID, textDstAddress;
	private JTextField textLisPort, textDstPort, textMaxCapacity;
	private JTextField textPublicKeyID, textPublicKeyPath, textDataID, textDataPath;
	private JLabel labelLocID, labelLisPort, labelLocAddress, labelDstID, labelDstPort, labelDstAddress;
	private JLabel labelMaxCapacity, labelPrivateKey, labelDataID;
	private JFileChooser fileChooser;
	private JPanel panelIdentity, panelFiles, panelEntity, panelInfo;
	private JPanel panelFilesMaxCapacity, panelPrivateKey, panelData;
	private DefaultTableModel tableModel;
	private JTable tablePublicKey;
	private JTextArea infos;
	private ButtonGroup entityGroup;
	
	UserInterface ui = this;
	
	public GUI() // the frame constructor method
	{
	    super("Courier Dependent Secure Protocol"); setBounds(400,200,820,700);
	    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    JPanel mainPanel = new JPanel();
	    
	    fileChooser = new JFileChooser();
	    
	    // Panel for choosing entity
	    dcRButton = new JRadioButton("DataCreator");
	    dcRButton.addActionListener(this);
	    rcRButton = new JRadioButton("CourierReceiver");
	    rcRButton.addActionListener(this);

	    drRButton = new JRadioButton("DataReceiver");
	    drRButton.addActionListener(this);
	    scRButton = new JRadioButton("CourierSender");
	    scRButton.addActionListener(this);
	    
	    entityGroup = new ButtonGroup();
	    entityGroup.add(dcRButton);
	    entityGroup.add(rcRButton);
	    entityGroup.add(drRButton);
	    entityGroup.add(scRButton);
	    
	    panelEntity = new JPanel();
	    panelEntity.setBorder(new TitledBorder(new EtchedBorder(), "Protocol Entity"));
	    panelEntity.add(dcRButton);
	    panelEntity.add(rcRButton);
	    panelEntity.add(drRButton);
	    panelEntity.add(scRButton);
	    
	    // Panel for identify sender and receiver
	    // Local ID & Address & Port
	    labelLocID = new JLabel("Local Entity ID:");
	    textLocID = new JTextField(20);
	    
	    labelLisPort = new JLabel("Listening Port:");
	    textLisPort = new JFormattedTextField();
	    textLisPort.setColumns(10);
	    
	    labelLocAddress = new JLabel("Local Address:");
	    textLocAddress = new JTextField(20);
	    
	    // Destination ID & Address & Port
	    labelDstID = new JLabel("Destination Entity ID:");
	    textDstID = new JTextField(20);
	    
	    labelDstPort = new JLabel("Destination Port:");
	    textDstPort = new JFormattedTextField();
	    textLisPort.setColumns(10);
	    
	    labelDstAddress = new JLabel("Desination Address:");
	    textDstAddress = new JTextField(20);
	    
	    panelIdentity = new JPanel(new GridLayout(12,1));
	    panelIdentity.setBorder(new TitledBorder(new EtchedBorder(), "Entity Identity"));
	    panelIdentity.add(labelLocID);
	    panelIdentity.add(textLocID);
	    panelIdentity.add(labelLocAddress);
	    panelIdentity.add(textLocAddress);
	    panelIdentity.add(labelLisPort);
	    panelIdentity.add(textLisPort);
	    panelIdentity.add(labelDstID);
	    panelIdentity.add(textDstID);
	    panelIdentity.add(labelDstAddress);
	    panelIdentity.add(textDstAddress);
	    panelIdentity.add(labelDstPort);
	    panelIdentity.add(textDstPort);
	    
	    // Panel for choosing files
	    // Maximum data capacity
	    labelMaxCapacity = new JLabel("Maximum capacity: ");
	    textMaxCapacity = new JFormattedTextField();
	    textMaxCapacity.setColumns(10);
	    panelFilesMaxCapacity = new JPanel();
	    panelFilesMaxCapacity.add(labelMaxCapacity);
	    panelFilesMaxCapacity.add(textMaxCapacity);
	    
	    // Choose Private Key file
	    labelPrivateKey = new JLabel("Private Key File: ");
	    textPrivateKey = new JTextField(20);
	    buttonPrivateKeyChoose = new JButton("Choose");
	    buttonPrivateKeyChoose.addActionListener(this);
	    
	    panelPrivateKey = new JPanel();
	    panelPrivateKey.add(labelPrivateKey);
	    panelPrivateKey.add(textPrivateKey);
	    panelPrivateKey.add(buttonPrivateKeyChoose);
	    
	    // Choose Public Key file
	    tableModel = new DefaultTableModel();
	    tableModel.addColumn("Owner ID");
	    tableModel.addColumn("Public Key Path");
	    tablePublicKey = new JTable(tableModel);
	    JScrollPane tableScroller = new JScrollPane(tablePublicKey);
	    tableScroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
	    tableScroller.setPreferredSize(new Dimension(250, 200));
	    
	    JLabel labelPublicKeyID = new JLabel("ID");
	    textPublicKeyID = new JTextField(10);
	    JLabel labelPublicKeyPath = new JLabel("Path");
	    textPublicKeyPath = new JTextField(20);
	    buttonPublicKeyChoose = new JButton("Choose");
	    buttonPublicKeyChoose.addActionListener(this);
	    JPanel panelPublicKeyText = new JPanel();
	    panelPublicKeyText.add(labelPublicKeyID);
	    panelPublicKeyText.add(textPublicKeyID);
	    panelPublicKeyText.add(labelPublicKeyPath);
	    panelPublicKeyText.add(textPublicKeyPath);
	    panelPublicKeyText.add(buttonPublicKeyChoose);
	    
	    buttonPublicKeyAdd = new JButton("Add");
	    buttonPublicKeyAdd.addActionListener(this);
	    buttonPublicKeyDel = new JButton("Delete");
	    buttonPublicKeyDel.addActionListener(this);
	    
	    JPanel panelPublicKeyButton = new JPanel();
	    panelPublicKeyButton.add(buttonPublicKeyAdd);
	    panelPublicKeyButton.add(buttonPublicKeyDel);
	    
	    JPanel panelFilesPublicKeys = new JPanel();
	    panelFilesPublicKeys.setBorder(new TitledBorder(new EtchedBorder(), "Public Keys"));
	    panelFilesPublicKeys.setLayout(new BoxLayout(panelFilesPublicKeys, BoxLayout.PAGE_AXIS));
	    panelFilesPublicKeys.add(tableScroller);
	    panelFilesPublicKeys.add(panelPublicKeyText);
	    panelFilesPublicKeys.add(panelPublicKeyButton);
	    
	    // Data section
	    labelDataID = new JLabel("Recipient");
	    textDataID = new JTextField(30);
	    JLabel labelData3 = new JLabel("Path");
	    textDataPath = new JTextField(20);
	    buttonDataChoose = new JButton("Choose");
	    buttonDataChoose.addActionListener(this);
	    
	    panelData = new JPanel();
	    panelData.setBorder(new TitledBorder(new EtchedBorder(), "Data to Send"));
	    panelData.setLayout(new BoxLayout(panelData, BoxLayout.PAGE_AXIS));
	    JPanel subPanelData0 = new JPanel();
	    subPanelData0.add(labelDataID);
	    subPanelData0.add(textDataID);
	    panelData.add(subPanelData0);
	    JPanel subPanelData1 = new JPanel();
	    subPanelData1.add(labelData3);
	    subPanelData1.add(textDataPath);
	    subPanelData1.add(buttonDataChoose);
	    panelData.add(subPanelData1);
	    
	    
	    panelFiles = new JPanel();
	    //panelFiles = new JPanel(new GridLayout(4,1));
	    panelFiles.setLayout(new BoxLayout(panelFiles, BoxLayout.PAGE_AXIS));
	    panelFiles.setBorder(new TitledBorder(new EtchedBorder(), "File Paths"));
	    panelFiles.add(panelFilesMaxCapacity);
	    panelFiles.add(panelFilesPublicKeys);
	    panelFiles.add(panelPrivateKey);
	    panelFiles.add(panelData);
	    
	    // Panel for displaying infos
	    JPanel panelInfoButtons = new JPanel();
	    buttonStart = new JButton("Start");
	    buttonStart.addActionListener(this);
	    buttonStop = new JButton("Stop");
	    buttonStop.addActionListener(this);
	    panelInfoButtons.add(buttonStart);
	    panelInfoButtons.add(buttonStop);
	    
	    infos = new JTextArea();
	    //infos.setRows(5);
	    infos.setEditable(false);
	    JScrollPane infosScroller = new JScrollPane(infos);
	    infosScroller.setPreferredSize(new Dimension(250, 150));
	    
	    //panelInfo = new JPanel(new GridLayout(2,1));
	    panelInfo = new JPanel();
	    panelInfo.setLayout(new BoxLayout(panelInfo, BoxLayout.PAGE_AXIS));
	    panelInfo.setBorder(new TitledBorder(new EtchedBorder(), "Console"));
	    panelInfo.add(panelInfoButtons);
	    panelInfo.add(infosScroller);
	    
	    mainPanel.setLayout(new BorderLayout(0, 0));
	    mainPanel.add(panelEntity, BorderLayout.PAGE_START);
	    mainPanel.add(panelIdentity, BorderLayout.LINE_START);
	    mainPanel.add(panelFiles, BorderLayout.LINE_END);
	    mainPanel.add(panelInfo, BorderLayout.PAGE_END);
	    
	    this.setContentPane(mainPanel);
	    loadPublicKeyFromFile();
	    setVisible(true); // display this frame
	}
	
	public static void main(String args[]) {new GUI();}
	
	public void setTag(String tag) {
		// TODO Auto-generated method stub
		
	}

	public void print(String str, String tag, String threadID) {
		infos.insert(tag + str + "\n", 0);
	}

	public void print(byte[] bytes, String tag, String threadID) {
		// TODO Auto-generated method stub
		
	}

	public void nextStep(String tag, String threadID) {
		// TODO Auto-generated method stub
		
	}

	public void printErr(String str, String threadID) {
		// TODO Auto-generated method stub
		infos.insert(str + "\n", 0);
	}

	public String getInput(String info, String threadID) {
		// TODO Auto-generated method stub
		return null;
	}

	public void start(ProtocolEntity entity) {
		// TODO Auto-generated method stub
		
	}
	
	public void actionPerformed(ActionEvent e) {
		int returnValue;
		if (e.getSource() == buttonPrivateKeyChoose) {
			returnValue = fileChooser.showOpenDialog(this);
			if (returnValue == JFileChooser.APPROVE_OPTION) {
				File f = fileChooser.getSelectedFile();
				textPrivateKey.setText(f.getName());
			}
		} else if (e.getSource() == buttonPublicKeyChoose) {
			returnValue = fileChooser.showOpenDialog(this);
			if (returnValue == JFileChooser.APPROVE_OPTION) {
				File f = fileChooser.getSelectedFile();
				textPublicKeyPath.setText(f.getPath());
			}
		} else if (e.getSource() == buttonPublicKeyAdd) {
			String id = textPublicKeyID.getText();
			String path = textPublicKeyPath.getText();
			if (!id.equals("") && !path.equals("")) {
				String[] rowData = {id, path};
				tableModel.insertRow(0, rowData);
			}
		} else if (e.getSource() == buttonPublicKeyDel) {
			int selectedIndex = tablePublicKey.getSelectedRow();
			tableModel.removeRow(selectedIndex);
		} else if (e.getSource() == buttonDataChoose) {
			returnValue = fileChooser.showOpenDialog(this);
			if (returnValue == JFileChooser.APPROVE_OPTION) {
				File f = fileChooser.getSelectedFile();
				textDataPath.setText(f.getPath());
			}
		} else if (e.getSource() == dcRButton) {
			setAllVisible();
			labelDstID.setVisible(false);
			textDstID.setVisible(false);
			labelDstAddress.setVisible(false);
			textDstAddress.setVisible(false);
			labelDstPort.setVisible(false);
			textDstPort.setVisible(false);
			panelFilesMaxCapacity.setVisible(false);
		} else if (e.getSource() == rcRButton) {
			setAllVisible();
			labelLocAddress.setVisible(false);
			textLocAddress.setVisible(false);
			labelLisPort.setVisible(false);
			textLisPort.setVisible(false);
			labelPrivateKey.setVisible(false);
			panelPrivateKey.setVisible(false);
			panelData.setVisible(false);
		} else if (e.getSource() == drRButton) {
			setAllVisible();
			labelDstID.setVisible(false);
			textDstID.setVisible(false);
			labelDstAddress.setVisible(false);
			textDstAddress.setVisible(false);
			labelDstPort.setVisible(false);
			textDstPort.setVisible(false);
			panelFilesMaxCapacity.setVisible(false);
			panelData.setVisible(false);
		} else if (e.getSource() == scRButton) {
			setAllVisible();
			labelLocAddress.setVisible(false);
			textLocAddress.setVisible(false);
			labelLisPort.setVisible(false);
			textLisPort.setVisible(false);
			panelFilesMaxCapacity.setVisible(false);
			panelPrivateKey.setVisible(false);
			labelDataID.setVisible(false);
			textDataID.setVisible(false);
		} else if (e.getSource() == buttonStart) {
			Enumeration<AbstractButton> rButtons = entityGroup.getElements();
			while (rButtons.hasMoreElements()) {
				AbstractButton rButton = rButtons.nextElement();
				if (rButton.isSelected()) {
					startEntity(rButton.getText());
				}
			}
		}
	}
	
	private void setAllVisible() {
		labelLocID.setVisible(true);
		labelDstID.setVisible(true);
		labelDstAddress.setVisible(true);
		labelDstPort.setVisible(true);
		labelLisPort.setVisible(true);
		labelLocAddress.setVisible(true);
		panelFilesMaxCapacity.setVisible(true);
		labelPrivateKey.setVisible(true);
		textLocID.setVisible(true);
		textDstID.setVisible(true);
		textLisPort.setVisible(true);
		textLocAddress.setVisible(true);
		textDstAddress.setVisible(true);
		textDstPort.setVisible(true);
		panelPrivateKey.setVisible(true);
		panelData.setVisible(true);
		labelDataID.setVisible(true);
		textDataID.setVisible(true);
		buttonStop.setVisible(true);
	}
	
	private void startEntity(String entityName) {		
		if (entityName.equals("DataCreator")) {
			textLisPort.setText("9888");
			textLocID.setText("Alice");
			textLocAddress.setText("localhost");
			textPrivateKey.setText("PrivateKey_Alice");
		    textDataID.setText("Bob");
		    textDataPath.setText("Data_Alice_To_Bob(Alice)");
			
			Thread thread = new Thread(new Runnable() {
				public void run() {
					String myID = textLocID.getText();
					String revID = textDataID.getText();
					int lisPort;
					
					try {
						lisPort = Integer.parseInt(textLisPort.getText());
					
						DataManager dataManager = new DataManager(myID);
						dataManager.setPathPrivateKey(textPrivateKey.getText());
						for (int i = 0; i < tableModel.getRowCount(); i++) {
							dataManager.setPathPublicKey((String)tableModel.getValueAt(i, 0), (String)tableModel.getValueAt(i, 1));
						}
	
						Accepter alice = 
								new Alice(textLocAddress.getText(), lisPort, ui, crypto, dataManager, myID, revID);
						alice.start();
					} catch (NumberFormatException e) {
						ui.printErr("MalFormatted Input: " + e.getMessage(), myID);
					}
				}
			});
			thread.start();
			
		} else if (entityName.equals("CourierReceiver")) {
			textLocID.setText("Courier");
			textDstID.setText("Alice");
			textDstAddress.setText("localhost");
			textDstPort.setText("9888");
			textMaxCapacity.setText("1000000");
		    
			Thread thread = new Thread(new Runnable() {
				public void run() {
					String myID = textLocID.getText();
					String senderID = textDstID.getText();
					int dstPort;
					int maxCapacity;
					
					try {
						dstPort = Integer.parseInt(textDstPort.getText());
						maxCapacity = Integer.parseInt(textMaxCapacity.getText());
						
						DataManager dataManager = new DataManager(myID, maxCapacity);
						for (int i = 0; i < tableModel.getRowCount(); i++) {
							dataManager.setPathPublicKey((String)tableModel.getValueAt(i, 0), (String)tableModel.getValueAt(i, 1));
						}
	
						Initiator courier = 
								new CourierA(textDstAddress.getText(), dstPort, ui, crypto, dataManager, myID, senderID);
						courier.start();
					} catch (NumberFormatException e) {
						ui.printErr("MalFormatted Input: " + e.getMessage(), myID);
					}
				}
			});
			thread.start();
		} else if (entityName.equals("DataReceiver")) {
			textLocID.setText("Bob");
			textLocAddress.setText("localhost");
			textLisPort.setText("8888");
		    textPrivateKey.setText("PrivateKey_Bob");
			
			Thread thread = new Thread(new Runnable() {
				public void run() {
					String myID = textLocID.getText();
					int lisPort;
					
					try {
						lisPort = Integer.parseInt(textLisPort.getText());
						
						DataManager dataManager = new DataManager(myID);
						for (int i = 0; i < tableModel.getRowCount(); i++) {
							dataManager.setPathPublicKey((String)tableModel.getValueAt(i, 0), (String)tableModel.getValueAt(i, 1));
						}
						dataManager.setPathPrivateKey(textPrivateKey.getText());
	
						Accepter receiver = new Bob(textLocAddress.getText(), lisPort, ui, crypto, dataManager, myID);
						receiver.start();
					} catch (NumberFormatException e) {
						ui.printErr("MalFormatted Input: " + e.getMessage(), myID);
					}
				}
			});
			thread.start();
			
		} else if (entityName.equals("CourierSender")) {
			textLocID.setText("Courier");
			textDstID.setText("Bob");
			textDstAddress.setText("localhost");
			textDstPort.setText("8888");
		    textDataID.setText("Alice");
		    textDataPath.setText("Data_Alice_To_Bob");
		    
			Thread thread = new Thread(new Runnable() {
				public void run() {
					String myID = textLocID.getText();
					String revID = textDstID.getText();
					int dstPort = 0;

					try {
						dstPort = Integer.parseInt(textDstPort.getText());

						DataManager dataManager = new DataManager(myID);
						for (int i = 0; i < tableModel.getRowCount(); i++) {
							dataManager.setPathPublicKey((String)tableModel.getValueAt(i, 0), (String)tableModel.getValueAt(i, 1));
						}

						Initiator courier = 
								new CourierB(textDstAddress.getText(), dstPort, ui, crypto, dataManager, myID, revID);
						courier.start();
					} catch (NumberFormatException e) {
						ui.printErr("MalFormatted Input: " + e.getMessage(), myID);
					}
				}
			});
			thread.start();
		}
	}
	
	private void loadPublicKeyFromFile() {
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(new File(PATH_PUBLICKEYFILE)));
			String readRow = reader.readLine();
			String[] row = new String[2];
			while (readRow != null) {
				row = readRow.split(";");
				tableModel.addRow(row);
				readRow = reader.readLine();
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private boolean isNumeric(String str) {
		return str.matches("-?\\d+(\\.\\d+)?");  //match a number with optional '-' and decimal.
	}
}
