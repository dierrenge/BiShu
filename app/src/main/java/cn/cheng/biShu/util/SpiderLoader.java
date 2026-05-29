package cn.cheng.biShu.util;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.WorkerThread;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import cn.cheng.biShu.bean.SpiderBean;

/**
 * 爬虫设置数据加载
 *
 * 调用前 需要先使用init方法注册
 */
public class SpiderLoader {

    private static final String SPIDER_SET_FILE = "spiderSet.txt";
    private static List<SpiderBean> list = new ArrayList<>();

    public static void init(final Context context) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    loadSpiderSetInfo(context);
                } catch (Exception e) {
                    // noop
                }
                return null;
            }
        }.execute();
    }

    public static SpiderBean getSpiderSet(String url) {
        for (SpiderBean bean : list) {
            if (bean.isNotEmpty() && bean.equals(url)) return bean;
        }
        return null;
    }

    private static File initFromAssets(Context context) {
        String filePath = PhoneSysPath.getDownloadDir() + "/BiShu/0_like";
        File spiderSetFile = new File(filePath + "/" + SPIDER_SET_FILE);
        if (!spiderSetFile.exists()) { // 手机上没有 则先写入默认spiderSet文件
            File pathFile = new File(filePath);
            if (!pathFile.exists()) pathFile.mkdirs();
            try (InputStream stream = context.getAssets().open(SPIDER_SET_FILE);
                 BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream));
                 BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(spiderSetFile))) {
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    bufferedWriter.write(line);
                    bufferedWriter.newLine();
                }
            } catch (Exception ignored) {}
        }
        return spiderSetFile;
    }

    @WorkerThread
    private static void loadSpiderSetInfo(Context context) {
        list.clear();
        File spiderSetFile = initFromAssets(context);
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(spiderSetFile))) {
            String line;
            int num = 0;
            while ((line = bufferedReader.readLine()) != null) {
                if (line.trim().startsWith("https:") || line.trim().startsWith("http:")) {
                    num = 1;
                    SpiderBean spiderBean = new SpiderBean();
                    list.add(spiderBean);
                }
                if (!line.trim().isEmpty()) {
                    if (num == 1) {
                        list.get(list.size() - 1).setUrl(line);
                    } else if (num == 2) {
                        list.get(list.size() - 1).setTitle(line);
                    } else if (num == 3) {
                        list.get(list.size() - 1).setChapter(line);
                    } else if (num == 4) {
                        list.get(list.size() - 1).setTxtContent(line);
                    } else if (num == 5) {
                        list.get(list.size() - 1).setFilter(line);
                    }
                }
                num++;
            }
        } catch (Exception ignored) {}
    }

    @WorkerThread
    public static String getSpiderSetInfo(Context context) {
        File spiderSetFile = initFromAssets(context);
        StringBuffer buffer = new StringBuffer();
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(spiderSetFile))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                buffer.append(line + "\n");
            }
        } catch (Exception ignored) {}
        return buffer.toString();
    }

    @WorkerThread
    public static void updateSpiderSetInfo(Context context, String text) {
        File spiderSetFile = initFromAssets(context);
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(spiderSetFile))) {
            bufferedWriter.write(text);
        } catch (Exception ignored) {}
        loadSpiderSetInfo(context);
    }

}
