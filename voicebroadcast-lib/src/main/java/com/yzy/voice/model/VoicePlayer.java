package com.yzy.voice.model;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.widget.Toast;

import com.yzy.voice.VoiceQueue;

import java.util.Locale;

public class VoicePlayer implements Runnable {
    private final TextToSpeech mSpeech;
    private final VoiceQueue ss;
    private boolean playEnabled = true;

    public VoicePlayer(Context context, VoiceQueue ss) {
        this.ss = ss;
        mSpeech = new TextToSpeech(context, status -> {
            if (status != TextToSpeech.SUCCESS) {
                Toast.makeText(context, "TTS引擎初始化失败", Toast.LENGTH_SHORT).show();
            }
        });
        mSpeech.setLanguage(Locale.CHINESE);
        mSpeech.setSpeechRate(1.2f);
        mSpeech.setPitch(1.0f);
    }

    public void setPlayEnabled(boolean playEnabled) {
        this.playEnabled = playEnabled;
    }

    /**
     * show 消费进程.
     */
    public void run() {
        while (playEnabled) {
            Log.e("TAG", "run: mSpeech.isSpeaking():" + mSpeech.isSpeaking());
            if (mSpeech.isSpeaking()) {
                // 有文件正在播放,则等待,至播放器状态空闲 继续播放, 播放器本身是异步播放
                try {
                    Thread.sleep((int) (Math.random() * 1000));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                continue;
            }
            String text = ss.pop();
            mSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, "id");
            Log.e("tag", "播放了：---------" + text);
            try {
                Thread.sleep((int) (Math.random() * 1000));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
