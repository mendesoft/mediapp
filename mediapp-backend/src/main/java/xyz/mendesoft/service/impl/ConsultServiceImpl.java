package xyz.mendesoft.service.impl;

import xyz.mendesoft.model.Consult;
import xyz.mendesoft.model.Exam;
import xyz.mendesoft.repo.IConsultExamRepo;
import xyz.mendesoft.repo.IGenericRepo;
import xyz.mendesoft.repo.IConsultRepo;
import xyz.mendesoft.service.IConsultService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ConsultServiceImpl extends CRUDImpl<Consult, Integer> implements IConsultService {

    //@Autowired
    private final IConsultRepo consultRepo;// = new ConsultRepo();
    private final IConsultExamRepo ceRepo;

    @Override
    protected IGenericRepo<Consult, Integer> getRepo() {
        return consultRepo;
    }

    @Transactional
    @Override
    public Consult saveTransactional(Consult consult, List<Exam> exams) {
        consultRepo.save(consult); //GUARDADO MAESTRO DETALLE
        exams.forEach( ex -> ceRepo.saveExam(consult.getIdConsult(), ex.getIdExam()));

        return consult;
    }
}
