package com.codepath.apps.restclienttemplate;

import android.content.Context;

import com.codepath.oauth.OAuthBaseClient;
import com.github.scribejava.apis.FlickrApi;
import com.github.scribejava.apis.TwitterApi;
import com.github.scribejava.core.builder.api.BaseApi;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/*
 * 
 * This is the object responsible for communicating with a REST API. 
 * Specify the constants below to change the API being communicated with.
 * See a full list of supported API classes: 
 *   https://github.com/scribejava/scribejava/tree/master/scribejava-apis/src/main/java/com/github/scribejava/apis
 * Key and Secret are provided by the developer site for the given API i.e dev.twitter.com
 * Add methods for each relevant endpoint in the API.
 * 
 * NOTE: You may want to rename this object based on the service i.e TwitterClient or FlickrClient
 * 
 */
public class TwitterClient extends OAuthBaseClient {
	public static final BaseApi REST_API_INSTANCE = TwitterApi.instance(); // Change this
	public static final String REST_URL = "https://api.twitter.com/1.1"; // Change this, base API URL
	public static final String REST_CONSUMER_KEY = "FeDq2W7I0LXmH9BA1OLDjcVic";       // Change this
	public static final String REST_CONSUMER_SECRET = "W79HPYRbBkWix8i9mvofm0aJChkJhAzOUcoU4rgHg5gLWp0c1F"; // Change this

	// Landing page to indicate the OAuth flow worked in case Chrome for Android 25+ blocks navigation back to the app.
	public static final String FALLBACK_URL = "https://codepath.github.io/android-rest-client-template/success.html";

	// See https://developer.chrome.com/multidevice/android/intents
	public static final String REST_CALLBACK_URL_TEMPLATE = "intent://%s#Intent;action=android.intent.action.VIEW;scheme=%s;package=%s;S.browser_fallback_url=%s;end";

	public TwitterClient(Context context) {
		super(context, REST_API_INSTANCE,
				REST_URL,
				REST_CONSUMER_KEY,
				REST_CONSUMER_SECRET,
				String.format(REST_CALLBACK_URL_TEMPLATE, context.getString(R.string.intent_host),
						context.getString(R.string.intent_scheme), context.getPackageName(), FALLBACK_URL));
	}
	// CHANGE THIS
	// DEFINE METHODS for different API endpoints here
	public void getHomeTimeline(AsyncHttpResponseHandler handler) {
		String apiUrl = getApiUrl("statuses/home_timeline.json");
		// Can specify query string params directly or through RequestParams.
		RequestParams params = new RequestParams();
		params.put("count", 30);
		params.put("since_id", 1);
		params.put("include_entities",true);
		client.get(apiUrl, params, handler);
	}

	public void getNextPageOfTweet(AsyncHttpResponseHandler handler, long maxId){
		String apiUrl = getApiUrl("statuses/home_timeline.json");
		// Can specify query string params directly or through RequestParams.
		RequestParams params = new RequestParams();
		params.put("count", 25);
		params.put("max_id", maxId);
		client.get(apiUrl, params, handler);
	}

	public void getMyUserSettings(AsyncHttpResponseHandler handler){
		String apiUrl = getApiUrl("account/settings.json");
		client.get(apiUrl,handler);
	}

	public void getMyUserInfo(String screenName, AsyncHttpResponseHandler handler){
		String apiUrl = getApiUrl("users/show.json");
		RequestParams params = new RequestParams();
		params.put("screen_name",screenName);
		params.put("include_entities",false);
		client.get(apiUrl,params,handler);
	}

	public void composeTweet(String tweet, AsyncHttpResponseHandler handler){
		String apiUrl = getApiUrl("statuses/update.json");
		RequestParams params = new RequestParams();
		params.put("status",tweet);
		client.post(apiUrl,params,handler);
	}

    public void replyTweet(long id ,String tweet, AsyncHttpResponseHandler handler){
        String apiUrl = getApiUrl("statuses/update.json");
        RequestParams params = new RequestParams();
        params.put("status",tweet);
        params.put("in_reply_to_status_id",id);
        client.post(apiUrl,params,handler);
    }

	public void likeThisTweet(long id, AsyncHttpResponseHandler handler){
		String apiUrl = getApiUrl("favorites/create.json");
		RequestParams params = new RequestParams();
		params.put("id",id);
		client.post(apiUrl,params,handler);
	}

	public void dislikeThistweet(long id, AsyncHttpResponseHandler handler){
		String apiUrl = getApiUrl("favorites/destroy.json");
		RequestParams params = new RequestParams();
		params.put("id",id);
		client.post(apiUrl,params,handler);
	}

	public void retweetThisPost(String id, AsyncHttpResponseHandler handler){
		String apiUrl = getApiUrl("retweet/"+id+".json");
		client.post(apiUrl,handler);
	}

	public void unretweetThisPost(String id,AsyncHttpResponseHandler handler){
		String apiUrl = getApiUrl("unretweet/"+id+".json");
		client.post(apiUrl,handler);
	}

	/* 1. Define the endpoint URL with getApiUrl and pass a relative path to the endpoint
	 * 	  i.e getApiUrl("statuses/home_timeline.json");
	 * 2. Define the parameters to pass to the request (query or body)
	 *    i.e RequestParams params = new RequestParams("foo", "bar");
	 * 3. Define the request method and make a call to the client
	 *    i.e client.get(apiUrl, params, handler);
	 *    i.e client.post(apiUrl, params, handler);
	 */
}
