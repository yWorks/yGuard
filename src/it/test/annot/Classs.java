package test.annot;

import java.util.List;

@TestAnnotation(value = "Class Annot Value")
public class Classs {

  @MyNotNull List<String> aList;
  List<@MyNotNull String> anotherList;
  
  public static final String constantString = "bla";

  @TestAnnotation(value = "asdf")
  public void foo(){
    
    Object instance = new @MyNotNull Classs();
    
  }
  
  private void doStuff(@MyNotNull String... stuffThings) {
    
  }
  
  @TestAnnotation()
  public void foo2(){}

  @TestInvAnnotation()
  public void fooInv(){}
  @TestInvAnnotation(invValue = "jkloe")
  public void foo2Inv(){}

  @NestingTestAnnotation(value = @TestAnnotation(value = "overwritten"))
  public void krass(){}

  @NestingTestAnnotation(value = @TestAnnotation())
  public void krass2(){}

  @NestingTestAnnotation()
  public void krass3(){}


  @RequestForEnhancement(id = 23, synopsis = "theSynopsis" )
  public void annotWithValue() {}

  public void paramAnnotTest( @ParameterAnnotation(paramAnnotFoo = "paramAnnotFooValue",paramAnnotBaz = "paramAnnotBazValue") String param1,
                              @ParameterAnnotation(paramAnnotFoo = "paramAnnotFooValue2") String param2 ) {}

  public void invisibleParamAnnotTest(@TestInvParameterAnnotation(value = "TestInvParameterAnnotationValue")String param) {}

  public void testTypeAnnotation(@MyNotNull Object o) {
    
  }
  
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
