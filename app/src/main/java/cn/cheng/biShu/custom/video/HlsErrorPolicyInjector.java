package cn.cheng.biShu.custom.video;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.annotation.OptIn;
import androidx.media3.common.MediaItem;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.datasource.DataSink;
import androidx.media3.datasource.DataSource;
import androidx.media3.datasource.TransferListener;
import androidx.media3.exoplayer.drm.DrmSessionManagerProvider;
import androidx.media3.exoplayer.hls.HlsMediaSource;
import androidx.media3.exoplayer.source.MediaSource;
import androidx.media3.exoplayer.upstream.LoadErrorHandlingPolicy;

import java.io.File;
import java.util.Map;

import tv.danmaku.ijk.media.exo2.ExoMediaSourceInterceptListener;
import tv.danmaku.ijk.media.exo2.ExoSourceManager;

@UnstableApi public class HlsErrorPolicyInjector {

    private final SkipCorruptedSegmentPolicy errorPolicy;

    public HlsErrorPolicyInjector(SkipCorruptedSegmentPolicy errorPolicy) {
        this.errorPolicy = errorPolicy;
    }

    public void install(Context context) {
        ExoSourceManager.setExoMediaSourceInterceptListener(new ExoMediaSourceInterceptListener() {
            @OptIn(markerClass = UnstableApi.class) @Nullable
            @Override
            public MediaSource getMediaSource(String dataSource, boolean preview, boolean cacheEnable, boolean isLooping, File cacheDir) {
                // ✅ 仅处理 HLS 流
                if (dataSource == null || !dataSource.toLowerCase().endsWith(".m3u8")) {
                    return null;
                }

                // 1. 获取 GSY 默认的 DataSourceFactory
                // Context context, boolean preview, String uerAgent, Map<String, String> mapHeadData
                DataSource.Factory dataSourceFactory = ExoSourceManager.getDataSourceFactory(
                        context, preview, null, null
                );

                // 2. ⚠️ 关键修正：不再继承 Factory，而是直接构造 HlsMediaSource
                return new MediaSource.Factory() {
                    @Override
                    public MediaSource.Factory setDrmSessionManagerProvider(DrmSessionManagerProvider drmSessionManagerProvider) {
                        return null;
                    }

                    @Override
                    public MediaSource.Factory setLoadErrorHandlingPolicy(LoadErrorHandlingPolicy loadErrorHandlingPolicy) {
                        return null;
                    }

                    @Override
                    public int[] getSupportedTypes() {
                        return new int[0];
                    }

                    @Override
                    public MediaSource createMediaSource(MediaItem mediaItem) {
                        // 3. 通过 HlsMediaSource 的隐藏构造参数注入策略
                        return new HlsMediaSource.Factory(dataSourceFactory)
                                .setLoadErrorHandlingPolicy(errorPolicy) // ✅ 合法！
                                .createMediaSource(mediaItem);
                    }
                }.createMediaSource(new MediaItem.Builder().setUri(dataSource).build());
            }

            @OptIn(markerClass = UnstableApi.class) @Nullable
            @Override
            public DataSource.Factory getHttpDataSourceFactory(String userAgent, @Nullable TransferListener listener, int connectTimeoutMillis, int readTimeoutMillis, Map<String, String> mapHeadData, boolean allowCrossProtocolRedirects) {
                return null;
            }

            @OptIn(markerClass = UnstableApi.class) @Nullable
            @Override
            public DataSink.Factory cacheWriteDataSinkFactory(String CachePath, String url) {
                return null;
            }
        });
    }
}