package com.example.administrator.dategetutils.Utils;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.example.administrator.dategetutils.Interface.Callback;
import com.example.administrator.dategetutils.Interface.callAfterDownload;
import com.example.administrator.dategetutils.Utils.GsonUtil.GsonStringUtils;
import com.example.administrator.dategetutils.resultbean.erroinfo;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class PostDatatoServer {

	Context context;
	Object result = null;
	Type type;
	Class<?> clsAll;

	//主进程做的的listener
	Callback call;

	//线程做的listener
	callAfterDownload callad;

//	public callAfterDownload getCallad() {
//		return callad;
//	}

	public void setCallad(callAfterDownload callad) {
		this.callad = callad;
	}
   //是否是错误信息
	Boolean iserro = false;

	//volly队列
	private RequestQueue requestQueue;
	//队列标志
	private String TAG = Mycontasts.QueueTAG;



	// private EncryptionDecryption eDecryption = new EncryptionDecryption();

	public Boolean getIserro() {
		return iserro;
	}

	public void setIserro(Boolean iserro) {
		this.iserro = iserro;
	}

	public void setcallbacklistener(Callback call) {
		this.call = call;
	}

	public PostDatatoServer(Context context) {

		this.context = context;
		requestQueue = App.requestQueue;
		Log.e(TAG, "getAbsolutePath()---------"
				+ this.context.getCacheDir().getAbsolutePath());
		//mAppendPost = App.getAppendPost();
	}


	/**
	 * 通用的网络请求
	 * 
	 * @param info
	 * @param url
	 * @param cls
	 * @return
	 */
	public <T> StringRequest postdata(final T info, final String url, final Class<?> cls) {

		this.clsAll = cls;
		StringRequest postRequest = new StringRequest(Request.Method.POST, url,
				new Response.Listener<String>() {
					@Override
					public void onResponse(String response) {

						Gson gson = GsonStringUtils.getInstance().gson;

						String erro = null;
						try {
							Log.i(TAG, "this is erro in");
							JSONObject oject = new JSONObject(response);

							erro = (String) oject.get("error");
							if (erro == null) {
								Log.i(TAG, "error is null");
							}
							if (erro != null) {
								iserro = true;
								result = gson.fromJson(response,
										new TypeToken<erroinfo>() {
										}.getType());

							}

						} catch (JSONException e) {
							Log.i(TAG, "this is success");
							iserro = false;
							try {
								result = gson.fromJson(response.trim(), clsAll);
							} catch (Exception e2) {
								Log.i(TAG,
										"---gson.fromJson----" + e2.toString());
							}
							// Log.i("ddd","000");
							e.printStackTrace();
							// if(is)
							//

						} finally {

							if(!iserro)
							{
								result=	clsAll.cast(result);
							}
							else {
                                 result=erroinfo.class.cast(result);
							}


							if (call != null) {
								Log.i(TAG, "----iserro---" + iserro);
								if (result == null) {
									Log.i(TAG, "result is null");

									call.Finish(result);
									return;
								}
								if (iserro) {

									//ErrorFormatToCn();

								} else {

								}


								call.Finish(result);
							}

							if (callad != null) {
								Log.i(TAG, "----iserro---" + iserro);
								if (result == null) {
									Log.i(TAG, "result is null");
									callad.SubmitTask(result);
									return;
								}
								if (iserro) {

									//ErrorFormatToCn();

								} else {

								}

								callad.SubmitTask(result);
							}

						}

					}

				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						// error

						iserro = true;
						erroinfo info = new erroinfo();


						Log.i(TAG, error.toString());
						if (error instanceof NetworkError
								|| error instanceof NoConnectionError
								|| error instanceof TimeoutError) {
							Log.e(TAG, "error instanceof NetworkError"
									+ "|| error instanceof NoConnectionError"
									+ "|| error instanceof TimeoutError");
							info.setError("networkerror");
						/*	if (call != null)
								call.networkerro();*/
							if (callad != null)
								callad.networktask();

						} else {
							Toast.makeText(context, "未知错误", Toast.LENGTH_SHORT)
									.show();
							// call.networkerro();
							info.setError("Servererro");
							if (call != null)
								call.finish(info);
							if (callad != null) {
								callad.SubmitTask(info);
							}
						}
						result = info;

					}
				}) {

			@Override
			protected Map<String, String> getParams() {
				// POST 参数 通过放射机制将参数提取出来 null的值将不被添加做参数
				Map<String, String> params = new HashMap<String, String>();
				try {
					if (info != null) {
						Field[] fields = info.getClass().getDeclaredFields();
						int length = fields.length;
						for (int i = 0; i < length; i++) {
							fields[i].setAccessible(true);
							Object value = fields[i].get(info);
								if (value != null) {
									if (value instanceof Enum) {
										// params.put(fields[i].getName(),((Enum<?>)
										// value).ordinal());
										//如果是枚举型例外处理。
									} else {
										params.put(fields[i].getName(), value
												+ "");
									}
								}


						}
					}

				} catch (Exception e) {
					Log.i("somethiing wrong", e.toString());
				}
				Log.e("params:", "----------" + url + "------");
				Log.e("params", params.toString());
				return params;
			}

			@Override
			protected Response<String> parseNetworkResponse(
					NetworkResponse response) {
				String parsed = null;
				try {
					parsed = new String(response.data, "UTF-8");
				} catch (UnsupportedEncodingException e) {
					parsed = new String(response.data);
				}
				return Response.success(parsed,
						HttpHeaderParser.parseCacheHeaders(response));
			}


			@Override
			public Request<?> setRetryPolicy(RetryPolicy retryPolicy) {
				return super.setRetryPolicy(new DefaultRetryPolicy(5000, 1,
						1.0f));
			}

		};
		// postRequest.setRetryPolicy(new DefaultRetryPolicy(5000, 2, 1.0f));
		Log.i(TAG, "postdata_middle******");
		postRequest.setTag(TAG);
		requestQueue.add(postRequest);
		Log.i(TAG, "postdata_end******");
		return postRequest;
	}


	/**
	 * 通用的网络请求，根据class解释的，在线程里面解释json
	 *
	 * @param info
	 * @param url
	 * @param cls
	 * @return
	 */
	public <T> StringRequest postdataparcelinThread(final T info, final String url, final Class<?> cls) {

		this.clsAll = cls;
		StringRequest postRequest = new StringRequest(Request.Method.POST, url,
				new Response.Listener<String>() {
					@Override
					public void onResponse(String response) {


						new ParcelTask().execute(response);



					}

				}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				// error

				iserro = true;
				erroinfo info = new erroinfo();


				Log.i(TAG, error.toString());
				if (error instanceof NetworkError
						|| error instanceof NoConnectionError
						|| error instanceof TimeoutError) {
					Log.e(TAG, "error instanceof NetworkError"
							+ "|| error instanceof NoConnectionError"
							+ "|| error instanceof TimeoutError");
					info.setError("networkerror");
						/*	if (call != null)
								call.networkerro();*/
					if (callad != null)
						callad.networktask();
					if(call!=null)
					{
						call.networkerro();
					}


				} else {
					Toast.makeText(context, "未知错误", Toast.LENGTH_SHORT)
							.show();
					// call.networkerro();
					info.setError("Servererro");
					if (call != null)
						call.finish(info);
					if (callad != null) {
						callad.SubmitTask(info);
					}
				}
				result = info;

			}
		}) {

			@Override
			protected Map<String, String> getParams() {
				// POST 参数 通过放射机制将参数提取出来 null的值将不被添加做参数
				Map<String, String> params = new HashMap<String, String>();
				try {
					if (info != null) {
						Field[] fields = info.getClass().getDeclaredFields();
						int length = fields.length;
						for (int i = 0; i < length; i++) {
							fields[i].setAccessible(true);
							Object value = fields[i].get(info);
							if (value != null) {
								if (value instanceof Enum) {
									// params.put(fields[i].getName(),((Enum<?>)
									// value).ordinal());
									//如果是枚举型例外处理。
								} else {
									params.put(fields[i].getName(), value
											+ "");
								}
							}


						}
					}

				} catch (Exception e) {
					Log.i("somethiing wrong", e.toString());
				}
				Log.e("params:", "----------" + url + "------");
				Log.e("params", params.toString());
				return params;
			}

			@Override
			protected Response<String> parseNetworkResponse(
					NetworkResponse response) {
				String parsed = null;
				try {
					parsed = new String(response.data, "UTF-8");
				} catch (UnsupportedEncodingException e) {
					parsed = new String(response.data);
				}
				return Response.success(parsed,
						HttpHeaderParser.parseCacheHeaders(response));
			}


			@Override
			public Request<?> setRetryPolicy(RetryPolicy retryPolicy) {
				return super.setRetryPolicy(new DefaultRetryPolicy(5000, 1,
						1.0f));
			}

		};
		// postRequest.setRetryPolicy(new DefaultRetryPolicy(5000, 2, 1.0f));
		Log.i(TAG, "postdata_middle******");
		postRequest.setTag(TAG);
		requestQueue.add(postRequest);
		Log.i(TAG, "postdata_end******");
		return postRequest;
	}

	/**
	 *    Type类型解释Json,解释在主进程
	 * @param info
	 * @param url
	 * @param tp
	 * @param <T>
	 * @return
	 */
	public <T> StringRequest postdata(final T info, final String url, Type tp) {


		this.type = tp;
		// Log.i(TAG, "postdata_start******");
		// Thread thread = Thread.currentThread();
		// Log.e(TAG,"Thread: " + thread);
		// Log.e(TAG,"Thread Id: " + thread.getId());
		StringRequest postRequest = new StringRequest(Request.Method.POST, url,
				new Response.Listener<String>() {
					@Override
					public void onResponse(String response) {

						Gson gson = GsonStringUtils.getInstance().gson;
						String erro = null;
						try {
							Log.i(TAG, "this is erro in");
							JSONObject oject = new JSONObject(response);

							erro = (String) oject.get("error");
							if (erro == null) {
								Log.i(TAG, "error is null");
							}
							if (erro != null) {
								iserro = true;
								result = gson.fromJson(response,
										new TypeToken<erroinfo>() {
										}.getType());

							}

						} catch (JSONException e) {
							Log.i(TAG, "this is success");

							iserro = false;
							try {
								result = gson.fromJson(response.trim(), type);
							} catch (Exception e2) {
								Log.i(TAG,
										"---gson.fromJson----" + e2.toString());
							}
							// Log.i("ddd","000");
							e.printStackTrace();
							// if(is)
							//

						}

						finally {

							if(!iserro)
							{
								//result=	type.getClass().cast(result);
							}
							else {
								result=erroinfo.class.cast(result);
							}

							if (call != null) {
								Log.i(TAG, "----iserro---" + iserro);
								if (result == null) {
									Log.i(TAG, "result is null");
									call.Finish(result);
									return;
								}
								if (iserro) {

									//ErrorFormatToCn();

								} else {

								}
								call.Finish(result);
							}

							if (callad != null) {
								Log.i(TAG, "----iserro---" + iserro);
								if (result == null) {
									Log.i(TAG, "result is null");
									callad.SubmitTask(result);
									return;
								}
								if (iserro) {

									//ErrorFormatToCn();

								} else {

								}

								callad.SubmitTask(result);
							}

						}

					}

				}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				// error

				iserro = true;
				erroinfo info = new erroinfo();

				result = info;
				Log.i(TAG, error.toString());
				if (error instanceof NetworkError
						|| error instanceof NoConnectionError
						|| error instanceof TimeoutError) {
					Log.e(TAG, "error instanceof NetworkError"
							+ "|| error instanceof NoConnectionError"
							+ "|| error instanceof TimeoutError");
					info.setError("networkerror");
						/*	if (call != null)
								call.networkerro();*/
					if (call != null)
						call.networkerro();

					if (callad != null)
						callad.networktask();

				} else {
					Toast.makeText(context, "未知错误", Toast.LENGTH_SHORT)
							.show();
					// call.networkerro();
					info.setError("Servererro");
					if (call != null)
						call.Finish(info);
					if (callad != null) {
						callad.SubmitTask(info);
					}
				}

			}
		}) {

			@Override
			protected Map<String, String> getParams() {
				// POST 参数 通过放射机制将参数提取出来 null的值将不被添加做参数
				Map<String, String> params = new HashMap<String, String>();
				try {
					if (info != null) {
						Field[] fields = info.getClass().getDeclaredFields();
						int length = fields.length;
						for (int i = 0; i < length; i++) {
							fields[i].setAccessible(true);
							Object value = fields[i].get(info);
							if (value != null) {
								if (value instanceof Enum) {
									// params.put(fields[i].getName(),((Enum<?>)
									// value).ordinal());
									//如果是枚举型例外处理。
								} else {
									params.put(fields[i].getName(), value
											+ "");
								}
							}


						}
					}

				} catch (Exception e) {
					Log.i("somethiing wrong", e.toString());
				}
				Log.e("params:", "----------" + url + "------");
				Log.e("params", params.toString());
				return params;
			}

			@Override
			protected Response<String> parseNetworkResponse(
					NetworkResponse response) {
				String parsed = null;
				try {
					parsed = new String(response.data, "UTF-8");
				} catch (UnsupportedEncodingException e) {
					parsed = new String(response.data);
				}
				return Response.success(parsed,
						HttpHeaderParser.parseCacheHeaders(response));
			}


			@Override
			public Request<?> setRetryPolicy(RetryPolicy retryPolicy) {
				return super.setRetryPolicy(new DefaultRetryPolicy(5000, 1,
						1.0f));
			}

		};
		// postRequest.setRetryPolicy(new DefaultRetryPolicy(5000, 2, 1.0f));
		Log.i(TAG, "postdata_middle******");
		postRequest.setTag(TAG);
		requestQueue.add(postRequest);
		Log.i(TAG, "postdata_end******");
		return postRequest;
	}

	/**
	 * 线程里解释Json，支持List列表的返回类型
	 * @param info
	 * @param url
	 * @param tp
	 * @param <T>
	 * @return
	 */
	public <T> StringRequest postdataInThreadType(final T info, final String url, Type tp) {


		this.type = tp;
		// Log.i(TAG, "postdata_start******");
		// Thread thread = Thread.currentThread();
		// Log.e(TAG,"Thread: " + thread);
		// Log.e(TAG,"Thread Id: " + thread.getId());
		StringRequest postRequest = new StringRequest(Request.Method.POST, url,
				new Response.Listener<String>() {
					@Override
					public void onResponse(String response) {

						new ParcelTypeTask().execute(response);



//						finally {
//
//							if(!iserro)
//							{
//								//result=	type.getClass().cast(result);
//							}
//							else {
//								result=erroinfo.class.cast(result);
//							}
//
//							if (call != null) {
//								Log.i(TAG, "----iserro---" + iserro);
//								if (result == null) {
//									Log.i(TAG, "result is null");
//									call.Finish(result);
//									return;
//								}
//								if (iserro) {
//
//									//ErrorFormatToCn();
//
//								} else {
//
//								}
//								call.Finish(result);
//							}
//
//							if (callad != null) {
//								Log.i(TAG, "----iserro---" + iserro);
//								if (result == null) {
//									Log.i(TAG, "result is null");
//									callad.SubmitTask(result);
//									return;
//								}
//								if (iserro) {
//
//									//ErrorFormatToCn();
//
//								} else {
//
//								}
//
//								callad.SubmitTask(result);
//							}
//
//						}
//
					}

				}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				// error

				iserro = true;
				erroinfo info = new erroinfo();

				result = info;
				Log.i(TAG, error.toString());
				if (error instanceof NetworkError
						|| error instanceof NoConnectionError
						|| error instanceof TimeoutError) {
					Log.e(TAG, "error instanceof NetworkError"
							+ "|| error instanceof NoConnectionError"
							+ "|| error instanceof TimeoutError");
					info.setError("networkerror");
						/*	if (call != null)
								call.networkerro();*/
					if (call != null)
						call.networkerro();

					if (callad != null)
						callad.networktask();

				} else {
					Toast.makeText(context, "未知错误", Toast.LENGTH_SHORT)
							.show();
					// call.networkerro();
					info.setError("Servererro");
					if (call != null)
						call.Finish(info);
					if (callad != null) {
						callad.SubmitTask(info);
					}
				}

			}
		}) {

			@Override
			protected Map<String, String> getParams() {
				// POST 参数 通过放射机制将参数提取出来 null的值将不被添加做参数
				Map<String, String> params = new HashMap<String, String>();
				try {
					if (info != null) {
						Field[] fields = info.getClass().getDeclaredFields();
						int length = fields.length;
						for (int i = 0; i < length; i++) {
							fields[i].setAccessible(true);
							Object value = fields[i].get(info);
							if (value != null) {
								if (value instanceof Enum) {
									// params.put(fields[i].getName(),((Enum<?>)
									// value).ordinal());
									//如果是枚举型例外处理。
								} else {
									params.put(fields[i].getName(), value
											+ "");
								}
							}


						}
					}

				} catch (Exception e) {
					Log.i("somethiing wrong", e.toString());
				}
				Log.e("params:", "----------" + url + "------");
				Log.e("params", params.toString());
				return params;
			}

			@Override
			protected Response<String> parseNetworkResponse(
					NetworkResponse response) {
				String parsed = null;
				try {
					parsed = new String(response.data, "UTF-8");
				} catch (UnsupportedEncodingException e) {
					parsed = new String(response.data);
				}
				return Response.success(parsed,
						HttpHeaderParser.parseCacheHeaders(response));
			}


			@Override
			public Request<?> setRetryPolicy(RetryPolicy retryPolicy) {
				return super.setRetryPolicy(new DefaultRetryPolicy(5000, 1,
						1.0f));
			}

		};
		// postRequest.setRetryPolicy(new DefaultRetryPolicy(5000, 2, 1.0f));
		Log.i(TAG, "postdata_middle******");
		postRequest.setTag(TAG);
		requestQueue.add(postRequest);
		Log.i(TAG, "postdata_end******");
		return postRequest;
	}


	public Object getResult() {
		return result;
	}

	public void setResult(Object result) {
		this.result = result;
	}

	/**
	 * 根据type的后台解释动作
	 * @param response
	 */
	private void parcelTypeInbackground(String response)
	{
		Gson gson = GsonStringUtils.getInstance().gson;
		String erro = null;
		try {
			Log.i(TAG, "this is erro in");
			JSONObject oject = new JSONObject(response);

			erro = (String) oject.get("error");
			if (erro == null) {
				Log.i(TAG, "error is null");
			}
			if (erro != null) {
				iserro = true;
				result = gson.fromJson(response,
						new TypeToken<erroinfo>() {
						}.getType());

			}

		} catch (JSONException e) {
			Log.i(TAG, "this is success");

			iserro = false;
			try {
				result = gson.fromJson(response.trim(), type);
			} catch (Exception e2) {
				Log.i(TAG,
						"---gson.fromJson----" + e2.toString());
			}
			// Log.i("ddd","000");
			e.printStackTrace();
			// if(is)
			//

		}

	}

	/**
	 * 根据class的后台解释动作
	 * @param response
	 */
	private void parcelInbackground(String response)
	{
		Log.i("parcelInbackground",response);
		Gson gson = GsonStringUtils.getInstance().gson;

		String erro = null;
		try {
			Log.i(TAG, "this is erro in");
			JSONObject oject = new JSONObject(response);

			erro = (String) oject.get("error");
			if (erro == null) {
				Log.i(TAG, "error is null");
			}
			if (erro != null) {
				iserro = true;
				result = gson.fromJson(response,
						new TypeToken<erroinfo>() {
						}.getType());

			}

		} catch (JSONException e) {
			Log.i(TAG, "this is success");
			iserro = false;
			try {
				result = gson.fromJson(response.trim(), clsAll);
			} catch (Exception e2) {
				Log.i(TAG,
						"---gson.fromJson----" + e2.toString());
			}
			// Log.i("ddd","000");
			e.printStackTrace();
			// if(is)
			//

		}

	}

	/**
	 * 通用的Json解释完毕后要做的
	 */
	private void DoAfterParcel()
	{
//		if(clsAll==null)
//		{
//			Log.i("clsAll","clasAll is null");
//		}
//		if(!iserro&&clsAll!=null)
//		{
//			result=	clsAll.cast(result);
//		}
//		else {
//			result=erroinfo.class.cast(result);
//		}


		if (call != null) {
			Log.i(TAG, "----iserro---" + iserro);
			if (result == null) {
				Log.i(TAG, "result is null");

				call.Finish(result);
				return;
			}
			if (iserro) {

				//ErrorFormatToCn();

			} else {

			}


			call.Finish(result);
		}

		if (callad != null) {
			Log.i(TAG, "----iserro---" + iserro);
			if (result == null) {
				Log.i(TAG, "result is null");
				callad.SubmitTask(result);
				return;
			}
			if (iserro) {

				//ErrorFormatToCn();

			} else {

			}

			callad.SubmitTask(result);
		}


	}


class ParcelTask extends AsyncTask<String,Integer,Boolean>
{
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
	}

	@Override
	protected void onPostExecute(Boolean aBoolean) {
		super.onPostExecute(aBoolean);

		DoAfterParcel();
	}

	@Override
	protected Boolean doInBackground(String... params) {

		String response=params[0];

		Log.i("response",response);

		parcelInbackground(response);


		return true;
	}
}

	class ParcelTypeTask extends AsyncTask<String,Integer,Boolean>
	{
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected void onPostExecute(Boolean aBoolean) {
			super.onPostExecute(aBoolean);

			DoAfterParcel();
		}

		@Override
		protected Boolean doInBackground(String... params) {

			String response=params[0];

			Log.i("response",response);

			parcelTypeInbackground(response);


			return true;
		}
	}


}
