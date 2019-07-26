package demo.spring.vaadin.chat;


import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Log4j2
@Service
public class PatientsService {

    private final PatientsRepository patientsRepository;
    //   private final ApplicationEventPublisher publisher;

    PatientsService(PatientsRepository patientsRepository) {
        this.patientsRepository = patientsRepository;
    }



    public Flux<Patient> getAll(){
        return this.patientsRepository
                .findAll();
    }
    public List<Patient> getAllList(){
        return this.getAll().take(10).collectList().block();
    }


    public Mono<Patient> getId(String id){
        return this.patientsRepository
                .findById(id).log();

    }

    public Patient getIdPatient(String id){

        return this.getId(id).block();
    }

    public Flux<Patient> getByNameLike(String name){
        return this.patientsRepository
                .findByNameLike(name);
    }

    public List<Patient> getByNameLikeList(String name){

        return getByNameLike(name).collectList().block();
    }


    public Mono<Patient> update(Patient patient) {
        return this.patientsRepository
                .save(patient);
    }


    public Mono<Patient> create(Patient patient) {
        return this.patientsRepository
                .save(patient);
    }

    public Mono<Patient> delete(String id){
        return this.patientsRepository
                .findById(id)
                .flatMap( p -> this.patientsRepository
                        .deleteById(p.getId())
                        .thenReturn(p)).log();

    }
}

