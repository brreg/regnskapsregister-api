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
        ]
]

def roleyOptions = [
        "appname"    : "regnskap-api",
        "filename"   : "apps/regnskap-api",
        "domain"     : "regnskap",
        "deployments": deployOrder
]

def overrides = [
        "noCucumber": true,
        "buildLoglevel": "DEBUG",
        "cleanJVMOpts": true,
        "deployFromBranch": "fix-swagger-ui",
        "mavenProfile" : "all-tests",
        roleyOptions: roleyOptions,
]
jenkinsfile.run(version, overrides)