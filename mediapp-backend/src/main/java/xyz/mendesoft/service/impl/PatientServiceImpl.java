package xyz.mendesoft.service.impl;

import xyz.mendesoft.model.Patient;
import xyz.mendesoft.repo.IGenericRepo;
import xyz.mendesoft.repo.IPatientRepo;
import xyz.mendesoft.service.IPatientService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PatientServiceImpl extends CRUDImpl<Patient, Integer> implements IPatientService {

    //@Autowired
    private final IPatientRepo repo;// = new PatientRepo();

    @Override
    protected IGenericRepo<Patient, Integer> getRepo() {
        return repo;
    }

}
