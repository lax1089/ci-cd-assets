/*
DigitalBank CI pipe which executes every night and pulls in the latest version of the application code, builds the new jar, executes full set of Regression tests (Serenity UI/API) remotely, runs Performance tests in Blazemeter, reports on the results, and finally deploys the new jar to QA if everything is successful.
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
   
   stage('Run Regression Tests') {
      echo "###Functional/Regression Tests###"
      wrap([$class: 'Xvfb', additionalOptions: '', assignedLabels: '', autoDisplayName: true, debug: true, displayNameOffset: 0, installationName: 'default-xvfb', parallelBuild: true, screen: '1440x900x24', timeout: 25]) {
        sh "'${mvnHome}/bin/mvn' clean verify -Dserver.port=9092 -Dwebdriver.base.url=http://localhost:9092/bank -Ddynamic.webdriver.driver=firefox -Dcucumber.options='--tags @positive' -DskipUnitTests -DbuildNumber=${BUILD_NUMBER} -Dmaven.test.failure.ignore -Dio.digisic.bank.atm.host=atmlocationsearch1032-8080-default.mock.blazemeter.com"
      }
   }
   
   stage('Deploy to Dev Performance Env') {
       echo "###Deploy to Perf Env###"
      
   }
   
   stage('Run Performance Tests') {
       echo "###Performance Tests###"
       blazeMeterTest credentialsId: '991d4a84-c9e8-4304-a7f1-9ef605fd636d', getJtl: true, getJunit: true, testId: '7102776.taurus', workspaceId: '348658'
   }
   
   stage('Reporting') {
      junit '**/target/failsafe-reports/TEST-*.xml'
      publishHTML(target: [
        reportName : 'Serenity',
        reportDir:   'target/site/serenity',
        reportFiles: 'index.html',
        keepAll:     true,
        alwaysLinkToLastBuild: true,
        allowMissing: false
      ])
      archiveArtifacts 'target/*.war'
   }
   
   stage('Deploy to QA Env') {
       echo "###Deploy to QA Env###"
   }
}
