package example.cashcard;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CashCardApplicationTests {

	@Autowired
	private TestRestTemplate restTemplate;
	
	
	@Test
    void shouldReturnACashCardWhenDataIsSaved() {
        ResponseEntity<String> response = restTemplate.getForEntity("/cashcards/99", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        
        DocumentContext documentContext = JsonPath.parse(response.getBody());
        Number id = documentContext.read("$.id");
        assertThat(id).isNotNull();
        
        assertThat(id).isEqualTo(99);
        
        Double amount = documentContext.read("$.amount");
        assertThat(amount).isEqualTo(123.45);
        
        
    }
	
	
	@Test
	void shouldNotReturnACashCardWithAnUnknownId() {
	  ResponseEntity<String> response = restTemplate.getForEntity("/cashcards/1000", String.class);
	
	  assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
	  assertThat(response.getBody()).isBlank();
	}
	
	@Test
	void shouldPostAndPersistIt() {
		
		CashCard cashCard = new CashCard(null, 250.00);
		ResponseEntity<String> savedCashCard = restTemplate.postForEntity("/cashcards", cashCard, String.class);
		assertThat(savedCashCard.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		
		URI locationOfSavedCashCard = savedCashCard.getHeaders().getLocation();
		
		ResponseEntity<String> getResponse = restTemplate.getForEntity(locationOfSavedCashCard, String.class);
		assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

		DocumentContext doc = JsonPath.parse(getResponse.getBody());
		Number id = doc.read("$.id");
		Double amount = doc.read("$.amount");
		assertThat(id).isNotNull();
		assertThat(cashCard.amount()).isEqualTo(amount);
		
	}
}







