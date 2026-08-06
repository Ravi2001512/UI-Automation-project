pipeline {
    agent any

    parameters {
        choice(
            name: 'TEST_SUITE',
            choices: ['UI_Suite.xml'],
            description: 'Select the TestNG suite'
        )
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: 'master',
                    url: 'https://github.com/Ravi2001512/UI-Automation-project.git'
            }
        }

        stage('Run UI Tests') {
            steps {
                sh "mvn clean test -Dheadless=true -Dsurefire.suiteXmlFiles=${params.TEST_SUITE}"
            }
        }
    }
}