package example.com.sunshine.download.Home;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.bumptech.glide.Glide;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import example.com.sunshine.Exo.E.PlayEvent;
import example.com.sunshine.Exo.ExoConstants;
import example.com.sunshine.Exo.PlayActivity;
import example.com.sunshine.R;
import example.com.sunshine.dagger.ActivityMobule;
import example.com.sunshine.dagger.DaggerActivityComponent;
import example.com.sunshine.download.Application.TLiveApplication;
import example.com.sunshine.download.Fragment.DiscoverFragment;
import example.com.sunshine.download.Fragment.HomeFragment;
import example.com.sunshine.download.Fragment.SubscriptionFragment;
import example.com.sunshine.download.Fragment.UserFragment;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * Created by qianxiangsen on 2017/5/3.
 */

public class Main111Activity extends BaseActivity implements View.OnClickListener{

    //当前tab位置
    public static int mFragCurrentIndex = 0;

    // 创建Fragment管理器
    private FragmentManager fragmentManager;

    @Bind(R.id.fragment_title)
    RadioGroup radioGroup;

    @Bind(R.id.find)
    RadioButton radioButton1;

    @Bind(R.id.custom)
    RadioButton radioButton2;

    @Bind(R.id.square)
    RadioButton radioButton3;

    @Bind(R.id.myspace)
    RadioButton radioButton4;

    @Inject
    HomeFragment homeFragment;
    @Inject
    SubscriptionFragment subscriptionFragment;
    @Inject
    DiscoverFragment discoverFragment;
    @Inject
    UserFragment hotPlayFragment;

    @Bind(R.id.common_playing_player)
    ImageView openPlayer;

    private Animation operatingAnim;

    @Override
    protected int getLayoutId() {
        return R.layout.act_main;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {

        if (this instanceof Main111Activity){
            DaggerActivityComponent.builder().
                    activityMobule(new ActivityMobule(this)).
                    build().inject(this);
        }

        ButterKnife.bind(this);

        if (savedInstanceState != null) {
            mFragCurrentIndex = savedInstanceState.getInt("curChoice", 0);
        }

        //移除fragment覆盖部分
        getWindow().setBackgroundDrawable(null);

        switchFragment(mFragCurrentIndex, true);

        operatingAnim = AnimationUtils.loadAnimation(this, R.anim.anim_play);
        LinearInterpolator lin = new LinearInterpolator();
        operatingAnim.setInterpolator(lin);
        int topicWidth = createTopicWidth();
        openPlayer.getLayoutParams().width = topicWidth;
        openPlayer.getLayoutParams().height = topicWidth;
        Glide.with(this).load(ExoConstants.IMAGE_URL).
                bitmapTransform(new CropCircleTransformation(this)).
                crossFade().into(openPlayer);

        radioButton1.setOnClickListener(this);
        radioButton2.setOnClickListener(this);
        radioButton3.setOnClickListener(this);
        radioButton4.setOnClickListener(this);
        openPlayer.setOnClickListener(this);
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void OnClick(View v) {

    }


    private void startPlayAnimation() {

        openPlayer.startAnimation(operatingAnim);
    }


    private void stopPlayAnimation() {

        openPlayer.clearAnimation();
    }
    /**
     * fragment切换
     *
     * @param index
     */
    private void switchFragment(int index, boolean isFirst) {
        setSelected(index, true);
        if (mFragCurrentIndex == index && !isFirst) {
            return;
        }

        if (fragmentManager == null) {
            fragmentManager = getSupportFragmentManager();
        }
        Fragment fragmentNow = fragmentManager.findFragmentByTag("home"
                + mFragCurrentIndex);
        if (fragmentNow != null) {

            fragmentManager.beginTransaction().hide(fragmentNow).commit();
        }
        Fragment fragment = fragmentManager.findFragmentByTag("home" + index);
        if (fragment == null) {
            fragment = createMainFragment(index);
            fragmentManager
                    .beginTransaction()
                    .add(R.id.fragment_container, fragment,
                            "home" + index).commit();
        } else {
            fragmentManager.beginTransaction().show(fragment).commit();
        }
        mFragCurrentIndex = index;
    }
    @Override
    public void onClick(View v) {
        setSelected(mFragCurrentIndex, false);
        switch (v.getId()) {
            case R.id.find:
                switchFragment(0, false);
                break;
            case R.id.custom:
                switchFragment(1, false);
                break;
            case R.id.square:
                switchFragment(2, false);
                break;
            case R.id.myspace:
                switchFragment(3, false);
                break;
            case R.id.common_playing_player:
                Intent intent = new Intent(this, PlayActivity.class);
                intent.putExtra("url",ExoConstants.PLAY_URL_NAME);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_botton_bottom,R.anim.slide_bottom);
                break;
        }
    }
    // 把显示的Fragment隐藏
    private void setSelected(int pos, boolean isSelected) {
        if (pos == 0) {
            radioButton1.setSelected(isSelected);
        } else if (pos == 1) {
            radioButton2.setSelected(isSelected);
        } else if (pos == 2) {
            radioButton3.setSelected(isSelected);
        } else {
            radioButton4.setSelected(isSelected);
        }
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("curChoice", mFragCurrentIndex);
    }
    /**
     * @param index
     */
    private Fragment createMainFragment(int index) {
        Fragment fragment = null;
        switch (index) {
            case 0:
                fragment = homeFragment;
                break;
            case 1:
                fragment = subscriptionFragment;
                break;
            case 2:
                fragment = discoverFragment;
                break;
            case 3:
                fragment = hotPlayFragment;
                break;
            default:
                break;
        }
        return fragment;
    }
    private int createTopicWidth() {
        int width = Math.min(TLiveApplication.mScreenWidth,
                TLiveApplication.mScreenHeight);
        int margin = (int) (3D * (double) getResources()
                .getDimensionPixelSize(R.dimen.topic_item_margin));
        return (int) ((double) (width - margin) / 6 + (double) (width - margin) % 4);
    }
    @Override
    public void onStart() {
        super.onStart();
        if(!EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().register(this);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPlayEvent(PlayEvent event) {

        boolean playing = event.isPlayWhenReady();
        if (playing){
            startPlayAnimation();
        }else {
            stopPlayAnimation();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (getSupportFragmentManager().getBackStackEntryCount() >= 1) {
                getSupportFragmentManager().popBackStack();
            } else {
                moveTaskToBack(true);
            }
            return true;

        }
        return false;
    }
    @Override
    protected void onDestroy() {
        if (EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().unregister(this);
        }
        super.onDestroy();

    }
}