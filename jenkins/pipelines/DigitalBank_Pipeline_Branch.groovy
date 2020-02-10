/*
DigitalBank CI pipe which pulls the code of a specified branch, builds the new war, executes limited set of Smoke tests (Serenity UI/API) remotely, and reports on the results.
*/
node {
   def mvnHome
   def GITHUB_PROJECT_URL = "https://github.com/asburymr/Digital-Bank.git"
   def APPLICATION_NAME = "Digital-Bank"
   def GITHUB_BRANCH = '${env.BRANCH_NAME}'
   
   //properties([
   //  parameters([
   //    string(name: 'BRANCH', defaultValue: '', description: 'git branch to pull', )
   //   ])
   //])
   
   stage ('Listing Branches') {
      echo "Initializing workflow"
      //checkout code
      echo GITHUB_PROJECT_URL
      git url: GITHUB_PROJECT_URL
      sh 'git branch -r | awk \'{print $1}\' ORS=\'\\n\' >branches.txt'
      sh 'cut -d '/' -f 2 branches.txt > branch.txt'
   }
   
   stage('Branch Param User Input') {
      branch_list = readFile 'branch.txt'
      echo "Please click here to chose the branch to build"
      env.BRANCH_SCOPE = input message: 'Please choose the branch to build ', ok: 'Validate!',
      parameters: [choice(name: 'BRANCH_NAME', choices: "${branch_list}", description: 'Branch to build?')]
   }
   
   stage('Pull Code') {
      echo "Pulling code from ${env.BRANCH_SCOPE}"
      // Pull code from the GitHub repository
      git branch: '${BRANCH_SCOPE}', url: 'https://github.com/asburymr/Digital-Bank.git'
      // Get the Maven tool.
      // ** NOTE: This 'M3' Maven tool must be configured in the global configuration.           
      mvnHome = tool 'M3'
   }
   
   stage('Build') {  
      // Run the maven build
      sh "'${mvnHome}/bin/mvn' -Dmaven.test.failure.ignore -DskipUnitTests clean package"
   }
   
   stage('Run Tests') {
      wrap([$class: 'Xvfb', additionalOptions: '', assignedLabels: '', autoDisplayName: true, debug: true, displayNameOffset: 1, installationName: 'default-xvfb', parallelBuild: true, screen: '1440x900x24', timeout: 25]) 
      {
        sh "'${mvnHome}/bin/mvn' -Dmaven.test.failure.ignore -DskipUnitTests clean verify -Dcucumber.options='--tags @positive' -Dserver.port=9092 -Dwebdriver.base.url='http://localhost:9092' -Dwebdriver.driver=firefox -Dio.demo.bank.atm.host=atmlocationsearch1032-8080-default.mock.blazemeter.com"
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
