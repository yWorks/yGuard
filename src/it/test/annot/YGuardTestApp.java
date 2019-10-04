package test.annot;

public class YGuardTestApp {
	
	public static void main(String[] args) {
		System.out.println("Hello World:" + doSomethingImportant());

	}
	
	public static @VeryImportantAnnotation String doSomethingImportant() {
		return "This is important!";
	}
}
