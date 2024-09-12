pipeline {
    agent any
    environment {
        REPO = "https://github.com/Barsoup-Tensor/auth-server.git"
    }
    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }
        stage('Setup Environment') {
            steps {
                script {
                    sh "ls -al"
                    sh "chmod +x ./gradlew"
                }
            }
        }
        stage('Stop and Remove Container') {
            steps {
                script {
                    sh "docker stop auth || true"
                    sh "docker rm auth || true"
                }
            }
        }
        stage('Remove Old Images') {
            steps {
                script {
                    sh "docker images auth -q | xargs -r docker rmi || true"
                    sh "docker images -f 'dangling=true' -q | xargs -r docker rmi || true"
                }
            }
        }
        stage("Build") {
            steps {
                script {
                    sh "docker build -t auth ."
                }
            }
        }
        stage('Up') {
            steps {
                script {
                    sh "docker run -d --rm --name auth -p 8050:8050 auth"
                }
            }
        }
    }
    post {
        always {
            echo "Build completed"
        }
    }
}
