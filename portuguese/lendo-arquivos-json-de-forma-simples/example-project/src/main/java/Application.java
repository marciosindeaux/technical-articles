import entity.Regiao;
import reader.RegiaoReader;

import java.io.IOException;
import java.nio.file.NoSuchFileException;

public class Application {
    public static void main(String[] args) {
        String path = "./static/RegiaoNorte.json";
        try {
            Regiao regiao = RegiaoReader.readOneFrom(path);
            System.out.println(regiao);
        } catch (NoSuchFileException e) {
            System.err.println("Não foi possivel encontrar o json no diretorio "+path);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
