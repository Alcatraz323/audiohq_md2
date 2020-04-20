package io.alcatraz.audiohq.utils;

import okio.*;

import java.io.*;

public class IOUtils {
    public static String Okioread(String dir) {
        Source source;
        BufferedSource bufferedSource = null;
        try {
            File file = new File(dir);
            source = Okio.source(file);
            bufferedSource = Okio.buffer(source);
            return bufferedSource.readUtf8();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeQuietly(bufferedSource);
        }
        return "Failed to Read:" + dir;
    }

    public static void Okiowrite(String dir, String content) {
        Sink sink;
        BufferedSink bufferedSink = null;
        try {
            File dest = new File(dir);
            sink = Okio.sink(dest);
            bufferedSink = Okio.buffer(sink);
            bufferedSink.writeUtf8(content);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeQuietly(bufferedSink);
        }
    }

    public static void write(File file, String content) {
        if (!file.exists()) {
            try {
                file.getParentFile().mkdirs();
                file.createNewFile();
            } catch (Exception e) {
            }
        }
        try {
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(content.getBytes());
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String read(File file, ReadMonitor rm) {
        String line;
        StringBuilder content = null;
        try {
            FileInputStream fis = new FileInputStream(file);
            InputStreamReader reader = new InputStreamReader(fis);
            BufferedReader buffreader = new BufferedReader(reader);
            while ((line = buffreader.readLine()) != null) {
                content = (content == null ? new StringBuilder("null") : content).append("\n");
                content.append(line);
            }
            fis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return content == null ? null : content.toString();
    }

    public interface ReadMonitor {
        void onLine(String line);

        void callFinish();
    }

    public static void closeQuietly(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (RuntimeException rethrown) {
                throw rethrown;
            } catch (Exception ignored) {

            }

        }

    }
}