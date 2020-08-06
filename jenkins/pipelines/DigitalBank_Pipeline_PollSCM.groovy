/*
DigitalBank CI pipe which detects any new commits to the git repo and then pulls the code, builds the new jar, executes limited set of Smoke tests (Serenity UI/API) remotely, and reports on the results.
*/
node {
   def mvnHome
   
   stage('Pull Code') {
      // Pull code from the GitHub repository
      git 'https://github.com/asburymr/Digital-Bank.git'
      // Get the Maven tool.
      // ** NOTE: This 'M3' Maven tool must be configured in the global configuration.           
      mvnHome = tool 'M3'
   }
   
   stage('Build') {  
      // Run the maven build
      sh "'${mvnHome}/bin/mvn' -Dmaven.test.failure.ignore clean package"
   }
   
   stage('Run Tests') {
      wrap([$class: 'Xvfb', additionalOptions: '', assignedLabels: '', autoDisplayName: true, debug: true, displayNameOffset: 1, installationName: 'default-xvfb', parallelBuild: true, screen: '1440x900x24', timeout: 25]) 
      {
        sh "'${mvnHome}/bin/mvn' clean verify clean verify -Dserver.port=9093 -Dwebdriver.base.url=http://localhost:9093/bank -Ddynamic.webdriver.driver=firefox -Dcucumber.options='--tags @positive' -DskipUnitTests -DbuildNumber=${BUILD_NUMBER} -Dmaven.test.failure.ignore -Dio.demo.bank.atm.host=atmlocationsearch1032-8080-default.mock.blazemeter.com"
      }
   }
   
   stage('Reporting') {
      junit '**/target/failsafe-reports/TEST-*.xml'
      publishHTML(target: [
        reportName : 'Acceptance Testing Report',
        reportDir:   'target/site/serenity',
        reportFiles: 'index.html',
        keepAll:     true,
        alwaysLinkToLastBuild: true,
        allowMissing: false
    ])
      archiveArtifacts 'target/*.war'
   }
}
