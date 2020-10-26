package com.yworks.yshrink.core;

import com.yworks.common.ShrinkBag;
import com.yworks.yshrink.model.Model;

import java.io.IOException;

/**
 * The interface Archive writer.
 */
public interface ArchiveWriter {
    /**
     * Write.
     *
     * @param model the model
     * @param bag   the bag
     * @throws IOException the io exception
     */
    void write( Model model, ShrinkBag bag ) throws IOException;
}
