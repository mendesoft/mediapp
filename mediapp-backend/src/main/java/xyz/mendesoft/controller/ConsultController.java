package xyz.mendesoft.controller;

import xyz.mendesoft.dto.ConsultDTO;
import xyz.mendesoft.dto.ConsultListExamDTO;
import xyz.mendesoft.model.Consult;
import xyz.mendesoft.model.Exam;
import xyz.mendesoft.service.IConsultService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/consults")
@RequiredArgsConstructor
public class ConsultController {
    
    private final IConsultService service;
    @Qualifier("consultMapper")
    private final ModelMapper mapper;
    
    @GetMapping
    public ResponseEntity<List<ConsultDTO>> findAll(){
        List<ConsultDTO> lst = service.findAll().stream().map(this::convertToDto).toList();
        return new ResponseEntity<>(lst, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ConsultDTO> findById(@PathVariable("id") Integer id){
        Consult obj = service.findById(id);
        return new ResponseEntity<>(convertToDto(obj), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<ConsultDTO> save(@Valid @RequestBody ConsultListExamDTO dto){
        Consult cons = this.convertToEntity(dto.getConsult());
        //List<Exam> exams = dto.getLstExam().stream().map( e -> mapper.map(e, Exam.class)).toList();
        List<Exam> exams = mapper.map(dto.getLstExam(), new TypeToken<List<Exam>>(){}.getType());

        Consult obj = service.saveTransactional(cons, exams);

        //localhost:8080/consults/3
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(obj.getIdConsult()).toUri();
        return ResponseEntity.created(location).build(); //.body(obj);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ConsultDTO> update(@Valid @PathVariable("id") Integer id, @RequestBody ConsultDTO dto){
        Consult obj = service.update(convertToEntity(dto), id);
        return new ResponseEntity<>(convertToDto(obj), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Integer id){
        service.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/hateoas/{id}")
    public EntityModel<ConsultDTO> findByIdHateoas(@PathVariable("id") Integer id){
        EntityModel<ConsultDTO> resource = EntityModel.of(convertToDto(service.findById(id)));
        //localhost:8080/consults/1
        WebMvcLinkBuilder link1 = linkTo(methodOn(this.getClass()).findById(id));
        WebMvcLinkBuilder link2 = linkTo(methodOn(this.getClass()).findById(id));

        resource.add(link1.withRel("consult-info1"));
        resource.add(link1.withRel("consult-info2"));
        return resource;
    }

    private ConsultDTO convertToDto(Consult obj){
        return mapper.map(obj, ConsultDTO.class);
    }

    private Consult convertToEntity(ConsultDTO dto){
        return mapper.map(dto, Consult.class);
    }
}