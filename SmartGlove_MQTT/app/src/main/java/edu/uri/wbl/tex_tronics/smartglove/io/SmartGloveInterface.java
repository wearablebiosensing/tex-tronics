package edu.uri.wbl.tex_tronics.smartglove.io;

/**
 * Created by Andrew on 7/20/17.
 */

public interface SmartGloveInterface
{
    /**
     * Interface options for graphing
     */
    interface Graph
    {
        int READY = 1;
        int READY2 = 2;
    }

    /**
     * Interface options for Shared Preferences
     */
    interface Preferences
    {
        String NAME = "name";
        String GLOVE = "glove_address";
        String SOCK = "sock_address";
    }
}
