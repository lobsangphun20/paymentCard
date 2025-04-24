package example.cashcard;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import example.cashcard.domainDefinition.CashCard;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class CashCardJsonTest {

    @Autowired
    private JacksonTester<CashCard> json;
    
    String expected = """
            {
                "id":99,
                "amount":123.45,
                "owner":"sarah1"
            }
    		""";

    @Test
    void cashCardSerializationTest() throws IOException {
        CashCard cashCard = new CashCard(99L, 123.45, "sarah1");
        assertThat(json.write(cashCard)).isStrictlyEqualToJson(expected);
        assertThat(json.write(cashCard)).hasJsonPathNumberValue("@.id");
        assertThat(json.write(cashCard)).extractingJsonPathNumberValue("@.id")
                .isEqualTo(99);
        assertThat(json.write(cashCard)).hasJsonPathNumberValue("@.amount");
        assertThat(json.write(cashCard)).extractingJsonPathNumberValue("@.amount")
             .isEqualTo(123.45);
    }
    
    @Test
    void cashCardDeserializationTest() throws IOException {
       assertThat(json.parse(expected))
               .isEqualTo(new CashCard(99L, 123.45, "sarah1"));
       assertThat(json.parseObject(expected).id()).isEqualTo(99L);
       assertThat(json.parseObject(expected).amount()).isEqualTo(123.45);
    }
    
}