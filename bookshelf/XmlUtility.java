package bookshelf;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PipedReader;
import java.io.PipedWriter;
import java.io.Reader;
import java.net.URL;
import java.net.URLConnection;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Wrapper class for XML API validation and parsing methods.
 * 
 */
public class XmlUtility {

	// --- Private Fields ---

	private static DocumentBuilder db = null;

	private static final DocumentBuilderFactory dbFactory;

	private static final DefaultHandler defaultHandler;

	private static final SAXParserFactory saxFactory;

	private static SchemaFactory schemaFactory;

	private static final TransformerFactory tFactory;

	private static Transformer transformer = null;

	static {
		defaultHandler = new DefaultHandler();

		dbFactory = DocumentBuilderFactory.newInstance();
		dbFactory.setNamespaceAware(true); // required for Java 6

		saxFactory = SAXParserFactory.newInstance();
		saxFactory.setValidating(true);

		tFactory = TransformerFactory.newInstance();
		tFactory.setAttribute("indent-number", 2);

		schemaFactory = SchemaFactory
				.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
	}

	// --- Constructor ---

	private XmlUtility() {
	}

	/**
	 * Gets a DocumentBuilder from the DocumentBuilderFactory
	 * 
	 * @return DocumentBuilder
	 * @throws ParserConfigurationException
	 */
	public static DocumentBuilder getDocumentBuilder()
			throws ParserConfigurationException {
		if (db == null) {
			db = dbFactory.newDocumentBuilder();
		}

		return db;
	}

	// --- Public Methods ---

	/**
	 * Gets a Transformer from the TransformerFactory
	 * 
	 * @return Transformer
	 * @throws TransformerConfigurationException
	 */
	public static Transformer getTransformer()
			throws TransformerConfigurationException {
		if (transformer == null) {
			transformer = tFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		}

		return transformer;
	}

	/**
	 * Checks if the File is valid XML (does not check it against a referenced
	 * schema)
	 */
	public static boolean isValidXml(final File file) {
		try {
			saxFactory.newSAXParser().parse(file, defaultHandler);
		} catch (Exception e) {
			return false;
		}

		return true;
	}

	public static Document parse(final File file)
			throws ParserConfigurationException, SAXException, IOException {
		return getDocumentBuilder().parse(file);
	}

	public static Document parse(final InputStream inputStream)
			throws ParserConfigurationException, SAXException, IOException {
		return getDocumentBuilder().parse(inputStream);
	}

	public static Reader read(final Document dom) throws IOException,
			TransformerException {
		final PipedWriter pw = new PipedWriter();
		final PipedReader pr = new PipedReader(pw);

		final DOMSource source = new DOMSource(dom);
		final StreamResult result = new StreamResult(pw);

		final Runnable runnable = new Runnable() {
			public void run() {
				try {
					getTransformer().transform(source, result);
					pw.close();
				} catch (final Exception e) {
					throw new RuntimeException(e);
				}
			}
		};

		new Thread(runnable).start();

		return pr;
	}

	public static InputStream stream(final Document dom) throws IOException,
			TransformerException {
		final PipedOutputStream pos = new PipedOutputStream();
		final PipedInputStream pis = new PipedInputStream(pos);

		final DOMSource source = new DOMSource(dom);
		final StreamResult result = new StreamResult(pos);

		final Runnable runnable = new Runnable() {
			public void run() {
				try {
					getTransformer().transform(source, result);
					pos.close();
				} catch (final Exception e) {
					throw new RuntimeException(e);
				}
			}
		};

		new Thread(runnable).start();

		return pis;
	}

	/**
	 * Prints an XML DOM to System.out
	 * 
	 * @param dom
	 * @throws TransformerException
	 */
	public static void print(final Document dom) throws TransformerException {
		final DOMSource source = new DOMSource(dom);
		final StreamResult result = new StreamResult(new OutputStreamWriter(
				System.out));

		getTransformer().transform(source, result);
	}

	/**
	 * Writes an XML DOM to a given File
	 * 
	 * @param dom
	 * @param outFile
	 * @throws TransformerException
	 * @throws FileNotFoundException
	 */
	public static void write(final Document dom, final File outFile)
			throws TransformerException, FileNotFoundException {
		final DOMSource source = new DOMSource(dom);
		final StreamResult result = new StreamResult(new OutputStreamWriter(
				new FileOutputStream(outFile)));

		getTransformer().transform(source, result);
	}

	/**
	 * Writes an XML DOM to a given OutputStream
	 * 
	 * @param dom
	 * @param oStream
	 * @throws TransformerException
	 */
	public static void write(final Document dom, final OutputStream oStream)
			throws TransformerException {
		final DOMSource source = new DOMSource(dom);
		final StreamResult result = new StreamResult(new OutputStreamWriter(
				oStream));

		getTransformer().transform(source, result);
	}

	/**
	 * 
	 */
	public static void write(final File xmlFile, final File xsltFile,
			final File htmlFile) throws TransformerException,
			FileNotFoundException {
		final StreamSource source = new StreamSource(xmlFile);

		final StreamResult result = new StreamResult(new OutputStreamWriter(
				new FileOutputStream(htmlFile)));

		Transformer xslTransformer = tFactory.newTransformer(new StreamSource(
				xsltFile));
		xslTransformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
		xslTransformer.setOutputProperty(OutputKeys.INDENT, "yes");

		xslTransformer.transform(source, result);
	}

	/**
	 * Validates an XML file against an XML schema. Throws a SAXException if the
	 * XML is invalid. Else returns the XML file as a org.w3c.dom.Document
	 * 
	 * @param xmlFile
	 * @param schemaSource
	 *            XML Schema source - can be created this way: Source
	 *            schemaSource = new StreamSource(new File(schemaFileName));
	 * @return
	 * @throws SAXException
	 * @throws IOException
	 * @throws ParserConfigurationException
	 */
	public static Document validate(final File xmlFile,
			final Source schemaSource) throws SAXException, IOException,
			ParserConfigurationException {
		// parse an XML document into a DOM tree
		final Document document = parse(xmlFile);

		// load a WXS, represented by a Schema instance
		final Schema schema = schemaFactory.newSchema(schemaSource);

		// create a Validator instance
		final Validator validator = schema.newValidator();

		// validate the DOM tree
		// throws an exception if there is a problem
		validator.validate(new DOMSource(document));

		// return the valid document
		return document;
	}

	public static Document validateNoNamespace(final File xmlFile)
			throws SAXException, IOException, ParserConfigurationException {
		return validateNoNamespace(new FileInputStream(xmlFile));
	}

	public static Document validateNoNamespace(final InputStream xmlStream)
			throws SAXException, IOException, ParserConfigurationException {
		// parse an XML document into a DOM tree
		final Document document = parse(xmlStream);

		// load WXS, represented by a Schema instance
		final Element root = document.getDocumentElement();
		final String schemaFileName = root
				.getAttribute("xsi:noNamespaceSchemaLocation");
		final Source schemaSource;
		if (schemaFileName.startsWith("http://")) {
			URL url = new URL(schemaFileName);
			URLConnection connection = url.openConnection();
			InputStream schemaStream = connection.getInputStream();
			schemaSource = new StreamSource(schemaStream);
		} else {
			schemaSource = new StreamSource(new File(schemaFileName));
		}
		final Schema schema = schemaFactory.newSchema(schemaSource);

		// create a Validator instance
		final Validator validator = schema.newValidator();

		// validate the DOM tree
		// throws an exception if there is a problem
		validator.validate(new DOMSource(document));

		// return the valid document
		return document;
	}
}