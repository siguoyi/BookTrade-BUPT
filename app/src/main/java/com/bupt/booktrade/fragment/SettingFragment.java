package com.bupt.booktrade.fragment;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bupt.booktrade.MyApplication;
import com.bupt.booktrade.R;
import com.bupt.booktrade.activity.LoginActivity;
import com.bupt.booktrade.entity.User;
import com.bupt.booktrade.utils.BitmapUtils;
import com.bupt.booktrade.utils.CacheUtils;
import com.bupt.booktrade.utils.Constant;
import com.bupt.booktrade.utils.LogUtils;
import com.bupt.booktrade.utils.ToastUtils;

import java.io.File;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;


public class SettingFragment extends BaseFragment implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private String TAG;
    private RelativeLayout setAvatarLayout;
    private RelativeLayout setNicknameLayout;
    private RelativeLayout clearCacheLayout;
    private RelativeLayout checkUpdateLayout;

    private CheckBox sexSwitch;
    private CheckBox allowPushSwitch;

    private Button logout;

    private ImageView settingAvatar;
    private TextView settingNickname;
    private TextView cacheSize;
    private User user;
    private final int REQUEST_CODE_ALBUM = 0;

    private View rootView;
    private ImageView drawerAvatar;
    private TextView drawerUserName;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TAG = getClass().getSimpleName();
        user = MyApplication.getMyApplication().getCurrentUser();
        drawerAvatar = (ImageView) getActivity().findViewById(R.id.drawer_user_avatar);
        drawerUserName = (TextView) getActivity().findViewById(R.id.drawer_user_name);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        LogUtils.i(TAG, "onCreateView");
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_setting, container, false);
            setAvatarLayout = (RelativeLayout) rootView.findViewById(R.id.setting_user_avatar_layout);
            settingAvatar = (ImageView) rootView.findViewById(R.id.setting_user_avatar);
            setNicknameLayout = (RelativeLayout) rootView.findViewById(R.id.setting_user_nickname_layout);
            settingNickname = (TextView) rootView.findViewById(R.id.setting_user_nickname);
            cacheSize = (TextView) rootView.findViewById(R.id.cache_size);
            clearCacheLayout = (RelativeLayout) rootView.findViewById(R.id.setting_clear_cache_layout);
            checkUpdateLayout = (RelativeLayout) rootView.findViewById(R.id.setting_update_layout);

            sexSwitch = (CheckBox) rootView.findViewById(R.id.switch_sex_choice);
            allowPushSwitch = (CheckBox) rootView.findViewById(R.id.switch_allow_push);

            logout = (Button) rootView.findViewById(R.id.setting_logout_button);
            setListener();
            initPersonalInfo();
        }
        ViewGroup parent = (ViewGroup) rootView.getParent();
        if (parent != null) {
            parent.removeView(rootView);
        }
        return rootView;
    }

    private void setListener() {
        // TODO Auto-generated method stub
        logout.setOnClickListener(this);
        checkUpdateLayout.setOnClickListener(this);
        clearCacheLayout.setOnClickListener(this);
        allowPushSwitch.setOnCheckedChangeListener(this);
        sexSwitch.setOnCheckedChangeListener(this);

        setAvatarLayout.setOnClickListener(this);
        setNicknameLayout.setOnClickListener(this);
        logout.setOnClickListener(this);
    }

    private void initPersonalInfo() {
        User user = MyApplication.getMyApplication().getCurrentUser();
        if (user != null) {
            settingNickname.setText(user.getUsername());
            if (user.getSex().equals(Constant.SEX_MALE)) {
                sexSwitch.setChecked(true);
                //sputil.setValue("sex_settings", 0);
            } else {
                sexSwitch.setChecked(false);
                //sputil.setValue("sex_settings", 1);
            }
            BmobFile avatarFile = user.getAvatar();
            if (null != avatarFile) {
                int defaultAvatar = user.getSex().equals(Constant.SEX_MALE) ? R.drawable.avatar_default_m : R.drawable.avatar_default_f;
                Glide.with(mContext)
                        .load(Uri.parse(avatarFile.getFileUrl(mContext)))
                        .centerCrop()
                        .placeholder(defaultAvatar)
                        .diskCacheStrategy(DiskCacheStrategy.RESULT)
                        .into(settingAvatar);
            }
            logout.setText("注销");
        } else {
            logout.setText("登录");
        }

        cacheSize.setText(String.valueOf(CacheUtils.getCacheSize()) + "KB");
    }

    private void setLogoutUserInfo() {
        int defaultAvatar = user.getSex().equals(Constant.SEX_MALE) ? R.drawable.avatar_default_m : R.drawable.avatar_default_f;
        drawerAvatar.setImageResource(defaultAvatar);
        drawerUserName.setText("点击登录");
    }

    /**
     * 判断用户是否登录
     *
     * @return
     */
    private boolean isLogin() {
        BmobUser user = BmobUser.getCurrentUser(mContext, User.class);
        if (user != null) {
            return true;
        }
        return false;
    }


    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.switch_sex_choice:
                if (isChecked) {
                    updateSex(0);
                } else {
                    updateSex(1);
                }
                break;

            case R.id.switch_allow_push:
                break;

            default:
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.setting_logout_button:
                if (isLogin()) {
                    BmobUser.logOut(mContext);
                    ToastUtils.showToast(mContext, "已注销", Toast.LENGTH_SHORT);
                    setLogoutUserInfo();
                    skipToLogin();
                }
                break;

            case R.id.setting_user_avatar_layout:
                if (isLogin()) {
                    Intent intent = new Intent(Intent.ACTION_PICK);//or ACTION_PICK
                    intent.setType("image/*");//相片类型
                    startActivityForResult(Intent.createChooser(intent,
                            "Select Picture"), REQUEST_CODE_ALBUM);
                } else {
                    skipToLogin();
                }
                break;

            case R.id.setting_clear_cache_layout:
                CacheUtils.deleteFilesInCache();
                cacheSize.setText(String.valueOf(CacheUtils.getCacheSize()) + "KB");
                break;

            case R.id.setting_update_layout:
                ToastUtils.showToast(mContext, "暂无新版本", Toast.LENGTH_SHORT);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_ALBUM:
                    if (data != null) {
                        Uri originalUri = data.getData();
                        String targetUrl = BitmapUtils.saveToSdCard(mContext, BitmapUtils.compressBitmapFromFile(getPath(originalUri), 800, 480));
                        LogUtils.d(TAG, targetUrl);
                        Glide.with(mContext)
                                .load(Uri.parse("file://" + targetUrl))
                                .centerCrop()
                                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                                .into(settingAvatar);
                        Glide.with(mContext)
                                .load(Uri.parse("file://" + targetUrl))
                                .centerCrop()
                                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                                .into(drawerAvatar);
                        //takeLayout.setVisibility(View.GONE);
                        final BmobFile avatar = new BmobFile(new File(targetUrl));
                        avatar.uploadblock(mContext, new UploadFileListener() {
                            @Override
                            public void onSuccess() {
                                if (avatar != null) {
                                    user.setAvatar(avatar);
                                    user.update(mContext, new UpdateListener() {
                                        @Override
                                        public void onSuccess() {
                                            LogUtils.d(TAG, "update avatar");
                                        }

                                        @Override
                                        public void onFailure(int i, String s) {
                                            LogUtils.d(TAG, s);
                                        }
                                    });
                                }
                            }

                            @Override
                            public void onFailure(int i, String s) {
                                LogUtils.d(TAG, s);
                            }
                        });
                    }

                    break;

                default:
                    break;
            }
        }
    }

    /**
     * helper to retrieve the path of an image URI
     */
    public String getPath(Uri uri) {
        // just some safety built in
        if (uri == null) {
            // TODO perform some logging or show user feedback
            return null;
        }
        // try to retrieve the image from the media store first
        // this will only work for images selected from gallery
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = mContext.getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            int columnIndex = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(columnIndex);
        }
        // this is our fallback here
        cursor.close();
        return uri.getPath();
    }

    private void skipToLogin() {
        ToastUtils.showToast(mContext, "请先登录", Toast.LENGTH_SHORT);
        Intent loginIntent = new Intent(mContext, LoginActivity.class);
        startActivity(loginIntent);
    }

    private void updateSex(int sex) {
        if (isLogin() && user != null) {
            if (sex == 0) {
                user.setSex(Constant.SEX_MALE);
            } else {
                user.setSex(Constant.SEX_FEMALE);
            }
            user.update(mContext, new UpdateListener() {

                @Override
                public void onSuccess() {
                    // TODO Auto-generated method stub
                    LogUtils.i(TAG, "sex:" + user.getSex());
                }

                @Override
                public void onFailure(int arg0, String arg1) {
                    // TODO Auto-generated method stub

                    LogUtils.i(TAG, arg1);
                }
            });
        } else {
            skipToLogin();
        }

    }
}
