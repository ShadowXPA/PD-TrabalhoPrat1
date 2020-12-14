package pt.isec.deis.lei.pd.trabprat.model;

import java.io.Serializable;
import java.util.UUID;

public class FileChunk implements Serializable {

    private final byte[] FilePart;
    private final long Offset;
    private final int Length;
    private final String Username;
    private final UUID GUID;
    private final String Extension;

    public byte[] getFilePart() {
        return FilePart;
    }

    public long getOffset() {
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

    public String getExtension() {
        return Extension;
    }

    @Override
    public String toString() {
        return "FileChunk{" + "Offset=" + Offset + ", Length=" + Length + ", Username=" + Username + ", GUID=" + GUID + ", Extension=" + Extension + '}';
    }

    public FileChunk(byte[] FilePart, long Offset, int Length, String Username, UUID GUID, String Extension) {
        this.FilePart = FilePart;
        this.Offset = Offset;
        this.Length = Length;
        this.Username = Username;
        this.GUID = GUID;
        this.Extension = Extension;
    }
}
