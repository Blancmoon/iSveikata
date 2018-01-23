import React, {Component} from 'react'
import axios from 'axios'

import DoctorListView from './AdminComponent/DoctorListView'
import DoctorListingItem from './AdminComponent/DoctorListingItem'
import {DoctorBindLink} from '../Container/LinksAndButtons/DoctorBindLink'


export default class AdminBindDoctorPartContainer extends Component{
    constructor(props){
        super(props);
        this.state = {
            search:'',
            doctors:null,
            notFound:''
        }
    }



    componentWillMount = () =>{

        var session =  JSON.parse(sessionStorage.getItem('session'))
        if(session.user.loggedIn !== true || session.user.userType !== 'admin'){
            this.props.router.push('/vartotojams');
            return '';
        }

        axios.get('http://localhost:8080/api/doctor')
        .then((response)=>{
            if(response.data.length === 0){
                this.setState({
                    notFound:(<h3>No doctor found</h3>),
                    doctor:(<td><th><h3>No doctor found</h3></th></td>)
                })
            }else{
                this.setState({
                    doctors:response.data.map(this.composeDoctor)
                })
            }
            console.log(response.status)
        })
        .catch((erorr) => {
            console.log(erorr)
        })
    }

    composeDoctor = (doctor, index) =>{
        return(
            <DoctorListingItem
                key={index}
                firstName={doctor.firstName}
                lastName={doctor.lastName}
                userName={doctor.userName}
                specialization={doctor.specialization.title}
                doctorBindLink={<DoctorBindLink userName={doctor.userName}/>}
            />)
    }
    
    

    
    searchdHandler = (e) =>{
        this.setState({[e.target.name]: e.target.value})
    }



    
    render(){
        return(
        <div className="container">
            <section>  
                <div className="panel-group">
                    <div className="panel panel-default">
                        <div className="panel-heading">
                            <h4>Apjungti daktara su pacientu</h4>
                        </div>
                        <div className="panel-body">
                            <div className="col-sm-12">
                                <div className="col-sm-4 col-sm-offset-4">
                                    <input type="text" className="form-control" value={this.state.search} onChange={this.searchdHandler} placeholder="Search" name="search" />
                                </div>
                            </div>
                            <div className="col-sm-12">
                                <DoctorListView 
                                    doctors={this.state.doctors}
                                />
                                {this.state.notFound}
                            </div>
                        </div> 
                    </div> 
                </div>           
            </section>
        </div>)
    }
}


