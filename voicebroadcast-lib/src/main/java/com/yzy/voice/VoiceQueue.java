package com.yzy.voice;

public class VoiceQueue {

    String[] sm = new String[6];
    int index = 0;

    /**
     * @param m 元素
     * @  没有返回值
     */

    public synchronized void push(String m) {
        try {
            while (index == sm.length) {
                System.out.println("!!!!!!!!!超过最大堆数量,执行等待,再压入!!!!!!!!!");
                this.wait();
            }
            this.notify();
        } catch (InterruptedException | IllegalMonitorStateException e) {
            e.printStackTrace();
        }

        sm[index] = m;
        index++;
    }

    /**
     * @return b true 表示显示，false 表示隐藏
     * 没有返回值
     */
    public synchronized String pop() {
        try {
            while (index == 0) {
                System.out.println("!!!!!!!!!消费光了!!!!!!!!!");
                this.wait();
            }
            this.notify();
        } catch (InterruptedException | IllegalMonitorStateException e) {
            e.printStackTrace();
        }
        index--;
        return sm[index];
    }

}
