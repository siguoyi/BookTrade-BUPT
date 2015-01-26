package com.bupt.booktrade.fragment;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bupt.booktrade.R;
import com.bupt.booktrade.nForumSDK.NForumSDK;
import com.bupt.booktrade.nForumSDK.http.NForumException;
import com.bupt.booktrade.nForumSDK.model.User;
import com.bupt.booktrade.nForumSDK.service.UserService;
import com.bupt.booktrade.nForumSDK.util.Host;
import com.bupt.booktrade.utils.TimeFormat;

import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

public class PersonalHomeFragment extends Fragment {

    private final static String TAG = "  PersonalHomeFragment: ";
    private final boolean D = true;
    User user = new User();
    private View rootView;
    Bitmap bitmap;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getUserInfo();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_personal_home, container, false);
        }

        ViewGroup parent = (ViewGroup) rootView.getParent();
        if (parent != null) {
            parent.removeView(rootView);
        }
        return rootView;
    }

    private void getUserInfo() {
        getUserInfoTask testAsyncTask = new getUserInfoTask(new FragmentCallback() {

            @Override
            public void onTaskDone() {
                //ToastUtils.showToast(getActivity(), userName.getText().toString(), Toast.LENGTH_SHORT);
                setUserInfo();
            }
        });
        testAsyncTask.execute();
    }

    private void setUserInfo() {
        TextView userName = (TextView) rootView.findViewById(R.id.person_home_username);
        TextView nickName = (TextView) rootView.findViewById(R.id.nick_name);
        TextView gender = (TextView) rootView.findViewById(R.id.gender);
        TextView constellation = (TextView) rootView.findViewById(R.id.constellation);
        TextView qq = (TextView) rootView.findViewById(R.id.qq);
        TextView msn = (TextView) rootView.findViewById(R.id.msn);
        TextView homePage = (TextView) rootView.findViewById(R.id.home_page);
        TextView level = (TextView) rootView.findViewById(R.id.level);
        TextView score = (TextView) rootView.findViewById(R.id.score);
        TextView isOnline = (TextView) rootView.findViewById(R.id.is_online);
        TextView firstLoginTime = (TextView) rootView.findViewById(R.id.first_login_time);
        TextView lastLoginTime = (TextView) rootView.findViewById(R.id.last_login_time);
        TextView lastLoginIp = (TextView) rootView.findViewById(R.id.last_login_ip);
        ImageView avatar = (ImageView) rootView.findViewById(R.id.person_home_avatar);

        userName.setText(user.id);

        if (user.user_name.isEmpty()) {
            nickName.setText("未填写");
        } else {
            nickName.setText(user.user_name);
        }

        if (user.gender.equals("m")) {
            gender.setText("男生");
        } else if (user.gender.equals("f")) {
            gender.setText("女生");
        } else {
            gender.setText("隐藏");
        }

        if (user.astro.isEmpty()) {
            constellation.setText("隐藏");
        } else {
            constellation.setText(user.astro);
        }

        if (user.user_name.isEmpty()) {
            qq.setText("未填写");
        } else {
            qq.setText(user.qq);
        }

        if (user.user_name.isEmpty()) {
            msn.setText("未填写");
        } else {
            msn.setText(user.msn);
        }

        if (user.user_name.isEmpty()) {
            homePage.setText("未填写");
        } else {
            homePage.setText(user.home_page);
        }

        level.setText(user.level);
        score.setText(user.score);
        if (user.is_online) {
            isOnline.setText("在线");
        } else {
            isOnline.setText("离线");
        }
        firstLoginTime.setText(TimeFormat.transTime(user.first_login_time * 1000l));
        lastLoginTime.setText(TimeFormat.transTime(user.last_login_time * 1000l));
        lastLoginIp.setText(user.last_login_ip);

        if (D) Log.d(TAG, String.valueOf(user.first_login_time));
        if (D) Log.d(TAG, String.valueOf(user.last_login_time));
        avatar.setImageBitmap(bitmap);
    }

    private interface FragmentCallback {
        public void onTaskDone();
    }

    class getUserInfoTask extends AsyncTask<Void, Void, Void> {
        private FragmentCallback mFragmentCallback;

        public getUserInfoTask(FragmentCallback fragmentCallback) {
            mFragmentCallback = fragmentCallback;
        }

        @Override
        protected Void doInBackground(Void... params) {
        /* Do your thing. */
            NForumSDK nForumSDK = new NForumSDK(Host.HOST_BYR, Host.APP_KEY, "Demievil", "liuyan1206");
            UserService userService = nForumSDK.getUserService();
            try {
                user = userService.queryById("Demievil");
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (NForumException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            try {
                bitmap = BitmapFactory.decodeStream((InputStream) new URL(user.face_url).getContent());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            mFragmentCallback.onTaskDone();
        }
    }
}
