package com.meplus.robot.viewholder;

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.meplus.client.R;
import com.meplus.robot.api.model.Robot;
import com.meplus.robot.api.model.User;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.trinea.android.common.util.ListUtils;

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
        final User user = User.getCurrentUser(User.class);
        final List<Robot> robotList = user.getRobotList();
        final String robotId = ListUtils.isEmpty(robotList) ? "" : robotList.get(0).getRobotId();
        mTitle.setText(String.format("用户名:%1$s", user.getUsername()));
        mContent.setText(TextUtils.isEmpty(robotId) ? "未绑定多我机器人" : String.format("机器人:%1$s", robotId));
        mText.setText(String.format("邮箱:%1$s", user.getEmail()));
    }
}