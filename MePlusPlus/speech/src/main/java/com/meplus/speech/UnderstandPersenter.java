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

import java.util.List;

public class UnderstandPersenter {
    private static String TAG = UnderstandPersenter.class.getSimpleName();

    //初始化监听器（语音到语义）。
    private final InitListener mSpeechUdrInitListener = new InitListener() {
        @Override
        public void onInit(int code) {
            Log.d(TAG, "speechUnderstanderListener init() code = " + code);
            if (code != ErrorCode.SUCCESS) {
                Log.d(TAG, "onInit：" + code);
                final Speech speech = new Speech(Speech.ACTION_UNDERSTAND_ERROR);
                speech.setError(String.valueOf(code));
                EventUtils.postEvent(new SpeechEvent(speech));
            }
        }
    };

    // 语义理解回调。
    private final SpeechUnderstanderListener mSpeechUnderstanderListener = new SpeechUnderstanderListener() {

        @Override
        public void onResult(final UnderstanderResult understanderResult) {
            final String text = null == understanderResult ? "" : understanderResult.getResultString();
            Log.d(TAG, "onResult: " + text);
            Result result = null;
            if (!TextUtils.isEmpty(text)) {// text 有效
                result = JsonUtils.readValue(text, Result.class);
            }

            final Speech speech = new Speech(Speech.ACTION_UNDERSTAND_END);
            if (result == null) {
                speech.setQuestion(text);
            } else {// result 有效
                final Answer answer = result.getAnswer();
                final List<Result> moreResults = result.getMoreResults();
                final boolean hasMoreAnswer = moreResults != null && moreResults.size() > 0;
                final Answer moreAnswer = hasMoreAnswer ? moreResults.get(0).getAnswer() : null;
                final String moreText = moreAnswer == null ? "" : moreAnswer.getText();
                speech.setQuestion(result.getText());
                speech.setAnswer(answer == null ? moreText : answer.getText());
            }

            EventUtils.postEvent(new SpeechEvent(speech));
        }

        @Override
        public void onVolumeChanged(int volume, byte[] data) {
            Log.d(TAG, "onVolumeChanged: " + volume);
            if (volume > 10) {
                final Speech speech = new Speech(Speech.ACTION_UNDERSTAND_BEGINE);
                EventUtils.postEvent(new SpeechEvent(speech));
            }
        }

        @Override
        public void onEndOfSpeech() {
            // 此回调表示：检测到了语音的尾端点，已经进入识别过程，不再接受语音输入
            Log.d(TAG, "onEndOfSpeech");
        }

        @Override
        public void onBeginOfSpeech() {
            // 此回调表示：sdk内部录音机已经准备好了，用户可以开始语音输入
            Log.d(TAG, "onBeginOfSpeech");
        }

        @Override
        public void onError(SpeechError error) {
            final String plainDescription = error == null ? "null" : error.getPlainDescription(true);
            Log.d(TAG, "onError: " + plainDescription);
            final Speech speech = new Speech(Speech.ACTION_UNDERSTAND_ERROR);
            speech.setError(plainDescription);
            EventUtils.postEvent(new SpeechEvent(speech));
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
        int code = mSpeechUnderstander.startUnderstanding(mSpeechUnderstanderListener);
        if (code != 0) {
            Log.d(TAG, "startUnderstanding error: " + code);
            final Speech speech = new Speech(Speech.ACTION_UNDERSTAND_ERROR);
            speech.setError(String.valueOf(code));
            EventUtils.postEvent(new SpeechEvent(speech));
        }
    }

    public boolean isUnderstanding() {
        return mSpeechUnderstander.isUnderstanding();
    }

    // 取消语义理解
    public void cancelUnderstanding() {
        Log.d(TAG, "cancelUnderstanding");
        mSpeechUnderstander.cancel();
    }

    // 停止语音理解
    public void stopUnderstanding() {
        Log.d(TAG, "stopUnderstanding");
        mSpeechUnderstander.stopUnderstanding();
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
