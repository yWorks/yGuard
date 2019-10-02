package test.internalclasses;


class InternalClassInB {

    public void internalClassMethodInB() {
        System.out.println("internalClassMethodInB");
        new InternalClassInA().internalClassMethodInA();
    }

}
