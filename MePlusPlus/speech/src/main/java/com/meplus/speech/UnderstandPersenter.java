package com.meplus.speech;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechUnderstander;
import com.iflytek.cloud.SpeechUnderstanderListener;
import com.iflytek.cloud.UnderstanderResult;
import com.meplus.events.EventUtils;
import com.meplus.speech.model.Answer;
import com.meplus.speech.model.Result;

public class UnderstandPersenter {
    private static String TAG = UnderstandPersenter.class.getSimpleName();

    //初始化监听器（语音到语义）。
    private final InitListener mSpeechUdrInitListener = new InitListener() {
        @Override
        public void onInit(int code) {
            Log.d(TAG, "speechUnderstanderListener init() code = " + code);
            if (code != ErrorCode.SUCCESS) {
                Log.d(TAG, "onInit：" + code);
                EventUtils.postEvent(new UnderstandEvent(new Understand(Understand.ACTION_ERROR)));
            }
        }
    };

    // 语义理解回调。
    private final SpeechUnderstanderListener mSpeechUnderstanderListener = new SpeechUnderstanderListener() {

        @Override
        public void onResult(final UnderstanderResult result) {
            final String text = null == result ? "" : result.getResultString();
            Log.d(TAG, "onResult: " + text);

            final Result r;
            final Answer answer;
            if (!TextUtils.isEmpty(text)// text 有效
                    && (r = JsonUtils.readValue(text, Result.class)) != null// result 有效
                    && (answer = r.getAnswer()) != null) { // answer 有效
                final Understand understand = new Understand(Understand.ACTION_SPEECH_UNDERSTAND);
                understand.setMessage(answer.getText());
                EventUtils.postEvent(new UnderstandEvent(understand));
            } else {
                startUnderstanding();
            }

        }

        @Override
        public void onVolumeChanged(int volume, byte[] data) {
            Log.d(TAG, "onVolumeChanged: " + volume);
        }

        @Override
        public void onEndOfSpeech() {
            // 此回调表示：检测到了语音的尾端点，已经进入识别过程，不再接受语音输入
            Log.d(TAG, "onEndOfSpeech");
            EventUtils.postEvent(new UnderstandEvent(new Understand(Understand.ACTION_UNDERSTAND)));
        }

        @Override
        public void onBeginOfSpeech() {
            // 此回调表示：sdk内部录音机已经准备好了，用户可以开始语音输入
            Log.d(TAG, "onBeginOfSpeech");
            // 开始听
            EventUtils.postEvent(new UnderstandEvent(new Understand(Understand.ACTION_LISTEN)));
        }

        @Override
        public void onError(SpeechError error) {
            Log.d(TAG, "onError: " + error == null ? "null" : error.getPlainDescription(true));
            startUnderstanding();
        }

        @Override
        public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
            // 以下代码用于获取与云端的会话id，当业务出错时将会话id提供给技术支持人员，可用于查询会话日志，定位出错原因
            //	if (SpeechEvent.EVENT_SESSION_ID == eventType) {
            //		String sid = obj.getString(SpeechEvent.KEY_EVENT_SESSION_ID);
            //		Log.d(TAG, "session id =" + sid);
            //	}
            Log.d(TAG, "onEvent: " + eventType);
        }
    };

    private SpeechUnderstander mSpeechUnderstander;  // 语义理解对象（语音到语义）。

    public void create(Context cnotext) {
        /**
         * 申请的appid时，我们为开发者开通了开放语义（语义理解）
         * 由于语义理解的场景繁多，需开发自己去开放语义平台：http://www.xfyun.cn/services/osp
         * 配置相应的语音场景，才能使用语义理解，否则文本理解将不能使用，语义理解将返回听写结果。
         */
        // 初始化对象
        mSpeechUnderstander = SpeechUnderstander.createUnderstander(cnotext, mSpeechUdrInitListener);
        // 设置参数
        setParam(Constants.LANG);
    }

    public void destroy() {
        // 退出时释放连接
        cancelUnderstanding();
        mSpeechUnderstander.destroy();
    }

    // 开始语音理解
    public void startUnderstanding() {
        if (mSpeechUnderstander.isUnderstanding()) {// 开始前检查状态
            mSpeechUnderstander.stopUnderstanding();
            Log.d(TAG, "stopUnderstanding");
        }
        int ret = mSpeechUnderstander.startUnderstanding(mSpeechUnderstanderListener);
        if (ret != 0) {
            Log.d(TAG, "startUnderstanding error: " + ret);
            EventUtils.postEvent(new UnderstandEvent(new Understand(Understand.ACTION_ERROR)));
        }

    }

    // 取消语义理解
    public void cancelUnderstanding() {
        mSpeechUnderstander.cancel();
        Log.d(TAG, "cancelUnderstanding");
    }

    // 停止语音理解
    public void stopUnderstanding() {
        mSpeechUnderstander.stopUnderstanding();
        Log.d(TAG, "stopUnderstanding");
    }

    public void setParam(String lang) {
        if (lang.equals("en_us")) {
            // 设置语言
            mSpeechUnderstander.setParameter(SpeechConstant.LANGUAGE, "en_us");
        } else {
            // 设置语言
            mSpeechUnderstander.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
            // 设置语言区域
            mSpeechUnderstander.setParameter(SpeechConstant.ACCENT, lang);
        }
        // 设置语音前端点:静音超时时间，即用户多长时间不说话则当做超时处理
        mSpeechUnderstander.setParameter(SpeechConstant.VAD_BOS, "4000");

        // 设置语音后端点:后端点静音检测时间，即用户停止说话多长时间内即认为不再输入， 自动停止录音
        mSpeechUnderstander.setParameter(SpeechConstant.VAD_EOS, "1000");

        // 设置标点符号，默认：1（有标点）
        mSpeechUnderstander.setParameter(SpeechConstant.ASR_PTT, "1");

        // 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
        // 注：AUDIO_FORMAT参数语记需要更新版本才能生效
        mSpeechUnderstander.setParameter(SpeechConstant.AUDIO_FORMAT, "wav");
        mSpeechUnderstander.setParameter(SpeechConstant.ASR_AUDIO_PATH, Environment.getExternalStorageDirectory() + "/msc/sud.wav");
    }
}
