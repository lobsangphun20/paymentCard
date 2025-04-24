package example.cashcard.contoller;

import java.net.URI;
import java.security.Principal;
import java.util.List;
//import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import example.cashcard.domainDefinition.CashCard;
import example.cashcard.repository.CashCardCrudRepository;

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
		
		CashCard cashCard = findCashCard(id, principal);
		
		if(cashCard != null) {
		return ResponseEntity.ok(cashCard);
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
	
	
	@PutMapping("/{id}")
    private ResponseEntity<Void> putCashCard(@PathVariable(name = "id") Long requestedId, @RequestBody CashCard cashCardUpdate, Principal principal) {
        CashCard cashCard = findCashCard(requestedId, principal);
        if(cashCard != null) {
        CashCard updatedCashCard = new CashCard(cashCard.id(), cashCardUpdate.amount(), principal.getName());
        cashCardCrudRepository.save(updatedCashCard);
        return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
	
	@DeleteMapping("/{requestedId}")
    private ResponseEntity<Void> deleteCashCard(@PathVariable Long requestedId, Principal principal){

        if (!cashCardCrudRepository.existsByIdAndOwner(requestedId, principal.getName())) {
            return ResponseEntity.notFound().build();
        }
        cashCardCrudRepository.deleteById(requestedId);
        return ResponseEntity.noContent().build();
    }
	
	//utility method
	private CashCard findCashCard(Long requestedId, Principal principal) {
	    return cashCardCrudRepository.findByIdAndOwner(requestedId, principal.getName());
	}
}

