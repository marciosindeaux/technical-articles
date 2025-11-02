package reader;

import com.google.gson.JsonSyntaxException;
import entity.Regiao;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.util.List;

import static org.junit.Assert.*;


public class ResgiaoReaderTest {

    @Test
    public void lerUmaRegiaoTest() throws IOException {
        Regiao regiao = RegiaoReader.readOneFrom("./static/RegiaoNorte.json");
        assertNotNull(regiao);
        assertTrue(regiao instanceof Regiao);
        assertNotNull(regiao.getEstados());
        assertEquals(7,regiao.getEstados().size());
    }

    @Test(expected = NoSuchFileException.class)
    public void lerUmaRegiaoPathErradoTest() throws IOException {
        RegiaoReader.readOneFrom("./static/Regiao.json");
    }

    @Test
    public void lerListaRegioes() throws IOException {
        List<Regiao> regioes = RegiaoReader.readListFrom("./static/Regioes.json");
        assertNotNull(regioes);
        assertFalse(regioes.isEmpty());
        regioes.forEach(item -> {
            assertTrue(item instanceof Regiao);
            assertNotNull( item);
            assertNotNull(item.getNome());
        });
    }

    @Test(expected = NoSuchFileException.class)
    public void lerListaRegioesPathErradoTest() throws IOException {
        RegiaoReader.readListFrom("./static/Regiao.json");
    }

    @Test(expected = JsonSyntaxException.class)
    public void lerListaRegioesJsonErradoTest() throws IOException {
        RegiaoReader.readListFrom("./static/RegiaoNorte.json");
    }
}
