package test.annot;

import java.util.List;

/**
 * The type Classs.
 */
@TestAnnotation(value = "Class Annot Value")
public class Classs {

    /**
     * The A list.
     */
    @MyNotNull List<String> aList;
    /**
     * The Another list.
     */
    List<@MyNotNull String> anotherList;

    /**
     * The constant constantString.
     */
    public static final String constantString = "bla";

    /**
     * Foo.
     */
    @TestAnnotation(value = "asdf")
  public void foo(){
    
    Object instance = new @MyNotNull Classs();
    
  }
  
  private void doStuff(@MyNotNull String... stuffThings) {
    
  }

    /**
     * Foo 2.
     */
    @TestAnnotation()
  public void foo2(){}

    /**
     * Foo inv.
     */
    @TestInvAnnotation()
  public void fooInv(){}

    /**
     * Foo 2 inv.
     */
    @TestInvAnnotation(invValue = "jkloe")
  public void foo2Inv(){}

    /**
     * Krass.
     */
    @NestingTestAnnotation(value = @TestAnnotation(value = "overwritten"))
  public void krass(){}

    /**
     * Krass 2.
     */
    @NestingTestAnnotation(value = @TestAnnotation())
  public void krass2(){}

    /**
     * Krass 3.
     */
    @NestingTestAnnotation()
  public void krass3(){}


    /**
     * Annot with value.
     */
    @RequestForEnhancement(id = 23, synopsis = "theSynopsis" )
  public void annotWithValue() {}

    /**
     * Param annot test.
     *
     * @param param1 the param 1
     * @param param2 the param 2
     */
    public void paramAnnotTest( @ParameterAnnotation(paramAnnotFoo = "paramAnnotFooValue",paramAnnotBaz = "paramAnnotBazValue") String param1,
                              @ParameterAnnotation(paramAnnotFoo = "paramAnnotFooValue2") String param2 ) {}

    /**
     * Invisible param annot test.
     *
     * @param param the param
     */
    public void invisibleParamAnnotTest(@TestInvParameterAnnotation(value = "TestInvParameterAnnotationValue")String param) {}

    /**
     * Test type annotation.
     *
     * @param o the o
     */
    public void testTypeAnnotation(@MyNotNull Object o) {
    
  }

    /**
     * The entry point of application.
     *
     * @param args the input arguments
     * @throws NoSuchMethodException the no such method exception
     */
    public static void main(String[] args) throws NoSuchMethodException {

//    System.out.println("-- Method Annotations --");
//
    //System.out.println(" = " + Classs.class.getMethod("foo", new Class[0]).getAnnotation(TestAnnotation.class));
    //System.out.println(" = " + Classs.class.getMethod("foo2", new Class[0]).getAnnotation(TestAnnotation.class));
    //System.out.println(" = " + Classs.class.getMethod("foo", new Class[0]).getAnnotation(TestAnnotation.class).unusedValue());
//    System.out.println(
//        " = " + Classs.class.getMethod("foo2", new Class[0]).getAnnotation(TestAnnotation.class).value());
//    System.out.println(" = " + Classs.class.getMethod("annotWithValue", new Class[0]).getAnnotation(RequestForEnhancement.class).id());
//    System.out.println(
//        " = " + Classs.class.getMethod("annotWithValue", new Class[0]).getAnnotation(RequestForEnhancement.class).engineer());
//
//    // Parameter Annotations
//    Annotation[][] parameterAnnotations = Classs.class.getMethod("paramAnnotTest",
//        String.class, String.class ).getParameterAnnotations();
//    Annotation param01Annot = parameterAnnotations[0][0];
//    Annotation param02Annot = parameterAnnotations[1][0];
//
//    System.out.println("\n-- Parameter Annotations --");
//
//    System.out.println(
//        " = " + ((ParameterAnnotation) param01Annot).paramAnnotFoo());
//    System.out.println(
//        " = " + ((ParameterAnnotation) param01Annot).paramAnnotBaz());
//    System.out.println(
//        " = " + ((ParameterAnnotation) param02Annot).paramAnnotFoo());
//    System.out.println(
//        " = " + ((ParameterAnnotation) param02Annot).paramAnnotBaz());
//
//    // Package Annot
//    System.out.println(
//        " = " + Classs.class.getPackage().getAnnotation(TestAnnotation.class).value());
//
//    // Class Annot
//    System.out.println(
//        " = " + Classs.class.getAnnotation(TestAnnotation.class).value());
  }
}
