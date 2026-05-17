package cn.cheng.biShu.custom.video;

import androidx.annotation.Nullable;
import androidx.media3.common.C;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.exoplayer.upstream.LoadErrorHandlingPolicy;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;

import cn.cheng.biShu.MyApplication;

@UnstableApi public class SkipCorruptedSegmentPolicy implements LoadErrorHandlingPolicy {

    SampleVideo mVideoPlayer;

    public SkipCorruptedSegmentPolicy(SampleVideo mVideoPlayer) {
        this.mVideoPlayer = mVideoPlayer;
    }

    @Override
    public int getMinimumLoadableRetryCount(int dataType) {
        // 媒体片段必须允许至少1次重试，否则播放器可能直接抛出致命错误停止播放
        return dataType == C.DATA_TYPE_MEDIA ? 1 : 3;
    }

    @Override
    public long getRetryDelayMsFor(LoadErrorInfo loadErrorInfo) {
        // 遇到媒体片段错误返回0，不重新播放
        if (loadErrorInfo.mediaLoadData.dataType == C.DATA_TYPE_MEDIA) {
            return 0;
        }
        return Math.min((loadErrorInfo.errorCount - 1) * 1000, 5000);
    }

    @Override
    @Nullable
    public FallbackSelection getFallbackSelectionFor(
            FallbackOptions fallbackOptions,
            LoadErrorInfo loadErrorInfo
    ) {
        // 核心破局点 写入备份容错视频文件
        if (loadErrorInfo.mediaLoadData.dataType == C.DATA_TYPE_MEDIA) {
            String url = loadErrorInfo.loadEventInfo.uri.toString();
            File path = new File(url.substring(0, url.lastIndexOf("/")));
            if (!path.exists()) path.mkdirs();
            try (BufferedInputStream bis = new BufferedInputStream(MyApplication.getContext().getAssets().open("default.mp4"));
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(url))) {
                int len = 0;
                byte[] buff = new byte[1024*4];
                while ((len = bis.read(buff)) != -1) {
                    bos.write(buff, 0, len);
                }
            } catch (Exception ignored) {}
        }
        return null;
    }
}