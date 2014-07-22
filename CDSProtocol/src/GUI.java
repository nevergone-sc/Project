import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Enumeration;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Dimension;


public class GUI extends JFrame implements UserInterface, ActionListener {
	private Crypto crypto = new Crypto();
	private DataManager dataManager;
	private String PATH_PUBLICKEYFILE = "PublicKeys";
	
	private JButton buttonPrivateKey, buttonPublicKeyAdd, buttonPublicKeyDel, buttonPublicKeyChoose, buttonDataChoose;
	private JButton buttonStart, buttonStop;
	private JRadioButton dcRButton, rcRButton, drRButton, scRButton;
	private JTextField textPrivateKey, textLocID, textLocAddress, textDstID, textDstAddress;
	private JTextField textLisPort, textDstPort, textMaxCapacity;
	private JTextField textPublicKeyID, textPublicKeyPath, textDataID, textDataPath;
	private JLabel labelLocID, labelLisPort, labelLocAddress, labelDstID, labelDstPort, labelDstAddress;
	private JLabel labelMaxCapacity, labelPublicKey, labelPrivateKey, labelData, labelDataID;
	private JFileChooser fileChooser;
	private JPanel panelIdentity, panelFiles, panelEntity, panelInfo;
	private JPanel panelPrivateKey, panelData;
	private DefaultTableModel tableModel;
	private JTable tablePublicKey;
	private JTextArea infos;
	private ButtonGroup entityGroup;
	
	UserInterface ui = this;
	
	public GUI() // the frame constructor method
	{
	    super("My Simple Frame"); setBounds(400,200,820,800);
	    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    JPanel mainPanel = new JPanel();
	    
	    fileChooser = new JFileChooser();
	    
	    // Panel for choosing entity
	    dcRButton = new JRadioButton("DataCreator");
	    dcRButton.addActionListener(this);
	    rcRButton = new JRadioButton("ReceiveCourier");
	    rcRButton.addActionListener(this);

	    drRButton = new JRadioButton("DataReceiver");
	    drRButton.addActionListener(this);
	    scRButton = new JRadioButton("SendCourier");
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
	    
	    panelIdentity = new JPanel(new GridLayout(12, 1));
	    panelIdentity.setBorder(new TitledBorder(new EtchedBorder(), "Entities' Identities"));
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
	    labelMaxCapacity = new JLabel("Maximum data capacity: ");
	    textMaxCapacity = new JFormattedTextField();
	    textMaxCapacity.setColumns(10);
	    
	    // Choose Private Key file
	    labelPrivateKey = new JLabel("Private Key File: ");
	    textPrivateKey = new JTextField(20);
	    buttonPrivateKey = new JButton("Choose");
	    buttonPrivateKey.addActionListener(this);
	    
	    panelPrivateKey = new JPanel();
	    panelPrivateKey.add(textPrivateKey);
	    panelPrivateKey.add(buttonPrivateKey);
	    
	    // Choose Public Key file
	    labelPublicKey = new JLabel("Public Key Files: ");
	    tableModel = new DefaultTableModel();
	    tableModel.addColumn("Owner ID");
	    tableModel.addColumn("Public Key Path");
	    tablePublicKey = new JTable(tableModel);
	    JScrollPane tableScroller = new JScrollPane(tablePublicKey);
	    tableScroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
	    tableScroller.setPreferredSize(new Dimension(250, 80));
	    
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
	    
	    labelData = new JLabel("Data to Send:");
	    labelDataID = new JLabel("Recipient");
	    textDataID = new JTextField(10);
	    JLabel labelData3 = new JLabel("Path");
	    textDataPath = new JTextField(20);
	    buttonDataChoose = new JButton("Choose");
	    panelData = new JPanel();
	    panelData.add(labelDataID);
	    panelData.add(textDataID);;
	    panelData.add(labelData3);
	    panelData.add(textDataPath);
	    panelData.add(buttonDataChoose);
	    
	    panelFiles = new JPanel(new GridLayout(10,1));
	    panelFiles.setBorder(new TitledBorder(new EtchedBorder(), "File Paths"));
	    panelFiles.add(labelMaxCapacity);
	    panelFiles.add(textMaxCapacity);
	    panelFiles.add(labelPublicKey);
	    panelFiles.add(tableScroller);
	    //panelFiles.add(tablePublicKey.getTableHeader());
	    //panelFiles.add(tablePublicKey);
	    panelFiles.add(panelPublicKeyText);
	    panelFiles.add(panelPublicKeyButton);
	    
	    panelFiles.add(labelPrivateKey);
	    panelFiles.add(panelPrivateKey);
	    
	    panelFiles.add(labelData);
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
	    infosScroller.setPreferredSize(new Dimension(250, 80));
	    
	    panelInfo = new JPanel(new GridLayout(2,1));
	    panelInfo.setBorder(new TitledBorder(new EtchedBorder(), "Console"));
	    panelInfo.add(panelInfoButtons);
	    panelInfo.add(infosScroller);
	    
	    mainPanel.setLayout(new BorderLayout(0, 0));
	    mainPanel.add(panelEntity, BorderLayout.PAGE_START);
	    mainPanel.add(panelIdentity, BorderLayout.LINE_START);
	    mainPanel.add(panelFiles, BorderLayout.LINE_END);
	    mainPanel.add(panelInfo, BorderLayout.PAGE_END);
	    
	    this.setContentPane(mainPanel);
	    setVisible(true); // display this frame
	}
	  public static void main(String args[]) {new GUI();}
	
