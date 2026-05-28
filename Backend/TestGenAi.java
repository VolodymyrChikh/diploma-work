import com.google.genai.types.Content;
import com.google.genai.types.Part;
import java.util.List;
public class TestGenAi {
    public static void main(String[] args) {
        Content c = Content.builder().role("user").parts(List.of(Part.builder().text("hello").build())).build();
        System.out.println(c.role() + " " + c.parts().get(0).text());
    }
}
