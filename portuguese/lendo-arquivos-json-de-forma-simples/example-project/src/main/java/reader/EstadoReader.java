package reader;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import entity.Estado;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

public class EstadoReader  extends AbstracrReader {
    public static Estado readOneFrom(String path) throws IOException {
        String jsonText = readJson(path);

        Type collectionType = new TypeToken<Estado>(){}.getType();
        return new Gson().fromJson(jsonText,collectionType);
    }

    public static List<Estado> readListFrom(String path) throws IOException {
        String jsonText = readJson(path);
        Type collectionType = new TypeToken<List<Estado>>(){}.getType();
        return new Gson().fromJson(jsonText, collectionType);

    }
}
