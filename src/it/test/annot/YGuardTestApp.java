package test.annot;

/**
 * The type Y guard test app.
 */
public class YGuardTestApp {

    /**
     * The entry point of application.
     *
     * @param args the input arguments
     */
    public static void main(String[] args) {
		System.out.println("Hello World:" + doSomethingImportant());

	}

    /**
     * Do something important string.
     *
     * @return the string
     */
    public static @VeryImportantAnnotation String doSomethingImportant() {
		return "This is important!";
	}
}
