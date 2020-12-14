package pt.isec.deis.lei.pd.trabprat.server.explorer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public final class ExplorerController {

    private static final Object Lock = new Object();

    private ExplorerController() {
    }

    public static final String BASE_DIR = "_files/";
    public static final String AVATAR_SUBDIR = "avatar";
    public static final String FILES_SUBDIR = "files";

    public static void CreateBaseDirectories(String DBName) {
        String BaseDir = DBName + BASE_DIR;
        CreateDirectory(BaseDir);
        CreateDirectory(BaseDir + AVATAR_SUBDIR);
        CreateDirectory(BaseDir + FILES_SUBDIR);
    }

    public static void CreateUserDirectory(String DBName, String Username) {
        String BaseDir = DBName + BASE_DIR;
        CreateDirectory(BaseDir + FILES_SUBDIR + "/" + Username);
    }

    private static void CreateDirectory(String Path) {
        File Dir = new File(Path);
        if (!(Dir.isDirectory() && Dir.exists())) {
            Dir.mkdir();
        }
    }

    public static void Touch(String DBName, String SubDir, String FileName) throws IOException, FileNotFoundException, InterruptedException {
        String BaseDir = DBName + BASE_DIR;
        _WriteFile(BaseDir + SubDir + "/" + FileName, null, 0, 0);
    }

    public static void WriteFile(String DBName, String SubDir, String FileName, byte[] Bytes, long Offset, int Length) throws FileNotFoundException, IOException, InterruptedException {
        String BaseDir = DBName + BASE_DIR;
        _WriteFile(BaseDir + SubDir + "/" + FileName, Bytes, Offset, Length);
    }

    private static void _WriteFile(String Path, byte[] Bytes, long Offset, int Length) throws FileNotFoundException, IOException, InterruptedException {
        File file = new File(Path);
        if (!file.exists()) {
            file.createNewFile();
        }
        try {
            if (Length > 0) {
                synchronized (Lock) {
                    FileOutputStream f = new FileOutputStream(file, true);
                    while (f.getChannel().position() < Offset) {
                        f.close();
                        Lock.wait(10);
                        f = new FileOutputStream(file, true);
                    }
                    f.write(Bytes, 0, Length);
                    f.flush();
                    f.close();
                }
            }
        } catch (Exception ex) {
        }
    }

    public static byte[] ReadFile(String DBName, String SubDir, String FileName, long Offset, int Length) throws IOException {
        String BaseDir = DBName + BASE_DIR;
        return _ReadFile(BaseDir + SubDir + "/" + FileName, Offset, Length);
    }

    public synchronized static byte[] _ReadFile(String Path, long Offset, int Length) throws IOException {
        byte[] buffer = new byte[Length];
        try ( FileInputStream f = new FileInputStream(Path)) {
            f.skipNBytes(Offset);
            int read = f.readNBytes(buffer, 0, Length);
            byte[] temp = new byte[read];
            System.arraycopy(buffer, 0, temp, 0, read);
            return temp;
        }
    }
}
