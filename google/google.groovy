pipeline {
  agent any
  options {
    buildDiscarder(logRotator(numToKeepStr: '5', artifactNumToKeepStr: '5'))
    timestamps()
  }
  environment {
    inventoryName = 'Bommasani'
  }
  parameters {
    choice(
      name: 'google_service',
      choices: ['s3', 'vpc', 'ec2_linux', 'ec2_windows', 'mysql', 'lambda', 'k8'],
      description: 'Select GOOGLE service'
    )
    choice(
      name: 'function',
      choices: ['plan', 'apply -auto-approve', 'destroy -auto-approve', 'refresh'],
      description: 'Select function'
    )
  }
  stages {
    stage('SCM') {
      steps {
        echo 'Checking out project from Bitbucket....'
        cleanWs()
        git(url: 'git@github.com:vamsi8977/terraform.git', branch: 'main')
      }
    }
    stage('Validate') {
      steps {
        ansiColor('xterm') {
          echo 'Check Tools Versions....'
          sh """
            terraform -v
          """
        }
      }
    }
    stage('GOOGLE') {
      steps {
        ansiColor('xterm') {
          script {
            if (params.google_service == 'ec2_linux') {
              echo "Running on ec2_linux"
              dir("google/env/${params.google_service}") {
                sh '''
                  terraform init -reconfigure
                  terraform validate
                  terraform ''' + params.function
              }
            } else if (params.google_service == 'ec2_windows') {
              echo "Running on ec2_windows"
              dir("google/env/${params.google_service}") {
                sh '''
                  terraform init -reconfigure
                  terraform validate
                  terraform ''' + params.function
              }
            } else if (params.google_service == 'vpc') {
              echo "Running on vpc"
              dir("google/env/${params.google_service}") {
                sh '''
                  terraform init -reconfigure
                  terraform validate
                  terraform ''' + params.function
              }
            } else if (params.google_service == 's3') {
              echo "Running on s3"
              dir("google/env/${params.google_service}") {
                sh '''
                  terraform init -reconfigure
                  terraform validate
                  terraform ''' + params.function
              }
            } else if (params.google_service == 'mysql') {
              echo "Running on mysql"
              dir("google/env/${params.google_service}") {
                sh '''
                  terraform init -reconfigure
                  terraform validate
                  terraform ''' + params.function
              }
            } else if (params.google_service == 'lambda') {
              echo "Running on lambda"
              dir("google/env/${params.google_service}") {
                sh '''
                  terraform init -reconfigure
                  terraform validate
                  terraform ''' + params.function
              }
            } else if (params.google_service == 'k8') {
              echo "Running on k8"
              dir("google/env/${params.google_service}") {
                sh '''
                  terraform init -reconfigure
                  terraform validate
                  terraform ''' + params.function
              }
            }
          }
        }  
      }
    }
  }
  post {
    success {
      script {
        echo "The build is Green, Infrastructure is created."
      }
    }
    failure {
      echo "The build failed."
    }
    always {
      deleteDir()
    }
  }
}