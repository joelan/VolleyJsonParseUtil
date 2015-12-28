package com.example.administrator.dategetutils.Utils.GsonUtil;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

/**
 * 自定义Gson解析
 * @author Aries
 *
 * @create 2015-11-3 上午10:05:08
 */
public class StringConverter implements JsonSerializer<String>,
		JsonDeserializer<String> {
	public JsonElement serialize(String src, Type typeOfSrc,
			JsonSerializationContext context) {
		if (src == null) {
			return new JsonPrimitive("");
		} else {
			return new JsonPrimitive(src.toString());
		}
	}

	public String deserialize(JsonElement json, Type typeOfT,
			JsonDeserializationContext context) throws JsonParseException {
		try{
			String str = json.getAsJsonPrimitive().getAsString();
//			Log.d("StringConverter", "str::"+str);
			return str;
		}catch (Exception e){
			return "";
		}
//		return json.getAsJsonPrimitive().getAsString();
	}
}
