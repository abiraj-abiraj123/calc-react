pipeline {
    agent any

    environment {
        DOCKER_IMAGE = 'abiraj165/calc-react:latest'  // Replace with your DockerHub username
        K8S_DEPLOYMENT_FILE = 'k8s/deployment.yaml'
        K8S_SERVICE_FILE = 'k8s/service.yaml'
    }

    stages {
        stage('Clone Repository') {
            steps {
                git credentialsId: 'github-credentials', url: 'https://github.com/abiraj-abiraj123/calc-react.git'
            }
        }

        stage('Install Dependencies & Build React App') {
            steps {
                sh 'npm install'
                sh 'npm run build'
            }
        }

        stage('Build Docker Image') {
            steps {
                script {
                    docker.build("${DOCKER_IMAGE}")
                }
            }
        }

        stage('Push Docker Image to DockerHub') {
            steps {
                withDockerRegistry([ credentialsId: 'dockerhub-credentials', url: '' ]) {
                    sh "docker push ${DOCKER_IMAGE}"
                }
            }
        }

        stage('Deploy to Kubernetes') {
            steps {
                sh "kubectl apply -f ${K8S_DEPLOYMENT_FILE}"
                sh "kubectl apply -f ${K8S_SERVICE_FILE}"
            }
        }
    }

    post {
        success {
            echo '✅ Deployment Successful!'
        }
        failure {
            echo '❌ Deployment Failed. Check logs!'
        }
    }
}
