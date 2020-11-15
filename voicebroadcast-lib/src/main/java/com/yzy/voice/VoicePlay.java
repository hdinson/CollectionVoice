package com.yzy.voice;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.media.PlaybackParams;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;
import android.widget.Toast;

import com.yzy.voice.constant.VoiceConstants;
import com.yzy.voice.util.FileUtils;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static android.content.ContentValues.TAG;

/**
 * @author 志尧
 * @date on 2018-01-12 15:09
 * @email 1417337180@qq.com
 * @describe 音频播放
 * @ideas
 */

public class VoicePlay {

    private ExecutorService mExecutorService;
    private Context mContext;
    private TextToSpeech mSpeech;

    private VoicePlay(Context context) {
        this.mContext = context;
        this.mExecutorService = Executors.newCachedThreadPool();
        mSpeech = new TextToSpeech(context, status -> {
            if (status != TextToSpeech.SUCCESS) {
                Toast.makeText(context, "TTS引擎初始化失败", Toast.LENGTH_SHORT).show();
            }
        });
        mSpeech.setLanguage(Locale.CHINESE);
        mSpeech.setSpeechRate(1.2f);
        mSpeech.setPitch(1.0f);
    }

    private volatile static VoicePlay mVoicePlay = null;

    /**
     * 单例
     *
     * @return
     */
    public static VoicePlay with(Context context) {
        if (mVoicePlay == null) {
            synchronized (VoicePlay.class) {
                if (mVoicePlay == null) {
                    mVoicePlay = new VoicePlay(context);
                }
            }
        }
        return mVoicePlay;
    }

    /**
     * 默认收款成功样式
     *
     * @param money
     */
    public void playCompose(String money) {
        playCompose(money, false);
    }

    /**
     * 设置播报数字
     *
     * @param money
     * @param checkNum
     */
    public void playCompose(String money, boolean checkNum) {
        VoiceBuilder voiceBuilder = new VoiceBuilder.Builder()
                .start(VoiceConstants.SUCCESS)
                .money(money)
                .unit(VoiceConstants.YUAN)
                .checkNum(checkNum)
                .builder();
        executeStart(voiceBuilder);
    }

    public void playTTs(String text) {
        Log.e(TAG, "playTTs: "+text );
        mExecutorService.execute(() -> {
            mSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, "id");
            mSpeech.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                @Override
                public void onStart(String utteranceId) {
                    Log.e(TAG, "onStart: " + utteranceId);
                }

                @Override
                public void onDone(String utteranceId) {
                    Log.e(TAG, "onDone: " + utteranceId);
                }

                @Override
                public void onError(String utteranceId) {
                    Log.e(TAG, "onError: " + utteranceId);
                }
            });

        });
    }


    /**
     * 接收自定义
     *
     * @param voiceBuilder
     */
    public void playCompose(VoiceBuilder voiceBuilder) {
        executeStart(voiceBuilder);
    }

    /**
     * 开启线程
     *
     * @param builder
     */
    private void executeStart(VoiceBuilder builder) {
        List<String> voicePlay = VoiceTextTemplate.genVoiceList(builder);
        if (voicePlay.isEmpty()) {
            return;
        }

        mExecutorService.execute(() -> start(voicePlay));
    }

    /**
     * 开始播报
     *
     * @param voicePlay
     */
    private void start(final List<String> voicePlay) {
        synchronized (VoicePlay.this) {

            MediaPlayer mMediaPlayer = new MediaPlayer();

            final CountDownLatch mCountDownLatch = new CountDownLatch(1);
            AssetFileDescriptor assetFileDescription = null;

            try {
                final int[] counter = {0};
                assetFileDescription = FileUtils.getAssetFileDescription(mContext,
                        String.format(VoiceConstants.FILE_PATH, voicePlay.get(counter[0])));
                mMediaPlayer.setDataSource(
                        assetFileDescription.getFileDescriptor(),
                        assetFileDescription.getStartOffset(),
                        assetFileDescription.getLength());
                mMediaPlayer.prepareAsync();
                mMediaPlayer.setOnPreparedListener(mediaPlayer -> mMediaPlayer.start());
                mMediaPlayer.setOnCompletionListener(mediaPlayer -> {
                    mediaPlayer.reset();
                    counter[0]++;

                    if (counter[0] < voicePlay.size()) {
                        try {
                            AssetFileDescriptor fileDescription2 = FileUtils.getAssetFileDescription(mContext,
                                    String.format(VoiceConstants.FILE_PATH, voicePlay.get(counter[0])));
                            mediaPlayer.setDataSource(
                                    fileDescription2.getFileDescriptor(),
                                    fileDescription2.getStartOffset(),
                                    fileDescription2.getLength());
                            mediaPlayer.prepare();
                        } catch (IOException e) {
                            e.printStackTrace();
                            mCountDownLatch.countDown();
                        }
                    } else {
                        mediaPlayer.release();
                        mCountDownLatch.countDown();
                    }
                });


            } catch (Exception e) {
                e.printStackTrace();
                mCountDownLatch.countDown();
            } finally {
                if (assetFileDescription != null) {
                    try {
                        assetFileDescription.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            try {
                mCountDownLatch.await();
                notifyAll();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
