node {
 
   //def bzt = '/opt/rh/rh-python36/root/usr/bin/bzt'
 
   stage('Pull Tests') {
      // Pull code from the GitHub repository
      git 'https://github.com/lax1089/jmx-examples.git'
      sh "chmod 755 run.sh"
   }
   
   stage('Build') {
      echo "### Building Code ###"
   }
   
   stage('Deploy (QA/Func envs)') {
       echo "### Deploy to Perf Env ###"  
       echo "### Deploy to QA Env ###"  
   }
   
   stage('Run Functional and Perf Tests') {
      echo "### Running Functional Tests ###"
      sh "./run.sh"
      //sh "${bzt} find-flights/findflights-test.yml -cloud -func"
      
   }
   
   /*
   stage('Run Performance Tests') {
       echo "### Running Performance Tests ###"
       //sh "bzt find-flights/findflights-test.yml -cloud"
   }
   */
   
   stage('Reporting') {
      echo "### Reporting ###"
   }

}
