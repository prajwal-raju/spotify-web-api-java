pipeline {
agent any
triggers {
  pollSCM 'H * * * *'
}
stages {
  stage('Checkout') {
    steps {
      checkout scmGit(branches: [[name: '*/master']], extensions: [], userRemoteConfigs: [[url: 'https://github.com/prajwal-raju/spotify-web-api-java/']])
    }
  }
  stage('Build') {
  steps {
    echo "Build Stage"
  }
 }
 stage('Deploy') {
  steps {
    echo "deployment stage"
  }
}
}
}
