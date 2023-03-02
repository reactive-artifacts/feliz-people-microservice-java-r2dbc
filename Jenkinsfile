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
  withCredentials([string(credentialsId: 'track', variable: 'API_KEY')]) {
                    dependencyTrackPublisher artifact: 'target/bom.xml', projectName: 'feliz', projectVersion: 'my-version', synchronous: true, dependencyTrackApiKey: API_KEY, projectProperties: [tags: ['tag1', 'tag2'], swidTagId: 'my swid tag', group: 'my group',synchronous: true]
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