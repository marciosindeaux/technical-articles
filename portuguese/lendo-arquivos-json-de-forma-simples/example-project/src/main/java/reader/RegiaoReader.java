package reader;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import entity.Regiao;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

public class RegiaoReader extends AbstracrReader{

    public static Regiao readOneFrom(String path) throws IOException {
        String jsonText = readJson(path);
        Type collectionType = new TypeToken<Regiao>(){}.getType();
        return new Gson().fromJson(jsonText,collectionType);
    }

    public static List<Regiao> readListFrom(String path) throws IOException {
        String jsonText = readJson(path);
        Type collectionType = new TypeToken<List<Regiao>>(){}.getType();
        return new Gson().fromJson(jsonText, collectionType);

    }
}
