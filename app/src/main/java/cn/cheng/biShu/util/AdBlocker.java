package cn.cheng.biShu.util;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.webkit.WebResourceResponse;

import androidx.annotation.WorkerThread;

import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

/**
 * 广告过滤器
 *
 * 调用前 需要先使用init方法注册
 */
public class AdBlocker {

    private static final String AD_HOSTS_FILE = "host.txt";
    private static final Set<String> AD_HOSTS = new HashSet<>();
    private static final Set<String> AD_URL= new HashSet<>();

    public static void init(final Context context) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    loadHostInfo(context);
                } catch (IOException e) {
                    // noop
                }
                return null;
            }
        }.execute();
    }

    private static File initFromAssets(Context context) {
        String filePath = PhoneSysPath.getDownloadDir() + "/BiShu/0_like";
        File hostFile = new File(filePath + "/" + AD_HOSTS_FILE);
        if (!hostFile.exists()) { // 手机上没有 则先写入默认host文件
            File pathFile = new File(filePath);
            if (!pathFile.exists()) pathFile.mkdirs();
            try (InputStream stream = context.getAssets().open(AD_HOSTS_FILE);
                 BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream));
                 BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(hostFile))) {
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    line = CommonUtils.removeBom(line);
                    bufferedWriter.write(line);
                    bufferedWriter.newLine();
                }
            } catch (Exception ignored) {}
        }
        return hostFile;
    }

    @WorkerThread
    private static void loadHostInfo(Context context) throws IOException {
        File hostFile = initFromAssets(context);
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(hostFile))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                if (line.contains("/")) {
                    line = CommonUtils.removeBom(line);
                    AD_URL.add(line);
                } else {
                    AD_HOSTS.add(line);
                }
            }
        } catch (Exception ignored) {}
    }

    @WorkerThread
    public static String getHostInfo(Context context) {
        File hostFile = initFromAssets(context);
        StringBuffer buffer = new StringBuffer();
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(hostFile))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                line = CommonUtils.removeBom(line);
                buffer.append(line + "\n");
            }
        } catch (Exception ignored) {}
        return buffer.toString();
    }

    @WorkerThread
    public static void updateHostInfo(Context context, String text) {
        File hostFile = initFromAssets(context);
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(hostFile))) {
            bufferedWriter.write(text);
        } catch (Exception ignored) {}
        String[] lines = text.split("\n");
        if (lines.length > 0) {
            AD_URL.clear();
            AD_HOSTS.clear();
        }
        for (String line : lines) {
            if (line != null && !line.trim().replaceAll("/", "").isEmpty()) {
                if (line.contains("/")) {
                    AD_URL.add(line);
                } else {
                    AD_HOSTS.add(line);
                }
            }
        }
    }

    public static boolean isAd(String url) {
        try {
            for (String u : AD_URL) {
                if (url.toLowerCase().contains(u.toLowerCase())) {
                    return true;
                }
            }
            return isAdHost(getHost(url))||AD_HOSTS.contains(Uri.parse(url).getLastPathSegment());
        } catch (MalformedURLException e) {
            return false;
        }

    }

    private static boolean isAdHost(String host) {
        if (StringUtils.isEmpty(host)) {
            return false;
        }
        int index = host.indexOf(".");
        return index >= 0 && (AD_HOSTS.contains(host) ||
                index + 1 < host.length() && isAdHost(host.substring(index + 1)));
    }

    public static String getHost(String url) throws MalformedURLException {
        return new URL(url).getHost();
    }

    public static WebResourceResponse createEmptyResource() {
        return new WebResourceResponse("text/plain", "utf-8", new ByteArrayInputStream("".getBytes()));
    }


}
