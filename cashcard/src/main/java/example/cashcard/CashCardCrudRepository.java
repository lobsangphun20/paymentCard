package example.cashcard;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

interface CashCardCrudRepository extends CrudRepository<CashCard, Long>, PagingAndSortingRepository<CashCard,Long>{

}