package xyz.mendesoft.controller;

import xyz.mendesoft.dto.PatientDTO;
import xyz.mendesoft.model.Patient;
import xyz.mendesoft.service.IPatientService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/patients") //endpoints | sustantivos plural
@RequiredArgsConstructor
public class PatientController {

    //@Autowired
    private final IPatientService service;
    private final ModelMapper mapper;

    /*public PatientController(IPatientService service) {
        this.service = service;
    }*/

    @GetMapping
    public ResponseEntity<List<PatientDTO>> findAll(){
        List<PatientDTO> lst = service.findAll().stream().map(this::convertToDto).toList();
        //List<PatientRecord> lst = service.findAll().stream().map(e -> new PatientRecord(e.getIdPatient(), e.getFirstName(), e.getLastName(), e.getDni(), e.getAddress(), e.getPhone(), e.getEmail())
            /*{
            PatientDTO dto = new PatientDTO();
            dto.setIdPatient(e.getIdPatient());
            dto.setPrimaryName(e.getFirstName());
            dto.setSurname(e.getLastName());
            dto.setDni(e.getDni());
            dto.setEmail(e.getEmail());
            dto.setAddress(e.getAddress());
            return dto;
        }*/
        //).toList();
        return new ResponseEntity<>(lst, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PatientDTO> findById(@PathVariable("id") Integer id){
        Patient obj = service.findById(id);
        return new ResponseEntity<>(convertToDto(obj), HttpStatus.OK);
    }

    /*@PostMapping
    public ResponseEntity<Patient> save(@RequestBody Patient patient){
        Patient obj = service.save(patient);
        return new ResponseEntity<>(obj, HttpStatus.CREATED);
    }*/

    @PostMapping
    public ResponseEntity<PatientDTO> save(@RequestBody PatientDTO dto){
        Patient obj = service.save(convertToEntity(dto));
        //localhost:8080/patients/3
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(obj.getIdPatient()).toUri();
        return ResponseEntity.created(location).build(); //.body(obj);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PatientDTO> update(@PathVariable("id") Integer id, @RequestBody PatientDTO dto){
        Patient obj = service.update(convertToEntity(dto), id);
        return new ResponseEntity<>(convertToDto(obj), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Integer id){
        service.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    private PatientDTO convertToDto(Patient obj){
        return mapper.map(obj, PatientDTO.class);
    }

    private Patient convertToEntity(PatientDTO dto){
        return mapper.map(dto, Patient.class);
    }
}
