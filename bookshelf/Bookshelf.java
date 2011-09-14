package bookshelf;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

public class Bookshelf {

	private String xmlFileName;
	private File xmlFile;
	private Document document;
	private Element shelfNode;

	public Bookshelf() {
		try {
			// parse an XML document into a DOM tree
			xmlFileName = new String("bookshelf.xml");
			xmlFile = new File(xmlFileName);
			document = XmlUtility.parse(xmlFile);
			shelfNode = document.getDocumentElement();
			
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private Element getPersonNode(Document doc, Element personNode,
			Person person) {

		// first name is optional
		if (person.getFirst().compareTo("") != 0) {
			Element firstNode = doc.createElement("first");
			firstNode.setTextContent(person.getFirst());
			personNode.appendChild(firstNode);
		}

		// last name
		Element lastNode = doc.createElement("last");
		lastNode.setTextContent(person.getLast());
		personNode.appendChild(lastNode);

		return personNode;
	}

	private Element getBookNode(Document doc, Book book) {
		Element bookNode = doc.createElement("book");

		// type attribute
		bookNode.setAttribute("type", book.getType());

		// medium attribute
		bookNode.setAttribute("medium", book.getMedium());

		// signed attribute
		if (book.getSigned().compareTo("") != 0) {
			bookNode.setAttribute("signed", book.getSigned());
		}

		// title element
		Element titleNode = doc.createElement("title");
		titleNode.setTextContent(book.getTitle());
		bookNode.appendChild(titleNode);

		// series element
		if (book.getSeries().compareTo("") != 0) {
			Element seriesNode = doc.createElement("series");
			seriesNode.setTextContent(book.getSeries());
			bookNode.appendChild(seriesNode);
		}

		// issue element
		if (book.getIssue().compareTo("") != 0) {
			Element issueNode = doc.createElement("issue");
			issueNode.setTextContent(book.getIssue());
			bookNode.appendChild(issueNode);
		}

		// authors element
		if (!(book.getAuthors() == null && book.getEditors() == null)) {
			Element authorsNode = doc.createElement("authors");

			// either has authors or editors
			if (book.getAuthors() != null) {
				// has authors
				for (Person author : book.getAuthors()) {
					Element authorNode = doc.createElement("author");
					authorNode = getPersonNode(doc, authorNode, author);
					authorsNode.appendChild(authorNode);
				}
			} else {
				// has editors
				for (Person editor : book.getEditors()) {
					Element editorNode = doc.createElement("editor");
					editorNode = getPersonNode(doc, editorNode, editor);
					authorsNode.appendChild(editorNode);
				}
			}
			bookNode.appendChild(authorsNode);
		}

		// either has a year element or contains other books
		if (book.getYear().compareTo("") != 0) {
			// has year element
			Element yearNode = doc.createElement("year");
			yearNode.setTextContent(book.getYear());
			bookNode.appendChild(yearNode);
		} else {
			// has contains element
			Element containsNode = doc.createElement("contains");

			for (Book subbook : book.getContains()) {
				// add each book
				Element subbookNode = getBookNode(doc, subbook);
				containsNode.appendChild(subbookNode);
			}

			bookNode.appendChild(containsNode);
		}

		return bookNode;
	}

	public void add(Book book) {
		try {
			// add the book to the shelf
			Element bookNode = getBookNode(document, book);
			
			shelfNode.appendChild(document.createTextNode("  "));
			shelfNode.appendChild(bookNode);
			shelfNode.appendChild(document.createTextNode("\n\n"));

			// write changes to XML file
			XmlUtility.write(document, xmlFile);
			
			// transform XML to HTML with XSLT
			XmlUtility.write(xmlFile, new File("bookshelf.xsl.xml"), new File("bookshelf.html"));

		} catch (TransformerException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

	}
	
	public void updateHTML() {
		try {
			XmlUtility.write(xmlFile, new File("bookshelf.xsl.xml"), new File("bookshelf.html"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		}
	}

/*	public static void main(String[] args) {
		Bookshelf shelf = new Bookshelf();
		shelf.updateHTML();
	}*/
}
