import { AsyncPipe, NgFor, NgIf } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatSnackBar } from '@angular/material/snack-bar';
import * as moment from 'moment';
import { Observable, map } from 'rxjs';
import { MaterialModule } from '../../material/material.module';
import { Patient } from '../../model/patient';
import { Medic } from '../../model/medic';
import { Exam } from '../../model/exam';
import { Specialty } from '../../model/specialty';
import { ConsultDetail } from '../../model/consultDetail';
import { PatientService } from '../../service/patient.service';
import { MedicService } from '../../service/medic.service';
import { SpecialtyService } from '../../service/specialty.service';
import { ExamService } from '../../service/exam.service';
import { ConsultService } from '../../service/consult.service';
import { Consult } from '../../model/consult';
import { ConsultListExamDTOI } from '../../dto/consultListExamDTOI';


@Component({
  selector: 'app-consult-autocomplete',
  standalone: true,
  templateUrl: './consult-autocomplete.component.html',
  styleUrls: ['./consult-autocomplete.component.css'],
  imports: [MaterialModule, ReactiveFormsModule, NgIf, NgFor, AsyncPipe]
})
export class ConsultAutocompleteComponent implements OnInit{

  patients?: Patient[];
  medics?: Medic[];
  form?: FormGroup;

  medicControl: FormControl = new FormControl();

  patients$?: Observable<Patient[]>
  medicsFiltered$?: Observable<Medic[]>;
  specialties?: Specialty[];
  exams?: Exam[];

  minDate: Date = new Date();
  details: ConsultDetail[] = [];
  examsSelected: Exam[] = [];
  //diagnosis: string;

  constructor(
    private patientService: PatientService,
    private medicService: MedicService,
    private specialtyService: SpecialtyService,
    private examService: ExamService,
    private consultService: ConsultService,
    private _snackBar: MatSnackBar
    ){}

  ngOnInit(): void {
    this.form = new FormGroup({
      patient: new FormControl([Validators.required]),
      medic: this.medicControl,
      specialty : new FormControl([Validators.required]),
      consultDate: new FormControl(new Date(), [Validators.required]),
      diagnosis: new FormControl('',[Validators.required]),
      treatment: new FormControl('',[Validators.required]),
      exam: new FormControl()
    });

    this.loadInitialData();
    this.medicsFiltered$ = this.medicControl.valueChanges.pipe(map(val => this.filterMedics(val)));
  }

  filterMedics(val: any){
    if(val?.idMedic > 0){
      return this.medics!.filter(el =>
        el.primaryName!.toLowerCase().includes(val.primaryName.toLowerCase()) || el.surname!.toLowerCase().includes(val.surname.toLowerCase()) || el.cmpMedic!.includes(val.cmpMedic)
      );
    }else{
      return this.medics!.filter(el =>
        el.primaryName!.toLowerCase().includes(val?.toLowerCase()) || el.surname!.toLowerCase().includes(val?.toLowerCase()) || el.cmpMedic!.includes(val)
      );
    }
  }

  loadInitialData(){
    this.patients$ = this.patientService.findAll();//.subscribe(data => this.patients = data);
    this.medicService.findAll().subscribe(data => this.medics = data);
    this.specialtyService.findAll().subscribe(data => this.specialties = data);
    this.examService.findAll().subscribe(data => this.exams = data);
  }

  showMedic(val: any){
    return val ? `${val.primaryName} ${val.surname}` : val;
  }

  addDetail(){
    const det = new ConsultDetail();
    det.diagnosis = this.form!.value['diagnosis'];
    det.treatment = this.form!.value['treatment'];

    this.details.push(det);
  }

  removeDetail(index: number){
    this.details.splice(index, 1);
  }

  addExam(){
    if(this.form!.value['exam'] != null){
      this.examsSelected.push(this.form!.value['exam']);
    }else{
      this._snackBar.open('Please select an exam', 'INFO', {duration: 2000});
    }
  }

  save(){
    if(this.form!.invalid){
      return;
    }

    const consult = new Consult();
    consult.patient = this.form!.value['patient'];
    consult.medic = this.form!.value['medic'];
    consult.specialty = this.form!.value['specialty'];
    consult.details = this.details;
    consult.numConsult = "C1";

    /*let tzoffset = (this.form.value['consultDate']).getTimezoneOffset() * 60000;
    let localISOTime = (new Date(this.form.value['consultDate'] - tzoffset)).toISOString();*/
    //console.log(localISOTime)

    // consult.consultDate = moment(this.form!.value['consultDate']).format('YYYY-MM-DDTHH:mm:ss');

    /*const consultListExamDTO = new ConsultListExamDTO();
    consultListExamDTO.consult = consult;
    consultListExamDTO.lstExam = this.examsSelected;*/

    const dto:ConsultListExamDTOI = {
      consult: consult,
      lstExam: this.examsSelected
    };

    this.consultService.saveTransactional(dto).subscribe( ()=> {
      this._snackBar.open('CREATED!', 'INFO', {duration: 2000});

      setTimeout( ()=> {
        this.cleanControls();
      }, 2000);
    });
  }

  cleanControls(){
    this.form!.reset();
    this.medicControl.reset();
    this.details = [];
    this.examsSelected = [];
  }

  //////////////simulacion de select anidados////////////////
  selectCountry(e: any){
    const idCountry = e.value.idPatient;
    //service.getCities(idCountry).suscribe(data => this.cities = data);
  }

}
