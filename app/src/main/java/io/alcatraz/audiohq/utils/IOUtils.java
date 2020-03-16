package io.alcatraz.audiohq.utils;

import okio.*;

import java.io.*;

public class IOUtils {
    public static String Okioread(String dir) {
        Source source = null;
        BufferedSource bufferedSource = null;
        try {
            File file = new File(dir);
            source = Okio.source(file);
            bufferedSource = Okio.buffer(source);
            String content = bufferedSource.readUtf8();
            return content;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeQuietly(bufferedSource);
        }
        return "Failed to Read:" + dir;
    }

    public static void Okiowrite(String dir, String content) {
        Sink sink = null;
        BufferedSink bufferedSink = null;
        try {
            File dest = new File(dir);
            sink = Okio.sink(dest);
            bufferedSink = Okio.buffer(sink);
            bufferedSink.writeUtf8(content);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
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
        String content = null;
        try {
            FileInputStream fis = new FileInputStream(file);
            if (fis != null) {
                InputStreamReader reader = new InputStreamReader(fis);
                BufferedReader buffreader = new BufferedReader(reader);
                while ((line = buffreader.readLine()) != null) {
                    content = content + "\n";
                    content += line;
                }
                fis.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return content;
    }

    public static interface ReadMonitor {
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