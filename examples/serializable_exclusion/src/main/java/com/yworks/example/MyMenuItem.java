package com.yworks.example;

import java.io.Serializable;

class MyMenuItem implements Serializable  {
    private static final long serialVersionUID = 1234L;

    public String label = null;
    public String content = null;
    private long width = 0;
    private long height = 0;
}
