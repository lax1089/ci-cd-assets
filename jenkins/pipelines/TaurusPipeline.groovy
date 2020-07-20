node {
 
   stage('Pull Tests') {
      // Pull code from the GitHub repository
      git branch: 'master',
       credentialsId: 'f8f53c52-f86d-4ba8-b69b-9dfadf99a4e9',
       url: 'https://github.com/lax1089/jmx-examples.git'
   }
   
   stage('Build') {
      echo "### Building Code ###"
   }
   
   stage('Deploy (QA/Func envs)') {
       echo "### Deploy to Perf Env ###"  
       echo "### Deploy to QA Env ###"  
   }
   
   stage('Run Functional Tests') {
      echo "### Running Functional Tests ###"
      sh "bzt findflights-test.yml -cloud -func"
      
   }
   
   stage('Run Performance Tests') {
       echo "### Running Performance Tests ###"
       //sh "bzt findflights-test.yml -cloud"
   }
   
   stage('Reporting') {
      echo "### Reporting ###"
   }

}
