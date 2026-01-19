package dev.mb_labs.travelshare.database;

import androidx.room.TypeConverter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;
import dev.mb_labs.travelshare.model.Frame;

public class Converters {
    @TypeConverter
    public static List<Frame.Photo> fromString(String value) {
        if (value == null) {
            return Collections.emptyList();
        }
        Type listType = new TypeToken<List<Frame.Photo>>() {}.getType();
        return new Gson().fromJson(value, listType);
    }

    @TypeConverter
    public static String fromList(List<Frame.Photo> list) {
        return new Gson().toJson(list);
    }
}