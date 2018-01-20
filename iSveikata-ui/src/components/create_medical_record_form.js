import React from 'react';
import { FormErrors } from './form_errors';
import './form.css';

class CreateMedicalRecordForm extends React.Component {

  constructor (props) {
    super(props);
    this.state = {
      icdCode: '',
      appointmentDescription: '',
      appointmentDuration: '',
      formErrors: {icdCode: '', appointmentDescription: '', appointmentDuration: ''},
      icdCodeValid: false,
      appointmentDescriptionValid: false,
      isCompensable: false,
      isRepetitive: false,
      appointmentDurationValid: false,                   
      formValid: false
    };
    this.handleUserInput = this.handleUserInput.bind(this);
  }

  handleUserInput (e) {
    // e === event
    const name = e.target.name;
    const value = e.target.type === 'checkbox' ? e.target.checked : e.target.value;

    this.setState({[name]: value},
      () => { this.validateField(name, value) });
  }

  validateField(fieldName, value) {
    let fieldValidationErrors = this.state.formErrors;
    let icdCodeValid = this.state.icdCodeValid;
    let appointmentDescriptionValid = this.state.appointmentDescriptionValid;
    let appointmentDurationValid = this.state.appointmentDurationValid;
  
    switch (fieldName) {
      case 'icdCode':
        if(value.match(/^[a-zA-Z]\d{2}(\.[a-zA-Z0-9]{1,4})?$/g)) {
          icdCodeValid = true;
        } else if(value.match(/^[1-7]{1}$/g)){
          icdCodeValid = true;
        } else if(value.match(/^[lL]\d{3}$/g)){
          icdCodeValid = true;
        } else if(value.match(/^[8-9]{1}\d{0,1}(\d-[mM]\d{2})?$/g)){
          icdCodeValid = true;
        } else {
          icdCodeValid = false;
        }
        // ^ [RAIDĖ][SKAIČIUS][SKAIČIUS](.)(SKAIČIUS*)(SKAIČIUS*)(SKAIČIUS*)(RAIDĖ ARBA SKAIČIUS). 
        //*rečiau, raidė, arba X -> jei yra 7-ta pozicija, o kitų pozicijų nėra, šios iki septintosios užpildomos raide X.
        //Specialūs kodai Lietuvoje - [RAIDĖ L][SKAIČIUS][SKAIČIUS][SKAIČIUS]. [SKAIČIUS NUO 1 IKI 7]. [SKAIČIUS 8 ARBA 9](SKAIČIUS)(SKAIČIUS -M SKAIČIUS SKAIČIUS). 
        fieldValidationErrors.icdCode = icdCodeValid ? '' : 'Įveskite taisyklingą TLK-10 kodą.';
        break;
      case 'appointmentDescription':
        appointmentDescriptionValid = value.length >= 3;
        // ^ Tikrina ar įrašyta bent kažkas.
        fieldValidationErrors.appointmentDescription = appointmentDescriptionValid ? '' : 'Aprašykite vizitą.';
        break;
      case 'appointmentDuration':
        appointmentDurationValid = value.match(/^\d{1,3}$/g);
        // ^ Tikrina ar įrašytas teigiamas skaičius.
        fieldValidationErrors.appointmentDuration = appointmentDurationValid ? '': 'Įveskite vizito trukmę.';
        break;
      default:
        break;
    }
    this.setState({formErrors: fieldValidationErrors,
                    icdCodeValid: icdCodeValid,
                    appointmentDescriptionValid: appointmentDescriptionValid,
                    appointmentDurationValid: appointmentDurationValid,
                  }, this.validateForm);
  }
  
  validateForm() {
    this.setState({formValid: this.state.icdCodeValid && this.state.appointmentDescriptionValid && this.state.appointmentDurationValid});
  }

  errorClass(error) {
    return(error.length === 0 ? 'form-control is-valid' : 'form-control is-invalid');
 }

 handleSubmit() {
  // Do whatever you like with the data.
 }
 
 render () {
   return (
     <form className="demoForm" onSubmit={this.handleSubmit}>
       <h2>Ligos įrašas</h2>
       <div className="panel panel-default">
        <FormErrors formErrors={this.state.formErrors} />
       </div>
       <div className="form-group">
        <label htmlFor="InputIcdCode">TLK-10 ligos kodas*</label>
        <input type="text" className={this.errorClass(this.state.formErrors.icdCode)} 
        id="InputIcdCode" name="icdCode" placeholder="X##(.###) arba specialus kodas" maxLength="8" value={(this.state.icdCode).toUpperCase()} required 
        onChange={(event) => this.handleUserInput(event)}/>
       </div>
       <div className="form-group">
        <label htmlFor="InputDescription">Vizito aprašymas*</label>
        <textarea className={this.errorClass(this.state.formErrors.appointmentDescription)}
        id="InputDescription" name="appointmentDescription" rows="3" maxLength="500" value={this.state.appointmentDescription} required 
        onChange={(event) => this.handleUserInput(event)}></textarea>
       </div>
       <div className="form-group">
        <div className="form-check">
         <input type="checkbox" className="form-check-input" id="CheckIfCompensable" name="isCompensable" checked={this.state.isCompensable} 
         onChange={(event) => this.handleUserInput(event)}/>
         <label className="form-check-label" htmlFor="CheckIfCompensable">Vizitas <strong>{this.state.isCompensable ? " kompensuojamas " : " nekompensuojamas "}</strong> Valstybinės ligonių kasos.</label>
        </div>
        <div className="form-check">
         <input type="checkbox" className="form-check-input" id="CheckIfRepetitive" name="isRepetitive" checked={this.state.isRepetitive} 
         onChange={(event) => this.handleUserInput(event)}/>
         <label className="form-check-label" htmlFor="CheckIfRepetitive">Vizitas <strong>{this.state.isRepetitive ? " yra " : " nėra "}</strong> pakartotinis dėl tos pačios priežasties.</label>
        </div>
       </div>
       <div className="form-group">
        <label htmlFor="InputDuration">Vizito trukmė (minutėmis)*</label>
        <input type="text" className={this.errorClass(this.state.formErrors.appointmentDuration)} 
        id="InputDuration" name="appointmentDuration" placeholder="Trukmė" maxLength="3" value={this.state.appointmentDuration} required 
        onChange={(event) => this.handleUserInput(event)}/>
       </div>
       <button type="submit" className="btn btn-primary" disabled={!this.state.formValid}>
          Sukurti įrašą
       </button>
     </form>
   )
 }
}
export default CreateMedicalRecordForm;


