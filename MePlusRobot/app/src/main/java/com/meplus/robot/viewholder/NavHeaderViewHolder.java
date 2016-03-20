package com.meplus.robot.viewholder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.meplus.client.R;
import com.meplus.robot.api.model.Robot;
import com.meplus.robot.app.MPApplication;

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
        final Robot user = MPApplication.getsInstance().getRobot();
        mTitle.setText(String.format("机器人:%1$s", user.getRobotName()));
        mContent.setText("写点什么内容呢？");
        mText.setText(String.format("机器ID:%1$s", user.getRobotId()));
    }
}