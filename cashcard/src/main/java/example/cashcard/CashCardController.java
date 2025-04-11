package example.cashcard;

import java.net.URI;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/cashcards")
public class CashCardController {
	
	@Autowired
	private CashCardCrudRepository cashCardCrudRepository;
	
	private CashCardController(CashCardCrudRepository cashCardCrudRepository){
	       this.cashCardCrudRepository = cashCardCrudRepository;
	   }
	
	@GetMapping("/{id}")
	private ResponseEntity<CashCard> findById(@PathVariable(name = "id") Long id, Principal principal) {
		
		Optional<CashCard> cashCard = Optional.ofNullable(cashCardCrudRepository.findByIdAndOwner(id, principal.getName()));
		
		if(cashCard.isPresent()) {
		return ResponseEntity.ok(cashCard.get());
		} else {
			return ResponseEntity.notFound().build();
		}
	}
	
	@PostMapping
	private ResponseEntity<Void> saveIt(@RequestBody CashCard cashCard, UriComponentsBuilder ucb, Principal principal){
		CashCard cashCardWithOwner = new CashCard(null, cashCard.amount(), principal.getName());
		
		CashCard savedCashCard = cashCardCrudRepository.save(cashCardWithOwner);
		URI locationOfNewCashCard = ucb.path("cashcards/{id}").buildAndExpand(savedCashCard.id()).toUri();
		
		return ResponseEntity.created(locationOfNewCashCard).build();
	}
	
	@GetMapping
	private ResponseEntity<List<CashCard>> findAll(Pageable pageable, Principal principal){
		
		Page<CashCard> page = cashCardCrudRepository.findByOwner(principal.getName(),
				PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), pageable.getSortOr(Sort.by(Direction.ASC, "amount")))
				);
		
		return ResponseEntity.ok(page.getContent());
		
	}
	
}

