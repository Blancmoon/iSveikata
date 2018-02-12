import React, {Component} from 'react'
import axios from 'axios'
import {connect} from 'react-redux'


//import SearchFieldForm from '../DoctorComponent/SearchFieldForm'
import PrescriptionListView from '../DoctorComponent/PrescriptionListView';
import PrescriptionListingItem from '../DoctorComponent/PrescriptionListingItem';

var backgroundStyle = {     height: '100%', width: '100%', zIndex: '3',
                            position: 'fixed', top: '0', left: '0', background: 'rgba(255,255,255,0.8)', display:'none'}
var recordDetailWindowStyle = {  height: '60%', width: '60%',  border: '2px solid black', zIndex: '4',
                                position: 'fixed', top: '20%', left: '20%', background: 'white', display:'block'}



class DruggistViewContainer extends Component{
    constructor(props){
        super(props)
        this.session = JSON.parse(sessionStorage.getItem('session'))
        this.state = {
            prescriptions:null,
            infoState:'',
            info:(<h3>Pacientui išrašytų receptų nerasta</h3>),

            listInfo:'',

            activePage:1,
            itemsPerPage:8,
            listLength:'',
        }
    }


    componentWillMount = () =>{
        if(this.session === null || this.session.user.loggedIn !== true || this.session.user.userType !== 'druggist'){
            this.props.router.push('/vartotojams');
            return '';
        }  
        this.getPatientPrescriptions(this.state.activePage)
       
    }

    getPatientPrescriptions = (activePage) =>{
        axios.get('http://localhost:8080/api/patient/'
        +this.props.params.patientId+'/prescription?page='
        +activePage+'&size='+this.state.itemsPerPage)
        .then((response)=>{
            if(response.data.content.length === 0){
                this.setState({
                    prescriptionList:this.state.info
                })
            }else{
                this.setState({
                    prescriptionList:<PrescriptionListView prescription={response.data.content.map(this.composePrescription)}/>
                })
            }
           
            console.log(response.status)
        })
        .catch((erorr) => {
            console.log(erorr)

        })
    }
    showDetails = (prescriptionId) =>{    
        this.loadSpecificPrescription(prescriptionId)
        this.closeOpenDetails()
        console.log(prescriptionId)
    }

    composePrescription = (prescription, index) =>{
 
        return(
            <PrescriptionListingItem 
                key={index}
                index={index}
                id={prescription.id}
                prescriptionDate={prescription.prescriptionDate}
                expirationDate={prescription.expirationDate}
                ingredientName={prescription.api.title}
                useAmount={prescription.useAmount}
                showDetails={this.showDetails}
            />
        )
    }
    prescriptionUsageSubmit = (prescriptionId) =>{
        let currentDate = new Date();
        axios.post('http://localhost:8080//api/prescription/'+prescriptionId+'/new/usage',{
            usage:{
                usageDate:currentDate
            },
            userName:this.session.user.userName,
        })
        .then((response) => {
            this.setState({
                infoState:<div className="alert alert-success"><strong>{response.data}</strong></div>,
            })
            this.getPatientPrescriptions(this.state.activePage)
            console.log(response.status)
        })
        .catch((erorr) =>{
            this.setState({
                infoState:<div className="alert alert-success"><strong>{erorr.response.data}</strong></div>,
            })
        })
        this.closeOpenDetails();
    }

    loadSpecificPrescription = (prescriptionId) =>{
        axios.get('http://localhost:8080//api/prescription/'+prescriptionId)
        .then((response) => {
            this.setState({
                    infoDetails:this.composeSpecificPrescription(response.data, prescriptionId)
                })
            console.log(response.status)
        })
        .catch((erorr) =>{
            console.log(erorr)
        })
    }
    composeSpecificPrescription = (prescription, prescriptionId) => {
       
        return (<div style={{padding:'30px' }}>
                <p>Išrašymo data: {prescription.prescriptionDate}</p>
                <p>Galiojimo data: {prescription.expirationDate}</p>
                <p>Recepto panaudojimų skaičius: {prescription.useAmount}</p>
                <p>Vaisto aktyvioji medžiaga: {prescription.apiDto.ingredientName}</p>
                <p>Aktyviosios medžiagos kiekis dozėje: {prescription.ingredientAmount}</p>
                <p>Matavimo vienetai: {prescription.apiDto.unit}</p>
                <p>Aprašymas: {prescription.description}</p>
                <button onClick={() => this.prescriptionUsageSubmit(prescriptionId) }className='btn btn-primary'>Pažymėti pirkimo faktą</button>
                
        </div>)
    }

    fielddHandler = (e) =>{
        this.setState({
            searchValue:e.target.value
        })
    }
    searchHandler = (e) =>{
        e.preventDefault()
       console.log("Searcrh search..."+ this.state.searchValue)
    }

    closeOpenDetails = () =>{
        let el = document.getElementById("recordDetails")
        if(el.style.display === 'block'){
            el.style.display = 'none'
        }else{
            el.style.display = 'block'
        }
    }


    


    render() {
        return (
            <div className="container">
            <section>
                <div className="panel-group">
                    <div className="panel panel-default">
                        <div className="panel-heading">
                            <button onClick={() =>  this.props.router.goBack()} className="btn btn-primary"> Atgal </button>
                            <h4>Pacientui išrašyti receptai</h4>
                        </div>
                        <div className="panel-body">
                            <div className="col-sm-12">
                                {this.state.infoState}

                                {this.state.prescriptionList}
                                
                                <div id="recordDetails" style={backgroundStyle}>
                                <div  style={recordDetailWindowStyle}>
                                <button onClick={this.closeOpenDetails} className="btn btn-success pull-right" >X</button> 
                                {this.state.infoDetails}
                                </div>
                                </div>
                            </div>
                        </div> 
                    </div> 
                </div>           
            </section>
        </div>)
    }
}

const mapStateToProps = (state) =>{
    return{
        user:state.user
    }
}



export default connect(mapStateToProps)(DruggistViewContainer)
