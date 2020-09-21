package com.yworks.yshrink.core;

import com.yworks.common.ShrinkBag;
import com.yworks.yshrink.model.Model;

import java.io.IOException;

public interface ArchiveWriter {
  void write( Model model, ShrinkBag bag ) throws IOException;
}
