import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;

public class Interface extends JFrame {
	private JTabbedPane tabbedPane = new JTabbedPane(); // this cpontainer for sing up and sing in panels
	private CardLayout cardLayout = new CardLayout(); // this a way to arrange panels like a cards, we use this way to
														// manipulating panels in easier way
	private JPanel mainPanel = new JPanel(cardLayout);// this container used to put other cards(panels) in it
	private PasswordOperations po = new PasswordOperations(); // we use this object to access the method in
																// passwrdOperations class
	private DatabaseOperations dbo = new DatabaseOperations();// we use this object to access the method in
																// DatabaseOperation class

	public Interface() {
		// frame attribute
		setTitle("Password Validator");
		setSize(Toolkit.getDefaultToolkit().getScreenSize());
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// add the first panel to the frame
		JPanel firstPanel = createFirstPanel();

		// add scroll to fit all sizes of screen
		JScrollPane scrollPane = new JScrollPane(firstPanel);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		getContentPane().add(scrollPane);

		setVisible(true);
	}

	// first panel
	private JPanel createFirstPanel() {
		// Create the panel
		JPanel firstPanel = new JPanel();
		firstPanel.setBackground(Color.decode("#f5fffa"));
		firstPanel.setBorder(new LineBorder(Color.decode("#37474F"), 20));
		firstPanel.setLayout(null);
		firstPanel.setPreferredSize(new Dimension(1200, 900));

		/*
		 * create the image locate at image folder in the same project
		 * if the image not exist or have any problem the text appear instead of it
		 */
		JLabel lblImage = new JLabel();
		URL imageUrl = getClass().getResource("/images/StartImage.png");
		if (imageUrl != null) {
			ImageIcon startImage = new ImageIcon(imageUrl);
			Image imageSize = startImage.getImage().getScaledInstance(700, 500, 0); // to change the size of the image
			lblImage.setIcon(new ImageIcon(imageSize));
		} else {
			lblImage.setText("Image of password security");
			lblImage.setHorizontalAlignment(SwingConstants.CENTER);
		}

		lblImage.setBounds(400, 100, 700, 500);
		firstPanel.add(lblImage);

		// create the label under the image
		JLabel lblTitle = createLabel("Welcome to Our Password Validator", new Font("Arial", Font.BOLD, 20),
				new Color(54, 54, 54), 550, 600, 400, 30);
		lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
		firstPanel.add(lblTitle);

		// create the button
		JButton btnStart = createButton("Get Started", new Font("Arial", Font.BOLD, 16), Color.decode("#ff9999"),
				Color.white, 650,
				700, 200, 60);

		firstPanel.add(btnStart);

		/*
		 * this method handle the click on the button
		 * if the button clicked the component in the first panel will be removed then
		 * call sign up and sign in panels
		 */
		btnStart.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				firstPanel.removeAll();
				firstPanel.revalidate();
				firstPanel.repaint();
				callOtherPanels(firstPanel);
			}
		});

		return firstPanel;
	}

	private void callOtherPanels(JPanel firstPanel) {
		// main panel attributes: we use this panel to put sign up and sign in and
		// welcome panels in it
		mainPanel.setBackground(Color.decode("#37474F"));
		mainPanel.setBounds(400, 100, 700, 700);
		firstPanel.add(mainPanel);

		// create three panels
		JPanel signUpPanel = createSignUpPanel();
		JPanel signInPanel = createSignInPanel();
		JPanel welcomePanel = welcomePanel();

		// add sign in and sing up panels to tabbed pane: this step to make the move
		// between two panels are easier
		tabbedPane.add(signUpPanel, "sign Up");
		tabbedPane.add(signInPanel, "sign In");

		// attributes for tabbed panel icons
		tabbedPane.setForegroundAt(0, Color.WHITE);
		tabbedPane.setForegroundAt(1, Color.WHITE);
		tabbedPane.setBackgroundAt(0, new Color(64, 181, 246));
		tabbedPane.setBackgroundAt(1, new Color(33, 150, 243));
		tabbedPane.setBorder(new LineBorder(new Color(189, 189, 189), 2));

		// add the tabbed pane and welcome panel to mainPanel
		mainPanel.add(tabbedPane);
		mainPanel.add(welcomePanel, "welcomePanel");
	}

	// Sing Up panel
	private JPanel createSignUpPanel() {
		// create the panel and its componets using createPnaelComponent method
		JPanel signUpPanel = createPanelComponent("Sign Up");

		// In this part we create the password criteria labels
		// main label
		JLabel lblCriteriaTitle = createLabel("Your Password Must Include the following:- ",
				new Font("Open Sans", Font.BOLD, 12), Color.GRAY, 154, 395, 300, 20);
		signUpPanel.add(lblCriteriaTitle);

		// label for each criteria
		JLabel[] lblCriteria = new JLabel[5];

		String[] criteriaTexts = {
				"• At Least 8 Characters.",
				"• One Upper Case.",
				"• One Lower Case.",
				"• One Number.",
				"• One Special Character (@, #, $, ...)"
		};

		for (int i = 0; i < criteriaTexts.length; i++) {
			lblCriteria[i] = createLabel(criteriaTexts[i], new Font("Open Sans", Font.PLAIN, 12),
					Color.decode("#a52a2a"), 154, 420 + (i * 20), 300, 20);
			signUpPanel.add(lblCriteria[i]);
		}

		// this method handle the color of each criteria label: if the criteria achieve
		// by the user the color will be green, otherwise the color will be red
		JPasswordField txtPassword = ((JPasswordField) signUpPanel.getComponent(4));
		txtPassword.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				String password = new String(txtPassword.getPassword());
				// here we using updateLabelColor method below which check if the criteria
				// achieved or not to reduce the repeat of the code
				updateLabelColor(lblCriteria[0], password.length() >= 8);
				updateLabelColor(lblCriteria[1], password.matches(".*[A-Z].*"));
				updateLabelColor(lblCriteria[2], password.matches(".*[a-z].*"));
				updateLabelColor(lblCriteria[3], password.matches(".*[0-9].*"));
				updateLabelColor(lblCriteria[4], password.matches(".*[@_/.$!#&].*"));
			}
		});

		// button for sign up
		JButton btnSignUp = createButton("Sign Up", new Font("Open Sans", Font.PLAIN, 22), Color.decode("#4682b4"),
				Color.white, 285,
				580, 127, 35);

		// this method handle clicking the button: if the password entered by the user
		// is correct will store in database then clear the content in textfields,
		// otherwise the password field border will be red
		btnSignUp.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String userName = ((JTextField) signUpPanel.getComponent(2)).getText();
				String password = new String(txtPassword.getPassword());
				if (po.isValidPassword(password)) {
					String salt = po.generateSalt();
					String hashPassword = po.hashPassword(password, salt);
					dbo.registerUser(userName, hashPassword, salt);
					tabbedPane.setComponentAt(0, createSignUpPanel());
					tabbedPane.setComponentAt(1, createSignInPanel());
					tabbedPane.setSelectedIndex(tabbedPane.getSelectedIndex() + 1);
				} else {
					txtPassword.setBorder(new LineBorder(Color.RED, 2));
				}
			}
		});
		signUpPanel.add(btnSignUp);

		return signUpPanel;
	}

	// Sing In panel
	private JPanel createSignInPanel() {

		JPanel signInPanel = createPanelComponent("Sign In");
		// create label to show error message when sign in failed now this label empty.
		JLabel lblResult = createLabel("", new Font("Open Sans", Font.PLAIN, 15), Color.DARK_GRAY, 185, 410, 350, 90);
		signInPanel.add(lblResult);

		// button for sign in
		JButton btnSignIn = createButton("Sign In", new Font("Open Sans", Font.PLAIN, 22), Color.decode("#4682b4"),
				Color.WHITE, 285,
				580, 127, 35);

		// this method handle clicking the button if the user enter correct username and
		// password welcome panel will appear, otherwise error message will be appear
		btnSignIn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				String userName = ((JTextField) signInPanel.getComponent(2)).getText();
				String password = new String(((JPasswordField) signInPanel.getComponent(4)).getPassword());

				if (dbo.isUserLogin(userName, password, po)) {
					tabbedPane.setComponentAt(0, createSignUpPanel());
					tabbedPane.setComponentAt(1, createSignInPanel());
					cardLayout.show(mainPanel, "welcomePanel");
				} else {
					lblResult.setText(
							"<html><center>Sign in failed, make sure your username and password are correct<center><html>");
					lblResult.setBorder(new LineBorder(Color.RED, 1));
					lblResult.setBackground(Color.PINK);
					lblResult.setOpaque(true);
					lblResult.setHorizontalAlignment(SwingConstants.CENTER);
				}

			}

		});
		signInPanel.add(btnSignIn);

		return signInPanel;
	}

	// Welcome panel
	private JPanel welcomePanel() {
		// create panel
		JPanel welcomePanel = new JPanel();
		welcomePanel.setBorder(new LineBorder(Color.decode("#37474F"), 20));
		welcomePanel.setBounds(187, 56, 596, 453);
		welcomePanel.setBackground(new Color(245, 245, 245));
		welcomePanel.setLayout(null);

		// label for panel title
		JLabel lblWelcome = createLabel("Well Done!", new Font("Roboto", Font.BOLD, 60), Color.decode("#99ccff"), 110,
				60, 500, 200);
		lblWelcome.setHorizontalAlignment(SwingConstants.CENTER);
		welcomePanel.add(lblWelcome);
		welcomePanel.add(createLabel(
				"<html><center>🎉🎉🎉🎉🎉🎉🎉🎉🎉🎉<br><br><br>You’ve just created a rock-solid password  🔒<br><br>"
						+ "one that hackers can only dream of breaking!<br><html>",
				new Font("Roboto", Font.PLAIN, 20), Color.decode("#99ccff"), 140, 20, 600, 550));

		JButton btnReturn = createButton("Try Again", new Font("Arial", Font.BOLD, 16), Color.decode("#ff9999"),
				Color.white, 250,
				500, 200, 60);

		btnReturn.addActionListener(e -> {
			tabbedPane.setSelectedIndex(0);
			cardLayout.previous(mainPanel);
		});
		welcomePanel.add(btnReturn);

		return welcomePanel;

	}

	/*
	 * this method to create the components inside the panels to reducing the repeat
	 * of the code
	 * first one to create label
	 * second one for create text field
	 * third one for create password field forth one for create button
	 * fifth one for create checkbox
	 */

	private JLabel createLabel(String text, Font font, Color color, int x, int y, int width, int height) {
		JLabel label = new JLabel(text);
		label.setFont(font);
		label.setForeground(color);
		label.setBounds(x, y, width, height);
		return label;
	}

	private JTextField createTextField(Font font, int x, int y, int width, int height, Color color, Border border) {
		JTextField textField = new JTextField();
		textField.setFont(font);
		textField.setBounds(x, y, width, height);
		textField.setBackground(color);
		textField.setBorder(border);
		return textField;
	}

	private JPasswordField createPasswordField(Font font, int x, int y, int width, int height, Color color,
			Border border) {
		JPasswordField passwordField = new JPasswordField();
		passwordField.setFont(font);
		passwordField.setBounds(x, y, width, height);
		passwordField.setBackground(color);
		passwordField.setBorder(border);
		return passwordField;
	}

	private JButton createButton(String text, Font font, Color color, Color fontColor, int x, int y, int width,
			int height) {
		JButton button = new JButton(text);
		button.setBounds(x, y, width, height);
		button.setFont(font);
		button.setBackground(color);
		button.setOpaque(true);
		button.setBorderPainted(false);
		button.setForeground(fontColor);
		button.setFocusable(false);
		return button;
	}

	private JCheckBox createCheckBox(String text, int x, int y, int width, int height) {
		JCheckBox checkBox = new JCheckBox(text);
		checkBox.setBounds(x, y, width, height);
		return checkBox;
	}

	// create label and textfeild in tabbed pane
	private JPanel createPanelComponent(String panelName) {
		// create the panel of sign in
		JPanel panel = new JPanel();
		panel.setBorder(new LineBorder(Color.decode("#D9EAF7"), 2));
		panel.setBackground(new Color(245, 245, 245));
		panel.setLayout(null);

		/*
		 * add the component inside the panel like username label and text field using
		 * the method below to reducing the repeat of the code
		 * first one for panel label
		 * second one for username label third one for username text field
		 * forth one for password label fifth one for password text field
		 * sixth one for show password by clicking the check box
		 * then we add all these component to the panel
		 */
		panel.add(createLabel(panelName, new Font("Roboto", Font.BOLD, 48), new Color(55, 71, 79), 250, 40, 288, 55));
		panel.add(createLabel("User Name:", new Font("Open Sans", Font.BOLD, 22), null, 154, 120, 400, 60));
		JTextField txtUserName = createTextField(new Font("Open Sans", Font.PLAIN, 22), 154, 170, 400, 60,
				new Color(250, 250, 250), new LineBorder(new Color(189, 189, 189), 1));
		panel.add(txtUserName);
		panel.add(createLabel("Password:", new Font("Open Sans", Font.BOLD, 22), null, 154, 238, 400, 60));
		JPasswordField txtPassword = createPasswordField(new Font("Open Sans", Font.PLAIN, 22), 154, 290, 400, 55,
				new Color(250, 250, 250), new LineBorder(new Color(189, 189, 189), 1));
		panel.add(txtPassword);
		JCheckBox showPassword = createCheckBox("Show Password", 154, 345, 200, 30);
		panel.add(showPassword);

		// this method handle the clicking on the check box: if check box is selected
		// the password will be appear on the screen, otherwise the password will be
		// hidden
		showPassword.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (showPassword.isSelected()) {
					txtPassword.setEchoChar('\0');
				} else {
					txtPassword.setEchoChar('•');
				}
			}
		});
		return panel;
	}

	// this method change the color of any label according to specific condition
	// (password criteria in our case)
	private void updateLabelColor(JLabel label, boolean condition) {
		label.setForeground(condition ? Color.decode("#008000") : Color.decode("#a52a2a"));
	}

}
