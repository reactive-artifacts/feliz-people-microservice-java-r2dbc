pipeline {
  agent any

  stages {
    stage('Build') {
      steps {
        echo 'Building...'
        withMaven(
           maven: 'maven-3'
        ) {
          sh "mvn clean verify"
        }
      }
    }
    stage('Test') {
      steps {
        echo 'Testing...'
        snykSecurity(
          snykInstallation: 'snyk',
          snykTokenId: 'snyk-id',
          failOnError: 'false'
          // place other parameters here
        )
      }
    }
    stage('Deploy') {
      steps {
        echo 'Deploying...'
      }
    }
  }
}