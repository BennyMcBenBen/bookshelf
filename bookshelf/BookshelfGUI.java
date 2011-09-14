package bookshelf;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.*;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.*;

import se.datadosen.component.RiverLayout;

// TODO pop-up window for contains, validation on author submit
// validation on year submit on add book button push
// add in spaces for correct formatting: two spaces at beg, \n at end

public class BookshelfGUI extends JPanel implements ActionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private JFrame frame;

	private Color uneditable = new Color(240, 240, 240);

	private JTextField titleField, authorField, yearField, issueField,
			seriesField;

	private JRadioButton authorTypeButton, editorTypeButton;

	private ButtonGroup authorTypeGroup;

	private JPanel authorTypeButtons;

	private JLabel authorTypeLabel;

	private JTextArea authorsTextArea;

	// book type
	private JRadioButton fictionButton, nonfictionButton, starWarsButton;

	private ButtonGroup typeGroup;

	private JPanel typeButtons;

	// book medium
	private JRadioButton bookButton, comicButton;

	private ButtonGroup mediumGroup;

	private JPanel mediumButtons;

	// signed
	private JRadioButton signedButton, notSignedButton;

	private ButtonGroup signedGroup;

	private JPanel signedButtons;

	// submit new book button
	private JButton addBookButton;

	private Bookshelf shelf;

	public BookshelfGUI(JFrame f) {
		super(new RiverLayout());
		frame = f;
		addComponents();
		shelf = new Bookshelf();
	}

	private void addComponents() {
		// title
		add("p left", new JLabel("Title"));
		titleField = new JTextField();
		add("tab hfill", titleField);

		// type
		add("br", new JLabel("Author Type"));

		authorTypeButton = new JRadioButton("Author");
		authorTypeButton.setActionCommand("Author");
		authorTypeButton.setSelected(true);
		editorTypeButton = new JRadioButton("Editor");
		editorTypeButton.setActionCommand("Editor");

		authorTypeGroup = new ButtonGroup();
		authorTypeGroup.add(authorTypeButton);
		authorTypeGroup.add(editorTypeButton);

		authorTypeButtons = new JPanel();
		authorTypeButtons.add(authorTypeButton);
		authorTypeButtons.add(editorTypeButton);

		add("tab", authorTypeButtons);

		// author
		authorTypeLabel = new JLabel("Authors");
		add("br", authorTypeLabel);

		authorTypeButton.addActionListener(new AuthorTypeChooser());
		editorTypeButton.addActionListener(new AuthorTypeChooser());

		authorField = new JTextField();
		add("tab hfill", authorField);

		add("br", new JLabel(""));
		authorsTextArea = new JTextArea();
		authorsTextArea.setRows(2);
		authorsTextArea.setEditable(false);
		authorsTextArea.setBackground(uneditable);
		add("tab hfill", new JScrollPane(authorsTextArea));

		authorField.addActionListener(new AuthorAdder());

		// type
		add("br", new JLabel("Type"));

		fictionButton = new JRadioButton("Fiction");
		fictionButton.setActionCommand("fiction");
		fictionButton.setSelected(true);
		nonfictionButton = new JRadioButton("Nonfiction");
		nonfictionButton.setActionCommand("nonfiction");
		starWarsButton = new JRadioButton("Star Wars");
		starWarsButton.setActionCommand("starWars");

		typeGroup = new ButtonGroup();
		typeGroup.add(fictionButton);
		typeGroup.add(nonfictionButton);
		typeGroup.add(starWarsButton);

		typeButtons = new JPanel();
		typeButtons.add(fictionButton);
		typeButtons.add(nonfictionButton);
		typeButtons.add(starWarsButton);

		add("tab", typeButtons);

		// medium
		add("br", new JLabel("Medium"));

		bookButton = new JRadioButton("Book");
		bookButton.setActionCommand("book");
		bookButton.setSelected(true);
		comicButton = new JRadioButton("Comic");
		comicButton.setActionCommand("comic");

		mediumGroup = new ButtonGroup();
		mediumGroup.add(bookButton);
		mediumGroup.add(comicButton);

		mediumButtons = new JPanel();
		mediumButtons.add(bookButton);
		mediumButtons.add(comicButton);

		add("tab", mediumButtons);

		// year
		add("br", new JLabel("Year"));
		yearField = new JTextField();
		add("tab hfill", yearField);

		// series
		add("br", new JLabel("Series"));
		seriesField = new JTextField();
		add("tab hfill", seriesField);

		// issue
		add("br", new JLabel("Issue"));
		issueField = new JTextField();
		add("tab hfill", issueField);

		// signed
		add("br", new JLabel("Signed?"));

		notSignedButton = new JRadioButton("Not Signed");
		notSignedButton.setActionCommand("no");
		notSignedButton.setSelected(true);
		signedButton = new JRadioButton("Signed");
		signedButton.setActionCommand("yes");

		signedGroup = new ButtonGroup();
		signedGroup.add(notSignedButton);
		signedGroup.add(signedButton);

		signedButtons = new JPanel();
		signedButtons.add(notSignedButton);
		signedButtons.add(signedButton);

		add("tab", signedButtons);

		// button
		addBookButton = new JButton("Add Book");
		addBookButton.addActionListener(this);
		add("p right", addBookButton);
	}

	/**
	 * Create the GUI and show it. For thread safety, this method should be
	 * invoked from the event-dispatching thread.
	 */
	private static void createAndShowGUI() {

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

			// Create and set up the window
			JFrame frame = new JFrame("Add new Book");
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

			// Create and set up the content pane
			JComponent newContentPane = new BookshelfGUI(frame);
			newContentPane.setOpaque(true);
			frame.setContentPane(newContentPane);

			// Display the window
			frame.setPreferredSize(new Dimension(500, 410));
			frame.pack();
			frame.setVisible(true);

		} catch (Exception e) {
			System.err.println("error");
		}
	}

	public static void main(String[] args) {
		// Schedule a job for the event-dispatching thread:
		// creating and showing this application's GUI.
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGUI();
			}
		});
	}

	private LinkedList<Person> getAuthors(JTextArea textArea) {
		LinkedList<Person> authors = new LinkedList<Person>();
		String[] strAuthors = textArea.getText().split("\n");
		Pattern lastNamePattern = Pattern.compile("\\w+$");
		for (String strAuthor : strAuthors) {
			Matcher matcher = lastNamePattern.matcher(strAuthor);
			if (matcher.find()) {
				String firstName;
				// ignore whitespace between first and last
				if (matcher.start() > 0) {
					firstName = strAuthor.substring(0, matcher.start() - 1);
				} else {
					firstName = "";
				}
				String lastName = matcher.group();
				authors.add(new Person(firstName, lastName));
			}
		}
		return authors;
	}

	public void actionPerformed(ActionEvent e) {
		if (titleField.getText().compareTo("") == 0) {
			// no title entered
			JOptionPane.showMessageDialog(null, "Enter a title.", "Error",
					JOptionPane.ERROR_MESSAGE);

		} else if (authorsTextArea.getText().compareTo("") == 0) {
			// no author or editor entered
			JOptionPane.showMessageDialog(null,
					"Enter an author or an editor.", "Error",
					JOptionPane.ERROR_MESSAGE);

		} else {
			// everything checks out

			// read in title, type, and medium
			Book book = new Book(titleField.getText(), typeGroup.getSelection()
					.getActionCommand(), mediumGroup.getSelection()
					.getActionCommand());

			// read in author/editor
			if (authorsTextArea.getText().compareTo("") != 0) {
				// read in authors/editors
				LinkedList<Person> authors = getAuthors(authorsTextArea);

				if (authorTypeLabel.getText().compareTo("Authors") == 0) {
					// authors case
					book.setAuthors(authors);
				} else {
					// editors case
					book.setEditors(authors);
				}
			}

			// read in year
			if (yearField.getText() != "") {
				book.setYear(yearField.getText());
			}

			// read in series
			if (seriesField.getText() != "") {
				book.setSeries(seriesField.getText());
			}

			// read in issue
			if (issueField.getText() != "") {
				book.setIssue(issueField.getText());
			}

			// read in signed
			if (signedGroup.getSelection().getActionCommand().compareTo("yes") == 0) {
				book.setSigned("signed");
			}

			// add the book to the Document and rewrite the file
			shelf.add(book);

			// close program
			frame.dispose();
			frame.setVisible(false);
		}
	}

	private class AuthorAdder implements ActionListener {
		private boolean empty;

		public AuthorAdder() {
			empty = true;
		}

		public void actionPerformed(ActionEvent e) {
			if (empty) {
				empty = false;
			} else {
				authorsTextArea.append("\n");
			}
			authorField.setText("");
			authorsTextArea.append(e.getActionCommand());
		}
	}

	private class AuthorTypeChooser implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			authorTypeLabel.setText(e.getActionCommand());
		}
	}
}