package com.meplus.client.viewholder;

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.meplus.client.R;
import com.meplus.client.api.model.User;

import butterknife.Bind;
import butterknife.ButterKnife;

public class NavHeaderViewHolder {
    @Bind(R.id.image)
    ImageView mImage;
    @Bind(R.id.title)
    TextView mTitle;
    @Bind(R.id.content)
    TextView mContent;
    @Bind(R.id.text)
    TextView mText;

    public NavHeaderViewHolder(View view) {
        ButterKnife.bind(this, view);
    }

    public void updateUserView() {
        User user = User.getCurrentUser(User.class);
        final String robotId = user.getRobotId();
        mTitle.setText(String.format("用户名:%1$s", user.getUsername()));
        mContent.setText(TextUtils.isEmpty(robotId) ? "未绑定多我机器人" : String.format("机器人:%1$s", robotId));
        mText.setText(String.format("邮箱:%1$s", user.getEmail()));
    }
}