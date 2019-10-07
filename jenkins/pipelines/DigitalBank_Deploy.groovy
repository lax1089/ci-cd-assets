/*
Stops DigitalBank docker instances, removes them, pulls down the new DigitalBank image, then starts DigitalBank instance pointing to a Mock Service (8082) as well as a different DigitalBank instance not pointing to a mock (8081)
*/
node {
    def dbank_container_name="bank"
    def dbank_with_mock_container_name="bank_with_mock"
    def dbank_port_no_mock=8081
    def dbank_port_with_mock=8082
    
    stage('Refresh Env') {
       // First Container
       echo "Stopping and removing '${dbank_container_name}' container"
       try {
            sh "docker stop ${dbank_container_name}"
       } catch (Exception e) {
            echo "Could not stop '${dbank_container_name}', it was either already stopped or did not exist. No problem - I can handle that."
       }
       try {
           sh "docker rm -f ${dbank_container_name}"
       } catch (Exception e) {
           echo "Could not remove ${dbank_container_name}"
       }
       sh "docker network disconnect --force bridge ${dbank_container_name}"
       
       // Second Container
       echo "Stopping and removing '${dbank_with_mock_container_name}' container"
       try {
            sh "docker stop ${dbank_with_mock_container_name}"
       } catch (Exception e) {
            echo "Could not stop '${dbank_with_mock_container_name}', it was either already stopped or did not exist. No problem - I can handle that."
       }
       try {
           sh "docker rm -f ${dbank_with_mock_container_name}"
       } catch (Exception e) {
           echo "Could not remove ${dbank_with_mock_container_name}"
       }
       sh "docker network disconnect --force bridge ${dbank_with_mock_container_name}"
    }
    
    stage('Pull Docker Images') {
        echo "Pulling latest docker image from repo"
        sh "docker pull asburymr/digitalbank:latest"
    }
    
    stage('Deploy Bank w/ Mock') {
        echo "Starting '${dbank_container_name}' container on port ${dbank_port_no_mock} which is not pointing to a mock"
        sh "docker run -d -p ${dbank_port_no_mock}:8080 --name ${dbank_container_name} asburymr/digitalbank:latest"
    }
    
    stage('Deploy Bank w/o Mock') {
        echo "Starting '${dbank_with_mock_container_name}' container on port ${dbank_port_with_mock} which is pointing to a VISA svc mock"
        sh "docker run -d -p ${dbank_port_with_mock}:8080 --name ${dbank_with_mock_container_name} -e IO_DEMO_BANK_VISA_HOST=dbankvisatransfer2475-8080-default.mock.blazemeter.com asburymr/digitalbank:latest"
    }
    
    // TODO: Add stage that loops indefinitely pinging the two URLs until the webapps are running and then returns success (to complete this pipeline)
}