package com.yworks.yguard.obf;

import com.yworks.yguard.obf.classfile.ClassFile;

import java.util.Objects;

public class ClassKey {
  private final String multiReleasePrefixOrEmpty;
  private final String className;

  public ClassKey( String entryName, ClassFile classFile ) {
    if (entryName.startsWith(GuardDB.SIGNATURE_MULTI_RELEASE_PREFIX)) {
      int indexOfPrefixTail = entryName.indexOf("/", GuardDB.SIGNATURE_MULTI_RELEASE_PREFIX.length());
      multiReleasePrefixOrEmpty = entryName.substring(0, indexOfPrefixTail);
    } else {
      multiReleasePrefixOrEmpty = "";
    }
    className = classFile.getName();
  }

  public String fullName() {
    if (multiReleasePrefixOrEmpty.isEmpty()) {
      return className;
    } else {
      return this.multiReleasePrefixOrEmpty + "/" + this.className;
    }
  }

  @Override
  public boolean equals( final Object o ) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ClassKey that = (ClassKey) o;
    return Objects.equals(multiReleasePrefixOrEmpty, that.multiReleasePrefixOrEmpty) && Objects.equals(className, that.className);
  }

  @Override
  public int hashCode() {
    return Objects.hash(multiReleasePrefixOrEmpty, className);
  }
}
