pipeline {
    agent any

    stages {

        stage('Checkout') {
            steps {
                git branch: 'main',
                    url: 'https://github.com/Ravi2001512/UI-Automation-project.git'
            }
        }

        stage('Run UI Tests') {
            steps {
                sh 'mvn clean test -Dheadless=true -Dsurefire.suiteXmlFiles=UI_Suite.xml'
            }
        }
    }
}