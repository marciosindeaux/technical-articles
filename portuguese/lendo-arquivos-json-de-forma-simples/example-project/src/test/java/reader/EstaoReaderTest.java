package reader;

import com.google.gson.JsonSyntaxException;
import entity.Estado;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.util.List;

import static org.junit.Assert.*;

public class EstaoReaderTest {
    @Test
    public void lerUmaRegiaoTest() throws IOException {

        Estado estado = EstadoReader.readOneFrom("./static/Tocantins.json");
        assertNotNull(estado);
        assertTrue(estado instanceof Estado);
        assertNotNull(estado.getRegiao());
    }

    @Test(expected = NoSuchFileException.class)
    public void lerUmaRegiaoPathErradoTest() throws IOException {
        RegiaoReader.readOneFrom("./static/Toca.json");
    }

    @Test
    public void lerListaRegioes() throws IOException {
        List<Estado> estados = EstadoReader.readListFrom("./static/Estados.json");
        assertNotNull(estados);
        assertFalse(estados.isEmpty());
        estados.forEach(item -> {
            assertTrue(item instanceof Estado);
            assertNotNull( item);
            assertNotNull(item.getNome());
        });
    }

    @Test(expected = NoSuchFileException.class)
    public void lerListaRegioesPathErradoTest() throws IOException {
        RegiaoReader.readListFrom("./static/Regiaos.json");
    }

    @Test(expected = JsonSyntaxException.class)
    public void lerListaRegioesJsonErradoTest() throws IOException {
        RegiaoReader.readListFrom("./static/Tocantins.json");
    }
}
