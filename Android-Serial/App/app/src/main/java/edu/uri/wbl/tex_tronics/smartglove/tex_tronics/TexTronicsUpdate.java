package edu.uri.wbl.tex_tronics.smartglove.tex_tronics;

/**
 * Created by mcons on 2/28/2018.
 */

public enum TexTronicsUpdate {
    connected ("uri.wbl.tex_tronics.connected"),
    disconnected ("uri.wbl.tex_tronics.disconnected");

    private final String mUpdate;

    private TexTronicsUpdate(String update) {
        mUpdate = update;
    }

    public static TexTronicsUpdate getUpdate(String update) {
        switch (update) {
            case "uri.wbl.tex_tronics.connected":
                return connected;
            case "uri.wbl.tex_tronics.disconnected":
                return disconnected;
            default:
                return null;
        }
    }

    public String toString() {
        return this.mUpdate;
    }
}
