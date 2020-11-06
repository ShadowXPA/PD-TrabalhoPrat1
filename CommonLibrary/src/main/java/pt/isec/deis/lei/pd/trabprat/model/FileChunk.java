package pt.isec.deis.lei.pd.trabprat.model;

import java.io.Serializable;
import java.util.UUID;

public class FileChunk implements Serializable {

    private final byte[] FilePart;
    private final int Offset;
    private final int Length;
    private final String Username;
    private final UUID GUID;

    public byte[] getFilePart() {
        return FilePart;
    }

    public int getOffset() {
        return Offset;
    }

    public int getLength() {
        return Length;
    }

    public String getUsername() {
        return Username;
    }

    public UUID getGUID() {
        return GUID;
    }

    public FileChunk(byte[] FilePart, int Offset, int Length, String Username, UUID GUID) {
        this.FilePart = FilePart;
        this.Offset = Offset;
        this.Length = Length;
        this.Username = Username;
        this.GUID = GUID;
    }
}
