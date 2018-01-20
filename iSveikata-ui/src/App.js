import React from 'react';
import './App.css';
//import CreatePatientForm from './components/create_patient_form';
import CreateMedicalRecordForm from './components/create_medical_record_form';
import Timer from './components/timer';

//LAIKINAI - BUS PER ROUTERĮ NAVIGACIJA

/* class App extends React.Component {
  render() {
    return (
      <div className="App">
        <CreatePatientForm />
      </div>
    );
  }
}  */

class App extends React.Component {
  render() {
    return (
      <div className="App">
        <CreateMedicalRecordForm />
        <div className="Timer">
          <Timer />
        </div>
      </div>
    );
  }
}

export default App;
