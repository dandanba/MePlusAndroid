package com.meplus.client.activity;

import android.os.Bundle;
import android.os.SystemClock;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.SignUpCallback;
import com.meplus.client.R;
import com.meplus.client.api.model.User;

/**
 * 注册页面
 */
public class RegisterActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        User user = new User();
        user.setUsername("hjiang" + SystemClock.currentThreadTimeMillis());
        user.setPassword("f32@ds*@&dsa");
        user.setEmail("hang" + SystemClock.currentThreadTimeMillis() + "@leancloud.rocks");
        // 其他属性可以像其他AVObject对象一样使用put方法添加
        user.put("phone", "186-1234-0000");

        user.signUpInBackground(new SignUpCallback() {
            public void done(AVException e) {
                if (e == null) {
                    // successfully
                    finish();
                } else {
                    // failed
                }
            }
        });

    }

}
