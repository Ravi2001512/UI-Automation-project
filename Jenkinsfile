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
                bat "mvn clean test -Dheadless=true -Dsurefire.suiteXmlFiles=${params.TEST_SUITE}"
            }
        }
    }
}