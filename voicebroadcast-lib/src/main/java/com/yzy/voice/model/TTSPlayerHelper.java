package com.yzy.voice.model;

import android.content.Context;

import com.yzy.voice.VoiceQueue;


public class TTSPlayerHelper {

    private volatile static TTSPlayerHelper mHelper = null;

    private final VoiceQueue queue;
    private final VoicePlayer player;


    private TTSPlayerHelper(Context context) {
        queue = new VoiceQueue();
        player = new VoicePlayer(context, queue);
    }

    public static TTSPlayerHelper init(Context context) {
        if (mHelper == null) {
            synchronized (TTSPlayerHelper.class) {
                if (mHelper == null) {
                    mHelper = new TTSPlayerHelper(context);
                }
            }
        }
        return mHelper;
    }


    /**
     * 播放器处理监听状态,等待queue中队列新数据
     */
    public void start() {
        Thread tc = new Thread(player);
        tc.start();
    }

    /**
     * 播放声音,可由线程压入
     */
    public void play(String text) {
        queue.push(text);
    }
}
