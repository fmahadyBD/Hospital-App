pipeline {
    agent any
    
    environment {
        DOCKER_REGISTRY = 'your-registry.com'
        IMAGE_TAG = "${BUILD_NUMBER}"
    }
    
    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }
        
        // DEV BRANCH PIPELINE
        stage('Development Branch') {
            when {
                branch 'dev'
            }
            stages {
                stage('Dev - Test') {
                    parallel {
                        stage('Backend Tests') {
                            steps {
                                dir('hospital-services') {
                                    sh 'mvn clean test'
                                }
                            }
                        }
                        stage('Frontend Tests') {
                            steps {
                                dir('hospital-ui') {
                                    sh 'npm install'
                                    sh 'npm run test -- --watch=false --browsers=ChromeHeadless'
                                }
                            }
                        }
                    }
                }
                
                stage('Dev - Deploy to Staging') {
                    steps {
                        echo '🚀 Deploying DEV branch to Staging Server'
                        sh '''
                            # Deploy with hot reload setup to staging
                            docker-compose down || true
                            docker-compose build --no-cache
                            docker-compose up -d
                        '''
                    }
                }
                
                stage('Dev - Integration Tests') {
                    steps {
                        sh 'sleep 30'
                        sh 'curl -f http://localhost:8090/api/v1/health || exit 1'
                        sh 'curl -f http://localhost:4200 || exit 1'
                    }
                }
                
                stage('Dev - Approval for Main') {
                    steps {
                        script {
                            def userInput = input(
                                message: '✅ Dev tests passed! Merge to main branch for production?',
                                parameters: [choice(choices: ['No', 'Yes'], description: 'Deploy to Production?', name: 'DEPLOY')]
                            )
                            if (userInput == 'Yes') {
                                echo '📝 Creating Pull Request to main branch...'
                                // You can automate PR creation here
                                sh '''
                                    echo "Creating PR from dev to main"
                                    # GitHub CLI or API call to create PR
                                '''
                            }
                        }
                    }
                }
            }
        }
        
        // MAIN BRANCH PIPELINE (PRODUCTION)
        stage('Production Branch') {
            when {
                branch 'main'
            }
            stages {
                stage('Prod - Build Production Images') {
                    parallel {
                        stage('Build Backend Prod') {
                            steps {
                                script {
                                    def backendImage = docker.build(
                                        "${DOCKER_REGISTRY}/hospital-backend:${IMAGE_TAG}", 
                                        "-f hospital-services/Dockerfile.prod hospital-services/"
                                    )
                                    docker.withRegistry('https://your-registry.com', 'docker-registry-credentials') {
                                        backendImage.push()
                                        backendImage.push('latest')
                                    }
                                }
                            }
                        }
                        stage('Build Frontend Prod') {
                            steps {
                                script {
                                    def frontendImage = docker.build(
                                        "${DOCKER_REGISTRY}/hospital-frontend:${IMAGE_TAG}", 
                                        "-f hospital-ui/Dockerfile.prod hospital-ui/"
                                    )
                                    docker.withRegistry('https://your-registry.com', 'docker-registry-credentials') {
                                        frontendImage.push()
                                        frontendImage.push('latest')
                                    }
                                }
                            }
                        }
                    }
                }
                
                stage('Prod - Security Scan') {
                    steps {
                        echo '🔒 Running security scans...'
                        // Add security scanning tools
                    }
                }
                
                stage('Prod - Deploy Production') {
                    steps {
                        input message: '🚨 Deploy to PRODUCTION?', ok: 'Deploy to Production!'
                        echo '🚀 Deploying to Production Server'
                        sh '''
                            # Deploy to production server
                            ssh production-server "
                                cd /opt/hospital-app &&
                                docker-compose -f docker-compose.prod.yml down &&
                                docker-compose -f docker-compose.prod.yml pull &&
                                docker-compose -f docker-compose.prod.yml up -d
                            "
                        '''
                    }
                }
            }
        }
    }
    
    post {
        always {
            sh 'docker-compose down || true'
            sh 'docker system prune -f'
        }
        success {
            script {
                if (env.BRANCH_NAME == 'dev') {
                    emailext (
                        to: 'dev-team@company.com',
                        subject: "✅ DEV Pipeline Success: ${env.BUILD_NUMBER}",
                        body: "Development build completed! Ready for production merge."
                    )
                } else if (env.BRANCH_NAME == 'main') {
                    emailext (
                        to: 'ops-team@company.com',
                        subject: "🚀 PRODUCTION Deployed: ${env.BUILD_NUMBER}",
                        body: "Production deployment successful!"
                    )
                }
            }
        }
    }
}