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
        stage('Run UI Tests') {
            steps {
                sh "mvn clean test -Dsurefire.suiteXmlFiles=${params.TEST_SUITE}"
            }
        }
    }
}