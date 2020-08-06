/*
DigitalBank CI pipe which gets all git branches for the repo, prompts user to select the branch they want to build, 
pulls the code of a specified branch, builds the new war, executes limited set of Smoke tests (Serenity UI/API) remotely, 
and reports on the results.
*/
node {
   def mvnHome
   def GITHUB_PROJECT_URL = "https://github.com/lax1089/jmx-examples.git"
   def BRANCH_NAME = "NOT_WORKING"
   
   stage ('Listing Branches') {
      echo "Getting git branches for repo"
      //checkout code
      echo GITHUB_PROJECT_URL
      git url: GITHUB_PROJECT_URL
      sh 'rm -f branches.txt'
      sh 'git branch -r | awk \'{print $1}\' ORS=\'\\n\' | cut -c 8- >> branches.txt'
      sh 'cat branches.txt'
   }
   
   stage('Branch Param User Input') {
      branch_list = readFile 'branches.txt'
      echo "Please click here to chose the branch to build"
      env.BRANCH_SCOPE = input message: 'Please choose the branch to build ', ok: 'Confirm',
      parameters: [choice(name: 'BRANCH_NAME', choices: "${branch_list}", description: 'Branch to build?')]
   }
   
   stage('Pull Tests') {
      echo "Pulling code from BRANCH_SCOPE=${BRANCH_SCOPE}"
      // Pull code from the GitHub repository
      git branch: "${BRANCH_SCOPE}", url: 'https://github.com/lax1089/jmx-examples.git', changelog: true
      sh "chmod 755 run.sh"
   }
   
   stage('Build') {
      echo "### Building Code ###"
   }
   
   stage('Deploy (Dev env)') {
       echo "### Spin up Dev Env and Deploy branch ###"  
   }
   
   stage('Run Functional and Perf Tests') {
      echo "### Running Functional Tests ###"
      sh "./run.sh ${BRANCH_SCOPE}"
   }
   
   stage('Reporting') {
      echo "### Reporting ###"
   }
}
