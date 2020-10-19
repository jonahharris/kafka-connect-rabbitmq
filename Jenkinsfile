pipeline {
    agent {
        label 'standard'
    }

    options {
        buildDiscarder(logRotator(numToKeepStr: '20'))
        timeout(time: 30, unit: 'MINUTES')
    }

    stages {
        stage('Build Docker') {
            steps {
                sh "REGISTRY_TARGET=eu.gcr.io/bbc-registry bbc dockerfile build"
            }
        }
        stage('Push docker image') {
            when {
                branch 'master'
            }
            steps {
                sh "bbc dockerfile push"
            }
        }
    }

    post {
        always {
            slackNotifications(
                    channels: '#data-feature-store-ci-cd',
                    nonDevelopmentBranches: ['master']
            )
        }
    }
}