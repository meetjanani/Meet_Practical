package com.example.meet_practical.Activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.aseem.versatileprogressbar.ProgBar;
import com.example.meet_practical.Adapter.Display_Adapter;
import com.example.meet_practical.DBHelper.Users_Db_Helper;
import com.example.meet_practical.R;
import com.example.meet_practical.Rest.ApiClient;
import com.example.meet_practical.Rest.ApiInterface;
import com.example.meet_practical.UserList_Bean.Response_Main;
import com.example.meet_practical.UserList_Bean.ResultsItem;
import com.example.meet_practical.Utility.Utils;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    EditText et_Search;
    private ProgBar LoaderProgressbar;
    private TextView tv_NoAnyData;
    private RecyclerView recyclerview_UserList;
    private SwipeRefreshLayout swipeRefreshLayout = null;
    private List<ResultsItem> Records = null;
    private Display_Adapter adapter;
    private Users_Db_Helper users_db_helper;
    private API_Call api_call;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Init();
        Load_Data_From_Local();

        // If User Want to Lead More New Data From Api then  Pull Down to Lead Mode Data
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //  productList.clear();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (Utils.isNetworkAvailable(MainActivity.this)) {
                            swipeRefreshLayout.setRefreshing(false);
                            api_call = new API_Call();
                            api_call.execute();
                        } else {
                            swipeRefreshLayout.setRefreshing(false);
                            Toast.makeText(MainActivity.this, getResources().getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                        }

                    }
                }, 1500);
            }
        });

        // First Time API Call If Internet Connecion is Avaiable
        if (Utils.isNetworkAvailable(MainActivity.this)) {
            swipeRefreshLayout.setRefreshing(false);
            api_call = new API_Call();
            api_call.execute();
        } else {
            swipeRefreshLayout.setRefreshing(false);
            Toast.makeText(MainActivity.this, getResources().getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
        }

        // Edittext -> On Text Change Event For Search on User List After Evecy Character Type
        // After Text Change
        et_Search.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            }

            @Override
            public void afterTextChanged(Editable arg0) {
                SearchByUserName(et_Search.getText().toString().toLowerCase());
            }
        });


    }

    // Fatch User Data From Local of Previous All Sessions in Descending order // Latest Data Will come First
    public void Load_Data_From_Local() {

        users_db_helper = new Users_Db_Helper(MainActivity.this);
        if (users_db_helper.getAllUser() != null) {
            Records = users_db_helper.getAllUser();
            adapter = new Display_Adapter(Records, MainActivity.this);
            recyclerview_UserList.setAdapter(adapter);
            tv_NoAnyData.setVisibility(View.GONE);
            LoaderProgressbar.setVisibility(View.GONE);
        } else {
            et_Search.setVisibility(View.GONE);
        }

    }

    // Initialization UI Component
    public void Init() {

        setTitle("Meet Practical");
        tv_NoAnyData = findViewById(R.id.tv_NoAnyData);
        recyclerview_UserList = findViewById(R.id.recyclerview_UserList);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        et_Search = findViewById(R.id.et_Search);
        LoaderProgressbar = findViewById(R.id.LoaderProgressbar);

        recyclerview_UserList.setLayoutManager(new LinearLayoutManager(MainActivity.this));

    }

    // Filter / Search From UserList Recyclerview By Any UserName in Edittext...
    public void SearchByUserName(String search_str) {
        if (search_str.length() >= 1) {

            List<ResultsItem> Records_ = new ArrayList<>();
            Records = users_db_helper.getAllUser();

            try {
                for (int i = 0; i < Records.size(); i++) {
                    if (Records.get(i).getName().toString().toLowerCase().contains(search_str.toLowerCase())) {
                        Records_.add(Records.get(i));
                    }
                }
            } catch (Exception e) {

            }

            adapter = new Display_Adapter(Records_, MainActivity.this);
            adapter.setHasStableIds(true);
            recyclerview_UserList.setAdapter(adapter);
            ViewCompat.setNestedScrollingEnabled(recyclerview_UserList, false);
        } else {
            Records = users_db_helper.getAllUser();
            adapter = new Display_Adapter(Records, MainActivity.this);
            adapter.setHasStableIds(true);
            recyclerview_UserList.setAdapter(adapter);
            ViewCompat.setNestedScrollingEnabled(recyclerview_UserList, false);
        }
    }

    // AsyncTask Class For Lead Data From Remote Server ( API Call ) if Internet Connecion is Avaiable..
    // Case 1
    // If User OPen First Time then Fatch List From API & Display in RecyclerView
    // Case 2
    // If User has Open aster First Time then Local User List + New Random User List From API Append then Display in RecyclerView
    class API_Call extends AsyncTask {

        ApiInterface apiService = null;
        Call<Response_Main> call = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            LoaderProgressbar.setVisibility(View.VISIBLE);
            swipeRefreshLayout.setRefreshing(true);
        }

        @Override
        protected Object doInBackground(Object[] objects) {

            apiService =
                    ApiClient.getClient().create(ApiInterface.class);
            call = apiService.Login_API(10);
            call.enqueue(new Callback<Response_Main>() {
                @Override
                public void onResponse(Call<Response_Main> call, Response<Response_Main> response) {

                    // There is Not an Any kind Of Error as of now in this block of code , after Removeing try catck block that is also Work FIne , if you can want to remove then you can ...
                    // Just For Safty Purpose Added Try Catch Block
                    try {
                        et_Search.setText(null);
                        et_Search.setVisibility(View.VISIBLE);
                        tv_NoAnyData.setVisibility(View.GONE);

                        if (users_db_helper.getAllUser() != null) {
                            if (users_db_helper.getAllUser().size() > 0) {
                                Records.addAll(response.body().getResults());
                                adapter.InsertNewUsers(response.body().getResults());
                                recyclerview_UserList.smoothScrollToPosition(adapter.getItemCount() - (response.body().getResults().size() + 1));
                            }
                        } else {
                            Records = response.body().getResults();
                            adapter = new Display_Adapter(Records, MainActivity.this);
                            recyclerview_UserList.setAdapter(adapter);
                        }

                        Gson gson = new Gson();
                        for (int i = 0; i < response.body().getResults().size(); i++) {
                            ResultsItem obj = response.body().getResults().get(i);
                            String json = gson.toJson(obj);
                            users_db_helper.Insert_user(json);
                        }
                    } catch (Exception e) {

                    }
                }

                @Override
                public void onFailure(Call<Response_Main> call, Throwable t) {

                }
            });
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);

            LoaderProgressbar.setVisibility(View.GONE);
            swipeRefreshLayout.setRefreshing(false);
        }

    }

}