	@Override
	public void setTag(String tag) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void print(String str, String tag, String threadID) {
		infos.insert(tag + str + "\n", 0);
		
	}

	@Override
	public void print(byte[] bytes, String tag, String threadID) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void nextStep(String tag, String threadID) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void printErr(String str, String threadID) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getInput(String info, String threadID) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void start(ProtocolEntity entity) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		int returnValue;
		if (e.getSource() == buttonPrivateKey) {
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
			labelDataID.setText("Recipient");
		} else if (e.getSource() == rcRButton) {
			setAllVisible();
			labelLocAddress.setVisible(false);
			textLocAddress.setVisible(false);
			labelLisPort.setVisible(false);
			textLisPort.setVisible(false);
			labelPrivateKey.setVisible(false);
			panelPrivateKey.setVisible(false);
			labelData.setVisible(false);
			panelData.setVisible(false);
		} else if (e.getSource() == drRButton) {
			setAllVisible();
			labelDstID.setVisible(false);
			textDstID.setVisible(false);
			labelDstAddress.setVisible(false);
			textDstAddress.setVisible(false);
			labelDstPort.setVisible(false);
			textDstPort.setVisible(false);
			labelData.setVisible(false);
			panelData.setVisible(false);
		} else if (e.getSource() == scRButton) {
			setAllVisible();
			labelLocAddress.setVisible(false);
			textLocAddress.setVisible(false);
			labelLisPort.setVisible(false);
			textLisPort.setVisible(false);
			labelPrivateKey.setVisible(false);
			panelPrivateKey.setVisible(false);
			labelDataID.setText("Sender");
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
		labelMaxCapacity.setVisible(true);
		labelPublicKey.setVisible(true);
		labelPrivateKey.setVisible(true);
		textLocID.setVisible(true);
		textDstID.setVisible(true);
		textLisPort.setVisible(true);
		textLocAddress.setVisible(true);
		textDstAddress.setVisible(true);
		textDstPort.setVisible(true);
		panelPrivateKey.setVisible(true);
		panelData.setVisible(true);
		buttonStop.setVisible(true);
	}
	
