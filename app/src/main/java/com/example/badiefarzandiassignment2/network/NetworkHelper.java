package com.example.badiefarzandiassignment2.network;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.badiefarzandiassignment2.Constant;
import com.example.badiefarzandiassignment2.R;
import com.example.badiefarzandiassignment2.data.async.DbResult;
import com.example.badiefarzandiassignment2.data.db.DbResponse;
import com.example.badiefarzandiassignment2.data.model.Story;
import com.example.badiefarzandiassignment2.data.model.User;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Array;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NetworkHelper {
    private static final String TAG = "NetworkHelper";
    private static NetworkHelper instance = null;
    private SharedPreferences preferences;
    private Context context;
    private Gson gson = new Gson();
    private RequestQueue requestQueue;
    private String appId;
    private String apiKey;
    private String hostUrl;

    private NetworkHelper(Context context) {
        preferences = context.getSharedPreferences(Constant.USER_SHARED_PREFRENCES, Context.MODE_PRIVATE);
        this.context = context;
        this.requestQueue = Volley.newRequestQueue(context);
        this.appId = context.getString(R.string.appId);
        this.apiKey = context.getString(R.string.appKey);
        this.hostUrl = context.getString(R.string.hostUrl);
    }

    public static NetworkHelper getInstance(Context context) {
        if(instance == null) {
            instance = new NetworkHelper(context);
        }
        return instance;
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo net = cm.getActiveNetworkInfo();
        return ((net != null) && net.isConnected());
    }

    private void printVolleyErrorDetails(VolleyError error){
        NetworkResponse errResponse = (error != null) ? error.networkResponse : null;
        int statusCode = 0;
        String data = "";
        if(errResponse != null) {
            statusCode = errResponse.statusCode;
            byte[] bytes = errResponse.data;
            data = (bytes != null) ? new String(bytes, StandardCharsets.UTF_8) : "";
        }
        Log.e(TAG, "Volley error width status code " + statusCode + " recieved with this message: " + data);
    }

    public void signupUser(final User user, final DbResponse<User> dbResponse) {
        if(!isNetworkConnected()) {
            dbResponse.onError(new Error(context.getString(R.string.network_connection_error)));
            return;
        }

        String url = hostUrl + "/users";
        String userJson = null;

        try {
            userJson = gson.toJson(user);
        } catch(Exception exception) {
            exception.printStackTrace();
            dbResponse.onError(new Error(context.getString(R.string.network_json_error)));
            return;
        }

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(TextUtils.isEmpty(response)) {
                    dbResponse.onError(new Error(context.getString(R.string.network_general_error)));
                    return;
                }

                User resultUser = null;

                try {
                    resultUser = gson.fromJson(response, new TypeToken<User>(){}.getType());
                } catch(Exception exception) {
                    exception.printStackTrace();
                    dbResponse.onError(new Error(context.getString(R.string.network_json_error)));
                    return;
                }
                dbResponse.onSuccess(resultUser);
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                dbResponse.onError(new Error(context.getString(R.string.network_general_error)));
                return;
            }
        };

        final String jsonStr = userJson;
        StringRequest request = new StringRequest(Request.Method.POST, url, responseListener, errorListener) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("X-Parse-Application-Id", appId);
                headers.put("X-Parse-REST-API-Key", apiKey);
                headers.put("X-Parse-Revocable-Session", "1");
                return headers;
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                return jsonStr.getBytes(StandardCharsets.UTF_8);
            }
        };
        requestQueue.add(request);
    }

    public void signinUser(final User user, final DbResponse<User> dbResponse) {
        if(!isNetworkConnected()) {
            dbResponse.onError(new Error(context.getString(R.string.network_connection_error)));
            return;
        }

        String url = hostUrl + "/login?username=" + user.getUsername() + "&password=" + user.getPassword();
        String userJson = null;

        try {
            userJson = gson.toJson(user);
        } catch(Exception exception) {
            exception.printStackTrace();
            dbResponse.onError(new Error(context.getString(R.string.network_json_error)));
            return ;
        }

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(TextUtils.isEmpty(response)) {
                    dbResponse.onError(new Error(context.getString(R.string.network_general_error)));
                    return;
                }

                User resultUser = null;

                try {
                    resultUser = gson.fromJson(response, new TypeToken<User>(){}.getType());
                } catch(Exception exception) {
                    exception.printStackTrace();
                    dbResponse.onError(new Error(context.getString(R.string.network_json_error)));
                    return;
                }
                dbResponse.onSuccess(resultUser);
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                dbResponse.onError(new Error(context.getString(R.string.network_general_error)));
                return;
            }
        };

        final String jsonStr = userJson;
        StringRequest request = new StringRequest(Request.Method.GET, url, responseListener, errorListener) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("X-Parse-Application-Id", appId);
                headers.put("X-Parse-REST-API-Key", apiKey);
                headers.put("X-Parse-Revocable-Session", "1");
                return headers;
            }
        };
        requestQueue.add(request);
    }

    public void getStories(final String userToken, final DbResponse<List<Story>> dbResponse) {
        if(!isNetworkConnected()) {
            dbResponse.onError(new Error(context.getString(R.string.network_connection_error)));
            return;
        }

        String url = hostUrl + "/classes/story";

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(TextUtils.isEmpty(response)) {
                    dbResponse.onError(new Error(context.getString(R.string.network_general_error)));
                    return;
                }

                List<Story> resultStories = null;

                try {
                    Log.i(TAG, response);
                    String[] splitedRes = response.split("\"results\":")[1].split("");
                    splitedRes[splitedRes.length -1] = "";
                    resultStories = gson.fromJson(String.join("", splitedRes), new TypeToken<List<Story>>(){}.getType());
                } catch(Exception exception) {
                    exception.printStackTrace();
                    dbResponse.onError(new Error(context.getString(R.string.network_json_error)));
                    return;
                }
                dbResponse.onSuccess(resultStories);
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                dbResponse.onError(new Error(context.getString(R.string.network_general_error)));
                return;
            }
        };

        StringRequest request = new StringRequest(Request.Method.GET, url, responseListener, errorListener) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("X-Parse-Application-Id", appId);
                headers.put("X-Parse-REST-API-Key", apiKey);
                headers.put("X-Parse-Session-Token", userToken);
                headers.put("X-Parse-Revocable-Session", "1");
                return headers;
            }
        };
        requestQueue.add(request);
    }

    public void getUserById(final String userId, final DbResponse<User> dbResponse) {
        if(!isNetworkConnected()) {
            dbResponse.onError(new Error(context.getString(R.string.network_connection_error)));
            return;
        }

        String url = hostUrl + "/users?where={\"objectId\": \"" + userId + "\"}";

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(TextUtils.isEmpty(response)) {
                    dbResponse.onError(new Error(context.getString(R.string.network_general_error)));
                    return;
                }

                List<User> resultUser = null;

                try {
                    Log.i(TAG, response);
                    String[] splitedRes = response.split("\"results\":")[1].split("");
                    splitedRes[splitedRes.length -1] = "";
                    resultUser = gson.fromJson(String.join("", splitedRes), new TypeToken<List<User>>(){}.getType());
                } catch(Exception exception) {
                    exception.printStackTrace();
                    dbResponse.onError(new Error(context.getString(R.string.network_json_error)));
                    return;
                }
                dbResponse.onSuccess(resultUser.get(0));
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                dbResponse.onError(new Error(context.getString(R.string.network_general_error)));
                return;
            }
        };

        StringRequest request = new StringRequest(Request.Method.GET, url, responseListener, errorListener) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("X-Parse-Application-Id", appId);
                headers.put("X-Parse-REST-API-Key", apiKey);
                headers.put("X-Parse-Revocable-Session", "1");
                return headers;
            }
        };
        requestQueue.add(request);
    }

    public void getUserStories(final String userToken, final User currentUser, final DbResponse<List<Story>> dbResponse) {
        if(!isNetworkConnected()) {
            dbResponse.onError(new Error(context.getString(R.string.network_connection_error)));
            return;
        }

        String url = hostUrl + "/classes/story?where={\"userId\": \"" + currentUser.getId() + "\"}";

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(TextUtils.isEmpty(response)) {
                    dbResponse.onError(new Error(context.getString(R.string.network_general_error)));
                    return;
                }

                List<Story> resultStories = null;

                try {
                    Log.i(TAG, response);
                    String[] splitedRes = response.split("\"results\":")[1].split("");
                    splitedRes[splitedRes.length -1] = "";
                    resultStories = gson.fromJson(String.join("", splitedRes), new TypeToken<List<Story>>(){}.getType());
                } catch(Exception exception) {
                    exception.printStackTrace();
                    dbResponse.onError(new Error(context.getString(R.string.network_json_error)));
                    return;
                }
                dbResponse.onSuccess(resultStories);
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                dbResponse.onError(new Error(context.getString(R.string.network_general_error)));
                return;
            }
        };

        StringRequest request = new StringRequest(Request.Method.GET, url, responseListener, errorListener) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("X-Parse-Application-Id", appId);
                headers.put("X-Parse-REST-API-Key", apiKey);
                headers.put("X-Parse-Session-Token", userToken);
                headers.put("X-Parse-Revocable-Session", "1");
                return headers;
            }
        };
        requestQueue.add(request);
    }

    public void insertStory(final Story story, final String userToken, final DbResponse<Story> dbResult) {
        if(!isNetworkConnected()) {
            dbResult.onError(new Error(context.getString(R.string.network_connection_error)));
            return;
        }

        String url = hostUrl + "/classes/story";
        String storyJson = null;
        try {
            storyJson = gson.toJson(story);
        } catch(Exception exception) {
            exception.printStackTrace();
            dbResult.onError(new Error(context.getString(R.string.network_json_error)));
            return;
        }

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(TextUtils.isEmpty(response)) {
                    dbResult.onError(new Error(context.getString(R.string.network_general_error)));
                    return;
                }

                Story resultStory = null;

                try {
                    resultStory = gson.fromJson(response, new TypeToken<Story>(){}.getType());
                } catch(Exception exception) {
                    exception.printStackTrace();
                    dbResult.onError(new Error(context.getString(R.string.network_json_error)));
                    return;
                }
                dbResult.onSuccess(resultStory);
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                dbResult.onError(new Error(context.getString(R.string.network_general_error)));
                return;
            }
        };

        final String jsonStr = storyJson;
        StringRequest request = new StringRequest(Request.Method.POST, url, responseListener, errorListener) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("X-Parse-Application-Id", appId);
                headers.put("X-Parse-REST-API-Key", apiKey);
                headers.put("X-Parse-Revocable-Session", "1");
                headers.put("X-Parse-Session-Token", userToken);

                return headers;
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                return jsonStr.getBytes(StandardCharsets.UTF_8);
            }
        };
        requestQueue.add(request);
    }

    public void updateStory(final Story story, final String userToken, final DbResponse<Story> dbResponse) {
        if(!isNetworkConnected()) {
            dbResponse.onError(new Error(context.getString(R.string.network_connection_error)));
            return;
        }

        String url = hostUrl + "/classes/story/" + story.getId();
        String storyJson = null;
        try {
            storyJson = gson.toJson(story);
        } catch(Exception exception) {
            exception.printStackTrace();
            dbResponse.onError(new Error(context.getString(R.string.network_json_error)));
            return;
        }

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Story update response: " + response);
                if(TextUtils.isEmpty(response)) {
                    dbResponse.onError(new Error(context.getString(R.string.network_general_error)));
                    return;
                }

                Story resultStory = null;

                try {
                    resultStory = gson.fromJson(response, new TypeToken<Story>(){}.getType());
                } catch(Exception exception) {
                    exception.printStackTrace();
                    dbResponse.onError(new Error(context.getString(R.string.network_json_error)));
                    return;
                }
                dbResponse.onSuccess(resultStory);
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                dbResponse.onError(new Error(context.getString(R.string.network_general_error)));
                return;
            }
        };

        final String jsonStr = storyJson;
        StringRequest request = new StringRequest(Request.Method.PUT, url, responseListener, errorListener) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("X-Parse-Application-Id", appId);
                headers.put("X-Parse-REST-API-Key", apiKey);
                headers.put("X-Parse-Revocable-Session", "1");
                headers.put("X-Parse-Session-Token", userToken);

                return headers;
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                return jsonStr.getBytes(StandardCharsets.UTF_8);
            }
        };
        requestQueue.add(request);
    }

    public void deleteStory(final Story story, final User currentUser, String userToken, final DbResponse<String> dbResponse) {
        if(!isNetworkConnected()) {
            dbResponse.onError(new Error(context.getString(R.string.network_connection_error)));
            return;
        }

        if(!story.getUserId().equals(currentUser.getId())) {
            dbResponse.onError(new Error(context.getString(R.string.story_delete_access_denied)));
            return;
        }

        String url = hostUrl + "/classes/story/" + story.getId();

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Story delete response: " + response);
                if(TextUtils.isEmpty(response)) {
                    dbResponse.onError(new Error(context.getString(R.string.network_general_error)));
                    return;
                }
                dbResponse.onSuccess(response);
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                dbResponse.onError(new Error(context.getString(R.string.network_general_error)));
                return;
            }
        };

        StringRequest request = new StringRequest(Request.Method.DELETE, url, responseListener, errorListener) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("X-Parse-Application-Id", appId);
                headers.put("X-Parse-REST-API-Key", apiKey);
                headers.put("X-Parse-Revocable-Session", "1");
                headers.put("X-Parse-Session-Token", userToken);

                return headers;
            }
        };
        requestQueue.add(request);
    }
}
