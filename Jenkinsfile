pipeline {
    agent any
    stages {
        stage ('Build Back-End'){
            steps {
                bat 'mvn clean package -DskipTests=true'
            }
        }
        stage ('Unit Tests'){
            steps {
                bat 'mvn test'
            }
        }
        stage ('Sonar Analysis'){
            environment {
                scannerHome = tool 'SONAR_SCANNER'
            }
            steps {
                withSonarQubeEnv('SONAR_LOCAL'){
                    bat "${scannerHome}/bin/sonar-scanner -e -Dsonar.projectKey=DeployBack -Dsonar.host.url=http://localhost:9000 -Dsonar.login=7497edb198aa9e12d459538bff0d0304dffc0022 -Dsonar.java.binaries=target -Dsonar.coverage.exclusions=**/.mvn/**,**/src/test/**,**/model/**,**Application.java"
                }

            }
        }
        stage ('Quality Gate'){
            steps {
                sleep(30)
                timeout (time: 1, unit: 'MINUTES'){
                    waitForQualityGate abortPipeline: true
                }
            }
        }
        stage ('Deploy Back-End'){
            steps {
                deploy adapters: [tomcat8(credentialsId: 'TomcatLogin', path: '', url: 'http://localhost:8001/')], contextPath: 'tasks-backend', war: 'target/tasks-backend.war'
            }
        }
        stage ('API Test'){
            steps {
                dir('api-test') {
                    git credentialsId: 'Login_Github', url: 'https://github.com/GuiJabour/tasks-api-test.git'
                    bat 'mvn test'
                }
            }
        }
        stage ('Deploy Front-End'){
            steps {
                dir('front-end'){
                    git credentialsId: 'Login_Github', url: 'https://github.com/GuiJabour/tasks-frontend'
                    bat 'mvn clean package -DskipTests=true'
                    deploy adapters: [tomcat8(credentialsId: 'TomcatLogin', path: '', url: 'http://localhost:8001/')], contextPath: 'tasks', war: 'target/tasks.war'
                }
            }
        }
        stage ('Functional Test'){
            steps {
                dir('functional-test') {
                    git credentialsId: 'Login_Github', url: 'https://github.com/GuiJabour/tasks-functional-tests.git'
                    bat 'mvn test'
                }
            }
        }
        stage ('Deploy Prod'){
            steps {
                //bat 'docker rm backend-prod'
                ///bat 'docker rm pg-prod'
                //bat 'docker rm frontend-prod'
                bat 'docker-compose build'
                bat 'docker-compose up -d'
            }
        }
        stage ('Health Check'){
            steps {
                sleep(10)
                dir ('functional-test'){
                    bat 'mvn verify -Dskip.surefire.tests'
                }
            }
        }
    }
    post {
        always {
            junit allowEmptyResults: true, testResults: 'target/surefire-reports/*.xml, api-test/target/surefire-reports/*.xml, functional-test/target/surefire-reports/*.xml, functional-test/target/failsafe-reports/*.xml'
            archiveArtifacts artifacts: 'target/tasks-backend.war, front-end/target/tasks.war', followSymlinks: false, onlyIfSuccessful: true
        }
        unsuccessful {
            emailext attachLog: true, body: 'See the attached Log below', subject: 'Build $BUILD_NUMBER has failed', to: 'henrique.jabour@gmail.com'
        }
        fixed {
            emailext attachLog: true, body: 'See the attached Log below', subject: 'Build is Fine!!!', to: 'henrique.jabour@gmail.com'
        }
    }
}

