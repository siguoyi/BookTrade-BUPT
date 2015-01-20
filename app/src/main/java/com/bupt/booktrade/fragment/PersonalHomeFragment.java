package com.bupt.booktrade.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bupt.booktrade.R;
import com.bupt.booktrade.nForumSDK.NForumSDK;
import com.bupt.booktrade.nForumSDK.http.NForumException;
import com.bupt.booktrade.nForumSDK.model.User;
import com.bupt.booktrade.nForumSDK.service.UserService;
import com.bupt.booktrade.nForumSDK.util.Host;
import com.bupt.booktrade.utils.ToastUtils;

import org.json.JSONException;

import java.io.IOException;

public class PersonalHomeFragment extends Fragment {

    User user = new User();
    private View rootView;


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

    private interface FragmentCallback {
        public void onTaskDone();
    }

    private void setUserInfo() {
        TextView userName = (TextView) rootView.findViewById(R.id.person_home_username);

    }
    class getUserInfoTask extends AsyncTask<Void, Void, Void> {
        private FragmentCallback mFragmentCallback;

        public getUserInfoTask(FragmentCallback fragmentCallback) {
            mFragmentCallback = fragmentCallback;
        }

        @Override
        protected Void doInBackground(Void... params) {
        /* Do your thing. */
            NForumSDK nForumSDK = new NForumSDK(Host.HOST_BYR, "91845df51583867c", "Demievil", "liuyan1206");
            UserService userService = nForumSDK.getUserService();
            try {
                user = userService.queryById("Demievil");
                Log.d("user ID: ", String.valueOf(user.first_login_time));
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
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            mFragmentCallback.onTaskDone();
        }
    }
}
