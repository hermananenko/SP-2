import java.io.*;
import java.nio.charset.Charset;

public class FileWorker {
    public static String read(String path) {
        try {
            InputStream inputStream = new FileInputStream(new File(System.getProperty("user.dir") + path));
            byte[] bytes = inputStream.readAllBytes();
            inputStream.close();
            return new String(bytes, Charset.defaultCharset());
        } catch (IOException e) {
            return "error";
        }
    }

    public static void write(String path, String text) {

        try {
            OutputStream outputStream = new FileOutputStream(new File(path));
            outputStream.write(text.getBytes());
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
