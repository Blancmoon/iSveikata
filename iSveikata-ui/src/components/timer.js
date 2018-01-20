import React from 'react';

class Timer extends React.Component {
    constructor(props) {
      super(props);
      this.state = { minutes: 0 };
    }
  
    tick() {
      this.setState(prevState => ({
        minutes: prevState.minutes + 1
      }));
    }
  
    componentDidMount() {
      this.interval = setInterval(() => this.tick(), 60000);
    }
  
    componentWillUnmount() {
      clearInterval(this.interval);
    }
  
    render() {
      return (
        <div>
          Forma atverta {this.state.minutes} min.
        </div>
      );
    }
  }
  
  export default Timer;