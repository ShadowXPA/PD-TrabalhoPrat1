package pt.isec.deis.lei.pd.trabprat.model;

import java.io.Serializable;
import java.util.UUID;

public class FileChunk implements Serializable {

    private final byte[] FilePart;
    private final String Username;
    private final UUID GUID;

    public byte[] getFilePart() {
        return FilePart;
    }

    public String getUsername() {
        return Username;
    }

    public UUID getGUID() {
        return GUID;
    }

    public FileChunk(byte[] FilePart, String Username, UUID GUID) {
        this.FilePart = FilePart;
        this.Username = Username;
        this.GUID = GUID;
    }
}
