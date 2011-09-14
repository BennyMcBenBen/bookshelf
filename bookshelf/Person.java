package bookshelf;
public class Person {
	private String first, last;

	public Person(String firstName, String lastName) {
		first = firstName;
		last = lastName;
	}

	public String getFirst() {
		return first;
	}

	public String getLast() {
		return last;
	}
	
	public String toString() {
		return new String(first + " " + last);
	}
}