	private void startEntity(String entityName) {
		loadPublicKeyFromFile();
		
		if (entityName.equals("DataCreator")) {
			textLisPort.setText("9888");
			textLocID.setText("Alice");
			textLocAddress.setText("localhost");
			textMaxCapacity.setText("1000");
			textPrivateKey.setText("PrivateKey_Alice");
		    textDataID.setText("Bob");
		    textDataPath.setText("Data_Alice_To_Bob(Alice)");
			
			Thread thread = new Thread(new Runnable() {
				public void run() {
					String myID = textLocID.getText();
					String revID = textDataID.getText();
					DataManager dataManager = new DataManager(Integer.parseInt(textMaxCapacity.getText()));
					dataManager.setPathPrivateKey(textPrivateKey.getText());
					for (int i = 0; i < tableModel.getRowCount(); i++) {
						dataManager.setPathPublicKey((String)tableModel.getValueAt(i, 0), (String)tableModel.getValueAt(i, 1));
					}
					dataManager.setPathData(myID, textDataID.getText(), textDataPath.getText());

					Accepter alice = 
							new Alice(textLocAddress.getText(), Integer.parseInt(textLisPort.getText()), ui, crypto, dataManager, myID, revID);
					alice.start();
				}
			});
			thread.start();
			
		} else if (entityName.equals("ReceiveCourier")) {
			textLocID.setText("Courier");
			textDstID.setText("Alice");
			textDstAddress.setText("localhost");
			textDstPort.setText("9888");
			textMaxCapacity.setText("1000");
		    
			Thread thread = new Thread(new Runnable() {
				public void run() {
					String myID = textLocID.getText();
					String senderID = textDstID.getText();
					DataManager dataManager = new DataManager(Integer.parseInt(textMaxCapacity.getText()));
					for (int i = 0; i < tableModel.getRowCount(); i++) {
						dataManager.setPathPublicKey((String)tableModel.getValueAt(i, 0), (String)tableModel.getValueAt(i, 1));
					}

					Initializer courier = 
							new CourierA(textDstAddress.getText(), Integer.parseInt(textDstPort.getText()), ui, crypto, dataManager, myID, senderID);
					courier.start();
				}
			});
			thread.start();
		} else if (entityName.equals("DataReceiver")) {
			textLocID.setText("Bob");
			textLocAddress.setText("localhost");
			textLisPort.setText("8888");
			textMaxCapacity.setText("1000");
		    textPrivateKey.setText("PrivateKey_Bob");
			
			Thread thread = new Thread(new Runnable() {
				public void run() {
					String myID = textLocID.getText();
					DataManager dataManager = new DataManager(Integer.parseInt(textMaxCapacity.getText()));
					for (int i = 0; i < tableModel.getRowCount(); i++) {
						dataManager.setPathPublicKey((String)tableModel.getValueAt(i, 0), (String)tableModel.getValueAt(i, 1));
					}
					dataManager.setPathPrivateKey(textPrivateKey.getText());

					Accepter receiver = new Bob(textLocAddress.getText(), Integer.parseInt(textLisPort.getText()), ui, crypto, dataManager, myID);
					receiver.start();
				}
			});
			thread.start();
			
		} else if (entityName.equals("SendCourier")) {
			textLocID.setText("Courier");
			textDstID.setText("Bob");
			textDstAddress.setText("localhost");
			textDstPort.setText("8888");
			textMaxCapacity.setText("1000");
		    textDataID.setText("Alice");
		    textDataPath.setText("Data_Alice_To_Bob");
		    
			Thread thread = new Thread(new Runnable() {
				public void run() {
					String myID = textLocID.getText();
					String sedID = textDataID.getText();
					String revID = textDstID.getText();
					DataManager dataManager = new DataManager(Integer.parseInt(textMaxCapacity.getText()));
					for (int i = 0; i < tableModel.getRowCount(); i++) {
						dataManager.setPathPublicKey((String)tableModel.getValueAt(i, 0), (String)tableModel.getValueAt(i, 1));
					}
					dataManager.setPathData(textDataID.getText(), textDstID.getText(), textDataPath.getText());

					Initializer courier = 
							new CourierB(textDstAddress.getText(), Integer.parseInt(textDstPort.getText()), ui, crypto, dataManager, myID, sedID, revID);
					courier.start();
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
}
