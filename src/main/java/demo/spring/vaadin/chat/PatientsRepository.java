package demo.spring.vaadin.chat;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface PatientsRepository extends ReactiveMongoRepository<Patient, String> {

    Flux<Patient> findByNameLike(String name);
    Mono<Patient> findById(String id);


}

