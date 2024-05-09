package xyz.mendesoft.service;

import xyz.mendesoft.model.Consult;
import xyz.mendesoft.model.Exam;

import java.util.List;

public interface IConsultService extends ICRUD<Consult, Integer> {

    Consult saveTransactional(Consult consult, List<Exam> exams);

}
