package bookshelf;
import java.util.LinkedList;

public class Book {
	private String type;

	private String signed;

	private String medium;

	private String title;

	private String series;

	private String issue;

	private LinkedList<Person> authors;

	private LinkedList<Person> editors;

	private String year;

	private LinkedList<Book> contains;

	public Book(String title, String type, String medium) {
		this.title = title;
		this.type = type;
		this.medium = medium;
		signed = "";
		year = "";
		series = "";
		issue = "";
		authors = null;
		editors = null;
		contains = null;
	}
	
	public LinkedList<Person> getAuthors() {
		return authors;
	}

	public void setAuthors(LinkedList<Person> authors) {
		this.authors = authors;
	}

	public LinkedList<Book> getContains() {
		return contains;
	}

	public void setContains(LinkedList<Book> contains) {
		this.contains = contains;
	}

	public LinkedList<Person> getEditors() {
		return editors;
	}

	public void setEditors(LinkedList<Person> editors) {
		this.editors = editors;
	}

	public String getIssue() {
		return issue;
	}

	public void setIssue(String issue) {
		this.issue = issue;
	}

	public String getMedium() {
		return medium;
	}

	public void setMedium(String medium) {
		this.medium = medium;
	}

	public String getSeries() {
		return series;
	}

	public void setSeries(String series) {
		this.series = series;
	}

	public String getSigned() {
		return signed;
	}

	public void setSigned(String signed) {
		this.signed = signed;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public String toString() {
		return new String(title + " by " + authors.getFirst() + " (" + year + ")");
	}
}
