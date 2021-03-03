def jenkinsfile
def version = '2'
def credentialsId = 'git'
stage('load pipeline') {
    jenkinsfile = fileLoader.fromGit('templates/leveranse-maven', 'https://bitbucket.brreg.no/scm/raas/pipeline-scripts.git', version, credentialsId)
}

def deployOrder = [
        "1": [
                "name"   : "-",
                "cluster": "regsys-st"
        ],
        "2": [
                "name"   : "-",
                "cluster": "regsys-ppe"
        ]
]

def roleyOptions = [
        "deployments": deployOrder
]

def overrides = [
        "noCucumber": true,
        "buildLoglevel": "DEBUG",
        "cleanJVMOpts": true,
        //TODO: ikke sikkert bruken av branch er i henhold til BR praksis
        "deployFromBranch": "prod",
        "mavenProfile" : "all-tests"
]
jenkinsfile.run(version, overrides)