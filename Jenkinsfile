pipeline {
  agent any

  stages {
    stage('Build') {
      steps {
        echo 'Building...'
        withMaven(
           maven: 'maven-3'
        ) {
          sh "mvn clean -Dmaven.test.skip=true"
        }
      }
    }
    stage('Test') {
      steps {
        echo 'Testing...'
        withMaven(
           maven: 'maven-3'
        ) {
          sh "mvn clean package -P gbom -Dmaven.test.skip=true"
        }
      }
    }
    stage('Deploy') {
      steps {
        echo 'Deploying...'
      }
    }
  }
}